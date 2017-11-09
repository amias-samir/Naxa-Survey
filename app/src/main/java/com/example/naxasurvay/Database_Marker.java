package com.example.naxasurvay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.R.attr.id;
import static android.R.attr.value;

/**
 * Created by RED_DEVIL on 8/21/2017.
 */

public class Database_Marker extends SQLiteOpenHelper {
    static String DATABASE_NAME = "naxasurvey.marker";
    static int DATABASE_VERSION = 1;
    long id;
    long result = 0;

//    SQLiteDatabase db = null;

    public final static String TABLE_MARKER = "_table_name";
    public final static String ID_TABLE = "_id_table";
    public final static String HOUSE_CODE = "_house_code";
    public final static String LATITUDE = "_table_lat";
    public final static String LONGITUDE = "_table_long";
    public final static String STATUS = "_table_status";
    public final static String PLACE_NAME = "_table_place_name";

    public Database_Marker(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_MARKER + " ( " + makeMarkTable() + " );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKER);

        onCreate(db);
    }

    public String makeMarkTable() {
        return ID_TABLE + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + HOUSE_CODE + " TEXT,"
                + LATITUDE + " TEXT,"
                + LONGITUDE + " TEXT,"
                + STATUS + " TEXT,"
                + PLACE_NAME + " TEXT";
    }

    public void insertIntoMarker(String code, double lat, double longi, String status, String location_name) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Log.d("Pradip", "insertIntoMarker : " + code);
        Log.d("Pradip", "insertIntoMarker : " + lat);
        Log.d("Pradip", "insertIntoMarker : " + longi);
        Log.d("Pradip", "insertIntoMarker : " + status);
        Log.d("Pradip", "insertIntoMarker : " + location_name);


        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(HOUSE_CODE, code);
            contentValues.put(LATITUDE, lat);
            contentValues.put(LONGITUDE, longi);
            contentValues.put(STATUS, status);
            contentValues.put(PLACE_NAME, location_name);

