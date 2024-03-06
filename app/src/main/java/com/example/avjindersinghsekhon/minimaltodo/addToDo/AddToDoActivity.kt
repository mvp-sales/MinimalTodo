package com.example.avjindersinghsekhon.minimaltodo.addToDo

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.appDefault.AppDefaultActivity
import com.example.avjindersinghsekhon.minimaltodo.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddToDoActivity : AppDefaultActivity() {

    private val viewModel: AddTodoViewModel by viewModels()

    override fun contentViewLayoutRes(): Int {
        return R.layout.activity_add_to_do
    }

    override fun createInitialFragment(): Fragment {
        return AddToDoFragment.newInstance()
    }

    companion object {
        const val TODO_ID = "TODO_ID"
    }
}
