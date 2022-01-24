package com.example.a1405178.contentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.net.URL;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickAddName(View view){
        //add a new student record
        ContentValues values = new ContentValues();
        values.put(StudentsProvider.NAME,((EditText)findViewById(R.id.editText2)).getText().toString());
        values.put(StudentsProvider.GRADE,((EditText)findViewById(R.id.editText3)).getText().toString());
        Uri uri=getContentResolver().insert(StudentsProvider.CONTENT_URI,values);
        Toast.makeText(getBaseContext(),uri.toString(),Toast.LENGTH_LONG).show();

    }

    public void onClickRetrieveStudents(View view){
        //Retrieve Students Records
        String url = "content://com.example.a1405178.contentprovider.StudentsProvider";
        Uri students = Uri.parse(url);
        Cursor c = managedQuery(students,null,null,null,"name");
        if (c.moveToFirst()){
            do {
                Toast.makeText(this,c.getString(c.getColumnIndex(StudentsProvider._ID))+", "
                        +c.getString(c.getColumnIndex(StudentsProvider.NAME)) + ", "
                        +c.getString(c.getColumnIndex(StudentsProvider.GRADE)),Toast.LENGTH_LONG).show();
                }while (c.moveToNext());
        }
    }
}
