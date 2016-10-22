package com.ike.taxi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.fragment.FirstFragment;
import com.ike.taxi.fragment.PersonalFragment;
import com.ike.taxi.fragment.SecondFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.iv_taxi)
    ImageView ivTaxi;
    @BindView(R.id.tv_taxi)
    TextView tvTzaxi;
    @BindView(R.id.rl_taxi)
    RelativeLayout rlTaxi;
    @BindView(R.id.iv_special)
    ImageView ivSpecial;
    @BindView(R.id.tv_special)
    TextView tvSpecial;
    @BindView(R.id.rl_special)
    RelativeLayout rlSpecial;
    @BindView(R.id.iv_personal)
    ImageView ivPersonal;
    @BindView(R.id.tv_personal)
    TextView tvPersonal;
    @BindView(R.id.rl_personal)
    RelativeLayout rlPersonal;

    private List<Fragment> mFragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViewPager();
        initView();
        changeTextViewColor();
        changeSelectedTabState(0);
//        initView();

    }

    private void initView() {
        rlTaxi.setOnClickListener(this);
        rlSpecial.setOnClickListener(this);
        rlPersonal.setOnClickListener(this);
    }

    private void initViewPager() {
        mFragment.add(new FirstFragment());
        mFragment.add(new SecondFragment());
        mFragment.add(new PersonalFragment());
        FragmentPagerAdapter fragment= new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }

        };
        mViewPager.setAdapter(fragment);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        changeTextViewColor();
        changeSelectedTabState(position);

    }

    private void changeSelectedTabState(int position) {
        switch (position) {
            case 0:
                tvTzaxi.setTextColor(Color.parseColor("#0099ff"));
                ivTaxi.setBackground(ContextCompat.getDrawable(this, R.mipmap.info_pressed));
                break;
            case 1:
                tvSpecial.setTextColor(Color.parseColor("#0099ff"));
                ivSpecial.setBackground(ContextCompat.getDrawable(this, R.mipmap.affairs_pressed));
                break;
            case 2:
                tvPersonal.setTextColor(Color.parseColor("#0099ff"));
                ivPersonal.setBackground(ContextCompat.getDrawable(this, R.mipmap.personal_pressed));
                break;
            default:
                break;
        }
    }

    private void changeTextViewColor() {
        ivTaxi.setBackground(ContextCompat.getDrawable(MainActivity.this, R.mipmap.info_normal));
        ivSpecial.setBackground(ContextCompat.getDrawable(MainActivity.this, R.mipmap.affairs_normal));
        ivPersonal.setBackground(ContextCompat.getDrawable(MainActivity.this, R.mipmap.personal_normal));
        tvTzaxi.setTextColor(Color.parseColor("#abadbb"));
        tvSpecial.setTextColor(Color.parseColor("#abadbb"));
        tvPersonal.setTextColor(Color.parseColor("#abadbb"));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_taxi:
                mViewPager.setCurrentItem(0,false);
                break;
            case R.id.rl_special:
                mViewPager.setCurrentItem(1,false);
                break;
            case R.id.rl_personal:
                mViewPager.setCurrentItem(2,false);
                break;
            default:
                break;
        }
    }
    /*public void initView(){
        firstFragment=new FirstFragment();
        getSupportFragmentManager().beginTransaction().replace(
                R.id.main_content,firstFragment).commit();
        radioGroup= (RadioGroup) findViewById(R.id.tab_menu);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_First:
                        firstFragment=new FirstFragment();
                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.main_content,firstFragment).commit();
                        break;
                    case R.id.rb_second:
                        secondFragment=new SecondFragment();
                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.main_content,secondFragment).commit();
                        break;
                    case R.id.rb_Third:
                        thirdFragment=new PersonalFragment();
                        getSupportFragmentManager().beginTransaction().replace(
                                R.id.main_content,thirdFragment).commit();
                        break;
                    default:
                        break;
                }
            }
        });
    }*/
}
