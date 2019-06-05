package club.llyronx.llyrichuarongdao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class WinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (StartActivity.mPlayer != null && !StartActivity.mPlayer.isPlaying()){
            StartActivity.mPlayer.start();
        }
        setContentView(R.layout.activity_win);
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(StartActivity.allTypefaces);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        textTitle.setTextSize(width / 30);
        Intent intent = getIntent();
        if (intent != null) {
            String mapName = intent.getStringExtra("mapname");
            int time = intent.getIntExtra("time", Integer.MAX_VALUE);
            String userRecordFile = "record.raw";
            HashMap<String, Integer> records = new HashMap<>();
            try {
                FileInputStream istream = openFileInput(userRecordFile);
                records = HrdReader.readRecordsFromRawText(istream);
                istream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (!records.containsKey(mapName) || records.get(mapName) > time) {
                    records.put(mapName, time);
                    textTitle.setText(getResources().getText(R.string.win_str) + "\n" + "打破新纪录：" + time + " 秒");
                }
                else {
                    textTitle.setText(R.string.win_str);
                }
                FileOutputStream ostream = openFileOutput(userRecordFile, MODE_PRIVATE);
                recordToOstream(ostream, records);
                ostream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }.start();
    }

    void recordToOstream(OutputStream ostream, HashMap<String, Integer> records) throws IOException {
        DataOutputStream dostream = new DataOutputStream(ostream);
        dostream.writeInt(records.size());
        for (String key:records.keySet()){
            dostream.writeUTF(key);
            dostream.writeInt(records.get(key));
        }
    }
}
