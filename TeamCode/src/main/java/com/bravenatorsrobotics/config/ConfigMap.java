package com.bravenatorsrobotics.config;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigMap {

    public static final String PREFERENCES_ID = "RobotPrefIntoTheDeep9533";

    private static SharedPreferences sp = null;

    public static final String ALLIANCE_COLOR = "ALLIANCE_COLOR";
    private static AllianceColor allianceColor = AllianceColor.RED;
    public enum AllianceColor {
        RED, BLUE;

        public static AllianceColor toAllianceColor(String textAllianceColor) {
            try {
                return valueOf(textAllianceColor);
            } catch (Exception e) {
                System.err.println("Could not find value of alliance color");
                return RED;
            }
        }
    }

    public static void load(Context context) {
        if(ConfigMap.sp == null)
            ConfigMap.sp = context.getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);

        allianceColor = AllianceColor.toAllianceColor(sp.getString(ALLIANCE_COLOR, AllianceColor.RED.name()));
    }

    public static void save() {

        if(sp == null) {
            System.err.println("Error trying to save on null shared preferences");
            return;
        }

        SharedPreferences.Editor editor = sp.edit();

        editor.putString(ALLIANCE_COLOR, allianceColor.name());

        editor.apply();
    }

    public static void setAllianceColor(AllianceColor allianceColor) { ConfigMap.allianceColor = allianceColor; }
    public static AllianceColor getAllianceColor() { return ConfigMap.allianceColor; }
}
