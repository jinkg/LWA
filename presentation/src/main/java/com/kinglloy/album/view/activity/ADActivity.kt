package com.kinglloy.album.view.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.kinglloy.album.R
import com.kinglloy.album.analytics.Analytics
import com.kinglloy.album.analytics.Event
import kotlinx.android.synthetic.main.activity_ad.*

/**
 * @author jinyalin
 * @since 2017/10/11.
 */
class ADActivity : AppCompatActivity() {
    var mAd: RewardedVideoAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        btnRetry.setOnClickListener { attachAD() }
        btnReview.setOnClickListener {
            Analytics.logEvent(this, Event.WATCH_AD_AGAIN)
            attachAD()
        }

        setSupportActionBar(appBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appBar.setNavigationOnClickListener {
            onBackPressed()
        }

        MobileAds.initialize(this, getString(R.string.app_ad_id))
        attachAD()
    }


    private fun attachAD() {
        content.visibility = View.GONE
        loading.visibility = View.VISIBLE
        retry.visibility = View.GONE

        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (code == ConnectionResult.SUCCESS) {
            attachVideoAD()
        } else {
            attachInterstitialAd()
        }
    }

    private fun attachInterstitialAd() {
        val mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.advance_inter_ad_unit_id)
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLeftApplication() {

            }

            override fun onAdFailedToLoad(code: Int) {
                Analytics.logEvent(this@ADActivity, Event.LOAD_INTER_AD_FAILED, code.toString())
                adLoadFailed()
            }

            override fun onAdClosed() {
                adClose()
            }

            override fun onAdOpened() {
                Analytics.logEvent(this@ADActivity, Event.OPEN_INTER_AD)
            }

            override fun onAdLoaded() {
                mInterstitialAd.show()
            }
        }
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun attachVideoAD() {
        mAd = MobileAds.getRewardedVideoAdInstance(this)
        mAd!!.rewardedVideoAdListener = object : RewardedVideoAdListener {
            override fun onRewardedVideoCompleted() {

            }

            override fun onRewardedVideoAdClosed() {
                adClose()

                mAd?.destroy(this@ADActivity)
            }

            override fun onRewardedVideoAdLeftApplication() {

            }

            override fun onRewardedVideoAdLoaded() {
                mAd?.show()
            }

            override fun onRewardedVideoAdOpened() {
                Analytics.logEvent(this@ADActivity, Event.OPEN_VIDEO_AD)
            }

            override fun onRewarded(p0: RewardItem?) {
                onRewardedVideoAdClosed()
            }

            override fun onRewardedVideoStarted() {

            }

            override fun onRewardedVideoAdFailedToLoad(code: Int) {
                adLoadFailed()
                Analytics.logEvent(this@ADActivity, Event.LOAD_VIDEO_AD_FAILED, code.toString())
            }
        }
        mAd!!.loadAd(getString(R.string.advance_video_ad_unit_id), AdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        mAd?.resume(this)
    }

    override fun onPause() {
        super.onPause()
        mAd?.pause(this)
    }

    override fun onDestroy() {
        mAd?.destroy(this)
        super.onDestroy()
    }

    private fun adClose() {
        content.visibility = View.VISIBLE
        loading.visibility = View.GONE
        retry.visibility = View.GONE
    }

    private fun adLoadFailed() {
        content.visibility = View.GONE
        loading.visibility = View.GONE
        retry.visibility = View.VISIBLE
    }
}