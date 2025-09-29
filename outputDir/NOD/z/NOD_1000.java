package NOD.z;
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
 * MatrixSeries.java
 * -----------------
 * (C) Copyright 2003-present, by Barak Naveh and Contributors.
 *
 * Original Author:  Barak Naveh;
 * Contributor(s):   David Gilbert;
 *                   Zhitao Wang;
 */
/**
 * Represents a dense matrix M[i,j] where each Mij item of the matrix has a
 * value (default is 0).
 *
 * @param <K> the series key type.
 */
public class MatrixSeries<K extends Comparable<K>> extends Series<K> implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7934188527308315704L;

    /**
     * Series matrix values
     */
    protected double[][] data;

    /**
     * Constructs a new matrix series.
     * <p>
     * By default, all matrix items are initialzed to 0.
     * </p>
     *
     * @param name  series name ({@code null} not permitted).
     * @param rows  the number of rows.
     * @param columns  the number of columns.
     */
    public MatrixSeries(K name, int rows, int columns) {
        super(name);
        this.data = new double[rows][columns];
        zeroAll();
    }

    /**
     * Returns the number of columns in this matrix series.
     *
     * @return The number of columns in this matrix series.
     */
    public int getColumnsCount() {
        return this.data[0].length;
    }

    /**
     * Return the matrix item at the specified index.  Note that this method
     * creates a new {@code double} instance every time it is called.
     *
     * @param itemIndex item index.
     *
     * @return The matrix item at the specified index.
     *
     * @see #get(int, int)
     */
    public Number getItem(int itemIndex) {
        int i = getItemRow(itemIndex);
        int j = getItemColumn(itemIndex);
        return get(i, j);
    }

    /**
     * Returns the column of the specified item.
     *
     * @param itemIndex the index of the item.
     *
     * @return The column of the specified item.
     */
    public int getItemColumn(int itemIndex) {
        //assert itemIndex >= 0 && itemIndex < getItemCount();
        return itemIndex % getColumnsCount();
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     */
    @Override
    public int getItemCount() {
        return getRowCount() * getColumnsCount();
    }

    /**
     * Returns the row of the specified item.
     *
     * @param itemIndex the index of the item.
     *
     * @return The row of the specified item.
     */
    public int getItemRow(int itemIndex) {
        //assert itemIndex >= 0 && itemIndex < getItemCount();
        return itemIndex / getColumnsCount();
    }

    /**
     * Returns the number of rows in this matrix series.
     *
     * @return The number of rows in this matrix series.
     */
    public int getRowCount() {
        return this.data.length;
    }

    /**
     * Returns the value of the specified item in this matrix series.
     *
     * @param i the row of the item.
     * @param j the column of the item.
     *
     * @return The value of the specified item in this matrix series.
     *
     * @see #getItem(int)
     * @see #update(int, int, double)
     */
    public double get(int i, int j) {
        return this.data[i][j];
    }

    /**
     * Updates the value of the specified item in this matrix series.
     *
     * @param i the row of the item.
     * @param j the column of the item.
     * @param mij the new value for the item.
     *
     * @see #get(int, int)
     */
    public void update(int i, int j, double mij) {
        this.data[i][j] = mij;
        fireSeriesChanged();
    }

    /**
     * Sets all matrix values to zero and sends a
     * {@link org.jfree.data.general.SeriesChangeEvent} to all registered
     * listeners.
     */
    public void zeroAll() {
        int rows = getRowCount();
        int columns = getColumnsCount();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.data[row][column] = 0.0;
            }
        }
        fireSeriesChanged();
    }

    /**
     * Tests this object instance for equality with an arbitrary object.
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
        if (!(obj instanceof MatrixSeries)) {
            return false;
        }
        MatrixSeries<K> that = (MatrixSeries) obj;
        if (!(getRowCount() == that.getRowCount())) {
            return false;
        }
        if (!(getColumnsCount() == that.getColumnsCount())) {
            return false;
        }
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnsCount(); c++) {
                if (get(r, c) != that.get(r, c)) {
                    return false;
                }
            }
        }
        return super.equals(obj);
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
 * VectorSeriesCollection.java
 * ---------------------------
 * (C) Copyright 2007-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A collection of {@link VectorSeries} objects.
 *
 * @param <S> The type for the series keys.
 *
 * @since 1.0.6
 */
