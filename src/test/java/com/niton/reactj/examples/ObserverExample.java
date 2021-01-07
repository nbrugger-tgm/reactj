package com.niton.reactj.examples;

import com.niton.reactj.*;
import com.niton.reactj.mvc.ReactiveModel;
import com.niton.reactj.mvc.ReactiveObject;

import java.awt.*;

public class ObserverExample {

	public static class Data{
		int a=1;
		int b =2;
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

	public static void main(String[] args) {
		ReactiveProxy<Data> model = ReactiveObject.create(Data.class);
		Data d = model.object;

		Observer<ReactiveModel<Data>> observer = new Observer<ReactiveModel<Data>>() {
			@Override
			public void onChange(String property, Object value) {
				System.out.println("Property "+property+" changed to "+value);
			}
		};
		observer.bind(model.reactive);

		d.setA(10);
		d.setB(20);
		d.setD("Some value");
		d.setD(Color.BLUE);
	}
}
