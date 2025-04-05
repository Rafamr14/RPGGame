package com.rafa.rpggame.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.rafa.rpggame.R;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.Equipment;
import com.rafa.rpggame.models.items.EquipmentSlot;
import java.util.Arrays;
import java.util.List;

public class EquipmentAdapter extends BaseAdapter {
    private Context context;
    private Equipment equipment;
    private List<EquipmentSlot> slots;

    public EquipmentAdapter(Context context, Equipment equipment) {
        this.context = context;
        this.equipment = equipment;
        this.slots = Arrays.asList(EquipmentSlot.values());
    }

    @Override
    public int getCount() {
        return slots.size();
    }

    @Override
    public Object getItem(int position) {
        EquipmentSlot slot = slots.get(position);
        return equipment.getItemInSlot(slot);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.equipment_grid_item, parent, false);
        }

        EquipmentSlot slot = slots.get(position);
        EquipableItem item = equipment.getItemInSlot(slot);

        ImageView iconView = convertView.findViewById(R.id.equipment_icon);
        TextView slotNameText = convertView.findViewById(R.id.slot_name);
        TextView itemNameText = convertView.findViewById(R.id.item_name);

        // Configurar icono según slot
        switch (slot) {
            case HEAD:
                iconView.setImageResource(R.drawable.ic_slot_head);
                break;
            case CHEST:
                iconView.setImageResource(R.drawable.ic_slot_chest);
                break;
            case LEGS:
                iconView.setImageResource(R.drawable.ic_slot_legs);
                break;
            case FEET:
                iconView.setImageResource(R.drawable.ic_slot_feet);
                break;
            case HANDS:
                iconView.setImageResource(R.drawable.ic_slot_hands);
                break;
            case MAIN_HAND:
                iconView.setImageResource(R.drawable.ic_slot_mainhand);
                break;
            case OFF_HAND:
                iconView.setImageResource(R.drawable.ic_slot_offhand);
                break;
            case NECK:
                iconView.setImageResource(R.drawable.ic_slot_neck);
                break;
            case RING1:
            case RING2:
                iconView.setImageResource(R.drawable.ic_slot_ring);
                break;
            default:
                iconView.setImageResource(R.drawable.ic_slot_generic);
                break;
        }

        // Configurar textos
        slotNameText.setText(getSlotDisplayName(slot));

        if (item != null) {
            itemNameText.setText(item.getName());

            // Configurar color según rareza
            switch (item.getRarity()) {
                case COMMON:
                    itemNameText.setTextColor(context.getResources().getColor(R.color.rarity_common));
                    break;
                case UNCOMMON:
                    itemNameText.setTextColor(context.getResources().getColor(R.color.rarity_uncommon));
                    break;
                case RARE:
                    itemNameText.setTextColor(context.getResources().getColor(R.color.rarity_rare));
                    break;
                case EPIC:
                    itemNameText.setTextColor(context.getResources().getColor(R.color.rarity_epic));
                    break;
                case LEGENDARY:
                    itemNameText.setTextColor(context.getResources().getColor(R.color.rarity_legendary));
                    break;
            }
        } else {
            itemNameText.setText("Vacío");
            itemNameText.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        }

        return convertView;
    }

    private String getSlotDisplayName(EquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return "Cabeza";
            case CHEST:
                return "Pecho";
            case LEGS:
                return "Piernas";
            case FEET:
                return "Pies";
            case HANDS:
                return "Manos";
            case MAIN_HAND:
                return "Mano Principal";
            case OFF_HAND:
                return "Mano Secundaria";
            case NECK:
                return "Cuello";
            case RING1:
                return "Anillo 1";
            case RING2:
                return "Anillo 2";
            default:
                return slot.toString();
        }
    }

    public void updateEquipment(Equipment newEquipment) {
        this.equipment = newEquipment;
        notifyDataSetChanged();
    }
}