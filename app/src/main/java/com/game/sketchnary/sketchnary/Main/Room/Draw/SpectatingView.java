package com.game.sketchnary.sketchnary.Main.Room.Draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import com.game.sketchnary.sketchnary.Main.Room.RoomLobby;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by danny on 26/05/2016.
 */

public class SpectatingView extends View {

    public int width;
    public  int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;

    public SpectatingView(Context c,Paint mPaint) {
        super(c);
        context=c;
        this.mPaint=mPaint;
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        System.out.println("SCREEN SIZE: w:"+w+ " h:"+h);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        System.out.println("I WAS CALLED!!");
        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath( mPath,  mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }




    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        //mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
    public void drawOnScreen(String res){
        if(res == null)
            return;
        try {
            JSONObject object = new JSONObject(res);
            int srcWidth = object.getInt("width");
            int srcHeight = object.getInt("height");
            int srcAction = object.getInt("action");
            double srcX = object.getDouble("x");
            double srcY = object.getDouble("y");
            float x = (float) (mBitmap.getWidth()*srcX/srcWidth);
            float y = (float) (mBitmap.getHeight()*srcY/srcHeight);
            switch (srcAction) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        circlePath.reset();
    }


    //esta função tem de automaticamente fazer o resize dos pontos que recebe para o ecrã do telemovel que recebe
    public void drawPoint(int x,int y){
        System.out.println("ID: "+x);
        System.out.println("WIDTH: "+this.width);
        System.out.println("HEIGTH: "+this.height);
    }
}