package com.fastspeedvpn.secureproxy.supervpn.turbo;

import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.INAPPSKUUNIT;
import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.IN_PURCHASE_KEY;
import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.One_Month_Sub;
import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.One_Year_Sub;
import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.PRIMIUM_STATE;
import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.PURCHASETIME;
import static com.fastspeedvpn.secureproxy.supervpn.turbo.utils.BillConfig.Six_Month_Sub;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.aemerse.iap.DataWrappers;
import com.aemerse.iap.IapConnector;
import com.aemerse.iap.SubscriptionServiceListener;
import com.airbnb.lottie.LottieAnimationView;
import com.fastspeedvpn.secureproxy.supervpn.turbo.utils.AdMod;
import com.fastspeedvpn.secureproxy.supervpn.turbo.utils.Preference;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import unified.vpn.sdk.Callback;
import unified.vpn.sdk.RemainingTraffic;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.VpnException;
import unified.vpn.sdk.VpnState;


public abstract class UIActivity extends AppCompatActivity implements View.OnClickListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    public String SKU_DELAROY_MONTHLY;
    public String SKU_DELAROY_SIXMONTH;
    public String SKU_DELAROY_YEARLY;
    public String base64EncodedPublicKey;


    private static InterstitialAd mInterstitialAd;

    @BindView(R.id.server_ip)
    TextView server_ip;
    @BindView(R.id.optimal_server_btn)
    LinearLayout currentServerBtn;
    @BindView(R.id.selected_server)
    TextView selectedServerTextView;
    @BindView(R.id.country_flag)
    ImageView country_flag;
    @BindView(R.id.uploading_speed)
    TextView uploading_speed_textview;
    @BindView(R.id.downloading_speed)
    TextView downloading_speed_textview;
    @BindView(R.id.off)
    LottieAnimationView off;
    @BindView(R.id.on)
    LottieAnimationView on;


//    @BindView(R.id.premium)
//    ImageView premium;
    Preference preference;
    boolean mSubscribedToDelaroy = false;
    boolean connected = false;
    String mDelaroySku = "";
    boolean mAutoRenewEnabled = false;

    private ActionBar actionBar;
    private Toolbar toolbar;


    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };


    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected abstract void checkRemainingTraffic();

    void complain(String message) {
        alert("Error: " + message);
    }

    void alert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }

    // private void unlockdata() {
