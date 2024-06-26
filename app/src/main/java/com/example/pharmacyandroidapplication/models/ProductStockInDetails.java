package com.example.pharmacyandroidapplication.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductStockInDetails {
    private String product_id;
    private String product_name;
    private String unit;
    private String lot_number;
    private String production_date;
    private String expiration_date;
    private int in_quantity;
    private int quantity_in_stock;
    private int unit_price;
    private String img;

    public ProductStockInDetails(String product_id, String product_name, String lot_number, String production_date, String expiration_date, int in_quantity, int quantity_in_stock, int unit_price, String unit, String img) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.unit = unit;
        this.lot_number = lot_number;
        this.production_date = production_date;
        this.expiration_date = expiration_date;
        this.in_quantity = in_quantity;
        this.quantity_in_stock = quantity_in_stock;
        this.unit_price = unit_price;
        this.img = img;
    }

    public String getProduction_date() {
        return production_date;
    }

    public void setProduction_date(String production_date) {
        this.production_date = production_date;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public int getIn_quantity() {
        return in_quantity;
    }

    public int getQuantity_in_stock() {
        return quantity_in_stock;
    }

    public void setQuantity_in_stock(int quantity_in_stock) {
        this.quantity_in_stock = quantity_in_stock;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getLot_number() {
        return lot_number;
    }

    public void setLot_number(String lot_number) {
        this.lot_number = lot_number;
    }


    public void setIn_quantity(int in_quantity) {
        this.in_quantity = in_quantity;
    }

    public int getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(int unit_price) {
        this.unit_price = unit_price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int totalPrice(){
        return this.in_quantity *this.unit_price;
    }
}
