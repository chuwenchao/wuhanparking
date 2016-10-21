package com.java.test;

public class B extends A{
	
	public B() {
		name = "aa";
		System.out.println("B init...");
	}
	
	public String show(B Obj){
		return ("B and B");
	}
	
	public String show(A Obj){
		return ("B and A");
	}

}
