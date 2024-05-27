package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class ProductDetailsActivity extends AppCompatActivity {
    String brandName, description, price, productDetails, genericName, category, imageUrl, quantity, productType, productClassification;
    TextView tvProductName, tvDescription, tvPrice, tvItemBrandName, tvItemPrice, tvItemGenericName, tvItemDesc, tvItemCategory, tvItemQuantity;
    ImageView ivProduct;
    Button btnAddToCart, btnBuyNow, btnVariation1, btnVariation2;

    DatabaseReference dbRef;

    ArrayList<ProductsModel> productsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        btnVariation1 = findViewById(R.id.btnVariation1);
        btnVariation2 = findViewById(R.id.btnVariation2);

        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productModel")).toString();
        category = getIntent().getExtras().getString("category");
        productType = getIntent().getExtras().getString("productType");
        productClassification = getIntent().getExtras().getString("classification");

        dbRef = FirebaseDatabase.getInstance().getReference();

        try {
            JSONObject productDetailsObj = new JSONObject(productDetails);
            brandName = productDetailsObj.getString("brandName");
            description = productDetailsObj.getString("description");
            price = productDetailsObj.getString("price");
            if (productDetailsObj.has("genericName")){
                genericName = productDetailsObj.getString("genericName");
            } else {
                genericName = "";
            }
            imageUrl = productDetailsObj.getString("imageUrl");
            quantity = productDetailsObj.getString("quantity");

        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        showProductDetails();
        addToCart();
        buyNow();

        if (productClassification.equals("Neozep") || productClassification.equals("Biogesic")){
            if (productClassification.equals("Neozep")){
                btnVariation1.setText("Non-Drowsy");
                btnVariation2.setText("Drops | 10 mL");
            }
            selectProductVariation();
            btnBuyNow.setEnabled(false);
            btnAddToCart.setEnabled(false);
        } else {
            btnVariation1.setVisibility(View.GONE);
            btnVariation2.setVisibility(View.GONE);
        }
    }

    private void selectProductVariation(){
        btnVariation1.setOnClickListener(v -> {
            btnVariation1.setBackgroundColor(Color.parseColor("#0581E8"));
            btnVariation2.setBackgroundResource(R.drawable.rectangle_blue_border);

            btnBuyNow.setEnabled(true);
            btnAddToCart.setEnabled(true);

            if (productClassification.equals("Biogesic")){
                getProductDetails(productType, "Biogesic® For Kids | 100 mg | orange flavor", "Biogesic® For Kids | 100 mg | orange flavor");
            } else {
                getProductDetails(productType, "Neozep®", "Neozep®");
            }
        });

        btnVariation2.setOnClickListener(v -> {
            btnBuyNow.setEnabled(true);
            btnAddToCart.setEnabled(true);

            btnVariation2.setBackgroundColor(Color.parseColor("#0581E8"));
            btnVariation1.setBackgroundResource(R.drawable.rectangle_blue_border);
            if (productClassification.equals("Biogesic")){
                getProductDetails(productType, "Biogesic® For Kids | 120 mg", "Biogesic® For Kids | 120 mg | strawberry flavor");
            } else {
                getProductDetails(productType, "Neozep® Drops | 10ml", "Neozep® Drops | 10ml");
            }

        });
    }

    private void getProductDetails(String productType, String productKeyName, String productBrandName){
        productsModelArrayList = new ArrayList<>();
        dbRef.child("product-list").child(productType).orderByChild("brandName").equalTo(productBrandName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    ProductsModel productsModel = productSnapshot.getValue(ProductsModel.class);
                    productsModelArrayList.add(productsModel);
                }

                brandName = productsModelArrayList.get(0).getBrandName();
                description = productsModelArrayList.get(0).getDescription();
                price = String.valueOf(productsModelArrayList.get(0).getPrice());
                if (productsModelArrayList.get(0).getGenericName() != null){
                    genericName = productsModelArrayList.get(0).getGenericName();
                } else {
                    genericName = "";
                }
                imageUrl = productsModelArrayList.get(0).getImageUrl();
                quantity = String.valueOf(productsModelArrayList.get(0).getQuantity());

                showProductDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showProductDetails(){
        tvProductName = findViewById(R.id.tvProductName);

        tvItemBrandName = findViewById(R.id.tvItemBrandName);
        tvItemPrice = findViewById(R.id.tvItemPrice);
        tvItemGenericName = findViewById(R.id.tvItemGenericName);
        tvItemDesc = findViewById(R.id.tvItemDesc);
        tvItemCategory = findViewById(R.id.tvItemCategory);
//        tvItemQuantity = findViewById(R.id.tvItemQuantity);

        ivProduct = findViewById(R.id.ivProductImg);
        Picasso.get().load(imageUrl).into(ivProduct);

        tvProductName.setText(brandName);
        DecimalFormat df = new DecimalFormat("#,###.00");
        String formattedPrice = "Php " + df.format(Integer.parseInt(price));
//        tvItemQuantity.setText("Stock: " + quantity);

        tvItemBrandName.setText(brandName);
        tvItemPrice.setText(formattedPrice);
        if (!genericName.isEmpty()){
            tvItemGenericName.setText(genericName);
        } else {
            tvItemGenericName.setText("");
        }

        SpannableString spannableDescTitle = new SpannableString("Description: " + description);
        spannableDescTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString spannableCategoryTitle = new SpannableString("Category: " + category);
        spannableCategoryTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, 9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvItemDesc.setText(spannableDescTitle);
        tvItemCategory.setText(spannableCategoryTitle);

    }

    private void addToCart() {
        SharedPreferences sharedPref = getSharedPreferences("sp", MODE_PRIVATE);
        JSONArray productsArray = new JSONArray();


        String productsOnCart = sharedPref.getString("productDetails", "");
        if (!productsOnCart.isEmpty()){
            try {
                productsArray = new JSONArray(productsOnCart);
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        JSONObject forSuggestionObj = new JSONObject();
        JSONArray forSuggestionArr = new JSONArray();
        String suggestionItems = sharedPref.getString("suggestionItems", "");
        if (!suggestionItems.isEmpty()){
            try {
                forSuggestionArr = new JSONArray(suggestionItems);
            } catch (JSONException e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        JSONArray finalProductsArray = productsArray;
        JSONArray finalForSuggestionArr = forSuggestionArr;
        btnAddToCart.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), CartActivity.class);
//            intent.putExtra("productDetails", productDetails);
//            startActivity(intent);


            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                if (productClassification.equals("Neozep") || productClassification.equals("Biogesic")){
                    String productName = productsModelArrayList.get(0).getBrandName();
                    JsonArray jsonArr = new Gson().fromJson(productsOnCart, JsonArray.class);
                    JsonArray jsonArr2 = new Gson().fromJson(suggestionItems, JsonArray.class);
                    if (hasValue(jsonArr, productName)){
                        Toast.makeText(this, "This item is already in the cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!hasSuggestion(jsonArr2, category)){
                            forSuggestionObj.put("suggestionCategory", category);
                            forSuggestionObj.put("suggestionItemName", productName);
                            finalForSuggestionArr.put(forSuggestionObj);
                        }
                        String gsonProductDetails = new Gson().toJson(productsModelArrayList);
                        JSONArray productDetailsArr = new JSONArray(gsonProductDetails);
                        JSONObject productDetailsObj = productDetailsArr.getJSONObject(0);
                        finalProductsArray.put(productDetailsObj);
                        editor.putString("productDetails", finalProductsArray.toString());
                        editor.putString("suggestionItems", finalForSuggestionArr.toString());
                        editor.apply();
                        Toast.makeText(this, "Item added to cart successfully!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    JSONObject productDetailsObj = new JSONObject(productDetails);
                    String productName = productDetailsObj.getString("brandName");
                    JsonArray jsonArr = new Gson().fromJson(productsOnCart, JsonArray.class);
                    JsonArray jsonArr2 = new Gson().fromJson(suggestionItems, JsonArray.class);
                    if (hasValue(jsonArr, productName)){
                        Toast.makeText(this, "This item is already in the cart!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!hasSuggestion(jsonArr2, category)){
                            forSuggestionObj.put("suggestionCategory", category);
                            forSuggestionObj.put("suggestionItemName", productName);
                            finalForSuggestionArr.put(forSuggestionObj);
                        }

                        finalProductsArray.put(productDetailsObj);
                        editor.putString("productDetails", finalProductsArray.toString());
                        editor.putString("suggestionItems", finalForSuggestionArr.toString());
                        editor.apply();
                        Toast.makeText(this, "Item added to cart successfully!", Toast.LENGTH_SHORT).show();
                    }

                }

                Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
                intent.putExtra("fromWhatTab", category);
                startActivity(intent);
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private boolean hasSuggestion(JsonArray json, String value){
        if (json != null){
            for (int i = 0; i < json.size(); i++){
                if (json.get(i).getAsJsonObject().get("suggestionCategory").getAsString().equals(value)){
                    return true;
                }
            }
        }

        return false;
    }


    private void buyNow() {
        SharedPreferences sharedPref = getSharedPreferences("sp", MODE_PRIVATE);
        JSONArray productsArray = new JSONArray();

//        String productsOnCart = sharedPref.getString("productDetails", "");
//        if (!productsOnCart.isEmpty()){
//            try {
//                productsArray = new JSONArray(productsOnCart);
//            } catch (JSONException e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }

        btnBuyNow.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                JSONObject productDetailsObj = new JSONObject(productDetails);
                productsArray.put(productDetailsObj);

                String productName = productDetailsObj.getString("brandName");
                JSONObject forSuggestionObj = new JSONObject();
                forSuggestionObj.put("suggestionCategory", category);
                forSuggestionObj.put("suggestionItemName", productName);
                JSONArray forSuggestionArr = new JSONArray();
                forSuggestionArr.put(forSuggestionObj);

                editor.putString("suggestionItems", forSuggestionArr.toString());
                editor.putString("buyNow", productsArray.toString());
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                intent.putExtra("fromBuyNow", "fromBuyNow");
                intent.putExtra("totalAmount", productDetailsObj.getString("price") + ".00");
                intent.putExtra("productModel", productDetails);
                intent.putExtra("category", category);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}