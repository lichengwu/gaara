package cn.lichengwu.gaara.info;

import java.io.Serializable;

/**
 * 瞬态信息。继承或实现这个接口的类，表示的信息为瞬时信息，在不同时间状态可能不同。
 * <p>
 * 例如，JVM中内存的消耗，在每时每刻都是不同的，调用{@link #refresh()}方法，刷新瞬态信息。
 * 
 * @author lichengwu
 * @created 2012-1-9
 * 
 * @version 1.0
 */
public interface TransientInfo extends Serializable{

	/**
	 * 刷新，更新瞬态信息。
	 * 
	 * @author lichengwu
	 * @created 2012-1-9
	 * 
	 * @return 如果刷新成功，返回true。否则返回false。
	 */
	boolean refresh();
	
	/**
	 * 返回最后一次更新时间的毫秒数
	 * 
	 * @author lichengwu
	 * @created 2012-2-16
	 *
	 * @return 最后一次更新时间的毫秒数
	 */
	long lastUpdate();
}