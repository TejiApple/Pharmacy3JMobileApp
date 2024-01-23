package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        customerName = Objects.requireNonNull(getIntent().getExtras()).getString("customerName");

        dbRef = FirebaseDatabase.getInstance().getReference();

        ordersModelArrayList = new ArrayList<>();

        lvCustomerDeliveryDetails = findViewById(R.id.lvOrderDelivery);

        retrievedCustomerOrders(customerName);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
            intent.putExtra("fromWhatTab", "Cart");
            startActivity(intent);
        });
    }

    private void showCustomerDeliveryDetails() {
        deliveryDetailsAdapter = new DeliveryDetailsAdapter(DeliveryActivity.this, ordersModelArrayList);
        deliveryDetailsAdapter.notifyDataSetChanged();
        lvCustomerDeliveryDetails.setAdapter(deliveryDetailsAdapter);
    }

    private void retrievedCustomerOrders(String customerName) {
        dbRef.child("orders").orderByChild("fullName").equalTo(customerName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    OrdersModel ordersModel = dataSnapshot.getValue(OrdersModel.class);
                    assert ordersModel != null;
                    ordersModelArrayList.add(ordersModel);
                    showCustomerDeliveryDetails();
                }
                Collections.reverse(ordersModelArrayList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}