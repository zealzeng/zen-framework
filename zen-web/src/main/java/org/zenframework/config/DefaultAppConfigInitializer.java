package org.zenframework.config;

import org.zenframework.util.*;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * 1.Find configuration dir and file in system environment
 * 2.Find configuration file in  class path
 * 3.Load and decrypt config file
 * Created by Zeal on 2019/1/12 0012.
 */
public class DefaultAppConfigInitializer extends AppConfigInitializer {

    /** -Dade=xxxx Use the key to decrypt the property source */
    private static final String APP_DECRYPT_KEY = "ade";

    /** Default configuration directory key in system environment */
    public static final String DEFAULT_SYS_ENV_CONFIG_DIR = "WEBAPP_CONFIG_DIR";

    /** Default configuration file */
    public static final String DEFAULT_CONFIG_FILE_NAME = "app.properties";

    /**
     * System environment name represents the config dir
     */
    private String sysEnvConfigDirName = DEFAULT_SYS_ENV_CONFIG_DIR;

    /**
     * Configuration file name
     */
    private String configFileName = DEFAULT_CONFIG_FILE_NAME;

    /**
     * Reserve the configuration file
     */
    private File configFile = null;

    /**
     * Property source
     */
    private PropertySource propertySource = null;

    /**
     * Load properties under system environment or class path config dir
     *
     * @throws IOException
     */
    public DefaultAppConfigInitializer(String sysEnvConfigDirName, String configFileName) {

        if (StringUtils.isNotBlank(sysEnvConfigDirName)) {
            this.sysEnvConfigDirName = sysEnvConfigDirName;
        }
        if (StringUtils.isNotBlank(configFileName)) {
            this.configFileName = configFileName;
        }
        File configDir = null;
        //Search in system environment path
        String configDirStr = System.getenv(this.sysEnvConfigDirName);
        if (StringUtils.isNotBlank(configDirStr)) {
            File _configDir = new File(configDirStr);
            if (_configDir.exists() && _configDir.isDirectory()) {
                configFile = new File(_configDir, this.configFileName);
                if (configFile.exists() && configFile.isFile() && configFile.canRead()) {
                    configDir = _configDir;
                }
            }
        }
        //Search in class path
        if (configDir == null) {
            File _configDir = null;
            try {
                _configDir = ClassPathUtils.getClassPath(DefaultAppConfigInitializer.class);
            }
            catch (Exception e) {
                throw new IllegalStateException("Failed to find class path dir", e);
            }
            if (_configDir.exists() && _configDir.isDirectory()) {
                File _configFile = new File(_configDir, this.configFileName);
                if (!(_configFile.exists() && _configFile.isFile() && _configFile.canRead())) {
                    throw new IllegalStateException("Property file " + this.configFileName + " cannot be found in environment or class path");
                }
                this.configFile = _configFile;
            }
            else {
                throw new IllegalStateException("Property file " + this.configFileName + " cannot be found in environment or class path");
            }
        }
        //Load and decrypt properties
        try {
            this.propertySource = new PropertiesPropertySource(this.getClass().getName(), this.loadAndDecryptProperties(configFile));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("Failed to load and decrypt properties", e);
        }
    }

    /**
     * Load and decrypt properties
     *
     * @param file
     * @return
     * @throws Exception
     */
    private Properties loadAndDecryptProperties(File file) throws Exception {

        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(file)){
            prop.load(fis);
        }
        if (prop.size() <= 0) {
            return prop;
        }
        Iterator<Map.Entry<Object, Object>> iter = prop.entrySet().iterator();
        Map<String, String> decryptMap = new HashMap<>();
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
                String decryptValue = new String(CryptoUtils.decryptByAes(valueBytes, bek), StandardCharsets.UTF_8);
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

    @Override
    protected PropertySource getAppConfigs() {
        return this.propertySource;
    }
}
