package com.xu.qqslidemenu.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.nineoldandroids.view.ViewHelper;
import com.xu.qqslidemenu.ColorUtil;

/*
 一.View移动的相关方法总结:
  1.通过改变view在父View的layout位置来移动,但是只能移动指定的View:
    view.layout(l,t,r,b);
	view.offsetLeftAndRight(offset);//同时改变left和right
	view.offsetTopAndBottom(offset);//同时改变top和bottom
  2.通过改变scrollX和scrollY来移动,但是可以移动所有的子View；
    scrollTo(x,y);
	scrollBy(xOffset,yOffset);
  3.通过改变Canvas绘制的位置来移动View的内容:
    canvas.drawBitmap(bitmap, left, top, paint)

 二.使用ViewDragHelper来处理移动

  1.ViewDragHelper在高版本的v4包(android4.4以上的v4)中
  2.它主要用于处理ViewGroup中对子View的拖拽处理
  3.它是Google在2013年开发者大会提出的
  4.它主要封装了对View的触摸位置，触摸速度，移动距离等的检测和Scroller,通过接口回调的方式告诉我们;只需要我们指定是否需要移动，移动多少等;
  5.本质是对触摸事件的解析类;

三.getHeight和getMeasuredHeight的区别:
  getMeasuredHeight:只要view执行完onMeasure方法就能够获取到值；
  getHeight：只有view执行完layout才能获取到值;

四.在自定义ViewGroup的时候，如果对子View的测量没有特殊的需求，那么可以继承系统已有的
   布局(比如FrameLayout)，目的是为了让已有的布局帮我们实行onMeasure;
* */
public class DragLayout extends FrameLayout {
	private View redView;// 红孩子
	private View blueView;// 蓝精灵

	private ViewDragHelper viewDragHelper;

