package com.example.projectman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectman.R;
import com.example.projectman.model.Task;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Integer> taskIDList;
    private ArrayList<String> taskNameList;
    private ArrayList<Integer> estimateDayList;
    private ArrayList<String> assigneeList;
    private ArrayList<String> startDateList;
    private ArrayList<String> endDateList;
    private TaskItemListener taskItemListener;
    private List<Task> taskListFull;

    public TaskAdapter(Context context,
                       ArrayList<Integer> taskIDList,
                       ArrayList<String> taskNameList,
                       ArrayList<Integer> estimateDayList,
                       ArrayList<String> assigneeList,
                       ArrayList<String> startDateList,
                       ArrayList<String> endDateList,
                       TaskItemListener taskItemListener) {
        this.context = context;
        this.taskIDList = taskIDList;
        this.taskNameList = taskNameList;
        this.estimateDayList = estimateDayList;
        this.assigneeList = assigneeList;
        this.startDateList = startDateList;
        this.endDateList = endDateList;
        this.taskItemListener = taskItemListener;


        taskListFull = new ArrayList<>();
        for (int i = 0; i < taskIDList.size(); i++) {
            taskListFull.add(new Task(
                    taskIDList.get(i),
                    taskNameList.get(i),
                    assigneeList.get(i),
                    estimateDayList.get(i),
                    startDateList.get(i),
                    endDateList.get(i)
            ));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.taskName.setText(taskNameList.get(position));
        holder.assignee.setText(assigneeList.get(position));
        holder.startDate.setText(startDateList.get(position));
        holder.endDate.setText(endDateList.get(position));
        holder.estimateDay.setText(String.valueOf(estimateDayList.get(position)));


        if (holder.deleteButton != null) {
            holder.deleteButton.setOnClickListener(v -> {
                Task task = new Task(
                        taskIDList.get(position),
                        taskNameList.get(position),
                        assigneeList.get(position),
                        estimateDayList.get(position),
                        startDateList.get(position),
                        endDateList.get(position)
                );
                taskItemListener.onTaskDelete(task);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Task task = new Task(
                    taskIDList.get(position),
                    taskNameList.get(position),
                    assigneeList.get(position),
                    estimateDayList.get(position),
                    startDateList.get(position),
                    endDateList.get(position)
            );
            taskItemListener.onTaskUpdate(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskNameList.size();
    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private Filter taskFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Task> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(taskListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Task task : taskListFull) {
                    if (task.getTaskName().toLowerCase().contains(filterPattern) ||
                            task.getAssignee().toLowerCase().contains(filterPattern)) {
                        filteredList.add(task);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            taskIDList.clear();
            taskNameList.clear();
            estimateDayList.clear();
            assigneeList.clear();
            startDateList.clear();
            endDateList.clear();

            List<Task> filteredTasks = (List<Task>) results.values;
            for (Task task : filteredTasks) {
                taskIDList.add(task.getTaskId());
                taskNameList.add(task.getTaskName());
                estimateDayList.add(task.getEstimateDay());
                assigneeList.add(task.getAssignee());
                startDateList.add(task.getStartDate());
                endDateList.add(task.getEndDate());
            }
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, assignee, estimateDay, startDate, endDate;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            assignee = itemView.findViewById(R.id.assigneeName);
            estimateDay = itemView.findViewById(R.id.estimateDay);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            deleteButton = itemView.findViewById(R.id.fabDelete);
        }
    }

    public interface TaskItemListener {
        void onTaskUpdate(Task task);
        void onTaskDelete(Task task);
    }
}
