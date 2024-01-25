package com.project.pharmacy3jmobileapp.ui.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CartProductDetailsListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ProductsModel> productsModel;

    private OrderDetails orderDetails;
    private SharedPreferences sharedPreferences;
    private String fromWhatScreen, fromWhatButton;
    double total;

    public CartProductDetailsListAdapter(Context context, ArrayList<ProductsModel> productsModel, OrderDetails orderDetails, SharedPreferences sharedPref, String fromWhatScreen, String fromWhatButton) {
        this.context = context;
        this.productsModel = productsModel;
        this.orderDetails = orderDetails;
        this.sharedPreferences = sharedPref;
        this.fromWhatScreen = fromWhatScreen;
        this.fromWhatButton = fromWhatButton;
    }

    @Override
    public int getCount() {
        return productsModel.size();
    }

    @Override
    public Object getItem(int position) {
        return productsModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_cart, null, true);
            }
            ImageView imageView = convertView.findViewById(R.id.ivProduct);
            TextView tvBrandName, tvPrice, tvQuantity, tvTotalAmount;
            int defaultQuantity = 1;
            CheckBox cbSelectItem = convertView.findViewById(R.id.cbSelectItem);
            Button removeItem = convertView.findViewById(R.id.btnRemoveProduct);
            tvBrandName = convertView.findViewById(R.id.tvProductNameInTheCart);
            tvPrice = convertView.findViewById(R.id.tvProductPriceInTheCart);
            tvQuantity = convertView.findViewById(R.id.tvProductQuantity);
            tvTotalAmount = convertView.findViewById(R.id.tvTotalAmount);
            ImageButton btnAdd, btnSubtract;
            btnAdd = convertView.findViewById(R.id.btnIncreaseProductQuantity);
            btnSubtract = convertView.findViewById(R.id.btnDecreaseProductQuantity);

            removeItem.setOnClickListener(v -> {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Remove item")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                productsModel.remove(position);
                                Gson gson = new Gson();
                                String newProductList = gson.toJson(productsModel);
                                JSONArray newProductsArray;
                                try {
                                    newProductsArray = new JSONArray(newProductList);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

//                productsArray.put(productsModel);

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("productDetails", newProductsArray.toString());
                                editor.apply();
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", null);
                alertDialog.show();
            });

            DecimalFormat df = new DecimalFormat("#,###.00");

            String price = String.valueOf(productsModel.get(position).getPrice());
            tvBrandName.setText(productsModel.get(position).getBrandName());
            String formattedPrice = "Php " + df.format(Integer.parseInt(price));
            tvPrice.setText(formattedPrice);
            if (fromWhatScreen.equals("CartActivity")){
                cbSelectItem.setVisibility(View.VISIBLE);
                tvTotalAmount.setText(formattedPrice);
                tvQuantity.setText(String.valueOf(defaultQuantity));
            } else if (fromWhatScreen.equals("CheckoutActivity")) {
                cbSelectItem.setVisibility(View.GONE);
                if (fromWhatButton.equals("fromBuyNow")){
                    btnAdd.setVisibility(View.VISIBLE);
                    btnSubtract.setVisibility(View.VISIBLE);
                    tvTotalAmount.setText(formattedPrice);
                    tvQuantity.setText(String.valueOf(defaultQuantity));
                } else {
                    btnAdd.setVisibility(View.GONE);
                    btnSubtract.setVisibility(View.GONE);
                    String totalAmountFromModel = productsModel.get(position).getTotalAmount();
                    String formattedTotalAmount = "Php " + df.format(Double.parseDouble(totalAmountFromModel.replace(",", "")));
                    tvTotalAmount.setText(formattedTotalAmount);
                    tvQuantity.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    tvQuantity.setText(productsModel.get(position).getQuantity() + " pc(s).");
                    total = Double.parseDouble(totalAmountFromModel.replace(",", ""));
                    orderDetails.cartTotalAmount(totalAmountFromModel.replace(",",""), defaultQuantity, 1, productsModel.get(position).getPosition(), fromWhatButton);
                }
//


            }
            Picasso.get().load(productsModel.get(position).getImageUrl()).into(imageView);


            AtomicInteger quantity = new AtomicInteger(defaultQuantity);
            AtomicInteger selectedItem = new AtomicInteger();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            int itemStocks = productsModel.get(position).getQuantity();
            btnAdd.setOnClickListener(v -> {
                quantity.getAndIncrement();
                tvQuantity.setText(quantity.toString());

                total = Double.parseDouble(price) * Double.parseDouble(String.valueOf(quantity));
                String totalAmt = df.format(total);
                tvTotalAmount.setText("Php " + totalAmt);
                if (fromWhatScreen.equals("CartActivity")){
                    if (cbSelectItem.isChecked()){
                        selectedItem.set(1);
                        String selectedItems = sharedPreferences.getString("selectedItems", "");
                        try {
                            JSONArray jsonArray2 = new JSONArray(selectedItems);
                            for (int i = 0; i < jsonArray2.length(); i++){
                                JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                int itemPosition = jsonObject.getInt("position");
                                if (itemPosition == position){
                                    jsonArray2.remove(i);
                                    editor.putString("selectedItems", jsonArray2.toString());
                                    editor.apply();
                                }
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        sharedPreferences = context.getSharedPreferences("sp", MODE_PRIVATE);
                        selectedItems = sharedPreferences.getString("selectedItems", "");
                        JSONArray jsonArray = new JSONArray();
                        if (!selectedItems.isEmpty()){
                            try {
                                jsonArray = new JSONArray(selectedItems);
                            } catch (JSONException e){
                                e.getMessage();
                            }
                        }

                        Gson gson = new Gson();

                        JSONObject jsonObj = null;
                        try {
                            jsonObj = new JSONObject(gson.toJson(productsModel.get(position)));
                            jsonObj.put("position", position);
                            jsonObj.put("quantity", quantity.get());
                            jsonObj.put("totalAmount", totalAmt);
                        } catch (JSONException e) {
                            e.getMessage();
                        }
                        jsonArray.put(jsonObj);
                        editor.putString("selectedItems", jsonArray.toString());
                        editor.apply();

                        orderDetails.cartTotalAmount(totalAmt, quantity.get(), selectedItem.get(), position, fromWhatButton);
                    } else {
                        selectedItem.set(0);
                        orderDetails.cartTotalAmount("0.00", 0, selectedItem.get(), position, fromWhatButton);
                    }
                } else {
                    selectedItem.set(1);
                    orderDetails.cartTotalAmount(totalAmt, quantity.get(), selectedItem.get(), position, fromWhatButton);
                    JSONArray buyNowArray = new JSONArray();
                    sharedPreferences = context.getSharedPreferences("sp", MODE_PRIVATE);

                    String buyNow = sharedPreferences.getString("buyNow", "");
                    if (!buyNow.isEmpty()){
                        sharedPreferences.edit().remove("buyNow").apply();
                    }

                    Gson gson = new Gson();
                    JSONObject buyNowObj;
                    try {
                        buyNowObj = new JSONObject(gson.toJson(productsModel.get(position)));
                        buyNowObj.put("position", position);
                        buyNowObj.put("quantity", quantity.get());
                        buyNowObj.put("totalAmount", totalAmt);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    buyNowArray.put(buyNowObj);
                    editor.putString("buyNow", buyNowArray.toString());
                    editor.apply();
                }


            });

            btnSubtract.setOnClickListener(v -> {
                quantity.getAndDecrement();
                if (quantity.get() < 1){
                    quantity.set(1);
                    tvQuantity.setText("1");
                } else {
                    tvQuantity.setText(quantity.toString());

                    double totalAmt = total - Double.parseDouble(price);
                    total = totalAmt;
                    tvTotalAmount.setText("Php " + df.format(totalAmt));
                    if (fromWhatScreen.equals("CartActivity")){
                        if (cbSelectItem.isChecked()){
                            selectedItem.set(1);
                            String selectedItems = sharedPreferences.getString("selectedItems", "");
                            try {
                                JSONArray jsonArray2 = new JSONArray(selectedItems);
                                for (int i = 0; i < jsonArray2.length(); i++){
                                    JSONObject jsonObject = jsonArray2.getJSONObject(i);
                                    int itemPosition = jsonObject.getInt("position");
                                    if (itemPosition == position){
                                        jsonArray2.remove(i);
                                        editor.putString("selectedItems", jsonArray2.toString());
                                        editor.apply();
                                    }
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            sharedPreferences = context.getSharedPreferences("sp", MODE_PRIVATE);
                            selectedItems = sharedPreferences.getString("selectedItems", "");
                            JSONArray jsonArray = new JSONArray();
                            if (!selectedItems.isEmpty()){
                                try {
                                    jsonArray = new JSONArray(selectedItems);
                                } catch (JSONException e){
                                    e.getMessage();
                                }
                            }

                            Gson gson = new Gson();

                            JSONObject jsonObj = null;
                            try {
                                jsonObj = new JSONObject(gson.toJson(productsModel.get(position)));
                                jsonObj.put("position", position);
                                jsonObj.put("quantity", quantity.get());
                                jsonObj.put("totalAmount", totalAmt);
                            } catch (JSONException e) {
                                e.getMessage();
                            }
                            jsonArray.put(jsonObj);
                            editor.putString("selectedItems", jsonArray.toString());
                            editor.apply();

                            orderDetails.cartTotalAmount(String.valueOf(totalAmt), quantity.get(), selectedItem.get(), position, fromWhatButton);
                        } else {
                            selectedItem.set(0);
                            orderDetails.cartTotalAmount("0.00", 0, selectedItem.get(), position, fromWhatButton);
                        }
                    } else {
                        selectedItem.set(1);
                        orderDetails.cartTotalAmount(String.valueOf(totalAmt), quantity.get(), selectedItem.get(), position, fromWhatButton);
                    }

                }
            });


            cbSelectItem.setOnClickListener(v -> {
                if (cbSelectItem.isChecked()) {
                    selectedItem.set(1);
                    String totalAmt;
                    if (quantity.get() == 1) {
                        total = Double.parseDouble(price);
                        totalAmt = df.format(total);
                    } else {
                        totalAmt = df.format(total);
                    }
                    orderDetails.cartTotalAmount(totalAmt, quantity.get(), selectedItem.get(), position, fromWhatButton);
                    sharedPreferences = context.getSharedPreferences("sp", MODE_PRIVATE);

                    JSONArray jsonArray = new JSONArray();
                    String selectedItems = sharedPreferences.getString("selectedItems", "");
                    if (!selectedItems.isEmpty()){
                        try {
                            jsonArray = new JSONArray(selectedItems);
                        } catch (JSONException e){
                            e.getMessage();
                        }
                    }

                    Gson gson = new Gson();

                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(gson.toJson(productsModel.get(position)));
                        jsonObj.put("position", position);
                        jsonObj.put("quantity", quantity.get());
                        jsonObj.put("totalAmount", totalAmt);
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                    jsonArray.put(jsonObj);
                    editor.putString("selectedItems", jsonArray.toString());
                    editor.apply();
                } else {
                    selectedItem.set(0);
                    orderDetails.cartTotalAmount("0.00", 0, selectedItem.get(), position, fromWhatButton);
                    String selectedItems = sharedPreferences.getString("selectedItems", "");
//                    sharedPreferences.edit().remove("selectedItems").apply();
                    try {
                        JSONArray jsonArray2 = new JSONArray(selectedItems);
                        for (int i = 0; i < jsonArray2.length(); i++){
                            JSONObject jsonObject = jsonArray2.getJSONObject(i);
                            int itemPosition = jsonObject.getInt("position");
                            if (itemPosition == position){
                                jsonArray2.remove(i);
                                editor.putString("selectedItems", jsonArray2.toString());
                                editor.apply();
                            }
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } catch (Exception e){
            e.getMessage();
        }

        return convertView;
    }
}
