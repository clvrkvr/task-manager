package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

public class Calendar extends AppCompatActivity {
    protected static final String MESSAGE_KEY = "com.example.taskmanager.EXTRA.SET_DATE";
    private Button btnCancelSetDate;
    private CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        btnCancelSetDate = findViewById(R.id.btnCancelSetDate);
        calendar = findViewById(R.id.calendar);

        // Cancel setting a date for the task
        btnCancelSetDate.setOnClickListener((View view) -> {
            startActivity(new Intent(this, Task.class));
            overridePendingTransition(0, 0);
        });

        // add a onDateChangeListener to the calendar
        // this will help us grab the data of the day the user picked
        // and pass it back to the task activity
        calendar.setOnDateChangeListener((CalendarView c, int year, int month, int day) -> {
            startActivity((new Intent(this, Task.class)).putExtra(MESSAGE_KEY, toDate(month, day, year)));
            overridePendingTransition(0, 0);
        });
    }

    // function to convert the date from the calendar to a string with a date format
    protected String toDate(int month, int day, int year){
        String monthInString;
        switch(month) {
            case 0:
                monthInString = "January";
                break;
            case 1:
                monthInString = "February";
                break;
            case 2:
                monthInString = "March";
                break;
            case 3:
                monthInString = "April";
                break;
            case 4:
                monthInString = "May";
                break;
            case 5:
                monthInString = "June";
                break;
            case 6:
                monthInString = "July";
                break;
            case 7:
                monthInString = "August";
                break;
            case 8:
                monthInString = "September";
                break;
            case 9:
                monthInString = "October";
                break;
            case 10:
                monthInString = "November";
                break;
            case 11:
                monthInString = "December";
                break;
            default:
                monthInString = "";
                break;
        }
        return "" +monthInString+ " " +day+ ", " +year+ "";
    }
}