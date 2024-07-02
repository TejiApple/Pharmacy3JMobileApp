package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.OrdersModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeliveryDetailsAdapter extends BaseAdapter {
    Context context;
    private ArrayList<OrdersModel> ordersModelArrayList;
    ArrayList<String> orderArray = new ArrayList<>();
    OrdersModel orders;
    DatabaseReference dbRef;
    String customerName;
    public DeliveryDetailsAdapter(Context context, ArrayList<OrdersModel> ordersModelArrayList, DatabaseReference dbRef, String customerName) {
        this.context = context;
        this.ordersModelArrayList = ordersModelArrayList;
        this.dbRef = dbRef;
        this.customerName = customerName;
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
            TextView tvOrderLabel, tvOrderDate, tvOrderItems, tvOrderItemPrice, tvOrderTotalAmount, tvOverallTotal, tvOrderStatus;
            Button btnOrderReceived = convertView.findViewById(R.id.btnOrderReceived);
            tvOrderDate = convertView.findViewById(R.id.tvOrderDate);
            tvOrderItems = convertView.findViewById(R.id.tvOrderItems);
            tvOrderItemPrice = convertView.findViewById(R.id.tvOrderItemPrice);
//            tvOrderTotalAmount = convertView.findViewById(R.id.tvOrderTotalAmount);
            tvOverallTotal = convertView.findViewById(R.id.tvOverallTotal);
            tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);
            tvOrderLabel = convertView.findViewById(R.id.tvLabelTotal);

            String date = ordersModelArrayList.get(0).getDateOrder();
            tvOrderDate.setText(date);
            if (position > 0){
                if (date.equals(ordersModelArrayList.get(position).getDateOrder())){
                    tvOrderDate.setVisibility(View.GONE);
                } else {
                    tvOrderDate.setText(ordersModelArrayList.get(position).getDateOrder());
                }
            } else if (position == 0) {
                tvOrderDate.setVisibility(View.VISIBLE);

            }
            tvOrderItems.setText(ordersModelArrayList.get(position).getQuantity() + " - " + ordersModelArrayList.get(position).getItemName());

            DecimalFormat df = new DecimalFormat("#,###.00");
            String formattedPrice = "P" + df.format(ordersModelArrayList.get(position).getUnitPrice());
            tvOrderItemPrice.setText(formattedPrice + " each.");

            String formattedTotalPay = "P" + df.format(ordersModelArrayList.get(position).getTotalPay());
            tvOverallTotal.setText(formattedTotalPay);

            String orderStatus = ordersModelArrayList.get(position).getStatus();
            if (orderStatus.equals("Pending")){
                btnOrderReceived.setVisibility(View.GONE);
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#b8ab00"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_yellow_border);
            } else if (orderStatus.equals("Delivered")){
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#25ba0b"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_green_border);
                btnOrderReceived.setVisibility(View.VISIBLE);
                btnOrderReceived.setOnClickListener(v -> {
                    Toast.makeText(context, "Order received", Toast.LENGTH_SHORT).show();
                    btnOrderReceived.setVisibility(View.GONE);
                    orders = new OrdersModel(
                            ordersModelArrayList.get(position).getAmount(),
                            ordersModelArrayList.get(position).getContactNumber(),
                            ordersModelArrayList.get(position).getDateDelivered(),
                            ordersModelArrayList.get(position).getDateOrder(),
                            ordersModelArrayList.get(position).getDiscount(),
                            ordersModelArrayList.get(position).getFullName(),
                            ordersModelArrayList.get(position).getItemName(),
                            ordersModelArrayList.get(position).getItemNumber(),
                            ordersModelArrayList.get(position).getDeliveryMode(),
                            ordersModelArrayList.get(position).getPaymentMode(),
                            ordersModelArrayList.get(position).getPrescription(),
                            ordersModelArrayList.get(position).getProductId(),
                            ordersModelArrayList.get(position).getQuantity(),
                            ordersModelArrayList.get(position).getShipAddress(),
                            "Order Received",
                            ordersModelArrayList.get(position).getTotalPay(),
                            ordersModelArrayList.get(position).getUnitPrice(),
                            ordersModelArrayList.get(position).getSeniorCitizenId()
                    );
                    Map<String, Object> orderNewValues = orders.toMap();
                    Map<String, Object> orderUpdates = new HashMap<>();
                    String key = ordersModelArrayList.get(position).getKey();
                    orderUpdates.put(key, orderNewValues);
                    dbRef.child("orders").updateChildren(orderUpdates);

                    tvOrderStatus.setText("Order Received");
                    tvOrderStatus.setTextColor(Color.parseColor("#25ba0b"));
                    tvOrderStatus.setBackgroundResource(R.drawable.rectangle_green_border);
                });
            } else if (orderStatus.contains("Order Received")){
                btnOrderReceived.setVisibility(View.GONE);
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#25ba0b"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_green_border);
            } else if (orderStatus.contains("Cancel")){
                btnOrderReceived.setVisibility(View.GONE);
                tvOrderStatus.setText(orderStatus);
                tvOrderStatus.setTextColor(Color.parseColor("#C50404"));
                tvOrderStatus.setBackgroundResource(R.drawable.rectangle_red_border);
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
