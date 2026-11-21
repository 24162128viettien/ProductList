package com.example.productlist;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Màn Giỏ hàng: đọc giỏ từ Firestore, xóa từng món, thanh toán thành đơn hàng. */
public class CartActivity extends AppCompatActivity {

    private final ArrayList<CartItem> items = new ArrayList<>();
    private CartAdapter adapter;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Thanh tiêu đề dùng chung, có mũi tên Quay lại
        UiUtils.setupToolbar(this, "Giỏ hàng", true);

        tvTotal = findViewById(R.id.tvTotal);
        ListView listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.tvEmpty));

        adapter = new CartAdapter(this, items, item ->
                UserData.cart().document(item.id).delete()
                        .addOnSuccessListener(v -> loadCart()));
        listView.setAdapter(adapter);

        findViewById(R.id.btnCheckout).setOnClickListener(v -> checkout());
        loadCart();
    }

    /** Đọc collection cart của người dùng và cập nhật tổng tiền. */
    private void loadCart() {
        UserData.cart().get().addOnSuccessListener(snapshot -> {
            items.clear();
            for (QueryDocumentSnapshot doc : snapshot) {
                CartItem c = new CartItem();
                c.id = doc.getId();
                c.name = doc.getString("name");
                Double p = doc.getDouble("price");
                c.price = p == null ? 0 : p;
                Long q = doc.getLong("quantity");
                c.quantity = q == null ? 1 : q.intValue();
                String img = doc.getString("image");
                int res = img == null ? 0
                        : getResources().getIdentifier(img, "drawable", getPackageName());
                c.image = res == 0 ? R.mipmap.ic_launcher : res;
                items.add(c);
            }
            adapter.notifyDataSetChanged();

            double total = 0;
            for (CartItem c : items) total += c.price * c.quantity;
            tvTotal.setText(String.format(Locale.US, "%,.0f đ", total));
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /** Thanh toán: tạo 1 đơn hàng trong orders và xóa các món trong cart (batch). */
    private void checkout() {
        if (items.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder sb = new StringBuilder();
        double total = 0;
        int count = 0;
        for (CartItem c : items) {
            sb.append(c.name).append(" x").append(c.quantity).append("\n");
            total += c.price * c.quantity;
            count += c.quantity;
        }

        Map<String, Object> order = new HashMap<>();
        order.put("summary", sb.toString().trim());
        order.put("total", total);
        order.put("count", count);
        order.put("createdAt", FieldValue.serverTimestamp());

        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        batch.set(UserData.orders().document(), order);
        for (CartItem c : items) batch.delete(UserData.cart().document(c.id));

        batch.commit().addOnSuccessListener(v -> {
            Toast.makeText(this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
            loadCart();
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}