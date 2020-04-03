package com.zhl.secondaryscreen.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;

import com.zhl.secondaryscreen.R;

import java.util.ArrayList;

/**
 * 描述：下拉的展示第二屏的viewgroup（类似mgtv下拉展示广告）
 * Created by zhaohl on 2020-03-31.
 */
public class SecondFloorView extends ViewGroup {
    private View secondaryView;
    private View mainView;
    private int downX, downY, endY, lastTouchX, lastTouchY;
    private boolean isBeingDraged;
    private float resistance = 0.60f;// 下拉阻力
    private boolean isScrollerDown = true;
    private int pullRefreshDistance = 260;
    private int topOffset = 0;// mainview头部偏移
    private ArrayList<ViewPager> viewPagers = new ArrayList<>();
    private ArrayList<RecyclerView> recyclerViews = new ArrayList<>();
    private ArrayList<AbsListView> absListViews = new ArrayList<>();
    private float touchSlop;
    private OnSecondFloorScrollistener scrollistener;
    private OnStateListener stateListener;
    private String curState = State.STATE_MAIN;
    private MotionEvent touchEvent;

    public SecondFloorView(Context context) {
        this(context, null);
    }

    public SecondFloorView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SecondFloorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SecondFloorView);
        resistance = array.getFloat(R.styleable.SecondFloorView_resistance, resistance);
        pullRefreshDistance = array.getInteger(R.styleable.SecondFloorView_pullRefreshDistance, pullRefreshDistance);
        topOffset = array.getDimensionPixelSize(R.styleable.SecondFloorView_topOffset,topOffset);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        if (stateListener != null) {
            stateListener.onStateMain();
        }
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
                SecondFloorView.LayoutParams params = (SecondFloorView.LayoutParams) childView.getLayoutParams();
                int childWidthSpec = MeasureSpec.AT_MOST;
                int childHeightSpec = MeasureSpec.AT_MOST;
                switch (params.width) {
                    case SecondFloorView.LayoutParams.MATCH_PARENT:
                        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
                            childWidthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
                        } else {
                            childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    case SecondFloorView.LayoutParams.WRAP_CONTENT:
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
                    case SecondFloorView.LayoutParams.MATCH_PARENT:
                        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize+topOffset, MeasureSpec.EXACTLY);
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    case SecondFloorView.LayoutParams.WRAP_CONTENT:
                        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(heightSize+topOffset, MeasureSpec.AT_MOST);
                        } else {
                            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                        }
                        break;
                    default:
                        childHeightSpec = MeasureSpec.makeMeasureSpec(params.height+topOffset, MeasureSpec.EXACTLY);
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
        getAllRecyclerView(recyclerViews, this);
        getAllAbsListView(absListViews, this);
        if (secondaryView != null) {
            secondaryView.layout(0, -secondaryView.getMeasuredHeight()+topOffset, secondaryView.getMeasuredWidth(), topOffset);
        }
        if (mainView != null) {
            mainView.layout(0, topOffset, mainView.getMeasuredWidth(), secondaryView.getMeasuredHeight());
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

    private void getAllRecyclerView(ArrayList<RecyclerView> recyclerViews, ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof RecyclerView) {
                recyclerViews.add((RecyclerView) child);
            } else if (child instanceof ViewGroup) {
                getAllRecyclerView(recyclerViews, (ViewGroup) child);
            }
        }
    }

    private void getAllAbsListView(ArrayList<AbsListView> listViews, ViewGroup parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child instanceof ListView) {
                listViews.add((ListView) child);
            } else if (child instanceof ViewGroup) {
                getAllAbsListView(listViews, (ViewGroup) child);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        // 处理viewpager的事件冲突
//        ViewPager pager = getTouchedViewPager(ev);
//        if(pager!=null&&pager.getCurrentItem()!=0){
//            return super.onInterceptTouchEvent(ev);
//        }
        // 处理recyclerview和listview的事件冲突
        RecyclerView recyclerView = getTouchedRecyclerView(ev);
        boolean recyclerViewFirstItemVisible = false;
        boolean absListViewFirstItemVisible = false;
        if (recyclerView != null) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null && layoutManager instanceof LinearLayoutManager) {
                if (((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() != 0) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    recyclerViewFirstItemVisible = true;
                }
            } else if (layoutManager != null && layoutManager instanceof GridLayoutManager) {
                if (((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition() != 0) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    recyclerViewFirstItemVisible = true;
                }
            } else if (layoutManager != null && layoutManager instanceof StaggeredGridLayoutManager) {
                int[] mFirstVisible = null;
                mFirstVisible = ((StaggeredGridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPositions(mFirstVisible);
                if (mFirstVisible != null && mFirstVisible[0] != 0) {
                    return super.onInterceptTouchEvent(ev);
                } else {
                    recyclerViewFirstItemVisible = true;
                }
            }
        }
        AbsListView absListView = getTouchedAbsListView(ev);
        if (absListView != null) {
            if (absListView.getFirstVisiblePosition() != 0) {
                return super.onInterceptTouchEvent(ev);
            } else {
                absListViewFirstItemVisible = true;
            }
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float delX = ev.getRawX() - downX;
                float delY = ev.getRawY() - downY;
                if (Math.abs(delY) > touchSlop && Math.abs(delY) > Math.abs(delX) && (recyclerViewFirstItemVisible || absListViewFirstItemVisible) && delY > 0) {
                    return true;
                }
                if (mainView.getScrollY() == 0 && delY > 0) {
                    return true;
                }
                if (curState == State.STATE_REFRESHING) {
                    return true;
                }
//                if(secondaryView.getX()==0f&& Math.abs(delX)>touchSlop&& Math.abs(delX)> Math.abs(delY)&&pager!=null&&pager.getCurrentItem()==pager.getChildCount()-1&&delX<0){
//                    return true;
//                }
                break;
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

    private RecyclerView getTouchedRecyclerView(MotionEvent ev) {
        if (recyclerViews == null || recyclerViews.size() == 0) {
            return null;
        }
        Rect mRect = new Rect();
        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.getHitRect(mRect);
            if (mRect.contains((int) ev.getX(), (int) ev.getY())) {
                return recyclerView;
            }
        }
        return null;
    }

    private AbsListView getTouchedAbsListView(MotionEvent ev) {
        if (absListViews == null || absListViews.size() == 0) {
            return null;
        }
        Rect mRect = new Rect();
        for (AbsListView listView : absListViews) {
            listView.getHitRect(mRect);
            if (mRect.contains((int) ev.getX(), (int) ev.getY())) {
                return listView;
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchEvent = event;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getRawX();
                downY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                isBeingDraged = true;
                int moveX = (int) (event.getRawX() - downX);
                int moveY = (int) (event.getRawY() - downY);
                if (secondaryView.getY() < 0) {
                    moveY *= resistance;
                }
                if (moveY > 0) {
                    isScrollerDown = true;
                } else {
                    isScrollerDown = false;
                }
                int translateY_second = endY + moveY;
                int translateY_main = endY + moveY;
                if (translateY_second >= secondaryView.getHeight()) {
                    translateY_second = secondaryView.getHeight();
                    translateY_main = mainView.getHeight();
                }
                if (translateY_second <= 0) {
                    translateY_second = 0;
                    translateY_main = 0;
                }
                secondaryView.setTranslationY(translateY_second);
                mainView.setTranslationY(translateY_main);
                if (isScrollerDown) {
                    if (Math.abs(translateY_main) > 0 && Math.abs(translateY_main) <= pullRefreshDistance) {
                        curState = State.STATE_PULL_TO_REFRESH;
                        if (stateListener != null) {
                            stateListener.onStatePullToRefresh();
                            stateListener.onStateChanged(curState);
                        }
                    } else if (Math.abs(translateY_main) > pullRefreshDistance && Math.abs(translateY_main) < mainView.getHeight() / 3) {
                        if(curState!=State.STATE_REFRESHING){
                            curState = State.STATE_RELEASE_TO_REFRESH;
                            if (stateListener != null) {
                                stateListener.onStateReleaseToRefresh();
                                stateListener.onStateChanged(curState);
                            }
                        }
                    } else if (Math.abs(translateY_main) >= mainView.getHeight() / 3) {
                        if(curState!=State.STATE_REFRESHING){
                            curState = State.STATE_RELEASE_TO_SECOND;
                            if (stateListener != null) {
                                stateListener.onStateReleaseToSecond();
                                stateListener.onStateChanged(curState);
                            }
                        }
                    }
                } else {
                    if (Math.abs(translateY_main) >= mainView.getHeight() / 3) {
                        curState = State.STATE_RELEASE_TO_MAIN;
                        if (stateListener != null) {
                            stateListener.onStateReleaseToMain();
                            stateListener.onStateChanged(curState);
                        }
                    } else {
                        curState = State.STATE_SECOND;
                        if (stateListener != null) {
                            stateListener.onStateSecond();
                            stateListener.onStateChanged(curState);
                        }
                    }
                }
                if (scrollistener != null) {
                    scrollistener.onScroll(translateY_main);
                }
                lastTouchX = (int) event.getRawX();
                lastTouchY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                isBeingDraged = false;
                endY = (int) mainView.getTranslationY();
                if (curState == State.STATE_RELEASE_TO_REFRESH) {
                    if (scrollistener != null) {
                        startReleaseAnim();
                    } else {
                        startTranslateAnim();
                    }
                }else if(curState == State.STATE_REFRESHING){
                    startReleaseAnim();
                }else {
                    startTranslateAnim();
                }

                break;
        }
        return true;
    }

    public void translateToSecondaryView() {
        endY = mainView.getHeight();
        isScrollerDown = true;
        startTranslateAnim();
    }

    public void translateToMainView() {
        endY = mainView.getHeight() / 3;
        isScrollerDown = false;
        startTranslateAnim();
    }

    public void finishRefresh() {
        translateToMainView();
    }

    public boolean isStillMainView() {
        return endY == 0 && mainView.getTranslationY() == 0;
    }

    public String getCurState() {
        return curState;
    }

    public void setCurState(String curState) {
        this.curState = curState;
    }

    private void startReleaseAnim() {
        float secondTranslationY = pullRefreshDistance;
        float mainTranslationY = pullRefreshDistance;
        AnimatorSet animator = new AnimatorSet();
        ObjectAnimator animator_secondaryView = ObjectAnimator.ofFloat(secondaryView, "translationY", secondTranslationY);
        ObjectAnimator animator_mainView = ObjectAnimator.ofFloat(mainView, "translationY", mainTranslationY);
        animator_mainView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (scrollistener != null) {
                    scrollistener.onScroll((int) value);
                }
            }
        });
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                curState = State.STATE_REFRESHING;
                endY = (int) mainView.getTranslationY();
                if (scrollistener != null) {
                    scrollistener.onRefresh();
                }
                if (stateListener != null) {
                    stateListener.onStateRefreshing();
                }
                Log.d("mytag", "----curState===" + curState);
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

    private void startTranslateAnim() {
        float secondTranslationY = 0;
        float mainTranslationY = 0;
        if (isScrollerDown) {//下滑
            if (endY > secondaryView.getHeight() / 3-100) {
                secondTranslationY = secondaryView.getHeight()-topOffset;
                mainTranslationY = mainView.getHeight();
                isScrollerDown = false;
            } else {
                secondTranslationY = 0;
                mainTranslationY = 0;
                isScrollerDown = true;
            }
        } else {
            if (endY < mainView.getHeight() * 2 / 3+100) {
                secondTranslationY = 0;
                mainTranslationY = 0;
                isScrollerDown = true;
            } else {
                secondTranslationY = secondaryView.getHeight()-topOffset;
                mainTranslationY = mainView.getHeight();
                isScrollerDown = false;
            }
        }
        AnimatorSet animator = new AnimatorSet();
        ObjectAnimator animator_secondaryView = ObjectAnimator.ofFloat(secondaryView, "translationY", secondTranslationY);
        ObjectAnimator animator_mainView = ObjectAnimator.ofFloat(mainView, "translationY", mainTranslationY);
        animator_mainView.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (scrollistener != null) {
                    scrollistener.onScroll((int) value);
                }
            }
        });
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                endY = (int) mainView.getTranslationY();
                Log.d("mytag", "----endy==" + endY);
                if (endY == 0) {
                    curState = State.STATE_MAIN;
                    if (stateListener != null) {
                        stateListener.onStateMain();
                    }
                } else if (endY >= mainView.getHeight()) {
                    curState = State.STATE_SECOND;
                    if (stateListener != null) {
                        stateListener.onStateSecond();
                    }
                }
                long time = System.currentTimeMillis();
                if (isBeingDraged) {
                    isBeingDraged = false;
                    SecondFloorView.super.dispatchTouchEvent(MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, lastTouchX, lastTouchY, 0));
                    SecondFloorView.super.dispatchTouchEvent(MotionEvent.obtain(time, time, MotionEvent.ACTION_MOVE, lastTouchX, lastTouchY, 0));
                }
                Log.d("mytag", "----curState===" + curState);
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
        return new SecondFloorView.LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new SecondFloorView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new SecondFloorView.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof SecondFloorView.LayoutParams;
    }

    public float getResistance() {
        return resistance;
    }

    public void setResistance(float resistance) {
        this.resistance = resistance;
    }

    public OnSecondFloorScrollistener getSecondFloorScrollistener() {
        return scrollistener;
    }

    public void setOnSecondFloorScrollistener(OnSecondFloorScrollistener scrollistener) {
        this.scrollistener = scrollistener;
    }

    public OnStateListener getOnStateListener() {
        return stateListener;
    }

    public void setOnStateListener(OnStateListener stateListener) {
        this.stateListener = stateListener;
    }

    public static class State {
        public static final String STATE_IDAL = "STATE_IDAL";
        public static final String STATE_PULL_TO_REFRESH = "STATE_PULL_TO_REFRESH";
        public static final String STATE_RELEASE_TO_REFRESH = "STATE_RELEASE_TO_REFRESH";
        public static final String STATE_REFRESHING = "STATE_REFRESHING";
        public static final String STATE_RELEASE_TO_SECOND = "STATE_RELEASE_TO_SECOND";
        public static final String STATE_RELEASE_TO_MAIN = "STATE_RELEASE_TO_MAIN";
        public static final String STATE_MAIN = "STATE_MAIN";
        public static final String STATE_SECOND = "STATE_SECOND";
    }
}
