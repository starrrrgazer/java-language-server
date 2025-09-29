package NOD.ao;
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
 * -------------------------------
 * XYIntervalSeriesCollection.java
 * -------------------------------
 * (C) Copyright 2006-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A collection of {@link XYIntervalSeries} objects.
 *
 * @param <S> The type for the series keys.
 *
 * @since 1.0.3
 *
 * @see XYIntervalSeries
 */
public class XYIntervalSeriesCollection<S extends Comparable<S>> extends AbstractIntervalXYDataset<S> implements IntervalXYDataset<S>, PublicCloneable, Serializable {

    /**
     * Storage for the data series.
     */
    private List<XYIntervalSeries<S>> data;

    /**
     * Creates a new instance of {@code XIntervalSeriesCollection}.
     */
    public XYIntervalSeriesCollection() {
        this.data = new ArrayList<>();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void addSeries(XYIntervalSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        this.data.add(series);
        series.addChangeListener(this);
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
    public XYIntervalSeries<S> getSeries(int series) {
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
    public Number getX(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getX(item);
    }

    /**
     * Returns the start x-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public double getStartXValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getXLowValue(item);
    }

    /**
     * Returns the end x-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public double getEndXValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getXHighValue(item);
    }

    /**
     * Returns the y-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public double getYValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getYValue(item);
    }

    /**
     * Returns the start y-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public double getStartYValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getYLowValue(item);
    }

    /**
     * Returns the end y-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The value.
     */
    @Override
    public double getEndYValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getYHighValue(item);
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
    public Number getY(int series, int item) {
        return getYValue(series, item);
    }

    /**
     * Returns the start x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getStartX(int series, int item) {
        return getStartXValue(series, item);
    }

    /**
     * Returns the end x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getEndX(int series, int item) {
        return getEndXValue(series, item);
    }

    /**
     * Returns the start y-value for an item within a series.  This method
     * maps directly to {@link #getY(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The start y-value.
     */
    @Override
    public Number getStartY(int series, int item) {
        return getStartYValue(series, item);
    }

    /**
     * Returns the end y-value for an item within a series.  This method
     * maps directly to {@link #getY(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The end y-value.
     */
    @Override
    public Number getEndY(int series, int item) {
        return getEndYValue(series, item);
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     *
     * @since 1.0.10
     */
    public void removeSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        XYIntervalSeries<S> ts = this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     *
     * @since 1.0.10
     */
    public void removeSeries(XYIntervalSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            fireDatasetChanged();
        }
    }

    /**
     * Removes all the series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @since 1.0.10
     */
    public void removeAllSeries() {
        // Unregister the collection as a change listener to each series in
        // the collection.
        for (int i = 0; i < this.data.size(); i++) {
            XYIntervalSeries<S> series = this.data.get(i);
            series.removeChangeListener(this);
        }
        this.data.clear();
        fireDatasetChanged();
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
        if (!(obj instanceof XYIntervalSeriesCollection)) {
            return false;
        }
        XYIntervalSeriesCollection<S> that = (XYIntervalSeriesCollection) obj;
        return Objects.equals(this.data, that.data);
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone of this dataset.
     *
     * @throws CloneNotSupportedException if there is a problem cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XYIntervalSeriesCollection clone = (XYIntervalSeriesCollection) super.clone();
        clone.data = CloneUtils.cloneList(this.data);
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
 * -------------
 * FlowPlot.java
 * -------------
 * (C) Copyright 2021-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 */
/**
 * A plot for visualising flows defined in a {@link FlowDataset}.  This enables
 * the production of a type of Sankey chart.  The example shown here is
 * produced by the {@code FlowPlotDemo1.java} program included in the JFreeChart
 * Demo Collection:
 * <img src="doc-files/FlowPlotDemo1.svg" width="600" height="400" alt="FlowPlotDemo1.svg">
 *
 * @param <K> the data key type.
 */
public class FlowPlot<K extends Comparable<K>> extends Plot implements Cloneable, PublicCloneable, Serializable {

    /**
     * The source of data.
     */
    private FlowDataset<K> dataset;

    /**
     * The node width in Java 2D user-space units.
     */
    private double nodeWidth = 20.0;

    /**
     * The gap between nodes (expressed as a percentage of the plot height).
     */
    private double nodeMargin = 0.01;

    /**
     * The percentage of the plot width to assign to a gap between the nodes
     * and the flow representation.
     */
    private double flowMargin = 0.005;

    /**
     * Stores colors for specific nodes - if there isn't a color in here for
     * the node, the default node color will be used (unless the color swatch
     * is active).
     */
    private Map<NodeKey<K>, Color> nodeColorMap;

    /**
     * Node colors.
     */
    private List<Color> nodeColorSwatch;

    /**
     * A pointer into the color swatch.
     */
    private int nodeColorSwatchPointer;

    /**
     * The default node color if nothing is defined in the nodeColorMap.
     */
    private Color defaultNodeColor;

    /**
     * Default node label font.
     */
    private Font defaultNodeLabelFont;

    /**
     * Default node label paint.
     */
    private Paint defaultNodeLabelPaint;

    /**
     * Default node label alignment.
     */
    private VerticalAlignment nodeLabelAlignment;

    /**
     * The x-offset for node labels.
     */
    private double nodeLabelOffsetX;

    /**
     * The y-offset for node labels.
     */
    private double nodeLabelOffsetY;

    /**
     * The tool tip generator - if null, no tool tips will be displayed.
     */
    private FlowLabelGenerator toolTipGenerator;

    /**
     * Creates a new instance that will source data from the specified dataset.
     *
     * @param dataset  the dataset.
     */
    public FlowPlot(FlowDataset<K> dataset) {
        super();
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.nodeColorMap = new HashMap<>();
        this.nodeColorSwatch = new ArrayList<>();
        this.defaultNodeColor = Color.GRAY;
        this.defaultNodeLabelFont = new Font(Font.DIALOG, Font.BOLD, 12);
        this.defaultNodeLabelPaint = Color.BLACK;
        this.nodeLabelAlignment = VerticalAlignment.CENTER;
        this.nodeLabelOffsetX = 2.0;
        this.nodeLabelOffsetY = 2.0;
        this.toolTipGenerator = new StandardFlowLabelGenerator();
    }

    /**
     * Returns a string identifying the plot type.
     *
     * @return A string identifying the plot type.
     */
    @Override
    public String getPlotType() {
        return "FlowPlot";
    }

    /**
     * Returns a reference to the dataset.
     *
     * @return A reference to the dataset (possibly {@code null}).
     */
    public FlowDataset<K> getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset for the plot and sends a change notification to all
     * registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     */
    public void setDataset(FlowDataset<K> dataset) {
        this.dataset = dataset;
        fireChangeEvent();
    }

    /**
     * Returns the node margin (expressed as a percentage of the available
     * plotting space) which is the gap between nodes (sources or destinations).
     * The initial (default) value is {@code 0.01} (1 percent).
     *
     * @return The node margin.
     */
    public double getNodeMargin() {
        return this.nodeMargin;
    }

    /**
     * Sets the node margin and sends a change notification to all registered
     * listeners.
     *
     * @param margin  the margin (expressed as a percentage).
     */
    public void setNodeMargin(double margin) {
        Args.requireNonNegative(margin, "margin");
        this.nodeMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the flow margin.  This determines the gap between the graphic
     * representation of the nodes (sources and destinations) and the curved
     * flow representation.  This is expressed as a percentage of the plot
     * width so that it remains proportional as the plot is resized.  The
     * initial (default) value is {@code 0.005} (0.5 percent).
     *
     * @return The flow margin.
     */
    public double getFlowMargin() {
        return this.flowMargin;
    }

    /**
     * Sets the flow margin and sends a change notification to all registered
     * listeners.
     *
     * @param margin  the margin (must be 0.0 or higher).
     */
    public void setFlowMargin(double margin) {
        Args.requireNonNegative(margin, "margin");
        this.flowMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the width of the source and destination nodes, expressed in
     * Java2D user-space units.  The initial (default) value is {@code 20.0}.
     *
     * @return The width.
     */
    public double getNodeWidth() {
        return this.nodeWidth;
    }

    /**
     * Sets the width for the source and destination nodes and sends a change
     * notification to all registered listeners.
     *
     * @param width  the width.
     */
    public void setNodeWidth(double width) {
        this.nodeWidth = width;
        fireChangeEvent();
    }

    /**
     * Returns the list of colors that will be used to autopopulate the node
     * colors when they are first rendered.  If the list is empty, no color
     * will be assigned to the node so, unless it is manually set, the default
     * color will apply.  This method returns a copy of the list, modifying
     * the returned list will not affect the plot.
     *
     * @return The list of colors (possibly empty, but never {@code null}).
     */
    public List<Color> getNodeColorSwatch() {
        return new ArrayList<>(this.nodeColorSwatch);
    }

    /**
     * Sets the color swatch for the plot and sends a change
     * notification to all registered listeners.
     *
     * @param colors  the list of colors ({@code null} not permitted).
     */
    public void setNodeColorSwatch(List<Color> colors) {
        Args.nullNotPermitted(colors, "colors");
        this.nodeColorSwatch = colors;
        fireChangeEvent();
    }

    /**
     * Returns the fill color for the specified node.
     *
     * @param nodeKey  the node key ({@code null} not permitted).
     *
     * @return The fill color (possibly {@code null}).
     */
    public Color getNodeFillColor(NodeKey<K> nodeKey) {
        return this.nodeColorMap.get(nodeKey);
    }

    /**
     * Sets the fill color for the specified node and sends a change
     * notification to all registered listeners.
     *
     * @param nodeKey  the node key ({@code null} not permitted).
     * @param color  the fill color ({@code null} permitted).
     */
    public void setNodeFillColor(NodeKey<K> nodeKey, Color color) {
        this.nodeColorMap.put(nodeKey, color);
        fireChangeEvent();
    }

    /**
     * Returns the default node color.  This is used when no specific node color
     * has been specified.  The initial (default) value is {@code Color.GRAY}.
     *
     * @return The default node color (never {@code null}).
     */
    public Color getDefaultNodeColor() {
        return this.defaultNodeColor;
    }

    /**
     * Sets the default node color and sends a change event to registered
     * listeners.
     *
     * @param color  the color ({@code null} not permitted).
     */
    public void setDefaultNodeColor(Color color) {
        Args.nullNotPermitted(color, "color");
        this.defaultNodeColor = color;
        fireChangeEvent();
    }

    /**
     * Returns the default font used to display labels for the source and
     * destination nodes.  The initial (default) value is
     * {@code Font(Font.DIALOG, Font.BOLD, 12)}.
     *
     * @return The default font (never {@code null}).
     */
    public Font getDefaultNodeLabelFont() {
        return this.defaultNodeLabelFont;
    }

    /**
     * Sets the default font used to display labels for the source and
     * destination nodes and sends a change notification to all registered
     * listeners.
     *
     * @param font  the font ({@code null} not permitted).
     */
    public void setDefaultNodeLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.defaultNodeLabelFont = font;
        fireChangeEvent();
    }

    /**
     * Returns the default paint used to display labels for the source and
     * destination nodes.  The initial (default) value is {@code Color.BLACK}.
     *
     * @return The default paint (never {@code null}).
     */
    public Paint getDefaultNodeLabelPaint() {
        return this.defaultNodeLabelPaint;
    }

    /**
     * Sets the default paint used to display labels for the source and
     * destination nodes and sends a change notification to all registered
     * listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setDefaultNodeLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultNodeLabelPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the vertical alignment of the node labels relative to the node.
     * The initial (default) value is {@link VerticalAlignment#CENTER}.
     *
     * @return The alignment (never {@code null}).
     */
    public VerticalAlignment getNodeLabelAlignment() {
        return this.nodeLabelAlignment;
    }

    /**
     * Sets the vertical alignment of the node labels and sends a change
     * notification to all registered listeners.
     *
     * @param alignment  the new alignment ({@code null} not permitted).
     */
    public void setNodeLabelAlignment(VerticalAlignment alignment) {
        Args.nullNotPermitted(alignment, "alignment");
        this.nodeLabelAlignment = alignment;
        fireChangeEvent();
    }

    /**
     * Returns the x-offset for the node labels.
     *
     * @return The x-offset for the node labels.
     */
    public double getNodeLabelOffsetX() {
        return this.nodeLabelOffsetX;
    }

    /**
     * Sets the x-offset for the node labels and sends a change notification
     * to all registered listeners.
     *
     * @param offsetX  the node label x-offset in Java2D units.
     */
    public void setNodeLabelOffsetX(double offsetX) {
        this.nodeLabelOffsetX = offsetX;
        fireChangeEvent();
    }

    /**
     * Returns the y-offset for the node labels.
     *
     * @return The y-offset for the node labels.
     */
    public double getNodeLabelOffsetY() {
        return nodeLabelOffsetY;
    }

    /**
     * Sets the y-offset for the node labels and sends a change notification
     * to all registered listeners.
     *
     * @param offsetY  the node label y-offset in Java2D units.
     */
    public void setNodeLabelOffsetY(double offsetY) {
        this.nodeLabelOffsetY = offsetY;
        fireChangeEvent();
    }

    /**
     * Returns the tool tip generator that creates the strings that are
     * displayed as tool tips for the flows displayed in the plot.
     *
     * @return The tool tip generator (possibly {@code null}).
     */
    public FlowLabelGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator and sends a change notification to all
     * registered listeners.  If the generator is set to {@code null}, no tool
     * tips will be displayed for the flows.
     *
     * @param generator  the new generator ({@code null} permitted).
     */
    public void setToolTipGenerator(FlowLabelGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Draws the flow plot within the specified area of the supplied graphics
     * target {@code g2}.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param area  the plot area ({@code null} not permitted).
     * @param anchor  the anchor point (ignored).
     * @param parentState  the parent state (ignored).
     * @param info  the plot rendering info.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        Args.nullNotPermitted(g2, "g2");
        Args.nullNotPermitted(area, "area");
        EntityCollection entities = null;
        if (info != null) {
            info.setPlotArea(area);
            entities = info.getOwner().getEntityCollection();
        }
        RectangleInsets insets = getInsets();
        insets.trim(area);
        if (info != null) {
            info.setDataArea(area);
        }
        // use default JFreeChart background handling
        drawBackground(g2, area);
        // we need to ensure there is space to show all the inflows and all
        // the outflows at each node group, so first we calculate the max
        // flow space required - for each node in the group, consider the
        // maximum of the inflow and the outflow
        double flow2d = Double.POSITIVE_INFINITY;
        double nodeMargin2d = this.nodeMargin * area.getHeight();
        int stageCount = this.dataset.getStageCount();
        for (int stage = 0; stage < this.dataset.getStageCount(); stage++) {
            List<K> sources = this.dataset.getSources(stage);
            int nodeCount = sources.size();
            double flowTotal = 0.0;
            for (K source : sources) {
                double inflow = FlowDatasetUtils.calculateInflow(this.dataset, source, stage);
                double outflow = FlowDatasetUtils.calculateOutflow(this.dataset, source, stage);
                flowTotal = flowTotal + Math.max(inflow, outflow);
            }
            if (flowTotal > 0.0) {
                double availableH = area.getHeight() - (nodeCount - 1) * nodeMargin2d;
                flow2d = Math.min(availableH / flowTotal, flow2d);
            }
            if (stage == this.dataset.getStageCount() - 1) {
                // check inflows to the final destination nodes...
                List<K> destinations = this.dataset.getDestinations(stage);
                int destinationCount = destinations.size();
                flowTotal = 0.0;
                for (K destination : destinations) {
                    double inflow = FlowDatasetUtils.calculateInflow(this.dataset, destination, stage + 1);
                    flowTotal = flowTotal + inflow;
                }
                if (flowTotal > 0.0) {
                    double availableH = area.getHeight() - (destinationCount - 1) * nodeMargin2d;
                    flow2d = Math.min(availableH / flowTotal, flow2d);
                }
            }
        }
        double stageWidth = (area.getWidth() - ((stageCount + 1) * this.nodeWidth)) / stageCount;
        double flowOffset = area.getWidth() * this.flowMargin;
        Map<NodeKey<K>, Rectangle2D> nodeRects = new HashMap<>();
        boolean hasNodeSelections = FlowDatasetUtils.hasNodeSelections(this.dataset);
        boolean hasFlowSelections = FlowDatasetUtils.hasFlowSelections(this.dataset);
        // iterate over all the stages, we can render the source node rects and
        // the flows ... we should add the destination node rects last, then
        // in a final pass add the labels
        for (int stage = 0; stage < this.dataset.getStageCount(); stage++) {
            double stageLeft = area.getX() + (stage + 1) * this.nodeWidth + (stage * stageWidth);
            double stageRight = stageLeft + stageWidth;
            // calculate the source node and flow rectangles
            Map<FlowKey<K>, Rectangle2D> sourceFlowRects = new HashMap<>();
            double nodeY = area.getY();
            for (K source : this.dataset.getSources(stage)) {
                double inflow = FlowDatasetUtils.calculateInflow(dataset, source, stage);
                double outflow = FlowDatasetUtils.calculateOutflow(dataset, source, stage);
                double nodeHeight = (Math.max(inflow, outflow) * flow2d);
                Rectangle2D nodeRect = new Rectangle2D.Double(stageLeft - nodeWidth, nodeY, nodeWidth, nodeHeight);
                if (entities != null) {
                    entities.add(new NodeEntity(new NodeKey<>(stage, source), nodeRect, source.toString()));
                }
                nodeRects.put(new NodeKey<>(stage, source), nodeRect);
                double y = nodeY;
                for (K destination : this.dataset.getDestinations(stage)) {
                    Number flow = this.dataset.getFlow(stage, source, destination);
                    if (flow != null) {
                        double height = flow.doubleValue() * flow2d;
                        Rectangle2D rect = new Rectangle2D.Double(stageLeft - nodeWidth, y, nodeWidth, height);
                        sourceFlowRects.put(new FlowKey<>(stage, source, destination), rect);
                        y = y + height;
                    }
                }
                nodeY = nodeY + nodeHeight + nodeMargin2d;
            }
            // calculate the destination rectangles
            Map<FlowKey<K>, Rectangle2D> destFlowRects = new HashMap<>();
            nodeY = area.getY();
            for (K destination : this.dataset.getDestinations(stage)) {
                double inflow = FlowDatasetUtils.calculateInflow(dataset, destination, stage + 1);
                double outflow = FlowDatasetUtils.calculateOutflow(dataset, destination, stage + 1);
                double nodeHeight = Math.max(inflow, outflow) * flow2d;
                nodeRects.put(new NodeKey<>(stage + 1, destination), new Rectangle2D.Double(stageRight, nodeY, nodeWidth, nodeHeight));
                double y = nodeY;
                for (K source : this.dataset.getSources(stage)) {
                    Number flow = this.dataset.getFlow(stage, source, destination);
                    if (flow != null) {
                        double height = flow.doubleValue() * flow2d;
                        Rectangle2D rect = new Rectangle2D.Double(stageRight, y, nodeWidth, height);
                        y = y + height;
                        destFlowRects.put(new FlowKey<>(stage, source, destination), rect);
                    }
                }
                nodeY = nodeY + nodeHeight + nodeMargin2d;
            }
            for (K source : this.dataset.getSources(stage)) {
                NodeKey<K> nodeKey = new NodeKey<>(stage, source);
                Rectangle2D nodeRect = nodeRects.get(nodeKey);
                Color ncol = lookupNodeColor(nodeKey);
                if (hasNodeSelections) {
                    if (!Boolean.TRUE.equals(dataset.getNodeProperty(nodeKey, NodeKey.SELECTED_PROPERTY_KEY))) {
                        int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                        ncol = new Color(g, g, g, ncol.getAlpha());
                    }
                }
                g2.setPaint(ncol);
                g2.fill(nodeRect);
                for (K destination : this.dataset.getDestinations(stage)) {
                    FlowKey<K> flowKey = new FlowKey<>(stage, source, destination);
                    Rectangle2D sourceRect = sourceFlowRects.get(flowKey);
                    if (sourceRect == null) {
                        continue;
                    }
                    Rectangle2D destRect = destFlowRects.get(flowKey);
                    Path2D connect = new Path2D.Double();
                    connect.moveTo(sourceRect.getMaxX() + flowOffset, sourceRect.getMinY());
                    connect.curveTo(stageLeft + stageWidth / 2.0, sourceRect.getMinY(), stageLeft + stageWidth / 2.0, destRect.getMinY(), destRect.getX() - flowOffset, destRect.getMinY());
                    connect.lineTo(destRect.getX() - flowOffset, destRect.getMaxY());
                    connect.curveTo(stageLeft + stageWidth / 2.0, destRect.getMaxY(), stageLeft + stageWidth / 2.0, sourceRect.getMaxY(), sourceRect.getMaxX() + flowOffset, sourceRect.getMaxY());
                    connect.closePath();
                    Color nc = lookupNodeColor(nodeKey);
                    if (hasFlowSelections) {
                        if (!Boolean.TRUE.equals(dataset.getFlowProperty(flowKey, FlowKey.SELECTED_PROPERTY_KEY))) {
                            int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                            nc = new Color(g, g, g, ncol.getAlpha());
                        }
                    }
                    GradientPaint gp = new GradientPaint((float) sourceRect.getMaxX(), 0, nc, (float) destRect.getMinX(), 0, new Color(nc.getRed(), nc.getGreen(), nc.getBlue(), 128));
                    Composite saved = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                    g2.setPaint(gp);
                    g2.fill(connect);
                    if (entities != null) {
                        String toolTip = null;
                        if (this.toolTipGenerator != null) {
                            toolTip = this.toolTipGenerator.generateLabel(this.dataset, flowKey);
                        }
                        entities.add(new FlowEntity<>(flowKey, connect, toolTip, ""));
                    }
                    g2.setComposite(saved);
                }
            }
        }
        // now draw the destination nodes
        int lastStage = this.dataset.getStageCount() - 1;
        for (K destination : this.dataset.getDestinations(lastStage)) {
            NodeKey<K> nodeKey = new NodeKey<>(lastStage + 1, destination);
            Rectangle2D nodeRect = nodeRects.get(nodeKey);
            if (nodeRect != null) {
                Color ncol = lookupNodeColor(nodeKey);
                if (hasNodeSelections) {
                    if (!Boolean.TRUE.equals(dataset.getNodeProperty(nodeKey, NodeKey.SELECTED_PROPERTY_KEY))) {
                        int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                        ncol = new Color(g, g, g, ncol.getAlpha());
                    }
                }
                g2.setPaint(ncol);
                g2.fill(nodeRect);
                if (entities != null) {
                    entities.add(new NodeEntity(new NodeKey<>(lastStage + 1, destination), nodeRect, destination.toString()));
                }
            }
        }
        // now draw all the labels over top of everything else
        g2.setFont(this.defaultNodeLabelFont);
        g2.setPaint(this.defaultNodeLabelPaint);
        for (NodeKey<K> key : nodeRects.keySet()) {
            Rectangle2D r = nodeRects.get(key);
            if (key.getStage() < this.dataset.getStageCount()) {
                TextUtils.drawAlignedString(key.getNode().toString(), g2, (float) (r.getMaxX() + flowOffset + this.nodeLabelOffsetX), (float) labelY(r), TextAnchor.CENTER_LEFT);
            } else {
                TextUtils.drawAlignedString(key.getNode().toString(), g2, (float) (r.getX() - flowOffset - this.nodeLabelOffsetX), (float) labelY(r), TextAnchor.CENTER_RIGHT);
            }
        }
    }

    /**
     * Performs a lookup on the color for the specified node.
     *
     * @param nodeKey  the node key ({@code null} not permitted).
     *
     * @return The node color.
     */
    protected Color lookupNodeColor(NodeKey<K> nodeKey) {
        Color result = this.nodeColorMap.get(nodeKey);
        if (result == null) {
            // if the color swatch is non-empty, we use it to autopopulate
            // the node colors...
            if (!this.nodeColorSwatch.isEmpty()) {
                // look through previous stages to see if this source key is already seen
                for (int s = 0; s < nodeKey.getStage(); s++) {
                    for (K key : dataset.getSources(s)) {
                        if (nodeKey.getNode().equals(key)) {
                            Color color = this.nodeColorMap.get(new NodeKey<>(s, key));
                            setNodeFillColor(nodeKey, color);
                            return color;
                        }
                    }
                }
                result = this.nodeColorSwatch.get(Math.min(this.nodeColorSwatchPointer, this.nodeColorSwatch.size() - 1));
                this.nodeColorSwatchPointer++;
                if (this.nodeColorSwatchPointer > this.nodeColorSwatch.size() - 1) {
                    this.nodeColorSwatchPointer = 0;
                }
                setNodeFillColor(nodeKey, result);
                return result;
            } else {
                result = this.defaultNodeColor;
            }
        }
        return result;
    }

    /**
     * Computes the y-coordinate for a node label taking into account the
     * current alignment settings.
     *
     * @param r  the node rectangle.
     *
     * @return The y-coordinate for the label.
     */
    private double labelY(Rectangle2D r) {
        if (this.nodeLabelAlignment == VerticalAlignment.TOP) {
            return r.getY() + this.nodeLabelOffsetY;
        } else if (this.nodeLabelAlignment == VerticalAlignment.BOTTOM) {
            return r.getMaxY() - this.nodeLabelOffsetY;
        } else {
            return r.getCenterY();
        }
    }

    /**
     * Tests this plot for equality with an arbitrary object.  Note that, for
     * the purposes of this equality test, the dataset is ignored.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FlowPlot)) {
            return false;
        }
        FlowPlot<K> that = (FlowPlot<K>) obj;
        if (!this.defaultNodeColor.equals(that.defaultNodeColor)) {
            return false;
        }
        if (!this.nodeColorMap.equals(that.nodeColorMap)) {
            return false;
        }
        if (!this.nodeColorSwatch.equals(that.nodeColorSwatch)) {
            return false;
        }
        if (!this.defaultNodeLabelFont.equals(that.defaultNodeLabelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultNodeLabelPaint, that.defaultNodeLabelPaint)) {
            return false;
        }
        if (this.flowMargin != that.flowMargin) {
            return false;
        }
        if (this.nodeMargin != that.nodeMargin) {
            return false;
        }
        if (this.nodeWidth != that.nodeWidth) {
            return false;
        }
        if (this.nodeLabelOffsetX != that.nodeLabelOffsetX) {
            return false;
        }
        if (this.nodeLabelOffsetY != that.nodeLabelOffsetY) {
            return false;
        }
        if (this.nodeLabelAlignment != that.nodeLabelAlignment) {
            return false;
        }
        if (!Objects.equals(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hashcode for this instance.
     *
     * @return A hashcode.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeWidth));
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeMargin));
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.flowMargin));
        hash = 83 * hash + Objects.hashCode(this.nodeColorMap);
        hash = 83 * hash + Objects.hashCode(this.nodeColorSwatch);
        hash = 83 * hash + Objects.hashCode(this.defaultNodeColor);
        hash = 83 * hash + Objects.hashCode(this.defaultNodeLabelFont);
        hash = 83 * hash + Objects.hashCode(this.defaultNodeLabelPaint);
        hash = 83 * hash + Objects.hashCode(this.nodeLabelAlignment);
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeLabelOffsetX));
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeLabelOffsetY));
        hash = 83 * hash + Objects.hashCode(this.toolTipGenerator);
        return hash;
    }

    /**
     * Returns an independent copy of this {@code FlowPlot} instance (note,
     * however, that the dataset is NOT cloned).
     *
     * @return A close of this instance.
     *
     * @throws CloneNotSupportedException if there is a problem cloning
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        FlowPlot<K> clone = (FlowPlot<K>) super.clone();
        clone.nodeColorMap = new HashMap<>(this.nodeColorMap);
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
 * XYLineAndShapeRenderer.java
 * ---------------------------
 * (C) Copyright 2004-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A renderer that connects data points with lines and/or draws shapes at each
 * data point.  This renderer is designed for use with the {@link XYPlot}
 * class.  The example shown here is generated by
 * the {@code XYLineAndShapeRendererDemo2.java} program included in the
 * JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/XYLineAndShapeRendererSample.png"
 * alt="XYLineAndShapeRendererSample.png">
 */
public class XYLineAndShapeRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -7435246895986425885L;

    /**
     * A table of flags that control (per series) whether lines are
     * visible.
     */
    private Map<Integer, Boolean> seriesLinesVisibleMap;

    /**
     * The default value returned by the getLinesVisible() method.
     */
    private boolean defaultLinesVisible;

    /**
     * The shape that is used to represent a line in the legend.
     */
    private transient Shape legendLine;

    /**
     * A table of flags that control (per series) whether shapes are
     * visible.
     */
    private Map<Integer, Boolean> seriesShapesVisibleMap;

    /**
     * The default value returned by the getShapeVisible() method.
     */
    private boolean defaultShapesVisible;

    /**
     * A table of flags that control (per series) whether shapes are
     * filled.
     */
    private Map<Integer, Boolean> seriesShapesFilledMap;

    /**
     * The default value returned by the getShapeFilled() method.
     */
    private boolean defaultShapesFilled;

    /**
     * A flag that controls whether outlines are drawn for shapes.
     */
    private boolean drawOutlines;

    /**
     * A flag that controls whether the fill paint is used for filling
     * shapes.
     */
    private boolean useFillPaint;

    /**
     * A flag that controls whether the outline paint is used for drawing shape
     * outlines.
     */
    private boolean useOutlinePaint;

    /**
     * A flag that controls whether each series is drawn as a single
     * path.
     */
    private boolean drawSeriesLineAsPath;

    /**
     * Creates a new renderer with both lines and shapes visible.
     */
    public XYLineAndShapeRenderer() {
        this(true, true);
    }

    /**
     * Creates a new renderer.
     *
     * @param lines  lines visible?
     * @param shapes  shapes visible?
     */
    public XYLineAndShapeRenderer(boolean lines, boolean shapes) {
        this.seriesLinesVisibleMap = new HashMap<>();
        this.defaultLinesVisible = lines;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.seriesShapesVisibleMap = new HashMap<>();
        this.defaultShapesVisible = shapes;
        // use item paint for fills by default
        this.useFillPaint = false;
        this.seriesShapesFilledMap = new HashMap<>();
        this.defaultShapesFilled = true;
        this.drawOutlines = true;
        // use item paint for outlines by
        this.useOutlinePaint = false;
        // default, not outline paint
        this.drawSeriesLineAsPath = false;
    }

    /**
     * Returns a flag that controls whether each series is drawn as a
     * single path.  The default value is {@code false}.
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
     * single path and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDrawSeriesLineAsPath()
     */
    public void setDrawSeriesLineAsPath(boolean flag) {
        if (this.drawSeriesLineAsPath != flag) {
            this.drawSeriesLineAsPath = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the number of passes through the data that the renderer requires
     * in order to draw the chart.  Most charts will require a single pass, but
     * some require two passes.
     *
     * @return The pass count.
     */
    @Override
    public int getPassCount() {
        return 2;
    }

    // LINES VISIBLE
    /**
     * Returns the flag used to control whether the shape for an item is
     * visible.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = getSeriesLinesVisible(series);
        if (flag != null) {
            return flag;
        }
        return this.defaultLinesVisible;
    }

    /**
     * Returns the flag used to control whether the lines for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesLinesVisible(int, Boolean)
     */
    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisibleMap.get(series);
    }

    /**
     * Sets the 'lines visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag ({@code null} permitted).
     *
     * @see #getSeriesLinesVisible(int)
     */
    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisibleMap.put(series, flag);
        fireChangeEvent();
    }

    /**
     * Sets the 'lines visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     *
     * @see #getSeriesLinesVisible(int)
     */
    public void setSeriesLinesVisible(int series, boolean visible) {
        setSeriesLinesVisible(series, Boolean.valueOf(visible));
    }

    /**
     * Returns the default 'lines visible' attribute.
     *
     * @return The default flag.
     *
     * @see #setDefaultLinesVisible(boolean)
     */
    public boolean getDefaultLinesVisible() {
        return this.defaultLinesVisible;
    }

    /**
     * Sets the default 'lines visible' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDefaultLinesVisible()
     */
    public void setDefaultLinesVisible(boolean flag) {
        this.defaultLinesVisible = flag;
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

    // SHAPES VISIBLE
    /**
     * Returns the flag used to control whether the shape for an item is
     * visible.
     * <p>
     * The default implementation passes control to the
     * {@code getSeriesShapesVisible()} method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = getSeriesShapesVisible(series);
        if (flag != null) {
            return flag;
        }
        return this.defaultShapesVisible;
    }

    /**
     * Returns the flag used to control whether the shapes for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesShapesVisible(int, Boolean)
     */
    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisibleMap.get(series);
    }

    /**
     * Sets the 'shapes visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     *
     * @see #getSeriesShapesVisible(int)
     */
    public void setSeriesShapesVisible(int series, boolean visible) {
        setSeriesShapesVisible(series, Boolean.valueOf(visible));
    }

    /**
     * Sets the 'shapes visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag.
     *
     * @see #getSeriesShapesVisible(int)
     */
    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisibleMap.put(series, flag);
        fireChangeEvent();
    }

    /**
     * Returns the default 'shape visible' attribute.
     *
     * @return The default flag.
     *
     * @see #setDefaultShapesVisible(boolean)
     */
    public boolean getDefaultShapesVisible() {
        return this.defaultShapesVisible;
    }

    /**
     * Sets the default 'shapes visible' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDefaultShapesVisible()
     */
    public void setDefaultShapesVisible(boolean flag) {
        this.defaultShapesVisible = flag;
        fireChangeEvent();
    }

    // SHAPES FILLED
    /**
     * Returns the flag used to control whether the shape for an item
     * is filled.
     * <p>
     * The default implementation passes control to the
     * {@code getSeriesShapesFilled} method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        Boolean flag = getSeriesShapesFilled(series);
        if (flag != null) {
            return flag;
        }
        return this.defaultShapesFilled;
    }

    /**
     * Returns the flag used to control whether the shapes for a series
     * are filled.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesShapesFilled(int, Boolean)
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
    public void setSeriesShapesFilled(int series, boolean flag) {
        setSeriesShapesFilled(series, Boolean.valueOf(flag));
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
     * Returns the default 'shape filled' attribute.
     *
     * @return The default flag.
     *
     * @see #setDefaultShapesFilled(boolean)
     */
    public boolean getDefaultShapesFilled() {
        return this.defaultShapesFilled;
    }

    /**
     * Sets the default 'shapes filled' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDefaultShapesFilled()
     */
    public void setDefaultShapesFilled(boolean flag) {
        this.defaultShapesFilled = flag;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if outlines should be drawn for shapes, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setDrawOutlines(boolean)
     */
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    /**
     * Sets the flag that controls whether outlines are drawn for
     * shapes, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * <P>
     * In some cases, shapes look better if they do NOT have an outline, but
     * this flag allows you to set your own preference.
     *
     * @param flag  the flag.
     *
     * @see #getDrawOutlines()
     */
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the renderer should use the fill paint
     * setting to fill shapes, and {@code false} if it should just
     * use the regular paint.
     * <p>
     * Refer to {@code XYLineAndShapeRendererDemo2.java} to see the
     * effect of this flag.
     *
     * @return A boolean.
     *
     * @see #setUseFillPaint(boolean)
     * @see #getUseOutlinePaint()
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
     * Returns {@code true} if the renderer should use the outline paint
     * setting to draw shape outlines, and {@code false} if it should just
     * use the regular paint.
     *
     * @return A boolean.
     *
     * @see #setUseOutlinePaint(boolean)
     * @see #getUseFillPaint()
     */
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    /**
     * Sets the flag that controls whether the outline paint is used to draw
     * shape outlines, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * <p>
     * Refer to {@code XYLineAndShapeRendererDemo2.java} to see the
     * effect of this flag.
     *
     * @param flag  the flag.
     *
     * @see #getUseOutlinePaint()
     */
    public void setUseOutlinePaint(boolean flag) {
        this.useOutlinePaint = flag;
        fireChangeEvent();
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
            this.seriesPath = new GeneralPath();
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
         * This method is called by the {@link XYPlot} at the start of each
         * series pass.  We reset the state for the current series.
         *
         * @param dataset  the dataset.
         * @param series  the series index.
         * @param firstItem  the first item index for this pass.
         * @param lastItem  the last item index for this pass.
         * @param pass  the current pass index.
         * @param passCount  the number of passes.
         */
        @Override
        public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
            this.seriesPath.reset();
            this.lastPointGood = false;
            super.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
        }
    }

    /**
     * Initialises the renderer.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to
     * maintain.  The renderer can do nothing if it chooses.
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
        return new State(info);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
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
        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }
        // first pass draws the background (lines, for instance)
        if (isLinePass(pass)) {
            if (getItemLineVisible(series, item)) {
                if (this.drawSeriesLineAsPath) {
                    drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                } else {
                    drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                }
            }
        } else // second pass adds shapes where the items are ..
        if (isItemPass(pass)) {
            // setup for collecting optional entity info...
            EntityCollection entities = null;
            if (info != null && info.getOwner() != null) {
                entities = info.getOwner().getEntityCollection();
            }
            drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
        }
    }

    /**
     * Returns {@code true} if the specified pass is the one for drawing
     * lines.
     *
     * @param pass  the pass.
     *
     * @return A boolean.
     */
    protected boolean isLinePass(int pass) {
        return pass == 0;
    }

    /**
     * Returns {@code true} if the specified pass is the one for drawing
     * items.
     *
     * @param pass  the pass.
     *
     * @return A boolean.
     */
    protected boolean isItemPass(int pass) {
        return pass == 1;
    }

    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     */
    protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        if (item == 0) {
            return;
        }
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        double x0 = dataset.getXValue(series, item - 1);
        double y0 = dataset.getYValue(series, item - 1);
        if (Double.isNaN(y0) || Double.isNaN(x0)) {
            return;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        // only draw if we have good values
        if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        boolean visible;
        if (orientation == PlotOrientation.HORIZONTAL) {
            state.workingLine.setLine(transY0, transX0, transY1, transX1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            state.workingLine.setLine(transX0, transY0, transX1, transY1);
        }
        visible = LineUtils.clipLine(state.workingLine, dataArea);
        if (visible) {
            drawFirstPassShape(g2, pass, series, item, state.workingLine);
        }
    }

    /**
     * Draws the first pass shape.
     *
     * @param g2  the graphics device.
     * @param pass  the pass.
     * @param series  the series index.
     * @param item  the item index.
     * @param shape  the shape.
     */
    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        g2.setPaint(getItemPaint(series, item));
        g2.draw(shape);
    }

    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items. Instead of drawing separate lines,
     * a {@code GeneralPath} is constructed and drawn at the end of
     * the series painting.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataArea  the area within which the data is being drawn.
     */
    protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        State s = (State) state;
        // update path to reflect latest point
        if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
            float x = (float) transX1;
            float y = (float) transY1;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                x = (float) transY1;
                y = (float) transX1;
            }
            if (s.isLastPointGood()) {
                s.seriesPath.lineTo(x, y);
            } else {
                s.seriesPath.moveTo(x, y);
            }
            s.setLastPointGood(true);
        } else {
            s.setLastPointGood(false);
        }
        // if this is the last item, draw the path ...
        if (item == s.getLastItemIndex()) {
            // draw path
            drawFirstPassShape(g2, pass, series, item, s.seriesPath);
        }
    }

    /**
     * Draws the item shapes and adds chart entities (second pass). This method
     * draws the shapes which mark the item positions. If {@code entities}
     * is not {@code null} it will be populated with entity information
     * for points that fall within the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param dataArea  the area within which the data is being drawn.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  the crosshair state.
     * @param entities the entity collection.
     */
    protected void drawSecondaryPass(Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, CrosshairState crosshairState, EntityCollection entities) {
        Shape entityArea = null;
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (getItemShapeVisible(series, item)) {
            Shape shape = getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transY1, transX1);
            } else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transX1, transY1);
            }
            entityArea = shape;
            if (shape.intersects(dataArea)) {
                if (getItemShapeFilled(series, item)) {
                    if (this.useFillPaint) {
                        g2.setPaint(getItemFillPaint(series, item));
                    } else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (getUseOutlinePaint()) {
                        g2.setPaint(getItemOutlinePaint(series, item));
                    } else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.setStroke(getItemOutlineStroke(series, item));
                    g2.draw(shape);
                }
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
        // add an entity for the item, but only if it falls within the data
        // area...
        if (entities != null && ShapeUtils.isPointInRect(dataArea, xx, yy)) {
            addEntity(entities, entityArea, dataset, series, item, xx, yy);
        }
    }

