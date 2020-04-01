package kushal.application.coronaupdates

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kushal.application.coronaupdates.International.Country
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

    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        holder.firstLetter.background = ContextCompat.getDrawable(myContext, backG[position % 5])

        val date = Date()
        val s = DateFormat.format("dd/MM/yyyy", date.time)

        holder.title.text = list[position].country.toString().trim()
        holder.firstLetter.text = list[position].country.toString()
        holder.totalInfected.text = list[position].totalConfirmed.toString()
        holder.date.text = s.toString()

        holder.itemView.setOnClickListener {
            val text =
                "Last Updated: ${s.toString()}\n" +
                        "Active:             ${list[position].totalConfirmed - list[position].totalDeaths - list[position].totalRecovered}\n" +
                        "Confirmed:      ${list[position].totalConfirmed.toString().trim()}\n" +
                        "Deaths:            ${list[position].totalDeaths.toString().trim()}\n" +
                        "Recovered:      ${list[position].totalRecovered.toString().trim()}"

            val d = AlertDialog.Builder(myContext, R.style.AlertDialogGreen)
            d.setTitle(list[position].country.toString())
            d.setMessage(text)
            d.setCancelable(false)
            d.setPositiveButton("Return") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            d.show()

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}