package io.github.shware10.GameBank.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import io.github.shware10.GameBank.android.DBHelperInterface;


public class DBHelper extends SQLiteOpenHelper implements DBHelperInterface {
    private static final String DATABASE_NAME = "game.db"; // 데이터베이스 이름
    private static final int DATABASE_VERSION = 1;         // 데이터베이스 버전

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성 SQL 작성
        db.execSQL("CREATE TABLE Users (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username TEXT UNIQUE NOT NULL, " +
            "password TEXT NOT NULL);");

        db.execSQL("CREATE TABLE Scores (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER NOT NULL, " +
            "score INTEGER NOT NULL, " +
            "FOREIGN KEY(user_id) REFERENCES Users(id));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 스키마 변경 시 실행
        db.execSQL("DROP TABLE IF EXISTS Users;");
        db.execSQL("DROP TABLE IF EXISTS Scores;");
        onCreate(db); // 테이블 다시 생성
    }

    @Override
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ?", new String[]{username});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("Users", null, values);
        return result != -1;
    }

    @Override
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT * FROM Users WHERE username = ? AND password = ?",
            new String[]{username, password}
        );
        boolean isAuthenticated = cursor.getCount() > 0;
        cursor.close();
        return isAuthenticated;
    }

    @Override
    public boolean saveScore(int userId, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("score", score);

        long result = db.insert("Scores", null, values);
        return result != -1;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Users WHERE username = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            cursor.close();
            return userId;
        }
        cursor.close();
        return -1;
    }

    public Cursor getTopScores(int limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT u.username, s.score " +
                "FROM Scores s " +
                "JOIN Users u ON s.user_id = u.id " +
                "ORDER BY s.score DESC " +
                "LIMIT ?",
            new String[]{String.valueOf(limit)}
        );
    }
}

