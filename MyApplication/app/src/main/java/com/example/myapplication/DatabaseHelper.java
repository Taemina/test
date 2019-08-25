package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {
    // информация о БД
    private static final String DATABASE_NAME = "Database";
    private static final int DATABASE_VERSION = 2;

    //Названия таблиц
    private static final String TABLE_POSTS = "accounts";
    private static final String TABLE_TRANS = "transactions";
    //название колонок в таблицах
    // таблица "accounts"
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_SUM = "sum";

    // таблица "transactions"
    private static final String NAME_WALLET = "NameWallet";
    private static final String SUM = "sum";
    private static final String TRANS_TYPE = "type";
    private static final String DATE = "date";
    private static final String CURRENCY = "currency";
    private static final String COMMENT = "comment";

    private static DatabaseHelper sInstance;

    public static synchronized DatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    //создание таблиц
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRANS_TABLE = "CREATE TABLE " + TABLE_TRANS +
                "(" +
                NAME_WALLET + " TEXT," +
                SUM + " TEXT," +
                TRANS_TYPE + " TEXT," +
                DATE + " TEXT," +
                CURRENCY + " TEXT," +
                COMMENT + " TEXT" +
                ")";
        db.execSQL(CREATE_TRANS_TABLE);
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_POSTS +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_SUM + " TEXT" +
                ")";
        db.execSQL(CREATE_POSTS_TABLE);

    }

    // обновление БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
            onCreate(db);
        }
    }
        // удаление строки из таблицы "accounts" по id
    public void delPost(int position) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {

            int id = position;
            db.delete(TABLE_POSTS, "id = " + id, null);

            db.setTransactionSuccessful();


        } catch (Exception e) {
            Log.d("MY", "Error while trying to del post to database");
        } finally {
            db.endTransaction();
        }
    }

    //обновление значений в строке таблицы "accounts" по id
    public void updateVal(Contact contact,int i) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();

            values.put(KEY_SUM, contact.getSum());
            values.put(KEY_NAME, contact.getName());
            values.put(KEY_ID, contact.getId());
            db.update(TABLE_POSTS,
                    values,
                    "id = " + (contact.getId()+i),
                    null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("MY", "Error while trying to update to database");
        } finally {
            db.endTransaction();
        }

    }
    //добавление строки в таблицу "accounts"
    public void addPost(Contact post) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, post.getName());
            values.put(KEY_SUM, post.getSum());


            db.insertOrThrow(TABLE_POSTS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("MY", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }
    //добавление строки в таблицу "transactions"
    public void addTrans(Transaction trans) {

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(NAME_WALLET, trans.getNameWallet());
            values.put(DATE, trans.getDate());
            values.put(COMMENT, trans.getComment());
            values.put(CURRENCY, trans.getCurrency());
            values.put(SUM, trans.getSum());
            values.put(TRANS_TYPE, trans.getType());

            db.insertOrThrow(TABLE_TRANS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("MY", "Error while trying to add trans to database");
        } finally {
            db.endTransaction();
        }
    }
// заполнение списка кошельков
    public ArrayList<Contact> getAllPosts() {
        ArrayList<Contact> posts = new ArrayList<>();

        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s ",
                        TABLE_POSTS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Contact newUser = new Contact();
                    newUser.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                    newUser.setName(cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                    newUser.setSum(cursor.getString(cursor.getColumnIndex(KEY_SUM)));

                    posts.add(newUser);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("MY", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return posts;
    }
    // заполнение списка операций
    public ArrayList<Transaction> getAllTransactions(String name) {
        ArrayList<Transaction> trans = new ArrayList<>();

        String SELECT_QUERY =
                String.format("SELECT * FROM %s ",
                        TABLE_TRANS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_QUERY, null);
        try {

            if (cursor.moveToFirst()) {
                do {
                    if (name.equals(cursor.getString(cursor.getColumnIndex(NAME_WALLET)))) {
                        Transaction newTrans = new Transaction();
                        newTrans.setNameWallet(cursor.getString(cursor.getColumnIndex(NAME_WALLET)));

                        newTrans.setDate(cursor.getString(cursor.getColumnIndex(DATE)));
                        newTrans.setComment(cursor.getString(cursor.getColumnIndex(COMMENT)));
                        newTrans.setCurrency(cursor.getString(cursor.getColumnIndex(CURRENCY)));
                        newTrans.setSum(cursor.getString(cursor.getColumnIndex(SUM)));
                        newTrans.setType(cursor.getString(cursor.getColumnIndex(TRANS_TYPE)));

                        trans.add(newTrans);
                    }

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("MY", "Error while trying to get Trans from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return trans;
    }


}