package kushal.application.coronaupdates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter(val myContext: Context, val list: MutableList<Int>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(myContext).inflate(R.layout.list2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.home_image.setImageDrawable(myContext.resources.getDrawable(list[position]))
    }

    override fun getItemCount(): Int = 2

}