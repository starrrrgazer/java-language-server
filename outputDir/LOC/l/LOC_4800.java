package LOC.l;
/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------------
 * DatasetUtils.java
 * -----------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski (bug fix);
 *                   Jonathan Nash (bug fix);
 *                   Richard Atkinson;
 *                   Andreas Schroeder;
 *                   Rafal Skalny (patch 1925366);
 *                   Jerome David (patch 2131001);
 *                   Peter Kolb (patch 2791407);
 *                   Martin Hoeller (patch 2952086);
 *
 */
/**
 * A collection of useful static methods relating to datasets.
 */
public final class DatasetUtils {

    /**
     * Private constructor for non-instanceability.
     */
    private DatasetUtils() {
        // now try to instantiate this ;-)
    }

    /**
     * Calculates the total of all the values in a {@link PieDataset}.  If
     * the dataset contains negative or {@code null} values, they are
     * ignored.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @return The total.
     *
     * @param <K>  the type for the keys.
     */
    public static <K extends Comparable<K>> double calculatePieDatasetTotal(PieDataset<K> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        double totalValue = 0;
        for (K key : dataset.getKeys()) {
            if (key != null) {
                Number value = dataset.getValue(key);
                double v = 0.0;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v > 0) {
                    totalValue = totalValue + v;
                }
            }
        }
        return totalValue;
    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single row.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param rowKey  the row key.
     *
     * @return A pie dataset.
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> PieDataset<C> createPieDatasetForRow(CategoryDataset<R, C> dataset, R rowKey) {
        int row = dataset.getRowIndex(rowKey);
        return createPieDatasetForRow(dataset, row);
    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single row.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param row  the row (zero-based index).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return A pie dataset.
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> PieDataset<C> createPieDatasetForRow(CategoryDataset<R, C> dataset, int row) {
        DefaultPieDataset<C> result = new DefaultPieDataset<>();
        int columnCount = dataset.getColumnCount();
        for (int current = 0; current < columnCount; current++) {
            C columnKey = dataset.getColumnKey(current);
            result.setValue(columnKey, dataset.getValue(row, current));
        }
        return result;
    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single column.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param columnKey  the column key.
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return A pie dataset.
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> PieDataset<R> createPieDatasetForColumn(CategoryDataset<R, C> dataset, C columnKey) {
        int column = dataset.getColumnIndex(columnKey);
        return createPieDatasetForColumn(dataset, column);
    }

    /**
     * Creates a pie dataset from a {@link CategoryDataset} by taking all the
     * values for a single column.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param column  the column (zero-based index).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return A pie dataset.
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> PieDataset<R> createPieDatasetForColumn(CategoryDataset<R, C> dataset, int column) {
        DefaultPieDataset<R> result = new DefaultPieDataset<>();
        int rowCount = dataset.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            R rowKey = dataset.getRowKey(i);
            result.setValue(rowKey, dataset.getValue(i, column));
        }
        return result;
    }

    /**
     * Creates a new pie dataset based on the supplied dataset, but modified
     * by aggregating all the low value items (those whose value is lower
     * than the {@code percentThreshold}) into a single item with the
     * key "Other".
     *
     * @param source  the source dataset ({@code null} not permitted).
     * @param key  a new key for the aggregated items ({@code null} not
     *             permitted).
     * @param minimumPercent  the percent threshold.
     *
     * @param <K>  the type for the data keys.
     *
     * @return The pie dataset with (possibly) aggregated items.
     */
    public static <K extends Comparable<K>> PieDataset<K> createConsolidatedPieDataset(PieDataset<K> source, K key, double minimumPercent) {
        return DatasetUtils.createConsolidatedPieDataset(source, key, minimumPercent, 2);
    }

    /**
     * Creates a new pie dataset based on the supplied dataset, but modified
     * by aggregating all the low value items (those whose value is lower
     * than the {@code percentThreshold}) into a single item.  The
     * aggregated items are assigned the specified key.  Aggregation only
     * occurs if there are at least {@code minItems} items to aggregate.
     *
     * @param source  the source dataset ({@code null} not permitted).
     * @param key  the key to represent the aggregated items.
     * @param minimumPercent  the percent threshold (ten percent is 0.10).
     * @param minItems  only aggregate low values if there are at least this
     *                  many.
     *
     * @param <K>  the type for the data keys.
     *
     * @return The pie dataset with (possibly) aggregated items.
     */
    public static <K extends Comparable<K>> PieDataset<K> createConsolidatedPieDataset(PieDataset<K> source, K key, double minimumPercent, int minItems) {
        DefaultPieDataset<K> result = new DefaultPieDataset<>();
        double total = DatasetUtils.calculatePieDatasetTotal(source);
        //  Iterate and find all keys below threshold percentThreshold
        List<K> keys = source.getKeys();
        List<K> otherKeys = new ArrayList<>();
        Iterator<K> iterator = keys.iterator();
        while (iterator.hasNext()) {
            K currentKey = iterator.next();
            Number dataValue = source.getValue(currentKey);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                if (value / total < minimumPercent) {
                    otherKeys.add(currentKey);
                }
            }
        }
        //  Create new dataset with keys above threshold percentThreshold
        iterator = keys.iterator();
        double otherValue = 0;
        while (iterator.hasNext()) {
            K currentKey = iterator.next();
            Number dataValue = source.getValue(currentKey);
            if (dataValue != null) {
                if (otherKeys.contains(currentKey) && otherKeys.size() >= minItems) {
                    //  Do not add key to dataset
                    otherValue += dataValue.doubleValue();
                } else {
                    //  Add key to dataset
                    result.setValue(currentKey, dataValue);
                }
            }
        }
        //  Add other category if applicable
        if (otherKeys.size() >= minItems) {
            result.setValue(key, otherValue);
        }
        return result;
    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an
     * array (instances of {@code double} are created to represent the
     * data items).
     * <p>
     * Row and column keys are created by appending 0, 1, 2, ... to the
     * supplied prefixes.
     *
     * @param rowKeyPrefix  the row key prefix.
     * @param columnKeyPrefix  the column key prefix.
     * @param data  the data.
     *
     * @return The dataset.
     */
    public static CategoryDataset<String, String> createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, double[][] data) {
        DefaultCategoryDataset<String, String> result = new DefaultCategoryDataset<>();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;
    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in
     * an array.
     * <p>
     * Row and column keys are created by appending 0, 1, 2, ... to the
     * supplied prefixes.
     *
     * @param rowKeyPrefix  the row key prefix.
     * @param columnKeyPrefix  the column key prefix.
     * @param data  the data.
     *
     * @return The dataset.
     */
    public static CategoryDataset<String, String> createCategoryDataset(String rowKeyPrefix, String columnKeyPrefix, Number[][] data) {
        DefaultCategoryDataset<String, String> result = new DefaultCategoryDataset<>();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;
    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in
     * an array (instances of {@code double} are created to represent the
     * data items).
     * <p>
     * Row and column keys are taken from the supplied arrays.
     *
     * @param rowKeys  the row keys ({@code null} not permitted).
     * @param columnKeys  the column keys ({@code null} not permitted).
     * @param data  the data.
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The dataset.
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> CategoryDataset<R, C> createCategoryDataset(R[] rowKeys, C[] columnKeys, double[][] data) {
        Args.nullNotPermitted(rowKeys, "rowKeys");
        Args.nullNotPermitted(columnKeys, "columnKeys");
        if (ArrayUtils.hasDuplicateItems(rowKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'rowKeys'.");
        }
        if (ArrayUtils.hasDuplicateItems(columnKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'columnKeys'.");
        }
        if (rowKeys.length != data.length) {
            throw new IllegalArgumentException("The number of row keys does not match the number of rows in " + "the data array.");
        }
        int columnCount = 0;
        for (int r = 0; r < data.length; r++) {
            columnCount = Math.max(columnCount, data[r].length);
        }
        if (columnKeys.length != columnCount) {
            throw new IllegalArgumentException("The number of column keys does not match the number of " + "columns in the data array.");
        }
        // now do the work...
        DefaultCategoryDataset<R, C> result = new DefaultCategoryDataset<>();
        for (int r = 0; r < data.length; r++) {
            R rowKey = rowKeys[r];
            for (int c = 0; c < data[r].length; c++) {
                C columnKey = columnKeys[c];
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;
    }

    /**
     * Creates a {@link CategoryDataset} by copying the data from the supplied
     * {@link KeyedValues} instance.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     * @param rowData  the row data ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return A dataset.
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> CategoryDataset<R, C> createCategoryDataset(R rowKey, KeyedValues<C> rowData) {
        Args.nullNotPermitted(rowKey, "rowKey");
        Args.nullNotPermitted(rowData, "rowData");
        DefaultCategoryDataset<R, C> result = new DefaultCategoryDataset<>();
        for (int i = 0; i < rowData.getItemCount(); i++) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;
    }

    /**
     * Creates an {@link XYDataset} by sampling the specified function over a
     * fixed range.
     *
     * @param f  the function ({@code null} not permitted).
     * @param start  the start value for the range.
     * @param end  the end value for the range.
     * @param samples  the number of sample points (must be &gt; 1).
     * @param seriesKey  the key to give the resulting series ({@code null} not
     *     permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return A dataset.
     */
    public static <S extends Comparable<S>> XYDataset<S> sampleFunction2D(Function2D f, double start, double end, int samples, S seriesKey) {
        // defer argument checking
        XYSeries<S> series = sampleFunction2DToSeries(f, start, end, samples, seriesKey);
        XYSeriesCollection<S> collection = new XYSeriesCollection<>(series);
        return collection;
    }

    /**
     * Creates an {@link XYSeries} by sampling the specified function over a
     * fixed range.
     *
     * @param f  the function ({@code null} not permitted).
     * @param start  the start value for the range.
     * @param end  the end value for the range.
     * @param samples  the number of sample points (must be &gt; 1).
     * @param seriesKey  the key to give the resulting series
     *                   ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return A series.
     *
     * @since 1.0.13
     */
    public static <S extends Comparable<S>> XYSeries<S> sampleFunction2DToSeries(Function2D f, double start, double end, int samples, S seriesKey) {
        Args.nullNotPermitted(f, "f");
        Args.nullNotPermitted(seriesKey, "seriesKey");
        if (start >= end) {
            throw new IllegalArgumentException("Requires 'start' < 'end'.");
        }
        if (samples < 2) {
            throw new IllegalArgumentException("Requires 'samples' > 1");
        }
        XYSeries<S> series = new XYSeries<>(seriesKey);
        double step = (end - start) / (samples - 1);
        for (int i = 0; i < samples; i++) {
            double x = start + (step * i);
            series.add(x, f.getValue(x));
        }
        return series;
    }

    /**
     * Returns {@code true} if the dataset is empty (or {@code null}),
     * and {@code false} otherwise.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(PieDataset<?> dataset) {
        if (dataset == null) {
            return true;
        }
        int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }
        for (int item = 0; item < itemCount; item++) {
            Number y = dataset.getValue(item);
            if (y != null) {
                double yy = y.doubleValue();
                if (yy > 0.0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the dataset is empty (or {@code null}),
     * and {@code false} otherwise.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(CategoryDataset<?, ?> dataset) {
        if (dataset == null) {
            return true;
        }
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (dataset.getValue(r, c) != null) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if the dataset is empty (or {@code null}),
     * and {@code false} otherwise.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return A boolean.
     */
    public static <S extends Comparable<S>> boolean isEmptyOrNull(XYDataset<S> dataset) {
        if (dataset != null) {
            for (int s = 0; s < dataset.getSeriesCount(); s++) {
                if (dataset.getItemCount(s) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns the range of values in the domain (x-values) of a dataset.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range of values (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range findDomainBounds(XYDataset<S> dataset) {
        return findDomainBounds(dataset, true);
    }

    /**
     * Returns the range of values in the domain (x-values) of a dataset.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  determines whether the x-interval is taken
     *                         into account (only applies if the dataset is an
     *                         {@link IntervalXYDataset}).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range of values (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range findDomainBounds(XYDataset<S> dataset, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result;
        // if the dataset implements DomainInfo, life is easier
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            result = info.getDomainBounds(includeInterval);
        } else {
            result = iterateDomainBounds(dataset, includeInterval);
        }
        return result;
    }

    /**
     * Returns the bounds of the x-values in the specified {@code dataset}
     * taking into account only the visible series and including any x-interval
     * if requested.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the visible series keys ({@code null}
     *     not permitted).
     * @param includeInterval  include the x-interval (if any)?
     *
     * @return The bounds (or {@code null} if the dataset contains no
     *     values).
     *
     * @param <S>  the type for the series keys.
     *
     * @since 1.0.13
     */
    public static <S extends Comparable<S>> Range findDomainBounds(XYDataset<S> dataset, List<S> visibleSeriesKeys, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result;
        if (dataset instanceof XYDomainInfo) {
            @SuppressWarnings("unchecked")
            XYDomainInfo<S> info = (XYDomainInfo) dataset;
            result = info.getDomainBounds(visibleSeriesKeys, includeInterval);
        } else {
            result = iterateToFindDomainBounds(dataset, visibleSeriesKeys, includeInterval);
        }
        return result;
    }

    /**
     * Iterates over the items in an {@link XYDataset} to find
     * the range of x-values.  If the dataset is an instance of
     * {@link IntervalXYDataset}, the starting and ending x-values
     * will be used for the bounds calculation.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range iterateDomainBounds(XYDataset<S> dataset) {
        return iterateDomainBounds(dataset, true);
    }

    /**
     * Iterates over the items in an {@link XYDataset} to find
     * the range of x-values.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines, for an
     *          {@link IntervalXYDataset}, whether the x-interval or just the
     *          x-value is used to determine the overall range.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range iterateDomainBounds(XYDataset<S> dataset, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        double lvalue, uvalue;
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            @SuppressWarnings("unchecked")
            IntervalXYDataset<S> intervalXYData = (IntervalXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value = intervalXYData.getXValue(series, item);
                    lvalue = intervalXYData.getStartXValue(series, item);
                    uvalue = intervalXYData.getEndXValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                        maximum = Math.max(maximum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        minimum = Math.min(minimum, uvalue);
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        }
        if (minimum > maximum) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Returns the range of values in the range for the dataset.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range findRangeBounds(CategoryDataset<R, C> dataset) {
        return findRangeBounds(dataset, true);
    }

    /**
     * Returns the range of values in the range for the dataset.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range findRangeBounds(CategoryDataset<R, C> dataset, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            result = info.getRangeBounds(includeInterval);
        } else {
            result = iterateRangeBounds(dataset, includeInterval);
        }
        return result;
    }

    /**
     * Finds the bounds of the y-values in the specified dataset, including
     * only those series that are listed in visibleSeriesKeys.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the keys for the visible series
     *     ({@code null} not permitted).
     * @param includeInterval  include the y-interval (if the dataset has a
     *     y-interval).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The data bounds.
     *
     * @since 1.0.13
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range findRangeBounds(CategoryDataset<R, C> dataset, List<R> visibleSeriesKeys, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result;
        if (dataset instanceof CategoryRangeInfo) {
            CategoryRangeInfo info = (CategoryRangeInfo) dataset;
            result = info.getRangeBounds(visibleSeriesKeys, includeInterval);
        } else {
            result = iterateToFindRangeBounds(dataset, visibleSeriesKeys, includeInterval);
        }
        return result;
    }

    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the {@link #findDomainBounds(XYDataset)} method.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range findRangeBounds(XYDataset<S> dataset) {
        return findRangeBounds(dataset, true);
    }

    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the {@link #findDomainBounds(XYDataset, boolean)}
     * method.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range findRangeBounds(XYDataset<S> dataset, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            result = info.getRangeBounds(includeInterval);
        } else {
            result = iterateRangeBounds(dataset, includeInterval);
        }
        return result;
    }

    /**
     * Finds the bounds of the y-values in the specified dataset, including
     * only those series that are listed in visibleSeriesKeys, and those items
     * whose x-values fall within the specified range.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the keys for the visible series
     *     ({@code null} not permitted).
     * @param xRange  the x-range ({@code null} not permitted).
     * @param includeInterval  include the y-interval (if the dataset has a
     *     y-interval).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The data bounds.
     *
     * @since 1.0.13
     */
    public static <S extends Comparable<S>> Range findRangeBounds(XYDataset<S> dataset, List<S> visibleSeriesKeys, Range xRange, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result;
        if (dataset instanceof XYRangeInfo) {
            XYRangeInfo info = (XYRangeInfo) dataset;
            result = info.getRangeBounds(visibleSeriesKeys, xRange, includeInterval);
        } else {
            result = iterateToFindRangeBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
        return result;
    }

    /**
     * Iterates over the data item of the category dataset to find
     * the range bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @return The range (possibly {@code null}).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @since 1.0.10
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range iterateRangeBounds(CategoryDataset<R, C> dataset) {
        return iterateRangeBounds(dataset, true);
    }

    /**
     * Iterates over the data item of the category dataset to find
     * the range bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     *
     * @return The range (possibly {@code null}).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @since 1.0.10
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range iterateRangeBounds(CategoryDataset<R, C> dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (includeInterval && dataset instanceof IntervalCategoryDataset) {
            // handle the special case where the dataset has y-intervals that
            // we want to measure
            @SuppressWarnings("unchecked")
            IntervalCategoryDataset<R, C> icd = (IntervalCategoryDataset) dataset;
            Number value, lvalue, uvalue;
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    value = icd.getValue(row, column);
                    double v;
                    if ((value != null) && !Double.isNaN(v = value.doubleValue())) {
                        minimum = Math.min(v, minimum);
                        maximum = Math.max(v, maximum);
                    }
                    lvalue = icd.getStartValue(row, column);
                    if (lvalue != null && !Double.isNaN(v = lvalue.doubleValue())) {
                        minimum = Math.min(v, minimum);
                        maximum = Math.max(v, maximum);
                    }
                    uvalue = icd.getEndValue(row, column);
                    if (uvalue != null && !Double.isNaN(v = uvalue.doubleValue())) {
                        minimum = Math.min(v, minimum);
                        maximum = Math.max(v, maximum);
                    }
                }
            }
        } else {
            // handle the standard case (plain CategoryDataset)
            for (int row = 0; row < rowCount; row++) {
                for (int column = 0; column < columnCount; column++) {
                    Number value = dataset.getValue(row, column);
                    if (value != null) {
                        double v = value.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(minimum, v);
                            maximum = Math.max(maximum, v);
                        }
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Iterates over the data item of the category dataset to find
     * the range bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     * @param visibleSeriesKeys  the visible series keys.
     *
     * @return The range (possibly {@code null}).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @since 1.0.13
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range iterateToFindRangeBounds(CategoryDataset<R, C> dataset, List<R> visibleSeriesKeys, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Args.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int columnCount = dataset.getColumnCount();
        if (includeInterval && dataset instanceof BoxAndWhiskerCategoryDataset) {
            // handle special case of BoxAndWhiskerDataset
            @SuppressWarnings("unchecked")
            BoxAndWhiskerCategoryDataset<R, C> bx = (BoxAndWhiskerCategoryDataset) dataset;
            for (R seriesKey : visibleSeriesKeys) {
                int series = dataset.getRowIndex(seriesKey);
                int itemCount = dataset.getColumnCount();
                for (int item = 0; item < itemCount; item++) {
                    Number lvalue = bx.getMinRegularValue(series, item);
                    if (lvalue == null) {
                        lvalue = bx.getValue(series, item);
                    }
                    Number uvalue = bx.getMaxRegularValue(series, item);
                    if (uvalue == null) {
                        uvalue = bx.getValue(series, item);
                    }
                    if (lvalue != null) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
        } else if (includeInterval && dataset instanceof IntervalCategoryDataset) {
            // handle the special case where the dataset has y-intervals that
            // we want to measure
            @SuppressWarnings("unchecked")
            IntervalCategoryDataset<R, C> icd = (IntervalCategoryDataset) dataset;
            Number lvalue, uvalue;
            for (R seriesKey : visibleSeriesKeys) {
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; column++) {
                    lvalue = icd.getStartValue(series, column);
                    uvalue = icd.getEndValue(series, column);
                    if (lvalue != null && !Double.isNaN(lvalue.doubleValue())) {
                        minimum = Math.min(minimum, lvalue.doubleValue());
                    }
                    if (uvalue != null && !Double.isNaN(uvalue.doubleValue())) {
                        maximum = Math.max(maximum, uvalue.doubleValue());
                    }
                }
            }
        } else if (includeInterval && dataset instanceof MultiValueCategoryDataset) {
            // handle the special case where the dataset has y-intervals that
            // we want to measure
            @SuppressWarnings("unchecked")
            MultiValueCategoryDataset<R, C> mvcd = (MultiValueCategoryDataset) dataset;
            for (R seriesKey : visibleSeriesKeys) {
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; column++) {
                    List<? extends Number> values = mvcd.getValues(series, column);
                    for (Number n : values) {
                        double v = n.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(minimum, v);
                            maximum = Math.max(maximum, v);
                        }
                    }
                }
            }
        } else if (includeInterval && dataset instanceof StatisticalCategoryDataset) {
            // handle the special case where the dataset has y-intervals that
            // we want to measure
            @SuppressWarnings("unchecked")
            StatisticalCategoryDataset<R, C> scd = (StatisticalCategoryDataset) dataset;
            for (R seriesKey : visibleSeriesKeys) {
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; column++) {
                    Number meanN = scd.getMeanValue(series, column);
                    if (meanN != null) {
                        double std = 0.0;
                        Number stdN = scd.getStdDevValue(series, column);
                        if (stdN != null) {
                            std = stdN.doubleValue();
                            if (Double.isNaN(std)) {
                                std = 0.0;
                            }
                        }
                        double mean = meanN.doubleValue();
                        if (!Double.isNaN(mean)) {
                            minimum = Math.min(minimum, mean - std);
                            maximum = Math.max(maximum, mean + std);
                        }
                    }
                }
            }
        } else {
            // handle the standard case (plain CategoryDataset)
            for (R seriesKey : visibleSeriesKeys) {
                int series = dataset.getRowIndex(seriesKey);
                for (int column = 0; column < columnCount; column++) {
                    Number value = dataset.getValue(series, column);
                    if (value != null) {
                        double v = value.doubleValue();
                        if (!Double.isNaN(v)) {
                            minimum = Math.min(minimum, v);
                            maximum = Math.max(maximum, v);
                        }
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Iterates over the data item of the xy dataset to find
     * the range bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     *
     * @since 1.0.10
     */
    public static <S extends Comparable<S>> Range iterateRangeBounds(XYDataset<S> dataset) {
        return iterateRangeBounds(dataset, true);
    }

    /**
     * Iterates over the data items of the xy dataset to find
     * the range bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines, for an
     *          {@link IntervalXYDataset}, whether the y-interval or just the
     *          y-value is used to determine the overall range.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     *
     * @since 1.0.10
     */
    public static <S extends Comparable<S>> Range iterateRangeBounds(XYDataset<S> dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        // handle three cases by dataset type
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            // handle special case of IntervalXYDataset
            @SuppressWarnings("unchecked")
            IntervalXYDataset<S> ixyd = (IntervalXYDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value = ixyd.getYValue(series, item);
                    double lvalue = ixyd.getStartYValue(series, item);
                    double uvalue = ixyd.getEndYValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                        maximum = Math.max(maximum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        minimum = Math.min(minimum, uvalue);
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else if (includeInterval && dataset instanceof OHLCDataset) {
            // handle special case of OHLCDataset
            OHLCDataset ohlc = (OHLCDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double lvalue = ohlc.getLowValue(series, item);
                    double uvalue = ohlc.getHighValue(series, item);
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else {
            // standard case - plain XYDataset
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value = dataset.getYValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Returns the range of values in the z-dimension for the dataset. This
     * method is the partner for the {@link #findRangeBounds(XYDataset)}
     * and {@link #findDomainBounds(XYDataset)} methods.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range findZBounds(XYZDataset<S> dataset) {
        return findZBounds(dataset, true);
    }

    /**
     * Returns the range of values in the z-dimension for the dataset.  This
     * method is the partner for the
     * {@link #findRangeBounds(XYDataset, boolean)} and
     * {@link #findDomainBounds(XYDataset, boolean)} methods.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *                         z-interval is taken into account.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range findZBounds(XYZDataset<S> dataset, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result = iterateZBounds(dataset, includeInterval);
        return result;
    }

    /**
     * Finds the bounds of the z-values in the specified dataset, including
     * only those series that are listed in visibleSeriesKeys, and those items
     * whose x-values fall within the specified range.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the keys for the visible series
     *     ({@code null} not permitted).
     * @param xRange  the x-range ({@code null} not permitted).
     * @param includeInterval  include the z-interval (if the dataset has a
     *     z-interval).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The data bounds.
     */
    public static <S extends Comparable<S>> Range findZBounds(XYZDataset<S> dataset, List<S> visibleSeriesKeys, Range xRange, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result = iterateToFindZBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
        return result;
    }

    /**
     * Iterates over the data item of the xyz dataset to find
     * the z-dimension bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range iterateZBounds(XYZDataset<S> dataset) {
        return iterateZBounds(dataset, true);
    }

    /**
     * Iterates over the data items of the xyz dataset to find
     * the z-dimension bounds.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param includeInterval  include the z-interval (if the dataset has a
     *     z-interval.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range iterateZBounds(XYZDataset<S> dataset, boolean includeInterval) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        if (includeInterval && dataset instanceof IntervalXYZDataset) {
            @SuppressWarnings("unchecked")
            IntervalXYZDataset<S> intervalDataset = (IntervalXYZDataset) dataset;
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    // first apply the z-value itself
                    double value = dataset.getZValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                    Number start = intervalDataset.getStartZValue(series, item);
                    if (start != null && !Double.isNaN(start.doubleValue())) {
                        minimum = Math.min(minimum, start.doubleValue());
                        maximum = Math.max(maximum, start.doubleValue());
                    }
                    Number end = intervalDataset.getEndZValue(series, item);
                    if (end != null && !Double.isNaN(end.doubleValue())) {
                        minimum = Math.min(minimum, end.doubleValue());
                        maximum = Math.max(maximum, end.doubleValue());
                    }
                }
            }
        } else {
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value = dataset.getZValue(series, item);
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                        maximum = Math.max(maximum, value);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Returns the range of x-values in the specified dataset for the
     * data items belonging to the visible series.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the visible series keys ({@code null} not
     *     permitted).
     * @param includeInterval  a flag that determines whether the
     *     y-interval for the dataset is included (this only applies if the
     *     dataset is an instance of IntervalXYDataset).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The x-range (possibly {@code null}).
     *
     * @since 1.0.13
     */
    public static <S extends Comparable<S>> Range iterateToFindDomainBounds(XYDataset<S> dataset, List<S> visibleSeriesKeys, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Args.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        if (includeInterval && dataset instanceof IntervalXYDataset) {
            // handle special case of IntervalXYDataset
            @SuppressWarnings("unchecked")
            IntervalXYDataset<S> ixyd = (IntervalXYDataset) dataset;
            for (S seriesKey : visibleSeriesKeys) {
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double xvalue = ixyd.getXValue(series, item);
                    double lvalue = ixyd.getStartXValue(series, item);
                    double uvalue = ixyd.getEndXValue(series, item);
                    if (!Double.isNaN(xvalue)) {
                        minimum = Math.min(minimum, xvalue);
                        maximum = Math.max(maximum, xvalue);
                    }
                    if (!Double.isNaN(lvalue)) {
                        minimum = Math.min(minimum, lvalue);
                    }
                    if (!Double.isNaN(uvalue)) {
                        maximum = Math.max(maximum, uvalue);
                    }
                }
            }
        } else {
            // standard case - plain XYDataset
            for (S seriesKey : visibleSeriesKeys) {
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double x = dataset.getXValue(series, item);
                    if (!Double.isNaN(x)) {
                        minimum = Math.min(minimum, x);
                        maximum = Math.max(maximum, x);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Returns the range of y-values in the specified dataset for the
     * data items belonging to the visible series and with x-values in the
     * given range.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the visible series keys ({@code null} not
     *     permitted).
     * @param xRange  the x-range ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *     y-interval for the dataset is included (this only applies if the
     *     dataset is an instance of IntervalXYDataset).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The y-range (possibly {@code null}).
     *
     * @since 1.0.13
     */
    public static <S extends Comparable<S>> Range iterateToFindRangeBounds(XYDataset<S> dataset, List<S> visibleSeriesKeys, Range xRange, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Args.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        Args.nullNotPermitted(xRange, "xRange");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        // handle three cases by dataset type
        if (includeInterval && dataset instanceof OHLCDataset) {
            // handle special case of OHLCDataset
            OHLCDataset ohlc = (OHLCDataset) dataset;
            for (S seriesKey : visibleSeriesKeys) {
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double x = ohlc.getXValue(series, item);
                    if (xRange.contains(x)) {
                        double lvalue = ohlc.getLowValue(series, item);
                        double uvalue = ohlc.getHighValue(series, item);
                        if (!Double.isNaN(lvalue)) {
                            minimum = Math.min(minimum, lvalue);
                        }
                        if (!Double.isNaN(uvalue)) {
                            maximum = Math.max(maximum, uvalue);
                        }
                    }
                }
            }
        } else if (includeInterval && dataset instanceof BoxAndWhiskerXYDataset) {
            // handle special case of BoxAndWhiskerXYDataset
            @SuppressWarnings("unchecked")
            BoxAndWhiskerXYDataset<S> bx = (BoxAndWhiskerXYDataset) dataset;
            for (S seriesKey : visibleSeriesKeys) {
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double x = bx.getXValue(series, item);
                    if (xRange.contains(x)) {
                        Number lvalue = bx.getMinRegularValue(series, item);
                        Number uvalue = bx.getMaxRegularValue(series, item);
                        if (lvalue != null) {
                            minimum = Math.min(minimum, lvalue.doubleValue());
                        }
                        if (uvalue != null) {
                            maximum = Math.max(maximum, uvalue.doubleValue());
                        }
                    }
                }
            }
        } else if (includeInterval && dataset instanceof IntervalXYDataset) {
            // handle special case of IntervalXYDataset
            @SuppressWarnings("unchecked")
            IntervalXYDataset<S> ixyd = (IntervalXYDataset) dataset;
            for (S seriesKey : visibleSeriesKeys) {
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double x = ixyd.getXValue(series, item);
                    if (xRange.contains(x)) {
                        double yvalue = ixyd.getYValue(series, item);
                        double lvalue = ixyd.getStartYValue(series, item);
                        double uvalue = ixyd.getEndYValue(series, item);
                        if (!Double.isNaN(yvalue)) {
                            minimum = Math.min(minimum, yvalue);
                            maximum = Math.max(maximum, yvalue);
                        }
                        if (!Double.isNaN(lvalue)) {
                            minimum = Math.min(minimum, lvalue);
                        }
                        if (!Double.isNaN(uvalue)) {
                            maximum = Math.max(maximum, uvalue);
                        }
                    }
                }
            }
        } else {
            // standard case - plain XYDataset
            for (S seriesKey : visibleSeriesKeys) {
                int series = dataset.indexOf(seriesKey);
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double x = dataset.getXValue(series, item);
                    double y = dataset.getYValue(series, item);
                    if (xRange.contains(x)) {
                        if (!Double.isNaN(y)) {
                            minimum = Math.min(minimum, y);
                            maximum = Math.max(maximum, y);
                        }
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Returns the range of z-values in the specified dataset for the
     * data items belonging to the visible series and with x-values in the
     * given range.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param visibleSeriesKeys  the visible series keys ({@code null} not
     *     permitted).
     * @param xRange  the x-range ({@code null} not permitted).
     * @param includeInterval  a flag that determines whether the
     *     z-interval for the dataset is included (this only applies if the
     *     dataset has an interval, which is currently not supported).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The y-range (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Range iterateToFindZBounds(XYZDataset<S> dataset, List<S> visibleSeriesKeys, Range xRange, boolean includeInterval) {
        Args.nullNotPermitted(dataset, "dataset");
        Args.nullNotPermitted(visibleSeriesKeys, "visibleSeriesKeys");
        Args.nullNotPermitted(xRange, "xRange");
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        for (S seriesKey : visibleSeriesKeys) {
            int series = dataset.indexOf(seriesKey);
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double x = dataset.getXValue(series, item);
                double z = dataset.getZValue(series, item);
                if (xRange.contains(x)) {
                    if (!Double.isNaN(z)) {
                        minimum = Math.min(minimum, z);
                        maximum = Math.max(maximum, z);
                    }
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        } else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Finds the minimum domain (or X) value for the specified dataset.  This
     * is easy if the dataset implements the {@link DomainInfo} interface (a
     * good idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns {@code null} if all the data values in the dataset are
     * {@code null}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The minimum value (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Number findMinimumDomainValue(XYDataset<S> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        Number result;
        // if the dataset implements DomainInfo, life is easy
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getDomainLowerBound(true);
        } else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        @SuppressWarnings("unchecked")
                        IntervalXYDataset<S> intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartXValue(series, item);
                    } else {
                        value = dataset.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                result = null;
            } else {
                result = minimum;
            }
        }
        return result;
    }

    /**
     * Returns the maximum domain value for the specified dataset.  This is
     * easy if the dataset implements the {@link DomainInfo} interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * {@code null} if all the data values in the dataset are
     * {@code null}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The maximum value (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Number findMaximumDomainValue(XYDataset<S> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        Number result;
        // if the dataset implements DomainInfo, life is easy
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getDomainUpperBound(true);
        } else // hasn't implemented DomainInfo, so iterate...
        {
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        @SuppressWarnings("unchecked")
                        IntervalXYDataset<S> intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndXValue(series, item);
                    } else {
                        value = dataset.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                result = null;
            } else {
                result = maximum;
            }
        }
        return result;
    }

    /**
     * Returns the minimum range value for the specified dataset.  This is
     * easy if the dataset implements the {@link RangeInfo} interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * {@code null} if all the data values in the dataset are
     * {@code null}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The minimum value (possibly {@code null}).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Number findMinimumRangeValue(CategoryDataset<R, C> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getRangeLowerBound(true);
        } else // hasn't implemented RangeInfo, so we'll have to iterate...
        {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        @SuppressWarnings("unchecked")
                        IntervalCategoryDataset<R, C> icd = (IntervalCategoryDataset) dataset;
                        value = icd.getStartValue(series, item);
                    } else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            } else {
                return minimum;
            }
        }
    }

    /**
     * Returns the minimum range value for the specified dataset.  This is
     * easy if the dataset implements the {@link RangeInfo} interface (a good
     * idea if there is an efficient way to determine the minimum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * {@code null} if all the data values in the dataset are
     * {@code null}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The minimum value (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Number findMinimumRangeValue(XYDataset<S> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getRangeLowerBound(true);
        } else // hasn't implemented RangeInfo, so we'll have to iterate...
        {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        @SuppressWarnings("unchecked")
                        IntervalXYDataset<S> intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartYValue(series, item);
                    } else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getLowValue(series, item);
                    } else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            } else {
                return minimum;
            }
        }
    }

    /**
     * Returns the maximum range value for the specified dataset.  This is easy
     * if the dataset implements the {@link RangeInfo} interface (a good idea
     * if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * {@code null} if all the data values are {@code null}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The maximum value (possibly {@code null}).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Number findMaximumRangeValue(CategoryDataset<R, C> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getRangeUpperBound(true);
        } else // hasn't implemented RangeInfo, so we'll have to iterate...
        {
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        @SuppressWarnings("unchecked")
                        IntervalCategoryDataset<R, C> icd = (IntervalCategoryDataset) dataset;
                        value = icd.getEndValue(series, item);
                    } else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            } else {
                return maximum;
            }
        }
    }

    /**
     * Returns the maximum range value for the specified dataset.  This is
     * easy if the dataset implements the {@link RangeInfo} interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.  Returns
     * {@code null} if all the data values are {@code null}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The maximum value (possibly {@code null}).
     */
    public static <S extends Comparable<S>> Number findMaximumRangeValue(XYDataset<S> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getRangeUpperBound(true);
        } else // hasn't implemented RangeInfo, so we'll have to iterate...
        {
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        @SuppressWarnings("unchecked")
                        IntervalXYDataset<S> intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndYValue(series, item);
                    } else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getHighValue(series, item);
                    } else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            } else {
                return maximum;
            }
        }
    }

    /**
     * Returns the minimum and maximum values for the dataset's range
     * (y-values), assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The range ({@code null} if the dataset contains no values).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range findStackedRangeBounds(CategoryDataset<R, C> dataset) {
        return findStackedRangeBounds(dataset, 0.0);
    }

    /**
     * Returns the minimum and maximum values for the dataset's range
     * (y-values), assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param base  the base value for the bars.
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The range ({@code null} if the dataset contains no values).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range findStackedRangeBounds(CategoryDataset<R, C> dataset, double base) {
        Args.nullNotPermitted(dataset, "dataset");
        Range result = null;
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    double value = number.doubleValue();
                    if (value > 0.0) {
                        positive = positive + value;
                    }
                    if (value < 0.0) {
                        negative = negative + value;
                        // '+', remember value is negative
                    }
                }
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        if (minimum <= maximum) {
            result = new Range(minimum, maximum);
        }
        return result;
    }

    /**
     * Returns the minimum and maximum values for the dataset's range
     * (y-values), assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset.
     * @param map  a structure that maps series to groups.
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     * @param <G>  the type for the group keys.
     *
     * @return The value range ({@code null} if the dataset contains no
     *         values).
     */
    public static <R extends Comparable<R>, C extends Comparable<C>, G extends Comparable<G>> Range findStackedRangeBounds(CategoryDataset<R, C> dataset, KeyToGroupMap<R, G> map) {
        Args.nullNotPermitted(dataset, "dataset");
        boolean hasValidData = false;
        Range result = null;
        // create an array holding the group indices for each series...
        int[] groupIndex = new int[dataset.getRowCount()];
        for (int i = 0; i < dataset.getRowCount(); i++) {
            groupIndex[i] = map.getGroupIndex(map.getGroup(dataset.getRowKey(i)));
        }
        // minimum and maximum for each group...
        int groupCount = map.getGroupCount();
        double[] minimum = new double[groupCount];
        double[] maximum = new double[groupCount];
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double[] positive = new double[groupCount];
            double[] negative = new double[groupCount];
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    hasValidData = true;
                    double value = number.doubleValue();
                    if (value > 0.0) {
                        positive[groupIndex[series]] = positive[groupIndex[series]] + value;
                    }
                    if (value < 0.0) {
                        negative[groupIndex[series]] = negative[groupIndex[series]] + value;
                        // '+', remember value is negative
                    }
                }
            }
            for (int g = 0; g < groupCount; g++) {
                minimum[g] = Math.min(minimum[g], negative[g]);
                maximum[g] = Math.max(maximum[g], positive[g]);
            }
        }
        if (hasValidData) {
            for (int j = 0; j < groupCount; j++) {
                result = Range.combine(result, new Range(minimum[j], maximum[j]));
            }
        }
        return result;
    }

    /**
     * Returns the minimum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The minimum value.
     *
     * @see #findMaximumStackedRangeValue(CategoryDataset)
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Number findMinimumStackedRangeValue(CategoryDataset<R, C> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        Number result = null;
        boolean hasValidData = false;
        double minimum = 0.0;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double total = 0.0;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    hasValidData = true;
                    double value = number.doubleValue();
                    if (value < 0.0) {
                        total = total + value;
                        // '+', remember value is negative
                    }
                }
            }
            minimum = Math.min(minimum, total);
        }
        if (hasValidData) {
            result = minimum;
        }
        return result;
    }

    /**
     * Returns the maximum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The maximum value (possibly {@code null}).
     *
     * @see #findMinimumStackedRangeValue(CategoryDataset)
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Number findMaximumStackedRangeValue(CategoryDataset<R, C> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        Number result = null;
        boolean hasValidData = false;
        double maximum = 0.0;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double total = 0.0;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    hasValidData = true;
                    double value = number.doubleValue();
                    if (value > 0.0) {
                        total = total + value;
                    }
                }
            }
            maximum = Math.max(maximum, total);
        }
        if (hasValidData) {
            result = maximum;
        }
        return result;
    }

    /**
     * Returns the minimum and maximum values for the dataset's range,
     * assuming that the series are stacked.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range ([0.0, 0.0] if the dataset contains no values).
     */
    public static <S extends Comparable<S>> Range findStackedRangeBounds(TableXYDataset<S> dataset) {
        return findStackedRangeBounds(dataset, 0.0);
    }

    /**
     * Returns the minimum and maximum values for the dataset's range,
     * assuming that the series are stacked, using the specified base value.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param base  the base value.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The range ({@code null} if the dataset contains no values).
     */
    public static <S extends Comparable<S>> Range findStackedRangeBounds(TableXYDataset<S> dataset, double base) {
        Args.nullNotPermitted(dataset, "dataset");
        double minimum = base;
        double maximum = base;
        for (int itemNo = 0; itemNo < dataset.getItemCount(); itemNo++) {
            double positive = base;
            double negative = base;
            int seriesCount = dataset.getSeriesCount();
            for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
                double y = dataset.getYValue(seriesNo, itemNo);
                if (!Double.isNaN(y)) {
                    if (y > 0.0) {
                        positive += y;
                    } else {
                        negative += y;
                    }
                }
            }
            if (positive > maximum) {
                maximum = positive;
            }
            if (negative < minimum) {
                minimum = negative;
            }
        }
        if (minimum <= maximum) {
            return new Range(minimum, maximum);
        } else {
            return null;
        }
    }

    /**
     * Calculates the total for the y-values in all series for a given item
     * index.
     *
     * @param dataset  the dataset.
     * @param item  the item index.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The total.
     *
     * @since 1.0.5
     */
    public static <S extends Comparable<S>> double calculateStackTotal(TableXYDataset<S> dataset, int item) {
        double total = 0.0;
        int seriesCount = dataset.getSeriesCount();
        for (int s = 0; s < seriesCount; s++) {
            double value = dataset.getYValue(s, item);
            if (!Double.isNaN(value)) {
                total = total + value;
            }
        }
        return total;
    }

    /**
     * Calculates the range of values for a dataset where each item is the
     * running total of the items for the current series.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @param <R>  the type for the row keys.
     * @param <C>  the type for the column keys.
     *
     * @return The range.
     *
     * @see #findRangeBounds(CategoryDataset)
     */
    public static <R extends Comparable<R>, C extends Comparable<C>> Range findCumulativeRangeBounds(CategoryDataset<R, C> dataset) {
        Args.nullNotPermitted(dataset, "dataset");
        // we'll set this to false if there is at
        boolean allItemsNull = true;
        // least one non-null data item...
        double minimum = 0.0;
        double maximum = 0.0;
        for (int row = 0; row < dataset.getRowCount(); row++) {
            double runningTotal = 0.0;
            for (int column = 0; column <= dataset.getColumnCount() - 1; column++) {
                Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    double value = n.doubleValue();
                    if (!Double.isNaN(value)) {
                        runningTotal = runningTotal + value;
                        minimum = Math.min(minimum, runningTotal);
                        maximum = Math.max(maximum, runningTotal);
                    }
                }
            }
        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        } else {
            return null;
        }
    }

    /**
     * Returns the interpolated value of y that corresponds to the specified
     * x-value in the given series.  If the x-value falls outside the range of
     * x-values for the dataset, this method returns {@code Double.NaN}.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param series  the series index.
     * @param x  the x-value.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The y value.
     *
     * @since 1.0.16
     */
    public static <S extends Comparable<S>> double findYValue(XYDataset<S> dataset, int series, double x) {
        // delegate null check on dataset
        int[] indices = findItemIndicesForX(dataset, series, x);
        if (indices[0] == -1) {
            return Double.NaN;
        }
        if (indices[0] == indices[1]) {
            return dataset.getYValue(series, indices[0]);
        }
        double x0 = dataset.getXValue(series, indices[0]);
        double x1 = dataset.getXValue(series, indices[1]);
        double y0 = dataset.getYValue(series, indices[0]);
        double y1 = dataset.getYValue(series, indices[1]);
        return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
    }

    /**
     * Finds the indices of the the items in the dataset that span the
     * specified x-value.  There are three cases for the return value:
     * <ul>
     * <li>there is an exact match for the x-value at index i
     * (returns {@code int[] {i, i}});</li>
     * <li>the x-value falls between two (adjacent) items at index i and i+1
     * (returns {@code int[] {i, i+1}});</li>
     * <li>the x-value falls outside the domain bounds, in which case the
     *    method returns {@code int[] {-1, -1}}.</li>
     * </ul>
     * @param dataset  the dataset ({@code null} not permitted).
     * @param series  the series index.
     * @param x  the x-value.
     *
     * @param <S>  the type for the series keys.
     *
     * @return The indices of the two items that span the x-value.
     *
     * @since 1.0.16
     *
     * @see #findYValue(org.jfree.data.xy.XYDataset, int, double)
     */
    public static <S extends Comparable<S>> int[] findItemIndicesForX(XYDataset<S> dataset, int series, double x) {
        Args.nullNotPermitted(dataset, "dataset");
        int itemCount = dataset.getItemCount(series);
        if (itemCount == 0) {
            return new int[] { -1, -1 };
        }
        if (itemCount == 1) {
            if (x == dataset.getXValue(series, 0)) {
                return new int[] { 0, 0 };
            } else {
                return new int[] { -1, -1 };
            }
        }
        if (dataset.getDomainOrder() == DomainOrder.ASCENDING) {
            int low = 0;
            int high = itemCount - 1;
            double lowValue = dataset.getXValue(series, low);
            if (lowValue > x) {
                return new int[] { -1, -1 };
            }
            if (lowValue == x) {
                return new int[] { low, low };
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue < x) {
                return new int[] { -1, -1 };
            }
            if (highValue == x) {
                return new int[] { high, high };
            }
            int mid = (low + high) / 2;
            while (high - low > 1) {
                double midV = dataset.getXValue(series, mid);
                if (x == midV) {
                    return new int[] { mid, mid };
                }
                if (midV < x) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return new int[] { low, high };
        } else if (dataset.getDomainOrder() == DomainOrder.DESCENDING) {
            int high = 0;
            int low = itemCount - 1;
            double lowValue = dataset.getXValue(series, low);
            if (lowValue > x) {
                return new int[] { -1, -1 };
            }
            double highValue = dataset.getXValue(series, high);
            if (highValue < x) {
                return new int[] { -1, -1 };
            }
            int mid = (low + high) / 2;
            while (high - low > 1) {
                double midV = dataset.getXValue(series, mid);
                if (x == midV) {
                    return new int[] { mid, mid };
                }
                if (midV < x) {
                    low = mid;
                } else {
                    high = mid;
                }
                mid = (low + high) / 2;
            }
            return new int[] { low, high };
        } else {
            // we don't know anything about the ordering of the x-values,
            // so we iterate until we find the first crossing of x (if any)
            // we know there are at least 2 items in the series at this point
            double prev = dataset.getXValue(series, 0);
            if (x == prev) {
                // exact match on first item
                return new int[] { 0, 0 };
            }
            for (int i = 1; i < itemCount; i++) {
                double next = dataset.getXValue(series, i);
                if (x == next) {
                    // exact match
                    return new int[] { i, i };
                }
                if ((x > prev && x < next) || (x < prev && x > next)) {
                    // spanning match
                    return new int[] { i - 1, i };
                }
            }
            // no crossing of x
            return new int[] { -1, -1 };
        }
    }
}
/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------------------------
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Richard Atkinson;
 *                   Peter Kolb (patch 2497611);
 * 
 */
/**
 * An abstract base class that you can use to implement a new
 * {@link CategoryItemRenderer}.  When you create a new
 * {@link CategoryItemRenderer} you are not required to extend this class,
 * but it makes the job easier.
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 1247553218442497391L;

    /**
     * The plot that the renderer is assigned to.
     */
    private CategoryPlot plot;

    /**
     * A list of item label generators (one per series).
     */
    private Map<Integer, CategoryItemLabelGenerator> itemLabelGeneratorMap;

    /**
     * The default item label generator.
     */
    private CategoryItemLabelGenerator defaultItemLabelGenerator;

    /**
     * A list of tool tip generators (one per series).
     */
    private Map<Integer, CategoryToolTipGenerator> toolTipGeneratorMap;

    /**
     * The default tool tip generator.
     */
    private CategoryToolTipGenerator defaultToolTipGenerator;

    /**
     * A list of item label generators (one per series).
     */
    private Map<Integer, CategoryURLGenerator> itemURLGeneratorMap;

    /**
     * The default item label generator.
     */
    private CategoryURLGenerator defaultItemURLGenerator;

    /**
     * The legend item label generator.
     */
    private CategorySeriesLabelGenerator legendItemLabelGenerator;

    /**
     * The legend item tool tip generator.
     */
    private CategorySeriesLabelGenerator legendItemToolTipGenerator;

    /**
     * The legend item URL generator.
     */
    private CategorySeriesLabelGenerator legendItemURLGenerator;

    /**
     * The number of rows in the dataset (temporary record).
     */
    private transient int rowCount;

    /**
     * The number of columns in the dataset (temporary record).
     */
    private transient int columnCount;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * The defaults (no tool tip or URL generators) have been chosen to
     * minimise the processing required to generate a default chart.  If you
     * require tool tips or URLs, then you can easily add the required
     * generators.
     */
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGeneratorMap = new HashMap<>();
        this.toolTipGeneratorMap = new HashMap<>();
        this.itemURLGeneratorMap = new HashMap<>();
        this.legendItemLabelGenerator = new StandardCategorySeriesLabelGenerator();
    }

    /**
     * Returns the number of passes through the dataset required by the
     * renderer.  This method returns {@code 1}, subclasses should
     * override if they need more passes.
     *
     * @return The pass count.
     */
    @Override
    public int getPassCount() {
        return 1;
    }

    /**
     * Returns the plot that the renderer has been assigned to (where
     * {@code null} indicates that the renderer is not currently assigned
     * to a plot).
     *
     * @return The plot (possibly {@code null}).
     *
     * @see #setPlot(CategoryPlot)
     */
    @Override
    public CategoryPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that the renderer has been assigned to.  This method is
     * usually called by the {@link CategoryPlot}, in normal usage you
     * shouldn't need to call this method directly.
     *
     * @param plot  the plot ({@code null} not permitted).
     *
     * @see #getPlot()
     */
    @Override
    public void setPlot(CategoryPlot plot) {
        Args.nullNotPermitted(plot, "plot");
        this.plot = plot;
    }

    // ITEM LABEL GENERATOR
    /**
     * Returns the item label generator for a data item.  This implementation
     * simply passes control to the {@link #getSeriesItemLabelGenerator(int)}
     * method.  If, for some reason, you want a different generator for
     * individual items, you can override this method.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public CategoryItemLabelGenerator getItemLabelGenerator(int row, int column) {
        return getSeriesItemLabelGenerator(row);
    }

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)
     */
    @Override
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        // otherwise look up the generator table
        CategoryItemLabelGenerator generator = this.itemLabelGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultItemLabelGenerator;
        }
        return generator;
    }

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    @Override
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
        setSeriesItemLabelGenerator(series, generator, true);
    }

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    @Override
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator, boolean notify) {
        this.itemLabelGeneratorMap.put(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default item label generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setDefaultItemLabelGenerator(CategoryItemLabelGenerator)
     */
    @Override
    public CategoryItemLabelGenerator getDefaultItemLabelGenerator() {
        return this.defaultItemLabelGenerator;
    }

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultItemLabelGenerator()
     */
    @Override
    public void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator) {
        setDefaultItemLabelGenerator(generator, true);
    }

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultItemLabelGenerator()
     */
    @Override
    public void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator, boolean notify) {
        this.defaultItemLabelGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    // TOOL TIP GENERATOR
    /**
     * Returns the tool tip generator that should be used for the specified
     * item.  This method looks up the generator using the "three-layer"
     * approach outlined in the general description of this interface.  You
     * can override this method if you want to return a different generator per
     * item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public CategoryToolTipGenerator getToolTipGenerator(int row, int column) {
        CategoryToolTipGenerator result = getSeriesToolTipGenerator(row);
        if (result == null) {
            result = this.defaultToolTipGenerator;
        }
        return result;
    }

    /**
     * Returns the tool tip generator for the specified series (a "layer 1"
     * generator).
     *
     * @param series  the series index (zero-based).
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setSeriesToolTipGenerator(int, CategoryToolTipGenerator)
     */
    @Override
    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series) {
        return this.toolTipGeneratorMap.get(series);
    }

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    @Override
    public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator) {
        setSeriesToolTipGenerator(series, generator, true);
    }

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    @Override
    public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator, boolean notify) {
        this.toolTipGeneratorMap.put(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default tool tip generator (the "layer 2" generator).
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setDefaultToolTipGenerator(CategoryToolTipGenerator)
     */
    @Override
    public CategoryToolTipGenerator getDefaultToolTipGenerator() {
        return this.defaultToolTipGenerator;
    }

    /**
     * Sets the default tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultToolTipGenerator()
     */
    @Override
    public void setDefaultToolTipGenerator(CategoryToolTipGenerator generator) {
        setDefaultToolTipGenerator(generator, true);
    }

    /**
     * Sets the default tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultToolTipGenerator()
     */
    @Override
    public void setDefaultToolTipGenerator(CategoryToolTipGenerator generator, boolean notify) {
        this.defaultToolTipGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    // URL GENERATOR
    /**
     * Returns the URL generator for a data item.  This method just calls the
     * getSeriesItemURLGenerator method, but you can override this behaviour if
     * you want to.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The URL generator.
     */
    @Override
    public CategoryURLGenerator getItemURLGenerator(int row, int column) {
        return getSeriesItemURLGenerator(row);
    }

    /**
     * Returns the URL generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The URL generator for the series.
     *
     * @see #setSeriesItemURLGenerator(int, CategoryURLGenerator)
     */
    @Override
    public CategoryURLGenerator getSeriesItemURLGenerator(int series) {
        // otherwise look up the generator table
        CategoryURLGenerator generator = this.itemURLGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultItemURLGenerator;
        }
        return generator;
    }

    /**
     * Sets the URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    @Override
    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator) {
        setSeriesItemURLGenerator(series, generator, true);
    }

    /**
     * Sets the URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     * @param notify  notify listeners?
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    @Override
    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator, boolean notify) {
        this.itemURLGeneratorMap.put(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default item URL generator.
     *
     * @return The item URL generator.
     *
     * @see #setDefaultItemURLGenerator(CategoryURLGenerator)
     */
    @Override
    public CategoryURLGenerator getDefaultItemURLGenerator() {
        return this.defaultItemURLGenerator;
    }

    /**
     * Sets the default item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     *
     * @see #getDefaultItemURLGenerator()
     */
    @Override
    public void setDefaultItemURLGenerator(CategoryURLGenerator generator) {
        setDefaultItemURLGenerator(generator, true);
    }

    /**
     * Sets the default item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultItemURLGenerator()
     */
    @Override
    public void setDefaultItemURLGenerator(CategoryURLGenerator generator, boolean notify) {
        this.defaultItemURLGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the number of rows in the dataset.  This value is updated in the
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return The row count.
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Returns the number of columns in the dataset.  This value is updated in
     * the {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return The column count.
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Creates a new state instance---this method is called from the
     * {@link #initialise(Graphics2D, Rectangle2D, CategoryPlot, int,
     * PlotRenderingInfo)} method.  Subclasses can override this method if
     * they need to use a subclass of {@link CategoryItemRendererState}.
     *
     * @param info  collects plot rendering info ({@code null} permitted).
     *
     * @return The new state instance (never {@code null}).
     */
    protected CategoryItemRendererState createState(PlotRenderingInfo info) {
        return new CategoryItemRendererState(info);
    }

    /**
     * Initialises the renderer and returns a state object that will be used
     * for the remainder of the drawing process for a single chart.  The state
     * object allows for the fact that the renderer may be used simultaneously
     * by multiple threads (each thread will work with a separate state object).
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  an object for returning information about the structure of
     *              the plot ({@code null} permitted).
     *
     * @return The renderer state.
     */
    @Override
    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        setPlot(plot);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        } else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        CategoryItemRendererState state = createState(info);
        state.setElementHinting(plot.fetchElementHintingFlag());
        int[] visibleSeriesTemp = new int[this.rowCount];
        int visibleSeriesCount = 0;
        for (int row = 0; row < this.rowCount; row++) {
            if (isSeriesVisible(row)) {
                visibleSeriesTemp[visibleSeriesCount] = row;
                visibleSeriesCount++;
            }
        }
        int[] visibleSeries = new int[visibleSeriesCount];
        System.arraycopy(visibleSeriesTemp, 0, visibleSeries, 0, visibleSeriesCount);
        state.setVisibleSeriesArray(visibleSeries);
        return state;
    }

    /**
     * Adds a {@code KEY_BEGIN_ELEMENT} hint to the graphics target.  This
     * hint is recognised by <b>JFreeSVG</b> (in theory it could be used by
     * other {@code Graphics2D} implementations also).
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param rowKey  the row key that identifies the element ({@code null} not
     *     permitted).
     * @param columnKey  the column key that identifies the element
     *     ({@code null} not permitted).
     */
    protected void beginElementGroup(Graphics2D g2, Comparable rowKey, Comparable columnKey) {
        beginElementGroup(g2, new KeyedValues2DItemKey(rowKey, columnKey));
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range (or {@code null} if the dataset is
     *         {@code null} or empty).
     */
    @Override
    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param includeInterval  include the y-interval if the dataset has one.
     *
     * @return The range ({@code null} if the dataset is {@code null} or empty).
     */
    protected Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (getDataBoundsIncludesVisibleSeriesOnly()) {
            List visibleSeriesKeys = new ArrayList();
            int seriesCount = dataset.getRowCount();
            for (int s = 0; s < seriesCount; s++) {
                if (isSeriesVisible(s)) {
                    visibleSeriesKeys.add(dataset.getRowKey(s));
                }
            }
            return DatasetUtils.findRangeBounds(dataset, visibleSeriesKeys, includeInterval);
        } else {
            return DatasetUtils.findRangeBounds(dataset, includeInterval);
        }
    }

    /**
     * Returns the Java2D coordinate for the middle of the specified data item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param dataset  the dataset.
     * @param axis  the axis.
     * @param area  the data area.
     * @param edge  the edge along which the axis lies.
     *
     * @return The Java2D coordinate for the middle of the item.
     */
    @Override
    public double getItemMiddle(Comparable<?> rowKey, Comparable<?> columnKey, CategoryDataset<?, ?> dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
        return axis.getCategoryMiddle(columnKey, dataset.getColumnKeys(), area, edge);
    }

    /**
     * Draws a background for the data area.  The default implementation just
     * gets the plot to draw the background, but some renderers will override
     * this behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    @Override
    public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        plot.drawBackground(g2, dataArea);
    }

    /**
     * Draws an outline for the data area.  The default implementation just
     * gets the plot to draw the outline, but some renderers will override this
     * behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    @Override
    public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        plot.drawOutline(g2, dataArea);
    }

    /**
     * Draws a grid line against the domain axis.
     * <P>
     * Note that this default implementation assumes that the horizontal axis
     * is the domain axis. If this is not the case, you will need to override
     * this method.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data.
     * @param value  the Java2D value at which the grid line should be drawn.
     */
    @Override
    public void drawDomainGridline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value) {
        Line2D line = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
        }
        Paint paint = plot.getDomainGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);
        Stroke stroke = plot.getDomainGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
        g2.setStroke(stroke);
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Draws a line perpendicular to the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data.
     * @param value  the value at which the grid line should be drawn.
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the stroke ({@code null} not permitted).
     */
    @Override
    public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        g2.setPaint(paint);
        g2.setStroke(stroke);
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Draws a marker for the domain axis.
     *
     * @param g2  the graphics device (not {@code null}).
     * @param plot  the plot (not {@code null}).
     * @param axis  the range axis (not {@code null}).
     * @param marker  the marker to be drawn (not {@code null}).
     * @param dataArea  the area inside the axes (not {@code null}).
     *
     * @see #drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker,
     *     Rectangle2D)
     */
    @Override
    public void drawDomainMarker(Graphics2D g2, CategoryPlot plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea) {
        Comparable category = marker.getKey();
        CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
        int columnIndex = dataset.getColumnIndex(category);
        if (columnIndex < 0) {
            return;
        }
        final Composite savedComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
        PlotOrientation orientation = plot.getOrientation();
        Rectangle2D bounds;
        if (marker.getDrawAsLine()) {
            double v = axis.getCategoryMiddle(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else {
                throw new IllegalStateException();
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            bounds = line.getBounds2D();
        } else {
            double v0 = axis.getCategoryStart(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double v1 = axis.getCategoryEnd(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            Rectangle2D area = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                area = new Rectangle2D.Double(dataArea.getMinX(), v0, dataArea.getWidth(), (v1 - v0));
            } else if (orientation == PlotOrientation.VERTICAL) {
                area = new Rectangle2D.Double(v0, dataArea.getMinY(), (v1 - v0), dataArea.getHeight());
            }
            g2.setPaint(marker.getPaint());
            g2.fill(area);
            bounds = area;
        }
        String label = marker.getLabel();
        RectangleAnchor anchor = marker.getLabelAnchor();
        if (label != null) {
            Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            Point2D coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, bounds, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
            TextUtils.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
        }
        g2.setComposite(savedComposite);
    }

    /**
     * Draws a marker for the range axis.
     *
     * @param g2  the graphics device (not {@code null}).
     * @param plot  the plot (not {@code null}).
     * @param axis  the range axis (not {@code null}).
     * @param marker  the marker to be drawn (not {@code null}).
     * @param dataArea  the area inside the axes (not {@code null}).
     *
     * @see #drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis,
     *     CategoryMarker, Rectangle2D)
     */
    @Override
    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }
            final Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            PlotOrientation orientation = plot.getOrientation();
            double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else {
                throw new IllegalStateException();
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                Rectangle2D rect = TextUtils.calcAlignedStringBounds(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(rect);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(savedComposite);
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = axis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }
            final Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            double start2d = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
            double end2d = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);
            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                // clip left and right bounds to data area
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
            } else if (orientation == PlotOrientation.VERTICAL) {
                // clip top and bottom bounds to data area
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
            }
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            } else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            // now draw the outlines, if visible...
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                if (orientation == PlotOrientation.VERTICAL) {
                    Line2D line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                } else {
                    // PlotOrientation.HORIZONTAL
                    Line2D line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                }
            }
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coords = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                Rectangle2D r = TextUtils.calcAlignedStringBounds(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(r);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(savedComposite);
        }
    }

    /**
     * Calculates the {@code (x, y)} coordinates for drawing the label for a
     * marker on the range axis.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker.
     * @param markerOffset  the marker offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return anchor.getAnchorPoint(anchorRect);
    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker.
     * @param markerOffset  the marker offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        return anchor.getAnchorPoint(anchorRect);
    }

    /**
     * Returns a legend item for a series.  This default implementation will
     * return {@code null} if {@link #isSeriesVisible(int)} or
     * {@link #isSeriesVisibleInLegend(int)} returns {@code false}.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item (possibly {@code null}).
     *
     * @see #getLegendItems()
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        CategoryPlot p = getPlot();
        if (p == null) {
            return null;
        }
        // check that a legend item needs to be displayed...
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }
        CategoryDataset dataset = p.getDataset(datasetIndex);
        String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
        String description = label;
        String toolTipText = null;
        if (this.legendItemToolTipGenerator != null) {
            toolTipText = this.legendItemToolTipGenerator.generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.legendItemURLGenerator != null) {
            urlText = this.legendItemURLGenerator.generateLabel(dataset, series);
        }
        Shape shape = lookupLegendShape(series);
        Paint paint = lookupSeriesPaint(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        LegendItem item = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        item.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            item.setLabelPaint(labelPaint);
        }
        item.setSeriesKey(dataset.getRowKey(series));
        item.setSeriesIndex(series);
        item.setDataset(dataset);
        item.setDatasetIndex(datasetIndex);
        return item;
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemRenderer)) {
            return false;
        }
        AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer) obj;
        if (!Objects.equals(this.itemLabelGeneratorMap, that.itemLabelGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemLabelGenerator, that.defaultItemLabelGenerator)) {
            return false;
        }
        if (!Objects.equals(this.toolTipGeneratorMap, that.toolTipGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultToolTipGenerator, that.defaultToolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.itemURLGeneratorMap, that.itemURLGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemURLGenerator, that.defaultItemURLGenerator)) {
            return false;
        }
        if (!Objects.equals(this.legendItemLabelGenerator, that.legendItemLabelGenerator)) {
            return false;
        }
        if (!Objects.equals(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.legendItemURLGenerator, that.legendItemURLGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for the renderer.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        return result;
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier (possibly {@code null}).
     */
    @Override
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        CategoryPlot cp = getPlot();
        if (cp != null) {
            result = cp.getDrawingSupplier();
        }
        return result;
    }

    /**
     * Considers the current (x, y) coordinate and updates the crosshair point
     * if it meets the criteria (usually means the (x, y) coordinate is the
     * closest to the anchor point so far).
     *
     * @param crosshairState  the crosshair state ({@code null} permitted,
     *                        but the method does nothing in that case).
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param value  the data value.
     * @param datasetIndex  the dataset index.
     * @param transX  the x-value translated to Java2D space.
     * @param transY  the y-value translated to Java2D space.
     * @param orientation  the plot orientation ({@code null} not permitted).
     */
    protected void updateCrosshairValues(CategoryCrosshairState crosshairState, Comparable rowKey, Comparable columnKey, double value, int datasetIndex, double transX, double transY, PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        if (crosshairState != null) {
            if (this.plot.isRangeCrosshairLockedOnData()) {
                // both axes
                crosshairState.updateCrosshairPoint(rowKey, columnKey, value, datasetIndex, transX, transY, orientation);
            } else {
                crosshairState.updateCrosshairX(rowKey, columnKey, datasetIndex, transX, orientation);
            }
        }
    }

    /**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param row  the row.
     * @param column  the column.
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param negative  indicates a negative value (which affects the item
     *                  label position).
     */
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, CategoryDataset dataset, int row, int column, double x, double y, boolean negative) {
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null) {
            Font labelFont = getItemLabelFont(row, column);
            Paint paint = getItemLabelPaint(row, column);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, row, column);
            ItemLabelPosition position;
            if (!negative) {
                position = getPositiveItemLabelPosition(row, column);
            } else {
                position = getNegativeItemLabelPosition(row, column);
            }
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtils.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    /**
     * Returns an independent copy of the renderer.  The {@code plot}
     * reference is shallow copied.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  can be thrown if one of the objects
     *         belonging to the renderer does not support cloning (for example,
     *         an item label generator).
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer) super.clone();
        if (this.itemLabelGeneratorMap != null) {
            clone.itemLabelGeneratorMap = CloneUtils.cloneMapValues(this.itemLabelGeneratorMap);
        }
        if (this.defaultItemLabelGenerator != null) {
            if (this.defaultItemLabelGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.defaultItemLabelGenerator;
                clone.defaultItemLabelGenerator = (CategoryItemLabelGenerator) pc.clone();
            } else {
                throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
            }
        }
        if (this.toolTipGeneratorMap != null) {
            clone.toolTipGeneratorMap = CloneUtils.cloneMapValues(this.toolTipGeneratorMap);
        }
        if (this.defaultToolTipGenerator != null) {
            if (this.defaultToolTipGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.defaultToolTipGenerator;
                clone.defaultToolTipGenerator = (CategoryToolTipGenerator) pc.clone();
            } else {
                throw new CloneNotSupportedException("Default tool tip generator not cloneable.");
            }
        }
        if (this.itemURLGeneratorMap != null) {
            clone.itemURLGeneratorMap = CloneUtils.cloneMapValues(this.itemURLGeneratorMap);
        }
        if (this.defaultItemURLGenerator != null) {
            if (this.defaultItemURLGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.defaultItemURLGenerator;
                clone.defaultItemURLGenerator = (CategoryURLGenerator) pc.clone();
            } else {
                throw new CloneNotSupportedException("Default item URL generator not cloneable.");
            }
        }
        if (this.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (CategorySeriesLabelGenerator) CloneUtils.clone((Object) this.legendItemLabelGenerator);
        }
        if (this.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (CategorySeriesLabelGenerator) CloneUtils.clone((Object) this.legendItemToolTipGenerator);
        }
        if (this.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (CategorySeriesLabelGenerator) CloneUtils.clone((Object) this.legendItemURLGenerator);
        }
        return clone;
    }

    /**
     * Returns a domain axis for a plot.
     *
     * @param plot  the plot.
     * @param index  the axis index.
     *
     * @return A domain axis.
     */
    protected CategoryAxis getDomainAxis(CategoryPlot plot, int index) {
        CategoryAxis result = plot.getDomainAxis(index);
        if (result == null) {
            result = plot.getDomainAxis();
        }
        return result;
    }

    /**
     * Returns a range axis for a plot.
     *
     * @param plot  the plot.
     * @param index  the axis index.
     *
     * @return A range axis.
     */
    protected ValueAxis getRangeAxis(CategoryPlot plot, int index) {
        ValueAxis result = plot.getRangeAxis(index);
        if (result == null) {
            result = plot.getRangeAxis();
        }
        return result;
    }

    /**
     * Returns a (possibly empty) collection of legend items for the series
     * that this renderer is responsible for drawing.
     *
     * @return The legend item collection (never {@code null}).
     *
     * @see #getLegendItem(int, int)
     */
    @Override
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.plot == null) {
            return result;
        }
        int index = this.plot.getIndexOf(this);
        CategoryDataset dataset = this.plot.getDataset(index);
        if (dataset == null) {
            return result;
        }
        int seriesCount = dataset.getRowCount();
        if (plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
            for (int i = 0; i < seriesCount; i++) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        } else {
            for (int i = seriesCount - 1; i >= 0; i--) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns the legend item label generator.
     *
     * @return The label generator (never {@code null}).
     *
     * @see #setLegendItemLabelGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    /**
     * Sets the legend item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} not permitted).
     *
     * @see #getLegendItemLabelGenerator()
     */
    public void setLegendItemLabelGenerator(CategorySeriesLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        this.legendItemLabelGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend item tool tip generator.
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    /**
     * Sets the legend item tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     */
    public void setLegendItemToolTipGenerator(CategorySeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend item URL generator.
     *
     * @return The URL generator (possibly {@code null}).
     *
     * @see #setLegendItemURLGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
        return this.legendItemURLGenerator;
    }

    /**
     * Sets the legend item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getLegendItemURLGenerator()
     */
    public void setLegendItemURLGenerator(CategorySeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Adds an entity with the specified hotspot.
     *
     * @param entities  the entity collection.
     * @param dataset  the dataset.
     * @param row  the row index.
     * @param column  the column index.
     * @param hotspot  the hotspot ({@code null} not permitted).
     */
    protected void addItemEntity(EntityCollection entities, CategoryDataset dataset, int row, int column, Shape hotspot) {
        Args.nullNotPermitted(hotspot, "hotspot");
        if (!getItemCreateEntity(row, column)) {
            return;
        }
        String tip = null;
        CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
        if (tipster != null) {
            tip = tipster.generateToolTip(dataset, row, column);
        }
        String url = null;
        CategoryURLGenerator urlster = getItemURLGenerator(row, column);
        if (urlster != null) {
            url = urlster.generateURL(dataset, row, column);
        }
        CategoryItemEntity entity = new CategoryItemEntity(hotspot, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column));
        entities.add(entity);
    }

    /**
     * Adds an entity to the collection.
     *
     * @param entities  the entity collection being populated.
     * @param hotspot  the entity area (if {@code null} a default will be
     *              used).
     * @param dataset  the dataset.
     * @param row  the series.
     * @param column  the item.
     * @param entityX  the entity's center x-coordinate in user space (only
     *                 used if {@code area} is {@code null}).
     * @param entityY  the entity's center y-coordinate in user space (only
     *                 used if {@code area} is {@code null}).
     */
    protected void addEntity(EntityCollection entities, Shape hotspot, CategoryDataset dataset, int row, int column, double entityX, double entityY) {
        if (!getItemCreateEntity(row, column)) {
            return;
        }
        Shape s = hotspot;
        if (hotspot == null) {
            double r = getDefaultEntityRadius();
            double w = r * 2;
            if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                s = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
            } else {
                s = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
            }
        }
        String tip = null;
        CategoryToolTipGenerator generator = getToolTipGenerator(row, column);
        if (generator != null) {
            tip = generator.generateToolTip(dataset, row, column);
        }
        String url = null;
        CategoryURLGenerator urlster = getItemURLGenerator(row, column);
        if (urlster != null) {
            url = urlster.generateURL(dataset, row, column);
        }
        CategoryItemEntity entity = new CategoryItemEntity(s, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column));
        entities.add(entity);
    }
}
/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------------------------------
 * DefaultIntervalCategoryDataset.java
 * -----------------------------------
 * (C) Copyright 2002-2021, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A convenience class that provides a default implementation of the
 * {@link IntervalCategoryDataset} interface.
 * <p>
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 */
public class DefaultIntervalCategoryDataset extends AbstractSeriesDataset implements IntervalCategoryDataset {

