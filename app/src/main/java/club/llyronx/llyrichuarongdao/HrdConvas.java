package club.llyronx.llyrichuarongdao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.os.Build.*;


public class HrdConvas extends View {

    private static final int WIDTHCOUNT = 4;
    private static final int HEIGHTCOUNT = 5;
    private static final Point LEFTBORDER = new Point(0, 0), RIGHTBORDER = new Point(WIDTHCOUNT, HEIGHTCOUNT);
    private static final int SOUNDRESOURCE = R.raw.tap;

    private Paint mPaint;
    private HrdMap mMap;
    private Chronometer mTimer;
    private boolean mTimerEnabled = false;
    private int mEveryWidth, mEveryHeight;
    private int mBorderWidth, mRoundRadius;

    private Point mTouchRecord;
    private HrdChess mTouchChess;
    private Point mOffset;
    private Point mTouchChessLURecord;
    private Context mContext;
    private ArrayList<byte[]> mUserOperations;
    private int mUserOperationIndex;
    private SoundPool mSoundPool;
    private int mSoundResult;

    private static int getRandomColor(){
        Random random = new Random();
        return Color.rgb(random.nextInt(200), random.nextInt(100), random.nextInt(100));
    }

    private boolean pointInChess(Point point, HrdChess HrdChess){
        return point.x > HrdChess.getBegin().x * mEveryWidth &&
                point.y > HrdChess.getBegin().y * mEveryHeight &&
                point.x < (HrdChess.getBegin().x + HrdChess.getWidth()) * mEveryWidth &&
                point.y < (HrdChess.getBegin().y + HrdChess.getHeight()) * mEveryHeight;
    }

    HrdConvas(Context context) {
        super(context);
        mContext = context;
    }

