package com.example.projectman.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.projectman.R;

import org.w3c.dom.Text;

public class AddTaskFragment extends DialogFragment {

    public interface AddTaskListener {
        void onTaskAdded(String taskName, String devName, String startDate, int estimateDay, String endDate);
    }

    private EditText editTextTaskName;
    private EditText editTextAssignee;
    private EditText editTextStartDate;
    private TextView editTextEstimateDate;
    private EditText editTextEndDate;
    private Spinner spinnerStatus;
    private AddTaskListener listener;

    public void setAddTaskListener(AddTaskListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_task, null);

        editTextTaskName = view.findViewById(R.id.taskNameInput);
        editTextAssignee = view.findViewById(R.id.assigneeInput);
        editTextStartDate = view.findViewById(R.id.startDateInput);
        editTextEndDate = view.findViewById(R.id.endDateInput);
        editTextEstimateDate = view.findViewById(R.id.estimateDayDisplay);

        Button buttonAddTask = view.findViewById(R.id.btnSaveTask);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.status_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        buttonAddTask.setOnClickListener(v -> addTask());

        builder.setView(view)
                .setTitle("Add New Task")
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
//        if (editTextTaskName.length() > 0 || editTextAssignee.length() >0
//                || editTextStartDate.length() >0 || editTextEndDate.length() >0
//                || editTextEstimateDate.length() >0) {
//            editTextTaskName.setText("Task Name");
//            editTextAssignee.setText("Assignee Name");
//            editTextStartDate.setText("Start Date");
//            editTextEndDate.setText("End Date");
//            editTextEstimateDate.setText("Estimate Days");
//        }
        return builder.create();
    }

    private void addTask() {
        String taskName = editTextTaskName.getText().toString().trim();
        String devName = "Your Assignee Name"; // TODO: Implement dynamic assignment
        String startDate = "2024/05/03"; // TODO: Implement date picker
        int estimateDay = 5; // TODO: Implement estimate input
        String endDate = "2024/05/05"; // TODO: Calculate based on start date and estimate

        if (taskName.isEmpty()) {
            editTextTaskName.setError("Task name cannot be empty");
            return;
        }

        if (listener != null) {
            listener.onTaskAdded(taskName, devName, startDate, estimateDay, endDate);
        }
        dismiss();
    }
}
