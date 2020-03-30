package kushal.application.coronaupdates

import android.os.Bundle
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

class MainActivity() : AppCompatActivity() {

    lateinit var que: RequestQueue
    val url = "https://api.covid19india.org/data.json"
    lateinit var coroutineContext: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coroutineContext = CoroutineScope(Dispatchers.IO)


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

                val adapter = My_adapter(this@MainActivity, list)

                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = adapter

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

}
