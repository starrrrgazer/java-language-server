package LOC.j;
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
 * SimpleHistogramDataset.java
 * ---------------------------
 * (C) Copyright 2005-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Sergei Ivanov;
 *
 */
/**
 * A dataset used for creating simple histograms with custom defined bins.
 *
 * @param <K> the key type.
 *
 * @see HistogramDataset
 */
public class SimpleHistogramDataset<K extends Comparable<K>> extends AbstractIntervalXYDataset implements IntervalXYDataset, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7997996479768018443L;

    /**
     * The series key.
     */
    private final K key;

    /**
     * The bins.
     */
    private List<SimpleHistogramBin> bins;

    /**
     * A flag that controls whether the bin count is divided by the
     * bin size.
     */
    private boolean adjustForBinSize;

    /**
     * Creates a new histogram dataset.  Note that the
     * {@code adjustForBinSize} flag defaults to {@code true}.
     *
     * @param key  the series key ({@code null} not permitted).
     */
    public SimpleHistogramDataset(K key) {
        super();
        Args.nullNotPermitted(key, "key");
        this.key = key;
        this.bins = new ArrayList<>();
        this.adjustForBinSize = true;
    }

    /**
     * Returns a flag that controls whether the bin count is divided by
     * the bin size in the {@link #getXValue(int, int)} method.
     *
     * @return A boolean.
     *
     * @see #setAdjustForBinSize(boolean)
     */
    public boolean getAdjustForBinSize() {
        return this.adjustForBinSize;
    }

    /**
     * Sets the flag that controls whether the bin count is divided by
     * the bin size in the {@link #getYValue(int, int)} method, and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param adjust  the flag.
     *
     * @see #getAdjustForBinSize()
     */
    public void setAdjustForBinSize(boolean adjust) {
        this.adjustForBinSize = adjust;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Returns the number of series in the dataset (always 1 for this dataset).
     *
     * @return The series count.
     */
    @Override
    public int getSeriesCount() {
        return 1;
    }

    /**
     * Returns the key for a series.  Since this dataset only stores a single
     * series, the {@code series} argument is ignored.
     *
     * @param series  the series (zero-based index, ignored in this dataset).
     *
     * @return The key for the series.
     */
    @Override
    public K getSeriesKey(int series) {
        return this.key;
    }

    /**
     * Returns the order of the domain (or X) values returned by the dataset.
     *
     * @return The order (never {@code null}).
     */
    @Override
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }

    /**
     * Returns the number of items in a series.  Since this dataset only stores
     * a single series, the {@code series} argument is ignored.
     *
     * @param series  the series index (zero-based, ignored in this dataset).
     *
     * @return The item count.
     */
    @Override
    public int getItemCount(int series) {
        return this.bins.size();
    }

    /**
     * Adds a bin to the dataset.  An exception is thrown if the bin overlaps
     * with any existing bin in the dataset.
     *
     * @param binToAdd  the bin ({@code null} not permitted).
     *
     * @see #removeAllBins()
     */
    public void addBin(SimpleHistogramBin binToAdd) {
        // check that the new bin doesn't overlap with any existing bin
        for (SimpleHistogramBin bin : this.bins) {
            if (binToAdd.overlapsWith(bin)) {
                throw new RuntimeException("Overlapping bin");
            }
        }
        this.bins.add(binToAdd);
        Collections.sort(this.bins);
    }

    /**
     * Adds an observation to the dataset (by incrementing the item count for
     * the appropriate bin).  A runtime exception is thrown if the value does
     * not fit into any bin.
     *
     * @param value  the value.
     */
    public void addObservation(double value) {
        addObservation(value, true);
    }

    /**
     * Adds an observation to the dataset (by incrementing the item count for
     * the appropriate bin).  A runtime exception is thrown if the value does
     * not fit into any bin.
     *
     * @param value  the value.
     * @param notify  send {@link DatasetChangeEvent} to listeners?
     */
    public void addObservation(double value, boolean notify) {
        boolean placed = false;
        Iterator iterator = this.bins.iterator();
        while (iterator.hasNext() && !placed) {
            SimpleHistogramBin bin = (SimpleHistogramBin) iterator.next();
            if (bin.accepts(value)) {
                bin.setItemCount(bin.getItemCount() + 1);
                placed = true;
            }
        }
        if (!placed) {
            throw new RuntimeException("No bin.");
        }
        if (notify) {
            notifyListeners(new DatasetChangeEvent(this, this));
        }
    }

    /**
     * Adds a set of values to the dataset and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param values  the values ({@code null} not permitted).
     *
     * @see #clearObservations()
     */
    public void addObservations(double[] values) {
        for (double value : values) {
            addObservation(value, false);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Removes all current observation data and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @since 1.0.6
     *
     * @see #addObservations(double[])
     * @see #removeAllBins()
     */
    public void clearObservations() {
        for (SimpleHistogramBin bin : this.bins) {
            bin.setItemCount(0);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Removes all bins and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     *
     * @since 1.0.6
     *
     * @see #addBin(SimpleHistogramBin)
     */
    public void removeAllBins() {
        this.bins = new ArrayList<>();
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Returns the x-value for an item within a series.  The x-values may or
     * may not be returned in ascending order, that is up to the class
     * implementing the interface.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The x-value (never {@code null}).
     */
    @Override
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    /**
     * Returns the x-value (as a double primitive) for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The x-value.
     */
    @Override
    public double getXValue(int series, int item) {
        SimpleHistogramBin bin = this.bins.get(item);
        return (bin.getLowerBound() + bin.getUpperBound()) / 2.0;
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The y-value (possibly {@code null}).
     */
    @Override
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the y-value (as a double primitive) for an item within a series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The y-value.
     *
     * @see #getAdjustForBinSize()
     */
    @Override
    public double getYValue(int series, int item) {
        SimpleHistogramBin bin = this.bins.get(item);
        if (this.adjustForBinSize) {
            return bin.getItemCount() / (bin.getUpperBound() - bin.getLowerBound());
        } else {
            return bin.getItemCount();
        }
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getStartX(int series, int item) {
        return getStartXValue(series, item);
    }

    /**
     * Returns the start x-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The start x-value.
     */
    @Override
    public double getStartXValue(int series, int item) {
        SimpleHistogramBin bin = this.bins.get(item);
        return bin.getLowerBound();
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getEndX(int series, int item) {
        return getEndXValue(series, item);
    }

    /**
     * Returns the end x-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The end x-value.
     */
    @Override
    public double getEndXValue(int series, int item) {
        SimpleHistogramBin bin = this.bins.get(item);
        return bin.getUpperBound();
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the start y-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The start y-value.
     */
    @Override
    public double getStartYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the end y-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The end y-value.
     */
    @Override
    public double getEndYValue(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Compares the dataset for equality with an arbitrary object.
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
        if (!(obj instanceof SimpleHistogramDataset)) {
            return false;
        }
        SimpleHistogramDataset that = (SimpleHistogramDataset) obj;
        if (!this.key.equals(that.key)) {
            return false;
        }
        if (this.adjustForBinSize != that.adjustForBinSize) {
            return false;
        }
        if (!this.bins.equals(that.bins)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.key);
        hash = 11 * hash + Objects.hashCode(this.bins);
        hash = 11 * hash + (this.adjustForBinSize ? 1 : 0);
        return hash;
    }

    /**
     * Returns a clone of the dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException not thrown by this class, but maybe
     *         by subclasses (if any).
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        SimpleHistogramDataset clone = (SimpleHistogramDataset) super.clone();
        clone.bins = CloneUtils.cloneList(this.bins);
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
 * ----------------
 * StringUtils.java
 * ----------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributors:     -;
 */
/**
 * String utilities.
 */
public class StringUtils {

    /**
     * Private constructor prevents object creation.
     */
    private StringUtils() {
    }

    /**
     * Helper functions to query a strings start portion. The comparison is case insensitive.
     *
     * @param base  the base string.
     * @param start  the starting text.
     *
     * @return true, if the string starts with the given starting text.
     */
    public static boolean startsWithIgnoreCase(String base, String start) {
        if (base.length() < start.length()) {
            return false;
        }
        return base.regionMatches(true, 0, start, 0, start.length());
    }

    /**
     * Helper functions to query a strings end portion. The comparison is case insensitive.
     *
     * @param base  the base string.
     * @param end  the ending text.
     *
     * @return true, if the string ends with the given ending text.
     */
    public static boolean endsWithIgnoreCase(String base, String end) {
        if (base.length() < end.length()) {
            return false;
        }
        return base.regionMatches(true, base.length() - end.length(), end, 0, end.length());
    }

    /**
     * Queries the system properties for the line separator. If access
     * to the System properties is forbidden, the UNIX default is returned.
     *
     * @return the line separator.
     */
    public static String getLineSeparator() {
        try {
            return System.getProperty("line.separator", "\n");
        } catch (Exception e) {
            return "\n";
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
 * ------------------
 * SpiderWebPlot.java
 * ------------------
 * (C) Copyright 2005-2021, by Heaps of Flavour Pty Ltd and Contributors.
 *
 * Company Info:  http://www.i4-talent.com
 *
 * Original Author:  Don Elliott;
 * Contributor(s):   David Gilbert;
 *                   Nina Jeliazkova;
 *
 */
/**
 * A plot that displays data from a {@link CategoryDataset} in the form of a
 * "spider web".  Multiple series can be plotted on the same axis to allow
 * easy comparison.  This plot doesn't support negative values at present.
 */
public class SpiderWebPlot extends Plot implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -5376340422031599463L;

    /**
     * The default head radius percent (currently 1%).
     */
    public static final double DEFAULT_HEAD = 0.01;

    /**
     * The default axis label gap (currently 10%).
     */
    public static final double DEFAULT_AXIS_LABEL_GAP = 0.10;

    /**
     * The default interior gap.
     */
    public static final double DEFAULT_INTERIOR_GAP = 0.25;

    /**
     * The maximum interior gap (currently 40%).
     */
    public static final double MAX_INTERIOR_GAP = 0.40;

    /**
     * The default starting angle for the radar chart axes.
     */
    public static final double DEFAULT_START_ANGLE = 90.0;

    /**
     * The default series label font.
     */
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * The default series label paint.
     */
    public static final Paint DEFAULT_LABEL_PAINT = Color.BLACK;

    /**
     * The default series label background paint.
     */
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255, 255, 192);

    /**
     * The default series label outline paint.
     */
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT = Color.BLACK;

    /**
     * The default series label outline stroke.
     */
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(0.5f);

    /**
     * The default series label shadow paint.
     */
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT = Color.LIGHT_GRAY;

    /**
     * The default maximum value plotted - forces the plot to evaluate
     *  the maximum from the data passed in
     */
    public static final double DEFAULT_MAX_VALUE = -1.0;

    /**
     * The head radius as a percentage of the available drawing area.
     */
    protected double headPercent;

    /**
     * The space left around the outside of the plot as a percentage.
     */
    private double interiorGap;

    /**
     * The gap between the labels and the axes as a %age of the radius.
     */
    private double axisLabelGap;

    /**
     * The paint used to draw the axis lines.
     */
    private transient Paint axisLinePaint;

    /**
     * The stroke used to draw the axis lines.
     */
    private transient Stroke axisLineStroke;

    /**
     * The dataset.
     */
    private CategoryDataset dataset;

    /**
     * The maximum value we are plotting against on each category axis
     */
    private double maxValue;

    /**
     * The data extract order (BY_ROW or BY_COLUMN). This denotes whether
     * the data series are stored in rows (in which case the category names are
     * derived from the column keys) or in columns (in which case the category
     * names are derived from the row keys).
     */
    private TableOrder dataExtractOrder;

    /**
     * The starting angle.
     */
    private double startAngle;

    /**
     * The direction for drawing the radar axis and plots.
     */
    private Rotation direction;

    /**
     * The legend item shape.
     */
    private transient Shape legendItemShape;

    /**
     * The series paint list.
     */
    private transient Map<Integer, Paint> seriesPaints;

    /**
     * The default series paint.
     */
    private transient Paint defaultSeriesPaint;

    /**
     * The series outline paint list.
     */
    private transient Map<Integer, Paint> seriesOutlinePaints;

    /**
     * The default series outline paint.
     */
    private transient Paint defaultSeriesOutlinePaint;

    /**
     * The series outline stroke list.
     */
    private transient Map<Integer, Stroke> seriesOutlineStrokes;

    /**
     * The default series outline stroke.
     */
    private transient Stroke defaultSeriesOutlineStroke;

    /**
     * The font used to display the category labels.
     */
    private Font labelFont;

    /**
     * The color used to draw the category labels.
     */
    private transient Paint labelPaint;

    /**
     * The label generator.
     */
    private CategoryItemLabelGenerator labelGenerator;

    /**
     * controls if the web polygons are filled or not
     */
    private boolean webFilled = true;

    /**
     * A tooltip generator for the plot ({@code null} permitted).
     */
    private CategoryToolTipGenerator toolTipGenerator;

    /**
     * A URL generator for the plot ({@code null} permitted).
     */
    private CategoryURLGenerator urlGenerator;

    /**
     * Creates a default plot with no dataset.
     */
    public SpiderWebPlot() {
        this(null);
    }

    /**
     * Creates a new spider web plot with the given dataset, with each row
     * representing a series.
     *
     * @param dataset  the dataset ({@code null} permitted).
     */
    public SpiderWebPlot(CategoryDataset dataset) {
        this(dataset, TableOrder.BY_ROW);
    }

    /**
     * Creates a new spider web plot with the given dataset.
     *
     * @param dataset  the dataset.
     * @param extract  controls how data is extracted ({@link TableOrder#BY_ROW}
     *                 or {@link TableOrder#BY_COLUMN}).
     */
    public SpiderWebPlot(CategoryDataset dataset, TableOrder extract) {
        super();
        Args.nullNotPermitted(extract, "extract");
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.dataExtractOrder = extract;
        this.headPercent = DEFAULT_HEAD;
        this.axisLabelGap = DEFAULT_AXIS_LABEL_GAP;
        this.axisLinePaint = Color.BLACK;
        this.axisLineStroke = new BasicStroke(1.0f);
        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.maxValue = DEFAULT_MAX_VALUE;
        this.seriesPaints = new HashMap<>();
        this.defaultSeriesPaint = null;
        this.seriesOutlinePaints = new HashMap<>();
        this.defaultSeriesOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.seriesOutlineStrokes = new HashMap<>();
        this.defaultSeriesOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelGenerator = new StandardCategoryItemLabelGenerator();
        this.legendItemShape = DEFAULT_LEGEND_ITEM_CIRCLE;
    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return The plot type.
     */
    @Override
    public String getPlotType() {
        // return localizationResources.getString("Radar_Plot");
        return ("Spider Web Plot");
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(CategoryDataset)
     */
    public CategoryDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset used by the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     */
    public void setDataset(CategoryDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of
        // change listeners...
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        // send a dataset change event to self to trigger plot change event
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    /**
     * Method to determine if the web chart is to be filled.
     *
     * @return A boolean.
     *
     * @see #setWebFilled(boolean)
     */
    public boolean isWebFilled() {
        return this.webFilled;
    }

    /**
     * Sets the webFilled flag and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #isWebFilled()
     */
    public void setWebFilled(boolean flag) {
        this.webFilled = flag;
        fireChangeEvent();
    }

    /**
     * Returns the data extract order (by row or by column).
     *
     * @return The data extract order (never {@code null}).
     *
     * @see #setDataExtractOrder(TableOrder)
     */
    public TableOrder getDataExtractOrder() {
        return this.dataExtractOrder;
    }

    /**
     * Sets the data extract order (by row or by column) and sends a
     * {@link PlotChangeEvent}to all registered listeners.
     *
     * @param order the order ({@code null} not permitted).
     *
     * @throws IllegalArgumentException if {@code order} is
     *     {@code null}.
     *
     * @see #getDataExtractOrder()
     */
    public void setDataExtractOrder(TableOrder order) {
        Args.nullNotPermitted(order, "order");
        this.dataExtractOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the head percent (the default value is 0.01).
     *
     * @return The head percent (always > 0).
     *
     * @see #setHeadPercent(double)
     */
    public double getHeadPercent() {
        return this.headPercent;
    }

    /**
     * Sets the head percent and sends a {@link PlotChangeEvent} to all
     * registered listeners.  Note that 0.10 is 10 percent.
     *
     * @param percent  the percent (must be greater than zero).
     *
     * @see #getHeadPercent()
     */
    public void setHeadPercent(double percent) {
        Args.requireNonNegative(percent, "percent");
        this.headPercent = percent;
        fireChangeEvent();
    }

    /**
     * Returns the start angle for the first radar axis.
     * <BR>
     * This is measured in degrees starting from 3 o'clock (Java Arc2D default)
     * and measuring anti-clockwise.
     *
     * @return The start angle.
     *
     * @see #setStartAngle(double)
     */
    public double getStartAngle() {
        return this.startAngle;
    }

    /**
     * Sets the starting angle and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     * <P>
     * The initial default value is 90 degrees, which corresponds to 12 o'clock.
     * A value of zero corresponds to 3 o'clock... this is the encoding used by
     * Java's Arc2D class.
     *
     * @param angle  the angle (in degrees).
     *
     * @see #getStartAngle()
     */
    public void setStartAngle(double angle) {
        this.startAngle = angle;
        fireChangeEvent();
    }

    /**
     * Returns the maximum value any category axis can take.
     *
     * @return The maximum value.
     *
     * @see #setMaxValue(double)
     */
    public double getMaxValue() {
        return this.maxValue;
    }

    /**
     * Sets the maximum value any category axis can take and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param value  the maximum value.
     *
     * @see #getMaxValue()
     */
    public void setMaxValue(double value) {
        this.maxValue = value;
        fireChangeEvent();
    }

    /**
     * Returns the direction in which the radar axes are drawn
     * (clockwise or anti-clockwise).
     *
     * @return The direction (never {@code null}).
     *
     * @see #setDirection(Rotation)
     */
    public Rotation getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction in which the radar axes are drawn and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param direction  the direction ({@code null} not permitted).
     *
     * @see #getDirection()
     */
    public void setDirection(Rotation direction) {
        Args.nullNotPermitted(direction, "direction");
        this.direction = direction;
        fireChangeEvent();
    }

    /**
     * Returns the interior gap, measured as a percentage of the available
     * drawing space.
     *
     * @return The gap (as a percentage of the available drawing space).
     *
     * @see #setInteriorGap(double)
     */
    public double getInteriorGap() {
        return this.interiorGap;
    }

    /**
     * Sets the interior gap and sends a {@link PlotChangeEvent} to all
     * registered listeners. This controls the space between the edges of the
     * plot and the plot area itself (the region where the axis labels appear).
     *
     * @param percent  the gap (as a percentage of the available drawing space).
     *
     * @see #getInteriorGap()
     */
    public void setInteriorGap(double percent) {
        if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException("Percentage outside valid range.");
        }
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            fireChangeEvent();
        }
    }

    /**
     * Returns the axis label gap.
     *
     * @return The axis label gap.
     *
     * @see #setAxisLabelGap(double)
     */
    public double getAxisLabelGap() {
        return this.axisLabelGap;
    }

    /**
     * Sets the axis label gap and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param gap  the gap.
     *
     * @see #getAxisLabelGap()
     */
    public void setAxisLabelGap(double gap) {
        this.axisLabelGap = gap;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to draw the axis lines.
     *
     * @return The paint used to draw the axis lines (never {@code null}).
     *
     * @see #setAxisLinePaint(Paint)
     * @see #getAxisLineStroke()
     */
    public Paint getAxisLinePaint() {
        return this.axisLinePaint;
    }

    /**
     * Sets the paint used to draw the axis lines and sends a
     * {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the stroke used to draw the axis lines.
     *
     * @return The stroke used to draw the axis lines (never {@code null}).
     *
     * @see #setAxisLineStroke(Stroke)
     * @see #getAxisLinePaint()
     */
    public Stroke getAxisLineStroke() {
        return this.axisLineStroke;
    }

    /**
     * Sets the stroke used to draw the axis lines and sends a
     * {@link PlotChangeEvent} to all registered listeners.
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

    //// SERIES PAINT /////////////////////////
    /**
     * Returns the paint for the specified series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (never {@code null}).
     *
     * @see #setSeriesPaint(int, Paint)
     */
    public Paint getSeriesPaint(int series) {
        // look up the paint list
        Paint result = this.seriesPaints.get(series);
        if (result == null) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                Paint p = supplier.getNextPaint();
                this.seriesPaints.put(series, p);
                result = p;
            } else {
                result = this.defaultSeriesPaint;
            }
        }
        return result;
    }

    /**
     * Sets the paint used to fill a series of the radar and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesPaint(int)
     */
    public void setSeriesPaint(int series, Paint paint) {
        this.seriesPaints.put(series, paint);
        fireChangeEvent();
    }

    /**
     * Returns the default series paint, used when no other paint is
     * available.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultSeriesPaint(Paint)
     */
    public Paint getDefaultSeriesPaint() {
        return this.defaultSeriesPaint;
    }

    /**
     * Sets the default series paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultSeriesPaint()
     */
    public void setDefaultSeriesPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultSeriesPaint = paint;
        fireChangeEvent();
    }

    //// SERIES OUTLINE PAINT ////////////////////////////
    /**
     * Returns the paint for the specified series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    public Paint getSeriesOutlinePaint(int series) {
        // otherwise look up the paint list
        Paint result = this.seriesOutlinePaints.get(series);
        if (result == null) {
            result = this.defaultSeriesOutlinePaint;
        }
        return result;
    }

    /**
     * Sets the paint used to fill a series of the radar and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        this.seriesOutlinePaints.put(series, paint);
        fireChangeEvent();
    }

    /**
     * Returns the base series paint. This is used when no other paint is
     * available.
     *
     * @return The paint (never {@code null}).
     */
    public Paint getDefaultSeriesOutlinePaint() {
        return this.defaultSeriesOutlinePaint;
    }

    /**
     * Sets the base series paint and sends a change event to all registered
     * listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setDefaultSeriesOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultSeriesOutlinePaint = paint;
        fireChangeEvent();
    }

    //// SERIES OUTLINE STROKE /////////////////////
    /**
     * Returns the stroke for the specified series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getSeriesOutlineStroke(int series) {
        Stroke result = this.seriesOutlineStrokes.get(series);
        if (result == null) {
            result = this.defaultSeriesOutlineStroke;
        }
        return result;
    }

    /**
     * Sets the stroke used to fill a series of the radar and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        this.seriesOutlineStrokes.put(series, stroke);
        fireChangeEvent();
    }

    /**
     * Returns the default series stroke. This is used when no other stroke is
     * available.
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getDefaultSeriesOutlineStroke() {
        return this.defaultSeriesOutlineStroke;
    }

    /**
     * Sets the default series stroke and sends a change event to all
     * registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     */
    public void setDefaultSeriesOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.defaultSeriesOutlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the shape used for legend items.
     *
     * @return The shape (never {@code null}).
     *
     * @see #setLegendItemShape(Shape)
     */
    public Shape getLegendItemShape() {
        return this.legendItemShape;
    }

    /**
     * Sets the shape used for legend items and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param shape  the shape ({@code null} not permitted).
     *
     * @see #getLegendItemShape()
     */
    public void setLegendItemShape(Shape shape) {
        Args.nullNotPermitted(shape, "shape");
        this.legendItemShape = shape;
        fireChangeEvent();
    }

    /**
     * Returns the series label font.
     *
     * @return The font (never {@code null}).
     *
     * @see #setLabelFont(Font)
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the series label font and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getLabelFont()
     */
    public void setLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.labelFont = font;
        fireChangeEvent();
    }

    /**
     * Returns the series label paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setLabelPaint(Paint)
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the series label paint and sends a {@link PlotChangeEvent} to all
     * registered listeners.
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
     * Returns the label generator.
     *
     * @return The label generator (never {@code null}).
     *
     * @see #setLabelGenerator(CategoryItemLabelGenerator)
     */
    public CategoryItemLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    /**
     * Sets the label generator and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param generator  the generator ({@code null} not permitted).
     *
     * @see #getLabelGenerator()
     */
    public void setLabelGenerator(CategoryItemLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        this.labelGenerator = generator;
    }

    /**
     * Returns the tool tip generator for the plot.
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setToolTipGenerator(CategoryToolTipGenerator)
     */
    public CategoryToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getToolTipGenerator()
     */
    public void setToolTipGenerator(CategoryToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the URL generator for the plot.
     *
     * @return The URL generator (possibly {@code null}).
     *
     * @see #setURLGenerator(CategoryURLGenerator)
     */
    public CategoryURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getURLGenerator()
     */
    public void setURLGenerator(CategoryURLGenerator generator) {
        this.urlGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns a collection of legend items for the spider web chart.
     *
     * @return The legend items (never {@code null}).
     */
    @Override
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (getDataset() == null) {
            return result;
        }
        List keys = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            keys = this.dataset.getRowKeys();
        } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            keys = this.dataset.getColumnKeys();
        }
        if (keys == null) {
            return result;
        }
        int series = 0;
        Iterator iterator = keys.iterator();
        Shape shape = getLegendItemShape();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            String label = key.toString();
            String description = label;
            Paint paint = getSeriesPaint(series);
            Paint outlinePaint = getSeriesOutlinePaint(series);
            Stroke stroke = getSeriesOutlineStroke(series);
            LegendItem item = new LegendItem(label, description, null, null, shape, paint, stroke, outlinePaint);
            item.setDataset(getDataset());
            item.setSeriesKey(key);
            item.setSeriesIndex(series);
            result.add(item);
            series++;
        }
        return result;
    }

    /**
     * Returns a cartesian point from a polar angle, length and bounding box
     *
     * @param bounds  the area inside which the point needs to be.
     * @param angle  the polar angle, in degrees.
     * @param length  the relative length. Given in percent of maximum extend.
     *
     * @return The cartesian point.
     */
    protected Point2D getWebPoint(Rectangle2D bounds, double angle, double length) {
        double angrad = Math.toRadians(angle);
        double x = Math.cos(angrad) * length * bounds.getWidth() / 2;
        double y = -Math.sin(angrad) * length * bounds.getHeight() / 2;
        return new Point2D.Double(bounds.getX() + x + bounds.getWidth() / 2, bounds.getY() + y + bounds.getHeight() / 2);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // adjust for insets...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        if (info != null) {
            info.setPlotArea(area);
            info.setDataArea(area);
        }
        drawBackground(g2, area);
        drawOutline(g2, area);
        Shape savedClip = g2.getClip();
        g2.clip(area);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        if (!DatasetUtils.isEmptyOrNull(this.dataset)) {
            int seriesCount, catCount;
            if (this.dataExtractOrder == TableOrder.BY_ROW) {
                seriesCount = this.dataset.getRowCount();
                catCount = this.dataset.getColumnCount();
            } else {
                seriesCount = this.dataset.getColumnCount();
                catCount = this.dataset.getRowCount();
            }
            // ensure we have a maximum value to use on the axes
            if (this.maxValue == DEFAULT_MAX_VALUE) {
                calculateMaxValue(seriesCount, catCount);
            }
            // Next, setup the plot area
            // adjust the plot area by the interior spacing value
            double gapHorizontal = area.getWidth() * getInteriorGap();
            double gapVertical = area.getHeight() * getInteriorGap();
            double X = area.getX() + gapHorizontal / 2;
            double Y = area.getY() + gapVertical / 2;
            double W = area.getWidth() - gapHorizontal;
            double H = area.getHeight() - gapVertical;
            double headW = area.getWidth() * this.headPercent;
            double headH = area.getHeight() * this.headPercent;
            // make the chart area a square
            double min = Math.min(W, H) / 2;
            X = (X + X + W) / 2 - min;
            Y = (Y + Y + H) / 2 - min;
            W = 2 * min;
            H = 2 * min;
            Point2D centre = new Point2D.Double(X + W / 2, Y + H / 2);
            Rectangle2D radarArea = new Rectangle2D.Double(X, Y, W, H);
            // draw the axis and category label
            for (int cat = 0; cat < catCount; cat++) {
                double angle = getStartAngle() + (getDirection().getFactor() * cat * 360 / catCount);
                Point2D endPoint = getWebPoint(radarArea, angle, 1);
                // 1 = end of axis
                Line2D line = new Line2D.Double(centre, endPoint);
                g2.setPaint(this.axisLinePaint);
                g2.setStroke(this.axisLineStroke);
                g2.draw(line);
                drawLabel(g2, radarArea, 0.0, cat, angle, 360.0 / catCount);
            }
            // Now actually plot each of the series polygons..
            for (int series = 0; series < seriesCount; series++) {
                drawRadarPoly(g2, radarArea, centre, info, series, catCount, headH, headW);
            }
        } else {
            drawNoDataMessage(g2, area);
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, area);
    }

    /**
     * loop through each of the series to get the maximum value
     * on each category axis
     *
     * @param seriesCount  the number of series
     * @param catCount  the number of categories
     */
    private void calculateMaxValue(int seriesCount, int catCount) {
        double v;
        Number nV;
        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            for (int catIndex = 0; catIndex < catCount; catIndex++) {
                nV = getPlotValue(seriesIndex, catIndex);
                if (nV != null) {
                    v = nV.doubleValue();
                    if (v > this.maxValue) {
                        this.maxValue = v;
                    }
                }
            }
        }
    }

    /**
     * Draws a radar plot polygon.
     *
     * @param g2 the graphics device.
     * @param plotArea the area we are plotting in (already adjusted).
     * @param centre the centre point of the radar axes
     * @param info chart rendering info.
     * @param series the series within the dataset we are plotting
     * @param catCount the number of categories per radar plot
     * @param headH the data point height
     * @param headW the data point width
     */
    protected void drawRadarPoly(Graphics2D g2, Rectangle2D plotArea, Point2D centre, PlotRenderingInfo info, int series, int catCount, double headH, double headW) {
        Polygon polygon = new Polygon();
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        // plot the data...
        for (int cat = 0; cat < catCount; cat++) {
            Number dataValue = getPlotValue(series, cat);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                if (value >= 0) {
                    // draw the polygon series...
                    // Finds our starting angle from the centre for this axis
                    double angle = getStartAngle() + (getDirection().getFactor() * cat * 360 / catCount);
                    // The following angle calc will ensure there isn't a top
                    // vertical axis - this may be useful if you don't want any
                    // given criteria to 'appear' move important than the
                    // others..
                    //  + (getDirection().getFactor()
                    //        * (cat + 0.5) * 360 / catCount);
                    // find the point at the appropriate distance end point
                    // along the axis/angle identified above and add it to the
                    // polygon
                    Point2D point = getWebPoint(plotArea, angle, value / this.maxValue);
                    polygon.addPoint((int) point.getX(), (int) point.getY());
                    // put an elipse at the point being plotted..
                    Paint paint = getSeriesPaint(series);
                    Paint outlinePaint = getSeriesOutlinePaint(series);
                    Stroke outlineStroke = getSeriesOutlineStroke(series);
                    Ellipse2D head = new Ellipse2D.Double(point.getX() - headW / 2, point.getY() - headH / 2, headW, headH);
                    g2.setPaint(paint);
                    g2.fill(head);
                    g2.setStroke(outlineStroke);
                    g2.setPaint(outlinePaint);
                    g2.draw(head);
                    if (entities != null) {
                        int row, col;
                        if (this.dataExtractOrder == TableOrder.BY_ROW) {
                            row = series;
                            col = cat;
                        } else {
                            row = cat;
                            col = series;
                        }
                        String tip = null;
                        if (this.toolTipGenerator != null) {
                            tip = this.toolTipGenerator.generateToolTip(this.dataset, row, col);
                        }
                        String url = null;
                        if (this.urlGenerator != null) {
                            url = this.urlGenerator.generateURL(this.dataset, row, col);
                        }
                        Shape area = new Rectangle((int) (point.getX() - headW), (int) (point.getY() - headH), (int) (headW * 2), (int) (headH * 2));
                        CategoryItemEntity entity = new CategoryItemEntity(area, tip, url, this.dataset, this.dataset.getRowKey(row), this.dataset.getColumnKey(col));
                        entities.add(entity);
                    }
                }
            }
        }
        // Plot the polygon
        Paint paint = getSeriesPaint(series);
        g2.setPaint(paint);
        g2.setStroke(getSeriesOutlineStroke(series));
        g2.draw(polygon);
        // Lastly, fill the web polygon if this is required
        if (this.webFilled) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g2.fill(polygon);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        }
    }

    /**
     * Returns the value to be plotted at the intersection of the
     * series and the category.  This allows us to plot
     * {@code BY_ROW} or {@code BY_COLUMN} which basically is just
     * reversing the definition of the categories and data series being
     * plotted.
     *
     * @param series the series to be plotted.
     * @param cat the category within the series to be plotted.
     *
     * @return The value to be plotted (possibly {@code null}).
     *
     * @see #getDataExtractOrder()
     */
    protected Number getPlotValue(int series, int cat) {
        Number value = null;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            value = this.dataset.getValue(series, cat);
        } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
            value = this.dataset.getValue(cat, series);
        }
        return value;
    }

    /**
     * Draws the label for one axis.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area
     * @param value  the value of the label (ignored).
     * @param cat  the category (zero-based index).
     * @param startAngle  the starting angle.
     * @param extent  the extent of the arc.
     */
    protected void drawLabel(Graphics2D g2, Rectangle2D plotArea, double value, int cat, double startAngle, double extent) {
        FontRenderContext frc = g2.getFontRenderContext();
        String label;
        if (this.dataExtractOrder == TableOrder.BY_ROW) {
            // if series are in rows, then the categories are the column keys
            label = this.labelGenerator.generateColumnLabel(this.dataset, cat);
        } else {
            // if series are in columns, then the categories are the row keys
            label = this.labelGenerator.generateRowLabel(this.dataset, cat);
        }
        Rectangle2D labelBounds = getLabelFont().getStringBounds(label, frc);
        LineMetrics lm = getLabelFont().getLineMetrics(label, frc);
        double ascent = lm.getAscent();
        Point2D labelLocation = calculateLabelLocation(labelBounds, ascent, plotArea, startAngle);
        Composite saveComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2.setPaint(getLabelPaint());
        g2.setFont(getLabelFont());
        g2.drawString(label, (float) labelLocation.getX(), (float) labelLocation.getY());
        g2.setComposite(saveComposite);
    }

    /**
     * Returns the location for a label
     *
     * @param labelBounds the label bounds.
     * @param ascent the ascent (height of font).
     * @param plotArea the plot area
     * @param startAngle the start angle for the pie series.
     *
     * @return The location for a label.
     */
    protected Point2D calculateLabelLocation(Rectangle2D labelBounds, double ascent, Rectangle2D plotArea, double startAngle) {
        Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);
        Point2D point1 = arc1.getEndPoint();
        double deltaX = -(point1.getX() - plotArea.getCenterX()) * this.axisLabelGap;
        double deltaY = -(point1.getY() - plotArea.getCenterY()) * this.axisLabelGap;
        double labelX = point1.getX() - deltaX;
        double labelY = point1.getY() - deltaY;
        if (labelX < plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth();
        }
        if (labelX == plotArea.getCenterX()) {
            labelX -= labelBounds.getWidth() / 2;
        }
        if (labelY > plotArea.getCenterY()) {
            labelY += ascent;
        }
        return new Point2D.Double(labelX, labelY);
    }

    /**
     * Tests this plot for equality with an arbitrary object.
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
        if (!(obj instanceof SpiderWebPlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        SpiderWebPlot that = (SpiderWebPlot) obj;
        if (!this.dataExtractOrder.equals(that.dataExtractOrder)) {
            return false;
        }
        if (this.headPercent != that.headPercent) {
            return false;
        }
        if (this.interiorGap != that.interiorGap) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (!this.direction.equals(that.direction)) {
            return false;
        }
        if (this.maxValue != that.maxValue) {
            return false;
        }
        if (this.webFilled != that.webFilled) {
            return false;
        }
        if (this.axisLabelGap != that.axisLabelGap) {
            return false;
        }
        if (!PaintUtils.equal(this.axisLinePaint, that.axisLinePaint)) {
            return false;
        }
        if (!this.axisLineStroke.equals(that.axisLineStroke)) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }
        if (!PaintUtils.equal(this.seriesPaints, that.seriesPaints)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultSeriesPaint, that.defaultSeriesPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.seriesOutlinePaints, that.seriesOutlinePaints)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultSeriesOutlinePaint, that.defaultSeriesOutlinePaint)) {
            return false;
        }
        if (!this.seriesOutlineStrokes.equals(that.seriesOutlineStrokes)) {
            return false;
        }
        if (!this.defaultSeriesOutlineStroke.equals(that.defaultSeriesOutlineStroke)) {
            return false;
        }
        if (!this.labelFont.equals(that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!this.labelGenerator.equals(that.labelGenerator)) {
            return false;
        }
        if (!Objects.equals(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.urlGenerator, that.urlGenerator)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this plot.
     *
     * @return A clone of this plot.
     *
     * @throws CloneNotSupportedException if the plot cannot be cloned for
     *         any reason.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        SpiderWebPlot clone = (SpiderWebPlot) super.clone();
        clone.legendItemShape = CloneUtils.clone(this.legendItemShape);
        clone.seriesPaints = CloneUtils.cloneMapValues(this.seriesPaints);
        clone.seriesOutlinePaints = CloneUtils.cloneMapValues(this.seriesOutlinePaints);
        clone.seriesOutlineStrokes = CloneUtils.cloneMapValues(this.seriesOutlineStrokes);
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
        SerialUtils.writeShape(this.legendItemShape, stream);
        SerialUtils.writeMapOfPaint(this.seriesPaints, stream);
        SerialUtils.writePaint(this.defaultSeriesPaint, stream);
        SerialUtils.writeMapOfPaint(this.seriesOutlinePaints, stream);
        SerialUtils.writePaint(this.defaultSeriesOutlinePaint, stream);
        SerialUtils.writeMapOfStroke(this.seriesOutlineStrokes, stream);
        SerialUtils.writeStroke(this.defaultSeriesOutlineStroke, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
        SerialUtils.writePaint(this.axisLinePaint, stream);
        SerialUtils.writeStroke(this.axisLineStroke, stream);
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
        this.legendItemShape = SerialUtils.readShape(stream);
        this.seriesPaints = SerialUtils.readMapOfPaint(stream);
        this.defaultSeriesPaint = SerialUtils.readPaint(stream);
        this.seriesOutlinePaints = SerialUtils.readMapOfPaint(stream);
        this.defaultSeriesOutlinePaint = SerialUtils.readPaint(stream);
        this.seriesOutlineStrokes = SerialUtils.readMapOfStroke(stream);
        this.defaultSeriesOutlineStroke = SerialUtils.readStroke(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
        this.axisLinePaint = SerialUtils.readPaint(stream);
        this.axisLineStroke = SerialUtils.readStroke(stream);
        if (this.dataset != null) {
            this.dataset.addChangeListener(this);
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
 * ------------------
 * XYBarRenderer.java
 * ------------------
 * (C) Copyright 2001-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *                   Bill Kelemen;
 *                   Marc van Glabbeek (bug 1775452);
 *                   Richard West, Advanced Micro Devices, Inc.;
 *                   Yuri Blankenstein;
 *
 */
/**
 * A renderer that draws bars on an {@link XYPlot} (requires an
 * {@link IntervalXYDataset}).  The example shown here is generated by the
 * {@code XYBarChartDemo1.java} program included in the JFreeChart
 * demo collection:
 * <br><br>
 * <img src="doc-files/XYBarRendererSample.png" alt="XYBarRendererSample.png">
 */
public class XYBarRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 770559577251370036L;

    /**
     * The default bar painter assigned to each new instance of this renderer.
     */
    private static XYBarPainter defaultBarPainter = new StandardXYBarPainter();

    /**
     * Returns the default bar painter.
     *
     * @return The default bar painter.
     */
    public static XYBarPainter getDefaultBarPainter() {
        return XYBarRenderer.defaultBarPainter;
    }

    /**
     * Sets the default bar painter.
     *
     * @param painter  the painter ({@code null} not permitted).
     */
    public static void setDefaultBarPainter(XYBarPainter painter) {
        Args.nullNotPermitted(painter, "painter");
        XYBarRenderer.defaultBarPainter = painter;
    }

    /**
     * The default value for the initialisation of the shadowsVisible flag.
     */
    private static boolean defaultShadowsVisible = true;

    /**
     * Returns the default value for the {@code shadowsVisible} flag.
     *
     * @return A boolean.
     *
     * @see #setDefaultShadowsVisible(boolean)
     */
    public static boolean getDefaultShadowsVisible() {
        return XYBarRenderer.defaultShadowsVisible;
    }

    /**
     * Sets the default value for the shadows visible flag.
     *
     * @param visible  the new value for the default.
     *
     * @see #getDefaultShadowsVisible()
     */
    public static void setDefaultShadowsVisible(boolean visible) {
        XYBarRenderer.defaultShadowsVisible = visible;
    }

    /**
     * The state class used by this renderer.
     */
    protected static class XYBarRendererState extends XYItemRendererState {

        /**
         * Base for bars against the range axis, in Java 2D space.
         */
        private double g2Base;

        /**
         * Creates a new state object.
         *
         * @param info  the plot rendering info.
         */
        public XYBarRendererState(PlotRenderingInfo info) {
            super(info);
        }

        /**
         * Returns the base (range) value in Java 2D space.
         *
         * @return The base value.
         */
        public double getG2Base() {
            return this.g2Base;
        }

        /**
         * Sets the range axis base in Java2D space.
         *
         * @param value  the value.
         */
        public void setG2Base(double value) {
            this.g2Base = value;
        }
    }

    /**
     * The default base value for the bars.
     */
    private double base;

    /**
     * A flag that controls whether the bars use the y-interval supplied by the
     * dataset.
     */
    private boolean useYInterval;

    /**
     * Percentage margin (to reduce the width of bars).
     */
    private double margin;

    /**
     * A flag that controls whether bar outlines are drawn.
     */
    private boolean drawBarOutline;

    /**
     * An optional class used to transform gradient paint objects to fit each
     * bar.
     */
    private GradientPaintTransformer gradientPaintTransformer;

    /**
     * The shape used to represent a bar in each legend item (this should never
     * be {@code null}).
     */
    private transient Shape legendBar;

    /**
     * The fallback position if a positive item label doesn't fit inside the
     * bar.
     */
    private ItemLabelPosition positiveItemLabelPositionFallback;

    /**
     * The fallback position if a negative item label doesn't fit inside the
     * bar.
     */
    private ItemLabelPosition negativeItemLabelPositionFallback;

    /**
     * The bar painter (never {@code null}).
     */
    private XYBarPainter barPainter;

    /**
     * The flag that controls whether shadows are drawn for the bars.
     */
    private boolean shadowsVisible;

    /**
     * The x-offset for the shadow effect.
     */
    private double shadowXOffset;

    /**
     * The y-offset for the shadow effect.
     */
    private double shadowYOffset;

    /**
     * A factor used to align the bars about the x-value.
     */
    private double barAlignmentFactor;

    /**
     * The minimum size for the bar to draw a label
     */
    private Dimension minimumLabelSize;

    /**
     * {@code true} if the label should be aligned to the visible part of the bar.
     */
    private boolean showLabelInsideVisibleBar;

    /**
     * The default constructor.
     */
    public XYBarRenderer() {
        this(0.0);
    }

    /**
     * Constructs a new renderer.
     *
     * @param margin  the percentage amount to trim from the width of each bar.
     */
    public XYBarRenderer(double margin) {
        super();
        this.margin = margin;
        this.base = 0.0;
        this.useYInterval = false;
        this.gradientPaintTransformer = new StandardGradientPaintTransformer();
        this.drawBarOutline = false;
        this.legendBar = new Rectangle2D.Double(-3.0, -5.0, 6.0, 10.0);
        this.barPainter = getDefaultBarPainter();
        this.shadowsVisible = getDefaultShadowsVisible();
        this.shadowXOffset = 4.0;
        this.shadowYOffset = 4.0;
        this.barAlignmentFactor = -1.0;
    }

    /**
     * Returns the base value for the bars.
     *
     * @return The base value for the bars.
     *
     * @see #setBase(double)
     */
    public double getBase() {
        return this.base;
    }

    /**
     * Sets the base value for the bars and sends a {@link RendererChangeEvent}
     * to all registered listeners.  The base value is not used if the dataset's
     * y-interval is being used to determine the bar length.
     *
     * @param base  the new base value.
     *
     * @see #getBase()
     * @see #getUseYInterval()
     */
    public void setBase(double base) {
        this.base = base;
        fireChangeEvent();
    }

    /**
     * Returns a flag that determines whether the y-interval from the dataset is
     * used to calculate the length of each bar.
     *
     * @return A boolean.
     *
     * @see #setUseYInterval(boolean)
     */
    public boolean getUseYInterval() {
        return this.useYInterval;
    }

    /**
     * Sets the flag that determines whether the y-interval from the dataset is
     * used to calculate the length of each bar, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param use  the flag.
     *
     * @see #getUseYInterval()
     */
    public void setUseYInterval(boolean use) {
        if (this.useYInterval != use) {
            this.useYInterval = use;
            fireChangeEvent();
        }
    }

    /**
     * Returns the margin which is a percentage amount by which the bars are
     * trimmed.
     *
     * @return The margin.
     *
     * @see #setMargin(double)
     */
    public double getMargin() {
        return this.margin;
    }

    /**
     * Sets the percentage amount by which the bars are trimmed and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param margin  the new margin.
     *
     * @see #getMargin()
     */
    public void setMargin(double margin) {
        this.margin = margin;
        fireChangeEvent();
    }

    /**
     * Returns a flag that controls whether bar outlines are drawn.
     *
     * @return A boolean.
     *
     * @see #setDrawBarOutline(boolean)
     */
    public boolean isDrawBarOutline() {
        return this.drawBarOutline;
    }

    /**
     * Sets the flag that controls whether bar outlines are drawn and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param draw  the flag.
     *
     * @see #isDrawBarOutline()
     */
    public void setDrawBarOutline(boolean draw) {
        this.drawBarOutline = draw;
        fireChangeEvent();
    }

    /**
     * Returns the gradient paint transformer (an object used to transform
     * gradient paint objects to fit each bar).
     *
     * @return A transformer ({@code null} possible).
     *
     * @see #setGradientPaintTransformer(GradientPaintTransformer)
     */
    public GradientPaintTransformer getGradientPaintTransformer() {
        return this.gradientPaintTransformer;
    }

    /**
     * Sets the gradient paint transformer and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param transformer  the transformer ({@code null} permitted).
     *
     * @see #getGradientPaintTransformer()
     */
    public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
        this.gradientPaintTransformer = transformer;
        fireChangeEvent();
    }

    /**
     * Returns the shape used to represent bars in each legend item.
     *
     * @return The shape used to represent bars in each legend item (never
     *         {@code null}).
     *
     * @see #setLegendBar(Shape)
     */
    public Shape getLegendBar() {
        return this.legendBar;
    }

    /**
     * Sets the shape used to represent bars in each legend item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param bar  the bar shape ({@code null} not permitted).
     *
     * @see #getLegendBar()
     */
    public void setLegendBar(Shape bar) {
        Args.nullNotPermitted(bar, "bar");
        this.legendBar = bar;
        fireChangeEvent();
    }

    /**
     * Returns the fallback position for positive item labels that don't fit
     * within a bar.
     *
     * @return The fallback position ({@code null} possible).
     *
     * @see #setPositiveItemLabelPositionFallback(ItemLabelPosition)
     */
    public ItemLabelPosition getPositiveItemLabelPositionFallback() {
        return this.positiveItemLabelPositionFallback;
    }

    /**
     * Sets the fallback position for positive item labels that don't fit
     * within a bar, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param position  the position ({@code null} permitted).
     *
     * @see #getPositiveItemLabelPositionFallback()
     */
    public void setPositiveItemLabelPositionFallback(ItemLabelPosition position) {
        this.positiveItemLabelPositionFallback = position;
        fireChangeEvent();
    }

    /**
     * Returns the fallback position for negative item labels that don't fit
     * within a bar.
     *
     * @return The fallback position ({@code null} possible).
     *
     * @see #setNegativeItemLabelPositionFallback(ItemLabelPosition)
     */
    public ItemLabelPosition getNegativeItemLabelPositionFallback() {
        return this.negativeItemLabelPositionFallback;
    }

    /**
     * Sets the fallback position for negative item labels that don't fit
     * within a bar, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param position  the position ({@code null} permitted).
     *
     * @see #getNegativeItemLabelPositionFallback()
     */
    public void setNegativeItemLabelPositionFallback(ItemLabelPosition position) {
        this.negativeItemLabelPositionFallback = position;
        fireChangeEvent();
    }

    /**
     * Returns the bar painter.
     *
     * @return The bar painter (never {@code null}).
     */
    public XYBarPainter getBarPainter() {
        return this.barPainter;
    }

    /**
     * Sets the bar painter and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param painter  the painter ({@code null} not permitted).
     */
    public void setBarPainter(XYBarPainter painter) {
        Args.nullNotPermitted(painter, "painter");
        this.barPainter = painter;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether shadows are drawn for
     * the bars.
     *
     * @return A boolean.
     */
    public boolean getShadowsVisible() {
        return this.shadowsVisible;
    }

    /**
     * Sets the flag that controls whether the renderer
     * draws shadows for the bars, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the new flag value.
     */
    public void setShadowVisible(boolean visible) {
        this.shadowsVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the shadow x-offset.
     *
     * @return The shadow x-offset.
     */
    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    /**
     * Sets the x-offset for the bar shadow and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param offset  the offset.
     */
    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the shadow y-offset.
     *
     * @return The shadow y-offset.
     */
    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    /**
     * Sets the y-offset for the bar shadow and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param offset  the offset.
     */
    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the bar alignment factor.
     *
     * @return The bar alignment factor.
     */
    public double getBarAlignmentFactor() {
        return this.barAlignmentFactor;
    }

    /**
     * Sets the bar alignment factor and sends a {@link RendererChangeEvent}
     * to all registered listeners.  If the alignment factor is outside the
     * range 0.0 to 1.0, no alignment will be performed by the renderer.
     *
     * @param factor  the factor.
     */
    public void setBarAlignmentFactor(double factor) {
        this.barAlignmentFactor = factor;
        fireChangeEvent();
    }

    /**
     * Returns the minimum size for the bar to draw a label.
     *
     * @return The minimum size to draw a label.
     */
    public Dimension getMinimumLabelSize() {
        return minimumLabelSize;
    }

    /**
     * Sets the minimum size for the bar to draw a label.
     *
     * @param minimumLabelSize The size
     */
    public void setMinimumLabelSize(Dimension minimumLabelSize) {
        this.minimumLabelSize = minimumLabelSize;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the label should be aligned to the visible part
     * of the bar.
     *
     * @return {@code true} if the label should be aligned to the visible part
     *         of the bar.
     * @see #setShowLabelInsideVisibleBar(boolean)
     */
    public boolean isShowLabelInsideVisibleBar() {
        return showLabelInsideVisibleBar;
    }

    /**
     * Sets whether the label should be aligned to the visible part of the
     * bar.<br>
     * This setting has no effect when {@link ItemLabelAnchor#isInternal()}
     * returns {@code false}.
     *
     * @param showLabelInsideVisibleBar {@code true} to align to the visible
     *                                  part.
     */
    public void setShowLabelInsideVisibleBar(boolean showLabelInsideVisibleBar) {
        this.showLabelInsideVisibleBar = showLabelInsideVisibleBar;
        fireChangeEvent();
    }

    /**
     * Initialises the renderer and returns a state object that should be
     * passed to all subsequent calls to the drawItem() method.  Here we
     * calculate the Java2D y-coordinate for zero, since all the bars have
     * their bases fixed at zero.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param dataset  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return A state object.
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        XYBarRendererState state = new XYBarRendererState(info);
        ValueAxis rangeAxis = plot.getRangeAxisForDataset(plot.indexOf(dataset));
        state.setG2Base(rangeAxis.valueToJava2D(this.base, dataArea, plot.getRangeAxisEdge()));
        return state;
    }

    /**
     * Returns a default legend item for the specified series.  Subclasses
     * should override this method to generate customised items.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot xyplot = getPlot();
        if (xyplot == null) {
            return null;
        }
        XYDataset dataset = xyplot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        LegendItem result;
        XYSeriesLabelGenerator lg = getLegendItemLabelGenerator();
        String label = lg.generateLabel(dataset, series);
        String description = label;
        String toolTipText = null;
        if (getLegendItemToolTipGenerator() != null) {
            toolTipText = getLegendItemToolTipGenerator().generateLabel(dataset, series);
        }
        String urlText = null;
        if (getLegendItemURLGenerator() != null) {
            urlText = getLegendItemURLGenerator().generateLabel(dataset, series);
        }
        Shape shape = this.legendBar;
        Paint paint = lookupSeriesPaint(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        if (this.drawBarOutline) {
            result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        } else {
            result = new LegendItem(label, description, toolTipText, urlText, shape, paint);
        }
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        if (getGradientPaintTransformer() != null) {
            result.setFillPaintTransformer(getGradientPaintTransformer());
        }
        return result;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc.).
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
        if (!getItemVisible(series, item)) {
            return;
        }
        IntervalXYDataset intervalDataset = (IntervalXYDataset) dataset;
        double value0;
        double value1;
        if (this.useYInterval) {
            value0 = intervalDataset.getStartYValue(series, item);
            value1 = intervalDataset.getEndYValue(series, item);
        } else {
            value0 = this.base;
            value1 = intervalDataset.getYValue(series, item);
        }
        if (Double.isNaN(value0) || Double.isNaN(value1)) {
            return;
        }
        if (value0 <= value1) {
            if (!rangeAxis.getRange().intersects(value0, value1)) {
                return;
            }
        } else {
            if (!rangeAxis.getRange().intersects(value1, value0)) {
                return;
            }
        }
        double translatedValue0 = rangeAxis.valueToJava2D(value0, dataArea, plot.getRangeAxisEdge());
        double translatedValue1 = rangeAxis.valueToJava2D(value1, dataArea, plot.getRangeAxisEdge());
        double bottom = Math.min(translatedValue0, translatedValue1);
        double top = Math.max(translatedValue0, translatedValue1);
        double startX = intervalDataset.getStartXValue(series, item);
        if (Double.isNaN(startX)) {
            return;
        }
        double endX = intervalDataset.getEndXValue(series, item);
        if (Double.isNaN(endX)) {
            return;
        }
        if (startX <= endX) {
            if (!domainAxis.getRange().intersects(startX, endX)) {
                return;
            }
        } else {
            if (!domainAxis.getRange().intersects(endX, startX)) {
                return;
            }
        }
        // is there an alignment adjustment to be made?
        if (this.barAlignmentFactor >= 0.0 && this.barAlignmentFactor <= 1.0) {
            double x = intervalDataset.getXValue(series, item);
            double interval = endX - startX;
            startX = x - interval * this.barAlignmentFactor;
            endX = startX + interval;
        }
        RectangleEdge location = plot.getDomainAxisEdge();
        double translatedStartX = domainAxis.valueToJava2D(startX, dataArea, location);
        double translatedEndX = domainAxis.valueToJava2D(endX, dataArea, location);
        double translatedWidth = Math.max(1, Math.abs(translatedEndX - translatedStartX));
        double left = Math.min(translatedStartX, translatedEndX);
        if (getMargin() > 0.0) {
            double cut = translatedWidth * getMargin();
            translatedWidth = translatedWidth - cut;
            left = left + cut / 2;
        }
        Rectangle2D bar = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.isHorizontal()) {
            // clip left and right bounds to data area
            bottom = Math.max(bottom, dataArea.getMinX());
            top = Math.min(top, dataArea.getMaxX());
            bar = new Rectangle2D.Double(bottom, left, top - bottom, translatedWidth);
        } else if (orientation.isVertical()) {
            // clip top and bottom bounds to data area
            bottom = Math.max(bottom, dataArea.getMinY());
            top = Math.min(top, dataArea.getMaxY());
            bar = new Rectangle2D.Double(left, bottom, translatedWidth, top - bottom);
        }
        boolean positive = (value1 > 0.0);
        boolean inverted = rangeAxis.isInverted();
        RectangleEdge barBase;
        if (orientation.isHorizontal()) {
            if (positive && inverted || !positive && !inverted) {
                barBase = RectangleEdge.RIGHT;
            } else {
                barBase = RectangleEdge.LEFT;
            }
        } else {
            if (positive && !inverted || !positive && inverted) {
                barBase = RectangleEdge.BOTTOM;
            } else {
                barBase = RectangleEdge.TOP;
            }
        }
        if (state.getElementHinting()) {
            beginElementGroup(g2, dataset.getSeriesKey(series), item);
        }
        if (getShadowsVisible()) {
            this.barPainter.paintBarShadow(g2, this, series, item, bar, barBase, !this.useYInterval);
        }
        this.barPainter.paintBar(g2, this, series, item, bar, barBase);
        if (state.getElementHinting()) {
            endElementGroup(g2);
        }
        if (isItemLabelVisible(series, item)) {
            XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
            drawItemLabel(g2, dataset, series, item, plot, generator, bar, value1 < 0.0);
        }
        // update the crosshair point
        double x1 = (startX + endX) / 2.0;
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, location);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(crosshairState, x1, y1, datasetIndex, transX1, transY1, plot.getOrientation());
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addEntity(entities, bar, dataset, series, item, 0.0, 0.0);
        }
    }

    /**
     * Draws an item label.  This method is provided as an alternative to
     * {@link #drawItemLabel(Graphics2D, PlotOrientation, XYDataset, int, int,
     * double, double, boolean)} so that the bar can be used to calculate the
     * label anchor point.
     *
     * @param g2  the graphics device.
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param item  the item index.
     * @param plot  the plot.
     * @param generator  the label generator ({@code null} permitted, in
     *         which case the method does nothing, just returns).
     * @param bar  the bar.
     * @param negative  a flag indicating a negative value.
     */
    protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative) {
        if (generator == null) {
            // nothing to do
            return;
        }
        String label = generator.generateLabel(dataset, series, item);
        if (label == null) {
            // nothing to do
            return;
        }
        Font labelFont = getItemLabelFont(series, item);
        g2.setFont(labelFont);
        Paint paint = getItemLabelPaint(series, item);
        g2.setPaint(paint);
        // find out where to place the label...
        ItemLabelPosition position;
        if (!negative) {
            position = getPositiveItemLabelPosition(series, item);
        } else {
            position = getNegativeItemLabelPosition(series, item);
        }
        Rectangle2D drawBar = bar;
        if (position.getItemLabelAnchor().isInternal()) {
            if (showLabelInsideVisibleBar && g2.getClipBounds() != null) {
                drawBar = drawBar.createIntersection(g2.getClipBounds().getBounds2D());
            }
            Rectangle2D labelBar = getItemLabelInsets().createInsetRectangle(drawBar);
            if (minimumLabelSize != null && (labelBar.getWidth() < minimumLabelSize.getWidth() || labelBar.getHeight() < minimumLabelSize.getHeight())) {
                // nothing to do
                return;
            }
        }
        // work out the label anchor point...
        Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), drawBar, plot.getOrientation());
        String drawLabel = calculateLabeltoDraw(label, anchorPoint, position, drawBar, g2);
        if (drawLabel == null) {
            if (!negative) {
                position = getPositiveItemLabelPositionFallback();
            } else {
                position = getNegativeItemLabelPositionFallback();
            }
            if (position != null) {
                g2.setFont(labelFont);
                g2.setPaint(paint);
                if (position.getItemLabelAnchor().isInternal()) {
                    if (showLabelInsideVisibleBar && g2.getClipBounds() != null) {
                        drawBar = drawBar.createIntersection(g2.getClipBounds().getBounds2D());
                    }
                    Rectangle2D labelBar = getItemLabelInsets().createInsetRectangle(drawBar);
                    if (minimumLabelSize != null && (labelBar.getWidth() < minimumLabelSize.getWidth() || labelBar.getHeight() < minimumLabelSize.getHeight())) {
                        // nothing to do
                        return;
                    }
                }
                anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), drawBar, plot.getOrientation());
                drawLabel = calculateLabeltoDraw(label, anchorPoint, position, drawBar, g2);
            }
        }
        if (drawLabel != null) {
            TextUtils.drawRotatedString(drawLabel, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    /**
     * @return The label to draw or {@code null} if label should not be drawn.
     */
    private String calculateLabeltoDraw(String label, Point2D anchorPoint, ItemLabelPosition position, Rectangle2D bar, Graphics2D g2) {
        if (!position.getItemLabelAnchor().isInternal()) {
            return label;
        }
        // Taking the bounds of the bounds will ceil the rectangle to its
        // smallest enclosing integer instance, this avoids rounding errors when
        // testing if the label fits
        Rectangle2D labelBar = getItemLabelInsets().createInsetRectangle(bar).getBounds();
        switch(position.getItemLabelClip()) {
            case CLIP:
                Shape currentClip = g2.getClip();
                if (currentClip == null) {
                    g2.setClip(labelBar);
                } else {
                    g2.setClip(labelBar.createIntersection(currentClip.getBounds2D()));
                }
                return label;
            case NONE:
                return label;
            default:
        }
        String result = label;
        while (result != null && !result.isEmpty()) {
            Rectangle2D labelBounds = TextUtils.calculateRotatedStringBounds(result, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor()).getBounds2D();
            if (labelBar.getHeight() >= labelBounds.getHeight() && labelBar.getWidth() >= labelBounds.getWidth()) {
                // Label fits
                return result;
            } else if (labelBar.getHeight() < labelBounds.getHeight()) {
                // Optimization: label will never fit due to insufficient height
                return null;
            } else {
                switch(position.getItemLabelClip()) {
                    case FIT:
                        return null;
                    case TRUNCATE:
                        {
                            String nextResult = result.replaceFirst(".(\\.{3})?$", "...");
                            if ("...".equals(nextResult) || result.equals(nextResult)) {
                                return null;
                            } else {
                                result = nextResult;
                            }
                            break;
                        }
                    case TRUNCATE_WORD:
                        {
                            String nextResult = result.replaceFirst("\\W+\\w*(\\.{3})?$", "...");
                            if ("...".equals(nextResult) || result.equals(nextResult)) {
                                return null;
                            } else {
                                result = nextResult;
                            }
                            break;
                        }
                    default:
                        throw new IllegalStateException("Should never happen");
                }
            }
        }
        return null;
    }

    /**
     * Calculates the item label anchor point.
     *
     * <pre>
     * Inside:
     *  +-----------------+
     *  | 10/11  12   1/2 |
     *  |   9     C    3  |
     *  |  7/8    6   4/5 |
     *  +-----------------+
     *
     * Outside:
     * 10/11       12         1/2
     *     +----------------+
     *     |                |
     *   9 |                |  3
     *     |                |
     *     +----------------+
     *  7/8        6          4/5
     * </pre>
     *
     * @param anchor  the anchor.
     * @param bar  the bar.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor, Rectangle2D bar, PlotOrientation orientation) {
        Point2D result = null;
        RectangleInsets labelInsets = getItemLabelInsets();
        Rectangle2D insideBar = labelInsets.createInsetRectangle(bar);
        Rectangle2D outsideBar = labelInsets.createOutsetRectangle(bar);
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(bar.getCenterX(), bar.getCenterY());
        } else if (anchor == ItemLabelAnchor.INSIDE1 || anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(insideBar.getMaxX(), insideBar.getMinY());
        } else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(insideBar.getMaxX(), bar.getCenterY());
        } else if (anchor == ItemLabelAnchor.INSIDE4 || anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(insideBar.getMaxX(), insideBar.getMaxY());
        } else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(bar.getCenterX(), insideBar.getMaxY());
        } else if (anchor == ItemLabelAnchor.INSIDE7 || anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(insideBar.getMinX(), insideBar.getMaxY());
        } else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(insideBar.getMinX(), bar.getCenterY());
        } else if (anchor == ItemLabelAnchor.INSIDE10 || anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(insideBar.getMinX(), insideBar.getMinY());
        } else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(bar.getCenterX(), insideBar.getMinY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE1 || anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(outsideBar.getMaxX(), outsideBar.getMinY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(outsideBar.getMaxX(), bar.getCenterY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE4 || anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(outsideBar.getMaxX(), outsideBar.getMaxY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(bar.getCenterX(), outsideBar.getMaxY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE7 || anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(outsideBar.getMinX(), outsideBar.getMaxY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(outsideBar.getMinX(), bar.getCenterY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE10 || anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(outsideBar.getMinX(), outsideBar.getMinY());
        } else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(bar.getCenterX(), outsideBar.getMinY());
        }
        return result;
    }

    /**
     * Returns the lower and upper bounds (range) of the x-values in the
     * specified dataset.  Since this renderer uses the x-interval in the
     * dataset, this is taken into account for the range.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range ({@code null} if the dataset is
     *         {@code null} or empty).
     */
    @Override
    public Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, true);
    }

    /**
     * Returns the lower and upper bounds (range) of the y-values in the
     * specified dataset.  If the renderer is plotting the y-interval from the
     * dataset, this is taken into account for the range.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range ({@code null} if the dataset is
     *         {@code null} or empty).
     */
    @Override
    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, this.useYInterval);
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
        XYBarRenderer result = (XYBarRenderer) super.clone();
        if (this.gradientPaintTransformer != null) {
            result.gradientPaintTransformer = CloneUtils.clone(this.gradientPaintTransformer);
        }
        result.legendBar = CloneUtils.clone(this.legendBar);
        return result;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
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
        if (!(obj instanceof XYBarRenderer)) {
            return false;
        }
        XYBarRenderer that = (XYBarRenderer) obj;
        if (this.base != that.base) {
            return false;
        }
        if (this.drawBarOutline != that.drawBarOutline) {
            return false;
        }
        if (this.margin != that.margin) {
            return false;
        }
        if (this.useYInterval != that.useYInterval) {
            return false;
        }
        if (!Objects.equals(this.gradientPaintTransformer, that.gradientPaintTransformer)) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendBar, that.legendBar)) {
            return false;
        }
        if (!Objects.equals(this.positiveItemLabelPositionFallback, that.positiveItemLabelPositionFallback)) {
            return false;
        }
        if (!Objects.equals(this.negativeItemLabelPositionFallback, that.negativeItemLabelPositionFallback)) {
            return false;
        }
        if (!this.barPainter.equals(that.barPainter)) {
            return false;
        }
        if (this.shadowsVisible != that.shadowsVisible) {
            return false;
        }
        if (this.shadowXOffset != that.shadowXOffset) {
            return false;
        }
        if (this.shadowYOffset != that.shadowYOffset) {
            return false;
        }
        if (this.barAlignmentFactor != that.barAlignmentFactor) {
            return false;
        }
        return super.equals(obj);
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
        this.legendBar = SerialUtils.readShape(stream);
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
        SerialUtils.writeShape(this.legendBar, stream);
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
 * DefaultIntervalXYDataset.java
 * -----------------------------
 * (C) Copyright 2006-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 */
