package com.rafa.rpggame.models.market;

import com.rafa.rpggame.models.UserAccount;
import com.rafa.rpggame.models.items.Item;
import java.util.Date;

public class MarketListing {
    private long id;
    private UserAccount seller;
    private Item item;
    private int price;
    private Date listingDate;
    private Date expiryDate;

    public MarketListing(UserAccount seller, Item item, int price) {
        this.seller = seller;
        this.item = item;
        this.price = price;
        this.listingDate = new Date();
        // 7 días de duración por defecto
        this.expiryDate = new Date(listingDate.getTime() + 7 * 24 * 60 * 60 * 1000);
    }

    public boolean purchase(UserAccount buyer) {
        if (buyer.getCoins() < price) {
            return false;
        }

        // Transferir el ítem
        if (!buyer.getSelectedCharacter().addToInventory(item)) {
            return false; // Inventario lleno
        }

        // Transferir dinero
        buyer.reduceCoins(price);
        seller.addCoins((int)(price * 0.95)); // Tarifa del 5%

        return true;
    }

    public boolean isExpired() {
        return new Date().after(expiryDate);
    }

    // Getters y Setters

    public UserAccount getSeller() {
        return seller;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public long getId() {
        return id;
    }
}