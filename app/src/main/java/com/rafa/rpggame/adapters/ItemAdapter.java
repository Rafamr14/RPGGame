package com.rafa.rpggame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rafa.rpggame.R;
import com.rafa.rpggame.models.items.ConsumableItem;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.Item;
import java.util.List;

public class ItemAdapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> items;

    public ItemAdapter(Context context, List<Item> items) {
        super(context, R.layout.item_list_item, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_item, parent, false);
        }

        Item item = items.get(position);

        ImageView iconView = convertView.findViewById(R.id.item_icon);
        TextView nameText = convertView.findViewById(R.id.item_name);
        TextView descText = convertView.findViewById(R.id.item_description);
        TextView rarityText = convertView.findViewById(R.id.item_rarity);

        // Configurar icono según tipo de ítem
        if (item instanceof EquipableItem) {
            EquipableItem equip = (EquipableItem) item;

            switch (equip.getSlot()) {
                case HEAD:
                    iconView.setImageResource(R.drawable.ic_item_helmet);
                    break;
                case CHEST:
                    iconView.setImageResource(R.drawable.ic_item_armor);
                    break;
                case MAIN_HAND:
                    iconView.setImageResource(R.drawable.ic_item_weapon);
                    break;
                // Otros slots...
                default:
                    iconView.setImageResource(R.drawable.ic_item_equipment);
                    break;
            }
        } else if (item instanceof ConsumableItem) {
            iconView.setImageResource(R.drawable.ic_item_potion);
        } else {
            iconView.setImageResource(R.drawable.ic_item_generic);
        }

        nameText.setText(item.getName());
        descText.setText(item.getDescription());

        // Configurar color según rareza
        rarityText.setText(item.getRarity().toString());

        // Asegurarse de que se usan los colores correctos definidos en colors.xml
        int colorResId;
        switch (item.getRarity()) {
            case COMMON:
                colorResId = R.color.rarity_common;
                break;
            case UNCOMMON:
                colorResId = R.color.rarity_uncommon;
                break;
            case RARE:
                colorResId = R.color.rarity_rare;
                break;
            case EPIC:
                colorResId = R.color.rarity_epic;
                break;
            case LEGENDARY:
                colorResId = R.color.rarity_legendary;
                break;
            default:
                colorResId = android.R.color.black;
                break;
        }

        // Asegurarse de que se aplica el color correctamente
        rarityText.setTextColor(context.getResources().getColor(colorResId));

        // También colorear el nombre del ítem según su rareza para hacerlo más visible
        nameText.setTextColor(context.getResources().getColor(colorResId));

        return convertView;
    }

    public void updateItems(List<Item> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }
}