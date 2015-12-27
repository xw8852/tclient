package com.msx7.android.tclient.ui.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiaowei on 2015/12/22.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
    protected Context context;
    protected List<T> data;
    protected LayoutInflater mInflater;

    public BaseAdapter(Context context, T... data) {
        this(context, Arrays.asList(data));
    }

    public BaseAdapter(Context context, List<T> data) {
        super();
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.data = new ArrayList<T>();
        if (data != null) this.data.addAll(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void change(List<T> data) {
        if (data == null) return;
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void addMore(List<T> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void add(T t) {
        if (t == null) return;
        this.data.add(t);
        notifyDataSetChanged();
    }

    public void addAll(int index, List<T> list) {
        if (list == null) return;
        this.data.addAll(index, list);
        notifyDataSetChanged();
    }

    public void remove(T t) {
        if (data == null) {
            return;
        }
        data.remove(t);
        notifyDataSetChanged();
    }

    public void remove(int index) {
        if (data == null) {
            return;
        }
        data.remove(index);
        notifyDataSetChanged();
    }


    public List<T> getList() {
        return data;
    }

    public void setList(List<T> list) {
        this.data = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, mInflater);
    }

    /**
     * @param @param  position
     * @param @param  convertView
     * @param @param  inflater
     * @param @return
     * @return View
     * @throws
     * @Title: createView
     * @Description: 创建自定义的listView item
     */
    public abstract View createView(int position, View convertView, LayoutInflater inflater);

}