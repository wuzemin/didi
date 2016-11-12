package com.ike.taxi.chat.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ike.taxi.R;
import com.ike.taxi.chat.bean.FriendInfo;
import com.ike.taxi.widget.image.SelectableRoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/11/12.
 */

public class SelectFriendAdapter extends RecyclerView.Adapter<MyHolder> {
    private Context context;
    private List<FriendInfo> list= new ArrayList<>(0);
    private LayoutInflater inflater;

    public SelectFriendAdapter(Context context, List<FriendInfo> list) {
        this.context = context;
        this.list = list;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyHolder(inflater.inflate(R.layout.select_friend_item,parent,false));
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.siv.setImageURI(Uri.parse(list.get(position).getPortraitUri()));
        holder.tv_friend_name.setText(list.get(position).getName());
        holder.checkBox.setTag(position);
//        holder.checkBox.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class MyHolder extends RecyclerView.ViewHolder{
    SelectableRoundedImageView siv;
    TextView tv_friend_name;
    CheckBox checkBox;

    public MyHolder(View itemView) {
        super(itemView);
        siv= (SelectableRoundedImageView) itemView.findViewById(R.id.siv_group_icon);
        tv_friend_name= (TextView) itemView.findViewById(R.id.tv_friend_name);
        checkBox= (CheckBox) itemView.findViewById(R.id.cb_select_friend);
    }
}