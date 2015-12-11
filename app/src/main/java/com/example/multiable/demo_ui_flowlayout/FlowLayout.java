package com.example.multiable.demo_ui_flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macremote on 2015/12/4.
 */
public class FlowLayout extends ViewGroup {
    private static final String TAG = "FlowLayout" ;
    public FlowLayout(Context context,AttributeSet attrs){
        super(context,attrs) ;
    }
    public FlowLayout(Context context){
        super(context) ;
    }

    /**返回一组基于提供的属性集合的布局参数集合
     * 只需要支持margin,所以直接使用系统的MarginLayoutParams
     * @param attrs
     * @return
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

    /**
     * 负责设置子控件的测量模式和大小，根据所有子控件设置自己的宽高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父容器的宽高和模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) ;
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec) ;
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec) ;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec) ;
        //如果是wrap_content,记录宽高 AT_MOST
        int width = 0;
        int height = 0;
        //记录每一行的宽度
        int lineWidth = 0 ;
        //记录每一行的高度
        int lineHeight = 0;
        //记录子控件的个数
        int cCount = getChildCount() ;
        //遍历每个子元素
        for(int i=0;i<cCount;i++){
            View child = getChildAt(i) ;
            //测量每个view的宽高
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            //得到child的LayoutParams
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams() ;
            //获得当前child实际所占的宽高
            int childWidth = child.getMeasuredWidth()+lp.leftMargin+lp.rightMargin ;
            int childHeight = child.getMeasuredHeight()+lp.topMargin+lp.bottomMargin;
            //如果加入的child，超出最大宽度
            if(lineWidth+childWidth>sizeWidth){
                width = Math.max(lineWidth,childWidth) ;
                //重新开启一行，lineWidth记录新行的宽度
                lineWidth = childWidth ;
                //叠加高度
                height += lineHeight ;
                //记录新一行的高度
                lineHeight = childHeight ;
            }
            else{
                //累加宽度
                lineWidth += childWidth ;
                //高度取该行最高的childView
                lineHeight = Math.max(lineHeight,childHeight) ;
            }
            //如果是最后一个,则将当前记录的最大宽度和当前的lineWidth做比较
            if(i == cCount-1){
                width = Math.max(width,lineWidth);
                height += lineHeight ;
            }
        }
        setMeasuredDimension((modeWidth==MeasureSpec.EXACTLY) ? sizeWidth:width,
                (modeHeight==MeasureSpec.EXACTLY) ? sizeHeight:height);
    }

    //存储所有的view，按行记录
    private List<List<View>> mAllViews = new ArrayList<List<View>>() ;
    //记录每一行的最大高度
    private List<Integer> mLineHeight = new ArrayList<Integer>() ;

    /**指定所有childView的位置以及大小
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();
        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;
        //存储每一行所有的childView
        List<View> lineViews = new ArrayList<>() ;
        int cCount = getChildCount() ;
        for(int i=0;i<cCount;i++){
            View child = getChildAt(i) ;
            MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams() ;
            int childWidth = child.getMeasuredWidth() ;
            int childHeight = child.getMeasuredHeight() ;
            //如果已经需要换行
            if(childWidth+lp.leftMargin+lp.rightMargin+lineWidth>width){
                //记录这行所有的view和最高高度
                mLineHeight.add(lineHeight) ;
                //将当前行的childView保存
                mAllViews.add(lineViews) ;
                //使用新的ArrayList记录下一行的childView
                lineWidth = 0;
                lineViews = new ArrayList<View>() ;
            }
            //如果不需要换行，累加
            lineWidth += childWidth+lp.leftMargin+lp.rightMargin;
            lineHeight = Math.max(lineHeight,childHeight +lp.topMargin+lp.bottomMargin);
            lineViews.add(child) ;
        }
        //记录最后一行
        mLineHeight.add(lineHeight) ;
        mAllViews.add(lineViews) ;

        int left = 0;
        int top = 0;
        //得到总行数
        int lineNums = mAllViews.size();
        for(int i=0;i<lineNums;i++){
            //每一行的所有views
            lineViews = mAllViews.get(i) ;
            //当前行的最大高度
            lineHeight = mLineHeight.get(i) ;
            //遍历所有的view
            for(int j=0;j<lineViews.size();j++){
                View child = lineViews.get(j) ;
                if(child.getVisibility()==View.GONE){
                    continue;
                }
                MarginLayoutParams lp = (MarginLayoutParams)child.getLayoutParams();
                //计算childView的left,top,right,bottom
                int lc = left+lp.leftMargin ;
                int tc = top+lp.topMargin ;
                int rc = lc+child.getMeasuredWidth();
                int bc = tc+child.getMeasuredHeight();
                child.layout(lc,tc,rc,bc);
                left+=child.getMeasuredWidth()+lp.rightMargin+lp.leftMargin;
            }
            left = 0;
            top+=lineHeight ;
        }
    }
}
