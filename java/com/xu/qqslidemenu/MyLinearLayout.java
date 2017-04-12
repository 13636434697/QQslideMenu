package com.xu.qqslidemenu;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import  com.xu.qqslidemenu.SlideMenu.DragState;
/**
 * 当slideMenu打开的时候，拦截并消费掉触摸事件
 * 因为，在缩小的时候是不能被点击的
 * @author Administrator
 * 
 */
public class MyLinearLayout extends LinearLayout {
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}

	//拿到slideMenu应用，传进来
	private SlideMenu slideMenu;
	public void setSlideMenu(SlideMenu slideMenu){
		this.slideMenu = slideMenu;
	}

	//应该在这里拦截，如果slideMenu打开的时候所有事件都应该拦截，拿到slideMenu应用，传进来
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//判断slideMenu不等于空，状态是开打的
		if(slideMenu!=null && slideMenu.getCurrentState()==DragState.Open){
			//如果slideMenu打开则应该拦截并消费掉事件
			//这里返回true，事件就传给onTouchEvent处理
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(slideMenu!=null && slideMenu.getCurrentState()==DragState.Open){
			if(event.getAction()==MotionEvent.ACTION_UP){
				//抬起则应该关闭slideMenu
				slideMenu.close();
			}
			
			//如果slideMenu打开则应该拦截并消费掉事件
			return true;
		}
		return super.onTouchEvent(event);
	}
}
