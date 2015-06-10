package appewtc.masterung.pbrurestaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by masterUNG on 6/10/15 AD.
 */
public class FoodTABLE {

    //Explicit
    private MyOpenHelper objMyOpenHelper;
    private SQLiteDatabase writeDatabase, readDatabase;

    public static final String FOOD_TABLE = "foodTABLE";
    public static final String COLUMN_ID_FOOD = "_id";
    public static final String COLUMN_FOOD = "Food";
    public static final String COLUMN_PRICE = "Price";

    public FoodTABLE(Context context) {

        objMyOpenHelper = new MyOpenHelper(context);
        writeDatabase = objMyOpenHelper.getWritableDatabase();
        readDatabase = objMyOpenHelper.getReadableDatabase();

    }   // Constructor

    public long addFood(String strFood, String strPrice) {

        ContentValues objContentValues = new ContentValues();
        objContentValues.put(COLUMN_FOOD, strFood);
        objContentValues.put(COLUMN_PRICE, strPrice);

        return writeDatabase.insert(FOOD_TABLE, null, objContentValues);
    }



}   // FoodTABLE
