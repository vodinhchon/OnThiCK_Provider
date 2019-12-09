package com.example.onthick_provider;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class AuthorActivity extends AppCompatActivity {

    Button buttonExit, buttonSelect, buttonSave, buttonDelete, buttonUpdate;
    EditText editTextID, editTextName, editTextAddress, editTextEmail;
    GridView gridViewDisplay;
    static final String URL = "content://com.example.onthick_provider.MyContentProvider_Author/authorPath";
    static final Uri uri = Uri.parse(URL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        initView();
        eventClickSelect();
        eventClickSave();
        eventClickDelete();
        eventClickUpdate();
        eventClickExit();
    }

    private void selectItem(String selection, String[] selectionArgs) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            Cursor cursor = getContentResolver().query(uri, null, selection, selectionArgs, "id_author");
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    list.add(cursor.getInt(0) + "");
                    list.add(cursor.getString(1) + "");
                    list.add(cursor.getString(2) + "");
                    list.add(cursor.getString(3) + "");
                } while (cursor.moveToNext());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AuthorActivity.this,
                        android.R.layout.simple_list_item_1, list);
                gridViewDisplay.setAdapter(adapter);
            } else {
                cleanRowDisplay();
                Toast.makeText(getApplicationContext(), "No result !", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            cleanRowDisplay();
            Toast.makeText(getApplicationContext(), "No result !", Toast.LENGTH_SHORT).show();
        }
    }

    private void eventClickSelect() {
        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] id = {editTextID.getText().toString()};
                if (!id[0].isEmpty()) {
                    selectItem("id_author=?", id);
                } else {
                    selectItem(null, null);
                }
            }
        });
    }

    private void eventClickSave() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String name = editTextName.getText().toString();
                String address = editTextAddress.getText().toString();
                String email = editTextEmail.getText().toString();
                if (checkEmpty(id, name, address, email)) {
                    ContentValues values = new ContentValues();
                    values.put("id_author", id);
                    values.put("name", name);
                    values.put("address", address);
                    values.put("email", email);
                    try {
                        getContentResolver().insert(uri, values);
                        clean();
                        selectItem(null, null);
                        Toast.makeText(getApplicationContext(), "Saved successfully.", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error ! ID already exists.", Toast.LENGTH_LONG).show();
                        editTextID.requestFocus();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter full information.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void eventClickDelete() {
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] id = {editTextID.getText().toString()};
                if (!id[0].isEmpty()) {
                    int i = getContentResolver().delete(uri, "id_author=?", id);
                    if (i > 0) {
                        clean();
                        selectItem(null, null);
                        deleteBookOfAuthor(id[0]);
                        Toast.makeText(AuthorActivity.this, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AuthorActivity.this, "Error ! ID is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AuthorActivity.this, "Please enter ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteBookOfAuthor(String id) {

    }

    private void eventClickUpdate() {
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String name = editTextName.getText().toString();
                String address = editTextAddress.getText().toString();
                String email = editTextEmail.getText().toString();
                if (checkEmpty(id, name, address, email)) {
                    ContentValues values = new ContentValues();
                    values.put("id_author", id);
                    values.put("name", name);
                    values.put("address", address);
                    values.put("email", email);
                    int i = getContentResolver().update(uri, values, "id_author=?", new String[]{id});
                    if (i > 0) {
                        clean();
                        selectItem(null, null);
                        Toast.makeText(getApplicationContext(), "Updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AuthorActivity.this, "Error ! ID is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter full information.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void eventClickExit() {
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean checkEmpty(String id, String name, String address, String email) {
        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || email.isEmpty())
            return false;
        return true;
    }

    private void cleanRowDisplay() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AuthorActivity.this,
                android.R.layout.simple_list_item_1, list);
        gridViewDisplay.setAdapter(adapter);
        editTextID.requestFocus();
    }

    private void clean() {
        editTextID.setText("");
        editTextName.setText("");
        editTextAddress.setText("");
        editTextEmail.setText("");
        editTextID.requestFocus();
    }

    private void initView() {
        editTextID = (EditText) findViewById(R.id.editTextID);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAddress = (EditText) findViewById(R.id.editTextAddress);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);

        buttonSelect = (Button) findViewById(R.id.buttonSelect);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonExit = (Button) findViewById(R.id.buttonExit);

        gridViewDisplay = (GridView) findViewById(R.id.gridViewDisplay);
    }
}
