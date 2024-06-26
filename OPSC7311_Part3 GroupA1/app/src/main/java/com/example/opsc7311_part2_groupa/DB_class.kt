package com.example.opsc7311_part2_groupa

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBClass(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "EpochDatabase"

        // User Table
        const val TABLE_CONTACTS = "user"
        const val KEY_UNAME = "username"
        const val KEY_MAIL = "email"
        const val KEY_PWORD = "password"

        // Categories Table
        const val TABLE_CATEGORIES = "categories"
        const val CATEGORY = "category"

        // Entries Table
        const val TABLE_ENTRIES = "entries"
        const val TIME_ENTRY = "time"
        const val CATEGORY_ENTRY = "category"
        const val DATE_ENTRY = "date"
        const val DESCRIPTION_ENTRY = "description"

        // Goals Table
        const val TABLE_GOALS = "goals"
        const val DATE_GOAL = "date"
        const val MIN_HOURS = "min_hours"
        const val MAX_HOURS = "max_hours"

        // UserLogged Table
        const val TABLE_USER_LOGGED = "user_logged"
        const val USER_LOGGED_EMAIL = "email"

        // Tasks Table
        const val TABLE_TASKS = "tasks"
        const val TASK_NAME = "task_name"
        const val SUB_TASK_NAME = "sub_task_name"
        const val TASK_DATE = "task_date"
        const val TASK_HOURS = "task_hours"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val loginDetails = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_UNAME + " TEXT,"
                + KEY_MAIL + " TEXT PRIMARY KEY,"
                + KEY_PWORD + " TEXT" + ")")
        db?.execSQL(loginDetails)

        val categoriesTable = ("CREATE TABLE " + TABLE_CATEGORIES + "("
                + CATEGORY + " TEXT PRIMARY KEY" + ")")
        db?.execSQL(categoriesTable)

        val timeEntries = ("CREATE TABLE " + TABLE_ENTRIES + "("
                + TIME_ENTRY + " TEXT,"
                + CATEGORY_ENTRY + " TEXT,"
                + KEY_MAIL + " TEXT,"
                + DATE_ENTRY + " TEXT,"
                + DESCRIPTION_ENTRY + " TEXT,"
                + "FOREIGN KEY(" + KEY_MAIL + ") REFERENCES " + TABLE_CONTACTS + "(" + KEY_MAIL + ")"
                + "FOREIGN KEY(" + CATEGORY_ENTRY + ") REFERENCES " + TABLE_CATEGORIES + "(" + CATEGORY + ")"+ ")")
        db?.execSQL(timeEntries)

        val goalsTable = ("CREATE TABLE " + TABLE_GOALS + " ("
                + DATE_GOAL + " TEXT, "
                + KEY_MAIL + " TEXT, "
                + MIN_HOURS + " REAL, "
                + MAX_HOURS + " REAL, "
                + "FOREIGN KEY(" + KEY_MAIL + ") REFERENCES " + TABLE_CONTACTS + "(" + KEY_MAIL + ")"
                + ")")
        db?.execSQL(goalsTable)

        val userLoggedTable = ("CREATE TABLE " + TABLE_USER_LOGGED + " ("
                + USER_LOGGED_EMAIL + " TEXT PRIMARY KEY, "
                + "FOREIGN KEY(" + USER_LOGGED_EMAIL + ") REFERENCES " + TABLE_CONTACTS + "(" + KEY_MAIL + ")"
                + ")")
        db?.execSQL(userLoggedTable)

        val tasksTable = ("CREATE TABLE " + TABLE_TASKS + " ("
                + TASK_NAME + " TEXT, "
                + SUB_TASK_NAME + " TEXT, "
                + TASK_DATE + " TEXT, "
                + TASK_HOURS + " REAL "
                + ")")
        db?.execSQL(tasksTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_ENTRIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER_LOGGED")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun getTotalHoursByCategory(startDate: String?, endDate: String?): Map<String, Double> {
        val totalHoursByCategory = mutableMapOf<String, Double>()
        val db = readableDatabase
        val query =
            "SELECT $CATEGORY_ENTRY, SUM($TIME_ENTRY) AS total_hours FROM $TABLE_ENTRIES " +
                    "WHERE $DATE_ENTRY BETWEEN '$startDate' AND '$endDate' " +
                    "GROUP BY $CATEGORY_ENTRY"
        val cursor = db.rawQuery(query, null)
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

    fun getTotalHoursByDay(startDate: String?, endDate: String?): Map<String, Double> {
        val totalHoursByDay = mutableMapOf<String, Double>()
        val db = readableDatabase
        val query =
            "SELECT $DATE_ENTRY, SUM($TIME_ENTRY) AS total_hours FROM $TABLE_ENTRIES " +
                    "WHERE $DATE_ENTRY BETWEEN '$startDate' AND '$endDate' " +
                    "GROUP BY $DATE_ENTRY"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val dateIndex = cursor.getColumnIndex(DATE_ENTRY)
            val totalHoursIndex = cursor.getColumnIndex("total_hours")
            if (dateIndex != -1 && totalHoursIndex != -1) {
                do {
                    val date = cursor.getString(dateIndex)
                    val totalHours = cursor.getDouble(totalHoursIndex)
                    totalHoursByDay[date] = totalHours
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return totalHoursByDay
    }
}
