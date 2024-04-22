package my.edu.utar.task_andassignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class task_view extends AppCompatActivity {


    //system will run this page first (a "task and assignment" button)
    //Easy to combine when integrating everything
    private Button task_button;
    private Button timetable_button;

    private Button saving_button;
    private Button calendar_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        task_button = findViewById(R.id.task_button);
        task_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(task_view.this, MainActivity.class);

                startActivity(intent);
            }
        });
        timetable_button = findViewById(R.id.timetable_button);
        timetable_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(task_view.this, ShowTimetable.class);

                startActivity(intent);
            }
        });
        saving_button = findViewById(R.id.saving_button);
        saving_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(task_view.this, SavingMainActivity.class);

                startActivity(intent);
            }
        });

        calendar_button = findViewById(R.id.calendar_button);
        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(task_view.this, PersonalCalendar.class);

                startActivity(intent);
            }
        });
    }
}
