package tictactoe.ngontinhkangkang.com.tictactoe

import androidx.multidex.MultiDexApplication
import com.core.common.Constants
import com.core.utilities.LConnectivityUtil
import com.core.utilities.LUIUtil
import com.data.ActivityData
import com.data.AdmobData
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import com.google.gson.Gson
import com.utils.util.Utils

class LApplication : MultiDexApplication() {
    private val TAG = LApplication::class.java.simpleName

    companion object {
        val gson: Gson = Gson()
    }

    override fun onCreate() {
        super.onCreate()

        Constants.setIsDebug(true)
        Utils.init(this)
        //config admob id
        AdmobData.instance.idAdmobFull = getString(R.string.str_f)
        //config activity transition default
        ActivityData.instance.type = Constants.TYPE_ACTIVITY_TRANSITION_SLIDELEFT

        //config fonts
        LUIUtil.fontForAll = "fonts/font.ttf"

        //big imageview
        BigImageViewer.initialize(GlideImageLoader.with(applicationContext))

        LConnectivityUtil.initOnNetworkChange(applicationContext)
    }
}