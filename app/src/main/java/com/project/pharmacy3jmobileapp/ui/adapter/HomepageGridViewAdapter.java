package com.project.pharmacy3jmobileapp.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.pharmacy3jmobileapp.R;
import com.project.pharmacy3jmobileapp.model.ProductsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomepageGridViewAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ProductsModel> productsModel;

    public HomepageGridViewAdapter(Context context, ArrayList<ProductsModel> productsModel) {
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
                convertView = inflater.inflate(R.layout.grid_products, null, true);
            }
            ImageView imageView = convertView.findViewById(R.id.selectedPhoto);
            TextView tvBrandName, tvPrice;
            tvBrandName = convertView.findViewById(R.id.tvBrandName);
            tvPrice = convertView.findViewById(R.id.tvPrice);

            String price = String.valueOf(productsModel.get(position).getPrice());
            tvBrandName.setText(productsModel.get(position).getBrandName());
            tvPrice.setText("Php " + price);
//            imageView.setImageResource(R.drawable.icon_beauty_care);
            Picasso.get().load(productsModel.get(position).getImageUrl()).into(imageView);
        } catch (Exception e){
            e.getMessage();
        }

        return convertView;
    }
}
