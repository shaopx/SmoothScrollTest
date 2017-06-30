package com.spx.smoothscrolltest;


import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Created by SHAOPENGXIANG on 2017/6/6.
 */

public class MyLinearLayout extends LinearLayout {
    private static final String TAG = "MyLinearLayout";
    private static final long ANIMI_OUT_TIME = 500;
    private static final long SCROLL_TIME = 20;

    int smallHeight = 64 * 3;
    int bigHeight = 150 * 3;

    float expandStep = 0f;

    int smallColor = Color.parseColor("#f9f9f9");
    int bigColor = Color.parseColor("#ffffff");

    public int expandChildIndex = -1;
    public int folderChildIndex = -1;

    ScrollView myScrollView = null;
    Handler mHandler = new Handler();
    int expandingIndex = -1;
    int targetScrollY = -1;

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollView(ScrollView scrollView) {
        myScrollView = scrollView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        smallHeight = getResources().getDimensionPixelSize(R.dimen.small_height);
        bigHeight = getResources().getDimensionPixelSize(R.dimen.big_height);

        expandStep = (bigHeight - smallHeight) / (1.0f * ANIMI_OUT_TIME);
        Log.d(TAG, "onFinishInflate: expandStep:" + expandStep);
    }

    public void onItemClicked(int position, final View view) {

        if (expandChildIndex != -1 && expandChildIndex == position) {
            return;
        }

        if (expandChildIndex != -1 && expandChildIndex != position) {
            folderView(expandChildIndex, getChildAt(expandChildIndex), expandChildIndex < position);
        }


        expandingIndex = position;
        expandView(position, view);

        scrollIfNeed(position, expandChildIndex);

    }

    private void scrollIfNeed(int expandIndex, int lastExpandIndex) {
        Log.d(TAG, "scrollIfNeed: expandIndex:" + expandIndex + ", lastExpandIndex:" + lastExpandIndex);
        Log.d(TAG, "scrollIfNeed: getScrollY:" + myScrollView.getScrollY());

        int currentScrollY = myScrollView.getScrollY();

        int targetScrollY = smallHeight * (expandIndex);
        Log.d(TAG, "scrollIfNeed: targetScrollY:" + targetScrollY);

        int count = 0;
        int diff = targetScrollY - currentScrollY;


        //mHandler.postDelayed(scrollTask, 20);

        int start = myScrollView.getScrollY();
        int end = expandingIndex * smallHeight;
        Log.d(TAG, "scrollIfNeed: anim  start:" + start + ", end:" + end);
        if (start == end) {
            return;
        }
        if (start > end) {
            ValueAnimator scrollYAnim = ValueAnimator.ofInt(start, end).setDuration(ANIMI_OUT_TIME);
            scrollYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer animatedValue = (Integer) animation.getAnimatedValue();
                    if (myScrollView.getScrollY() > animatedValue) {
                        myScrollView.smoothScrollBy(0, -(myScrollView.getScrollY() - animatedValue));
                    }
                }
            });
            scrollYAnim.start();
            return;
        }

        ValueAnimator scrollYAnim = ValueAnimator.ofInt(start, end).setDuration(ANIMI_OUT_TIME);
        scrollYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                if (myScrollView.getScrollY() < animatedValue) {
                    myScrollView.smoothScrollBy(0, animatedValue - myScrollView.getScrollY());
                }
            }
        });
        scrollYAnim.start();
    }


    Runnable scrollTask = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "scrollTask: run:...expand:" + expandingIndex + ", fold:" + folderChildIndex);
            View expandingChild = getChildAt(expandingIndex);

            if (folderChildIndex == -1) {//如果是没有要闭合的view, 也就是第一次
                if (myScrollView.getScrollY() < expandingIndex * smallHeight) {
                    int delta = (int) (SCROLL_TIME * expandStep);
                    myScrollView.smoothScrollBy(0, delta);
                    mHandler.postDelayed(scrollTask, SCROLL_TIME);
                    return;
                } else {
                    return;
                }
            }

            if (expandingIndex > folderChildIndex) {  //如果要扩大的view在上, 那只可能向上滑动
                if (myScrollView.getScrollY() > expandingIndex * smallHeight) {
                    myScrollView.smoothScrollBy(0, 9);
                    mHandler.postDelayed(scrollTask, SCROLL_TIME);
                }
            } else {

            }

            if (expandingChild.getY() > expandingIndex * smallHeight && myScrollView.getScrollY() > expandingIndex * smallHeight) {
                mHandler.postDelayed(scrollTask, SCROLL_TIME);
            } else {

            }
        }
    };

    public void expandView(final int position, final View view) {
        final View expandLayout = view.findViewById(R.id.expand_layout);
        expandLayout.setVisibility(VISIBLE);
        expandLayout.setAlpha(1.0f);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                expandChildIndex = position;
            }
        }, ANIMI_OUT_TIME);

        ValueAnimator heightAnim = ValueAnimator.ofInt(smallHeight, bigHeight).setDuration(ANIMI_OUT_TIME);
        heightAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                Float value = animation.getAnimatedFraction();


                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = animatedValue;
