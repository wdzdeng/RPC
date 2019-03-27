package rpc.wdz.registerCenter;

import java.nio.channels.NonWritableChannelException;
import java.util.HashMap;
import java.util.Map;

public class RegisterCenter {
	private static Map<String,Class> centerMap;
	private static RegisterCenter registerCenter;
	private RegisterCenter() {
		centerMap = new HashMap<>();
	}
	public static RegisterCenter getReigsterCenter() {
		if (registerCenter == null) {
			synchronized (RegisterCenter.class) {
				if(registerCenter == null) {
					registerCenter = new RegisterCenter();
				}
			}
		}
		return registerCenter;		
	}
	public Class getServiceClass(String serviceName) {
		return centerMap.get(serviceName);
	}
	public void register(String serviceName,Class serviceImp) {
		centerMap.put(serviceName, serviceImp);
	}
	public void remove(String serviceName) {
		centerMap.remove(serviceName);		
	}
	public void removeAll() {
		centerMap.clear();
	}
}
