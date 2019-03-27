package rpc.wdz.serverInf;

public interface Server {
	public void start();
	
	public void stop();
	
	public void register(Class service,Class serviceImp);
	
	public void remove(Class service);
	
	public Class getServiceClass(Class service);
	
}
