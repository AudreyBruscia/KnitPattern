package xyz.audbru.knitpattern;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import xyz.audbru.knitpattern.PatternContract.PatternEntry;

/**
 * Created by User on 4/19/2016.
 */
public class PatternSQLHelper extends SQLiteOpenHelper {
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_PROJECT_TABLE = "CREATE TABLE " +
            PatternEntry.PROJECT_TABLE_NAME + " (" +
            //PatternEntry.ID + INT_TYPE + " PRIMARY KEY " + COMMA_SEP +
            PatternEntry.COLUMN_NAME_PROJECT_TITLE + ")";
    private static final String SQL_CREATE_PATTERN_TABLE = "CREATE TABLE " +
            PatternEntry.PATTERN_TABLE_NAME + " (" +
            //PatternEntry.ID + INT_TYPE + " PRIMARY KEY " + COMMA_SEP +
            PatternEntry.COLUMN_NAME_PROJECT_NUM + INT_TYPE + COMMA_SEP +
            PatternEntry.COLUMN_NAME_ROW_NUM + INT_TYPE + COMMA_SEP +
            PatternEntry.COLUMN_NAME_ROW_TEXT + TEXT_TYPE + ")";;
    private static final String SQL_DELETE_PROJECT_TABLE = "DROP TABLE IF EXISTS " + PatternEntry.PROJECT_TABLE_NAME;
    private static final String SQL_DELETE_PATTERN_TABLE = "DROP TABLE IF EXISTS " + PatternEntry.PATTERN_TABLE_NAME;

    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "MyPatterns.db";

    public PatternSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PROJECT_TABLE);
        db.execSQL(SQL_CREATE_PATTERN_TABLE);
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PROJECT_TABLE);
        db.execSQL(SQL_DELETE_PATTERN_TABLE);
        onCreate(db);
        //db.close();
    }

    public long insertInProjectTable(String projectTitle) {
        //Get the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        //map of column names and value to insert
        ContentValues values = new ContentValues();
        values.put(PatternEntry.COLUMN_NAME_PROJECT_TITLE, projectTitle);

        //Insert the new row, returning the primary key value of the new row
        long primaryKey = db.insert(PatternEntry.PROJECT_TABLE_NAME, "null", values);
        //db.close();

        return primaryKey;
    }

    public long insertInPatternTable(int projectNum, int rowNum, String patternText) {
        //Get the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        //map of column names and value to insert
        ContentValues values = new ContentValues();
        values.put(PatternEntry.COLUMN_NAME_PROJECT_NUM, projectNum);
        values.put(PatternEntry.COLUMN_NAME_ROW_NUM, rowNum);
        values.put(PatternEntry.COLUMN_NAME_ROW_TEXT, patternText);

        //Insert the new row, returning the primary key value of the new row
        long primaryKey = db.insert(PatternEntry.PATTERN_TABLE_NAME, "null", values);
        return primaryKey;
    }

    public void modifyProjectName(int projectNum, String newName) {
        String query = "UPDATE " + PatternEntry.PROJECT_TABLE_NAME + " SET " +
                PatternEntry.COLUMN_NAME_PROJECT_TITLE + " = '" + newName +
                "' WHERE " + "rowid" + " = " + projectNum;

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    public ArrayList<Pattern> getAllProjectTitles() {
        String query = "SELECT " + PatternEntry.COLUMN_NAME_PROJECT_TITLE + COMMA_SEP +
                "rowid" + " FROM " + PatternEntry.PROJECT_TABLE_NAME +
                " ORDER BY " + "rowid" + " DESC";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Pattern> arrPatterns = new ArrayList<Pattern>();

        if (cursor == null || cursor.getCount() == 0) {
            return arrPatterns;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Pattern newPattern = new Pattern();

            newPattern.setProjectName(cursor.getString(0));
            newPattern.setProjectNum(cursor.getInt(1));

            arrPatterns.add(newPattern);

            cursor.moveToNext();
        }

        //db.close();
        cursor.close();
        return arrPatterns;
    }

    public Pattern getFullPattern(Pattern pattern) {
        String query = "SELECT " + PatternEntry.COLUMN_NAME_ROW_NUM + COMMA_SEP +
                PatternEntry.COLUMN_NAME_ROW_TEXT + " FROM " + PatternEntry.PATTERN_TABLE_NAME +
                " WHERE " + PatternEntry.COLUMN_NAME_PROJECT_NUM + " = " + pattern.getProjectNum() +
                " ORDER BY " + PatternEntry.COLUMN_NAME_ROW_NUM;

        Log.d("Get Pattern query", query);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Row> rowArrayList = new ArrayList<>();

        if (cursor == null || !cursor.moveToFirst()) {
            pattern.setArrRows(rowArrayList);
            //db.close();
            return pattern;
        }

        while (!cursor.isAfterLast()) {
            Row newRow = new Row();

            newRow.setRowNum(cursor.getInt(0));
            newRow.setPatternText(cursor.getString(1));

            rowArrayList.add(newRow);

            cursor.moveToNext();
        }

        pattern.setArrRows(rowArrayList);

        cursor.close();
        //db.close();
        return pattern;
    }

    public void deleteProjectById(int projectNum) {
        String query1 = "DELETE FROM " + PatternEntry.PROJECT_TABLE_NAME + " WHERE " +
                "rowid" + " = '" + projectNum  + "'";
//        String query2 = "DELETE FROM " + PatternEntry.PATTERN_TABLE_NAME + " WHERE " +
//                PatternEntry.COLUMN_NAME_PROJECT_NUM + " = '" + projectNum + "'";
        //String[] args = {projectNum + ""};

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query1);
//        db.execSQL(query2);
        deleteRowsById(projectNum);
        //db.close();
    }

    public void deleteRowsById(int projectNum) {
        String query2 = "DELETE FROM " + PatternEntry.PATTERN_TABLE_NAME + " WHERE " +
                PatternEntry.COLUMN_NAME_PROJECT_NUM + " = '" + projectNum + "'";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query2);
        //db.close();
    }

}
