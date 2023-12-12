package com.example.itoiletmobileapp;

//class to hold only necessary user information
public class User {
    private int id;
    private String email;
    private boolean highlighted;

    public User(int id, String email){
        this.id = id;
        this.email = email;
    }

    public int getId(){
        return id;
    }
    public String getEmail(){
        return email;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public boolean isHighlighted() {
        return highlighted;
    }
    public void setHighlighted(boolean highlighted){
        this.highlighted = highlighted;
    }
}
