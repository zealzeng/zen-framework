/**
 * 
 */
package org.zenframework.schedule;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.collections.map.AbstractReferenceMap;
import org.apache.commons.collections.map.ReferenceMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

/**
 * Wrapper of ThreadPoolTaskScheduler
 * @author Zeal
 * @see org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
 *
 */
public class SimpleTaskScheduler implements InitializingBean,DisposableBean {
	
	private ThreadPoolTaskScheduler taskScheduler = null;
	
	private Map<String,ScheduledFuture<?>> taskMap = null;
	
	public SimpleTaskScheduler() {
		taskScheduler = new ThreadPoolTaskScheduler();
		setRemoveOnCancelPolicy(true);
	}
	
	public void setPoolSize(int poolSize) {
		taskScheduler.setPoolSize(poolSize);
	}
	
	public int getPoolSize() {
		return taskScheduler.getPoolSize();
	}
	
	public void setRemoveOnCancelPolicy(boolean removeOnCancelPolicy) {
		this.taskScheduler.setRemoveOnCancelPolicy(removeOnCancelPolicy);
	}
	
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.taskScheduler.setErrorHandler(errorHandler);
	}
	
	/**
	 * Schedule task
	 * @param task
	 * @return
	 */
	public ScheduledFuture<?> scheduleTask(SimpleTask task) {
		ScheduledFuture<?> future = null;
		if (task.getFixedRate() > 0) {
			if (task.getStartTime() != null) {
			    future = this.taskScheduler.scheduleAtFixedRate(task, task.getStartTime(), task.getFixedRate());
			}
			else {
				future = this.taskScheduler.scheduleAtFixedRate(task, task.getFixedRate());
			}
		}
		else if (task.getFixedDelay() > 0) {
			if (task.getStartTime() != null) {
				future = this.taskScheduler.scheduleWithFixedDelay(task, task.getStartTime(), task.getFixedDelay());
			}
			else {
				future = this.taskScheduler.scheduleWithFixedDelay(task, task.getFixedDelay());
			}
		}
		else {
			if (task.getStartTime() != null) {
				future = this.taskScheduler.schedule(task, task.getStartTime());
			}
			else {
				future = this.taskScheduler.schedule(task, new Date());
			}
		}
		this.taskMap.put(task.getTaskId(), future);
		return future;
	}
	
	/**
	 * Cancel task by id
	 * @param taskId
	 */
	public void cancelTask(String taskId) {
		ScheduledFuture<?> future = this.taskMap.remove(taskId);
		if (future != null) {
			//Interupt thread might be a bit danger
			future.cancel(false);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		this.taskScheduler.initialize();
		taskMap = Collections.synchronizedMap(new ReferenceMap(AbstractReferenceMap.HARD, AbstractReferenceMap.SOFT));
	}
	
	
	public static void main(String[] args) throws Exception {
		String str = null;
		System.out.println(str instanceof String);
	}

	@Override
	public void destroy() throws Exception {
		this.taskScheduler.destroy();
	}

}
