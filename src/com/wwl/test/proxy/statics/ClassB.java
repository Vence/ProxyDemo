package com.wwl.test.proxy.statics;

class ClassA {
	
	public void operateMethod1(){
		System.out.println("a");
	};
	
	public void operateMethod2(){};
	
	public void operateMethod3(){};
}

/**
 * 静态代理
 * 
 * @author 王文路
 * @date 2015-8-1
 *
 */
public class ClassB {

	private ClassA a;
	
	public ClassB(ClassA a) {
		super();
		this.a = a;
	}

	public void operateMethod1(){
		a.operateMethod1();
	};
	
	public void operateMethod2(){
		a.operateMethod2();
	};
	
}
