package com.rafa.rpggame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.rafa.rpggame.R;
import com.rafa.rpggame.models.zones.Zone;
import java.util.List;

public class ZoneAdapter extends ArrayAdapter<Zone> {
    private Context context;
    private List<Zone> zones;

    public ZoneAdapter(Context context, List<Zone> zones) {
        super(context, R.layout.zone_list_item, zones);
        this.context = context;
        this.zones = zones;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.zone_list_item, parent, false);
        }

        Zone zone = zones.get(position);

        TextView nameText = convertView.findViewById(R.id.zone_name);
        TextView levelText = convertView.findViewById(R.id.zone_level);
        TextView staminaText = convertView.findViewById(R.id.zone_stamina);

        nameText.setText(zone.getName());
        levelText.setText("Nivel " + zone.getMinLevel() + " - " + zone.getMaxLevel());
        staminaText.setText("Stamina: " + zone.getStaminaCost());

        return convertView;
    }
}