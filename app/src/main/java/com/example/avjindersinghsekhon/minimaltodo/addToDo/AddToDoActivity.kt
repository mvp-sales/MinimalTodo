package com.example.avjindersinghsekhon.minimaltodo.addToDo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultActivity

class AddToDoActivity : AppDefaultActivity() {

    override fun contentViewLayoutRes(): Int {
        return R.layout.activity_add_to_do
    }

    override fun createInitialFragment(): Fragment {
        return AddToDoFragment.newInstance()
    }
}
