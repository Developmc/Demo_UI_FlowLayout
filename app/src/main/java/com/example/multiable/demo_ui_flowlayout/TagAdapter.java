package com.example.multiable.demo_ui_flowlayout;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macremote on 2015/12/10.
 */
public abstract class TagAdapter<T> {
    private List<T> mTagDatas ;
    private OnDataChangedListener mOnDataChangeListener ;
    //接口
    static interface OnDataChangedListener{
        void onChanged() ;
    }
    public TagAdapter(List<T> datas){
        mTagDatas = datas ;
    }
    public TagAdapter(T[] datas){
        mTagDatas = new ArrayList<T>(Arrays.asList(datas)) ;
    }
    void setOnDataChangedListener(OnDataChangedListener listener){
        mOnDataChangeListener = listener ;
    }
    public int getCount(){
        return mTagDatas == null?0:mTagDatas.size() ;
    }
    public void notifyDataChanged(){
        mOnDataChangeListener.onChanged();
    }
    public T getItem(int position){
        return mTagDatas.get(position) ;
    }
    public abstract View getView(FlowLayout parent,int position,T t);
}
