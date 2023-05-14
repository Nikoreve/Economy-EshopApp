package com.example.eshopapplication;

public class ProductInfo {
    private String productName;
    private String category;
    private double price;
    private int stock;
    private byte[] image;
    private String supplier;


    public ProductInfo(String productName, String category, double price, int stock, byte[] image, String supplier) {
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.supplier = supplier;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}
