package com.niton.reactj.lists.diff;

import com.niton.reactj.api.diff.DiffTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import static com.niton.reactj.lists.diff.ListOperation.*;
import static java.lang.Math.min;

/**
 * A tool to generate a git like diff list for lists
 */
public class ListDiffTool<T> implements DiffTool<List<T>, ListChange<T>> {
    /**
     * changes how much effort is put into splicing
     * <p>
     * Max value is {@link #maxSpliceSize}/2
     */
    private int maxScans      = 32;
    /**
     * The size at which a splicing will take place -> the lists will be cut in half
     */
    private int maxSpliceSize = 126;

    /**
     * Removed entries at start & end of the list if they are the same
     *
     * @return the amount of entries removed from the head of the list
     */
    public static <T> int trimEqualEntries(List<T> oldState, List<T> newState) {
        //remove common head
        int min             = min(oldState.size(), newState.size());
        int removeFromStart = 0;
        for (int i = 0; i < min; i++) {
            T n = newState.get(i);
            T o = oldState.get(i);
            if (!n.equals(o))
                break;
            removeFromStart++;
        }
        min -= removeFromStart;
        for (int i = 0; i < removeFromStart; i++) {
            oldState.remove(0);
            newState.remove(0);
        }

        //remove  common tail
        int newEnd        = newState.size() - 1;
        int oldEnd        = oldState.size() - 1;
        int removeFromEnd = 0;
        for (int i = 0; i < min; i++) {
            T n = newState.get(newEnd - i);
            T o = oldState.get(oldEnd - i);
            if (!n.equals(o))
                break;
            removeFromEnd++;
        }
        for (int i = 0; i < removeFromEnd; i++) {
            newState.remove(newEnd--);
            oldState.remove(oldEnd--);
        }
        return removeFromStart;
    }

    /**
     * Handles list that only require one type of modification therefore being linear in O(n)
     *
     * @param list1Size the size of list 1
     * @param list2Size the size of list 2
     * @param changes   the set to add the changes to
     * @param offset    the offset from {@link #trimEqualEntries(List, List)}
     *
     * @return true if linear operations were performed. if false continue with default cubic search
     */
    private static <T> boolean handleLinearChanges(
            List<T> oldState,
            List<T> newState,
            SortedSet<ListChange<T>> changes,
            int offset
    ) {
        int list2Size = newState.size();
        int list1Size = oldState.size();
        if (list1Size == list2Size && list2Size == 0)
            return true;
        else if (list1Size == 0) {
            AtomicInteger index = new AtomicInteger();
            newState.stream()
                    .map(obj -> new ListChange<>(ADD, offset + index.getAndIncrement(), obj))
                    .forEach(changes::add);
            return true;
        } else if (list2Size == 0) {
            AtomicInteger index = new AtomicInteger();
            oldState.stream()
                    .map(obj -> new ListChange<>(REMOVE, offset + index.getAndIncrement(), obj))
                    .forEach(changes::add);
            return true;
        }
        return false;
    }

    /**
     * Calculates a cost matrix for the two lists using a modified version of the
     * Levenshtein,LCS,Wagner–Fischer
     * algorithm
     * <p>
     * The mayor difference is that SUBSTITUITON is not allowed in the matrix returned by this
     * method,
     * only insert/remove operations are performed
     * </p>
     *
     * @param oldState the origin list (base of the changes)
     * @param newState the modified version of the list
     * @param m        the size of the 1. list
     * @param n        the size of the 2. list
     *
     * @return a cost metric.
     *
     * @see <a href="https://en.wikipedia.org/wiki/Wagner–Fischer_algorithm">Wagner Fischer WIKIPEDIA</a>
     * @see <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenshtein distance
     * WIKIPEDIA</a>
     */
    public static <T> int[][] calcCostMatrix(List<T> oldState, List<T> newState, int m, int n) {
        int[][] costs = new int[n + 1][m + 1];
        for (int i = 0; i <= n; i++) {
            costs[i][0] = i;
        }
        for (int i = 0; i <= m; i++) {
            costs[0][i] = i;
        }
        for (int row = 1; row <= n; row++) {
            for (int column = 1; column <= m; column++) {
                calculateCost(costs, row, column, oldState, newState);
            }
        }
        return costs;
    }

    /**
     * Calculates the cost of the given cell and writes it to the cost matrix
     *
     * @param costs    the cost matrix
     * @param row      the row of the cell
     * @param col      the column of the cell
     * @param oldState the origin list (base of the changes)
     * @param newState the modified version of the list
     * @param <T>      the type of the list
     */
    private static <T> void calculateCost(
            int[][] costs,
            int row,
            int col,
            List<T> oldState,
            List<T> newState
    ) {
        if (row == 0 || col == 0)
            return;
        costs[row][col] = getCost(
                newState.get(row - 1), oldState.get(col - 1),
                costs[row][col - 1],
                costs[row - 1][col],
                costs[row - 1][col - 1]
        );
    }

