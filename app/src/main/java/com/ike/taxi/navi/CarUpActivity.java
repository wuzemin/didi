package com.ike.taxi.navi;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.ike.taxi.R;
import com.ike.taxi.utils.TTSController;

import java.util.ArrayList;
import java.util.List;

/**
 * 导航
 */
public class CarUpActivity extends Activity implements View.OnClickListener, AMapNaviListener {
    private AMapNaviView mAMapNaviView;
    private AMapNavi mAMapNavi;
    private TTSController mTtsManager;
    private boolean isGps;
    private NaviLatLng mStartLatlng = new NaviLatLng(23.126459, 113.387662);
    private NaviLatLng mEndLatlng = new NaviLatLng(23.058098, 113.385625);
    private List<NaviLatLng> mStartList=new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mEndList=new ArrayList<NaviLatLng>();
    private List<NaviLatLng> mWayPointList;
    private Button btn_north;
    private Button btn_carup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_up);
        initView();
        initNavi(savedInstanceState);

    }
    private void initView() {
        btn_north= (Button) findViewById(R.id.btn_north);
        btn_carup= (Button) findViewById(R.id.btn_carup);
        btn_north.setOnClickListener(this);
        btn_carup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_north:
                mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
                break;
            case R.id.btn_carup:
                mAMapNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);
                break;
            default:
                break;
        }
    }
    private void initNavi(Bundle savedInstanceState) {
        //实例化语音引擎
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();
        mTtsManager.startSpeaking();

        //获取AMapNavi实例
        mAMapNavi= AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);
        //获取AMapNaviView
        mAMapNaviView= (AMapNaviView) findViewById(R.id.navi);
        mAMapNaviView.onCreate(savedInstanceState);

        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(375);
        mStartList.add(mStartLatlng);
        mEndList.add(mEndLatlng);

        //算路
        mAMapNavi.calculateDriveRoute(mStartList,mEndList,mWayPointList,
                PathPlanningStrategy.DRIVING_DEFAULT);

        AMapNaviViewOptions options = mAMapNaviView.getViewOptions();
        options.setTilt(0);
        mAMapNaviView.setViewOptions(options);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteSuccess() {
        mAMapNavi.startNavi(AMapNavi.EmulatorNaviMode);
    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }
}
