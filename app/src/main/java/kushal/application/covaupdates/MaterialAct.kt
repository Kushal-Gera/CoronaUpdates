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
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
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
    val urlInter = "https://api.covid19api.com/summary"
    val url = "https://api.covid19india.org/data.json"
    val que by lazy {
        Volley.newRequestQueue(this)
    }
    var position: Int = -1
    var searchablelist: ArrayList<SearchModel>? = null
    var dataListInter: MutableList<Country>? = null
    var dataList: MutableList<Statewise>? = null
    var inter = false
    var first = false
    var reloaded = false
    var reloadedInter = false
    var firstInter = false

    var taptargetview = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_material)
        coroutineScope = CoroutineScope(Dispatchers.IO)
        window.statusBarColor = ContextCompat.getColor(this, R.color.purpleDark)


        val shared_pref = getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
        if (shared_pref.getBoolean("first_material_here", true)) {
            taptargetview = true;
            TapTargetSequence(this).targets(
                TapTarget.forView(
                    findViewById(R.id.mat_back),
                    "Tap for going back to Normal Mode"
                )
                    .outerCircleAlpha(0.5f).outerCircleColor(R.color.purpleDark)
                    .titleTextSize(28)
                    .textColor(R.color.white).cancelable(false),
                TapTarget.forView(
                    findViewById(R.id.location_inter),
                    "Switch between National and Global Charts !!"
                )
                    .outerCircleAlpha(0.5f).outerCircleColor(R.color.blue)
                    .titleTextSize(28)
                    .textColor(R.color.white).cancelable(false)
            ).listener(object : TapTargetSequence.Listener {
                override fun onSequenceCanceled(lastTarget: TapTarget?) {
                }

                override fun onSequenceFinish() {
                    shared_pref.edit().putBoolean("first_material_here", false).apply()
                }

                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {
                }

            }).start()
        }

        loadData()

        swiper.setColorSchemeColors(ContextCompat.getColor(this, R.color.purpleDark))

        swiper.setOnRefreshListener {
            if (inter)
                loadDataInter()
            else
                loadData()
            swiper.isRefreshing = false
        }

        toolbar_material.setOnClickListener {
            if (searchablelist == null) {
                return@setOnClickListener
            }

            SimpleSearchDialogCompat(
                this, "Search...", "Type Name...", null, searchablelist,
                SearchResultListener { dialog, item, position ->
                    if (inter)
                        setDataInter(item.title)
                    else
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

            if (inter)
                update_chartInter()
            else
                update_chart()
        }

        mat_back.setOnClickListener {
            val sharedPref = getSharedPreferences("sharedpref", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("normal", true).apply()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        location_inter.setOnClickListener {

            it.animate().rotationBy(3 * 360f).duration = 800

            if (inter) {
                reloaded = true
                loadData()
            } else {
                reloadedInter = true
                loadDataInter()
            }

            inter = !inter
        }
    }

    private fun update_chartInter() {

        if (dataListInter == null || position == -1) return

        val item = dataListInter!![position]

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

    private fun update_chart() {

        if (dataList == null || position == -1) return

        val item = dataList!![position]

        val inc = item.deltaconfirmed.toString().trim()
        val conf = item.confirmed.toString().trim()
        mat_dia_active.text =
            (item.confirmed.toInt() - item.deaths.toInt() - item.recovered.toInt()).toString()
        mat_dia_confirm.text = conf
        mat_dia_death.text = item.deaths.toString()
        mat_dia_recovered.text = item.recovered.toString()
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
    fun setDataInter(mstring: String) {

        var pos = 0
        for (i in dataListInter!!.indices) {
            if (dataListInter!![i].country == mstring)
                pos = i
        }
        position = pos
        val item = dataListInter!![pos]

        val date = item.date.replace("T", " ").replace("Z", " ").trim()

        mat_update.text = "Updated On $date"
        mat_rec.text = item.totalRecovered.toString()
        mat_dead.text = item.totalDeaths.toString()
        mat_infected.text = item.totalConfirmed.toString()
        mat_name.text = item.country.toString()

        update_chartInter()

    }

    @SuppressLint("SetTextI18n")
    fun setData(mstring: String) {

        var pos = 0
        for (i in dataList!!.indices) {
            if (dataList!![i].state == mstring)
                pos = i
        }
        position = pos
        val item = dataList!![pos]

        val date = item.lastupdatedtime.trim()
        mat_update.text = "Updated On $date"
        mat_rec.text = item.recovered.toString()
        mat_dead.text = item.deaths.toString()
        mat_infected.text = item.confirmed.toString()
        mat_name.text = item.state.toString()

        update_chart()

    }

    private fun loadDataInter() {

        swiper.isRefreshing = true
        coroutineScope.launch {

            val request = StringRequest(urlInter, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()
                val users = gson.fromJson(response, ExampleInter::class.java)
                val list = users.countries
                list.removeAt(0)
                inter = true

                dataListInter = list
                searchablelist = transformInter(list)
                swiper.isRefreshing = false
                if (!firstInter or reloadedInter)
                    setDataInter("United States of America")
                firstInter = true
                reloadedInter = false

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

    private fun loadData() {

        swiper.isRefreshing = true
        coroutineScope.launch {

            val request = StringRequest(url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()
                val users = gson.fromJson(response, Example::class.java)
                val list = users.statewise

                inter = false
                dataList = list
                searchablelist = transform(list)
                swiper.isRefreshing = false
                if (!first or reloaded)
                    setData("Total")
                first = true
                reloaded = false

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

    fun transformInter(list: MutableList<Country>): ArrayList<SearchModel> {
        val newList = ArrayList<SearchModel>()
        list.sortByDescending { it.totalConfirmed }

        for (item in list) {
            newList.add(SearchModel(item.country))
        }



        return newList
    }

    fun transform(list: MutableList<Statewise>): ArrayList<SearchModel> {
        val newList = ArrayList<SearchModel>()

        for (item in list) {
            newList.add(SearchModel(item.state))
        }

        return newList
    }

    override fun onBackPressed() {
        if (taptargetview)
            return
        if (swiper.isRefreshing)
            swiper.isRefreshing = false
        if (inter)
            location_inter.performClick()
        else
            super.onBackPressed()
    }

}
