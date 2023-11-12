package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    }

    private void showCustomerDeliveryDetails() {
        deliveryDetailsAdapter = new DeliveryDetailsAdapter(DeliveryActivity.this, ordersModelArrayList);
        lvCustomerDeliveryDetails.setAdapter(deliveryDetailsAdapter);
    }

    private void retrievedCustomerOrders(String customerName) {
        dbRef.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                        String completeName = Objects.requireNonNull(dataSnapshot.child("completeName").getValue()).toString();
                    String key = dataSnapshot.getKey();
                    dbRef.child("orders").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                                OrdersModel ordersModel = dataSnapshot1.getValue(OrdersModel.class);
                                assert ordersModel != null;
                                if (ordersModel.getFullName().equals(customerName)){
                                    ordersModelArrayList.add(ordersModel);
                                    showCustomerDeliveryDetails();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(DeliveryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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