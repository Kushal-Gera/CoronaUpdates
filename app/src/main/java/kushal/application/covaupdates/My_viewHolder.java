package kushal.application.covaupdates;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class My_viewHolder extends RecyclerView.ViewHolder {

    TextView title, totalInfected, date,firstLetter;

    My_viewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        firstLetter = itemView.findViewById(R.id.firstLetter);
        date = itemView.findViewById(R.id.date);
        totalInfected = itemView.findViewById(R.id.totalInfected);

    }

}