            result = sqLiteDatabase.insert(TABLE_MARKER, null, contentValues);
            Log.d("RESULT", "insertIntoMarker: " + result);

        } catch (Exception e) {
            Log.d("SUSAN", "Failed to save: " + e.toString());
        } finally {
            sqLiteDatabase.close();
        }


    }

    public ArrayList<Mapinfo> getUnsavedata() {
        ArrayList<Mapinfo> array = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String getdata = "SELECT * FROM " + TABLE_MARKER + " WHERE " + STATUS + " = '0'";
        Cursor cursor = db.rawQuery(getdata, null);

//        db.query(false, TABLE_MARKER, null, STATUS + "=?", new String[]{"0"}, null, null, null, null);

        Log.d("abc", "cu" + cursor.getCount());

        if (cursor.moveToNext()) {
            do {
                Mapinfo info = new Mapinfo();

                info.id = cursor.getString(cursor.getColumnIndex(ID_TABLE));
                info.houseCode = cursor.getString(cursor.getColumnIndex(HOUSE_CODE));
                info.latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
                info.longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
                info.status = cursor.getString(cursor.getColumnIndex(STATUS));
                info.placeName = cursor.getString(cursor.getColumnIndex(PLACE_NAME));

                array.add(info);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return array;
    }

    public ArrayList<Mapinfo> getsavedata() {
        ArrayList<Mapinfo> array = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String getdata = "SELECT * FROM " + TABLE_MARKER + " WHERE " + STATUS + " = '1'";
        Cursor cursor = db.rawQuery(getdata, null);


        Log.d("abc", "cu" + cursor.getCount());

        if (cursor.moveToNext()) {
            do {
                Mapinfo info = new Mapinfo();

                info.id = cursor.getString(cursor.getColumnIndex(ID_TABLE));
                info.houseCode = cursor.getString(cursor.getColumnIndex(HOUSE_CODE));
                info.latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
                info.longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
                info.status = cursor.getString(cursor.getColumnIndex(STATUS));
                info.placeName = cursor.getString(cursor.getColumnIndex(PLACE_NAME));

                array.add(info);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return array;
    }

    public ArrayList<Mapinfo> getsenddata() {
        ArrayList<Mapinfo> array = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String getdata = "SELECT * FROM " + TABLE_MARKER + " WHERE " + STATUS + " = '2'";
        Cursor cursor = db.rawQuery(getdata, null);


        Log.d("abc", "cu" + cursor.getCount());

        if (cursor.moveToNext()) {
            do {
                Mapinfo info = new Mapinfo();

                info.id = cursor.getString(cursor.getColumnIndex(ID_TABLE));
                info.houseCode = cursor.getString(cursor.getColumnIndex(HOUSE_CODE));
                info.latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
                info.longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
                info.status = cursor.getString(cursor.getColumnIndex(STATUS));
                info.placeName = cursor.getString(cursor.getColumnIndex(PLACE_NAME));

                array.add(info);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return array;
    }

    public ArrayList<Mapinfo> getSurveyData() {
        ArrayList<Mapinfo> array = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String getdata = "SELECT * FROM " + TABLE_MARKER + " WHERE " + STATUS + " = '4'";
        Cursor cursor = db.rawQuery(getdata, null);


        Log.d("abc", "cu" + cursor.getCount());

        if (cursor.moveToNext()) {
            do {
                Mapinfo info = new Mapinfo();

                info.id = cursor.getString(cursor.getColumnIndex(ID_TABLE));
                info.houseCode = cursor.getString(cursor.getColumnIndex(HOUSE_CODE));
                info.latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
                info.longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
                info.status = cursor.getString(cursor.getColumnIndex(STATUS));
                info.placeName = cursor.getString(cursor.getColumnIndex(PLACE_NAME));

                array.add(info);
            } while (cursor.moveToNext());
        }
        db.close();
        cursor.close();
        return array;
    }

    public int getProfilesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MARKER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
//        Log.d("Pradip Ach", "getProfilesCount" + cnt);
        cursor.close();
        return cnt;

//        long numRows = DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + TABLE_MARKER, null);
    }

    public void replaceSave(String houseHoldIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("replaceSave", "replace save : " + houseHoldIdValue);
        //String replacesend = "UPDATE " + TABLE_MARKER + " SET " + STATUS + " = '1' WHERE " + HOUSE_CODE + " = '" + houseHoldIdValue + "'";
//        db.query(false, TABLE_MARKER, null, STATUS + "=?", new String[]{"0"}, null, null, null, null);


        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS,1);

        int i = db.update(TABLE_MARKER,contentValues,HOUSE_CODE+"=?",new String[]{houseHoldIdValue});
        //db.rawQuery(replacesend, null);
        Log.d("replaceSave", " replacesend : " + i);
    }


    public void replaceSend(String houseHoldIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("replaceSave", " check api update : " + houseHoldIdValue);
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS,2);

        int i = db.update(TABLE_MARKER,contentValues,HOUSE_CODE+"=?",new String[]{houseHoldIdValue});

    }

    public void forSurvey(String houseHoldIdValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("replaceSave", "for Survey : " + houseHoldIdValue);
        ContentValues contentValues = new ContentValues();
        contentValues.put(STATUS,4);

        int i = db.update(TABLE_MARKER,contentValues,HOUSE_CODE+"=?",new String[]{houseHoldIdValue});

    }

//    public void open() throws SQLException {
//        db = this.getWritableDatabase();
//    }
//
//    public void close() {
//        db.close();
//    }

    public boolean doesDataExistOrNot(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_MARKER, null, HOUSE_CODE + "=?", new String[]{code}, null, null, null);

        if (cursor.getCount() > 0) {
            db.close();
            cursor.close();
            return true;
        }
        db.close();
        cursor.close();
        return false;
    }
}
