package com.fastspeedvpn.secureproxy.supervpn.turbo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class BillConfig {
    private static final String PREF_NAME = "snow-intro-slider";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;
    public static final String INAPPSKUUNIT = "inappskuunit";
    public static final String PURCHASETIME = "purchasetime";
    public static final String PRIMIUM_STATE = "primium_state";//boolean

    public static final String COUNTRY_DATA = "Country_data";
    public static final String BUNDLE = "Bundle";
    public static final String SELECTED_COUNTRY = "selected_country";

    public static final String IN_PURCHASE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApjNtQn4c03Rz0TSuF7GawAFP6C5UAW/Ap9wiF6v0z0rZpReY/IVdORbDGjfSWbHb0q884E0e70un+iDJenmtdIggghuM4hv4FVOvFN0dh5smFJeva5Q/kcf9XoQv0o5bQ+HcICXPuXKFqmqUrcHJ0GqaRA6mk0qEa3RM98jn3gnhROeyTvNe9dzr8ZQWfcdvqs3d39a28PcJIuzypFcrmECWeWwWw8r9whOQtXZOmIPInbHyCCn3lEqemQhbsMwj7OhltNjB6NNE9NPhNXhKhxx9re1PXJoSvom7lnMrS0mZQy6owhXSgwAFMdgWP1mhXqYky2RKJRnA53z2CkgnywIDAQAB";
    public static final String One_Month_Sub = "oll_feature_for_onemonth";
    public static final String Six_Month_Sub = "oll_feature_for_sixmonth";
    public static final String One_Year_Sub = "oll_feature_for_year";


    public BillConfig(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

}