package com.tip31.flightmode

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Account : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        supportActionBar?.hide() // Скрыть бар с названием

        val oldPassword: EditText = findViewById(R.id.current_password)
        val newPassword: EditText = findViewById(R.id.new_password)
        val changePassword: Button = findViewById(R.id.change_password)

        val pref: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val token = pref.getString("token", "-0")

        changePassword.setOnClickListener {
            runBlocking {
                withContext(Dispatchers.Default) {
                    val res = SendPost("""{"token": "$token", "old": "${oldPassword.text}", "new": "${newPassword.text}"}""",
                    "change_password").json.getString("result")
                    if (res == "fail") {
                        runOnUiThread {
                            Toast.makeText(this@Account, "Неверный пароль.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        runOnUiThread {
                            Toast.makeText(this@Account,"Пароль успешно изменён", Toast.LENGTH_SHORT).show()}
                        finish()
                    }
                }
            }
        }
    }

}