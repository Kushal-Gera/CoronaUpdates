package kushal.application.coronaupdates

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kushal.application.coronaupdates.International.ExampleInter

class MainActivity() : AppCompatActivity() {

    lateinit var que: RequestQueue
    val url = "https://api.covid19india.org/data.json"
    val urlInter = "https://api.covid19api.com/summary"
    lateinit var coroutineContext: CoroutineScope
    var is_domestic = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coroutineContext = CoroutineScope(Dispatchers.IO)

        val list = mutableListOf(R.drawable.prevention, R.drawable.symptoms)
        val adapter = Adapter(this, list)
        recView.adapter = adapter
        recView.addItemDecoration(CirclePagerIndicatorDecoration())
        recView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        que = Volley.newRequestQueue(this)
        loadData(url)

        val sh = getSharedPreferences("shared", Context.MODE_PRIVATE)
        if (sh.getBoolean("isFirst", true)) {

        }

    }

    private fun loadData(url: String) {

        coroutineContext.launch(Dispatchers.Default) {

            val request = StringRequest(url, Response.Listener { response ->

                val builder = GsonBuilder()
                val gson = builder.create()

                val users = gson.fromJson(response, Example::class.java)
                val list = users.statewise

                val adapter = My_adapter(this@MainActivity, list)

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
                val adapter = My_adapter_Inter(this@MainActivity, list)

                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = adapter
//                adapter.notifyDataSetChanged()
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

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.switchTab) {
            progressBar.visibility = ProgressBar.VISIBLE
            recyclerView.removeAllViews()

            if (!is_domestic) {
                Toast.makeText(this, "National Selected", Toast.LENGTH_LONG).show()
                tv.text = "Know About India's Count ?"
                loadData(url)

            } else {
                tv.text = "International Count ?"
                loadDataInter(urlInter)
                Toast.makeText(this, "International Selected", Toast.LENGTH_LONG).show()
            }

            is_domestic = !is_domestic
        }

        return super.onOptionsItemSelected(item)
    }

}
