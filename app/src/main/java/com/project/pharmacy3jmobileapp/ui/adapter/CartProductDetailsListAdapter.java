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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class CartProductDetailsListAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ProductsModel> productsModel;

    public CartProductDetailsListAdapter(Context context, ArrayList<ProductsModel> productsModel) {
        this.context = context;
        this.productsModel = productsModel;
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
            TextView tvBrandName, tvPrice, tvQuantity;
            tvBrandName = convertView.findViewById(R.id.tvProductNameInTheCart);
            tvPrice = convertView.findViewById(R.id.tvProductPriceInTheCart);
            tvQuantity = convertView.findViewById(R.id.tvProductQuantity);

            String price = String.valueOf(productsModel.get(position).getPrice());
            tvBrandName.setText(productsModel.get(position).getBrandName());
            tvPrice.setText(price);
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
            });

            btnSubtract.setOnClickListener(v -> {
                quantity.getAndDecrement();
                if (quantity.get() < 1){
                    tvQuantity.setText("0");
                } else {
                    tvQuantity.setText(quantity.toString());
                }
            });
        } catch (Exception e){
            e.getMessage();
        }

        return convertView;
    }
}
