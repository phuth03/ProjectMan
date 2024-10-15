package com.example.projectman;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectman.adapter.TaskAdapter;
import com.example.projectman.data.DataHelper;
import com.example.projectman.model.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements TaskAdapter.TaskItemListener {

    private DataHelper dataHelper;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Integer> taskIDList;
    private ArrayList<String> taskNameList;
    private ArrayList<Integer> estimateDayList;
    private ArrayList<String> assigneeList;
    private ArrayList<String> startDateList;
    private ArrayList<String> endDateList;
    private FloatingActionButton buttonAddTask;
    private int estimatedDays = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupBottomNavigation();
        setupRecyclerView();
        setupSearchView();
        setupAddTaskButton();
    }

    private void initializeViews() {
        dataHelper = new DataHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        buttonAddTask = findViewById(R.id.fabAdd);

        taskIDList = new ArrayList<>();
        taskNameList = new ArrayList<>();
        estimateDayList = new ArrayList<>();
        assigneeList = new ArrayList<>();
        startDateList = new ArrayList<>();
        endDateList = new ArrayList<>();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.recyclerView);
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        } catch (Exception e) {
            e.printStackTrace();
            // Log error
        }
    }

    private void setupRecyclerView() {
        fetchTasksFromDatabase();

        if (taskIDList.size() == taskNameList.size() &&
                taskNameList.size() == estimateDayList.size() &&
                estimateDayList.size() == assigneeList.size() &&
                assigneeList.size() == startDateList.size() &&
                startDateList.size() == endDateList.size()) {

            taskAdapter = new TaskAdapter(this, taskIDList, taskNameList, estimateDayList, assigneeList, startDateList, endDateList, this);
            recyclerView.setAdapter(taskAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            Toast.makeText(this, "Error: Task data is inconsistent.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                taskAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void setupAddTaskButton() {
        buttonAddTask.setOnClickListener(v -> showAddTaskDialog());
    }

    private void showAddTaskDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);

        EditText taskNameInput = dialogView.findViewById(R.id.taskNameInput);
        EditText assigneeInput = dialogView.findViewById(R.id.assigneeInput);
        EditText startDateInput = dialogView.findViewById(R.id.startDateInput);
        EditText endDateInput = dialogView.findViewById(R.id.endDateInput);
        TextView estimateDayDisplay = dialogView.findViewById(R.id.estimateDayDisplay);

        // Add TextWatcher to start and end date inputs
        TextWatcher dateWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Call updateEstimateDays with latest input
                updateEstimateDays(startDateInput.getText().toString(),
                        endDateInput.getText().toString(),
                        estimateDayDisplay);
            }
        };

        startDateInput.addTextChangedListener(dateWatcher);
        endDateInput.addTextChangedListener(dateWatcher);

        dialogView.findViewById(R.id.btnSaveTask).setOnClickListener(v -> {
            String taskName = taskNameInput.getText().toString().trim();
            String assignee = assigneeInput.getText().toString().trim();
            String startDate = startDateInput.getText().toString().trim();
            String endDate = endDateInput.getText().toString().trim();

            // Validate input
            if (taskName.isEmpty() || assignee.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate estimate days before saving
            int estimateDays = calculateEstimateDays(startDate, endDate);
            if (estimateDays == -1) {
                Toast.makeText(MainActivity.this, "Invalid dates. Please check your input.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Display the estimate day value
            estimateDayDisplay.setText("Estimate Days: " + estimateDays);

            // Save the task in the database
            long taskId = dataHelper.insertTask(taskName);
            if (taskId != -1) {
                long devTaskId = dataHelper.insertDevTask(assignee, taskId, startDate, endDate, estimateDays);
                if (devTaskId != -1) {
                    fetchTasksFromDatabase();
                    taskAdapter.notifyItemInserted(taskIDList.size() - 1);
                    bottomSheetDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Inserted Estimate Days: " + estimateDays, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "Failed to add dev task. TaskID: " + taskId);
                    Toast.makeText(MainActivity.this, "Failed to add dev task. Check logs for details.", Toast.LENGTH_LONG).show();
                    dataHelper.deleteTask((int) taskId); // Rollback on failure
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });


        bottomSheetDialog.setContentView(dialogView);
        bottomSheetDialog.show();
    }

    private int calculateEstimateDays(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            if (start != null && end != null && !end.before(start)) {
                long diffInMillies = end.getTime() - start.getTime();
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                return (int) diff + 1; // Include both start and end dates
            } else {
                return -1; // Invalid date range
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Parsing failed
        }
    }


    private void updateEstimateDays(String startDate, String endDate, TextView estimateDayDisplay) {
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                if (start != null && end != null && !end.before(start)) {
                    long diffInMillies = Math.abs(end.getTime() - start.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    int estimateDays = (int) diff + 1;  // Include both start and end dates

                    // Set only the number without extra text
                    estimateDayDisplay.setText(String.valueOf(estimateDays));
                } else {
                    estimateDayDisplay.setText("0");  // Invalid range case
                    Toast.makeText(this, "End date must be after or same as start date", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
                estimateDayDisplay.setText("0");
                Toast.makeText(this, "Invalid date format. Use yyyy-MM-dd.", Toast.LENGTH_SHORT).show();
            }
        } else {
            estimateDayDisplay.setText("0");  // If either date is empty
        }
    }

    private void fetchTasksFromDatabase() {
        taskIDList.clear();
        taskNameList.clear();
        assigneeList.clear();
        startDateList.clear();
        endDateList.clear();
        estimateDayList.clear();

        Cursor cursor = dataHelper.getCombinedTaskData();
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No task data found.", Toast.LENGTH_SHORT).show();
        } else {
            int taskIdIndex = cursor.getColumnIndex(DataHelper.COLUMN_TASK_ID);
            int taskNameIndex = cursor.getColumnIndex(DataHelper.COLUMN_TASK_NAME);
            int devNameIndex = cursor.getColumnIndex(DataHelper.COLUMN_DEV_NAME);
            int startDateIndex = cursor.getColumnIndex(DataHelper.COLUMN_STARTDATE);
            int endDateIndex = cursor.getColumnIndex(DataHelper.COLUMN_ENDDATE);
            int estimateDayIndex = cursor.getColumnIndex(DataHelper.COLUMN_TASK_ESTIMATE);

            while (cursor.moveToNext()) {
                if (taskIdIndex != -1) taskIDList.add(cursor.getInt(taskIdIndex));
                if (taskNameIndex != -1) taskNameList.add(cursor.getString(taskNameIndex));
                if (devNameIndex != -1) assigneeList.add(cursor.getString(devNameIndex));
                if (startDateIndex != -1) startDateList.add(cursor.getString(startDateIndex));
                if (endDateIndex != -1) endDateList.add(cursor.getString(endDateIndex));
                if (estimateDayIndex != -1) estimateDayList.add(cursor.getInt(estimateDayIndex));
            }
        }
        cursor.close();

        // Debug log to verify sizes
        Log.d("MainActivity", "taskIDList size: " + taskIDList.size());
        Log.d("MainActivity", "taskNameList size: " + taskNameList.size());

        Log.d("MainActivity", "estimateDayList size: " + estimateDayList.size());
        Log.d("MainActivity", "assigneeList size: " + assigneeList.size());
        Log.d("MainActivity", "startDateList size: " + startDateList.size());
        Log.d("MainActivity", "endDateList size: " + endDateList.size());
    }


    @Override
    public void onTaskUpdate(Task task) {
        // Implement task update logic
        // For example, show an edit task dialog
        Toast.makeText(this, "Update task: " + task.getTaskName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskDelete(Task task) {
        // Implement task delete logic
        boolean success = dataHelper.deleteTask(task.getTaskId());
        if (success) {
            fetchTasksFromDatabase();
            taskAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Task deleted: " + task.getTaskName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show();
        }
    }
}
