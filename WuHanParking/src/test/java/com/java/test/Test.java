package com.java.test;

public class Test {

	public static void main(String[] args) {
		A a1 = new A();
		A a2 = new B(); //向上转型，用子类继承的方法重写父类的方法，实际调用的为父类方法
		B b = new B();
		C c = new C();
		D d = new D();

//		System.out.println(a1.show(b));
//		System.out.println(a1.show(c));
//		System.out.println(a1.show(d));
//		System.out.println("");
		System.out.println(a2.show(b));
//		System.out.println(a2.show(c));
//		System.out.println(a2.show(d));
//		System.out.println("");
		
		System.out.println(b.show(a1)); 
		
	}
}
