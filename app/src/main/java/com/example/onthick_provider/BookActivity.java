package com.example.onthick_provider;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {

    Button buttonExit, buttonSelect, buttonSave, buttonDelete, buttonUpdate;
    EditText editTextID, editTextTitle, editTextAuthor;
    GridView gridViewDisplay;
    static final String URL = "content://com.example.onthick_provider.MyContentProvider_Book/bookPath";
    static final Uri uri = Uri.parse(URL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
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
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null,
                    selection, selectionArgs, "id_book");
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    list.add(cursor.getInt(0) + "");
                    list.add(cursor.getString(1) + "");
                    list.add(cursor.getInt(2) + "");
                } while (cursor.moveToNext());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookActivity.this,
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
                    selectItem("id_book=?", id);
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
                String title = editTextTitle.getText().toString();
                String[] author = {editTextAuthor.getText().toString()};
                if (checkEmpty(id, title, author[0])) {
                    if (checkAuthorForSave(author)) {
                        ContentValues values = new ContentValues();
                        values.put("id_book", id);
                        values.put("title", title);
                        values.put("id_author", author[0]);
                        try {
                            getContentResolver().insert(uri, values);
                            clean();
                            selectItem(null, null);
                            Toast.makeText(getApplicationContext(), "Saved successfully.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error ! ID already exists.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Error ! Author's ID does not exists.", Toast.LENGTH_LONG).show();
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
                    int i = getContentResolver().delete(uri, "id_book=?", id);
                    if (i > 0) {
                        clean();
                        selectItem(null, null);
                        Toast.makeText(BookActivity.this, "Deleted successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(BookActivity.this, "Error ! ID is incorrect.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BookActivity.this, "Please enter ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void eventClickUpdate() {
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editTextID.getText().toString();
                String title = editTextTitle.getText().toString();
                String author = editTextAuthor.getText().toString();
                if (checkEmpty(id, title, author)) {
                    ContentValues values = new ContentValues();
                    values.put("id_book", id);
                    values.put("title", title);
                    values.put("id_author", author);
                    int i = getContentResolver().update(uri, values, "id_book=?", new String[]{id});
                    if (i > 0) {
                        clean();
                        selectItem(null, null);
                        Toast.makeText(getApplicationContext(), "Updated successfully.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(BookActivity.this, "Error ! ID is incorrect.", Toast.LENGTH_SHORT).show();
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

    private boolean checkAuthorForSave(String[] author) {
        ArrayList<String> list = new ArrayList<String>();
        String URL = "content://com.example.onthick_provider.MyContentProvider_Author/authorPath";
        Uri uri = Uri.parse(URL);
        try {
            @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(uri, null,
                    "id_author=?", author, "id_author");
            if (cursor != null) {
                cursor.moveToFirst();
                do {
                    list.add(cursor.getInt(0) + "");
                    list.add(cursor.getString(1) + "");
                    list.add(cursor.getString(2) + "");
                    list.add(cursor.getString(3) + "");
                } while (cursor.moveToNext());
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean checkEmpty(String id, String title, String author) {
        if (id.isEmpty() || title.isEmpty() || author.isEmpty())
            return false;
        return true;
    }

    private void cleanRowDisplay() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(BookActivity.this,
                android.R.layout.simple_list_item_1, list);
        gridViewDisplay.setAdapter(adapter);
        editTextID.requestFocus();
    }

    private void clean() {
        editTextID.setText("");
        editTextTitle.setText("");
        editTextAuthor.setText("");
        editTextID.requestFocus();
    }

    private void initView() {
        editTextID = (EditText) findViewById(R.id.editTextID);
        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextAuthor = (EditText) findViewById(R.id.editTextAuthor);

        buttonSelect = (Button) findViewById(R.id.buttonSelect);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonExit = (Button) findViewById(R.id.buttonExit);

        gridViewDisplay = (GridView) findViewById(R.id.gridViewDisplay);
    }
}
