package com.example.avjindersinghsekhon.minimaltodo.reminder

import android.os.Bundle
import androidx.activity.viewModels
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultActivity
import com.example.avjindersinghsekhon.minimaltodo.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReminderActivity : AppDefaultActivity() {
    private val viewModel: ReminderViewModel by viewModels()

    override fun contentViewLayoutRes(): Int {
        return R.layout.reminder_layout
    }

    override fun createInitialFragment(): ReminderFragment {
        return ReminderFragment.newInstance()
    }
}
