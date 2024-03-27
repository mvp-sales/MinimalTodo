package com.example.avjindersinghsekhon.minimaltodo.utility

import android.content.Intent
import android.os.Build
import java.io.Serializable
import java.util.UUID

fun Intent.getUUIDExtra(key: String): UUID? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, UUID::class.java)
    } else {
        getSerializableExtra(key) as? UUID
    }
}