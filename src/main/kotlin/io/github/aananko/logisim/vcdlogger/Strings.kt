package io.github.aananko.logisim.vcdlogger

import com.cburch.logisim.util.LocaleManager
import com.cburch.logisim.util.LocaleListener
import java.util.*

object Strings {
    private const val resourceBundleName = "resources/localization"
    private val defaultBundle: ResourceBundle = ResourceBundle.getBundle(resourceBundleName, Locale("en"))
    private var bundle = getBundle()
    private val localeListener = LocaleListener { bundle = getBundle() }

    init { LocaleManager.addLocaleListener(localeListener) }
    fun finalize() { LocaleManager.removeLocaleListener(localeListener) }

    private fun getBundle(): ResourceBundle =
        try { ResourceBundle.getBundle(resourceBundleName) }
        catch (e: MissingResourceException) { defaultBundle }

    fun get(key: String): String =
        try { bundle.getString(key) }
        catch (e: MissingResourceException) {
            try { defaultBundle.getString(key) }
            catch (e: MissingResourceException) { key }
        }
}
