package com.example.colorgame

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class SQLiteHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private var instance: SQLiteHelper? = null
        fun getInstance(context: Context): SQLiteHelper {
            if (instance == null) {
                instance = SQLiteHelper(context)
            }

            return instance!!
        }

        //Constants for db name and version
        private const val DATABASE_NAME = "players.db"
        private const val DATABASE_VERSION = 1

        //Constants for identifying table and columns
        const val TABLE_PLAYERS = "players"
        private const val PLAYER_ID = "id"
        private const val PLAYER_NAME = "name"
        private const val PLAYER_RECORD = "record"

        //SQL to create table
        private const val TABLE_CREATE = "CREATE TABLE " + TABLE_PLAYERS + " (" +
                PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PLAYER_NAME + " TEXT, " +
                PLAYER_RECORD + " INTEGER " +
                ")"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
        onCreate(db)
    }

    fun insertPlayer(plr: PlayerModel): Long {
        if (checkIsDataAlreadyInDBorNot(plr.name.toString())) {
            return 0
        }

        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(PLAYER_NAME, plr.name)
        contentValues.put(PLAYER_RECORD, plr.best)

        val result = db.insert(TABLE_PLAYERS, null, contentValues)
        db.close()
        return result
    }

    private fun checkIsDataAlreadyInDBorNot(fieldValue: String): Boolean {
        val db = this.readableDatabase
        val query = "Select * from $TABLE_PLAYERS where $PLAYER_NAME = '$fieldValue'"
        val cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        db.close()
        return true
    }

    fun getAllPlayers(): ArrayList<PlayerModel> {
        val plrList: ArrayList<PlayerModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_PLAYERS"
        val db = this.readableDatabase

        val cursor: Cursor

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var record: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(PLAYER_ID))
                name = cursor.getString(cursor.getColumnIndexOrThrow(PLAYER_NAME))
                record = cursor.getInt(cursor.getColumnIndexOrThrow(PLAYER_RECORD))

                val plr = PlayerModel(id = id, name = name, best = record)
                plrList.add(plr)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return plrList
    }

    fun updateBestByName(playerName: String, newScore: Int): Int {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(PLAYER_RECORD, newScore)

        val whereclause = "$PLAYER_NAME = ?"
        val whereargs = arrayOf(playerName)

        val result = db.update(TABLE_PLAYERS, cv, whereclause, whereargs)
        db.close()
        return result
    }

    fun getByName(playerName: String): PlayerModel {
        val db = this.writableDatabase
        val selectQuery = "SELECT  * FROM $TABLE_PLAYERS WHERE $PLAYER_NAME = ?"
        val result = PlayerModel()
        db.rawQuery(selectQuery, arrayOf(playerName)).use {
            if (it.moveToFirst()) {
                result.id = it.getInt(it.getColumnIndexOrThrow(PLAYER_ID))
                result.name = it.getString(it.getColumnIndexOrThrow(PLAYER_NAME))
                result.best = it.getInt(it.getColumnIndexOrThrow(PLAYER_RECORD))

            }
        }
        return result
    }

    fun deleteData() {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_PLAYERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLAYERS")
        onCreate(db)
    }

}