package com.niton.reactj.test.lists;

import com.niton.reactj.lists.diff.ListChange;
import com.niton.reactj.lists.diff.ListDiff;
import com.niton.reactj.lists.diff.ListDiffTool;
import org.junit.jupiter.api.*;

import java.util.*;

import static com.niton.reactj.lists.diff.ListOperation.*;
import static java.lang.String.format;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

@DisplayName("ListDiff")
class ListDiffToolTest {
    static final ListDiffTool<Integer> git = new ListDiffTool<>();

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

    void testDiff(
            List<Integer> oldList,
            List<Integer> newList,
            Set<ListChange<Integer>> expectedChanges
    ) {
        SortedSet<ListChange<Integer>> changes = git.diff(oldList, newList);
        assertArrayEquals(
                new TreeSet<>(expectedChanges).toArray(),
                changes.toArray(),
                format("DIFF between %s and %s is wrong", oldList, newList)
        );
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
                                            int           size      = 600;
                                            List<Integer> largeList = new ArrayList<>(size);
                                            for (int i = 0; i < size; i++) {
                                                largeList.add(i);
                                            }
                                            List<Integer> modified = new ArrayList<>(largeList);
                                            modified.remove(50);
                                            modified.add(20, 70);

                                            var diff = git.diff(largeList, modified);

                                            var adapted = new ArrayList<>(largeList);
                                            diff.applyChanges(adapted);
                                            assertArrayEquals(modified.toArray(), adapted.toArray());

                                            assertArrayEquals(
                                                    of(
                                                            new ListChange<>(ADD, 20, 70),
                                                            new ListChange<>(REMOVE, 51, 50)//the add shifts the index by 1
                                                    ).toArray(),
                                                    diff.toArray()
                                            );
                                        }),
                                        dynamicTest("contributed changes", () -> {
                                            int           size      = 300;
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
                                            ListDiff<Integer> diffRes = git.diff(largeList, modified);
                                            diffRes.forEach(System.out::println);
                                            diffRes.applyChanges(largeList);
                                            assertArrayEquals(modified.toArray(), largeList.toArray());
                                        }),
                                        dynamicTest("contributed changes without splicing", () -> {
                                            git.setMinSpliceSize(1024);
                                            int           size      = 300;
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
                                                    git.diff(largeList, modified).toArray()
                                            );
                                        }),
                                        dynamicTest("splicing with changes in first half only", () -> {
                                            git.setMinSpliceSize(132);
                                            git.setMaxScans(64);
                                            int           size      = 300;
                                            List<Integer> largeList = new ArrayList<>(size);
                                            for (int i = 0; i < size; i++) {
                                                largeList.add(i);
                                            }
                                            List<Integer> modified = new ArrayList<>(largeList);
                                            modified.set(0, 999);
                                            modified.remove(50);
                                            modified.add(50, 150);
                                            modified.add(70, 170);
                                            modified.add(100, 200);
                                            var replacedBy299 = modified.set(299, 0);

                                            var diff = git.diff(largeList, modified);
                                            System.out.println(diff);
                                            var adapted = new ArrayList<>(largeList);
                                            diff.applyChanges(adapted);
                                            assertArrayEquals(modified.toArray(), adapted.toArray());

                                            assertArrayEquals(
                                                    of(
                                                            new ListChange<>(REMOVE, 0, 0),
                                                            new ListChange<>(ADD, 0, 999),
                                                            new ListChange<>(REMOVE, 50, 50),
                                                            new ListChange<>(ADD, 50, 150),
                                                            new ListChange<>(ADD, 70, 170),
                                                            new ListChange<>(ADD, 100, 200),
                                                            new ListChange<>(REMOVE, 299, replacedBy299),
                                                            new ListChange<>(ADD, 299, 0)
                                                    ).toArray(),
                                                    diff.toArray()
                                            );
                                        }),
                                        dynamicTest("first last swap", () -> {
                                            git.setMinSpliceSize(5);
                                            testBigSwap();
                                        }),
                                        dynamicTest("first last swap without splicing", this::testBigSwap)
                                )
        );
    }

    private void testBigSwap() {
        List<Integer> original = new ArrayList<>(of(
                1,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                6
        ));
        List<Integer> modified = of(
                6,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
                1
        );

        var diffRes = git.diff(original, modified);
        diffRes.applyChanges(original);
        assertArrayEquals(modified.toArray(), original.toArray());
        assertArrayEquals(of(
                new ListChange<>(REMOVE, 0, 1),
                new ListChange<>(ADD, 0, 6),
                new ListChange<>(REMOVE, 31, 6),
                new ListChange<>(ADD, 31, 1)

        ).toArray(), diffRes.toArray());
    }

    @BeforeEach
    void prepare() {
        git.setMinSpliceSize(126);
    }

    @Test
    @DisplayName("applyChanges()")
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
        var changes = git.diff(original, changed);
        changes.applyChanges(original);
        assertArrayEquals(
                original.toArray(),
                changed.toArray(),
                format(
                        "Applying changes %s to list %s should result in %s",
                        changed,
                        changed,
                        original
                )
        );
    }
}