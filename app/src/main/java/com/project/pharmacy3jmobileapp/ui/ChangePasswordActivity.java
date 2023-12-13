package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.pharmacy3jmobileapp.MainActivity;
import com.project.pharmacy3jmobileapp.R;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etUsername;
    TextInputEditText etCurrentPassword, etNewPassword;
    Button btnUpdateCreds;
    String username, currentPassword, newPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etUsername = findViewById(R.id.etUsernameUpdate);
        etCurrentPassword = findViewById(R.id.etCurrentPasswordUpdate);
        etNewPassword = findViewById(R.id.etNewPasswordUpdate);
        btnUpdateCreds = findViewById(R.id.btnUpdateCreds);

        btnUpdateCreds.setOnClickListener(v -> {
            try {
                username = etUsername.getText().toString() + "@gmail.com";
                currentPassword = Objects.requireNonNull(etCurrentPassword.getText()).toString();
                newPassword = Objects.requireNonNull(etNewPassword.getText()).toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                AuthCredential credential = EmailAuthProvider.getCredential(username, currentPassword);

                if (!currentPassword.isEmpty() || !newPassword.isEmpty() || etUsername.getText().length() > 0){
                    assert user != null;
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(ChangePasswordActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        } else {
                                            Toast.makeText(ChangePasswordActivity.this, "Something went wrong in updating. Please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Missing required fields!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}