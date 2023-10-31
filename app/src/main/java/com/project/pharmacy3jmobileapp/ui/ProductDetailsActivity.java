package com.project.pharmacy3jmobileapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class ProductDetailsActivity extends AppCompatActivity {
    String brandName, description, price, productDetails;
    TextView tvProductName, tvDescription, tvPrice;
    Button btnAddToCart;

    ArrayList<ProductsModel> productsModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productModel")).toString();

        try {
            JSONObject productDetailsObj = new JSONObject(productDetails);
            brandName = productDetailsObj.getString("brandName");
            description = productDetailsObj.getString("description");
            price = productDetailsObj.getString("price");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        showProductDetails();
        addToCart();
    }

    private void showProductDetails(){
        tvProductName = findViewById(R.id.tvProductName);
        tvDescription = findViewById(R.id.tvProductDescription);
        tvPrice = findViewById(R.id.tvProductPrice);

        tvProductName.setText(brandName);
        tvDescription.setText(description);
        tvPrice.setText(price);
    }

    private void addToCart() {
        btnAddToCart.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CartActivity.class);
            intent.putExtra("productDetails", productDetails);
            startActivity(intent);
        });
    }

}