package com.example.neb.radarviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.PoiOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.neb.radarviewdemo.view.RadarView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {
    EditText editQuery;
    @BindView(R.id.bt_search)
    Button btSearch;
    private RelativeLayout activity_main;
    private MapView mapView;
    private AMap aMap;
    //private LatLng centerpoint = new LatLng(30.287459, 120.153576);// 杭州市经纬度
    private LatLng centerpoint = new LatLng(30.203588, 120.216596);// 温馨人家
    private LinearLayout.LayoutParams mParams;
    private Marker marker;
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private LatLonPoint lp;
    private ArrayList<PoiItem> pois;
    //private LatLonPoint latLonPoint = new LatLonPoint(30.287459, 120.153576);
    private LatLonPoint latLonPoint = new LatLonPoint(30.203588, 120.216596);
    private LatLonPoint latLonPoint1;
    private RadarView radarView;
    //private LatLonPoint latLonPoint = new LatLonPoint(31.238068, 121.501654);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        radarView = (RadarView) findViewById(R.id.radarView);
        editQuery = (EditText) findViewById(R.id.editQuery);
        //设置初始坐标
        AMapOptions aMapOptions = new AMapOptions();
        aMapOptions.camera(new CameraPosition(centerpoint, 10f, 0, 0));
        //mapView = (MapView) findViewById(R.id.map);
        mapView = new MapView(this, aMapOptions);
        mapView.onCreate(savedInstanceState);
        init();
        showMarker(centerpoint);
        //doSearchQuery("网咖");
    }

    /**
     * 标注当前位置
     *
     * @param centerpoint
     */
    private void showMarker(LatLng centerpoint) {
        //标注点
        if (marker != null) {
            marker.destroy();
        }
        marker = aMap.addMarker(new MarkerOptions()
                .position(centerpoint)
                .title("温馨人家")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .draggable(true));
        marker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
            mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            activity_main.addView(mapView, mParams);
        }
    }


    /**
     *      * 开始进行poi搜索
     *      
     */
    protected void doSearchQuery(String keyWord) {
        //aMap.clear();
        int currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "杭州市");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, 1000));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();// 异步搜索
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        pois = poiResult.getPois();
        aMap.clear();// 清理之前的图标
        radarView.clearPOI();//清空poi
        PoiOverlay poiOverlay = new PoiOverlay(aMap, pois);
        poiOverlay.removeFromMap();
        poiOverlay.addToMap();
        poiOverlay.zoomToSpan();
        for (PoiItem poi : pois) {
            latLonPoint1 = poi.getLatLonPoint();
            System.out.println("距离：   " + poi.getDirection() + poi.getDistance() + "==============");
            double x1 = (poi.getLatLonPoint().getLongitude());
            double x2 = (poi.getLatLonPoint().getLatitude());
            double y1 = (latLonPoint.getLongitude());
            double y2 = (latLonPoint.getLatitude());
            System.out.println("  poi点：" + x1 + "==" + x2 + "==\n 中心点：" + y1 + "==" + y2 + "");
            //System.out.println("店名 ==：" + poi.getTitle());
            RadarView.MyLatLng A = new RadarView.MyLatLng(x1, x2);
            RadarView.MyLatLng B = new RadarView.MyLatLng(y1, y2);
            System.out.println("角度：==" + RadarView.getAngle(A, B));
            radarView.addPoint(poi, latLonPoint);
            //radarView.addPoint();
            //System.out.println("poi坐标：" + poi.getLatLonPoint().getLongitude() + ":" + poi.getLatLonPoint().getLatitude() + "====");
            System.out.println("==============================================================================");

        }
        RadarView.MyLatLng A = new RadarView.MyLatLng(latLonPoint.getLongitude(), latLonPoint.getLatitude());
        //radarView.addPoint(A, A, pois.get(0));
        showMarker(centerpoint);

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        System.out.println(poiItem.getAdName() + "=====");
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        radarView.unregisterListenter();
        mapView.onDestroy();
    }

    public void searchPOI(View view) {
        String keyWord = editQuery.getText().toString();
        if (TextUtils.isEmpty(keyWord)) {
            Toast.makeText(MainActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            doSearchQuery(keyWord);
        }
    }

}