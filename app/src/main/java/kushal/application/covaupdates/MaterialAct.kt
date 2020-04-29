package kushal.application.covaupdates

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import kotlinx.android.synthetic.main.activity_material.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kushal.application.covaupdates.International.Country
import kushal.application.covaupdates.International.ExampleInter

class MaterialAct : AppCompatActivity() {

    lateinit var coroutineScope: CoroutineScope
    val url = "https://api.covid19api.com/summary"
    val que by lazy {
        Volley.newRequestQueue(this)
    }
    var position: Int = -1
    var searchablelist: ArrayList<SearchModel>? = null
    var dataList: MutableList<Country>? = null
    var first = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purpleDark)


        loadData()

        swiper.setOnRefreshListener {
            loadData()
            swiper.isRefreshing = false
        }

        toolbar_material.setOnClickListener {
            if (searchablelist == null) {
                return@setOnClickListener
            }

            SimpleSearchDialogCompat(
                this, "Search...", "Country Name...", null, searchablelist,
                SearchResultListener { dialog, item, position ->
                    setData(item.title)
                    dialog.dismiss()
                }).show()

        }

        mat_less.setOnClickListener {
            mat_more.visibility = View.VISIBLE
            mat_less.visibility = View.GONE
            mat_container.visibility = View.GONE
            mat_appbarlayout.setExpanded(true)
        }

        mat_more.setOnClickListener {
            mat_more.visibility = View.GONE
            mat_less.visibility = View.VISIBLE
            mat_container.visibility = View.VISIBLE
            mat_appbarlayout.setExpanded(false)

            update_chart()
        }

        mat_back.setOnClickListener {
            val sharedPref = getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("normal", true).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    private fun update_chart() {

        if (dataList == null || position == -1) return

        val item = dataList!![position]

        val inc = item.newConfirmed.toString().trim()
        val conf = item.totalConfirmed.toString().trim()
        mat_dia_active.text =
            (item.totalConfirmed - item.totalDeaths - item.totalRecovered).toString()
        mat_dia_confirm.text = conf
        mat_dia_death.text = item.totalDeaths.toString()
        mat_dia_recovered.text = item.totalRecovered.toString()
        mat_dia_increase.text = "+$inc"
        mat_dia_increase_percent.text =
            String.format("%.1f", (inc.toFloat() * 100 / conf.toFloat()))
        mat_dia_increase_percent.append("%")

        mat_bar_before.post {
            val h = mat_bar_before.height
            mat_bar_before.animate()
                .translationY(inc.toFloat() * h / (inc.toFloat() + conf.toFloat())).duration = 1
        }
    }

    @SuppressLint("SetTextI18n")
    fun setData(mstring: String) {

        var pos = 0
        for (i in dataList!!.indices) {
            if (dataList!![i].country == mstring)
                pos = i
        }
        position = pos
        val item = dataList!![pos]

        val date = item.date.replace("T", " ").replace("Z", " ").trim()

        mat_update.text = "Updated On $date"
        mat_rec.text = item.totalRecovered.toString()
        mat_dead.text = item.totalDeaths.toString()
        mat_infected.text = item.totalConfirmed.toString()
        mat_name.text = item.country.toString()

        update_chart()

    }

    private fun loadData() {

        swiper.isRefreshing = true
        coroutineScope.launch {

            val request = StringRequest(url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()
                val users = gson.fromJson(response, ExampleInter::class.java)
                val list = users.countries
                list.removeAt(0)

                dataList = list
                searchablelist = transform(list)
                swiper.isRefreshing = false
                if (!first)
                    setData("India")
                first = true

            },
                Response.ErrorListener {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MaterialAct, "Data May Not be Available", Toast.LENGTH_LONG
                        ).show()
                    }
                })

            que.add(request)

        }

    }

    fun transform(list: MutableList<Country>): ArrayList<SearchModel> {
        val newList = ArrayList<SearchModel>()

        for (item in list) {
            newList.add(SearchModel(item.country))
        }

        return newList
    }

    override fun onBackPressed() {
        if (swiper.isRefreshing)
            swiper.isRefreshing = false
        else
            super.onBackPressed()
    }

}
