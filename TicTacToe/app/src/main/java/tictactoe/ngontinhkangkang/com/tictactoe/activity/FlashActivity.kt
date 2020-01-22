package tictactoe.ngontinhkangkang.com.tictactoe.activity

import android.content.Intent
import android.os.Bundle
import com.core.base.BaseFontActivity
import com.core.utilities.LActivityUtil
import com.core.utilities.LUIUtil
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import kotlinx.android.synthetic.main.activity_flash.*
import tictactoe.ngontinhkangkang.com.tictactoe.R

class FlashActivity : BaseFontActivity() {
    private var interstitial: InterstitialAd? = null

    override fun setFullScreen(): Boolean {
        return false
    }

    override fun setLayoutResourceId(): Int {
        return R.layout.activity_flash
    }

    override fun setTag(): String? {
        return javaClass.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        interstitial = InterstitialAd(this)
        interstitial?.adUnitId = resources.getString(R.string.str_f)
        val adRequest = AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("33F2CB83BAADAD6C").addTestDevice("179198315EB7B069037C5BE8DEF8319A")
                .addTestDevice("7DA8A5B216E868636B382A7B9756A4E6").addTestDevice("58844B2E50AF6E33DC818387CC50E593")
                .addTestDevice("8FA8E91902B43DCB235ED2F6BBA9CAE0").addTestDevice("A1EC01C33BD69CD589C2AF605778C2E6")
                .build()
        interstitial?.loadAd(adRequest)

        /*LUIUtil.setDelay(1000, Runnable {
            moveToMainActivity()
        })*/
    }

    private fun moveToMainActivity() {
        val intent = Intent(activity, GameActivity::class.java)
        startActivity(intent)
        LActivityUtil.tranIn(activity)
        finish()
        displayInterstitial()
    }

    private fun displayInterstitial() {
        if (interstitial?.isLoaded == true) {
            interstitial?.show()
        }
    }
}