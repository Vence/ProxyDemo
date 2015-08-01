# ProxyDemo

##Java动态代理

##1.相关概念
###1.1代理
在某些情况下，我们不希望或是不能直接访问对象A,而是通过访问一个中介对象B,由B去访问A达成目的，这种方式我们就成为代理。

这里对象A所属类我们成为委托类，也成为被代理类，对象B所属类成为代理类。

代理优点有：

- 隐藏委托类的实现
- 解耦，不改变委托类代码的情况下做一些额外处理，比如添加初始判断及其他公共操作

根据程序运行前代理类是否已经存在，可以将代理分为静态代理和动态代理。

###1.2静态代理
代理类在程序运行前已经存在的代理方式成为静态代理。

通过上面解释可以知道，由开发人员编写或是编译器生成代理类的方式都属于静态代理。
源码中实现了一个静态代理的实例：

`ClassA` 是委托类 ， `ClassB`是代理类，`ClassB`中的函数都是直接调用`ClassA`相应函数，并且可以隐藏`ClassA`的`operateMethod3()`函数。

静态代理中代理类和委托类也常常继承同一父类或实现同一接口。

###1.3动态代理
代理类在程序运行前不存在，运行时由程序动态生成的代理方式成为动态代理。

Java提供了动态代理的实现方式，可以在运行时刻动态生成代理类。这种代理方式的一大好处是可以方便对代理类的函数做统一或特殊处理，如记录所有函数的执行时间、所有函数执行前添加验证判断、对某个特殊函数进行特殊操作，而不用像静态代理方式那样需要修改每一个函数。

##2.动态代理实例

实现动态代理包括三步：

- 新建委托类
- 实现`InvocationHandler`接口，这是负责连接代理类或委托类的中间类必须实现的接口
- 通过`Proxy`类新建代理类对象

###2.1 新建委托类

	public interface Operate {
		
		public void operateMethod1();
		
		public void operateMethod2();
		
		public void operateMethod3();
	
	}

实现：

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

`Operate`是一个接口，定义了一些函数，我们要统计这些函数的执行时间。

`OperateImpl`是委托类，实现`Operate`接口。每个函数简单输出字符串，并等待一段时间。

动态代理要求委托类必须实现某个接口，比如这里委托类`OperateImpl`实现了`Operate`。

###2.2实现InvocationHandler接口

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

target 属性表示委托类对象。

InvocationHandler 是负责连接代理类和委托类的中间类必须实现的接口。其中只有一个：

	public Object invoke(Object proxy, Method method, Object[] args)

函数需要去实现，参数：

- `Proxy`表示下面2.3通过`Proxy.newProxyInstance()`生成的代理类对象
- `Method`表示代理对象被调用的函数
- `args`表示代理类对象被调用的函数的参数

调用代理类对象的每个函数实际最终都是调用了`InvocationHandelr`的`invoke`函数。这里我们在`invoke`实现最红刚添加了开始结束计时，其中还调用了委托类对象`target`的相应函数，这样便完成了统计执行时间的需求。

`invoke`函数中我们也可以通过对`method`做一些判断，从而对某些函数特殊处理。

###2.3通过Proxy类静态函数生成代理对象

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

这里我们先将委托类对象 `new OperateImpl()` 作为`TimingInvocationHandler`构造函数入参创建`timingInvocationHandler`对象；

然后通过`xy.newProxyInstance(...)`函数新建了一个代理对象，实际代理类就是在这时候动态生成的。我们调用该代理对象的函数就会调用到`timingInvocationHandler`的`invoke`函数（是不是有点类似静态代理） ， 而invoke函数实现中调用委托类对象 `new OperateImpl()` 相应的	`method`。

	 public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException

- `loader`表示类加载器
- `interfaces` 表示委托类的接口，生成代理类时需要实现这些接口
- `h`是`InvocationHandler`实现类接口，负责连接代理类和委托类的中间类。

我们可以这样理解，如上的动态代理实现实际是双层的静态代理，开发者提供了委托类B ,程序动态生成了代理类A。开发者还需要提供一个实现了InvocationHandler的之类C,子类C连接代理类A和委托类B，它是代理类A的委托类，委托类B的代理类。用户直接调用代理类A的对象，A将调用转发给委托类C，委托类C再讲调用转发给它的委托类B.

##3.动态代理原理
实际上面最后一段已经说明了动态代理的真正原理。我们来仔细分析下。

