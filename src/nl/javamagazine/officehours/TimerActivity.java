package nl.javamagazine.officehours;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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

    private static final String KEY_START_TIME = "mStartTime";
    private static final String TAG = "OfficeHours";
    private static final String PREFS_NAME = "OfficeHours";
    
    private long mStartTime;
    private Handler mHandler;
    private SharedPreferences mSharedPreferences;
    
    private TextView mElapsedTime;
    private Spinner mProjectSpinner;
    private ToggleButton mToggleTimerButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Bekijk deze log-statements in de logcat view in Eclipse of via "<ANDROID_SDK>/platform-tools/adb logcat OfficeHours:V *:S"
        Log.d(TAG, "onCreate"); 
        setContentView(R.layout.main);

        mElapsedTime = (TextView) findViewById(R.id.elapsed_time);
        mProjectSpinner = (Spinner) findViewById(R.id.project_spinner);

        mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mStartTime = mSharedPreferences.getLong(KEY_START_TIME, 0L);
        
        mHandler = new Handler();

        mToggleTimerButton = (ToggleButton) findViewById(R.id.timer_button);
        mToggleTimerButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "toggleTimerButton is checked");
                    mProjectSpinner.setEnabled(false);
                    if (mStartTime == 0) {
                        mStartTime = System.currentTimeMillis();
                        Editor editor = mSharedPreferences.edit();
                        editor.putLong(KEY_START_TIME, mStartTime);
                        editor.commit();
                    }
                    mHandler.removeCallbacks(mUpdateTimeTask); // Haal alle bestaande en nog wachtende messages van dit object uit de message queue
                    mHandler.post(mUpdateTimeTask); // Voer de mUpdateTimeTask de eerste keer 'direct' uit
                } else {
                    Log.d(TAG, "toggleTimerButton is unchecked");
                    mProjectSpinner.setEnabled(true);
                    mStartTime = 0L;
                    Editor editor = mSharedPreferences.edit();
                    editor.remove(KEY_START_TIME);
                    editor.commit();
                    mHandler.removeCallbacks(mUpdateTimeTask);
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
            int minutes = (int) (elapsedTime / 1000 / 60) % 60;
            int hours = (int) (elapsedTime / 1000 / 60 / 60) % 24;

            final long uptimeMillis = SystemClock.uptimeMillis();
            final long systemStartTime = currentTimeMillis - uptimeMillis;

            mElapsedTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            
            final long nextUpdateTime = elapsedTime - (systemStartTime - timerStartTime) + 1000;
            mHandler.postAtTime(this, nextUpdateTime);
        }
    };

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
        if (mStartTime > 0) {
            mToggleTimerButton.setChecked(true);
            mProjectSpinner.setEnabled(false);
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.post(mUpdateTimeTask);
        }
    }
    
}