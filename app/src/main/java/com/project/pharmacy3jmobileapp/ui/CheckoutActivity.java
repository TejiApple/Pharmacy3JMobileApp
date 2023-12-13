package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.math.MathUtils;

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
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.project.pharmacy3jmobileapp.model.OrdersModel;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;
import com.project.pharmacy3jmobileapp.ui.adapter.CartProductDetailsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class CheckoutActivity extends AppCompatActivity implements OrderDetails {
    String productDetails, itemName, username, fullName, address;

    Double initialAmount;
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
        String productsOnCart = sp.getString("selectedItems", "");
        String productsOnBuyNow = sp.getString("buyNow", "");
        if (!productsOnCart.isEmpty()){
            try {
                JSONArray productsOnCartArray = new JSONArray(productsOnCart);
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
        } else if (!productsOnBuyNow.isEmpty()) {
            try {
                JSONArray productsOnBuyNowArray = new JSONArray(productsOnBuyNow);
                JSONObject productsOnBuyNowObj;
                for (int i = 0; i < productsOnBuyNowArray.length(); i++){
                    productsOnBuyNowObj = productsOnBuyNowArray.getJSONObject(i);
                    Gson gson = new Gson();
                    ProductsModel productsInTheCart = gson.fromJson(String.valueOf(productsOnBuyNowObj), ProductsModel.class);
                    productsModelArrayList.add(productsInTheCart);
                }
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
//        rdBtnGcash = findViewById(R.id.rdBtnGcash);
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

                if (registrationModelArrayList.get(0).getSeniorCitizenId().length() > 0){
                    discount = 0.8;
                    totalPay = initialAmount * discount;
                }

                int unitPrice = 0;

                String paymentMode = "";
                /*if (rdBtnGcash.isChecked()){
                    paymentMode = "Gcash";
                } else */
                if (rdBtnCod.isChecked()){
                    paymentMode = "Cash on delivery";
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
                            itemName, randomItemNumber, "Door-to-door", paymentMode, "Sample Prescription", randomProductId, itemQuantity,
                            address, "Pending", totalPay, unitPrice);

                    dbRef.child("orders").push().setValue(ordersModels);
                }

                sp.edit().remove("selectedItems").apply();
                sp.edit().remove("productDetails").apply();


                Intent intent = new Intent(getApplicationContext(), DeliveryActivity.class);
                intent.putExtra("customerName", fullName);
                startActivity(intent);
            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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