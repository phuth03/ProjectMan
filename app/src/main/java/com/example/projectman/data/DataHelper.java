    package com.example.projectman.data;

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.os.Handler;
    import android.util.Log;
    import android.widget.Toast;

    import com.example.projectman.NotificationHelper;
    import com.example.projectman.model.Task;

    import java.text.ParseException;
    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.Locale;
    import java.util.concurrent.TimeUnit;


    public class DataHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "app_database.db";
        private static final int DATABASE_VERSION = 5;

        public static final String TABLE_TASK = "task";
        public static final String TABLE_DEV_TASK = "dev_task";

        public static final String COLUMN_TASK_ID = "ID";
        public static final String COLUMN_TASK_NAME = "TASK_NAME";
        public static final String COLUMN_TASK_ESTIMATE = "ESTIMATE_DAY";

        public static final String COLUMN_DEV_TASK_ID = "ID";
        public static final String COLUMN_DEV_NAME = "DEV_NAME";
        public static final String COLUMN_TASKID = "TASKID";
        public static final String COLUMN_STARTDATE = "STARTDATE";
        public static final String COLUMN_ENDDATE = "ENDDATE";


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

        private final Context context;
        public DataHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_TASK);
            db.execSQL(CREATE_TABLE_DEV_TASK);
            insertSampleData(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEV_TASK);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
            onCreate(db);
        }

        private void insertSampleData(SQLiteDatabase db) {
            // Insert sample data for task table
            db.execSQL("INSERT INTO " + TABLE_TASK + " (TASK_NAME, ESTIMATE_DAY) VALUES " +
                    "('Order list', 5), " +
                    "('Order detail', 3), " +
                    "('Product list', 3), " +
                    "('Product detail', 3), " +
                    "('Coupon list', 3)");

            // Insert sample data for dev_task table
            db.execSQL("INSERT INTO " + TABLE_DEV_TASK + " (DEV_NAME, TASKID, STARTDATE, ENDDATE, ESTIMATE_DAY) VALUES " +
                    "('kaushik', 1, '2024-09-16', '2024-09-20', 5), " +
                    "('Khilan', 2, '2024-09-18', '2024-09-20', 3), " +
                    "('Ramesh', 3, '2024-09-17', '2024-09-19', 3), " +
                    "('kaushik', 4, '2024-09-19', '2024-09-21', 3), " +
                    "('Superman', 5, '2024-09-18', '2024-09-20', 3)");
        }

        public List<Task> getGanttChartData() {
            List<Task> tasks = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT t." + COLUMN_TASK_ID + ", t." + COLUMN_TASK_NAME + ", " +
                    "d." + COLUMN_DEV_NAME + ", d." + COLUMN_STARTDATE + ", d." + COLUMN_ENDDATE + ", " +
                    "d." + COLUMN_TASK_ESTIMATE +
                    " FROM " + TABLE_TASK + " t " +
                    "JOIN " + TABLE_DEV_TASK + " d ON t." + COLUMN_TASK_ID + " = d." + COLUMN_TASKID +
                    " ORDER BY d." + COLUMN_STARTDATE;

            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int taskIdIndex = cursor.getColumnIndex(COLUMN_TASK_ID);
                    int taskNameIndex = cursor.getColumnIndex(COLUMN_TASK_NAME);
                    int devNameIndex = cursor.getColumnIndex(COLUMN_DEV_NAME);
                    int taskEstimateIndex = cursor.getColumnIndex(COLUMN_TASK_ESTIMATE);
                    int startDateIndex = cursor.getColumnIndex(COLUMN_STARTDATE);
                    int endDateIndex = cursor.getColumnIndex(COLUMN_ENDDATE);

                    // Only create a Task object if all required columns are present
                    if (taskIdIndex != -1 && taskNameIndex != -1 && devNameIndex != -1 &&
                            taskEstimateIndex != -1 && startDateIndex != -1 && endDateIndex != -1) {

                        Task task = new Task(
                                cursor.getInt(taskIdIndex),
                                cursor.getString(taskNameIndex),
                                cursor.getString(devNameIndex),
                                cursor.getInt(taskEstimateIndex),
                                cursor.getString(startDateIndex),
                                cursor.getString(endDateIndex)
                        );
                        tasks.add(task);
                    } else {
                        Log.e("DataHelper", "One or more required columns are missing in the query result.");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            return tasks;
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

        public List<String> getTaskNames() {
            List<String> taskNames = new ArrayList<>();
            List<Task> tasks = getGanttChartData(); // Assuming this method already exists
            for (Task task : tasks) {
                taskNames.add(task.getTaskName());
            }
            return taskNames;
        }
        public Cursor getDevTaskData() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DEV_TASK + " ORDER BY " + COLUMN_TASKID, null);
            Log.d("DevTaskData", "Number of rows: " + cursor.getCount());
            return cursor;
        }
        public Cursor getCombinedTaskData() {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT t." + COLUMN_TASK_ID + ", t." + COLUMN_TASK_NAME +
                    ", COALESCE(d." + COLUMN_TASK_ESTIMATE + ", 0) AS " + COLUMN_TASK_ESTIMATE +
                    ", d." + COLUMN_DEV_NAME + ", d." + COLUMN_STARTDATE + ", d." + COLUMN_ENDDATE +
                    " FROM " + TABLE_TASK + " t " +
                    "LEFT JOIN " + TABLE_DEV_TASK + " d ON t." + COLUMN_TASK_ID + " = d." + COLUMN_TASKID;
            return db.rawQuery(query, null);
        }
        public int updateTask(Task task) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_DEV_NAME, task.getAssignee());
            values.put(COLUMN_STARTDATE, task.getStartDate());
            values.put(COLUMN_ENDDATE, task.getEndDate());
            values.put(COLUMN_TASK_ESTIMATE, task.getEstimateDay());

            // Check for overlaps
            List<Task> overlappingTasks = getOverlappingTasks(task);
            if (!overlappingTasks.isEmpty()) {
                // Create notification
                createOverlapNotification(context, task, overlappingTasks);
            }

            return db.update(TABLE_DEV_TASK, values, COLUMN_DEV_TASK_ID + " = ?",
                    new String[]{String.valueOf(task.getTaskId())});
        }

        private List<Task> getOverlappingTasks(Task task) {
            List<Task> overlappingTasks = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_DEV_TASK +
                    " WHERE " + COLUMN_DEV_NAME + " = ? " +
                    " AND " + COLUMN_DEV_TASK_ID + " != ? " +
                    " AND ((? BETWEEN " + COLUMN_STARTDATE + " AND " + COLUMN_ENDDATE + ") " +
                    " OR (? BETWEEN " + COLUMN_STARTDATE + " AND " + COLUMN_ENDDATE + ") " +
                    " OR (" + COLUMN_STARTDATE + " BETWEEN ? AND ?))";

            Cursor cursor = db.rawQuery(query, new String[]{
                    task.getAssignee(),
                    String.valueOf(task.getTaskId()),
                    task.getStartDate(),
                    task.getEndDate(),
                    task.getStartDate(),
                    task.getEndDate()
            });

            while (cursor.moveToNext()) {
                int devTaskId = getColumnValue(cursor, COLUMN_DEV_TASK_ID, -1);
                String devName = getColumnValue(cursor, COLUMN_DEV_NAME, "");
                String startDate = getColumnValue(cursor, COLUMN_STARTDATE, "");
                String endDate = getColumnValue(cursor, COLUMN_ENDDATE, "");
                int taskEstimate = getColumnValue(cursor, COLUMN_TASK_ESTIMATE, 0);
                int taskId = getColumnValue(cursor, COLUMN_TASKID, -1);

                if (taskId != -1) {
                    Task overlappingTask = new Task(
                            devTaskId,
                            getTaskName(taskId),
                            devName,
                            taskEstimate,
                            startDate,
                            endDate
                    );
                    overlappingTasks.add(overlappingTask);
                }
            }
            cursor.close();

            return overlappingTasks;
        }

        // Utility function to safely get column values with a default fallback
        private int getColumnValue(Cursor cursor, String columnName, int defaultValue) {
            int index = cursor.getColumnIndex(columnName);
            return index != -1 ? cursor.getInt(index) : defaultValue;
        }

        private String getColumnValue(Cursor cursor, String columnName, String defaultValue) {
            int index = cursor.getColumnIndex(columnName);
            return index != -1 ? cursor.getString(index) : defaultValue;
        }

        private String getTaskName(int taskId) {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT " + COLUMN_TASK_NAME + " FROM " + TABLE_TASK +
                    " WHERE " + COLUMN_TASK_ID + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});

            String taskName = "";
            if (cursor.moveToFirst()) {
                taskName = getColumnValue(cursor, COLUMN_TASK_NAME,"");
            }
            cursor.close();

            return taskName;
        }

        private void createOverlapNotification(Context context, Task updatedTask, List<Task> overlappingTasks) {
            StringBuilder notificationContent = new StringBuilder();
            notificationContent.append("Task '").append(updatedTask.getTaskName())
                    .append("'overlap with:\n");

            for (Task overlappingTask : overlappingTasks) {
                notificationContent.append("- ").append(overlappingTask.getTaskName())
                        .append(" (").append(overlappingTask.getStartDate())
                        .append(" to ").append(overlappingTask.getEndDate())
                        .append(")\n");
            }

            // Here you would integrate with your app's notification system
            // For example:

            NotificationHelper.createNotification(
                    context, "Task Overlap", notificationContent.toString());
            // Show the message as a Toast
            showLongToast(context, notificationContent.toString(), 5000);  // 10 seconds
        }
        private void showLongToast(Context context, String message, int durationInMillis) {
            final Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);

            // Calculate how many times to show the Toast based on total duration
            int repeatCount = durationInMillis / 3500;  // Toast.LENGTH_LONG lasts ~3.5 seconds

            // Use a Handler to repeat the Toast
            Handler handler = new Handler();
            for (int i = 0; i < repeatCount; i++) {
                handler.postDelayed(toast::show, i * 3500L);
            }
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
                return (int) diff + 1;
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
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

