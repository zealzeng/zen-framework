import org.zenframework.util.StringUtils;

/**
 * @author Zeal 2016年7月7日
 */
public class Ognl {
	
	/**
	 * Check whether object is null or not
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		return obj == null;
	}
	
	/**
	 * @param obj
	 * @return
	 */
	public static boolean isNotNull(Object obj) {
		return obj != null;
	}
	
	/**
	 * Check whether string is blank, null,empty string,white charactor string are considered as blank
	 * @param str
	 * @return
	 */
	public static boolean isBlankString(Object str) {
		if (str == null) {
			return true;
		}
		if (!(str instanceof String)) {
			return false;
		}
		String string = (String) str;
		return StringUtils.isBlank(string);
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static boolean isNotBlankString(Object str) {
		return !isBlankString(str);
	}
	
	/**
	 * Check whether string is null or empty whose length is zero
	 * @param str
	 * @return
	 */
	public static boolean isEmptyString(Object str) {
		if (str == null) {
			return true;
		}
		if (!(str instanceof String)) {
			return false;
		}
		String string = (String) str;
		return StringUtils.isEmpty(string);
	}
	
	/**
	 * @param str
	 * @return
	 */
	public static boolean isNotEmptyString(Object str) {
		return !isEmptyString(str);
	}
	


}
