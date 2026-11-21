package com.example.productlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class CartAdapter extends ArrayAdapter<CartItem> {

    public interface OnRemoveListener { void onRemove(CartItem item); }

    private final OnRemoveListener listener;

    public CartAdapter(Context context, List<CartItem> items, OnRemoveListener listener) {
        super(context, 0, items);
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_cart, parent, false);
        }
        CartItem c = getItem(position);

        ((ImageView) convertView.findViewById(R.id.imgCart)).setImageResource(c.image);
        ((TextView) convertView.findViewById(R.id.tvCartName)).setText(c.name);
        ((TextView) convertView.findViewById(R.id.tvCartPrice))
                .setText(String.format(Locale.US, "%,.0f đ", c.price));
        ((TextView) convertView.findViewById(R.id.tvCartQty))
                .setText("Số lượng: " + c.quantity);
        convertView.findViewById(R.id.btnRemove).setOnClickListener(v -> listener.onRemove(c));
        return convertView;
    }
}