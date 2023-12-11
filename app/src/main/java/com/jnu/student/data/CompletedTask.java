package com.jnu.student.data;

import java.io.Serializable;
import java.util.Date;

public class CompletedTask implements Serializable {
    private String name;
    private double price;
    private int imageResourceId;
    private Date completionDate;

    public CompletedTask(String name, double price, int imageResourceId, Date completionDate) {
        this.name = name;
        this.price = price;
        this.imageResourceId = imageResourceId;
        this.completionDate = completionDate;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }
}