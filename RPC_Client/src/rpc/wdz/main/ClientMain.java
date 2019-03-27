package rpc.wdz.main;

import java.net.InetSocketAddress;

import rpc.wdz.client.Client;
import rpc.wdz.intf.HelloWorld;

public class ClientMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HelloWorld helloWorld = (HelloWorld) Client.getProxy(HelloWorld.class, new InetSocketAddress("192.168.157.65", 9999));
		System.out.println(helloWorld.hello("wdz"));
		System.out.println(helloWorld.hello("wdz"));
		System.out.println(helloWorld.hello("wdz"));
	}

}
