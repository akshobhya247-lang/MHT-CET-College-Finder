package com.cet.finder.model;

public abstract class User{

    private int userId;
    private String name ;
    private String email ;
    private String passwordHash ;


    //Parameterized Constructor
    public User(int userId, String name , String email , String passwordHash) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters
    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Abstract method
    public abstract String getRole();


}

