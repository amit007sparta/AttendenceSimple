package com.sharma.tushar.attendencesimple;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.sharma.tushar.attendencesimple.Data.DataContract;
import com.sharma.tushar.attendencesimple.Data.DatabaseHelper;
import com.sharma.tushar.attendencesimple.Data.SubjectAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class Details extends AppCompatActivity {

    DatabaseHelper helper;

    public static StringBuffer builder = null;

    public static String todaysClasses[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        int dateCode = 0;
        int day = 0;
        if (intent.hasExtra(MainActivity.EXTRA_DATE_CODE) && intent.hasExtra(MainActivity.EXTRA_DAY)) {
            dateCode = intent.getIntExtra(MainActivity.EXTRA_DATE_CODE, 0);
            day = intent.getIntExtra(MainActivity.EXTRA_DAY, 1);
        }

        helper = new DatabaseHelper(this);
        builder = new StringBuffer("");

        ListView listView = findViewById(R.id.subject_list);
        String todaysList = getList(day);
        todaysClasses = todaysList.split("!");
        ArrayList arrayList = formList(todaysList);
        SubjectAdapter adapter = new SubjectAdapter(this, arrayList);
        listView.setAdapter(adapter);

        Button btn = findViewById(R.id.submit_button);
        final int finalDateCode = dateCode;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performTask(finalDateCode);
            }
        });

        btn = findViewById(R.id.cancel_button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(Details.this);
            }
        });

    }

    private String getList(int day) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(DataContract.DAILY_SCHEDULE_TABLE,
                new String[]{DataContract.DAILY_SCHEDULE_CODE},
                DataContract._ID + " = ?",
                new String[]{String.valueOf(day)},
                null,
                null,
                null);
        cursor.moveToFirst();
        String list = cursor.getString(cursor.getColumnIndex(DataContract.DAILY_SCHEDULE_CODE));
        cursor.close();
        return list;
    }

    private ArrayList formList(String string) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String arr[] = string.split("!");
        for (int i = 0; i < arr.length; i++) {
            Cursor cursor = db.query(DataContract.SUBJECT_TABLE,
                    new String[]{DataContract.SUBJECT_SUB_NAME},
                    DataContract._ID + " = ?",
                    new String[]{arr[i]},
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            arr[i] = cursor.getString(cursor.getColumnIndex(DataContract.SUBJECT_SUB_NAME));
            cursor.close();
        }
        return new ArrayList<>(Arrays.asList(arr));
    }

    private void performTask(int finalDateCode) {
        if (builder.length()!=0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataContract.NOT_ATTENDED_DATE, finalDateCode);
        values.put(DataContract.NOT_ATTENDED_CODE, builder.toString());
        db.insert(DataContract.NOT_ATTENDED_TABLE, null, values);
        Log.i(" Inserted code", builder.toString());

        SQLiteDatabase db1 = helper.getReadableDatabase();

        for (String todaysClass : todaysClasses) {
            Cursor cursor = db1.query(DataContract.SUBJECT_TABLE,
                    new String[]{DataContract.SUBJECT_TOT_CLASSES, DataContract.SUBJECT_NOT_ATTENDED},
                    DataContract._ID + " = ?",
                    new String[]{todaysClass},
                    null,
                    null,
                    null);
            cursor.moveToFirst();

            int total = cursor.getInt(cursor.getColumnIndex(DataContract.SUBJECT_TOT_CLASSES));
            total++;
            Log.i(" Total classes attended", total + "");
            values = new ContentValues();
            if (builder.toString().contains(todaysClass)) {
                int notAttended = cursor.getInt(cursor.getColumnIndex(DataContract.SUBJECT_NOT_ATTENDED));
                notAttended++;
                Log.i(" Not Attended ", notAttended + "");
                values.put(DataContract.SUBJECT_NOT_ATTENDED, notAttended);
            }
            values.put(DataContract.SUBJECT_TOT_CLASSES, total);
            db.update(DataContract.SUBJECT_TABLE,
                    values,
                    DataContract._ID + " = ?",
                    new String[]{todaysClass});
            cursor.close();
        }
        NavUtils.navigateUpFromSameTask(Details.this);
    }

}
