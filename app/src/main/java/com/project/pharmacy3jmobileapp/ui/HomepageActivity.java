package com.project.pharmacy3jmobileapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.RegistrationModel;
import com.project.pharmacy3jmobileapp.ui.adapter.HomepageFilterAdapter;
import com.project.pharmacy3jmobileapp.ui.adapter.HomepageGridViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomepageActivity extends AppCompatActivity {

    DatabaseReference dbRef;
    ArrayList<ProductsModel> productsModelArrayList;
    ArrayList<RegistrationModel> registrationModelArrayList;
    BaseAdapter baseAdapter;
    HomepageGridViewAdapter productsAdapter;
    GridView gridView;
    String username, category;
    TextView tvUsername, tvProductsOnCart;
    ImageButton btnHealthCare, btnPersonalCare, btnBeautyCare, btnBabyAndKids, btnCart, btnSettings, btnFilter;
    EditText etSearchProducts;
    SharedPreferences sp;

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
        btnFilter = findViewById(R.id.ibFilter);
        etSearchProducts = findViewById(R.id.etSearchProducts);

        sp = getSharedPreferences("sp", MODE_PRIVATE);
        username = sp.getString("username", "");
        tvUsername.setText("Hello, " + username);
        String productsOnCart = sp.getString("productDetails", "");
        if (!productsOnCart.isEmpty()){
            try {
                JSONArray productsOnCartArray = new JSONArray(productsOnCart);
                if (productsOnCartArray.length() == 0){
                    tvProductsOnCart.setVisibility(View.GONE);
                } else {
                    tvProductsOnCart.setVisibility(View.VISIBLE);
                    tvProductsOnCart.setText(Integer.toString(productsOnCartArray.length()));
                }
            } catch (JSONException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            tvProductsOnCart.setVisibility(View.GONE);
        }
        retrieveData();

        if (getIntent().getExtras().get("fromWhatTab") != null){
            String fromWhatTab = getIntent().getExtras().getString("fromWhatTab");
            if (fromWhatTab.equals("Personal Care")){
                displayPersonalCareProducts();
            } else if (fromWhatTab.equals("Beauty Care")) {
                displayBeautyCareProducts();
            } else if (fromWhatTab.equals("Baby & Kids")) {
                displayBabyAndKidsProducts();
            } else {
                displayHealthCareProducts();
            }
        } else {
            displayHealthCareProducts();
            category = "Health Care";
        }

        btnHealthCare.setOnClickListener(v -> {
            displayHealthCareProducts();
            category = "Health Care";
        });
        btnPersonalCare.setOnClickListener(v -> {
            displayPersonalCareProducts();
            category = "Personal Care";
        });
        btnBeautyCare.setOnClickListener(v -> {
            displayBeautyCareProducts();
            category = "Beauty Care";
        });
        btnBabyAndKids.setOnClickListener(v -> {
            displayBabyAndKidsProducts();
            category = "Baby & Kids";
        });
        btnCart.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CartActivity.class)));
        btnSettings.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SettingsActivity.class)));
        btnFilter.setOnClickListener(v -> {
            if (getIntent().getExtras().get("fromWhatTab") != null){
                String fromWhatTab = getIntent().getExtras().getString("fromWhatTab");
                if (fromWhatTab.equals("Personal Care")){
                    filter(fromWhatTab);
                } else if (fromWhatTab.equals("Beauty Care")) {
                    filter(fromWhatTab);
                } else if (fromWhatTab.equals("Baby & Kids")) {
                    filter(fromWhatTab);
                } else {
                    filter(fromWhatTab);
                }
            } else {
                filter(category);
            }
        });
        etSearchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                performSearch(s.toString(), "", "");
            }
        });
        etSearchProducts.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(etSearchProducts.getText().toString(), "", "");
                return true;
            }
            return false;
        });


    }

    private void performSearch(String value, String value1, String value2) {
        try {
            ArrayList<ProductsModel> productsSearchResult = new ArrayList<>();
            for (int i = 0; i < productsModelArrayList.size(); i++){
                ProductsModel productsModel = productsModelArrayList.get(i);
                if (!value.isEmpty()){
                    if (!value1.isEmpty() && !value2.isEmpty()){
                        int productAmount = productsModel.getPrice();
                        int minAmt = Integer.parseInt(value1);
                        int maxAmt = Integer.parseInt(value2);

                        if (productsModel.getBrandName().toLowerCase().contains(value.toLowerCase()) && productAmount >= minAmt && productAmount <= maxAmt){
                            productsSearchResult.add(productsModel);
                        } else if (productsModel.getTags().toLowerCase().contains(value.toLowerCase()) && productAmount >= minAmt && productAmount <= maxAmt) {
                            productsSearchResult.add(productsModel);
                        }
                    } else {
                        if (productsModel.getBrandName().toLowerCase().contains(value.toLowerCase())){
                            productsSearchResult.add(productsModel);
                        } else if (productsModel.getTags().toLowerCase().contains(value.toLowerCase())) {
                            productsSearchResult.add(productsModel);
                        }
                    }
                } else if (!value1.isEmpty() && !value2.isEmpty()) {
                    int productAmount = productsModel.getPrice();
                    int minAmt = Integer.parseInt(value1);
                    int maxAmt = Integer.parseInt(value2);

                    if (productAmount >= minAmt && productAmount <= maxAmt){
                        productsSearchResult.add(productsModel);
                    }
                }

            }
            productsAdapter = new HomepageGridViewAdapter(HomepageActivity.this, productsSearchResult);
            gridView.setAdapter(productsAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), ProductDetailsActivity.class);
                    String objectFromArray = "";
                    try {
                        Gson gson = new Gson();
                        String productModelAsString = gson.toJson(productsSearchResult);
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
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void performMultipleSearch(String value1, String value2) {
        try {
            ArrayList<ProductsModel> productsSearchResult = new ArrayList<>();
            String filters = sp.getString("filters", "");
            ArrayList<String> filtersList = new ArrayList<>();
            if (!filters.isEmpty()){
               filters = filters.substring(1, filters.length() - 1);
               String[] filterItems = filters.split(",\\s*");
               filtersList = new ArrayList<>(Arrays.asList(filterItems));
            }
            for (int j = 0; j < filtersList.size(); j++){
                String value = filtersList.get(j);
                for (int i = 0; i < productsModelArrayList.size(); i++){
                    ProductsModel productsModel = productsModelArrayList.get(i);
                    if (!value.isEmpty()){
                        if (!value1.isEmpty() && !value2.isEmpty()){
                            int productAmount = productsModel.getPrice();
                            int minAmt = Integer.parseInt(value1);
                            int maxAmt = Integer.parseInt(value2);

                            if (productsModel.getBrandName().toLowerCase().contains(value.toLowerCase()) && productAmount >= minAmt && productAmount <= maxAmt){
                                productsSearchResult.add(productsModel);
                            } else if (productsModel.getTags().toLowerCase().contains(value.toLowerCase()) && productAmount >= minAmt && productAmount <= maxAmt) {
                                productsSearchResult.add(productsModel);
                            }
                        } else {
                            if (productsModel.getBrandName().toLowerCase().contains(value.toLowerCase())){
                                productsSearchResult.add(productsModel);
                            } else if (productsModel.getTags().toLowerCase().contains(value.toLowerCase())) {
                                productsSearchResult.add(productsModel);
                            }
                        }
                    } else if (!value1.isEmpty() && !value2.isEmpty()) {
                        int productAmount = productsModel.getPrice();
                        int minAmt = Integer.parseInt(value1);
                        int maxAmt = Integer.parseInt(value2);

                        if (productAmount >= minAmt && productAmount <= maxAmt){
                            productsSearchResult.add(productsModel);
                        }
                    }

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

                            if (productsModelArrayList.get(position).getQuantity() == 0){
                                Toast.makeText(HomepageActivity.this, "This item is sold out.", Toast.LENGTH_SHORT).show();
                            } else {
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

    private void retrieveData(){
        dbRef = FirebaseDatabase.getInstance().getReference();
        registrationModelArrayList = new ArrayList<>();
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

                                String customerName = registrationModelArrayList.get(0).getCompleteName();
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("customerName", customerName);
                                editor.apply();
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

    private void filter(String category) {
        Dialog filterDialog = new Dialog(this);
        View view = getLayoutInflater().inflate(R.layout.homepage_filter_drawer, null);
        filterDialog.setCancelable(true);
        filterDialog.setContentView(view);
        Objects.requireNonNull(filterDialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        GridView gvOptions = filterDialog.findViewById(R.id.gvFilter);

        String[] healthcare = {"For Cough", "For Fever", "For Colds", "For Body ache, etc.", "For Head ache",
                "For clogged nose"};
        String[] personalcare = {"Lotion", "Hand Sanitizer", "Disinfectant", "Feminine", "Oral Hygiene",
                "Antiseptic"};
        String[] beautycare = {"Skincare", "Facial Wash", "Toner", "Moisturizer", "Cleanser"};
        String[] babyandkids = {"Baby Body Wash", "Moisturizer", "Hypoallergenic", "Baby diaper", "Baby cologne",
                "Baby wipes"};

        ArrayList<String> filterList;
        if (category.equals("Health Care")){
            filterList = new ArrayList<>(Arrays.asList(healthcare));
        } else if (category.equals("Personal Care")) {
            filterList = new ArrayList<>(Arrays.asList(personalcare));
        } else if (category.equals("Beauty Care")) {
            filterList = new ArrayList<>(Arrays.asList(beautycare));
        } else if (category.equals("Baby & Kids")) {
            filterList = new ArrayList<>(Arrays.asList(babyandkids));
        } else {
            filterList = new ArrayList<>();
        }

        ArrayList<Integer> selectedFilterPosition = new ArrayList<>();

        HomepageFilterAdapter homepageFilterAdapter = new HomepageFilterAdapter(this, filterList, sp);
        gvOptions.setAdapter(homepageFilterAdapter);




//        gvOptions.setOnItemClickListener((parent, view1, position, id) -> {
//            if (category.equals("Health Care")){
//                if (position == 0){
//                    selectedFilter.add("Cough");
//                } else if (position == 1) {
//                    selectedFilter.add("Fever");
//                } else if (position == 2) {
//                    selectedFilter.add("Colds");
//                } else if (position == 3) {
//                    selectedFilter.add("Body ache");
//                } else if (position == 4) {
//                    selectedFilter.add("Headache");
//                } else if (position == 5) {
//                    selectedFilter.add("Clogged nose");
//                }
//            } else {
//                selectedFilter.add(filterList.get(position));
//            }
//
//            view1.setBackgroundColor(Color.parseColor("#faa63e"));
//        });

        Button btn050 = filterDialog.findViewById(R.id.btn050);
        Button btn51200 = filterDialog.findViewById(R.id.btn51200);
        Button btn201500 = filterDialog.findViewById(R.id.btn201500);
        Button btnCancelFilter = filterDialog.findViewById(R.id.btnCancelFilter);
        Button btnApplyFilter = filterDialog.findViewById(R.id.btnApplyFilter);

        TextInputEditText etMinimumAmt = filterDialog.findViewById(R.id.etMinimumAmount);
        TextInputEditText etMaximumAmt = filterDialog.findViewById(R.id.etMaximumAmount);

        btn050.setOnClickListener(v -> {
            etMinimumAmt.setText("1");
            etMaximumAmt.setText("50");
        });

        btn51200.setOnClickListener(v -> {
            etMinimumAmt.setText("51");
            etMaximumAmt.setText("200");
        });

        btn201500.setOnClickListener(v -> {
            etMinimumAmt.setText("201");
            etMaximumAmt.setText("500");
        });

        ArrayList<String> filters = new ArrayList<>();
        btnApplyFilter.setOnClickListener(v -> {
            String selectedFilter = sp.getString("selectedFilter", "");

            if (!selectedFilter.isEmpty()){
                Pattern pattern = Pattern.compile("\\d+");
                Matcher matcher = pattern.matcher(selectedFilter);

                while (matcher.find()) {
                    int number = Integer.parseInt(matcher.group());
                    selectedFilterPosition.add(number);
                }

                for (int i = 0; i < selectedFilterPosition.size(); i++){
                    int position = selectedFilterPosition.get(i);
                    if (category.equals("Health Care")){
                        if (position == 0){
                            filters.add("Cough");
                        } else if (position == 1) {
                            filters.add("Fever");
                        } else if (position == 2) {
                            filters.add("Colds");
                        } else if (position == 3) {
                            filters.add("Body ache");
                        } else if (position == 4) {
                            filters.add("Headache");
                        } else if (position == 5) {
                            filters.add("Clogged nose");
                        }
                    } else {
                        filters.add(filterList.get(position));
                    }
                }

            }

            if (filters.size() > 0) {
                for (int i = 0; i < filters.size(); i++) {
                    SharedPreferences.Editor editor = sp.edit();
                    if (!etMinimumAmt.getText().toString().isEmpty() && !etMaximumAmt.getText().toString().isEmpty()) {
//                        performSearch(filters.get(i), etMinimumAmt.getText().toString(), etMaximumAmt.getText().toString());
                        editor.putString("filters", filters.toString()).apply();
                        performMultipleSearch(etMinimumAmt.getText().toString(), etMaximumAmt.getText().toString());
                    } else {
//                        performSearch(filters.get(i), "", "");
                        editor.putString("filters", filters.toString()).apply();
                        performMultipleSearch("", "");

                    }
                }
                filterDialog.hide();
            } else if (!etMinimumAmt.getText().toString().isEmpty() && !etMaximumAmt.getText().toString().isEmpty()) {
                performSearch("", etMinimumAmt.getText().toString(), etMaximumAmt.getText().toString());
                sp.edit().remove("selectedFilter").apply();
                filterDialog.hide();
            } else {
                Toast.makeText(this, "No filter selected.", Toast.LENGTH_SHORT).show();
            }

        });

        btnCancelFilter.setOnClickListener(v -> {
            sp.edit().remove("selectedFilter").apply();
            sp.edit().remove("filters").apply();
            filters.clear();
            filterDialog.dismiss();
            if (category.equals("Personal Care")){
                displayPersonalCareProducts();
            } else if (category.equals("Health Care")) {
                displayHealthCareProducts();
            } else if (category.equals("Beauty Care")) {
                displayBeautyCareProducts();
            } else if (category.equals("Baby & Kids")) {
                displayBabyAndKidsProducts();
            }
        });

        filterDialog.show();
    }

}