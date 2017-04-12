package com.xu.qqslidemenu;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
/*
* 侧滑的效果，继承FrameLayout，实现3个构造方法
* */
public class SlideMenu extends FrameLayout{

	private View menuView;//菜单的view
	private View mainView;//主界面的view
	//需要拖拽的时候需要的类
	private ViewDragHelper viewDragHelper;
	private int width;
	private float dragRange;//拖拽范围
	//用non的包
	private FloatEvaluator floatEvaluator;//float的计算器
	private IntEvaluator intEvaluator;//int的计算器
	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public SlideMenu(Context context) {
		super(context);
		init();
	}

    //给回调接口，定义几个变量值，来表示到底是打开还是关闭
	//定义状态常量，用的是枚举的方法
	enum DragState{
		Open,Close;
	}
	//还得定义一个变量表示当前状态
	private DragState currentState = DragState.Close;//当前SlideMenu的状态默认是关闭的

	//初始化方法，需要在构造方法里面都调用一次
	private void init(){
		//callback需要new一个内部类，不能解析事件，还需要onTouchEvent传递事件
		viewDragHelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
		intEvaluator = new IntEvaluator();
	}
	/**
	 * 子view的状态，暴漏一个方法，获取当前的状态
	 * @return
	 */
	public DragState getCurrentState(){
		return currentState;
	}

