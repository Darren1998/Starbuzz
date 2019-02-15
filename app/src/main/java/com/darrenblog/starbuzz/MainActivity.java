package com.darrenblog.starbuzz;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private Cursor favoritesCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupOptionsListView();
        setupFavoritesListView();
    }

    private void setupOptionsListView() {
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                if (position == 0){
                    Intent intent = new Intent(MainActivity.this,DrinkCategoryActivity.class);
                    startActivity(intent);
                }
            }
        };
        ListView listview = findViewById(R.id.list_options);
        listview.setOnItemClickListener(itemClickListener);
    }

    public void setupFavoritesListView(){
        ListView favoriteListview = findViewById(R.id.list_favorite);
        try{
            SQLiteOpenHelper starbuzzHelper = new StarbuzzDatabaseHelper(this);
            db = starbuzzHelper.getReadableDatabase();
            favoritesCursor = db.query("DRINK",new String[]{"_id","NAME"},"FAVORITE=1",null,null,null,null);
            CursorAdapter favoriteAdapter = new SimpleCursorAdapter(MainActivity.this,android.R.layout.simple_list_item_1,favoritesCursor,new String[]{"NAME"},new int[]{android.R.id.text1},0);
            favoriteListview.setAdapter(favoriteAdapter);
        } catch (SQLException e) {
            Toast.makeText(this,"Database unavailabe.",Toast.LENGTH_SHORT).show();
        }
        favoriteListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINKID,(int)id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        favoritesCursor.close();
        db.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Cursor newCursor = db.query("DRINK",new String[]{"_id","NAME"},"FAVORITE=1",null,null,null,null);
        ListView listFavorites = findViewById(R.id.list_favorite);
        CursorAdapter adapter = (CursorAdapter) listFavorites.getAdapter();
        adapter.changeCursor(newCursor);
        favoritesCursor = newCursor;
    }
}
