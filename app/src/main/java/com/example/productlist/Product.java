package com.example.productlist;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private double price;
    private int image;
    private String cpu, ram, storage, screen, battery, description;

    public Product(String name, double price, int image) {
        this.name = name;
        this.price = price;
        this.image = image;
    }

    public Product(String name, double price, int image, String cpu, String ram,
                   String storage, String screen, String battery, String description) {
        this(name, price, image);
        this.cpu = cpu;
        this.ram = ram;
        this.storage = storage;
        this.screen = screen;
        this.battery = battery;
        this.description = description;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getImage() { return image; }
    public String getCpu() { return cpu; }
    public String getRam() { return ram; }
    public String getStorage() { return storage; }
    public String getScreen() { return screen; }
    public String getBattery() { return battery; }
    public String getDescription() { return description; }
}