	// 生成父类的构造方法:alt+shift+s->c
	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context) {
		super(context);
		init();
	}

	private void init() {
		//要监视自己的子view就是this
		//callback是内部的接口
		//创建对象后，还不能解析，需要生效的话，需要传递触摸事件，实现onTouchEvent，来实现移动，因为本身是一个解析类
		viewDragHelper = ViewDragHelper.create(this, callback);
	}

	/**
	 * 当DragLayout的xml布局的结束标签被读取完成会执行该方法，此时会知道自己有几个子View了 一般用来初始化子View的引用
	 * 解析xml完成之后的方法，就知道自己有几个子view
	 */
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		redView = getChildAt(0);
		blueView = getChildAt(1);
	}

	//这个方法大部分是不需要实现的，如果需要实现因为要设置当前布局的宽和高，要摆放控件
	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	//默认父类的实现，里面是设置当前的宽高，这里可删可以不删，因为xml已经指定了宽高
	// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// //要测量我自己的子View
	//获取定义的大小
	// // int size = getResources().getDimension(R.dimen.width);//100dp
	//精确的模式
	// int measureSpec = MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width,MeasureSpec.EXACTLY);
	//要调用测量方法，需要有view引用，getChildat来获取子view（不推荐，在onFinishInflate上面的方法里）
	//不能填写0，0，一个父view已经测量过子view的话，可以填0，0。这里没有测量过
	// // redView.measure(measureSpec,measureSpec);
	// // blueView.measure(measureSpec, measureSpec);
	//
	// //如果说没有特殊的对子View的测量需求，可以用如下方法（子view宽度就是父view宽度）
	// measureChild(redView, widthMeasureSpec, heightMeasureSpec);
	// measureChild(blueView, widthMeasureSpec, heightMeasureSpec);
	// }

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		//layout宽的一办减去控件的一半，就是左上角起始点的位置
//		int left = getPaddingLeft()+getMeasuredWidth()/2 - redView.getMeasuredWidth()/2;
		int left = getPaddingLeft();
		int top = getPaddingTop();
		//摆放一下位置，左上角，测量的宽度，测量的高度
		redView.layout(left, top, left + redView.getMeasuredWidth(), top + redView.getMeasuredHeight());
		blueView.layout(left, redView.getBottom(),left + blueView.getMeasuredWidth(), redView.getBottom() + blueView.getMeasuredHeight());
	}

	//创建对象后，还不能解析，需要生效的话，需要传递触摸事件，实现onTouchEvent，来实现移动，因为本身是一个解析类
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 让ViewDragHelper帮我们判断是否应该拦截
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}

	//创建对象后，还不能解析，需要生效的话，需要传递触摸事件，实现onTouchEvent，来实现移动，因为本身是一个解析类
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 将触摸事件交给ViewDragHelper来解析处理
		viewDragHelper.processTouchEvent(event);
		//因为自己要来处理，要消费掉
		return true;
	}


	//callback是内部的接口，通过接口回调方法告诉移动速度位置距离
	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		/**
		 * 用于判断是否捕获当前child的触摸事件 child: 当前触摸的子View return: true:就捕获并解析 false：不处理
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child == blueView || child == redView;
		}

		/**
		 * 当view被开始捕获和解析的回调 capturedChild:当前被捕获的子view
		 * 当上面的控件被触摸时候就回触发这个方法
		 */
		@Override
		public void onViewCaptured(View capturedChild, int activePointerId) {
			super.onViewCaptured(capturedChild, activePointerId);
			// Log.e("tag", "onViewCaptured");
		}

		/**
		 * 获取view水平方向的拖拽范围,但是目前不能限制边界,
		 * 返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面;（抬起手指的之后自动移动到固定位置） 最好不要返回0
		 * 必须和clampViewPositionHorizontal的方法结合
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			//当前layout宽减去控件的宽
			return getMeasuredWidth() - child.getMeasuredWidth();
		}

		/**
		 * 获取view垂直方向的拖拽范围，最好不要返回0，和上面水平方向一样的操作
		 */
		public int getViewVerticalDragRange(View child) {
			return getMeasuredHeight() - child.getMeasuredHeight();
		};

		/**
		 * 控制child在水平方向的移动
		 * left:表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft(之前的坐标)+dx（本次移动的距离）
		 * dx:本次child水平方向移动的距离
		 * return: 表示你真正想让child的left变成的值，如果想保持之前的值的话，就去减去dx
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if (left < 0) {
				// 限制左边界
				left = 0;
			} else if (left > (getMeasuredWidth() - child.getMeasuredWidth())) {
				// 限制右边界
				//控件的宽减去子控件的宽
				left = getMeasuredWidth() - child.getMeasuredWidth();
			}
			return left;
		}

		/**
		 * 控制child在垂直方向的移动 top:
		 * 表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy dy:
		 * 本次child垂直方向移动的距离 return: 表示你真正想让child的top变成的值
		 */
		public int clampViewPositionVertical(View child, int top, int dy) {
			if (top < 0) {
				//限制上面
				top = 0;
			} else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
				//限制下面
				//控件的高减去子控件的高
				top = getMeasuredHeight() - child.getMeasuredHeight();
			}
			return top;
		};

		/**
		 * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
		 * left：child当前最新的left top: child当前最新的top dx: 本次水平移动的距离 dy: 本次垂直移动的距离
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if (changedView == blueView) {
				// blueView移动的时候需要让redView跟随移动
				redView.layout(redView.getLeft() + dx, redView.getTop() + dy,redView.getRight() + dx, redView.getBottom() + dy);
			} else if (changedView == redView) {
				// redView移动的时候需要让blueView跟随移动
				blueView.layout(blueView.getLeft() + dx,blueView.getTop() + dy, blueView.getRight() + dx,blueView.getBottom() + dy);
			}
			
			//1.计算view移动的百分比，需要编程float，小的整数除以大的整数，始终是0
			//当前的left除以left最多能改变的值
			float fraction = changedView.getLeft()*1f/(getMeasuredWidth()-changedView.getMeasuredWidth());
			Log.e("tag", "fraction:"+fraction);
			//2.执行一系列的伴随动画
			executeAnim(fraction);
		}

		/**
		 * 手指抬起的执行该方法， releasedChild：当前抬起的view
		 * xvel: x方向的移动的速度 正：向右移动， 负：向左移动
		 * yvel: y方向移动的速度
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			int centerLeft = getMeasuredWidth() / 2 - releasedChild.getMeasuredWidth() / 2;
			if (releasedChild.getLeft() < centerLeft) {
				// 在左半边，应该向左缓慢移动
				//以及各封装scroller了，所有这里不用，已经提供方法了
				//用平滑滚动view到位置，滑动releasedChild，到0左边，top不变
				viewDragHelper.smoothSlideViewTo(releasedChild, 0,releasedChild.getTop());
				//底层用scroller，要刷新整个布局
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			} else {
				// 在右半边，应该向右缓慢移动
				viewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth() - releasedChild.getMeasuredWidth(),releasedChild.getTop());
				//底层用scroller，要刷新整个布局
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
		}
	};
	/**
	 * 执行伴随动画
	 * @param fraction
	 *
	 * nineoldandroids需要这个类库，可以兼容低版本的
	 */
	private void executeAnim(float fraction){
		//缩放，是0到1的变化，1+0.5f表示，至少可见，
//		ViewHelper.setScaleX(redView, 1+0.5f*fraction);
//		ViewHelper.setScaleY(redView, 1+0.5f*fraction);
		//旋转
//		ViewHelper.setRotation(redView,360*fraction);//围绕z轴转（中心轴）
		ViewHelper.setRotationX(redView,360*fraction);//围绕x轴转
//		ViewHelper.setRotationY(redView,360*fraction);//围绕y轴转
		ViewHelper.setRotationX(blueView,360*fraction);//围绕z轴转
		//平移
//		ViewHelper.setTranslationX(redView,80*fraction);
		//透明
//		ViewHelper.setAlpha(redView, 1-fraction);
		
		//设置过度颜色的渐变
		//传百分比，从什么颜色到什么颜色，需要转成integer值
		redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.GREEN));
		//设置当前layout的颜色
//		setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.GREEN));
	}

	//重写computeScroll()的原因是，调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
	public void computeScroll() {
		//表示没有移动结束
		if (viewDragHelper.continueSettling(true)) {
//			if(scroller.computeScrolloffset){
//				scrollTo(scroller.getCurrX(),scroller.getCurrY());
//				//刷新
//				invalidate();
//			}
			//底层用scroller，要刷新整个布局
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	};
}
