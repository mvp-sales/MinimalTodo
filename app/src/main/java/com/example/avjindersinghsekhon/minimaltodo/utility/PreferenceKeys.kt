package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.res.Resources
import com.example.avjindersinghsekhon.minimaltodo.R

/**
 * Created by avjindersinghsekhon on 9/21/15.
 */
class PreferenceKeys(resources: Resources) {
    @JvmField
    val night_mode_pref_key: String

    init {
        night_mode_pref_key = resources.getString(R.string.night_mode_pref_key)
    }
}
