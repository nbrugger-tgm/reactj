package com.niton.reactj.lists.test;

import com.niton.reactj.lists.ListChange;
import com.niton.reactj.lists.ListDiffUtil;
import org.junit.jupiter.api.*;

import java.util.*;

import static com.niton.reactj.lists.Operation.ADD;
import static com.niton.reactj.lists.Operation.REMOVE;
import static java.lang.String.format;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("ListDiff")
class ListDiffUtilTest {
	@TestFactory
	@DisplayName("diff()")
	List<DynamicNode> diffTests() {
		return of(addDiffTest(), removeDiffTests(), splicingTest());
	}

	private DynamicContainer addDiffTest() {
		return dynamicContainer("ADD", of(
				dynamicTest("to empty", () -> {
					testDiff(of(), of(1, 3, 2), Set.of(
							new ListChange<>(ADD, 0, 1),
							new ListChange<>(ADD, 1, 3),
							new ListChange<>(ADD, 2, 2)
					));
				}),
				dynamicTest("to non-empty", () -> {
					testDiff(of(1), of(1, 3, 2), Set.of(
							new ListChange<>(ADD, 1, 3),
							new ListChange<>(ADD, 2, 2)
					));
				}),
				dynamicTest("multiple", () -> {
					testDiff(of(1, 1), of(1, 1, 1), Set.of(
							new ListChange<>(ADD, 2, 1)
					));
				}),
				dynamicTest("to start", () -> {
					testDiff(of(3), of(1, 2, 3), Set.of(
							new ListChange<>(ADD, 0, 1),
							new ListChange<>(ADD, 1, 2)
					));
				}),
				dynamicTest("with remove", () -> {
					testDiff(of(5, 2, 1, 3), of(2, 1, 3, 5), Set.of(
							new ListChange<>(REMOVE, 0, 5),
							new ListChange<>(ADD, 3, 5)
					));
				}),
				dynamicTest("no change", () -> testDiff(of(1, 1, 1), of(1, 1, 1), Set.of()))
		));
	}

	private DynamicContainer removeDiffTests() {
		return dynamicContainer("REMOVE", of(
				dynamicTest("combined", () -> {
					testDiff(of(1, 4), of(1, 3, 2), Set.of(
							new ListChange<>(REMOVE, 1, 4),
							new ListChange<>(ADD, 1, 3),
							new ListChange<>(ADD, 2, 2)
					));
				}),
				dynamicTest("from end", () -> {
					testDiff(of(1, 3, 2), of(1, 3), Set.of(
							new ListChange<>(REMOVE, 2, 2)
					));
				}),
				dynamicTest("from start", () -> {
					testDiff(of(1, 2, 3), of(3), Set.of(
							new ListChange<>(REMOVE, 0, 1),
							new ListChange<>(REMOVE, 1, 2)
					));
				}),
				dynamicTest("multiple", () -> {
					testDiff(of(1, 1, 1, 1, 1), of(1, 1, 1), Set.of(
							new ListChange<>(REMOVE, 3, 1),
							new ListChange<>(REMOVE, 4, 1)
					));
				}),
				dynamicTest("no change", () -> {
					testDiff(of(1, 1, 1), of(1, 1, 1), Set.of());
				})
				//,addReconstructionTest(ListDiffUtil::removeDiff, List::remove)
		));
	}

