package guinovart.joaquim.lafosca_beach.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import guinovart.joaquim.lafosca_beach.Models.Kid;
import guinovart.joaquim.lafosca_beach.R;

/**
 * Created by perecullera on 23/9/15.
 */
public class KidsAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    Context context;

    private  List <Kid> arraylist;
    private ArrayList<Kid> kidList;

    public KidsAdapter(Context context, ArrayList<Kid> kids) {
        this.context=context;
        this.arraylist=kids;
        inflater = LayoutInflater.from(context);
        this.kidList=new ArrayList<>();
        this.kidList.addAll(arraylist);

    }

    @Override
    public int getCount() {
        return arraylist.size();
    }

    @Override
    public Kid getItem(int position) {
        return arraylist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Kid kid = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.kid_layout, null);
            TextView nameTV = (TextView) convertView.findViewById(R.id.nameTW);
            TextView ageTV = (TextView) convertView.findViewById(R.id.ageTW);
            nameTV.setText(kid.name);
            ageTV.setText(Integer.toString(kid.age));
        }
        return convertView;
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist.clear();
        if (charText.length() == 0) {
            arraylist.addAll(kidList);
        }
        else
        {
            for (Kid kid : kidList)
            {
                if (kid.getName().toLowerCase(Locale.getDefault()).startsWith(charText))
                {
                    arraylist.add(kid);
                }
            }
        }
        notifyDataSetChanged();
    }
}
