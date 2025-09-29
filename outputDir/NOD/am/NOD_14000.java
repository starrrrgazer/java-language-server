package NOD.am;
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
 * ---------
 * Axis.java
 * ---------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Bill Kelemen;
 *                   Nicolas Brodu;
 *                   Peter Kolb (patches 1934255 and 2603321);
 *                   Andrew Mickish (patch 1870189); 
 *
 */
/**
 * The base class for all axes in JFreeChart.  Subclasses are divided into
 * those that display values ({@link ValueAxis}) and those that display
 * categories ({@link CategoryAxis}).
 */
public abstract class Axis implements ChartElement, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7719289504573298271L;

    /**
     * The default axis visibility ({@code true}).
     */
    public static final boolean DEFAULT_AXIS_VISIBLE = true;

    /**
     * The default axis label font ({@code Font("SansSerif", Font.PLAIN, 12)}).
     */
    public static final Font DEFAULT_AXIS_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * The default axis label paint ({@code Color.BLACK}).
     */
    public static final Paint DEFAULT_AXIS_LABEL_PAINT = Color.BLACK;

    /**
     * The default axis label insets ({@code RectangleInsets(3.0, 3.0, 3.0, 3.0)}).
     */
    public static final RectangleInsets DEFAULT_AXIS_LABEL_INSETS = new RectangleInsets(3.0, 3.0, 3.0, 3.0);

    /**
     * The default axis line paint ({@code Color.GRAY}).
     */
    public static final Paint DEFAULT_AXIS_LINE_PAINT = Color.GRAY;

    /**
     * The default axis line stroke ({@code BasicStroke(0.5f)}).
     */
    public static final Stroke DEFAULT_AXIS_LINE_STROKE = new BasicStroke(0.5f);

    /**
     * The default tick labels visibility ({@code true}).
     */
    public static final boolean DEFAULT_TICK_LABELS_VISIBLE = true;

    /**
     * The default tick label font ({@code Font("SansSerif", Font.PLAIN, 10)}).
     */
    public static final Font DEFAULT_TICK_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * The default tick label paint ({@code Color.BLACK}).
     */
    public static final Paint DEFAULT_TICK_LABEL_PAINT = Color.BLACK;

    /**
     * The default tick label insets ({@code RectangleInsets(2.0, 4.0, 2.0, 4.0)}).
     */
    public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS = new RectangleInsets(2.0, 4.0, 2.0, 4.0);

    /**
     * The default tick marks visible ({@code true}).
     */
    public static final boolean DEFAULT_TICK_MARKS_VISIBLE = true;

    /**
     * The default tick stroke ({@code BasicStroke(0.5f)}).
     */
    public static final Stroke DEFAULT_TICK_MARK_STROKE = new BasicStroke(0.5f);

    /**
     * The default tick paint ({@code Color.GRAY}).
     */
    public static final Paint DEFAULT_TICK_MARK_PAINT = Color.GRAY;

    /**
     * The default tick mark inside length ({@code 0.0f}).
     */
    public static final float DEFAULT_TICK_MARK_INSIDE_LENGTH = 0.0f;

    /**
     * The default tick mark outside length ({@code 2.0f}).
     */
    public static final float DEFAULT_TICK_MARK_OUTSIDE_LENGTH = 2.0f;

    /**
     * A flag indicating whether the axis is visible.
     */
    private boolean visible;

    /**
     * The label for the axis.
     */
    private String label;

    /**
     * An attributed label for the axis (overrides label if non-null).
     * We have to use this override method to preserve the API compatibility.
     */
    private transient AttributedString attributedLabel;

    /**
     * The font for displaying the axis label.
     */
    private Font labelFont;

    /**
     * The paint for drawing the axis label.
     */
    private transient Paint labelPaint;

    /**
     * The insets for the axis label.
     */
    private RectangleInsets labelInsets;

    /**
     * The label angle.
     */
    private double labelAngle;

    /**
     * The axis label location (new in 1.0.16).
     */
    private AxisLabelLocation labelLocation;

    /**
     * A flag that controls whether the axis line is visible.
     */
    private boolean axisLineVisible;

    /**
     * The stroke used for the axis line.
     */
    private transient Stroke axisLineStroke;

    /**
     * The paint used for the axis line.
     */
    private transient Paint axisLinePaint;

    /**
     * A flag that indicates whether tick labels are visible for the
     * axis.
     */
    private boolean tickLabelsVisible;

    /**
     * The font used to display the tick labels.
     */
    private Font tickLabelFont;

    /**
     * The color used to display the tick labels.
     */
    private transient Paint tickLabelPaint;

    /**
     * The blank space around each tick label.
     */
    private RectangleInsets tickLabelInsets;

    /**
     * A flag that indicates whether major tick marks are visible for
     * the axis.
     */
    private boolean tickMarksVisible;

    /**
     * The length of the major tick mark inside the data area (zero
     * permitted).
     */
    private float tickMarkInsideLength;

    /**
     * The length of the major tick mark outside the data area (zero
     * permitted).
     */
    private float tickMarkOutsideLength;

    /**
     * A flag that indicates whether minor tick marks are visible for the
     * axis.
     */
    private boolean minorTickMarksVisible;

    /**
     * The length of the minor tick mark inside the data area (zero permitted).
     */
    private float minorTickMarkInsideLength;

    /**
     * The length of the minor tick mark outside the data area (zero permitted).
     */
    private float minorTickMarkOutsideLength;

    /**
     * The stroke used to draw tick marks.
     */
    private transient Stroke tickMarkStroke;

    /**
     * The paint used to draw tick marks.
     */
    private transient Paint tickMarkPaint;

    /**
     * The fixed (horizontal or vertical) dimension for the axis.
     */
    private double fixedDimension;

    /**
     * A reference back to the plot that the axis is assigned to (can be
     * {@code null}).
     */
    private transient Plot plot;

    /**
     * Storage for registered listeners.
     */
    private transient EventListenerList listenerList;

    /**
     * Constructs an axis with the specific label and default values for other
     * attributes.
     *
     * @param label  the axis label ({@code null} permitted).
     */
    protected Axis(String label) {
        this.label = label;
        this.visible = DEFAULT_AXIS_VISIBLE;
        this.labelFont = DEFAULT_AXIS_LABEL_FONT;
        this.labelPaint = DEFAULT_AXIS_LABEL_PAINT;
        this.labelInsets = DEFAULT_AXIS_LABEL_INSETS;
        this.labelAngle = 0.0;
        this.labelLocation = AxisLabelLocation.MIDDLE;
        this.axisLineVisible = true;
        this.axisLinePaint = DEFAULT_AXIS_LINE_PAINT;
        this.axisLineStroke = DEFAULT_AXIS_LINE_STROKE;
        this.tickLabelsVisible = DEFAULT_TICK_LABELS_VISIBLE;
        this.tickLabelFont = DEFAULT_TICK_LABEL_FONT;
        this.tickLabelPaint = DEFAULT_TICK_LABEL_PAINT;
        this.tickLabelInsets = DEFAULT_TICK_LABEL_INSETS;
        this.tickMarksVisible = DEFAULT_TICK_MARKS_VISIBLE;
        this.tickMarkStroke = DEFAULT_TICK_MARK_STROKE;
        this.tickMarkPaint = DEFAULT_TICK_MARK_PAINT;
        this.tickMarkInsideLength = DEFAULT_TICK_MARK_INSIDE_LENGTH;
        this.tickMarkOutsideLength = DEFAULT_TICK_MARK_OUTSIDE_LENGTH;
        this.minorTickMarksVisible = false;
        this.minorTickMarkInsideLength = 0.0f;
        this.minorTickMarkOutsideLength = 2.0f;
        this.plot = null;
        this.listenerList = new EventListenerList();
    }

    /**
     * Returns {@code true} if the axis is visible, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setVisible(boolean)
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets a flag that controls whether the axis is visible and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #isVisible()
     */
    public void setVisible(boolean flag) {
        if (flag != this.visible) {
            this.visible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the label for the axis.
     *
     * @return The label for the axis ({@code null} possible).
     *
     * @see #getLabelFont()
     * @see #getLabelPaint()
     * @see #setLabel(String)
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label for the axis and sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param label  the new label ({@code null} permitted).
     *
     * @see #getLabel()
     * @see #setLabelFont(Font)
     * @see #setLabelPaint(Paint)
     */
    public void setLabel(String label) {
        this.label = label;
        fireChangeEvent();
    }

    /**
     * Returns the attributed label (the returned value is a copy, so
     * modifying it will not impact the state of the axis).  The default value
     * is {@code null}.
     *
     * @return The attributed label (possibly {@code null}).
     */
    public AttributedString getAttributedLabel() {
        if (this.attributedLabel != null) {
            return new AttributedString(this.attributedLabel.getIterator());
        } else {
            return null;
        }
    }

    /**
     * Sets the attributed label for the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  This is a
     * convenience method that converts the string into an
     * {@code AttributedString} using the current font attributes.
     *
     * @param label  the label ({@code null} permitted).
     */
    public void setAttributedLabel(String label) {
        setAttributedLabel(createAttributedLabel(label));
    }

    /**
     * Sets the attributed label for the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param label  the label ({@code null} permitted).
     */
    public void setAttributedLabel(AttributedString label) {
        if (label != null) {
            this.attributedLabel = new AttributedString(label.getIterator());
        } else {
            this.attributedLabel = null;
        }
        fireChangeEvent();
    }

    /**
     * Creates and returns an {@code AttributedString} with the specified
     * text and the labelFont and labelPaint applied as attributes.
     *
     * @param label  the label ({@code null} permitted).
     *
     * @return An attributed string or {@code null}.
     */
    public AttributedString createAttributedLabel(String label) {
        if (label == null) {
            return null;
        }
        AttributedString s = new AttributedString(label);
        s.addAttributes(this.labelFont.getAttributes(), 0, label.length());
        return s;
    }

    /**
     * Returns the font for the axis label.
     *
     * @return The font (never {@code null}).
     *
     * @see #setLabelFont(Font)
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the font for the axis label and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getLabelFont()
     */
    public void setLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        if (!this.labelFont.equals(font)) {
            this.labelFont = font;
            fireChangeEvent();
        }
    }

    /**
     * Returns the color/shade used to draw the axis label.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setLabelPaint(Paint)
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the paint used to draw the axis label and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelPaint()
     */
    public void setLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.labelPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the insets for the label (that is, the amount of blank space
     * that should be left around the label).
     *
     * @return The label insets (never {@code null}).
     *
     * @see #setLabelInsets(RectangleInsets)
     */
    public RectangleInsets getLabelInsets() {
        return this.labelInsets;
    }

    /**
     * Sets the insets for the axis label, and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param insets  the insets ({@code null} not permitted).
     *
     * @see #getLabelInsets()
     */
    public void setLabelInsets(RectangleInsets insets) {
        setLabelInsets(insets, true);
    }

    /**
     * Sets the insets for the axis label, and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param insets  the insets ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void setLabelInsets(RectangleInsets insets, boolean notify) {
        Args.nullNotPermitted(insets, "insets");
        if (!insets.equals(this.labelInsets)) {
            this.labelInsets = insets;
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    /**
     * Returns the angle of the axis label.
     *
     * @return The angle (in radians).
     *
     * @see #setLabelAngle(double)
     */
    public double getLabelAngle() {
        return this.labelAngle;
    }

    /**
     * Sets the angle for the label and sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param angle  the angle (in radians).
     *
     * @see #getLabelAngle()
     */
    public void setLabelAngle(double angle) {
        this.labelAngle = angle;
        fireChangeEvent();
    }

    /**
     * Returns the location of the axis label.  The default is
     * {@link AxisLabelLocation#MIDDLE}.
     *
     * @return The location of the axis label (never {@code null}).
     */
    public AxisLabelLocation getLabelLocation() {
        return this.labelLocation;
    }

    /**
     * Sets the axis label location and sends an {@link AxisChangeEvent} to
     * all registered listeners.
     *
     * @param location  the new location ({@code null} not permitted).
     */
    public void setLabelLocation(AxisLabelLocation location) {
        Args.nullNotPermitted(location, "location");
        this.labelLocation = location;
        fireChangeEvent();
    }

    /**
     * A flag that controls whether the axis line is drawn.
     *
     * @return A boolean.
     *
     * @see #getAxisLinePaint()
     * @see #getAxisLineStroke()
     * @see #setAxisLineVisible(boolean)
     */
    public boolean isAxisLineVisible() {
        return this.axisLineVisible;
    }

    /**
     * Sets a flag that controls whether the axis line is visible and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #isAxisLineVisible()
     * @see #setAxisLinePaint(Paint)
     * @see #setAxisLineStroke(Stroke)
     */
    public void setAxisLineVisible(boolean visible) {
        this.axisLineVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to draw the axis line.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setAxisLinePaint(Paint)
     */
    public Paint getAxisLinePaint() {
        return this.axisLinePaint;
    }

    /**
     * Sets the paint used to draw the axis line and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getAxisLinePaint()
     */
    public void setAxisLinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.axisLinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw the axis line.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setAxisLineStroke(Stroke)
     */
    public Stroke getAxisLineStroke() {
        return this.axisLineStroke;
    }

    /**
     * Sets the stroke used to draw the axis line and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getAxisLineStroke()
     */
    public void setAxisLineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.axisLineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns a flag indicating whether the tick labels are visible.
     *
     * @return The flag.
     *
     * @see #getTickLabelFont()
     * @see #getTickLabelPaint()
     * @see #setTickLabelsVisible(boolean)
     */
    public boolean isTickLabelsVisible() {
        return this.tickLabelsVisible;
    }

    /**
     * Sets the flag that determines whether the tick labels are
     * visible and sends an {@link AxisChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #isTickLabelsVisible()
     * @see #setTickLabelFont(Font)
     * @see #setTickLabelPaint(Paint)
     */
    public void setTickLabelsVisible(boolean flag) {
        if (flag != this.tickLabelsVisible) {
            this.tickLabelsVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that indicates whether the minor tick marks are
     * showing.
     *
     * @return The flag that indicates whether the minor tick marks are
     *         showing.
     *
     * @see #setMinorTickMarksVisible(boolean)
     */
    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }

    /**
     * Sets the flag that indicates whether the minor tick marks are
     * showing and sends an {@link AxisChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #isMinorTickMarksVisible()
     */
    public void setMinorTickMarksVisible(boolean flag) {
        if (flag != this.minorTickMarksVisible) {
            this.minorTickMarksVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the font used for the tick labels (if showing).
     *
     * @return The font (never {@code null}).
     *
     * @see #setTickLabelFont(Font)
     */
    public Font getTickLabelFont() {
        return this.tickLabelFont;
    }

    /**
     * Sets the font for the tick labels and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param font  the font ({@code null} not allowed).
     *
     * @see #getTickLabelFont()
     */
    public void setTickLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        if (!this.tickLabelFont.equals(font)) {
            this.tickLabelFont = font;
            fireChangeEvent();
        }
    }

    /**
     * Returns the color/shade used for the tick labels.
     *
     * @return The paint used for the tick labels.
     *
     * @see #setTickLabelPaint(Paint)
     */
    public Paint getTickLabelPaint() {
        return this.tickLabelPaint;
    }

    /**
     * Sets the paint used to draw tick labels (if they are showing) and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getTickLabelPaint()
     */
    public void setTickLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.tickLabelPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the insets for the tick labels.
     *
     * @return The insets (never {@code null}).
     *
     * @see #setTickLabelInsets(RectangleInsets)
     */
    public RectangleInsets getTickLabelInsets() {
        return this.tickLabelInsets;
    }

    /**
     * Sets the insets for the tick labels and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param insets  the insets ({@code null} not permitted).
     *
     * @see #getTickLabelInsets()
     */
    public void setTickLabelInsets(RectangleInsets insets) {
        Args.nullNotPermitted(insets, "insets");
        if (!this.tickLabelInsets.equals(insets)) {
            this.tickLabelInsets = insets;
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that indicates whether the tick marks are
     * showing.
     *
     * @return The flag that indicates whether the tick marks are
     *         showing.
     *
     * @see #setTickMarksVisible(boolean)
     */
    public boolean isTickMarksVisible() {
        return this.tickMarksVisible;
    }

    /**
     * Sets the flag that indicates whether the tick marks are showing
     * and sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #isTickMarksVisible()
     */
    public void setTickMarksVisible(boolean flag) {
        if (flag != this.tickMarksVisible) {
            this.tickMarksVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the inside length of the tick marks.
     *
     * @return The length.
     *
     * @see #getTickMarkOutsideLength()
     * @see #setTickMarkInsideLength(float)
     */
    public float getTickMarkInsideLength() {
        return this.tickMarkInsideLength;
    }

    /**
     * Sets the inside length of the tick marks and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param length  the new length.
     *
     * @see #getTickMarkInsideLength()
     */
    public void setTickMarkInsideLength(float length) {
        this.tickMarkInsideLength = length;
        fireChangeEvent();
    }

    /**
     * Returns the outside length of the tick marks.
     *
     * @return The length.
     *
     * @see #getTickMarkInsideLength()
     * @see #setTickMarkOutsideLength(float)
     */
    public float getTickMarkOutsideLength() {
        return this.tickMarkOutsideLength;
    }

    /**
     * Sets the outside length of the tick marks and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param length  the new length.
     *
     * @see #getTickMarkInsideLength()
     */
    public void setTickMarkOutsideLength(float length) {
        this.tickMarkOutsideLength = length;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw tick marks.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setTickMarkStroke(Stroke)
     */
    public Stroke getTickMarkStroke() {
        return this.tickMarkStroke;
    }

    /**
     * Sets the stroke used to draw tick marks and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getTickMarkStroke()
     */
    public void setTickMarkStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        if (!this.tickMarkStroke.equals(stroke)) {
            this.tickMarkStroke = stroke;
            fireChangeEvent();
        }
    }

    /**
     * Returns the paint used to draw tick marks (if they are showing).
     *
     * @return The paint (never {@code null}).
     *
     * @see #setTickMarkPaint(Paint)
     */
    public Paint getTickMarkPaint() {
        return this.tickMarkPaint;
    }

    /**
     * Sets the paint used to draw tick marks and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getTickMarkPaint()
     */
    public void setTickMarkPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.tickMarkPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the inside length of the minor tick marks.
     *
     * @return The length.
     *
     * @see #getMinorTickMarkOutsideLength()
     * @see #setMinorTickMarkInsideLength(float)
     */
    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }

    /**
     * Sets the inside length of the minor tick marks and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param length  the new length.
     *
     * @see #getMinorTickMarkInsideLength()
     */
    public void setMinorTickMarkInsideLength(float length) {
        this.minorTickMarkInsideLength = length;
        fireChangeEvent();
    }

    /**
     * Returns the outside length of the minor tick marks.
     *
     * @return The length.
     *
     * @see #getMinorTickMarkInsideLength()
     * @see #setMinorTickMarkOutsideLength(float)
     */
    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }

    /**
     * Sets the outside length of the minor tick marks and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param length  the new length.
     *
     * @see #getMinorTickMarkInsideLength()
     */
    public void setMinorTickMarkOutsideLength(float length) {
        this.minorTickMarkOutsideLength = length;
        fireChangeEvent();
    }

    /**
     * Returns the plot that the axis is assigned to.  This method will return
     * {@code null} if the axis is not currently assigned to a plot.
     *
     * @return The plot that the axis is assigned to (possibly {@code null}).
     *
     * @see #setPlot(Plot)
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Sets a reference to the plot that the axis is assigned to.
     * <P>
     * This method is used internally, you shouldn't need to call it yourself.
     *
     * @param plot  the plot.
     *
     * @see #getPlot()
     */
    public void setPlot(Plot plot) {
        this.plot = plot;
        configure();
    }

    /**
     * Returns the fixed dimension for the axis.
     *
     * @return The fixed dimension.
     *
     * @see #setFixedDimension(double)
     */
    public double getFixedDimension() {
        return this.fixedDimension;
    }

    /**
     * Sets the fixed dimension for the axis.
     * <P>
     * This is used when combining more than one plot on a chart.  In this case,
     * there may be several axes that need to have the same height or width so
     * that they are aligned.  This method is used to fix a dimension for the
     * axis (the context determines whether the dimension is horizontal or
     * vertical).
     *
     * @param dimension  the fixed dimension.
     *
     * @see #getFixedDimension()
     */
    public void setFixedDimension(double dimension) {
        this.fixedDimension = dimension;
    }

    /**
     * Configures the axis to work with the current plot.  Override this method
     * to perform any special processing (such as auto-rescaling).
     */
    public abstract void configure();

    /**
     * Estimates the space (height or width) required to draw the axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot that the axis belongs to.
     * @param plotArea  the area within which the plot (including axes) should
     *                  be drawn.
     * @param edge  the axis location.
     * @param space  space already reserved.
     *
     * @return The space required to draw the axis (including pre-reserved
     *         space).
     */
    public abstract AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space);

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location (determines where to draw the axis).
     * @param plotArea  the area within which the axes and plot should be drawn.
     * @param dataArea  the area within which the data should be drawn.
     * @param edge  the axis location ({@code null} not permitted).
     * @param plotState  collects information about the plot
     *                   ({@code null} permitted).
     *
     * @return The axis state (never {@code null}).
     */
    public abstract AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState);

    /**
     * Calculates the positions of the ticks for the axis, storing the results
     * in the tick list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the area inside the axes.
     * @param edge  the edge on which the axis is located.
     *
     * @return The list of ticks.
     */
    public abstract List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge);

    /**
     * Creates an entity for the axis and adds it to the rendering info.
     * If {@code plotState} is {@code null}, this means that rendering info
     * is not being collected so this method simply returns without doing
     * anything.
     *
     * @param cursor  the initial cursor value.
     * @param state  the axis state after completion of the drawing with a
     *     possibly updated cursor position.
     * @param dataArea  the data area.
     * @param edge  the edge ({@code null} not permitted).
     * @param plotState  the PlotRenderingInfo from which a reference to the
     *     entity collection can be obtained ({@code null} permitted).
     */
    protected void createAndAddEntity(double cursor, AxisState state, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        Args.nullNotPermitted(edge, "edge");
        if (plotState == null || plotState.getOwner() == null) {
            // no need to create entity if we can't save it anyways...
            return;
        }
        Rectangle2D hotspot = null;
        switch(edge) {
            case TOP:
                hotspot = new Rectangle2D.Double(dataArea.getX(), state.getCursor(), dataArea.getWidth(), cursor - state.getCursor());
                break;
            case BOTTOM:
                hotspot = new Rectangle2D.Double(dataArea.getX(), cursor, dataArea.getWidth(), state.getCursor() - cursor);
                break;
            case LEFT:
                hotspot = new Rectangle2D.Double(state.getCursor(), dataArea.getY(), cursor - state.getCursor(), dataArea.getHeight());
                break;
            case RIGHT:
                hotspot = new Rectangle2D.Double(cursor, dataArea.getY(), state.getCursor() - cursor, dataArea.getHeight());
                break;
            default:
                break;
        }
        EntityCollection e = plotState.getOwner().getEntityCollection();
        if (e != null) {
            e.add(new AxisEntity(hotspot, this));
        }
    }

    /**
     * Registers an object for notification of changes to the axis.
     *
     * @param listener  the object that is being registered.
     *
     * @see #removeChangeListener(AxisChangeListener)
     */
    public void addChangeListener(AxisChangeListener listener) {
        this.listenerList.add(AxisChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the axis.
     *
     * @param listener  the object to deregister.
     *
     * @see #addChangeListener(AxisChangeListener)
     */
    public void removeChangeListener(AxisChangeListener listener) {
        this.listenerList.remove(AxisChangeListener.class, listener);
    }

    /**
     * Returns {@code true} if the specified object is registered with
     * the dataset as a listener.  Most applications won't need to call this
     * method, it exists mainly for use by unit testing code.
     *
     * @param listener  the listener.
     *
     * @return A boolean.
     */
    public boolean hasListener(EventListener listener) {
        List list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    /**
     * Notifies all registered listeners that the axis has changed.
     * The AxisChangeEvent provides information about the change.
     *
     * @param event  information about the change to the axis.
     */
    protected void notifyListeners(AxisChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == AxisChangeListener.class) {
                ((AxisChangeListener) listeners[i + 1]).axisChanged(event);
            }
        }
    }

    /**
     * Sends an {@link AxisChangeEvent} to all registered listeners.
     */
    protected void fireChangeEvent() {
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns a rectangle that encloses the axis label.  This is typically
     * used for layout purposes (it gives the maximum dimensions of the label).
     *
     * @param g2  the graphics device.
     * @param edge  the edge of the plot area along which the axis is measuring.
     *
     * @return The enclosing rectangle.
     */
    protected Rectangle2D getLabelEnclosure(Graphics2D g2, RectangleEdge edge) {
        Rectangle2D result = new Rectangle2D.Double();
        Rectangle2D bounds = null;
        if (this.attributedLabel != null) {
            TextLayout layout = new TextLayout(this.attributedLabel.getIterator(), g2.getFontRenderContext());
            bounds = layout.getBounds();
        } else {
            String axisLabel = getLabel();
            if (axisLabel != null && !axisLabel.equals("")) {
                FontMetrics fm = g2.getFontMetrics(getLabelFont());
                bounds = TextUtils.getTextBounds(axisLabel, g2, fm);
            }
        }
        if (bounds != null) {
            RectangleInsets insets = getLabelInsets();
            bounds = insets.createOutsetRectangle(bounds);
            double angle = getLabelAngle();
            if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                angle = angle - Math.PI / 2.0;
            }
            double x = bounds.getCenterX();
            double y = bounds.getCenterY();
            AffineTransform transformer = AffineTransform.getRotateInstance(angle, x, y);
            Shape labelBounds = transformer.createTransformedShape(bounds);
            result = labelBounds.getBounds2D();
        }
        return result;
    }

    /**
     * Returns the x-coordinate for the point to which the axis label should
     * be aligned.
     *
     * @param location  the axis label location ({@code null} not permitted).
     * @param dataArea  the display area in which the data will be rendered ({@code null} not permitted).
     *
     * @return The x-coordinate.
     */
    protected double labelLocationX(AxisLabelLocation location, Rectangle2D dataArea) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return dataArea.getMaxX();
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return dataArea.getCenterX();
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return dataArea.getMinX();
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    /**
     * Returns the y-coordinate for the point to which the axis label should
     * be aligned.
     *
     * @param location  the location ({@code null} not permitted).
     * @param dataArea  the data area ({@code null} not permitted).
     *
     * @return The y-coordinate.
     */
    protected double labelLocationY(AxisLabelLocation location, Rectangle2D dataArea) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return dataArea.getMinY();
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return dataArea.getCenterY();
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return dataArea.getMaxY();
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    /**
     * Returns the appropriate horizontal text anchor for the specified axis
     * location.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @return The text anchor (never {@code null}).
     */
    protected TextAnchor labelAnchorH(AxisLabelLocation location) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return TextAnchor.CENTER_RIGHT;
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return TextAnchor.CENTER;
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return TextAnchor.CENTER_LEFT;
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    /**
     * Returns the appropriate vertical text anchor for the specified axis
     * location.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @return The text anchor (never {@code null}).
     */
    protected TextAnchor labelAnchorV(AxisLabelLocation location) {
        if (location.equals(AxisLabelLocation.HIGH_END)) {
            return TextAnchor.CENTER_RIGHT;
        }
        if (location.equals(AxisLabelLocation.MIDDLE)) {
            return TextAnchor.CENTER;
        }
        if (location.equals(AxisLabelLocation.LOW_END)) {
            return TextAnchor.CENTER_LEFT;
        }
        throw new RuntimeException("Unexpected AxisLabelLocation: " + location);
    }

    /**
     * Draws the axis label.
     *
     * @param label  the label text.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the area inside the axes.
     * @param edge  the location of the axis.
     * @param state  the axis state ({@code null} not permitted).
     *
     * @return Information about the axis.
     */
    protected AxisState drawLabel(String label, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        // it is unlikely that 'state' will be null, but check anyway...
        Args.nullNotPermitted(state, "state");
        if ((label == null) || (label.equals(""))) {
            return state;
        }
        Font font = getLabelFont();
        RectangleInsets insets = getLabelInsets();
        g2.setFont(font);
        g2.setPaint(getLabelPaint());
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D labelBounds = TextUtils.getTextBounds(label, g2, fm);
        if (edge == RectangleEdge.TOP) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = labelLocationX(this.labelLocation, dataArea);
            double labely = state.getCursor() - insets.getBottom() - labelBounds.getHeight() / 2.0;
            TextAnchor anchor = labelAnchorH(this.labelLocation);
            TextUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle(), TextAnchor.CENTER);
            state.cursorUp(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
        } else if (edge == RectangleEdge.BOTTOM) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = labelLocationX(this.labelLocation, dataArea);
            double labely = state.getCursor() + insets.getTop() + labelBounds.getHeight() / 2.0;
            TextAnchor anchor = labelAnchorH(this.labelLocation);
            TextUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle(), TextAnchor.CENTER);
            state.cursorDown(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
        } else if (edge == RectangleEdge.LEFT) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle() - Math.PI / 2.0, labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = state.getCursor() - insets.getRight() - labelBounds.getWidth() / 2.0;
            double labely = labelLocationY(this.labelLocation, dataArea);
            TextAnchor anchor = labelAnchorV(this.labelLocation);
            TextUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle() - Math.PI / 2.0, anchor);
            state.cursorLeft(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
        } else if (edge == RectangleEdge.RIGHT) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle() + Math.PI / 2.0, labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = state.getCursor() + insets.getLeft() + labelBounds.getWidth() / 2.0;
            double labely = labelLocationY(this.labelLocation, dataArea);
            TextAnchor anchor = labelAnchorV(this.labelLocation);
            TextUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle() + Math.PI / 2.0, anchor);
            state.cursorRight(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
        }
        return state;
    }

    /**
     * Draws the axis label.
     *
     * @param label  the label text.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the area inside the axes.
     * @param edge  the location of the axis.
     * @param state  the axis state ({@code null} not permitted).
     *
     * @return Information about the axis.
     */
    protected AxisState drawAttributedLabel(AttributedString label, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        // it is unlikely that 'state' will be null, but check anyway...
        Args.nullNotPermitted(state, "state");
        if (label == null) {
            return state;
        }
        RectangleInsets insets = getLabelInsets();
        g2.setFont(getLabelFont());
        g2.setPaint(getLabelPaint());
        TextLayout layout = new TextLayout(this.attributedLabel.getIterator(), g2.getFontRenderContext());
        Rectangle2D labelBounds = layout.getBounds();
        if (edge == RectangleEdge.TOP) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = labelLocationX(this.labelLocation, dataArea);
            double labely = state.getCursor() - insets.getBottom() - labelBounds.getHeight() / 2.0;
            TextAnchor anchor = labelAnchorH(this.labelLocation);
            AttrStringUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle(), TextAnchor.CENTER);
            state.cursorUp(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
        } else if (edge == RectangleEdge.BOTTOM) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = labelLocationX(this.labelLocation, dataArea);
            double labely = state.getCursor() + insets.getTop() + labelBounds.getHeight() / 2.0;
            TextAnchor anchor = labelAnchorH(this.labelLocation);
            AttrStringUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle(), TextAnchor.CENTER);
            state.cursorDown(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
        } else if (edge == RectangleEdge.LEFT) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle() - Math.PI / 2.0, labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = state.getCursor() - insets.getRight() - labelBounds.getWidth() / 2.0;
            double labely = labelLocationY(this.labelLocation, dataArea);
            TextAnchor anchor = labelAnchorV(this.labelLocation);
            AttrStringUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle() - Math.PI / 2.0, anchor);
            state.cursorLeft(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
        } else if (edge == RectangleEdge.RIGHT) {
            AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle() + Math.PI / 2.0, labelBounds.getCenterX(), labelBounds.getCenterY());
            Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
            labelBounds = rotatedLabelBounds.getBounds2D();
            double labelx = state.getCursor() + insets.getLeft() + labelBounds.getWidth() / 2.0;
            double labely = labelLocationY(this.labelLocation, dataArea);
            TextAnchor anchor = labelAnchorV(this.labelLocation);
            AttrStringUtils.drawRotatedString(label, g2, (float) labelx, (float) labely, anchor, getLabelAngle() + Math.PI / 2.0, anchor);
            state.cursorRight(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
        }
        return state;
    }

    /**
     * Draws an axis line at the current cursor position and edge.
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor position.
     * @param dataArea  the data area.
     * @param edge  the edge.
     */
    protected void drawAxisLine(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        Line2D axisLine = null;
        double x = dataArea.getX();
        double y = dataArea.getY();
        if (edge == RectangleEdge.TOP) {
            axisLine = new Line2D.Double(x, cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.BOTTOM) {
            axisLine = new Line2D.Double(x, cursor, dataArea.getMaxX(), cursor);
        } else if (edge == RectangleEdge.LEFT) {
            axisLine = new Line2D.Double(cursor, y, cursor, dataArea.getMaxY());
        } else if (edge == RectangleEdge.RIGHT) {
            axisLine = new Line2D.Double(cursor, y, cursor, dataArea.getMaxY());
        }
        g2.setPaint(this.axisLinePaint);
        g2.setStroke(this.axisLineStroke);
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(axisLine);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Returns a clone of the axis.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the axis does
     *         not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Axis clone = (Axis) super.clone();
        // It's up to the plot which clones up to restore the correct references
        clone.plot = null;
        clone.listenerList = new EventListenerList();
        return clone;
    }

    /**
     * Tests this axis for equality with another object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Axis)) {
            return false;
        }
        Axis that = (Axis) obj;
        if (this.visible != that.visible) {
            return false;
        }
        if (!Objects.equals(this.label, that.label)) {
            return false;
        }
        if (!AttributedStringUtils.equal(this.attributedLabel, that.attributedLabel)) {
            return false;
        }
        if (!Objects.equals(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!Objects.equals(this.labelInsets, that.labelInsets)) {
            return false;
        }
        if (this.labelAngle != that.labelAngle) {
            return false;
        }
        if (!this.labelLocation.equals(that.labelLocation)) {
            return false;
        }
        if (this.axisLineVisible != that.axisLineVisible) {
            return false;
        }
        if (!Objects.equals(this.axisLineStroke, that.axisLineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.axisLinePaint, that.axisLinePaint)) {
            return false;
        }
        if (this.tickLabelsVisible != that.tickLabelsVisible) {
            return false;
        }
        if (!Objects.equals(this.tickLabelFont, that.tickLabelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.tickLabelPaint, that.tickLabelPaint)) {
            return false;
        }
        if (!Objects.equals(this.tickLabelInsets, that.tickLabelInsets)) {
            return false;
        }
        if (this.tickMarksVisible != that.tickMarksVisible) {
            return false;
        }
        if (this.tickMarkInsideLength != that.tickMarkInsideLength) {
            return false;
        }
        if (this.tickMarkOutsideLength != that.tickMarkOutsideLength) {
            return false;
        }
        if (!PaintUtils.equal(this.tickMarkPaint, that.tickMarkPaint)) {
            return false;
        }
        if (!Objects.equals(this.tickMarkStroke, that.tickMarkStroke)) {
            return false;
        }
        if (this.minorTickMarksVisible != that.minorTickMarksVisible) {
            return false;
        }
        if (this.minorTickMarkInsideLength != that.minorTickMarkInsideLength) {
            return false;
        }
        if (this.minorTickMarkOutsideLength != that.minorTickMarkOutsideLength) {
            return false;
        }
        if (this.fixedDimension != that.fixedDimension) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        if (this.label != null) {
            hash = 83 * hash + this.label.hashCode();
        }
        return hash;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeAttributedString(this.attributedLabel, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
        SerialUtils.writePaint(this.tickLabelPaint, stream);
        SerialUtils.writeStroke(this.axisLineStroke, stream);
        SerialUtils.writePaint(this.axisLinePaint, stream);
        SerialUtils.writeStroke(this.tickMarkStroke, stream);
        SerialUtils.writePaint(this.tickMarkPaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributedLabel = SerialUtils.readAttributedString(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
        this.tickLabelPaint = SerialUtils.readPaint(stream);
        this.axisLineStroke = SerialUtils.readStroke(stream);
        this.axisLinePaint = SerialUtils.readPaint(stream);
        this.tickMarkStroke = SerialUtils.readStroke(stream);
        this.tickMarkPaint = SerialUtils.readPaint(stream);
        this.listenerList = new EventListenerList();
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
 */
/**
 * A list of {@link TextLine} objects that form a block of text.
 *
 * @see TextUtils#createTextBlock(String, Font, Paint)
 */
public class TextBlock implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -4333175719424385526L;

    /**
     * Storage for the lines of text.
     */
    private List<TextLine> lines;

    /**
     * The alignment of the lines.
     */
    private HorizontalAlignment lineAlignment;

    /**
     * Creates a new empty text block.
     */
    public TextBlock() {
        this.lines = new ArrayList<>();
        this.lineAlignment = HorizontalAlignment.CENTER;
    }

    /**
     * Returns the alignment of the lines of text within the block.
     *
     * @return The alignment (never {@code null}).
     */
    public HorizontalAlignment getLineAlignment() {
        return this.lineAlignment;
    }

    /**
     * Sets the alignment of the lines of text within the block.
     *
     * @param alignment  the alignment ({@code null} not permitted).
     */
    public void setLineAlignment(HorizontalAlignment alignment) {
        Args.nullNotPermitted(alignment, "alignment");
        this.lineAlignment = alignment;
    }

    /**
     * Adds a line of text that will be displayed using the specified font and
     * color.
     *
     * @param text  the text ({@code null} not permitted).
     * @param font  the font ({@code null} not permitted).
     * @param paint  the paint ({@code null} not permitted).
     */
    public void addLine(String text, Font font, Paint paint) {
        addLine(new TextLine(text, font, paint));
    }

    /**
     * Adds a {@link TextLine} to the block.
     *
     * @param line  the line.
     */
    public void addLine(TextLine line) {
        this.lines.add(line);
    }

    /**
     * Returns the last line in the block.
     *
     * @return The last line in the block.
     */
    public TextLine getLastLine() {
        TextLine last = null;
        final int index = this.lines.size() - 1;
        if (index >= 0) {
            last = this.lines.get(index);
        }
        return last;
    }

    /**
     * Returns an unmodifiable list containing the lines for the text block.
     *
     * @return A list of {@link TextLine} objects.
     */
    public List<TextLine> getLines() {
        return Collections.unmodifiableList(this.lines);
    }

    /**
     * Returns the width and height of the text block.
     *
     * @param g2  the graphics device.
     *
     * @return The width and height.
     */
    public Size2D calculateDimensions(Graphics2D g2) {
        double width = 0.0;
        double height = 0.0;
        for (TextLine line : this.lines) {
            final Size2D dimension = line.calculateDimensions(g2);
            width = Math.max(width, dimension.getWidth());
            height = height + dimension.getHeight();
        }
        return new Size2D(width, height);
    }

    /**
     * Returns the bounds of the text block.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param anchorX  the x-coordinate for the anchor point.
     * @param anchorY  the y-coordinate for the anchor point.
     * @param anchor  the text block anchor ({@code null} not permitted).
     * @param rotateX  the x-coordinate for the rotation point.
     * @param rotateY  the y-coordinate for the rotation point.
     * @param angle  the rotation angle.
     *
     * @return The bounds.
     */
    public Shape calculateBounds(Graphics2D g2, float anchorX, float anchorY, TextBlockAnchor anchor, float rotateX, float rotateY, double angle) {
        Size2D d = calculateDimensions(g2);
        float[] offsets = calculateOffsets(anchor, d.getWidth(), d.getHeight());
        Rectangle2D bounds = new Rectangle2D.Double(anchorX + offsets[0], anchorY + offsets[1], d.getWidth(), d.getHeight());
        Shape rotatedBounds = ShapeUtils.rotateShape(bounds, angle, rotateX, rotateY);
        return rotatedBounds;
    }

    /**
     * Draws the text block at a specific location.
     *
     * @param g2  the graphics device.
     * @param x  the x-coordinate for the anchor point.
     * @param y  the y-coordinate for the anchor point.
     * @param anchor  the anchor point.
     */
    public void draw(Graphics2D g2, float x, float y, TextBlockAnchor anchor) {
        draw(g2, x, y, anchor, 0.0f, 0.0f, 0.0);
    }

    /**
     * Draws the text block, aligning it with the specified anchor point and
     * rotating it about the specified rotation point.
     *
     * @param g2  the graphics device.
     * @param anchorX  the x-coordinate for the anchor point.
     * @param anchorY  the y-coordinate for the anchor point.
     * @param anchor  the point on the text block that is aligned to the
     *                anchor point.
     * @param rotateX  the x-coordinate for the rotation point.
     * @param rotateY  the x-coordinate for the rotation point.
     * @param angle  the rotation (in radians).
     */
    public void draw(Graphics2D g2, float anchorX, float anchorY, TextBlockAnchor anchor, float rotateX, float rotateY, double angle) {
        Size2D d = calculateDimensions(g2);
        float[] offsets = calculateOffsets(anchor, d.getWidth(), d.getHeight());
        float yCursor = 0.0f;
        for (TextLine line : this.lines) {
            Size2D dimension = line.calculateDimensions(g2);
            float lineOffset = 0.0f;
            if (this.lineAlignment == HorizontalAlignment.CENTER) {
                lineOffset = (float) (d.getWidth() - dimension.getWidth()) / 2.0f;
            } else if (this.lineAlignment == HorizontalAlignment.RIGHT) {
                lineOffset = (float) (d.getWidth() - dimension.getWidth());
            }
            line.draw(g2, anchorX + offsets[0] + lineOffset, anchorY + offsets[1] + yCursor, TextAnchor.TOP_LEFT, rotateX, rotateY, angle);
            yCursor = yCursor + (float) dimension.getHeight();
        }
    }

    /**
     * Calculates the x and y offsets required to align the text block with the
     * specified anchor point.  This assumes that the top left of the text
     * block is at (0.0, 0.0).
     *
     * @param anchor  the anchor position.
     * @param width  the width of the text block.
     * @param height  the height of the text block.
     *
     * @return The offsets (float[0] = x offset, float[1] = y offset).
     */
    private float[] calculateOffsets(TextBlockAnchor anchor, double width, double height) {
        float[] result = new float[2];
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor == TextBlockAnchor.TOP_CENTER || anchor == TextBlockAnchor.CENTER || anchor == TextBlockAnchor.BOTTOM_CENTER) {
            xAdj = (float) -width / 2.0f;
        } else if (anchor == TextBlockAnchor.TOP_RIGHT || anchor == TextBlockAnchor.CENTER_RIGHT || anchor == TextBlockAnchor.BOTTOM_RIGHT) {
            xAdj = (float) -width;
        }
        if (anchor == TextBlockAnchor.TOP_LEFT || anchor == TextBlockAnchor.TOP_CENTER || anchor == TextBlockAnchor.TOP_RIGHT) {
            yAdj = 0.0f;
        } else if (anchor == TextBlockAnchor.CENTER_LEFT || anchor == TextBlockAnchor.CENTER || anchor == TextBlockAnchor.CENTER_RIGHT) {
            yAdj = (float) -height / 2.0f;
        } else if (anchor == TextBlockAnchor.BOTTOM_LEFT || anchor == TextBlockAnchor.BOTTOM_CENTER || anchor == TextBlockAnchor.BOTTOM_RIGHT) {
            yAdj = (float) -height;
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
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
        if (obj instanceof TextBlock) {
            TextBlock block = (TextBlock) obj;
            return this.lines.equals(block.lines);
        }
        return false;
    }

    /**
     * Returns a hash code for this object.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return (this.lines != null ? this.lines.hashCode() : 0);
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
 * ---------------------------
 * StandardXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2001-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Andreas Schneider;
 *                   Norbert Kiesel (for TBD Networks);
 *                   Christian W. Zuckschwerdt;
 *                   Bill Kelemen;
 *                   Nicolas Brodu (for Astrium and EADS Corporate Research
 *                   Center);
 *
 */
/**
 * Standard item renderer for an {@link XYPlot}.  This class can draw (a)
 * shapes at each point, or (b) lines between points, or (c) both shapes and
 * lines.
 * <P>
 * This renderer has been retained for historical reasons and, in general, you
 * should use the {@link XYLineAndShapeRenderer} class instead.
 */
public class StandardXYItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -3271351259436865995L;

    /**
     * Constant for the type of rendering (shapes only).
     */
    public static final int SHAPES = 1;

    /**
     * Constant for the type of rendering (lines only).
     */
    public static final int LINES = 2;

    /**
     * Constant for the type of rendering (shapes and lines).
     */
    public static final int SHAPES_AND_LINES = SHAPES | LINES;

    /**
     * Constant for the type of rendering (images only).
     */
    public static final int IMAGES = 4;

    /**
     * Constant for the type of rendering (discontinuous lines).
     */
    public static final int DISCONTINUOUS = 8;

    /**
     * Constant for the type of rendering (discontinuous lines).
     */
    public static final int DISCONTINUOUS_LINES = LINES | DISCONTINUOUS;

    /**
     * A flag indicating whether shapes are drawn at each XY point.
     */
    private boolean baseShapesVisible;

    /**
     * A flag indicating whether lines are drawn between XY points.
     */
    private boolean plotLines;

    /**
     * A flag indicating whether images are drawn between XY points.
     */
    private boolean plotImages;

    /**
     * A flag controlling whether discontinuous lines are used.
     */
    private boolean plotDiscontinuous;

    /**
     * Specifies how the gap threshold value is interpreted.
     */
    private UnitType gapThresholdType = UnitType.RELATIVE;

    /**
     * Threshold for deciding when to discontinue a line.
     */
    private double gapThreshold = 1.0;

    /**
     * A table of flags that control (per series) whether shapes are
     * filled.
     */
    private Map<Integer, Boolean> seriesShapesFilledMap;

    /**
     * The default value returned by the getShapeFilled() method.
     */
    private boolean baseShapesFilled;

    /**
     * A flag that controls whether each series is drawn as a single
     * path.
     */
    private boolean drawSeriesLineAsPath;

    /**
     * The shape that is used to represent a line in the legend.
     * This should never be set to {@code null}.
     */
    private transient Shape legendLine;

    /**
     * Constructs a new renderer.
     */
    public StandardXYItemRenderer() {
        this(LINES, null);
    }

    /**
     * Constructs a new renderer.  To specify the type of renderer, use one of
     * the constants: {@link #SHAPES}, {@link #LINES} or
     * {@link #SHAPES_AND_LINES}.
     *
     * @param type  the type.
     */
    public StandardXYItemRenderer(int type) {
        this(type, null);
    }

    /**
     * Constructs a new renderer.  To specify the type of renderer, use one of
     * the constants: {@link #SHAPES}, {@link #LINES} or
     * {@link #SHAPES_AND_LINES}.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the item label generator ({@code null}
     *                          permitted).
     */
    public StandardXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator) {
        this(type, toolTipGenerator, null);
    }

    /**
     * Constructs a new renderer.  To specify the type of renderer, use one of
     * the constants: {@link #SHAPES}, {@link #LINES} or
     * {@link #SHAPES_AND_LINES}.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the item label generator ({@code null}
     *                          permitted).
     * @param urlGenerator  the URL generator.
     */
    public StandardXYItemRenderer(int type, XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {
        super();
        setDefaultToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
        if ((type & SHAPES) != 0) {
            this.baseShapesVisible = true;
        }
        if ((type & LINES) != 0) {
            this.plotLines = true;
        }
        if ((type & IMAGES) != 0) {
            this.plotImages = true;
        }
        if ((type & DISCONTINUOUS) != 0) {
            this.plotDiscontinuous = true;
        }
        this.seriesShapesFilledMap = new HashMap<>();
        this.baseShapesFilled = true;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.drawSeriesLineAsPath = false;
    }

    /**
     * Returns true if shapes are being plotted by the renderer.
     *
     * @return {@code true} if shapes are being plotted by the renderer.
     *
     * @see #setBaseShapesVisible
     */
    public boolean getBaseShapesVisible() {
        return this.baseShapesVisible;
    }

    /**
     * Sets the flag that controls whether a shape is plotted at each
     * data point.
     *
     * @param flag  the flag.
     *
     * @see #getBaseShapesVisible
     */
    public void setBaseShapesVisible(boolean flag) {
        if (this.baseShapesVisible != flag) {
            this.baseShapesVisible = flag;
            fireChangeEvent();
        }
    }

    // SHAPES FILLED
    /**
     * Returns the flag used to control whether the shape for an item is
     * filled.
     * <p>
     * The default implementation passes control to the
     * {@code getSeriesShapesFilled()} method.  You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     *
     * @see #getSeriesShapesFilled(int)
     */
    public boolean getItemShapeFilled(int series, int item) {
        // otherwise look up the paint table
        Boolean flag = this.seriesShapesFilledMap.get(series);
        if (flag != null) {
            return flag;
        } else {
            return this.baseShapesFilled;
        }
    }

    /**
     * Returns the flag used to control whether the shapes for a series
     * are filled.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public Boolean getSeriesShapesFilled(int series) {
        return this.seriesShapesFilledMap.get(series);
    }

    /**
     * Sets the 'shapes filled' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag.
     *
     * @see #getSeriesShapesFilled(int)
     */
    public void setSeriesShapesFilled(int series, Boolean flag) {
        this.seriesShapesFilledMap.put(series, flag);
        fireChangeEvent();
    }

    /**
     * Returns the base 'shape filled' attribute.
     *
     * @return The base flag.
     *
     * @see #setBaseShapesFilled(boolean)
     */
    public boolean getBaseShapesFilled() {
        return this.baseShapesFilled;
    }

    /**
     * Sets the base 'shapes filled' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getBaseShapesFilled()
     */
    public void setBaseShapesFilled(boolean flag) {
        this.baseShapesFilled = flag;
    }

    /**
     * Returns true if lines are being plotted by the renderer.
     *
     * @return {@code true} if lines are being plotted by the renderer.
     *
     * @see #setPlotLines(boolean)
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Sets the flag that controls whether a line is plotted between
     * each data point and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getPlotLines()
     */
    public void setPlotLines(boolean flag) {
        if (this.plotLines != flag) {
            this.plotLines = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the gap threshold type (relative or absolute).
     *
     * @return The type.
     *
     * @see #setGapThresholdType(UnitType)
     */
    public UnitType getGapThresholdType() {
        return this.gapThresholdType;
    }

    /**
     * Sets the gap threshold type and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param thresholdType  the type ({@code null} not permitted).
     *
     * @see #getGapThresholdType()
     */
    public void setGapThresholdType(UnitType thresholdType) {
        Args.nullNotPermitted(thresholdType, "thresholdType");
        this.gapThresholdType = thresholdType;
        fireChangeEvent();
    }

    /**
     * Returns the gap threshold for discontinuous lines.
     *
     * @return The gap threshold.
     *
     * @see #setGapThreshold(double)
     */
    public double getGapThreshold() {
        return this.gapThreshold;
    }

    /**
     * Sets the gap threshold for discontinuous lines and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param t  the threshold.
     *
     * @see #getGapThreshold()
     */
    public void setGapThreshold(double t) {
        this.gapThreshold = t;
        fireChangeEvent();
    }

    /**
     * Returns true if images are being plotted by the renderer.
     *
     * @return {@code true} if images are being plotted by the renderer.
     *
     * @see #setPlotImages(boolean)
     */
    public boolean getPlotImages() {
        return this.plotImages;
    }

    /**
     * Sets the flag that controls whether an image is drawn at each
     * data point and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #getPlotImages()
     */
    public void setPlotImages(boolean flag) {
        if (this.plotImages != flag) {
            this.plotImages = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns a flag that controls whether the renderer shows
     * discontinuous lines.
     *
     * @return {@code true} if lines should be discontinuous.
     */
    public boolean getPlotDiscontinuous() {
        return this.plotDiscontinuous;
    }

    /**
     * Sets the flag that controls whether the renderer shows
     * discontinuous lines, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the new flag value.
     */
    public void setPlotDiscontinuous(boolean flag) {
        if (this.plotDiscontinuous != flag) {
            this.plotDiscontinuous = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns a flag that controls whether each series is drawn as a
     * single path.
     *
     * @return A boolean.
     *
     * @see #setDrawSeriesLineAsPath(boolean)
     */
    public boolean getDrawSeriesLineAsPath() {
        return this.drawSeriesLineAsPath;
    }

    /**
     * Sets the flag that controls whether each series is drawn as a
     * single path.
     *
     * @param flag  the flag.
     *
     * @see #getDrawSeriesLineAsPath()
     */
    public void setDrawSeriesLineAsPath(boolean flag) {
        this.drawSeriesLineAsPath = flag;
    }

    /**
     * Returns the shape used to represent a line in the legend.
     *
     * @return The legend line (never {@code null}).
     *
     * @see #setLegendLine(Shape)
     */
    public Shape getLegendLine() {
        return this.legendLine;
    }

    /**
     * Sets the shape used as a line in each legend item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param line  the line ({@code null} not permitted).
     *
     * @see #getLegendLine()
     */
    public void setLegendLine(Shape line) {
        Args.nullNotPermitted(line, "line");
        this.legendLine = line;
        fireChangeEvent();
    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        LegendItem result = null;
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset != null) {
            if (getItemVisible(series, 0)) {
                String label = getLegendItemLabelGenerator().generateLabel(dataset, series);
                String description = label;
                String toolTipText = null;
                if (getLegendItemToolTipGenerator() != null) {
                    toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
                }
                String urlText = null;
                if (getLegendItemURLGenerator() != null) {
                    urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
                }
                Shape shape = lookupLegendShape(series);
                boolean shapeFilled = getItemShapeFilled(series, 0);
                Paint paint = lookupSeriesPaint(series);
                Paint linePaint = paint;
                Stroke lineStroke = lookupSeriesStroke(series);
                result = new LegendItem(label, description, toolTipText, urlText, this.baseShapesVisible, shape, shapeFilled, paint, !shapeFilled, paint, lineStroke, this.plotLines, this.legendLine, lineStroke, linePaint);
                result.setLabelFont(lookupLegendTextFont(series));
                Paint labelPaint = lookupLegendTextPaint(series);
                if (labelPaint != null) {
                    result.setLabelPaint(labelPaint);
                }
                result.setDataset(dataset);
                result.setDatasetIndex(datasetIndex);
                result.setSeriesKey(dataset.getSeriesKey(series));
                result.setSeriesIndex(series);
            }
        }
        return result;
    }

    /**
     * Records the state for the renderer.  This is used to preserve state
     * information between calls to the drawItem() method for a single chart
     * drawing.
     */
    public static class State extends XYItemRendererState {

        /**
         * The path for the current series.
         */
        public GeneralPath seriesPath;

        /**
         * The series index.
         */
        private int seriesIndex;

        /**
         * A flag that indicates if the last (x, y) point was 'good'
         * (non-null).
         */
        private boolean lastPointGood;

        /**
         * Creates a new state instance.
         *
         * @param info  the plot rendering info.
         */
        public State(PlotRenderingInfo info) {
            super(info);
        }

        /**
         * Returns a flag that indicates if the last point drawn (in the
         * current series) was 'good' (non-null).
         *
         * @return A boolean.
         */
        public boolean isLastPointGood() {
            return this.lastPointGood;
        }

        /**
         * Sets a flag that indicates if the last point drawn (in the current
         * series) was 'good' (non-null).
         *
         * @param good  the flag.
         */
        public void setLastPointGood(boolean good) {
            this.lastPointGood = good;
        }

        /**
         * Returns the series index for the current path.
         *
         * @return The series index for the current path.
         */
        public int getSeriesIndex() {
            return this.seriesIndex;
        }

        /**
         * Sets the series index for the current path.
         *
         * @param index  the index.
         */
        public void setSeriesIndex(int index) {
            this.seriesIndex = index;
        }
    }

    /**
     * Initialises the renderer.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to
     * maintain. The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return The renderer state.
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        State state = new State(info);
        state.seriesPath = new GeneralPath();
        state.seriesIndex = -1;
        return state;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        boolean itemVisible = getItemVisible(series, item);
        // setup for collecting optional entity info...
        Shape entityArea = null;
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(seriesStroke);
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(x1) || Double.isNaN(y1)) {
            itemVisible = false;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (getPlotLines()) {
            if (this.drawSeriesLineAsPath) {
                State s = (State) state;
                if (s.getSeriesIndex() != series) {
                    // we are starting a new series path
                    s.seriesPath.reset();
                    s.lastPointGood = false;
                    s.setSeriesIndex(series);
                }
                // update path to reflect latest point
                if (itemVisible && !Double.isNaN(transX1) && !Double.isNaN(transY1)) {
                    float x = (float) transX1;
                    float y = (float) transY1;
                    if (orientation == PlotOrientation.HORIZONTAL) {
                        x = (float) transY1;
                        y = (float) transX1;
                    }
                    if (s.isLastPointGood()) {
                        // TODO: check threshold
                        s.seriesPath.lineTo(x, y);
                    } else {
                        s.seriesPath.moveTo(x, y);
                    }
                    s.setLastPointGood(true);
                } else {
                    s.setLastPointGood(false);
                }
                if (item == dataset.getItemCount(series) - 1) {
                    if (s.seriesIndex == series) {
                        // draw path
                        g2.setStroke(lookupSeriesStroke(series));
                        g2.setPaint(lookupSeriesPaint(series));
                        g2.draw(s.seriesPath);
                    }
                }
            } else if (item != 0 && itemVisible) {
                // get the previous data point...
                double x0 = dataset.getXValue(series, item - 1);
                double y0 = dataset.getYValue(series, item - 1);
                if (!Double.isNaN(x0) && !Double.isNaN(y0)) {
                    boolean drawLine = true;
                    if (getPlotDiscontinuous()) {
                        // only draw a line if the gap between the current and
                        // previous data point is within the threshold
                        int numX = dataset.getItemCount(series);
                        double minX = dataset.getXValue(series, 0);
                        double maxX = dataset.getXValue(series, numX - 1);
                        if (this.gapThresholdType == UnitType.ABSOLUTE) {
                            drawLine = Math.abs(x1 - x0) <= this.gapThreshold;
                        } else {
                            drawLine = Math.abs(x1 - x0) <= ((maxX - minX) / numX * getGapThreshold());
                        }
                    }
                    if (drawLine) {
                        double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
                        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
                        // only draw if we have good values
                        if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
                            return;
                        }
                        if (orientation == PlotOrientation.HORIZONTAL) {
                            state.workingLine.setLine(transY0, transX0, transY1, transX1);
                        } else if (orientation == PlotOrientation.VERTICAL) {
                            state.workingLine.setLine(transX0, transY0, transX1, transY1);
                        }
                        if (state.workingLine.intersects(dataArea)) {
                            g2.draw(state.workingLine);
                        }
                    }
                }
            }
        }
        // we needed to get this far even for invisible items, to ensure that
        // seriesPath updates happened, but now there is nothing more we need
        // to do for non-visible items...
        if (!itemVisible) {
            return;
        }
        if (getBaseShapesVisible()) {
            Shape shape = getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transY1, transX1);
            } else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transX1, transY1);
            }
            if (shape.intersects(dataArea)) {
                if (getItemShapeFilled(series, item)) {
                    g2.fill(shape);
                } else {
                    g2.draw(shape);
                }
            }
            entityArea = shape;
        }
        if (getPlotImages()) {
            Image image = getImage(plot, series, item, transX1, transY1);
            if (image != null) {
                Point hotspot = getImageHotspot(plot, series, item, transX1, transY1, image);
                g2.drawImage(image, (int) (transX1 - hotspot.getX()), (int) (transY1 - hotspot.getY()), null);
                entityArea = new Rectangle2D.Double(transX1 - hotspot.getX(), transY1 - hotspot.getY(), image.getWidth(null), image.getHeight(null));
            }
        }
        double xx = transX1;
        double yy = transY1;
        if (orientation == PlotOrientation.HORIZONTAL) {
            xx = transY1;
            yy = transX1;
        }
        // draw the item label if there is one...
        if (isItemLabelVisible(series, item)) {
            drawItemLabel(g2, orientation, dataset, series, item, xx, yy, (y1 < 0.0));
        }
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(crosshairState, x1, y1, datasetIndex, transX1, transY1, orientation);
        // add an entity for the item...
        if (entities != null && ShapeUtils.isPointInRect(dataArea, xx, yy)) {
            addEntity(entities, entityArea, dataset, series, item, xx, yy);
        }
    }

    /**
     * Tests this renderer for equality with another object.
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
        if (!(obj instanceof StandardXYItemRenderer)) {
            return false;
        }
        StandardXYItemRenderer that = (StandardXYItemRenderer) obj;
        if (this.baseShapesVisible != that.baseShapesVisible) {
            return false;
        }
        if (this.plotLines != that.plotLines) {
            return false;
        }
        if (this.plotImages != that.plotImages) {
            return false;
        }
        if (this.plotDiscontinuous != that.plotDiscontinuous) {
            return false;
        }
        if (this.gapThresholdType != that.gapThresholdType) {
            return false;
        }
        if (this.gapThreshold != that.gapThreshold) {
            return false;
        }
        if (!this.seriesShapesFilledMap.equals(that.seriesShapesFilledMap)) {
            return false;
        }
        if (this.baseShapesFilled != that.baseShapesFilled) {
            return false;
        }
        if (this.drawSeriesLineAsPath != that.drawSeriesLineAsPath) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the renderer cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        StandardXYItemRenderer clone = (StandardXYItemRenderer) super.clone();
        clone.seriesShapesFilledMap = new HashMap<>(this.seriesShapesFilledMap);
        clone.legendLine = CloneUtils.clone(this.legendLine);
        return clone;
    }

    ////////////////////////////////////////////////////////////////////////////
    // PROTECTED METHODS
    // These provide the opportunity to subclass the standard renderer and
    // create custom effects.
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Returns the image used to draw a single data item.
     *
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param series  the series index.
     * @param item  the item index.
     * @param x  the x value of the item.
     * @param y  the y value of the item.
     *
     * @return The image.
     *
     * @see #getPlotImages()
     */
    protected Image getImage(Plot plot, int series, int item, double x, double y) {
        // this method must be overridden if you want to display images
        return null;
    }

    /**
     * Returns the hotspot of the image used to draw a single data item.
     * The hotspot is the point relative to the top left of the image
     * that should indicate the data item. The default is the center of the
     * image.
     *
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param image  the image (can be used to get size information about the
     *               image)
     * @param series  the series index
     * @param item  the item index
     * @param x  the x value of the item
     * @param y  the y value of the item
     *
     * @return The hotspot used to draw the data item.
     */
    protected Point getImageHotspot(Plot plot, int series, int item, double x, double y, Image image) {
        int height = image.getHeight(null);
        int width = image.getWidth(null);
        return new Point(width / 2, height / 2);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.legendLine = SerialUtils.readShape(stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeShape(this.legendLine, stream);
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
 * -----------------------
 * GradientBarPainter.java
 * -----------------------
 * (C) Copyright 2008-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * An implementation of the {@link BarPainter} interface that uses several
 * gradient fills to enrich the appearance of the bars.
 */
public class GradientBarPainter implements BarPainter, Serializable {

    /**
     * The division point between the first and second gradient regions.
     */
    private double g1;

    /**
     * The division point between the second and third gradient regions.
     */
    private double g2;

    /**
     * The division point between the third and fourth gradient regions.
     */
    private double g3;

    /**
     * Creates a new instance.
     */
    public GradientBarPainter() {
        this(0.10, 0.20, 0.80);
    }

    /**
     * Creates a new instance.
     *
     * @param g1  percentage value defining the line between regions 1 and 2.
     * @param g2  percentage value defining the line between regions 2 and 3.
     * @param g3  percentage value defining the line between regions 3 and 4.
     */
    public GradientBarPainter(double g1, double g2, double g3) {
        this.g1 = g1;
        this.g2 = g2;
        this.g3 = g3;
    }

    /**
     * Paints a single bar instance.
     *
     * @param g2  the graphics target.
     * @param renderer  the renderer.
     * @param row  the row index.
     * @param column  the column index.
     * @param bar  the bar
     * @param base  indicates which side of the rectangle is the base of the
     *              bar.
     */
    @Override
    public void paintBar(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) {
        Paint itemPaint = renderer.getItemPaint(row, column);
        Color c0, c1;
        if (itemPaint instanceof Color) {
            c0 = (Color) itemPaint;
            c1 = c0.brighter();
        } else if (itemPaint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) itemPaint;
            c0 = gp.getColor1();
            c1 = gp.getColor2();
        } else {
            c0 = Color.BLUE;
            c1 = Color.BLUE.brighter();
        }
        // as a special case, if the bar colour has alpha == 0, we draw
        // nothing.
        if (c0.getAlpha() == 0) {
            return;
        }
        if (base == RectangleEdge.TOP || base == RectangleEdge.BOTTOM) {
            Rectangle2D[] regions = splitVerticalBar(bar, this.g1, this.g2, this.g3);
            GradientPaint gp = new GradientPaint((float) regions[0].getMinX(), 0.0f, c0, (float) regions[0].getMaxX(), 0.0f, Color.WHITE);
            g2.setPaint(gp);
            g2.fill(regions[0]);
            gp = new GradientPaint((float) regions[1].getMinX(), 0.0f, Color.WHITE, (float) regions[1].getMaxX(), 0.0f, c0);
            g2.setPaint(gp);
            g2.fill(regions[1]);
            gp = new GradientPaint((float) regions[2].getMinX(), 0.0f, c0, (float) regions[2].getMaxX(), 0.0f, c1);
            g2.setPaint(gp);
            g2.fill(regions[2]);
            gp = new GradientPaint((float) regions[3].getMinX(), 0.0f, c1, (float) regions[3].getMaxX(), 0.0f, c0);
            g2.setPaint(gp);
            g2.fill(regions[3]);
        } else if (base == RectangleEdge.LEFT || base == RectangleEdge.RIGHT) {
            Rectangle2D[] regions = splitHorizontalBar(bar, this.g1, this.g2, this.g3);
            GradientPaint gp = new GradientPaint(0.0f, (float) regions[0].getMinY(), c0, 0.0f, (float) regions[0].getMaxY(), Color.WHITE);
            g2.setPaint(gp);
            g2.fill(regions[0]);
            gp = new GradientPaint(0.0f, (float) regions[1].getMinY(), Color.WHITE, 0.0f, (float) regions[1].getMaxY(), c0);
            g2.setPaint(gp);
            g2.fill(regions[1]);
            gp = new GradientPaint(0.0f, (float) regions[2].getMinY(), c0, 0.0f, (float) regions[2].getMaxY(), c1);
            g2.setPaint(gp);
            g2.fill(regions[2]);
            gp = new GradientPaint(0.0f, (float) regions[3].getMinY(), c1, 0.0f, (float) regions[3].getMaxY(), c0);
            g2.setPaint(gp);
            g2.fill(regions[3]);
        }
        // draw the outline...
        if (renderer.isDrawBarOutline()) /*&& state.getBarWidth() > renderer.BAR_OUTLINE_WIDTH_THRESHOLD*/
        {
            Stroke stroke = renderer.getItemOutlineStroke(row, column);
            Paint paint = renderer.getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
    }

    /**
     * Paints a single bar instance.
     *
     * @param g2  the graphics target.
     * @param renderer  the renderer.
     * @param row  the row index.
     * @param column  the column index.
     * @param bar  the bar
     * @param base  indicates which side of the rectangle is the base of the
     *              bar.
     * @param pegShadow  peg the shadow to the base of the bar?
     */
    @Override
    public void paintBarShadow(Graphics2D g2, BarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
        // handle a special case - if the bar colour has alpha == 0, it is
        // invisible so we shouldn't draw any shadow
        Paint itemPaint = renderer.getItemPaint(row, column);
        if (itemPaint instanceof Color) {
            Color c = (Color) itemPaint;
            if (c.getAlpha() == 0) {
                return;
            }
        }
        RectangularShape shadow = createShadow(bar, renderer.getShadowXOffset(), renderer.getShadowYOffset(), base, pegShadow);
        g2.setPaint(renderer.getShadowPaint());
        g2.fill(shadow);
    }

    /**
     * Creates a shadow for the bar.
     *
     * @param bar  the bar shape.
     * @param xOffset  the x-offset for the shadow.
     * @param yOffset  the y-offset for the shadow.
     * @param base  the edge that is the base of the bar.
     * @param pegShadow  peg the shadow to the base?
     *
     * @return A rectangle for the shadow.
     */
    private Rectangle2D createShadow(RectangularShape bar, double xOffset, double yOffset, RectangleEdge base, boolean pegShadow) {
        double x0 = bar.getMinX();
        double x1 = bar.getMaxX();
        double y0 = bar.getMinY();
        double y1 = bar.getMaxY();
        if (base == RectangleEdge.TOP) {
            x0 += xOffset;
            x1 += xOffset;
            if (!pegShadow) {
                y0 += yOffset;
            }
            y1 += yOffset;
        } else if (base == RectangleEdge.BOTTOM) {
            x0 += xOffset;
            x1 += xOffset;
            y0 += yOffset;
            if (!pegShadow) {
                y1 += yOffset;
            }
        } else if (base == RectangleEdge.LEFT) {
            if (!pegShadow) {
                x0 += xOffset;
            }
            x1 += xOffset;
            y0 += yOffset;
            y1 += yOffset;
        } else if (base == RectangleEdge.RIGHT) {
            x0 += xOffset;
            if (!pegShadow) {
                x1 += xOffset;
            }
            y0 += yOffset;
            y1 += yOffset;
        }
        return new Rectangle2D.Double(x0, y0, (x1 - x0), (y1 - y0));
    }

    /**
     * Splits a bar into subregions (elsewhere, these subregions will have
     * different gradients applied to them).
     *
     * @param bar  the bar shape.
     * @param a  the first division.
     * @param b  the second division.
     * @param c  the third division.
     *
     * @return An array containing four subregions.
     */
    private Rectangle2D[] splitVerticalBar(RectangularShape bar, double a, double b, double c) {
        Rectangle2D[] result = new Rectangle2D[4];
        double x0 = bar.getMinX();
        double x1 = Math.rint(x0 + (bar.getWidth() * a));
        double x2 = Math.rint(x0 + (bar.getWidth() * b));
        double x3 = Math.rint(x0 + (bar.getWidth() * c));
        result[0] = new Rectangle2D.Double(bar.getMinX(), bar.getMinY(), x1 - x0, bar.getHeight());
        result[1] = new Rectangle2D.Double(x1, bar.getMinY(), x2 - x1, bar.getHeight());
        result[2] = new Rectangle2D.Double(x2, bar.getMinY(), x3 - x2, bar.getHeight());
        result[3] = new Rectangle2D.Double(x3, bar.getMinY(), bar.getMaxX() - x3, bar.getHeight());
        return result;
    }

    /**
     * Splits a bar into subregions (elsewhere, these subregions will have
     * different gradients applied to them).
     *
     * @param bar  the bar shape.
     * @param a  the first division.
     * @param b  the second division.
     * @param c  the third division.
     *
     * @return An array containing four subregions.
     */
    private Rectangle2D[] splitHorizontalBar(RectangularShape bar, double a, double b, double c) {
        Rectangle2D[] result = new Rectangle2D[4];
        double y0 = bar.getMinY();
        double y1 = Math.rint(y0 + (bar.getHeight() * a));
        double y2 = Math.rint(y0 + (bar.getHeight() * b));
        double y3 = Math.rint(y0 + (bar.getHeight() * c));
        result[0] = new Rectangle2D.Double(bar.getMinX(), bar.getMinY(), bar.getWidth(), y1 - y0);
        result[1] = new Rectangle2D.Double(bar.getMinX(), y1, bar.getWidth(), y2 - y1);
        result[2] = new Rectangle2D.Double(bar.getMinX(), y2, bar.getWidth(), y3 - y2);
        result[3] = new Rectangle2D.Double(bar.getMinX(), y3, bar.getWidth(), bar.getMaxY() - y3);
        return result;
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the obj ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GradientBarPainter)) {
            return false;
        }
        GradientBarPainter that = (GradientBarPainter) obj;
        if (this.g1 != that.g1) {
            return false;
        }
        if (this.g2 != that.g2) {
            return false;
        }
        if (this.g3 != that.g3) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int hash = 37;
        hash = HashUtils.hashCode(hash, this.g1);
        hash = HashUtils.hashCode(hash, this.g2);
        hash = HashUtils.hashCode(hash, this.g3);
        return hash;
    }
}