public class VectorSeriesCollection<S extends Comparable<S>> extends AbstractXYDataset<S> implements VectorXYDataset<S>, PublicCloneable, Serializable {

    /**
     * Storage for the data series.
     */
    private List<VectorSeries<S>> data;

    /**
     * Creates a new {@code VectorSeriesCollection} instance.
     */
    public VectorSeriesCollection() {
        this.data = new ArrayList<>();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void addSeries(VectorSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Removes the specified series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     *
     * @return A boolean indicating whether the series has actually been
     *         removed.
     */
    public boolean removeSeries(VectorSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        boolean removed = this.data.remove(series);
        if (removed) {
            series.removeChangeListener(this);
            fireDatasetChanged();
        }
        return removed;
    }

    /**
     * Removes all the series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     */
    public void removeAllSeries() {
        // deregister the collection as a change listener to each series in the
        // collection
        for (VectorSeries<S> series : this.data) {
            series.removeChangeListener(this);
        }
        // remove all the series from the collection and notify listeners.
        this.data.clear();
        fireDatasetChanged();
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
     */
    @Override
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series from the collection.
     *
     * @param series  the series index (zero-based).
     *
     * @return The series.
     *
     * @throws IllegalArgumentException if {@code series} is not in the
     *     range {@code 0} to {@code getSeriesCount() - 1}.
     */
    public VectorSeries<S> getSeries(int series) {
        Args.requireInRange(series, "series", 0, this.data.size() - 1);
        return this.data.get(series);
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The key for a series.
     *
     * @throws IllegalArgumentException if {@code series} is not in the
     *     specified range.
     */
    @Override
    public S getSeriesKey(int series) {
        // defer argument checking
        return getSeries(series).getKey();
    }

    /**
     * Returns the index of the specified series, or -1 if that series is not
     * present in the dataset.
     *
     * @param series  the series ({@code null} not permitted).
     *
     * @return The series index.
     */
    public int indexOf(VectorSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        return this.data.indexOf(series);
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The item count.
     *
     * @throws IllegalArgumentException if {@code series} is not in the
     *     range {@code 0} to {@code getSeriesCount() - 1}.
     */
    @Override
    public int getItemCount(int series) {
        // defer argument checking
        return getSeries(series).getItemCount();
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public double getXValue(int series, int item) {
        VectorSeries<S> s = this.data.get(series);
        VectorDataItem di = (VectorDataItem) s.getDataItem(item);
        return di.getXValue();
    }

    /**
     * Returns the x-value for an item within a series.  Note that this method
     * creates a new {@link Double} instance every time it is called---use
     * {@link #getXValue(int, int)} instead, if possible.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The y-value.
     */
    @Override
    public double getYValue(int series, int item) {
        VectorSeries<S> s = this.data.get(series);
        VectorDataItem di = (VectorDataItem) s.getDataItem(item);
        return di.getYValue();
    }

    /**
     * Returns the y-value for an item within a series.  Note that this method
     * creates a new {@link Double} instance every time it is called---use
     * {@link #getYValue(int, int)} instead, if possible.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The y-value.
     */
    @Override
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the vector for an item in a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The vector (possibly {@code null}).
     */
    @Override
    public Vector getVector(int series, int item) {
        VectorSeries<S> s = this.data.get(series);
        VectorDataItem di = (VectorDataItem) s.getDataItem(item);
        return di.getVector();
    }

    /**
     * Returns the x-component of the vector for an item in a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-component of the vector.
     */
    @Override
    public double getVectorXValue(int series, int item) {
        VectorSeries<S> s = this.data.get(series);
        VectorDataItem di = (VectorDataItem) s.getDataItem(item);
        return di.getVectorX();
    }

    /**
     * Returns the y-component of the vector for an item in a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The y-component of the vector.
     */
    @Override
    public double getVectorYValue(int series, int item) {
        VectorSeries<S> s = this.data.get(series);
        VectorDataItem di = (VectorDataItem) s.getDataItem(item);
        return di.getVectorY();
    }

    /**
     * Tests this instance for equality with an arbitrary object.
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
        if (!(obj instanceof VectorSeriesCollection)) {
            return false;
        }
        VectorSeriesCollection<S> that = (VectorSeriesCollection<S>) obj;
        return Objects.equals(this.data, that.data);
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        VectorSeriesCollection<S> clone = (VectorSeriesCollection<S>) super.clone();
        clone.data = CloneUtils.cloneList(this.data);
        return clone;
    }
}
