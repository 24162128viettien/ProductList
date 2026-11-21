package com.example.productlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/** Màn Thông tin tài khoản: tên đăng nhập, email, số đơn đã đặt và nút Đăng xuất. */
public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        UiUtils.setupToolbar(this, "Tài khoản", true);

        // username đọc từ SharedPreferences, email lấy từ Firebase Auth
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        ((TextView) findViewById(R.id.tvAccName)).setText(username);

        LinearLayout container = findViewById(R.id.infoContainer);
        addRow(container, "Tên đăng nhập", username);
        addRow(container, "Email", user != null && user.getEmail() != null ? user.getEmail() : "");
        addRow(container, "Trạng thái", "Đang đăng nhập");
        TextView tvOrders = addRow(container, "Đơn đã đặt", "...");

        // Đếm số đơn trong Firestore
        if (user != null) {
            UserData.orders().get()
                    .addOnSuccessListener(s -> tvOrders.setText(s.size() + " đơn"))
                    .addOnFailureListener(e -> tvOrders.setText("Không tải được"));
        }

        findViewById(R.id.btnLogout).setOnClickListener(v -> logout(prefs));
    }

    /** Thêm một dòng "nhãn - giá trị" và trả về TextView giá trị để cập nhật sau. */
    private TextView addRow(LinearLayout container, String label, String value) {
        View row = getLayoutInflater().inflate(R.layout.item_spec, container, false);
        ((TextView) row.findViewById(R.id.tvSpecLabel)).setText(label);
        TextView tvValue = row.findViewById(R.id.tvSpecValue);
        tvValue.setText(value);
        container.addView(row);
        return tvValue;
    }

    /** Đăng xuất Firebase, xóa username, về Login và xóa các màn cũ khỏi back stack. */
    private void logout(SharedPreferences prefs) {
        FirebaseAuth.getInstance().signOut();
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}