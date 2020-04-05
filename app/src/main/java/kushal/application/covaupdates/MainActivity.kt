package kushal.application.covaupdates

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kushal.application.covaupdates.International.ExampleInter

class MainActivity() : AppCompatActivity() {

    lateinit var que: RequestQueue
    val url = "https://api.covid19india.org/data.json"
    val urlInter = "https://api.covid19api.com/summary"
    val INTERSTITIAL_ID =
        "ca-app-pub-5073642246912223/8824671181"   // news hunter one
    //        "ca-app-pub-5073642246912223/4646545920" // old one

    lateinit var coroutineContext: CoroutineScope
    var is_domestic = true
    val interstitialAd by lazy {
        InterstitialAd(this)
    }

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

            val d = AlertDialog.Builder(this, R.style.AlertDialogGreen)
            d.setTitle("Want Global Charts ?")
            d.setMessage("To see global charts\nSelect the icon from the menu box.\nVice-Versa for National Charts\n")
            d.setCancelable(true)
            d.setPositiveButton("Understood") { dialogInterface: DialogInterface, i: Int ->
                sh.edit().putBoolean("isFirst", false).apply()
            }
            d.setNegativeButton("Cancel") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            d.show()
        }


//      ad stuff here
        interstitialAd.adUnitId = INTERSTITIAL_ID
        interstitialAd.loadAd(AdRequest.Builder().build())
        interstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                val h = Handler()
                h.postDelayed({
                    interstitialAd.loadAd(
                        AdRequest.Builder().build()
                    )
                }, 60 * 1000.toLong())
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                interstitialAd.show()
            }
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
                val adapter = My_adapter_Inter(this@MainActivity, list)

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

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.switchTab) {

            if (interstitialAd.isLoaded) {
                interstitialAd.show()
            } else {
                interstitialAd.loadAd(AdRequest.Builder().build())
                interstitialAd.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        super.onAdClosed()
                    }

                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        interstitialAd.show()
                    }
                }
            }

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
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!is_domestic) {

            findViewById<TextView>(R.id.switchTab).performClick()
            return
        }

        super.onBackPressed()
    }

}