###3.1生成的动态代理类代码
下面是上面示例程序运行时自动生成的动态代理类代码，如何得到这些生成的代码请见ProxyUtils，查看 class 文件可使用 jd-gui

	import com.codekk.java.test.dynamicproxy.Operate; 
	import java.lang.reflect.InvocationHandler; 
	import java.lang.reflect.Method; 
	import java.lang.reflect.Proxy; 
	import java.lang.reflect.UndeclaredThrowableException;  
	public final class $Proxy0 extends Proxy   implements Operate {   
	private static Method m4;   
	private static Method m1;   
	private static Method m5;   
	private static Method m0;   
	private static Method m3;   
	private static Method m2;    
	
	public $Proxy0(InvocationHandler paramInvocationHandler)     throws    {     
		super(paramInvocationHandler);   
	}    
	
	public final void operateMethod1()     throws    {     
		try     {      
			h.invoke(this, m4, null);       return;     
		} catch (Error|RuntimeException localError)     {      
			throw localError;     
		} catch (Throwable localThrowable)     {      
			throw new UndeclaredThrowableException(localThrowable);     
		}   
	}    
	
	public final boolean equals(Object paramObject)     throws    {     
	
		try     {      
			return ((Boolean)h.invoke(this, m1, new Object[] { paramObject })).booleanValue();     
		}   catch (Error|RuntimeException localError)     {       
			throw localError;    
		}   catch (Throwable localThrowable)     {       
			throw new UndeclaredThrowableException(localThrowable);     
		}   
	}    
	
	public final void operateMethod2()     throws    {     
		try     {      
			h.invoke(this, m5, null);       return;     
		} catch (Error|RuntimeException localError)     {       
			throw localError;     
		}  catch (Throwable localThrowable)     {      
			throw new UndeclaredThrowableException(localThrowable); 
		}   
	}    
	
	public final int hashCode()     throws    {    
		try     {       
			return ((Integer)h.invoke(this, m0, null)).intValue();     
		} catch (Error|RuntimeException localError)     {      
			throw localError;     
		}catch (Throwable localThrowable)     {      
			throw new UndeclaredThrowableException(localThrowable); 
		}   
	}   

	public final void operateMethod3()     throws    {     
		try     {       
			h.invoke(this, m3, null);       return;     
		}  catch (Error|RuntimeException localError)     {       
			throw localError;     
		}catch (Throwable localThrowable)     {       
			throw new UndeclaredThrowableException(localThrowable);    
		}  
	}    
	
	public final String toString()     throws    {     
		try     {       
			return (String)h.invoke(this, m2, null);     
		}  catch (Error|RuntimeException localError)     {       
			throw localError;     
		}  catch (Throwable localThrowable)     {       
			throw new UndeclaredThrowableException(localThrowable);     
		}   
	}    
	
	static   {     
		try     {      
			m4 = Class.forName("com.codekk.java.test.dynamicproxy.Operate").getMethod("operateMethod1", new Class[0]);       
			m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });       
			m5 = Class.forName("com.codekk.java.test.dynamicproxy.Operate").getMethod("operateMethod2", new Class[0]);       
			m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);      
			m3 = Class.forName("com.codekk.java.test.dynamicproxy.Operate").getMethod("operateMethod3", new Class[0]);       
			m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);       return;     
		}catch (NoSuchMethodException localNoSuchMethodException)     {       
			throw new NoSuchMethodError(localNoSuchMethodException.getMessage());     
		}catch (ClassNotFoundException localClassNotFoundException)     {       
			throw new NoClassDefFoundError(localClassNotFoundException.getMessage());     }   
		} 
	} 
	
从中我们可以看出动态生成的代理类是以	`$Proxy`为类名前缀，继承自`Proxy`，并且实现了`Proxy.newProxyInstance(…)`第二个参数传入的所有接口的类。
如果代理类实现的接口中存在非 public 接口，则其包名为该接口的包名，否则为com.sun.proxy。
其中的operateMethod1()、operateMethod2()、operateMethod3()函数都是直接交给h去处理，h在父类Proxy中定义为

	protected InvocationHandler h; 

即为Proxy.newProxyInstance(…)第三个参数。
所以InvocationHandler的子类 C 连接代理类 A 和委托类 B，它是代理类 A 的委托类，委托类 B 的代理类。

###3.2生成动态代理类原理

以下针对 Java 1.6 源码进行分析，动态代理类是在调用Proxy.newProxyInstance(…)函数时生成的。

