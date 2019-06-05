package club.llyronx.llyrichuarongdao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        final View cv = getWindow().getDecorView();
        cv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        finish();
                        overridePendingTransition(0, R.anim.slide_out_left);
                        break;
                }
                return true;
            }
        });
        TextView mainTextView = findViewById(R.id.main_textview);
        mainTextView.setTypeface(StartActivity.allTypefaces);
    }
}
