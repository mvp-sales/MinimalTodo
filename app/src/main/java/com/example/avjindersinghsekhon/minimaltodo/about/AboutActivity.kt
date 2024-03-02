package com.example.avjindersinghsekhon.minimaltodo.about

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.about.AboutFragment.Companion.newInstance
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultActivity
import com.example.avjindersinghsekhon.minimaltodo.databinding.AboutLayoutBinding
import com.example.avjindersinghsekhon.minimaltodo.main.MainFragment

class AboutActivity : AppDefaultActivity() {
    private lateinit var toolbar: Toolbar
    private var theme: String? = null
    private lateinit var binding: AboutLayoutBinding

    //    private UUID mId;
    override fun onCreate(savedInstanceState: Bundle?) {
        theme = getSharedPreferences(MainFragment.THEME_PREFERENCES, MODE_PRIVATE).getString(MainFragment.THEME_SAVED, MainFragment.LIGHTTHEME)
        if (theme == MainFragment.DARKTHEME) {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_LightTheme)
        }
        super.onCreate(savedInstanceState)
        binding = AboutLayoutBinding.inflate(layoutInflater)
        val backArrow = ResourcesCompat.getDrawable(resources, R.drawable.ic_back_arrow, getTheme())
        backArrow?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(Color.WHITE, BlendModeCompat.SRC_ATOP)
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(backArrow)
        }
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.about_layout
    }

    override fun createInitialFragment(): Fragment {
        return newInstance()
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
