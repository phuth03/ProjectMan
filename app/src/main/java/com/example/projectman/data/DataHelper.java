package com.example.projectman.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.projectman.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DataHelper extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "app_database.db";
    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Table Names
    public static final String TABLE_TASK = "task";
    public static final String TABLE_DEV_TASK = "dev_task";

    // Task Table Columns
    public static final String COLUMN_TASK_ID = "ID";
    public static final String COLUMN_TASK_NAME = "TASK_NAME";
    public static final String COLUMN_TASK_ESTIMATE = "ESTIMATE_DAY";

    // Dev Task Table Columns
    public static final String COLUMN_DEV_TASK_ID = "ID";
    public static final String COLUMN_DEV_NAME = "DEV_NAME";
    public static final String COLUMN_TASKID = "TASKID";
    public static final String COLUMN_STARTDATE = "STARTDATE";
    public static final String COLUMN_ENDDATE = "ENDDATE";

    // Create table SQL statements
    private static final String CREATE_TABLE_TASK = "CREATE TABLE "
            + TABLE_TASK + "(" + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TASK_NAME + " TEXT, "
            + COLUMN_TASK_ESTIMATE + " INTEGER" + ")";

    private static final String CREATE_TABLE_DEV_TASK = "CREATE TABLE "
            + TABLE_DEV_TASK + "("
            + COLUMN_DEV_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DEV_NAME + " TEXT, "
            + COLUMN_TASKID + " INTEGER, "
            + COLUMN_STARTDATE + " TEXT, "
            + COLUMN_ENDDATE + " TEXT, "
            + COLUMN_TASK_ESTIMATE + " INTEGER, "
            + "FOREIGN KEY(" + COLUMN_TASKID + ") REFERENCES " + TABLE_TASK + "(" + COLUMN_TASK_ID + "))";

    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_TASK);
        db.execSQL(CREATE_TABLE_DEV_TASK);

        // Insert sample data
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade here
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEV_TASK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }

    // Insert sample data
    private void insertSampleData(SQLiteDatabase db) {
        // Insert data into `task` table
        db.execSQL("INSERT INTO " + TABLE_TASK + " (TASK_NAME, ESTIMATE_DAY) VALUES ('Order list', 5), " +
                "('Order detail', 3), " +
                "('Product list', 3), " +
                "('Product detail', 3), " +
                "('Coupon list', 3)");

        // Check number of rows in task table
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TASK, null);
        if (cursor.moveToFirst()) {
            Log.d("DataHelper", "Number of tasks inserted: " + cursor.getInt(0));
        }
        cursor.close();

        // Insert data into `dev_task` table
        db.execSQL("INSERT INTO " + TABLE_DEV_TASK + " (DEV_NAME, TASKID, STARTDATE, ENDDATE) VALUES ('Ramesh', 3, '2024-05-01', '2024-05-03'), " +
                "('Khilan', 2, '2024-05-02', '2024-05-04'), " +
                "('Kaushik', 1, '2024-04-28', '2024-04-30'), " +
                "('Kaushik', 4, '2024-05-06', '2024-05-08'), " +
                "('Superman', 5, '2024-05-03', '2024-05-05')");

        // Check number of rows in dev_task table
        cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DEV_TASK, null);
        if (cursor.moveToFirst()) {
            Log.d("DataHelper", "Number of dev tasks inserted: " + cursor.getInt(0));
        }
        cursor.close();
    }

    // Method to fetch all data from `task` table
    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TASK, null);
    }

    // Method to fetch all data from `dev_task` table
    public Cursor getAllDevTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DEV_TASK, null);
    }
    public Cursor getTaskData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_TASK_ID + ", " + COLUMN_TASK_NAME + ", " + COLUMN_TASK_ESTIMATE + " FROM " + TABLE_TASK, null);
    }
    public Cursor getDevTaskData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DEV_TASK + " ORDER BY " + COLUMN_TASKID, null);
        Log.d("DevTaskData", "Number of rows: " + cursor.getCount());
        return cursor;
    }
    public Cursor getCombinedTaskData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT t.ID, t.TASK_NAME, t.ESTIMATE_DAY, d.DEV_NAME, d.STARTDATE, d.ENDDATE " +
                "FROM " + TABLE_TASK + " t " +
                "LEFT JOIN " + TABLE_DEV_TASK + " d ON t.ID = d.TASKID";
        return db.rawQuery(query, null);
    }
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, task.getTaskName());
        values.put(COLUMN_DEV_NAME, task.getAssignee());
        values.put(COLUMN_STARTDATE, task.getStartDate());
        values.put(COLUMN_ENDDATE, task.getEndDate());
        values.put(COLUMN_TASK_ESTIMATE, task.getEstimateDay());

        return db.update(TABLE_DEV_TASK, values, COLUMN_TASK_ID + " = ?",
                new String[]{String.valueOf(task.getTaskId())});
    }

    public boolean deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASK, COLUMN_TASK_ID + "=?", new String[]{String.valueOf(taskId)});
        return result > 0;
    }

    public long insertTask(String taskName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_NAME, taskName);
        return db.insert(TABLE_TASK, null, values);
    }

    public long insertDevTask(String devName, long taskId, String startDate, String endDate, int estimateDays) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEV_NAME, devName);
        values.put(COLUMN_TASKID, taskId);
        values.put(COLUMN_STARTDATE, startDate);
        values.put(COLUMN_ENDDATE, endDate);

        // Calculate estimate days
        estimateDays = calculateEstimateDays(startDate, endDate);
        values.put(COLUMN_TASK_ESTIMATE, estimateDays);

        long result = db.insert(TABLE_DEV_TASK, null, values);

        if (result == -1) {
            // Log error details
            Log.e("DataHelper", "Failed to insert dev task. TaskID: " + taskId +
                    ", DevName: " + devName + ", StartDate: " + startDate +
                    ", EndDate: " + endDate + ", EstimateDays: " + estimateDays);
        }

        return result;
    }


    private int calculateEstimateDays(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            long diffInMillies = Math.abs(end.getTime() - start.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return (int) diff + 1; // Adding 1 to include both start and end dates
        } catch (ParseException e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an error parsing dates
        }
    }

    public void logTableSchema(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        Log.d("DataHelper", "Schema for table " + tableName + ":");

        int nameIndex = cursor.getColumnIndex("name");
        int typeIndex = cursor.getColumnIndex("type");

        while (cursor.moveToNext()) {
            String columnName = (nameIndex != -1) ? cursor.getString(nameIndex) : "Unknown";
            String columnType = (typeIndex != -1) ? cursor.getString(typeIndex) : "Unknown";
            Log.d("DataHelper", columnName + " - " + columnType);
        }
    }



}

