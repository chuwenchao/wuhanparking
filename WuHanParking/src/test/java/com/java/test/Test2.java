package com.java.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//通过反射获取类的私有构造方法、私有普通方法和私有属性
public class Test2 {

	public static void main(String[] args) {
		String cn = MathDemo.class.getCanonicalName();
		
		try {
			Class<?> c = Class.forName(cn);
			Field[] field = c.getDeclaredFields();
			for(Field f : field) {
				System.out.println(c.getDeclaredField(f.getName()));
			}
			Method[] method = c.getDeclaredMethods();
			for(Method m : method) {
				System.out.println(c.getDeclaredMethod(m.getName(), m.getParameterTypes()));
				m.setAccessible(true); //取消访问检查
				//方式1：无参构造方法需为public
//				if(m.getName() == "add"){
//					System.out.println("调用方法：" + m.getName() + " 结果：" + m.invoke(c.newInstance(), 2, 4));
//				}
				//方式2：无参构造方法可为private
				if(m.getName() == "add"){
					Constructor<MathDemo> con =  MathDemo.class.getDeclaredConstructor();
					con.setAccessible(true);
					System.out.println("调用方法：" + m.getName() + " 结果：" + m.invoke(con.newInstance(), 2, 4));
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
