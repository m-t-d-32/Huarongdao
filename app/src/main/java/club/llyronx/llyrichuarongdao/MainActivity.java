package club.llyronx.llyrichuarongdao;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private HrdMap mMap;
    private HrdConvas mConvas;

    private void initializeButtonViews(TextView view, String text){
        //Button extends TextView
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        int everyHeight = ((height - mConvas.getTrueHeight()) - mConvas.getTrueBorder() * 2) / 2;
        int everyWidth = (mConvas.getTrueWidth() - mConvas.getTrueBorder() * 2) / 3;
        view.setBackgroundColor(Color.TRANSPARENT);
        view.setHeight(everyHeight);
        view.setWidth(everyWidth);
        view.setTypeface(StartActivity.allTypefaces);
        view.setTextSize(everyWidth / 10);
        view.setTextColor(Color.LTGRAY);
        view.setGravity(Gravity.CENTER);
        view.setText(text);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            LinearLayout mainLayout = new LinearLayout(this);
            mainLayout.setOrientation(LinearLayout.VERTICAL);
            setContentView(mainLayout);
            mConvas = new HrdConvas(this);
            mainLayout.addView(mConvas);
            if (ChangeActivity.mNextBackground != null) {
                getWindow().setBackgroundDrawable(ChangeActivity.mNextBackground);
            }

            if (savedInstanceState == null){
                savedInstanceState = getIntent().getExtras();
            }
            if (savedInstanceState.containsKey("map")){
                byte [] mapdata = savedInstanceState.getByteArray("map");
                mMap = new HrdMap(new DataInputStream(new ByteArrayInputStream(mapdata)));
            }
            else {
                String filename = savedInstanceState.getString("filename");
                mMap = HrdReader.readMapFromPlainText(getAssets().open(filename));
            }

            Button buttonRollback = new Button(this);
            Chronometer timeDisplayer = new Chronometer(this);
            Button buttonRecover = new Button(this);
            mConvas.setUp(mMap, timeDisplayer);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int height = dm.heightPixels;
            int everyHeight = ((height - mConvas.getTrueHeight()) - mConvas.getTrueBorder() * 2) / 2;
            int everyWidth = (mConvas.getTrueWidth() - mConvas.getTrueBorder() * 2) / 3;
            FrameLayout.LayoutParams lpall = (FrameLayout.LayoutParams) mainLayout.getLayoutParams();
            lpall.setMargins(mConvas.getTrueBorder(), everyHeight / 3, mConvas.getTrueBorder(), 0);
            LinearLayout downLayout = new LinearLayout(this);
            mainLayout.addView(downLayout);

            initializeButtonViews(buttonRollback, "撤销");
            buttonRollback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mConvas.rollback();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            initializeButtonViews(timeDisplayer, "");

            initializeButtonViews(buttonRecover, "恢复");
            buttonRecover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mConvas.recover();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            downLayout.addView(buttonRollback);
            downLayout.addView(timeDisplayer);
            downLayout.addView(buttonRecover);

            LinearLayout.LayoutParams lpdown = (LinearLayout.LayoutParams) downLayout.getLayoutParams();
            lpdown.setMargins(mConvas.getTrueBorder(), everyHeight / 4, mConvas.getTrueBorder(), everyHeight / 3);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Store the game state
        super.onSaveInstanceState(outState);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        DataOutputStream dos = new DataOutputStream(bos);
        try {
            mMap.printToOstream(dos);
            outState.putByteArray("mMap", bos.toByteArray());
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mConvas.shutDown();
        super.onDestroy();
    }
}
