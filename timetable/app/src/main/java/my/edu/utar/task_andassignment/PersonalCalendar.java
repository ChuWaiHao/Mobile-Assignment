package my.edu.utar.task_andassignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PersonalCalendar extends AppCompatActivity {

    private Map<String, String> publicHolidays;
    private LinearLayout publicHolidayLayout;
    private DatabaseHelper dbHelper;

    public int currentmonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_calendar);
        dbHelper = new DatabaseHelper(this);



        CalendarView calendarView = findViewById(R.id.calendarView);
        publicHolidays = new HashMap<>();
        publicHolidayLayout = findViewById(R.id.publicHolidayLayout);
        dbHelper = new DatabaseHelper(this);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            String formattedDate = dateFormat.format(selectedCalendar.getTime());

            String[] dateParts = formattedDate.split("-");
            if (dateParts.length >= 3) {
                String monthDay = dateParts[1] + "-" + dateParts[2];

                if (publicHolidays.containsKey(monthDay)) {
                    String holidayName = publicHolidays.get(monthDay);
                    Toast.makeText(PersonalCalendar.this, "Selected Date: " + formattedDate + "\nHoliday: " + holidayName, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PersonalCalendar.this, "Selected Date: " + formattedDate, Toast.LENGTH_SHORT).show();
                }
            }
            currentmonth = month + 1; // Since Calendar.MONTH is zero-based
            // Fetch holidays for the selected month
            new FetchHolidayDataTask(currentmonth).execute();
        });



        currentmonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

        new FetchHolidayDataTask(currentmonth).execute();

        TextView saveTextView = findViewById(R.id.saveTv);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalCalendar.this, addCalendarEvent.class);
                startActivity(intent);
            }
        });

        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {

            publicHolidays.clear();
            publicHolidayLayout.removeAllViews();
            displayEventData();
        });
    }


    private void displayEventData() {
        // Add header title "Personal Event"
        TextView eventHeader = new TextView(this);
        eventHeader.setText("Personal Event");
        eventHeader.setTextSize(20);
        eventHeader.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerLayoutParams.setMargins(0, 20, 0, 10);
        eventHeader.setLayoutParams(headerLayoutParams);
        publicHolidayLayout.addView(eventHeader);

        // Display events
        List<DataModel> eventData = dbHelper.getAllEvents();
        for (DataModel event : eventData) {
            if (event.getDate() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd");
                try {
                    String eventDate = dateFormat.format(event.getDate());

                    TextView eventTextView = new TextView(this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 10, 0, 10);
                    eventTextView.setLayoutParams(layoutParams);

                    eventTextView.setBackgroundResource(R.drawable.custom_background);
                    eventTextView.setTextColor(Color.WHITE);
                    eventTextView.setPadding(20, 20, 20, 20);

                    eventTextView.setText(eventDate + "\n" + event.getTitle());

                    // Set onClickListener to delete the event
                    eventTextView.setOnClickListener(view -> {
                        boolean isDeleted = dbHelper.deleteEvent(event.getId());
                        if (isDeleted) {
                            Toast.makeText(PersonalCalendar.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                            // Remove the event from the layout
                            publicHolidayLayout.removeView(eventTextView);
                        } else {
                            Toast.makeText(PersonalCalendar.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                        }
                    });

                    publicHolidayLayout.addView(eventTextView);
                } catch (Exception e) {
                    Log.e("PersonalCalendar", "Error formatting date for event: " + event.getTitle(), e);
                }
            } else {
                Log.e("PersonalCalendar", "Null date string for event: " + event.getTitle());
                // Handle the case of a null date string, such as skipping the event or logging an error
            }
        }
    }





    private class FetchHolidayDataTask extends AsyncTask<Void, Void, String> {
        private int selectedMonth;

        public FetchHolidayDataTask(int selectedMonth) {
            this.selectedMonth = selectedMonth;
        }


        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/calendar/v3/calendars/en.malaysia%23holiday%40group.v.calendar.google.com/events?key=AIzaSyDLeynxdSYWILnpu1-PAQZOGDC1aaXcjZg")
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    return response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String responseBody) {
            if (responseBody != null) {
                parseHolidayData(responseBody, selectedMonth); // Pass selectedMonth
            } else {
                Toast.makeText(PersonalCalendar.this, "Failed to fetch holiday data", Toast.LENGTH_SHORT).show();
            }
        }


        private void parseHolidayData(String responseBody, int selectedMonth) {
            try {
                // Clear the existing holidays for the selected month
                publicHolidays.clear();

                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                // Add header title "Public Holidays"
                TextView holidayHeader = new TextView(PersonalCalendar.this);
                holidayHeader.setText("Public Holidays");
                holidayHeader.setTextSize(20);
                holidayHeader.setTextColor(Color.BLACK);
                LinearLayout.LayoutParams headerLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                headerLayoutParams.setMargins(0, 20, 0, 10);
                holidayHeader.setLayoutParams(headerLayoutParams);
                publicHolidayLayout.addView(holidayHeader);

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject itemObject = itemsArray.getJSONObject(i);
                    JSONObject startObject = itemObject.getJSONObject("start");
                    String holidayDateStr = startObject.getString("date");

                    String[] dateParts = holidayDateStr.split("-");
                    if (dateParts.length >= 3) {
                        int holidayMonth = Integer.parseInt(dateParts[1]);
                        if (holidayMonth == selectedMonth) {
                            // Extract month and day ignoring the year
                            holidayDateStr = dateParts[1] + "-" + dateParts[2];
                            String holidayName = itemObject.getString("summary");
                            publicHolidays.put(holidayDateStr, holidayName);
                        }
                    }
                }

                // Sort holidays by month and day
                List<String> sortedHolidayDates = new ArrayList<>(publicHolidays.keySet());
                Collections.sort(sortedHolidayDates);

                // Clear the layout before adding holidays to avoid duplication
                publicHolidayLayout.removeAllViews();

                // Re-add the header after clearing the layout
                publicHolidayLayout.addView(holidayHeader);

                for (String holidayDate : sortedHolidayDates) {
                    String holidayName = publicHolidays.get(holidayDate);

                    TextView holidayTextView = new TextView(PersonalCalendar.this);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 10, 0, 10);
                    holidayTextView.setLayoutParams(layoutParams);

                    holidayTextView.setBackgroundResource(R.drawable.custom_background);
                    holidayTextView.setTextColor(Color.WHITE);
                    holidayTextView.setPadding(20, 20, 20, 20);

                    holidayTextView.setText(holidayDate + "\n" + holidayName);

                    publicHolidayLayout.addView(holidayTextView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }




    }
}