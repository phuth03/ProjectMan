package com.example.projectman.ui.home;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.projectman.R;
import com.example.projectman.adapter.TaskAdapter;
import com.example.projectman.data.DataHelper;
import com.example.projectman.model.Task;
import com.example.projectman.ui.AddTaskFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment implements TaskAdapter.TaskItemListener {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private DataHelper db;
    private ArrayList<Integer> taskIDList;
    private ArrayList<String> taskNameList;
    private ArrayList<Integer> estimateDayList;
    private ArrayList<String> assigneeList;
    private ArrayList<String> startDateList;
    private ArrayList<String> endDateList;
    private FloatingActionButton buttonAddTask; // Declare the button



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));




        taskList = new ArrayList<>();
        db = new DataHelper(getContext());
        taskIDList = new ArrayList<>(); // Now Integer type
        taskNameList = new ArrayList<>();
        estimateDayList = new ArrayList<>();
        assigneeList = new ArrayList<>(); // Initialize
        startDateList = new ArrayList<>(); // Initialize
        endDateList = new ArrayList<>(); // Initialize


        loadData();



        taskAdapter = new TaskAdapter(getContext(), taskIDList, taskNameList, estimateDayList, assigneeList, startDateList, endDateList, this);

        recyclerView.setAdapter(taskAdapter);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void loadData() {
        Cursor cursor = db.getDevTaskData();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int taskIdIndex = cursor.getColumnIndex("TASK_ID");
                int taskNameIndex = cursor.getColumnIndex("TASK_NAME");
                int devNameIndex = cursor.getColumnIndex("DEV_NAME");
                int estimateDayIndex = cursor.getColumnIndex("ESTIMATE_DAY");
                int startDateIndex = cursor.getColumnIndex("STARTDATE");
                int endDateIndex = cursor.getColumnIndex("ENDDATE");

                if (taskIdIndex != -1 && taskNameIndex != -1 && devNameIndex != -1 &&
                        estimateDayIndex != -1 && startDateIndex != -1 && endDateIndex != -1) {
                    Task task = new Task(
                            cursor.getInt(taskIdIndex),
                            cursor.getString(taskNameIndex),
                            cursor.getString(devNameIndex),
                            cursor.getInt(estimateDayIndex),
                            cursor.getString(startDateIndex),
                            cursor.getString(endDateIndex)
                    );
                    taskList.add(task);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
    }


    @Override
    public void onTaskUpdate(Task task) {
        // Implement task update logic
        if (isTimeOverlap(task)) {
            createOverlapNotification(task);
        }
        db.updateTask(task);
        loadData();
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskDelete(Task task) {
        // Implement task delete logic
        db.deleteTask(task.getTaskId());
        loadData();
        taskAdapter.notifyDataSetChanged();
    }

    private boolean isTimeOverlap(Task updatedTask) {
        // Implement time overlap check logic
        return false; // Placeholder
    }

    private void createOverlapNotification(Task task) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String message = "Task " + task.getTaskName() + " causes an overlap to other tasks when updating at " + currentTime;
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        // You might want to save this notification to a database or show it in a dedicated notifications area
    }

}