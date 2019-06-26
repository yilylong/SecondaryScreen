package com.zhl.secondaryscreen.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.zhl.secondaryscreen.R;


/**
 * 描述：
 * Created by zhaohl on 2018-5-21.
 */
public class TagImageView extends android.support.v7.widget.AppCompatImageView {
    private Paint tagPaint;
    private Paint backgroundPaint;
    private int tagSize = 40;
    private String tag = "NEW";
    private int tagBackgroud = 0XFFFB0C0C;
    private int textColor = 0XFFFFFFFF;
    private Location location = Location.ON_TOP_RIGHT;
    private boolean showTag = true;

    public enum Location{
        ON_TOP_lEFT,ON_TOP_RIGHT
    }

    public TagImageView(Context context) {
        this(context,null);
    }

    public TagImageView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public TagImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.TagImageAttr);
        if(array!=null&&array.length()>0){
            textColor = array.getColor(R.styleable.TagImageAttr_tagTextColor,0XFFFFFFFF);
            tagSize = array.getInt(R.styleable.TagImageAttr_tagSize,40);
            tagBackgroud = array.getColor(R.styleable.TagImageAttr_tagBackgroud,0XFFFB0C0C);
            showTag = array.getBoolean(R.styleable.TagImageAttr_showTag,true);
            int loc = array.getInt(R.styleable.TagImageAttr_tagLocation,1);
            switch (loc){
                case 0:
                    location = Location.ON_TOP_lEFT;
                    break;
                case 1:
                    location = Location.ON_TOP_RIGHT;
                    break;
            }
            array.recycle();
        }
        tagPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        tagPaint.setColor(textColor);
        tagPaint.setFakeBoldText(true);
        backgroundPaint.setColor(tagBackgroud);
        tagPaint.setTextSize(tagSize);
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(showTag){
            double sin45 = Math.sin(Math.toRadians(45));
            // 先测出文字大小
            Paint.FontMetrics metrics = new Paint.FontMetrics();
            tagPaint.getFontMetrics(metrics);

            Rect bounds = new Rect();
            tagPaint.getTextBounds(tag,1,tag.length(),bounds);

            float textW = tagPaint.measureText(tag);
            float textH = metrics.bottom-metrics.top;

            float bgH = textH*1.2f;
            float offsetV = -(bgH-(bounds.bottom-bounds.top))/2;
            float unit = (float) (bgH/sin45);
            float wrapW = (float) (unit/sin45);
            float offsetH = bgH+((wrapW-textW)/2);

            switch (location){
                case ON_TOP_lEFT:
                    drawOnLeft(canvas,unit,offsetH,offsetV);
                    break;
                case ON_TOP_RIGHT:
                    drawOnRight(canvas,unit,offsetH,offsetV);
                    break;
            }
        }
    }

    private void drawOnLeft(Canvas canvas, float unit, float offsetH, float offsetV) {
        Path path = new Path();
        path.moveTo(unit,0);
        path.lineTo(2*unit,0);
        path.lineTo(0,2*unit);
        path.lineTo(0,unit);
        path.close();
        canvas.drawPath(path,backgroundPaint);

        Path tagPath = new Path();
        tagPath.moveTo(0,2*unit);
        tagPath.lineTo(2*unit,0);

        canvas.drawTextOnPath(tag,tagPath, offsetH,offsetV,tagPaint);
    }

    private void drawOnRight(Canvas canvas, float unit, float offsetH, float offsetV){
        Path path = new Path();
        path.moveTo(getMeasuredWidth()-2*unit,0);
        path.lineTo(getMeasuredWidth()-unit,0);
        path.lineTo(getMeasuredWidth(),unit);
        path.lineTo(getMeasuredWidth(),2*unit);
        path.close();
        canvas.drawPath(path,backgroundPaint);

        Path tagPath = new Path();
        tagPath.moveTo(getMeasuredWidth()-2*unit,0);
        tagPath.lineTo(getMeasuredWidth(),2*unit);

        canvas.drawTextOnPath(tag,tagPath, offsetH,offsetV,tagPaint);
    }

    public void setTagSize(int size){
        if(size<=0||size>200){
            return ;
        }
        tagSize = size;
        tagPaint.setTextSize(tagSize);
        invalidate();
    }

    public void setTagBackgroud(int color){
        tagBackgroud = color;
        backgroundPaint.setColor(tagBackgroud);
        invalidate();
    }
    public void setTagTextColor(int color){
        textColor = color;
        tagPaint.setColor(textColor);
        invalidate();
    }

    public void setTagLocation(Location location){
        this.location = location;
        invalidate();
    }

    public void showTag(boolean show){
        this.showTag = show;
        invalidate();
    }

}
