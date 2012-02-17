package nl.javamagazine.officehours;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
        
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "officehours.sql";
    
    private static final String TABLE_TIMEBOOKINGS="timebookings";
    
    public interface TimeBookingColumns {
        public static final String PROJECT = "project";
        public static final String STARTDT = "startdt";
        public static final String HOURS = "hours";
        public static final String MINUTES = "minutes";
    }
    
    private static final String CREATE_TABLE_TIMEBOOKINGS =
            "CREATE TABLE " + TABLE_TIMEBOOKINGS + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            TimeBookingColumns.PROJECT + " TEXT, " +
            TimeBookingColumns.STARTDT + " DATE, " +
            TimeBookingColumns.HOURS + " INTEGER, " +
            TimeBookingColumns.MINUTES + " INTEGER" + ");";
    
    public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TIMEBOOKINGS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to upgrade for the first version
    }
    
    public void insertTimeBooking(String projectName, long start, int hours, int minutes) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
        Date startDateTime = new Date(start);
        ContentValues values = new ContentValues();
        values.put(TimeBookingColumns.PROJECT, projectName);
        values.put(TimeBookingColumns.STARTDT, dateFormat.format(startDateTime));
        values.put(TimeBookingColumns.HOURS, hours);
        values.put(TimeBookingColumns.MINUTES, minutes);
        this.getWritableDatabase().insert(TABLE_TIMEBOOKINGS, TimeBookingColumns.PROJECT, values);
    }
    
    public void updateTimeBooking(long id, int hours, int minutes) {
        ContentValues values = new ContentValues();
        values.put(TimeBookingColumns.HOURS, hours);
        values.put(TimeBookingColumns.MINUTES, minutes);
        this.getWritableDatabase().update(TABLE_TIMEBOOKINGS, values, BaseColumns._ID + "=" + id, null);
    }
    
    public Cursor getTimeBookings() {
        Cursor cursor = this.getReadableDatabase().query(
                TABLE_TIMEBOOKINGS, 
                new String[] {BaseColumns._ID, TimeBookingColumns.PROJECT, TimeBookingColumns.STARTDT, TimeBookingColumns.HOURS, TimeBookingColumns.MINUTES} , 
                null, null, null, null, TimeBookingColumns.STARTDT + " desc");
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public TimeBooking getTimeBooking(long id) throws ParseException {
        Cursor cursor = this.getReadableDatabase().query(
                TABLE_TIMEBOOKINGS, 
                new String[] {BaseColumns._ID, TimeBookingColumns.PROJECT, TimeBookingColumns.STARTDT, TimeBookingColumns.HOURS, TimeBookingColumns.MINUTES} , 
                BaseColumns._ID + "=" + id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            TimeBooking timeBooking = new TimeBooking();
            timeBooking.setProject(cursor.getString(cursor.getColumnIndexOrThrow(TimeBookingColumns.PROJECT)));
            String dateTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TimeBookingColumns.STARTDT));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
            timeBooking.setStartDateTime(dateFormat.parse(dateTimeString));
            timeBooking.setHours(cursor.getInt(cursor.getColumnIndexOrThrow(TimeBookingColumns.HOURS)));
            timeBooking.setMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(TimeBookingColumns.MINUTES)));
            return timeBooking;
        }
        return null;
    }

    public void deleteTimeBooking(long id) {
        this.getWritableDatabase().delete(TABLE_TIMEBOOKINGS, BaseColumns._ID + "=" + id, null);
    }

}