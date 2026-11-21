package com.example.productlist;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class UiUtils {

    /** Gắn MaterialToolbar làm thanh tiêu đề: đặt tên, bật/tắt mũi tên Quay lại. */
    public static void setupToolbar(AppCompatActivity activity, String title, boolean showBack) {
        MaterialToolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(showBack);
        if (showBack) {
            toolbar.setNavigationOnClickListener(v -> activity.finish());
        }
    }
}