package com.example.passwordmanagerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    EditText name,email,password;
    Button signupButton;
    String userName,userEmail,userPassword;
    TextView loginBack;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name=(EditText) findViewById(R.id.fullnameedt);
        email=(EditText) findViewById(R.id.emailedt);
        password=(EditText) findViewById(R.id.passedt);
        signupButton=(Button) findViewById(R.id.signupbtn);
        loginBack=(TextView) findViewById(R.id.login);

        //change password symbol dot to asterisk
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());


        // get instance of firebase auth
        mAuth=FirebaseAuth.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addUser();

            }
            private void addUser(){

                userName=name.getText().toString();
                userEmail=email.getText().toString();
                userPassword=password.getText().toString();

                if((!TextUtils.isEmpty(userName)&& !TextUtils.isEmpty(userEmail))&& !TextUtils.isEmpty(userPassword)){

                    // get instance of firebase auth
                    mAuth=FirebaseAuth.getInstance();

                    //get instance for realtime database
                    FirebaseDatabase firebaseDatabase= FirebaseDatabase.getInstance();
                    DatabaseReference mref=firebaseDatabase.getReference("users");

                    mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnSuccessListener(authResult -> {
                        String id= mAuth.getCurrentUser().getUid();

                        LoginInfo userData=new LoginInfo(id,userName,userEmail,userPassword);

                        mref.child(id).setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                name.setText("");
                                email.setText("");
                                password.setText("");

                                Toast.makeText(getApplicationContext(),"User Added Successfully",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Sorry, something went wrong.",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Authentication Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
//                    String id= mref.push().getKey();
//
//                    LoginInfo userData=new LoginInfo(id,userName,userEmail,userPassword);
//
//                    mref.child(id).setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            name.setText("");
//                            email.setText("");
//                            password.setText("");
//
//                            Toast.makeText(getApplicationContext(),"User Added Successfully",Toast.LENGTH_SHORT).show();
//                            Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
//                            startActivity(intent);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getApplicationContext(),"Sorry, something went wrong.",Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }
                else if(TextUtils.isEmpty(userName)){
                    name.setError("Name field can't be empty");
                }
                else if(TextUtils.isEmpty(userEmail)){
                    email.setError("E-mail field can't be empty");
                }
                else if(TextUtils.isEmpty(userPassword)){
                    password.setError("Password field can't be empty");
                }
            }
        });

        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}