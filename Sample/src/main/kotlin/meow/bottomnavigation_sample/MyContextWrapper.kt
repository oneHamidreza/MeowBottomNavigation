package meow.bottomnavigation_sample

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

@Suppress("DEPRECATION", "unused")
class MyContextWrapper(base: Context) : ContextWrapper(base) {

    companion object {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
        @SuppressLint("ObsoleteSdkInt")
        fun wrap(contextParam: Context?, language: String): ContextWrapper? {
            var context = contextParam ?: return null
            val config = context.resources.configuration
            if (language != "") {
                val locale = Locale(language)
                Locale.setDefault(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setSystemLocale(
                        config,
                        locale
                    )
                } else {
                    setSystemLocaleLegacy(
                        config,
                        locale
                    )
                }
                config.setLayoutDirection(locale)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(config)
            } else {
                context.resources.updateConfiguration(config, context.resources.displayMetrics)
            }
            return MyContextWrapper(context)
        }

        private fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun getSystemLocale(config: Configuration): Locale {
            return config.locales.get(0)
        }

        private fun setSystemLocaleLegacy(config: Configuration, locale: Locale) {
            config.locale = locale
        }

        @TargetApi(Build.VERSION_CODES.N)
        private fun setSystemLocale(config: Configuration, locale: Locale) {
            config.setLocale(locale)
        }
    }
}