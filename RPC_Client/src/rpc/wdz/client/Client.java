package rpc.wdz.client;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
	/**
	 * 客户端主要使用动态代理
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getProxy(Class<?> serviceInterface,InetSocketAddress addr) {
		return (T)Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[] {serviceInterface}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				Socket socket = new Socket();
				ObjectOutputStream outputStream = null;
				ObjectInputStream inputStream = null;
				Object result = null;
				try {
					socket.connect(addr);
					outputStream = new ObjectOutputStream(socket.getOutputStream());
					outputStream.writeUTF(serviceInterface.getName());
					outputStream.writeUTF(method.getName());
					outputStream.writeObject(method.getParameterTypes());
					outputStream.writeObject(args);
					
					inputStream = new ObjectInputStream(socket.getInputStream());
					result = inputStream.readObject();
					
				}catch (Exception e) {
					e.printStackTrace();
				}finally {
					try {
						if (outputStream != null) {
							outputStream.close();
						}
						if (inputStream != null) {
							inputStream.close();
						}
						if(socket != null) {
							socket.close();
						}	
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
				return result;

			}
		});
	}
}
