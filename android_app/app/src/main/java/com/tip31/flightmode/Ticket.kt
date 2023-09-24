package com.tip31.flightmode

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*


class Ticket : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket)
        supportActionBar?.hide() // Скрыть бар с названием

        val b = intent.extras
        var j = JSONObject("{}")
        if (b != null) {
            j = JSONObject(b.getString("key").toString())
        }

        val ticketinfo: TextView = findViewById(R.id.ticket_info)
        val qrCodeIV: ImageView = findViewById(R.id.idIVQrcode)
        val returnTicket: Button = findViewById(R.id.return_ticket)

        val opt = j.getString("opt")
        var chosenclass = ""
        val services: ArrayList<String> = arrayListOf()
        var cost = 0

        if (opt.substring(0, 1) == "0") {
            chosenclass = "Эконом ($${j.getInt("eco_price")})"
            cost += j.getInt("eco_price")
        }
        if (opt.substring(0, 1) == "1") {
            chosenclass = "Комфорт ($${j.getInt("comf_price")})"
            cost += j.getInt("comf_price")
        }
        if (opt.substring(0, 1) == "2") {
            chosenclass = "Бизнес ($${j.getInt("biz_price")})"
            cost += j.getInt("biz_price")
        }

        if (opt.substring(1, 2) == "1") {
            cost += j.getInt("baggage_price")
            services.add("багаж ($${j.getInt("baggage_price")})")
        }
        if (opt.substring(2, 3) == "1") {
            cost += j.getInt("pets_price")
            services.add("питомцы ($${j.getInt("pets_price")})")
        }
        if (opt.substring(3, 4) == "1") {
            cost += j.getInt("windows_price")
            services.add("место у окна ($${j.getInt("windows_price")})")
        }
        if (opt.substring(4) == "1") {
            cost += j.getInt("insurance_price")
            services.add("страховка ($${j.getInt("insurance_price")})")
        }

        val servicestext = if (services.size == 0) {
            "нет"
        } else {
            services.joinToString(", ")
        }

        ticketinfo.text = """Аэропорт: ${p(j, "airport")} (${p(j, "airport_en")})
Город: ${p(j, "city")} (${p(j, "city_en")})
Страна: ${p(j, "country")} (${p(j, "country_en")})
            
Отправляется: ${p(j, "departure")} МСК
Прибывает: ${p(j, "arrive")} МСК
            
Авиакомпания: ${p(j, "airlines")}
Самолёт: ${p(j, "plane")}

Класс: $chosenclass
Выбраны услуги: $servicestext
Итог: $$cost

ФИО: ${p(j, "full_name")}
Пол: ${p(j, "sex")}
Гражданство: ${p(j, "citizenship")}
Дата рождения: ${p(j, "birth_date")}
Номер и серия паспорта: ${p(j, "pass_no")}"""

        if (p(j, "cancel") == "1") {
            ticketinfo.text = ticketinfo.text.toString() + """

Рейс отменён. Покупка недоступна. Деньги будут возвращены через несколько дней.
            """
        }
        else {
            // ПРОБЛЕМА: API НИЖЕ ANDROID 11 НЕ ПОДДЕРЖИВАЮТ ЭТОТ КОД: РЕКОМЕНДУЕТСЯ ИСПРАВИТЬ

            // val windowManager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val display = windowManager.currentWindowMetrics
            // val point: Point = Point()
            val width = display.bounds.width()
            val height = display.bounds.height()
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4
            val txt = """{"ticket_id": ${p(j, "ticket_id")}, "flight_id": ${p(j, "flight_id")}}"""
            // val enc = QRGEncoder(j.toString(), null, QRGContents.Type.TEXT, dimen)
            val enc = QRGEncoder(txt, null, QRGContents.Type.TEXT, dimen)
            val bitmap = inverseBitmapColors(enc.bitmap)
            qrCodeIV.setImageBitmap(bitmap)
        }

        returnTicket.setOnClickListener {
            Toast.makeText(this, "Билет возвращён. Деньги вернутся через несколько дней.", Toast.LENGTH_SHORT).show()
            runBlocking {
                withContext(Dispatchers.Default) {
                    SendPost(
                        """{"token": "$token", "ticket": ${p(j, "ticket_id")}}""",
                        "delete_ticket"
                    ).json.getString("resul")
                }
            }
        }

    }
    private fun p(j: JSONObject, str: String): String {return j.getString(str)}

    private fun inverseBitmapColors(bitmap: Bitmap): Bitmap {
        for (i in 0 until bitmap.width) {
            for (j in 0 until bitmap.height) {
                bitmap.setPixel(i, j, bitmap.getPixel(i, j) xor 0x00ffffff)
            }
        }
        return bitmap
    }
}