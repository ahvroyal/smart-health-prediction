package org.ahvroyal.smarthealthprediction;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class SymptomsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SymptomModel> symptomsData;
    private LayoutInflater layoutInflater;
    private SymptomModel symptomModel;

    public SymptomsAdapter(Context context, ArrayList<SymptomModel> symptomsData) {
        this.context = context;
        this.symptomsData = symptomsData;
        layoutInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {;
        return symptomsData.size();
    }

    @Override
    public Object getItem(int position) {
        return symptomsData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return symptomsData.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView = view;

        if(rowView == null) {
            rowView = layoutInflater.inflate(R.layout.symptom_row,parent,false);
        }
        TextView symptomsName = (TextView) rowView.findViewById(R.id.symptomTv);
        CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        symptomModel = symptomsData.get(position);

        symptomsName.setText(symptomModel.getSymptomName());
        checkBox.setChecked(symptomModel.isChecked());
        // Tag is important to get position clicked checkbox
        checkBox.setTag(position);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPos = (int) v.getTag();
                boolean isChecked = !symptomsData.get(currentPos).isChecked();
                Log.d("response ",currentPos+ " "+isChecked);
                symptomsData.get(currentPos).setChecked(isChecked);
                notifyDataSetChanged();
            }
        });
        return rowView;
    }

}