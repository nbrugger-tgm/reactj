package com.niton.reactj.utils.misc;

import java.io.PrintStream;

import static java.lang.String.format;

public final class TablePrinter {

	private static final char BORDER_KNOT       = '+';
	private static final char HORIZONTAL_BORDER = '-';
	private static final char VERTICAL_BORDER   = '|';

	private static final String DEFAULT_AS_NULL = "(NULL)";

	private final PrintStream out;
	private final String      asNull;

	public TablePrinter(PrintStream out) {
		this(out, DEFAULT_AS_NULL);
	}

	public TablePrinter(PrintStream out, String asNull) {
		if (out == null) {
			throw new IllegalArgumentException("No print stream provided");
		}
		if (asNull == null) {
			throw new IllegalArgumentException("No NULL-value placeholder provided");
		}
		this.out = out;
		this.asNull = asNull;
	}

	public <T> void print(T[][] table) {
		if (table == null) {
			throw new IllegalArgumentException("No tabular data provided");
		}
		if (table.length == 0) {
			return;
		}
		final int[] widths = new int[getMaxColumns(table)];
		adjustColumnWidths(table, widths);
		printPreparedTable(table, widths, getHorizontalBorder(widths));
	}

	private int getMaxColumns(Object[][] rows) {
		int max = 0;
		for (final Object[] row : rows) {
			if (row != null && row.length > max) {
				max = row.length;
			}
		}
		return max;
	}

	private void adjustColumnWidths(Object[][] rows, int[] widths) {
		for (final Object[] row : rows) {
			if (row != null) {
				for (int c = 0; c < widths.length; c++) {
					final String cv = getCellValue(safeGet(row, c, asNull));
					final int l = cv.length();
					if (widths[c] < l) {
						widths[c] = l;
					}
				}
			}
		}
	}

	private void printPreparedTable(Object[][] table, int[] widths, String horizontalBorder) {
		final int lineLength = horizontalBorder.length();
		out.println(horizontalBorder);
		for (final Object[] row : table) {
			if (row != null) {
				out.println(getRow(row, widths, lineLength));
				out.println(horizontalBorder);
			}
		}
	}

	private String getHorizontalBorder(int[] widths) {
		final StringBuilder builder = new StringBuilder(256);
		builder.append(BORDER_KNOT);
		for (final int w : widths) {
			builder.append(String.valueOf(HORIZONTAL_BORDER).repeat(Math.max(0, w)));
			builder.append(BORDER_KNOT);
		}
		return builder.toString();
	}

	private String getCellValue(Object value) {
		return value == null ? asNull : value.toString();
	}

	private static Object safeGet(Object[] array, int index, String defaultValue) {
		return index < array.length ? array[index] : defaultValue;
	}

	private String getRow(Object[] row, int[] widths, int lineLength) {
		final StringBuilder builder = new StringBuilder(lineLength).append(VERTICAL_BORDER);
		final int maxWidths = widths.length;
		for (int i = 0; i < maxWidths; i++) {
			builder.append(padRight(getCellValue(safeGet(row, i, null)), widths[i])).append(VERTICAL_BORDER);
		}
		return builder.toString();
	}

	private static String padRight(String s, int n) {
		return format("%1$-" + n + "s", s);
	}

}