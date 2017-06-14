package com.xylife.trip;

import android.content.Context;
import android.taobao.atlas.runtime.AtlasPreLauncher;
import android.util.Log;

/**
 * Created by Lzx on 2017/6/14.
 */

public class AppPreLaunch implements AtlasPreLauncher {
    @Override
    public void initBeforeAtlas(Context context) {
        Log.e("tag", "=========prelaunch invokded");
    }
}
