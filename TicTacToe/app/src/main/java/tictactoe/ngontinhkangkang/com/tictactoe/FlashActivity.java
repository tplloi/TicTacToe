package tictactoe.ngontinhkangkang.com.tictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import me.wangyuwei.particleview.ParticleView;

public class FlashActivity extends Activity {
    private InterstitialAd interstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //set color navigation & status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        setContentView(R.layout.activity_flash);

        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(getResources().getString(R.string.str_f));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("33F2CB83BAADAD6C").addTestDevice("179198315EB7B069037C5BE8DEF8319A")
                .addTestDevice("7DA8A5B216E868636B382A7B9756A4E6").addTestDevice("58844B2E50AF6E33DC818387CC50E593")
                .addTestDevice("8FA8E91902B43DCB235ED2F6BBA9CAE0").addTestDevice("A1EC01C33BD69CD589C2AF605778C2E6")
                .build();
        interstitial.loadAd(adRequest);

        ParticleView particleView = (ParticleView) findViewById(R.id.particle_view);
        if (particleView != null) {
            particleView.startAnim();
            particleView.setOnParticleAnimListener(new ParticleView.ParticleAnimListener() {
                @Override
                public void onAnimationEnd() {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moveToMainActivity();
                        }
                    }, 500);
                }
            });
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moveToMainActivity();
                }
            }, 1500);
        }
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
        displayInterstitial();
    }

    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
