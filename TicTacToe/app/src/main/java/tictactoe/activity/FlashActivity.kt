package tictactoe.activity

import android.content.Intent
import android.os.Bundle
import com.annotation.IsFullScreen
import com.annotation.LogTag
import com.core.base.BaseFontActivity
import com.core.utilities.LActivityUtil
import com.core.utilities.LSocialUtil
import com.core.utilities.LUIUtil
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.views.setSafeOnClickListener
import kotlinx.android.synthetic.main.activity_flash.*
import tictactoe.R

@LogTag("FlashActivity")
@IsFullScreen(false)
class FlashActivity : BaseFontActivity() {
    private var interstitialAd: InterstitialAd? = null

    override fun setLayoutResourceId(): Int {
        return R.layout.activity_flash
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LUIUtil.createAdFull(
                context = this,
                onAdLoaded = {
                    interstitialAd = it
                },
                onAdFailedToLoad = {
                    logE("createAdFull onAdFailedToLoad ${it.message}")
                }
        )
        setupViews()
    }

    private fun setupViews() {
        btPlay.setSafeOnClickListener {
            moveToMainActivity()
        }

        btChangeTheme.setSafeOnClickListener {
            val isDarkTheme = LUIUtil.isDarkTheme()
            LUIUtil.setDarkTheme(isDarkTheme = !isDarkTheme)
            finish()
            startActivity(Intent(this, FlashActivity::class.java))
            overridePendingTransition(0, 0)
        }

        btRate.setSafeOnClickListener {
            LSocialUtil.rateApp(activity = this, packageName = packageName)
        }

        btMore.setSafeOnClickListener {
            LSocialUtil.moreApp(activity = this)
        }
    }

    private fun moveToMainActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        LActivityUtil.tranIn(this)
        displayInterstitial()
    }

    private fun displayInterstitial() {
        LUIUtil.displayInterstitial(activity = this, interstitial = interstitialAd, maxNumber = 70)
    }
}