    /**
     * The series keys.
     */
    private Comparable[] seriesKeys;

    /**
     * The category keys.
     */
    private Comparable[] categoryKeys;

    /**
     * Storage for the start value data.
     */
    private Number[][] startData;

    /**
     * Storage for the end value data.
     */
    private Number[][] endData;

    /**
     * Creates a new dataset using the specified data values and automatically
     * generated series and category keys.
     *
     * @param starts  the starting values for the intervals ({@code null}
     *                not permitted).
     * @param ends  the ending values for the intervals ({@code null} not
     *                permitted).
     */
    public DefaultIntervalCategoryDataset(double[][] starts, double[][] ends) {
        this(DataUtils.createNumberArray2D(starts), DataUtils.createNumberArray2D(ends));
    }

    /**
     * Constructs a dataset and populates it with data from the array.
     * <p>
     * The arrays are indexed as data[series][category].  Series and category
     * names are automatically generated - you can change them using the
     * {@link #setSeriesKeys(Comparable[])} and
     * {@link #setCategoryKeys(Comparable[])} methods.
     *
     * @param starts  the start values data.
     * @param ends  the end values data.
     */
    public DefaultIntervalCategoryDataset(Number[][] starts, Number[][] ends) {
        this(null, null, starts, ends);
    }

    /**
     * Constructs a DefaultIntervalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series.
     * <p>
     * Category names are generated automatically ("Category 1", "Category 2",
     * etc).
     *
     * @param seriesNames  the series names (if {@code null}, series names
     *         will be generated automatically).
     * @param starts  the start values data, indexed as data[series][category].
     * @param ends  the end values data, indexed as data[series][category].
     */
    public DefaultIntervalCategoryDataset(String[] seriesNames, Number[][] starts, Number[][] ends) {
        this(seriesNames, null, starts, ends);
    }