//                if (expandLayout != null)
//                    expandLayout.setTranslationY(bigHeight - animatedValue);
                view.requestLayout();
            }
        });
        heightAnim.start();

//        ValueAnimator transYAnim = ValueAnimator.ofInt(smallHeight, 0).setDuration(ANIMI_OUT_TIME);
//        transYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                Integer animatedValue = (Integer) animation.getAnimatedValue();
//                if (expandLayout != null)
//                    expandLayout.setTranslationY(animatedValue);
//                view.requestLayout();
//            }
//        });
//        transYAnim.start();

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), smallColor, bigColor)
                .setDuration(ANIMI_OUT_TIME);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                //Log.d(TAG, "onFinished: bg color:" + animatedValue);
                if (animatedValue != null) {
                    view.setBackgroundColor((Integer) animatedValue);
                }
            }
        });
        colorAnimator.start();

        final View collLayout = view.findViewById(R.id.coll_layout);
        ValueAnimator alphaAnim = ValueAnimator.ofFloat(0.8f, 0.0f).setDuration(ANIMI_OUT_TIME);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                collLayout.setAlpha(value);
            }
        });
        alphaAnim.start();
    }

    public void folderView(int position, final View view, boolean foldFast) {
        folderChildIndex = position;
        final View expandLayout = view.findViewById(R.id.expand_layout);
        final View collLayout = view.findViewById(R.id.coll_layout);

//        collLayout.setBackgroundResource(R.color.transparent);

//        if (expandLayout != null)
//            expandLayout.setVisibility(INVISIBLE);

//        if (foldFast) {
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.height = smallHeight;
//            view.requestLayout();
//            return;
//        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(bigHeight, smallHeight).setDuration(ANIMI_OUT_TIME);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer animatedValue = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = animatedValue;
                view.requestLayout();
            }
        });
        valueAnimator.start();

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), bigColor, smallColor)
                .setDuration(ANIMI_OUT_TIME);
        colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                //Log.d(TAG, "onFinished: bg color:" + animatedValue);
                if (animatedValue != null) {
                    view.setBackgroundColor((Integer) animatedValue);
                }
            }
        });
        colorAnimator.start();


        ValueAnimator alphaAnim = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(ANIMI_OUT_TIME);
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                expandLayout.setAlpha(value);
                collLayout.setAlpha(1.0f - value);
            }
        });
        alphaAnim.start();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        //Log.d(TAG, "onLayout: ... change:"+changed+", t:"+t+", b:"+b);
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        final int paddingLeft = getPaddingLeft();

        int childTop;
        int childLeft;

        // Where right end of child should go
        final int width = getWidth();
        int childRight = width - getPaddingRight();

        // Space available for child
        int childSpace = width - paddingLeft - mPaddingRight;

        final int count = getChildCount();

        int mPaddingTop = getPaddingTop();
        childTop = mPaddingTop;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child == null) {
                childTop += 0;
            } else if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp =
                        (LinearLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                if (gravity < 0) {
                    gravity = 0;
                }
                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        childLeft = childRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin;
                        break;
                }

//                if (hasDividerBeforeChildAt(i)) {
//                    childTop += mDividerHeight;
//                }

                childTop += lp.topMargin;
                setChildFrame(child, childLeft, childTop + getLocationOffset(child),
                        childWidth, childHeight);
                childTop += childHeight + lp.bottomMargin + getNextLocationOffset(child);

                i += getChildrenSkipCount(child, i);
            }
        }
    }

    private void setChildFrame(View child, int left, int top, int width, int height) {
        child.layout(left, top, left + width, top + height);
    }

    int getLocationOffset(View child) {
        return 0;
    }

    int getNextLocationOffset(View child) {
        return 0;
    }

    int getChildrenSkipCount(View child, int index) {
        return 0;
    }

}
