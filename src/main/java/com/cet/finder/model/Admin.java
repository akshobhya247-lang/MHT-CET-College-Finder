package com.cet.finder.model;

public class Admin extends User {

    private String adminCode ;

    public Admin(int userId , String name , String email , String passwordHash , String adminCode){

        super(userId , name , email , passwordHash);

        this.adminCode = adminCode ;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode){
        this.adminCode = adminCode;
    }

    @Override
    public String getRole()
    {
        return "ADMIN";
    }

}