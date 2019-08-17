/*
 * Copyright (c) 2016, All rights reserved.
 */
package org.zenframework.conf;

import org.zenframework.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Firstly, we will find the config file under system environment,
 * if it's not existed, we will search the file in class path/config.
 * @author Zeal 2016年4月27日
 */
public class Environment {

	/** -Dade=xxxx Use the key to decrypt the property source */
    private static final String APP_DECRYPT_KEY = "ade";

	public static final String APP_NAME_KEY = "app.name";

	public static final String APP_VERSION_KEY = "app.version";

	public static final String APP_PROFILE_KEY = "app.profile";

	public static final String APP_ID_KEY = "app.id";

	/** Default configuration directory key in system environment */
    public static final String DEFAULT_SYS_ENV_CONFIG_DIR = "APP_CONFIG_DIR";

    /** Default configuration file */
    public static final String DEFAULT_CONFIG_FILE_NAME = "app.properties";

	/** Configuration file content */
	private Properties properties = null;

	/** System environment name represents the config dir */
	private String sysConfigDirName = DEFAULT_SYS_ENV_CONFIG_DIR;

	/** Configuration file name */
	private String configFileName = DEFAULT_CONFIG_FILE_NAME;

	/** Reserve the configuration dir */
	private File configDir = null;

	public Environment() {
	}

	/**
	 * Load properties under system environment or class path config dir
	 */
	public void initialize() {

		File configFile = null;
		//Search in system environment path
		if (StringUtils.isNotBlank(this.sysConfigDirName)) {
			String configDirStr = System.getenv(this.sysConfigDirName);
			if (StringUtils.isNotBlank(configDirStr)) {
				File _configDir = new File(configDirStr);
				if (_configDir.exists() && _configDir.isDirectory()) {
					configFile = new File(_configDir, this.configFileName);
					if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
						this.configDir = _configDir;
					}
				}
			}
		}
		//Search in class path
		if (this.configDir == null) {
			File _configDir = null;
			try {
				_configDir = ClassPathUtils.getClassPath(Environment.class);
			}
			catch (IOException e) {
				e.printStackTrace();
				throw new IllegalStateException("Failed to find config file in class path", e);
			}
			if (_configDir.exists() && _configDir.isDirectory()) {
				configFile = new File(_configDir, this.configFileName);
				if (!(configFile.exists() && configFile.isFile() && configFile.canRead())) {
					throw new IllegalStateException("Property file " + this.configFileName + " cannot be found in environment or class path");
				}
				this.configDir = _configDir;
			}
			else {
				throw new IllegalStateException("Property file " + this.configFileName + " cannot be found in environment or class path");
			}
		}
		//Load and decrypt properties
		try {
			this.properties = this.loadAndDecryptProperties(configFile);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IllegalStateException("Failed to load and decrypt properties", e);
		}
	}

	/**
	 * Load and decrypt properties
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private Properties loadAndDecryptProperties(File file) throws Exception {

		Properties prop = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			prop.load(fis);
		}
		if (prop.size() <= 0) {
			return prop;
		}
		Iterator<Map.Entry<Object, Object>> iter = prop.entrySet().iterator();
		Map<String,String> decryptMap = new HashMap<>();
		byte[] bek = null;
		while (iter.hasNext()) {
			Map.Entry<Object, Object> entry = iter.next();
			String key = (String) entry.getKey();
			if (key.charAt(0) == '*') {
				if (bek == null) {
					bek = getAppDecryptKey();
					if (bek == null) {
						throw new IllegalStateException("Failed to get bootstrap environment key");
					}
				}
				String value = (String) entry.getValue();
				byte[] valueBytes = CryptoUtils.hexStringToBytes(value);
				String decryptValue =  new String(CryptoUtils.decryptByAes(valueBytes, bek), StandardCharsets.UTF_8);
				String decryptKey = key.substring(1);
				decryptMap.put(decryptKey, decryptValue);
				iter.remove();
			}
		}
		if (decryptMap.size() > 0) {
			prop.putAll(decryptMap);
		}
		return prop;
	}

    /**
     * getAppDecryptKey
     *
     * @return
     */
    private byte[] getAppDecryptKey() {
        String bek = System.getProperty(APP_DECRYPT_KEY);
        if (StringUtils.isEmpty(bek)) {
            try {
                return NetUtils.getLocalMacAddress(false);
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        else {
            System.setProperty(APP_DECRYPT_KEY, null);
            try {
                return bek.getBytes("UTF-8");
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

	/**
	 * @return the propertyFileName
	 */
	public String getConfigFileName() {
		return configFileName;
	}

	/**
	 * @param propertyFileName the propertyFileName to set
	 */
	public void setConfigFileName(String propertyFileName) {
		this.configFileName = propertyFileName;
	}

	public String getProperty(String key, String defaultValue) {
		return this.properties.getProperty(key, defaultValue);
	}

	public String getProperty(String key) {
		return getProperty(key, "");
	}

	public int getIntProperty(String key, int defaultValue) {
		String value = this.properties.getProperty(key);
		return NumberUtils.toInt(value, defaultValue);
	}

	public int getIntProperty(String key) {
		return this.getIntProperty(key, 0);
	}

	public double getDoubleProperty(String key, double defaultValue) {
		String value = this.properties.getProperty(key);
		return NumberUtils.toDouble(value, defaultValue);
	}

	public double getDoubleProperty(String key) {
		return this.getDoubleProperty(key, 0d);
	}

	/**
	 * @return the sysConfigDirName
	 */
	public String getSysConfigDirName() {
		return sysConfigDirName;
	}

	/**
	 * @param sysConfigDirName the sysConfigDirName to set
	 */
	public void setSysConfigDirName(String sysConfigDirName) {
		this.sysConfigDirName = sysConfigDirName;
	}

	public String getAppName() {
		return this.getProperty(APP_NAME_KEY, "");
	}

	/**
	 * Default version is current timestamp-based value
	 * @return
	 */
	public String getAppVersion() {
		return this.getProperty(APP_VERSION_KEY, "");
	}

	public String getAppProfile() {
		return this.getProperty(APP_PROFILE_KEY, "");
	}

	public String getAppId() {
		return this.getProperty(APP_ID_KEY, "");
	}

	public File getConfigDir() {
		return configDir;
	}
}
