package com.ike.taxi.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.base.BaseRecyclerAdapter;
import com.ike.taxi.base.BaseRecyclerHolder;
import com.ike.taxi.entity.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 讨论功能
 */
public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private List<Comment> list;
    private RecyclerView recyclerView;
    private BaseRecyclerAdapter<Comment> adapter;
    private EditText text;
    private Button btn_click;
    private TextView tv_time;
    private TextView tv_user;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();

        list=new ArrayList<>();
        initList();
        initAdapter();
        initRecyclerClick();

    }

    private void initView() {
        recyclerView= (RecyclerView) findViewById(R.id.recycleView);
        text= (EditText) findViewById(R.id.et_test);
        btn_click= (Button) findViewById(R.id.btn_click);
        btn_click.setOnClickListener(this);
        tv_time= (TextView) findViewById(R.id.tv_comment_time);
        tv_user= (TextView) findViewById(R.id.tv_comment_user);
    }

    private void initRecyclerClick() {
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, final View view, int position) {
                Toast.makeText(CommentActivity.this, String.format(Locale.CHINA,
                        "你点击了第%d项,长按会删除！",position+1),Toast.LENGTH_SHORT).show();
            }
        });

        //长按
        adapter.setOnItemLongClickListener(new BaseRecyclerAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position) {
                adapter.delete(position);
                return true;
            }
        });
    }

    private void initAdapter() {
        adapter = new BaseRecyclerAdapter<Comment>(this,list,R.layout.comment_item_list) {
            @Override
            public void convert(
                    BaseRecyclerHolder holder, Comment item, int position, boolean isScrolling) {
                holder.setText(R.id.tv_comment_content,item.getText());
                holder.setText(R.id.tv_comment_time,item.getTime());
                holder.setText(R.id.tv_comment_user,item.getUser());
                if (item.getImageUrl() != null){
                    holder.setImageByUrl(R.id.iv_head,item.getImageUrl());
                }else {
                    holder.setImageResource(R.id.iv_head,item.getImageId());
                }
            }
        };
        recyclerView.setAdapter(adapter);
        //布局管理
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(
                this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        //分割线
//        recyclerView.addItemDecoration(new ItemDivider(this,ItemDivider.VERTICAL_LIST));
    }

    private void initList() {
        String sss="2016-08-15 16:00:22";
        String ss="sdsdsd";
        for (int i = 1; i < 6; i++) {
            list.add(new Comment("张"+i," "+i,"你好吗？"+"+"+i,
                    "http://img0.imgtn.bdimg.com/it/u=1417959626,20178292&fm=21&gp=0.jpg"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_click:
                String string = text.getText().toString().trim();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(System.currentTimeMillis());
                time=dateFormat.format(date);
                if("".equals(string)){
                    Toast.makeText(CommentActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    Comment data = new Comment("张三", time, string,
                            "http://img0.imgtn.bdimg.com/it/u=1417959626,20178292&fm=21&gp=0.jpg");
                    adapter.insert(data, 0);
                    Toast.makeText(CommentActivity.this, list.size() + "", Toast.LENGTH_SHORT).show();
                    text.setText("");
                }
                break;
            default:
                break;
        }
    }
}
