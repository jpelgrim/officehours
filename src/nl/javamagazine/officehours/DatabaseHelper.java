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
    
    public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TimeBooking.TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to upgrade for the first version
    }
    
    public void insertTimeBooking(String projectName, long start, int hours, int minutes) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
        Date startDateTime = new Date(start);
        ContentValues values = new ContentValues();
        values.put(TimeBooking.KEY_PROJECT, projectName);
        values.put(TimeBooking.KEY_STARTDT, dateFormat.format(startDateTime));
        values.put(TimeBooking.KEY_HOURS, hours);
        values.put(TimeBooking.KEY_MINUTES, minutes);
        this.getWritableDatabase().insert(TimeBooking.TABLE_NAME, TimeBooking.KEY_PROJECT, values);
    }
    
    public void updateTimeBooking(long id, int hours, int minutes) {
        ContentValues values = new ContentValues();
        values.put(TimeBooking.KEY_HOURS, hours);
        values.put(TimeBooking.KEY_MINUTES, minutes);
        this.getWritableDatabase().update(TimeBooking.TABLE_NAME, values, BaseColumns._ID + "=" + id, null);
    }
    
    public Cursor getTimeBookings() {
        return this.getReadableDatabase().query(
                TimeBooking.TABLE_NAME, 
                new String[] {BaseColumns._ID, TimeBooking.KEY_PROJECT, TimeBooking.KEY_STARTDT, TimeBooking.KEY_HOURS, TimeBooking.KEY_MINUTES} , 
                null, null, null, null, TimeBooking.KEY_STARTDT + " desc");
    }

    public TimeBooking getTimeBooking(long id) throws ParseException {
        Cursor cursor = this.getReadableDatabase().query(
                TimeBooking.TABLE_NAME, 
                new String[] {BaseColumns._ID, TimeBooking.KEY_PROJECT, TimeBooking.KEY_STARTDT, TimeBooking.KEY_HOURS, TimeBooking.KEY_MINUTES} , 
                BaseColumns._ID + "=" + id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            TimeBooking timeBooking = new TimeBooking();
            timeBooking.setProject(cursor.getString(cursor.getColumnIndexOrThrow(TimeBooking.KEY_PROJECT)));
            String dateTimeString = cursor.getString(cursor.getColumnIndexOrThrow(TimeBooking.KEY_STARTDT));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
            timeBooking.setStartDateTime(dateFormat.parse(dateTimeString));
            timeBooking.setHours(cursor.getInt(cursor.getColumnIndexOrThrow(TimeBooking.KEY_HOURS)));
            timeBooking.setMinutes(cursor.getInt(cursor.getColumnIndexOrThrow(TimeBooking.KEY_MINUTES)));
            return timeBooking;
        }
        return null;
    }

    public void deleteTimeBooking(long id) {
        this.getWritableDatabase().delete(TimeBooking.TABLE_NAME, BaseColumns._ID + "=" + id, null);
    }

}