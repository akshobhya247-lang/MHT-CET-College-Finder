package com.cet.finder.model;

public class Branch {

    private int branchId ;
    private String branchName ;
    private String branchCode;

    public Branch(int branchId , String branchName , String branchCode)
    {
        this.branchId = branchId;
        this.branchName = branchName;
        this.branchCode = branchCode ;
    }


    public int getBranchId(){
        return branchId;
    }


    public String getBranchName(){
        return branchName;
    }

    public void setBranchName(String branchName)
    {
        this.branchName = branchName;
    }

    @Override
    public String toString(){
        return branchName + " (" + branchCode + ")";
    }

    public String getBranchCode() {
        return branchCode;
    }

}