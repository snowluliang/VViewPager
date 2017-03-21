package snow.com.ppviewpager.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 图片轮播的核心容器.
 */

public class ImageViewGroup extends ViewGroup {


    private int childCount;//子视图的个数
    private int childHeight;//子视图的高度
    private int childWidth;//子视图的宽度

    private int x;//按下位置的横坐标,
    private int index;//图片下标索引





    public ImageViewGroup(Context context) {
        super(context);
        initView();
    }

    public ImageViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public ImageViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();

    }
    //使用Scroller对象进行滑动
    private Scroller mScroller;

    //自动轮播
    private Timer timer = new Timer();
    private TimerTask task ;

    private boolean isAuto = true;
    private int mIndex = 0;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (++mIndex >= childCount) {
                        mIndex = 0;
                    }
                        scrollTo(childWidth * mIndex, 0);
                        break;
            }

        }
    };


    private void initView() {
        mScroller = new Scroller(getContext());
        task = new TimerTask() {
            @Override
            public void run() {
                if (isAuto) {
                    handler.sendEmptyMessage(0);
                }

            }
        };
        timer.schedule(task, 200, 3000);

    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    private ViewGroupListener listener;
    private boolean isClick;

    public void setListener(ViewGroupListener listener) {
        this.listener = listener;
    }

    public ViewGroupListener getListener() {
        return listener;
    }

    public interface ViewGroupListener {
        void clickImgListener(int pos);
    }

    /**
     * 布局的测量方法,
     * @param widthMeasureSpec 测量宽度
     * @param heightMeasureSpec 测量高度
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //要确定ViewGroup容器的大小,就要先测量ViewGroup的子view的个数
         childCount = getChildCount();
        if (childCount == 0) {
            //说明没有子view,可以直接确定ViewGroup容器的大小
            setMeasuredDimension(0,0);
        } else {
            //如果有子view,先测量子view的宽度和高度
            measureChildren(widthMeasureSpec, heightMeasureSpec);
            //以第一个子视图为基准,ViewGroup的高度是第一个子视图的高度,
            // 宽度是第一个子视图的宽度 * 子视图的个数
            View view = getChildAt(0);
            childHeight = view.getMeasuredHeight();
            childWidth = view.getMeasuredWidth();
            int width = view.getMeasuredWidth() * childCount;
            setMeasuredDimension(width, childHeight);
        }

    }

    /**
     * 自定义ViewGroup 必须实现布局的onLayout方法,
     * @param changed 位置是否改变
     * @param l       左
     * @param t       上
     * @param r       右
     * @param b       下
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int leftMargin = 0;
            for (int i = 0; i < childCount; i++) {
                View view = getChildAt(i);
                view.layout(leftMargin, t, leftMargin + childWidth, childHeight);
                leftMargin += childWidth;

            }
        }

    }

    /**
     * 事件拦截,返回true则代表处理此次事件,不往下传递,false表示向下分发,询问子View是否处理
     *
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 事件处理,根据动作处理事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN://按下
                isAuto = false;
                isClick = true;
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                x = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE://滑动
                int moveX = (int) event.getX();
                int distance = moveX - x;
                scrollBy(-distance, 0);
                x = moveX;
                break;
            case MotionEvent.ACTION_UP://抬起
                int scrollX = getScrollX();
                index = (scrollX + childWidth / 2) / childWidth;
                if (index < 0) {
                    index = 0;
                } else if (index > childCount - 1) {
                    index = childCount - 1;
                }

                if (isClick) {
                    listener.clickImgListener(index);
                } else {
                    int dx = index * childWidth - scrollX;

                    mScroller.startScroll(scrollX, 0, dx, 0);
                    postInvalidate();
                }

                isAuto = true;
//                scrollTo(index*childWidth,0);
                break;

        }

        return true;//代表处理此次事件
    }
}
