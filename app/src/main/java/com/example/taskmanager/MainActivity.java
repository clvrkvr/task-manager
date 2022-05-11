package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // find all the menu items
        final int LOGIN_ID = R.id.actionLoginOrSignUp;
        final int TASKS_ID = R.id.actionTasks;
        final int ABOUT_ID = R.id.actionAboutApp;
        // switch statement to check which menu item the user clicked
        switch(item.getItemId()) {
            case LOGIN_ID:
                // Launch the Login Activity and pass in the user ID
                return true;
            case TASKS_ID:
                // Launch the TaskList Activity and pass in the user ID
                return true;
            case ABOUT_ID:
                // Launch the AboutApp Activity
                startActivity(new Intent(this, AboutApp.class));
                overridePendingTransition(0, 0);
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}