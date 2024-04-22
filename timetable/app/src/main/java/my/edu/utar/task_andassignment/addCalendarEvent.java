package my.edu.utar.task_andassignment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class addCalendarEvent extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, dateEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcalendarevent);

        titleEditText = findViewById(R.id.titleEt); // Add this line
        descriptionEditText = findViewById(R.id.descriptionEt); // Add this line
        dateEditText = findViewById(R.id.dateEt);
        ImageButton datePickerButton = findViewById(R.id.datePickerBtn);

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        TextView saveButton = findViewById(R.id.saveTv);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEventData();
            }
        });


        TextView saveTextView = findViewById(R.id.cancelTv);
        saveTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(addCalendarEvent.this, PersonalCalendar.class);
                startActivity(intent);
            }
        });

        // Initialize the database helper
        dbHelper = new DatabaseHelper(this);
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Adjust month by 1 since Calendar.MONTH is zero-based
                        month = month + 1;
                        // Format the selected date as "yyyy-MM-dd"
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth);
                        dateEditText.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }


    private void saveEventData() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String date = dateEditText.getText().toString();

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && !TextUtils.isEmpty(date)) {
            DataModel data = new DataModel(title, description, date);
            long result = dbHelper.insertEvent(data);

            if (result != -1) {
                Toast.makeText(addCalendarEvent.this, "Event saved successfully!", Toast.LENGTH_SHORT).show();
                // Clear input fields after saving
                titleEditText.setText("");
                descriptionEditText.setText("");
                dateEditText.setText("");
            } else {
                Toast.makeText(addCalendarEvent.this, "Failed to save event", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(addCalendarEvent.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
