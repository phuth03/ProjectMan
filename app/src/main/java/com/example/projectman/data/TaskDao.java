package com.example.projectman.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.projectman.model.Task;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(Task task);

    // Update an existing Task
    @Update
    void updateTask(Task task);

    // Delete a specific Task
    @Delete
    void deleteTask(Task task);

    // Get all Tasks
    @Query("SELECT * FROM task ORDER BY taskId ASC")
    LiveData<List<Task>> getAllTasks();
//    @Query("SELECT * FROM task WHERE taskId = :taskId")
//    Task getTaskById(int taskId);
//
//    // Use raw string "TASK_NAME" instead of COLUMN_TASK_NAME
//    @Query("SELECT * FROM task WHERE taskName LIKE :taskName")
//    LiveData<List<Task>> getTasksByName(String taskName);
}

