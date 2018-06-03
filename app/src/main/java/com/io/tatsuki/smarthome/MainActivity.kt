package com.io.tatsuki.smarthome

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.android.things.pio.PeripheralManager

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        Log.d(TAG, "BOARD : " + Build.BOARD)
        Log.d(TAG, "DEVICE : " + Build.DEVICE)
        Log.d(TAG, "BRAND : " + Build.BRAND)
        Log.d(TAG, "PRODUCT : " + Build.PRODUCT)

        val peripheralManager = PeripheralManager.getInstance()
        Log.d(TAG, "GPIO : " + peripheralManager.gpioList.toString())
        Log.d(TAG, "PWM : " + peripheralManager.pwmList.toString())
        Log.d(TAG, "I2C : " + peripheralManager.i2cBusList.toString())
    }
}
