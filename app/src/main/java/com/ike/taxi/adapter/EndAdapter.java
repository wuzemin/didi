package com.ike.taxi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ike.taxi.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Min on 2016/7/21.
 * 终点搜索
 */
public class EndAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Map<String,Object>> data=new ArrayList<Map<String,Object>>();
    private View view;

    public EndAdapter(Context context, List<Map<String,Object>> data) {
        this.context = context;
        this.data = data;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view=convertView;
        ViewHolder holder;
        if(view==null){
            view=inflater.inflate(R.layout.list_item,null);
            holder=new ViewHolder();
            holder.iv_icon= (ImageView) view.findViewById(R.id.imageView);
            holder.tv_title= (TextView) view.findViewById(R.id.tv_end_title);
            holder.tv_addr= (TextView) view.findViewById(R.id.tv_end_addr);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        if(null!=data && position<data.size()){
            Map<String,Object> row=data.get(position);
            holder.tv_title.setText((CharSequence) row.get("名称"));
            holder.tv_addr.setText((CharSequence) row.get("地址"));
        }
        return view;
    }
}
class ViewHolder{
    ImageView iv_icon;
    TextView tv_title;
    TextView tv_addr;
}
