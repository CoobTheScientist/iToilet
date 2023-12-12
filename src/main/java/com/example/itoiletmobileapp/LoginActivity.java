package com.example.itoiletmobileapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class LoginActivity extends Activity{
    private EditText email;
    private EditText password;
    private Button loginBtn;
    private Button forgotBtn;
    private Button registerBtn;
    private static int userID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //populate database
        DBHandler dbHandler = new DBHandler(this);
        //dbHandler.clearBathrooms();
        //dbHandler.populateBathroomsAtLaunch(this);
        dbHandler.populateAdmins(this);


        //associate layout
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        forgotBtn = findViewById(R.id.forgotBtn);
        registerBtn = findViewById(R.id.regBtn);

        //Handle buttons
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = email.getText().toString();
                String pass = password.getText().toString();
                DBHandler db = new DBHandler(LoginActivity.this);
                //Authenticate with database
                boolean adminUser = db.authenticateAdmin(user, pass);
                if (adminUser){
                    openAdminActivity();
                    return;
                }
                userID = db.authenticateUser(user, pass);
                if(userID != -1){
                    openHomeActivity();
                }
                //handle errors
                else{
                    if(db.userExists(user)){
                        Toast.makeText(LoginActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Email is incorrect!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //open forgot password page on click
        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPassword();
            }
        });

        //open register page on click
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRegisterActivity(view);

            }
        });
        if(dbHandler != null){
            dbHandler.close();
        }


    }

    //method for various db methods to get the user id upon login
    public static int getUserID(){
        return userID;
    }

    public void openRegisterActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void openForgotPassword(){
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void openAdminActivity() {
        Intent intent = new Intent(this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

}
