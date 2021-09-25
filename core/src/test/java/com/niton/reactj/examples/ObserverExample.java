package com.niton.reactj.examples;

import com.niton.reactj.core.observer.ObjectObserver;
import com.niton.reactj.core.proxy.ProxyCreator;
import com.niton.reactj.core.proxy.ProxySubject;

import java.awt.*;


public class ObserverExample {

	public static class Data implements ProxySubject {
		int    a = 1;
		int    b = 2;
		Object d = (Runnable) () -> {
		};

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

	public static void main(String[] args) {
		ProxyCreator creator = ProxyCreator.besideOrigin();
		Data model = creator.create(new Data());

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
}
