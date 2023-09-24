package com.tip31.flightmode

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject

class TicketCard (val json: JSONObject, var dep: String, var arr: String,
                  var price: Int, var airport: String, val cancel: Int)

class Tickets : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tickets)
        supportActionBar?.hide() // Скрыть бар с названием

        val pref: SharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        token = pref.getString("token", "-0")

        val recyclerview: RecyclerView = findViewById(R.id.recyclerView)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val flightCardArrayList = runBlocking {
            return@runBlocking getFlights()
        }

        val flightCardAdapter = TicketCardAdapter(flightCardArrayList)
        recyclerview.adapter = flightCardAdapter
    }

    private suspend fun getFlights() = withContext(Dispatchers.Default) {
        val jsonarr = SendPost("""{"token": "$token"}""", "get_tickets").json.getJSONArray("result")
        val flightCardArrayList: ArrayList<TicketCard> = ArrayList()
        for (i in 0 until jsonarr.length()) {
            val opt = jsonarr.getJSONObject(i).getString("opt")
            var cost = 0
            if (opt.substring(0, 1) == "0") {cost += jsonarr.getJSONObject(i).getInt("eco_price")}
            if (opt.substring(0, 1) == "1") {cost += jsonarr.getJSONObject(i).getInt("comf_price")}
            if (opt.substring(0, 1) == "2") {cost += jsonarr.getJSONObject(i).getInt("biz_price")}

            if (opt.substring(1, 2) == "1") {cost += jsonarr.getJSONObject(i).getInt("baggage_price")}
            if (opt.substring(2, 3) == "1") {cost += jsonarr.getJSONObject(i).getInt("pets_price")}
            if (opt.substring(3, 4) == "1") {cost += jsonarr.getJSONObject(i).getInt("windows_price")}
            if (opt.substring(4) == "1") {cost += jsonarr.getJSONObject(i).getInt("insurance_price")}
            flightCardArrayList.add(TicketCard(
                jsonarr.getJSONObject(i),
                jsonarr.getJSONObject(i).getString("departure"),
                jsonarr.getJSONObject(i).getString("arrive"),
                cost,
                jsonarr.getJSONObject(i).getString("airport"),
                jsonarr.getJSONObject(i).getInt("cancel")
            ))
            // ids2.add(jsonarr.getJSONObject(i).getInt("id"))
        }

        return@withContext flightCardArrayList
    }

    // class TicketCardAdapter(private val context: Flights, flightCardArrayList: ArrayList<TicketCard>
    class TicketCardAdapter(flightCardArrayList: ArrayList<TicketCard>):
        RecyclerView.Adapter<TicketCardAdapter.ViewHolder>() {
        private val flightCardArrayList: ArrayList<TicketCard>
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // to inflate the layout for each item of recycler view.
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.fcards, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            // to set data to textview and imageview of each card layout
            val model: TicketCard = flightCardArrayList[position]
            holder.dep.text = model.dep + " МСК"
            holder.arr.text = model.arr + " МСК"
            if (model.cancel == 1) {holder.arrplace.text = model.airport + " (ОТМЕНЕНО)"}
            else {holder.arrplace.text = model.airport}
            holder.price.text = "$" + model.price.toString()
            holder.json = model.json
            holder.cancel = model.cancel
        }

        override fun getItemCount(): Int {
            // this method is used for showing number of card items in recycler view.
            return flightCardArrayList.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // val depplace: TextView
            val arrplace: TextView
            val dep: TextView
            val arr: TextView
            val price: TextView
            var cancel = 0
            lateinit var json: JSONObject
            private val context: Context = itemView.context
            private val intent = Intent(context, Auth::class.java)

            init {
                itemView.setOnClickListener {
                    if (token == "-0") {
                        context.startActivity(intent)
                    }
                    else {
                        context.startActivity(
                            Intent(context, Ticket::class.java)
                                .putExtra("key", json.toString()))
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