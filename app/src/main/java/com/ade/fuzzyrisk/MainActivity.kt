package com.ade.fuzzyrisk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ade.fuzzyrisk.ui.FuzzyRiskApp
import com.ade.fuzzyrisk.ui.theme.FuzzyRiskTheme

private const val PREFS_NAME = "fuzzy_risk_settings"
private const val PREF_DARK_THEME = "dark_theme"
private const val PREF_LOGGED_IN = "logged_in"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
            var darkTheme by rememberSaveable { mutableStateOf(prefs.getBoolean(PREF_DARK_THEME, false)) }
            var loggedIn by rememberSaveable { mutableStateOf(prefs.getBoolean(PREF_LOGGED_IN, false)) }

            FuzzyRiskTheme(darkTheme = darkTheme) {
                FuzzyRiskApp(
                    loggedIn = loggedIn,
                    darkTheme = darkTheme,
                    onLoginSuccess = {
                        loggedIn = true
                        prefs.edit().putBoolean(PREF_LOGGED_IN, true).apply()
                    },
                    onLogout = {
                        loggedIn = false
                        prefs.edit().putBoolean(PREF_LOGGED_IN, false).apply()
                    },
                    onThemeChange = { enabled ->
                        darkTheme = enabled
                        prefs.edit().putBoolean(PREF_DARK_THEME, enabled).apply()
                    }
                )
            }
        }
    }
}
