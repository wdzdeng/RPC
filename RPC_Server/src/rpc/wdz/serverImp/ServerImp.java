package rpc.wdz.serverImp;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import rpc.wdz.registerCenter.RegisterCenter;
import rpc.wdz.serverInf.Server;

public class ServerImp implements Server {

	private static RegisterCenter registerCenter;// 注册中心
	private static Integer port;// 服务端端口号
	private static ExecutorService executor;// 线程池
	private static boolean isStart = false;

	public ServerImp(RegisterCenter registerCenter, ExecutorService executor, Integer port) {
		this.registerCenter = registerCenter;
		this.port = port;
		this.executor = executor;

	}

	// 服务端开启服务，使用多线程
	@Override
	public void start() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			isStart = true;
			while (isStart) {
				System.out.println("服务器已经启动");
				Socket socket = serverSocket.accept();
				executor.execute(new Task(socket));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (serverSocket != null) {
					serverSocket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	// 服务端关闭服务
	@Override
	public void stop() {
		isStart = false;
		executor.shutdown();

	}

	// 服务端向注册中心注册服务
	@Override
	public void register(Class service, Class serviceImp) {
		registerCenter.register(service.getName(), serviceImp);

	}

	// 服务端从注册中心注销服务
	@Override
	public void remove(Class service) {

		registerCenter.remove(service.getName());

	}
	//服务端从注册中心取对应的接口实现类的class
	@Override
	public Class getServiceClass(Class service) {
		return registerCenter.getServiceClass(service.getName());
		
	}

	private static class Task implements Runnable {
		Socket socket;

		public Task(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			ObjectOutputStream outputStream = null;
			ObjectInputStream inputStream = null;
			try {
				inputStream = new ObjectInputStream(socket.getInputStream());
				String serviceName = inputStream.readUTF();//得到调用接口的名称
				String MethodName = inputStream.readUTF();//得到调用方法的名称
				Class[] parameterTypes = (Class[]) inputStream.readObject();//得到调用方法里参数类型列表
				Object[] arguments = (Object[]) inputStream.readObject();//得到传入的形参
				Class ServiceCalss = registerCenter.getServiceClass(serviceName);//从注册中心拿到调用接口的实现类
				Method method = ServiceCalss.getMethod(MethodName, parameterTypes);
				Object result = method.invoke(ServiceCalss.newInstance(), arguments);//反射
				outputStream = new ObjectOutputStream(socket.getOutputStream());//
				outputStream.writeObject(result);//将调用的结果再传递给消费端
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (outputStream != null) {
						outputStream.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}



}
