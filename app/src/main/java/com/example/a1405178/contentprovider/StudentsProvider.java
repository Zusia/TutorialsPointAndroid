package com.example.a1405178.contentprovider;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;

public class StudentsProvider extends ContentProvider {

    static final String ProviderName="com.example.a1405178.contentprovider.StudentsProvider";
    static final String URL = "content://"+ ProviderName+"/students";
    static final Uri CONTENT_URI =Uri.parse(URL);
    static final String _ID = "id";
    static final String NAME = "name";
    static final String GRADE ="grade";

    private static HashMap<String,String> STUDENTS_PROTECTION_MAP;

    static final int STUDENTS = 1;
    static final int STUDENT_ID = 2;

    static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ProviderName,"students" ,STUDENTS);
        uriMatcher.addURI(ProviderName,"students/#",STUDENT_ID);
    }

    private SQLiteDatabase db;
    static final String DATABASE_NAME = "College";
    static final String STUDENTS_TABLE_NAME = "students";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            "CREATE TABLE " + STUDENTS_TABLE_NAME
            + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT NOT NULL,"
            + "grade TEXT NOT NULL);";


    private static class DatabaseHelper extends SQLiteOpenHelper{

       DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + STUDENTS_TABLE_NAME);
            onCreate(db);

        }
    }
    @Override
    public boolean onCreate() {
        Context context=getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        db = dbHelper.getWritableDatabase();
        return (db == null)?false:true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(STUDENTS_TABLE_NAME);

        switch (uriMatcher.match(uri)){
            case STUDENTS:qb.setProjectionMap(STUDENTS_PROTECTION_MAP);
                            break;
            case STUDENT_ID :qb.appendWhere(_ID + "="+ uri.getPathSegments().get(1));
                            break;
            default:
        }
        if(sortOrder ==  null || sortOrder == ""){
            sortOrder = NAME;
        }
        Cursor c= qb.query(db , projection, selection, selectionArgs,null,null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
       long rowID = db.insert(STUDENTS_TABLE_NAME,"",values);
       //record is added successfully
         if(rowID > 0){
             Uri _uri = ContentUris.withAppendedId(CONTENT_URI,rowID);
             getContext().getContentResolver().notifyChange(_uri,null);
             return _uri;

       }
       throw new SQLException("Failed to add record into "+uri);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case STUDENTS:
                db.delete(STUDENTS_TABLE_NAME, selection, selectionArgs);
                break;
            case STUDENT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(STUDENTS_TABLE_NAME, _ID + "="
                        + id + (!TextUtils.isEmpty(selection)? "AND(" + selection + ')' : ""),selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
            return count;
        }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case STUDENTS:
                count = db.update(STUDENTS_TABLE_NAME,values,selection,selectionArgs);
                break;
            case STUDENT_ID:
                count = db.update(STUDENTS_TABLE_NAME,values,_ID+"="
                        + uri.getPathSegments().get(1)+(TextUtils.isEmpty(selection)?"AND("+selection +')':""),selectionArgs);
                break;
                default:throw new IllegalArgumentException("Unknown Uri"+uri);
                }
        getContext().getContentResolver().notifyChange(uri,null);
        return count;
    }
    @Override
    public String getType(Uri uri){
        switch (uriMatcher.match(uri)){
            //get all students records
            case STUDENTS:
                return "vnd.android.cursor.dir/vnd.example.students";
            default:throw new IllegalArgumentException("Unsupported URI"  + uri);
        }
    }
}
