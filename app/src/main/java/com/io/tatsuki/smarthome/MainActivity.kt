package com.io.tatsuki.smarthome

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.google.firebase.database.*
import java.io.IOException

class MainActivity : Activity() {

    private val TAG = MainActivity::class.simpleName

    enum class SwitchList() {
        TV
    }

    private val SWITCH_1 = "BCM4"
    private val SWITCH_2 = "BCM17"
    private val SWITCH_3 = "BCM27"
    private val SWITCH_4 = "BCM18"
    private val SWITCH_5 = "BCM5"
    private val SWITCH_6 = "BCM6"
    private val SWITCH_7 = "BCM13"
    private val SWITCH_8 = "BCM12"
    private val SWITCH_9 = "BCM22"
    private val SWITCH_10 = "BCM23"

    private var mFristFlg = true;
    private var tvGPIO: Gpio? = null
    private val mTVDatabaseRef = FirebaseDatabase.getInstance().reference.child(SwitchList.TV.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        Log.d(TAG, "BOARD : " + Build.BOARD)
        Log.d(TAG, "DEVICE : " + Build.DEVICE)
        Log.d(TAG, "BRAND : " + Build.BRAND)
        Log.d(TAG, "PRODUCT : " + Build.PRODUCT)

        init()
        readData()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // GPIOの使用を終了する
            tvGPIO?.close()
        } catch (e: IOException) {
            Log.e(TAG, "error", e)
        }
    }

    private fun init() {
        val peripheralManager = PeripheralManager.getInstance()
        Log.d(TAG, "GPIO : " + peripheralManager.gpioList.toString())
        Log.d(TAG, "PWM : " + peripheralManager.pwmList.toString())
        Log.d(TAG, "I2C : " + peripheralManager.i2cBusList.toString())

        try {
            // 使用するGPIOの指定
            tvGPIO = peripheralManager.openGpio(SWITCH_1)
            // 出力モード：初期値0V
            tvGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        } catch (e: IOException) {
            Log.e(TAG, "error", e)
        }
    }

    /**
     * データの書き込み
     * @type        機器の種類
     * @isLaunch    起動中か
     */
    private fun writeData(type: String, isLaunch: Boolean) {
        when(type) {
            SwitchList.TV.toString() -> {
                // DB書き込み
                mTVDatabaseRef.setValue(isLaunch)
            }
        }
    }

    private fun readData() {
        mTVDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "TV value : " + dataSnapshot.value)
                // アプリ起動時にも呼ばれるため初回のみ何もしないようにする
                if (mFristFlg) {
                    mFristFlg = false
                } else {
                    // 基本は赤外線を送信するだけなので使用後はDBの値に限らずGPIOをfalseに戻す
                    tvGPIO?.value = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        tvGPIO?.value = false
                    }, 2000)
                }
            }
        })
    }
}
