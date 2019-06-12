package club.llyronx.llyrichuarongdao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import static android.util.TypedValue.COMPLEX_UNIT_PX;

public class SelectActivity extends HrdBaseActivity {


    private static final String ANIMATIONFILENAME_UNUSED = "animations/muzli.json";
    private static final String ANIMATIONFILENAME_USED = "animations/used.json";
    private static final int WIDTHCOUNT = 4;
    private static final int HEIGHTCOUNT = 5;

    private NumberedLottieAnimationView [] mButtons;
    private int mSelectedMapIndex = -1;

    private int mEveryWidth, mEveryHeight;
    private int mBorderWidth;
    private AbsoluteLayout mMainLayout;
    private Button mSubmitButton;
    private int[] mMapPositions;
    private TextView mHintInfo;

    public static int [] generateRandomNumbers(int allCount, int selectCount){
        int []rawShuffled = new int[allCount];
        Random random = new Random();
        for (int i = 0; i < allCount; ++i){
            rawShuffled[i] = i;
        }
        for (int i = 0; i < allCount; ++i){
            int index = random.nextInt(allCount - i);
            int temp = rawShuffled[index];
            rawShuffled[index] = rawShuffled[allCount - 1 - i];
            rawShuffled[allCount - 1 - i] = temp;
        }
        int []results = new int[selectCount];
        for (int i = 0; i < selectCount; ++i){
            results[i] = rawShuffled[i];
        }
        return results;
    }

    class NumberedLottieAnimationView extends LottieAnimationView{
        private int mNumber;

        NumberedLottieAnimationView(Context context) {
            super(context);
        }

        public void setNumber(int number){
            mNumber = number;
        }
        public int getNumber(){
            return mNumber;
        }
    }

    private void addSubmitButton(){
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubmitButton.setTextColor(Color.RED);
                mSubmitButton.setEnabled(false);
                for (int i = 0; i < mButtons.length; ++i){
                    mButtons[i].setEnabled(false);
                }
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            Intent intent = new Intent(SelectActivity.this, ChangeActivity.class);
                            intent.putExtra("filename", getMapFiles()[mSelectedMapIndex]);
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        mSubmitButton.setVisibility(View.INVISIBLE);
        mSubmitButton.setX(width / 3);
        mSubmitButton.setY(mEveryHeight * (HEIGHTCOUNT + 1));
        mSubmitButton.setWidth(width / 3);
        mSubmitButton.setText("确认选择");
        mSubmitButton.setTypeface(getTypeface());
        mSubmitButton.setTextColor(Color.CYAN);
        mSubmitButton.setGravity(Gravity.CENTER);
        mSubmitButton.setBackgroundColor(Color.TRANSPARENT);
        mSubmitButton.setTextSize(COMPLEX_UNIT_PX, mEveryWidth / 3);
        mSubmitButton.setVisibility(View.INVISIBLE);
    }

