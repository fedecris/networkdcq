package networkdcq;

/**
 * Network support main class.  Applications interested in using this framework should
 * invoke the static methods configureStartup() and doStartup() in that order.
 */

import networkdcq.communication.NetworkCommunication;
import networkdcq.communication.NetworkCommunicationFactory;
import networkdcq.discovery.HostDiscovery;
import networkdcq.discovery.HostDiscoveryFactory;
import networkdcq.util.Logger;

public class NetworkStartup {

	/**
	 * NetworkCommunication implementation logic
	 */
	protected static NetworkCommunication networkCommunication = null;
	
	/**
	 * NetworkDicovery implementation logic
	 */
	protected static HostDiscovery networkDiscovery = null;
	
	
	/**
	 * Network main application entry point for configuration.
	 * @param consumer 
	 * 		instance in charge of updating the local model based on the received messages from other hosts
	 * @param producer
	 * 		instance in charge of setting the local information to be broadcasted periodically to the other hosts
	 * @return
	 * 		true of configuration was OK, or false otherwise
	 * @throws 
	 * 		Exception if an application is trying to configure an already configured startup
	 */
	public static boolean configureStartup(NetworkApplicationDataConsumer consumer, NetworkApplicationDataProducer producer) throws Exception {
		
		try {
			
			if (networkDiscovery!=null || networkCommunication!=null)
				throw new Exception ("NetworkStartup already configured.");
			
	    	// Discovery handler
	        networkDiscovery = HostDiscoveryFactory.getHostDiscovery(HostDiscoveryFactory.getDefaultDiscoveryMethod());
	        // Communication listener
	        networkCommunication = NetworkCommunicationFactory.getNetworkCommunication(NetworkCommunicationFactory.getDefaultNetworkCommunication());
	        networkCommunication.setConsumer(consumer);
	        networkCommunication.setProducer(producer);
	        return true;
		} 
		catch (Exception e) {
			Logger.e(e.getMessage());
			return false;
		}
        
	}
	
	/**
	 * Network main application entry point for starting host discovery and host communication logic.
	 * Must execute the proper configuration first. 
	 * @return 
	 * 		true if startup was OK, or false otherwise
	 * @throws 
	 * 		Exception in case of error or misconfiguration
	 */
	public static boolean doStartup() throws Exception {
		
		// Was the startup correctly configured?
		if (networkCommunication==null || networkDiscovery==null)
			throw new Exception ("NetworkStartup not configured.  Invoke configureStartup() first.");
		
		try {
			if (HostDiscovery.NO_NETWORK_IP.equals(HostDiscovery.thisHost.getHostIP())) {
				Logger.w("Cannot start network services.  No network detected");
				return false;
			}
			// Communication server
			if (!networkCommunication.startService())
				throw new Exception ("Error starting network communication service");
	        
	        // Discovery service
	        if (!networkDiscovery.startDiscovery())
	        	throw new Exception ("Error starting network discovery service");
	        
	        // Communication client
	        if (!networkCommunication.startBroadcast())
	        	throw new Exception ("Error starting network communication broadcast");
	        	
	        return true;
		}
		catch (Exception e) {
			Logger.e(e.getMessage());
			return false;
		}		
        
	}

	/**
	 * Shortcut for:
	 * <code>HostDiscoveryFactory.getHostDiscovery(HostDiscoveryFactory.getDefaultDiscoveryMethod())</code>
	 * @return the default discovery method instance
	 */
	public static HostDiscovery getDiscovery() {
		return HostDiscoveryFactory.getHostDiscovery(HostDiscoveryFactory.getDefaultDiscoveryMethod());
	}
	
	/**
	 * Shortcut for:
	 * <code>NetworkCommunicationFactory.getNetworkCommunication(NetworkCommunicationFactory.getDefaultNetworkCommunication())</code>
	 * @return the default network communication instance
	 */
	public static NetworkCommunication getCommunication() {
		return NetworkCommunicationFactory.getNetworkCommunication(NetworkCommunicationFactory.getDefaultNetworkCommunication());
	}

	
	
}

