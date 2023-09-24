package com.tip31.flightmode

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import androidx.activity.OnBackPressedCallback

class Auth : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide() // Скрыть бар с названием

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                startActivity(Intent(this@Auth, MainActivity::class.java))
            }
        })

        val loginField: EditText = findViewById(R.id.login_field)
        val passwordField: EditText = findViewById(R.id.password_field)

        val regButton: Button = findViewById(R.id.reg_button)
        val authButton: Button = findViewById(R.id.auth_button)

        val authHeader: TextView = findViewById(R.id.auth_text)

        val pref: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

        regButton.setOnClickListener {
            if (loginField.text.toString() == "" || passwordField.text.toString() == "") {
                authHeader.text = "Не все поля заполнены"
                authHeader.setTextColor(Color.parseColor("#FF0000"))
            }
            else {
                val json: JSONObject = runBlocking {
                    return@runBlocking register(
                        loginField.text.toString(),
                        passwordField.text.toString()
                    )
                }

                if (json.getString("result") == "fail") {
                    authHeader.setTextColor(Color.parseColor("#FF0000"))
                    when (json.getString("reason")) {
                        "invalid_characters" -> authHeader.text = "Неверные символы"
                        "already_exists" -> authHeader.text = "Пользователь уже существует"
                    }
                } else {
                    pref.edit().putString("token", json.getString("result")).apply()
                    Toast.makeText(this, "Вход успешно выполнен!", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

        authButton.setOnClickListener {
            if (loginField.text.toString() == "" || passwordField.text.toString() == "") {
                authHeader.text = "Не все поля заполнены"
                authHeader.setTextColor(Color.parseColor("#FF0000"))
            }
            else {
                val json: JSONObject = runBlocking {
                    return@runBlocking login(
                        loginField.text.toString(),
                        passwordField.text.toString()
                    )
                }

                if (json.getString("result") == "fail") {
                    authHeader.setTextColor(Color.parseColor("#FF0000"))
                    when (json.getString("reason")) {
                        "invalid_characters" -> authHeader.text = "Неверные символы"
                        "incorrect_data" -> authHeader.text = "Неправильный логин/пароль"
                    }
                } else {
                    pref.edit().putString("token", json.getString("result")).apply()
                    Toast.makeText(this, "Вход успешно выполнен!", Toast.LENGTH_SHORT).show()
                    finish()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }

    }

    private suspend fun register(login: String, password: String) = withContext(Dispatchers.Default) {
        return@withContext SendPost(
            """{"login": "$login", "password": "$password"}""",
            "register"
        ).json
    }

    private suspend fun login(login: String, password: String) = withContext(Dispatchers.Default) {
        return@withContext SendPost(
            """{"login": "$login", "password": "$password"}""",
            "login"
        ).json
    }
}