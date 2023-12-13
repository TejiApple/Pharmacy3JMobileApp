package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    DatabaseReference dbRef;
    String username, address;
    SharedPreferences sp;
    ArrayList<RegistrationModel> registrationModelArrayList;
    TextView tvUserCompleteName, tvUserMobilePhone, tvUserCityAddress, tvUserBarangayAddress, tvUserHouseAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        dbRef = FirebaseDatabase.getInstance().getReference();
        sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        registrationModelArrayList = new ArrayList<>();
        tvUserCompleteName = findViewById(R.id.etCompleteName);
        tvUserMobilePhone = findViewById(R.id.etMobilePhone);
        tvUserCityAddress = findViewById(R.id.etCityMunicipality);
        tvUserBarangayAddress = findViewById(R.id.etBarangay);
        tvUserHouseAddress = findViewById(R.id.etHouseNo);

    }

    private void retrieveUserData(){
        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        String completeName = Objects.requireNonNull(dataSnapshot.child("completeName").getValue()).toString();
                    String key = dataSnapshot.getKey();
                    dbRef.child("users").child(key).orderByChild("usernameReg").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                RegistrationModel registrationModel = dataSnapshot1.getValue(RegistrationModel.class);
                                registrationModelArrayList.add(registrationModel);

                                String completeName = registrationModelArrayList.get(0).getCompleteName();
                                if (!registrationModelArrayList.get(0).getMobilePhone().isEmpty()){
                                    String mobilePhone = registrationModelArrayList.get(0).getMobilePhone();
                                    tvUserMobilePhone.setText(mobilePhone);
                                }
                                String city = registrationModelArrayList.get(0).getCityMunicipality();
                                String barangay = registrationModelArrayList.get(0).getBarangay();
                                String houseNo = registrationModelArrayList.get(0).getHouseNo();
                                address = houseNo + barangay;
                                tvUserCompleteName.setText(completeName);
                                tvUserCityAddress.setText(city);
                                tvUserBarangayAddress.setText(barangay);
                                tvUserHouseAddress.setText(houseNo);
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

}