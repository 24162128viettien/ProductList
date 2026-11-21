package com.example.productlist;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/** Màn Lịch sử mua hàng: đọc collection orders từ Firestore, đơn mới nhất lên đầu. */
public class OrderHistoryActivity extends AppCompatActivity {

    private final ArrayList<Order> orders = new ArrayList<>();
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // Thanh tiêu đề dùng chung, có mũi tên Quay lại
        UiUtils.setupToolbar(this, "Lịch sử mua hàng", true);

        // Danh sách đơn hàng; khi rỗng tự hiện "Chưa có đơn hàng nào"
        ListView listView = findViewById(R.id.listView);
        listView.setEmptyView(findViewById(R.id.tvEmpty));
        adapter = new OrderAdapter(this, orders);
        listView.setAdapter(adapter);

        loadOrders();
    }

    /** Đọc orders của người dùng, sắp xếp theo thời gian giảm dần rồi vẽ lại danh sách. */
    private void loadOrders() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        UserData.orders().orderBy("createdAt", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(snapshot -> {
                    orders.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Order o = new Order();
                        Timestamp t = doc.getTimestamp("createdAt");
                        o.date = t == null ? "" : fmt.format(t.toDate());
                        o.summary = doc.getString("summary");
                        Double total = doc.getDouble("total");
                        o.total = total == null ? 0 : total;
                        Long count = doc.getLong("count");
                        o.count = count == null ? 0 : count.intValue();
                        orders.add(o);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}