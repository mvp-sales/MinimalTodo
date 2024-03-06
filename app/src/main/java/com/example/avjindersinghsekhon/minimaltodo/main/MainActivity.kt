package com.example.avjindersinghsekhon.minimaltodo.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.about.AboutActivity
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultActivity
import com.example.avjindersinghsekhon.minimaltodo.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppDefaultActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun createInitialFragment(): Fragment {
        return MainFragment.newInstance()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutMeMenuItem -> {
                val i = Intent(this, AboutActivity::class.java)
                startActivity(i)
                true
            }

            R.id.preferences -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}