/**
 * A dataset that defines a range (interval) for both the x-values and the
 * y-values.  This implementation uses six arrays to store the x, start-x,
 * end-x, y, start-y and end-y values.
 * <br><br>
 * An alternative implementation of the {@link IntervalXYDataset} interface
 * is provided by the {@link XYIntervalSeriesCollection} class.
 *
 * @param <S> the series key type.
 */
public class DefaultIntervalXYDataset<S extends Comparable<S>> extends AbstractIntervalXYDataset<S> implements PublicCloneable {

    /**
     * Storage for the series keys.  This list must be kept in sync with the
     * seriesList.
     */
    private List<S> seriesKeys;

    /**
     * Storage for the series in the dataset.  We use a list because the
     * order of the series is significant.  This list must be kept in sync
     * with the seriesKeys list.
     */
    private List<double[][]> seriesList;

    /**
     * Creates a new {@code DefaultIntervalXYDataset} instance, initially
     * containing no data.
     */
    public DefaultIntervalXYDataset() {
        super();
        this.seriesKeys = new ArrayList<>();
        this.seriesList = new ArrayList<>();
    }

    /**
     * Returns the number of series in the dataset.
     *
     * @return The series count.
     */
    @Override
    public int getSeriesCount() {
        return this.seriesList.size();
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The key for the series.
     *
     * @throws IllegalArgumentException if {@code series} is not in the
     *     specified range.
     */
    @Override
    public S getSeriesKey(int series) {
        Args.requireInRange(series, "series", 0, this.seriesKeys.size() - 1);
        return this.seriesKeys.get(series);
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     *
     * @return The item count.
     *
     * @throws IllegalArgumentException if {@code series} is not in the
     *     specified range.
     */
    @Override
    public int getItemCount(int series) {
        Args.requireInRange(series, "series", 0, this.seriesList.size() - 1);
        double[][] seriesArray = this.seriesList.get(series);
        return seriesArray[0].length;
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The x-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getX(int, int)
     */
    @Override
    public double getXValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[0][item];
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The y-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getY(int, int)
     */
    @Override
    public double getYValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[3][item];
    }

    /**
     * Returns the starting x-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The starting x-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getStartX(int, int)
     */
    @Override
    public double getStartXValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[1][item];
    }

    /**
     * Returns the ending x-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The ending x-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getEndX(int, int)
     */
    @Override
    public double getEndXValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[2][item];
    }

