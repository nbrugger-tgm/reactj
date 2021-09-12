package com.niton.reactj.test;

import com.niton.reactj.ReactiveObject;
import com.niton.reactj.ReactiveWrapper;
import com.niton.reactj.proxy.ProxyCreator;
import com.niton.reactj.proxy.ProxySubject;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("equals(), hashCode() and serialization")
public class EqualsAndHashcodeTest {
	/**
	 * If this is true also classes without a {@code equals} or {@code hashCode} implementation will be tested
	 */
	public static boolean testHashless = false;

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
			RProxySubject.WithHashEquals withHash = ProxyCreator.subject(RProxySubject.WithHashEquals.class);
			RProxySubject.WithHashEquals withHash2 = ProxyCreator.subject(RProxySubject.WithHashEquals.class);

			RProxySubject.WithoutHashEquals withtOutHash = ProxyCreator.subject(RProxySubject.WithoutHashEquals.class);
			RProxySubject.WithoutHashEquals withtOutHash2 = ProxyCreator.subject(RProxySubject.WithoutHashEquals.class);

			RProxySubject.WithHashEquals baseWith = new RProxySubject.WithHashEquals();
			RProxySubject.WithoutHashEquals baseWithout = new RProxySubject.WithoutHashEquals();


			RProxySubject.WithHashEquals changedWith = new RProxySubject.WithHashEquals();
			RProxySubject.WithoutHashEquals changedWithout = new RProxySubject.WithoutHashEquals();

			changedWith.i = 99;
			changedWithout.i = 77;

			RProxySubject.WithoutHashEquals[] unique = new RProxySubject.WithoutHashEquals[5];
			for (int i = 0; i < unique.length; i++) {
				unique[i] = ProxyCreator.subject(RProxySubject.WithoutHashEquals.class);
				unique[i].i = i + 100;
			}
			RProxySubject.WithHashEquals[] unique2 = new RProxySubject.WithHashEquals[5];
			for (int i = 0; i < unique2.length; i++) {
				unique2[i] = ProxyCreator.subject(RProxySubject.WithHashEquals.class);
				unique2[i].i = i + 20;
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
			RProxy.WithHashEquals withHash = ProxyCreator.wrapper(RProxy.WithHashEquals.class)
			                                                    .getObject();
			RProxy.WithHashEquals withHash2 = ProxyCreator.wrapper(RProxy.WithHashEquals.class)
			                                                     .getObject();

			RProxy.WithoutHashEquals withOutHash = ProxyCreator.wrapper(RProxy.WithoutHashEquals.class)
			                                                          .getObject();
			RProxy.WithoutHashEquals withOutHash2 = ProxyCreator.wrapper(RProxy.WithoutHashEquals.class)
			                                                           .getObject();

			RProxy.WithHashEquals baseWith = new RProxy.WithHashEquals();
			RProxy.WithoutHashEquals baseWithout = new RProxy.WithoutHashEquals();


			RProxy.WithHashEquals changedWith = new RProxy.WithHashEquals();
			RProxy.WithoutHashEquals changedWithout = new RProxy.WithoutHashEquals();

			changedWith.i = 99;
			changedWithout.i = 77;

			RProxy.WithoutHashEquals[] unique = new RProxy.WithoutHashEquals[5];
			for (int i = 0; i < unique.length; i++) {
				unique[i] = ProxyCreator.wrapper(RProxy.WithoutHashEquals.class).getObject();
				unique[i].i = i + 100;
			}
			RProxy.WithHashEquals[] unique2 = new RProxy.WithHashEquals[5];
			for (int i = 0; i < unique2.length; i++) {
				unique2[i] = ProxyCreator.wrapper(RProxy.WithHashEquals.class).getObject();
				unique2[i].i = i + 20;
			}
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

	public static class RProxySubject {
		public static class WithoutHashEquals implements ProxySubject, Serializable {
			public int i = 0;
		}

		public static class WithHashEquals implements ProxySubject, Serializable {
			public int i = 0;

			@Override
			public int hashCode() {
				return Objects.hash(i);
			}

			public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof RProxySubject.WithHashEquals)) return false;
				RProxySubject.WithHashEquals yeet = (RProxySubject.WithHashEquals) o;
				return i == yeet.i;
			}
		}
	}

	public static class RProxy {
		public static class WithoutHashEquals implements Serializable {
			public int i = 0;
		}

		public static class WithHashEquals implements Serializable {
			public int i = 0;

			@Override
			public int hashCode() {
				return Objects.hash(i);
			}

			public boolean equals(Object o) {
				if (this == o) return true;
				if (!(o instanceof RProxy.WithHashEquals)) return false;
				RProxy.WithHashEquals yeet = (RProxy.WithHashEquals) o;
				return i == yeet.i;
			}
		}
	}

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
				"Hash code of Original Object (" + original + ") and reactive counterpart should be the same");
		HashMap<Object, Integer> map = new HashMap<>();
		for (int i = 0; i < unique.length; i++) {
			map.put(unique[i], i);
		}
		assertEquals(unique.length, map.size());
		for (int i = 0; i < unique.length; i++) {
			assertEquals(i, map.get(unique[i]));
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
					"Deserialized Object should have same hash code as original");
			assertEquals(o, read, "Deserialized Object should yield true on compare with original");
		});
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