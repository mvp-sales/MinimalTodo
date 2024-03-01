package com.example.avjindersinghsekhon.minimaltodo.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private val appVersion = "0.1"
    //private lateinit var app: AnalyticsApplication
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val app = requireActivity().application as AnalyticsApplication
        app.send("this")
        with(binding) {
            aboutVersionTextView.text = String.format(resources.getString(R.string.app_version), appVersion)
            aboutContactMe.setOnClickListener {
                app.send("this", "Action", "Feedback")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}
