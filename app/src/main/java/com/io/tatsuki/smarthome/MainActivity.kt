package com.io.tatsuki.smarthome

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException

/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    private val TAG = MainActivity::class.simpleName

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

    private var tvGPIO: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        Log.d(TAG, "BOARD : " + Build.BOARD)
        Log.d(TAG, "DEVICE : " + Build.DEVICE)
        Log.d(TAG, "BRAND : " + Build.BRAND)
        Log.d(TAG, "PRODUCT : " + Build.PRODUCT)

        init()
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
            //tvGPIO?.value = true
        } catch (e: IOException) {
            Log.e(TAG, "error", e)
        }
    }
}
