package my.edu.utar.task_andassignment;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

public class AddTaskActivity extends AppCompatActivity {

    private TextView cancelTv, saveTv;
    private EditText titleEt, dateEt, timeEt, descriptionEt;
    private Spinner subjectSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

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
                        AddTaskActivity.this,
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
                        AddTaskActivity.this,
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
                addTask();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void addTask() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);

        String title = titleEt.getText().toString().trim();
        String date = dateEt.getText().toString().trim();
        String time = timeEt.getText().toString();
        String description = descriptionEt.getText().toString();
        String status = "inProgress";
        String subjectTitle = subjectSpinner.getSelectedItem().toString();

        long id = dbHelper.insertTask(title, date, time, description, subjectTitle, status);
        if (id != -1) {
            Toast.makeText(this, "Task Added Successfully...", Toast.LENGTH_SHORT).show();
            // data inserted successfully
        } else {
            Toast.makeText(this, "Failed to add task...", Toast.LENGTH_SHORT).show();
            // error occurred while inserting data
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