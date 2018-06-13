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

/**
 * 学習した赤外線をとばす操作を GPIO で制御する
 * Firebase の値が更新された時に GPIO を ON にするので
 * Firebase の値と GPIO の ON/OFF が一致しない
 * （音声やアプリ以外でも機器を操作することがあり、Firebase でオンになっている状態でFirebase の方にオンという値を書き込んでも更新されないため）
 */

class MainActivity : Activity() {

    private val TAG = MainActivity::class.simpleName

    private val mSwitchList = arrayOf("BCM4",                   // テレビON/OFF
                                      "BCM17",                  // スピーカーON/OFF
                                      "BCM27",                  // ライトON
                                      "BCM18",                  // ライトOFF
                                      "BCM5",                   // エアコンON
                                      "BCM6",                   // 暖房ON
                                      "BCM13",                  // 除湿ON
                                      "BCM12",                  // エアコン・暖房・除湿OFF
                                      "BCM22",                  // 扇風機ON
                                      "BCM23")                  // 扇風機OFF
    private val mDeviceList = arrayOf("テレビ", "スピーカー", "ライト", "エアコン", "暖房", "除湿", "扇風機")

    private var mTVFristFlag = true
    //private var mSpeakerFirstFlag = true
    private var mLightOnFirstFlag = true
    private var mLightOffFirstFlag = true
    private var mAirConOnFirstFlag = true
    private var mHeatFirstFlag = true
    private var mDehumFirstFlag = true
    private var mAirConOffFirstFlag = true

    private var tvGPIO: Gpio? = null
    //private var speakerGPIO: Gpio? = null
    private var lightOnGPIO: Gpio? = null
    private var lightOffGPIO: Gpio? = null
    private var airconOnGPIO: Gpio? = null
    private var heatGPIO: Gpio? = null
    private var dehumGPIO: Gpio? = null
    private var airconOffGPIO: Gpio? = null

    private val mTVDatabaseRef = FirebaseDatabase.getInstance().reference.child(mDeviceList[0])
    //private val mSpeakerDatabaseRef = FirebaseDatabase.getInstance().reference.child(mDeviceList[1])
    private var mLightDatabaseRef = FirebaseDatabase.getInstance().reference.child(mDeviceList[2])
    private var mAirconDatabaseRef = FirebaseDatabase.getInstance().reference.child(mDeviceList[3])
    private var mHeatDatabaseRef = FirebaseDatabase.getInstance().reference.child(mDeviceList[4])
    private var mDehumDatabaseRef = FirebaseDatabase.getInstance().reference.child(mDeviceList[5])

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init()
        readData()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // GPIOの使用を終了する
            tvGPIO?.close()
            //speakerGPIO?.close()
            lightOnGPIO?.close()
            lightOffGPIO?.close()
            airconOnGPIO?.close()
            heatGPIO?.close()
            dehumGPIO?.close()
            airconOffGPIO?.close()
        } catch (e: IOException) {
            Log.e(TAG, "error", e)
        }
    }

    private fun init() {
        val peripheralManager = PeripheralManager.getInstance()
        Log.d(TAG, "GPIO : " + peripheralManager.gpioList.toString())

        try {
            // 使用するGPIOの指定
            tvGPIO = peripheralManager.openGpio(mSwitchList[0])
            //speakerGPIO = peripheralManager.openGpio(mSwitchList[1])
            lightOnGPIO = peripheralManager.openGpio(mSwitchList[2])
            lightOffGPIO = peripheralManager.openGpio(mSwitchList[3])
            airconOnGPIO = peripheralManager.openGpio(mSwitchList[4])
            heatGPIO = peripheralManager.openGpio(mSwitchList[5])
            dehumGPIO = peripheralManager.openGpio(mSwitchList[6])
            airconOffGPIO  = peripheralManager.openGpio(mSwitchList[7])

            // 出力モード：初期値0V
            tvGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            //speakerGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            lightOnGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            lightOffGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            airconOnGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            heatGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            dehumGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            airconOffGPIO?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        } catch (e: IOException) {
            Log.e(TAG, "error", e)
        }
    }

    /**
     * データの読み込み
     */
    private fun readData() {
        // テレビ
        mTVDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // アプリ起動時にも呼ばれるため初回のみ何もしないようにする
                if (mTVFristFlag) {
                    mTVFristFlag = false
                } else {
                    sendInfrared(tvGPIO)
                }
            }
        })

        // ライト
        mLightDatabaseRef.child("オン").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mLightOnFirstFlag) {
                    mLightOnFirstFlag = false
                } else {
                    sendInfrared(lightOnGPIO)
                }
            }
        })
        mLightDatabaseRef.child("オフ").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mLightOffFirstFlag) {
                    mLightOffFirstFlag = false
                } else {
                    sendInfrared(lightOffGPIO)
                }
            }
        })

        // 冷房
        mAirconDatabaseRef.child("オン").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAirConOnFirstFlag) {
                    mAirConOnFirstFlag = false
                } else {
                    sendInfrared(airconOnGPIO)
                }
            }
        })
        mAirconDatabaseRef.child("オフ").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAirConOffFirstFlag) {
                    mAirConOffFirstFlag = false
                } else {
                    sendInfrared(airconOffGPIO)
                }
            }
        })

        // 暖房
        mHeatDatabaseRef.child("オン").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mHeatFirstFlag) {
                    mHeatFirstFlag = false
                } else {
                    sendInfrared(heatGPIO)
                }
            }
        })
        mHeatDatabaseRef.child("オフ").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAirConOffFirstFlag) {
                    mAirConOffFirstFlag = false
                } else {
                    sendInfrared(airconOffGPIO)
                }
            }
        })

        // 除湿
        mDehumDatabaseRef.child("オン").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mDehumFirstFlag) {
                    mDehumFirstFlag = false
                } else {
                    sendInfrared(dehumGPIO)
                }
            }
        })
        mDehumDatabaseRef.child("オフ").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(TAG, "error", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (mAirConOffFirstFlag) {
                    mAirConOffFirstFlag = false
                } else {
                    sendInfrared(airconOffGPIO)
                }
            }
        })
    }

    /**
     * 赤外線を送信
     * 基本は赤外線を送信するだけなので使用後はDBの値に限らずGPIOをfalseに戻す
     */
    private fun sendInfrared(gpio: Gpio?) {
        gpio?.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            gpio?.value = false
        }, 1000)
    }
}
