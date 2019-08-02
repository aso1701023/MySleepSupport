package jp.ac.asojuku.st.mysleepsupport

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.TEXT
import org.jetbrains.anko.db.createTable
import java.util.Calendar.LONG

class hisDatabaseOpenHelper(context: Context):ManagedSQLiteOpenHelper(context,"history.db",null,1) {
    companion object {
        val tableName = "history"
        private  var instance :hisDatabaseOpenHelper? = null;

        fun getInstance(context: Context):hisDatabaseOpenHelper{
            return instance ?: hisDatabaseOpenHelper(context.applicationContext)!!
        }
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.run { createTable(tableName,ifNotExists = true,
            columns = *arrayOf( "count" to TEXT, "result" to TEXT))
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}
class ListDataParser : MapRowParser<ListData> {
    override fun parseRow(columns: Map<String, Any?>): ListData {
        return ListData(columns["result"] as String, columns["count"] as String)
    }
}
