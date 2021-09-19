package com.example.passwordmanagerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewPasswordActivity extends AppCompatActivity {
EditText currentPassword,newPassword,reTypePassword;
Button updateBtn;

FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        currentPassword=(EditText) findViewById(R.id.currentPassword);
        newPassword=(EditText) findViewById(R.id.newpassword);
        reTypePassword=(EditText) findViewById(R.id.retypePassword);

        currentPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        newPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        reTypePassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        updateBtn=(Button) findViewById(R.id.updatebtn);

        mAuth=FirebaseAuth.getInstance();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserPassword();
            }
        });
    }

    private void updateUserPassword(){

        String currentPass=currentPassword.getText().toString();
        String newPass=newPassword.getText().toString();
        String retypedPass=reTypePassword.getText().toString();

        if((!TextUtils.isEmpty(currentPass)&&!TextUtils.isEmpty(newPass))&&!TextUtils.isEmpty(retypedPass)) {

            if (newPass.equals(retypedPass)) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), currentPass);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                updateInDataBase(newPass);
                                                Toast.makeText(getApplicationContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(NewPasswordActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Password not updated!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error! Authentication Failed.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            } else {
                reTypePassword.setError("Password not matched.");
            }
        }

    }
    public void updateInDataBase(String newPass){
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference mRef=firebaseDatabase.getReference("users");

        //update password in database
        mRef.child(mAuth.getCurrentUser().getUid()).child("password").setValue(newPass);


    }
}