package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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

public class SuggestionsActivity extends AppCompatActivity {
    DatabaseReference dbRef;

    ArrayList<ProductsModel> productsModelArrayList;
    HomepageGridViewAdapter productsAdapter;

    GridView gridView;
    Button btnBackToHome;

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        sharedPref = getSharedPreferences("sp", MODE_PRIVATE);
        dbRef = FirebaseDatabase.getInstance().getReference();

        btnBackToHome = findViewById(R.id.btnBackToHome);
        gridView = findViewById(R.id.gvSuggestionProducts);

        retrieveSharedPref();
        btnBackToHome.setOnClickListener(v -> {
            deleteSharedPrefAndGoBack();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteSharedPrefAndGoBack();
    }

    private void retrieveSharedPref() {
        String suggestedItem = sharedPref.getString("suggestionItems", "");

        try {
            JSONArray suggestedFromSelectedItems = new JSONArray(suggestedItem);
            for (int i = 0; i < suggestedFromSelectedItems.length(); i++){
                JSONObject jsonObj = suggestedFromSelectedItems.getJSONObject(i);
                String category = jsonObj.getString("suggestionCategory");
                String itemName = jsonObj.getString("suggestionItemName");
                if (category.equals("Health Care")){
                    displayHealthCareProducts();
                } else if (category.equals("Personal Care")) {
                    displayPersonalCareProducts();
                }
            }
        } catch (JSONException e){

        }
    }

    private void deleteSharedPrefAndGoBack(){
        sharedPref.edit().remove("suggestionItems").apply();
        Intent intent = new Intent(getApplicationContext(), HomepageActivity.class);
        intent.putExtra("fromWhatTab", "Suggestion");
        startActivity(intent);
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
                    productsAdapter = new HomepageGridViewAdapter(SuggestionsActivity.this, productsModelArrayList);
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
                                Toast.makeText(SuggestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                productsAdapter = new HomepageGridViewAdapter(SuggestionsActivity.this, productsModelArrayList);
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
                            Toast.makeText(SuggestionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        intent.putExtra("productModel", objectFromArray);
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

}