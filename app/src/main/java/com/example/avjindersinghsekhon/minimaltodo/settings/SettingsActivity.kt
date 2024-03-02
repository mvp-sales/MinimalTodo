package com.example.avjindersinghsekhon.minimaltodo.settings

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment
import com.example.avjindersinghsekhon.minimaltodo.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var app: AnalyticsApplication
    override fun onResume() {
        super.onResume()
        app.send("this")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        app = application as AnalyticsApplication
        val theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, getTheme())
        backArrow?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_ATOP)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(backArrow)
        }
        supportFragmentManager.beginTransaction().replace(R.id.mycontent, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
