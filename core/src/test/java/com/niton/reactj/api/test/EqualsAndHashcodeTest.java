package com.niton.reactj.api.test;

import com.niton.reactj.api.proxy.ProxyCreator;
import com.niton.reactj.api.proxy.ProxySubject;
import com.niton.reactj.api.react.ReactiveObject;
import com.niton.reactj.api.react.ReactiveWrapper;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("equals(), hashCode() and serialization")
public class EqualsAndHashcodeTest {
	/**
	 * If this is true also classes without a {@code equals} or {@code hashCode} implementation will be tested
	 */
	public static final boolean testHashless = false;

	private static <A, B, C, D> DynamicContainer generateTests(
			A withHash,
			A withHash2,
			B withOutHash,
			B withOutHash2,
			C equalBaseWithHash,
			C equalBaseWithoutHash,
			D differentWithHash,
			D differentWithoutHash,
			A[] unique,
			B[] unique2
	) {
		Stream<DynamicNode> hashless = Stream.of(
				dynamicTest("inequality", () -> unequalsTest(differentWithoutHash, withOutHash)),
				dynamicTest("content equality", () -> equalsTest(withOutHash2, withOutHash)),
				dynamicTest("content equality (reactive->base)",
						() -> equalsTest(equalBaseWithoutHash, withOutHash)),
				dynamicTest("hash-code", () -> hashTest(equalBaseWithoutHash, withOutHash, unique))
		);
		Stream<DynamicNode> hashfull = Stream.of(
				dynamicTest("inequality       (hashCode & equals implemented)",
						() -> unequalsTest(differentWithHash, withHash)),
				dynamicTest("content equality (hashCode & equals implemented)",
						() -> equalsTest(withHash2, withHash)),
				dynamicTest("content equality (reactive->base)(hashCode & equals implemented)",
						() -> equalsTest(equalBaseWithHash, withHash)),
				dynamicTest("hash-code        (hashCode & equals implemented)",
						() -> hashTest(equalBaseWithHash, withHash2, unique2)),
				dynamicTest("serializing (base)", () -> serializeTest(equalBaseWithHash)),
				dynamicTest("serializing (reactive)", () -> serializeTest(withHash))
		);
		Stream<DynamicNode> all = testHashless ? Stream.concat(hashfull, hashless) : hashfull;
		return dynamicContainer("Reactive Object", all);
	}

	public static void unequalsTest(Object o1, Object o2) {
		assertNotEquals(o1, o2, "Two objects with different content should be equal->false");
	}

	public static <T> void equalsTest(T o1, T o2) {
		assertEquals(o1, o2, "Two objects with the same content should be equal->true");
	}

	public static void hashTest(Object original, Object reactive, Object[] unique) {
		assertTrue(unique.length > 1, "Need at least 2 unique objects for hash check");
		assertEquals(original.hashCode(),
				reactive.hashCode(),
				"Hash code of Original Object (" + original + ") and proxy counterpart should be the same");
		HashMap<Object, Integer> map = new HashMap<>();
		for (int i = 0; i < unique.length; i++) {
			map.put(unique[i], i);
		}
		assertEquals(
				unique.length, map.size(),
				format("Added %s keys to map size afterwards is just %s -> unique key got replaced\n" +
								"Unique Keys : %s"
						, unique.length, map.size(), Arrays.toString(unique))
		);
		for (int i = 0; i < unique.length; i++) {
			assertEquals(i, map.get(unique[i]),
					format("Reading from map returned wrong result (from a different key)\n" +
									"Key '%s'(hash : %s) returned value of key '%s'(hash : %s)\n" +
									"usedKey.equals(valueKey) : %s",
							unique[i], unique[i].hashCode(),
							unique[map.get(unique[i])], unique[map.get(unique[i])].hashCode(),
							unique[i].equals(unique[map.get(unique[i])])));
		}
		map.put(reactive, Integer.MAX_VALUE);
		assertEquals(map.get(original),
				Integer.MAX_VALUE,
				"the unreactive version should act the same as a key as the reactive version");
	}

