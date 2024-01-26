package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrdersModel;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DeliveryDetailsAdapter extends BaseAdapter {
    Context context;
    private ArrayList<OrdersModel> ordersModelArrayList;

    public DeliveryDetailsAdapter(Context context, ArrayList<OrdersModel> ordersModelArrayList) {
        this.context = context;
        this.ordersModelArrayList = ordersModelArrayList;
    }

    @Override
    public int getCount() {
        return ordersModelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return ordersModelArrayList.get(position);
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
                convertView = inflater.inflate(R.layout.listview_delivery, null, true);
            }
            TextView tvOrderNumber, tvOrderDate, tvOrderItems, tvOrderItemPrice, tvOrderTotalAmount, tvOverallTotal, tvOrderStatus;
            tvOrderDate = convertView.findViewById(R.id.tvOrderDate);
            tvOrderItems = convertView.findViewById(R.id.tvOrderItems);
            tvOrderItemPrice = convertView.findViewById(R.id.tvOrderItemPrice);
            tvOrderTotalAmount = convertView.findViewById(R.id.tvOrderTotalAmount);
            tvOverallTotal = convertView.findViewById(R.id.tvOverallTotal);
            tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);

            tvOrderDate.setText(ordersModelArrayList.get(position).getDateOrder());
            tvOrderItems.setText(ordersModelArrayList.get(position).getQuantity() + " - " + ordersModelArrayList.get(position).getItemName());

            DecimalFormat df = new DecimalFormat("#,###.00");
            String formattedPrice = "Php " + df.format(ordersModelArrayList.get(position).getUnitPrice());
            tvOrderItemPrice.setText(formattedPrice + " each.");

            String formattedTotalPay = "Php " + df.format(ordersModelArrayList.get(position).getTotalPay());
            tvOrderTotalAmount.setText("Total \n" + formattedTotalPay);
            tvOverallTotal.setText(formattedTotalPay);

            String orderStatus = ordersModelArrayList.get(position).getStatus();
            if (orderStatus.equals("Pending")){
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#b8ab00"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_yellow_border);
            } else if (orderStatus.equals("Delivered")){
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#25ba0b"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_green_border);
            } else {
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#13548A"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_blue_border);
            }
        } catch (Exception e){
            e.getMessage();
        }
        return convertView;
    }
}
