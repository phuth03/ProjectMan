package com.example.projectman.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "task")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private int taskId;
    private String taskName;
    private String assignee;
    private int estimateDay;
    private String startDate;
    private String endDate;

    public Task(int taskId, String taskName, String assignee, int estimateDay, String startDate, String endDate) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.assignee = assignee;
        this.estimateDay = estimateDay;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    // Getters and Setters
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getAssignee() { return assignee; }
    public void setAssignee(String assignee) { this.assignee = assignee; }

    public int getEstimateDay() { return estimateDay; }
    public void setEstimateDay(int estimateDay) { this.estimateDay = estimateDay; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", assignee='" + assignee + '\'' +
                ", estimateDay=" + estimateDay +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
