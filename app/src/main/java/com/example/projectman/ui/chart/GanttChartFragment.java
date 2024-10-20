package com.example.projectman.ui.chart;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.projectman.R;
import com.example.projectman.data.DataHelper;
import com.example.projectman.model.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GanttChartFragment extends Fragment {
    private GanttChartView ganttChartView;
    private DataHelper dataHelper;
    private Button datePickerButton;
    private Spinner taskSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gantt_chart_layout, container, false);

        ganttChartView = view.findViewById(R.id.gantt_chart);
        datePickerButton = view.findViewById(R.id.date_picker_button);
        taskSpinner = view.findViewById(R.id.task_spinner);

        dataHelper = new DataHelper(requireContext());

        setupGanttChart();
        setupDatePicker();
        setupTaskSpinner();

        return view;
    }

    private void setupGanttChart() {
        List<Task> tasks = dataHelper.getGanttChartData();
        if (tasks == null) {
            tasks = new ArrayList<>(); // Provide an empty list if null
        }
        ganttChartView.setTasks(tasks);
    }

    private void setupDatePicker() {
        datePickerButton.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        // Handle the selected date
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        ganttChartView.setStartDate(selectedDate.getTime());
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupTaskSpinner() {
        List<String> taskNames = dataHelper.getTaskNames();
        if (taskNames == null) {
            taskNames = new ArrayList<>(); // Provide an empty list if null
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, taskNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taskSpinner.setAdapter(adapter);

        taskSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTask = (String) parent.getItemAtPosition(position);
                // Handle the selected task
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
}