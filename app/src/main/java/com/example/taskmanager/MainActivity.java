package com.example.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    protected static final String ADD_TASK_KEY = "com.example.taskmanager.EXTRA.ADD_TASK";
    protected static final String LOGIN_KEY = "com.example.taskmanager.EXTRA.LOGIN";
    protected static final String VIEW_TASK_KEY = "com.example.taskmanager.EXTRA.VIEW_TASK";
    protected static final String USER_LOGGED_IN = "user";
    private final String DB_NAME = "TaskManager";
    private final String USER_TABLE_NAME = "users";
    private final String USER_ID = "id";
    private final String USER_USERNAME = "username";
    private final String USER_PASSWORD = "password";
    private final String TASK_TABLE_NAME = "tasks";
    private final String TASK_ID = "id";
    private final String TASK_TITLE = "title";
    private final String TASK_CONTENT = "content";
    private final String TASK_DATE = "date";
    private final String TASK_USER = "user_id";
    private SQLiteDatabase offlineDb;
    private FloatingActionButton fabAddTask;
    public static SharedPreferences userSharedPrefs;
    private final String userSharedPrefFile = "com.example.taskmanager.usersharedprefs";
    private int userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // check if there are any sharedpreferences saved,
        // if so use them.
        userSharedPrefs = getSharedPreferences(userSharedPrefFile, MODE_PRIVATE);
        userID = userSharedPrefs.getInt(USER_LOGGED_IN, userID);

        // if the user data passed by the Login Activity is empty,
        // meaning no one logged in
        userID = ((getIntent().getIntExtra(Login.USER_ID_KEY, 0)) == 0) ? userID : getIntent().getIntExtra(Login.USER_ID_KEY, 0);

        fabAddTask = findViewById(R.id.fabAddTask_MainPage);

        // onClickListener for the add task fab button to add task
        fabAddTask.setOnClickListener((View view) -> {
            startActivity((new Intent(this, Task.class)).putExtra(ADD_TASK_KEY, userID));
            overridePendingTransition(0, 0);
        });

        try {
            // Open or Create database and tables if it does not exist
            offlineDb = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            offlineDb.execSQL("CREATE TABLE IF NOT EXISTS " +USER_TABLE_NAME+ " (" +USER_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +USER_USERNAME+ " VARCHAR NOT NULL, "+USER_PASSWORD+" VARCHAR NOT NULL)");
            offlineDb.execSQL("CREATE TABLE IF NOT EXISTS " +TASK_TABLE_NAME+ " (" +TASK_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +TASK_TITLE+ " VARCHAR NOT NULL, " +TASK_CONTENT+ " VARCHAR NOT NULL, "+TASK_DATE+" VARCHAR NOT NULL, " +TASK_USER+ " INTEGER NOT NULL)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Action Bar Menu Items
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
                startActivity((new Intent(this, Login.class)).putExtra(LOGIN_KEY, userID));
                overridePendingTransition(0, 0);
                return true;
            case TASKS_ID:
                // Launch the TaskList Activity and pass in the user ID
                startActivity((new Intent(this, TaskList.class)).putExtra(VIEW_TASK_KEY, userID));
                overridePendingTransition(0, 0);
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

    // Get the menu items to appear in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Check if there are changes in activity, if so save the data.
    // This is for the activity life cycle, when users rotate their screens etc.
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefEditor = userSharedPrefs.edit();
        prefEditor.putInt(USER_LOGGED_IN, userID);
        prefEditor.apply();
    }
}