//        if (mSubscribedToDelaroy) {
//            unlock();
//        } else {
//            preference.setBooleanpreference(PRIMIUM_STATE, false);
//        }
//        if (!preference.isBooleenPreference(PRIMIUM_STATE)) {
//            premium.setVisibility(View.VISIBLE);
//
//        } else {
//            premium.setVisibility(View.GONE);
//
//        }
//
//
//        MobileAds.initialize(this, initializationStatus -> {
//            Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
//            for (String adapterClass : statusMap.keySet()) {
//                AdapterStatus status = statusMap.get(adapterClass);
//                Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
//            }
//            LoadBannerAd();
//            LoadInterstitialAd();
//        });
//
//
//    }

    public void unlock() {
        preference.setBooleanpreference(PRIMIUM_STATE, true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar();
        initNavigationMenu();
        loginToVpn();
//        ImageView img_rate = findViewById(R.id.imgrate);
//        img_rate.setOnClickListener(view -> {
//            Uri uri = Uri.parse("market://details?id=" + UIActivity.this.getPackageName());
//            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//            try {
//                startActivity(goToMarket);
//            } catch (ActivityNotFoundException e) {
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("https://play.google.com/store/apps/details?id=" + UIActivity.this.getPackageName())));
//            }
//        });
//        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageView img_menu = findViewById(R.id.imgmenu1);
//        img_menu.setOnClickListener(view -> startActivity(new Intent(UIActivity.this, MenuActivity.class)));

        preference = new Preference(this);
        if (BuildConfig.USE_IN_APP_PURCHASE) {
            base64EncodedPublicKey = preference.getStringpreference(IN_PURCHASE_KEY, base64EncodedPublicKey);
            SKU_DELAROY_MONTHLY = preference.getStringpreference(One_Month_Sub, SKU_DELAROY_MONTHLY);
            SKU_DELAROY_SIXMONTH = preference.getStringpreference(Six_Month_Sub, SKU_DELAROY_SIXMONTH);
            SKU_DELAROY_YEARLY = preference.getStringpreference(One_Year_Sub, SKU_DELAROY_YEARLY);


            ArrayList<String> nonConsumableKeys = new ArrayList<>();

            ArrayList<String> consumableKeys = new ArrayList<>();

            ArrayList<String> subscriptionKeys = new ArrayList<>();
            subscriptionKeys.add(SKU_DELAROY_MONTHLY);
            subscriptionKeys.add(SKU_DELAROY_SIXMONTH);
            subscriptionKeys.add(SKU_DELAROY_YEARLY);

            IapConnector iapConnector = new IapConnector(this, nonConsumableKeys, consumableKeys, subscriptionKeys, base64EncodedPublicKey, true);

            //unlockdata();
            iapConnector.addSubscriptionListener(new SubscriptionServiceListener() {
                @Override
                public void onSubscriptionRestored(@NonNull DataWrappers.PurchaseInfo purchaseInfo) {
                    Log.e("Subscribe", "yes" + purchaseInfo.getSku());
                    if (purchaseInfo.getSku().equals(SKU_DELAROY_MONTHLY) && purchaseInfo.isAutoRenewing()) {
                        mDelaroySku = SKU_DELAROY_MONTHLY;
                        mAutoRenewEnabled = true;
                        mSubscribedToDelaroy = true;
                    } else if (purchaseInfo.getSku().equals(SKU_DELAROY_SIXMONTH) && purchaseInfo.isAutoRenewing()) {
                        mDelaroySku = SKU_DELAROY_SIXMONTH;
                        mAutoRenewEnabled = true;
                        mSubscribedToDelaroy = true;
                    } else if (purchaseInfo.getSku().equals(SKU_DELAROY_YEARLY) && purchaseInfo.isAutoRenewing()) {
                        mDelaroySku = SKU_DELAROY_YEARLY;
                        mAutoRenewEnabled = true;
                        mSubscribedToDelaroy = true;
                    } else {
                        mDelaroySku = "";
                        mAutoRenewEnabled = false;
                        mSubscribedToDelaroy = false;
                    }

                    if (!mDelaroySku.equals("")) {
                        preference.setStringpreference(INAPPSKUUNIT, mDelaroySku);
                        preference.setLongpreference(PURCHASETIME, purchaseInfo.getPurchaseTime());
                    }
                  //  unlockdata();

                }

                @Override
                public void onSubscriptionPurchased(@NonNull DataWrappers.PurchaseInfo purchaseInfo) {


                }

                @Override
                public void onPricesUpdated(@NonNull Map<String, DataWrappers.SkuDetails> map) {

                }
            });

        } else {
            preference.setBooleanpreference(PRIMIUM_STATE, false);
          //  premium.setVisibility(View.GONE);


            MobileAds.initialize(this, initializationStatus -> {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                }
                LoadInterstitialAd();
                LoadBannerAd();
            });


        }
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isConnected(new Callback<Boolean>() {
                    @Override
                    public void success(@NonNull Boolean aBoolean) {
                        if (aBoolean) {
                            disconnectFromVnp();
                        } else {
                            connectToVpn();
                        }
                    }

                    @Override
                    public void failure(@NonNull VpnException e) {
                    }
                });
            }
        });
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected(new Callback<Boolean>() {
                    @Override
                    public void success(@NonNull Boolean aBoolean) {
                        if (aBoolean) {
                            disconnectFromVnp();
                            off.cancelAnimation();
                        } else {
                            connectToVpn();
                            off.setVisibility(View.VISIBLE);
                            off.animate();
                        }
                    }

                    @Override
                    public void failure(@NonNull VpnException e) {
                    }
                });
            }
        });

    }


    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

//    @OnClick(R.id.premium)
//    public void premiumMenu(View v) {
//        startActivity(new Intent(this, GetPremiumActivity.class));
//    }


    @OnClick(R.id.img_connect)
    public void onConnectBtnClick(View v) {
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectFromVnp();
                } else {
                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }

    @OnClick(R.id.optimal_server_btn)
    public void onServerChooserClick(View v) {
        //showInterstial();
        chooseServer();
    }


    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }


    protected void updateUI() {
        UnifiedSdk.getVpnState(new Callback<VpnState>() {
            @Override
            public void success(@NonNull VpnState vpnState) {
                switch (vpnState) {
                    case IDLE: {
                        Log.e(TAG, "success: IDLE");
                        //   connectionStateTextView.setImageResource(R.drawable.disc);
                        off.setVisibility(View.VISIBLE);
                        off.cancelAnimation();
                        on.setVisibility(View.GONE);
                        /*getip();*/
                        if (connected) {
                            connected = false;
                            on.setVisibility(View.GONE);
                            off.setVisibility(View.VISIBLE);
                            //animate(img_connect, Ondisconnect, 0, false);
                        }
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
//                        ChangeBlockVisibility();
                        uploading_speed_textview.setText("0 KB");
                        downloading_speed_textview.setText("0 KB");

                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
                        Log.e(TAG, "success: CONNECTED");
                        if (!connected) {
                            connected = true;
                            // animate(img_connect, Onconnect, 0, false);
                            off.setVisibility(View.GONE);
                            on.setVisibility(View.VISIBLE);

                        }
                        //   connectionStateTextView.setImageResource(R.drawable.conne);
                        off.setVisibility(View.GONE);
                        on.setVisibility(View.VISIBLE);
                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        // connectionStateTextView.setImageResource(R.drawable.connecting);
                        off.playAnimation();
                    //    ChangeBlockVisibility();
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
                        Log.e(TAG, "success: PAUSED");
                       // ChangeBlockVisibility();
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerTextView.setText(R.string.select_country);
            }
        });
        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                        selectedServerTextView.setText(R.string.select_country);
                        if (!currentServer.equals("")) {
                            Locale locale = new Locale("", currentServer);
                            Resources resources = getResources();
                            String sb = "drawable/" + currentServer.toLowerCase();
                            country_flag.setImageResource(resources.getIdentifier(sb, null, getPackageName()));
                            selectedServerTextView.setText(locale.getDisplayCountry());
                        } else {
                            country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                            selectedServerTextView.setText(R.string.select_country);
                        }
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                country_flag.setImageDrawable(getResources().getDrawable(R.drawable.ic_earth));
                selectedServerTextView.setText(R.string.select_country);
            }
        });
    }

