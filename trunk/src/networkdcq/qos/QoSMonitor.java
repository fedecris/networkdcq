package networkdcq.qos;

/**
 * This class calculates how many messages can be sent to other hosts (or a specific host), 
 * considering (or not) the wireless signal strength, the network bandwith and the message object size  
 * 
 * This class must consider or base its information on:
 * """"""""""""""""""""""""""""""""""""""""""""""""""""
 *  - The network bandwidth
 * 	- The NetworkApplicationData object size to be sent
 *  - The amount of hosts in the network
 *  - The wireless signal strength   
 *  - Historical MPS
 */

import networkdcq.NetworkApplicationData;
import networkdcq.util.Logger;
import networkdcq.util.MemoryUtils;
import android.content.Context;

public abstract class QoSMonitor {

	/** Strength levels count for a wireless network */
	public static final int WIFI_STRENGTH_LEVELS = 10;
	
	/**
	 * Calculates the total amount of <code>message</code> messages per second  
	 * that can be sent to the other hosts, considering bandwidth but ignoring signal strength
	 * 
	 * @param 
	 * 		message the base object message to use for calculation
	 * @param 
	 * 		targetHostQty the amount of target hosts to send the message.  The total amount of hosts
	 * 		in the network can be obtained through <code>HostDiscovery.otherHosts.size()</code>
	 * @return 
	 * 		a value equal or greater than 0, or -1 in case of an error
	 */
	public int calculateIdealMPS(NetworkApplicationData message, int targetHostQty, Context context) {
		return getMPS(message, targetHostQty, false, false, context);
	}

	/**
	 * Estimates the total amount of <code>message</code> messages per second  
	 * that can be sent to the other hosts, considering bandwidth and signal strength (for WiFi networks)
	 * 
	 * @param 
	 * 		message the base object message to use for calculation
	 * @param 
	 * 		targetHostQty the amount of target hosts to send the message.  The total amount of hosts
	 * 		in the network can be obtained through <code>HostDiscovery.otherHosts.size()</code>
	 * @return 
	 * 		a value equal or greater than 0, or -1 in case of an error
	 */
	public int estimateRealMPS(NetworkApplicationData message, int targetHostQty, Context context) {
		return getMPS(message, targetHostQty, true, false, context);
	}

	/**
	 * Retrieves the total amount of <code>message</code> messages per second
	 * that where sent to other hosts, based on the corresponding logged information
	 * 
	 * @param 
	 * 		message the base object message to use for calculation
	 * @param 
	 * 		targetHostQty the amount of target hosts to send the message.  The total amount of hosts
	 * 		in the network can be obtained through <code>HostDiscovery.otherHosts.size()</code>
	 * @return 
	 * 		a value equal or greater than 0, or -1 in case of an error
	 */
	public int retrieveLoggedMPS(NetworkApplicationData object, int targetHostQty, Context context) {
		return getMPS(object, targetHostQty, false, true, context);
	}

	/**
	 * Calculates/estimates/retrieves the total amount of <code>object</code> messages per second that can be sent
	 * 
	 * @param 
	 * 		message the base object to use for calculation
	 * @param 
	 * 		targetHostQty the a mount of target hosts to send the same message object
	 * @param 
	 * 		considerSS takes into account the signal strength for the estimation.  
	 * 		If <code>useLoggedMPS</code> is true, this value has to be false.
	 * @param 
	 * 		useLoggedMPS bases its return value on the historical MPS, no estimation is made.
	 * 		If <code>considerSS</code> is true, this value has to be false.
	 * 
	 * @return  
	 * 		a value equal or greater than 0, or -1 in case of an error
	 */
	protected int getMPS(NetworkApplicationData message, int targetHostQty, boolean considerSS, boolean useLoggedMPS, Context context) {
		// Whether calculate/estimate or base the return value on historical data, but not both
		if (considerSS && useLoggedMPS) {
			Logger.e("Invalid arguments: considerSS = useLoggedMPS = true");
			throw new RuntimeException("Invalid arguments: considerSS = useLoggedMPS = true");
		}
		// Validate targetHostQty 
		if (targetHostQty < 1) {
			Logger.e("Invalid argument: targetHostQty < 1");
			throw new RuntimeException("Invalid argument: targetHostQty < 1");			
		}

		// Retrieve logged. TODO: Implementation pending
		if (useLoggedMPS) {
			return 0;
		}
		
		// Initial values
		double retValue = -1;
		int messageSizeBits = MemoryUtils.sizeOf(message) * 8;
		int networkSpeedMbps = getNetworkSpeed(context);
		float signalStrengthFactor = considerSS ? (float)((getNetworkSignalStrength(context) + 1)) / (float)WIFI_STRENGTH_LEVELS : 1;
		
		// Calulate ideal o estimate real
		retValue = (networkSpeedMbps * 1024 * 1024 / messageSizeBits) * ((signalStrengthFactor + .1) / 2);
		return (int)(retValue / targetHostQty);
	}
	
	/**
	 * This method must return the established network speed in Mbps
	 * 
	 * @param context
	 * 		the Android context
	 * @return
	 * 		the corresponding value in Mbps, or -1 in case of an error
	 */
	public abstract int getNetworkSpeed(Context context);
	
	/**
	 * In a wireless network, this method must return a normalized 
	 * signal strength raging from 0 to {@code WIFI_STRENGTH_LEVELS}
	 * In a wired network this method must return {@code WIFI_STRENGTH_LEVELS}
	 * 
	 * @param context
	 * 		the Android context
	 * @return
	 * 		the corresponding value, or -1 in case of an error
	 */
	public abstract int getNetworkSignalStrength(Context context);

}
