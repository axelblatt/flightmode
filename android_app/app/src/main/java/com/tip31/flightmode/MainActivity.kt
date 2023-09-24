package com.tip31.flightmode

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlinx.coroutines.*

// data class Status(val result: String)
// data class NextFlights(val result: Array<Map<String, String>>)
var ids: Array<Int?> = arrayOfNulls(5)
var currentButton = 1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        fun rowClick(currentButton: Int, token: String?, i: Int) {
            if (currentButton == 0) {
                if (token == "-0") {
                    finish()
                    startActivity(Intent(this, Auth::class.java))
                }
                else {
                    when {
                        i >= 0 -> startActivity(Intent(this, Booking::class.java).putExtra("key", ids[i]))
                        i == -1 -> startActivity(Intent(this, Tickets::class.java).putExtra("key", token))
                        i == -2 -> startActivity(Intent(this, Account::class.java))
                    }
                }
            }
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide() // Скрыть бар с названием

        val takeoffButton: Button = findViewById(R.id.takeoffButton)
        val landButton: Button = findViewById(R.id.landButton)
        val search: AutoCompleteTextView = findViewById(R.id.searchView)

        val ticketsButton: Button = findViewById(R.id.my_tickets)
        val accountButton: Button = findViewById(R.id.account_button)

        val pref: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val token = pref.getString("token", "-0")

        ticketsButton.setOnClickListener {
            rowClick(currentButton, token, -1)
        }

        accountButton.setOnClickListener {
            rowClick(currentButton, token, -2)
        }

        /*
        runBlocking {
            withContext(Dispatchers.Default) {
                SendPost("""{"type_": 0}""", "next_flights").json.getJSONArray("result")
            }
        }
        */

        currentButton = 1
        takeoffButton.setOnClickListener {getNextFlightsAsync(0, this)}
        landButton.setOnClickListener {getNextFlightsAsync(1, this)}

        takeoffButton.performClick()

        /*
        val cities = runBlocking {
            val gc = getCities()
            val m = ArrayList<String>()
            for (i in 0 until gc.length()) {
                m.add(gc.getString(i))
            }
            return@runBlocking m
        }
        */

        val tr1: TableRow = findViewById(R.id.tr1)
        val tr2: TableRow = findViewById(R.id.tr2)
        val tr3: TableRow = findViewById(R.id.tr3)
        val tr4: TableRow = findViewById(R.id.tr4)
        val tr5: TableRow = findViewById(R.id.tr5)

        val cities = resources.getStringArray(R.array.places)
        val arrd = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, cities
        )
        search.setAdapter(arrd)

        search.setOnItemClickListener {_, _, position, _ ->
            val value = arrd.getItem(position) ?: ""
            // Log.i("API", value.dropLast(value.length - value.indexOf(",")))
            startActivity(Intent(this, Flights::class.java)
                .putExtra("key", value))
        }

        tr1.setOnClickListener {rowClick(currentButton, token, 0)}
        tr2.setOnClickListener {rowClick(currentButton, token, 1)}
        tr3.setOnClickListener {rowClick(currentButton, token, 2)}
        tr4.setOnClickListener {rowClick(currentButton, token, 3)}
        tr5.setOnClickListener {rowClick(currentButton, token, 4)}

        /*
        val from = arrayOf("_id", "text")
        val to = intArrayOf(android.R.id.text1)
        val mAdapter = SimpleCursorAdapter(
            this,
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )
        */

        /*
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                Log.i("API", query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val res = runBlocking {
                    return@runBlocking getCities(newText.toString())
                    // Log.i("API2", res.toString())
                }
                for (i in 0 until res.length()) {
                    cities.add(res.getString(i))
                }
                val columns = arrayOf("_id", "text")
                val temp = arrayOf<Any>(0, "default")

                val cursor = MatrixCursor(columns)

                for (i in 0 until cities.size) {
                    temp[0] = i
                    temp[1] = cities[i]
                    cursor.addRow(temp)
                }
                mAdapter.changeCursor(cursor)
                searchView.suggestionsAdapter = mAdapter
                return false
            }
        })
         */

    }
    /*
    fun updateAdapter(arr: ArrayList<String>): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            arr
        )
    }
    */

    @OptIn(DelicateCoroutinesApi::class)
    private fun getNextFlightsAsync(type: Int, con: Context) = GlobalScope.async(Dispatchers.Main) {
        withContext(Dispatchers.Default) {
            // val dr = ResourcesCompat.getDrawable(resources, R.drawable.border_tables, null)
            val t0: TextView = findViewById(R.id.t0)

            val to: Button = findViewById(R.id.takeoffButton)
            val l: Button = findViewById(R.id.landButton)

            val tr1: TableRow = findViewById(R.id.tr1)
            val tr2: TableRow = findViewById(R.id.tr2)
            val tr3: TableRow = findViewById(R.id.tr3)
            val tr4: TableRow = findViewById(R.id.tr4)
            val tr5: TableRow = findViewById(R.id.tr5)

            val p1: TextView = findViewById(R.id.p1)
            val p2: TextView = findViewById(R.id.p2)
            val p3: TextView = findViewById(R.id.p3)
            val p4: TextView = findViewById(R.id.p4)
            val p5: TextView = findViewById(R.id.p5)

            val t11: TextView = findViewById(R.id.t11)
            val t12: TextView = findViewById(R.id.t12)

            val t21: TextView = findViewById(R.id.t21)
            val t22: TextView = findViewById(R.id.t22)

            val t31: TextView = findViewById(R.id.t31)
            val t32: TextView = findViewById(R.id.t32)

            val t41: TextView = findViewById(R.id.t41)
            val t42: TextView = findViewById(R.id.t42)

            val t51: TextView = findViewById(R.id.t51)
            val t52: TextView = findViewById(R.id.t52)

            val responsejson =
                SendPost("""{"type_": ${type}}""", "next_flights").json.getJSONArray("result")

            // val dr = ResourcesCompat.getDrawable(resources, R.drawable.border_tables, null)
            runOnUiThread {
                if (type == 0) {
                    t0.text = "Пункт назначения"
                    to.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.takeoff_button_activated,
                        null
                    )
                    l.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.land_button_deactivated,
                        null
                    )

                    ids[0] = responsejson.getJSONObject(0).getInt("id")
                    ids[1] = responsejson.getJSONObject(1).getInt("id")
                    ids[2] = responsejson.getJSONObject(2).getInt("id")
                    ids[3] = responsejson.getJSONObject(3).getInt("id")
                    ids[4] = responsejson.getJSONObject(4).getInt("id")

                    currentButton = 0

                } else {
                    t0.text = "Пункт отправления"
                    to.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.takeoff_button_deactivated,
                        null
                    )
                    l.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.land_button_activated,
                        null
                    )

                    ids = arrayOfNulls(5)
                    currentButton = 1

                }

                p1.text = responsejson.getJSONObject(0).getString("city")
                p2.text = responsejson.getJSONObject(1).getString("city")
                p3.text = responsejson.getJSONObject(2).getString("city")
                p4.text = responsejson.getJSONObject(3).getString("city")
                p5.text = responsejson.getJSONObject(4).getString("city")

                t11.text = fromT(responsejson.getJSONObject(0).getString("departure"))
                t12.text = fromT(responsejson.getJSONObject(0).getString("arrive"))
                if (responsejson.getJSONObject(0).getInt("cancel") == 1) {
                    tr1.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_red)
                }
                else {tr1.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_grey)}

                t21.text = fromT(responsejson.getJSONObject(1).getString("departure"))
                t22.text = fromT(responsejson.getJSONObject(1).getString("arrive"))
                if (responsejson.getJSONObject(1).getInt("cancel") == 1) {
                    tr2.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_red)
                }
                else {tr2.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_grey)}

                t31.text = fromT(responsejson.getJSONObject(2).getString("departure"))
                t32.text = fromT(responsejson.getJSONObject(2).getString("arrive"))
                if (responsejson.getJSONObject(2).getInt("cancel") == 1) {
                    tr3.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_red)
                }
                else {tr3.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_grey)}

                t41.text = fromT(responsejson.getJSONObject(3).getString("departure"))
                t42.text = fromT(responsejson.getJSONObject(3).getString("arrive"))
                if (responsejson.getJSONObject(3).getInt("cancel") == 1) {
                    tr4.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_red)
                }
                else {tr4.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_grey)}

                t51.text = fromT(responsejson.getJSONObject(4).getString("departure"))
                t52.text = fromT(responsejson.getJSONObject(4).getString("arrive"))
                if (responsejson.getJSONObject(4).getInt("cancel") == 1) {
                    tr5.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_red_bottom)
                }
                else {tr5.background = ContextCompat.getDrawable(con, R.drawable.cell_shape_grey_bottom)}

            }
        }
    }
    private fun fromT(str: String): String {
        return str.split(" ").toTypedArray()[1].slice(0..4)
    }
    /*
    private suspend fun getCities(): JSONArray {
        return withContext(Dispatchers.Default) {
            val url = URL("http://192.168.1.24:8000/cities")
            val request = Request.Builder().url(url).get().build()
            val response = OkHttpClient().newCall(request).execute()
            val text = response.body?.string()
            /*
            runOnUiThread {listAdapter.filter.filter(q)}
            }
            catch (ex: Exception) {
                Log.i("API", ex.toString())
            }
            */
            // Log.i("API", cities.toString())
            return@withContext JSONObject(text).getJSONArray("result")
        }
    }
    */
    /*
    private suspend fun getCities(q: String): JSONArray {
        return withContext(Dispatchers.Default) {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = """{"search": "$q"}""".toRequestBody(mediaType)
            val url = URL("http://192.168.1.24:8000/cities")
            val request = Request.Builder().url(url).post(requestBody).build()
            val response = OkHttpClient().newCall(request).execute()
            val text = response.body?.string()
            /*
            runOnUiThread {listAdapter.filter.filter(q)}
            }
            catch (ex: Exception) {
                Log.i("API", ex.toString())
            }
            */
            // Log.i("API", cities.toString())
            return@withContext JSONObject(text).getJSONArray("result")
        }
    }
    private fun getStatus() = GlobalScope.async(Dispatchers.Main) {
        val job = GlobalScope.async {
            val x = Gson().fromJson(
                URL("http://192.168.1.24:8000/status").readText(),
                Status::class.java
            )
            val textView: TextView = findViewById(R.id.textView)
            textView.text = x.result
        }
        job.await()
    }
    */
}