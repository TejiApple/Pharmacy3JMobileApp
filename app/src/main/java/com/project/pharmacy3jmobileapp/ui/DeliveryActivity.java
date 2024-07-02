package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.pharmacy3jmobileapp.MainActivity;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrdersModel;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;
import com.project.pharmacy3jmobileapp.ui.adapter.DeliveryDetailsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class DeliveryActivity extends AppCompatActivity {

    String customerName;
    DatabaseReference dbRef;
    ArrayList<OrdersModel> ordersModelArrayList;
    DeliveryDetailsAdapter deliveryDetailsAdapter;
    ListView lvCustomerDeliveryDetails;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        sharedPref = getSharedPreferences("sp", MODE_PRIVATE);
        customerName = Objects.requireNonNull(getIntent().getExtras()).getString("customerName");

        dbRef = FirebaseDatabase.getInstance().getReference();

        ordersModelArrayList = new ArrayList<>();

        lvCustomerDeliveryDetails = findViewById(R.id.lvOrderDelivery);

        retrievedCustomerOrders(customerName);

//        Button btnBack = findViewById(R.id.btnBack);
//        btnBack.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
//            intent.putExtra("fromWhatTab", "Cart");
//            startActivity(intent);
//        });
    }

    private void showCustomerDeliveryDetails() {
        deliveryDetailsAdapter = new DeliveryDetailsAdapter(DeliveryActivity.this, ordersModelArrayList, dbRef, customerName);
        deliveryDetailsAdapter.notifyDataSetChanged();
        lvCustomerDeliveryDetails.setAdapter(deliveryDetailsAdapter);
        int cancelledOrderCount = 0;
        if (!ordersModelArrayList.isEmpty()) {
            for (int i = 0; i < ordersModelArrayList.size(); i++){
                if (ordersModelArrayList.get(i).getStatus().contains("Cancel")){
                    cancelledOrderCount++;
                }
            }
            int isNotified = sharedPref.getInt("notified", 0);
            if (isNotified != 1){
                if (cancelledOrderCount > 0) {
                    AlertDialog dialog = getAlertDialog();
                    dialog.show();
                }
            }
        }
    }

    @NonNull
    private AlertDialog getAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notice...");
        builder.setMessage("Some order(s) has been cancelled by the seller. Please check...\n\nRemarks: Order(s) might not be available at this time.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", (dialog, which) -> {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("notified", 1);
            editor.apply();
            dialog.dismiss();
        });

        return builder.create();
    }

    private void retrievedCustomerOrders(String customerName) {
        dbRef.child("orders").orderByChild("fullName").equalTo(customerName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    key = dataSnapshot.getKey();
                    OrdersModel ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    assert ordersModel != null;
                    ordersModel.setKey(key);
                    ordersModelArrayList.add(ordersModel);
                }
                Collections.reverse(ordersModelArrayList);
                showCustomerDeliveryDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}