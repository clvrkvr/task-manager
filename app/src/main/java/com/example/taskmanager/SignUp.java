package com.example.taskmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    private final String DB_NAME = "TaskManager";
    private final String USER_TABLE_NAME = "users";
    private final String USER_ID = "id";
    private final String USER_USERNAME = "username";
    private final String USER_PASSWORD = "password";
    private SQLiteDatabase offlineDb;
    private Button btnCreateAccount;
    private EditText editTxtUsername;
    private EditText editTxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnCreateAccount = findViewById(R.id.btnCreateAccount_SignUpPage);
        editTxtUsername = findViewById(R.id.editTxtUsername_SignUpPage);
        editTxtPassword = findViewById(R.id.editTxtPassword_SignUpPage);

        // Creates an account for the user if the credentials
        // dont match the credentials in the database
        btnCreateAccount.setOnClickListener((View view) -> {
            // Grab the text or data from the text fields
            String usernameFromTextField = editTxtUsername.getText().toString();
            String passwordFromTextField = editTxtPassword.getText().toString();

            String usernameFromTable = "";
            try {
                // Open or Create database and user table if it does not exist
                offlineDb = this.openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
                offlineDb.execSQL("CREATE TABLE IF NOT EXISTS " +USER_TABLE_NAME+ " (" +USER_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +USER_USERNAME+ " VARCHAR NOT NULL, "+USER_PASSWORD+" VARCHAR NOT NULL)");
                // Points to a record in a database
                Cursor cursor = offlineDb.rawQuery("SELECT * FROM "+USER_TABLE_NAME+" WHERE " +USER_USERNAME+ " = '" +usernameFromTextField+ "'", null);

                int usernameIndex = cursor.getColumnIndex(USER_USERNAME);
                if (cursor.moveToFirst()) {
                    do {
                        usernameFromTable = cursor.getString(usernameIndex);
                    } while (cursor.moveToNext());
                }
                // After using Cursor, always close it
                cursor.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create account checking if the fields are empty or if the account already exist
            if(usernameFromTextField.equals("") && !passwordFromTextField.equals("")) {
                Toast.makeText(this, "Username field is empty! Please fill it in before creating an account", Toast.LENGTH_SHORT).show();
            }
            if(!usernameFromTextField.equals("") && passwordFromTextField.equals("")) {
                Toast.makeText(this, "Password field is empty! Please fill it in before creating an account", Toast.LENGTH_SHORT).show();
            }
            if(usernameFromTextField.equals("") && passwordFromTextField.equals("")) {
                Toast.makeText(this, "Username and Password field is empty! Please fill it in before creating an account", Toast.LENGTH_SHORT).show();
            }
            if(!usernameFromTextField.equals("") && !passwordFromTextField.equals("")) {
                if(!usernameFromTable.equals("")) {
                    Toast.makeText(this, "Username already exist!", Toast.LENGTH_SHORT).show();
                }
                if(usernameFromTable.equals("")) {
                    // if the user credentials dont match the credentials in the database, create their
                    // account, save it to the database then start the login activity
                    createAccount(usernameFromTextField, passwordFromTextField);
                    Toast.makeText(this, "Account Successfully Created!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, Login.class));
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    // function to create an account and add it to the user table
    protected void createAccount(String username, String password){
        String sql = "INSERT INTO " +USER_TABLE_NAME+ " (" +USER_USERNAME+ ", " +USER_PASSWORD+ ") VALUES (?, ?)";
        SQLiteStatement statement = offlineDb.compileStatement(sql);
        statement.bindString(1, username);
        statement.bindString(2, password);
        statement.execute();
    }
}