####（1） newProjectInstance(...)

	public static Object newProxyInstance(ClassLoader loader,  Class<?>[] interfaces, InvocationHandler h)     throws IllegalArgumentException {     

	if (h == null) {        
		throw new NullPointerException();     
	 }     

	 /*      * Look up or generate the designated proxy class.      */     
	 Class cl = getProxyClass(loader, interfaces);      
	 
	 /*      * Invoke its constructor with the designated invocation handler.      */     
	 try {         
	 
		Constructor cons = cl.getConstructor(constructorParams);         
	 
		return (Object) cons.newInstance(new Object[] { h });     
	 } catch (NoSuchMethodException e) {         
		throw new InternalError(e.toString());     
	 } catch (IllegalAccessException e) {         
		throw new InternalError(e.toString());    
	 } catch (InstantiationException e) {         
		throw new InternalError(e.toString());     
	 } catch (InvocationTargetException e) {         
		throw new InternalError(e.toString());     
	} 
	 } 

####（2）getProxyClass(…)
函数代码及解释如下(省略了)：


函数主要包括三部分：

- 入参 interfaces 检验，包含是否在入参指定的 ClassLoader 内、是否是 Interface、interfaces 中是否有重复
- 以接口名对应的 List 为 key 查找代理类，如果结果为：

 - 弱引用，表示代理类已经在缓存中；
 - pendingGenerationMarker 对象，表示代理类正在生成中，等待生成完成返回；
 - null 表示不在缓存中且没有开始生成，添加标记到缓存中，继续生成代理类。
    
- 如果代理类不存在调用ProxyGenerator.generateProxyClass(…)生成代理类并存入缓存，通知在等待的缓存。

函数中几个注意的地方：

- 代理类的缓存 key 为接口名对应的 List，接口顺序不同表示不同的 key 即不同的代理类。
- 如果 interfaces 中存在非 public 的接口，则所有非 public 接口必须在同一包下面，后续生成的代理类也会在该包下面。
- 代理类如果在 ClassLoader 中已经存在的情况没有做处理。
- 可以开启 System Properties 的sun.misc.ProxyGenerator.saveGeneratedFiles开关，保存动态类到目的地址。


Java 1.7 的实现略有不同，通过getProxyClass0(…)函数实现，实现中调用代理类的缓存，判断代理类在缓存中是否已经存在，存在直接返回，不存在则调用proxyClassCache的valueFactory属性进行动态生成，valueFactory的apply函数与上面的getProxyClass(…)函数逻辑类似。

##4.使用场景
###4.1J2EE Web 开发中 Spring 的 AOP(面向切面编程) 特性

作用：目标函数之间解耦。

比如在 Dao 中，每次数据库操作都需要开启事务，而且在操作的时候需要关注权限。一般写法是在 Dao 的每个函数中添加相应逻辑，造成代码冗余，耦合度高。
使用动态代理前伪代码如下：

	Dao {    
		 insert() {        
			 判断是否有保存的权限；        
			 开启事务；         
			 插入；         
			 提交事务；     
		}     

		 delete() {        
			 判断是否有删除的权限；         
			 开启事务；         
			 删除；         
			 提交事务；     
  		} 
	} 

使用动态代理的伪代码如下：

	// 使用动态代理，组合每个切面的函数，而每个切面只需要关注自己的逻辑就行，达到减少代码，松耦合的效果 
	invoke(Object proxy, Method method, Object[] args)                     throws Throwable {     
		判断是否有权限；     
		开启事务；    
		 Object ob = method.invoke(dao, args)；    
		 提交事务；     
		return ob;  
	} 

###4.2基于 REST 的 Android 端网络请求框架 Retrofit

作用：简化网络请求操作。

一般情况下每个网络请求我们都需要调用一次HttpURLConnection或者HttpClient进行请求，或者像Volley一样丢进等待队列中，Retrofit 极大程度简化了这些操作，示例代码如下:

	public interface GitHubService {  
		 @GET("/users/{user}/repos")  
		 List<Repo> listRepos(@Path("user") String user); 
	}  

	RestAdapter restAdapter = new RestAdapter.Builder()     
		.setEndpoint("https://api.github.com") .build();  

	GitHubService service = restAdapter.create(GitHubService.class); 

以后我们只需要直接调用:

	List<Repo> repos = service.listRepos("octocat"); 

即可开始网络请求，Retrofit的原理就是基于动态代理，它同时用到了注解的原理，本文不做深入介绍，具体请等待Retrofit 源码解析完成。
