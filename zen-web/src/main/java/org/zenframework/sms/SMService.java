/**
 * 
 */
package org.zenframework.sms;

import org.zenframework.common.Result;

/**
 * Short message service
 * @author Zeal
 *
 */
public interface SMService {
	
	Result<String> send(String mobile, String message, String callbackURL);

}
