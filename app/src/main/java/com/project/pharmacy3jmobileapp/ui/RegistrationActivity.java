package com.project.pharmacy3jmobileapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.pharmacy3jmobileapp.MainActivity;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference dbRef;
    EditText etCompleteName, etMobilePhone, etBirthdate, etSeniorCitizenId, etBarangay, etHouseNo, etUsernameReg, etPasswordReg;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        etCompleteName = findViewById(R.id.etCompleteName);
        etMobilePhone = findViewById(R.id.etMobilePhone);
        etBirthdate = findViewById(R.id.etBirthdate);
        etSeniorCitizenId = findViewById(R.id.etSeniorCitizenId);
        etBarangay = findViewById(R.id.etBarangay);
        etHouseNo = findViewById(R.id.etHouseNo);
        etUsernameReg = findViewById(R.id.etUsernameReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        progressBar = findViewById(R.id.progressBar);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInputData();

            }
        });
    }

    private void saveInputData() {
        progressBar.setVisibility(View.VISIBLE);

        String completeName = etCompleteName.getText().toString();
        String mobilePhone = etMobilePhone.getText().toString();
        String birthdate = etBirthdate.getText().toString();
        String seniorCitizenId = etSeniorCitizenId.getText().toString();
        String barangay = etBarangay.getText().toString();
        String houseNo = etHouseNo.getText().toString();
        String username = etUsernameReg.getText().toString();
        String password = etPasswordReg.getText().toString();
        String usernameWithGmail = username + "@gmail.com";
        if (TextUtils.isEmpty(completeName)){
            etCompleteName.setError("Complete name is required");
            return;
        }

        if (TextUtils.isEmpty(birthdate)){
            etBirthdate.setError("Birthdate is required");
            return;
        }

        if (TextUtils.isEmpty(username)){
            etUsernameReg.setError("Username is required");
            return;
        }

        if (TextUtils.isEmpty(password)){
            etPasswordReg.setError("Password is required");
            return;
        }

        RegistrationModel registrationModel = new RegistrationModel(completeName, mobilePhone, birthdate, seniorCitizenId,
                barangay, houseNo, username, password);

        firebaseAuth.createUserWithEmailAndPassword(usernameWithGmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dbRef.child("users").push().child(completeName).setValue(registrationModel);
                    Toast.makeText(RegistrationActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(RegistrationActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });



    }
}