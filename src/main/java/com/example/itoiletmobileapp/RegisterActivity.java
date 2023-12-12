package com.example.itoiletmobileapp;


import android.database.Cursor;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText verifyEmail;
    private EditText password;
    private EditText verifyPass;
    private Button registerButton;
    private EditText securityQ;

    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //associate layout elements
        email = findViewById(R.id.email);
        verifyEmail = findViewById(R.id.verify);
        password = findViewById(R.id.password);
        verifyPass = findViewById(R.id.verifyPass);
        registerButton = findViewById(R.id.registerBtn);
        securityQ = findViewById(R.id.security);

        // initialize DB
        db = new DBHandler(this); // Initialize the database handler

        //register user when button clicked
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void registerUser() {
        String userEmail = email.getText().toString().trim();
        String verifyE = verifyEmail.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String verifyPassword = verifyPass.getText().toString().trim();
        String securityQuestion = securityQ.getText().toString().trim();

        // check if email and password are empty
        if (!userEmail.isEmpty() || !userPassword.isEmpty() || verifyPassword.isEmpty() || securityQuestion.isEmpty()) {

            //handle rest of possible errors
            if(checkExists(userEmail)) {
                Toast.makeText(RegisterActivity.this, "This email is already in use!", Toast.LENGTH_SHORT).show();
            }
            else if(!userPassword.equals(verifyPassword)){
                Toast.makeText(RegisterActivity.this, "Passwords must match!", Toast.LENGTH_SHORT).show();
            }
            else if(!db.checkPass(userPassword)) {
                Toast.makeText(RegisterActivity.this, "Password must have atleast 8 characters and a number!", Toast.LENGTH_SHORT).show();
            }
            else if (!userEmail.equals(verifyE)) {
                Toast.makeText(RegisterActivity.this, "Emails must match!", Toast.LENGTH_SHORT).show();
            }
            //all is well add the user
            else {
                int accountStatus = 1;
                // Add the user
                db.addUser(userEmail, userPassword, accountStatus, securityQuestion);
                Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                //go back to login
                openLoginActivity();

            }
        }
        else {
            Toast.makeText(RegisterActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
        }
    }

    //method to check if email already exists
    private boolean checkExists(String email) {
        Cursor cursor = db.getAllUsers();

        if(cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(db.getColumnEmail());
            do {
                String inDB = cursor.getString(index);
                if(inDB.equals(email)) {
                    cursor.close();
                    return true;
                }
            } while(cursor.moveToNext());
        }

        cursor.close();
        return false;
    }
    private void openLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}