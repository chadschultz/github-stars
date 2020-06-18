package com.example.chromecustomtabsnavigator

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment

/**
 * Inspiration from https://proandroiddev.com/add-chrome-custom-tabs-to-the-android-navigation-component-75092ce20c6a
 * This version has significant changes and additions.
 *
 * This is a helper class for using Chrome Custom Tabs with the Navigation component.
 *
 * To use, change the class of your [NavHostFragment] in your [Activity] to this class.
 *
 * To use, specify the attributes in your navigation graph XML file, like so:
 *     <chrome
 *     android:id="@+id/github_repo_detail"
 *     android:name="com.example.chromecustomtabsnavigator.ChromeCustomTabsNavigator"
 *     android:label="GitHubRepoDetail"
 *     app:colorScheme="light"
 *     app:toolbarColor="@color/colorPrimary"
 *     app:enterAnim="@anim/expand"
 *     app:exitAnim="@anim/collapse"
 *     app:upInsteadOfClose="true"
 *     app:addDefaultShareMenuItem="true"/>
 * All of the custom attributes (those starting with `app:`) are optional.
 *
 * Then from your Fragment's [Fragment.onStart()] function, call
 * `findChromeCustomTabsNavigator().bindCustomTabsService()`
 *
 * You may optionally call
 * `findChromeCustomTabsNavigator().mayLaunchUrl(url)`
 * See https://developer.android.com/reference/androidx/browser/customtabs/CustomTabsSession#mayLaunchUrl(android.net.Uri,%20android.os.Bundle,%20java.util.List%3Candroid.os.Bundle%3E)
 * Note that https://developer.android.com/reference/androidx/browser/customtabs/CustomTabsSession#mayLaunchUrl(android.net.Uri,%20android.os.Bundle,%20java.util.List%3Candroid.os.Bundle%3E)
 * suggests "if your user has at least a 50% likelihood of clicking on the link, call the mayLaunchUrl() method."
 *
 * And then when you are ready to open Chrome Custom Tabs, call code similar to
 *     val action = MyDirections.actionOneFragmentToAnotherFragment(uri)
 *     binding.root.findNavController().navigate(action)`
 *
 * Some XML attributes are provided for common customizations. This doesn't cover everything that
 * can be customized with Chrome Custom Tabs. More additions would just make the set of XML attributes
 * more complicated. It's possible this helper class will not be needed in the future anyway.
 * As of April 2020, Google is actively working on https://github.com/GoogleChrome/android-browser-helper
 * Currently that is focused on Trusted Web Activities, but the original helper/example code for
 * Chrome Custom Tabs says "Please use the Android Browser Helper library instead. It contains the "
 * "same functionality updated to work with AndroidX." https://github.com/GoogleChrome/android-browser-helper
 * Hopefully Android Browser Helper will become a helpful utility for modern Android apps using
 * Chrome Custom Tabs, and this class will become obsolete.
 *
 * Note: if you have trouble seeing color changes when testing, try using a physical device instead of
 * the emulator.
 *
 * @attr ref R.styleable.ChromeCustomTabsNavigator_colorScheme
 * @attr ref R.styleable.ChromeCustomTabsNavigator_toolbarColor
 * @attr ref R.styleable.ChromeCustomTabsNavigator_navigationBarColor
 * @attr ref R.styleable.ChromeCustomTabsNavigator_enterAnim
 * @attr ref R.styleable.ChromeCustomTabsNavigator_exitAnim
 * @attr ref R.styleable.ChromeCustomTabsNavigator_upInsteadOfClose
 * @attr ref R.styleable.ChromeCustomTabsNavigator_addDefaultShareMenuItem
 */