	private DynamicContainer splicingTest() {
		return dynamicContainer("splitting", of(
						dynamicTest("center changes", () -> {
							int size = 600;
							List<Integer> largeList = new ArrayList<>(size);
							for (int i = 0; i < size; i++) {
								largeList.add(i);
							}
							List<Integer> modified = new ArrayList<>(largeList);
							modified.remove(50);
							modified.add(20, 70);
							assertArrayEquals(
									of(
											new ListChange<>(ADD, 20, 70),
											new ListChange<>(REMOVE, 50, 50)
									).toArray(),
									ListDiffUtil.diff(largeList, modified).toArray()
							);
						}),
						dynamicTest("contributed changes", () -> {
							int size = 300;
							List<Integer> largeList = new ArrayList<>(size);
							for (int i = 0; i < size; i++) {
								largeList.add(i);
							}
							List<Integer> modified = new ArrayList<>(largeList);
							modified.remove(50);
							modified.add(100, 70);
							modified.add(200, 70);
							modified.add(300, 70);
							//cannot assert exact changes because splicing might return an imperfect route
							SortedSet<ListChange<Integer>> diffRes = ListDiffUtil.diff(largeList, modified);
							diffRes.forEach(System.out::println);
							ListDiffUtil.applyChanges(largeList, diffRes);
							assertArrayEquals(modified.toArray(), largeList.toArray());
						}),
						dynamicTest("contributed changes without splicing", () -> {
							ListDiffUtil.CUT_SIZE = 1024;
							int size = 300;
							List<Integer> largeList = new ArrayList<>(size);
							for (int i = 0; i < size; i++) {
								largeList.add(i);
							}
							List<Integer> modified = new ArrayList<>(largeList);
							modified.remove(50);
							modified.add(100, 70);
							modified.add(200, 70);
							modified.add(300, 70);
							assertArrayEquals(
									of(
											new ListChange<>(REMOVE, 50, 50),
											new ListChange<>(ADD, 100, 70),
											new ListChange<>(ADD, 200, 70),
											new ListChange<>(ADD, 300, 70)
									).toArray(),
									ListDiffUtil.diff(largeList, modified).toArray()
							);
						}),
						dynamicTest("first last swap", () -> {
							ListDiffUtil.CUT_SIZE = 5;
							List<Integer> original = new ArrayList<>(of(1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6));
							List<Integer> modified = of(6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1);
							//cannot assert exact changes because splicing might return an imperfect route
							SortedSet<ListChange<Integer>> diffRes = ListDiffUtil.diff(original, modified);
							ListDiffUtil.applyChanges(original, diffRes);
							assertArrayEquals(modified.toArray(), original.toArray());
							assertArrayEquals(of(
									new ListChange<>(REMOVE, 0, 1),
									new ListChange<>(ADD, 0, 6),
									new ListChange<>(REMOVE, 32, 6),
									new ListChange<>(ADD, 32, 1)

							).toArray(), diffRes.toArray());
						}),
						dynamicTest("first last swap without splicing", () -> {
							List<Integer> original = new ArrayList<>(of(1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6));
							List<Integer> modified = of(6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 1);
							//cannot assert exact changes because splicing might return an imperfect route
							SortedSet<ListChange<Integer>> diffRes = ListDiffUtil.diff(original, modified);
							ListDiffUtil.applyChanges(original, diffRes);
							assertArrayEquals(modified.toArray(), original.toArray());
							assertArrayEquals(of(
									new ListChange<>(REMOVE, 0, 1),
									new ListChange<>(ADD, 0, 6),
									new ListChange<>(REMOVE, 32, 6),
									new ListChange<>(ADD, 32, 1)

							).toArray(), diffRes.toArray());
						})
				)
		);
	}

	<T> void testDiff(List<T> oldList, List<T> newList, Set<ListChange<T>> expectedChanges) {
		SortedSet<ListChange<T>> changes = ListDiffUtil.diff(oldList, newList);
		assertArrayEquals(
				new TreeSet<>(expectedChanges).toArray(),
				changes.toArray(),
				format("DIFF between %s and %s is wrong", oldList, newList)
		);
	}

	@BeforeEach
	void prepare() {
		ListDiffUtil.CUT_SIZE = 126;
	}

	@DisplayName("applyChanges()")
	@Test
	void testReconstruction() {
		List<Integer> original = new ArrayList<>(10);
		for (int i = 0; i < 10; i++) {
			original.add(i);
		}
		List<Integer> changed = new ArrayList<>(original);
		for (int i = 0; i < 5; i++) {
			if (i % 3 == 1)
				changed.remove(i);
			else
				changed.add(i * 2, i + (i % 2));
		}
		SortedSet<ListChange<Integer>> changes = ListDiffUtil.diff(original, changed);
		ListDiffUtil.applyChanges(original, changes);
		assertArrayEquals(original.toArray(), changed.toArray(), format("Applying changes %s to list %s should result in %s", changed, changed, original));
	}
}