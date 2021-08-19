package com.openclassrooms.realestatemanager;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import  androidx.test.core.app.ApplicationProvider;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UtilsInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = ApplicationProvider.getApplicationContext();

        assertEquals("com.openclassrooms.realestatemanager", appContext.getPackageName());
    }

    @Test
    public void connectionTest() throws InterruptedException {
//        Context appContext = ApplicationProvider.getApplicationContext();
        assertTrue(Utils.isOnline());
//        Utils.enableData(appContext, false);
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
        Thread.sleep(1000);
        assertFalse(Utils.isOnline());
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
    }
}
