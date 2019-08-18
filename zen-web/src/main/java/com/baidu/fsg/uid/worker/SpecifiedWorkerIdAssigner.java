/**
 * 
 */
package com.baidu.fsg.uid.worker;

/**
 * Assign the worker id manually from the configuration file or etc.
 * @author Zeal
 *
 */
public class SpecifiedWorkerIdAssigner implements WorkerIdAssigner {
	
	private long workderId = 0;
	
	public SpecifiedWorkerIdAssigner(long workerId) {
		this.workderId = workerId;
	}

	public long assignWorkerId() {
		return this.workderId;
	}

}
