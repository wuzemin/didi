package com.ike.taxi.route;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.ike.taxi.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/8/31.
 * 封装驾车路径规划
 */
public class DriveRoute  implements RouteSearch.OnRouteSearchListener{
    private Context context;
    private static DriveRoute mRouteTask;
    private RouteSearch mRouteSearch;

    private LatLonPoint mFromPoint;
    private LatLonPoint mToPoint;

    private DriveRouteResult mDriveRouteResult;
    private WalkRouteResult mWalkPouteResult;
    private BusRouteResult mBusRouteResult;

    private DrivePath drivePath;
    private WalkPath walkPath;
    private BusPath busPath;

    private AMap aMap;

    private List<OnRouteCalculateListener> mListeners = new ArrayList<DriveRoute.OnRouteCalculateListener>();

    public interface OnRouteCalculateListener {
        public void onRouteCalculate(float cost, int distance, int duration ,
                      DriveRouteResult mDriveRouteResult,DrivePath drivePath);

    }

    public static DriveRoute getInstance(Context context,AMap aMap) {
        if (mRouteTask == null) {
            mRouteTask = new DriveRoute(context,aMap);
        }
        return mRouteTask;
    }

    public DriveRoute(Context context,AMap aMap) {
        this.aMap=aMap;
        this.context= context;
        mRouteSearch = new RouteSearch(context);
        mRouteSearch.setRouteSearchListener(this);
    }

    public void search(LatLonPoint fromPoint,LatLonPoint toPoint) {
        mFromPoint=fromPoint;
        mToPoint=toPoint;
        if(mFromPoint==null){
            ToastUtil.show(context,"定位中，稍后再试...");
            return;
        }
        if(mToPoint == null) {
            ToastUtil.show(context, "终点未设置");
            return;
        }
//        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo=new RouteSearch.FromAndTo(mFromPoint,mToPoint);
        /**
        * 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，
        * 第四个参数表示避让区域，第五个参数表示避让道路
        */
                RouteSearch.DriveRouteQuery query=new RouteSearch.DriveRouteQuery(
                fromAndTo, RouteSearch.DrivingDefault,null,null,"");
        mRouteSearch.calculateDriveRouteAsyn(query);
    }

    public void addRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            if (mListeners.contains(listener))
                return;
            mListeners.add(listener);
        }
    }

    public void removeRouteCalculateListener(OnRouteCalculateListener listener) {
        synchronized (this) {
            mListeners.add(listener);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult,
                                     int errorCode) {
        if(errorCode==1000){
            if(null!=driveRouteResult && null!=driveRouteResult.getPaths()){
                if(driveRouteResult.getPaths().size()>0){
                    for (OnRouteCalculateListener listener : mListeners) {
                        mDriveRouteResult = driveRouteResult;
                        drivePath = mDriveRouteResult.getPaths().get(0);
                        DriveRouteColorfulOverLay driveRouteColorfulOverLay=new
                            DriveRouteColorfulOverLay(aMap,drivePath,
                            mDriveRouteResult.getStartPos(),mDriveRouteResult.getTargetPos(),null);
                        driveRouteColorfulOverLay.setNodeIconVisibility(false);//设置节点marker是否显示
                        driveRouteColorfulOverLay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                        driveRouteColorfulOverLay.removeFromMap();
                        driveRouteColorfulOverLay.addToMap();
                        driveRouteColorfulOverLay.zoomToSpan();
                        int dis = (int) drivePath.getDistance(); //距离
                        int dur = (int) drivePath.getDuration(); //时间
                        int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                        listener.onRouteCalculate(taxiCost, dis, dur, mDriveRouteResult, drivePath);
                    }
                }else if(null!=driveRouteResult && driveRouteResult.getPaths()==null){
                    ToastUtil.show(context, "对不起，没有搜索到相关数据！");
                }
            }else {
                ToastUtil.show(context, "对不起，没有搜索到相关数据！");
            }
        }else {
            ToastUtil.showerror(context, errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {
        if(errorCode==1000){
            if(null!=walkRouteResult && null!=walkRouteResult.getPaths()){
                if(walkRouteResult.getPaths().size()>0){
                    for(OnRouteCalculateListener listener : mListeners){
                        mWalkPouteResult=walkRouteResult;
                        walkPath=mWalkPouteResult.getPaths().get(0);
                        WalkRouteOverlay walkRouteOverlay=new WalkRouteOverlay(context,aMap,walkPath,
                                mWalkPouteResult.getStartPos(),mWalkPouteResult.getTargetPos());
                        walkRouteOverlay.setNodeIconVisibility(false);
                        walkRouteOverlay.removeFromMap();
                        walkRouteOverlay.addToMap();
                        walkRouteOverlay.zoomToSpan();
                        int dis= (int) walkPath.getDistance();
                        int dur= (int) walkPath.getDuration();
                    }
                }
            }
        }

    }
}
