package com.ike.taxi.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.activity.EndActivity;
import com.ike.taxi.chat.activity.TestLoginActivity;
import com.ike.taxi.entity.LocationEntity;
import com.ike.taxi.entity.Nearby;
import com.ike.taxi.listener.OnLocationGetListener;
import com.ike.taxi.location.LocationTask;
import com.ike.taxi.location.RegeocodeTask;
import com.ike.taxi.navi.CarUpActivity;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.route.DriveRoute;
import com.ike.taxi.route.DriveRouteDetailActivity;
import com.ike.taxi.route.DriveTime;
import com.ike.taxi.utils.AMapUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

import static com.loopj.android.http.AsyncHttpClient.log;

public class FirstFragment extends Fragment implements View.OnClickListener,
         View.OnTouchListener,OnLocationGetListener
        ,AMap.OnInfoWindowClickListener, AMap.OnMapLoadedListener, DriveTime.OnRouteCalculateListener, AMap.OnPOIClickListener {
    private View view;
    private MapView mapView;
    private AMap aMap;
    private EditText et_start;
    private EditText et_end;
    private EditText et_number;
    private Button btn_route; //线路
    private Button btn_call_car;  //一键叫车
    private Button btn_chat;

    private Button btn_share_position;
    private final int REQUEST_CODE=1;

    private Activity activity;

    private LatLng ll;
    private boolean flag=true;

    //路径规划
    private RelativeLayout mBottomLayout;
    private TextView mRouteTimeDes;
    private TextView mRouteDetailDes;
    private LatLonPoint mStartPoint;  //起点
    private LatLonPoint mEndPoint;    //终点
    private ProgressDialog progDialog = null;// 搜索时进度条
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private List<LatLng> list=new ArrayList<>();
    private int time; //行车路线所用的时间
    private List<Integer> timelist=new ArrayList<>();

    //地图定位
    private LocationTask mLocationTask;
    private RegeocodeTask mRegeocodeTask;

    private double ddd[]={23.126823,113.388011,23.127823,113.388011,23.128823,113.388011,23.129823,113.388011,
            23.130823,113.388011,23.131823,113.388011,23.132823,113.388011,23.133823,113.388011,
            23.134823,113.388011,23.235823,113.388011};

    //导航
    private Button btn_navi;

    private List<Nearby> nearby;
    private List<Nearby> orbit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("11111111111---onCreate","FirstFragm");
        view = inflater.inflate(R.layout.fragment_first, container, false);
        activity=getActivity();
        initMap(savedInstanceState);
        initView();
        NearbyHttpUtils();
        OrbitHttpUtils();

        return view;
    }

    //地图
    private void initMap(Bundle savedInstanceState) {
        //获取地图控件
        mapView= (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); //必须
        aMap=mapView.getMap();

        //定位
        mLocationTask=LocationTask.getInstance(getActivity());
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask=new RegeocodeTask(getActivity());
        mRegeocodeTask.setOnLocationGetListener(this);

        //显示定位按钮
        aMap.setLocationSource(mLocationTask);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
//        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        //指南针
        aMap.getUiSettings().setCompassEnabled(true);
        //比例尺
        aMap.getUiSettings().setScaleControlsEnabled(true);
        //旋转手势
        aMap.getUiSettings().setRotateGesturesEnabled(true);

        //搜索其他车辆,当可视范围改变时回调的接口
//        aMap.setOnCameraChangeListener(this);

        aMap.setOnInfoWindowClickListener(this);

        //底图poi点击事件
        aMap.setOnPOIClickListener(this);

        //附近派单功能
//        initNearby();//
    }

    private void initView() {
        et_start= (EditText) view.findViewById(R.id.et_start);
        et_end= (EditText) view.findViewById(R.id.et_end);
        et_number= (EditText) view.findViewById(R.id.et_number);
        btn_route= (Button) view.findViewById(R.id.btn_route);
        btn_navi= (Button) view.findViewById(R.id.btn_navi);
        btn_share_position= (Button) view.findViewById(R.id.btn_share_position);
        btn_call_car= (Button) view.findViewById(R.id.btn_call_car);
        btn_chat= (Button) view.findViewById(R.id.btn_chat);

        et_end.setOnTouchListener(this);
        btn_route.setOnClickListener(this);
        btn_navi.setOnClickListener(this);
        btn_share_position.setOnClickListener(this);
        btn_call_car.setOnClickListener(this);
        btn_chat.setOnClickListener(this);


        mBottomLayout= (RelativeLayout) view.findViewById(R.id.bottom_layout);
        mRouteTimeDes= (TextView) view.findViewById(R.id.firstline);
        mRouteDetailDes= (TextView) view.findViewById(R.id.secondline);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_route:  //线路
                Log.v("---------------=-=","btn_call！");
                DriveRoute.getInstance(getActivity(),aMap).search(mStartPoint,mEndPoint);
                //驾车路径
                DriveRoute.getInstance(getActivity(),aMap).addRouteCalculateListener(onRouteCalculate);
                break;
            case R.id.btn_navi: //导航
                Log.v("---------------=-=","暂时还没哟！");
                //导航
                Intent intent=new Intent(getActivity(), CarUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_share_position:  //共享位置
                initShare();
                Log.e("--------====",mStartPoint+"");
                break;
            case R.id.btn_call_car:  //一键叫车
                Log.e("--------===",btn_call_car+"");
                call_car();
                break;
            case R.id.btn_chat:
                Intent intent1=new Intent(getActivity(), TestLoginActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }

    //一键叫车
    private void call_car() {
        String startpoint=et_start.getText().toString().trim();
        String endpoint=et_end.getText().toString().trim();
        String num=et_number.getText().toString().trim();
        if(!"".equals(num)) {
            for (int i = 0; i < nearby.size(); i++) {
                mEndPoint = new LatLonPoint(nearby.get(i).getLatitude(), nearby.get(i).getLongitude());
                DriveTime.getInstance(getActivity(), aMap).search(mStartPoint, mEndPoint);
                DriveTime.getInstance(getActivity(), aMap).addRouteCalculateListener(this);
                log.e("-------=-=-=time", time + " " + timelist.size());
            }
            int j = Integer.parseInt(num);
            for (int i = 0; i < j; i++) {

            }
        }else {
            Toast.makeText(getActivity(), "不能为空", Toast.LENGTH_SHORT).show();
        }
    }
    //叫车时间
    @Override
    public void onRouteTime(float cost, int distance, int duration) {
        log.e("-------=-=-=onRouteTime",duration+"");
        String des= AMapUtil.getFriendlyTime(duration);
        time=duration;
        timelist.add(duration);
        Log.e("-------=-=-=onRouteTime",des+" "+timelist.size());
        initNum();
    }

    private void initNum() {
        int num= Integer.parseInt(et_number.getText().toString().trim());
        int sss=0;
        int ssss=0;
        for(int i=0;i<num;i++){
            Collections.sort(timelist);
            sss=timelist.get(i);
            ssss=timelist.get(i);
        }
        log.e("_+-=-=-=-=-=",sss+","+ssss);
//        Log.e("-----------==-=-=",timelist+"");
        timelist.clear();
    }

    //共享位置
    private void initShare(){
        String url="/share";
        HttpUtils.sendPostStringRequest(url, mStartPoint, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(getActivity(), "共享失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Toast.makeText(getActivity(),"共享成功",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //点击edittext事件选取终点位置
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(getActivity(), EndActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        return true;
    }
    
    //回调事件--获取终点事件
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            Bundle bundle=data.getExtras();
            String address=bundle.getString("addr");
            String lat=bundle.getString("lat");
            String[] strPoint;
            if(!"".equals(lat)){
                strPoint=lat.split(",");
                endLatitude=Double.parseDouble(strPoint[0]);
                endLongitude=Double.parseDouble(strPoint[1]);
                et_end.setText(address);
                mEndPoint=new LatLonPoint(endLatitude,endLongitude);
//                Log.v("-------=====",address+"------"+lat+"----"+mEndPoint);
            }else {
                et_end.setText("");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //定位
    @Override
    public void onLocationGet(LocationEntity entity) {
        et_start.setText(entity.address);
//        Log.e("-=-=-=-=-=onLocationGet",entity.address+" "+entity.latitue+","+entity.longitude);
        startLatitude=entity.latitue;
        startLongitude=entity.longitude;
        mStartPoint=new LatLonPoint(startLatitude,startLongitude);

    }

    //地理逆编码
    @Override
    public void onRegecodeGet(LocationEntity entity) {
//        Log.e("-=-=-=-=-=onRegecodeGet",entity.address+" "+entity.latitue+","+entity.longitude);
    }

   /* @Override
    public void onRouteTime(int duration) {
        Log.e("---------=-=-=-=",duration+"");
    }*/

    private DriveRoute.OnRouteCalculateListener onRouteCalculate=new DriveRoute.OnRouteCalculateListener(){
        @Override
        public void onRouteCalculate(float cost, int distance, int duration,
                                     final DriveRouteResult mDriveRouteResult, final DrivePath drivePath) {
            mBottomLayout.setVisibility(View.VISIBLE);
            String des= AMapUtil.getFriendlyTime(duration)+"("+AMapUtil.getFriendlyLength(distance)+")";
//            time=duration;
            mRouteTimeDes.setText(des);
            mRouteDetailDes.setVisibility(View.VISIBLE);
            mRouteDetailDes.setText("打车约"+cost+"元");
            mBottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(), DriveRouteDetailActivity.class);
                    intent.putExtra("drive_path",drivePath);
                    intent.putExtra("drive_result",mDriveRouteResult);
                    startActivity(intent);
                }
            });
        }
    };
    //路径规划
    /*@Override
    public void onRouteCalculate(float cost, int distance, int duration,
                final DriveRouteResult mDriveRouteResult, final DrivePath drivePath) {
        mBottomLayout.setVisibility(View.VISIBLE);
        String des= AMapUtil.getFriendlyTime(duration)+"("+AMapUtil.getFriendlyLength(distance)+")";
        mRouteTimeDes.setText(des);
        mRouteDetailDes.setVisibility(View.VISIBLE);
        mRouteDetailDes.setText("打车约"+cost+"元");
        mBottomLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), DriveRouteDetailActivity.class);
                intent.putExtra("drive_path",drivePath);
                intent.putExtra("drive_result",mDriveRouteResult);
                startActivity(intent);
            }
        });
    }*/

    //
    private void NearbyHttpUtils(){
        String url="/nearby";
        HttpUtils.getRequest(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Message message=new Message();
                message.what=2;
                handler.sendMessage(message);
//                Toast.makeText(getActivity(), "数据获取失败--nearby，请检查网络是否完好或服务器是否开启",
//                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type= new TypeToken<List<Nearby>>(){}.getType();
                nearby=gson.fromJson(response,type);
                initMarker();
            }
        });
    }
    //点击marker
    private void initMarker(){
        String name[]={"赵一","钱二","孙三","李四","周五","吴六","郑七","王八"};
        for(int i=0;i<nearby.size();i++) {
            double latitude=nearby.get(i).getLatitude();
            double longitude=nearby.get(i).getLongitude();
            ll=new LatLng(latitude,longitude);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(ll).title(name[i])
                    .snippet("棠东：" + latitude + "," + longitude);
            markerOptions.draggable(true);
            BitmapDescriptor bitmapDescriptor=BitmapDescriptorFactory.fromBitmap(BitmapFactory
                    .decodeResource(getResources(), R.mipmap.taxi));
            markerOptions.icon(bitmapDescriptor);
            markerOptions.setFlat(true);
            aMap.addMarker(markerOptions);
            aMap.setOnMarkerClickListener(markerListener);
        }
    }

    AMap.OnMarkerClickListener markerListener=new AMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    };

    //点击infowindow
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "你点击了"+marker.getTitle(), Toast.LENGTH_SHORT).show();
    }




    private void OrbitHttpUtils(){
        String url="/orbit";
        HttpUtils.getRequest(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
                /*Toast.makeText(getActivity(), "数据获取失败--orbit，请检查网络是否完好或服务器是否开启",
                        Toast.LENGTH_SHORT).show();*/
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<List<Nearby>>(){}.getType();
                orbit=gson.fromJson(response,type);
                initOrbit();
            }
        });
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.e("==========","数据获取失败--orbit，请检查网络是否完好或服务器是否开启");
//                    ToastUtil.show(activity,"数据获取失败--orbit，请检查网络是否完好或服务器是否开启");
                    break;
                case 2:
                    Log.e("==========","数据获取失败--nearby，请检查网络是否完好或服务器是否开启");
