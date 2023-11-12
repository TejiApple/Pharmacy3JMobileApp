package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import org.json.JSONObject;

import java.util.ArrayList;

public class HomepageActivity extends AppCompatActivity {

    DatabaseReference dbRef;
    ArrayList<ProductsModel> productsModelArrayList;
    BaseAdapter baseAdapter;
    HomepageGridViewAdapter productsAdapter;
    GridView gridView;
    String username;
    TextView tvUsername;
    ImageButton btnHealthCare, btnPersonalCare, btnBeautyCare, btnBabyAndKids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        gridView = findViewById(R.id.gvProducts);
        tvUsername = findViewById(R.id.tvUser);
        btnHealthCare = findViewById(R.id.ibHealthCare);
        btnPersonalCare = findViewById(R.id.ibPersonalCare);
        btnBeautyCare = findViewById(R.id.ibBeautyCare);
        btnBabyAndKids = findViewById(R.id.ibBabyAndKids);

        username = getIntent().getExtras().get("username").toString();
        tvUsername.setText("Hello " + username);

        displayHealthCareProducts();

        btnHealthCare.setOnClickListener(v -> displayHealthCareProducts());
        btnPersonalCare.setOnClickListener(v -> displayPersonalCareProducts());
        btnBeautyCare.setOnClickListener(v -> displayBeautyCareProducts());
        btnBabyAndKids.setOnClickListener(v -> displayBabyAndKidsProducts());

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