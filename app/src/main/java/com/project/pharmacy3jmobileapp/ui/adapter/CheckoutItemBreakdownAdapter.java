package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.project.pharmacy3jmobileapp.model.ProductsModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CheckoutItemBreakdownAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ProductsModel> productsModel;

    private OrderDetails orderDetails;
    private SharedPreferences sharedPreferences;
    private String fromWhatScreen, fromWhatButton;
    double total;

    public CheckoutItemBreakdownAdapter(Context context, ArrayList<ProductsModel> productsModel, OrderDetails orderDetails, SharedPreferences sharedPref, String fromWhatScreen, String fromWhatButton) {
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_checkout_items, null, true);
        }
        DecimalFormat df = new DecimalFormat("#,###.00");

        TextView tvPrice, tvQuantity, tvSubtotalAmount;
        tvPrice = convertView.findViewById(R.id.tvPrice);
        tvQuantity = convertView.findViewById(R.id.tvQuantity);
        tvSubtotalAmount = convertView.findViewById(R.id.tvSubtotalAmount);
        tvPrice.setText("Php " + df.format(productsModel.get(position).getPrice()));
        tvQuantity.setText("x " + productsModel.get(position).getQuantity());
        tvSubtotalAmount.setText("Php " + productsModel.get(position).getTotalAmount());
        return convertView;
    }
}