    /**
     * Constructs a DefaultIntervalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series and the
     * supplied objects for the categories.
     *
     * @param seriesKeys  the series keys (if {@code null}, series keys
     *         will be generated automatically).
     * @param categoryKeys  the category keys (if {@code null}, category
     *         keys will be generated automatically).
     * @param starts  the start values data, indexed as data[series][category].
     * @param ends  the end values data, indexed as data[series][category].
     */
    public DefaultIntervalCategoryDataset(Comparable[] seriesKeys, Comparable[] categoryKeys, Number[][] starts, Number[][] ends) {
        this.startData = starts;
        this.endData = ends;
        if (starts != null && ends != null) {
            ResourceBundle resources = ResourceBundle.getBundle("org.jfree.data.resources.DataPackageResources");
            int seriesCount = starts.length;
            if (seriesCount != ends.length) {
                String errMsg = "DefaultIntervalCategoryDataset: the number " + "of series in the start value dataset does " + "not match the number of series in the end " + "value dataset.";
                throw new IllegalArgumentException(errMsg);
            }
            if (seriesCount > 0) {
                // set up the series names...
                if (seriesKeys != null) {
                    if (seriesKeys.length != seriesCount) {
                        throw new IllegalArgumentException("The number of series keys does not " + "match the number of series in the data.");
                    }
                    this.seriesKeys = seriesKeys;
                } else {
                    String prefix = resources.getString("series.default-prefix") + " ";
                    this.seriesKeys = generateKeys(seriesCount, prefix);
                }
                // set up the category names...
                int categoryCount = starts[0].length;
                if (categoryCount != ends[0].length) {
                    String errMsg = "DefaultIntervalCategoryDataset: the " + "number of categories in the start value " + "dataset does not match the number of " + "categories in the end value dataset.";
                    throw new IllegalArgumentException(errMsg);
                }
                if (categoryKeys != null) {
                    if (categoryKeys.length != categoryCount) {
                        throw new IllegalArgumentException("The number of category keys does not match " + "the number of categories in the data.");
                    }
                    this.categoryKeys = categoryKeys;
                } else {
                    String prefix = resources.getString("categories.default-prefix") + " ";
                    this.categoryKeys = generateKeys(categoryCount, prefix);
                }
            } else {
                this.seriesKeys = new Comparable[0];
                this.categoryKeys = new Comparable[0];
            }
        }
    }

