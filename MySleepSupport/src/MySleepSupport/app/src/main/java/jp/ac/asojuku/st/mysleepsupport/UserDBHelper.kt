package jp.ac.asojuku.st.mysleepsupport

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDBHelper(var context: Context?) : SQLiteOpenHelper(context, "sample.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        //データベースがないときに実行される
        db?.execSQL("create table sample ( " +
                "_id integer primary key autoincrement, " +
                "Count integer not null, " +
                "Result text not null " +
                ");")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //バージョンアップしたときに実行される
        //テーブルのdeleteなどを行う
    }
}