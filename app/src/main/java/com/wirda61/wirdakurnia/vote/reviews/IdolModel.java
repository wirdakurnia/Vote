package com.wirda61.wirdakurnia.vote.reviews;

public class IdolModel {
    private String idIdol;
    private int totalVoters;
    private double totalRating;
    private int star1;
    private int star2;
    private int star3;
    private int star4;
    private int star5;
    private String nameIdol;

    public IdolModel(int totalVoters, double totalRating, int star1, int star2, int star3, int star4, int star5, String nameIdol) {
        this.totalVoters = totalVoters;
        this.totalRating = totalRating;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;
        this.nameIdol = nameIdol;
    }

    public IdolModel() {
    }

    public String getNameIdol() {
        return nameIdol;
    }

    public void setNameIdol(String nameIdol) {
        this.nameIdol = nameIdol;
    }

    public String getIdIdol() {
        return idIdol;
    }

    public void setIdIdol (String idIdol) {
        this.idIdol = idIdol;
    }

    public int getTotalVoters() {
        return totalVoters;
    }

    public void setTotalVoters(int totalVoters) {
        this.totalVoters = totalVoters;
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public int getStar1() {
        return star1;
    }

    public void setStar1(int star1) {
        this.star1 = star1;
    }

    public int getStar2() {
        return star2;
    }

    public void setStar2(int star2) {
        this.star2 = star2;
    }

    public int getStar3() {
        return star3;
    }

    public void setStar3(int star3) {
        this.star3 = star3;
    }

    public int getStar4() {
        return star4;
    }

    public void setStar4(int star4) {
        this.star4 = star4;
    }

    public int getStar5() {
        return star5;
    }

    public void setStar5(int star5) {
        this.star5 = star5;
    }
}
