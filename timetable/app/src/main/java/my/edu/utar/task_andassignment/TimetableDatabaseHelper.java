package my.edu.utar.task_andassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


//store all the data
public class TimetableDatabaseHelper extends SQLiteOpenHelper {

    private static TimetableDatabaseHelper instance;
    private static final String DATABASE_NAME = "schedule.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_NAME = "schedule";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SUBJECT_TITLE = "subject_title";
    public static final String COLUMN_DAY_OF_WEEK = "day_of_week";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_COLOR = "color";
    public static final String COLUMN_DESCRIPTION = "description";

    // Second table for task
    public static final String TASK_TABLE_NAME = "task";
    public static final String TASK_COLUMN_ID = "id";
    public static final String TASK_COLUMN_TASK_NAME = "task_name";
    public static final String TASK_COLUMN_DATE = "date";
    public static final String TASK_COLUMN_TIME = "time";
    public static final String TASK_COLUMN_DESCRIPTION = "description";
    public static final String TASK_COLUMN_COMPLETED = "completed";
    public static final String TASK_COLUMN_SUBJECT_ID = "subject_id";

    private static final String CREATE_TABLE_SCHEDULE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SUBJECT_TITLE + " TEXT NOT NULL, " +
            COLUMN_DAY_OF_WEEK + " TEXT NOT NULL, " +
            COLUMN_START_TIME + " TEXT NOT NULL, " +
            COLUMN_END_TIME + " TEXT NOT NULL, " +
            COLUMN_COLOR + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT" +
            ");";

    private static final String CREATE_TABLE_TASK = "CREATE TABLE " + TASK_TABLE_NAME + " (" +
            TASK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TASK_COLUMN_TASK_NAME + " TEXT NOT NULL, " +
            TASK_COLUMN_DATE + " TEXT NOT NULL, " +
            TASK_COLUMN_TIME + " TEXT NOT NULL, " +
            TASK_COLUMN_COMPLETED + " TEXT NOT NULL DEFAULT 'inProgress', " +
            TASK_COLUMN_DESCRIPTION + " TEXT, " +
            TASK_COLUMN_SUBJECT_ID + " INTEGER, " +
            "FOREIGN KEY (" + TASK_COLUMN_SUBJECT_ID + ") REFERENCES " + TABLE_NAME + " (" + COLUMN_ID + ")" +
            ");";

    public TimetableDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TimetableDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new TimetableDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public long insertSchedule(String title, String dayOfWeek, String startTime, String endTime, String color, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT_TITLE, title);
        values.put(COLUMN_DAY_OF_WEEK, dayOfWeek);
        values.put(COLUMN_START_TIME, startTime);
        values.put(COLUMN_END_TIME, endTime);
        values.put(COLUMN_COLOR, color);
        values.put(COLUMN_DESCRIPTION, description);
        long id = db.insert(TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
                int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && titleIndex != -1 && dayOfWeekIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && colorIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String title = cursor.getString(titleIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    String color = cursor.getString(colorIndex);
                    String description = cursor.getString(descriptionIndex);

                    schedules.add(new Schedule(id, title, dayOfWeek, startTime, endTime, color, description));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public boolean deleteSchedule(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public int updateSchedule(int id, String title, String dayOfWeek, String startTime, String endTime, String color, String description) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_SUBJECT_TITLE, title);
        contentValues.put(COLUMN_DAY_OF_WEEK, dayOfWeek);
        contentValues.put(COLUMN_START_TIME, startTime);
        contentValues.put(COLUMN_END_TIME, endTime);
        contentValues.put(COLUMN_COLOR, color);
        contentValues.put(COLUMN_DESCRIPTION, description);

        String whereClause = COLUMN_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};

        return db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
    }