    void setUp(HrdMap map, Chronometer timer) throws IOException {
        mPaint =new Paint();
        mPaint.setTypeface(HrdBaseActivity.getTypeface());
        mUserOperations = new ArrayList<>();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                performClick();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchChess = null;
                        Point nowPoint = new Point((int)event.getX(), (int)event.getY());
                        if (!mTimerEnabled){
                            mTimer.setBase(SystemClock.elapsedRealtime());//计时器清零
                            mTimer.start();
                            mTimerEnabled = true;
                        }
                        for (int i = 0; i < mMap.getChesses().size(); ++i){
                            if (pointInChess(nowPoint, mMap.getChesses().get(i))){
                                mTouchChess = mMap.getChesses().get(i);
                                mTouchChessLURecord = new Point(mTouchChess.getBegin().x * mEveryWidth, mTouchChess.getBegin().y * mEveryHeight);
                                mTouchRecord = new Point((int)event.getX() - mTouchChessLURecord.x, (int)event.getY() - mTouchChessLURecord.y);
                                break;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mTouchChess != null) {
                            mOffset.x = (int)event.getX() - (mTouchChessLURecord.x + mTouchRecord.x);
                            mOffset.y = (int)event.getY() - (mTouchChessLURecord.y + mTouchRecord.y);
                            fixMoveByBorder();
                            if (Math.abs(mOffset.x) > Math.abs(mOffset.y)){
                                fixMovesByCollisionX();
                                mTouchChessLURecord.x += mOffset.x;
                                fixMovesByCollisionY();
                                mTouchChessLURecord.y += mOffset.y;
                            }
                            else {
                                fixMovesByCollisionY();
                                mTouchChessLURecord.y += mOffset.y;
                                fixMovesByCollisionX();
                                mTouchChessLURecord.x += mOffset.x;
                            }
                            postInvalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (mTouchChess != null) {
                            if (mSoundPool != null){
                                mSoundPool.play(mSoundResult, 1, 1, 0, 0, 1);
                            }
                            Point touchChessbegin = mTouchChess.getBegin();
                            touchChessbegin.x = Math.round((float) mTouchChessLURecord.x / mEveryWidth);
                            touchChessbegin.y = Math.round((float) mTouchChessLURecord.y / mEveryHeight);
                            if (touchChessbegin.x != mTouchChess.getBegin().x
                                    || touchChessbegin.y != mTouchChess.getBegin().y){
                                mTouchChess.setBegin(touchChessbegin);
                                ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                                try {
                                    mMap.printToOstream(new DataOutputStream(bos));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                for (int i = mUserOperationIndex + 1; i < mUserOperations.size(); ++i){
                                    mUserOperations.remove(mUserOperations.size() - 1);
                                }
                                mUserOperations.add(bos.toByteArray());
                                ++mUserOperationIndex;
                            }
                            postInvalidate();
                            if (mMap.judgeWin()){
                                win();
                            }
                            mOffset.x = 0;
                            mOffset.y = 0;
                            mTouchChessLURecord.x = 0;
                            mTouchChessLURecord.y = 0;
                            mTouchChess = null;
                        }
                        break;
                }
                return true;
            }
        });
        //Initialize map
        mMap = map;
        //Initialize width and height of every HrdChess
        DisplayMetrics dm = new DisplayMetrics();
        ((MainActivity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        mEveryWidth = width / WIDTHCOUNT;
        mEveryHeight = mEveryWidth;
        //Initialize movements
        mTouchRecord = new Point(-1, -1);
        mOffset = new Point(0, 0);
        mBorderWidth = mEveryWidth / 20;
        mRoundRadius = mEveryWidth / 20;
        //Set height
        ViewGroup.LayoutParams linearParams = getLayoutParams();
        linearParams.height = width / WIDTHCOUNT * HEIGHTCOUNT;
        //Initialize Useroperation
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        mMap.printToOstream(new DataOutputStream(bos));
        mUserOperations.add(bos.toByteArray());
        //Initialize game sounds
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundResult = mSoundPool.load(mContext, SOUNDRESOURCE, 1);
        //Set timer
        mTimer = timer;
    }

    private void win() {
        Intent intent = new Intent(mContext, WinActivity.class);
        intent.putExtra("mapname", mMap.getName());
        String []timeSplits = mTimer.getText().toString().split(":");
        int time = 0;
        if (timeSplits.length == 3){
            time = Integer.parseInt(timeSplits[0]) * 60 * 60
                    + Integer.parseInt(timeSplits[1]) * 60 + Integer.parseInt(timeSplits[2]);
        }
        else if (timeSplits.length == 2){
            time = Integer.parseInt(timeSplits[0]) * 60 + Integer.parseInt(timeSplits[1]);
        }
        else {
            time = Integer.parseInt(timeSplits[0]);
        }
        intent.putExtra("time", time);
        mContext.startActivity(intent);
        ((MainActivity) mContext).finish();
    }

    public int getTrueHeight(){
        return mEveryHeight * HEIGHTCOUNT;
    }
    public int getTrueWidth(){
        return mEveryWidth * WIDTHCOUNT;
    }
    public int getTrueBorder(){
        return mBorderWidth;
    }
    public void rollback() throws IOException {
        if (mSoundPool != null){
            mSoundPool.play(mSoundResult, 1, 1, 0, 0, 1);
        }
        if (mUserOperationIndex > 0){
            --mUserOperationIndex;
            mMap = new HrdMap(new DataInputStream(new ByteArrayInputStream(mUserOperations.get(mUserOperationIndex))));
            postInvalidate();
        }
    }
    public void recover() throws IOException{
        if (mSoundPool != null){
            mSoundPool.play(mSoundResult, 1, 1, 0, 0, 1);
        }
        if (mUserOperationIndex < mUserOperations.size() - 1){
            ++mUserOperationIndex;
            mMap = new HrdMap(new DataInputStream(new ByteArrayInputStream(mUserOperations.get(mUserOperationIndex))));
            postInvalidate();
        }
    }

    private void fixMoveByBorder(){
        mOffset.x = mTouchChessLURecord.x + mOffset.x < LEFTBORDER.x * mEveryWidth
                ? LEFTBORDER.x * mEveryWidth - mTouchChessLURecord.x : mOffset.x;
        mOffset.y = mTouchChessLURecord.y + mOffset.y < LEFTBORDER.y * mEveryHeight
                ? LEFTBORDER.y * mEveryHeight - mTouchChessLURecord.y : mOffset.y;
        mOffset.x = mTouchChessLURecord.x + mOffset.x + mTouchChess.getWidth() * mEveryWidth > RIGHTBORDER.x * mEveryWidth
                ? RIGHTBORDER.x * mEveryWidth - mTouchChessLURecord.x - mTouchChess.getWidth() * mEveryWidth : mOffset.x;
        mOffset.y = mTouchChessLURecord.y + mOffset.y + mTouchChess.getHeight() * mEveryHeight > RIGHTBORDER.y * mEveryHeight
                ? RIGHTBORDER.y * mEveryHeight - mTouchChessLURecord.y - mTouchChess.getHeight() * mEveryHeight : mOffset.y;
    }

    private void fixMovesByCollisionX(){
        for (int i = 0; i < mMap.getChesses().size(); ++i){
            if (mMap.getChesses().get(i) == mTouchChess){
                continue;
            }
            if (mTouchChessLURecord.y < (mMap.getChesses().get(i).getBegin().y + mMap.getChesses().get(i).getHeight()) * mEveryHeight &&
                    mTouchChessLURecord.y > (mMap.getChesses().get(i).getBegin().y - mTouchChess.getHeight()) * mEveryHeight) {
                if (mTouchChessLURecord.x >= (mMap.getChesses().get(i).getBegin().x + mMap.getChesses().get(i).getWidth()) * mEveryWidth &&
                        mTouchChessLURecord.x + mOffset.x < (mMap.getChesses().get(i).getBegin().x + mMap.getChesses().get(i).getWidth()) * mEveryWidth) {
                    mOffset.x = (mMap.getChesses().get(i).getBegin().x + mMap.getChesses().get(i).getWidth()) * mEveryWidth - mTouchChessLURecord.x;
                } else if (mTouchChessLURecord.x <= (mMap.getChesses().get(i).getBegin().x - mTouchChess.getWidth()) * mEveryWidth &&
                        mTouchChessLURecord.x + mOffset.x > (mMap.getChesses().get(i).getBegin().x - mTouchChess.getWidth()) * mEveryWidth) {
                    mOffset.x = (mMap.getChesses().get(i).getBegin().x - mTouchChess.getWidth()) * mEveryWidth - mTouchChessLURecord.x;
                }
            }
        }
    }
    private void fixMovesByCollisionY(){
        for (int i = 0; i < mMap.getChesses().size(); ++i){
            if (mMap.getChesses().get(i) == mTouchChess){
                continue;
            }
            if (mTouchChessLURecord.x < (mMap.getChesses().get(i).getBegin().x + mMap.getChesses().get(i).getWidth()) * mEveryWidth &&
            mTouchChessLURecord.x > (mMap.getChesses().get(i).getBegin().x - mTouchChess.getWidth()) * mEveryWidth) {
                if (mTouchChessLURecord.y >= (mMap.getChesses().get(i).getBegin().y + mMap.getChesses().get(i).getHeight()) * mEveryHeight &&
                        mTouchChessLURecord.y + mOffset.y < (mMap.getChesses().get(i).getBegin().y + mMap.getChesses().get(i).getHeight()) * mEveryHeight) {
                    mOffset.y = (mMap.getChesses().get(i).getBegin().y + mMap.getChesses().get(i).getHeight()) * mEveryHeight - mTouchChessLURecord.y;
                } else if (mTouchChessLURecord.y <= (mMap.getChesses().get(i).getBegin().y - mTouchChess.getHeight()) * mEveryHeight &&
                        mTouchChessLURecord.y + mOffset.y > (mMap.getChesses().get(i).getBegin().y - mTouchChess.getHeight()) * mEveryHeight) {
                    mOffset.y = (mMap.getChesses().get(i).getBegin().y - mTouchChess.getHeight()) * mEveryHeight - mTouchChessLURecord.y;
                }
            }
        }
    }

    private void drawTextAligned(Canvas canvas, String str, int beginx, int beginy, int endx, int endy, int size){
        canvas.save();
        canvas.translate((beginx + endx) / 2, (beginy + endy) / 2);
        mPaint.setTextSize(size);
        mPaint.setColor(Color.LTGRAY);
        int baseLineX = (int) (-mPaint.measureText(str) / 2);
        int baseLineY = (int) (Math.abs(mPaint.ascent() + mPaint.descent()) / 2);
        canvas.drawText(str, baseLineX, baseLineY, mPaint);
        canvas.restore();
    }

    private void DrawRoundRect(Canvas canvas, int beginx, int beginy, int endx, int endy){
        if (VERSION.SDK_INT >= 21){
            canvas.drawRoundRect(beginx, beginy, endx, endy,
                    mRoundRadius, mRoundRadius,
                    mPaint);
        }
        else {
            canvas.drawRoundRect(new RectF(beginx, beginy, endx, endy),
                    mRoundRadius, mRoundRadius,
                    mPaint);
        }
    }


    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        ArrayList<HrdChess> chesses = mMap.getChesses();
        for (int i = 0; i < chesses.size(); ++i){
            HrdChess chess = chesses.get(i);
            if (chess == mMap.getMainChess()) {
                mPaint.setColor(Color.argb(100, 255, 0, 0));
            }
            else {
                mPaint.setColor(Color.argb(100, 255, 127, 0));
            }
            if (chess != mTouchChess) {
                DrawRoundRect(canvas, mEveryWidth * chess.getBegin().x + mBorderWidth,
                        mEveryHeight * chess.getBegin().y + mBorderWidth,
                        mEveryWidth * (chess.getBegin().x + chess.getWidth()) - mBorderWidth,
                        mEveryHeight * (chess.getBegin().y + chess.getHeight()) - mBorderWidth);
                drawTextAligned(canvas, chess.getName(),
                        mEveryWidth * chess.getBegin().x,
                        mEveryHeight * chess.getBegin().y,
                        mEveryWidth * (chess.getBegin().x + chess.getWidth()),
                        mEveryHeight * (chess.getBegin().y + chess.getHeight()),
                        mEveryWidth / 4);
            }
            else {
                DrawRoundRect(canvas, mTouchChessLURecord.x + mBorderWidth,
                        mTouchChessLURecord.y + mBorderWidth,
                        mEveryWidth * chess.getWidth() + mTouchChessLURecord.x - mBorderWidth,
                        mEveryHeight * chess.getHeight() + mTouchChessLURecord.y - mBorderWidth);
                drawTextAligned(canvas, chess.getName(),
                        mTouchChessLURecord.x,
                        mTouchChessLURecord.y,
                        mEveryWidth * chess.getWidth() + mTouchChessLURecord.x,
                        mEveryHeight * chess.getHeight() + mTouchChessLURecord.y,
                        mEveryWidth / 4);
            }
        }
    }

    void shutDown(){
        if (mSoundPool != null){
            mSoundPool.release();
            mSoundPool = null;
        }
    }
}
