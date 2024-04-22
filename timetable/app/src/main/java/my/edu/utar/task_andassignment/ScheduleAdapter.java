package my.edu.utar.task_andassignment;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Schedule> scheduleList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public ScheduleAdapter(Context context, List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.startTimeTv.setText(schedule.getStartTime());
        holder.subjectTv.setText(schedule.getTitle());
        holder.timeTv.setText(schedule.getStartTime() + " - " + schedule.getEndTime());
        holder.descriptionTv.setText(schedule.getDescription());

        // Check if the color string is valid before setting the background color
        try {
            int colorValue = Color.parseColor(schedule.getColor());
            holder.scheduleLayout.setBackgroundColor(colorValue);
        } catch (IllegalArgumentException e) {
            // Handle invalid color strings here, such as setting a default color
            holder.scheduleLayout.setBackgroundColor(Color.BLUE); // Default color
            Log.e("ScheduleAdapter", "Invalid color string: " + schedule.getColor());
        }

        holder.editButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onEditClick(schedule, position);
            }
        });

        holder.deleteButton.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onDeleteClick(schedule, position);
            }
        });
    }

    public void setSchedules(List<Schedule> schedules) {

        this.scheduleList = schedules;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onEditClick(Schedule schedule, int position);

        void onDeleteClick(Schedule schedule, int position);
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView startTimeTv, subjectTv, timeTv, descriptionTv;
        ImageButton editButton, deleteButton;

        LinearLayout scheduleLayout;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            startTimeTv = itemView.findViewById(R.id.startTimeTv);
            subjectTv = itemView.findViewById(R.id.subjectTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            scheduleLayout = itemView.findViewById(R.id.scheduleLayout);
        }
    }
}