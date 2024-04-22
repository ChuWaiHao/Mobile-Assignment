package my.edu.utar.task_andassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {

    private EditText titleEt, descriptionEt, startTimeEt, endTimeEt, colorEt;
    private Spinner dayOfWeekSpinner;

    private TextView cancelTv, saveTv;

    private String dayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        cancelTv = findViewById(R.id.cancelTv);
        saveTv = findViewById(R.id.saveTv);

        titleEt = findViewById(R.id.titleEt);
        startTimeEt = findViewById(R.id.startTimeEt);
        endTimeEt = findViewById(R.id.endTimeEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        colorEt = findViewById(R.id.colorEt);

        initializeDayOfWeekSpinner();


        TimePickerDialog.OnTimeSetListener startTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update the EditText with the selected time
                String time = String.format("%02d:%02d", hourOfDay, minute);
                startTimeEt.setText(time);
            }
        };

        TimePickerDialog.OnTimeSetListener endTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Update the EditText with the selected time
                String time = String.format("%02d:%02d", hourOfDay, minute);
                endTimeEt.setText(time);
            }
        };

        startTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddScheduleActivity.this,
                        startTimeSetListener,
                        0, 0, true);
                timePickerDialog.getWindow().setGravity(Gravity.CENTER);
                timePickerDialog.updateTime(12, 0);
                timePickerDialog.setTitle("Select Start Time");
                timePickerDialog.show();
                timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(startTimeEt);
            }
        });

        endTimeEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        AddScheduleActivity.this,
                        endTimeSetListener,
                        0, 0, true);
                timePickerDialog.getWindow().setGravity(Gravity.CENTER);
                timePickerDialog.updateTime(12, 0);
                timePickerDialog.setTitle("Select End Time");
                timePickerDialog.show();
                timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#000000"));
                timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTag(endTimeEt);
            }
        });

        saveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSchedule();
            }
        });

        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void addSchedule() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        String title = titleEt.getText().toString().trim();
        String dayOfWeek = dayOfWeekSpinner.getSelectedItem().toString();
        String startTime = startTimeEt.getText().toString();
        String endTime = endTimeEt.getText().toString();
        String color = colorEt.getText().toString().trim();
        String description = descriptionEt.getText().toString();
        long id = dbHelper.insertSchedule(title, dayOfWeek, startTime, endTime, color, description);
        if (id != -1) {
            Toast.makeText(this, "Schedule Added Successfully...", Toast.LENGTH_SHORT).show();
            // data inserted successfully
        } else {
            Toast.makeText(this, "Failed to add schedule...", Toast.LENGTH_SHORT).show();
            // error occurred while inserting data
        }
    }

    private void initializeDayOfWeekSpinner() {
        //Category Spinner
        dayOfWeekSpinner = findViewById(R.id.dayOfWeekSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_items, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dayOfWeekSpinner.setAdapter(adapter);

        // Set an item selected listener if needed
        dayOfWeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item as a string
                String selectedItem = parent.getItemAtPosition(position).toString();

                // Set the productCategory variable to the selected item
                dayOfWeek = selectedItem;

            }




            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


}