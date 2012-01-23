package nl.javamagazine.officehours;

import android.app.Application;

public class OfficeHoursApplication extends Application {
	
    public static final String TAG = "OfficeHours";
    
    public static final String KEY_START_TIME = "mStartTime";
    public static final String KEY_PROJECT = "mSelectedProject";
    public static final String PREFS_NAME = "OfficeHours";
    
	private static DatabaseHelper databaseHelper;
	
	@Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(this);
    }	
	    
    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
    	
}