    /**
     * Returns the starting y-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The starting y-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getStartY(int, int)
     */
    @Override
    public double getStartYValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[4][item];
    }

    /**
     * Returns the ending y-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The ending y-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getEndY(int, int)
     */
    @Override
    public double getEndYValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[5][item];
    }

    /**
     * Returns the ending x-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The ending x-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getEndXValue(int, int)
     */
    @Override
    public Number getEndX(int series, int item) {
        return getEndXValue(series, item);
    }

    /**
     * Returns the ending y-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The ending y-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getEndYValue(int, int)
     */
    @Override
    public Number getEndY(int series, int item) {
        return getEndYValue(series, item);
    }

    /**
     * Returns the starting x-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The starting x-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getStartXValue(int, int)
     */
    @Override
    public Number getStartX(int series, int item) {
        return getStartXValue(series, item);
    }

    /**
     * Returns the starting y-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The starting y-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getStartYValue(int, int)
     */
    @Override
    public Number getStartY(int series, int item) {
        return getStartYValue(series, item);
    }

    /**
     * Returns the x-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The x-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getXValue(int, int)
     */
    @Override
    public Number getX(int series, int item) {
        return getXValue(series, item);
    }

    /**
     * Returns the y-value for an item within a series.
     *
     * @param series  the series index (in the range {@code 0} to
     *     {@code getSeriesCount() - 1}).
     * @param item  the item index (in the range {@code 0} to
     *     {@code getItemCount(series)}).
     *
     * @return The y-value.
     *
     * @throws ArrayIndexOutOfBoundsException if {@code series} is not
     *     within the specified range.
     * @throws ArrayIndexOutOfBoundsException if {@code item} is not
     *     within the specified range.
     *
     * @see #getYValue(int, int)
     */
    @Override
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Adds a series or if a series with the same key already exists replaces
     * the data for that series, then sends a {@link DatasetChangeEvent} to
     * all registered listeners.
     *
     * @param seriesKey  the series key ({@code null} not permitted).
     * @param data  the data (must be an array with length 6, containing six
     *     arrays of equal length, the first three containing the x-values
     *     (x, xLow and xHigh) and the last three containing the y-values
     *     (y, yLow and yHigh)).
     */
    public void addSeries(S seriesKey, double[][] data) {
        Args.nullNotPermitted(seriesKey, "seriesKey");
        Args.nullNotPermitted(data, "data");
        if (data.length != 6) {
            throw new IllegalArgumentException("The 'data' array must have length == 6.");
        }
        int length = data[0].length;
        if (length != data[1].length || length != data[2].length || length != data[3].length || length != data[4].length || length != data[5].length) {
            throw new IllegalArgumentException("The 'data' array must contain six arrays with equal length.");
        }
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex == -1) {
            // add a new series
            this.seriesKeys.add(seriesKey);
            this.seriesList.add(data);
        } else {
            // replace an existing series
            this.seriesList.remove(seriesIndex);
            this.seriesList.add(seriesIndex, data);
        }
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Tests this {@code DefaultIntervalXYDataset} instance for equality
     * with an arbitrary object.  This method returns {@code true} if and
     * only if:
     * <ul>
     * <li>{@code obj} is not {@code null};</li>
     * <li>{@code obj} is an instance of {@code DefaultIntervalXYDataset};</li>
     * <li>both datasets have the same number of series, each containing
     *         exactly the same values.</li>
     * </ul>
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
        if (!(obj instanceof DefaultIntervalXYDataset)) {
            return false;
        }
        DefaultIntervalXYDataset<String> that = (DefaultIntervalXYDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] d1 = this.seriesList.get(i);
            double[][] d2 = that.seriesList.get(i);
            double[] d1x = d1[0];
            double[] d2x = d2[0];
            if (!Arrays.equals(d1x, d2x)) {
                return false;
            }
            double[] d1xs = d1[1];
            double[] d2xs = d2[1];
            if (!Arrays.equals(d1xs, d2xs)) {
                return false;
            }
            double[] d1xe = d1[2];
            double[] d2xe = d2[2];
            if (!Arrays.equals(d1xe, d2xe)) {
                return false;
            }
            double[] d1y = d1[3];
            double[] d2y = d2[3];
            if (!Arrays.equals(d1y, d2y)) {
                return false;
            }
            double[] d1ys = d1[4];
            double[] d2ys = d2[4];
            if (!Arrays.equals(d1ys, d2ys)) {
                return false;
            }
            double[] d1ye = d1[5];
            double[] d2ye = d2[5];
            if (!Arrays.equals(d1ye, d2ye)) {
                return false;
            }
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
        int result;
        result = this.seriesKeys.hashCode();
        result = 29 * result + this.seriesList.hashCode();
        return result;
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the dataset contains a series with
     *         a key that cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultIntervalXYDataset clone = (DefaultIntervalXYDataset) super.clone();
        clone.seriesKeys = new ArrayList<>(this.seriesKeys);
        clone.seriesList = new ArrayList<>(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] data = this.seriesList.get(i);
            double[] x = data[0];
            double[] xStart = data[1];
            double[] xEnd = data[2];
            double[] y = data[3];
            double[] yStart = data[4];
            double[] yEnd = data[5];
            double[] xx = new double[x.length];
            double[] xxStart = new double[xStart.length];
            double[] xxEnd = new double[xEnd.length];
            double[] yy = new double[y.length];
            double[] yyStart = new double[yStart.length];
            double[] yyEnd = new double[yEnd.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(xStart, 0, xxStart, 0, xStart.length);
            System.arraycopy(xEnd, 0, xxEnd, 0, xEnd.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            System.arraycopy(yStart, 0, yyStart, 0, yStart.length);
            System.arraycopy(yEnd, 0, yyEnd, 0, yEnd.length);
            clone.seriesList.add(i, new double[][] { xx, xxStart, xxEnd, yy, yyStart, yyEnd });
        }
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
 * ---------------------------
 * CategoryDatasetHandler.java
 * ---------------------------
 * (C) Copyright 2003-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A SAX handler for reading a {@link CategoryDataset} from an XML file.
 */
public class CategoryDatasetHandler extends RootHandler implements DatasetTags {

    /**
     * The dataset under construction.
     */
    private DefaultCategoryDataset<String, String> dataset;

    /**
     * Creates a new handler.
     */
    public CategoryDatasetHandler() {
        super();
        this.dataset = null;
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset.
     */
    public CategoryDataset<String, String> getDataset() {
        return this.dataset;
    }

    /**
     * Adds an item to the dataset.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param value  the value.
     */
    public void addItem(String rowKey, String columnKey, Number value) {
        this.dataset.addValue(value, rowKey, columnKey);
    }

    /**
     * The start of an element.
     *
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     * @param atts  the element attributes.
     *
     * @throws SAXException for errors.
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.startElement(namespaceURI, localName, qName, atts);
        } else if (qName.equals(CATEGORYDATASET_TAG)) {
            this.dataset = new DefaultCategoryDataset<>();
        } else if (qName.equals(SERIES_TAG)) {
            CategorySeriesHandler subhandler = new CategorySeriesHandler(this);
            getSubHandlers().push(subhandler);
            subhandler.startElement(namespaceURI, localName, qName, atts);
        } else {
            throw new SAXException("Element not recognised: " + qName);
        }
    }

    /**
     * The end of an element.
     *
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     *
     * @throws SAXException for errors.
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.endElement(namespaceURI, localName, qName);
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
 * --------------------------------
 * AbstractPieLabelDistributor.java
 * --------------------------------
 * (C) Copyright 2007-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 * 
 */
/**
 * A base class for handling the distribution of pie section labels.  Create
 * your own subclass and set it using the
 * {@link PiePlot#setLabelDistributor(AbstractPieLabelDistributor)} method
 * if you want to customise the label distribution.
 */
public abstract class AbstractPieLabelDistributor implements Serializable {

    /**
     * The label records.
     */
    protected List labels;

    /**
     * Creates a new instance.
     */
    public AbstractPieLabelDistributor() {
        this.labels = new java.util.ArrayList();
    }

    /**
     * Returns a label record from the list.
     *
     * @param index  the index.
     *
     * @return The label record.
     */
    public PieLabelRecord getPieLabelRecord(int index) {
        return (PieLabelRecord) this.labels.get(index);
    }

    /**
     * Adds a label record.
     *
     * @param record  the label record ({@code null} not permitted).
     */
    public void addPieLabelRecord(PieLabelRecord record) {
        Args.nullNotPermitted(record, "record");
        this.labels.add(record);
    }

    /**
     * Returns the number of items in the list.
     *
     * @return The item count.
     */
    public int getItemCount() {
        return this.labels.size();
    }

    /**
     * Clears the list of labels.
     */
    public void clear() {
        this.labels.clear();
    }

    /**
     * Called by the {@link PiePlot} class.  Implementations should distribute
     * the labels in this.labels then return.
     *
     * @param minY  the y-coordinate for the top of the label area.
     * @param height  the height of the label area.
     */
    public abstract void distributeLabels(double minY, double height);
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
 * XYBarPainter.java
 * -----------------
 * (C) Copyright 2008-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * The interface for plugin painter for the {@link XYBarRenderer} class.  When
 * developing a class that implements this interface, bear in mind the
 * following:
 * <ul>
 * <li>the {@code equals(Object)} method should be overridden;</li>
 * <li>instances of the class should be immutable OR implement the
 *     {@code PublicCloneable} interface, so that a renderer using the
 *     painter can be cloned reliably;
 * <li>the class should be {@code Serializable}, otherwise chart
 *     serialization will not be supported.</li>
 * </ul>
 */
public interface XYBarPainter {

    /**
     * Paints a single bar on behalf of a renderer.
     *
     * @param g2  the graphics target.
     * @param renderer  the renderer.
     * @param row  the row index for the item.
     * @param column  the column index for the item.
     * @param bar  the bounds for the bar.
     * @param base  the base of the bar.
     */
    void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base);

    /**
     * Paints the shadow for a single bar on behalf of a renderer.
     *
     * @param g2  the graphics target.
     * @param renderer  the renderer.
     * @param row  the row index for the item.
     * @param column  the column index for the item.
     * @param bar  the bounds for the bar.
     * @param base  the base of the bar.
     * @param pegShadow  peg the shadow to the base of the bar?
     */
    void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow);
}
