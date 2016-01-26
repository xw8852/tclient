package com.msx7.android.tclient.ui.list;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

/**
 * 文件名: BinderListView.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */

public class BinderListView<T> {
    ListView mListView;
    IList<T> mListListener;
    BinderAdapter mAdapter;



    public BinderListView(ListView listView) {
        this.mListView = listView;
        init();
    }


    void init() {
        mAdapter = new BinderAdapter(mListView.getContext(), new ArrayList<T>());
        mListView.setAdapter(mAdapter);
    }




    /**
     * 设置ListView的数据源
     * @param data
     */
    public void setData(List<T> data) {
        mAdapter.change(data);
    }

    public void addData(List<T> data) {
        mAdapter.addMore(data);
    }




    public IList<T> getIList() {
        return mListListener;
    }

    public BinderListView<T> setIList(IList<T> ilist) {
        this.mListListener = ilist;
        return this;
    }



    class BinderAdapter extends BaseAdapter<T> {
        public BinderAdapter(Context context, List<T> data) {
            super(context, data);
        }

        /**
         * @param position
         * @param convertView
         * @param inflater    @return View
         * @throws
         * @Title: createView
         * @Description: 创建自定义的listView item
         */
        @Override
        public View createView(int position, View convertView, LayoutInflater inflater) {
            return getIList().BinderData(getItem(position), convertView, inflater);
        }
    }

}
