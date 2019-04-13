package com.bignerdranch.android.todoapp;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.ArraySet;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Preferences {

    private static final String IS_SINGLETON_ADDED = "isAdded";
    private static final String SINGLETON_ARRAY = "singletonArray";

    public static boolean isSingletonAdded(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(IS_SINGLETON_ADDED, false);
    }

    public static void setSingletonAdded(Context context, boolean isAdded) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_SINGLETON_ADDED, isAdded)
                .apply();
    }

    public static List<Headline> getSingletonArray(Context context) {
        Set<String> strings = PreferenceManager.getDefaultSharedPreferences(context).getStringSet(SINGLETON_ARRAY, null);
        List<Headline> headlines = new ArrayList<>();
        for (String string : strings) {
            headlines.add(HeadlineLab.get(context).getHeadline(UUID.fromString(string)));
        }

        return headlines;
    }

    public static void setSingletonArray(Context context, List<Headline> singletonHeadlines) {
        Set<String> stringsUUID = new HashSet<>();
        for (Headline headline : singletonHeadlines) {
            stringsUUID.add(headline.getId().toString());
        }

        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putStringSet(SINGLETON_ARRAY, stringsUUID)
                .apply();
    }
}
