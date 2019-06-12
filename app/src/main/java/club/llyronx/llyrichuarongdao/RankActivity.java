package club.llyronx.llyrichuarongdao;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class RankActivity extends HrdBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        TextView mainView = findViewById(R.id.rank_title);
        mainView.setTextSize(width / 50);
        mainView.setTextColor(Color.WHITE);
        mainView.setTypeface(getTypeface());
        LinearLayout llContent = findViewById(R.id.rank_content);
        String userRecordFile = "record.raw";
        HashMap<String, Integer> userInfo = new HashMap<>();
        try {
            FileInputStream istream = openFileInput(userRecordFile);
            userInfo = HrdReader.readRecordsFromRawText(istream);
            istream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String mapName: getMapNames()){
            TextView textView = new TextView(this);
            if (!userInfo.containsKey(mapName)){
                textView.setText(mapName + ": 您还没有通过本关。");
            }
            else {
                textView.setText(mapName + ": 您已经通过本关。最快时间为：" + userInfo.get(mapName) + "秒");
            }
            textView.setPadding(width / 10, width / 50, width / 10, width / 50);
            textView.setTypeface(getTypeface());
            textView.setTextSize(width / 70);
            textView.setTextColor(Color.MAGENTA);
            textView.setGravity(Gravity.CENTER);
            llContent.addView(textView);
        }
    }

    public void goBack(View view) {
        finish();
    }
}
