package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import guinovart.joaquim.lafosca_beach.Models.Kid;
import guinovart.joaquim.lafosca_beach.R;

/**
 * Created by perecullera on 23/9/15.
 */
public class KidsAdapter extends ArrayAdapter<Kid> {

    public KidsAdapter(Context context, ArrayList<Kid> kids) {
        super(context, 0, kids);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Kid kid = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.kid_layout, parent, false);
        }
        // Lookup view for data population
        TextView nameTV = (TextView) convertView.findViewById(R.id.nameTW);
        TextView ageTV = (TextView) convertView.findViewById(R.id.ageTW);
        // Populate the data into the template view using the data object
        nameTV.setText(kid.name);
        ageTV.setText(Integer.toString(kid.age));
        // Return the completed view to render on screen
        return convertView;
    }
}
