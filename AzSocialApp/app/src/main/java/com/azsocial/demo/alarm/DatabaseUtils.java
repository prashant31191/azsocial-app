package com.azsocial.demo.alarm;

/**
 * Created by prashant.chovatiya on 1/30/2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.azsocial.App;

import java.util.Hashtable;

/**
 * Created by Admin on 8/31/2016.
 */

public class DatabaseUtils extends SQLiteOpenHelper {
    private  static Context myContext,context;
    private SQLiteDatabase DataBase;



    //DATABASE NAME
    private static  int VERSION = 2;

    private static String FOLDER_NAME="app_name";
    //public static String DATABASE_NAME = App.strFolderNamePath+ File.separator +"Database"+File.separator+"app_name.db";
    public static String DATABASE_NAME ="app_name.db";

    private static String TABLE_NAME="";

    //TABLE NAME
    public static String TABLE_NOTIFICATION="table_alarm";

    Hashtable<String, String> pair = new Hashtable<String, String>();




    @SuppressWarnings("static-access")

    public DatabaseUtils(Context context,String data_name,String tab_name,Hashtable<String, String> column_pairs)
    {
        super(context, data_name, null, VERSION);
        this.myContext = context;
        TABLE_NAME=tab_name;
        pair=column_pairs;

    }



    public DatabaseUtils(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }
    public void TruncateAllTabel()
    {
        deleteAll(TABLE_NOTIFICATION);
    }
    public void deleteAll(String TableName) {
        try {
            App.showLog("====delete=========TABLE===================="+TableName);
            SQLiteDatabase db = this.getWritableDatabase();
            App.showLog("DELETED==" + db.delete(TableName, null, null) + " " + TableName);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onCreate(SQLiteDatabase database) {

        App.showLog("=====create========DATABSE===================="+DATABASE_NAME);

        create_category(database);
    }
    //Create Table NOTIFICATION
    public void create_category(SQLiteDatabase database)
    {
        try
        {
            App.showLog("====create=========TABLE===================="+TABLE_NOTIFICATION);

            Hashtable<String, String> tmp_poeple = new Hashtable<String, String>();
            tmp_poeple.put(AlarmFields.KEY_nid, "INTEGER");
            tmp_poeple.put(AlarmFields.KEY_ttl, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_img, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_category, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_start_date_time, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_end_date_time, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_event_alert, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_event_repeat, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_stop_repeat, "TEXT");

            tmp_poeple.put(AlarmFields.KEY_isNotify, "TEXT");
            tmp_poeple.put(AlarmFields.KEY_isRead, "TEXT");

            createTable(database,TABLE_NOTIFICATION, tmp_poeple);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void createTable(SQLiteDatabase database,String tableName,Hashtable<String, String> tmp1)
    {
        String CREATE_TABLE= "create table "+ tableName + "(";

        for (String key : tmp1.keySet() )
        {
            CREATE_TABLE=CREATE_TABLE + key +" "+tmp1.get(key)+",";
        }

        int len=CREATE_TABLE.length();
        CREATE_TABLE=CREATE_TABLE.substring(0, len-1)+")";

        database.execSQL(CREATE_TABLE);
    }

    public void dropTableData(SQLiteDatabase database,String tableName,Hashtable<String, String> tmp1)
    {
        database.execSQL("DROP TABLE IF EXISTS '" + tableName + "'");

        String CREATE_TABLE= "create table "+ tableName + "(";
        for (String key : tmp1.keySet() )
        {
            CREATE_TABLE=CREATE_TABLE + key +" "+tmp1.get(key)+",";
        }

        int len=CREATE_TABLE.length();
        CREATE_TABLE=CREATE_TABLE.substring(0, len-1)+")";

        database.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseUtils.class.getName(),"Upgrading database from version " + oldVersion + " to "+ newVersion + ", which will destroy all old data");
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        onCreate(db);
    }

    public void insertRecord(Hashtable<String, String> queryValues,String TableName)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (String key : queryValues.keySet() )
        {
            values.put(key,queryValues.get(key));
        }
        database.insert(TableName, null, values);
        database.close();
    }



    public void updateNotificationTable(Hashtable<String, String> queryValues,String table_name,String fieldName,String matchValue)
    {
        try
        {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            String query="SELECT * FROM "+ table_name +" WHERE cache_urls="+matchValue;
            Cursor cursor=database.rawQuery(query, null);
            if(cursor.getCount()==0)
            {
                App.showLog("== Record Not found for update ==");
            }
            else
            {
                for (String key : queryValues.keySet() )
                {
                    values.put(key,queryValues.get(key));
                    database.update(table_name, values, "flag" + "='" + matchValue + "'",null);
                }
                App.showLog("== Record updated ==");
                database.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public void updateFieldsNotificationTable(String table_name,String setColumnName,String setColumnValues,String whereColName,String whereColValues, String UpdateTag)
    {
        try
        {
            App.showLog("===updateFieldsNotificationTable==="+UpdateTag);


            SQLiteDatabase database = this.getWritableDatabase();
            App.showLog("==set flag value is -setColumnValues=="+setColumnValues);
            if(setColumnValues!=null && whereColValues!=null)
            {
                database.execSQL("UPDATE "+table_name+" SET "+setColumnName+"='"+setColumnValues+"' WHERE "+whereColName+"='"+whereColValues+"'");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            App.showLog("=Exception when update="+e.toString());
        }
    }



    public Cursor getNotificationList(String table_name)
    {
        Cursor cursor=null;
        try
        {
            String selectQuery = "SELECT  * FROM " + table_name;
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            return cursor;
        } catch (Exception e)
        {
            Log.e("all_", "Error"  );
            e.printStackTrace();
        }
        return cursor;
    }


    public Cursor getNotificationList2(String table_name,String currentDate)
    {
        Cursor cursor=null;
        try
        {
            //App.showLog("called Get_Poeples");
            //  String selectQuery2 ="SELECT  * FROM " + table_name+" WHERE "+AlarmFields.KEY_timeNotifty+" <= '"+currentDate+"' ORDER BY "+AlarmFields.KEY_timeNotifty;
            String selectQuery2 = "SELECT  * FROM " + table_name;
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery2, null);
            return cursor;
        } catch (Exception e)
        {
            Log.e("all_", "Error"  );
            e.printStackTrace();
        }
        return cursor;
    }


    public static class AlarmFields{
        public static String KEY_nid = "KEY_nid";
        public static String KEY_ttl = "KEY_ttl";
        public static String KEY_img = "KEY_img";
        public static String KEY_category = "KEY_category"; // categoty
        public static String KEY_start_date_time = "KEY_start_date_time";
        public static String KEY_end_date_time = "KEY_end_date_time";
        public static String KEY_event_alert = "KEY_event_alert";
        public static String KEY_event_repeat = "KEY_event_repeat";
        public static String KEY_stop_repeat = "KEY_stop_repeat";

        public static String KEY_isNotify = "KEY_isNotify";
        public static String KEY_isRead = "KEY_isRead";
    }
}
