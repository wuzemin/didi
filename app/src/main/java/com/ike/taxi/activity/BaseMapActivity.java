package com.ike.taxi.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.NaviType;
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
import com.ike.taxi.utils.TTSController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/8/30.
 * 导航基类
 */
public class BaseMapActivity extends Activity implements AMapNaviListener, AMapNaviViewListener {

    protected AMapNaviView mAMapNaviView;
    protected AMapNavi mAMapNavi;
    protected TTSController mTtsManager;
    protected NaviLatLng mEndLatlng = new NaviLatLng(39.925846, 116.432765);
    protected NaviLatLng mStartLatlng = new NaviLatLng(39.925041, 116.437901);
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    protected List<NaviLatLng> mWayPointList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //实例化语音引擎
        mTtsManager = TTSController.getInstance(getApplicationContext());
        mTtsManager.init();
        mTtsManager.startSpeaking();

        //
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.addAMapNaviListener(mTtsManager);

        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(75);
        sList.add(mStartLatlng);
        eList.add(mEndLatlng);

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

//        仅仅是停止你当前在说的这句话，一会到新的路口还是会再说的
        mTtsManager.stopSpeaking();
//
//        停止导航之后，会触及底层stop，然后就不会再有回调了，但是讯飞当前还是没有说完的半句话还是会说完
//        mAMapNavi.stopNavi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
        mTtsManager.destroy();
    }

    //导航创建失败时的回调函数。
    @Override
    public void onInitNaviFailure() {
        Toast.makeText(this, "init navi Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitNaviSuccess() {
        /**
         * 方法:
         *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
         * 参数:
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         * 说明:
         *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         * 注意:
         *      不走高速与高速优先不能同时为true
         *      高速优先与避免收费不能同时为true
         */
        int strategy=0;
        try {
            strategy=mAMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, mWayPointList,strategy);
    }

    @Override
    public void onStartNavi(int type) {
        Log.e("--------","onStartNavi");
    }

    @Override
    public void onTrafficStatusUpdate() {
        Log.e("--------","onTrafficStatusUpdate");
    }

    @Override
    public void onLocationChange(AMapNaviLocation location) {
        Log.e("--------","onLocationChange");

    }

    @Override
    public void onGetNavigationText(int type, String text) {
        Log.e("--------","onGetNavigationText");

    }

    @Override
    public void onEndEmulatorNavi() {
        Log.e("--------","onEndEmulatorNavi");
    }

    @Override
    public void onArriveDestination() {
        Log.e("--------","onArriveDestination");
    }

    @Override
    public void onCalculateRouteSuccess() {
        mAMapNavi.startNavi(NaviType.EMULATOR);
    }

    @Override
    public void onCalculateRouteFailure(int errorInfo) {
        Log.e("--------","onCalculateRouteFailure");
    }

    @Override
    public void onReCalculateRouteForYaw() {
        Log.e("--------","onReCalculateRouteForYaw");

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        Log.e("--------","onReCalculateRouteForTrafficJam");

    }

    @Override
    public void onArrivedWayPoint(int wayID) {
        Log.e("--------","onArrivedWayPoint");

    }

    @Override
    public void onGpsOpenStatus(boolean enabled) {
        Log.e("--------","onGpsOpenStatus");

    }

    @Override
    public void onNaviSetting() {
        Log.e("--------","onNaviSetting");

    }

    @Override
    public void onNaviMapMode(int isLock) {
        Log.e("--------","onNaviMapMode");

    }

    @Override
    public void onNaviCancel() {
        finish();
        Log.e("--------","onNaviCancel");

    }


    @Override
    public void onNaviTurnClick() {
        Log.e("--------","onNaviTurnClick");

    }

    @Override
    public void onNextRoadClick() {
        Log.e("--------","onNextRoadClick");

    }


    @Override
    public void onScanViewButtonClick() {
        Log.e("--------","onScanViewButtonClick");

    }

    @Deprecated
    @Override
    public void onNaviInfoUpdated(AMapNaviInfo naviInfo) {
        Log.e("--------","onNaviInfoUpdated");

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviinfo) {
        Log.e("--------","onNaviInfoUpdate");

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {
        Log.e("--------","OnUpdateTrafficFacility");
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        Log.e("--------","OnUpdateTrafficFacility");

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        Log.e("--------","showCross");

    }

    @Override
    public void hideCross() {
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] laneInfos, byte[] laneBackgroundInfo, byte[] laneRecommendedInfo) {
        Log.e("--------","showLaneInfo");

    }

    @Override
    public void hideLaneInfo() {
        Log.e("--------","hideLaneInfo");

    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        Log.e("--------","onCalculateMultipleRoutesSuccess");

    }

    @Override
    public void notifyParallelRoad(int i) {
        Log.e("--------","notifyParallelRoad");

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        Log.e("--------","OnUpdateTrafficFacility");

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        Log.e("--------","updateAimlessModeStatistics");

    }


    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        Log.e("--------","updateAimlessModeCongestionInfo");

    }


    @Override
    public void onLockMap(boolean isLock) {
        Log.e("--------","onLockMap");
    }

    @Override
    public void onNaviViewLoaded() {
        Log.d("wlx", "导航页面加载成功");
        Log.d("wlx", "请不要使用AMapNaviView.getMap().setOnMapLoadedListener();会overwrite导航SDK内部画线逻辑");
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }


}
