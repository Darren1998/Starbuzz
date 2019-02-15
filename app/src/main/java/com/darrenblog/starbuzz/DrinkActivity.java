package com.darrenblog.starbuzz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINKID = "drinkId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);

        int drinkId = (Integer) getIntent().getExtras().get(EXTRA_DRINKID);
        /*Drink drink = Drink.drinks[drinkId];*/

        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try {
            SQLiteDatabase db = starbuzzDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("DRINK",new String[]{"NAME","DESCRIPTION","IMAGE_RESOURCE_ID","FAVORITE"},
                    "_id=?", new String[]{Integer.toString(drinkId)},null,null,null);
            if (cursor.moveToFirst()){
                String nameText = cursor.getString(0);
                String descriptionText = cursor.getString(1);
                int photoId = cursor.getInt(2);
                boolean isFavorite = (cursor.getInt(3) == 1);

                TextView name = findViewById(R.id.name);
                name.setText(nameText);

                TextView description = findViewById(R.id.description);
                description.setText(descriptionText);

                ImageView photo = findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(nameText);

                CheckBox favorite = findViewById(R.id.favorite);
                favorite.setChecked(isFavorite);
            }
            cursor.close();
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this,"Database unavailable",Toast.LENGTH_SHORT).show();
        }
    }

    public void onFavoriteClicked(View view) {
        int drinkId = (Integer) getIntent().getExtras().get(EXTRA_DRINKID);

        CheckBox favorite = findViewById(R.id.favorite);
        ContentValues drinkvalues = new ContentValues();
        drinkvalues.put("FAVORITE",favorite.isChecked());

        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
        try {
            SQLiteDatabase db = starbuzzDatabaseHelper.getWritableDatabase();
            db.update("DRINK", drinkvalues, "_id = ?", new String[]{Integer.toString(drinkId)});
            db.close();
        } catch (SQLException e) {
            Toast.makeText(this,"Database unavailable",Toast.LENGTH_SHORT).show();
        }
    }
}
