package club.llyronx.llyrichuarongdao;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends HrdBaseActivity {

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
        TextView mainTextView = findViewById(R.id.about_main);
        TextView thanksTextView = findViewById(R.id.about_thanks);
        mainTextView.setTypeface(getTypeface());
        thanksTextView.setTypeface(getTypeface());
    }
}
