package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    protected static final String USER_ID_KEY = "com.example.taskmanager.EXTRA.USER_ID";
    private final String DB_NAME = "TaskManager";
    private final String USER_TABLE_NAME = "users";
    private final String USER_ID = "id";
    private final String USER_USERNAME = "username";
    private final String USER_PASSWORD = "password";
    private SQLiteDatabase offlineDb;
    private LinearLayout linearLayoutUsername;
    private LinearLayout linearLayoutPassword;
    private Button btnLogin;
    private Button btnLogout;
    private Button btnSignUp;
    private TextView txtLoggedIn;
    private EditText editTxtUsername;
    private EditText editTxtPassword;
    private int userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // if the user data passed by the Main Activity is empty,
        // meaning no one logged in
        userID = ((getIntent().getIntExtra(MainActivity.LOGIN_KEY, 0)) == 0) ? userID : getIntent().getIntExtra(MainActivity.LOGIN_KEY, 0);

        linearLayoutUsername = findViewById(R.id.linearLayoutUsernameLogin);
        linearLayoutPassword = findViewById(R.id.linearLayoutPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin_LoginPage);
        btnLogout = findViewById(R.id.btnLogout_LoginPage);
        btnSignUp = findViewById(R.id.btnSignUp_LoginPage);
        txtLoggedIn = findViewById(R.id.txtLoggedIn);
        editTxtUsername = findViewById(R.id.editTxtUsername_LoginPage);
        editTxtPassword = findViewById(R.id.editTxtPassword_LoginPage);

        // Checking if a user is logged in or not
        if(userID != 0) {
            linearLayoutUsername.setVisibility(View.INVISIBLE);
            linearLayoutPassword.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
            btnSignUp.setVisibility(View.INVISIBLE);
            txtLoggedIn.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            linearLayoutUsername.setVisibility(View.VISIBLE);
            linearLayoutPassword.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
            btnSignUp.setVisibility(View.VISIBLE);
            txtLoggedIn.setVisibility(View.INVISIBLE);
            btnLogout.setVisibility(View.INVISIBLE);
        }

        // Logs the user in if the credentials match the credentials in the database
        btnLogin.setOnClickListener((View view) -> {
            // Grab the text or data from the text fields
            String usernameFromTextField = editTxtUsername.getText().toString();
            String passwordFromTextField = editTxtPassword.getText().toString();

            int idFromTable = 0;
            String usernameFromTable = "";
            String passwordFromTable = "";
            try {
                // Open or Create database and user table if it does not exist
                offlineDb = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
                offlineDb.execSQL("CREATE TABLE IF NOT EXISTS " +USER_TABLE_NAME+ " (" +USER_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +USER_USERNAME+ " VARCHAR NOT NULL, "+USER_PASSWORD+" VARCHAR NOT NULL)");
                // Points to a record in a database
                Cursor cursor = offlineDb.rawQuery("SELECT * FROM "+USER_TABLE_NAME+" WHERE " +USER_USERNAME+ " = '" +usernameFromTextField+ "'", null);

                int idIndex = cursor.getColumnIndex(USER_ID);
                int usernameIndex = cursor.getColumnIndex(USER_USERNAME);
                int passwordIndex = cursor.getColumnIndex(USER_PASSWORD);
                if (cursor.moveToFirst()) {
                    do {
                        idFromTable = cursor.getInt(idIndex);
                        usernameFromTable = cursor.getString(usernameIndex);
                        passwordFromTable = cursor.getString(passwordIndex);
                    } while (cursor.moveToNext());
                }
                // After using Cursor, always close it
                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Login account checking if the fields are empty or if the account does not exist
            if(usernameFromTextField.equals("") && !passwordFromTextField.equals("")) {
                Toast.makeText(this, "Username field is empty! Please fill it in before logging in", Toast.LENGTH_SHORT).show();
            }
            if(!usernameFromTextField.equals("") && passwordFromTextField.equals("")) {
                Toast.makeText(this, "Password field is empty! Please fill it in before logging in", Toast.LENGTH_SHORT).show();
            }
            if(usernameFromTextField.equals("") && passwordFromTextField.equals("")) {
                Toast.makeText(this, "Username and Password field is empty! Please fill it in before logging in", Toast.LENGTH_SHORT).show();
            }
            if(!usernameFromTextField.equals("") && !passwordFromTextField.equals("")) {
                if(!usernameFromTextField.equals(usernameFromTable) && passwordFromTextField.equals(passwordFromTable)) {
                    Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                }
                if(usernameFromTextField.equals(usernameFromTable) && !passwordFromTextField.equals(passwordFromTable)) {
                    Toast.makeText(this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                }
                if(!usernameFromTextField.equals(usernameFromTable) && !passwordFromTextField.equals(passwordFromTable)) {
                    Toast.makeText(this, "Username and Password does not exist! Please create an account", Toast.LENGTH_SHORT).show();
                }
                if(usernameFromTextField.equals(usernameFromTable) && passwordFromTextField.equals(passwordFromTable)) {
                    // if the user credentials match the credentials in the database, log them in then pass the user id to the main activity
                    Toast.makeText(this, "Successfully Logged In! Welcome " +usernameFromTable+ "!", Toast.LENGTH_SHORT).show();
                    TaskList.taskList.clear();
                    startActivity((new Intent(this, MainActivity.class)).putExtra(USER_ID_KEY, idFromTable));
                    overridePendingTransition(0, 0);
                }
            }
        });

        // Logout functionality of the app, logs out the current user logged in.
        btnLogout.setOnClickListener((View view) -> {
            // Clear the main activity sharedpreferences
            SharedPreferences.Editor userSharedPrefEditor = MainActivity.userSharedPrefs.edit();
            userSharedPrefEditor.clear();
            userSharedPrefEditor.apply();
            // Clear the task activity sharedpreferences
            SharedPreferences.Editor taskSharedPrefEditor = Task.taskSharedPrefs.edit();
            taskSharedPrefEditor.clear();
            taskSharedPrefEditor.apply();
            // if a user is logged in, log them out by setting the id number back to the default value which is zero
            // which is the value of the user id when they are logged out
            if(userID != 0) userID = 0;
            TaskList.taskList.clear();
            // pass the user id to the main activity
            startActivity((new Intent(this, MainActivity.class)).putExtra(USER_ID_KEY, userID));
            overridePendingTransition(0, 0);
        });

        // Launch the SignUp Activity if username and password does not exist
        btnSignUp.setOnClickListener((View view) -> {
            startActivity(new Intent(this, SignUp.class));
            overridePendingTransition(0, 0);
        });
    }
}