    private static <T> int getCost(
            T newElem,
            T oldElem,
            int downCost,
            int rightCost,
            int originCost
    ) {
        int cost;
        if (newElem.equals(oldElem)) {
            cost = originCost;
        } else {
            int addCost    = downCost + 1;
            int removeCost = rightCost + 1;
            cost = min(addCost, removeCost);
        }
        return cost;
    }

    /**
     * Extracts the diff-log from a cost matrix
     *
     * @param costs   the cost matrix produced by {@link #calcCostMatrix(List, List, int, int)}
     * @param changes the set to put the change history into
     * @param oldList the origin list
     * @param newList the modified list
     * @param offset  the offset (if applicable) from {@link #trimEqualEntries(List, List)}
     */
    public void findChanges(
            int[][] costs,
            SortedSet<ListChange<T>> changes,
            List<T> oldList,
            List<T> newList,
            int offset
    ) {
        int row = costs.length - 1;
        int col = costs[0].length - 1;
        while (col > 0 && row > 0) {
            int take    = costs[row - 1][col - 1];
            int remove  = costs[row][col - 1];
            int add     = costs[row - 1][col];
            int current = costs[row][col];
            if (take < add && take < remove && current == take) {
                row--;
                col--;
            } else if (remove < add) {
                changes.add(new ListChange<>(REMOVE, offset + (row), oldList.get(--col)));
            } else {
                changes.add(new ListChange<>(ADD, offset + (--row), newList.get(row)));
            }
        }
        while (col > 0) {
            changes.add(new ListChange<>(REMOVE, offset + (--col), oldList.get(col)));
        }
        while (row > 0) {
            changes.add(new ListChange<>(ADD, offset + (--row), newList.get(row)));
        }
    }

    /**
     * Calculates the changes between  the two lists.
     *
     * @return a set containing all changes that if applied to the "oldState" will result in "newState"
     */
    @Override
    public ListDiff<T> diff(List<T> oldState, List<T> newState) {
        return diff(oldState, newState, 0);
    }


    public int getMaxScans() {
        return maxScans;
    }

    public void setMaxScans(int maxScans) {
        this.maxScans = maxScans;
    }

    public int getMinSpliceSize() {
        return maxSpliceSize;
    }

    public void setMinSpliceSize(int maxSpliceSize) {
        this.maxSpliceSize = maxSpliceSize;
    }

    private ListDiff<T> diff(List<T> oldState, List<T> newState, int baseOffset) {
        ArrayList<T> oldList = new ArrayList<>(oldState);
        ArrayList<T> newList = new ArrayList<>(newState);

        int offset = trimEqualEntries(oldList, newList) + baseOffset;
        int m      = oldList.size();
        int n      = newList.size();

        ListDiff<T> changes = new ListDiff<>();

        if (handleLinearChanges(oldList, newList, changes, offset))
            return changes;

        if (min(m, n) > maxSpliceSize) {
            Optional<SortedSet<ListChange<T>>> divRes = divideTask(
                    oldList,
                    newList,
                    m,
                    n,
                    changes,
                    offset
            );
            if (divRes.isPresent())
                return changes;
        }

        int[][] costs = calcCostMatrix(oldList, newList, m, n);
        findChanges(costs, changes, oldList, newList, offset);

        return changes;
    }

    /**
     * Splits a diff task in 2 smaller diff tasks.
     * <p>
     * This CAN boost performance for big lists. In the best case 2X performance can be archived
     * </p>
     * Works like this:
     * <p>
     * Splits the given lists in two sublist each and performs a diff on them.
     * If the given lists differe greatly in size this will be inefficient as lists with the size
     * 1000 and 200
     * Will be split into a diff(199,199) and diff(901,1) therefore the changelist will be quite
     * inaccurate.
     * </p>
     * <p>
     * Can be configured with {@link #maxSpliceSize} and {@link #maxScans}
     * </p>
     *
     * @param oldState the old list to split
     * @param newState the new list to split
     * @param m        size of old list
     * @param n        size of new list
     * @param changes  the set to add the changes to
     * @param offset   the offset from {@link #trimEqualEntries(List, List)}
     *
     * @return empty when the set was not touched, or optional including the modified set (you can
     * also use your old ref)
     */
    private Optional<SortedSet<ListChange<T>>> divideTask(
            List<T> oldState,
            List<T> newState,
            int m,
            int n,
            SortedSet<ListChange<T>> changes,
            int offset
    ) {
        int scans = 0;
        int pos   = min(m, n) / 2;
        while (!oldState.get(pos).equals(newState.get(pos))) {
            pos++;
            scans++;
            if (scans > maxScans || pos >= min(m, n))
                return Optional.empty();
        }
        changes.addAll(diff(oldState.subList(0, pos), newState.subList(0, pos), offset));
        changes.addAll(diff(oldState.subList(pos, m), newState.subList(pos, n), pos + offset));
        return Optional.of(changes);
    }
}
