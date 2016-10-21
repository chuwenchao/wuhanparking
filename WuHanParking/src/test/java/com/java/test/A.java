package com.java.test;

public class A {
	public String name;
	private String sex;
	
	public A() {
		System.out.println("A init...");
	}
	
	public String show(D Obj){
		return ("A and D");
	}
	
	public String show(A Obj){
		return ("A and A");
	}

}
