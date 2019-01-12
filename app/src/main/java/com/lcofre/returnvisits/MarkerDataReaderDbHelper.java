package com.lcofre.returnvisits;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lcofre.returnvisits.MarkerDataReaderContract.MarkerDataEntry;

import java.util.ArrayList;
import java.util.List;

public class MarkerDataReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ReturnVisit.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MarkerDataEntry.TABLE_NAME + " (" +
                    MarkerDataEntry._ID + " INTEGER PRIMARY KEY," +
                    MarkerDataEntry.COLUMN_NAME_LAT + REAL_TYPE + COMMA_SEP +
                    MarkerDataEntry.COLUMN_NAME_LONG + REAL_TYPE + COMMA_SEP +
                    MarkerDataEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    MarkerDataEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    MarkerDataEntry.COLUMN_NAME_DETAILS + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MarkerDataEntry.TABLE_NAME;

    private static MarkerDataReaderDbHelper mDbHelper;
    private Cursor cursor;

    public static MarkerDataReaderDbHelper getInstance(Context context) {
        if(mDbHelper == null) {
            mDbHelper = new MarkerDataReaderDbHelper(context);
        }

        return mDbHelper;
    }

    private MarkerDataReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void insert(ReturnVisit returnVisit) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MarkerDataEntry.COLUMN_NAME_LAT, returnVisit.latitude);
        values.put(MarkerDataEntry.COLUMN_NAME_LONG, returnVisit.longitude);
        values.put(MarkerDataEntry.COLUMN_NAME_NAME, returnVisit.name);
        values.put(MarkerDataEntry.COLUMN_NAME_ADDRESS, returnVisit.address);
        values.put(MarkerDataEntry.COLUMN_NAME_DETAILS, returnVisit.details);

        db.insert(MarkerDataEntry.TABLE_NAME, null, values);
    }

    public void insert(List<ReturnVisit> returnVisitList) {
        for (ReturnVisit element : returnVisitList) {
            insert(element);
        }
    }

    public List<ReturnVisit> readAllFromDb(String defaultInfoMessage) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                MarkerDataEntry.COLUMN_NAME_LAT,
                MarkerDataEntry.COLUMN_NAME_LONG,
                MarkerDataEntry.COLUMN_NAME_NAME,
                MarkerDataEntry.COLUMN_NAME_ADDRESS,
                MarkerDataEntry.COLUMN_NAME_DETAILS
        };

        cursor = db.query(
                MarkerDataEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        List<ReturnVisit> returnVisitList = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                ReturnVisit returnVisit = new ReturnVisit();
                returnVisit.latitude = cursor.getDouble(get(MarkerDataEntry.COLUMN_NAME_LAT));
                returnVisit.longitude = cursor.getDouble(get(MarkerDataEntry.COLUMN_NAME_LONG));
                returnVisit.name = cursor.getString(get(MarkerDataEntry.COLUMN_NAME_NAME));
                returnVisit.address = cursor.getString(get(MarkerDataEntry.COLUMN_NAME_ADDRESS));
                returnVisit.details = cursor.getString(get(MarkerDataEntry.COLUMN_NAME_DETAILS));

                returnVisitList.add(returnVisit);
            }
        } finally {
            cursor.close();
        }

        return returnVisitList;
    }

    private int get(String column) {
        return cursor.getColumnIndexOrThrow(column);
    }

    public void deleteAll() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}