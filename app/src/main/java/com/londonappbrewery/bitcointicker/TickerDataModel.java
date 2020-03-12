package com.londonappbrewery.bitcointicker;

import org.json.JSONException;
import org.json.JSONObject;

class TickerDataModel {

    // Member variables
    private String mExchangeRate;

    // Create a TickerDataModel from JSON
    static TickerDataModel fromJson(JSONObject jsonObject) {

        try {
            TickerDataModel tickerData = new TickerDataModel();
            tickerData.mExchangeRate = jsonObject.getString("last");

            return tickerData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    String getExchangeRate() {
        return mExchangeRate;
    }
}
