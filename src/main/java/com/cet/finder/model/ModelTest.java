
package com.cet.finder.model;

public class ModelTest {

    public static void main(String[] args) {

        Branch branch = new Branch(
                1,
                "Computer Science and Engineering",
                "CSE"
        );

        College college = new College(
                101,
                "Sample Engineering College",
                branch,
                "Pune",
                "1002"
        );

        Cutoff cutoff = new Cutoff(
                1,
                college,
                2025,
                3,
                "OPEN",
                92.5,
                "GOPEN"
        );

        System.out.println("===== BRANCH DETAILS =====");
        System.out.println("Branch ID: " + branch.getBranchId());
        System.out.println("Branch Name: " + branch.getBranchName());
        System.out.println("Branch Code: " + branch.getBranchCode());

        System.out.println("\n===== COLLEGE DETAILS =====");
        System.out.println("College ID: " + college.getCollegeId());
        System.out.println("College Name: " + college.getCollegeName());
        System.out.println("Location: " + college.getLocation());
        System.out.println("Institute Code: " + college.getInstituteCode());

        System.out.println("\n===== CUTOFF DETAILS =====");
        System.out.println(cutoff);

    }
}
