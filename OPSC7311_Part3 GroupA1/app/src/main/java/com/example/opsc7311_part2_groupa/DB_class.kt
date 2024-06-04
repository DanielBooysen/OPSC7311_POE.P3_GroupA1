package com.example.opsc7311_part2_groupa

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
class DBClass(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "EpochDatabase"
        const val TABLE_CONTACTS = "user"
        const val KEY_UNAME = "username"
        const val KEY_MAIL = "email"
        const val KEY_PWORD = "password"
        const val TABLE_CATEGORIES = "categories"
        const val CATEGORY = "category"
        const val TABLE_ENTRIES = "entries"
        const val TIME_ENTRY = "time"
        const val CATEGORY_ENTRY = "category"
        const val DATE_ENTRY = "date"
        const val DESCRIPTION_ENTRY = "description"
        const val TABLE_GOALS = "goals"
        const val DATE_GOAL = "date"
        const val MIN_HOURS = "min_hours"
        const val MAX_HOURS = "max_hours"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val loginDetails = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_UNAME + " TEXT,"
                + KEY_MAIL + " TEXT,"
                + KEY_PWORD + " TEXT" + ")")
        db?.execSQL(loginDetails)
        val categories = ("CREATE TABLE " + TABLE_CATEGORIES + "("
                + CATEGORY + " TEXT" + ")")
        db?.execSQL(categories)
        val timeEntries = ("CREATE TABLE " + TABLE_ENTRIES + "("
                + TIME_ENTRY + " TEXT,"
                + CATEGORY_ENTRY + " TEXT,"
                + DATE_ENTRY + " TEXT,"
                + DESCRIPTION_ENTRY + " TEXT,"
                + "FOREIGN KEY(" + CATEGORY_ENTRY + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORY + ")" + ")")
        db?.execSQL(timeEntries)
        val goalsTable = ("CREATE TABLE " + TABLE_GOALS + "("
                + DATE_GOAL + " TEXT,"
                + MIN_HOURS + " REAL,"
                + MAX_HOURS + " REAL" + ")")
        db?.execSQL(goalsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ENTRIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        onCreate(db)
    }
    fun getTotalHoursByCategory(startDate: String?, endDate: String?): Map<String, Double> {
        val totalHoursByCategory = mutableMapOf<String, Double>()
        val db = readableDatabase
        val query =
            "SELECT $CATEGORY_ENTRY, SUM($TIME_ENTRY) AS total_hours FROM $TABLE_ENTRIES " +
                    "WHERE $DATE_ENTRY BETWEEN '$startDate' AND '$endDate' " +
                    "GROUP BY $CATEGORY_ENTRY"
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val categoryIndex = cursor.getColumnIndex(CATEGORY_ENTRY)
            val totalHoursIndex = cursor.getColumnIndex("total_hours")
            if (categoryIndex != -1 && totalHoursIndex != -1) {
                do {
                    val category = cursor.getString(categoryIndex)
                    val totalHours = cursor.getDouble(totalHoursIndex)
                    totalHoursByCategory[category] = totalHours
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return totalHoursByCategory
    }
}
