package kushal.application.covaupdates

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog.*
import kushal.application.covaupdates.International.Country
import java.util.*

class My_adapter_Inter(val myContext: Context, var list: MutableList<Country>) :
    RecyclerView.Adapter<My_viewHolder>() {

    val backG: ArrayList<Int> = arrayListOf(
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4,
        R.drawable.bg5
    )

    init {
        list.sortByDescending { it.totalConfirmed }
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): My_viewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.list, parent, false)
        return My_viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        holder.firstLetter.background = ContextCompat.getDrawable(myContext, backG[position % 5])
        val s = list[position].date.replace("T", " ").replace("Z", " ").trim()

        holder.title.text = list[position].country.toString().trim()
        holder.firstLetter.text = list[position].country.toString().trim()
        holder.totalInfected.text = list[position].totalConfirmed.toString()
        holder.date.text = s.subSequence(0, 10)

        holder.itemView.setOnClickListener {
            val d = Dialog(myContext)
            val inc = list[position].newConfirmed.toString().trim()
            val conf = list[position].totalConfirmed.toString().trim()
            d.let {

                it.setContentView(R.layout.dialog)
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.show()

                it.dia_place.text = list[position].country.toString().trim()
                it.dia_update.text = s
                it.dia_active.text =
                    (list[position].totalConfirmed - list[position].totalDeaths - list[position].totalRecovered).toString()
                it.dia_confirm.text = conf
                it.dia_death.text = list[position].totalDeaths.toString()
                it.dia_recovered.text = list[position].totalRecovered.toString()
                it.dia_increase.text = "+$inc"
                it.dia_increase_percent.text =
                    String.format("%.1f", (inc.toFloat() * 100 / conf.toFloat()))
                it.dia_increase_percent.append("%")
            }

            d.bar_before.post {
                val h = d.bar_before.height
                d.bar_before.animate()
                    .translationY(inc.toFloat() * h / (inc.toFloat() + conf.toFloat())).duration = 1
            }

        }

    }

    fun filter(l: MutableList<Country>) {
        list = l
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

}