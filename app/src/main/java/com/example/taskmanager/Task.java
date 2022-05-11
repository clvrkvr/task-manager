package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Task extends AppCompatActivity {
    protected static final String TASK_TITLE_DATA = "title";
    protected static final String TASK_CONTENT_DATA = "content";
    private final String DB_NAME = "TaskManager";
    private final String TASK_TABLE_NAME = "tasks";
    private final String TASK_ID = "id";
    private final String TASK_TITLE = "title";
    private final String TASK_CONTENT = "content";
    private final String TASK_DATE = "date";
    private final String TASK_USER = "user_id";
    private Button btnSave;
    private Button btnCancel;
    private FloatingActionButton fabSetDate;
    private EditText editTextTaskTitle;
    private EditText editTextTaskContent;
    private SQLiteDatabase offlineDb;
    public static SharedPreferences taskSharedPrefs;
    private final String taskSharedPrefFile = "com.example.taskmanager.tasksharedprefs";
    private String title = "";
    private String content = "";
    private int userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        // check if there are any sharedpreferences saved,
        // if so use them.
        taskSharedPrefs = getSharedPreferences(taskSharedPrefFile, MODE_PRIVATE);
        title = taskSharedPrefs.getString(TASK_TITLE_DATA, title);
        content = taskSharedPrefs.getString(TASK_CONTENT_DATA, content);
        userID = taskSharedPrefs.getInt(MainActivity.USER_LOGGED_IN, userID);

        // if the user data passed by the Main Activity is empty,
        // meaning no one logged in
        userID = ((getIntent().getIntExtra(MainActivity.ADD_TASK_KEY, 0)) == 0) ? userID : getIntent().getIntExtra(MainActivity.ADD_TASK_KEY, 0);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        fabSetDate = findViewById(R.id.fabSetDate);
        editTextTaskTitle = findViewById(R.id.editTxtTaskTitle);
        editTextTaskContent = findViewById(R.id.editTextTaskContent);

        editTextTaskTitle.setText(title);
        editTextTaskContent.setText(content);

        // Save the task to the task list
        btnSave.setOnClickListener((View view) -> {
            // Grab the text or data from the text fields
            title = editTextTaskTitle.getText().toString();
            content = editTextTaskContent.getText().toString();
            String date = "No date set";

            try {
                // Open or Create database and task table if it does not exist
                offlineDb = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
                offlineDb.execSQL("CREATE TABLE IF NOT EXISTS " +TASK_TABLE_NAME+ " (" +TASK_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +TASK_TITLE+
                        " VARCHAR NOT NULL, "+TASK_CONTENT+" VARCHAR NOT NULL, "+TASK_DATE+" VARCHAR NOT NULL, "+TASK_USER+" INTEGER NOT NULL)");
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Checking if the fields are empty
            if((getIntent().getStringExtra(Calendar.MESSAGE_KEY)) != null)
                date = getIntent().getStringExtra(Calendar.MESSAGE_KEY);
            if(title.equals("") && content.equals("") && date.equals("No date set")) {
                Toast.makeText(this, "No task has been saved! All the fields are empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if(title.equals("") && !content.equals("") && !date.equals("No date set")) {
                Toast.makeText(this, "Please add a title!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!title.equals("") && content.equals("") && !date.equals("No date set")) {
                Toast.makeText(this, "The task content must not be empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!title.equals("") && !content.equals("") && date.equals("No date set")) {
                Toast.makeText(this, "Please set a date!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!title.equals("") && content.equals("") && date.equals("No date set")) {
                Toast.makeText(this, "Only the title has been filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(title.equals("") && !content.equals("") && date.equals("No date set")) {
                Toast.makeText(this, "Only the task content has been filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if(title.equals("") && content.equals("") && !date.equals("No date set")) {
                Toast.makeText(this, "The date has been set but the title and task content is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                // Add task to the table
                createTask(title, content, date, userID);

                // Once added, start the MainActivity class
                Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);

                // Empty the fields
                editTextTaskTitle.setText("");
                editTextTaskContent.setText("");
                getIntent().removeExtra(Calendar.MESSAGE_KEY);
            }
        });

        // Cancel adding task
        btnCancel.setOnClickListener((View view) -> {
            // if user clicks cancel, empty the fields
            title = "";
            content = "";
            editTextTaskTitle.setText("");
            editTextTaskContent.setText("");
            getIntent().removeExtra(Calendar.MESSAGE_KEY);
            // start the MainActivity class
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            // delete or remove all saved sharedpreferences
            SharedPreferences.Editor prefEditor = taskSharedPrefs.edit();
            prefEditor.clear();
            prefEditor.apply();
        });

        // Opens the Calendar Activity where we can grab the data of what date is a certain day
        // that we want to set out task to
        fabSetDate.setOnClickListener((View view) -> {
            startActivity(new Intent(this, Calendar.class));
            overridePendingTransition(0, 0);
        });
    }

    // Check if there are changes in activity, if so save the data.
    // This is for the activity life cycle, when users rotate their screens etc.
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefEditor = taskSharedPrefs.edit();
        prefEditor.putString(TASK_TITLE_DATA, editTextTaskTitle.getText().toString());
        prefEditor.putString(TASK_CONTENT_DATA, editTextTaskContent.getText().toString());
        prefEditor.putInt(MainActivity.USER_LOGGED_IN, userID);
        prefEditor.apply();
    }

    // function to add a task in the task table.
    protected void createTask(String title, String taskContent, String date, int user){
        String sql = "INSERT INTO " +TASK_TABLE_NAME+ " (" +TASK_TITLE+ ", " +TASK_CONTENT+ ", " +TASK_DATE+ ", " +TASK_USER+ ") VALUES (?, ?, ?, ?)";
        SQLiteStatement statement = offlineDb.compileStatement(sql);
        statement.bindString(1, title);
        statement.bindString(2, taskContent);
        statement.bindString(3, date);
        statement.bindLong(4, user);
        statement.execute();
    }
}