//                    ToastUtil.show(activity,"数据获取失败--nearby，请检查网络是否完好或服务器是否开启");
                default:
                    break;
            }
        }
    };


    //显示轨迹
    private void initOrbit() {
        for(int i=0;i<orbit.size();i++){
            double latitude=orbit.get(i).getLatitude();
            double longitude=orbit.get(i).getLongitude();
            LatLng latlng=new LatLng(latitude,longitude);
            list.add(latlng);
            Log.d("-----------",list+"");
        }
        mapView.getMap().setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                mapView.getMap().addPolygon(new PolygonOptions().addAll(list));
            }
        });
        aMap.setOnMapLoadedListener(this);

    }
    @Override
    public void onMapLoaded() {
        aMap.addPolygon(new PolygonOptions().addAll(list));
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(getActivity());
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /*@Override
    public void onPause() {
        super.onPause();
//        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
//        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.onDestroy();
        *//*if (null != mLocationClient) {
            *//**//**
         * 如果AMapLocationClient是在当前Activity实例化的，
         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
         *//**//*
            mLocationClient.onDestroy();
        }*//*
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPOIClick(Poi poi) {
        aMap.clear();
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
        TextView textView = new TextView(getActivity());
        textView.setText(poi.getName());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.custom_info_bubble);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
        aMap.addMarker(markOptiopns);
    }

    //            //显示轨迹
//        mapView.getMap().setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
//            @Override
//            public void onMapLoaded() {
//                mapView.getMap().addPolygon(new PolygonOptions().addAll(list));
//            }
//        });
//
//        for(int i=0;i<ddd.length;i++){
//            LatLng latlng=new LatLng(ddd[i],ddd[++i]);
//            list.add(latlng);
//            Log.d("-----------",list+"");
//        }

    //附近派单
    /*private void initNearby() {
        //设置搜索条件
        NearbySearch.NearbyQuery query=new NearbySearch.NearbyQuery();
        //设置搜索的中心点
        query.setCenterPoint(new LatLonPoint(23.126823,113.388011));
        //设置搜索的坐标体系
        query.setCoordType(NearbySearch.AMAP);
        //设置搜索半径
        query.setRadius(10000);
        //设置查询时间
        query.setTimeRange(1000);
        //设置查询的方式：驾车还是距离
        query.setType(NearbySearchFunctionType.DRIVING_DISTANCE_SEARCH);
        //调用一部查询接口
        NearbySearch.getInstance(getActivity()).searchNearbyInfoAsyn(query);
        NearbySearch.getInstance(getActivity()).addNearbyListener(listener);
        NearbySearch.getInstance(getActivity()).startUploadNearbyInfoAuto(uploadInfoCallback,10000);
    }
    UploadInfoCallback uploadInfoCallback=new UploadInfoCallback() {
        @Override
        public UploadInfo OnUploadInfoCallback() {
            return null;
        }
    };

    NearbySearch.NearbyListener listener=new NearbySearch.NearbyListener() {
        //查询周边的人的回调接口。
        @Override
        public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int resultCode) {
            *//**
             * resultCode - 清除结果,1000代表成功
             * nearbySearchResult - 附近查询的结果
             *//*

            if(resultCode==1000){
                if(nearbySearchResult!=null && nearbySearchResult.getNearbyInfoList()!=null
                        && nearbySearchResult.getNearbyInfoList().size()>0){
                    Log.e("resultCode",resultCode+""+nearbySearchResult+"\n"+
                            nearbySearchResult.getNearbyInfoList()+"\n"+
                            nearbySearchResult.getNearbyInfoList().size());
                    NearbyInfo nearbyInfo=nearbySearchResult.getNearbyInfoList().get(0);
                    int size=nearbySearchResult.getNearbyInfoList().size();
                    String id=nearbyInfo.getUserID(); //用户ID
                    int distance=nearbyInfo.getDistance(); //直线距离
                    int drivingDis=nearbyInfo.getDrivingDistance(); //行驶里程
                    long time=nearbyInfo.getTimeStamp(); //获取时间戳数据。
                    String Point=nearbyInfo.getPoint().toString(); //用户位置
                    String string=size+id+distance+drivingDis+time+Point;
                    Toast.makeText(getActivity(), string,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(), "周边搜索为空",Toast.LENGTH_LONG).show();
                    Log.e("resultCode",resultCode+"\n"+
                            nearbySearchResult+"\n"+
                            nearbySearchResult.getNearbyInfoList()+"\n"+
                            nearbySearchResult.getNearbyInfoList().size());
                }
            }else {
                Toast.makeText(getActivity(), "错误码为",Toast.LENGTH_LONG).show();
            }
        }

        //上传用户信息的回调监听。
        @Override
        public void onNearbyInfoUploaded(int resultCode) {
            if(resultCode==1000){
                //构造上传位置信息
                UploadInfo loadInfo = new UploadInfo();
                //设置上传位置的坐标系支持AMap坐标数据与GPS数据
                loadInfo.setCoordType(NearbySearch.AMAP);
                //设置上传数据位置,位置的获取推荐使用高德定位sdk进行获取
                loadInfo.setPoint(mStartPoint);
                //设置上传用户id
                loadInfo.setUserID("taxi");
                //调用异步上传接口
                NearbySearch.getInstance(getActivity())
                        .uploadNearbyInfoAsyn(loadInfo);
            }else {
                Toast.makeText(getActivity(),"上传失败",Toast.LENGTH_SHORT).show();
            }
        }

        //用户信息清除回调接口。
        @Override
        public void onUserInfoCleared(int resultCode) {
            //resultCode
            if(resultCode==1000){
                //获取附近实例，并设置要清楚用户的id
                NearbySearch.getInstance(getActivity()).setUserID("driver");
                //调用异步清除用户接口
                NearbySearch.getInstance(getActivity())
                        .clearUserInfoAsyn();
            }
        }
    };*/

}
