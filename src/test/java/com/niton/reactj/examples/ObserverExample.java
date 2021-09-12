package com.niton.reactj.examples;

import com.niton.reactj.observers.ObjectObserver;
import com.niton.reactj.proxy.ProxyCreator;
import com.niton.reactj.proxy.ProxySubject;

import java.awt.*;

public class ObserverExample {

	public static void main(String[] args) {
		Data model = ProxyCreator.subject(Data.class);

		ObjectObserver<Data> observer = new ObjectObserver<>();

		observer.addListener(
				change -> System.out.println("Property " + change.propertyName + " changed to " + change.propertyName)
		);
		observer.observe(model);

		model.setA(10);
		model.setB(20);
		model.setD("Some value");
		model.setD(Color.BLUE);
	}

	public static class Data implements ProxySubject {
		int    a = 1;
		int    b = 2;
		Object d = (Runnable) () -> {};

		public void setA(int a) {
			this.a = a;
		}

		public void setB(int b) {
			this.b = b;
		}

		public void setD(Object d) {
			this.d = d;
		}
	}
}
