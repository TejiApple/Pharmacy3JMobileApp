package com.project.pharmacy3jmobileapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;
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
    String brandName, description, price, productDetails, genericName, category, imageUrl;
    TextView tvProductName, tvDescription, tvPrice, tvItemBrandName, tvItemPrice, tvItemGenericName, tvItemDesc, tvItemCategory;
    ImageView ivProduct;
    Button btnAddToCart, btnBuyNow;

    ArrayList<ProductsModel> productsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productModel")).toString();
        category = getIntent().getExtras().getString("category");

        try {
            JSONObject productDetailsObj = new JSONObject(productDetails);
            brandName = productDetailsObj.getString("brandName");
            description = productDetailsObj.getString("description");
            price = productDetailsObj.getString("price");
            genericName = productDetailsObj.getString("genericName");
            imageUrl = productDetailsObj.getString("imageUrl");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        showProductDetails();
        addToCart();
    }

    @SuppressLint("SetTextI18n")
    private void showProductDetails(){
        tvProductName = findViewById(R.id.tvProductName);
        tvDescription = findViewById(R.id.tvProductDescription);
        tvPrice = findViewById(R.id.tvProductPrice);

        tvItemBrandName = findViewById(R.id.tvItemBrandName);
        tvItemPrice = findViewById(R.id.tvItemPrice);
        tvItemGenericName = findViewById(R.id.tvItemGenericName);
        tvItemDesc = findViewById(R.id.tvItemDesc);
        tvItemCategory = findViewById(R.id.tvItemCategory);

        ivProduct = findViewById(R.id.ivProductImg);
        Picasso.get().load(imageUrl).into(ivProduct);

        tvProductName.setText(brandName);
        tvDescription.setText(description);
        DecimalFormat df = new DecimalFormat("#,###.00");
        String formattedPrice = "Php " + df.format(Integer.parseInt(price));
        tvPrice.setText(formattedPrice);

        tvItemBrandName.setText(brandName);
        tvItemPrice.setText(formattedPrice);
        tvItemGenericName.setText(genericName);

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

        JSONArray finalProductsArray = productsArray;
        btnAddToCart.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), CartActivity.class);
//            intent.putExtra("productDetails", productDetails);
//            startActivity(intent);


            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                JSONObject productDetailsObj = new JSONObject(productDetails);
                finalProductsArray.put(productDetailsObj);
                editor.putString("productDetails", finalProductsArray.toString());
                editor.apply();
                Toast.makeText(this, "Item added to cart successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomepageActivity.class));
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buyNow() {
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

        JSONArray finalProductsArray = productsArray;
        btnBuyNow.setOnClickListener(v -> {

            SharedPreferences.Editor editor = sharedPref.edit();
            try {
                JSONObject productDetailsObj = new JSONObject(productDetails);
                finalProductsArray.put(productDetailsObj);
                editor.putString("buyNow", finalProductsArray.toString());
                editor.apply();
                startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}