    /**
     * Returns a legend item for the specified series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series (possibly {@code null}).
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        if (!getItemVisible(series, 0)) {
            return null;
        }
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
        boolean shapeIsVisible = getItemShapeVisible(series, 0);
        Shape shape = lookupLegendShape(series);
        boolean shapeIsFilled = getItemShapeFilled(series, 0);
        Paint fillPaint = (this.useFillPaint ? lookupSeriesFillPaint(series) : lookupSeriesPaint(series));
        boolean shapeOutlineVisible = this.drawOutlines;
        Paint outlinePaint = (this.useOutlinePaint ? lookupSeriesOutlinePaint(series) : lookupSeriesPaint(series));
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        boolean lineVisible = getItemLineVisible(series, 0);
        Stroke lineStroke = lookupSeriesStroke(series);
        Paint linePaint = lookupSeriesPaint(series);
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, shapeIsVisible, shape, shapeIsFilled, fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke, lineVisible, this.legendLine, lineStroke, linePaint);
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        return result;
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the clone cannot be created.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XYLineAndShapeRenderer clone = (XYLineAndShapeRenderer) super.clone();
        clone.seriesLinesVisibleMap = new HashMap<>(this.seriesLinesVisibleMap);
        clone.legendLine = CloneUtils.clone(this.legendLine);
        clone.seriesShapesVisibleMap = new HashMap<>(this.seriesShapesVisibleMap);
        clone.seriesShapesFilledMap = new HashMap<>(this.seriesShapesFilledMap);
        return clone;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
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
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYLineAndShapeRenderer that = (XYLineAndShapeRenderer) obj;
        if (!Objects.equals(this.seriesLinesVisibleMap, that.seriesLinesVisibleMap)) {
            return false;
        }
        if (this.defaultLinesVisible != that.defaultLinesVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (!Objects.equals(this.seriesShapesVisibleMap, that.seriesShapesVisibleMap)) {
            return false;
        }
        if (this.defaultShapesVisible != that.defaultShapesVisible) {
            return false;
        }
        if (!Objects.equals(this.seriesShapesFilledMap, that.seriesShapesFilledMap)) {
            return false;
        }
        if (this.defaultShapesFilled != that.defaultShapesFilled) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (this.drawSeriesLineAsPath != that.drawSeriesLineAsPath) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + seriesLinesVisibleMap.hashCode();
        result = 31 * result + (defaultLinesVisible ? 1 : 0);
        result = 31 * result + seriesShapesVisibleMap.hashCode();
        result = 31 * result + (defaultShapesVisible ? 1 : 0);
        result = 31 * result + seriesShapesFilledMap.hashCode();
        result = 31 * result + (defaultShapesFilled ? 1 : 0);
        result = 31 * result + (drawOutlines ? 1 : 0);
        result = 31 * result + (useFillPaint ? 1 : 0);
        result = 31 * result + (useOutlinePaint ? 1 : 0);
        result = 31 * result + (drawSeriesLineAsPath ? 1 : 0);
        return result;
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
 * --------------------------
 * DefaultTableXYDataset.java
 * --------------------------
 * (C) Copyright 2003-2021, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   Jody Brownell;
 *                   David Gilbert;
 *                   Andreas Schroeder;
 * 
 */
/**
 * An {@link XYDataset} where every series shares the same x-values (required
 * for generating stacked area charts).
 *
 * @param <S> The type for the series keys.
 */
public class DefaultTableXYDataset<S extends Comparable<S>> extends AbstractIntervalXYDataset<S> implements TableXYDataset<S>, IntervalXYDataset<S>, DomainInfo, PublicCloneable {

