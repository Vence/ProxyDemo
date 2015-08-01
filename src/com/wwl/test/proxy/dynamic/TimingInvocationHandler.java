package com.wwl.test.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理
 * 
 * @author 王文路
 * @date 2015-8-1
 *
 */
public class TimingInvocationHandler implements InvocationHandler {
	
	private Object target;
	

	public TimingInvocationHandler(Object target) {
		super();
		this.target = target;
	}

	public TimingInvocationHandler() {
		super();
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		long start =  System.currentTimeMillis();
		
		Object obj = method.invoke(target, args);
		
		System.out.println(method.getName() + "cost time is :" + (System.currentTimeMillis() - start)); 
		
		return obj;
	}

}
