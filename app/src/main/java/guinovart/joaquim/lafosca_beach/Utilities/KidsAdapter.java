package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import guinovart.joaquim.lafosca_beach.Models.Kid;
import guinovart.joaquim.lafosca_beach.R;

/**
 * Created by perecullera on 23/9/15.
 */
public class KidsAdapter extends BaseAdapter implements Filterable {

    private List<Kid>originalData = null;
    private List<Kid>filteredData = null;
    private LayoutInflater mInflater;

    public KidsAdapter(Context context, ArrayList<Kid> kids) {
        this.filteredData = kids ;
        this.originalData = kids ;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Kid getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Kid kid = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.kid_layout, null);
            TextView nameTV = (TextView) convertView.findViewById(R.id.nameTW);
            TextView ageTV = (TextView) convertView.findViewById(R.id.ageTW);
            nameTV.setText(kid.name);
            ageTV.setText(Integer.toString(kid.age));
        }else{
            convertView = mInflater.inflate(R.layout.kid_layout, null);
            TextView nameTV = (TextView) convertView.findViewById(R.id.nameTW);
            TextView ageTV = (TextView) convertView.findViewById(R.id.ageTW);
            nameTV.setText(filteredData.get(position).getName());
            ageTV.setText(Integer.toString(filteredData.get(position).age));
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //Log.d(Constants.TAG, "**** PUBLISHING RESULTS for: " + constraint);
                filteredData = (ArrayList<Kid>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //Log.d(Constants.TAG, "**** PERFORM FILTERING for: " + constraint);
                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<Kid> list = originalData;

                int count = list.size();
                final ArrayList<Kid> nlist = new ArrayList<Kid>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).getName();
                    if (filterableString.toLowerCase().startsWith(filterString)) {
                        nlist.add(list.get(i));
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }
        };
    }


}
