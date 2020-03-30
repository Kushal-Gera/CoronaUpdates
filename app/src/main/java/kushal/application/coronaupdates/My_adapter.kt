package kushal.application.coronaupdates

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class My_adapter(val myContext: Context, val data: Utils) : RecyclerView.Adapter<My_viewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): My_viewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.list, parent, false)
        return My_viewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        val list = data.india
        holder.totalInfected.text = list[position].confirmed.toString()

        holder.date.text = list[position].date.toString()

    }

    override fun getItemCount(): Int {
        return data.india.size
    }

}