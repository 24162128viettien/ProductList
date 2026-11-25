package com.example.productlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    private final ArrayList<Product> products = new ArrayList<>(); // dữ liệu hiển thị trên ListView
    private ProductAdapter adapter;                                // cầu nối giữa dữ liệu và ListView
    private ListView listView;
    private SharedPreferences prefs;                               // file "MyPrefs", chứa username đã lưu lúc đăng nhập

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UiUtils.setupToolbar(this, "Cửa hàng của tôi", false);

        // Lấy username từ SharedPreferences để chào người dùng
        prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        ((TextView) findViewById(R.id.tvWelcome))
                .setText("Xin chào, " + prefs.getString("username", ""));

        setupList();
        setupBottomNav();
        loadProducts();
    }

    /**
     * gắn adapter vào ListView, khi bấm một sản phẩm:
     * mở màn Detail, truyền giá trị qua Intent.
     */
    private void setupList() {
        listView = findViewById(R.id.listView);
        adapter = new ProductAdapter(this, products);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product product = products.get(position);

            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });
    }

    /**
     * xử lý thanh menu dưới cùng.
     * trang chủ: cuộn danh sách về đầu và hiện thông báo.
     * đăng xuất: gọi logout().
     */
    private void setupBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                listView.smoothScrollToPosition(0);
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return false;   // không đổi mục đang chọn
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(this, OrderHistoryActivity.class));
                return false;
            } else if (id == R.id.nav_logout) {
                logout();
            }
            return true;   // Tìm kiếm: chưa có chức năng
        });
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    /**
     * đọc products từ Firestore.
     * hiện thanh tiến trình khi bắt đầu, ẩn đi khi tải xong hoặc lỗi.
     */
    private void loadProducts() {
        View progress = findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        FirebaseFirestore.getInstance().collection("products").get()
                .addOnSuccessListener(snapshot -> {
                    progress.setVisibility(View.GONE);   // tải xong: ẩn thanh tiến trình
                    products.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        int[] imgs = resolveImages(doc);
                        Double price = doc.getDouble("price");
                        products.add(new Product(doc.getString("name"),
                                price == null ? 0 : price,
                                imgs,
                                doc.getString("cpu"), doc.getString("ram"), doc.getString("storage"),
                                doc.getString("screen"), doc.getString("battery"), doc.getString("description")));
                    }
                    adapter.notifyDataSetChanged();
                    if (products.isEmpty()) {
                        Toast.makeText(this, "Chưa có sản phẩm trên Firebase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);   // lỗi: cũng phải ẩn
                    Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }


    /**
     * Lấy danh sách ảnh của sản phẩm (Product sẽ tự bù cho đủ ít nhất 2 ảnh).
     * Ưu tiên mảng "images" trên Firestore; nếu không có thì dùng "image" rồi tự dò thêm
     * các ảnh cùng tên có hậu tố _2, _3, ... trong drawable (vd: airpods_pro, airpods_pro_2).
     */
    private int[] resolveImages(QueryDocumentSnapshot doc) {
        ArrayList<String> names = new ArrayList<>();
        Object raw = doc.get("images");
        if (raw instanceof java.util.List) {
            for (Object o : (java.util.List<?>) raw) if (o != null) names.add(o.toString());
        }
        String main = doc.getString("image");
        if (names.isEmpty() && main != null) {
            names.add(main);
            for (int i = 2; i <= 10; i++) {
                if (getResources().getIdentifier(main + "_" + i, "drawable", getPackageName()) == 0) break;
                names.add(main + "_" + i);
            }
        }
        ArrayList<Integer> ids = new ArrayList<>();
        for (String n : names) {
            int res = getResources().getIdentifier(n, "drawable", getPackageName());
            if (res != 0) ids.add(res);
        }
        int[] out = new int[ids.size()];
        for (int i = 0; i < out.length; i++) out[i] = ids.get(i);
        return out;
    }

    /**
     * đăng xuất: thoát Firebase Auth, xóa username trong SharedPreferences,
     * mở lại màn Login và xóa các màn cũ khỏi back stack.
     */
    private void logout() {
        FirebaseAuth.getInstance().signOut();
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }

    /**nạp icon tài khoản từ res/menu/main_menu.xml lên thanh tiêu đề. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /** Khi bấm icon tài khoản: mở màn Thông tin tài khoản. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_account) {
            startActivity(new Intent(this, AccountActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}