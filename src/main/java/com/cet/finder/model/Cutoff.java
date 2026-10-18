
package com.cet.finder.model;

public class Cutoff {

    private int cutoffId;
    private College college;
    private int year;
    private int capRound;
    private String category;
    private double percentile;
    private String quota;

    public Cutoff(int cutoffId, College college, int year,
                  int capRound, String category,
                  double percentile, String quota) {
        this.cutoffId = cutoffId;
        this.college = college;
        this.year = year;
        this.capRound = capRound;
        this.category = category;
        this.percentile = percentile;
        this.quota = quota;
    }

    public int getCutoffId() {
        return cutoffId;
    }

    public College getCollege() {
        return college;
    }

    public int getYear() {
        return year;
    }

    public int getCapRound() {
        return capRound;
    }

    public String getCategory() {
        return category;
    }

    public double getPercentile() {
        return percentile;
    }

    public String getQuota() {
        return quota;
    }

    @Override
    public String toString() {
        return "Cutoff{" +
                "college=" + (college != null
                ? college.getCollegeName()
                : "N/A") +
                ", year=" + year +
                ", capRound=" + capRound +
                ", category='" + category + '\'' +
                ", percentile=" + percentile +
                ", quota='" + quota + '\'' +
                '}';
    }
}
