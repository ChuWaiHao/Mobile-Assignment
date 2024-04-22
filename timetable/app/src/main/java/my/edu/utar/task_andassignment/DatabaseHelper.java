package my.edu.utar.task_andassignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_calendar.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";

    private static final String CREATE_EVENTS_TABLE =
            "CREATE TABLE " + TABLE_EVENTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_DATE + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        onCreate(db);
    }

    public long insertEvent(DataModel data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, data.getTitle());
        values.put(COLUMN_DESCRIPTION, data.getDescription());

        // Check if date is not null
        if (data.getDate() != null) {
            // Convert Date object to string
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String dateString = dateFormat.format(data.getDate());
            values.put(COLUMN_DATE, dateString);
        } else {
            // Handle null date appropriately (e.g., set to current date or log an error)
            Log.e("DatabaseHelper", "Date is null for event: " + data.getTitle());
            // Here you could set a default date or handle it according to your app's logic
            // values.put(COLUMN_DATE, DEFAULT_DATE_STRING);
        }

        long result = db.insert(TABLE_EVENTS, null, values);
        db.close();

        return result;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EVENTS, null, null); // Delete all rows from the events table
        db.close();
    }




    public List<DataModel> getAllEvents() {
        List<DataModel> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_EVENTS, null);
        if (cursor.moveToFirst()) {
            do {
                DataModel event = new DataModel();

                // Error handling for column indices
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                int dateIndex = cursor.getColumnIndex(COLUMN_DATE);

                // Check if column indices are valid
                if (idIndex != -1 && titleIndex != -1 && descriptionIndex != -1 && dateIndex != -1) {
                    event.setId(cursor.getInt(idIndex));
                    event.setTitle(cursor.getString(titleIndex));
                    event.setDescription(cursor.getString(descriptionIndex));
                    event.setDate(cursor.getString(dateIndex));
                    eventsList.add(event);
                } else {
                    // Log an error if any column index is invalid
                    Log.e("DatabaseHelper", "Invalid column index detected");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return eventsList;
    }

    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_EVENTS, COLUMN_ID + " = ?", new String[]{String.valueOf(eventId)});
        db.close();
        return result > 0;
    }


}
