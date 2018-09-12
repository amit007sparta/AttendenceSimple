package com.sharma.tushar.attendencesimple;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;

    public static final String EXTRA_DATE_CODE = "ExtraData";
    public static final String EXTRA_DAY = "Day";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendarView = findViewById(R.id.calender_view);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    int dateCode = Integer.parseInt(year + "" + month +"" + dayOfMonth);
                    Calendar cal = new GregorianCalendar(year, month, dayOfMonth);
                    int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    Log.i("Date Stored ", String.valueOf(dateCode));
                    if (day!=0 && day!=6) {
                        Intent intent = new Intent(MainActivity.this, Details.class);
                        intent.putExtra(EXTRA_DATE_CODE, dateCode);
                        intent.putExtra(EXTRA_DAY, day);
                        startActivity(intent);
                    }
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_per_subject, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,Record.class );
        startActivity(intent);
        return true;
    }
}
