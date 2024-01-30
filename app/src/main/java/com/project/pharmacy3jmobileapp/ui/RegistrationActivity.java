package com.project.pharmacy3jmobileapp.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.pharmacy3jmobileapp.MainActivity;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseStorage firebaseStorage;
    DatabaseReference dbRef;
    EditText etCompleteName, etMobilePhone, etBirthdate, etSeniorCitizenId, etCityMunicipality, etBarangay, etHouseNo, etUsernameReg, etPasswordReg, etConfirmPasswordReg;
    ImageButton btnCamera;
    ImageView ivIDPhoto;
    ProgressBar progressBar;
    final Calendar myCalendar= Calendar.getInstance();
    private final int REQUEST_CODE = 22;
    private Uri imageUri;
    Bitmap image;
    String fileName;
    byte[] imageData = null;

    boolean isLegalAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        dbRef = FirebaseDatabase.getInstance().getReference();

        etCompleteName = findViewById(R.id.etCompleteName);
        etMobilePhone = findViewById(R.id.etMobilePhone);
        etBirthdate = findViewById(R.id.etBirthdate);
        etSeniorCitizenId = findViewById(R.id.etSeniorCitizenId);
        etCityMunicipality = findViewById(R.id.etCityMunicipality);
        etBarangay = findViewById(R.id.etBarangay);
        etCityMunicipality.setEnabled(false);
        etBarangay.setEnabled(false);
        etHouseNo = findViewById(R.id.etHouseNo);
        etUsernameReg = findViewById(R.id.etUsernameReg);
        etPasswordReg = findViewById(R.id.etPasswordReg);
        etConfirmPasswordReg = findViewById(R.id.etConfirmPasswordReg);
        progressBar = findViewById(R.id.progressBar);
        btnCamera = findViewById(R.id.btnCamera);
        ivIDPhoto = findViewById(R.id.ivIDPhoto);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> saveInputData());

        Button btnCancelRegistration = findViewById(R.id.btnCancelRegistration);
        btnCancelRegistration.setOnClickListener(v -> onBackPressed());

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                try {
                    isLegalAge = ageValidation(year, month, day);
                    if (isLegalAge == false) {
                        etBirthdate.setError("The user is not on legal age yet");
                        Toast.makeText(RegistrationActivity.this, "The user is not on legal age yet", Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                updateLabel();
            }
        };
        etBirthdate.setOnClickListener(v -> {
            new DatePickerDialog(this,date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        scIdFieldValidation();
        openCamera();
    }

    private void scIdFieldValidation(){
        etSeniorCitizenId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    btnCamera.setVisibility(View.VISIBLE);
                } else {
                    btnCamera.setVisibility(View.GONE);
                    ivIDPhoto.setVisibility(View.GONE);
                    if (imageData != null){
                        imageData = null;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateLabel(){
        String myFormat="MMM. dd, yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etBirthdate.setText(dateFormat.format(myCalendar.getTime()));
    }

    private boolean ageValidation(int year, int month, int day) throws ParseException {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss O yyyy");

            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String dob = month + "/" + day + "/" + year;
            Date completeDob = sdf.parse(dob);
            String endDate = today.toString();
            String startDate = completeDob.toString();

            ZoneOffset zoneOffset = ZoneOffset.ofHours(6);

            OffsetDateTime offsetStartDate = OffsetDateTime.parse(startDate, formatter).withOffsetSameLocal(zoneOffset);
            OffsetDateTime offsetEndDate = OffsetDateTime.parse(endDate, formatter).withOffsetSameLocal(zoneOffset);

            Period period = Period.between(offsetStartDate.toLocalDate(), offsetEndDate.toLocalDate());

            if (period.getYears() < 18){
                return false;
            }
        } else {
            Date today = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            String dob = month + "/" + day + "/" + year;
            Date completeDob = sdf.parse(dob);
//            Date completeToday = sdf.parse(today.toString());

            assert completeDob != null;
            long time_diff = today.getTime() - completeDob.getTime();
            long years_difference = (time_diff / (1000L*60*60*24*365));

            if (years_difference < 18){
                return false;
            }
        }
        return true;
    }
    private void saveInputData() {
        progressBar.setVisibility(View.VISIBLE);

        String completeName = etCompleteName.getText().toString();
        String mobilePhone = etMobilePhone.getText().toString();
        String birthdate = etBirthdate.getText().toString();
        String seniorCitizenId = etSeniorCitizenId.getText().toString();
        String cityMunicipality = etCityMunicipality.getText().toString();
        String barangay = etBarangay.getText().toString();
        String houseNo = etHouseNo.getText().toString();
        String username = etUsernameReg.getText().toString();
        String password = etPasswordReg.getText().toString();
        String confirmPassword = etConfirmPasswordReg.getText().toString();
        String usernameWithGmail = username + "@gmail.com";
        if (TextUtils.isEmpty(completeName)){
            etCompleteName.setError("Complete name is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(birthdate)){
            etBirthdate.setError("Birthdate is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!TextUtils.isEmpty(seniorCitizenId)){
            if (imageData.length == 0){
                Toast.makeText(this, "Senior Citizen ID Photo is required", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
        }

        if (TextUtils.isEmpty(mobilePhone)){
            etMobilePhone.setError("Mobile/Phone number is required");
            progressBar.setVisibility(View.GONE);
            return;
        } else if (mobilePhone.length() > 0 && mobilePhone.length() < 11){
            etMobilePhone.setError("Mobile/Phone number is invalid");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(houseNo)){
            etHouseNo.setError("House No., Street, and/or Purok is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(cityMunicipality)){
            etCityMunicipality.setError("City/Municipality is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(barangay)){
            etBarangay.setError("Barangay is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(username)){
            etUsernameReg.setError("Username is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (TextUtils.isEmpty(password)){
            etPasswordReg.setError("Password is required");
            progressBar.setVisibility(View.GONE);
            return;
        }

        if (!password.equals(confirmPassword)){
            etConfirmPasswordReg.setError("Password is different");
            progressBar.setVisibility(View.GONE);
            return;
        }

        RegistrationModel registrationModel = new RegistrationModel(completeName, mobilePhone, birthdate, seniorCitizenId, cityMunicipality,
                barangay, houseNo, username, password);

        firebaseAuth.createUserWithEmailAndPassword(usernameWithGmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    dbRef.child("users").push().child(username).setValue(registrationModel);

                    if (imageData == null){
                        Toast.makeText(RegistrationActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else if (imageData.length > 0){
                        fileName = "sc_id_" + etCompleteName.getText().toString() + ".jpg";
                        StorageReference storageReference = firebaseStorage.getReference().child("images").child(fileName);
                        UploadTask uploadTask = storageReference.putBytes(imageData);
                        uploadTask.addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "User created!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Error : " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                    Toast.makeText(RegistrationActivity.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openCamera(){
        btnCamera.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            assert data != null;
//            imageUri = data.getData();
            image = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageData = baos.toByteArray();
            ivIDPhoto.setImageBitmap(image);
            ivIDPhoto.setVisibility(View.VISIBLE);
        }
    }
}