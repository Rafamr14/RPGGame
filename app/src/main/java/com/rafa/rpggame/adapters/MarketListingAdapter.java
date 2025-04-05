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
import com.rafa.rpggame.models.market.MarketListing;
import java.util.List;

public class MarketListingAdapter extends ArrayAdapter<MarketListing> {
    private Context context;
    private List<MarketListing> listings;

    public MarketListingAdapter(Context context, List<MarketListing> listings) {
        super(context, R.layout.market_listing_item, listings);
        this.context = context;
        this.listings = listings;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.market_listing_item, parent, false);
        }

        MarketListing listing = listings.get(position);
        Item item = listing.getItem();

        ImageView itemIcon = convertView.findViewById(R.id.item_icon);
        TextView itemNameText = convertView.findViewById(R.id.item_name);
        TextView sellerText = convertView.findViewById(R.id.seller_name);
        TextView priceText = convertView.findViewById(R.id.price_text);
        TextView rarityText = convertView.findViewById(R.id.rarity_text);

        // Configurar icono según tipo de ítem (mismo código que en ItemAdapter)
        if (item instanceof EquipableItem) {
            EquipableItem equip = (EquipableItem) item;

            switch (equip.getSlot()) {
                case HEAD:
                    itemIcon.setImageResource(R.drawable.ic_item_helmet);
                    break;
                case CHEST:
                    itemIcon.setImageResource(R.drawable.ic_item_armor);
                    break;
                case MAIN_HAND:
                    itemIcon.setImageResource(R.drawable.ic_item_weapon);
                    break;
                default:
                    itemIcon.setImageResource(R.drawable.ic_item_equipment);
                    break;
            }
        } else if (item instanceof ConsumableItem) {
            itemIcon.setImageResource(R.drawable.ic_item_potion);
        } else {
            itemIcon.setImageResource(R.drawable.ic_item_generic);
        }

        itemNameText.setText(item.getName());
        sellerText.setText("Vendedor: " + listing.getSeller().getUsername());
        priceText.setText(listing.getPrice() + " monedas");

        // Configurar color según rareza
        rarityText.setText(item.getRarity().toString());
        switch (item.getRarity()) {
            case COMMON:
                rarityText.setTextColor(context.getResources().getColor(R.color.rarity_common));
                break;
            case UNCOMMON:
                rarityText.setTextColor(context.getResources().getColor(R.color.rarity_uncommon));
                break;
            case RARE:
                rarityText.setTextColor(context.getResources().getColor(R.color.rarity_rare));
                break;
            case EPIC:
                rarityText.setTextColor(context.getResources().getColor(R.color.rarity_epic));
                break;
            case LEGENDARY:
                rarityText.setTextColor(context.getResources().getColor(R.color.rarity_legendary));
                break;
        }

        return convertView;
    }

    public void updateListings(List<MarketListing> newListings) {
        this.listings.clear();
        this.listings.addAll(newListings);
        notifyDataSetChanged();
    }
}