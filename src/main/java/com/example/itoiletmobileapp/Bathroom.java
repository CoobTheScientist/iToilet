package com.example.itoiletmobileapp;

//class to hold bathrooms snd ratings
public class Bathroom {
    private int id;
    private String address;
    private String location;
    private String gender;
    private int overall;
    private int cleanliness;
    private int toiletPaper;
    private String handicap;
    private  String drying;
    private String babyStation;
    private boolean highlighted;

    public Bathroom(int id, String address, String location, String gender, int overall,
                    int cleanliness, int toiletPaper, String handicap, String drying, String babyStation) {
        this.id = id;
        this.address = address;
        this.location = location;
        this.gender = gender;
        this.overall = overall;
        this.cleanliness = cleanliness;
        this.toiletPaper = toiletPaper;
        this.handicap = handicap;
        this.drying = drying;
        this.babyStation = babyStation;
    }
    public int getId(){
        return id;
    }
    public String getAddress(){
        return address;
    }
    public String getLocation(){
        return location;
    }
    public String getGender(){
        return gender;
    }
    public int getOverall(){
        return overall;
    }
    public int getCleanliness(){
        return cleanliness;
    }
    public int getToiletPaper(){
        return toiletPaper;
    }
    public String getHandicap(){
        return handicap;
    }
    public String getDrying(){
        return drying;
    }
    public String getBabyStation(){
        return babyStation;
    }
    public void setOverall(int overall){
        this.overall = overall;
    }
    public void setCleanliness(int cleanliness){
        this.cleanliness = cleanliness;
    }
    public void setToiletPaper(int toiletPaper){
        this.toiletPaper = toiletPaper;
    }
    public void setHandicap(String handicap){
        this.handicap = handicap;
    }
    public void setDrying(String drying){
        this.drying = drying;
    }
    public void setBabyStation(String babyStation){
        this.babyStation = babyStation;
    }
    public boolean isHighlighted() {
        return highlighted;
    }
    public void setHighlighted(boolean highlighted){
        this.highlighted = highlighted;
    }
}
