package com.wwl.test.proxy.dynamic;

import java.lang.reflect.Proxy;

/**
 * 动态代理测试
 * 
 * @author 王文路
 * @date 2015-8-1
 *
 */
public class Main {

	public static void main(String[] args){
		
		// create proxy instance
		TimingInvocationHandler timingInvocationHandler
		 = new TimingInvocationHandler(new OperateImpl());
		
		Operate operate = (Operate)Proxy.newProxyInstance(Operate.class.getClassLoader(), new Class[]{Operate.class},
				timingInvocationHandler);
		
		
		// call method of proxy instance
		
		operate.operateMethod1();
		
		System.out.println();
		
		operate.operateMethod2();
		
		System.out.println();
		
		operate.operateMethod3();
		
	}
}
