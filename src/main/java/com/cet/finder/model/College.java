
package com.cet.finder.model;

public class College {

    private int collegeId;
    private String collegeName;
    private Branch branch;
    private String location;
    private String instituteCode;

    public College(int collegeId, String collegeName,
                   Branch branch, String location,
                   String instituteCode) {
        this.collegeId = collegeId;
        this.collegeName = collegeName;
        this.branch = branch;
        this.location = location;
        this.instituteCode = instituteCode;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public Branch getBranch() {
        return branch;
    }

    public String getLocation() {
        return location;
    }

    public String getInstituteCode() {
        return instituteCode;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "College{" +
                "collegeId=" + collegeId +
                ", collegeName='" + collegeName + '\'' +
                ", branch=" + branch +
                ", location='" + location + '\'' +
                ", instituteCode='" + instituteCode + '\'' +
                '}';
    }
}
