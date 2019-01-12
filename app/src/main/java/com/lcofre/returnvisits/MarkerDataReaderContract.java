package com.lcofre.returnvisits;

import android.provider.BaseColumns;

public final class MarkerDataReaderContract {
    private MarkerDataReaderContract(){}

    public static class MarkerDataEntry implements BaseColumns {
        public static final String TABLE_NAME = "ReturnVisit";
        public static final String COLUMN_NAME_LAT = "latitude";
        public static final String COLUMN_NAME_LONG = "longitude";
        public static final String COLUMN_NAME_NAME = "householder_name";
        public static final String COLUMN_NAME_ADDRESS = "address";
        public static final String COLUMN_NAME_DETAILS = "other_details";
    }
}
