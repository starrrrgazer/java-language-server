package LOC.p;
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
 * ----------------------------------------
 * DefaultBoxAndWhiskerCategoryDataset.java
 * ----------------------------------------
 * (C) Copyright 2003-present, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for Australian Institute of Marine
 *                   Science);
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A convenience class that provides a default implementation of the
 * {@link BoxAndWhiskerCategoryDataset} interface.
 *
 * @param <R> the row key type.
 * @param <C> the column key type.
 */
public class DefaultBoxAndWhiskerCategoryDataset<R extends Comparable<R>, C extends Comparable<C>> extends AbstractDataset implements BoxAndWhiskerCategoryDataset<R, C>, RangeInfo, PublicCloneable {

    /**
     * Storage for the data.
     */
    protected KeyedObjects2D<R, C> data;

    /**
     * The minimum range value.
     */
    private double minimumRangeValue;

    /**
     * The row index for the cell that the minimum range value comes from.
     */
    private int minimumRangeValueRow;

    /**
     * The column index for the cell that the minimum range value comes from.
     */
    private int minimumRangeValueColumn;

    /**
     * The maximum range value.
     */
    private double maximumRangeValue;

    /**
     * The row index for the cell that the maximum range value comes from.
     */
    private int maximumRangeValueRow;

    /**
     * The column index for the cell that the maximum range value comes from.
     */
    private int maximumRangeValueColumn;

    /**
     * Creates a new dataset.
     */
    public DefaultBoxAndWhiskerCategoryDataset() {
        super();
        this.data = new KeyedObjects2D<>();
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
    }

    /**
     * Adds a list of values relating to one box-and-whisker entity to the
     * table.  The various median values are calculated.
     *
     * @param list  a collection of values from which the various medians will
     *              be calculated.
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @see #add(BoxAndWhiskerItem, Comparable, Comparable)
     */
    public void add(List<? extends Number> list, R rowKey, C columnKey) {
        BoxAndWhiskerItem item = BoxAndWhiskerCalculator.calculateBoxAndWhiskerStatistics(list);
        add(item, rowKey, columnKey);
    }

