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

        buttonAddTask.setOnClickListener(v -> addTask());

        builder.setView(view)
                .setTitle("Add New Task")
                .setNegativeButton("Cancel", (dialog, id) -> dismiss());
        return builder.create();
    }

    private void addTask() {
        String taskName = editTextTaskName.getText().toString().trim();
        String devName = "Your Assignee Name";
        String startDate = "2024/05/03";
        int estimateDay = 5;
        String endDate = "2024/05/05";

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
