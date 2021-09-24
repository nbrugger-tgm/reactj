package com.niton.reactj.lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.niton.reactj.lists.Operation.ADD;
import static com.niton.reactj.lists.Operation.REMOVE;
import static java.lang.Math.min;
import static java.util.List.of;

public class ListDiffUtil {
	private static final int MAX_SCANS = 50;

	public static int CUT_SCAN = 32;
	public static int CUT_SIZE = 126;

	private ListDiffUtil() {
	}

	public static void main(String[] args) {
		diff(of(1, 2, 3, 4, 5, 6), of(5, 6, 3, 4, 2, 1), 0);
	}

	private static <T> SortedSet<ListChange<T>> diff(List<T> oldState, List<T> newState, int baseOffset) {
		oldState = new ArrayList<>(oldState);
		newState = new ArrayList<>(newState);
		int offset = cutUnchanged(oldState, newState) + baseOffset;
		int m = oldState.size();
		int n = newState.size();
		TreeSet<ListChange<T>> changes = new TreeSet<>();

		if (handleLinearChanges(oldState, newState, m, n, changes, offset))
			return changes;

		if (min(m, n) > CUT_SIZE) {
			Optional<SortedSet<ListChange<T>>> divRes = divideTask(oldState, newState, m, n, changes, offset);
			if (divRes.isPresent())
				return changes;
		}

		int[][] costs = calcCostMatrix(oldState, newState, m, n);
		findChanges(costs, changes, oldState, newState, offset);

		return changes;
	}

	/**
	 * Removed entries at start & end of the list if they are the same
	 *
	 * @return the amount of entries removed from the head of the list
	 */
	public static <T> int cutUnchanged(List<T> oldState, List<T> newState) {
		//remove common head
		int min = min(oldState.size(), newState.size());
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
		int newEnd = newState.size() - 1;
		int oldEnd = oldState.size() - 1;
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

	private static <T> Optional<SortedSet<ListChange<T>>> divideTask(
			List<T> oldState,
			List<T> newState,
			int m,
			int n,
			SortedSet<ListChange<T>> changes,
			int offset
	) {
		int scans = 0;
		int pos = min(m, n) / 2;
		while (!oldState.get(pos).equals(newState.get(pos))) {
			pos++;
			scans++;
			if (scans > MAX_SCANS || pos >= min(m, n))
				return Optional.empty();
		}
		changes.addAll(diff(oldState.subList(0, pos), newState.subList(0, pos), offset));
		changes.addAll(diff(oldState.subList(pos, m), newState.subList(pos, n), pos + offset));
		return Optional.of(changes);
	}

	private static <T> boolean handleLinearChanges(
			List<T> oldState,
			List<T> newState,
			int m,
			int n,
			SortedSet<ListChange<T>> changes,
			int offset
	) {
		if (m == n && n == 0)
			return true;
		else if (m == 0) {
			AtomicInteger index = new AtomicInteger();
			newState.stream()
					.map(obj -> new ListChange<>(ADD, offset + index.getAndIncrement(), obj))
					.forEach(changes::add);
			return true;
		} else if (n == 0) {
			AtomicInteger index = new AtomicInteger();
			oldState.stream()
					.map(obj -> new ListChange<>(REMOVE, offset + index.getAndIncrement(), obj))
					.forEach(changes::add);
			return true;
		}
		return false;
	}

	private static <T> int[][] calcCostMatrix(List<T> oldState, List<T> newState, int m, int n) {
		int[][] costs = new int[n + 1][m + 1];
		for (int i = 0; i <= n; i++) {
			costs[i][0] = i;
		}
		for (int i = 0; i <= m; i++) {
			costs[0][i] = i;
		}
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= m; j++) {
				calculateCost(costs, i, j, oldState, newState);
			}
		}
		return costs;
	}

	static <T> void findChanges(
			int[][] costs,
			SortedSet<ListChange<T>> changes,
			List<T> oldList,
			List<T> newList,
			int offset
	) {
		int iPos = costs.length - 1;
		int jPos = costs[0].length - 1;
		while (jPos > 0 && iPos > 0) {
			int take = costs[iPos - 1][jPos - 1];
			int remove = costs[iPos][jPos - 1];
			int add = costs[iPos - 1][jPos];
			int current = costs[iPos][jPos];
			if (take < add && take < remove && current == take) {
				iPos--;
				jPos--;
			} else if (remove < add) {
				changes.add(new ListChange<>(REMOVE, offset + (--jPos), oldList.get(jPos)));
			} else {
				changes.add(new ListChange<>(ADD, offset + (--iPos), newList.get(iPos)));
			}
		}
		while (jPos > 0) {
			changes.add(new ListChange<>(REMOVE, offset + (--jPos), oldList.get(jPos)));
		}
		while (iPos > 0) {
			changes.add(new ListChange<>(ADD, offset + (--iPos), newList.get(iPos)));
		}
	}

	private static <T> void calculateCost(int[][] costs, int i, int j, List<T> oldState, List<T> newState) {
		if (i == 0 || j == 0)
			return;
		costs[i][j] = getCost(
				newState.get(i - 1), oldState.get(j - 1),
				costs[i][j - 1],
				costs[i - 1][j],
				costs[i - 1][j - 1]
		);
	}

	private static <T> int getCost(T newElem, T oldElem, int downCost, int rightCost, int originCost) {
		int cost;
		if (newElem.equals(oldElem)) {
			cost = originCost;
		} else {
			int addCost = downCost + 1;
			int removeCost = rightCost + 1;
			cost = min(addCost, removeCost);
		}
		return cost;
	}

	public static <T> SortedSet<ListChange<T>> diff(List<T> oldState, List<T> newState) {
		return diff(oldState, newState, 0);
	}

	/**
	 * Applies all changes to the given list
	 *
	 * @param list    the list to modify
	 * @param changes the changes to apply
	 */
	public static <T> void applyChanges(List<T> list, SortedSet<ListChange<T>> changes) {
		for (ListChange<T> change : changes) {
			change.apply(list);
		}
	}

}
