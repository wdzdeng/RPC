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

	private static RegisterCenter registerCenter;// ע������
	private static Integer port;// ����˶˿ں�
	private static ExecutorService executor;// �̳߳�
	private static boolean isStart = false;

	public ServerImp(RegisterCenter registerCenter, ExecutorService executor, Integer port) {
		this.registerCenter = registerCenter;
		this.port = port;
		this.executor = executor;

	}

	// ����˿�������ʹ�ö��߳�
	@Override
	public void start() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			isStart = true;
			while (isStart) {
				System.out.println("�������Ѿ�����");
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

	// ����˹رշ���
	@Override
	public void stop() {
		isStart = false;
		executor.shutdown();

	}

	// �������ע������ע�����
	@Override
	public void register(Class service, Class serviceImp) {
		registerCenter.register(service.getName(), serviceImp);

	}

	// ����˴�ע������ע������
	@Override
	public void remove(Class service) {

		registerCenter.remove(service.getName());

	}
	//����˴�ע������ȡ��Ӧ�Ľӿ�ʵ�����class
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
				String serviceName = inputStream.readUTF();//�õ����ýӿڵ�����
				String MethodName = inputStream.readUTF();//�õ����÷���������
				Class[] parameterTypes = (Class[]) inputStream.readObject();//�õ����÷�������������б�
				Object[] arguments = (Object[]) inputStream.readObject();//�õ�������β�
				Class ServiceCalss = registerCenter.getServiceClass(serviceName);//��ע�������õ����ýӿڵ�ʵ����
				Method method = ServiceCalss.getMethod(MethodName, parameterTypes);
				Object result = method.invoke(ServiceCalss.newInstance(), arguments);//����
				outputStream = new ObjectOutputStream(socket.getOutputStream());//
				outputStream.writeObject(result);//�����õĽ���ٴ��ݸ����Ѷ�
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
