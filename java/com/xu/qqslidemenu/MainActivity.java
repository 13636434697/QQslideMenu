package com.xu.qqslidemenu;
import java.util.Random;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    //初始化listView来填充数据
    private ListView menu_listview,main_listview;
    private SlideMenu slideMenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        //初始化listView来填充数据，带数组的adapter（this，布局文件（用的安卓自带的），泛型的数组显示字符串了）
        menu_listview.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
            //重写了getView方法
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //接收一下父类，并强转下textView
                TextView textView = (TextView) super.getView(position, convertView, parent);
                //设置字体颜色
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        //初始化listView来填充数据，带数组的adapter（this，布局文件（用的安卓自带的），泛型的数组显示字符串了）
        //自己写的话，就给apdater的convertView加就行了
        //还有些问题（可能是arrayadapter），用的话，就convertView操纵
        main_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Constant.NAMES){
            @Override
            //重写了getView方法
            public View getView(int position, View convertView, ViewGroup parent) {
                //如果convertView等于空的话，那就使用，父类，否者就使用convertView
                View view = convertView==null?super.getView(position, convertView, parent):convertView;
                //先缩小view
                ViewHelper.setScaleX(view, 0.5f);
                ViewHelper.setScaleY(view, 0.5f);
                //以属性动画放大
                //不用android的，对view就是当前动画，执行缩放x轴1原来的，执行时间350毫秒，开始动画
                ViewPropertyAnimator.animate(view).scaleX(1).setDuration(350).start();
                //不用android的，对view就是当前动画，执行缩放x轴1原来的，执行时间350毫秒，开始动画
                ViewPropertyAnimator.animate(view).scaleY(1).setDuration(350).start();
                return view;
            }
        });

        //回调接口在这里就可以set了，使用了匿名内部类的方法
        slideMenu.setOnDragStateChangeListener(new SlideMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
//				Log.e("tag", "onOpen");
                //随机选择条目，滚动到一个位置
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }
            @Override
            public void onDraging(float fraction) {
//				Log.e("tag", "onDraging fraction:"+fraction);
                //设置透明的动画，完全透明就是1-fraction
                ViewHelper.setAlpha(iv_head,1-fraction);
            }
            @Override
            public void onClose() {
//				Log.e("tag", "onClose");
                //用属性动画，平移动画15，set一个循环的插值器，循环4此，执行事件500毫秒，开始动画
                ViewPropertyAnimator.animate(iv_head).translationXBy(15).setInterpolator(new CycleInterpolator(4)).setDuration(500).start();
            }
        });

        my_layout.setSlideMenu(slideMenu);
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        main_listview = (ListView) findViewById(R.id.main_listview);
        slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}