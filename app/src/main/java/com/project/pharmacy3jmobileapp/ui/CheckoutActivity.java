package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.gson.JsonArray;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.project.pharmacy3jmobileapp.model.OrdersModel;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;
import com.project.pharmacy3jmobileapp.ui.adapter.CartProductDetailsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class CheckoutActivity extends AppCompatActivity implements OrderDetails {
    String productDetails, itemName, username, fullName, address, fromBuyNow;
    EditText etGcashNumber;
    Dialog dialog;
    Double initialAmount;
    String amountForBuyNow;
    Integer itemQuantity = 0;
    ListView lvProductInTheCart;
    Button btnProceedToOrders, btnCancel;
    TextView tvTotalAmount, tvCustAddress;
    RadioButton rdBtnGcash, rdBtnCod;
    CartProductDetailsListAdapter cartProductDetailsListAdapter;
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

        sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        String selectedProducts = sp.getString("selectedItems", "");
        String productsOnBuyNow = sp.getString("buyNow", "");
        fromBuyNow = Objects.requireNonNull(getIntent().getExtras()).getString("fromBuyNow");
        assert fromBuyNow != null;
        if (fromBuyNow.isEmpty()){
            if (!selectedProducts.isEmpty()){
                try {
                    JSONArray productsOnCartArray = new JSONArray(selectedProducts);
                    JSONObject productsOnCartObj;
                    productsModelArrayList = new ArrayList<>();


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
                        initialAmount = productsOnCartObj.getDouble("totalAmount");
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
                        } else {
                            productsOnBuyNowObj.put("totalAmount", productsOnBuyNowObj.getString("price"));
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
        btnProceedToOrders = findViewById(R.id.btnProceedToOrders);
        btnCancel = findViewById(R.id.btnCancelCheckout);
        tvTotalAmount = findViewById(R.id.tvCartTotalAmount);
        tvCustAddress = findViewById(R.id.tvCustAddress);
        rdBtnGcash = findViewById(R.id.rdBtnGcash);
        rdBtnCod = findViewById(R.id.rdBtnCashOnDelivery);

//        initialAmount = Double.parseDouble(df.format(productsModelArrayList.get(0).getPrice()));
//        String formattedPrice = "Php " + df.format(productsModelArrayList.get(0).getPrice());
//        tvTotalAmount.setText(formattedPrice);
        dbRef = FirebaseDatabase.getInstance().getReference();

        //Call functions
        retrieveData();
        showProductsInTheCart();
        proceedToCheckout();
        cancelCheckout();
        showGcashDialog();
    }

    private void showGcashDialog(){
        dialog = new Dialog(this);

        rdBtnGcash.setOnClickListener(v1 -> {
            try {
                dialog.setContentView(R.layout.gcash_dialog);
                etGcashNumber = dialog.findViewById(R.id.etGcashNumber);
                Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

                String gcashNumber = sp.getString("gcashNumber", "");

                if (!gcashNumber.isEmpty()){
                    etGcashNumber.setText(gcashNumber);
                } else {
                    etGcashNumber.setText("");
                }

                Button confirm = dialog.findViewById(R.id.btnGcashConfirm);
                Button cancel = dialog.findViewById(R.id.btnGcashCancel);
                SharedPreferences.Editor editor = sp.edit();
                confirm.setOnClickListener(v2 -> {
                    try {
                        String gcash = etGcashNumber.getText().toString();
                        if (etGcashNumber.getText().length() > 0){
                            editor.putString("gcashNumber", gcash);
                            editor.apply();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(this, "Account number is required!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e){

                    }

                });
                cancel.setOnClickListener(v2 -> dialog.dismiss());
                dialog.show();
            } catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

    }
    private void showProductsInTheCart() {
        cartProductDetailsListAdapter = new CartProductDetailsListAdapter(CheckoutActivity.this, productsModelArrayList, this, sp, "CheckoutActivity");
        lvProductInTheCart.setAdapter(cartProductDetailsListAdapter);
    }

    private void proceedToCheckout() {
        btnProceedToOrders.setOnClickListener(v -> {
//            try {
//                if (productsModelArrayList.size() > 0){
//                    Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
//                    intent.putExtra("productDetails", productsModelArrayList);
//                    startActivity(intent);
//                }
//            } catch (Exception e){
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }

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
                                Gson gson = new Gson();
                                ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnBuyNowObj), ProductsModel.class);
                                productsModelArrayList.add(productsInTheCart);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                if (!amountForBuyNow.isEmpty()){
                    initialAmount = Double.parseDouble(amountForBuyNow);
                }

                if (registrationModelArrayList.get(0).getSeniorCitizenId().length() > 0){
                    discount = 0.8;
                    totalPay = initialAmount * discount;
                }

                int unitPrice = 0;

                AtomicReference<String> paymentMode = new AtomicReference<>("");

                if (rdBtnGcash.isChecked()){
                    paymentMode.set("Gcash");
                } else if (rdBtnCod.isChecked()){
                    paymentMode.set("Cash on delivery");
                } else {
                    Toast.makeText(CheckoutActivity.this, "Payment mode is Required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int randomProductId = new Random().nextInt(10000);
                int randomItemNumber = new Random().nextInt(10000);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                String date = dateFormat.format(new Date());
                for (int i = 0; i < productsModelArrayList.size(); i++){
                    itemName = productsModelArrayList.get(i).getBrandName();
                    unitPrice = productsModelArrayList.get(i).getPrice();
                    ordersModels = new OrdersModel(initialAmount.toString(), contactNumber, "", date, discount, fullName,
                            itemName, randomItemNumber, "Door-to-door", paymentMode.get(), "Sample Prescription", randomProductId, itemQuantity,
                            address, "Pending", totalPay, unitPrice);

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
//                            for (int i = 0; i < jsonProductsOnCart.length(); i++){
//                                JSONObject productsOnCartObj = jsonProductsOnCart.getJSONObject(i);
//                                if (productsOnCartObj.getString("brandName").equals(itemName)){
//                                    int index = productsOnCartObj.
//                                }
//                            }
                            }

//                        productsModelArrayList = new ArrayList<>();
//
//                        for (int i = 0; i < productsOnCartArray.length(); i++){
//                            productsOnCartObj = productsOnCartArray.getJSONObject(i);
//                            Gson gson = new Gson();
//                            ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnCartObj), ProductsModel.class);
//                            productsModelArrayList.add(productsInTheCart);
//                        }

                        } catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    sp.edit().remove("selectedItems").apply();
                    sp.edit().putString("productDetails", jsonProductsOnCart.toString()).apply();
//                sp.edit().remove("productDetails").apply();

                }

                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                intent.putExtra("customerName", fullName);
                startActivity(intent);
            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

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

                                String city = registrationModelArrayList.get(0).getCityMunicipality();
                                String barangay = registrationModelArrayList.get(0).getBarangay();
                                String houseNo = registrationModelArrayList.get(0).getHouseNo();
                                address = houseNo + ", " + barangay + ", " + city;
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
            startActivity(new Intent(getApplicationContext(), CartActivity.class));
        });
    }
    @Override
    public void cartTotalAmount(String totalAmount, int quantity, int selectedItem, int position) {
        try {
            double finalTotalAmt = 0.00;
//            if (selectedItem == 1){
////                itemsSubtotal.put(position, totalAmount);
////                for (String subtotal : itemsSubtotal.values()){
//                    finalTotalAmt += Double.parseDouble(totalAmount);
////                }
//                System.out.println("TOTAL AMOUNT : " + finalTotalAmt);
//            } else {
//                itemsSubtotal.remove(position);
//            }
            String totalAmountFromCart = getIntent().getExtras().getString("totalAmount");
//            DecimalFormat df = new DecimalFormat("#,##0.00");

            tvTotalAmount.setText(totalAmountFromCart);
//            initialAmount = Double.parseDouble(String.valueOf(finalTotalAmt));
//            itemQuantity = quantity;
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}