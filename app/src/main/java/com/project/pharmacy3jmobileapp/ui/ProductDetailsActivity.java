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
    String brandName, description, price, productDetails, genericName, category, imageUrl, quantity;
    TextView tvProductName, tvDescription, tvPrice, tvItemBrandName, tvItemPrice, tvItemGenericName, tvItemDesc, tvItemCategory, tvItemQuantity;
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
    }

    @SuppressLint("SetTextI18n")
    private void showProductDetails(){
        tvProductName = findViewById(R.id.tvProductName);

        tvItemBrandName = findViewById(R.id.tvItemBrandName);
        tvItemPrice = findViewById(R.id.tvItemPrice);
        tvItemGenericName = findViewById(R.id.tvItemGenericName);
        tvItemDesc = findViewById(R.id.tvItemDesc);
        tvItemCategory = findViewById(R.id.tvItemCategory);
        tvItemQuantity = findViewById(R.id.tvItemQuantity);

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
                JSONObject productDetailsObj = new JSONObject(productDetails);
                String productName = productDetailsObj.getString("brandName");
//                for (int i = 0; i < finalProductsArray.length(); i++){
//                    if (finalProductsArray.getJSONObject(i).getString("brandName").equals(productName)){
//                        int selectedProductQuantity = finalProductsArray.getJSONObject(i).getInt("quantity") + 1;
//                        productDetailsObj.put("quantity", selectedProductQuantity);
//                        finalProductsArray.put(productDetailsObj);
//                        finalProductsArray.remove(i);
//                        break;
//                    } else {
//                        finalProductsArray.put(productDetailsObj);
//                        break;
//                    }
//                }
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
//                for (int i = 0; i < finalProductsArray.length(); i++){
//                    if (finalProductsArray.getJSONObject(i).getString("brandName").equals(productName)){
//                        Toast.makeText(this, "This item is already in the cart!", Toast.LENGTH_SHORT).show();
//                        break;
//                    } else {
//                        int selectedProductQuantity = finalProductsArray.getJSONObject(i).getInt("quantity") + 1;
//                        productDetailsObj.put("quantity", selectedProductQuantity);
//                        finalProductsArray.put(productDetailsObj);
//                        editor.putString("productDetails", finalProductsArray.toString());
//                        editor.apply();
//                        Toast.makeText(this, "Item added to cart successfully!", Toast.LENGTH_SHORT).show();
//                    }
//                }

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