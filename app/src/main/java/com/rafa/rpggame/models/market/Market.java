package com.rafa.rpggame.models.market;

import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.character.CharacterClass;
import com.rafa.rpggame.models.items.EquipableItem;
import com.rafa.rpggame.models.items.Item;
import com.rafa.rpggame.models.items.ItemRarity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Market {
    private List<MarketListing> listings;

    public Market() {
        this.listings = new ArrayList<>();
    }

    public List<MarketListing> searchListings(String query, ItemRarity minRarity,
                                              int minLevel, int maxPrice,
                                              CharacterClass forClass) {
        List<MarketListing> results = new ArrayList<>();

        for (MarketListing listing : listings) {
            if (listing.isExpired()) {
                continue;
            }

            Item item = listing.getItem();

            // Filtros
            if (minRarity != null && item.getRarity().ordinal() < minRarity.ordinal()) {
                continue;
            }

            if (minLevel > 0 && item.getRequiredLevel() < minLevel) {
                continue;
            }

            if (maxPrice > 0 && listing.getPrice() > maxPrice) {
                continue;
            }

            if (forClass != null && item instanceof EquipableItem) {
                EquipableItem equipable = (EquipableItem) item;
                if (!equipable.canBeEquippedBy(forClass)) {
                    continue;
                }
            }

            if (query != null && !query.isEmpty()) {
                if (!item.getName().toLowerCase().contains(query.toLowerCase())) {
                    continue;
                }
            }

            results.add(listing);
        }

        return results;
    }

    public boolean addListing(UserAccount seller, Item item, int price) {
        if (!seller.getSelectedCharacter().getInventory().contains(item)) {
            return false;
        }

        // Remover el ítem del inventario
        seller.getSelectedCharacter().getInventory().remove(item);

        // Crear el listing
        MarketListing listing = new MarketListing(seller, item, price);
        listings.add(listing);

        return true;
    }

    public boolean cancelListing(UserAccount seller, MarketListing listing) {
        if (listing.getSeller().getId() != seller.getId()) {
            return false;
        }

        // Devolver el ítem al inventario
        if (!seller.getSelectedCharacter().addToInventory(listing.getItem())) {
            return false; // Inventario lleno
        }

        // Eliminar el listing
        listings.remove(listing);

        return true;
    }

    public void cleanExpiredListings() {
        List<MarketListing> expiredListings = new ArrayList<>();

        for (MarketListing listing : listings) {
            if (listing.isExpired()) {
                expiredListings.add(listing);

                // Devolver ítem al vendedor
                UserAccount seller = listing.getSeller();
                Item item = listing.getItem();

                // Si el inventario está lleno, enviarlo a un "buzón" especial
                if (!seller.getSelectedCharacter().addToInventory(item)) {
                    seller.addToMailbox(item);
                }
            }
        }

        listings.removeAll(expiredListings);
    }

    public List<MarketListing> getListingsBySeller(UserAccount seller) {
        List<MarketListing> sellerListings = new ArrayList<>();

        for (MarketListing listing : listings) {
            if (listing.getSeller().getId() == seller.getId()) {
                sellerListings.add(listing);
            }
        }

        return sellerListings;
    }

    public List<MarketListing> getAllListings() {
        return listings;
    }
}