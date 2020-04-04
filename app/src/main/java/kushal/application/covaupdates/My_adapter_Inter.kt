package kushal.application.covaupdates

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog.*
import kushal.application.covaupdates.International.Country
import java.util.*

class My_adapter_Inter(val myContext: Context, val list: MutableList<Country>) :
    RecyclerView.Adapter<My_viewHolder>() {

    val backG: ArrayList<Int> = arrayListOf(
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4,
        R.drawable.bg5
    )

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): My_viewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.list, parent, false)
        return My_viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        holder.firstLetter.background = ContextCompat.getDrawable(myContext, backG[position % 5])

        val date = Date()
        val s = DateFormat.format("dd/MM/yyyy", date.time)

        holder.title.text = list[position].country.toString().trim()
        holder.firstLetter.text = list[position].country.toString().trim()
        holder.totalInfected.text = list[position].totalConfirmed.toString()
        holder.date.text = s.toString()


        holder.itemView.setOnClickListener {
            val d = Dialog(myContext)
            val inc = list[position].newConfirmed.toString().trim()
            val conf = list[position].totalConfirmed.toString().trim()
            d.let {

                it.setContentView(R.layout.dialog)
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.show()

                it.dia_place.text = list[position].country.toString().trim()
                it.dia_update.text = s.toString()
                it.dia_active.text =
                    (list[position].totalConfirmed - list[position].totalDeaths - list[position].totalRecovered).toString()
                it.dia_confirm.text = conf
                it.dia_death.text = list[position].totalDeaths.toString()
                it.dia_recovered.text = list[position].totalDeaths.toString()
                it.dia_increase.text = "+$inc"

            }

            d.bar_before.post {
                val h = d.bar_before.height
                d.bar_before.animate()
                    .translationY(inc.toFloat() * h / (inc.toFloat() + conf.toFloat())).duration =
                    10
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}