    // get the Monday data from the database
    public List<Schedule> getMondaySchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DAY_OF_WEEK + " = ?";
        String[] selectionArgs = {"Monday"};
        String orderBy = COLUMN_START_TIME + " ASC"; // Add this line to sort by startTime in ascending order

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
                int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && titleIndex != -1 && dayOfWeekIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && colorIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String title = cursor.getString(titleIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    String color = cursor.getString(colorIndex);
                    String description = cursor.getString(descriptionIndex);

                    schedules.add(new Schedule(id, title, dayOfWeek, startTime, endTime, color, description));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    //get thuesday data from database
    public List<Schedule> getTuesdaySchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DAY_OF_WEEK + " = ?";
        String[] selectionArgs = {"Tuesday"};
        String orderBy = COLUMN_START_TIME + " ASC"; // Add this line to sort by startTime in ascending order

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
                int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && titleIndex != -1 && dayOfWeekIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && colorIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String title = cursor.getString(titleIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    String color = cursor.getString(colorIndex);
                    String description = cursor.getString(descriptionIndex);

                    schedules.add(new Schedule(id, title, dayOfWeek, startTime, endTime, color, description));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    //get wednesdat data from database
    public List<Schedule> getWednesdaySchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DAY_OF_WEEK + " = ?";
        String[] selectionArgs = {"Wednesday"};
        String orderBy = COLUMN_START_TIME + " ASC"; // Add this line to sort by startTime in ascending order

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
                int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && titleIndex != -1 && dayOfWeekIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && colorIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String title = cursor.getString(titleIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    String color = cursor.getString(colorIndex);
                    String description = cursor.getString(descriptionIndex);

                    schedules.add(new Schedule(id, title, dayOfWeek, startTime, endTime, color, description));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    //get thurday data from database
    public List<Schedule> getThursdaySchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DAY_OF_WEEK + " = ?";
        String[] selectionArgs = {"Thursday"};
        String orderBy = COLUMN_START_TIME + " ASC"; // Add this line to sort by startTime in ascending order

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
                int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && titleIndex != -1 && dayOfWeekIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && colorIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String title = cursor.getString(titleIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    String color = cursor.getString(colorIndex);
                    String description = cursor.getString(descriptionIndex);

                    schedules.add(new Schedule(id, title, dayOfWeek, startTime, endTime, color, description));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    //get friday data from database
    public List<Schedule> getFridaySchedules() {
        List<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_DAY_OF_WEEK + " = ?";
        String[] selectionArgs = {"Friday"};
        String orderBy = COLUMN_START_TIME + " ASC"; // Add this line to sort by startTime in ascending order

        Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, orderBy);
        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                int dayOfWeekIndex = cursor.getColumnIndex(COLUMN_DAY_OF_WEEK);
                int startTimeIndex = cursor.getColumnIndex(COLUMN_START_TIME);
                int endTimeIndex = cursor.getColumnIndex(COLUMN_END_TIME);
                int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && titleIndex != -1 && dayOfWeekIndex != -1 && startTimeIndex != -1 && endTimeIndex != -1 && colorIndex != -1 && descriptionIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String title = cursor.getString(titleIndex);
                    String dayOfWeek = cursor.getString(dayOfWeekIndex);
                    String startTime = cursor.getString(startTimeIndex);
                    String endTime = cursor.getString(endTimeIndex);
                    String color = cursor.getString(colorIndex);
                    String description = cursor.getString(descriptionIndex);

                    schedules.add(new Schedule(id, title, dayOfWeek, startTime, endTime, color, description));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public List<String> getScheduleTitles() {
        List<String> titles = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_SUBJECT_TITLE}, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int titleIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
                if (titleIndex != -1) {
                    String title = cursor.getString(titleIndex);

                    titles.add(title);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return titles;
    }

    public int getSubjectId(String subjectTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_SUBJECT_TITLE + " = ?";
        String[] selectionArgs = {subjectTitle};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);

        int subjectId = -1;
        if (cursor.moveToFirst()) {
            int subjectIndex = cursor.getColumnIndex(COLUMN_ID);
            if (subjectIndex != -1) {
                subjectId = cursor.getInt(subjectIndex);

            }
        }

        cursor.close();
        return subjectId;
    }

    public long insertTask(String taskName, String date, String time, String description, String subjectTitle, String completed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Get the subject ID from the subject title
        int subjectId = getSubjectId(subjectTitle);

        contentValues.put(TASK_COLUMN_TASK_NAME, taskName);
        contentValues.put(TASK_COLUMN_DATE, date);
        contentValues.put(TASK_COLUMN_TIME, time);
        contentValues.put(TASK_COLUMN_DESCRIPTION, description);
        contentValues.put(TASK_COLUMN_COMPLETED, completed);
        contentValues.put(TASK_COLUMN_SUBJECT_ID, subjectId);

        long id = db.insert(TASK_TABLE_NAME, null, contentValues);
        db.close();

        return id;
    }

    // In TimetableDatabaseHelper.java

    public String getSubjectTitleById(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String subjectTitle = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_SUBJECT_TITLE},
                COLUMN_ID + "=?", new String[]{String.valueOf(subjectId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int subjectIndex = cursor.getColumnIndex(COLUMN_SUBJECT_TITLE);
            if (subjectIndex != -1) {
                subjectTitle = cursor.getString(subjectIndex);
            }
        }

        cursor.close();
        return subjectTitle;
    }

    public String getSubjectColorById(int subjectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String subjectColor = null;

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_COLOR},
                COLUMN_ID + "=?", new String[]{String.valueOf(subjectId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int colorIndex = cursor.getColumnIndex(COLUMN_COLOR);
            if (colorIndex != -1) {
                subjectColor = cursor.getString(colorIndex);
            }
        }

        cursor.close();
        return subjectColor;
    }


    public List<Task> getInProgressTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the selection and arguments for the query
        String selection = TASK_COLUMN_COMPLETED + "=?";
        String[] selectionArgs = new String[]{"inProgress"};

        Cursor cursor = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, TASK_COLUMN_DATE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int taskIdIndex = cursor.getColumnIndex(TASK_COLUMN_ID);
                int taskNameIndex = cursor.getColumnIndex(TASK_COLUMN_TASK_NAME);
                int taskDateIndex = cursor.getColumnIndex(TASK_COLUMN_DATE);
                int taskTimeIndex = cursor.getColumnIndex(TASK_COLUMN_TIME);
                int taskCompletedIndex = cursor.getColumnIndex(TASK_COLUMN_COMPLETED);
                int taskDescriptionIndex = cursor.getColumnIndex(TASK_COLUMN_DESCRIPTION);
                int taskSubjectIdIndex = cursor.getColumnIndex(TASK_COLUMN_SUBJECT_ID);

                if(taskIdIndex != -1 && taskNameIndex != -1 && taskDateIndex != -1 && taskTimeIndex != -1 &&
                        taskCompletedIndex != -1 && taskDescriptionIndex != -1 && taskSubjectIdIndex != -1){
                    int taskId = cursor.getInt(taskIdIndex);
                    String taskName = cursor.getString(taskNameIndex);
                    String taskDate = cursor.getString(taskDateIndex);
                    String taskTime = cursor.getString(taskTimeIndex);
                    String taskCompleted = cursor.getString(taskCompletedIndex);
                    String taskDescription = cursor.getString(taskDescriptionIndex);
                    int taskSubjectId = cursor.getInt(taskSubjectIdIndex);

                    Task task = new Task(taskId, taskName, taskDate, taskTime, taskDescription, taskCompleted, taskSubjectId);
                    tasks.add(task);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }

    public List<Task> getCompletedTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the selection and arguments for the query
        String selection = TASK_COLUMN_COMPLETED + "=?";
        String[] selectionArgs = new String[]{"Completed"};

        Cursor cursor = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, TASK_COLUMN_DATE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int taskIdIndex = cursor.getColumnIndex(TASK_COLUMN_ID);
                int taskNameIndex = cursor.getColumnIndex(TASK_COLUMN_TASK_NAME);
                int taskDateIndex = cursor.getColumnIndex(TASK_COLUMN_DATE);
                int taskTimeIndex = cursor.getColumnIndex(TASK_COLUMN_TIME);
                int taskCompletedIndex = cursor.getColumnIndex(TASK_COLUMN_COMPLETED);
                int taskDescriptionIndex = cursor.getColumnIndex(TASK_COLUMN_DESCRIPTION);
                int taskSubjectIdIndex = cursor.getColumnIndex(TASK_COLUMN_SUBJECT_ID);

                if(taskIdIndex != -1 && taskNameIndex != -1 && taskDateIndex != -1 && taskTimeIndex != -1 &&
                        taskCompletedIndex != -1 && taskDescriptionIndex != -1 && taskSubjectIdIndex != -1){
                    int taskId = cursor.getInt(taskIdIndex);
                    String taskName = cursor.getString(taskNameIndex);
                    String taskDate = cursor.getString(taskDateIndex);
                    String taskTime = cursor.getString(taskTimeIndex);
                    String taskCompleted = cursor.getString(taskCompletedIndex);
                    String taskDescription = cursor.getString(taskDescriptionIndex);
                    int taskSubjectId = cursor.getInt(taskSubjectIdIndex);

                    Task task = new Task(taskId, taskName, taskDate, taskTime, taskDescription, taskCompleted, taskSubjectId);
                    tasks.add(task);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }

    public boolean deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TASK_TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rowsAffected > 0;
    }

    public boolean updateTask(int taskId, String taskName, String date, String time, String description, String subjectTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // Get the subject ID from the subject title
        int subjectId = getSubjectId(subjectTitle);

        contentValues.put(TASK_COLUMN_TASK_NAME, taskName);
        contentValues.put(TASK_COLUMN_DATE, date);
        contentValues.put(TASK_COLUMN_TIME, time);
        contentValues.put(TASK_COLUMN_DESCRIPTION, description);
        contentValues.put(TASK_COLUMN_SUBJECT_ID, subjectId);

        int rowsAffected = db.update(TASK_TABLE_NAME, contentValues, TASK_COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();

        return rowsAffected > 0;
    }

    public boolean updateTaskToCompleted(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TASK_COLUMN_COMPLETED, "Completed");

        int rowsAffected = db.update(TASK_TABLE_NAME, contentValues, TASK_COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();

        return rowsAffected > 0;
    }

    public boolean updateTaskToInProgress(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TASK_COLUMN_COMPLETED, "inProgress");

        int rowsAffected = db.update(TASK_TABLE_NAME, contentValues, TASK_COLUMN_ID + "=?", new String[]{String.valueOf(taskId)});
        db.close();

        return rowsAffected > 0;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SCHEDULE);
        db.execSQL(CREATE_TABLE_TASK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}