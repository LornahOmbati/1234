package com.example.secucapture.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.secucapture.R;
import com.example.secucapture.models.driversListModel;

import java.util.ArrayList;
import java.util.List;

public class DriversListAdapter extends ArrayAdapter<driversListModel> implements Filterable {
    private final Context context;
    private Filter driversFilter;
    private List<driversListModel> driversList;
    private final List<driversListModel> origdriversList;

    public DriversListAdapter(Context ctx, List<driversListModel> driversList) {
        super(ctx, R.layout.drivers_list, driversList);
        // TODO Auto-generated constructor stub
        this.driversList = driversList;
        this.context = ctx;
        this.origdriversList = driversList;
    }

    public int getCount() {
        return driversList.size();
    }

    public driversListModel getItem(int position) {
        return driversList.get(position);
    }

    public long getItemId(int position) {
        return driversList.get(position).hashCode();
    }

    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;

        DriversHolder holder = new DriversHolder();

        if (convertView == null){

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.drivers_list, null);

            TextView fullName = (TextView) v.findViewById(R.id.fullName);
            TextView Id_no = (TextView) v.findViewById(R.id.Id_no);
            TextView licence_no = (TextView) v.findViewById(R.id.licence_no);

            holder.fullName = fullName;
            holder.Id_no = Id_no;
            holder.licence_no = licence_no;

            v.setTag(holder);

        }
        else
            holder = (DriversHolder) v.getTag();

        driversListModel sup = driversList.get(position);

        holder.fullName.setText("::- "+sup.full_name);
        holder.Id_no.setText("ID No. "+sup.id_no);
        holder.licence_no.setText("Licence No. "+sup.drivers_licence_no);


        return v;
    }

    private static class DriversHolder{

        public TextView fullName;
        public TextView Id_no;
        public TextView licence_no;

    }

    public void resetData() {
        driversList = origdriversList;
    }


    @Override
    public Filter getFilter() {
        if (driversFilter == null)
            driversFilter = new DriversListAdapter.DriversFilter();
        return driversFilter;
    }

    private class DriversFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            // We implement here the filter logic
            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = origdriversList;
                results.count = origdriversList.size();

            }
            else {
                // We perform filtering operation
                final List<driversListModel> list = origdriversList;
                int count = list.size();
                List<driversListModel> nDriversList = new ArrayList<driversListModel>(count);
                driversListModel filterableString;
                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);

                    if (filterableString.getFull_name().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nDriversList.add(filterableString);

                    if (filterableString.getId_no().toUpperCase().contains(constraint.toString().toUpperCase()))
                        nDriversList.add(filterableString);

                }



                results.values = nDriversList;
                results.count = nDriversList.size();
                //Log.e("VALUES&&&&&&&&&&&&&& ", results.values.toString());

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                driversList = (List<driversListModel>) results.values;
                notifyDataSetChanged();
            }

        }

    }
}
