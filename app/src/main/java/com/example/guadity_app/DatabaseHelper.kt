package com.example.guadity_app

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(private val context: Context):

    SQLiteOpenHelper(context, DATABASE_NAME,  null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "Guardity.db"
        private const val  DATABASE_VERSION = 1

        // TABLA DEL LOGIN
        private  const val TABLE_NAME = "Login"
        private const val  COLUMN_ID = "id"
        private const val COLUMN_USUARIO = "Usuario"
        private const val COLUMN_CORREO = "Correo"
        private const val COLUMN_CONTRASENA = "Contrasena"
        private const val COLUMN_NUSUARIO = "Nombre del usuario"




        //TABLA DE UBICACION
        /*
        private  const val TABLE_NAME = "Ubicacion"
        private const val  COLUMN_ID = "id"
        private const val COLUMN_CODIGOEQUIPO = "Codigo de Equipo"
        private const val COLUMN_LATITUD = "Latitud"
        private const val COLUMN_LONGITUD = "Longitud"
        private const val COLUMN_DUBICACION = "d_Ubicacion"

        */
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ( "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USUARIO TEXT, " +
                "$COLUMN_CORREO TEXT, " +
                "$COLUMN_CONTRASENA TEXT, " +
                "$COLUMN_NUSUARIO TEXT)")
        db?.execSQL(createTableQuery)
        /*
        val createTableQuery = ( "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$CCOLUMN_CODIGOEQUIPO TEXT, " +
                "$COLUMN_LATITUDO TEXT, " +
                "$COLUMN_LONGITUD TEXT, " +
                "$COLUMN_DUBICACION TEXT)")
         db?.execSQL(createTableQuery)
         */


    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertUser( usuario: String, contrasena: String, correo: String, nusuario: String ): Long {
        val values = ContentValues().apply {
            put(COLUMN_USUARIO, usuario)
            put(COLUMN_CONTRASENA, contrasena)
            put(COLUMN_CORREO, correo)
            put(COLUMN_NUSUARIO, nusuario)
        }
        val db = writableDatabase
        return db.insert(TABLE_NAME, null, values)
    }

    fun readUser( usuario: String, contrasena: String) : Boolean {
        val db = readableDatabase
        val selection = " ($COLUMN_USUARIO = ? AND $COLUMN_CONTRASENA = ?) OR($COLUMN_CORREO = ? AND $COLUMN_CONTRASENA = ?)"
        val selectionArgs = arrayOf(usuario, contrasena)
        val cursor = db.query(TABLE_NAME, null, selection , selectionArgs, null, null, null)

        val userExist = cursor.count >0
        cursor.close()
        return userExist
    }
}

