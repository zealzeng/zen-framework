/*
 * Copyright (c) 2016, All rights reserved.
 */
package com.github.zenframework.util;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.github.zenframework.util.bcrypt.BCryptPasswordEncoder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.github.zenframework.vo.Pair;

/**
 * @author Zeal 2016年2月26日
 */
public class CryptoUtils {
    
    private static final String HEX_DIGITS = "0123456789abcdef";
    
    private static final String RSA_SEPERATOR = "#";
	
	//It should be thread safe
	private static BCryptPasswordEncoder bcryptPwEncoder = new BCryptPasswordEncoder();
	
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
	
	/**
	 * @param rawPass
	 * @return
	 */
	public static String encodePwd(CharSequence rawPass) {
		return bcryptPwEncoder.encode(rawPass);
	}
	
	/**
	 * @param rawPassword
	 * @param encodedPassword
	 * @return
	 */
	public static boolean pwdMatches(CharSequence rawPassword, String encodedPassword){
		return bcryptPwEncoder.matches(rawPassword, encodedPassword);
	}
	
	/**
	 * AES/CBC/PKCS5Padding
	 * @param text
	 * @param keys
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByAes(byte[] text, byte[] keys) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    //The password must be 16  bytes
	    byte[] keyBytes = new byte[16];
	    byte[] b = keys;
	    int len = b.length;
	    if (len > keyBytes.length) {
	        len = keyBytes.length;
	    }
	    System.arraycopy(b, 0, keyBytes, 0, len);
	    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
	    IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
	    cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
	    return cipher.doFinal(text);
	}
	
	
	/**
	 * AES/CBC/PKCS5Padding
	 * @param text
	 * @param keys
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByAes(byte[] text, byte[] keys) throws Exception {
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    byte[] keyBytes = new byte[16];
	    byte[] b = keys;
	    int len = b.length;
	    if (len > keyBytes.length) {
	        len = keyBytes.length;
	    }
	    System.arraycopy(b, 0, keyBytes, 0, len);
	    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
	    IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
	    cipher.init(Cipher.DECRYPT_MODE, keySpec,ivSpec);
	    return cipher.doFinal(text);
	}
	
	/**
     * https://blog.csdn.net/weilai_zhilu/article/details/77932630
     * @param dataBytes
     * @param keyBytes
     * @param ivBytes
     * @return
     * @throws Exception
     */
    public static byte[] decryptByAesCbcPkcs7Padding(byte[] dataBytes, byte[] keyBytes, byte[] ivBytes) throws Exception {

        // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
        int base = 16;
        if (keyBytes.length % base != 0) {
            int groups = keyBytes.length / base + (keyBytes.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(keyBytes, 0, temp, 0, keyBytes.length);
            keyBytes = temp;
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        SecretKeySpec spec = new SecretKeySpec(keyBytes, "AES");
        AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
        parameters.init(new IvParameterSpec(ivBytes));
        cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
        return cipher.doFinal(dataBytes);
    }
	
    /**  
     * 转换字节数组为十六进制字符串 
     * @param    b
     */  
    public static String byteArrayToHexString(byte[] b){  
        StringBuilder sb = new StringBuilder(b.length * 2);  
        for (int i = 0; i < b.length; i++){  
            //resultSb.append(byteToHexString(b[i]));
        	byteToHextString(sb, b[i]);
        }  
        return sb.toString();  
    }

    /** 将一个字节转化成十六进制形式的字符串     */  
    public static String byteToHexString(byte b){  
        StringBuilder sb = new StringBuilder(2);
        byteToHextString(sb, b);
        return sb.toString();
    }
    
    private static void byteToHextString(StringBuilder sb, byte b) {
    	int n = b;  
    	//ASCII范围
        if (n < 0) {
            n = 256 + n;
        }
        int size = HEX_DIGITS.length();
        int d1 = n / size;  
        int d2 = n % size;  
        sb.append(HEX_DIGITS.charAt(d1)).append(HEX_DIGITS.charAt(d2));  
    }
    
    /**  
     * Convert hex string to byte[]  
     * @param s
     * @return byte[]
     */  
    public static byte[] hexStringToBytes(String s) {   
    	int len = s.length();
        byte[] bytes = new byte[len/2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            bytes[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return bytes;
    }
    
    /**
     * @param messageBytes
     * @param algorithm
     * @return
     * @throws Exception
     */
    public static byte[] messageDigest(byte[] messageBytes, String algorithm) throws Exception {
		MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
		messageDigest.update(messageBytes);
		return messageDigest.digest();
    }
    
    
    /**
     * Generate RSA public key and private key
     * @param size
     * @return
     * @throws Exception
     */
    public static Pair<RSAPublicKey, RSAPrivateKey> generateRsaKeys(int size) throws Exception {
    	KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
    	generator.initialize(size);
    	KeyPair pair = generator.generateKeyPair();
    	Pair<RSAPublicKey, RSAPrivateKey> result = new Pair<>();
    	result.setKey((RSAPublicKey) pair.getPublic());
    	result.setValue((RSAPrivateKey) pair.getPrivate());
    	return result;
    }
    
    /**
     * 串行化公钥
     * @param publicKey
     * @return
     */
    public static String serializeRsaPublicKey(RSAPublicKey publicKey) {
    	return serializeRsaKey(publicKey.getModulus(), publicKey.getPublicExponent());
    }
    
    /**
     * 串行化私钥
     * @param privateKey
     * @return
     */
    public static String serializeRsaPrivateKey(RSAPrivateKey privateKey) {
    	return serializeRsaKey(privateKey.getModulus(), privateKey.getPrivateExponent());
    }
    
    /**
     * 反串行化公钥
     * @param serializeString
     * @return
     * @throws Exception
     */
    public static RSAPublicKey deserializeRsaPublicKey(String serializeString) throws Exception {
    	
    	String[] values = deserializeModulusAndExponent(serializeString);
    	if (values == null) {
    		return null;
    	}
    	String modulusString = values[0];
    	String exponentString = values[1];
    	
    	BigInteger modulus = new BigInteger(Base64.getUrlDecoder().decode(modulusString));
    	BigInteger exponent = new BigInteger(Base64.getUrlDecoder().decode(exponentString));
    	KeyFactory factory = KeyFactory.getInstance("RSA");
    	RSAPublicKeySpec sepc = new RSAPublicKeySpec(modulus, exponent);
    	return (RSAPublicKey) factory.generatePublic(sepc);
    }
    
    /**
     * RSA加密
     * @param sources
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByRsa(byte[] sources, Key key) throws Exception {
    	return rsaCipher(Cipher.ENCRYPT_MODE, sources, key);
    }
    
    /**
     * RSA解密
     * @param targets
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByRsa(byte[] targets, Key key) throws Exception {
		return rsaCipher(Cipher.DECRYPT_MODE, targets, key);
    }
    
    private static byte[] rsaCipher(int mode, byte[] sources, Key key) throws Exception {
    	Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(mode, key);
		return cipher.doFinal(sources);
    }
    
    
    /**
     * 反串行化私钥
     * @param serializeString
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey deserializeRsaPrivateKey(String serializeString) throws Exception {
    	
    	String[] values = deserializeModulusAndExponent(serializeString);
    	if (values == null) {
    		return null;
    	}
    	String modulusString = values[0];
    	String exponentString = values[1];
    	
    	BigInteger modulus = new BigInteger(Base64.getUrlDecoder().decode(modulusString));
    	BigInteger exponent = new BigInteger(Base64.getUrlDecoder().decode(exponentString));
    	KeyFactory factory = KeyFactory.getInstance("RSA");
    	RSAPrivateKeySpec sepc = new RSAPrivateKeySpec(modulus, exponent);
    	return (RSAPrivateKey) factory.generatePrivate(sepc);
    }
    
    private static String[] deserializeModulusAndExponent(String serializeString) {
    	int index = serializeString.indexOf(RSA_SEPERATOR);
    	if (index == -1) {
    		return null;
    	}
    	String modulusString = serializeString.substring(0, index);
    	String exponentString = serializeString.substring(index + 1);
    	return new String[] {modulusString, exponentString};
    }
    
    private static String serializeRsaKey(BigInteger modulus, BigInteger exponent) {
    	String modulusString = Base64.getUrlEncoder().encodeToString(modulus.toByteArray());
    	String exponentString =  Base64.getUrlEncoder().encodeToString(exponent.toByteArray());
    	StringBuilder sb = new StringBuilder(modulusString.length() + exponentString.length() + RSA_SEPERATOR.length());
    	sb.append(exponentString).append(RSA_SEPERATOR).append(modulusString);
    	return sb.toString();
    }
   

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		String str = "123456";
		byte[] bytes = str.getBytes("UTF-8");
		byte[] targets = messageDigest(bytes, "SHA3-256");
		System.out.println(byteArrayToHexString(targets));
        
	}

}
