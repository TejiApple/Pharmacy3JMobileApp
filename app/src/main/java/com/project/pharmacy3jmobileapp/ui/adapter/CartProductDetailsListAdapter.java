package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.project.pharmacy3jmobileapp.model.OrderDetails;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CartProductDetailsListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ProductsModel> productsModel;

    private OrderDetails orderDetails;
    double total;

    public CartProductDetailsListAdapter(Context context, ArrayList<ProductsModel> productsModel, OrderDetails orderDetails) {
        this.context = context;
        this.productsModel = productsModel;
        this.orderDetails = orderDetails;
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
            tvBrandName = convertView.findViewById(R.id.tvProductNameInTheCart);
            tvPrice = convertView.findViewById(R.id.tvProductPriceInTheCart);
            tvQuantity = convertView.findViewById(R.id.tvProductQuantity);
            tvTotalAmount = convertView.findViewById(R.id.tvTotalAmount);

            DecimalFormat df = new DecimalFormat("#,###.00");

            String price = String.valueOf(productsModel.get(position).getPrice());
            tvBrandName.setText(productsModel.get(position).getBrandName());
            String formattedPrice = "Php " + df.format(Integer.parseInt(price));
            tvPrice.setText(formattedPrice);
            tvTotalAmount.setText(formattedPrice);
//            totalAmount.cartTotalAmount(formattedPrice);
//            imageView.setImageResource(R.drawable.icon_beauty_care);
            Picasso.get().load(productsModel.get(position).getImageUrl()).into(imageView);

            ImageButton btnAdd, btnSubtract;
            btnAdd = convertView.findViewById(R.id.btnIncreaseProductQuantity);
            btnSubtract = convertView.findViewById(R.id.btnDecreaseProductQuantity);
            tvQuantity.setText("1");
            AtomicInteger quantity = new AtomicInteger(Integer.parseInt(tvQuantity.getText().toString()));

            btnAdd.setOnClickListener(v -> {
                quantity.getAndIncrement();
                tvQuantity.setText(quantity.toString());

                total = Double.parseDouble(price) * Double.parseDouble(String.valueOf(quantity));
                String totalAmt = df.format(total);
                tvTotalAmount.setText("Php " + totalAmt);
                orderDetails.cartTotalAmount(totalAmt, quantity.get());

            });

            btnSubtract.setOnClickListener(v -> {
                quantity.getAndDecrement();
                if (quantity.get() < 1){
                    quantity.set(1);
                    tvQuantity.setText("1");
                } else {
                    tvQuantity.setText(quantity.toString());

                    String totalAmt = df.format(total - Double.parseDouble(price));
                    total = Double.parseDouble(totalAmt);
                    tvTotalAmount.setText("Php " + totalAmt);
                    orderDetails.cartTotalAmount(totalAmt, quantity.get());
                }
            });
        } catch (Exception e){
            e.getMessage();
        }

        return convertView;
    }
}
