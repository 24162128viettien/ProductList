package com.example.productlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends ArrayAdapter<Order> {

    public OrderAdapter(Context context, List<Order> orders) {
        super(context, 0, orders);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_order, parent, false);
        }
        Order o = getItem(position);

        ((TextView) convertView.findViewById(R.id.tvOrderDate)).setText("Đơn hàng • " + o.date);
        ((TextView) convertView.findViewById(R.id.tvOrderItems)).setText(o.summary);
        ((TextView) convertView.findViewById(R.id.tvOrderTotal))
                .setText(String.format(Locale.US, "Tổng: %,.0f đ", o.total));
        return convertView;
    }
}