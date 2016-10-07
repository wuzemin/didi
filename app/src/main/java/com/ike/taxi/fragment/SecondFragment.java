package com.ike.taxi.fragment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseRecyclerAdapter;
import com.ike.taxi.base.BaseRecyclerHolder;
import com.ike.taxi.entity.Carpool;
import com.ike.taxi.widget.ItemDivider;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<Carpool> list=new ArrayList<>();
    private BaseRecyclerAdapter<Carpool> adapter;
    private View view;
    private FloatingActionButton floatingActionButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_second, container, false);
        initView();
        //测试数据
        initList();
        initAdapter();
        return view;
    }

    private void initAdapter() {
        adapter=new BaseRecyclerAdapter<Carpool>(getActivity(),list,R.layout.carpool_item_list) {
            @Override
            public void convert(BaseRecyclerHolder holder, Carpool item,
                                int position, boolean isScrolling) {
                holder.setText(R.id.tv_carpool_user,item.getUser());
                holder.setText(R.id.tv_carpool_startPoint,item.getStartPoint());
                holder.setText(R.id.tv_carpool_time,item.getTime());
                holder.setText(R.id.tv_carpool_text,item.getText());
                holder.setText(R.id.tv_carpool_endPoint,item.getEndPoint());
                holder.setText(R.id.tv_carpool_distance,item.getDistance());
                holder.setImageByUrl(R.id.iv_carpool_head,item.getImage_head());
                holder.setImageByUrl(R.id.iv_carpool_image,item.getImage_content());

            }
        };
        mRecyclerView.setAdapter(adapter);
        //布局管理
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new ItemDivider(getActivity(),ItemDivider.VERTICAL_LIST));
    }

    private void initList() {
        for(int i=0;i<5;i++){
            list.add(new Carpool(
                    "http://img0.imgtn.bdimg.com/it/u=1417959626,20178292&fm=21&gp=0.jpg",
                    "张三", "广州棠东","广州塔","1分钟前","12公里", "我想去看小蛮腰，谁要一起吗？",
                    "http://img0.imgtn.bdimg.com/it/u=1417959626,20178292&fm=21&gp=0.jpg"));
        }
    }

    private void initView() {
        mRecyclerView= (RecyclerView) view.findViewById(R.id.recycleView);
        floatingActionButton= (FloatingActionButton) view.findViewById(R.id.fb);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"等一会啊",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
