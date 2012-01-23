package nl.javamagazine.officehours;

import java.util.Date;

import android.provider.BaseColumns;

public class TimeBooking {

    public static final String TABLE_NAME="timebookings";
    public static final String KEY_PROJECT = "project";
    public static final String KEY_STARTDT = "startdt";
    public static final String KEY_HOURS = "hours";
    public static final String KEY_MINUTES = "minutes";
    
    public static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_PROJECT + " TEXT, " +
            KEY_STARTDT + " DATE, " +
            KEY_HOURS + " INTEGER, " +
            KEY_MINUTES + " INTEGER" + ");";

    private long id;
    private String project;
    private Date startDateTime;
    private int hours;
    private int minutes;
    
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getProject() {
        return project;
    }
    public void setProject(String project) {
        this.project = project;
    }
    public Date getStartDateTime() {
        return startDateTime;
    }
    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }
    public int getHours() {
        return hours;
    }
    public void setHours(int hours) {
        this.hours = hours;
    }
    public int getMinutes() {
        return minutes;
    }
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

}