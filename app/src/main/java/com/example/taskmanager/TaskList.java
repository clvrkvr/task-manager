package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class TaskList extends AppCompatActivity {
    private final String DB_NAME = "TaskManager";
    private final String TASK_TABLE_NAME = "tasks";
    private final String TASK_ID = "id";
    private final String TASK_TITLE = "title";
    private final String TASK_CONTENT = "content";
    private final String TASK_DATE = "date";
    private final String TASK_USER = "user_id";
    private SQLiteDatabase offlineDb;
    private int userID = 0;
    public static ArrayList<String> taskList=new ArrayList<>();
    private ListView taskListView;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // if the user data passed by the Main Activity is empty,
        // meaning no one logged in
        userID = ((getIntent().getIntExtra(MainActivity.VIEW_TASK_KEY, 0)) == 0) ? userID : getIntent().getIntExtra(MainActivity.VIEW_TASK_KEY, 0);

        taskListView = findViewById(R.id.taskList);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList);
        taskListView.setAdapter(arrayAdapter);

        try {
            // Open or Create database and task table if it does not exist
            offlineDb = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
            offlineDb.execSQL("CREATE TABLE IF NOT EXISTS " +TASK_TABLE_NAME+ " (" +TASK_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    +TASK_TITLE+ " VARCHAR NOT NULL, "+TASK_CONTENT+" VARCHAR NOT NULL, "+TASK_DATE+" VARCHAR NOT NULL, "+TASK_USER+" INTEGER NOT NULL)");
            // Points to a record in a database
            Cursor cursor = offlineDb.rawQuery("SELECT * FROM "+TASK_TABLE_NAME+" WHERE " +TASK_USER+ " = '" +userID+ "'", null);

            int idIndex = cursor.getColumnIndex(TASK_ID);
            int titleIndex = cursor.getColumnIndex(TASK_TITLE);
            int contentIndex = cursor.getColumnIndex(TASK_CONTENT);
            int dateIndex = cursor.getColumnIndex(TASK_DATE);
            int userIndex = cursor.getColumnIndex(TASK_USER);
            if (cursor.moveToFirst()) {
                taskList.clear();
                do {
                    // add task to the task list
                    taskList.add(new TaskData(cursor.getInt(idIndex), cursor.getString(titleIndex), cursor.getString(contentIndex), cursor.getString(dateIndex), cursor.getInt(userIndex)).toString());
                } while (cursor.moveToNext());
                // update the array adapter to check if there are any changes
                // made by the user to update the list of tasks of the user
                arrayAdapter.notifyDataSetChanged();
            }
            // After using Cursor, always close it
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}