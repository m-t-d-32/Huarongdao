package club.llyronx.llyrichuarongdao;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RandomActivity extends AppCompatActivity {

    private byte [] mapdata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(StartActivity.allTypefaces);
        textTitle.setText(R.string.random_str);
        textTitle.setTextColor(Color.YELLOW);
        final Thread randomThread = new Thread() {
            @Override
            public void run() {
                HrdMap map = null;
                int i = 0;
                while (map == null){
                    ++i;
                    map = HrdMapAutoGenerator.randomGenerateFromWin(HrdMapAutoGenerator.randomGenerateWin());
                }
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                    map.printToOstream(new DataOutputStream(bos));
                    mapdata = bos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        randomThread.start();
        final Thread processThread = new Thread() {
            @Override
            public void run() {
                ChangeActivity.clipbackground(RandomActivity.this);
            }
        };
        processThread.start();
        final Intent now = getIntent();
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    randomThread.join();
                    processThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RandomActivity.this, MainActivity.class);
                intent.putExtra("map", mapdata);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
