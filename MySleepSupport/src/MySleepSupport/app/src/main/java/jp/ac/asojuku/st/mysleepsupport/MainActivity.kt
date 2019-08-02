package jp.ac.asojuku.st.mysleepsupport

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.admin.DevicePolicyManager
import android.support.v7.app.AppCompatActivity
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import android.provider.Settings
import android.provider.Settings.System.SCREEN_OFF_TIMEOUT
import android.content.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.startActivity
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.Intent.ACTION_SCREEN_ON
import android.os.*
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.support.annotation.RequiresApi
import android.widget.TextView
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    var flg = 0
    var time = 0
    var resultflg = 0
    var limitcount = 0
    var resultString = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var minit = intent.getIntExtra("minit",0)
        var hour = intent.getIntExtra("hour",0)
        var time = intent.getIntExtra("time",0)
        ResetButton.setOnClickListener {
            onReset()
        }
        minit30.setOnClickListener {
            onCount()
        }
        Limit.setOnClickListener {
            onLimit()
        }
    }
    fun getToday(): String {
        val date = Date()
        val format = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        return format.format(date)
    }

    fun onCount(){
        var minit = intent.getIntExtra("minit",10)
        var hour = intent.getIntExtra("hour",0)
        if(hour >= 24 && time >= 86400000){
            hour = 24
            time = 86400000
        }else{
            minit += 30
            time += 1800000
            if(minit == 60){
                hour += 1
                minit = 0
                minitText.setText("00")
            }else{
                minitText.setText("30")
            }
            intent.putExtra("minit",minit)
            intent.putExtra("hour",hour)
            if(hour < 10){
                hourText.setText("0" + Integer.toString(hour))
            }else{
                hourText.setText(Integer.toString(hour))
            }
        }
    }

    fun onReset(){
        if(flg == 0){
            var minit = intent.getIntExtra("minit",10)
            var hour = intent.getIntExtra("hour",0)
            minit = 0
            time = 0
            hour = 0
            intent.putExtra("minit",minit)
            intent.putExtra("hour",hour)
            hourText.setText("00")
            minitText.setText("00")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onLimit() {
        val x = time.toLong()
        if (time == 0) {

        } else if (flg == 0) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("スマホ制限開始")
            dialog.setPositiveButton("キャンセル", DialogInterface.OnClickListener { _, _ ->
                // キャンセルボタン押したときの処理
                Toast.makeText(this, "キャンセルしました", Toast.LENGTH_SHORT).show()
            })
            dialog.setNegativeButton("OK", DialogInterface.OnClickListener { _, _ ->
                // OKボタン押したときの処理
                flg = 1
                limitcount += 1
                this.startLockTask()
                Handler().postDelayed(Runnable {
                    onEnd()
                }, x)
            })
            dialog.show()
        }
    }
    fun onEnd(){
        if(flg == 1) {
            var minit = intent.getIntExtra("minit",10)
            var hour = intent.getIntExtra("hour",0)
            minit = 0
            time = 0
            hour = 0
            intent.putExtra("minit",minit)
            intent.putExtra("hour",hour)
            hourText.setText("00")
            minitText.setText("00")
            flg = 0
            resultString = "制限終了"
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("制限終了、お疲れ様でした")
            dialog.setNegativeButton("OK", DialogInterface.OnClickListener{_,_ ->
                var date = getToday()
                var helper = hisDatabaseOpenHelper.getInstance(this);
                helper.use {
                    insert(hisDatabaseOpenHelper.tableName,*arrayOf(
                        "count" to date,"result" to resultString
                    ))
            }})
            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(flg == 1){
            vibrator.vibrate(2000)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("スマホ制限時間中です！！")
            dialog.setPositiveButton("制限中止", DialogInterface.OnClickListener { _, _ ->
                // 中止ボタン押したときの処理
                flg = 0
                resultflg = 3
                var minit = intent.getIntExtra("minit",10)
                var hour = intent.getIntExtra("hour",0)
                minit = 0
                time = 0
                hour = 0
                this.stopLockTask()
                intent.putExtra("minit",minit)
                intent.putExtra("hour",hour)
                hourText.setText("00")
                minitText.setText("00")
                resultString = "制限中止"
                var date = getToday()
                var helper = hisDatabaseOpenHelper.getInstance(this);
                helper.use {
                    insert(hisDatabaseOpenHelper.tableName,*arrayOf(
                        "count" to date,"result" to resultString
                    ))
                }
                Toast.makeText(this, "使用制限を中止しました(デバッグ用)", Toast.LENGTH_SHORT).show()
            })
            dialog.setNegativeButton("制限継続", DialogInterface.OnClickListener { _, _ ->
                // 制限継続ボタン押したときの処理

            })
            dialog.show()
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onTouchEvent(event: MotionEvent) :Boolean {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(flg == 1){
            vibrator.vibrate(2000)
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("スマホ制限時間中です！！")
            dialog.setPositiveButton("制限中止", DialogInterface.OnClickListener { _, _ ->
                // 中止ボタン押したときの処理
                flg = 0
                resultflg = 3
                var minit = intent.getIntExtra("minit",10)
                var hour = intent.getIntExtra("hour",0)
                minit = 0
                time = 0
                hour = 0
                this.stopLockTask()
                intent.putExtra("minit",minit)
                intent.putExtra("hour",hour)
                hourText.setText("00")
                minitText.setText("00")
                resultString = "制限中止"
                var date = getToday()
                var helper = hisDatabaseOpenHelper.getInstance(this);
                helper.use {
                    insert(hisDatabaseOpenHelper.tableName,*arrayOf(
                        "count" to date,"result" to resultString
                    ))
                }
                Toast.makeText(this, "使用制限を中止しました(デバッグ用)", Toast.LENGTH_SHORT).show()
            })
            dialog.setNegativeButton("制限継続", DialogInterface.OnClickListener { _, _ ->
                // 制限継続ボタン押したときの処理

            })
            dialog.show()
        }
        return super.onTouchEvent(event)
    }
    fun onClickHistoryButton(view: View) = startActivity(Intent(baseContext,HistoryActivity::class.java))
}