    /**
     * Storage for the data - this list will contain zero, one or many
     * XYSeries objects.
     */
    private List<XYSeries<S>> data = null;

    /**
     * Storage for the x values.
     */
    private HashSet xPoints = null;

    /**
     * A flag that controls whether events are propogated.
     */
    private boolean propagateEvents = true;

    /**
     * A flag that controls auto pruning.
     */
    private boolean autoPrune = false;

    /**
     * The delegate used to control the interval width.
     */
    private IntervalXYDelegate intervalDelegate;

    /**
     * Creates a new empty dataset.
     */
    public DefaultTableXYDataset() {
        this(false);
    }

    /**
     * Creates a new empty dataset.
     *
     * @param autoPrune  a flag that controls whether x-values are
     *                   removed whenever the corresponding y-values are all
     *                   {@code null}.
     */
    public DefaultTableXYDataset(boolean autoPrune) {
        this.autoPrune = autoPrune;
        this.data = new ArrayList<>();
        this.xPoints = new HashSet();
        this.intervalDelegate = new IntervalXYDelegate(this, false);
        addChangeListener(this.intervalDelegate);
    }

    /**
     * Returns the flag that controls whether x-values are removed from
     * the dataset when the corresponding y-values are all {@code null}.
     *
     * @return A boolean.
     */
    public boolean isAutoPrune() {
        return this.autoPrune;
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.  The series should be configured to NOT
     * allow duplicate x-values.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void addSeries(XYSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        if (series.getAllowDuplicateXValues()) {
            throw new IllegalArgumentException("Cannot accept XYSeries that allow duplicate values. " + "Use XYSeries(seriesName, <sort>, false) constructor.");
        }
        updateXPoints(series);
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Adds any unique x-values from 'series' to the dataset, and also adds any
     * x-values that are in the dataset but not in 'series' to the series.
     *
     * @param series  the series ({@code null} not permitted).
     */
    private void updateXPoints(XYSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        HashSet seriesXPoints = new HashSet();
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int itemNo = 0; itemNo < series.getItemCount(); itemNo++) {
            Number xValue = series.getX(itemNo);
            seriesXPoints.add(xValue);
            if (!this.xPoints.contains(xValue)) {
                this.xPoints.add(xValue);
                int seriesCount = this.data.size();
                for (int seriesNo = 0; seriesNo < seriesCount; seriesNo++) {
                    XYSeries<S> dataSeries = this.data.get(seriesNo);
                    if (!dataSeries.equals(series)) {
                        dataSeries.add(xValue, null);
                    }
                }
            }
        }
        for (Object point : this.xPoints) {
            Number xPoint = (Number) point;
            if (!seriesXPoints.contains(xPoint)) {
                series.add(xPoint, null);
            }
        }
        this.propagateEvents = savedState;
    }

    /**
     * Updates the x-values for all the series in the dataset.
     */
    public void updateXPoints() {
        this.propagateEvents = false;
        for (int s = 0; s < this.data.size(); s++) {
            updateXPoints(this.data.get(s));
        }
        if (this.autoPrune) {
            prune();
        }
        this.propagateEvents = true;
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
     * Returns the number of x values in the dataset.
     *
     * @return The number of x values in the dataset.
     */
    @Override
    public int getItemCount() {
        if (this.xPoints == null) {
            return 0;
        } else {
            return this.xPoints.size();
        }
    }

    /**
     * Returns a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The series (never {@code null}).
     */
    public XYSeries<S> getSeries(int series) {
        Args.requireInRange(series, "series", 0, this.data.size() - 1);
        return this.data.get(series);
    }

    /**
     * Returns the key for a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The key for a series.
     */
    @Override
    public S getSeriesKey(int series) {
        // check arguments...delegated
        return getSeries(series).getKey();
    }

    /**
     * Returns the number of items in the specified series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The number of items in the specified series.
     */
    @Override
    public int getItemCount(int series) {
        // check arguments...delegated
        return getSeries(series).getItemCount();
    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The x-value for the specified series and item.
     */
    @Override
    public Number getX(int series, int item) {
        XYSeries<S> s = this.data.get(series);
        return s.getX(item);
    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The starting X value.
     */
    @Override
    public Number getStartX(int series, int item) {
        return this.intervalDelegate.getStartX(series, item);
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The ending X value.
     */
    @Override
    public Number getEndX(int series, int item) {
        return this.intervalDelegate.getEndX(series, item);
    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param index  the index of the item of interest (zero-based).
     *
     * @return The y-value for the specified series and item (possibly
     *         {@code null}).
     */
    @Override
    public Number getY(int series, int index) {
        XYSeries<S> s = this.data.get(series);
        return s.getY(index);
    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The starting Y value.
     */
    @Override
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The ending Y value.
     */
    @Override
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Removes all the series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     */
    public void removeAllSeries() {
        // Unregister the collection as a change listener to each series in
        // the collection.
        for (XYSeries<S> series : this.data) {
            series.removeChangeListener(this);
        }
        // Remove all the series from the collection and notify listeners.
        this.data.clear();
        this.xPoints.clear();
        fireDatasetChanged();
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void removeSeries(XYSeries<S> series) {
        Args.nullNotPermitted(series, "series");
        if (this.data.contains(series)) {
            series.removeChangeListener(this);
            this.data.remove(series);
            if (this.data.isEmpty()) {
                this.xPoints.clear();
            }
            fireDatasetChanged();
        }
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series (zero based index).
     */
    public void removeSeries(int series) {
        Args.requireInRange(series, "series", 0, this.data.size() - 1);
        // fetch the series, remove the change listener, then remove the series.
        XYSeries<S> s = this.data.get(series);
        s.removeChangeListener(this);
        this.data.remove(series);
        if (this.data.isEmpty()) {
            this.xPoints.clear();
        } else if (this.autoPrune) {
            prune();
        }
        fireDatasetChanged();
    }

    /**
     * Removes the items from all series for a given x value.
     *
     * @param x  the x-value.
     */
    public void removeAllValuesForX(Number x) {
        Args.nullNotPermitted(x, "x");
        boolean savedState = this.propagateEvents;
        this.propagateEvents = false;
        for (int s = 0; s < this.data.size(); s++) {
            XYSeries<S> series = this.data.get(s);
            series.remove(x);
        }
        this.propagateEvents = savedState;
        this.xPoints.remove(x);
        fireDatasetChanged();
    }

    /**
     * Returns {@code true} if all the y-values for the specified x-value
     * are {@code null} and {@code false} otherwise.
     *
     * @param x  the x-value.
     *
     * @return A boolean.
     */
    protected boolean canPrune(Number x) {
        for (int s = 0; s < this.data.size(); s++) {
            XYSeries<S> series = this.data.get(s);
            if (series.getY(series.indexOf(x)) != null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Removes all x-values for which all the y-values are {@code null}.
     */
    public void prune() {
        HashSet hs = (HashSet) this.xPoints.clone();
        for (Object h : hs) {
            Number x = (Number) h;
            if (canPrune(x)) {
                removeAllValuesForX(x);
            }
        }
    }

    /**
     * This method receives notification when a series belonging to the dataset
     * changes.  It responds by updating the x-points for the entire dataset
     * and sending a {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param event  information about the change.
     */
    @Override
    public void seriesChanged(SeriesChangeEvent event) {
        if (this.propagateEvents) {
            updateXPoints();
            fireDatasetChanged();
        }
    }

    /**
     * Tests this collection for equality with an arbitrary object.
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
        if (!(obj instanceof DefaultTableXYDataset)) {
            return false;
        }
        DefaultTableXYDataset that = (DefaultTableXYDataset) obj;
        if (this.autoPrune != that.autoPrune) {
            return false;
        }
        if (this.propagateEvents != that.propagateEvents) {
            return false;
        }
        if (!this.intervalDelegate.equals(that.intervalDelegate)) {
            return false;
        }
        if (!Objects.equals(this.data, that.data)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result;
        result = (this.data != null ? this.data.hashCode() : 0);
        result = 29 * result + (this.xPoints != null ? this.xPoints.hashCode() : 0);
        result = 29 * result + (this.propagateEvents ? 1 : 0);
        result = 29 * result + (this.autoPrune ? 1 : 0);
        return result;
    }

    /**
     * Returns an independent copy of this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is some reason that cloning
     *     cannot be performed.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultTableXYDataset clone = (DefaultTableXYDataset) super.clone();
        int seriesCount = this.data.size();
        clone.data = new ArrayList<>(seriesCount);
        for (XYSeries<S> series : this.data) {
            clone.data.add(CloneUtils.clone(series));
        }
        clone.intervalDelegate = new IntervalXYDelegate(clone);
        // need to configure the intervalDelegate to match the original
        clone.intervalDelegate.setFixedIntervalWidth(getIntervalWidth());
        clone.intervalDelegate.setAutoWidth(isAutoWidth());
        clone.intervalDelegate.setIntervalPositionFactor(getIntervalPositionFactor());
        clone.updateXPoints();
        return clone;
    }

    /**
     * Returns the minimum x-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether the
     *                         x-interval is taken into account.
     *
     * @return The minimum value.
     */
    @Override
    public double getDomainLowerBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainLowerBound(includeInterval);
    }

    /**
     * Returns the maximum x-value in the dataset.
     *
     * @param includeInterval  a flag that determines whether the
     *                         x-interval is taken into account.
     *
     * @return The maximum value.
     */
    @Override
    public double getDomainUpperBound(boolean includeInterval) {
        return this.intervalDelegate.getDomainUpperBound(includeInterval);
    }

    /**
     * Returns the range of the values in this dataset's domain.
     *
     * @param includeInterval  a flag that determines whether the
     *                         x-interval is taken into account.
     *
     * @return The range.
     */
    @Override
    public Range getDomainBounds(boolean includeInterval) {
        if (includeInterval) {
            return this.intervalDelegate.getDomainBounds(includeInterval);
        } else {
            return DatasetUtils.iterateDomainBounds(this, includeInterval);
        }
    }

    /**
     * Returns the interval position factor.
     *
     * @return The interval position factor.
     */
    public double getIntervalPositionFactor() {
        return this.intervalDelegate.getIntervalPositionFactor();
    }

    /**
     * Sets the interval position factor. Must be between 0.0 and 1.0 inclusive.
     * If the factor is 0.5, the gap is in the middle of the x values. If it
     * is lesser than 0.5, the gap is farther to the left and if greater than
     * 0.5 it gets farther to the right.
     *
     * @param d the new interval position factor.
     */
    public void setIntervalPositionFactor(double d) {
        this.intervalDelegate.setIntervalPositionFactor(d);
        fireDatasetChanged();
    }

    /**
     * returns the full interval width.
     *
     * @return The interval width to use.
     */
    public double getIntervalWidth() {
        return this.intervalDelegate.getIntervalWidth();
    }

    /**
     * Sets the interval width to a fixed value, and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param d  the new interval width (must be &gt; 0).
     */
    public void setIntervalWidth(double d) {
        this.intervalDelegate.setFixedIntervalWidth(d);
        fireDatasetChanged();
    }

    /**
     * Returns whether the interval width is automatically calculated or not.
     *
     * @return A flag that determines whether the interval width is
     *         automatically calculated.
     */
    public boolean isAutoWidth() {
        return this.intervalDelegate.isAutoWidth();
    }

    /**
     * Sets the flag that indicates whether the interval width is automatically
     * calculated or not.
     *
     * @param b  a boolean.
     */
    public void setAutoWidth(boolean b) {
        this.intervalDelegate.setAutoWidth(b);
        fireDatasetChanged();
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
 * PeriodAxis.java
 * ---------------
 * (C) Copyright 2004-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * An axis that displays a date scale based on a
 * {@link org.jfree.data.time.RegularTimePeriod}.  This axis works when
 * displayed across the bottom or top of a plot, but is broken for display at
 * the left or right of charts.
 */
public class PeriodAxis extends ValueAxis implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 8353295532075872069L;

    /**
     * The first time period in the overall range.
     */
    private RegularTimePeriod first;

    /**
     * The last time period in the overall range.
     */
    private RegularTimePeriod last;

    /**
     * The time zone used to convert 'first' and 'last' to absolute
     * milliseconds.
     */
    private TimeZone timeZone;

    /**
     * The locale (never {@code null}).
     */
    private Locale locale;

    /**
     * A calendar used for date manipulations in the current time zone and
     * locale.
     */
    private Calendar calendar;

    /**
     * The {@link RegularTimePeriod} subclass used to automatically determine
     * the axis range.
     */
    private Class autoRangeTimePeriodClass;

    /**
     * Indicates the {@link RegularTimePeriod} subclass that is used to
     * determine the spacing of the major tick marks.
     */
    private Class majorTickTimePeriodClass;

    /**
     * A flag that indicates whether tick marks are visible for the
     * axis.
     */
    private boolean minorTickMarksVisible;

    /**
     * Indicates the {@link RegularTimePeriod} subclass that is used to
     * determine the spacing of the minor tick marks.
     */
    private Class minorTickTimePeriodClass;

    /**
     * The length of the tick mark inside the data area (zero permitted).
     */
    private float minorTickMarkInsideLength = 0.0f;

    /**
     * The length of the tick mark outside the data area (zero permitted).
     */
    private float minorTickMarkOutsideLength = 2.0f;

    /**
     * The stroke used to draw tick marks.
     */
    private transient Stroke minorTickMarkStroke = new BasicStroke(0.5f);

    /**
     * The paint used to draw tick marks.
     */
    private transient Paint minorTickMarkPaint = Color.BLACK;

    /**
     * Info for each labeling band.
     */
    private PeriodAxisLabelInfo[] labelInfo;

    /**
     * Creates a new axis.
     *
     * @param label  the axis label.
     */
    public PeriodAxis(String label) {
        this(label, new Day(), new Day());
    }

    /**
     * Creates a new axis.
     *
     * @param label  the axis label ({@code null} permitted).
     * @param first  the first time period in the axis range
     *               ({@code null} not permitted).
     * @param last  the last time period in the axis range
     *              ({@code null} not permitted).
     */
    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last) {
        this(label, first, last, TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Creates a new axis.
     *
     * @param label  the axis label ({@code null} permitted).
     * @param first  the first time period in the axis range
     *               ({@code null} not permitted).
     * @param last  the last time period in the axis range
     *              ({@code null} not permitted).
     * @param timeZone  the time zone ({@code null} not permitted).
     * @param locale  the locale ({@code null} not permitted).
     */
    public PeriodAxis(String label, RegularTimePeriod first, RegularTimePeriod last, TimeZone timeZone, Locale locale) {
        super(label, null);
        Args.nullNotPermitted(timeZone, "timeZone");
        Args.nullNotPermitted(locale, "locale");
        this.first = first;
        this.last = last;
        this.timeZone = timeZone;
        this.locale = locale;
        this.calendar = Calendar.getInstance(timeZone, locale);
        this.first.peg(this.calendar);
        this.last.peg(this.calendar);
        this.autoRangeTimePeriodClass = first.getClass();
        this.majorTickTimePeriodClass = first.getClass();
        this.minorTickMarksVisible = false;
        this.minorTickTimePeriodClass = RegularTimePeriod.downsize(this.majorTickTimePeriodClass);
        setAutoRange(true);
        this.labelInfo = new PeriodAxisLabelInfo[2];
        SimpleDateFormat df0 = new SimpleDateFormat("MMM", locale);
        df0.setTimeZone(timeZone);
        this.labelInfo[0] = new PeriodAxisLabelInfo(Month.class, df0);
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy", locale);
        df1.setTimeZone(timeZone);
        this.labelInfo[1] = new PeriodAxisLabelInfo(Year.class, df1);
    }

    /**
     * Returns the first time period in the axis range.
     *
     * @return The first time period (never {@code null}).
     */
    public RegularTimePeriod getFirst() {
        return this.first;
    }

    /**
     * Sets the first time period in the axis range and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param first  the time period ({@code null} not permitted).
     */
    public void setFirst(RegularTimePeriod first) {
        Args.nullNotPermitted(first, "first");
        this.first = first;
        this.first.peg(this.calendar);
        fireChangeEvent();
    }

    /**
     * Returns the last time period in the axis range.
     *
     * @return The last time period (never {@code null}).
     */
    public RegularTimePeriod getLast() {
        return this.last;
    }

    /**
     * Sets the last time period in the axis range and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param last  the time period ({@code null} not permitted).
     */
    public void setLast(RegularTimePeriod last) {
        Args.nullNotPermitted(last, "last");
        this.last = last;
        this.last.peg(this.calendar);
        fireChangeEvent();
    }

    /**
     * Returns the time zone used to convert the periods defining the axis
     * range into absolute milliseconds.
     *
     * @return The time zone (never {@code null}).
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Sets the time zone that is used to convert the time periods into
     * absolute milliseconds.
     *
     * @param zone  the time zone ({@code null} not permitted).
     */
    public void setTimeZone(TimeZone zone) {
        Args.nullNotPermitted(zone, "zone");
        this.timeZone = zone;
        this.calendar = Calendar.getInstance(zone, this.locale);
        this.first.peg(this.calendar);
        this.last.peg(this.calendar);
        fireChangeEvent();
    }

    /**
     * Returns the locale for this axis.
     *
     * @return The locale (never ({@code null}).
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Returns the class used to create the first and last time periods for
     * the axis range when the auto-range flag is set to {@code true}.
     *
     * @return The class (never {@code null}).
     */
    public Class getAutoRangeTimePeriodClass() {
        return this.autoRangeTimePeriodClass;
    }

    /**
     * Sets the class used to create the first and last time periods for the
     * axis range when the auto-range flag is set to {@code true} and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param c  the class ({@code null} not permitted).
     */
    public void setAutoRangeTimePeriodClass(Class c) {
        Args.nullNotPermitted(c, "c");
        this.autoRangeTimePeriodClass = c;
        fireChangeEvent();
    }

    /**
     * Returns the class that controls the spacing of the major tick marks.
     *
     * @return The class (never {@code null}).
     */
    public Class getMajorTickTimePeriodClass() {
        return this.majorTickTimePeriodClass;
    }

    /**
     * Sets the class that controls the spacing of the major tick marks, and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param c  the class (a subclass of {@link RegularTimePeriod} is
     *           expected).
     */
    public void setMajorTickTimePeriodClass(Class c) {
        Args.nullNotPermitted(c, "c");
        this.majorTickTimePeriodClass = c;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether minor tick marks
     * are displayed for the axis.
     *
     * @return A boolean.
     */
    @Override
    public boolean isMinorTickMarksVisible() {
        return this.minorTickMarksVisible;
    }

    /**
     * Sets the flag that controls whether minor tick marks
     * are displayed for the axis, and sends a {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param visible  the flag.
     */
    @Override
    public void setMinorTickMarksVisible(boolean visible) {
        this.minorTickMarksVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the class that controls the spacing of the minor tick marks.
     *
     * @return The class (never {@code null}).
     */
    public Class getMinorTickTimePeriodClass() {
        return this.minorTickTimePeriodClass;
    }

    /**
     * Sets the class that controls the spacing of the minor tick marks, and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param c  the class (a subclass of {@link RegularTimePeriod} is
     *           expected).
     */
    public void setMinorTickTimePeriodClass(Class c) {
        Args.nullNotPermitted(c, "c");
        this.minorTickTimePeriodClass = c;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to display minor tick marks, if they are
     * visible.
     *
     * @return A stroke (never {@code null}).
     */
    public Stroke getMinorTickMarkStroke() {
        return this.minorTickMarkStroke;
    }

    /**
     * Sets the stroke used to display minor tick marks, if they are
     * visible, and sends a {@link AxisChangeEvent} to all registered
     * listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     */
    public void setMinorTickMarkStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.minorTickMarkStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to display minor tick marks, if they are
     * visible.
     *
     * @return A paint (never {@code null}).
     */
    public Paint getMinorTickMarkPaint() {
        return this.minorTickMarkPaint;
    }

    /**
     * Sets the paint used to display minor tick marks, if they are
     * visible, and sends a {@link AxisChangeEvent} to all registered
     * listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setMinorTickMarkPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.minorTickMarkPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the inside length for the minor tick marks.
     *
     * @return The length.
     */
    @Override
    public float getMinorTickMarkInsideLength() {
        return this.minorTickMarkInsideLength;
    }

    /**
     * Sets the inside length of the minor tick marks and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param length  the length.
     */
    @Override
    public void setMinorTickMarkInsideLength(float length) {
        this.minorTickMarkInsideLength = length;
        fireChangeEvent();
    }

    /**
     * Returns the outside length for the minor tick marks.
     *
     * @return The length.
     */
    @Override
    public float getMinorTickMarkOutsideLength() {
        return this.minorTickMarkOutsideLength;
    }

    /**
     * Sets the outside length of the minor tick marks and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param length  the length.
     */
    @Override
    public void setMinorTickMarkOutsideLength(float length) {
        this.minorTickMarkOutsideLength = length;
        fireChangeEvent();
    }

    /**
     * Returns an array of label info records.
     *
     * @return An array.
     */
    public PeriodAxisLabelInfo[] getLabelInfo() {
        return this.labelInfo;
    }

    /**
     * Sets the array of label info records and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param info  the info.
     */
    public void setLabelInfo(PeriodAxisLabelInfo[] info) {
        this.labelInfo = info;
        fireChangeEvent();
    }

    /**
     * Sets the range for the axis, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect,
     * the auto-range flag is set to {@code false} (optional).
     *
     * @param range  the range ({@code null} not permitted).
     * @param turnOffAutoRange  a flag that controls whether the auto
     *                          range is turned off.
     * @param notify  a flag that controls whether listeners are
     *                notified.
     */
    @Override
    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        long upper = Math.round(range.getUpperBound());
        long lower = Math.round(range.getLowerBound());
        this.first = createInstance(this.autoRangeTimePeriodClass, new Date(lower), this.timeZone, this.locale);
        this.last = createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone, this.locale);
        super.setRange(new Range(this.first.getFirstMillisecond(), this.last.getLastMillisecond() + 1.0), turnOffAutoRange, notify);
    }

    /**
     * Configures the axis to work with the current plot.  Override this method
     * to perform any special processing (such as auto-rescaling).
     */
    @Override
    public void configure() {
        if (this.isAutoRange()) {
            autoAdjustRange();
        }
    }

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
    @Override
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        // create a new space object if one wasn't supplied...
        if (space == null) {
            space = new AxisSpace();
        }
        // if the axis is not visible, no additional space is required...
        if (!isVisible()) {
            return space;
        }
        // if the axis has a fixed dimension, return it...
        double dimension = getFixedDimension();
        if (dimension > 0.0) {
            space.ensureAtLeast(dimension, edge);
        }
        // get the axis label size and update the space object...
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight, labelWidth;
        double tickLabelBandsDimension = 0.0;
        for (PeriodAxisLabelInfo info : this.labelInfo) {
            FontMetrics fm = g2.getFontMetrics(info.getLabelFont());
            tickLabelBandsDimension += info.getPadding().extendHeight(fm.getHeight());
        }
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelBandsDimension, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelBandsDimension, edge);
        }
        // add space for the outer tick labels, if any...
        double tickMarkSpace = 0.0;
        if (isTickMarksVisible()) {
            tickMarkSpace = getTickMarkOutsideLength();
        }
        if (this.minorTickMarksVisible) {
            tickMarkSpace = Math.max(tickMarkSpace, this.minorTickMarkOutsideLength);
        }
        space.add(tickMarkSpace, edge);
        return space;
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
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        // if the axis is not visible, don't draw it... bug#198
        if (!isVisible()) {
            AxisState state = new AxisState(cursor);
            // even though the axis is not visible, we need to refresh ticks in
            // case the grid is being drawn...
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        AxisState axisState = new AxisState(cursor);
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        if (isTickMarksVisible()) {
            drawTickMarks(g2, axisState, dataArea, edge);
        }
        if (isTickLabelsVisible()) {
            for (int band = 0; band < this.labelInfo.length; band++) {
                axisState = drawTickLabels(band, g2, axisState, dataArea, edge);
            }
        }
        if (getAttributedLabel() != null) {
            axisState = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, axisState);
        } else {
            axisState = drawLabel(getLabel(), g2, plotArea, dataArea, edge, axisState);
        }
        return axisState;
    }

    /**
     * Draws the tick marks for the axis.
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the data area.
     * @param edge  the edge.
     */
    protected void drawTickMarks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            drawTickMarksHorizontal(g2, state, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            drawTickMarksVertical(g2, state, dataArea, edge);
        }
    }

    /**
     * Draws the major and minor tick marks for an axis that lies at the top or
     * bottom of the plot.
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the data area.
     * @param edge  the edge.
     */
    protected void drawTickMarksHorizontal(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List ticks = new ArrayList();
        double x0;
        double y0 = state.getCursor();
        double insideLength = getTickMarkInsideLength();
        double outsideLength = getTickMarkOutsideLength();
        RegularTimePeriod t = createInstance(this.majorTickTimePeriodClass, this.first.getStart(), getTimeZone(), this.locale);
        long t0 = t.getFirstMillisecond();
        Line2D inside = null;
        Line2D outside = null;
        long firstOnAxis = getFirst().getFirstMillisecond();
        long lastOnAxis = getLast().getLastMillisecond() + 1;
        while (t0 <= lastOnAxis) {
            ticks.add(new NumberTick((double) t0, "", TextAnchor.CENTER, TextAnchor.CENTER, 0.0));
            x0 = valueToJava2D(t0, dataArea, edge);
            if (edge == RectangleEdge.TOP) {
                inside = new Line2D.Double(x0, y0, x0, y0 + insideLength);
                outside = new Line2D.Double(x0, y0, x0, y0 - outsideLength);
            } else if (edge == RectangleEdge.BOTTOM) {
                inside = new Line2D.Double(x0, y0, x0, y0 - insideLength);
                outside = new Line2D.Double(x0, y0, x0, y0 + outsideLength);
            }
            if (t0 >= firstOnAxis) {
                g2.setPaint(getTickMarkPaint());
                g2.setStroke(getTickMarkStroke());
                g2.draw(inside);
                g2.draw(outside);
            }
            // draw minor tick marks
            if (this.minorTickMarksVisible) {
                RegularTimePeriod tminor = createInstance(this.minorTickTimePeriodClass, new Date(t0), getTimeZone(), this.locale);
                long tt0 = tminor.getFirstMillisecond();
                while (tt0 < t.getLastMillisecond() && tt0 < lastOnAxis) {
                    double xx0 = valueToJava2D(tt0, dataArea, edge);
                    if (edge == RectangleEdge.TOP) {
                        inside = new Line2D.Double(xx0, y0, xx0, y0 + this.minorTickMarkInsideLength);
                        outside = new Line2D.Double(xx0, y0, xx0, y0 - this.minorTickMarkOutsideLength);
                    } else if (edge == RectangleEdge.BOTTOM) {
                        inside = new Line2D.Double(xx0, y0, xx0, y0 - this.minorTickMarkInsideLength);
                        outside = new Line2D.Double(xx0, y0, xx0, y0 + this.minorTickMarkOutsideLength);
                    }
                    if (tt0 >= firstOnAxis) {
                        g2.setPaint(this.minorTickMarkPaint);
                        g2.setStroke(this.minorTickMarkStroke);
                        g2.draw(inside);
                        g2.draw(outside);
                    }
                    tminor = tminor.next();
                    tminor.peg(this.calendar);
                    tt0 = tminor.getFirstMillisecond();
                }
            }
            t = t.next();
            t.peg(this.calendar);
            t0 = t.getFirstMillisecond();
        }
        if (edge == RectangleEdge.TOP) {
            state.cursorUp(Math.max(outsideLength, this.minorTickMarkOutsideLength));
        } else if (edge == RectangleEdge.BOTTOM) {
            state.cursorDown(Math.max(outsideLength, this.minorTickMarkOutsideLength));
        }
        state.setTicks(ticks);
    }

    /**
     * Draws the tick marks for a vertical axis.
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the data area.
     * @param edge  the edge.
     */
    protected void drawTickMarksVertical(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        // FIXME:  implement this...
    }

    /**
     * Draws the tick labels for one "band" of time periods.
     *
     * @param band  the band index (zero-based).
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the data area.
     * @param edge  the edge where the axis is located.
     *
     * @return The updated axis state.
     */
    protected AxisState drawTickLabels(int band, Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        // work out the initial gap
        double delta1 = 0.0;
        FontMetrics fm = g2.getFontMetrics(this.labelInfo[band].getLabelFont());
        if (edge == RectangleEdge.BOTTOM) {
            delta1 = this.labelInfo[band].getPadding().calculateTopOutset(fm.getHeight());
        } else if (edge == RectangleEdge.TOP) {
            delta1 = this.labelInfo[band].getPadding().calculateBottomOutset(fm.getHeight());
        }
        state.moveCursor(delta1, edge);
        long axisMin = this.first.getFirstMillisecond();
        long axisMax = this.last.getLastMillisecond();
        g2.setFont(this.labelInfo[band].getLabelFont());
        g2.setPaint(this.labelInfo[band].getLabelPaint());
        // work out the number of periods to skip for labelling
        RegularTimePeriod p1 = this.labelInfo[band].createInstance(new Date(axisMin), this.timeZone, this.locale);
        RegularTimePeriod p2 = this.labelInfo[band].createInstance(new Date(axisMax), this.timeZone, this.locale);
        DateFormat df = this.labelInfo[band].getDateFormat();
        df.setTimeZone(this.timeZone);
        String label1 = df.format(new Date(p1.getMiddleMillisecond()));
        String label2 = df.format(new Date(p2.getMiddleMillisecond()));
        Rectangle2D b1 = TextUtils.getTextBounds(label1, g2, g2.getFontMetrics());
        Rectangle2D b2 = TextUtils.getTextBounds(label2, g2, g2.getFontMetrics());
        double w = Math.max(b1.getWidth(), b2.getWidth());
        long ww = Math.round(java2DToValue(dataArea.getX() + w + 5.0, dataArea, edge));
        if (isInverted()) {
            ww = axisMax - ww;
        } else {
            ww = ww - axisMin;
        }
        long length = p1.getLastMillisecond() - p1.getFirstMillisecond();
        int periods = (int) (ww / length) + 1;
        RegularTimePeriod p = this.labelInfo[band].createInstance(new Date(axisMin), this.timeZone, this.locale);
        Rectangle2D b = null;
        long lastXX = 0L;
        float y = (float) (state.getCursor());
        TextAnchor anchor = TextAnchor.TOP_CENTER;
        float yDelta = (float) b1.getHeight();
        if (edge == RectangleEdge.TOP) {
            anchor = TextAnchor.BOTTOM_CENTER;
            yDelta = -yDelta;
        }
        while (p.getFirstMillisecond() <= axisMax) {
            float x = (float) valueToJava2D(p.getMiddleMillisecond(), dataArea, edge);
            String label = df.format(new Date(p.getMiddleMillisecond()));
            long first = p.getFirstMillisecond();
            long last = p.getLastMillisecond();
            if (last > axisMax) {
                // this is the last period, but it is only partially visible
                // so check that the label will fit before displaying it...
                Rectangle2D bb = TextUtils.getTextBounds(label, g2, g2.getFontMetrics());
                if ((x + bb.getWidth() / 2) > dataArea.getMaxX()) {
                    float xstart = (float) valueToJava2D(Math.max(first, axisMin), dataArea, edge);
                    if (bb.getWidth() < (dataArea.getMaxX() - xstart)) {
                        x = ((float) dataArea.getMaxX() + xstart) / 2.0f;
                    } else {
                        label = null;
                    }
                }
            }
            if (first < axisMin) {
                // this is the first period, but it is only partially visible
                // so check that the label will fit before displaying it...
                Rectangle2D bb = TextUtils.getTextBounds(label, g2, g2.getFontMetrics());
                if ((x - bb.getWidth() / 2) < dataArea.getX()) {
                    float xlast = (float) valueToJava2D(Math.min(last, axisMax), dataArea, edge);
                    if (bb.getWidth() < (xlast - dataArea.getX())) {
                        x = (xlast + (float) dataArea.getX()) / 2.0f;
                    } else {
                        label = null;
                    }
                }
            }
            if (label != null) {
                g2.setPaint(this.labelInfo[band].getLabelPaint());
                b = TextUtils.drawAlignedString(label, g2, x, y, anchor);
            }
            if (lastXX > 0L) {
                if (this.labelInfo[band].getDrawDividers()) {
                    long nextXX = p.getFirstMillisecond();
                    long mid = (lastXX + nextXX) / 2;
                    float mid2d = (float) valueToJava2D(mid, dataArea, edge);
                    g2.setStroke(this.labelInfo[band].getDividerStroke());
                    g2.setPaint(this.labelInfo[band].getDividerPaint());
                    g2.draw(new Line2D.Float(mid2d, y, mid2d, y + yDelta));
                }
            }
            lastXX = last;
            for (int i = 0; i < periods; i++) {
                p = p.next();
            }
            p.peg(this.calendar);
        }
        double used = 0.0;
        if (b != null) {
            used = b.getHeight();
            // work out the trailing gap
            if (edge == RectangleEdge.BOTTOM) {
                used += this.labelInfo[band].getPadding().calculateBottomOutset(fm.getHeight());
            } else if (edge == RectangleEdge.TOP) {
                used += this.labelInfo[band].getPadding().calculateTopOutset(fm.getHeight());
            }
        }
        state.moveCursor(used, edge);
        return state;
    }

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
    @Override
    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        return Collections.EMPTY_LIST;
    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the area.
     *
     * @param value  the data value.
     * @param area  the area for plotting the data.
     * @param edge  the edge along which the axis lies.
     *
     * @return The Java2D coordinate.
     */
    @Override
    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        double result = Double.NaN;
        double axisMin = this.first.getFirstMillisecond();
        double axisMax = this.last.getLastMillisecond();
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                result = maxX + ((value - axisMin) / (axisMax - axisMin)) * (minX - maxX);
            } else {
                result = minX + ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
            }
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            if (isInverted()) {
                result = minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            } else {
                result = maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            }
        }
        return result;
    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the area in which the data is plotted.
     * @param edge  the edge along which the axis lies.
     *
     * @return The data value.
     */
    @Override
    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        double result;
        double min = 0.0;
        double max = 0.0;
        double axisMin = this.first.getFirstMillisecond();
        double axisMax = this.last.getLastMillisecond();
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        if (isInverted()) {
            result = axisMax - ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        } else {
            result = axisMin + ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        }
        return result;
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    @Override
    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot == null) {
            // no plot, no data
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;
            Range r = vap.getDataRange(this);
            if (r == null) {
                r = getDefaultAutoRange();
            }
            long upper = Math.round(r.getUpperBound());
            long lower = Math.round(r.getLowerBound());
            this.first = createInstance(this.autoRangeTimePeriodClass, new Date(lower), this.timeZone, this.locale);
            this.last = createInstance(this.autoRangeTimePeriodClass, new Date(upper), this.timeZone, this.locale);
            setRange(r, false, false);
        }
    }

    /**
     * Tests the axis for equality with an arbitrary object.
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
        if (!(obj instanceof PeriodAxis)) {
            return false;
        }
        PeriodAxis that = (PeriodAxis) obj;
        if (!this.first.equals(that.first)) {
            return false;
        }
        if (!this.last.equals(that.last)) {
            return false;
        }
        if (!this.timeZone.equals(that.timeZone)) {
            return false;
        }
        if (!this.locale.equals(that.locale)) {
            return false;
        }
        if (!this.autoRangeTimePeriodClass.equals(that.autoRangeTimePeriodClass)) {
            return false;
        }
        if (!(isMinorTickMarksVisible() == that.isMinorTickMarksVisible())) {
            return false;
        }
        if (!this.majorTickTimePeriodClass.equals(that.majorTickTimePeriodClass)) {
            return false;
        }
        if (!this.minorTickTimePeriodClass.equals(that.minorTickTimePeriodClass)) {
            return false;
        }
        if (!this.minorTickMarkPaint.equals(that.minorTickMarkPaint)) {
            return false;
        }
        if (!this.minorTickMarkStroke.equals(that.minorTickMarkStroke)) {
            return false;
        }
        if (!Arrays.equals(this.labelInfo, that.labelInfo)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hash code for this object.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns a clone of the axis.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  this class is cloneable, but
     *         subclasses may not be.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PeriodAxis clone = (PeriodAxis) super.clone();
        clone.timeZone = (TimeZone) this.timeZone.clone();
        clone.labelInfo = (PeriodAxisLabelInfo[]) this.labelInfo.clone();
        return clone;
    }

    /**
     * A utility method used to create a particular subclass of the
     * {@link RegularTimePeriod} class that includes the specified millisecond,
     * assuming the specified time zone.
     *
     * @param periodClass  the class.
     * @param millisecond  the time.
     * @param zone  the time zone.
     * @param locale  the locale.
     *
     * @return The time period.
     */
    private RegularTimePeriod createInstance(Class periodClass, Date millisecond, TimeZone zone, Locale locale) {
        RegularTimePeriod result = null;
        try {
            Constructor c = periodClass.getDeclaredConstructor(new Class[] { Date.class, TimeZone.class, Locale.class });
            result = (RegularTimePeriod) c.newInstance(new Object[] { millisecond, zone, locale });
        } catch (Exception e) {
            try {
                Constructor c = periodClass.getDeclaredConstructor(new Class[] { Date.class });
                result = (RegularTimePeriod) c.newInstance(new Object[] { millisecond });
            } catch (Exception e2) {
                // do nothing
            }
        }
        return result;
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
        SerialUtils.writeStroke(this.minorTickMarkStroke, stream);
        SerialUtils.writePaint(this.minorTickMarkPaint, stream);
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
        this.minorTickMarkStroke = SerialUtils.readStroke(stream);
        this.minorTickMarkPaint = SerialUtils.readPaint(stream);
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
 * --------------------
 * XYBoxAnnotation.java
 * --------------------
 * (C) Copyright 2005-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Peter Kolb (see patch 2809117);
 *
 */
/**
 * A box annotation that can be placed on an {@link XYPlot}.  The
 * box coordinates are specified in data space.
 */
public class XYBoxAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 6764703772526757457L;

    /**
     * The lower x-coordinate.
     */
    private double x0;

    /**
     * The lower y-coordinate.
     */
    private double y0;

    /**
     * The upper x-coordinate.
     */
    private double x1;

    /**
     * The upper y-coordinate.
     */
    private double y1;

    /**
     * The stroke used to draw the box outline.
     */
    private transient Stroke stroke;

    /**
     * The paint used to draw the box outline.
     */
    private transient Paint outlinePaint;

    /**
     * The paint used to fill the box.
     */
    private transient Paint fillPaint;

    /**
     * Creates a new annotation (where, by default, the box is drawn
     * with a black outline).
     *
     * @param x0  the lower x-coordinate of the box (in data space).
     * @param y0  the lower y-coordinate of the box (in data space).
     * @param x1  the upper x-coordinate of the box (in data space).
     * @param y1  the upper y-coordinate of the box (in data space).
     */
    public XYBoxAnnotation(double x0, double y0, double x1, double y1) {
        this(x0, y0, x1, y1, new BasicStroke(1.0f), Color.BLACK);
    }

    /**
     * Creates a new annotation where the box is drawn as an outline using
     * the specified {@code stroke} and {@code outlinePaint}.
     *
     * @param x0  the lower x-coordinate of the box (in data space).
     * @param y0  the lower y-coordinate of the box (in data space).
     * @param x1  the upper x-coordinate of the box (in data space).
     * @param y1  the upper y-coordinate of the box (in data space).
     * @param stroke  the shape stroke ({@code null} permitted).
     * @param outlinePaint  the shape color ({@code null} permitted).
     */
    public XYBoxAnnotation(double x0, double y0, double x1, double y1, Stroke stroke, Paint outlinePaint) {
        this(x0, y0, x1, y1, stroke, outlinePaint, null);
    }

    /**
     * Creates a new annotation.
     *
     * @param x0  the lower x-coordinate of the box (in data space, must be finite).
     * @param y0  the lower y-coordinate of the box (in data space, must be finite).
     * @param x1  the upper x-coordinate of the box (in data space, must be finite).
     * @param y1  the upper y-coordinate of the box (in data space, must be finite).
     * @param stroke  the shape stroke ({@code null} permitted).
     * @param outlinePaint  the shape color ({@code null} permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   permitted).
     */
    public XYBoxAnnotation(double x0, double y0, double x1, double y1, Stroke stroke, Paint outlinePaint, Paint fillPaint) {
        super();
        Args.requireFinite(x0, "x0");
        Args.requireFinite(y0, "y0");
        Args.requireFinite(x1, "x1");
        Args.requireFinite(y1, "y1");
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.fillPaint = fillPaint;
    }

    /**
     * Returns the x-coordinate for the bottom left corner of the box (set in the
     * constructor).
     *
     * @return The x-coordinate for the bottom left corner of the box.
     */
    public double getX0() {
        return x0;
    }

    /**
     * Returns the y-coordinate for the bottom left corner of the box (set in the
     * constructor).
     *
     * @return The y-coordinate for the bottom left corner of the box.
     */
    public double getY0() {
        return y0;
    }

    /**
     * Returns the x-coordinate for the top right corner of the box (set in the
     * constructor).
     *
     * @return The x-coordinate for the top right corner of the box.
     */
    public double getX1() {
        return x1;
    }

    /**
     * Returns the y-coordinate for the top right corner of the box (set in the
     * constructor).
     *
     * @return The y-coordinate for the top right corner of the box.
     */
    public double getY1() {
        return y1;
    }

    /**
     * Returns the stroke used for the box outline.
     *
     * @return The stroke (possibly {@code null}).
     */
    public Stroke getStroke() {
        return stroke;
    }

    /**
     * Returns the paint used for the box outline.
     *
     * @return The paint (possibly {@code null}).
     */
    public Paint getOutlinePaint() {
        return outlinePaint;
    }

    /**
     * Returns the paint used for the box fill.
     *
     * @return The paint (possibly {@code null}).
     */
    public Paint getFillPaint() {
        return fillPaint;
    }

    /**
     * Draws the annotation.  This method is usually called by the
     * {@link XYPlot} class, you shouldn't need to call it directly.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param rendererIndex  the renderer index.
     * @param info  the plot rendering info.
     */
    @Override
    public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
        RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
        double transX0 = domainAxis.valueToJava2D(this.x0, dataArea, domainEdge);
        double transY0 = rangeAxis.valueToJava2D(this.y0, dataArea, rangeEdge);
        double transX1 = domainAxis.valueToJava2D(this.x1, dataArea, domainEdge);
        double transY1 = rangeAxis.valueToJava2D(this.y1, dataArea, rangeEdge);
        Rectangle2D box = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            box = new Rectangle2D.Double(transY0, transX1, transY1 - transY0, transX0 - transX1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            box = new Rectangle2D.Double(transX0, transY1, transX1 - transX0, transY0 - transY1);
        }
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(box);
        }
        if (this.stroke != null && this.outlinePaint != null) {
            g2.setPaint(this.outlinePaint);
            g2.setStroke(this.stroke);
            g2.draw(box);
        }
        addEntity(info, box, rendererIndex, getToolTipText(), getURL());
    }

    /**
     * Tests this annotation for equality with an arbitrary object.
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
        // now try to reject equality
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof XYBoxAnnotation)) {
            return false;
        }
        XYBoxAnnotation that = (XYBoxAnnotation) obj;
        if (!(this.x0 == that.x0)) {
            return false;
        }
        if (!(this.y0 == that.y0)) {
            return false;
        }
        if (!(this.x1 == that.x1)) {
            return false;
        }
        if (!(this.y1 == that.y1)) {
            return false;
        }
        if (!Objects.equals(this.stroke, that.stroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(this.x0);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.x1);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y0);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y1);
        result = 29 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Returns a clone.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException not thrown by this class, but may be
     *                                    by subclasses.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream ({@code null} not permitted).
     *
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeStroke(this.stroke, stream);
        SerialUtils.writePaint(this.outlinePaint, stream);
        SerialUtils.writePaint(this.fillPaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream ({@code null} not permitted).
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.stroke = SerialUtils.readStroke(stream);
        this.outlinePaint = SerialUtils.readPaint(stream);
        this.fillPaint = SerialUtils.readPaint(stream);
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
 * ---------------------
 * PaintScaleLegend.java
 * ---------------------
 * (C) Copyright 2007-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Peter Kolb - see patch 2686872;
 *
 */
/**
 * A legend that shows a range of values and their associated colors, driven
 * by an underlying {@link PaintScale} implementation.
 */
public class PaintScaleLegend extends Title implements AxisChangeListener, PublicCloneable {

    /**
     * For serialization.
     */
    static final long serialVersionUID = -1365146490993227503L;

    /**
     * The paint scale (never {@code null}).
     */
    private PaintScale scale;

    /**
     * The value axis (never {@code null}).
     */
    private ValueAxis axis;

    /**
     * The axis location (handles both orientations, never
     * {@code null}).
     */
    private AxisLocation axisLocation;

    /**
     * The offset between the axis and the paint strip (in Java2D units).
     */
    private double axisOffset;

    /**
     * The thickness of the paint strip (in Java2D units).
     */
    private double stripWidth;

    /**
     * A flag that controls whether an outline is drawn around the
     * paint strip.
     */
    private boolean stripOutlineVisible;

    /**
     * The paint used to draw an outline around the paint strip.
     */
    private transient Paint stripOutlinePaint;

    /**
     * The stroke used to draw an outline around the paint strip.
     */
    private transient Stroke stripOutlineStroke;

    /**
     * The background paint (never {@code null}).
     */
    private transient Paint backgroundPaint;

    /**
     * The number of subdivisions for the scale when rendering.
     */
    private int subdivisions;

    /**
     * Creates a new instance.
     *
     * @param scale  the scale ({@code null} not permitted).
     * @param axis  the axis ({@code null} not permitted).
     */
    public PaintScaleLegend(PaintScale scale, ValueAxis axis) {
        Args.nullNotPermitted(axis, "axis");
        this.scale = scale;
        this.axis = axis;
        this.axis.addChangeListener(this);
        this.axisLocation = AxisLocation.BOTTOM_OR_LEFT;
        this.axisOffset = 0.0;
        this.axis.setRange(scale.getLowerBound(), scale.getUpperBound());
        this.stripWidth = 15.0;
        this.stripOutlineVisible = true;
        this.stripOutlinePaint = Color.GRAY;
        this.stripOutlineStroke = new BasicStroke(0.5f);
        this.backgroundPaint = Color.WHITE;
        this.subdivisions = 100;
    }

    /**
     * Returns the scale used to convert values to colors.
     *
     * @return The scale (never {@code null}).
     *
     * @see #setScale(PaintScale)
     */
    public PaintScale getScale() {
        return this.scale;
    }

    /**
     * Sets the scale and sends a {@link TitleChangeEvent} to all registered
     * listeners.
     *
     * @param scale  the scale ({@code null} not permitted).
     *
     * @see #getScale()
     */
    public void setScale(PaintScale scale) {
        Args.nullNotPermitted(scale, "scale");
        this.scale = scale;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the axis for the paint scale.
     *
     * @return The axis (never {@code null}).
     *
     * @see #setAxis(ValueAxis)
     */
    public ValueAxis getAxis() {
        return this.axis;
    }

    /**
     * Sets the axis for the paint scale and sends a {@link TitleChangeEvent}
     * to all registered listeners.
     *
     * @param axis  the axis ({@code null} not permitted).
     *
     * @see #getAxis()
     */
    public void setAxis(ValueAxis axis) {
        Args.nullNotPermitted(axis, "axis");
        this.axis.removeChangeListener(this);
        this.axis = axis;
        this.axis.addChangeListener(this);
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the axis location.
     *
     * @return The axis location (never {@code null}).
     *
     * @see #setAxisLocation(AxisLocation)
     */
    public AxisLocation getAxisLocation() {
        return this.axisLocation;
    }

    /**
     * Sets the axis location and sends a {@link TitleChangeEvent} to all
     * registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getAxisLocation()
     */
    public void setAxisLocation(AxisLocation location) {
        Args.nullNotPermitted(location, "location");
        this.axisLocation = location;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the offset between the axis and the paint strip.
     *
     * @return The offset between the axis and the paint strip.
     *
     * @see #setAxisOffset(double)
     */
    public double getAxisOffset() {
        return this.axisOffset;
    }

    /**
     * Sets the offset between the axis and the paint strip and sends a
     * {@link TitleChangeEvent} to all registered listeners.
     *
     * @param offset  the offset.
     */
    public void setAxisOffset(double offset) {
        this.axisOffset = offset;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the width of the paint strip, in Java2D units.
     *
     * @return The width of the paint strip.
     *
     * @see #setStripWidth(double)
     */
    public double getStripWidth() {
        return this.stripWidth;
    }

    /**
     * Sets the width of the paint strip and sends a {@link TitleChangeEvent}
     * to all registered listeners.
     *
     * @param width  the width.
     *
     * @see #getStripWidth()
     */
    public void setStripWidth(double width) {
        this.stripWidth = width;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the flag that controls whether an outline is drawn
     * around the paint strip.
     *
     * @return A boolean.
     *
     * @see #setStripOutlineVisible(boolean)
     */
    public boolean isStripOutlineVisible() {
        return this.stripOutlineVisible;
    }

    /**
     * Sets the flag that controls whether an outline is drawn around
     * the paint strip, and sends a {@link TitleChangeEvent} to all registered
     * listeners.
     *
     * @param visible  the flag.
     *
     * @see #isStripOutlineVisible()
     */
    public void setStripOutlineVisible(boolean visible) {
        this.stripOutlineVisible = visible;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the paint used to draw the outline of the paint strip.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setStripOutlinePaint(Paint)
     */
    public Paint getStripOutlinePaint() {
        return this.stripOutlinePaint;
    }

    /**
     * Sets the paint used to draw the outline of the paint strip, and sends
     * a {@link TitleChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getStripOutlinePaint()
     */
    public void setStripOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.stripOutlinePaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the stroke used to draw the outline around the paint strip.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setStripOutlineStroke(Stroke)
     */
    public Stroke getStripOutlineStroke() {
        return this.stripOutlineStroke;
    }

    /**
     * Sets the stroke used to draw the outline around the paint strip and
     * sends a {@link TitleChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getStripOutlineStroke()
     */
    public void setStripOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.stripOutlineStroke = stroke;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the background paint.
     *
     * @return The background paint.
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background paint and sends a {@link TitleChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     */
    public void setBackgroundPaint(Paint paint) {
        this.backgroundPaint = paint;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Returns the number of subdivisions used to draw the scale.
     *
     * @return The subdivision count.
     */
    public int getSubdivisionCount() {
        return this.subdivisions;
    }

    /**
     * Sets the subdivision count and sends a {@link TitleChangeEvent} to
     * all registered listeners.
     *
     * @param count  the count.
     */
    public void setSubdivisionCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Requires 'count' > 0.");
        }
        this.subdivisions = count;
        notifyListeners(new TitleChangeEvent(this));
    }

    /**
     * Receives notification of an axis change event and responds by firing
     * a title change event.
     *
     * @param event  the event.
     */
    @Override
    public void axisChanged(AxisChangeEvent event) {
        if (this.axis == event.getAxis()) {
            notifyListeners(new TitleChangeEvent(this));
        }
    }

    /**
     * Arranges the contents of the block, within the given constraints, and
     * returns the block size.
     *
     * @param g2  the graphics device.
     * @param constraint  the constraint ({@code null} not permitted).
     *
     * @return The block size (in Java2D units, never {@code null}).
     */
    @Override
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        RectangleConstraint cc = toContentConstraint(constraint);
        LengthConstraintType w = cc.getWidthConstraintType();
        LengthConstraintType h = cc.getHeightConstraintType();
        Size2D contentSize = null;
        if (w == LengthConstraintType.NONE) {
            if (h == LengthConstraintType.NONE) {
                contentSize = new Size2D(getWidth(), getHeight());
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.RANGE) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                contentSize = arrangeRR(g2, cc.getWidthRange(), cc.getHeightRange());
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        } else if (w == LengthConstraintType.FIXED) {
            if (h == LengthConstraintType.NONE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.RANGE) {
                throw new RuntimeException("Not yet implemented.");
            } else if (h == LengthConstraintType.FIXED) {
                throw new RuntimeException("Not yet implemented.");
            }
        }
        // suppress compiler warning
        assert contentSize != null;
        return new Size2D(calculateTotalWidth(contentSize.getWidth()), calculateTotalHeight(contentSize.getHeight()));
    }

    /**
     * Returns the content size for the title.  This will reflect the fact that
     * a text title positioned on the left or right of a chart will be rotated
     * 90 degrees.
     *
     * @param g2  the graphics device.
     * @param widthRange  the width range.
     * @param heightRange  the height range.
     *
     * @return The content size.
     */
    protected Size2D arrangeRR(Graphics2D g2, Range widthRange, Range heightRange) {
        RectangleEdge position = getPosition();
        if (position == RectangleEdge.TOP || position == RectangleEdge.BOTTOM) {
            float maxWidth = (float) widthRange.getUpperBound();
            // determine the space required for the axis
            AxisSpace space = this.axis.reserveSpace(g2, null, new Rectangle2D.Double(0, 0, maxWidth, 100), RectangleEdge.BOTTOM, null);
            return new Size2D(maxWidth, this.stripWidth + this.axisOffset + space.getTop() + space.getBottom());
        } else if (position == RectangleEdge.LEFT || position == RectangleEdge.RIGHT) {
            float maxHeight = (float) heightRange.getUpperBound();
            AxisSpace space = this.axis.reserveSpace(g2, null, new Rectangle2D.Double(0, 0, 100, maxHeight), RectangleEdge.RIGHT, null);
            return new Size2D(this.stripWidth + this.axisOffset + space.getLeft() + space.getRight(), maxHeight);
        } else {
            throw new RuntimeException("Unrecognised position.");
        }
    }

    /**
     * Draws the legend within the specified area.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param area  the drawing area ({@code null} not permitted).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    /**
     * Draws the legend within the specified area.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param area  the drawing area ({@code null} not permitted).
     * @param params  drawing parameters (ignored here).
     *
     * @return {@code null}.
     */
    @Override
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        Rectangle2D target = (Rectangle2D) area.clone();
        target = trimMargin(target);
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(target);
        }
        getFrame().draw(g2, target);
        getFrame().getInsets().trim(target);
        target = trimPadding(target);
        double base = this.axis.getLowerBound();
        double increment = this.axis.getRange().getLength() / this.subdivisions;
        Rectangle2D r = new Rectangle2D.Double();
        if (RectangleEdge.isTopOrBottom(getPosition())) {
            RectangleEdge axisEdge = Plot.resolveRangeAxisLocation(this.axisLocation, PlotOrientation.HORIZONTAL);
            if (axisEdge == RectangleEdge.TOP) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.TOP);
                    double vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.TOP);
                    double ww = Math.abs(vv1 - vv0) + 1.0;
                    r.setRect(Math.min(vv0, vv1), target.getMaxY() - this.stripWidth, ww, this.stripWidth);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    g2.draw(new Rectangle2D.Double(target.getMinX(), target.getMaxY() - this.stripWidth, target.getWidth(), this.stripWidth));
                }
                this.axis.draw(g2, target.getMaxY() - this.stripWidth - this.axisOffset, target, target, RectangleEdge.TOP, null);
            } else if (axisEdge == RectangleEdge.BOTTOM) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.BOTTOM);
                    double vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.BOTTOM);
                    double ww = Math.abs(vv1 - vv0) + 1.0;
                    r.setRect(Math.min(vv0, vv1), target.getMinY(), ww, this.stripWidth);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    g2.draw(new Rectangle2D.Double(target.getMinX(), target.getMinY(), target.getWidth(), this.stripWidth));
                }
                this.axis.draw(g2, target.getMinY() + this.stripWidth + this.axisOffset, target, target, RectangleEdge.BOTTOM, null);
            }
        } else {
            RectangleEdge axisEdge = Plot.resolveRangeAxisLocation(this.axisLocation, PlotOrientation.VERTICAL);
            if (axisEdge == RectangleEdge.LEFT) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.LEFT);
                    double vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.LEFT);
                    double hh = Math.abs(vv1 - vv0) + 1.0;
                    r.setRect(target.getMaxX() - this.stripWidth, Math.min(vv0, vv1), this.stripWidth, hh);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    g2.draw(new Rectangle2D.Double(target.getMaxX() - this.stripWidth, target.getMinY(), this.stripWidth, target.getHeight()));
                }
                this.axis.draw(g2, target.getMaxX() - this.stripWidth - this.axisOffset, target, target, RectangleEdge.LEFT, null);
            } else if (axisEdge == RectangleEdge.RIGHT) {
                for (int i = 0; i < this.subdivisions; i++) {
                    double v = base + (i * increment);
                    Paint p = this.scale.getPaint(v);
                    double vv0 = this.axis.valueToJava2D(v, target, RectangleEdge.LEFT);
                    double vv1 = this.axis.valueToJava2D(v + increment, target, RectangleEdge.LEFT);
                    double hh = Math.abs(vv1 - vv0) + 1.0;
                    r.setRect(target.getMinX(), Math.min(vv0, vv1), this.stripWidth, hh);
                    g2.setPaint(p);
                    g2.fill(r);
                }
                if (isStripOutlineVisible()) {
                    g2.setPaint(this.stripOutlinePaint);
                    g2.setStroke(this.stripOutlineStroke);
                    g2.draw(new Rectangle2D.Double(target.getMinX(), target.getMinY(), this.stripWidth, target.getHeight()));
                }
                this.axis.draw(g2, target.getMinX() + this.stripWidth + this.axisOffset, target, target, RectangleEdge.RIGHT, null);
            }
        }
        return null;
    }

    /**
     * Tests this legend for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PaintScaleLegend)) {
            return false;
        }
        PaintScaleLegend that = (PaintScaleLegend) obj;
        if (!this.scale.equals(that.scale)) {
            return false;
        }
        if (!this.axis.equals(that.axis)) {
            return false;
        }
        if (!this.axisLocation.equals(that.axisLocation)) {
            return false;
        }
        if (this.axisOffset != that.axisOffset) {
            return false;
        }
        if (this.stripWidth != that.stripWidth) {
            return false;
        }
        if (this.stripOutlineVisible != that.stripOutlineVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.stripOutlinePaint, that.stripOutlinePaint)) {
            return false;
        }
        if (!this.stripOutlineStroke.equals(that.stripOutlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (this.subdivisions != that.subdivisions) {
            return false;
        }
        return super.equals(obj);
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
        SerialUtils.writePaint(this.backgroundPaint, stream);
        SerialUtils.writePaint(this.stripOutlinePaint, stream);
        SerialUtils.writeStroke(this.stripOutlineStroke, stream);
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
        this.backgroundPaint = SerialUtils.readPaint(stream);
        this.stripOutlinePaint = SerialUtils.readPaint(stream);
        this.stripOutlineStroke = SerialUtils.readStroke(stream);
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
 * -------------------------------------
 * DefaultMultiValueCategoryDataset.java
 * -------------------------------------
 * (C) Copyright 2007-present, by David Forslund and Contributors.
 *
 * Original Author:  David Forslund;
 * Contributor(s):   David Gilbert;
 * 
 */