    /**
     * Returns the number of series in the dataset (possibly zero).
     *
     * @return The number of series in the dataset.
     *
     * @see #getRowCount()
     * @see #getCategoryCount()
     */
    @Override
    public int getSeriesCount() {
        int result = 0;
        if (this.startData != null) {
            result = this.startData.length;
        }
        return result;
    }

    /**
     * Returns a series index.
     *
     * @param seriesKey  the series key.
     *
     * @return The series index.
     *
     * @see #getRowIndex(Comparable)
     * @see #getSeriesKey(int)
     */
    public int getSeriesIndex(Comparable seriesKey) {
        int result = -1;
        for (int i = 0; i < this.seriesKeys.length; i++) {
            if (seriesKey.equals(this.seriesKeys[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the name of the specified series.
     *
     * @param series  the index of the required series (zero-based).
     *
     * @return The name of the specified series.
     *
     * @see #getSeriesIndex(Comparable)
     */
    @Override
    public Comparable getSeriesKey(int series) {
        if ((series >= getSeriesCount()) || (series < 0)) {
            throw new IllegalArgumentException("No such series : " + series);
        }
        return this.seriesKeys[series];
    }

    /**
     * Sets the names of the series in the dataset.
     *
     * @param seriesKeys  the new keys ({@code null} not permitted, the
     *         length of the array must match the number of series in the
     *         dataset).
     *
     * @see #setCategoryKeys(Comparable[])
     */
    public void setSeriesKeys(Comparable[] seriesKeys) {
        Args.nullNotPermitted(seriesKeys, "seriesKeys");
        if (seriesKeys.length != getSeriesCount()) {
            throw new IllegalArgumentException("The number of series keys does not match the data.");
        }
        this.seriesKeys = seriesKeys;
        fireDatasetChanged();
    }

    /**
     * Returns the number of categories in the dataset.
     *
     * @return The number of categories in the dataset.
     *
     * @see #getColumnCount()
     */
    public int getCategoryCount() {
        int result = 0;
        if (this.startData != null) {
            if (getSeriesCount() > 0) {
                result = this.startData[0].length;
            }
        }
        return result;
    }

    /**
     * Returns a list of the categories in the dataset.  This method supports
     * the {@link CategoryDataset} interface.
     *
     * @return A list of the categories in the dataset.
     *
     * @see #getRowKeys()
     */
    @Override
    public List getColumnKeys() {
        // the CategoryDataset interface expects a list of categories, but
        // we've stored them in an array...
        if (this.categoryKeys == null) {
            return new ArrayList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(this.categoryKeys));
        }
    }

    /**
     * Sets the categories for the dataset.
     *
     * @param categoryKeys  an array of objects representing the categories in
     *                      the dataset.
     *
     * @see #getRowKeys()
     * @see #setSeriesKeys(Comparable[])
     */
    public void setCategoryKeys(Comparable[] categoryKeys) {
        Args.nullNotPermitted(categoryKeys, "categoryKeys");
        if (categoryKeys.length != getCategoryCount()) {
            throw new IllegalArgumentException("The number of categories does not match the data.");
        }
        for (int i = 0; i < categoryKeys.length; i++) {
            if (categoryKeys[i] == null) {
                throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setCategoryKeys(): " + "null category not permitted.");
            }
        }
        this.categoryKeys = categoryKeys;
        fireDatasetChanged();
    }

    /**
     * Returns the data value for one category in a series.
     * <P>
     * This method is part of the CategoryDataset interface.  Not particularly
     * meaningful for this class...returns the end value.
     *
     * @param series    The required series (zero based index).
     * @param category  The required category.
     *
     * @return The data value for one category in a series (null possible).
     *
     * @see #getEndValue(Comparable, Comparable)
     */
    @Override
    public Number getValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex < 0) {
            throw new UnknownKeyException("Unknown 'category' key.");
        }
        return getValue(seriesIndex, itemIndex);
    }

    /**
     * Returns the data value for one category in a series.
     * <P>
     * This method is part of the CategoryDataset interface.  Not particularly
     * meaningful for this class...returns the end value.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return The data value for one category in a series (null possible).
     *
     * @see #getEndValue(int, int)
     */
    @Override
    public Number getValue(int series, int category) {
        return getEndValue(series, category);
    }

    /**
     * Returns the start data value for one category in a series.
     *
     * @param series  the required series.
     * @param category  the required category.
     *
     * @return The start data value for one category in a series
     *         (possibly {@code null}).
     *
     * @see #getStartValue(int, int)
     */
    @Override
    public Number getStartValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex < 0) {
            throw new UnknownKeyException("Unknown 'category' key.");
        }
        return getStartValue(seriesIndex, itemIndex);
    }

