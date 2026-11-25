package com.example.productlist;

import java.io.Serializable;

public class Product implements Serializable {
    /** Số ảnh tối thiểu mỗi sản phẩm phải có. */
    public static final int MIN_IMAGES = 2;

    private String name;
    private double price;
    private int[] images;   // luôn có ít nhất MIN_IMAGES phần tử
    private String cpu, ram, storage, screen, battery, description;

    public Product(String name, double price, int image) {
        this(name, price, new int[]{image});
    }

    public Product(String name, double price, int[] images) {
        this.name = name;
        this.price = price;
        this.images = ensureMinImages(images);
    }

    public Product(String name, double price, int image, String cpu, String ram,
                   String storage, String screen, String battery, String description) {
        this(name, price, new int[]{image}, cpu, ram, storage, screen, battery, description);
    }

    public Product(String name, double price, int[] images, String cpu, String ram,
                   String storage, String screen, String battery, String description) {
        this(name, price, images);
        this.cpu = cpu;
        this.ram = ram;
        this.storage = storage;
        this.screen = screen;
        this.battery = battery;
        this.description = description;
    }

    /** Nếu thiếu ảnh thì lặp lại ảnh cuối (hoặc ảnh mặc định) cho đủ MIN_IMAGES. */
    private static int[] ensureMinImages(int[] src) {
        if (src == null || src.length == 0) src = new int[]{R.mipmap.ic_launcher};
        if (src.length >= MIN_IMAGES) return src;
        int[] out = new int[MIN_IMAGES];
        for (int i = 0; i < MIN_IMAGES; i++) out[i] = src[Math.min(i, src.length - 1)];
        return out;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    /** Ảnh đại diện (ảnh đầu) cho danh sách và giỏ hàng. */
    public int getImage() { return images[0]; }
    public int[] getImages() { return images; }
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getStorage() { return storage; }
    public String getScreen() { return screen; }
    public String getBattery() { return battery; }
    public String getDescription() { return description; }
}