/**
 * A category dataset that defines multiple values for each item.
 *
 * @param <R> the row key type.
 * @param <C> the column key type.
 */
public class DefaultMultiValueCategoryDataset<R extends Comparable<R>, C extends Comparable<C>> extends AbstractDataset implements MultiValueCategoryDataset<R, C>, RangeInfo, PublicCloneable {

    /**
     * Storage for the data.
     */
    protected KeyedObjects2D<R, C> data;

    /**
     * The minimum range value.
     */
    private Number minimumRangeValue;

    /**
     * The maximum range value.
     */
    private Number maximumRangeValue;

    /**
     * The range of values.
     */
    private Range rangeBounds;

    /**
     * Creates a new dataset.
     */
    public DefaultMultiValueCategoryDataset() {
        super();
        this.data = new KeyedObjects2D<>();
        this.minimumRangeValue = null;
        this.maximumRangeValue = null;
        this.rangeBounds = new Range(0.0, 0.0);
    }

    /**
     * Adds a list of values to the dataset ({@code null} and Double.NaN
     * items are automatically removed) and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param values  a list of values ({@code null} not permitted).
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     */
    public void add(List<? extends Number> values, R rowKey, C columnKey) {
        Args.nullNotPermitted(values, "values");
        Args.nullNotPermitted(rowKey, "rowKey");
        Args.nullNotPermitted(columnKey, "columnKey");
        List<Double> vlist = new ArrayList<>(values.size());
        for (Number v : values) {
            if (v != null && !Double.isNaN(v.doubleValue())) {
                vlist.add(v.doubleValue());
            }
        }
        Collections.sort(vlist);
        this.data.addObject(vlist, rowKey, columnKey);
        if (!vlist.isEmpty()) {
            double maxval = Double.NEGATIVE_INFINITY;
            double minval = Double.POSITIVE_INFINITY;
            for (Double n : vlist) {
                minval = Math.min(minval, n);
                maxval = Math.max(maxval, n);
            }
            // update the cached range values...
            if (this.maximumRangeValue == null) {
                this.maximumRangeValue = maxval;
            } else if (maxval > this.maximumRangeValue.doubleValue()) {
                this.maximumRangeValue = maxval;
            }
            if (this.minimumRangeValue == null) {
                this.minimumRangeValue = minval;
            } else if (minval < this.minimumRangeValue.doubleValue()) {
                this.minimumRangeValue = minval;
            }
            this.rangeBounds = new Range(this.minimumRangeValue.doubleValue(), this.maximumRangeValue.doubleValue());
        }
        fireDatasetChanged();
    }

