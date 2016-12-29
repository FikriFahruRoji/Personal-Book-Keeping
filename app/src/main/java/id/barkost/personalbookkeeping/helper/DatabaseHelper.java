package id.barkost.personalbookkeeping.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.barkost.personalbookkeeping.fragment.Dashboard;

/**
 * Created by fikri on 20/12/16.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String table_create_transaction = "CREATE TABLE transactions ( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date TEXT, " +
            "type TEXT, " +
            "detail TEXT, " +
            "amount INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, "transaction.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(table_create_transaction);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS transactions");
        onCreate(db);
    }

    public boolean save_table_transaction(String date, String type, String detail, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();
        content_values.put("date", date);
        content_values.put("type", type);
        content_values.put("detail", detail);
        content_values.put("amount", amount);
        long result = db.insert("transactions", null, content_values);
        return result != -1;
    }

    public Cursor list_table_transaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor transaction = db.rawQuery("SELECT * FROM transactions ORDER BY id DESC", null);
        return transaction;
    }

    public void sum_in_transaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor qwery = db.rawQuery("SELECT SUM(amount) as Total FROM transactions WHERE type = 'In'", null);
        if (qwery.moveToFirst()){
            Dashboard.total_income = qwery.getInt(qwery.getColumnIndex("Total"));
        }
    }

    public void sum_out_transaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor qwery = db.rawQuery("SELECT SUM(amount) as Total FROM transactions WHERE type = 'Out'", null);
        if (qwery.moveToFirst()){
            Dashboard.total_outcome = qwery.getInt(qwery.getColumnIndex("Total"));
        }
    }

    public boolean update_table_transaction(int id, String date, String type, String detail, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content_values = new ContentValues();
        content_values.put("id", id);
        content_values.put("date", date);
        content_values.put("type", type);
        content_values.put("detail", detail);
        content_values.put("amount", amount);
        db.update("transactions", content_values, "id = ?", new String[]{String.valueOf(id)});
        return true;
    }

    public Integer delete_transaction(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("transactions", "id = ?", new String[] {id});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM transactions");
        db.close();
    }
}