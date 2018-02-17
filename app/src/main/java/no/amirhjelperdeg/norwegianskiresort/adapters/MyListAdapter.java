package no.amirhjelperdeg.norwegianskiresort.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import no.amirhjelperdeg.norwegianskiresort.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by apple on 1/28/18.
 */

public class MyListAdapter extends BaseAdapter {

    ArrayList<HashMap<String,String>>resortData;

    private LayoutInflater inflater=null;

    Activity activity;

    public MyListAdapter(Activity activity, ArrayList<HashMap<String,String>>resortData)
    {
        this.resortData=resortData;
        this.activity=activity;

        inflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return resortData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view=convertView;

        if(convertView==null)
        {
            view=inflater.inflate(R.layout.list_rows,null);

        }

        TextView resortName=(TextView) view.findViewById(R.id.resort_title);
        TextView slopeDistance=(TextView)view.findViewById(R.id.slope_sub_title);
        TextView  lifts=(TextView)view.findViewById(R.id.slope_sub_lifts);
        TextView chargeAmount=(TextView)view.findViewById(R.id.slope_sub_charges);

        HashMap<String,String> data= resortData.get(position);

        resortName.setText(data.get("name"));
        int km= 0;
        if(data.get("slope")!=null)
            km=Double.valueOf(data.get("slope")).intValue();
        slopeDistance.setText("slope:"+km+" KM");
        lifts.setText("lifts:"+data.get("lifts"));
        chargeAmount.setText("charge: "+data.get("charge"));

        return  view;
    }
}
