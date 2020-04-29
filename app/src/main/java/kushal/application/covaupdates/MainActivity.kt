package kushal.application.covaupdates

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kushal.application.covaupdates.International.Country
import kushal.application.covaupdates.International.ExampleInter
import java.util.*


class MainActivity() : AppCompatActivity() {

    lateinit var que: RequestQueue
    val url = "https://api.covid19india.org/data.json"
    val urlInter = "https://api.covid19api.com/summary"

    lateinit var coroutineContext: CoroutineScope
    var is_domestic = true
    var search_used = false

    lateinit var data_dom: MutableList<Statewise>
    lateinit var data_glob: MutableList<Country>
    var adapter_dom: My_adapter? = null
    var adapter_glob: My_adapter_Inter? = null
    lateinit var searchView: SearchView
    val vib by lazy {
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coroutineContext = CoroutineScope(Dispatchers.IO)
        coroutineContext.async(Dispatchers.Default) {
            sharedPref = getSharedPreferences("sharedpref", Context.MODE_PRIVATE)

            val normal = sharedPref.getBoolean("normal", false)
            if (!normal) {
                startActivity(Intent(this@MainActivity, MaterialAct::class.java))
                finish()
            }

        }.onAwait


        setSupportActionBar(toolbar)

        val list = mutableListOf(R.drawable.prevention, R.drawable.symptoms)
        val adapter = Adapter(this, list)
        recView.adapter = adapter
        recView.addItemDecoration(CirclePagerIndicatorDecoration())
        recView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        que = Volley.newRequestQueue(this)
        loadData(url)

    }

    private fun loadData(url: String) {

        coroutineContext.launch(Dispatchers.Default) {

            val request = StringRequest(url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()

                val users = gson.fromJson(response, Example::class.java)
                val list = users.statewise
                data_dom = list

                val adapter = My_adapter(this@MainActivity, list, vib)
                adapter_dom = adapter

                recyclerView.visibility = RecyclerView.VISIBLE
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = adapter
                progressBar.visibility = ProgressBar.GONE
            },
                Response.ErrorListener {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Internet May Not be Available",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })

            que.add(request)

        }

    }

    private fun loadDataInter(url: String) {

        coroutineContext.launch(Dispatchers.Default) {

            val request = StringRequest(url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()

                val users = gson.fromJson(response, ExampleInter::class.java)
                val list = users.countries
                list.removeAt(0)
                data_glob = list

                val adapter = My_adapter_Inter(this@MainActivity, list)
                adapter_glob = adapter

                recyclerView.visibility = RecyclerView.VISIBLE
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = adapter
                progressBar.visibility = ProgressBar.GONE
            },
                Response.ErrorListener {
                    launch(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Internet May Not be Available",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })

            que.add(request)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        searchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.queryHint = "Search By Name..."
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.setOnCloseListener {
            search_used = false
            recView.visibility = RecyclerView.VISIBLE
            searchView.onActionViewCollapsed()
            return@setOnCloseListener true
        }

        searchView.setOnSearchClickListener {
            search_used = true
            recView.visibility = RecyclerView.GONE
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { filter(it) }
                return true
            }

        })

        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.switchTab) {

            recyclerView.visibility = RecyclerView.GONE
            progressBar.visibility = ProgressBar.VISIBLE

            if (!is_domestic) {
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_business_black_24dp)
                Toast.makeText(this, "National Selected", Toast.LENGTH_LONG).show()
                tv.text = "Know About India's Health ?"
                loadData(url)

            } else {
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_home_black_24dp)
                tv.text = "Global Health ?"
                loadDataInter(urlInter)
                Toast.makeText(this, "International Selected", Toast.LENGTH_LONG).show()
            }

            is_domestic = !is_domestic

        } else if (item.itemId == R.id.material) {
            sharedPref.edit().putBoolean("normal", false).apply()
            startActivity(Intent(this, MaterialAct::class.java))
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    fun filter(s: String) {
        if (is_domestic) {
            adapter_dom?.let {

                val newList = mutableListOf<Statewise>()
                for (state in data_dom) {
                    if (state.state.toString().toLowerCase(Locale.getDefault()).contains(s))
                        newList.add(state)
                }

                it.filter(newList)
            }

        } else {
            adapter_glob?.let {

                val newList = mutableListOf<Country>()

                for (country in data_glob) {
                    if (country.country.toString().toLowerCase(Locale.getDefault()).contains(s))
                        newList.add(country)
                }

                it.filter(newList)
            }
        }
    }

    override fun onBackPressed() {

        if (search_used) {
            recView.visibility = RecyclerView.VISIBLE
            searchView.onActionViewCollapsed()
            search_used = false
            return
        }

        if (!is_domestic) {
            findViewById<TextView>(R.id.switchTab).performClick()
            return
        }

        super.onBackPressed()
    }

}
