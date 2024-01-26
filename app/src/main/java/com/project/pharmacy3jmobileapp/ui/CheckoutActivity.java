package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.project.pharmacy3jmobileapp.model.OrdersModel;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;
import com.project.pharmacy3jmobileapp.ui.adapter.CartProductDetailsListAdapter;
import com.project.pharmacy3jmobileapp.ui.adapter.CheckoutItemBreakdownAdapter;
import com.project.pharmacy3jmobileapp.ui.adapter.CheckoutListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CheckoutActivity extends AppCompatActivity implements OrderDetails {
    String seniorCitizenId, itemName, username, fullName, address;
    String fromBuyNow = null;
    EditText etGcashNumber;
    Dialog dialog;
    Double initialAmount;
    String amountForBuyNow;
    Integer itemQuantity = 0;
    ListView lvProductInTheCart, lvCheckoutItemBreakdown;
    Button btnProceedToOrders, btnCancel;
    TextView tvTotalAmount, tvCustAddress, tvDiscountName, tvDiscountPercent, tvCod, tvPriceName, tvQuantityName;
    CartProductDetailsListAdapter cartProductDetailsListAdapter;

    CheckoutListAdapter checkoutListAdapter;
    CheckoutItemBreakdownAdapter checkoutItemBreakdownAdapter;
    ArrayList<ProductsModel> productsModelArrayList;
    DatabaseReference dbRef;

    ArrayList<RegistrationModel> registrationModelArrayList;
    OrdersModel ordersModels;
    HashMap<Integer, String> itemsSubtotal = new HashMap<>();

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        tvDiscountName = findViewById(R.id.tvDiscountName);
        tvDiscountPercent = findViewById(R.id.tvDiscountPercent);
        tvTotalAmount = findViewById(R.id.tvCartTotalAmount);
        tvPriceName = findViewById(R.id.tvPriceName);
        tvQuantityName = findViewById(R.id.tvQuantityName);

        dbRef = FirebaseDatabase.getInstance().getReference();

        //Call functions
        retrieveData();

        sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        String selectedProducts = sp.getString("selectedItems", "");
        String productsOnBuyNow = sp.getString("buyNow", "");
        fromBuyNow = getIntent().getExtras().getString("fromBuyNow");
        if (fromBuyNow.isEmpty()){
            if (!selectedProducts.isEmpty()){
                try {
                    JSONArray productsOnCartArray = new JSONArray(selectedProducts);
                    JSONObject productsOnCartObj;
                    productsModelArrayList = new ArrayList<>();

                    int position = 0;
                    String totalAmount = "";
                    for (int i = 0; i < productsOnCartArray.length(); i++){
                        productsOnCartObj = productsOnCartArray.getJSONObject(i);
                        Gson gson = new Gson();
                        ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnCartObj), ProductsModel.class);
                        productsModelArrayList.add(productsInTheCart);
                        int quantityFromObj = productsOnCartObj.getInt("quantity");
                        if (itemQuantity == 0){
                            itemQuantity = quantityFromObj;
                        } else {
                            itemQuantity = quantityFromObj + itemQuantity;
                        }
                        position = productsOnCartObj.getInt("position");
                        initialAmount = Double.valueOf(productsOnCartObj.getString("totalAmount").replace(",",""));
                        totalAmount = productsOnCartObj.getString("totalAmount").replace(",","");
                        itemsSubtotal.put(position, totalAmount);

                    }






                } catch (JSONException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (!productsOnBuyNow.isEmpty()) {
                try {
                    productsModelArrayList = new ArrayList<>();
                    JSONArray productsOnBuyNowArray = new JSONArray(productsOnBuyNow);
                    JSONObject productsOnBuyNowObj;
                    for (int i = 0; i < productsOnBuyNowArray.length(); i++){
                        productsOnBuyNowObj = productsOnBuyNowArray.getJSONObject(i);
                        if (productsOnBuyNowObj.has("totalAmount")){
                            productsOnBuyNowObj.put("totalAmount", productsOnBuyNowObj.getString("totalAmount"));
                            initialAmount = Double.valueOf(productsOnBuyNowObj.getString("totalAmount"));
                        } else {
                            productsOnBuyNowObj.put("totalAmount", productsOnBuyNowObj.getString("price"));
                            initialAmount = Double.valueOf(productsOnBuyNowObj.getString("price"));
                        }
                        Gson gson = new Gson();
                        ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnBuyNowObj), ProductsModel.class);
                        productsModelArrayList.add(productsInTheCart);
                    }

                } catch (JSONException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
//        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productDetails")).toString();
//        Gson gson = new Gson();
//        ProductsModel productsInTheCart = gson.fromJson(productDetails, ProductsModel.class);

        registrationModelArrayList = new ArrayList<>();

        //Setting up views
        lvProductInTheCart = findViewById(R.id.lvProductsToCheckout);
        lvCheckoutItemBreakdown = findViewById(R.id.lvCheckoutItem);
        btnProceedToOrders = findViewById(R.id.btnProceedToOrders);
        btnCancel = findViewById(R.id.btnCancelCheckout);
        tvCustAddress = findViewById(R.id.tvCustAddress);
//        rdBtnGcash = findViewById(R.id.rdBtnGcash);
        tvCod = findViewById(R.id.tvCashOnDelivery);


//        if (initialAmount >= 200 && initialAmount < 1000){
//            tvDiscountName.setVisibility(View.VISIBLE);
//            tvDiscountPercent.setVisibility(View.VISIBLE);
//            tvDiscountPercent.setText("5%");
//        } else if (initialAmount > 1000) {
//            tvDiscountName.setVisibility(View.VISIBLE);
//            tvDiscountPercent.setVisibility(View.VISIBLE);
//            tvDiscountPercent.setText("10%");
//        } else {
//            tvDiscountName.setVisibility(View.GONE);
//            tvDiscountPercent.setVisibility(View.GONE);
//            tvDiscountPercent.setText("0%");
//        }

//        initialAmount = Double.parseDouble(df.format(productsModelArrayList.get(0).getPrice()));
//        String formattedPrice = "Php " + df.format(productsModelArrayList.get(0).getPrice());
//        tvTotalAmount.setText(formattedPrice);

        showProductsInTheCart();
        showProductBreakdown();
        proceedToCheckout();
        cancelCheckout();
//        showGcashDialog();
    }

    private void showProductsInTheCart() {
        checkoutListAdapter = new CheckoutListAdapter(CheckoutActivity.this, productsModelArrayList, this, sp, "CheckoutActivity", fromBuyNow);
        lvProductInTheCart.setAdapter(checkoutListAdapter);
    }

    private void showProductBreakdown() {
        checkoutItemBreakdownAdapter = new CheckoutItemBreakdownAdapter(this, productsModelArrayList, this, sp, "CheckoutActivity", fromBuyNow);
        tvPriceName.setVisibility(View.VISIBLE);
        tvQuantityName.setVisibility(View.VISIBLE);
        lvCheckoutItemBreakdown.setVisibility(View.VISIBLE);
        lvCheckoutItemBreakdown.setAdapter(checkoutItemBreakdownAdapter);
    }

    private void proceedToCheckout() {
        btnProceedToOrders.setOnClickListener(v -> {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                    .setTitle("Confirming Orders...")
                    .setMessage("Are you sure you want to place this order?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            fullName = registrationModelArrayList.get(0).getCompleteName();
                            String contactNumber = registrationModelArrayList.get(0).getMobilePhone();
                            double discount = 0.0;
                            double totalPay = 0;

                            if (!fromBuyNow.isEmpty()){
                                String productsOnBuyNow = sp.getString("buyNow", "");
                                if (!productsOnBuyNow.isEmpty()) {
                                    try {
                                        productsModelArrayList = new ArrayList<>();
                                        JSONArray productsOnBuyNowArray = new JSONArray(productsOnBuyNow);
                                        JSONObject productsOnBuyNowObj;
                                        for (int i = 0; i < productsOnBuyNowArray.length(); i++){
                                            productsOnBuyNowObj = productsOnBuyNowArray.getJSONObject(i);
                                            if (productsOnBuyNowObj.has("totalAmount")){
                                                productsOnBuyNowObj.put("totalAmount", productsOnBuyNowObj.getString("totalAmount"));
                                            } else {
                                                productsOnBuyNowObj.put("totalAmount", productsOnBuyNowObj.getString("price"));
                                            }
                                            amountForBuyNow = productsOnBuyNowObj.getString("totalAmount");
                                            itemQuantity = productsOnBuyNowObj.getInt("quantity");
                                            Gson gson = new Gson();
                                            ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnBuyNowObj), ProductsModel.class);
                                            productsModelArrayList.add(productsInTheCart);
                                        }
                                        if (!amountForBuyNow.isEmpty()){
                                            initialAmount = Double.parseDouble(amountForBuyNow);
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }


                            int unitPrice = 0;

                            AtomicReference<String> paymentMode = new AtomicReference<>("");

//                            if (rdBtnGcash.isChecked()){
//                                paymentMode.set("Gcash");
//                            } else
                            paymentMode.set("Cash on delivery");

                            int randomProductId = new Random().nextInt(10000);
                            int randomItemNumber = new Random().nextInt(10000);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                            String date = dateFormat.format(new Date());
                            for (int i = 0; i < productsModelArrayList.size(); i++){
                                itemName = productsModelArrayList.get(i).getBrandName();
                                unitPrice = productsModelArrayList.get(i).getPrice();
                                itemQuantity = productsModelArrayList.get(i).getQuantity();
//                                initialAmount = Double.valueOf(productsModelArrayList.get(i).getTotalAmount());
//                                if (registrationModelArrayList.get(0).getSeniorCitizenId().length() > 0){
//                                    discount = 0.8;
//                                    totalPay = initialAmount * discount;
//                                } else {
//                                }
                                totalPay = initialAmount;

                                ordersModels = new OrdersModel(initialAmount.toString(), contactNumber, "", date, discount, fullName,
                                        itemName, randomItemNumber, "Door-to-door", paymentMode.get(), "Sample Prescription", randomProductId, itemQuantity,
                                        address, "Pending", totalPay, unitPrice, seniorCitizenId);

                                dbRef.child("orders").push().setValue(ordersModels);
                            }

                            String productsOnCart = sp.getString("productDetails", "");

                            JsonArray productsOnCartArray;
                            JSONArray jsonProductsOnCart = new JSONArray();

                            if (!fromBuyNow.isEmpty()){
                                sp.edit().remove("buyNow").apply();
                            } else {
                                if (!productsOnCart.isEmpty()){
                                    try {
                                        productsOnCartArray = new Gson().fromJson(productsOnCart, JsonArray.class);
                                        if (hasValue(productsOnCartArray, itemName)){
                                            jsonProductsOnCart = new JSONArray(productsOnCart);
                                            ArrayList<Integer> itemPositionList = (ArrayList<Integer>) getIntent().getExtras().get("itemPositionList");
                                            for (int i = 0; i < itemPositionList.size(); i++){
                                                int index = itemPositionList.get(i);
                                                jsonProductsOnCart.remove(index);
                                            }
                                        }

                                    } catch (Exception e) {
                                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                sp.edit().remove("selectedItems").apply();
                                sp.edit().putString("productDetails", jsonProductsOnCart.toString()).apply();
//                                   sp.edit().remove("productDetails").apply();

                            }

                            Intent intent = new Intent(getApplicationContext(), SuggestionsActivity.class);
                            intent.putExtra("customerName", fullName);
                            startActivity(intent);
                        } catch (Exception e) {
//                              Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    })
                    .setNegativeButton("No", null);
            alertDialog.show();

        });
    }

    private boolean hasValue(JsonArray json, String value){
        if (json != null){
            for (int i = 0; i < json.size(); i++){
                if (json.get(i).getAsJsonObject().get("brandName").getAsString().equals(value)){
                    return true;
                }
            }
        }

        return false;
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

                                try {
                                    String city = registrationModelArrayList.get(0).getCityMunicipality();
                                    String barangay = registrationModelArrayList.get(0).getBarangay();
                                    String houseNo = registrationModelArrayList.get(0).getHouseNo();

                                    if (registrationModelArrayList.get(0).getSeniorCitizenId() != null){
                                        seniorCitizenId = registrationModelArrayList.get(0).getSeniorCitizenId();
                                    } else {
                                        seniorCitizenId = "";
                                    }

                                    if (city == null && !houseNo.isEmpty()){
                                        address = houseNo + ", " + barangay;
                                    } else if (houseNo == null || houseNo.isEmpty() && !barangay.isEmpty()){
                                        address = barangay;
                                    }

                                    double finalTotalAmt = 0.00;
                                    for (String subtotal : itemsSubtotal.values()) {
                                        finalTotalAmt += Double.parseDouble(subtotal.replace(",", ""));
                                    }

                                    DecimalFormat df = new DecimalFormat("#,##0.00");

                                    if (!seniorCitizenId.isEmpty()){
                                        if (finalTotalAmt >= 200 && finalTotalAmt < 1000){
                                            tvDiscountName.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setText("Senior Citizen 20% + 5%");
                                            initialAmount = finalTotalAmt * 0.75;
                                            tvTotalAmount.setText("Php " + df.format(initialAmount));
                                        } else if (finalTotalAmt > 1000) {
                                            tvDiscountName.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setText("Senior Citizen 20% + 10%");
                                            initialAmount = finalTotalAmt * 0.70;
                                            tvTotalAmount.setText("Php " + df.format(initialAmount));
                                        } else {
                                            tvDiscountName.setVisibility(View.GONE);
                                            tvDiscountPercent.setVisibility(View.GONE);
                                            tvDiscountPercent.setText("0%");
                                            initialAmount = finalTotalAmt * 0.80;
                                            tvTotalAmount.setText("Php " + df.format(initialAmount));
                                        }
                                    } else {
                                        if (finalTotalAmt >= 200 && finalTotalAmt < 1000){
                                            tvDiscountName.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setText("5%");
                                            initialAmount = finalTotalAmt * 0.95;
                                            tvTotalAmount.setText("Php " + df.format(initialAmount));
                                        } else if (finalTotalAmt > 1000) {
                                            tvDiscountName.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setVisibility(View.VISIBLE);
                                            tvDiscountPercent.setText("10%");
                                            initialAmount = finalTotalAmt * 0.9;
                                            tvTotalAmount.setText("Php " + df.format(initialAmount));
                                        } else {
                                            tvDiscountName.setVisibility(View.GONE);
                                            tvDiscountPercent.setVisibility(View.GONE);
                                            tvDiscountPercent.setText("0%");
                                            initialAmount = finalTotalAmt;
                                            tvTotalAmount.setText("Php " + df.format(initialAmount));
                                        }
                                    }
                                } catch (Exception e){
                                    Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
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
            if (fromBuyNow.isEmpty()){
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            } else {
                sp.edit().remove("buyNow").apply();
                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                intent.putExtra("fromWhatTab", "Checkout");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (fromBuyNow.isEmpty()){
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        } else {
            sp.edit().remove("buyNow").apply();
            String productDetails = getIntent().getExtras().getString("productModel");
            String category = getIntent().getExtras().getString("category");
            Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
            intent.putExtra("productModel", productDetails);
            intent.putExtra("category", category);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void cartTotalAmount(String totalAmount, int quantity, int selectedItem, int position, String fromWhatButton) {
        try {
            double finalTotalAmt = 0.00;

//            String totalAmountFromCart = getIntent().getExtras().getString("totalAmount");
            DecimalFormat df = new DecimalFormat("#,##0.00");
            double amountToBeDiscounted = 0.00;
            if (fromWhatButton.equals("fromBuyNow")){
                if (selectedItem == 1){
                    itemsSubtotal.put(position, totalAmount);
                    for (String subtotal : itemsSubtotal.values()) {
                        finalTotalAmt += Double.parseDouble(subtotal.replace(",", ""));
                    }
                } else {
                    itemsSubtotal.remove(position);
                }

                if (finalTotalAmt >= 200 && finalTotalAmt < 1000){
                    finalTotalAmt = finalTotalAmt * 0.95;
                } else if (finalTotalAmt > 1000) {
                    finalTotalAmt = finalTotalAmt * 0.9;
                }

                tvTotalAmount.setText("Php " + df.format(finalTotalAmt) + "0");
                amountToBeDiscounted = finalTotalAmt;
            }

            if (amountToBeDiscounted >= 200 && amountToBeDiscounted < 1000){
                tvDiscountName.setVisibility(View.VISIBLE);
                tvDiscountPercent.setVisibility(View.VISIBLE);
                tvDiscountPercent.setText("5%");
            } else if (amountToBeDiscounted > 1000) {
                tvDiscountName.setVisibility(View.VISIBLE);
                tvDiscountPercent.setVisibility(View.VISIBLE);
                tvDiscountPercent.setText("10%");
            } else {
                tvDiscountName.setVisibility(View.GONE);
                tvDiscountPercent.setVisibility(View.GONE);
                tvDiscountPercent.setText("0%");
            }
        } catch (Exception e){
            if(!((Activity) getApplicationContext()).isFinishing()) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }

    }
}