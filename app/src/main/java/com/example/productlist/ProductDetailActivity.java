package com.example.productlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.LinearLayout;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        UiUtils.setupToolbar(this, "Chi tiết sản phẩm", true);

        Product product = (Product) getIntent().getSerializableExtra("product");

        ((TextView) findViewById(R.id.tvDetailName)).setText(product.getName());
        ((TextView) findViewById(R.id.tvDetailPrice))
                .setText(String.format("%,.0f đ", product.getPrice()));
        // Thư viện ảnh: vuốt ngang xem nhiều ảnh, chấm tròn bên dưới chỉ trang hiện tại
        ViewPager2 pager = findViewById(R.id.pagerImages);
        pager.setAdapter(new ImagePagerAdapter(product.getImages()));
        new TabLayoutMediator(findViewById(R.id.tabDots), pager, (tab, position) -> { }).attach();

        // Danh sách thông số: nhãn + giá trị (bỏ qua dòng không có dữ liệu)
        LinearLayout container = findViewById(R.id.specContainer);
        String[][] specs = {
                {"Hiệu năng", product.getCpu()},
                {"RAM", product.getRam()},
                {"Bộ nhớ", product.getStorage()},
                {"Màn hình", product.getScreen()},
                {"Pin", product.getBattery()}
        };
        for (String[] s : specs) {
            if (s[1] == null || s[1].isEmpty()) continue;
            View row = getLayoutInflater().inflate(R.layout.item_spec, container, false);
            ((TextView) row.findViewById(R.id.tvSpecLabel)).setText(s[0]);
            ((TextView) row.findViewById(R.id.tvSpecValue)).setText(s[1]);
            container.addView(row);
        }

        // Mô tả
        String desc = product.getDescription();
        ((TextView) findViewById(R.id.tvDetailDesc))
                .setText(desc == null || desc.isEmpty() ? "Chưa có mô tả" : desc);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");

        Button btnBuy = findViewById(R.id.btnBuy);
        btnBuy.setOnClickListener(v -> {
            Map<String, Object> item = new HashMap<>();
            item.put("name", product.getName());
            item.put("price", product.getPrice());
            item.put("image", getResources().getResourceEntryName(product.getImage()));
            item.put("quantity", FieldValue.increment(1));   // đã có thì cộng thêm 1

            UserData.cart().document(product.getName())
                    .set(item, SetOptions.merge())
                    .addOnSuccessListener(x -> Snackbar.make(findViewById(android.R.id.content),
                            username + " đã thêm " + product.getName() + " vào giỏ hàng",
                            Snackbar.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this,
                            "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Chia sẻ");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Toast.makeText(this, "Đã chia sẻ sản phẩm", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}