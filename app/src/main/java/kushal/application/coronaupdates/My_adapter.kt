package kushal.application.coronaupdates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class My_adapter(val myContext: Context, val list: MutableList<Statewise>) :
    RecyclerView.Adapter<My_viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): My_viewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.list, parent, false)
        return My_viewHolder(view)
    }

    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        holder.title.text = list[position].state.toString()
        holder.firstLetter.text = list[position].state.toCharArray()[0].toString()

        holder.totalInfected.text = list[position].confirmed.toString()

        holder.date.text = list[position].lastupdatedtime.subSequence(0, 10).toString()

    }

    override fun getItemCount(): Int {
        return list.size
    }

}