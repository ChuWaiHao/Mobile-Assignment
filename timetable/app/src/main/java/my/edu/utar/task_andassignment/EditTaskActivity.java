package my.edu.utar.task_andassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.List;

public class EditTaskActivity extends AppCompatActivity {

    private TextView cancelTv, saveTv;
    private EditText titleEt, dateEt, timeEt, descriptionEt;
    private Spinner subjectSpinner;

    private int id;
    private String taskName;
    private String date;
    private String time;
    private String description;
    private String completed;
    private int subjectId;

    TimetableDatabaseHelper timetableDatabaseHelper = TimetableDatabaseHelper.getInstance(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        cancelTv = findViewById(R.id.cancelTv);
        saveTv = findViewById(R.id.saveTv);
        titleEt = findViewById(R.id.titleEt);
        dateEt = findViewById(R.id.dateEt);
        timeEt = findViewById(R.id.timeEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        subjectSpinner = findViewById(R.id.subjectSpinner);

        initializeSubjectSpinner();

        dateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                                dateEt.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update the EditText with the selected time
                String time = String.format("%02d:%02d", hourOfDay, minute);
                timeEt.setText(time);
            }
        };

        timeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        EditTaskActivity.this,
                        timeSetListener,
                        0, 0, true);
                timePickerDialog.getWindow().setGravity(Gravity.CENTER);
                timePickerDialog.updateTime(12, 0);
                timePickerDialog.setTitle("Select Start Time");
                timePickerDialog.show();
                timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(timeEt);
            }
        });

        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        taskName = intent.getStringExtra("taskName");
        date = intent.getStringExtra("date");
        time = intent.getStringExtra("time");
        description = intent.getStringExtra("description");
        completed = intent.getStringExtra("completed");
        subjectId = intent.getIntExtra("subjectId", -1);

        if (id != -1){
            titleEt.setText(taskName);
            dateEt.setText(date);
            timeEt.setText(time);
            descriptionEt.setText(description);
            String subjectTitle = timetableDatabaseHelper.getSubjectTitleById(subjectId);
            int spinnerPosition = ((ArrayAdapter<String>) subjectSpinner.getAdapter()).getPosition(subjectTitle);
            subjectSpinner.setSelection(spinnerPosition);
        }

    }

    private void updateTask() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);

        String title = titleEt.getText().toString().trim();
        String date = dateEt.getText().toString().trim();
        String time = timeEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String subjectTitle = subjectSpinner.getSelectedItem().toString();

        boolean isSuccess = dbHelper.updateTask(id, title, date, time, description, subjectTitle);

        if (isSuccess) {
            Toast.makeText(this, "Task updated successfully...", Toast.LENGTH_SHORT).show();
            // data updated successfully
        } else {
            Toast.makeText(this, "Failed to update task...", Toast.LENGTH_SHORT).show();
            // error occurred while updating data
        }
    }



    private void initializeSubjectSpinner() {
        // Fetch the schedule titles from the database
        TimetableDatabaseHelper dbHelper = TimetableDatabaseHelper.getInstance(this);
        List<String> scheduleTitles = dbHelper.getScheduleTitles();

        // Create an ArrayAdapter with the schedule titles
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scheduleTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Set the ArrayAdapter to your subjectSpinner
        subjectSpinner = findViewById(R.id.subjectSpinner);
        subjectSpinner.setAdapter(adapter);
    }


}