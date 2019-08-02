package jp.ac.asojuku.st.mysleepsupport

import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_history.*
import org.jetbrains.anko.db.select
import java.util.Collections.addAll

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val helper = hisDatabaseOpenHelper.getInstance(this)
        val dataList =  helper.readableDatabase.select(hisDatabaseOpenHelper.tableName).parseList<ListData>(ListDataParser())

        list.adapter = ListAdapter(baseContext, R.layout.row).apply {
            addAll(dataList)
        }
    }
}
