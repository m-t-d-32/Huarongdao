package club.llyronx.llyrichuarongdao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Random;

import static android.graphics.Bitmap.Config.RGB_565;

public class ChangeActivity extends MusicalActivity {

    private static final int BACKGROUND_WIDTH = 1024, BACKGROUND_HEIGHT = 768;
    public static Drawable mNextBackground = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change);
        TextView textTitle = findViewById(R.id.text_title);
        textTitle.setTypeface(StartActivity.allTypefaces);
        String []titleStrs = getResources().getStringArray(R.array.changeTexts);
        textTitle.setText(titleStrs[new Random().nextInt(titleStrs.length)]);
        final Thread processThread = new Thread() {
            @Override
            public void run() {
                clipbackground(ChangeActivity.this);
            }
        };
        processThread.start();
        final Intent now = getIntent();
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    processThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(ChangeActivity.this, MainActivity.class);
                intent.putExtra("filename", now.getStringExtra("filename"));
                startActivity(intent);
                finish();
            }
        }.start();
    }

    static void clipbackground(Context context){
        try {
            BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
            bfoOptions.inScaled = false;
            bfoOptions.inPreferredConfig = RGB_565;
            Bitmap background = BitmapFactory.decodeResource(context.getResources(), R.drawable.cha, bfoOptions);
            Random random = new Random();
            int x = random.nextInt(background.getWidth() - BACKGROUND_WIDTH);
            int y = random.nextInt(background.getHeight() - BACKGROUND_HEIGHT);
            mNextBackground = new BitmapDrawable(Bitmap.createBitmap(background, x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT));
            System.gc();
        }
        catch (OutOfMemoryError e){
            System.gc();
        }
    }

}