    /**
     * Returns a list (possibly empty) of the values for the specified item.
     * The returned list should be unmodifiable.
     *
     * @param row  the row index (zero-based).
     * @param column   the column index (zero-based).
     *
     * @return The list of values.
     */
    @Override
    public List<? extends Number> getValues(int row, int column) {
        List values = (List) this.data.getObject(row, column);
        if (values != null) {
            return Collections.unmodifiableList(values);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Returns a list (possibly empty) of the values for the specified item.
     * The returned list should be unmodifiable.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @return The list of values.
     */
    @Override
    public List<? extends Number> getValues(R rowKey, C columnKey) {
        return Collections.unmodifiableList((List) this.data.getObject(rowKey, columnKey));
    }

    /**
     * Returns the average value for the specified item.
     *
     * @param row  the row key.
     * @param column  the column key.
     *
     * @return The average value.
     */
    @Override
    public Number getValue(R row, C column) {
        List l = (List) this.data.getObject(row, column);
        double average = 0.0d;
        int count = 0;
        if (l != null && !l.isEmpty()) {
            for (int i = 0; i < l.size(); i++) {
                Number n = (Number) l.get(i);
                average += n.doubleValue();
                count += 1;
            }
            if (count > 0) {
                average = average / count;
            }
        }
        if (count == 0) {
            return null;
        }
        return average;
    }

    /**
     * Returns the average value for the specified item.
     *
     * @param row  the row index.
     * @param column  the column index.
     *
     * @return The average value.
     */
    @Override
    public Number getValue(int row, int column) {
        List l = (List) this.data.getObject(row, column);
        double average = 0.0d;
        int count = 0;
        if (l != null && !l.isEmpty()) {
            for (int i = 0; i < l.size(); i++) {
                Number n = (Number) l.get(i);
                average += n.doubleValue();
                count += 1;
            }
            if (count > 0) {
                average = average / count;
            }
        }
        if (count == 0) {
            return null;
        }
        return average;
    }

    /**
     * Returns the column index for a given key.
     *
     * @param key  the column key.
     *
     * @return The column index.
     */
    @Override
    public int getColumnIndex(C key) {
        return this.data.getColumnIndex(key);
    }

    /**
     * Returns a column key.
     *
     * @param column the column index (zero-based).
     *
     * @return The column key.
     */
    @Override
    public C getColumnKey(int column) {
        return this.data.getColumnKey(column);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     */
    @Override
    public List<C> getColumnKeys() {
        return this.data.getColumnKeys();
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key the row key.
     *
     * @return The row index.
     */
    @Override
    public int getRowIndex(R key) {
        return this.data.getRowIndex(key);
    }

    /**
     * Returns a row key.
     *
     * @param row the row index (zero-based).
     *
     * @return The row key.
     */
    @Override
    public R getRowKey(int row) {
        return this.data.getRowKey(row);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     */
    @Override
    public List<R> getRowKeys() {
        return this.data.getRowKeys();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     */
    @Override
    public int getRowCount() {
        return this.data.getRowCount();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     */
    @Override
    public int getColumnCount() {
        return this.data.getColumnCount();
    }

    /**
     * Returns the minimum y-value in the dataset.
     *
     * @param includeInterval a flag that determines whether the
     *                        y-interval is taken into account.
     *
     * @return The minimum value.
     */
    @Override
    public double getRangeLowerBound(boolean includeInterval) {
        double result = Double.NaN;
        if (this.minimumRangeValue != null) {
            result = this.minimumRangeValue.doubleValue();
        }
        return result;
    }

    /**
     * Returns the maximum y-value in the dataset.
     *
     * @param includeInterval a flag that determines whether the
     *                        y-interval is taken into account.
     *
     * @return The maximum value.
     */
    @Override
    public double getRangeUpperBound(boolean includeInterval) {
        double result = Double.NaN;
        if (this.maximumRangeValue != null) {
            result = this.maximumRangeValue.doubleValue();
        }
        return result;
    }

    /**
     * Returns the range of the values in this dataset's range.
     *
     * @param includeInterval a flag that determines whether the
     *                        y-interval is taken into account.
     * @return The range.
     */
    @Override
    public Range getRangeBounds(boolean includeInterval) {
        return this.rangeBounds;
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
        if (!(obj instanceof DefaultMultiValueCategoryDataset)) {
            return false;
        }
        DefaultMultiValueCategoryDataset that = (DefaultMultiValueCategoryDataset) obj;
        return this.data.equals(that.data);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.data);
        return hash;
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the dataset cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultMultiValueCategoryDataset clone = (DefaultMultiValueCategoryDataset) super.clone();
        clone.data = (KeyedObjects2D) this.data.clone();
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
 */
/**
 * Specialised layout manager for a grid of components.
 */
public class LCBLayout implements LayoutManager, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -2531780832406163833L;

    /**
     * A constant for the number of columns in the layout.
     */
    private static final int COLUMNS = 3;

    /**
     * Tracks the column widths.
     */
    private int[] colWidth;

    /**
     * Tracks the row heights.
     */
    private int[] rowHeight;

    /**
     * The gap between each label and component.
     */
    private int labelGap;

    /**
     * The gap between each component and button.
     */
    private int buttonGap;

    /**
     * The gap between rows.
     */
    private int vGap;

    /**
     * Creates a new LCBLayout with the specified maximum number of rows.
     *
     * @param maxrows  the maximum number of rows.
     */
    public LCBLayout(int maxrows) {
        this.labelGap = 10;
        this.buttonGap = 6;
        this.vGap = 2;
        this.colWidth = new int[COLUMNS];
        this.rowHeight = new int[maxrows];
    }

    /**
     * Returns the preferred size using this layout manager.
     *
     * @param parent  the parent.
     *
     * @return the preferred size using this layout manager.
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = ncomponents / COLUMNS;
            for (int c = 0; c < COLUMNS; c++) {
                for (int r = 0; r < nrows; r++) {
                    Component component = parent.getComponent(r * COLUMNS + c);
                    Dimension d = component.getPreferredSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r = 0; r < nrows; r++) {
                totalHeight = totalHeight + this.rowHeight[r];
            }
            int totalWidth = this.colWidth[0] + this.labelGap + this.colWidth[1] + this.buttonGap + this.colWidth[2];
            return new Dimension(insets.left + insets.right + totalWidth + this.labelGap + this.buttonGap, insets.top + insets.bottom + totalHeight + this.vGap);
        }
    }

    /**
     * Returns the minimum size using this layout manager.
     *
     * @param parent  the parent.
     *
     * @return the minimum size using this layout manager.
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = ncomponents / COLUMNS;
            for (int c = 0; c < COLUMNS; c++) {
                for (int r = 0; r < nrows; r++) {
                    Component component = parent.getComponent(r * COLUMNS + c);
                    Dimension d = component.getMinimumSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r = 0; r < nrows; r++) {
                totalHeight = totalHeight + this.rowHeight[r];
            }
            int totalWidth = this.colWidth[0] + this.labelGap + this.colWidth[1] + this.buttonGap + this.colWidth[2];
            return new Dimension(insets.left + insets.right + totalWidth + this.labelGap + this.buttonGap, insets.top + insets.bottom + totalHeight + this.vGap);
        }
    }

    /**
     * Lays out the components.
     *
     * @param parent  the parent.
     */
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int ncomponents = parent.getComponentCount();
            int nrows = ncomponents / COLUMNS;
            for (int c = 0; c < COLUMNS; c++) {
                for (int r = 0; r < nrows; r++) {
                    Component component = parent.getComponent(r * COLUMNS + c);
                    Dimension d = component.getPreferredSize();
                    if (this.colWidth[c] < d.width) {
                        this.colWidth[c] = d.width;
                    }
                    if (this.rowHeight[r] < d.height) {
                        this.rowHeight[r] = d.height;
                    }
                }
            }
            int totalHeight = this.vGap * (nrows - 1);
            for (int r = 0; r < nrows; r++) {
                totalHeight = totalHeight + this.rowHeight[r];
            }
            int totalWidth = this.colWidth[0] + this.colWidth[1] + this.colWidth[2];
            // adjust the width of the second column to use up all of parent
            int available = parent.getWidth() - insets.left - insets.right - this.labelGap - this.buttonGap;
            this.colWidth[1] = this.colWidth[1] + (available - totalWidth);
            // *** DO THE LAYOUT ***
            int x = insets.left;
            for (int c = 0; c < COLUMNS; c++) {
                int y = insets.top;
                for (int r = 0; r < nrows; r++) {
                    int i = r * COLUMNS + c;
                    if (i < ncomponents) {
                        Component component = parent.getComponent(i);
                        Dimension d = component.getPreferredSize();
                        int h = d.height;
                        int adjust = (this.rowHeight[r] - h) / 2;
                        parent.getComponent(i).setBounds(x, y + adjust, this.colWidth[c], h);
                    }
                    y = y + this.rowHeight[r] + this.vGap;
                }
                x = x + this.colWidth[c];
                if (c == 0) {
                    x = x + this.labelGap;
                }
                if (c == 1) {
                    x = x + this.buttonGap;
                }
            }
        }
    }

    /**
     * Not used.
     *
     * @param comp  the component.
     */
    public void addLayoutComponent(Component comp) {
        // not used
    }

    /**
     * Not used.
     *
     * @param comp  the component.
     */
    @Override
    public void removeLayoutComponent(Component comp) {
        // not used
    }

    /**
     * Not used.
     *
     * @param name  the component name.
     * @param comp  the component.
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
        // not used
    }

    /**
     * Not used.
     *
     * @param name  the component name.
     * @param comp  the component.
     */
    public void removeLayoutComponent(String name, Component comp) {
        // not used
    }
}
