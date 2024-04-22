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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    //add task
    private ImageButton plusButton;
    private TextView inProgressTv, completedTv;
    private RecyclerView taskRv;

    private List<Task> taskList;

    private TaskAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        plusButton = findViewById(R.id.plusButton);
        inProgressTv = findViewById(R.id.inProgressTv);
        completedTv = findViewById(R.id.completedTv);
        taskRv = findViewById(R.id.taskRv);

        taskRv.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, taskList);
        taskRv.setAdapter(taskAdapter);

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        inProgressTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInProgressTasks();
                highlightSelectedStatus(inProgressTv);
            }
        });

        completedTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadCompletedTasks();
                highlightSelectedStatus(completedTv);
            }
        });

        taskAdapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(Task task, int position) {
                Intent intent = new Intent(TaskActivity.this, EditTaskActivity.class);
                intent.putExtra("id", task.getId());
                intent.putExtra("taskName", task.getTaskName());
                intent.putExtra("date", task.getDate());
                intent.putExtra("time", task.getTime());
                intent.putExtra("description", task.getDescription());
                intent.putExtra("completed", task.getCompleted());
                intent.putExtra("subjectId", task.getSubjectId());

                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Task task, int position) {
                showDeleteConfirmationDialog(task.getId());
            }

            @Override
            public void onCompletedClick(Task task, int position) {
                TimetableDatabaseHelper timetableDatabaseHelper = new TimetableDatabaseHelper(TaskActivity.this);

                if(task.getCompleted().equals("inProgress")){
                    timetableDatabaseHelper.updateTaskToCompleted(task.getId());
                    taskList.clear();
                    taskList.addAll(timetableDatabaseHelper.getInProgressTasks());
                    taskAdapter.notifyDataSetChanged();
                }
                else{
                    timetableDatabaseHelper.updateTaskToInProgress(task.getId());
                    taskList.clear();
                    taskList.addAll(timetableDatabaseHelper.getCompletedTasks());
                    taskAdapter.notifyDataSetChanged();
                }


            }
        });

        inProgressTv.performClick();

    }

    private void loadCompletedTasks() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        taskList = dbHelper.getCompletedTasks();
        updateTask(taskList);
    }

    private void highlightSelectedStatus(TextView selectedStatus) {
        inProgressTv.setTextColor(Color.parseColor("#554F4F"));
        completedTv.setTextColor(Color.parseColor("#554F4F"));

        // Set the text color of the selected day to #EEEEEE
        selectedStatus.setTextColor(Color.parseColor("#EEEEEE"));
    }

    private void updateTask(List<Task> tasks) {
        taskAdapter.setTasks(tasks);
        taskAdapter.notifyDataSetChanged();
    }

    private void loadInProgressTasks() {
        TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(this);
        taskList = dbHelper.getInProgressTasks();
        updateTask(taskList);
    }

    private void showDeleteConfirmationDialog(final int taskId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Schedule");
        builder.setMessage("Are you sure you want to delete this schedule?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TimetableDatabaseHelper dbHelper = new TimetableDatabaseHelper(TaskActivity.this);
                boolean isDeleted = dbHelper.deleteTask(taskId);
                if (isDeleted) {
                    Toast.makeText(TaskActivity.this, "Schedule Deleted Successfully", Toast.LENGTH_SHORT).show();
                    loadInProgressTasks(); // Reload schedules after deletion
                    taskAdapter.setTasks(taskList); // Update the adapter with the new schedule list
                } else {
                    Toast.makeText(TaskActivity.this, "Failed to delete schedule", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}