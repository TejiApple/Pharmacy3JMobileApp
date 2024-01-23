package com.project.pharmacy3jmobileapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.project.pharmacy3jmobileapp.MainActivity;
import com.project.pharmacy3jmobileapp.R;

public class SettingsActivity extends AppCompatActivity {

    String[] itemlist = {"Account Settings", "Change Password", "My Orders", "Logout"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ListView lvSettings = findViewById(R.id.lvSettings);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_listview, R.id.tvListItem, itemlist);
        lvSettings.setAdapter(adapter);
        lvSettings.setOnItemClickListener((parent, view, position, id) -> {
           switch (position) {
               case 0 : {
                   startActivity(new Intent(getApplicationContext(), AccountSettingsActivity.class));
               }
               break;
               case 1 : {
                   startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
               }
               break;
               case 2 : {
                   SharedPreferences sharedPref = getSharedPreferences("sp", MODE_PRIVATE);
                   String customerName = sharedPref.getString("customerName", "");
                   Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                   intent.putExtra("customerName", customerName);
                   startActivity(intent);
               }
               break;
               case 3 : {
                   FirebaseAuth.getInstance().signOut();
                   startActivity(new Intent(getApplicationContext(), MainActivity.class));
               }
               break;
           }
        });
    }
}