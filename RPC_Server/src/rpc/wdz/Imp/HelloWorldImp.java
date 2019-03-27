package rpc.wdz.Imp;

import rpc.wdz.intf.HelloWorld;

public class HelloWorldImp implements HelloWorld {

	@Override
	public String hello(String name) {
		return name+"£ºÄãºÃ£¡";
	}

}