	//不需要on什么什么
	//操纵2个子view，先获取2个子view的引用，在这个方法里面，完成填充
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		//只能处理2个子布局的类，
		//简单的异常处理
		if(getChildCount()!=2){
			//抛了一个具体的异常信息
			throw new IllegalArgumentException("SlideMenu only have 2 children!");
		}
		//获取的菜单的view
		menuView = getChildAt(0);
		//获取的主界面的view
		mainView = getChildAt(1);
		//处理主界面拖拽的时候，需要获取宽高
	}
	/**
	 * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化自己和子View的宽高
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		//获取当前控件的宽
		width = getMeasuredWidth();
		//获取下拖拽范围，宽乘以0.6f
		dragRange = width*0.6f;
	}

	//是否应该拦截事件，他会自己判断，一般这2个方法，和onTouchEvent都要重写一边
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return viewDragHelper.shouldInterceptTouchEvent(ev);
	}
	//callback需要new一个内部类，不能解析事件，还需要onTouchEvent传递事件
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		//因为自己要处理，所以返回true事件消费
		return true;
	}


	//callback需要new一个内部类，不能解析事件，还需要onTouchEvent传递事件
	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
		/**
		 * 用于判断是否捕获当前child的触摸事件 
		 * child: 当前触摸的子View 
		 * return: true:就捕获并解析 false：不处理
		 */
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			//2个子布局都需要解析捕获
			return child==menuView || child==mainView;
		}

		//只要获取水平方向的几个方法就可以了

		/**
		 * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 
		 * 最好不要返回0
		 */
		public int getViewHorizontalDragRange(View child) {
			//这个方法不能限制边界，用在手指抬起时，动画时间的计算上，不能返回0
			return (int) dragRange;
		}
		/**
		 * 控制child在水平方向的移动 left:
		 * 表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx dx:
		 * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
		 */
		//修正的，用来控制view水平拖拽的范围
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			//限制一下主界面移动的
			if(child==mainView){
				if(left<0)left=0;//限制mainView的左边
				if(left>dragRange)left=(int) dragRange;//限制mainView的右边
			}
			//返回left就可以直接跑了
			return left;
		}
		/**
		 * 控制child在垂直方向的移动 top:
		 * 表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy dy:
		 * 本次child垂直方向移动的距离 return: 表示你真正想让child的top变成的值
		 */
		//用来做伴随移动的，也需要重写限制
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			if(changedView==menuView){
				//固定住menuView
				menuView.layout(0, 0, menuView.getMeasuredWidth(),menuView.getMeasuredHeight());

				//让mainView移动起来
				int newLeft = mainView.getLeft()+dx;
				if(newLeft<0)newLeft=0;//限制mainView的左边
				if(newLeft>dragRange)newLeft=(int) dragRange;//限制mainView的右边
				mainView.layout(newLeft,mainView.getTop()+dy,newLeft+mainView.getMeasuredWidth(),mainView.getBottom()+dy);
			}

			//因为在滑动过程中，2个布局有逐渐变大或变小，或者逐渐变暗或亮的情况，需要把百分比算出来
			//1.计算滑动的百分比，当前的left除以最大的left
			float fraction = mainView.getLeft()/dragRange;
			//2.执行伴随动画
			//执行的方法会不断的调用的，在滑动的过程中，会得到一个百分比，是一个0-1的取值
			executeAnim(fraction);


			//枚举定义的常量，给开关状态进行赋值，在这里做，因为每次滑动的时候都会回调这个方法完全打开的时候fraction百分比是1
			//3.更改状态，回调listener的方法，加严禁的判断，当前状态如果关闭的时候不用关闭了
			if(fraction==0 && currentState!=DragState.Close){
				//更改状态为关闭，并回调关闭的方法
				currentState = DragState.Close;
				//需要判断下之后才关闭
				if(listener!=null)listener.onClose();

			}else if (fraction==1f && currentState!=DragState.Open) {
				//更改状态为打开，并回调打开的方法
				currentState = DragState.Open;
				if(listener!=null)listener.onOpen();
			}
			//将drag的fraction暴漏给外界
			if(listener!=null){
				listener.onDraging(fraction);
			}
		}

		/**
		 * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
		 * left：child当前最新的left top: child当前最新的top dx: 本次水平移动的距离 dy: 本次垂直移动的距离
		 */
		//实在不知道去看testtActivity，这个方法是释放的，就是可以自动移动的
		//因为滑动达不到0的效果（完全遮盖住另外一个布局）
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if(mainView.getLeft()<dragRange/2){
				//在左半边
				close();
			}else {
				//在右半边
				open();
			}

			//xvel用户滑动的速度
			//处理用户的稍微滑动
			if(xvel>200 && currentState!=DragState.Open){
				open();
			}else if (xvel<-200 && currentState!=DragState.Close) {
				close();
			}
		}
	};
	/**
	 * 关闭菜单
	 */
	public void close() {
		//执行一个平滑滚动，用的是封装过的方法
		viewDragHelper.smoothSlideViewTo(mainView,0,mainView.getTop());
		//刷新界面，是属性整个view
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		//还不要忘记实现一个方法computeScroll
		//重写computeScroll()的原因是，调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
	}
	/**
	 * 打开菜单
	 */
	public void open() {
		//执行一个平滑滚动，用的是封装过的方法
		viewDragHelper.smoothSlideViewTo(mainView,(int) dragRange,mainView.getTop());
		//刷新界面，是属性整个view
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		//还不要忘记实现一个方法computeScroll
		//重写computeScroll()的原因是，调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
	}
	/**
	 * 执行伴随动画
	 * 	执行的方法会不断的调用的，在滑动的过程中，会得到一个百分比，是一个0-1的取值
	 * @param fraction
	 */
	private void executeAnim(float fraction){
		//fraction:0-1
		//先让主界面缩小，缩小mainView
//		float scaleValue = 0.8f+0.2f*(1-fraction);//1-0.8f
		//缩小哪个view，缩小到多少,从1-0.4的变窄
		//1到0.8至少也是0.8加上1减去0.2乘以1-fraction，和下面的一样的，（fraction一开始是0，0的时候整体是1，fraction是1的时候整体是0.8）（最小是0.8，变化的范围0.2）
//		float scaleValue = 0.8f+0.2f*（1-fraction);这个变化的值是1到0.8f，根据百分比0-1的范围（起始值+百分比*（结束值-开始值））
//		ViewHelper.setScaleX(mainView,1-0.4f*fraction);

		//计算的帮助类，决绝上面的问题上面是手工算的
		ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));//百分比，起始值，结束值
		ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
		//移动menuView，这里计算的是int的（百分比，开始值负的宽的一般，结束值0）
		ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
		//放大menuView，（百分比，起始值0.5，结束值1）
		ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
		ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
		//改变menuView的透明度（百分比，起始值0.3，结束值1）
		ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));
		
		//给SlideMenu的背景添加黑色的遮罩效果
		//获取背景图片，然后给图片设置一个颜色的过滤器 用工具类来计算（百分比，从什么颜色，到什么颜色，模式是怎么覆盖）
		getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT),Mode.SRC_OVER);
	}

	//重写computeScroll()的原因是，调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
	public void computeScroll() {
		//如果动画还没有结束
		if(viewDragHelper.continueSettling(true)){
			//在刷新一下整个布局
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	};


	//给回调接口，定义几个变量值，来表示到底是打开还是关闭
	//定义状态常量，用的是枚举的方法
	//还得定义一个变量表示当前状态
	//给回调方法提供对应的set方法
	private OnDragStateChangeListener listener;
	public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
		this.listener = listener;
	}
	/*
	* 需要将拖拽的百分比暴漏给外界
	* 外界能根据百分比，来做一些额外的动画，
	* 并且打开和关闭也暴漏出去
	*
	* 在上面提供队形的set方法
	* */
	public interface OnDragStateChangeListener{
		/**
		 * 打开的回调
		 */
		void onOpen();
		/**
		 * 关闭的回调
		 */
		void onClose();
		/**
		 * 正在拖拽中的回调
		 * 需要将拖拽的百分比的参数加进去
		 */
		void onDraging(float fraction);
	}
	
}
