package rpc.wdz.main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import rpc.wdz.Imp.HelloWorldImp;
import rpc.wdz.intf.HelloWorld;
import rpc.wdz.registerCenter.RegisterCenter;
import rpc.wdz.serverImp.ServerImp;
import rpc.wdz.serverInf.Server;

public class ServerMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RegisterCenter registerCenter = RegisterCenter.getReigsterCenter();
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		Server server =new ServerImp(registerCenter, executor, 9999);
		server.register(HelloWorld.class, HelloWorldImp.class);
		server.start();
	}

}
