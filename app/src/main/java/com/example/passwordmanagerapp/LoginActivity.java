package com.example.passwordmanagerapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UProperty.INT_START;

public class LoginActivity extends AppCompatActivity {
    Button loginButton;
    EditText email,password;
    TextView signup,forgotPassword;
    String emailText,passwordText;
    boolean flag=false;
    List<LoginInfo> accountList=new ArrayList<>();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton=(Button) findViewById(R.id.login);
        email=(EditText) findViewById(R.id.username);
        password=(EditText) findViewById(R.id.password);
        signup=(TextView) findViewById(R.id.newacc);
        forgotPassword=(TextView) findViewById(R.id.forgetPasscode1);

        //change password symbol dot to asterisk
        password.setTransformationMethod(new AsteriskPasswordTransformationMethod());


        //get all users login info
//        getAllUsersInfo();

        //get instance of firebase auth
        mAuth=FirebaseAuth.getInstance();

        // check if user is already logged in
        if(mAuth.getCurrentUser()!=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            // intent.putExtra("user id", account.userID);
            startActivity(intent);

        }

        //forgot password email
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress= email.getText().toString();

                if(!TextUtils.isEmpty(emailAddress)) {

                    mAuth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),"E-mail sent.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    email.setError("E-mail shouldn't be empty.");
                }
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }

            public void checkUser(){

                emailText=email.getText().toString();
                passwordText=password.getText().toString();

                if(TextUtils.isEmpty(emailText)){
                    email.setError("E-mail field can't be empty.");
                }
                else if(TextUtils.isEmpty(passwordText)){
                    password.setError("Password field can't be empty.");
                }
                else {

//                    for (LoginInfo account : accountList) {
//                        if (account.email.equals(emailText) && account.password.equals(passwordText)) {
//
//                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                            intent.putExtra("user id", account.userID);
//                            flag=true;
//                            Toast.makeText(getApplicationContext(),
//                                    "Login Successfully",Toast.LENGTH_SHORT).show();
//                            startActivity(intent);
//                            break;
//
//                        }
//                    }
//
//                    if(!flag){
//                        Toast.makeText(getApplicationContext(),
//                                "Wrong E-mail or Password.", Toast.LENGTH_SHORT).show();
//                    }

                    mAuth.signInWithEmailAndPassword(emailText,passwordText).addOnSuccessListener(authResult -> {
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        // intent.putExtra("user id", account.userID);
                        startActivity(intent);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(),"User not found",Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent=new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    //get all users login info
    private void getAllUsersInfo(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mref = firebaseDatabase.getReference("users");
        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    LoginInfo account = data.getValue(LoginInfo.class);
//                    System.out.println(account.getName());
                    accountList.add(account);
                }
//                System.out.println(accountList.get(0).getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}