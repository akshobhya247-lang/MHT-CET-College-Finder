package com.cet.finder.model;

public class Student extends User {

    private double percentile;
    private String category;

    public Student(int userId, String name, String email,
                   String passwordHash, double percentile,
                   String category) {

        super(userId, name, email, passwordHash);

        this.percentile = percentile;
        this.category = category;
    }

    public double getPercentile() {
        return percentile;
    }

    public void setPercentile(double percentile) {
        this.percentile = percentile;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getRole() {
        return "STUDENT";
    }
}