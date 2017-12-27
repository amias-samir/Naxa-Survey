package com.example.naxasurvay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.naxasurvay.mapbox.MapboxApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RED_DEVIL on 8/6/2017.
 */

public class Database_SaveForm extends ODKSQLiteOpenHelper {

    static String name = "naxasurvey.save";
    static int version = 1;

    public final static String ID_TABLE = "_id_table";
    public final static String TABLE_ID = "_table_id";
    public final static String TABLE_NAME = "_table_name";
    public final static String TABLE_DATE = "_table_date";
    public final static String TABLE_JSON = "_table_json";
    public final static String TABLE_STATUS = "_table_status";
    public final static String TABLE_GPS = "_table_gps";
    public final static String TABLE_PHOTO = "_table_photo";
    public final static String DELETE_FLAG = "_delete_flag";
    public final static String TABLE_MAIN = "_table_save";

    public final static String[] COLS_TABLE_MAIN = new String[]{ID_TABLE, TABLE_ID, TABLE_NAME, TABLE_DATE, TABLE_JSON, TABLE_GPS, TABLE_PHOTO, TABLE_STATUS, DELETE_FLAG};

    static String CREATE_TABLE_MAIN = "Create table if not exists " + TABLE_MAIN + "("
            + ID_TABLE + " INTEGER PRIMARY KEY AUTOINCREMENT ," + TABLE_ID + " Text not null ," + TABLE_NAME + " Text not null ,"
            + TABLE_DATE + " Text not null ," + TABLE_JSON + " Text not null ," + TABLE_GPS + " Text not null ," + TABLE_PHOTO + " Text not null ,"
            + TABLE_STATUS + " Text not null ," + DELETE_FLAG + " Text not null )";
    ;
    static final String DROP_TABLE_MAIN = "DROP TABLE IF EXISTS " + TABLE_MAIN + ";";

    SQLiteDatabase db = null;
    Context con;

    public Database_SaveForm(Context context) {
        super(MapboxApplication.extSdcard+MapboxApplication.mainFolder+MapboxApplication.dataFolder, name, null, version);
    }

    public void open() throws SQLException {
        db = this.getWritableDatabase();
    }

    public void close() {
        db.close();
    }


    public long insertIntoTable_Main(String[] list) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_ID , list[0]);
        contentValues.put(TABLE_NAME, list[1] );
        contentValues.put(TABLE_DATE, list[2]);
        contentValues.put(TABLE_JSON, list[3]);
        contentValues.put(TABLE_GPS, list[4]);
        contentValues.put(TABLE_PHOTO, list[5]);
        contentValues.put(TABLE_STATUS , list[6]);
        contentValues.put(DELETE_FLAG, list[7]);
        long id = db.insert(TABLE_MAIN, null, contentValues);
        return id;
    }

    public void dropRowNotSentForms(String DBid) {
        //Open the database
        SQLiteDatabase database = this.getWritableDatabase();
        Log.e("", "dropRowNotSentFormsID: "+ DBid );
        //Execute sql query to remove from database
        //NOTE: When removing by String in SQL, value must be enclosed with ''
        database.execSQL("DELETE FROM " + TABLE_MAIN + " WHERE " + ID_TABLE + "= '" + DBid + "'");
        //Close the database
        database.close();
    }

    public List<SavedFormParameters> getAllSaveForms() {
        ArrayList<SavedFormParameters> sentFormsDetailsAll = new ArrayList<SavedFormParameters>();

        String sql = "SELECT  * FROM " + TABLE_MAIN + " ORDER BY "+ ID_TABLE+" DESC" ;

        Cursor c = getReadableDatabase().rawQuery(sql, null);

        int totalCount = c.getCount();
        Log.e("SAVED FORMS", "getAllSaveForms: "+totalCount );

        if(totalCount > 0) {


                while (c.moveToNext()) {
                    SavedFormParameters savedFormParameters = new SavedFormParameters();
                    savedFormParameters.setDbId(c.getString(c.getColumnIndex(ID_TABLE)));
                    savedFormParameters.setFormId(c.getString(c.getColumnIndex(TABLE_ID)));
                    savedFormParameters.setFormName(c.getString(c.getColumnIndex(TABLE_NAME)));
                    savedFormParameters.setDate(c.getString(c.getColumnIndex(TABLE_DATE)));
                    savedFormParameters.setStatus(c.getString(c.getColumnIndex(TABLE_STATUS)));
                    savedFormParameters.setjSON(c.getString(c.getColumnIndex(TABLE_JSON)));
                    savedFormParameters.setPhoto(c.getString(c.getColumnIndex(TABLE_PHOTO)));
                    savedFormParameters.setGps(c.getString(c.getColumnIndex(TABLE_GPS)));
                    savedFormParameters.setDeletedStatus(c.getString(c.getColumnIndex(DELETE_FLAG)));


                    sentFormsDetailsAll.add(savedFormParameters);

                    Log.e("", "getNOT_SENT_FORMS: " + sentFormsDetailsAll.size());

//                }
                }
//            }

            c.close();
        }

        return sentFormsDetailsAll;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MAIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_MAIN);
    }


    public int getTotalRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_MAIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}
