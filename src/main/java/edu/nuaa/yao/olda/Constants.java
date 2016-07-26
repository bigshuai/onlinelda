package edu.nuaa.yao.olda;

public class Constants {
	public static final long BUFFER_SIZE_LONG = 1000000;
	public static final short BUFFER_SIZE_SHORT = 512;
	//模型状态设置4种
	public static final int MODEL_STATUS_UNKNOWN = 0;
	public static final int MODEL_STATUS_EST = 1;
	public static final int MODEL_STATUS_ESTC = 2;
	public static final int MODEL_STATUS_INF = 3;
	
	//速度? 比率? 等级?
	public static final int FORWARD_RATE = 1;
	public static final int COMMENT_RATE = 1;
	public static final int LIKE_RATE = 1;
	
	
}
