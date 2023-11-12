package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrdersModel;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.project.pharmacy3jmobileapp.ui.adapter.CartProductDetailsListAdapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class CartActivity extends AppCompatActivity implements OrderDetails {
    String productDetails, itemName, username, fullName, address;

    Double initialAmount;
    Integer itemQuantity = 1;
    ListView lvProductInTheCart;
    Button btnProceedToDelivery, btnCancel;
    TextView tvTotalAmount, tvCustAddress;
    RadioButton rdBtnGcash, rdBtnCod;
    CartProductDetailsListAdapter cartProductDetailsListAdapter;
    ArrayList<ProductsModel> productsModelArrayList;
    DatabaseReference dbRef;

    ArrayList<RegistrationModel> registrationModelArrayList;
    OrdersModel ordersModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");

        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productDetails")).toString();
        Gson gson = new Gson();
        ProductsModel productsInTheCart = gson.fromJson(productDetails, ProductsModel.class);
        productsModelArrayList = new ArrayList<>();
        productsModelArrayList.add(productsInTheCart);

        registrationModelArrayList = new ArrayList<>();

        //Setting up views
        lvProductInTheCart = findViewById(R.id.lvProductsToCheckout);
        btnProceedToDelivery = findViewById(R.id.btnProceedToDelivery);
        btnCancel = findViewById(R.id.btnCancelCheckout);
        tvTotalAmount = findViewById(R.id.tvCartTotalAmount);
        tvCustAddress = findViewById(R.id.tvCustAddress);
        rdBtnGcash = findViewById(R.id.rdBtnGcash);
        rdBtnCod = findViewById(R.id.rdBtnCashOnDelivery);

        DecimalFormat df = new DecimalFormat("#,###.00");
        initialAmount = Double.parseDouble(df.format(productsModelArrayList.get(0).getPrice()));
        String formattedPrice = "Php " + df.format(productsModelArrayList.get(0).getPrice());
        tvTotalAmount.setText(formattedPrice);
        dbRef = FirebaseDatabase.getInstance().getReference();

        //Call functions
        retrieveData();
        showProductsInTheCart();
        proceedToDelivery();
        cancelCheckout();
    }

    private void showProductsInTheCart() {
        cartProductDetailsListAdapter = new CartProductDetailsListAdapter(CartActivity.this, productsModelArrayList, this);
        lvProductInTheCart.setAdapter(cartProductDetailsListAdapter);
    }

    private void proceedToDelivery() {
        btnProceedToDelivery.setOnClickListener(v -> {
            try {
                fullName = registrationModelArrayList.get(0).getCompleteName();
                String contactNumber = registrationModelArrayList.get(0).getMobilePhone();
                double discount = 0.0;
                double totalPay = 0;

                if (registrationModelArrayList.get(0).getSeniorCitizenId().length() > 0){
                    discount = 0.8;
                    totalPay = initialAmount * discount;
                }

                int unitPrice = 0;
                for (int i = 0; i < productsModelArrayList.size(); i++){
                    itemName = productsModelArrayList.get(i).getBrandName();
                    unitPrice = productsModelArrayList.get(i).getPrice();
                }

                String paymentMode = "";
                if (rdBtnGcash.isChecked()){
                    paymentMode = "Gcash";
                } else if (rdBtnCod.isChecked()){
                    paymentMode = "Cash on delivery";
                } else {
                    Toast.makeText(CartActivity.this, "Payment mode is Required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int randomProductId = new Random().nextInt(10000);
                int randomItemNumber = new Random().nextInt(10000);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String date = dateFormat.format(new Date());
                ordersModels = new OrdersModel(initialAmount.toString(), contactNumber, "", date, discount, fullName,
                        itemName, randomItemNumber, "Door-to-door", paymentMode, "Sample Prescription", randomProductId, itemQuantity,
                        address, "Pending", totalPay, unitPrice);

                dbRef.child("orders").push().setValue(ordersModels);
                Toast.makeText(CartActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                intent.putExtra("customerName", fullName);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void retrieveData(){
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

                                String barangay = registrationModelArrayList.get(0).getBarangay();
                                String houseNo = registrationModelArrayList.get(0).getHouseNo();
                                address = houseNo + barangay;
                                tvCustAddress.setText(address);
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

    private void cancelCheckout(){
        btnCancel.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
        });
    }
    @Override
    public void cartTotalAmount(String totalAmount, int quantity) {
        tvTotalAmount.setText("Php " + totalAmount);
        initialAmount = Double.parseDouble(totalAmount);
        itemQuantity = quantity;
    }
}