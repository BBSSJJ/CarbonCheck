package kr.co.carboncheck.android.carboncheckapp.battery

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import java.util.*

class BatteryService : Service() {
    private lateinit var batteryStatusReceiver: BatteryStatusReceiver
    private lateinit var dbHandler: DatabaseHandler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        batteryStatusReceiver = BatteryStatusReceiver()
        dbHandler = DatabaseHandler(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryStatusReceiver, filter)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryStatusReceiver)
    }

    inner class BatteryStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                val batteryPct: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val time = Calendar.getInstance().timeInMillis
                val batteryHistory = BatteryHistory(time, batteryPct)
                dbHandler.addBatteryHistory(batteryHistory)
            }
        }
    }
}