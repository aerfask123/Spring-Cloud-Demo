package com.java.springcould.gateway.demo.domain;

public class Product {
    private String name = "泡泡马特";
    private Integer stock;

    public Product() {
        stock = 100;
    }

    public Product(Integer stock) {
        this.stock = stock;
    }

    public static void nameOf(Product product) {
        System.out.println("Product name is " + product.name);
    }

    public Integer reduceStock() {
        return stock -= 1;
    }

    public boolean isEnough(int quantity) {
        return stock >= quantity;
    }

    public Integer reduceStock(Integer change) {
        return stock -= change;
    }

    public Integer reduceStock(Integer current, Integer change) {
        return current -= change;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", stock=" + stock +
                '}';
    }
}
