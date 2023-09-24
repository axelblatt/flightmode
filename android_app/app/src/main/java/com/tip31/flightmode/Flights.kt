package com.tip31.flightmode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class FlightCard (val id: Int, var dep: String, var arr: String,
                  var price: Int, var airport: String)

// var ids2: MutableList<Int> = ArrayList()
var token: String? = ""


class Flights : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flights)
        supportActionBar?.hide()

        val pref: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        token = pref.getString("token", "-0")

        val b = intent.extras
        var value = "Лондон, Великобритания"
        var valshort = ""
        if (b != null) {
            value = b.getString("key").toString()
            valshort = value.dropLast(value.length - value.indexOf(","))
        }

        val header: TextView = findViewById(R.id.cityhead)
        header.text = value

        val recyclerview: RecyclerView = findViewById(R.id.recyclerView)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val flightCardArrayList = runBlocking {
            return@runBlocking getFlights(valshort)
        }

        val flightCardAdapter = FlightCardAdapter(flightCardArrayList)
        recyclerview.adapter = flightCardAdapter
    }

    private suspend fun getFlights(q: String) = withContext(Dispatchers.Default) {
            val jsonarr = SendPost("""{"city": "$q", "type_": 0}""", "search").json.getJSONArray("result")

            val flightCardArrayList: ArrayList<FlightCard> = ArrayList()
            for (i in 0 until jsonarr.length()) {
                flightCardArrayList.add(FlightCard(
                    jsonarr.getJSONObject(i).getInt("id"),
                    jsonarr.getJSONObject(i).getString("departure"),
                    jsonarr.getJSONObject(i).getString("arrive"),
                    jsonarr.getJSONObject(i).getInt("eco_price"),
                    jsonarr.getJSONObject(i).getString("airport")
                ))

                // ids2.add(jsonarr.getJSONObject(i).getInt("id"))
            }

            return@withContext flightCardArrayList
        }

    // class FlightCardAdapter(private val context: Flights, flightCardArrayList: ArrayList<FlightCard>
    class FlightCardAdapter(flightCardArrayList: ArrayList<FlightCard>):
        RecyclerView.Adapter<FlightCardAdapter.ViewHolder>() {
        private val flightCardArrayList: ArrayList<FlightCard>
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // to inflate the layout for each item of recycler view.
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fcards, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // to set data to textview and imageview of each card layout
            val model: FlightCard = flightCardArrayList[position]
            holder.dep.text = model.dep + " МСК"
            holder.arr.text = model.arr + " МСК"
            holder.arrplace.text = model.airport
            holder.price.text = "от $" + model.price.toString()
            holder.id = model.id
        }

        override fun getItemCount(): Int {
            // this method is used for showing number of card items in recycler view.
            return flightCardArrayList.size
        }

        /*
        fun rowClick(token: String?, i: Int, intent: Intent, context: Context, activity: Activity) {
                if (token == "-0") {
                    activity.finish()
                    context.startActivity(intent)
                }
                else {
                    Log.i("API", i.toString())
                }
            }
        */
        // token: String?, intent: Intent, context: Context, activity: Activity
        // View holder class for initializing of your views such as TextView and Imageview.

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // val depplace: TextView
            val arrplace: TextView
            val dep: TextView
            val arr: TextView
            val price: TextView
            var id: Int = 0
            private val context: Context = itemView.context
            private val intent = Intent(context, Auth::class.java)

            init {
                itemView.setOnClickListener {
                    if (token == "-0") {
                        context.startActivity(intent)
                    }
                    else {
                        context.startActivity(Intent(context, Booking::class.java)
                            .putExtra("key", id))
                    }
                }
                // depplace = itemView.findViewById(R.id.depplaceview)
                arrplace = itemView.findViewById(R.id.arrplaceview)
                dep = itemView.findViewById(R.id.deptimeview)
                arr = itemView.findViewById(R.id.arrtimeview)
                price = itemView.findViewById(R.id.pricecardview)
            }
        }

        init {
            this.flightCardArrayList = flightCardArrayList
        }
    }
}