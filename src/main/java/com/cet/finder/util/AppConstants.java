package com.cet.finder.util;

public final class AppConstants {

    private AppConstants(){
        // prevents object creation
    }
    public static final String APPLICATION_NAME =
            "MHT-CET College Finder";

    public static final String DATABASE_NAME =
            "college_info_db";

    public static final int DEFAULT_CUTOFF_YEAR = 2025;

    public static final int DEFAULT_CAP_ROUND = 3;

    public static final int MAX_CSV_FILE_SIZE_MB = 50;

    public static final String CSV_DELIMITER = ",";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

}
