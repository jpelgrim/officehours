package nl.javamagazine.officehours;

import static nl.javamagazine.officehours.ApplicationConstants.KEY_PROJECT;
import static nl.javamagazine.officehours.ApplicationConstants.KEY_START_TIME;
import static nl.javamagazine.officehours.ApplicationConstants.PREFS_NAME;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TimerActivity extends Activity {

    /*
     * Log tag used for filtering in LogCat
     */
    public static final String TAG = "TimerActivity";

    private long mStartTime;
    private int mSelectedProject;

    private int mHours;
    private int mMinutes;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;

    private TextView mElapsedTime;
    private Spinner mProjectSpinner;
    private ToggleButton mToggleTimerButton;
    
    private DatabaseHelper mDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseHelper = new DatabaseHelper(this);
        // You can see these log statements in the logcat view in Eclipse or via
        // "<ANDROID_SDK>/platform-tools/adb logcat OfficeHours:V *:S"
        Log.d(TAG, "onCreate");
        setContentView(R.layout.main);

        mElapsedTime = (TextView) findViewById(R.id.elapsed_time);
        mProjectSpinner = (Spinner) findViewById(R.id.project_spinner);

        mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mStartTime = mSharedPreferences.getLong(KEY_START_TIME, 0L);
        mSelectedProject = mSharedPreferences.getInt(KEY_PROJECT, -1);

        mHandler = new Handler();

        mToggleTimerButton = (ToggleButton) findViewById(R.id.timer_button);
        mToggleTimerButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "toggleTimerButton is checked");
                    mProjectSpinner.setEnabled(false);
                    if (mStartTime == 0) { // Start a new timer
                        mStartTime = System.currentTimeMillis();
                        mSharedPreferences.edit().putLong(KEY_START_TIME, mStartTime).putInt(KEY_PROJECT, mProjectSpinner.getSelectedItemPosition()).commit();
                    }

                    // Remove already existing messages from the message queue
                    mHandler.removeCallbacks(mUpdateTimeTask);

                    // Execute the first update task without a delay
                    mHandler.post(mUpdateTimeTask);

                } else {
                    Log.d(TAG, "toggleTimerButton is unchecked");
                    mDatabaseHelper.insertTimeBooking(
                                mProjectSpinner.getSelectedItem().toString(), mStartTime, mHours, mMinutes);
                    mSharedPreferences.edit().remove(KEY_START_TIME).remove(KEY_PROJECT).commit();
                    
                    // No reason to continue updating the UI, we're done
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    
                    // We were started from the TimeBookingActivity, so we only have to remove ourselves from the Back Stack.
                    finish();
                }
            }

        });

    }
    
    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {
            final long timerStartTime = mStartTime;
            final long currentTimeMillis = System.currentTimeMillis();

            final long elapsedTime = currentTimeMillis - timerStartTime;
            int seconds = (int) (elapsedTime / 1000) % 60;
            mMinutes = (int) (elapsedTime / 1000 / 60) % 60;
            mHours = (int) (elapsedTime / 1000 / 60 / 60) % 24;

            final long uptimeMillis = SystemClock.uptimeMillis();
            final long systemStartTime = currentTimeMillis - uptimeMillis;

            mElapsedTime.setText(String.format("%02d:%02d:%02d", mHours, mMinutes, seconds));

            final long nextUpdateTime = elapsedTime - (systemStartTime - timerStartTime) + 1000;
            mHandler.postAtTime(this, nextUpdateTime);
        }
    };
    
    @Override
    protected void onDestroy() {
        if (mDatabaseHelper != null) {
            mDatabaseHelper.close();
        }
        super.onDestroy();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (mStartTime > 0) { // We have an active timer
            mToggleTimerButton.setChecked(true);
            if (mSelectedProject >= 0) {
                mProjectSpinner.setSelection(mSelectedProject);
            }
            mProjectSpinner.setEnabled(false);
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.post(mUpdateTimeTask);
        }
    }

}