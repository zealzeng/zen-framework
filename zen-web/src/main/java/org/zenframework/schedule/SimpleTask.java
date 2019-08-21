package org.zenframework.schedule;

import java.util.Date;

/**
 * @author Zeal
 */
public interface SimpleTask extends Runnable {
	
	/**
	 * Unique task id
	 * @return
	 */
	String getTaskId();
	
	/**
	 * Start time
	 * @return
	 */
	Date getStartTime();
	
	/**
	 * With fixed rate/period in ms
	 * @return
	 */
	long getFixedRate();
	
	/**
	 * With fixed delay in ms
	 * @return
	 */
	long getFixedDelay();
	

}
