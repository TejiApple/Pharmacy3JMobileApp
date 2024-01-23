package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity implements OrderDetails {
    String productDetails, itemName, username, fullName, address;

    Double initialAmount;
    Integer itemQuantity = 1;
    Integer currentItemSelected = 0;

    ListView lvProductInTheCart;
    Button btnProceedToCheckout, btnCancel;
    TextView tvTotalAmount, tvCustAddress, tvCartNoOrders;
    RadioButton rdBtnGcash, rdBtnCod;
    CartProductDetailsListAdapter cartProductDetailsListAdapter;
    ArrayList<ProductsModel> productsModelArrayList;
    DatabaseReference dbRef;

    ArrayList<RegistrationModel> registrationModelArrayList;
    OrdersModel ordersModels;

    HashMap<Integer, String> itemsSubtotal = new HashMap<>();
    ArrayList<Integer> itemPositionList = new ArrayList<>();
    JSONArray productsOnCartArray;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //Setting up views
        lvProductInTheCart = findViewById(R.id.lvProductsToCheckout);
        btnProceedToCheckout = findViewById(R.id.btnProceedToCheckout);
        btnCancel = findViewById(R.id.btnCancelCheckout);
        tvTotalAmount = findViewById(R.id.tvCartTotalAmount);
        tvCartNoOrders = findViewById(R.id.tvCartNoOrders);

        sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        sp.edit().remove("selectedItems").apply();

        String productsOnCart = sp.getString("productDetails", "");
        if (!productsOnCart.isEmpty()){
            try {
                productsOnCartArray = new JSONArray(productsOnCart);
                JSONObject productsOnCartObj;
                productsModelArrayList = new ArrayList<>();

                for (int i = 0; i < productsOnCartArray.length(); i++){
                    productsOnCartObj = productsOnCartArray.getJSONObject(i);
                    Gson gson = new Gson();
                    ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnCartObj), ProductsModel.class);
                    productsModelArrayList.add(productsInTheCart);
                }

                if (productsOnCartArray.length() == 0){
                    lvProductInTheCart.setVisibility(View.GONE);
                    tvCartNoOrders.setVisibility(View.VISIBLE);
                    btnProceedToCheckout.setEnabled(false);
                } else {
                    showProductsInTheCart();
                    btnProceedToCheckout.setEnabled(true);
                }
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            lvProductInTheCart.setVisibility(View.GONE);
            tvCartNoOrders.setVisibility(View.VISIBLE);
            btnProceedToCheckout.setEnabled(false);
        }
//
//        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productDetails")).toString();
//        Gson gson = new Gson();
//        ProductsModel productsInTheCart = gson.fromJson(productDetails, ProductsModel.class);
//        productsModelArrayList = new ArrayList<>();
//        productsModelArrayList.add(productsInTheCart);

        registrationModelArrayList = new ArrayList<>();


//        tvCustAddress = findViewById(R.id.tvCustAddress);
//        rdBtnGcash = findViewById(R.id.rdBtnGcash);
//        rdBtnCod = findViewById(R.id.rdBtnCashOnDelivery);

        DecimalFormat df = new DecimalFormat("#,###.00");
//        initialAmount = Double.parseDouble(df.format(productsModelArrayList.get(0).getPrice()));
//        String formattedPrice = "Php " + df.format(productsModelArrayList.get(0).getPrice());
//        tvTotalAmount.setText("Php 0.00");
        dbRef = FirebaseDatabase.getInstance().getReference();

        //Call functions
//        retrieveData();
        proceedToCheckout();
        cancelCheckout();
    }

    private void showProductsInTheCart() {
        cartProductDetailsListAdapter = new CartProductDetailsListAdapter(CartActivity.this, productsModelArrayList, this, sp, "CartActivity", "");
        lvProductInTheCart.setAdapter(cartProductDetailsListAdapter);
    }

    private void proceedToCheckout() {
        btnProceedToCheckout.setOnClickListener(v -> {
            if (currentItemSelected > 0){
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                intent.putExtra("itemPositionList", itemPositionList);
                intent.putExtra("fromBuyNow", "");

                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select an item to checkout", Toast.LENGTH_SHORT).show();
            }


//            try {
//                fullName = registrationModelArrayList.get(0).getCompleteName();
//                String contactNumber = registrationModelArrayList.get(0).getMobilePhone();
//                double discount = 0.0;
//                double totalPay = 0;
//
//                if (registrationModelArrayList.get(0).getSeniorCitizenId().length() > 0){
//                    discount = 0.8;
//                    totalPay = initialAmount * discount;
//                }
//
//                int unitPrice = 0;
//                for (int i = 0; i < productsModelArrayList.size(); i++){
//                    itemName = productsModelArrayList.get(i).getBrandName();
//                    unitPrice = productsModelArrayList.get(i).getPrice();
//                }
//
//                String paymentMode = "";
//                if (rdBtnGcash.isChecked()){
//                    paymentMode = "Gcash";
//                } else if (rdBtnCod.isChecked()){
//                    paymentMode = "Cash on delivery";
//                } else {
//                    Toast.makeText(CartActivity.this, "Payment mode is Required!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                int randomProductId = new Random().nextInt(10000);
//                int randomItemNumber = new Random().nextInt(10000);
//
//                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
//                String date = dateFormat.format(new Date());
//                ordersModels = new OrdersModel(initialAmount.toString(), contactNumber, "", date, discount, fullName,
//                        itemName, randomItemNumber, "Door-to-door", paymentMode, "Sample Prescription", randomProductId, itemQuantity,
//                        address, "Pending", totalPay, unitPrice);
//
//                dbRef.child("orders").push().setValue(ordersModels);
//                Toast.makeText(CartActivity.this, "Checkout successful!", Toast.LENGTH_SHORT).show();
//
//                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
//                intent.putExtra("customerName", fullName);
//                startActivity(intent);
//            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }

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
//                                tvCustAddress.setText(address);
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
            Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
            intent.putExtra("fromWhatTab", "Cart");
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
        intent.putExtra("fromWhatTab", "Cart");
        startActivity(intent);
    }

    @Override
    public void cartTotalAmount(String totalAmount, int quantity, int selectedItem, int position, String fromWhatButton) {
        try {
            double finalTotalAmt = 0.00;
            if (selectedItem == 1){
                itemPositionList.add(position);
                itemsSubtotal.put(position, totalAmount);
                for (String subtotal : itemsSubtotal.values()){
                    finalTotalAmt += Double.parseDouble(subtotal.replace(",", ""));
                }
            } else {
                itemPositionList.remove(Integer.valueOf(position));
                itemsSubtotal.remove(position);
                double d = 0.00;
                for (String subtotal : itemsSubtotal.values()){
                    d += Double.parseDouble(subtotal);
                    finalTotalAmt = d;
                }

            }
            DecimalFormat df = new DecimalFormat("#,##0.00");

            tvTotalAmount.setText("Php " + df.format(finalTotalAmt));
            initialAmount = Double.parseDouble(String.valueOf(finalTotalAmt));
            itemQuantity = quantity;
            if (selectedItem == 1){
                currentItemSelected++;
            } else {
                if (currentItemSelected > 0){
                    currentItemSelected--;
                } else {
                    currentItemSelected = 0;
                }
            }
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}