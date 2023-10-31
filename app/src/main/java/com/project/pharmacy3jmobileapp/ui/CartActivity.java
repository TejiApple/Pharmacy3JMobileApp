package com.project.pharmacy3jmobileapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.google.gson.Gson;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.ui.adapter.CartProductDetailsListAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {
    String productDetails;
    ListView lvProductInTheCart;
    CartProductDetailsListAdapter cartProductDetailsListAdapter;
    ArrayList<ProductsModel> productsModelArrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        productDetails = Objects.requireNonNull(getIntent().getExtras().get("productDetails")).toString();
        Gson gson = new Gson();
        ProductsModel productsInTheCart = gson.fromJson(productDetails, ProductsModel.class);
        productsModelArrayList = new ArrayList<>();
        productsModelArrayList.add(productsInTheCart);

        lvProductInTheCart = findViewById(R.id.lvProductsToCheckout);

        showProductsInTheCart();
    }

    private void showProductsInTheCart() {
        cartProductDetailsListAdapter = new CartProductDetailsListAdapter(CartActivity.this, productsModelArrayList);
        lvProductInTheCart.setAdapter(cartProductDetailsListAdapter);
    }
}