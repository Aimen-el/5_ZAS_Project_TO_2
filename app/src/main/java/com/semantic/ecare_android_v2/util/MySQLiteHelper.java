package com.semantic.ecare_android_v2.util;

import com.semantic.ecare_android_v2.object.Measure;
import com.semantic.ecare_android_v2.object.Patient;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper  extends SQLiteOpenHelper {

	public MySQLiteHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);  
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // nothing for me
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing for me
    }
    
    public long addMeasure(Measure m, Patient p){
        
    	long result;
        SQLiteDatabase db = this.getWritableDatabase();
 
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        
        values.put("id_patient",p.getUid());
        values.put("sensor",m.getSensor());
        values.put("date",m.getDate().getTime());
        values.put("valeur",m.getValue());
        values.put("synchronized",0);
        values.put("note","new");
        values.put("noteDate",m.getDate().getTime());
 
        // 3. insert
        result = db.insert(Constants.TABLE_MESURE, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
 
        // 4. close
        db.close(); 
        
        return result;
    }
   
}
