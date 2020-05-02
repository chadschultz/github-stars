package com.example.chromecustomtabsnavigator

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.CustomTabsIntent
import androidx.test.core.app.ApplicationProvider
import com.example.chromecustomtabsnavigator.ChromeCustomTabsNavigator.Companion.KEY_URI
import io.mockk.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

//TODO: comments
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ChromeCustomTabsNavigatorUnitTest {

    private lateinit var appContext: Context
    private lateinit var navigator: ChromeCustomTabsNavigator

    @Before
    fun setUp() {
        appContext = ApplicationProvider.getApplicationContext<Application>()
        navigator = spyk(ChromeCustomTabsNavigator(appContext))
    }

    @Test
    fun doNotThrottleFirstLoad() {

        val destination = mockk<ChromeCustomTabsNavigator.Destination>()

        val uri = Uri.parse("https://github.com/android/architecture-samples")

//        every { navigator getProperty "urisInProgress"} returns mutableMapOf<Uri, Long>()
//        assertTrue(navigator.should)
        val intent = mockk<CustomTabsIntent>()
        every { navigator["buildCustomTabsIntent"](destination) } returns intent
//        every {intent.launchUrl(any(), any())} just Runs
        val args = Bundle().apply {
            putParcelable(KEY_URI, uri)
        }

        navigator.navigate(destination, args, null, null)
//        verify {
//            intent.launchUrl(any(), uri)
//        }

        val activity = Robolectric.setupActivity()
    }

    @Test
    fun throttleSecondLoad() {

        val destination = mockk<ChromeCustomTabsNavigator.Destination>()

        val uri = Uri.parse("https://github.com/android/architecture-samples")

//        every { navigator getProperty "urisInProgress"} returns mutableMapOf<Uri, Long>()
//        assertTrue(navigator.should)
        val intent = mockk<CustomTabsIntent>()
        every { navigator["buildCustomTabsIntent"](destination) } returns intent

        every { intent.launchUrl(any(), any()) } just Runs
        val args = Bundle().apply {
            putParcelable(KEY_URI, uri)
        }

        navigator.navigate(destination, args, null, null)
        // Launch the same URL again with no delay. This should not go through.
        navigator.navigate(destination, args, null, null)
        verify(exactly = 1) {
            intent.launchUrl(any(), uri)
        }
    }

    @Ignore
    @Test
    fun doNotThrottleAfterDelay() {

        val destination = mockk<ChromeCustomTabsNavigator.Destination>()

        val uri = Uri.parse("https://github.com/android/architecture-samples")

//        every { navigator getProperty "urisInProgress"} returns mutableMapOf<Uri, Long>()
//        assertTrue(navigator.should)
        val intent = mockk<CustomTabsIntent>()
        every { navigator["buildCustomTabsIntent"](destination) } returns intent
        val urisInProgress = mutableMapOf(Pair(uri, System.currentTimeMillis() - 2001))
//        every { navigator getProperty "urisInProgress"} propertyType MutableMap::class answers { urisInProgress }

        every { intent.launchUrl(any(), any()) } just Runs
        val args = Bundle().apply {
            putParcelable(KEY_URI, uri)
        }

        navigator.navigate(destination, args, null, null)
        navigator.navigate(destination, args, null, null)
        verify(exactly = 1) {
            intent.launchUrl(any(), uri)
        }
    }


}
