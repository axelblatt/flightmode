package com.tip31.flightmode

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class Booking : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        supportActionBar?.hide() // Скрыть бар с названием
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {finish()}})

        val pref: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        token = pref.getString("token", "-0")
        val info: IntArray = intArrayOf(0, 0, 0, 0, 0)
        val prices: IntArray = intArrayOf(0, 0, 0, 0)
        val pricesclass: IntArray = intArrayOf(0, 0, 0)

        val dateAndTime = Calendar.getInstance()
        val birth: EditText = findViewById(R.id.birth_)
        val formatter = SimpleDateFormat("dd.MM.y", Locale.ROOT)
        val d =
            OnDateSetListener {_, year, monthOfYear, dayOfMonth ->
                dateAndTime.set(Calendar.YEAR, year)
                dateAndTime.set(Calendar.MONTH, monthOfYear)
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                birth.text = SpannableStringBuilder(formatter.format(dateAndTime.timeInMillis))
            }
        birth.setOnClickListener {
            DatePickerDialog(this, d,
            dateAndTime.get(Calendar.YEAR),
            dateAndTime.get(Calendar.MONTH),
            dateAndTime.get(Calendar.DAY_OF_MONTH))
            .show()
        }

        val genderspinner: Spinner = findViewById(R.id.gend_)
        val genders = resources.getStringArray(R.array.genders)
        val gendadapter = ArrayAdapter(this, R.layout.custom_spinner_list_item, genders)
        gendadapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        genderspinner.adapter = gendadapter

        val countryspinner: Spinner = findViewById(R.id.nationality_)
        val countries = resources.getStringArray(R.array.countries)
        val countryadapter = ArrayAdapter(this, R.layout.custom_spinner_list_item, countries)
        countryadapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        countryspinner.adapter = countryadapter

        val fn: EditText = findViewById(R.id.full_name)
        val pn: EditText = findViewById(R.id.pn_)

        val buyb: Button = findViewById(R.id.submit)
        val r1: TableRow = findViewById(R.id.tlb_1)
        val r2: TableRow = findViewById(R.id.tlb_2)
        val r3: TableRow = findViewById(R.id.tlb_3)

        val r4: TableRow = findViewById(R.id.tlb2_1)
        val r5: TableRow = findViewById(R.id.tlb2_2)
        val r6: TableRow = findViewById(R.id.tlb2_3)
        val r7: TableRow = findViewById(R.id.tlb2_4)

        val h1: TextView = findViewById(R.id.t2_1)
        val h2: TextView = findViewById(R.id.t2_2)
        val h3: TextView = findViewById(R.id.t2_3)

        val h4: TextView = findViewById(R.id.t3_1)
        val h5: TextView = findViewById(R.id.t3_2)
        val h6: TextView = findViewById(R.id.t3_3)
        val h7: TextView = findViewById(R.id.t3_4)

        val flightinfo: TextView = findViewById(R.id.flight_info)
        val t1: TextView = findViewById(R.id.t2_11)
        val t2: TextView = findViewById(R.id.t2_21)
        val t3: TextView = findViewById(R.id.t2_31)
        val t4: TextView = findViewById(R.id.t3_11)
        val t5: TextView = findViewById(R.id.t3_21)
        val t6: TextView = findViewById(R.id.t3_31)
        val t7: TextView = findViewById(R.id.t3_41)

        fun clearb() {
            r1.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white_top)

            h1.setTextColor(ContextCompat.getColor(this, R.color.black))
            t1.setTextColor(ContextCompat.getColor(this, R.color.black))
            r2.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white)
            h2.setTextColor(ContextCompat.getColor(this, R.color.black))
            t2.setTextColor(ContextCompat.getColor(this, R.color.black))
            r3.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white_bottom)
            h3.setTextColor(ContextCompat.getColor(this, R.color.black))
            t3.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        fun updateb() {
            val s: Int = pricesclass[info[0]] + info[1] * prices[0] + info[2] * prices[1] + info[3] * prices[2] + info[4] * prices[3]
            buyb.text = "Купить за $$s"
        }

        r1.setOnClickListener {
            clearb()
            r1.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple_top)
            h1.setTextColor(ContextCompat.getColor(this, R.color.white))
            t1.setTextColor(ContextCompat.getColor(this, R.color.white))
            info[0] = 0
            updateb()
        }

        r2.setOnClickListener {
            clearb()
            r2.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple)
            h2.setTextColor(ContextCompat.getColor(this, R.color.white))
            t2.setTextColor(ContextCompat.getColor(this, R.color.white))
            info[0] = 1
            updateb()
        }

        r3.setOnClickListener {
            clearb()
            r3.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple_bottom)
            h3.setTextColor(ContextCompat.getColor(this, R.color.white))
            t3.setTextColor(ContextCompat.getColor(this, R.color.white))
            info[0] = 2
            updateb()
        }

        r4.setOnClickListener {
            if (info[1] == 0) {
                r4.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple_top)
                h4.setTextColor(ContextCompat.getColor(this, R.color.white))
                t4.setTextColor(ContextCompat.getColor(this, R.color.white))
                info[1] = 1
            }
            else {
                r4.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white_top)
                h4.setTextColor(ContextCompat.getColor(this, R.color.black))
                t4.setTextColor(ContextCompat.getColor(this, R.color.black))
                info[1] = 0
            }
            updateb()
        }

        r5.setOnClickListener {
            if (info[2] == 0) {
                r5.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple)
                h5.setTextColor(ContextCompat.getColor(this, R.color.white))
                t5.setTextColor(ContextCompat.getColor(this, R.color.white))
                info[2] = 1
            }
            else {
                r5.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white)
                h5.setTextColor(ContextCompat.getColor(this, R.color.black))
                t5.setTextColor(ContextCompat.getColor(this, R.color.black))
                info[2] = 0
            }
            updateb()
        }

        r6.setOnClickListener {
            if (info[3] == 0) {
                r6.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple)
                h6.setTextColor(ContextCompat.getColor(this, R.color.white))
                t6.setTextColor(ContextCompat.getColor(this, R.color.white))
                info[3] = 1
            }
            else {
                r6.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white)
                h6.setTextColor(ContextCompat.getColor(this, R.color.black))
                t6.setTextColor(ContextCompat.getColor(this, R.color.black))
                info[3] = 0
            }
            updateb()
        }

        r7.setOnClickListener {
            if (info[4] == 0) {
                r7.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_purple_bottom)
                h7.setTextColor(ContextCompat.getColor(this, R.color.white))
                t7.setTextColor(ContextCompat.getColor(this, R.color.white))
                info[4] = 1
            }
            else {
                r7.background = ContextCompat.getDrawable(this, R.drawable.cell_shape_white_bottom)
                h7.setTextColor(ContextCompat.getColor(this, R.color.black))
                t7.setTextColor(ContextCompat.getColor(this, R.color.black))
                info[4] = 0
            }
            updateb()
        }

        val b = intent.extras
        var id = 0
        if (b != null) {id = b.getInt("key")}

        val j = runBlocking {
            return@runBlocking withContext(Dispatchers.Default) {
                return@withContext SendPost("""{"id": $id}""", "get_flight").json.getJSONObject("result")
            }
        }

        pricesclass[0] = j.getInt("eco_price")
        pricesclass[1] = j.getInt("comf_price")
        pricesclass[2] = j.getInt("biz_price")

        prices[0] = j.getInt("baggage_price")
        prices[1] = j.getInt("pets_price")
        prices[2] = j.getInt("windows_price")
        prices[3] = j.getInt("insurance_price")

        if (j.getInt("cancel") == 1) {
            flightinfo.text = """Аэропорт: ${p(j, "airport")} (${p(j, "airport_en")})
Город: ${p(j, "city")} (${p(j, "city_en")})
Страна: ${p(j, "country")} (${p(j, "country_en")})
            
Отправляется: ${p(j, "departure")}
Прибывает: ${p(j, "arrive")}
            
Авиакомпания: ${p(j, "airlines")}
Самолёт: ${p(j, "plane")}

Рейс отменён. Покупка недоступна."""
            // buyb.visibility = View.GONE
            fn.visibility = View.GONE
            pn.visibility = View.GONE
            genderspinner.visibility = View.GONE
            countryspinner.visibility = View.GONE
            birth.visibility = View.GONE
            val tab1: TableLayout = findViewById(R.id.tlb)
            val tab2: TableLayout = findViewById(R.id.tlb2)
            tab1.visibility = View.GONE
            tab2.visibility = View.GONE
            buyb.visibility = View.INVISIBLE
        }
        else {
            flightinfo.text = """Аэропорт: ${p(j, "airport")} (${p(j, "airport_en")})
Город: ${p(j, "city")} (${p(j, "city_en")})
Страна: ${p(j, "country")} (${p(j, "country_en")})
            
Отправляется: ${p(j, "departure")}
Прибывает: ${p(j, "arrive")}
            
Авиакомпания: ${p(j, "airlines")}
Самолёт: ${p(j, "plane")}

Выберите класс и услуги:"""

            t1.text = "$" + p(j, "eco_price")
            t2.text = "$" + p(j, "comf_price")
            t3.text = "$" + p(j, "biz_price")
            t4.text = "$" + p(j, "baggage_price")
            t5.text = "$" + p(j, "pets_price")
            t6.text = "$" + p(j, "windows_price")
            t7.text = "$" + p(j, "insurance_price")

            buyb.setOnClickListener {
                if (0 in arrayOf(fn.text.length,
                        pn.text.length) || genderspinner.selectedItemId.toInt() == 0 ||
                    countryspinner.selectedItemId.toInt() == 0) {
                    Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
                else {
                    val j2: JSONObject = runBlocking {
                        return@runBlocking withContext(Dispatchers.Default) {
                            return@withContext SendPost("""{
                    "token": "$token",
                    "flight": $id,
                    "full_name": "${fn.text}",
                    "sex": "${genderspinner.selectedItem}",
                    "citizenship": "${countryspinner.selectedItem}",
                    "birth_date": "${birth.text}",
                    "pass_no": "${pn.text}",
                    "class_": ${info[0]},
                    "baggage": ${info[1]},
                    "pets": ${info[2]},
                    "windows": ${info[3]},
                    "insurance": ${info[4]}
                }""", "buy_ticket"
                            ).json
                        }
                    }
                    if (j2.getString("result") == "fail") {
                        if (j2.getString("reason") == "invalid_characters") {
                            Toast.makeText(this, "Неверные символы", Toast.LENGTH_SHORT).show()
                        }
                        if (j2.getString("reason") == "invalid_flight") {
                            Toast.makeText(this, "Самолёт улетел, пока вы заполняли билет", Toast.LENGTH_SHORT).show()
                        }
                        if (j2.getString("reason") == "invalid_birth") {
                            Toast.makeText(this, "Вы пытались зарегистрировать зародыша?", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(this, "Билет успешно куплен! Приятного полёта!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            r1.performClick()
        }

    }

    private fun p(j: JSONObject, str: String): String {return j.getString(str)}
}