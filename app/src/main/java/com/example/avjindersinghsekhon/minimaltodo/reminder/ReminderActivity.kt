package com.example.avjindersinghsekhon.minimaltodo.reminder

import android.os.Bundle
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultActivity

class ReminderActivity : AppDefaultActivity() {

    override fun contentViewLayoutRes(): Int {
        return R.layout.reminder_layout
    }

    override fun createInitialFragment(): ReminderFragment {
        return ReminderFragment.newInstance()
    }
}
