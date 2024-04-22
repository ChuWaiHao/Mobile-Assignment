package my.edu.utar.task_andassignment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;

    private OnItemClickListener onItemClickListener;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TimetableDatabaseHelper dbHelper = TimetableDatabaseHelper.getInstance(context);

        Task task = taskList.get(position);

        if(task.getCompleted().equals("Completed")){
            holder.taskTv.setPaintFlags(holder.taskTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.taskTv.setPaintFlags(holder.taskTv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        holder.taskTv.setText(task.getTaskName());

        String subjectTitle = dbHelper.getSubjectTitleById(task.getSubjectId());
        holder.subjectTv.setText(subjectTitle);

        String subjectColor = dbHelper.getSubjectColorById(task.getSubjectId());
        if (subjectColor != null && !subjectColor.isEmpty()) {
            holder.taskLayout.setBackgroundColor(Color.parseColor(subjectColor));
        } else {
            holder.taskLayout.setBackgroundColor(Color.BLUE); // Default color
        }

        String date = task.getDate();
        String dayOfWeek = getDayOfWeek(date);
        holder.dayofWeekTv.setText(dayOfWeek);


        holder.dateTv.setText(task.getDate());

        holder.descriptionTv.setText(task.getDescription());

        holder.editButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(task, position);
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            // Implement delete functionality here
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(task, position);
            }
        });

        holder.isCompletedButton.setOnClickListener(view -> {
            // Implement mark as completed functionality here
            if (onItemClickListener != null) {
                onItemClickListener.onCompletedClick(task, position);
            }
        });
    }

    public String getDayOfWeek(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = dateFormat.parse(dateString);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

            return dayFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks;
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEditClick(Task schedule, int position);

        void onDeleteClick(Task schedule, int position);

        void onCompletedClick(Task schedule, int position);
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTv, subjectTv, dateTv, dayofWeekTv, descriptionTv;
        ImageButton editButton, deleteButton, isCompletedButton;
        RelativeLayout taskLayout;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskLayout = itemView.findViewById(R.id.taskLayout);
            taskTv = itemView.findViewById(R.id.taskTv);
            subjectTv = itemView.findViewById(R.id.subjectTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            dayofWeekTv = itemView.findViewById(R.id.dayofWeekTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            isCompletedButton = itemView.findViewById(R.id.isCompletedButton);
        }
    }
}
