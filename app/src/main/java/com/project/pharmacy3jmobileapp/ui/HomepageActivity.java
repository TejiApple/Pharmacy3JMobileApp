package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.ui.adapter.HomepageGridViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {

    DatabaseReference dbRef;
    ArrayList<ProductsModel> productsModelArrayList;
    BaseAdapter baseAdapter;
    HomepageGridViewAdapter productsAdapter;
    GridView gridView;
    String username;
    TextView tvUsername, tvProductsOnCart;
    ImageButton btnHealthCare, btnPersonalCare, btnBeautyCare, btnBabyAndKids, btnCart, btnSettings;
    EditText etSearchProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        gridView = findViewById(R.id.gvProducts);
        tvUsername = findViewById(R.id.tvUser);
        tvProductsOnCart = findViewById(R.id.tvProductsOnCart);
        btnHealthCare = findViewById(R.id.ibHealthCare);
        btnPersonalCare = findViewById(R.id.ibPersonalCare);
        btnBeautyCare = findViewById(R.id.ibBeautyCare);
        btnBabyAndKids = findViewById(R.id.ibBabyAndKids);
        btnCart = findViewById(R.id.ibCart);
        btnSettings = findViewById(R.id.ibSettings);
        etSearchProducts = findViewById(R.id.etSearchProducts);

        SharedPreferences sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        tvUsername.setText("Hello, " + username);
        String productsOnCart = sp.getString("productDetails", "");
        if (!productsOnCart.isEmpty()){
            try {
                JSONArray productsOnCartArray = new JSONArray(productsOnCart);
                tvProductsOnCart.setText(Integer.toString(productsOnCartArray.length()));
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        displayHealthCareProducts();

        btnHealthCare.setOnClickListener(v -> displayHealthCareProducts());
        btnPersonalCare.setOnClickListener(v -> displayPersonalCareProducts());
        btnBeautyCare.setOnClickListener(v -> displayBeautyCareProducts());
        btnBabyAndKids.setOnClickListener(v -> displayBabyAndKidsProducts());
        btnCart.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CartActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));

        etSearchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString());
            }
        });
        etSearchProducts.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearchProducts.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String value) {
        try {
            ArrayList<ProductsModel> productsSearchResult = new ArrayList<>();
            for (int i = 0; i < productsModelArrayList.size(); i++){
                ProductsModel productsModel = productsModelArrayList.get(i);
                if (productsModel.getBrandName().toLowerCase().contains(value.toLowerCase())){
                    productsSearchResult.add(productsModel);
                }
            }
            productsAdapter = new HomepageGridViewAdapter(HomepageActivity.this, productsSearchResult);
            gridView.setAdapter(productsAdapter);
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayHealthCareProducts() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        productsModelArrayList = new ArrayList<>();

        try {
            dbRef.child("product-list").child("health-care").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot productSnapshot : snapshot.getChildren()){
                        ProductsModel productsModel = productSnapshot.getValue(ProductsModel.class);
                        productsModelArrayList.add(productsModel);
                    }
                    productsAdapter = new HomepageGridViewAdapter(HomepageActivity.this, productsModelArrayList);
                    gridView.setAdapter(productsAdapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                            String objectFromArray = "";
                            try {
                                Gson gson = new Gson();
                                String productModelAsString = gson.toJson(productsModelArrayList);
                                JSONArray jsonArray = new JSONArray(productModelAsString);
                                objectFromArray = jsonArray.get(position).toString();
                            } catch (JSONException e){
                                Toast.makeText(HomepageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            intent.putExtra("productModel", objectFromArray);
                            intent.putExtra("category", "Health Care");
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void displayPersonalCareProducts() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        productsModelArrayList = new ArrayList<>();

        dbRef.child("product-list").child("personal-care").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    ProductsModel productsModel = productSnapshot.getValue(ProductsModel.class);
                    productsModelArrayList.add(productsModel);
                }
                productsAdapter = new HomepageGridViewAdapter(HomepageActivity.this, productsModelArrayList);
                gridView.setAdapter(productsAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                        intent.putExtra("brandName",productsModelArrayList.get(position).getBrandName());
                        intent.putExtra("description", productsModelArrayList.get(position).getDescription());
                        intent.putExtra("price", productsModelArrayList.get(position).getPrice());
                        intent.putExtra("category", "Personal Care");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayBeautyCareProducts() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        productsModelArrayList = new ArrayList<>();

        dbRef.child("product-list").child("beauty-care").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    ProductsModel productsModel = productSnapshot.getValue(ProductsModel.class);
                    productsModelArrayList.add(productsModel);
                }
                productsAdapter = new HomepageGridViewAdapter(HomepageActivity.this, productsModelArrayList);
                gridView.setAdapter(productsAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                        intent.putExtra("brandName",productsModelArrayList.get(position).getBrandName());
                        intent.putExtra("description", productsModelArrayList.get(position).getDescription());
                        intent.putExtra("price", productsModelArrayList.get(position).getPrice());
                        intent.putExtra("category", "Beauty Care");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void displayBabyAndKidsProducts() {
        dbRef = FirebaseDatabase.getInstance().getReference();
        productsModelArrayList = new ArrayList<>();

        dbRef.child("product-list").child("baby-and-kids").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    ProductsModel productsModel = productSnapshot.getValue(ProductsModel.class);
                    productsModelArrayList.add(productsModel);
                }
                productsAdapter = new HomepageGridViewAdapter(HomepageActivity.this, productsModelArrayList);
                gridView.setAdapter(productsAdapter);

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                        intent.putExtra("brandName",productsModelArrayList.get(position).getBrandName());
                        intent.putExtra("description", productsModelArrayList.get(position).getDescription());
                        intent.putExtra("price", productsModelArrayList.get(position).getPrice());
                        intent.putExtra("category", "Baby & Kids");
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}