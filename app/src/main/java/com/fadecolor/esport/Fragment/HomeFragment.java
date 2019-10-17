package com.fadecolor.esport.Fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fadecolor.esport.MainActivity;
import com.fadecolor.esport.R;
import com.fadecolor.esport.Util.GlideImageLoader;
import com.youth.banner.Banner;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements AMapLocationListener {
    private static final String TAG = "HomeFragment";

    private RadioButton mTvPosition;

    private TextView mTvSearch;

    private Banner mBanner;

    private ImageView qrCodeScanner;

    private LinearLayout linearLayout;
    private String locationText = "点击获取";

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationClientOption = null;    //定位参数类
    private String city = "点击获取";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocation();//高德定位

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mTvSearch = view.findViewById(R.id.tv_search);
        mTvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "不许点我>_<", Toast.LENGTH_SHORT).show();
            }
        });
        qrCodeScanner = view.findViewById(R.id.qr_code_scanner);
        qrCodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CaptureActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_SCAN);
            }
        });
        int statusBarHeight = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelOffset(resourceId);
        }
        linearLayout = view.findViewById(R.id.linear_layout);
        linearLayout.setPadding(linearLayout.getPaddingStart(), statusBarHeight, linearLayout.getPaddingEnd(), linearLayout.getPaddingBottom());
        mTvPosition = view.findViewById(R.id.tv_position);
        mTvPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTvPosition.setText("定位中");
                // getLocation();
                requestLocation();
            }
        });
        mBanner = view.findViewById(R.id.banner);
        // 速度
        mBanner.setDelayTime(3000);
        // 设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader());
        // 设置图片集合
        List<Uri> images = new ArrayList<>();
        images.add(getUriFromDrawableRes(R.drawable.post_image1));
        images.add(getUriFromDrawableRes(R.drawable.post_image3));
        mBanner.setImages(images);
        mBanner.start();
        mTvPosition.setText(locationText);
        return view;
    }

    public Uri getUriFromDrawableRes(int id) {
        Resources resources = getResources();
        String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(id) + "/"
                + resources.getResourceTypeName(id) + "/"
                + resources.getResourceEntryName(id);
        return Uri.parse(path);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        Log.d("aaa", "requestCode" + requestCode);
        if (requestCode == MainActivity.REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                if (content.matches("wechat")) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                    intent.setData(Uri.parse(content));
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setComponent(cmp);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //
    private void requestLocation() {
        locationClient = new AMapLocationClient(MainActivity.context);
        locationClient.setLocationListener(this);
        initLocation();
        locationClient.startLocation();
    }

    //定位参数设置
    private void initLocation() {
        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setInterval(0);
        locationClientOption.setNeedAddress(true);
        locationClientOption.setMockEnable(true);
        locationClient.setLocationOption(locationClientOption);
        //locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
    }

    @Override
    public void onLocationChanged(final AMapLocation amapLocation) {
        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
        city = amapLocation.getCity();//获取当前城市
        amapLocation.getAccuracy();//获取精度信息
        if (null != city)
            mTvPosition.setText(city);
    }

//
//    public void getLocation() {
//        mLocationClient = new LocationClient(MainActivity.context);
//        //声明LocationClient类
//        mLocationClient.registerLocationListener(myListener);
//        //注册监听函数
//        LocationClientOption option = new LocationClientOption();
//        option.setIsNeedAddress(true);
//        //可选，是否需要地址信息，默认为不需要，即参数为false
//        //如果开发者需要获得当前点的地址信息，此处必须为true
//        mLocationClient.setLocOption(option);
//        //mLocationClient为第二步初始化过的LocationClient对象
//        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
//        mLocationClient.start();
//    }
//
//    public class MyLocationListener extends BDAbstractLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location){
//            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
//            //以下只列举部分获取地址相关的结果信息
//            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
//
//            String addr = location.getAddrStr();    //获取详细地址信息
//            String country = location.getCountry();    //获取国家
//            String province = location.getProvince();    //获取省份
//            String city = location.getCity();    //获取城市
//            String district = location.getDistrict();    //获取区县
//            String street = location.getStreet();    //获取街道信息
//            if (city != null) {
//                mTvPosition.setText(city);
//                locationText = city;
//            } else {
//                mTvPosition.setText(locationText);
//            }
//        }
//    }

}