@Navigator.Name("chrome")
class ChromeCustomTabsNavigator(
    private val context: Context
) : Navigator<ChromeCustomTabsNavigator.Destination>() {

    /**
     * Initialized when `findChromeCustomTabsNavigator().bindCustomTabsService()` is called.
     */
    private var session: CustomTabsSession? = null

    private val urisInProgress = mutableMapOf<Uri, Long>()

    /**
     * Prevent the user from repeatedly launching Chrome Custom Tabs for the same URL. Throttle
     * rapid repeats unless the URL has finished loading, or this timeout has passed (just in
     * case something went wrong with detecting that the page finished loading).
     * Feel free to change this value with [Fragment.findChromeCustomTabsNavigator.throttleTimeout()]
     * if you feel the need, or for testing purposes.
     * Defaults to two seconds.
     */
    @SuppressWarnings("WeakerAccess")
    var throttleTimeout: Long = 2000L

    private val upIconBitmap: Bitmap by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_arrow_back_black_24dp)?.toBitmap()!!
    }

    override fun createDestination() =
        Destination(
            this
        )

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        // The Navigation framework enforces the destination URL being non-null
        val uri = args?.getParcelable<Uri>(KEY_URI)!!

        if (!shouldAllowLaunch(uri)) return null

        buildCustomTabsIntent(destination).launchUrl(context, uri)

        return null // Do not add to the back stack, managed by Chrome Custom Tabs
    }

    override fun popBackStack() = true // Managed by Chrome Custom Tabs

    private fun buildCustomTabsIntent(destination: Destination): CustomTabsIntent {
        val builder = CustomTabsIntent.Builder()

        session?.let {
            builder.setSession(it)
        }
        builder.setColorScheme(destination.colorScheme)
        if (destination.toolbarColor != 0) {
            builder.setToolbarColor(ContextCompat.getColor(context, destination.toolbarColor))
        }
        if (destination.navigationBarColor != 0) {
            builder.setNavigationBarColor(ContextCompat.getColor(context, destination.navigationBarColor))
        }
        builder.setStartAnimations(context, destination.enterAnim, 0)
        builder.setExitAnimations(context, 0, destination.exitAnim)
        if (destination.upInsteadOfClose) {
            builder.setCloseButtonIcon(upIconBitmap)
        }
        if (destination.addDefaultShareMenuItem) {
            builder.addDefaultShareMenuItem()
        }

        val customTabsIntent = builder.build()

        // Adding referrer so websites know where their traffic came from, per Google's recommendations:
        // https://medium.com/google-developers/best-practices-for-custom-tabs-5700e55143ee
        customTabsIntent.intent.putExtra(
            Intent.EXTRA_REFERRER,
            Uri.parse("android-app://" + context.packageName)
        )

        return customTabsIntent
    }

    private fun shouldAllowLaunch(uri: Uri): Boolean {
        urisInProgress[uri]?.let { tabStartTime ->
            // Have we launched this URI before recently?
            if (System.currentTimeMillis() - tabStartTime > throttleTimeout) {
                // Since we've exceeded the throttle timeout, continue as normal, launching
                // the destination and updating the time.
                Log.w(
                    TAG, "Throttle timeout for $uri exceeded. This means ChromeCustomTabsNavigator "
                        + "failed to accurately determine that the URL finished loading. If you see this error "
                        + "frequently, it could indicate a bug in ChromeCustomTabsNavigator.")
            } else {
                // The user has tried to repeatedly open the same URL in rapid succession. Let them chill.
                // The tab probably just hasn't opened yet. Abort opening the tab a second time.
                urisInProgress.remove(uri)
                return false
            }
        }
        urisInProgress[uri] = System.currentTimeMillis()
        return true
    }

    /**
     * Boilerplate setup for Chrome Custom Tabs. This should suffice for most apps using Chrome
     * Custom Tabs with the Navigation component. It warms up Chrome in advance to save a few
     * milliseconds, and sets a [CustomTabsSession] for the [ChromeCustomTabsNavigator] so that
     * [CustomTabsSession.mayLaunchUrl] can be called from application code.
     */
    fun bindCustomTabsService() {
        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0L)
                session = client.newSession(customTabsCallback)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
            }
        }
        CustomTabsClient.bindCustomTabsService(
            context,
            CUSTOM_TAB_PACKAGE_NAME,
            connection
        )
    }

    /**
     * Possibly pre-load one or more URLs. Note that
     * per https://developer.chrome.com/multidevice/android/customtabs#pre-render-content,
     * mayLaunchUrl should only be used if the odds are at least 50% of the user clicking
     * the link.
     * @see [CustomTabsSession.mayLaunchUrl] for more details on mayLaunchUrl.
     */
    fun mayLaunchUrl(url: Uri, extras: Bundle? = null, otherLikelyBundles: List<Bundle>? = null) {
        session?.mayLaunchUrl(url, extras, otherLikelyBundles)
    }

    val customTabsCallback: CustomTabsCallback by lazy {
        object : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                when (navigationEvent) {
                    NAVIGATION_ABORTED, NAVIGATION_FAILED, NAVIGATION_FINISHED -> {
                        // Navigation has finished. Remove the indication that page has not finished
                        // loading, so we will allow the user to try to open the same page again.
                        with(urisInProgress.entries) {
                            remove(first())
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "ChromeTabsNavigator"
        private const val CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome"
        const val KEY_URI = "uri"
    }

    @NavDestination.ClassType(Activity::class)
    class Destination(navigator: Navigator<out NavDestination>) : NavDestination(navigator) {

        var colorScheme: Int = 0

        @ColorRes
        var toolbarColor: Int = 0

        @ColorRes
        var navigationBarColor: Int = 0

        @AnimRes
        var enterAnim: Int = 0

        @AnimRes
        var exitAnim: Int = 0

        var upInsteadOfClose: Boolean = false

        var addDefaultShareMenuItem: Boolean = false

        override fun onInflate(context: Context, attrs: AttributeSet) {
            super.onInflate(context, attrs)

            context.withStyledAttributes(attrs, R.styleable.ChromeCustomTabsNavigator, 0, 0) {
                colorScheme = getInt(R.styleable.ChromeCustomTabsNavigator_colorScheme, 0)
                toolbarColor = getResourceId(R.styleable.ChromeCustomTabsNavigator_toolbarColor, 0)
                navigationBarColor =
                    getResourceId(R.styleable.ChromeCustomTabsNavigator_navigationBarColor, 0)
                enterAnim = getResourceId(R.styleable.ChromeCustomTabsNavigator_enterAnim, 0)
                exitAnim = getResourceId(R.styleable.ChromeCustomTabsNavigator_exitAnim, 0)
                upInsteadOfClose =
                    getBoolean(R.styleable.ChromeCustomTabsNavigator_upInsteadOfClose, false)
                addDefaultShareMenuItem =
                    getBoolean(R.styleable.ChromeCustomTabsNavigator_addDefaultShareMenuItem, false)
            }
        }
    }
}
