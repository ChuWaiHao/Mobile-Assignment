package my.edu.utar.task_andassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class MainActivity extends AppCompatActivity {

    //private ImageButton chatButton;
    private Button taskButton,plusButton;
    private TextView mondayTv, tuesdayTv, wednesdayTv, thursdayTv, fridayTv;

    private List<Schedule> scheduleList;

    private RecyclerView scheduleRv;

    private ScheduleAdapter scheduleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        plusButton = findViewById(R.id.plusButton);
        mondayTv = findViewById(R.id.mondayTv);
        tuesdayTv = findViewById(R.id.tuesdayTv);
        wednesdayTv = findViewById(R.id.wednesdayTv);
        thursdayTv = findViewById(R.id.thursdayTv);
        fridayTv = findViewById(R.id.fridayTv);
        scheduleRv = findViewById(R.id.scheduleRv);
        taskButton = findViewById(R.id.taskButton);

        scheduleRv.setLayoutManager(new LinearLayoutManager(this));
        scheduleList = new ArrayList<>(); // Add this line
        scheduleAdapter = new ScheduleAdapter(this, scheduleList);
        scheduleRv.setAdapter(scheduleAdapter);

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            }
        });

        taskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the "MON" TextView
        mondayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMondaySchedules();
                highlightSelectedDay(mondayTv);
            }
        });

        // Set OnClickListener for the "Tue" TextView
        tuesdayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTuesdaySchedules();
                highlightSelectedDay(tuesdayTv);
            }
        });

        // Set OnClickListener for the "Wed" TextView
        wednesdayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWednesdaySchedules();
                highlightSelectedDay(wednesdayTv);
            }
        });

        // Set OnClickListener for the "Thu" TextView
        thursdayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadThursdaySchedules();
                highlightSelectedDay(thursdayTv);
            }
        });

        // Set OnClickListener for the "Fri" TextView
        fridayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFridaySchedules();
                highlightSelectedDay(fridayTv);
            }
        });

        // Automatically select the TextView corresponding to the current day
        if (dayOfWeek == Calendar.MONDAY) {
            mondayTv.performClick();
        } else if (dayOfWeek == Calendar.TUESDAY) {
            tuesdayTv.performClick();
        } else if (dayOfWeek == Calendar.WEDNESDAY) {
            wednesdayTv.performClick();
        } else if (dayOfWeek == Calendar.THURSDAY) {
            thursdayTv.performClick();
        } else if (dayOfWeek == Calendar.FRIDAY) {
            fridayTv.performClick();
        }

        scheduleAdapter.setOnItemClickListener(new ScheduleAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Schedule schedule, int position) {
                Intent intent = new Intent(MainActivity.this, EditScheduleActivity.class);
                intent.putExtra("id", schedule.getId());
                intent.putExtra("title", schedule.getTitle());
                intent.putExtra("dayOfWeek", schedule.getDayOfWeek());
                intent.putExtra("startTime", schedule.getStartTime());
                intent.putExtra("endTime", schedule.getEndTime());
                intent.putExtra("color", schedule.getColor());
                intent.putExtra("description", schedule.getDescription());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Schedule schedule, int position) {
                showDeleteConfirmationDialog(schedule.getId());
            }
        });

    }

    // update the schedules
    private void updateSchedules(List<Schedule> schedules) {
        scheduleAdapter.setSchedules(schedules);
        scheduleAdapter.notifyDataSetChanged();
    }



    private void highlightSelectedDay(TextView selectedDay) {
        // Reset the text color of all day TextViews to the original color
        mondayTv.setTextColor(Color.parseColor("#554F4F"));
        tuesdayTv.setTextColor(Color.parseColor("#554F4F"));
        wednesdayTv.setTextColor(Color.parseColor("#554F4F"));
        thursdayTv.setTextColor(Color.parseColor("#554F4F"));
        fridayTv.setTextColor(Color.parseColor("#554F4F"));

        // Set the text color of the selected day to #EEEEEE
        selectedDay.setTextColor(Color.parseColor("#EEEEEE"));
    }

    private void showDeleteConfirmationDialog(final long scheduleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Schedule");
        builder.setMessage("Are you sure you want to delete this schedule?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(MainActivity.this);
                boolean isDeleted = dbHelper.deleteSchedule(scheduleId);
                if (isDeleted) {
                    Toast.makeText(MainActivity.this, "Schedule Deleted Successfully", Toast.LENGTH_SHORT).show();
                    loadSchedules(); // Reload schedules after deletion
                    scheduleAdapter.setSchedules(scheduleList); // Update the adapter with the new schedule list
                } else {
                    Toast.makeText(MainActivity.this, "Failed to delete schedule", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void loadSchedules() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        scheduleList = dbHelper.getAllSchedules();
    }

    private void loadMondaySchedules() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        scheduleList = dbHelper.getMondaySchedules();
        updateSchedules(scheduleList);
    }

    private void loadTuesdaySchedules() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        scheduleList = dbHelper.getTuesdaySchedules();
        updateSchedules(scheduleList);
    }

    private void loadWednesdaySchedules() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        scheduleList = dbHelper.getWednesdaySchedules();
        updateSchedules(scheduleList);
    }

    private void loadThursdaySchedules() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        scheduleList = dbHelper.getThursdaySchedules();
        updateSchedules(scheduleList);
    }

    private void loadFridaySchedules() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        scheduleList = dbHelper.getFridaySchedules();
        updateSchedules(scheduleList);
    }
}