    /**
     * Returns the start data value for one category in a series.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return The start data value for one category in a series
     *         (possibly {@code null}).
     *
     * @see #getStartValue(Comparable, Comparable)
     */
    @Override
    public Number getStartValue(int series, int category) {
        // check arguments...
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "series index out of range.");
        }
        if ((category < 0) || (category >= getCategoryCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "category index out of range.");
        }
        // fetch the value...
        return this.startData[series][category];
    }

    /**
     * Returns the end data value for one category in a series.
     *
     * @param series  the required series.
     * @param category  the required category.
     *
     * @return The end data value for one category in a series (null possible).
     *
     * @see #getEndValue(int, int)
     */
    @Override
    public Number getEndValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex < 0) {
            throw new UnknownKeyException("Unknown 'category' key.");
        }
        return getEndValue(seriesIndex, itemIndex);
    }

    /**
     * Returns the end data value for one category in a series.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return The end data value for one category in a series (null possible).
     *
     * @see #getEndValue(Comparable, Comparable)
     */
    @Override
    public Number getEndValue(int series, int category) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "series index out of range.");
        }
        if ((category < 0) || (category >= getCategoryCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "category index out of range.");
        }
        return this.endData[series][category];
    }

    /**
     * Sets the start data value for one category in a series.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @param value The value.
     *
     * @see #setEndValue(int, Comparable, Number)
     */
    public void setStartValue(int series, Comparable category, Number value) {
        // does the series exist?
        if ((series < 0) || (series > getSeriesCount() - 1)) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "series outside valid range.");
        }
        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "unrecognised category.");
        }
        // update the data...
        this.startData[series][categoryIndex] = value;
        fireDatasetChanged();
    }

    /**
     * Sets the end data value for one category in a series.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @param value the value.
     *
     * @see #setStartValue(int, Comparable, Number)
     */
    public void setEndValue(int series, Comparable category, Number value) {
        // does the series exist?
        if ((series < 0) || (series > getSeriesCount() - 1)) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "series outside valid range.");
        }
        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "unrecognised category.");
        }
        // update the data...
        this.endData[series][categoryIndex] = value;
        fireDatasetChanged();
    }

    /**
     * Returns the index for the given category.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @return The index.
     *
     * @see #getColumnIndex(Comparable)
     */
    public int getCategoryIndex(Comparable category) {
        int result = -1;
        for (int i = 0; i < this.categoryKeys.length; i++) {
            if (category.equals(this.categoryKeys[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Generates an array of keys, by appending a space plus an integer
     * (starting with 1) to the supplied prefix string.
     *
     * @param count  the number of keys required.
     * @param prefix  the name prefix.
     *
     * @return An array of <i>prefixN</i> with N = { 1 .. count}.
     */
    private Comparable[] generateKeys(int count, String prefix) {
        Comparable[] result = new Comparable[count];
        String name;
        for (int i = 0; i < count; i++) {
            name = prefix + (i + 1);
            result[i] = name;
        }
        return result;
    }

    /**
     * Returns a column key.
     *
     * @param column  the column index.
     *
     * @return The column key.
     *
     * @see #getRowKey(int)
     */
    @Override
    public Comparable getColumnKey(int column) {
        return this.categoryKeys[column];
    }

    /**
     * Returns a column index.
     *
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @return The column index.
     *
     * @see #getCategoryIndex(Comparable)
     */
    @Override
    public int getColumnIndex(Comparable columnKey) {
        Args.nullNotPermitted(columnKey, "columnKey");
        return getCategoryIndex(columnKey);
    }

    /**
     * Returns a row index.
     *
     * @param rowKey  the row key.
     *
     * @return The row index.
     *
     * @see #getSeriesIndex(Comparable)
     */
    @Override
    public int getRowIndex(Comparable rowKey) {
        return getSeriesIndex(rowKey);
    }

    /**
     * Returns a list of the series in the dataset.  This method supports the
     * {@link CategoryDataset} interface.
     *
     * @return A list of the series in the dataset.
     *
     * @see #getColumnKeys()
     */
    @Override
    public List getRowKeys() {
        // the CategoryDataset interface expects a list of series, but
        // we've stored them in an array...
        if (this.seriesKeys == null) {
            return new java.util.ArrayList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(this.seriesKeys));
        }
    }

    /**
     * Returns the name of the specified series.
     *
     * @param row  the index of the required row/series (zero-based).
     *
     * @return The name of the specified series.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public Comparable getRowKey(int row) {
        if ((row >= getRowCount()) || (row < 0)) {
            throw new IllegalArgumentException("The 'row' argument is out of bounds.");
        }
        return this.seriesKeys[row];
    }

    /**
     * Returns the number of categories in the dataset.  This method is part of
     * the {@link CategoryDataset} interface.
     *
     * @return The number of categories in the dataset.
     *
     * @see #getCategoryCount()
     * @see #getRowCount()
     */
    @Override
    public int getColumnCount() {
        return this.categoryKeys.length;
    }

    /**
     * Returns the number of series in the dataset (possibly zero).
     *
     * @return The number of series in the dataset.
     *
     * @see #getSeriesCount()
     * @see #getColumnCount()
     */
    @Override
    public int getRowCount() {
        return this.seriesKeys.length;
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DefaultIntervalCategoryDataset)) {
            return false;
        }
        DefaultIntervalCategoryDataset that = (DefaultIntervalCategoryDataset) obj;
        if (!Arrays.equals(this.seriesKeys, that.seriesKeys)) {
            return false;
        }
        if (!Arrays.equals(this.categoryKeys, that.categoryKeys)) {
            return false;
        }
        if (!equal(this.startData, that.startData)) {
            return false;
        }
        if (!equal(this.endData, that.endData)) {
            return false;
        }
        // seem to be the same...
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Arrays.deepHashCode(this.seriesKeys);
        hash = 61 * hash + Arrays.deepHashCode(this.categoryKeys);
        hash = 61 * hash + Arrays.deepHashCode(this.startData);
        hash = 61 * hash + Arrays.deepHashCode(this.endData);
        return hash;
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning the
     *         dataset.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultIntervalCategoryDataset clone = (DefaultIntervalCategoryDataset) super.clone();
        clone.categoryKeys = (Comparable[]) this.categoryKeys.clone();
        clone.seriesKeys = (Comparable[]) this.seriesKeys.clone();
        clone.startData = clone(this.startData);
        clone.endData = clone(this.endData);
        return clone;
    }

    /**
     * Tests two double[][] arrays for equality.
     *
     * @param array1  the first array ({@code null} permitted).
     * @param array2  the second arrray ({@code null} permitted).
     *
     * @return A boolean.
     */
    private static boolean equal(Number[][] array1, Number[][] array2) {
        if (array1 == null) {
            return (array2 == null);
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (!Arrays.equals(array1[i], array2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clones a two dimensional array of {@code Number} objects.
     *
     * @param array  the array ({@code null} not permitted).
     *
     * @return A clone of the array.
     */
    private static Number[][] clone(Number[][] array) {
        Args.nullNotPermitted(array, "array");
        Number[][] result = new Number[array.length][];
        for (int i = 0; i < array.length; i++) {
            Number[] child = array[i];
            Number[] copychild = new Number[child.length];
            System.arraycopy(child, 0, copychild, 0, child.length);
            result[i] = copychild;
        }
        return result;
    }
}
/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * ---------------
 * TaskSeries.java
 * ---------------
 * (C) Copyright 2002-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Tracy Hiltbrand;
 */
/**
 * A series that contains zero, one or many {@link Task} objects.
 * <P>
 * This class is used as a building block for the {@link TaskSeriesCollection}
 * class that can be used to construct basic Gantt charts.
 *
 * @param <K> the key type.
 */
public class TaskSeries<K extends Comparable<K>> extends Series<K> {

    /**
     * Storage for the tasks in the series.
     */
    private List<Task> tasks;

    /**
     * Constructs a new series with the specified name.
     *
     * @param name  the series name ({@code null} not permitted).
     */
    public TaskSeries(K name) {
        super(name);
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the series and sends a
     * {@link org.jfree.data.general.SeriesChangeEvent} to all registered
     * listeners.
     *
     * @param task  the task ({@code null} not permitted).
     */
    public void add(Task task) {
        Args.nullNotPermitted(task, "task");
        this.tasks.add(task);
        fireSeriesChanged();
    }

    /**
     * Removes a task from the series and sends
     * a {@link org.jfree.data.general.SeriesChangeEvent}
     * to all registered listeners.
     *
     * @param task  the task.
     */
    public void remove(Task task) {
        this.tasks.remove(task);
        fireSeriesChanged();
    }

    /**
     * Removes all tasks from the series and sends
     * a {@link org.jfree.data.general.SeriesChangeEvent}
     * to all registered listeners.
     */
    public void removeAll() {
        this.tasks.clear();
        fireSeriesChanged();
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     */
    @Override
    public int getItemCount() {
        return this.tasks.size();
    }

    /**
     * Returns a task from the series.
     *
     * @param index  the task index (zero-based).
     *
     * @return The task.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Returns the task in the series that has the specified description.
     *
     * @param description  the name ({@code null} not permitted).
     *
     * @return The task (possibly {@code null}).
     */
    public Task get(String description) {
        Task result = null;
        for (Task t : this.tasks) {
            if (t.getDescription().equals(description)) {
                result = t;
                break;
            }
        }
        return result;
    }

    /**
     * Returns an unmodifialble list of the tasks in the series.
     *
     * @return The tasks.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param obj  the object to test against ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TaskSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        TaskSeries that = (TaskSeries) obj;
        if (!this.tasks.equals(that.tasks)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.tasks);
        return hash;
    }

    /**
     * Returns an independent copy of this series.
     *
     * @return A clone of the series.
     *
     * @throws CloneNotSupportedException if there is some problem cloning
     *     the dataset.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        TaskSeries clone = (TaskSeries) super.clone();
        clone.tasks = CloneUtils.cloneList(this.tasks);
        return clone;
    }
}
/* ======================================================
 * JFreeChart : a chart library for the Java(tm) platform
 * ======================================================
 *
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Project Info:  https://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -----------------
 * VectorSeries.java
 * -----------------
 * (C) Copyright 2007-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 */
/**
 * A list of (x,y, deltaX, deltaY) data items.
 *
 * @param <S> the series key type.
 *
 * @see VectorSeriesCollection
 */
public class VectorSeries<S extends Comparable<S>> extends ComparableObjectSeries<S> {

    /**
     * Creates a new empty series.
     *
     * @param key  the series key ({@code null} not permitted).
     */
    public VectorSeries(S key) {
        this(key, false, true);
    }

    /**
     * Constructs a new series that contains no data.  You can specify
     * whether duplicate x-values are allowed for the series.
     *
     * @param key  the series key ({@code null} not permitted).
     * @param autoSort  a flag that controls whether the items in the
     *                  series are sorted.
     * @param allowDuplicateXValues  a flag that controls whether duplicate
     *                               x-values are allowed.
     */
    public VectorSeries(S key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key, autoSort, allowDuplicateXValues);
    }

    /**
     * Adds a data item to the series.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     * @param deltaX  the vector x.
     * @param deltaY  the vector y.
     */
    public void add(double x, double y, double deltaX, double deltaY) {
        add(new VectorDataItem(x, y, deltaX, deltaY), true);
    }

    /**
     * Adds a data item to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param item  the data item ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @since 1.0.18
     */
    public void add(VectorDataItem item, boolean notify) {
        super.add(item, notify);
    }

    /**
     * Removes the item at the specified index and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     *
     * @return The item removed.
     */
    @Override
    public ComparableObjectItem remove(int index) {
        VectorDataItem result = (VectorDataItem) this.data.remove(index);
        fireSeriesChanged();
        return result;
    }

    /**
     * Returns the x-value for the specified item.
     *
     * @param index  the item index.
     *
     * @return The x-value.
     */
    public double getXValue(int index) {
        VectorDataItem item = (VectorDataItem) this.getDataItem(index);
        return item.getXValue();
    }

    /**
     * Returns the y-value for the specified item.
     *
     * @param index  the item index.
     *
     * @return The y-value.
     */
    public double getYValue(int index) {
        VectorDataItem item = (VectorDataItem) getDataItem(index);
        return item.getYValue();
    }

    /**
     * Returns the x-component of the vector for an item in the series.
     *
     * @param index  the item index.
     *
     * @return The x-component of the vector.
     */
    public double getVectorXValue(int index) {
        VectorDataItem item = (VectorDataItem) getDataItem(index);
        return item.getVectorX();
    }

    /**
     * Returns the y-component of the vector for an item in the series.
     *
     * @param index  the item index.
     *
     * @return The y-component of the vector.
     */
    public double getVectorYValue(int index) {
        VectorDataItem item = (VectorDataItem) getDataItem(index);
        return item.getVectorY();
    }

    /**
     * Returns the data item at the specified index.
     *
     * @param index  the item index.
     *
     * @return The data item.
     */
    @Override
    public ComparableObjectItem getDataItem(int index) {
        // overridden to make public
        return super.getDataItem(index);
    }
}
