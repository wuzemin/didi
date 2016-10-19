package com.ike.taxi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.ike.taxi.fragment.FirstFragment;
import com.ike.taxi.fragment.PersonalFragment;
import com.ike.taxi.fragment.SecondFragment;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity{
    private RadioGroup radioGroup;
    private FirstFragment firstFragment;
    private SecondFragment secondFragment;
    private PersonalFragment thirdFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }
    public void initView(){
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
    }
}
