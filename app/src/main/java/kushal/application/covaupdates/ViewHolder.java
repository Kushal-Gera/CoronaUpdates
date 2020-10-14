package kushal.application.covaupdates;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ViewHolder extends RecyclerView.ViewHolder {

    ImageView home_image;

    ViewHolder(@NonNull View itemView) {
        super(itemView);
        home_image = itemView.findViewById(R.id.home_image);
    }

}
