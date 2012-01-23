package nl.javamagazine.officehours;

import static nl.javamagazine.officehours.OfficeHoursApplication.KEY_START_TIME;
import static nl.javamagazine.officehours.OfficeHoursApplication.PREFS_NAME;
import static nl.javamagazine.officehours.OfficeHoursApplication.TAG;

import java.text.ParseException;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AnalogClock;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeBookingActivity extends ListActivity {

    static final int TIME_DIALOG_ID = 0;
    private long mId;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timebooking);

        registerForContextMenu(getListView());

        mCursor = OfficeHoursApplication.getDatabaseHelper().getTimeBookings();
        startManagingCursor(mCursor);

        String[] from = new String[] { TimeBooking.KEY_PROJECT, TimeBooking.KEY_STARTDT, TimeBooking.KEY_HOURS,
                TimeBooking.KEY_MINUTES };

        int[] to = new int[] { R.id.project_name_list_item, R.id.startdt_list_item, R.id.hours_list_item,
                R.id.minutes_list_item };

        mAdapter = new SimpleCursorAdapter(this, R.layout.timebooking_list_item, mCursor, from, to);

        setListAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long startTime = sharedPreferences.getLong(KEY_START_TIME, 0L);
        AnalogClock clock = (AnalogClock) findViewById(R.id.active_timer_button);
        TextView plusButton = (TextView) findViewById(R.id.new_timer_button);
        if (startTime > 0) { // Timer active
            clock.setVisibility(View.VISIBLE);
            plusButton.setVisibility(View.GONE);
        } else { // No timer active
            clock.setVisibility(View.GONE);
            plusButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (v.getId() == android.R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            String title = this.getString(R.string.time_booking_label_single);

            try {
                TimeBooking timeBooking = OfficeHoursApplication.getDatabaseHelper().getTimeBooking(info.id);
                if (timeBooking != null) {
                    title += String.format(" (%dh%dm)", timeBooking.getHours(), timeBooking.getMinutes());
                }
            } catch (ParseException e) {
                Log.d(TAG, "Could not parse date for time booking with id " + info.id);
            }

            menu.setHeaderTitle(title);
            String[] menuItems = getResources().getStringArray(R.array.timebookings_menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 0) { // Edit
            mId = info.id;
            removeDialog(TIME_DIALOG_ID); // Make sure the dialog is not reused.
            showDialog(TIME_DIALOG_ID);
        }
        if (menuItemIndex == 1) { // Delete
            OfficeHoursApplication.getDatabaseHelper().deleteTimeBooking(info.id);
            refreshListView();
        }
        return true;
    }

    private void refreshListView() {
        if (mCursor != null) {
            stopManagingCursor(mCursor);
        }
        mCursor = OfficeHoursApplication.getDatabaseHelper().getTimeBookings();
        startManagingCursor(mCursor);
        mAdapter.changeCursor(mCursor);
    }

    public void onTimerButtonClicked(View v) {
        Intent intent = new Intent(this, TimerActivity.class);
        this.startActivity(intent);
    }

    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hours, int minutes) {
            OfficeHoursApplication.getDatabaseHelper().updateTimeBooking(mId, hours, minutes);
            refreshListView();
        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
            TimeBooking timeBooking;
            try {
                timeBooking = OfficeHoursApplication.getDatabaseHelper().getTimeBooking(mId);
                if (timeBooking != null) {
                    return new TimePickerDialog(this, mTimeSetListener, timeBooking.getHours(),
                            timeBooking.getMinutes(), true);
                }
            } catch (ParseException e) {
                Log.d(TAG, "Could not parse date for time booking with id " + mId);
            }
        }
        return null;
    }

}