	public static void serializeTest(Object o) {
		assertDoesNotThrow(() -> {
			byte[] buff;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(o);
			buff = baos.toByteArray();

			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buff));
			Object read = ois.readObject();
			assertEquals(o.hashCode(),
					read.hashCode(),
					"Deserialized Object should have the same hashcode as the original object");
			assertEquals(o, read, "Deserialized Object should yield true on compare with original");
		});
	}

	public static class RProxySubject {
		public static class Base {
			private int i = 0;

			public int getI() {
				return i;
			}

			public void setI(int i) {
				this.i = i;
			}
		}

		public static class WithoutHashEquals extends Base implements ProxySubject, Serializable {
		}

		public static class WithHashEquals extends Base implements ProxySubject, Serializable {
			@Override
			public int hashCode() {
				return Objects.hash(getI());
			}

			public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof RProxySubject.WithHashEquals)) return false;
				RProxySubject.WithHashEquals yeet = (RProxySubject.WithHashEquals) o;
				return getI() == yeet.getI();
			}
		}
	}

	public static class RProxy {
		public static class Base {
			private int i = 0;

			public void add(int i) {
				this.i += i;
			}

			public int getI() {
				return i;
			}

			public void setI(int i) {
				this.i = i;
			}
		}

		public static class WithoutHashEquals extends Base implements Serializable {
			@Override
			public String toString() {
				return "WithoutHashEquals{" + "i=" + getI() +
						'}';
			}
		}

		public static class WithHashEquals extends Base implements Serializable {
			@Override
			public int hashCode() {
				return Objects.hash(getI());
			}

			public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof RProxy.WithHashEquals)) return false;
				RProxy.WithHashEquals yeet = (RProxy.WithHashEquals) o;
				return getI() == yeet.getI();
			}
		}
	}

	@Nested
	@DisplayName("Reactive Object")
	class ReactiveObjectTests {
		@TestFactory
		DynamicContainer testReactiveObjects() {
			RObject.WithHashEquals withHash = new RObject.WithHashEquals();
			RObject.WithHashEquals withHash2 = new RObject.WithHashEquals();
			RObject.WithoutHashEquals withOutHash = new RObject.WithoutHashEquals();
			RObject.WithoutHashEquals withOutHash2 = new RObject.WithoutHashEquals();
			ReactiveWrapper<RObject.WithHashEquals> ro = new ReactiveWrapper<>(withHash);
			RObject.WithoutHashEquals[] unique = new RObject.WithoutHashEquals[5];
			for (int i = 0; i < unique.length; i++) {
				unique[i] = new RObject.WithoutHashEquals();
				unique[i].i = i + 5;
			}
			RObject.WithHashEquals[] unique2 = new RObject.WithHashEquals[5];
			for (int i = 0; i < unique2.length; i++) {
				unique2[i] = new RObject.WithHashEquals();
				unique2[i].i = i + 5;
			}
			return generateTests(withHash,
					withHash2,
					withOutHash,
					withOutHash2,
					withHash2,
					withOutHash2,
					ro,
					ro,
					unique,
					unique2);
		}
	}

	@Nested
	@DisplayName("Reactive Proxy Subject")
	class ReactiveProxySubjectTests {
		@TestFactory
		DynamicContainer testProxySubjects() {
			RProxySubject.WithHashEquals withHash = ProxyCreator.create(new RProxySubject.WithHashEquals());
			RProxySubject.WithHashEquals withHash2 = ProxyCreator.create(new RProxySubject.WithHashEquals());

			RProxySubject.WithoutHashEquals withtOutHash = ProxyCreator.create(new RProxySubject.WithoutHashEquals());
			RProxySubject.WithoutHashEquals withtOutHash2 = ProxyCreator.create(new RProxySubject.WithoutHashEquals());

			RProxySubject.WithHashEquals baseWith = new RProxySubject.WithHashEquals();
			RProxySubject.WithoutHashEquals baseWithout = new RProxySubject.WithoutHashEquals();


			RProxySubject.WithHashEquals changedWith = new RProxySubject.WithHashEquals();
			RProxySubject.WithoutHashEquals changedWithout = new RProxySubject.WithoutHashEquals();

			changedWith.setI(99);
			changedWithout.setI(77);

			RProxySubject.WithoutHashEquals[] unique = new RProxySubject.WithoutHashEquals[5];
			for (int i = 0; i < unique.length; i++) {
				unique[i] = ProxyCreator.create(new RProxySubject.WithoutHashEquals());
				unique[i].setI(unique[i].getI() + i + 100);
			}
			RProxySubject.WithHashEquals[] unique2 = new RProxySubject.WithHashEquals[5];
			for (int i = 0; i < unique2.length; i++) {
				unique2[i] = ProxyCreator.create(new RProxySubject.WithHashEquals());
				unique2[i].setI(unique2[i].getI() + i + 20);
			}
			return generateTests(withHash,
					withHash2,
					withtOutHash,
					withtOutHash2,
					baseWith,
					baseWithout,
					changedWith,
					changedWithout,
					unique,
					unique2);
		}
	}

	@Nested
	@DisplayName("Reactive Proxy")
	class ReactiveProxyTests {
		@TestFactory
		DynamicContainer testProxySubjects() {
			RProxy.WithHashEquals withHash = ProxyCreator.create(new RProxy.WithHashEquals()).getObject();
			RProxy.WithHashEquals withHash2 = ProxyCreator.create(new RProxy.WithHashEquals()).getObject();

			RProxy.WithoutHashEquals withOutHash = ProxyCreator.create(new RProxy.WithoutHashEquals()).getObject();
			RProxy.WithoutHashEquals withOutHash2 = ProxyCreator.create(new RProxy.WithoutHashEquals()).getObject();

			RProxy.WithHashEquals baseWith = new RProxy.WithHashEquals();
			RProxy.WithoutHashEquals baseWithout = new RProxy.WithoutHashEquals();


			RProxy.WithHashEquals changedWith = new RProxy.WithHashEquals();
			RProxy.WithoutHashEquals changedWithout = new RProxy.WithoutHashEquals();

			changedWith.setI(99);
			changedWithout.setI(77);

			RProxy.WithoutHashEquals[] unique = new RProxy.WithoutHashEquals[5];
			for (int i = 0; i < unique.length; i++) {
				unique[i] = ProxyCreator.create(new RProxy.WithoutHashEquals()).getObject();
				unique[i].add(i + 100);
			}
			RProxy.WithHashEquals[] unique2 = new RProxy.WithHashEquals[5];
			for (int i = 0; i < unique2.length; i++) {
				unique2[i] = ProxyCreator.create(new RProxy.WithHashEquals()).getObject();
				unique2[i].add(i + 20);
			}
			System.out.println(Arrays.toString(unique));
			System.out.println(Arrays.toString(unique2));
			return generateTests(withHash,
					withHash2,
					withOutHash,
					withOutHash2,
					baseWith,
					baseWithout,
					changedWith,
					changedWithout,
					unique,
					unique2);
		}
	}
}

class RObject {
	static class WithoutHashEquals extends ReactiveObject implements Serializable {
		public int i = 0;

		public WithoutHashEquals() {
		}
	}

	static class WithHashEquals extends ReactiveObject implements Serializable {
		public int i = 0;

		public WithHashEquals() {
		}

		@Override
		public int hashCode() {
			return Objects.hash(i);
		}

		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof WithHashEquals)) return false;
			WithHashEquals yeet = (WithHashEquals) o;
			return i == yeet.i;
		}
	}
}