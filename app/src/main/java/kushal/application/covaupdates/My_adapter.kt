package kushal.application.covaupdates

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog.*

class My_adapter(val myContext: Context, var list: MutableList<Statewise>, val vib: Vibrator) :
    RecyclerView.Adapter<My_viewHolder>() {

    val backG: ArrayList<Int> = arrayListOf(
        R.drawable.bg1,
        R.drawable.bg2,
        R.drawable.bg3,
        R.drawable.bg4,
        R.drawable.bg5
    )

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): My_viewHolder {
        if (vib.hasVibrator())
            vib.cancel()

        val view = LayoutInflater.from(myContext).inflate(R.layout.list, parent, false)
        return My_viewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: My_viewHolder, position: Int) {

        holder.title.text = list[position].state.toString().trim()
        val total = list[position].confirmed.toString()

        if (list[position].state.toString().trim().equals("Total", true)) {

            val animator = ValueAnimator.ofInt(0, total.toInt())
            animator.duration = 2500

            animator.addUpdateListener {
                holder.totalInfected.text = it.animatedValue.toString()
                vib.vibrate(2)
            }
            animator.start()

        } else
            holder.totalInfected.text = total

        holder.firstLetter.background = ContextCompat.getDrawable(myContext, backG[position % 5])
        holder.firstLetter.text = list[position].state.toCharArray()[0].toString()
        holder.totalInfected.text = list[position].confirmed.toString()
        holder.date.text = list[position].lastupdatedtime.subSequence(0, 10).toString()

        holder.itemView.setOnClickListener {
            val d = Dialog(myContext)
            val inc = list[position].deltaconfirmed.toString().trim()
            val conf = list[position].confirmed.trim()
            d.let {

                it.setContentView(R.layout.dialog)
                it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.show()

                it.dia_place.text = list[position].state.toString().trim()
                it.dia_update.text = list[position].lastupdatedtime.trim()
                it.dia_active.text = list[position].active.trim()
                it.dia_confirm.text = conf
                it.dia_death.text = list[position].deaths.trim()
                it.dia_recovered.text = list[position].recovered.trim()
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

    fun filter(l: MutableList<Statewise>) {
        list = l
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

}