package com.ike.taxi.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.ike.taxi.R;
import com.ike.taxi.utils.T;

import java.util.List;

/**
 * 搜索
 */
public class OthersLocationActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, AMap.OnMarkerClickListener {
    private MapView mapView;
    private AMap aMap;
    private Button btn_search;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;// POI搜索
    private PoiResult poiResult; // poi返回的结果
    private double ll[]={23.127823,113.388011};
    /* private String[] string={"棠东", "棠东(公交站)", "棠东村", "棠东(地铁站)", "棠东(公交站)", "棠东(西南2门)",
            "棠东(西南1门)", "棠东小学", "棠东车行", "棠东商业大厦"};*/
    private LatLonPoint mStartPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_location);
        initMap(savedInstanceState);
        initView();

    }

    private void initMap(Bundle savedInstanceState) {
        mapView= (MapView) findViewById(R.id.map);
        aMap=mapView.getMap();
        mapView.onCreate(savedInstanceState);
        aMap.setOnMarkerClickListener(this);
    }

    private void initView() {
        btn_search= (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPoisearch();
            }
        });
    }

    private void doPoisearch() {
        mStartPoint= new LatLonPoint(ll[0],ll[1]);
        query=new PoiSearch.Query("棠东","","广州");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem

        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(mStartPoint,1000,true));
        poiSearch.searchPOIAsyn();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return false;
    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    Log.e("-----==-=-=-=",poiResult.getPois()+"");
//                    List<PoiItem> poiItems;
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
//                    List<PoiItem> poiItems=new ArrayList<>();

                    if (poiItems != null && poiItems.size() > 0) {
                        aMap.clear();// 清理之前的图标
                        PoiOverlay poiOverlay = new PoiOverlay(aMap, poiItems);
                        poiOverlay.removeFromMap();
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        T.showShort(OthersLocationActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                T.showShort(OthersLocationActivity.this,
                        R.string.no_result);
            }
        } else {
            T.showerror(this, rCode);
        }

    }

   /**
    * poi没有搜索到数据，返回一些推荐城市的信息
    */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        T.showShort(OthersLocationActivity.this, infomation);

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}