    /**
     * Adds a list of values relating to one Box and Whisker entity to the
     * table.  The various median values are calculated.
     *
     * @param item  a box and whisker item ({@code null} not permitted).
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @see #add(List, Comparable, Comparable)
     */
    public void add(BoxAndWhiskerItem item, R rowKey, C columnKey) {
        this.data.addObject(item, rowKey, columnKey);
        // update cached min and max values
        int r = this.data.getRowIndex(rowKey);
        int c = this.data.getColumnIndex(columnKey);
        if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn == c) || (this.minimumRangeValueRow == r && this.minimumRangeValueColumn == c)) {
            updateBounds();
        } else {
            double minval = Double.NaN;
            if (item.getMinOutlier() != null) {
                minval = item.getMinOutlier().doubleValue();
            }
            double maxval = Double.NaN;
            if (item.getMaxOutlier() != null) {
                maxval = item.getMaxOutlier().doubleValue();
            }
            if (Double.isNaN(this.maximumRangeValue)) {
                this.maximumRangeValue = maxval;
                this.maximumRangeValueRow = r;
                this.maximumRangeValueColumn = c;
            } else if (maxval > this.maximumRangeValue) {
                this.maximumRangeValue = maxval;
                this.maximumRangeValueRow = r;
                this.maximumRangeValueColumn = c;
            }
            if (Double.isNaN(this.minimumRangeValue)) {
                this.minimumRangeValue = minval;
                this.minimumRangeValueRow = r;
                this.minimumRangeValueColumn = c;
            } else if (minval < this.minimumRangeValue) {
                this.minimumRangeValue = minval;
                this.minimumRangeValueRow = r;
                this.minimumRangeValueColumn = c;
            }
        }
        fireDatasetChanged();
    }

    /**
     * Removes an item from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @see #add(BoxAndWhiskerItem, Comparable, Comparable)
     *
     * @since 1.0.7
     */
    public void remove(R rowKey, C columnKey) {
        // defer null argument checks
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        this.data.removeObject(rowKey, columnKey);
        // if this cell held a maximum and/or minimum value, we'll need to
        // update the cached bounds...
        if ((this.maximumRangeValueRow == r && this.maximumRangeValueColumn == c) || (this.minimumRangeValueRow == r && this.minimumRangeValueColumn == c)) {
            updateBounds();
        }
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowIndex  the row index.
     *
     * @see #removeColumn(int)
     *
     * @since 1.0.7
     */
    public void removeRow(int rowIndex) {
        this.data.removeRow(rowIndex);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Removes a row from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param rowKey  the row key.
     *
     * @see #removeColumn(Comparable)
     *
     * @since 1.0.7
     */
    public void removeRow(R rowKey) {
        this.data.removeRow(rowKey);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnIndex  the column index.
     *
     * @see #removeRow(int)
     *
     * @since 1.0.7
     */
    public void removeColumn(int columnIndex) {
        this.data.removeColumn(columnIndex);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Removes a column from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param columnKey  the column key.
     *
     * @see #removeRow(Comparable)
     *
     * @since 1.0.7
     */
    public void removeColumn(C columnKey) {
        this.data.removeColumn(columnKey);
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Clears all data from the dataset and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @since 1.0.7
     */
    public void clear() {
        this.data.clear();
        updateBounds();
        fireDatasetChanged();
    }

    /**
     * Return an item from within the dataset.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return The item.
     */
    public BoxAndWhiskerItem getItem(int row, int column) {
        return (BoxAndWhiskerItem) this.data.getObject(row, column);
    }

    /**
     * Returns the value for an item.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return The value.
     *
     * @see #getMedianValue(int, int)
     * @see #getValue(Comparable, Comparable)
     */
    @Override
    public Number getValue(int row, int column) {
        return getMedianValue(row, column);
    }

    /**
     * Returns the value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return The value.
     *
     * @see #getMedianValue(Comparable, Comparable)
     * @see #getValue(int, int)
     */
    @Override
    public Number getValue(R rowKey, C columnKey) {
        return getMedianValue(rowKey, columnKey);
    }

    /**
     * Returns the mean value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The mean value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMeanValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getMean();
        }
        return result;
    }

    /**
     * Returns the mean value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The mean value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMeanValue(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMean();
        }
        return result;
    }

    /**
     * Returns the median value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The median value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMedianValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getMedian();
        }
        return result;
    }

    /**
     * Returns the median value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the columnKey.
     *
     * @return The median value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMedianValue(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMedian();
        }
        return result;
    }

    /**
     * Returns the first quartile value.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The first quartile value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getQ1Value(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getQ1();
        }
        return result;
    }

    /**
     * Returns the first quartile value.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The first quartile value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getQ1Value(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getQ1();
        }
        return result;
    }

    /**
     * Returns the third quartile value.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The third quartile value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getQ3Value(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getQ3();
        }
        return result;
    }

    /**
     * Returns the third quartile value.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The third quartile value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getQ3Value(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getQ3();
        }
        return result;
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key ({@code null} not permitted).
     *
     * @return The column index.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public int getColumnIndex(C key) {
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns a column key.
     *
     * @param column  the column index (zero-based).
     *
     * @return The column key.
     *
     * @see #getColumnIndex(Comparable)
     */
    @Override
    public C getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     *
     * @see #getRowKeys()
     */
    @Override
    public List<C> getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key ({@code null} not permitted).
     *
     * @return The row index.
     *
     * @see #getRowKey(int)
     */
    @Override
    public int getRowIndex(R key) {
        // defer null argument check
        return this.data.getRowIndex(key);
    }

    /**
     * Returns a row key.
     *
     * @param row  the row index (zero-based).
     *
     * @return The row key.
     *
     * @see #getRowIndex(Comparable)
     */
    @Override
    public R getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     *
     * @see #getColumnKeys()
     */
    @Override
    public List<R> getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     *
     * @see #getColumnCount()
     */
    @Override
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     *
     * @see #getRowCount()
     */
    @Override
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Returns the minimum y-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     *
     * @return The minimum value.
     *
     * @see #getRangeUpperBound(boolean)
     */
    @Override
    public double getRangeLowerBound(boolean includeInterval) {
        return this.minimumRangeValue;
    }

    /**
     * Returns the maximum y-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     *
     * @return The maximum value.
     *
     * @see #getRangeLowerBound(boolean)
     */
    @Override
    public double getRangeUpperBound(boolean includeInterval) {
        return this.maximumRangeValue;
    }

    /**
     * Returns the range of the values in this dataset's range.
     *
     * @param includeInterval  a flag that determines whether the
     *                         y-interval is taken into account.
     *
     * @return The range.
     */
    @Override
    public Range getRangeBounds(boolean includeInterval) {
        return new Range(this.minimumRangeValue, this.maximumRangeValue);
    }

    /**
     * Returns the minimum regular (non outlier) value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The minimum regular value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMinRegularValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getMinRegularValue();
        }
        return result;
    }

    /**
     * Returns the minimum regular (non outlier) value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The minimum regular value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMinRegularValue(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMinRegularValue();
        }
        return result;
    }

    /**
     * Returns the maximum regular (non outlier) value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The maximum regular value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMaxRegularValue(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getMaxRegularValue();
        }
        return result;
    }

    /**
     * Returns the maximum regular (non outlier) value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The maximum regular value.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMaxRegularValue(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMaxRegularValue();
        }
        return result;
    }

    /**
     * Returns the minimum outlier (non farout) value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The minimum outlier.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMinOutlier(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getMinOutlier();
        }
        return result;
    }

    /**
     * Returns the minimum outlier (non farout) value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The minimum outlier.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMinOutlier(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMinOutlier();
        }
        return result;
    }

    /**
     * Returns the maximum outlier (non farout) value for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The maximum outlier.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMaxOutlier(int row, int column) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            result = item.getMaxOutlier();
        }
        return result;
    }

    /**
     * Returns the maximum outlier (non farout) value for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The maximum outlier.
     *
     * @see #getItem(int, int)
     */
    @Override
    public Number getMaxOutlier(R rowKey, C columnKey) {
        Number result = null;
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            result = item.getMaxOutlier();
        }
        return result;
    }

    /**
     * Returns a list of outlier values for an item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return A list of outlier values.
     *
     * @see #getItem(int, int)
     */
    @Override
    public List<? extends Number> getOutliers(int row, int column) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(row, column);
        if (item != null) {
            return item.getOutliers();
        }
        return null;
    }

    /**
     * Returns a list of outlier values for an item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return A list of outlier values.
     *
     * @see #getItem(int, int)
     */
    @Override
    public List<? extends Number> getOutliers(R rowKey, C columnKey) {
        BoxAndWhiskerItem item = (BoxAndWhiskerItem) this.data.getObject(rowKey, columnKey);
        if (item != null) {
            return item.getOutliers();
        }
        return null;
    }

    /**
     * Resets the cached bounds, by iterating over the entire dataset to find
     * the current bounds.
     */
    private void updateBounds() {
        this.minimumRangeValue = Double.NaN;
        this.minimumRangeValueRow = -1;
        this.minimumRangeValueColumn = -1;
        this.maximumRangeValue = Double.NaN;
        this.maximumRangeValueRow = -1;
        this.maximumRangeValueColumn = -1;
        int rowCount = getRowCount();
        int columnCount = getColumnCount();
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                BoxAndWhiskerItem item = getItem(r, c);
                if (item != null) {
                    Number min = item.getMinOutlier();
                    if (min != null) {
                        double minv = min.doubleValue();
                        if (!Double.isNaN(minv)) {
                            if (minv < this.minimumRangeValue || Double.isNaN(this.minimumRangeValue)) {
                                this.minimumRangeValue = minv;
                                this.minimumRangeValueRow = r;
                                this.minimumRangeValueColumn = c;
                            }
                        }
                    }
                    Number max = item.getMaxOutlier();
                    if (max != null) {
                        double maxv = max.doubleValue();
                        if (!Double.isNaN(maxv)) {
                            if (maxv > this.maximumRangeValue || Double.isNaN(this.maximumRangeValue)) {
                                this.maximumRangeValue = maxv;
                                this.maximumRangeValueRow = r;
                                this.maximumRangeValueColumn = c;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
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
        if (obj instanceof DefaultBoxAndWhiskerCategoryDataset) {
            DefaultBoxAndWhiskerCategoryDataset dataset = (DefaultBoxAndWhiskerCategoryDataset) obj;
            return Objects.equals(this.data, dataset.data);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + Objects.hashCode(this.data);
        return hash;
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if cloning is not possible.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultBoxAndWhiskerCategoryDataset<R, C> clone = (DefaultBoxAndWhiskerCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D<R, C>) this.data.clone();
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
 * --------------
 * PolarPlot.java
 * --------------
 * (C) Copyright 2004-present, by Solution Engineering, Inc. and Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert;
 *                   Martin Hoeller (patches 1871902 and 2850344);
 * 
 */
/**
 * Plots data that is in (theta, radius) pairs where theta equal to zero is
 * due north and increases clockwise.
 */
public class PolarPlot extends Plot implements ValueAxisPlot, Zoomable, RendererChangeListener, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 3794383185924179525L;

    /**
     * The default margin.
     */
    private static final int DEFAULT_MARGIN = 20;

    /**
     * The annotation margin.
     */
    private static final double ANNOTATION_MARGIN = 7.0;

    /**
     * The default angle tick unit size.
     */
    public static final double DEFAULT_ANGLE_TICK_UNIT_SIZE = 45.0;

    /**
     * The default angle offset.
     */
    public static final double DEFAULT_ANGLE_OFFSET = -90.0;

    /**
     * The default grid line stroke.
     */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);

    /**
     * The default grid line paint.
     */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.GRAY;

    /**
     * The resourceBundle for the localization.
     */
    protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * The angles that are marked with gridlines.
     */
    private List<ValueTick> angleTicks;

    /**
     * The range axis (used for the y-values).
     */
    private Map<Integer, ValueAxis> axes;

    /**
     * The axis locations.
     */
    private final Map<Integer, PolarAxisLocation> axisLocations;

    /**
     * Storage for the datasets.
     */
    private Map<Integer, XYDataset> datasets;

    /**
     * Storage for the renderers.
     */
    private Map<Integer, PolarItemRenderer> renderers;

    /**
     * The tick unit that controls the spacing between the angular grid lines.
     */
    private TickUnit angleTickUnit;

    /**
     * An offset for the angles, to start with 0 degrees at north, east, south
     * or west.
     */
    private double angleOffset;

    /**
     * A flag indicating if the angles increase counterclockwise or clockwise.
     */
    private boolean counterClockwise;

    /**
     * A flag that controls whether the angle labels are visible.
     */
    private boolean angleLabelsVisible = true;

    /**
     * The font used to display the angle labels - never null.
     */
    private Font angleLabelFont = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * The paint used to display the angle labels.
     */
    private transient Paint angleLabelPaint = Color.BLACK;

    /**
     * A flag that controls whether the angular grid-lines are visible.
     */
    private boolean angleGridlinesVisible;

    /**
     * The stroke used to draw the angular grid-lines.
     */
    private transient Stroke angleGridlineStroke;

    /**
     * The paint used to draw the angular grid-lines.
     */
    private transient Paint angleGridlinePaint;

    /**
     * A flag that controls whether the radius grid-lines are visible.
     */
    private boolean radiusGridlinesVisible;

    /**
     * The stroke used to draw the radius grid-lines.
     */
    private transient Stroke radiusGridlineStroke;

    /**
     * The paint used to draw the radius grid-lines.
     */
    private transient Paint radiusGridlinePaint;

    /**
     * A flag that controls whether the radial minor grid-lines are visible.
     */
    private boolean radiusMinorGridlinesVisible;

    /**
     * The annotations for the plot.
     */
    private List<String> cornerTextItems = new ArrayList<>();

    /**
     * The actual margin in pixels.
     */
    private int margin;

    /**
     * An optional collection of legend items that can be returned by the
     * getLegendItems() method.
     */
    private LegendItemCollection fixedLegendItems;

    /**
     * Storage for the mapping between datasets/renderers and range axes.  The
     * keys in the map are Integer objects, corresponding to the dataset
     * index.  The values in the map are List&lt;Integer&gt; instances (corresponding
     * to the axis indices).  If the map contains no
     * entry for a dataset, it is assumed to map to the primary domain axis
     * (index = 0).
     */
    private final Map<Integer, List<Integer>> datasetToAxesMap;

    /**
     * Default constructor.
     */
    public PolarPlot() {
        this(null, null, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param radiusAxis  the radius axis ({@code null} permitted).
     * @param renderer  the renderer ({@code null} permitted).
     */
    public PolarPlot(XYDataset dataset, ValueAxis radiusAxis, PolarItemRenderer renderer) {
        super();
        this.datasets = new HashMap<>();
        this.datasets.put(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.angleTickUnit = new NumberTickUnit(DEFAULT_ANGLE_TICK_UNIT_SIZE);
        this.axes = new HashMap<>();
        this.datasetToAxesMap = new TreeMap<>();
        this.axes.put(0, radiusAxis);
        if (radiusAxis != null) {
            radiusAxis.setPlot(this);
            radiusAxis.addChangeListener(this);
        }
        // define the default locations for up to 8 axes...
        this.axisLocations = new HashMap<>();
        this.axisLocations.put(0, PolarAxisLocation.EAST_ABOVE);
        this.axisLocations.put(1, PolarAxisLocation.NORTH_LEFT);
        this.axisLocations.put(2, PolarAxisLocation.WEST_BELOW);
        this.axisLocations.put(3, PolarAxisLocation.SOUTH_RIGHT);
        this.axisLocations.put(4, PolarAxisLocation.EAST_BELOW);
        this.axisLocations.put(5, PolarAxisLocation.NORTH_RIGHT);
        this.axisLocations.put(6, PolarAxisLocation.WEST_ABOVE);
        this.axisLocations.put(7, PolarAxisLocation.SOUTH_LEFT);
        this.renderers = new HashMap<>();
        this.renderers.put(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.angleOffset = DEFAULT_ANGLE_OFFSET;
        this.counterClockwise = false;
        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.radiusGridlinesVisible = true;
        this.radiusMinorGridlinesVisible = true;
        this.radiusGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.margin = DEFAULT_MARGIN;
    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    @Override
    public String getPlotType() {
        return PolarPlot.localizationResources.getString("Polar_Plot");
    }

    /**
     * Returns the primary axis for the plot.
     *
     * @return The primary axis (possibly {@code null}).
     *
     * @see #setAxis(ValueAxis)
     */
    public ValueAxis getAxis() {
        return getAxis(0);
    }

    /**
     * Returns an axis for the plot.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setAxis(int, ValueAxis)
     */
    public ValueAxis getAxis(int index) {
        return this.axes.get(index);
    }

    /**
     * Sets the primary axis for the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param axis  the new primary axis ({@code null} permitted).
     */
    public void setAxis(ValueAxis axis) {
        setAxis(0, axis);
    }

    /**
     * Sets an axis for the plot and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getAxis(int)
     */
    public void setAxis(int index, ValueAxis axis) {
        setAxis(index, axis, true);
    }

    /**
     * Sets an axis for the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getAxis(int)
     */
    public void setAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.axes.put(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the location of the primary axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #setAxisLocation(PolarAxisLocation)
     */
    public PolarAxisLocation getAxisLocation() {
        return getAxisLocation(0);
    }

    /**
     * Returns the location for an axis.
     *
     * @param index  the axis index.
     *
     * @return The location (possibly {@code null}).
     *
     * @see #setAxisLocation(int, PolarAxisLocation)
     */
    public PolarAxisLocation getAxisLocation(int index) {
        return this.axisLocations.get(index);
    }

    /**
     * Sets the location of the primary axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getAxisLocation()
     */
    public void setAxisLocation(PolarAxisLocation location) {
        // delegate argument checks...
        setAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the primary axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getAxisLocation()
     */
    public void setAxisLocation(PolarAxisLocation location, boolean notify) {
        // delegate...
        setAxisLocation(0, location, notify);
    }

    /**
     * Sets the location for an axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getAxisLocation(int)
     */
    public void setAxisLocation(int index, PolarAxisLocation location) {
        // delegate...
        setAxisLocation(index, location, true);
    }

    /**
     * Sets the axis location for an axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void setAxisLocation(int index, PolarAxisLocation location, boolean notify) {
        Args.nullNotPermitted(location, "location");
        this.axisLocations.put(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the number of domain axes.
     *
     * @return The axis count.
     */
    public int getAxisCount() {
        return this.axes.size();
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly {@code null}).
     *
     * @see #setDataset(XYDataset)
     */
    public XYDataset getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset with the specified index, if any.
     *
     * @param index  the dataset index.
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(int, XYDataset)
     */
    public XYDataset getDataset(int index) {
        return this.datasets.get(index);
    }

    /**
     * Sets the primary dataset for the plot, replacing the existing dataset
     * if there is one, and sends a {@code link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     */
    public void setDataset(XYDataset dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot, replacing the existing dataset at the same
     * index if there is one, and sends a {@code link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the dataset index.
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset(int)
     */
    public void setDataset(int index, XYDataset dataset) {
        XYDataset existing = getDataset(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.datasets.put(index, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }

    /**
     * Returns the number of datasets.
     *
     * @return The number of datasets.
     */
    public int getDatasetCount() {
        return this.datasets.size();
    }

    /**
     * Returns the index of the specified dataset, or {@code -1} if the
     * dataset does not belong to the plot.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     *
     * @return The index.
     */
    public int indexOf(XYDataset dataset) {
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            if (entry.getValue() == dataset) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the primary renderer.
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(PolarItemRenderer)
     */
    public PolarItemRenderer getRenderer() {
        return getRenderer(0);
    }

    /**
     * Returns the renderer at the specified index, if there is one.
     *
     * @param index  the renderer index.
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(int, PolarItemRenderer)
     */
    public PolarItemRenderer getRenderer(int index) {
        return this.renderers.get(index);
    }

    /**
     * Sets the primary renderer, and notifies all listeners of a change to the
     * plot.  If the renderer is set to {@code null}, no data items will
     * be drawn for the corresponding dataset.
     *
     * @param renderer  the new renderer ({@code null} permitted).
     *
     * @see #getRenderer()
     */
    public void setRenderer(PolarItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    /**
     * Sets a renderer and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the index.
     * @param renderer  the renderer.
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, PolarItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    /**
     * Sets a renderer and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the index.
     * @param renderer  the renderer.
     * @param notify  notify listeners?
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, PolarItemRenderer renderer, boolean notify) {
        PolarItemRenderer existing = getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.put(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the tick unit that controls the spacing of the angular grid
     * lines.
     *
     * @return The tick unit (never {@code null}).
     */
    public TickUnit getAngleTickUnit() {
        return this.angleTickUnit;
    }

    /**
     * Sets the tick unit that controls the spacing of the angular grid
     * lines, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param unit  the tick unit ({@code null} not permitted).
     */
    public void setAngleTickUnit(TickUnit unit) {
        Args.nullNotPermitted(unit, "unit");
        this.angleTickUnit = unit;
        fireChangeEvent();
    }

    /**
     * Returns the offset that is used for all angles.
     *
     * @return The offset for the angles.
     */
    public double getAngleOffset() {
        return this.angleOffset;
    }

    /**
     * Sets the offset that is used for all angles and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * This is useful to let 0 degrees be at the north, east, south or west
     * side of the chart.
     *
     * @param offset The offset
     */
    public void setAngleOffset(double offset) {
        this.angleOffset = offset;
        fireChangeEvent();
    }

    /**
     * Get the direction for growing angle degrees.
     *
     * @return {@code true} if angle increases counterclockwise,
     *         {@code false} otherwise.
     */
    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    /**
     * Sets the flag for increasing angle degrees direction.
     *
     * {@code true} for counterclockwise, {@code false} for
     * clockwise.
     *
     * @param counterClockwise The flag.
     */
    public void setCounterClockwise(boolean counterClockwise) {
        this.counterClockwise = counterClockwise;
    }

    /**
     * Returns a flag that controls whether the angle labels are visible.
     *
     * @return A boolean.
     *
     * @see #setAngleLabelsVisible(boolean)
     */
    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }

    /**
     * Sets the flag that controls whether the angle labels are visible,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #isAngleLabelsVisible()
     */
    public void setAngleLabelsVisible(boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the font used to display the angle labels.
     *
     * @return A font (never {@code null}).
     *
     * @see #setAngleLabelFont(Font)
     */
    public Font getAngleLabelFont() {
        return this.angleLabelFont;
    }

    /**
     * Sets the font used to display the angle labels and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getAngleLabelFont()
     */
    public void setAngleLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.angleLabelFont = font;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to display the angle labels.
     *
     * @return A paint (never {@code null}).
     *
     * @see #setAngleLabelPaint(Paint)
     */
    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }

    /**
     * Sets the paint used to display the angle labels and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setAngleLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.angleLabelPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the angular gridlines are visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setAngleGridlinesVisible(boolean)
     */
    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the angular grid-lines are
     * visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isAngleGridlinesVisible()
     */
    public void setAngleGridlinesVisible(boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid-lines (if any) plotted against the
     * angular axis.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setAngleGridlineStroke(Stroke)
     */
    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the angular axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getAngleGridlineStroke()
     */
    public void setAngleGridlineStroke(Stroke stroke) {
        this.angleGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the
     * angular axis.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setAngleGridlinePaint(Paint)
     */
    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the angular axis.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getAngleGridlinePaint()
     */
    public void setAngleGridlinePaint(Paint paint) {
        this.angleGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the radius axis grid is visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setRadiusGridlinesVisible(boolean)
     */
    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the radius axis grid lines
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isRadiusGridlinesVisible()
     */
    public void setRadiusGridlinesVisible(boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid lines (if any) plotted against the
     * radius axis.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setRadiusGridlineStroke(Stroke)
     */
    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the radius axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getRadiusGridlineStroke()
     */
    public void setRadiusGridlineStroke(Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the radius
     * axis.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setRadiusGridlinePaint(Paint)
     */
    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the radius axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getRadiusGridlinePaint()
     */
    public void setRadiusGridlinePaint(Paint paint) {
        this.radiusGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Return the current value of the flag indicating if radial minor
     * grid-lines will be drawn or not.
     *
     * @return Returns {@code true} if radial minor grid-lines are drawn.
     */
    public boolean isRadiusMinorGridlinesVisible() {
        return this.radiusMinorGridlinesVisible;
    }

    /**
     * Set the flag that determines if radial minor grid-lines will be drawn,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param flag {@code true} to draw the radial minor grid-lines,
     *             {@code false} to hide them.
     */
    public void setRadiusMinorGridlinesVisible(boolean flag) {
        this.radiusMinorGridlinesVisible = flag;
        fireChangeEvent();
    }

    /**
     * Returns the margin around the plot area.
     *
     * @return The actual margin in pixels.
     */
    public int getMargin() {
        return this.margin;
    }

    /**
     * Set the margin around the plot area and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param margin The new margin in pixels.
     */
    public void setMargin(int margin) {
        this.margin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the fixed legend items, if any.
     *
     * @return The legend items (possibly {@code null}).
     *
     * @see #setFixedLegendItems(LegendItemCollection)
     */
    public LegendItemCollection getFixedLegendItems() {
        return this.fixedLegendItems;
    }

    /**
     * Sets the fixed legend items for the plot.  Leave this set to
     * {@code null} if you prefer the legend items to be created
     * automatically.
     *
     * @param items  the legend items ({@code null} permitted).
     *
     * @see #getFixedLegendItems()
     */
    public void setFixedLegendItems(LegendItemCollection items) {
        this.fixedLegendItems = items;
        fireChangeEvent();
    }

    /**
     * Add text to be displayed in the lower right hand corner and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param text  the text to display ({@code null} not permitted).
     *
     * @see #removeCornerTextItem(String)
     */
    public void addCornerTextItem(String text) {
        Args.nullNotPermitted(text, "text");
        this.cornerTextItems.add(text);
        fireChangeEvent();
    }

    /**
     * Remove the given text from the list of corner text items and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param text  the text to remove ({@code null} ignored).
     *
     * @see #addCornerTextItem(String)
     */
    public void removeCornerTextItem(String text) {
        boolean removed = this.cornerTextItems.remove(text);
        if (removed) {
            fireChangeEvent();
        }
    }

    /**
     * Clear the list of corner text items and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @see #addCornerTextItem(String)
     * @see #removeCornerTextItem(String)
     */
    public void clearCornerTextItems() {
        if (!this.cornerTextItems.isEmpty()) {
            this.cornerTextItems.clear();
            fireChangeEvent();
        }
    }

    /**
     * Generates a list of tick values for the angular tick marks.
     *
     * @return A list of {@link NumberTick} instances.
     */
    protected List<ValueTick> refreshAngleTicks() {
        List<ValueTick> ticks = new ArrayList<>();
        for (double currentTickVal = 0.0; currentTickVal < 360.0; currentTickVal += this.angleTickUnit.getSize()) {
            TextAnchor ta = calculateTextAnchor(currentTickVal);
            NumberTick tick = new NumberTick(currentTickVal, this.angleTickUnit.valueToString(currentTickVal), ta, TextAnchor.CENTER, 0.0);
            ticks.add(tick);
        }
        return ticks;
    }

    /**
     * Calculate the text position for the given degrees.
     *
     * @param angleDegrees  the angle in degrees.
     *
     * @return The optimal text anchor.
     */
    protected TextAnchor calculateTextAnchor(double angleDegrees) {
        TextAnchor ta = TextAnchor.CENTER;
        // normalize angle
        double offset = this.angleOffset;
        while (offset < 0.0) {
            offset += 360.0;
        }
        double normalizedAngle = (((this.counterClockwise ? -1 : 1) * angleDegrees) + offset) % 360;
        while (this.counterClockwise && (normalizedAngle < 0.0)) {
            normalizedAngle += 360.0;
        }
        if (normalizedAngle == 0.0) {
            ta = TextAnchor.CENTER_LEFT;
        } else if (normalizedAngle > 0.0 && normalizedAngle < 90.0) {
            ta = TextAnchor.TOP_LEFT;
        } else if (normalizedAngle == 90.0) {
            ta = TextAnchor.TOP_CENTER;
        } else if (normalizedAngle > 90.0 && normalizedAngle < 180.0) {
            ta = TextAnchor.TOP_RIGHT;
        } else if (normalizedAngle == 180) {
            ta = TextAnchor.CENTER_RIGHT;
        } else if (normalizedAngle > 180.0 && normalizedAngle < 270.0) {
            ta = TextAnchor.BOTTOM_RIGHT;
        } else if (normalizedAngle == 270) {
            ta = TextAnchor.BOTTOM_CENTER;
        } else if (normalizedAngle > 270.0 && normalizedAngle < 360.0) {
            ta = TextAnchor.BOTTOM_LEFT;
        }
        return ta;
    }

    /**
     * Maps a dataset to a particular axis.  All data will be plotted
     * against axis zero by default, no mapping is required for this case.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index.
     */
    public void mapDatasetToAxis(int index, int axisIndex) {
        List<Integer> axisIndices = new ArrayList<>(1);
        axisIndices.add(axisIndex);
        mapDatasetToAxes(index, axisIndices);
    }

    /**
     * Maps the specified dataset to the axes in the list.  Note that the
     * conversion of data values into Java2D space is always performed using
     * the first axis in the list.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndices  the axis indices ({@code null} permitted).
     */
    public void mapDatasetToAxes(int index, List<Integer> axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        checkAxisIndices(axisIndices);
        Integer key = index;
        this.datasetToAxesMap.put(key, new ArrayList<>(axisIndices));
        // fake a dataset change event to update axes...
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    /**
     * This method is used to perform argument checking on the list of
     * axis indices passed to mapDatasetToAxes().
     *
     * @param indices  the list of indices ({@code null} permitted).
     */
    private void checkAxisIndices(List<Integer> indices) {
        // axisIndices can be:
        // 1.  null;
        // 2.  non-empty, containing only Integer objects that are unique.
        if (indices == null) {
            // OK
            return;
        }
        if (indices.isEmpty()) {
            throw new IllegalArgumentException("Empty list not permitted.");
        }
        Set<Integer> set = new HashSet<>();
        for (Integer i : indices) {
            if (set.contains(i)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            set.add(i);
        }
    }

    /**
     * Returns the axis for a dataset.
     *
     * @param index  the dataset index.
     *
     * @return The axis.
     */
    public ValueAxis getAxisForDataset(int index) {
        ValueAxis valueAxis;
        List<Integer> axisIndices = this.datasetToAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            valueAxis = getAxis(axisIndex);
        } else {
            valueAxis = getAxis(0);
        }
        return valueAxis;
    }

    /**
     * Returns the index of the given axis.
     *
     * @param axis  the axis.
     *
     * @return The axis index or -1 if axis is not used in this plot.
     */
    public int getAxisIndex(ValueAxis axis) {
        for (Entry<Integer, ValueAxis> entry : this.axes.entrySet()) {
            if (axis.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        // try the parent plot
        Plot parent = getParent();
        if (parent instanceof PolarPlot) {
            PolarPlot p = (PolarPlot) parent;
            return p.getAxisIndex(axis);
        }
        return -1;
    }

    /**
     * Returns the index of the specified renderer, or {@code -1} if the
     * renderer is not assigned to this plot.
     *
     * @param renderer  the renderer ({@code null} not permitted).
     *
     * @return The renderer index.
     */
    public int getIndexOf(PolarItemRenderer renderer) {
        Args.nullNotPermitted(renderer, "renderer");
        for (Entry<Integer, PolarItemRenderer> entry : this.renderers.entrySet()) {
            if (renderer.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        // FIXME: handle axes and renderers
        visitor.visit(this);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This plot relies on a {@link PolarItemRenderer} to draw each
     * item in the plot.  This allows the visual representation of the data to
     * be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in
     * {@code null} if you do not need this information.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot (including axes and
     *              labels) should be drawn.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  ignored.
     * @param info  collects chart drawing information ({@code null}
     *              permitted).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }
        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        Rectangle2D dataArea = area;
        if (info != null) {
            info.setDataArea(dataArea);
        }
        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        int axisCount = this.axes.size();
        AxisState state = null;
        for (int i = 0; i < axisCount; i++) {
            ValueAxis axis = getAxis(i);
            if (axis != null) {
                PolarAxisLocation location = this.axisLocations.get(i);
                AxisState s = drawAxis(axis, location, g2, dataArea);
                if (i == 0) {
                    state = s;
                }
            }
        }
        // now for each dataset, get the renderer and the appropriate axis
        // and render the dataset...
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        this.angleTicks = refreshAngleTicks();
        drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
        render(g2, dataArea, info);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);
        drawCornerTextItems(g2, dataArea);
    }

    /**
     * Draws the corner text items.
     *
     * @param g2  the drawing surface.
     * @param area  the area.
     */
    protected void drawCornerTextItems(Graphics2D g2, Rectangle2D area) {
        if (this.cornerTextItems.isEmpty()) {
            return;
        }
        g2.setColor(Color.BLACK);
        double width = 0.0;
        double height = 0.0;
        for (String msg : this.cornerTextItems) {
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D bounds = TextUtils.getTextBounds(msg, g2, fm);
            width = Math.max(width, bounds.getWidth());
            height += bounds.getHeight();
        }
        double xadj = ANNOTATION_MARGIN * 2.0;
        double yadj = ANNOTATION_MARGIN;
        width += xadj;
        height += yadj;
        double x = area.getMaxX() - width;
        double y = area.getMaxY() - height;
        g2.drawRect((int) x, (int) y, (int) width, (int) height);
        x += ANNOTATION_MARGIN;
        for (String msg : this.cornerTextItems) {
            Rectangle2D bounds = TextUtils.getTextBounds(msg, g2, g2.getFontMetrics());
            y += bounds.getHeight();
            g2.drawString(msg, (int) x, (int) y);
        }
    }

    /**
     * Draws the axis with the specified index.
     *
     * @param axis  the axis.
     * @param location  the axis location.
     * @param g2  the graphics target.
     * @param plotArea  the plot area.
     *
     * @return The axis state.
     */
    protected AxisState drawAxis(ValueAxis axis, PolarAxisLocation location, Graphics2D g2, Rectangle2D plotArea) {
        double centerX = plotArea.getCenterX();
        double centerY = plotArea.getCenterY();
        double r = Math.min(plotArea.getWidth() / 2.0, plotArea.getHeight() / 2.0) - this.margin;
        double x = centerX - r;
        double y = centerY - r;
        Rectangle2D dataArea = null;
        AxisState result = null;
        if (location == PolarAxisLocation.NORTH_RIGHT) {
            dataArea = new Rectangle2D.Double(x, y, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.RIGHT, null);
        } else if (location == PolarAxisLocation.NORTH_LEFT) {
            dataArea = new Rectangle2D.Double(centerX, y, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.LEFT, null);
        } else if (location == PolarAxisLocation.SOUTH_LEFT) {
            dataArea = new Rectangle2D.Double(centerX, centerY, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.LEFT, null);
        } else if (location == PolarAxisLocation.SOUTH_RIGHT) {
            dataArea = new Rectangle2D.Double(x, centerY, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.RIGHT, null);
        } else if (location == PolarAxisLocation.EAST_ABOVE) {
            dataArea = new Rectangle2D.Double(centerX, centerY, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.TOP, null);
        } else if (location == PolarAxisLocation.EAST_BELOW) {
            dataArea = new Rectangle2D.Double(centerX, y, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.BOTTOM, null);
        } else if (location == PolarAxisLocation.WEST_ABOVE) {
            dataArea = new Rectangle2D.Double(x, centerY, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.TOP, null);
        } else if (location == PolarAxisLocation.WEST_BELOW) {
            dataArea = new Rectangle2D.Double(x, y, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.BOTTOM, null);
        }
        return result;
    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current m_Renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension
     *              information ({@code null} permitted).
     */
    protected void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        // now get the data and plot it (the visual representation will depend
        // on the m_Renderer that has been set)...
        boolean hasData = false;
        int datasetCount = this.datasets.size();
        for (int i = datasetCount - 1; i >= 0; i--) {
            XYDataset dataset = getDataset(i);
            if (dataset == null) {
                continue;
            }
            PolarItemRenderer renderer = getRenderer(i);
            if (renderer == null) {
                continue;
            }
            if (!DatasetUtils.isEmptyOrNull(dataset)) {
                hasData = true;
                int seriesCount = dataset.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    renderer.drawSeries(g2, dataArea, info, this, dataset, series);
                }
            }
        }
        if (!hasData) {
            drawNoDataMessage(g2, dataArea);
        }
    }

    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param angularTicks  the ticks for the angular axis.
     * @param radialTicks  the ticks for the radial axis.
     */
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> angularTicks, List<ValueTick> radialTicks) {
        PolarItemRenderer renderer = getRenderer();
        // no renderer, no gridlines...
        if (renderer == null) {
            return;
        }
        // draw the domain grid lines, if any...
        if (isAngleGridlinesVisible()) {
            Stroke gridStroke = getAngleGridlineStroke();
            Paint gridPaint = getAngleGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                renderer.drawAngularGridLines(g2, this, angularTicks, dataArea);
            }
        }
        // draw the radius grid lines, if any...
        if (isRadiusGridlinesVisible()) {
            Stroke gridStroke = getRadiusGridlineStroke();
            Paint gridPaint = getRadiusGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                List<ValueTick> ticks = buildRadialTicks(radialTicks);
                renderer.drawRadialGridLines(g2, this, getAxis(), ticks, dataArea);
            }
        }
    }

    /**
     * Create a list of ticks based on the given list and plot properties.
     * Only ticks of a specific type may be in the result list.
     *
     * @param allTicks A list of all available ticks for the primary axis.
     *        {@code null} not permitted.
     * @return Ticks to use for radial gridlines.
     */
    protected List<ValueTick> buildRadialTicks(List<ValueTick> allTicks) {
        List<ValueTick> ticks = new ArrayList<>();
        for (ValueTick tick : allTicks) {
            if (isRadiusMinorGridlinesVisible() || TickType.MAJOR.equals(tick.getTickType())) {
                ticks.add(tick);
            }
        }
        return ticks;
    }

    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  the amount of the zoom.
     */
    @Override
    public void zoom(double percent) {
        for (int axisIdx = 0; axisIdx < getAxisCount(); axisIdx++) {
            final ValueAxis axis = getAxis(axisIdx);
            if (axis != null) {
                if (percent > 0.0) {
                    double radius = axis.getUpperBound();
                    double scaledRadius = radius * percent;
                    axis.setUpperBound(scaledRadius);
                    axis.setAutoRange(false);
                } else {
                    axis.setAutoRange(true);
                }
            }
        }
    }

    /**
     * A utility method that returns a list of datasets that are mapped to a
     * particular axis.
     *
     * @param axisIndex  the axis index ({@code null} not permitted).
     *
     * @return A list of datasets.
     */
    private List<XYDataset> getDatasetsMappedToAxis(Integer axisIndex) {
        Args.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset> result = new ArrayList<>();
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            List<Integer> mappedAxes = this.datasetToAxesMap.get(entry.getKey());
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(getDataset(entry.getKey()));
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(getDataset(entry.getKey()));
                }
            }
        }
        return result;
    }

    /**
     * Returns the range for the specified axis.
     *
     * @param axis  the axis.
     *
     * @return The range.
     */
    @Override
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        List<XYDataset> mappedDatasets = new ArrayList<>();
        int axisIndex = getAxisIndex(axis);
        if (axisIndex >= 0) {
            mappedDatasets = getDatasetsMappedToAxis(axisIndex);
        }
        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
        for (XYDataset dataset : mappedDatasets) {
            if (dataset != null) {
                // FIXME better ask the renderer instead of DatasetUtilities
                result = Range.combine(result, DatasetUtils.findRangeBounds(dataset));
            }
        }
        return result;
    }

    /**
     * Receives notification of a change to the plot's m_Dataset.
     * <P>
     * The axis ranges are updated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        for (int i = 0; i < this.axes.size(); i++) {
            final ValueAxis axis = this.axes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
        if (getParent() != null) {
            getParent().datasetChanged(event);
        } else {
            super.datasetChanged(event);
        }
    }

    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's m_Renderer.
     *
     * @param event  information about the property change.
     */
    @Override
    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Returns the legend items for the plot.  Each legend item is generated by
     * the plot's m_Renderer, since the m_Renderer is responsible for the visual
     * representation of the data.
     *
     * @return The legend items.
     */
    @Override
    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        int count = this.datasets.size();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
            XYDataset dataset = getDataset(datasetIndex);
            PolarItemRenderer renderer = getRenderer(datasetIndex);
            if (dataset != null && renderer != null) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = renderer.getLegendItem(i);
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Tests this plot for equality with another object.
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
        if (!(obj instanceof PolarPlot)) {
            return false;
        }
        PolarPlot that = (PolarPlot) obj;
        if (!this.axes.equals(that.axes)) {
            return false;
        }
        if (!this.axisLocations.equals(that.axisLocations)) {
            return false;
        }
        if (!this.renderers.equals(that.renderers)) {
            return false;
        }
        if (!this.angleTickUnit.equals(that.angleTickUnit)) {
            return false;
        }
        if (this.angleGridlinesVisible != that.angleGridlinesVisible) {
            return false;
        }
        if (this.angleOffset != that.angleOffset) {
            return false;
        }
        if (this.counterClockwise != that.counterClockwise) {
            return false;
        }
        if (this.angleLabelsVisible != that.angleLabelsVisible) {
            return false;
        }
        if (!this.angleLabelFont.equals(that.angleLabelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.angleLabelPaint, that.angleLabelPaint)) {
            return false;
        }
        if (!Objects.equals(this.angleGridlineStroke, that.angleGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.angleGridlinePaint, that.angleGridlinePaint)) {
            return false;
        }
        if (this.radiusGridlinesVisible != that.radiusGridlinesVisible) {
            return false;
        }
        if (!Objects.equals(this.radiusGridlineStroke, that.radiusGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.radiusGridlinePaint, that.radiusGridlinePaint)) {
            return false;
        }
        if (this.radiusMinorGridlinesVisible != that.radiusMinorGridlinesVisible) {
            return false;
        }
        if (!this.cornerTextItems.equals(that.cornerTextItems)) {
            return false;
        }
        if (this.margin != that.margin) {
            return false;
        }
        if (!Objects.equals(this.fixedLegendItems, that.fixedLegendItems)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  this can occur if some component of
     *         the plot cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PolarPlot clone = (PolarPlot) super.clone();
        clone.axes = CloneUtils.clone(this.axes);
        for (int i = 0; i < this.axes.size(); i++) {
            ValueAxis axis = this.axes.get(i);
            if (axis != null) {
                ValueAxis clonedAxis = (ValueAxis) axis.clone();
                clone.axes.put(i, clonedAxis);
                clonedAxis.setPlot(clone);
                clonedAxis.addChangeListener(clone);
            }
        }
        // the datasets are not cloned, but listeners need to be added...
        clone.datasets = CloneUtils.clone(this.datasets);
        for (int i = 0; i < clone.datasets.size(); ++i) {
            XYDataset d = getDataset(i);
            if (d != null) {
                d.addChangeListener(clone);
            }
        }
        clone.renderers = CloneUtils.clone(this.renderers);
        for (int i = 0; i < this.renderers.size(); i++) {
            PolarItemRenderer renderer2 = this.renderers.get(i);
            if (renderer2 instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) renderer2;
                PolarItemRenderer rc = (PolarItemRenderer) pc.clone();
                clone.renderers.put(i, rc);
                rc.setPlot(clone);
                rc.addChangeListener(clone);
            }
        }
        clone.cornerTextItems = new ArrayList<>(this.cornerTextItems);
        return clone;
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
        SerialUtils.writeStroke(this.angleGridlineStroke, stream);
        SerialUtils.writePaint(this.angleGridlinePaint, stream);
        SerialUtils.writeStroke(this.radiusGridlineStroke, stream);
        SerialUtils.writePaint(this.radiusGridlinePaint, stream);
        SerialUtils.writePaint(this.angleLabelPaint, stream);
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
        this.angleGridlineStroke = SerialUtils.readStroke(stream);
        this.angleGridlinePaint = SerialUtils.readPaint(stream);
        this.radiusGridlineStroke = SerialUtils.readStroke(stream);
        this.radiusGridlinePaint = SerialUtils.readPaint(stream);
        this.angleLabelPaint = SerialUtils.readPaint(stream);
        int rangeAxisCount = this.axes.size();
        for (int i = 0; i < rangeAxisCount; i++) {
            Axis axis = this.axes.get(i);
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        int datasetCount = this.datasets.size();
        for (int i = 0; i < datasetCount; i++) {
            Dataset dataset = this.datasets.get(i);
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        int rendererCount = this.renderers.size();
        for (int i = 0; i < rendererCount; i++) {
            PolarItemRenderer renderer = this.renderers.get(i);
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }

    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
        // do nothing
    }

    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     * @param useAnchor  use source point as zoom anchor?
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {
        // do nothing
    }

    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        // do nothing
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        zoom(factor);
    }

    /**
     * Multiplies the range on the range axis by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point (in Java2D space).
     * @param useAnchor  use source point as zoom anchor?
     *
     * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        // get the source coordinate - this plot has always a VERTICAL
        // orientation
        final double sourceX = source.getX();
        for (int axisIdx = 0; axisIdx < getAxisCount(); axisIdx++) {
            final ValueAxis axis = getAxis(axisIdx);
            if (axis != null) {
                if (useAnchor) {
                    double anchorX = axis.java2DToValue(sourceX, info.getDataArea(), RectangleEdge.BOTTOM);
                    axis.resizeRange(factor, anchorX);
                } else {
                    axis.resizeRange(factor);
                }
            }
        }
    }

    /**
     * Zooms in on the range axes.
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        zoom((upperPercent + lowerPercent) / 2.0);
    }

    /**
     * Returns {@code false} always.
     *
     * @return {@code false} always.
     */
    @Override
    public boolean isDomainZoomable() {
        return false;
    }

    /**
     * Returns {@code true} to indicate that the range axis is zoomable.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isRangeZoomable() {
        return true;
    }

    /**
     * Returns the orientation of the plot.
     *
     * @return The orientation.
     */
    @Override
    public PlotOrientation getOrientation() {
        return PlotOrientation.HORIZONTAL;
    }

    /**
     * Translates a (theta, radius) pair into Java2D coordinates.  If
     * {@code radius} is less than the lower bound of the axis, then
     * this method returns the centre point.
     *
     * @param angleDegrees  the angle in degrees.
     * @param radius  the radius.
     * @param axis  the axis.
     * @param dataArea  the data area.
     *
     * @return A point in Java2D space.
     */
    public Point translateToJava2D(double angleDegrees, double radius, ValueAxis axis, Rectangle2D dataArea) {
        if (counterClockwise) {
            angleDegrees = -angleDegrees;
        }
        double radians = Math.toRadians(angleDegrees + this.angleOffset);
        double minx = dataArea.getMinX() + this.margin;
        double maxx = dataArea.getMaxX() - this.margin;
        double miny = dataArea.getMinY() + this.margin;
        double maxy = dataArea.getMaxY() - this.margin;
        double halfWidth = (maxx - minx) / 2.0;
        double halfHeight = (maxy - miny) / 2.0;
        double midX = minx + halfWidth;
        double midY = miny + halfHeight;
        double l = Math.min(halfWidth, halfHeight);
        Rectangle2D quadrant = new Rectangle2D.Double(midX, midY, l, l);
        double axisMin = axis.getLowerBound();
        double adjustedRadius = Math.max(radius, axisMin);
        double length = axis.valueToJava2D(adjustedRadius, quadrant, RectangleEdge.BOTTOM) - midX;
        float x = (float) (midX + Math.cos(radians) * length);
        float y = (float) (midY + Math.sin(radians) * length);
        int ix = Math.round(x);
        int iy = Math.round(y);
        return new Point(ix, iy);
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
 * -----------------------------
 * DefaultPolarItemRenderer.java
 * -----------------------------
 * (C) Copyright 2004-2021, by Solution Engineering, Inc. and
 *     Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert;
 *                   Martin Hoeller (patch 2850344);
 * 
 */
/**
 * A renderer that can be used with the {@link PolarPlot} class.
 */
public class DefaultPolarItemRenderer extends AbstractRenderer implements PolarItemRenderer {

    /**
     * The plot that the renderer is assigned to.
     */
    private PolarPlot plot;

    /**
     * Flags that control whether the renderer fills each series or not.
     */
    private Map<Integer, Boolean> seriesFilledMap;

    /**
     * Flag that controls whether an outline is drawn for filled series or
     * not.
     */
    private boolean drawOutlineWhenFilled;

    /**
     * The composite to use when filling series.
     */
    private transient Composite fillComposite;

    /**
     * A flag that controls whether the fill paint is used for filling
     * shapes.
     */
    private boolean useFillPaint;

    /**
     * The shape that is used to represent a line in the legend.
     */
    private transient Shape legendLine;

    /**
     * Flag that controls whether item shapes are visible or not.
     */
    private boolean shapesVisible;

    /**
     * Flag that controls if the first and last point of the dataset should be
     * connected or not.
     */
    private boolean connectFirstAndLastPoint;

    /**
     * A list of tool tip generators (one per series).
     */
    private Map<Integer, XYToolTipGenerator> toolTipGeneratorMap;

    /**
     * The default tool tip generator.
     */
    private XYToolTipGenerator defaultToolTipGenerator;

    /**
     * The URL text generator.
     */
    private XYURLGenerator urlGenerator;

    /**
     * The legend item tool tip generator.
     */
    private XYSeriesLabelGenerator legendItemToolTipGenerator;

    /**
     * The legend item URL generator.
     */
    private XYSeriesLabelGenerator legendItemURLGenerator;

    /**
     * Creates a new instance of DefaultPolarItemRenderer
     */
    public DefaultPolarItemRenderer() {
        this.seriesFilledMap = new HashMap<>();
        this.drawOutlineWhenFilled = true;
        this.fillComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
        // use item paint for fills by default
        this.useFillPaint = false;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.shapesVisible = true;
        this.connectFirstAndLastPoint = true;
        this.toolTipGeneratorMap = new HashMap<>();
        this.urlGenerator = null;
        this.legendItemToolTipGenerator = null;
        this.legendItemURLGenerator = null;
    }

    /**
     * Set the plot associated with this renderer.
     *
     * @param plot  the plot.
     *
     * @see #getPlot()
     */
    @Override
    public void setPlot(PolarPlot plot) {
        this.plot = plot;
    }

    /**
     * Return the plot associated with this renderer.
     *
     * @return The plot.
     *
     * @see #setPlot(PolarPlot)
     */
    @Override
    public PolarPlot getPlot() {
        return this.plot;
    }

    /**
     * Returns {@code true} if the renderer will draw an outline around
     * a filled polygon, {@code false} otherwise.
     *
     * @return A boolean.
     */
    public boolean getDrawOutlineWhenFilled() {
        return this.drawOutlineWhenFilled;
    }

    /**
     * Set the flag that controls whether the outline around a filled
     * polygon will be drawn or not and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param drawOutlineWhenFilled  the flag.
     */
    public void setDrawOutlineWhenFilled(boolean drawOutlineWhenFilled) {
        this.drawOutlineWhenFilled = drawOutlineWhenFilled;
        fireChangeEvent();
    }

    /**
     * Get the composite that is used for filling.
     *
     * @return The composite (never {@code null}).
     */
    public Composite getFillComposite() {
        return this.fillComposite;
    }

    /**
     * Sets the composite which will be used for filling polygons and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param composite  the composite to use ({@code null} not permitted).
     */
    public void setFillComposite(Composite composite) {
        Args.nullNotPermitted(composite, "composite");
        this.fillComposite = composite;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if a shape will be drawn for every item, or
     * {@code false} if not.
     *
     * @return A boolean.
     */
    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    /**
     * Set the flag that controls whether a shape will be drawn for every
     * item, or not and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param visible  the flag.
     */
    public void setShapesVisible(boolean visible) {
        this.shapesVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if first and last point of a series will be
     * connected, {@code false} otherwise.
     *
     * @return The current status of the flag.
     */
    public boolean getConnectFirstAndLastPoint() {
        return this.connectFirstAndLastPoint;
    }

    /**
     * Set the flag that controls whether the first and last point of a series
     * will be connected or not and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param connect the flag.
     */
    public void setConnectFirstAndLastPoint(boolean connect) {
        this.connectFirstAndLastPoint = connect;
        fireChangeEvent();
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier.
     */
    @Override
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        PolarPlot p = getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    /**
     * Returns {@code true} if the renderer should fill the specified
     * series, and {@code false} otherwise.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public boolean isSeriesFilled(int series) {
        boolean result = false;
        Boolean b = this.seriesFilledMap.get(series);
        if (b != null) {
            result = b;
        }
        return result;
    }

    /**
     * Sets a flag that controls whether a series is filled.
     *
     * @param series  the series index.
     * @param filled  the flag.
     */
    public void setSeriesFilled(int series, boolean filled) {
        this.seriesFilledMap.put(series, filled);
    }

    /**
     * Returns {@code true} if the renderer should use the fill paint
     * setting to fill shapes, and {@code false} if it should just
     * use the regular paint.
     *
     * @return A boolean.
     *
     * @see #setUseFillPaint(boolean)
     */
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    /**
     * Sets the flag that controls whether the fill paint is used to fill
     * shapes, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getUseFillPaint()
     */
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
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
     * Adds an entity to the collection.
     *
     * @param entities  the entity collection being populated.
     * @param area  the entity area (if {@code null} a default will be
     *              used).
     * @param dataset  the dataset.
     * @param series  the series.
     * @param item  the item.
     * @param entityX  the entity's center x-coordinate in user space (only
     *                 used if {@code area} is {@code null}).
     * @param entityY  the entity's center y-coordinate in user space (only
     *                 used if {@code area} is {@code null}).
     */
    protected void addEntity(EntityCollection entities, Shape area, XYDataset dataset, int series, int item, double entityX, double entityY) {
        if (!getItemCreateEntity(series, item)) {
            return;
        }
        Shape hotspot = area;
        if (hotspot == null) {
            double r = getDefaultEntityRadius();
            double w = r * 2;
            if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
            } else {
                hotspot = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
            }
        }
        String tip = null;
        XYToolTipGenerator generator = getToolTipGenerator(series, item);
        if (generator != null) {
            tip = generator.generateToolTip(dataset, series, item);
        }
        String url = null;
        if (getURLGenerator() != null) {
            url = getURLGenerator().generateURL(dataset, series, item);
        }
        XYItemEntity entity = new XYItemEntity(hotspot, dataset, series, item, tip, url);
        entities.add(entity);
    }

    /**
     * Plots the data for a given series.
     *
     * @param g2  the drawing surface.
     * @param dataArea  the data area.
     * @param info  collects plot rendering info.
     * @param plot  the plot.
     * @param dataset  the dataset.
     * @param seriesIndex  the series index.
     */
    @Override
    public void drawSeries(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, PolarPlot plot, XYDataset dataset, int seriesIndex) {
        final int numPoints = dataset.getItemCount(seriesIndex);
        if (numPoints == 0) {
            return;
        }
        GeneralPath poly = null;
        ValueAxis axis = plot.getAxisForDataset(plot.indexOf(dataset));
        for (int i = 0; i < numPoints; i++) {
            double theta = dataset.getXValue(seriesIndex, i);
            double radius = dataset.getYValue(seriesIndex, i);
            Point p = plot.translateToJava2D(theta, radius, axis, dataArea);
            if (poly == null) {
                poly = new GeneralPath();
                poly.moveTo(p.x, p.y);
            } else {
                poly.lineTo(p.x, p.y);
            }
        }
        assert poly != null;
        if (getConnectFirstAndLastPoint()) {
            poly.closePath();
        }
        g2.setPaint(lookupSeriesPaint(seriesIndex));
        g2.setStroke(lookupSeriesStroke(seriesIndex));
        if (isSeriesFilled(seriesIndex)) {
            Composite savedComposite = g2.getComposite();
            g2.setComposite(this.fillComposite);
            g2.fill(poly);
            g2.setComposite(savedComposite);
            if (this.drawOutlineWhenFilled) {
                // draw the outline of the filled polygon
                g2.setPaint(lookupSeriesOutlinePaint(seriesIndex));
                g2.draw(poly);
            }
        } else {
            // just the lines, no filling
            g2.draw(poly);
        }
        // draw the item shapes
        if (this.shapesVisible) {
            // setup for collecting optional entity info...
            EntityCollection entities = null;
            if (info != null) {
                entities = info.getOwner().getEntityCollection();
            }
            PathIterator pi = poly.getPathIterator(null);
            int i = 0;
            while (!pi.isDone()) {
                final float[] coords = new float[6];
                final int segType = pi.currentSegment(coords);
                pi.next();
                if (segType != PathIterator.SEG_LINETO && segType != PathIterator.SEG_MOVETO) {
                    continue;
                }
                final int x = Math.round(coords[0]);
                final int y = Math.round(coords[1]);
                final Shape shape = ShapeUtils.createTranslatedShape(getItemShape(seriesIndex, i++), x, y);
                Paint paint;
                if (useFillPaint) {
                    paint = lookupSeriesFillPaint(seriesIndex);
                } else {
                    paint = lookupSeriesPaint(seriesIndex);
                }
                g2.setPaint(paint);
                g2.fill(shape);
                if (isSeriesFilled(seriesIndex) && this.drawOutlineWhenFilled) {
                    g2.setPaint(lookupSeriesOutlinePaint(seriesIndex));
                    g2.setStroke(lookupSeriesOutlineStroke(seriesIndex));
                    g2.draw(shape);
                }
                // add an entity for the item, but only if it falls within the
                // data area...
                if (entities != null && ShapeUtils.isPointInRect(dataArea, x, y)) {
                    addEntity(entities, shape, dataset, seriesIndex, i - 1, x, y);
                }
            }
        }
    }

    /**
     * Draw the angular gridlines - the spokes.
     *
     * @param g2  the drawing surface.
     * @param plot  the plot ({@code null} not permitted).
     * @param ticks  the ticks ({@code null} not permitted).
     * @param dataArea  the data area.
     */
    @Override
    public void drawAngularGridLines(Graphics2D g2, PolarPlot plot, List ticks, Rectangle2D dataArea) {
        g2.setFont(plot.getAngleLabelFont());
        g2.setStroke(plot.getAngleGridlineStroke());
        g2.setPaint(plot.getAngleGridlinePaint());
        ValueAxis axis = plot.getAxis();
        double centerValue, outerValue;
        if (axis.isInverted()) {
            outerValue = axis.getLowerBound();
            centerValue = axis.getUpperBound();
        } else {
            outerValue = axis.getUpperBound();
            centerValue = axis.getLowerBound();
        }
        Point center = plot.translateToJava2D(0, centerValue, axis, dataArea);
        for (Object o : ticks) {
            NumberTick tick = (NumberTick) o;
            double tickVal = tick.getNumber().doubleValue();
            Point p = plot.translateToJava2D(tickVal, outerValue, axis, dataArea);
            g2.setPaint(plot.getAngleGridlinePaint());
            g2.drawLine(center.x, center.y, p.x, p.y);
            if (plot.isAngleLabelsVisible()) {
                int x = p.x;
                int y = p.y;
                g2.setPaint(plot.getAngleLabelPaint());
                TextUtils.drawAlignedString(tick.getText(), g2, x, y, tick.getTextAnchor());
            }
        }
    }

    /**
     * Draw the radial gridlines - the rings.
     *
     * @param g2  the drawing surface ({@code null} not permitted).
     * @param plot  the plot ({@code null} not permitted).
     * @param radialAxis  the radial axis ({@code null} not permitted).
     * @param ticks  the ticks ({@code null} not permitted).
     * @param dataArea  the data area.
     */
    @Override
    public void drawRadialGridLines(Graphics2D g2, PolarPlot plot, ValueAxis radialAxis, List ticks, Rectangle2D dataArea) {
        Args.nullNotPermitted(radialAxis, "radialAxis");
        g2.setFont(radialAxis.getTickLabelFont());
        g2.setPaint(plot.getRadiusGridlinePaint());
        g2.setStroke(plot.getRadiusGridlineStroke());
        double centerValue;
        if (radialAxis.isInverted()) {
            centerValue = radialAxis.getUpperBound();
        } else {
            centerValue = radialAxis.getLowerBound();
        }
        Point center = plot.translateToJava2D(0, centerValue, radialAxis, dataArea);
        for (Object o : ticks) {
            NumberTick tick = (NumberTick) o;
            double angleDegrees = plot.isCounterClockwise() ? plot.getAngleOffset() : -plot.getAngleOffset();
            Point p = plot.translateToJava2D(angleDegrees, tick.getNumber().doubleValue(), radialAxis, dataArea);
            int r = p.x - center.x;
            int upperLeftX = center.x - r;
            int upperLeftY = center.y - r;
            int d = 2 * r;
            Ellipse2D ring = new Ellipse2D.Double(upperLeftX, upperLeftY, d, d);
            g2.setPaint(plot.getRadiusGridlinePaint());
            g2.draw(ring);
        }
    }

    /**
     * Return the legend for the given series.
     *
     * @param series  the series index.
     *
     * @return The legend item.
     */
    @Override
    public LegendItem getLegendItem(int series) {
        LegendItem result;
        PolarPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(plot.getIndexOf(this));
        if (dataset == null) {
            return null;
        }
        String toolTipText = null;
        if (getLegendItemToolTipGenerator() != null) {
            toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (getLegendItemURLGenerator() != null) {
            urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        Comparable seriesKey = dataset.getSeriesKey(series);
        String label = seriesKey.toString();
        String description = label;
        Shape shape = lookupSeriesShape(series);
        Paint paint;
        if (this.useFillPaint) {
            paint = lookupSeriesFillPaint(series);
        } else {
            paint = lookupSeriesPaint(series);
        }
        Stroke stroke = lookupSeriesStroke(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        boolean shapeOutlined = isSeriesFilled(series) && this.drawOutlineWhenFilled;
        result = new LegendItem(label, description, toolTipText, urlText, getShapesVisible(), shape, /* shapeFilled=*/
        true, paint, shapeOutlined, outlinePaint, outlineStroke, /* lineVisible= */
        true, this.legendLine, stroke, paint);
        result.setToolTipText(toolTipText);
        result.setURLText(urlText);
        result.setDataset(dataset);
        result.setSeriesKey(seriesKey);
        result.setSeriesIndex(series);
        return result;
    }

    /**
     * Returns the tooltip generator for the specified series and item.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The tooltip generator (possibly {@code null}).
     */
    @Override
    public XYToolTipGenerator getToolTipGenerator(int series, int item) {
        XYToolTipGenerator generator = this.toolTipGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultToolTipGenerator;
        }
        return generator;
    }

    /**
     * Returns the tool tip generator for the specified series.
     *
     * @return The tooltip generator (possibly {@code null}).
     */
    @Override
    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {
        return this.toolTipGeneratorMap.get(series);
    }

    /**
     * Sets the tooltip generator for the specified series.
     *
     * @param series  the series index.
     * @param generator  the tool tip generator ({@code null} permitted).
     */
    @Override
    public void setSeriesToolTipGenerator(int series, XYToolTipGenerator generator) {
        this.toolTipGeneratorMap.put(series, generator);
        fireChangeEvent();
    }

    /**
     * Returns the default tool tip generator.
     *
     * @return The default tool tip generator (possibly {@code null}).
     */
    @Override
    public XYToolTipGenerator getDefaultToolTipGenerator() {
        return this.defaultToolTipGenerator;
    }

    /**
     * Sets the default tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     */
    @Override
    public void setDefaultToolTipGenerator(XYToolTipGenerator generator) {
        this.defaultToolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the URL generator.
     *
     * @return The URL generator (possibly {@code null}).
     */
    @Override
    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator.
     *
     * @param urlGenerator  the generator ({@code null} permitted)
     */
    @Override
    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
        fireChangeEvent();
    }

    /**
     * Returns the legend item tool tip generator.
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setLegendItemToolTipGenerator(XYSeriesLabelGenerator)
     */
    public XYSeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    /**
     * Sets the legend item tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getLegendItemToolTipGenerator()
     */
    public void setLegendItemToolTipGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend item URL generator.
     *
     * @return The URL generator (possibly {@code null}).
     *
     * @see #setLegendItemURLGenerator(XYSeriesLabelGenerator)
     */
    public XYSeriesLabelGenerator getLegendItemURLGenerator() {
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
    public void setLegendItemURLGenerator(XYSeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} not permitted).
     *
     * @return {@code true} if this renderer is equal to {@code obj},
     *     and {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DefaultPolarItemRenderer)) {
            return false;
        }
        DefaultPolarItemRenderer that = (DefaultPolarItemRenderer) obj;
        if (!this.seriesFilledMap.equals(that.seriesFilledMap)) {
            return false;
        }
        if (this.drawOutlineWhenFilled != that.drawOutlineWhenFilled) {
            return false;
        }
        if (!Objects.equals(this.fillComposite, that.fillComposite)) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (this.shapesVisible != that.shapesVisible) {
            return false;
        }
        if (this.connectFirstAndLastPoint != that.connectFirstAndLastPoint) {
            return false;
        }
        if (!this.toolTipGeneratorMap.equals(that.toolTipGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultToolTipGenerator, that.defaultToolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.urlGenerator, that.urlGenerator)) {
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
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the renderer cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultPolarItemRenderer clone = (DefaultPolarItemRenderer) super.clone();
        clone.legendLine = CloneUtils.clone(this.legendLine);
        clone.seriesFilledMap = new HashMap<>(this.seriesFilledMap);
        clone.toolTipGeneratorMap = CloneUtils.cloneMapValues(this.toolTipGeneratorMap);
        if (clone.defaultToolTipGenerator instanceof PublicCloneable) {
            clone.defaultToolTipGenerator = CloneUtils.clone(this.defaultToolTipGenerator);
        }
        if (clone.urlGenerator instanceof PublicCloneable) {
            clone.urlGenerator = CloneUtils.clone(this.urlGenerator);
        }
        if (clone.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = CloneUtils.clone(this.legendItemToolTipGenerator);
        }
        if (clone.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = CloneUtils.clone(this.legendItemURLGenerator);
        }
        //        clone.defaultToolTipGenerator = CloneUtils.copy(this.defaultToolTipGenerator);
        //        clone.urlGenerator = CloneUtils.copy(this.urlGenerator);
        //        clone.legendItemToolTipGenerator = CloneUtils.copy(this.legendItemToolTipGenerator);
        //        clone.legendItemURLGenerator = CloneUtils.copy(this.legendItemURLGenerator);
        return clone;
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
        this.fillComposite = SerialUtils.readComposite(stream);
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
        SerialUtils.writeComposite(this.fillComposite, stream);
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
 * -------------------------
 * NumberTickUnitSource.java
 * -------------------------
 * (C) Copyright 2014-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A tick unit source implementation that returns NumberTickUnit instances
 * that are multiples of 1, 2 or 5 times some power of 10.
 */
public class NumberTickUnitSource implements TickUnitSource, Serializable {

    /**
     * Show integers only?
     */
    private final boolean integers;

    /**
     * The power exponent.
     */
    private int power;

    /**
     * The factor (1, 2 or 5)
     */
    private int factor;

    /**
     * The number formatter to use (an override, it can be null).
     */
    private final NumberFormat formatter;

    /**
     * Creates a new instance.
     */
    public NumberTickUnitSource() {
        this(false);
    }

    /**
     * Creates a new instance.
     *
     * @param integers  show integers only.
     */
    public NumberTickUnitSource(boolean integers) {
        this(integers, null);
    }

    /**
     * Creates a new instance.
     *
     * @param integers  show integers only?
     * @param formatter  a formatter for the axis tick labels ({@code null}
     *         permitted).
     */
    public NumberTickUnitSource(boolean integers, NumberFormat formatter) {
        this.integers = integers;
        this.formatter = formatter;
        this.power = 0;
        this.factor = 1;
    }

    @Override
    public TickUnit getLargerTickUnit(TickUnit unit) {
        TickUnit t = getCeilingTickUnit(unit);
        if (t.equals(unit)) {
            next();
            t = new NumberTickUnit(getTickSize(), getTickLabelFormat(), getMinorTickCount());
        }
        return t;
    }

    @Override
    public TickUnit getCeilingTickUnit(TickUnit unit) {
        return getCeilingTickUnit(unit.getSize());
    }

    @Override
    public TickUnit getCeilingTickUnit(double size) {
        if (Double.isInfinite(size)) {
            throw new IllegalArgumentException("Must be finite.");
        }
        this.power = (int) Math.ceil(Math.log10(size));
        if (this.integers) {
            power = Math.max(this.power, 0);
        }
        this.factor = 1;
        boolean done = false;
        // step down in size until the current size is too small or there are
        // no more units
        while (!done) {
            done = !previous();
            if (getTickSize() < size) {
                next();
                done = true;
            }
        }
        return new NumberTickUnit(getTickSize(), getTickLabelFormat(), getMinorTickCount());
    }

    private boolean next() {
        if (factor == 1) {
            factor = 2;
            return true;
        }
        if (factor == 2) {
            factor = 5;
            return true;
        }
        if (factor == 5) {
            if (power == 300) {
                return false;
            }
            power++;
            factor = 1;
            return true;
        }
        throw new IllegalStateException("We should never get here.");
    }

    private boolean previous() {
        if (factor == 1) {
            if (this.integers && power == 0 || power == -300) {
                return false;
            }
            factor = 5;
            power--;
            return true;
        }
        if (factor == 2) {
            factor = 1;
            return true;
        }
        if (factor == 5) {
            factor = 2;
            return true;
        }
        throw new IllegalStateException("We should never get here.");
    }

    private double getTickSize() {
        return this.factor * Math.pow(10.0, this.power);
    }

    /**
     * Formatter with 4 decimal places.
     */
    private final DecimalFormat dfNeg4 = new DecimalFormat("0.0000");

    /**
     * Formatter with 3 decimal places.
     */
    private final DecimalFormat dfNeg3 = new DecimalFormat("0.000");

    /**
     * Formatter with 2 decimal places.
     */
    private final DecimalFormat dfNeg2 = new DecimalFormat("0.00");

    /**
     * Formatter with 1 decimal place.
     */
    private final DecimalFormat dfNeg1 = new DecimalFormat("0.0");

    /**
     * Formatter for integers.
     */
    private final DecimalFormat df0 = new DecimalFormat("#,##0");

    /**
     * Formatter for general numbers.
     */
    private final DecimalFormat df = new DecimalFormat("#.######E0");

    private NumberFormat getTickLabelFormat() {
        if (this.formatter != null) {
            return this.formatter;
        }
        if (power == -4) {
            return dfNeg4;
        }
        if (power == -3) {
            return dfNeg3;
        }
        if (power == -2) {
            return dfNeg2;
        }
        if (power == -1) {
            return dfNeg1;
        }
        if (power >= 0 && power <= 6) {
            return df0;
        }
        return df;
    }

    private int getMinorTickCount() {
        if (factor == 1) {
            return 10;
        } else if (factor == 5) {
            return 5;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof NumberTickUnitSource)) {
            return false;
        }
        NumberTickUnitSource that = (NumberTickUnitSource) obj;
        if (this.integers != that.integers) {
            return false;
        }
        if (!Objects.equals(this.formatter, that.formatter)) {
            return false;
        }
        if (this.power != that.power) {
            return false;
        }
        if (this.factor != that.factor) {
            return false;
        }
        return true;
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
 * --------
 * Day.java
 * --------
 * (C) Copyright 2001-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * Represents a single day in the range 1-Jan-1900 to 31-Dec-9999.  This class
 * is immutable, which is a requirement for all {@link RegularTimePeriod}
 * subclasses.
 */
public class Day extends RegularTimePeriod implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -7082667380758962755L;

    /**
     * A date formatter - used for parsing, therefore we fix the locale
     * so we get dependable results.
     */
    protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);

    /**
     * A date formatter for the default locale.
     */
    protected static final DateFormat DATE_FORMAT_SHORT = DateFormat.getDateInstance(DateFormat.SHORT);

    /**
     * A date formatter for the default locale.
     */
    protected static final DateFormat DATE_FORMAT_MEDIUM = DateFormat.getDateInstance(DateFormat.MEDIUM);

    /**
     * A date formatter for the default locale.
     */
    protected static final DateFormat DATE_FORMAT_LONG = DateFormat.getDateInstance(DateFormat.LONG);

    /**
     * The day (uses SerialDate for convenience).
     */
    private SerialDate serialDate;

    /**
     * The first millisecond.
     */
    private long firstMillisecond;

    /**
     * The last millisecond.
     */
    private long lastMillisecond;

    /**
     * Creates a new instance, derived from the system date/time.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     */
    public Day() {
        this(new Date());
    }

    /**
     * Constructs a new one day time period.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param day  the day-of-the-month.
     * @param month  the month (1 to 12).
     * @param year  the year (1900 &lt;= year &lt;= 9999).
     */
    public Day(int day, int month, int year) {
        super();
        this.serialDate = SerialDate.createInstance(day, month, year);
        peg(getCalendarInstance());
    }

    /**
     * Constructs a new one day time period.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param serialDate  the day ({@code null} not permitted).
     */
    public Day(SerialDate serialDate) {
        super();
        Args.nullNotPermitted(serialDate, "serialDate");
        this.serialDate = serialDate;
        peg(getCalendarInstance());
    }

    /**
     * Constructs a new instance, based on a particular date/time.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param time  the time ({@code null} not permitted).
     *
     * @see #Day(Date, TimeZone, Locale)
     */
    public Day(Date time) {
        // defer argument checking...
        this(time, getCalendarInstance());
    }

    /**
     * Constructs a new instance, based on a particular date/time and time zone.
     *
     * @param time  the date/time ({@code null} not permitted).
     * @param zone  the time zone ({@code null} not permitted).
     * @param locale  the locale ({@code null} not permitted).
     */
    public Day(Date time, TimeZone zone, Locale locale) {
        super();
        Args.nullNotPermitted(time, "time");
        Args.nullNotPermitted(zone, "zone");
        Args.nullNotPermitted(locale, "locale");
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        initUsing(calendar);
        peg(calendar);
    }

    /**
     * Constructs a new instance, based on a particular date/time.
     * The time zone and locale are determined by the {@code calendar}
     * parameter.
     *
     * @param time the date/time ({@code null} not permitted).
     * @param calendar the calendar to use for calculations ({@code null} not permitted).
     */
    public Day(Date time, Calendar calendar) {
        super();
        Args.nullNotPermitted(time, "time");
        Args.nullNotPermitted(calendar, "calendar");
        calendar.setTime(time);
        initUsing(calendar);
        peg(calendar);
    }

    private void initUsing(Calendar calendar) {
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH) + 1;
        int y = calendar.get(Calendar.YEAR);
        this.serialDate = SerialDate.createInstance(d, m, y);
    }

    /**
     * Returns the day as a {@link SerialDate}.  Note: the reference that is
     * returned should be an instance of an immutable {@link SerialDate}
     * (otherwise the caller could use the reference to alter the state of
     * this {@code Day} instance, and {@code Day} is supposed
     * to be immutable).
     *
     * @return The day as a {@link SerialDate}.
     */
    public SerialDate getSerialDate() {
        return this.serialDate;
    }

    /**
     * Returns the year.
     *
     * @return The year.
     */
    public int getYear() {
        return this.serialDate.getYYYY();
    }

    /**
     * Returns the month.
     *
     * @return The month.
     */
    public int getMonth() {
        return this.serialDate.getMonth();
    }

    /**
     * Returns the day of the month.
     *
     * @return The day of the month.
     */
    public int getDayOfMonth() {
        return this.serialDate.getDayOfMonth();
    }

    /**
     * Returns the first millisecond of the day.  This will be determined
     * relative to the time zone specified in the constructor, or in the
     * calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The first millisecond of the day.
     *
     * @see #getLastMillisecond()
     */
    @Override
    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    /**
     * Returns the last millisecond of the day.  This will be
     * determined relative to the time zone specified in the constructor, or
     * in the calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The last millisecond of the day.
     *
     * @see #getFirstMillisecond()
     */
    @Override
    public long getLastMillisecond() {
        return this.lastMillisecond;
    }

    /**
     * Recalculates the start date/time and end date/time for this time period
     * relative to the supplied calendar (which incorporates a time zone).
     *
     * @param calendar  the calendar ({@code null} not permitted).
     *
     * @since 1.0.3
     */
    @Override
    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
        this.lastMillisecond = getLastMillisecond(calendar);
    }

    /**
     * Returns the day preceding this one.
     * No matter what time zone and locale this instance was created with,
     * the returned instance will use the default calendar for time
     * calculations, obtained with {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @return The day preceding this one.
     */
    @Override
    public RegularTimePeriod previous() {
        int serial = this.serialDate.toSerial();
        if (serial > SerialDate.SERIAL_LOWER_BOUND) {
            SerialDate yesterday = SerialDate.createInstance(serial - 1);
            return new Day(yesterday);
        } else {
            return null;
        }
    }

    /**
     * Returns the day following this one, or {@code null} if some limit
     * has been reached.
     * No matter what time zone and locale this instance was created with,
     * the returned instance will use the default calendar for time
     * calculations, obtained with {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @return The day following this one, or {@code null} if some limit
     *         has been reached.
     */
    @Override
    public RegularTimePeriod next() {
        int serial = this.serialDate.toSerial();
        if (serial < SerialDate.SERIAL_UPPER_BOUND) {
            SerialDate tomorrow = SerialDate.createInstance(serial + 1);
            return new Day(tomorrow);
        } else {
            return null;
        }
    }

    /**
     * Returns a serial index number for the day.
     *
     * @return The serial index number.
     */
    @Override
    public long getSerialIndex() {
        return this.serialDate.toSerial();
    }

    /**
     * Returns the first millisecond of the day, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  calendar to use ({@code null} not permitted).
     *
     * @return The start of the day as milliseconds since 01-01-1970.
     *
     * @throws NullPointerException if {@code calendar} is
     *     {@code null}.
     */
    @Override
    public long getFirstMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Returns the last millisecond of the day, evaluated using the supplied
     * calendar (which determines the time zone).
     *
     * @param calendar  calendar to use ({@code null} not permitted).
     *
     * @return The end of the day as milliseconds since 01-01-1970.
     *
     * @throws NullPointerException if {@code calendar} is
     *     {@code null}.
     */
    @Override
    public long getLastMillisecond(Calendar calendar) {
        int year = this.serialDate.getYYYY();
        int month = this.serialDate.getMonth();
        int day = this.serialDate.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month - 1, day, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Tests the equality of this Day object to an arbitrary object.  Returns
     * true if the target is a Day instance or a SerialDate instance
     * representing the same day as this object. In all other cases,
     * returns false.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A flag indicating whether an object is equal to this day.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Day)) {
            return false;
        }
        Day that = (Day) obj;
        if (!this.serialDate.equals(that.getSerialDate())) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this object instance.  The approach described by
     * Joshua Bloch in "Effective Java" has been used here:
     * <p>
     * {@code http://developer.java.sun.com/developer/Books/effectivejava
     * /Chapter3.pdf}
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return this.serialDate.hashCode();
    }

    /**
     * Returns an integer indicating the order of this Day object relative to
     * the specified object:
     * <p>
     * negative == before, zero == same, positive == after.
     *
     * @param o1  the object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    @Override
    public int compareTo(TimePeriod o1) {
        int result;
        // CASE 1 : Comparing to another Day object
        // ----------------------------------------
        if (o1 instanceof Day) {
            Day d = (Day) o1;
            result = -d.getSerialDate().compare(this.serialDate);
        } else // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        if (o1 instanceof RegularTimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        } else // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        {
            // consider time periods to be ordered after general objects
            result = 1;
        }
        return result;
    }

    /**
     * Returns a string representing the day.
     *
     * @return A string representing the day.
     */
    @Override
    public String toString() {
        return this.serialDate.toString();
    }

    /**
     * Parses the string argument as a day.
     * <P>
     * This method is required to recognise YYYY-MM-DD as a valid format.
     * Anything else, for now, is a bonus.
     *
     * @param s  the date string to parse.
     *
     * @return {@code null} if the string does not contain any parseable
     *      string, the day otherwise.
     */
    public static Day parseDay(String s) {
        try {
            return new Day(Day.DATE_FORMAT.parse(s));
        } catch (ParseException e1) {
            try {
                return new Day(Day.DATE_FORMAT_SHORT.parse(s));
            } catch (ParseException e2) {
                // ignore
            }
        }
        return null;
    }
}
