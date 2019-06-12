package club.llyronx.llyrichuarongdao;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class HrdBaseActivity extends AppCompatActivity {

    private static MediaPlayer mPlayer = null;
    private static Typeface allTypeface = null;
    private static final String [] MAPFILENAMES = {
            "maps/小试牛刀.txt",
            "maps/一路进军.txt",
            "maps/一路顺风.txt",
            "maps/兵分三路.txt",
            "maps/围而不歼.txt",
            "maps/将拥曹营.txt",
            "maps/左右布兵.txt",
            "maps/指挥若定.txt",
            "maps/齐头并进.txt",
            "maps/捷足先登.txt",
            "maps/峰回路转.txt",
    };
    private static final String [] MAPNAMES = {
            "小试牛刀",
            "一路进军",
            "一路顺风",
            "兵分三路",
            "围而不歼",
            "将拥曹营",
            "左右布兵",
            "指挥若定",
            "齐头并进",
            "捷足先登",
            "峰回路转",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPlayer == null){
            mPlayer = MediaPlayer.create(this, R.raw.thais);
            mPlayer.setLooping(true);
        }
        mPlayer.start();
        if (allTypeface == null){
            allTypeface = Typeface.createFromAsset(getAssets(), "fonts/font.ttf");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayer.start();
    }

    private boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    public static MediaPlayer getPlayer(){
        return mPlayer;
    }

    public static Typeface getTypeface(){
        return allTypeface;
    }

    public static void setPlayer(MediaPlayer mPlayer) {
        HrdBaseActivity.mPlayer = mPlayer;
    }

    public static String [] getMapFiles(){
        return MAPFILENAMES;
    }

    public static String [] getMapNames(){
        return MAPNAMES;
    }

}
