package com.example.itoiletmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button submitEBtn;
    private EditText security;
    private EditText newPass;
    private EditText verifyPass;
    private Button submitPBtn;
    private TextView question;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        db = new DBHandler(this);

        //associate all the layout elements
        email = findViewById(R.id.emailEditText);
        submitEBtn = findViewById(R.id.submitEBtn);
        security = findViewById(R.id.securityText);
        newPass = findViewById(R.id.passText);
        verifyPass = findViewById(R.id.verifyPassText);
        submitPBtn = findViewById(R.id.submitPassBtn);
        question = findViewById(R.id.textView17);

        //make some invisible at first
        security.setVisibility(View.GONE);
        newPass.setVisibility(View.GONE);
        verifyPass.setVisibility(View.GONE);
        submitPBtn.setVisibility(View.GONE);
        question.setVisibility(View.GONE);

        //listener for first submit button
        submitEBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString().trim();
                //check if users exist in the database
                if(db.userExists(userEmail)){
                    //show what was invisible
                    security.setVisibility(View.VISIBLE);
                    newPass.setVisibility(View.VISIBLE);
                    verifyPass.setVisibility(View.VISIBLE);
                    submitPBtn.setVisibility(View.VISIBLE);
                    question.setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Email is incorrect!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //listener for second submit button
        submitPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPass.getText().toString().trim();
                String verifyPassword = verifyPass.getText().toString().trim();
                String securityQ = security.getText().toString().trim();
                String userEmail = email.getText().toString().trim();

                //handle errors
                if(newPassword.isEmpty() || verifyPassword.isEmpty() || securityQ.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "Please fill out all fields!", Toast.LENGTH_SHORT).show();
                }
                else if(!newPassword.equals(verifyPassword)){
                    Toast.makeText(ForgotPasswordActivity.this, "Passwords must match!", Toast.LENGTH_SHORT).show();
                }
                else if(!db.checkPass(newPassword)){
                    Toast.makeText(ForgotPasswordActivity.this, "Passwords must consist of at least 8 characters and a number!", Toast.LENGTH_SHORT).show();
                }
                else if(!db.securityRight(userEmail, securityQ)){
                    Toast.makeText(ForgotPasswordActivity.this, "Answer to question is wrong!", Toast.LENGTH_SHORT).show();
                }
                else{
                    //if all is correct update the password
                    db.updateForgotPass(userEmail, newPassword);
                    Toast.makeText(ForgotPasswordActivity.this, "Successfully changed password", Toast.LENGTH_SHORT).show();
                    //back to log in
                    openLoginActivity();
                }


            }
        });

    }

    private void openLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

}
