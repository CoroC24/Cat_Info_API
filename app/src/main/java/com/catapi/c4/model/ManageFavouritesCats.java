package com.catapi.c4.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.catapi.c4.R;

public class ManageFavouritesCats {

    public static final String FAV_CATS_KEY = "FAV_CATS";

    public static void setValueSharedPref(Context context, String key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getValuesSharedPref(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name), MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void saveFavouritesCats(Context context, String key, String values) {
        String data = getValuesSharedPref(context, key);

        if (values.equals("")) return;

        if (data.length() == 0) {
            setValueSharedPref(context, key, data + values);
        } else {
            String[] favCatsIds = getFavouritesCatsValues(context, key);
            if (favCatsIds != null) {
                for (String ids : favCatsIds) {
                    if (values.equals(ids)) {
                        return;
                    }
                }
            }
            setValueSharedPref(context, key, data + "," + values);
        }
    }

    public static void deleteFavouritesCats(Context context, String key, String values) {
        String data = getValuesSharedPref(context, key);

        if (data.length() == 0) {
            setValueSharedPref(context, key, data);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            String[] favCatsIds = getFavouritesCatsValues(context, key);

            if (favCatsIds != null) {
                for (String ids : favCatsIds) {
                    if (!ids.startsWith(values)) {
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(ids);
                    }
                }
            }
            setValueSharedPref(context, key, String.valueOf(stringBuilder));
        }
    }

    public static String[] getFavouritesCatsValues(Context context, String value) {
        String data = getValuesSharedPref(context, value);
        if (data.length() == 0) {
            return null;
        } else {
            return data.split(",");
        }
    }
}