//    private void ChangeBlockVisibility() {
//        if (BuildConfig.USE_IN_APP_PURCHASE) {
//            if (preference.isBooleenPreference(PRIMIUM_STATE)) {
//                premium.setVisibility(View.GONE);
//            } else {
//                premium.setVisibility(View.VISIBLE);
//            }
//        } else {
//            premium.setVisibility(View.GONE);
//        }
//    }

    private void animate(final ImageView imageView, final int images[], final int imageIndex, final boolean forever) {


        int fadeInDuration = 500;
        int timeBetween = 3000;
        int fadeOutDuration = 1000;

        imageView.setVisibility(View.VISIBLE);
        imageView.setImageResource(images[imageIndex]);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(fadeInDuration);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(fadeInDuration + timeBetween);
        fadeOut.setDuration(fadeOutDuration);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);

        animation.setRepeatCount(1);
        imageView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (images.length - 1 > imageIndex) {
                    animate(imageView, images, imageIndex + 1, forever); //Calls itself until it gets to the end of the array
                } else {
                    if (forever) {
                        animate(imageView, images, 0, forever);  //Calls itself to start the animation all over again in a loop if forever = true
                    }
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });
    }


    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        uploading_speed_textview.setText(inString);
        downloading_speed_textview.setText(outString);

    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {

        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

        }
    }

    protected void ShowIPaddera(String ipaddress) {
        server_ip.setText(ipaddress);
    }


    protected void showConnectProgress() {

    }

    protected void hideConnectProgress() {

    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    public void LoadBannerAd() {
        RelativeLayout adContainer = findViewById(R.id.adView);
        if (BuildConfig.GOOGlE_AD) {
            AdMod.buildAdBanner(getApplicationContext(), adContainer, 0, new AdMod.MyAdListener() {
                @Override
                public void onAdClicked() {
                }

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onFaildToLoad(int i) {
                }
            });
        }
    }

    private void LoadInterstitialAd() {
        if (BuildConfig.GOOGlE_AD) {
            Preference preference = new Preference(UIActivity.this);
            if (!preference.isBooleenPreference(PRIMIUM_STATE)) {
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(this, (BuildConfig.GOOGLE_INTERSTITIAL), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.e(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.e("TAG", "The ad was dismissed.");

                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.e("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                mInterstitialAd = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.e(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
            }
        }
    }


    public void showInterstial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(UIActivity.this);
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Master VPN");
//        Tools.setSystemBarColor(this, R.color.green_600);
    }

    DrawerLayout drawer;

    private void initNavigationMenu() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // open drawer at start
//        drawer.openDrawer(GravityCompat.START);
    }

    public void onMenuClick(View view) {

        if (view.getId() == R.id.ic_premium) {
            startActivity(new Intent(this, GetPremiumActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.ic_share) {
            shareApp();
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.ic_privacy) {
            seePrivacy();
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.ic_faq) {
            startActivity(new Intent(this, FaqActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.ic_help) {
            HELP();
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.Select_location) {
            startActivity(new Intent(this, ServerActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        }
        if (view.getId() == R.id.ic_close) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void HELP() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.dev_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.improve_us_body));

        try {
            startActivity(Intent.createChooser(intent, "send mail"));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(UIActivity.this, "No mail app found!!!", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(UIActivity.this, "Unexpected Error!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void seePrivacy() {
        Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
        Intent intent_policy = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent_policy);
    }

    private void shareApp() {
        Intent ishare = new Intent(Intent.ACTION_SEND);
        ishare.setType("text/plain");
        String sAux = "\n" + getResources().getString(R.string.app_name) + "\n\n";
        sAux = sAux + "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
        ishare.putExtra(Intent.EXTRA_TEXT, sAux);
        startActivity(Intent.createChooser(ishare, "choose one"));
    }
}