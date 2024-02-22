package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    Button btnUpdateCreds, btnCancelChangePassword;
    String username, currentPassword, newPassword, confirmNewPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etUsername = findViewById(R.id.etUsernameUpdate);
        etCurrentPassword = findViewById(R.id.etCurrentPasswordUpdate);
        etNewPassword = findViewById(R.id.etNewPasswordUpdate);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPasswordUpdate);
        btnUpdateCreds = findViewById(R.id.btnUpdateCreds);
        btnCancelChangePassword = findViewById(R.id.btnCancelChangePassword);

        btnUpdateCreds.setOnClickListener(v -> {
            try {
                username = etUsername.getText().toString() + "@gmail.com";
                currentPassword = Objects.requireNonNull(etCurrentPassword.getText()).toString();
                newPassword = Objects.requireNonNull(etNewPassword.getText()).toString();
                confirmNewPassword = Objects.requireNonNull(etConfirmNewPassword.getText()).toString();

                if (TextUtils.isEmpty(newPassword)){
                    etNewPassword.setError("New password is required");
                    return;
                }

                if (TextUtils.isEmpty(newPassword)){
                    etNewPassword.setError("Confirm new password is required");
                    return;
                }

                if (newPassword.equals(confirmNewPassword)){
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
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                                builder.setTitle("Change Password...");
                                                builder.setMessage("Password updated successfully!");
                                                builder.setPositiveButton("OK", (dialog, which) -> {
                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                });
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                                                builder.setTitle("Change Password...");
                                                builder.setMessage("Something went wrong in updating. Please try again later.");
                                                builder.setPositiveButton("OK", (dialog, which) -> {
                                                    dialog.dismiss();
                                                });
                                                builder.create();
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                    builder.setTitle("Change Password...");
                    builder.setMessage("New password and confirm new password fields did not match.");
                    builder.setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.create();
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelChangePassword.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}