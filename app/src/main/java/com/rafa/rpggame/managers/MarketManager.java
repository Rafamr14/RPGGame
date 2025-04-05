package com.rafa.rpggame.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rafa.rpggame.models.market.Market;
import java.lang.reflect.Type;

public class MarketManager {
    private static final String PREF_NAME = "market_prefs";
    private static final String KEY_MARKET = "market_data";

    private static Market market;
    private static Context appContext;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
        loadMarket();
    }

    private static void loadMarket() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String marketJson = prefs.getString(KEY_MARKET, null);
        if (marketJson != null) {
            Type marketType = new TypeToken<Market>(){}.getType();
            market = gson.fromJson(marketJson, marketType);
        } else {
            market = new Market();
        }
    }

    private static void saveMarket() {
        SharedPreferences prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        String marketJson = gson.toJson(market);
        editor.putString(KEY_MARKET, marketJson);
        editor.apply();
    }

    public static Market getMarket() {
        if (market == null) {
            market = new Market();
        }

        return market;
    }

    public static void updateMarket() {
        saveMarket();
    }
}