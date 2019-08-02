package com.aprz.layoutmanagerdemo


import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().context
        assertEquals("com.aprz.layoutmanagerdemo.test", appContext.packageName)
        println("5555555555555555555555")
    }

    @Test
    fun robbidding() {

        val context = InstrumentationRegistry.getInstrumentation().context
        val intent = context.packageManager
            .getLaunchIntentForPackage("com.aprz.layoutmanagerdemo")
        // Clear out any previous instances
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        sleep(5 * 1000)
        //device.pressBack();
        device.findObject(By.text("13")).click()
        sleep(5 * 1000)
    }


}
