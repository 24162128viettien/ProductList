package com.example.productlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** Adapter cho ViewPager2: mỗi trang là một ảnh sản phẩm. */
public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.VH> {

    private final int[] images;

    public ImagePagerAdapter(int[] images) {
        this.images = images;
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView img;
        VH(View v) {
            super(v);
            img = v.findViewById(R.id.imgGallery);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.img.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }
}