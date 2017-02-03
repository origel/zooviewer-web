package com.xiaomi.zooviewer.util;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

public class ZKTester {
    public static final int SESSION_TIMEOUT = 30000;
    public static final int CONNECTION_TIMEOUT = 30000;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ZkClient zkCLient = new ZkClient("localhost:2181", SESSION_TIMEOUT, CONNECTION_TIMEOUT, new BytesPushThroughSerializer());
		
		System.out.println(zkCLient.exists("/"));
		
		byte[] byts = zkCLient.readData("/");
		System.out.println(new String(byts));
	}

}
