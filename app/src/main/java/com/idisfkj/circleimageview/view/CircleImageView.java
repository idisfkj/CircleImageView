package com.idisfkj.circleimageview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.idisfkj.circleimageview.R;

/**
 * Created by idisfkj on 16/3/15.
 * Email : idisfkj@qq.com.
 */
public class CircleImageView extends ImageView {
    private TypedArray ta;
    private int mBitmapWidth;
    private int mBitmapheight;
    private int mBorderColor;
    private int mFillColor;
    private int mStrokeWidth;

    private static final int DEFAULT_BORDER_COLOR = Color.RED;
    private static final int DEFAULT_FILL_COLOR = R.color.fill_color;
    private static final int DEFAULT_STROKE_WIDTH = 0;
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private Paint mFillPaint = new Paint();
    private Paint mBorderPaint = new Paint();
    private Matrix mMatrix = new Matrix();
    private RectF mDrawableRec = new RectF();
    private BitmapShader mBitmapShader;
    private int mRadius;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        this.setScaleType(SCALE_TYPE);
        mBorderColor = DEFAULT_BORDER_COLOR;
        mFillColor = DEFAULT_FILL_COLOR;
        mStrokeWidth = DEFAULT_STROKE_WIDTH;
        setUp();
    }

    public void setBorderColor(int color){
        mBorderColor = color;
    }

    public void setFillColor(int color){
        mFillColor = color;
    }

    public void setStrokeWidth(int pd){
        mStrokeWidth = pd;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setUp();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setUp();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = uri != null ? getBitmapFromDrawable(getDrawable()) : null;
        setUp();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setUp();
    }

    public Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap;
        if (drawable instanceof ColorDrawable) {
            bitmap = Bitmap.createBitmap(2, 2, BITMAP_CONFIG);
        } else {
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            bitmap = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
        }

        Canvas canvas = new Canvas(bitmap);
        //设置显示区域
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void setUp() {
        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mFillPaint.setStyle(Paint.Style.FILL);
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(mFillColor);

        if (mStrokeWidth != 0) {
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mStrokeWidth);
        }

        mRadius = (int) Math.min(getWidth() / 2.0f, getHeight() / 2.0f);

        mBitmapWidth = mBitmap.getWidth();
        mBitmapheight = mBitmap.getHeight();

        float scal;
        mMatrix.set(null);
        mDrawableRec.set(0, 0, getWidth(), getHeight());
        if (mBitmapWidth * mDrawableRec.height() > mDrawableRec.width() * mBitmapheight) {
            scal = mDrawableRec.height() / mBitmapheight;
        } else {
            scal = mDrawableRec.width() / mBitmapWidth;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        //设置shader
        mPaint.setShader(mBitmapShader);
        mPaint.setAntiAlias(true);
        mMatrix.setScale(scal, scal);
        //设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        //填充
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mFillPaint);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mPaint);
        //描边
        if (mStrokeWidth != 0)
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mBorderPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setUp();
    }
}
