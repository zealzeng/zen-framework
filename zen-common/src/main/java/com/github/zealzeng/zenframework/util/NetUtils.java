/**
 * 
 */
package com.github.zealzeng.zenframework.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author Administrator
 *
 */
public class NetUtils {
	
	
    /**
     * Retrieve the first validated local ip address(the Public and LAN ip addresses are validated).
     *
     * @return the local address
     * @throws SocketException the socket exception
     */
    public static List<InetAddress> getLocalInetAddresses() throws SocketException {
        // enumerates all network interfaces
        Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
        List<InetAddress> addresses = new ArrayList<InetAddress>(4);
        while (enu.hasMoreElements()) {
            NetworkInterface ni = enu.nextElement();
            if (ni.isLoopback()) {
                continue;
            }

            Enumeration<InetAddress> addressEnumeration = ni.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress address = addressEnumeration.nextElement();

                // ignores all invalidated addresses
                if (address.isLinkLocalAddress() || address.isLoopbackAddress() || address.isAnyLocalAddress()) {
                    continue;
                }
                
                addresses.add(address);
            }
        }
        return addresses;
    }
    
    /**
     * Get all mac addresses
     * @param up true the mac must be active, false - up or down both are included
     * @return
     * @throws SocketException
     */
    public static List<byte[]> getLocalMacAddresses(boolean up) throws SocketException {
        Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
        List<byte[]> addresses = new ArrayList<byte[]>(4);
        while (enu.hasMoreElements()) {
            NetworkInterface ni = enu.nextElement();
            if (ni.isLoopback()) {
            	continue;
            }
            if (up && !ni.isUp()) {
            	continue;
            }
            if (ni.getHardwareAddress() != null) {
                addresses.add(ni.getHardwareAddress());
            }
        }
        return addresses;
    }
    
    public static byte[] getLocalMacAddress(boolean up) throws SocketException {
        Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
        while (enu.hasMoreElements()) {
            NetworkInterface ni = enu.nextElement();
            if (ni.isLoopback()) {
            	continue;
            }
            if (up && !ni.isUp()) {
            	continue;
            }
            if (ni.getHardwareAddress() != null) {
                return ni.getHardwareAddress();
            }
        }
        return null;
    }
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//getLocalMacAddresses();
		getLocalMacAddresses(false);

	}

}
