package kushal.application.coronaupdates

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class My_adapter(val myContext: Context, val list: MutableList<Statewise>) :
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

        holder.title.text = list[position].state.toString()
        holder.firstLetter.text = list[position].state.toCharArray()[0].toString()
        holder.totalInfected.text = list[position].confirmed.toString()
        holder.date.text = list[position].lastupdatedtime.subSequence(0, 10).toString()

        holder.itemView.setOnClickListener {
            val text =
                "Last Updated: ${list[position].lastupdatedtime.trim()}\n" +
                        "Active:             ${list[position].active.trim()}\n" +
                        "Confirmed:      ${list[position].confirmed.trim()}\n" +
                        "Deaths:            ${list[position].deaths.trim()}\n" +
                        "Recovered:      ${list[position].recovered.trim()}"

            val d = AlertDialog.Builder(myContext, R.style.AlertDialogGreen)
            d.setTitle(list[position].state.toString())
            d.setMessage(text)
            d.setCancelable(false)
            d.setPositiveButton("Return") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
            d.show()

        }


    }

    override fun getItemCount(): Int = list.size

}