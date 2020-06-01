package kushal.application.covaupdates;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class My_viewHolder extends RecyclerView.ViewHolder {

    TextView title, totalInfected, date,firstLetter;
    ImageView updated;
    LinearLayout container;

    My_viewHolder(@NonNull View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.title);
        container = itemView.findViewById(R.id.container);
        firstLetter = itemView.findViewById(R.id.firstLetter);
        date = itemView.findViewById(R.id.date);
        totalInfected = itemView.findViewById(R.id.totalInfected);
        updated = itemView.findViewById(R.id.updated);

    }

}
