package nl.javamagazine.officehours;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TimerActivity extends Activity {

    private static final String KEY_START_TIME = "mStartTime";
    private static final String PREFS_NAME = "OfficeHours";
    private long mStartTime;
    private TextView mElapsedTime;
    private EditText mNotesField;
    private Spinner mProjectSpinner;
    private Handler mHandler;
    private ToggleButton toggleTimerButton;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        mStartTime = sharedPreferences.getLong(KEY_START_TIME, 0L);

        mHandler = new Handler();

        toggleTimerButton = (ToggleButton) findViewById(R.id.timer_button);
        toggleTimerButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mNotesField.setEnabled(false);
                    mProjectSpinner.setEnabled(false);
                    if (mStartTime == 0L) {
                        mStartTime = System.currentTimeMillis();
                        Editor editor = sharedPreferences.edit();
                        editor.putLong(KEY_START_TIME, mStartTime);
                        editor.commit();
                    }
                    mHandler.removeCallbacks(mUpdateTimeTask);
                    mHandler.postDelayed(mUpdateTimeTask, 0);
                } else {
                    mNotesField.setEnabled(true);
                    mProjectSpinner.setEnabled(true);
                    mStartTime = 0L;
                    Editor editor = sharedPreferences.edit();
                    editor.putLong(KEY_START_TIME, mStartTime);
                    editor.commit();
                    mHandler.removeCallbacks(mUpdateTimeTask);
                }
            }
        });

        mElapsedTime = (TextView) findViewById(R.id.elapsed_time);
        
        mNotesField = (EditText) findViewById(R.id.notes_field);
        mProjectSpinner = (Spinner) findViewById(R.id.project_spinner);
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
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mStartTime > 0) {
            toggleTimerButton.setChecked(true);
            mNotesField.setEnabled(false);
            mProjectSpinner.setEnabled(false);
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 0);
        }
    }
    
}