package networkdcq.qos;


public class QoSMonitorFactory {
	
	/** Wireless monitor implementation */
	public static final int QOS_MONITOR_WIFI = 1;
	/** Selected QosMonitor implementation instance */
	private static QoSMonitor instance = null;

	/**
	 * Default QoSMonitor
	 * @return method identifier for default QoSMonitor
	 */
	public static int getDefaultQoSMonitor() {
		return QOS_MONITOR_WIFI;
	}
	
	/**
	 * Factory for QosMonitor implementations
	 * @param 
	 * 		type identifier for default QosMonitor
	 * @return 
	 * 		an instance of the selected QosMonitor implementation, or null otherwise
	 */
	public static QoSMonitor getQosMonitor(int type) {
		switch (type) {
		case QOS_MONITOR_WIFI:
			if (instance==null)
				instance = new WiFiQoSMonitor(); 
			return instance;
		default:
			return null;
		}
	}


}