    private void addButtonsByMaps(){
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
        mMapPositions = generateRandomNumbers(WIDTHCOUNT * HEIGHTCOUNT, getMapFiles().length);
        for (int i = 0; i < mMapPositions.length; ++i){
            final NumberedLottieAnimationView button = mButtons[i];
            button.setNumber(i);
            if (userInfo.containsKey(getMapNames()[button.getNumber()])){
                button.setAnimation(ANIMATIONFILENAME_USED);
            }
            else {
                button.setAnimation(ANIMATIONFILENAME_UNUSED);
            }
            button.setRepeatCount(-1);
            //button.playAnimation();
            button.setX(mMapPositions[i] % WIDTHCOUNT * mEveryWidth + mEveryWidth / 4);
            button.setY(mMapPositions[i] / WIDTHCOUNT * mEveryHeight + mEveryHeight);
            final AbsoluteLayout.LayoutParams para = (AbsoluteLayout.LayoutParams) button.getLayoutParams();
            para.width = mEveryWidth / 2;
            para.height = mEveryHeight / 2;
            button.setLayoutParams(para);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recover();
                    button.playAnimation();
                    button.setX(button.getX() - mEveryWidth / 4);
                    button.setY(button.getY() - mEveryHeight / 4);
                    para.width = mEveryWidth;
                    para.height = mEveryHeight;
                    button.setLayoutParams(para);
                    mSelectedMapIndex = button.getNumber();
                    mSubmitButton.setVisibility(View.VISIBLE);
                    mMainLayout.postInvalidate();
                }
            });
        }
    }

    class MainLayout extends AbsoluteLayout{
        private Paint mPaint;

        MainLayout(Context context){
            super(context);
            mPaint = new Paint();
            mPaint.setTypeface(getTypeface());
            mPaint.setColor(Color.WHITE);
            mPaint.setTextSize(mEveryWidth / 4);
        }

        private void drawText(Canvas canvas){
            if (mSelectedMapIndex >= 0) {
                canvas.drawText(getMapNames()[mSelectedMapIndex],
                        mButtons[mSelectedMapIndex].getX() + mEveryWidth,
                        mButtons[mSelectedMapIndex].getY() + mEveryHeight,
                        mPaint);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawText(canvas);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        mBorderWidth = width / 10;
        mEveryWidth = width / (WIDTHCOUNT + 1);
        mEveryHeight = mEveryWidth;
        mButtons = new NumberedLottieAnimationView[getMapFiles().length];
        mMainLayout = new MainLayout(this);
        mMainLayout.setBackgroundColor(Color.BLACK);
        setContentView(mMainLayout);
        //buttons
        mMapPositions = generateRandomNumbers(WIDTHCOUNT * HEIGHTCOUNT, getMapFiles().length);
        for (int i = 0; i < mMapPositions.length; ++i){
            NumberedLottieAnimationView button = new NumberedLottieAnimationView(this);
            mButtons[i] = button;
            mMainLayout.addView(button);
        }
        //text
        mHintInfo = new TextView(this);
        mMainLayout.addView(mHintInfo);
        //submitbutton
        mSubmitButton = new Button(this);
        mMainLayout.addView(mSubmitButton);
    }

    private void addHintTextView() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        mHintInfo.setX(mBorderWidth);
        mHintInfo.setY(mBorderWidth);
        mHintInfo.setTypeface(getTypeface());
        mHintInfo.setText("请选择你的关卡：");
        mHintInfo.setTextColor(Color.LTGRAY);
        mHintInfo.setBackgroundColor(Color.TRANSPARENT);
        mHintInfo.setTextSize(COMPLEX_UNIT_PX, mEveryWidth / 3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        clear();
        addHintTextView();
        addButtonsByMaps();
        addSubmitButton();
    }

    void recover(){
        if (mSelectedMapIndex >= 0) {
            LottieAnimationView button = mButtons[mSelectedMapIndex];
            button.setProgress(0);
            button.cancelAnimation();
            button.setX(mMapPositions[mSelectedMapIndex] % WIDTHCOUNT * mEveryWidth + mEveryWidth / 4);
            button.setY(mMapPositions[mSelectedMapIndex] / WIDTHCOUNT * mEveryHeight + mEveryHeight);
            AbsoluteLayout.LayoutParams para = (AbsoluteLayout.LayoutParams) button.getLayoutParams();
            para.width = mEveryWidth / 2;
            para.height = mEveryHeight / 2;
            button.setLayoutParams(para);
            mSelectedMapIndex = -1;
        }
    }

    void clear(){
        recover();
        mSubmitButton.setTextColor(Color.CYAN);
        mSubmitButton.setEnabled(true);
        mSubmitButton.setVisibility(View.INVISIBLE);
        for (int i = 0; i < mButtons.length; ++i){
            mButtons[i].setEnabled(true);
        }
    }
}
