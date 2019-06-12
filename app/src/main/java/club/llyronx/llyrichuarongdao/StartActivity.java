package club.llyronx.llyrichuarongdao;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class StartActivity extends HrdBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        Resources resource = getResources();
        Configuration configuration = resource.getConfiguration();
        configuration.fontScale = 1.0f;
        resource.updateConfiguration(configuration, resource.getDisplayMetrics());
        setContentView(R.layout.activity_start);
        TextView mainView = findViewById(R.id.start_title);
        mainView.setTypeface(getTypeface());
        final View cv = getWindow().getDecorView();
        cv.setOnTouchListener(new View.OnTouchListener() {
            private Point nowPos = new Point(0, 0);
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        nowPos.x = (int) event.getX();
                        nowPos.y = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (event.getX() - nowPos.x > cv.getWidth() / 2){
                            Intent intent = new Intent(StartActivity.this, AboutActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_left, 0);
                        }
                        else if (nowPos.x - event.getX() > cv.getWidth() / 2){
                            Intent intent = new Intent(StartActivity.this, RandomActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, 0);
                        }
                        break;
                }
                return true;
            }
        });
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, SelectActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTaskRoot()) {
            getPlayer().stop();
            setPlayer(null);
        }
    }

    public void rankGame(View view) {
        Intent intent = new Intent(this, RankActivity.class);
        startActivity(intent);
    }
}
