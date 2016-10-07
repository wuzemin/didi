package com.ike.taxi.search;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.ike.taxi.R;
import com.ike.taxi.entity.TestEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Min on 2016/9/2.
 * 搜索周边车辆
 */
public class OthersCar {
    private static List<Marker> markers=new ArrayList<>();
    private static List<MarkerOptions> list=new ArrayList<>();
    /**
     * 添加模拟测试的车的点
     */
    public static void addEmulateData(AMap amap, LatLng center){
//        if(markers.size()==0){
            BitmapDescriptor bitmapDescriptor= BitmapDescriptorFactory
                    .fromResource(R.mipmap.taxi);
            for(int i=0;i<10;i++){
                TestEntity testEntity=new TestEntity();
                float latitude= (float) testEntity.getLatitude();
                float longitude= (float) testEntity.getLongitude();
                MarkerOptions markerOptions=new MarkerOptions();
//                markerOptions.setFlat(true);
                markerOptions.anchor(latitude,longitude);
                markerOptions.icon(bitmapDescriptor);
//                markerOptions.position(new LatLng(latitude,longitude));
                Marker marker=amap.addMarker(markerOptions);
                markers.add(marker);
            }

            /*for(int i=0;i<20;i++){
                double latitudeDelt=(Math.random()-0.5)*0.1;
                double longitudeDelt=(Math.random()-0.5)*0.1;
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.setFlat(true);
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.icon(bitmapDescriptor);
                markerOptions.position(new LatLng(center.latitude+latitudeDelt, center.longitude+longitudeDelt));
                Marker marker=amap.addMarker(markerOptions);
                markers.add(marker);
            }*/
        }
       /* else{
            for(Marker marker:markers){
                double latitudeDelt=(Math.random()-0.5)*0.1;
                double longtitudeDelt=(Math.random()-0.5)*0.1;
                marker.setPosition(new LatLng(center.latitude+latitudeDelt, center.longitude+longtitudeDelt));

            }
        }

    }*/

}
