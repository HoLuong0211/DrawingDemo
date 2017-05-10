package ominext.com.drawingapplication.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by LuongHH on 5/10/2017.
 */

public class DrawingView extends View {

    private static final float TOUCH_TOLERANCE = 4f;
    private static final float DEFAULT_BRUSH_SIZE = 10f;
    private static final String DEFAULT_PAINT_COLOR = "#1abf9a";

    private Path mDrawPath;
    private Canvas mCanvas;
    private Paint mDrawPaint;
    private int mPaintColor;

    private float mCurrentBrushSize;
    private float mLastBrushSize;

    private ArrayList<Path> mPaths = new ArrayList<>();
    private ArrayList<Path> mUndonePaths = new ArrayList<>();

    private float mX;
    private float mY;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setBrushSize(float newSize) {
        mCurrentBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
//        canvasPaint.setStrokeWidth(newSize);
        mDrawPaint.setStrokeWidth(newSize);
    }

    public void setLastBrushSize(float lastSize) {
        mLastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return mLastBrushSize;
    }

    public int getPathsSize() {
        return mPaths.size();
    }

    public int getUndonePathsSize() {
        return mUndonePaths.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Path p : mPaths) {
            canvas.drawPath(p, mDrawPaint);
        }
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //create Bitmap of certain w,h
        Bitmap canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        //apply bitmap to graphic to start drawing.
        mCanvas = new Canvas(canvasBitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(touchX, touchY);
                //redraw
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(touchX, touchY);
                //redraw
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                //redraw
                invalidate();
                break;
            default:
                return false;
        }
        return true;
    }

    private void init() {
        mCurrentBrushSize = DEFAULT_BRUSH_SIZE;
        mLastBrushSize = mCurrentBrushSize;

        mDrawPath = new Path();
        mDrawPaint = new Paint();

        mDrawPaint.setStrokeWidth(mCurrentBrushSize);
        mPaintColor = Color.parseColor(DEFAULT_PAINT_COLOR);
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void touch_start(float x, float y) {
        mUndonePaths.clear();
        mDrawPath.reset();
        mDrawPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mDrawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mDrawPath.lineTo(mX, mY);
        mCanvas.drawPath(mDrawPath, mDrawPaint);
        mPaths.add(mDrawPath);
        mDrawPath = new Path();

    }

    public void eraseAll() {
        mPaths.clear();
        //redraw
        invalidate();
    }

    public void undo() {
        if (mPaths.size() > 0) {
            mUndonePaths.add(mPaths.remove(mPaths.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (mUndonePaths.size() > 0) {
            mPaths.add(mUndonePaths.remove(mUndonePaths.size() - 1));
            invalidate();
        }
    }
}
