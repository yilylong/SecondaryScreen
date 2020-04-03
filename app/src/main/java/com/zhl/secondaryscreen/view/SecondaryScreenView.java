package com.zhl.secondaryscreen.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import com.zhl.secondaryscreen.R;

import java.util.ArrayList;

/**
 * 描述：左滑负一屏的viewgroup
 * Created by zhaohl on 2019-1-18.
 */
public class SecondaryScreenView extends ViewGroup {
    private View secondaryView;
    private View mainView;
    private int downX, downY, endX;
    private float rate = 0.60f;
    private boolean isScrollerRight = true;
    private ArrayList<ViewPager> viewPagers = new ArrayList<>();
    private float touchSlop;

    public SecondaryScreenView(Context context) {
        this(context, null);
    }

    public SecondaryScreenView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SecondaryScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SecondaryScreenView);
        rate = array.getFloat(R.styleable.SecondaryScreenView_rate, rate);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        if (count > 2) {
            throw new RuntimeException("only can host 2 direct child view");
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int usedWidth = 0;
        int usedHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                SecondaryScreenView.LayoutParams params = (SecondaryScreenView.LayoutParams) childView.getLayoutParams();
                int childWidthSpec = MeasureSpec.AT_MOST;
                int childHeightSpec = MeasureSpec.AT_MOST;
                switch (params.width) {
                    case SecondaryScreenView.LayoutParams.MATCH_PARENT:
                        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
                            childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
                        } else {
                            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    case SecondaryScreenView.LayoutParams.WRAP_CONTENT:
                        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
                            childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.AT_MOST);
                        } else {
                            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    default:
                        childWidthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
                        break;
                }
                usedWidth += MeasureSpec.getSize(childWidthSpec) + params.leftMargin + params.rightMargin;
                switch (params.height) {
                    case SecondaryScreenView.LayoutParams.MATCH_PARENT:
                        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    case SecondaryScreenView.LayoutParams.WRAP_CONTENT:
                        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.AT_MOST);
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    default:
                        childHeightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
                        break;
                }
                usedHeight += MeasureSpec.getSize(childHeightSpec) + params.bottomMargin + params.topMargin;
                measureChild(childView, childWidthSpec, childHeightSpec);
            }
        }
        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = usedWidth;
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = usedHeight;
        }
        setMeasuredDimension(widthSize, heightSize);
        secondaryView = getChildAt(0);
        mainView = getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getAllViewPager(viewPagers, this);
        if (secondaryView != null) {
            secondaryView.layout(-secondaryView.getMeasuredWidth(), 0, 0, secondaryView.getMeasuredHeight());
        }
        if (mainView != null) {
            mainView.layout(0, 0, mainView.getMeasuredWidth(), secondaryView.getMeasuredHeight());
        }
    }

    private void getAllViewPager(ArrayList<ViewPager> viewPagers, ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ViewPager) {
                viewPagers.add((ViewPager) child);
            } else if (child instanceof ViewGroup) {
                getAllViewPager(viewPagers, (ViewGroup) child);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 处理viewpager的事件冲突
        ViewPager pager = getTouchedViewPager(ev);
        if (pager != null && pager.getCurrentItem() != 0) {
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float delX = ev.getRawX() - downX;
                float delY = ev.getRawY() - downY;
                if (Math.abs(delX) > touchSlop && Math.abs(delX) > Math.abs(delY) && pager != null && pager.getCurrentItem() == 0 && delX > 0) {
                    return true;
                }
//                if(secondaryView.getX()==0f&& Math.abs(delX)>touchSlop&& Math.abs(delX)> Math.abs(delY)&&pager!=null&&pager.getCurrentItem()==pager.getChildCount()-1&&delX<0){
//                    return true;
//                }
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private ViewPager getTouchedViewPager(MotionEvent ev) {
        if (viewPagers == null || viewPagers.size() == 0) {
            return null;
        }
        Rect mRect = new Rect();
        for (ViewPager viewPager : viewPagers) {
            viewPager.getHitRect(mRect);
            if (mRect.contains((int) ev.getX(), (int) ev.getY())) {
                return viewPager;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) (event.getRawX() - downX);
                int moveY = (int) (event.getRawY() - downY);
                if (secondaryView.getX() < 0 && moveX > 0) {
                    moveX *= rate;
                }
                if (moveX > 0) {
                    isScrollerRight = true;
                } else {
                    isScrollerRight = false;
                }
                int translateX_second = endX + moveX;
                int translateX_main = endX + moveX;
                if (translateX_second >= secondaryView.getWidth()) {
                    translateX_second = secondaryView.getWidth();
                    translateX_main = mainView.getWidth();
                }
                if (translateX_second <= 0) {
                    translateX_second = 0;
                    translateX_main = 0;
                }
                secondaryView.setTranslationX(translateX_second);
                mainView.setTranslationX(translateX_main);
                break;
            case MotionEvent.ACTION_UP:
                endX = (int) mainView.getTranslationX();
                startTranslateAnim();
                break;
        }
        return true;
    }

    public void translateToSecondaryView() {
        endX = secondaryView.getWidth();
        isScrollerRight = true;
        startTranslateAnim();
    }

    public void translateToMainView() {
        endX = mainView.getWidth() * 1 / 3;
        isScrollerRight = false;
        startTranslateAnim();
    }

    public boolean isStillMainView() {
        return endX == 0 && mainView.getTranslationX() == 0;
    }

    private void startTranslateAnim() {
        float secondTranslationX = 0;
        float mainTranslationX = 0;
        if (isScrollerRight) {//右滑
            if (endX > secondaryView.getWidth() / 3) {
                secondTranslationX = secondaryView.getWidth();
                mainTranslationX = mainView.getWidth();
                isScrollerRight = false;
            } else {
                secondTranslationX = 0;
                mainTranslationX = 0;
                isScrollerRight = true;
            }
        } else {
            if (endX < mainView.getWidth() * 2 / 3) {
                secondTranslationX = 0;
                mainTranslationX = 0;
                isScrollerRight = true;
            } else {
                secondTranslationX = secondaryView.getWidth();
                mainTranslationX = mainView.getWidth();
                isScrollerRight = false;
            }
        }
        AnimatorSet animator = new AnimatorSet();
        ObjectAnimator animator_secondaryView = ObjectAnimator.ofFloat(secondaryView, "translationX", secondTranslationX);
        ObjectAnimator animator_mainView = ObjectAnimator.ofFloat(mainView, "translationX", mainTranslationX);
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                endX = (int) mainView.getTranslationX();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.play(animator_secondaryView).with(animator_mainView);
        animator.start();

    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new SecondaryScreenView.LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new SecondaryScreenView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SecondaryScreenView.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof SecondaryScreenView.LayoutParams;
    }
}
