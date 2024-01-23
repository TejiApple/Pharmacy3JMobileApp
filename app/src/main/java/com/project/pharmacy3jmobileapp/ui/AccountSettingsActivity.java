package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AccountSettingsActivity extends AppCompatActivity {

    DatabaseReference dbRef;
    String username, key;
    SharedPreferences sp;
    ArrayList<RegistrationModel> registrationModelArrayList;
    EditText etCompleteName, etMobilePhone, etBirthdate, etSeniorCitizenId, etHouseNo;
    TextView editInfo;
    Button btnSubmit, btnCancel;

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        dbRef = FirebaseDatabase.getInstance().getReference();

        sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        registrationModelArrayList = new ArrayList<>();

        etCompleteName = findViewById(R.id.etAccountCompleteName);
        etMobilePhone = findViewById(R.id.etAccountMobilePhone);
        etBirthdate = findViewById(R.id.etAccountBirthdate);
        etSeniorCitizenId = findViewById(R.id.etAccountSeniorCitizenId);
        etHouseNo = findViewById(R.id.etAccountHouseNo);
        editInfo = findViewById(R.id.tvEditInfo);
        btnSubmit = findViewById(R.id.btnSubmitUpdate);
        btnCancel = findViewById(R.id.btnCancelUpdate);

        retrieveData();
        proceedToEditInfo();
        cancelEditInfo();
        submitUpdatedData();
    }

    private void proceedToEditInfo() {
        editInfo.setOnClickListener(v -> {
            enableTextFields();
        });
    }

    private void cancelEditInfo() {
        btnCancel.setOnClickListener(v -> {
            disableAndClearTextFields();
            hideButtons();

        });
    }

    private void retrieveData(){
        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        String completeName = Objects.requireNonNull(dataSnapshot.child("completeName").getValue()).toString();
                    key = dataSnapshot.getKey();
                    dbRef.child("users").child(key).orderByChild("usernameReg").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                RegistrationModel registrationModel = dataSnapshot1.getValue(RegistrationModel.class);
                                registrationModelArrayList.add(registrationModel);

                                String address = null;
                                try {
                                    String city = registrationModelArrayList.get(0).getCityMunicipality();
                                    String barangay = registrationModelArrayList.get(0).getBarangay();
                                    String houseNo = registrationModelArrayList.get(0).getHouseNo();
                                    if (city == null && !houseNo.isEmpty()) {
                                        address = houseNo;
                                    } else if (houseNo == null || houseNo.isEmpty() && !barangay.isEmpty()) {
                                        address = "";
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(AccountSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                String completeName = registrationModelArrayList.get(0).getCompleteName();
                                String mobilePhone = registrationModelArrayList.get(0).getMobilePhone();

                                etHouseNo.setText(address);
                                etCompleteName.setText(completeName);
                                etMobilePhone.setText(mobilePhone);
                                if (registrationModelArrayList.get(0).getBirthdate().isEmpty()){
                                    etBirthdate.setText("");
                                } else {
                                    String birthdate = registrationModelArrayList.get(0).getBirthdate();
                                    etBirthdate.setText(birthdate);
                                }

                                if (registrationModelArrayList.get(0).getSeniorCitizenId().isEmpty()){
                                    etSeniorCitizenId.setText("");
                                } else {
                                    String seniorCitizenId = registrationModelArrayList.get(0).getSeniorCitizenId();
                                    etSeniorCitizenId.setText(seniorCitizenId);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void submitUpdatedData(){
        btnSubmit.setOnClickListener(v -> {
            try {
                String updatedCompleteName = etCompleteName.getText().toString();
                String updatedMobilePhone = etMobilePhone.getText().toString();
                String updatedBirthdate = etBirthdate.getText().toString();
                String updatedSeniorCitizenId = etSeniorCitizenId.getText().toString();
                String updatedHouseNo = etHouseNo.getText().toString();

                Map<String, Object> updatedUserDataMap = new HashMap<>();
                updatedUserDataMap.put("completeName", updatedCompleteName);
                updatedUserDataMap.put("mobilePhone", updatedMobilePhone);
                updatedUserDataMap.put("birthdate", updatedBirthdate);
                updatedUserDataMap.put("seniorCitizenId", updatedSeniorCitizenId);
                updatedUserDataMap.put("houseNo", updatedHouseNo);
                dbRef.child("users").child(key).child(username).updateChildren(updatedUserDataMap);

                Toast.makeText(this, "User info is updated succesfully", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
            } catch (Exception e){
                Toast.makeText(this, "There is an error in updating user info. Please try again or contact support.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableTextFields(){
        etCompleteName.setEnabled(true);
        etMobilePhone.setEnabled(true);
        etBirthdate.setEnabled(true);
        etSeniorCitizenId.setEnabled(true);
        etHouseNo.setEnabled(true);
        setBirthdate();
        showButtons();
    }

    private void disableAndClearTextFields(){
        etCompleteName.getText().clear();
        etCompleteName.setEnabled(false);
        etMobilePhone.getText().clear();
        etMobilePhone.setEnabled(false);
        etBirthdate.getText().clear();
        etBirthdate.setEnabled(false);
        etSeniorCitizenId.getText().clear();
        etSeniorCitizenId.setEnabled(false);
        etHouseNo.getText().clear();
        etHouseNo.setEnabled(false);

    }

    private void showButtons(){
        btnSubmit.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
    }

    private void hideButtons(){
        btnSubmit.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    private void setBirthdate(){
        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };
        etBirthdate.setOnClickListener(v -> {
            new DatePickerDialog(this,date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateLabel(){
        String myFormat="MMM. dd, yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        etBirthdate.setText(dateFormat.format(myCalendar.getTime()));
    }

}