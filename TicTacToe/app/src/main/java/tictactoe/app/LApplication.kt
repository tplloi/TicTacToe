package tictactoe.app

import com.core.base.BaseApplication
import com.core.common.Constants
import com.core.utilities.LUIUtil
import com.data.ActivityData
import com.data.AdmobData
import tictactoe.R

//TODO theme
//TODO admod
class LApplication : BaseApplication() {

    override fun onCreate() {
        super.onCreate()

        //config admob id
        AdmobData.instance.idAdmobFull = getString(R.string.str_f)

        //config activity transition default
        ActivityData.instance.type = Constants.TYPE_ACTIVITY_TRANSITION_SLIDELEFT

        //config fonts
        LUIUtil.fontForAll = "fonts/font.ttf"

    }
}