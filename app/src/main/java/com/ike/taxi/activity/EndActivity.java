package com.ike.taxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ike.taxi.R;
import com.ike.taxi.adapter.EndAdapter;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.entity.End;
import com.ike.taxi.utils.AMapUtil;
import com.ike.taxi.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 终点搜索
 */
public class EndActivity extends BaseActivity implements View.OnClickListener,
        PoiSearch.OnPoiSearchListener, AdapterView.OnItemClickListener, TextWatcher, Inputtips.InputtipsListener {
//    private EditText et_end;
    private AutoCompleteTextView et_end;
    private Button btn_search;
    private ImageView iv_left_arrow;
    private ListView ll_end;
    private PoiSearch.Query query;
    private PoiResult result;
    private List<PoiItem> poiItems;
    private TextView textView;
    private List<End> list=new ArrayList<>();
    private ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    private EndAdapter adapter;
    private final int REQUEST_CODE=1;

    private String keyWord="";
    private String ctgr="汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|" +
            "医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|" +
            "金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        initView();
    }

    private void initView() {
        et_end= (AutoCompleteTextView) findViewById(R.id.et_end);
        btn_search= (Button) findViewById(R.id.btn_search);
        textView= (TextView) findViewById(R.id.text2);
        ll_end= (ListView) findViewById(R.id.ll_end);
        iv_left_arrow= (ImageView) findViewById(R.id.iv_left_arrow);

        et_end.addTextChangedListener(this);
        iv_left_arrow.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        btn_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH || event!=null &&
                        event.getKeyCode()==KeyEvent.KEYCODE_ENTER ){
                    data.clear();
                    doPoisearch();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search:
                data.clear();
                doPoisearch();
                break;
            case R.id.iv_left_arrow:
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                bundle.putString("addr","");
                bundle.putSerializable("lat","");
                intent.putExtras(bundle);
                setResult(REQUEST_CODE,intent);
                EndActivity.this.finish();
            default:
                break;
        }
    }
    private void doPoisearch() {
        if(null!=et_end.getText().toString()){
            keyWord=et_end.getText().toString();
            query = new PoiSearch.Query(keyWord, ctgr, "广州");
            query.setPageSize(20);
            PoiSearch poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.searchPOIAsyn();//开始搜索
        }else {
            Toast.makeText(EndActivity.this,"请输入搜索关键字",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if(rCode==1000){
            if(null!=poiResult && null!=poiResult.getQuery()){
                result=poiResult;
                poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                for(int i=0;i<poiItems.size();i++){
                    LatLonPoint latPoiint=poiItems.get(i).getLatLonPoint();
                    String city=poiItems.get(i).getCityName();// 市
                    String area=poiItems.get(i).getAdName(); //区
                    String snippet=poiItems.get(i).getSnippet(); //路
                    String title=poiItems.get(i).getTitle();  //公司、地名...标题
                    Map<String, Object> row = new HashMap<String, Object>();
                    row.put("名称",title);
                    row.put("地址",snippet);
                    row.put("距离","0 米");
                    row.put("经纬度",latPoiint);
                    data.add(row);
                }
                Log.d("-----+++++-----",data.size()+"");
                adapter=new EndAdapter(EndActivity.this,data);
                ll_end.setAdapter(adapter);
                ll_end.setOnItemClickListener(this);
            }
        } else {
            Toast.makeText(EndActivity.this,"搜索不到",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(EndActivity.this,data.get(position).get("名称")+"",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        String addr=data.get(position).get("名称").toString();
        String lat = data.get(position).get("经纬度").toString();
        bundle.putString("addr",addr);
        bundle.putSerializable("lat",lat);
        intent.putExtras(bundle);
        setResult(REQUEST_CODE,intent);
        EndActivity.this.finish();
    }

    //et_end 输入提示
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText=s.toString().trim();
        if(!AMapUtil.IsEmptyOrNullString(newText)){
            InputtipsQuery inputtipsQuery=new InputtipsQuery(newText,et_end.getText().toString());
            Inputtips inputtips=new Inputtips(EndActivity.this,inputtipsQuery);
            inputtips.setInputtipsListener(this);
            inputtips.requestInputtipsAsyn();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onGetInputtips(List<Tip> list, int rCode) {
        if(rCode==1000){
            List<String> listString=new ArrayList<>();
            for(int i=0;i<list.size();i++){
                listString.add(list.get(i).getName());
            }
            ArrayAdapter<String> adapter= new ArrayAdapter<String>(
                    EndActivity.this,R.layout.route_inputs,listString);
            et_end.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            ToastUtil.show(EndActivity.this,rCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent();
            Bundle bundle=new Bundle();
            bundle.putString("addr","");
            bundle.putSerializable("lat","");
            intent.putExtras(bundle);
            setResult(REQUEST_CODE,intent);
            EndActivity.this.finish();
        }
        return false;
    }
}
