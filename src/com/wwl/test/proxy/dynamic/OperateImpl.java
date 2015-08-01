package com.wwl.test.proxy.dynamic;

/**
 * ί����
 * @author ����·
 * @date 2015-8-1
 *
 */
public class OperateImpl implements Operate{

	@Override
	public void operateMethod1() {
		// TODO Auto-generated method stub
		System.out.println("Invoke operateMethod1");
		sleep(110);
	}

	@Override
	public void operateMethod2() {
		// TODO Auto-generated method stub
		System.out.println("Invoke oprateMethod2");
		sleep(120);
	}

	@Override
	public void operateMethod3() {
		// TODO Auto-generated method stub
		System.out.println("Invoke operateMethod3");
		sleep(130);
	}
	
	private static void sleep(long millSecond){
		try {
			Thread.sleep(millSecond);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
