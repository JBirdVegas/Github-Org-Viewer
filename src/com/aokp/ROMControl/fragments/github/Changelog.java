package com.aokp.ROMControl.fragments.github;

import android.preference.Preference;
import android.preference.PreferenceCategory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: jbird
 * Date: 11/1/12
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Changelog {
    public Changelog() {

    }

    public static long parseDate(String stamp) {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(utc);
        GregorianCalendar commitCal = new GregorianCalendar(utc);

        try {
            commitCal.setTime(sdf.parse(
                /* work around as timezone parsing failes */
                stamp.substring(0, stamp.length() - 6)));
        } catch (ParseException pe) {
            // failed to parse assume it happened in the past
            commitCal.setTime(new Date(0));
        }
        return commitCal.getTimeInMillis();
    }

    public static boolean alreadyFoundCommit(PreferenceCategory category, Preference pref) {
        boolean same = false;
        for (int i = 0; category.getPreferenceCount() > i; i++) {
                Preference mCatPref = category.getPreference(i);
            same = (pref.compareTo(mCatPref) == 0);
            if (same) break;
        }
        return same;
    }
}
