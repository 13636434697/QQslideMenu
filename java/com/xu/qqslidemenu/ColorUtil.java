package com.xu.qqslidemenu;

/*
* 通过百分比计算颜色，传开始的颜色和结束的颜色
* */
public class ColorUtil {
	public static Object evaluateColor(float fraction, Object startValue,Object endValue) {
		//4个通过的值，进行位移
		int startInt = (Integer) startValue;
		int startA = (startInt >> 24) & 0xff;
		int startR = (startInt >> 16) & 0xff;
		int startG = (startInt >> 8) & 0xff;
		int startB = startInt & 0xff;

		int endInt = (Integer) endValue;
		int endA = (endInt >> 24) & 0xff;
		int endR = (endInt >> 16) & 0xff;
		int endG = (endInt >> 8) & 0xff;
		int endB = endInt & 0xff;

		return (int) ((startA + (int) (fraction * (endA - startA))) << 24)
				| (int) ((startR + (int) (fraction * (endR - startR))) << 16)
				| (int) ((startG + (int) (fraction * (endG - startG))) << 8)
				| (int) ((startB + (int) (fraction * (endB - startB))));
	}
}
