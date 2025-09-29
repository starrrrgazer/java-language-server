package DEF.ct;
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
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 */
/**
 * A plug-in object that is used by the {@link CategoryPlot} class to display
 * individual data items from a {@link CategoryDataset}.
 * <p>
 * This interface defines the methods that must be provided by all renderers.
 * If you are implementing a custom renderer, you should consider extending the
 * {@link AbstractCategoryItemRenderer} class.
 * <p>
 * Most renderer attributes are defined using a two layer approach.  When
 * looking up an attribute (for example, the outline paint) the renderer first
 * checks to see if there is a setting that applies to a specific series
 * that the renderer draws.  If there is, that setting is used, but if it is
 * {@code null} the renderer looks up the default setting.  Some attributes
 * allow the base setting to be {@code null}, while other attributes enforce
 * non-{@code null} values.
 */
public interface CategoryItemRenderer extends ChartElement, LegendItemSource {

    /**
     * Returns the number of passes through the dataset required by the
     * renderer.  Usually this will be one, but some renderers may use
     * a second or third pass to overlay items on top of things that were
     * drawn in an earlier pass.
     *
     * @return The pass count.
     */
    int getPassCount();

    /**
     * Returns the plot that the renderer has been assigned to (where
     * {@code null} indicates that the renderer is not currently assigned
     * to a plot).
     *
     * @return The plot (possibly {@code null}).
     *
     * @see #setPlot(CategoryPlot)
     */
    CategoryPlot<?, ?> getPlot();

    /**
     * Sets the plot that the renderer has been assigned to.  This method is
     * usually called by the {@link CategoryPlot}, in normal usage you
     * shouldn't need to call this method directly.
     *
     * @param plot  the plot ({@code null} not permitted).
     *
     * @see #getPlot()
     */
    void setPlot(CategoryPlot<?, ?> plot);

    /**
     * Adds a change listener.
     *
     * @param listener  the listener.
     *
     * @see #removeChangeListener(RendererChangeListener)
     */
    void addChangeListener(RendererChangeListener listener);

    /**
     * Removes a change listener.
     *
     * @param listener  the listener.
     *
     * @see #addChangeListener(RendererChangeListener)
     */
    void removeChangeListener(RendererChangeListener listener);

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range (or {@code null} if the dataset is
     *         {@code null} or empty).
     */
    Range findRangeBounds(CategoryDataset<?, ?> dataset);

    /**
     * Initialises the renderer.  This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain. The renderer can do nothing if
     * it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  collects chart rendering information for return to caller.
     *
     * @return A state object (maintains state information relevant to one
     *         chart drawing).
     */
    CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot<?, ?> plot, int rendererIndex, PlotRenderingInfo info);

    /**
     * Returns a boolean that indicates whether the specified item
     * should be drawn (this is typically used to hide an entire series).
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return A boolean.
     */
    boolean getItemVisible(int series, int item);

    /**
     * Returns a boolean that indicates whether the specified series
     * should be drawn (this is typically used to hide an entire series).
     *
     * @param series  the series index.
     *
     * @return A boolean.
     */
    boolean isSeriesVisible(int series);

    /**
     * Returns the flag that controls whether a series is visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesVisible(int, Boolean)
     */
    Boolean getSeriesVisible(int series);

    /**
     * Sets the flag that controls whether a series is visible and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #getSeriesVisible(int)
     */
    void setSeriesVisible(int series, Boolean visible);

    /**
     * Sets the flag that controls whether a series is visible and, if
     * requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index.
     * @param visible  the flag ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesVisible(int)
     */
    void setSeriesVisible(int series, Boolean visible, boolean notify);

    /**
     * Returns the default visibility for all series.
     *
     * @return The default visibility.
     *
     * @see #setDefaultSeriesVisible(boolean)
     */
    boolean getDefaultSeriesVisible();

    /**
     * Sets the default visibility and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultSeriesVisible()
     */
    void setDefaultSeriesVisible(boolean visible);

    /**
     * Sets the default visibility and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultSeriesVisible()
     */
    void setDefaultSeriesVisible(boolean visible, boolean notify);

    // SERIES VISIBLE IN LEGEND (not yet respected by all renderers)
    /**
     * Returns {@code true} if the series should be shown in the legend,
     * and {@code false} otherwise.
     *
     * @param series  the series index.
     *
     * @return A boolean.
     */
    boolean isSeriesVisibleInLegend(int series);

    /**
     * Returns the flag that controls whether a series is visible in the
     * legend.  This method returns only the "per series" settings - to
     * incorporate the override and base settings as well, you need to use the
     * {@link #isSeriesVisibleInLegend(int)} method.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesVisibleInLegend(int, Boolean)
     */
    Boolean getSeriesVisibleInLegend(int series);

    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #getSeriesVisibleInLegend(int)
     */
    void setSeriesVisibleInLegend(int series, Boolean visible);

    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and, if requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index.
     * @param visible  the flag ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesVisibleInLegend(int)
     */
    void setSeriesVisibleInLegend(int series, Boolean visible, boolean notify);

    /**
     * Returns the default visibility in the legend for all series.
     *
     * @return The default visibility.
     *
     * @see #setDefaultSeriesVisibleInLegend(boolean)
     */
    boolean getDefaultSeriesVisibleInLegend();

    /**
     * Sets the default visibility in the legend and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultSeriesVisibleInLegend()
     */
    void setDefaultSeriesVisibleInLegend(boolean visible);

    /**
     * Sets the default visibility in the legend and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultSeriesVisibleInLegend()
     */
    void setDefaultSeriesVisibleInLegend(boolean visible, boolean notify);

    //// PAINT /////////////////////////////////////////////////////////////////
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemPaint(int row, int column);

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesPaint(int, Paint)
     */
    Paint getSeriesPaint(int series);

    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesPaint(int)
     */
    void setSeriesPaint(int series, Paint paint);

    /**
     * Sets the paint used for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesPaint(int)
     */
    void setSeriesPaint(int series, Paint paint, boolean notify);

    /**
     * Returns the default paint.  During rendering, a renderer will first look
     * up the series paint and, if this is {@code null}, it will use the
     * default paint.
     *
     * @return The default paint (never {@code null}).
     *
     * @see #setDefaultPaint(Paint)
     */
    Paint getDefaultPaint();

    /**
     * Sets the default paint and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultPaint()
     */
    void setDefaultPaint(Paint paint);

    /**
     * Sets the default paint and sends a {@link RendererChangeEvent} to all
     * registered listeners if requested.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultPaint()
     */
    void setDefaultPaint(Paint paint, boolean notify);

    //// FILL PAINT /////////////////////////////////////////////////////////
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemFillPaint(int row, int column);

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesFillPaint(int, Paint)
     */
    Paint getSeriesFillPaint(int series);

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesFillPaint(int)
     */
    void setSeriesFillPaint(int series, Paint paint);

    /**
     * Returns the default outline paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultFillPaint(Paint)
     */
    Paint getDefaultFillPaint();

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultFillPaint()
     */
    void setDefaultFillPaint(Paint paint);

    //// OUTLINE PAINT /////////////////////////////////////////////////////////
    /**
     * Returns the paint used to outline data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemOutlinePaint(int row, int column);

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesOutlinePaint(int, Paint)
     */
    Paint getSeriesOutlinePaint(int series);

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesOutlinePaint(int)
     */
    void setSeriesOutlinePaint(int series, Paint paint);

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesOutlinePaint(int)
     */
    void setSeriesOutlinePaint(int series, Paint paint, boolean notify);

    /**
     * Returns the default outline paint.  During rendering, the renderer
     * will look up the series outline paint and, if this is {@code null}, it
     * will use the default outline paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultOutlinePaint(Paint)
     */
    Paint getDefaultOutlinePaint();

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultOutlinePaint()
     */
    void setDefaultOutlinePaint(Paint paint);

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners if requested.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  send a change event?
     *
     * @see #getDefaultOutlinePaint()
     */
    void setDefaultOutlinePaint(Paint paint, boolean notify);

    //// STROKE ////////////////////////////////////////////////////////////////
    /**
     * Returns the stroke used to draw data items.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    Stroke getItemStroke(int row, int column);

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setSeriesStroke(int, Stroke)
     */
    Stroke getSeriesStroke(int series);

    /**
     * Sets the stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getSeriesStroke(int)
     */
    void setSeriesStroke(int series, Stroke stroke);

    /**
     * Sets the stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesStroke(int)
     */
    void setSeriesStroke(int series, Stroke stroke, boolean notify);

    /**
     * Returns the default stroke.
     *
     * @return The default stroke (never {@code null}).
     *
     * @see #setDefaultStroke(Stroke)
     */
    Stroke getDefaultStroke();

    /**
     * Sets the default stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultStroke()
     */
    void setDefaultStroke(Stroke stroke);

    /**
     * Sets the default stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners if requested.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultStroke()
     */
    void setDefaultStroke(Stroke stroke, boolean notify);

    //// OUTLINE STROKE ////////////////////////////////////////////////////////
    /**
     * Returns the stroke used to outline data items.
     * <p>
     * The default implementation passes control to the
     * lookupSeriesOutlineStroke method.  You can override this method if you
     * require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    Stroke getItemOutlineStroke(int row, int column);

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setSeriesOutlineStroke(int, Stroke)
     */
    Stroke getSeriesOutlineStroke(int series);

    /**
     * Sets the outline stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getSeriesOutlineStroke(int)
     */
    void setSeriesOutlineStroke(int series, Stroke stroke);

    /**
     * Sets the outline stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesOutlineStroke(int)
     */
    void setSeriesOutlineStroke(int series, Stroke stroke, boolean notify);

    /**
     * Returns the default outline stroke.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDefaultOutlineStroke(Stroke)
     */
    Stroke getDefaultOutlineStroke();

    /**
     * Sets the default outline stroke and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultOutlineStroke()
     */
    void setDefaultOutlineStroke(Stroke stroke);

    /**
     * Sets the default outline stroke and sends a {@link RendererChangeEvent} to
     * all registered listeners if requested.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultOutlineStroke()
     */
    void setDefaultOutlineStroke(Stroke stroke, boolean notify);

    //// SHAPE /////////////////////////////////////////////////////////////////
    /**
     * Returns a shape used to represent a data item.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The shape (never {@code null}).
     */
    Shape getItemShape(int row, int column);

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The shape (possibly {@code null}).
     *
     * @see #setSeriesShape(int, Shape)
     */
    Shape getSeriesShape(int series);

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape ({@code null} permitted).
     *
     * @see #getSeriesShape(int)
     */
    void setSeriesShape(int series, Shape shape);

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesShape(int)
     */
    void setSeriesShape(int series, Shape shape, boolean notify);

    /**
     * Returns the default shape.
     *
     * @return The shape (never {@code null}).
     *
     * @see #setDefaultShape(Shape)
     */
    Shape getDefaultShape();

    /**
     * Sets the default shape and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param shape  the shape ({@code null} not permitted).
     *
     * @see #getDefaultShape()
     */
    void setDefaultShape(Shape shape);

    /**
     * Sets the default shape and sends a {@link RendererChangeEvent} to all
     * registered listeners if requested.
     *
     * @param shape  the shape ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultShape()
     */
    void setDefaultShape(Shape shape, boolean notify);

    // ITEM LABELS VISIBLE
    /**
     * Returns {@code true} if an item label is visible, and
     * {@code false} otherwise.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return A boolean.
     */
    boolean isItemLabelVisible(int row, int column);

    /**
     * Returns {@code true} if the item labels for a series are visible,
     * and {@code false} otherwise.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesItemLabelsVisible(int, Boolean)
     */
    boolean isSeriesItemLabelsVisible(int series);

    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     *
     * @see #isSeriesItemLabelsVisible(int)
     */
    void setSeriesItemLabelsVisible(int series, boolean visible);

    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #isSeriesItemLabelsVisible(int)
     */
    void setSeriesItemLabelsVisible(int series, Boolean visible);

    /**
     * Sets the visibility of item labels for a series and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the visible flag.
     * @param notify  a flag that controls whether listeners are notified.
     *
     * @see #isSeriesItemLabelsVisible(int)
     */
    void setSeriesItemLabelsVisible(int series, Boolean visible, boolean notify);

    /**
     * Returns the default setting for item label visibility.  A {@code null}
     * result should be interpreted as equivalent to {@code Boolean.FALSE}
     * (this is an error in the API design, the return value should have been
     * a boolean primitive).
     *
     * @return A flag (possibly {@code null}).
     *
     * @see #setDefaultItemLabelsVisible(boolean)
     */
    boolean getDefaultItemLabelsVisible();

    /**
     * Sets the default flag that controls whether item labels are visible
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultItemLabelsVisible()
     */
    void setDefaultItemLabelsVisible(boolean visible);

    /**
     * Sets the default visibility for item labels and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility flag.
     * @param notify  a flag that controls whether listeners are notified.
     *
     * @see #getDefaultItemLabelsVisible()
     */
    void setDefaultItemLabelsVisible(boolean visible, boolean notify);

    // ITEM LABEL GENERATOR
    /**
     * Returns the item label generator for the specified data item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The generator (possibly {@code null}).
     */
    CategoryItemLabelGenerator getItemLabelGenerator(int series, int item);

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The label generator (possibly {@code null}).
     *
     * @see #setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)
     */
    CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series);

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator);

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     * @param notify  send change event?
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator, boolean notify);

    /**
     * Returns the default item label generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setDefaultItemLabelGenerator(CategoryItemLabelGenerator)
     */
    CategoryItemLabelGenerator getDefaultItemLabelGenerator();

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultItemLabelGenerator()
     */
    void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator);

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemLabelGenerator()
     */
    void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator, boolean notify);

    // TOOL TIP GENERATOR
    /**
     * Returns the tool tip generator that should be used for the specified
     * item.  This method looks up the generator using the "three-layer"
     * approach outlined in the general description of this interface.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The generator (possibly {@code null}).
     */
    CategoryToolTipGenerator getToolTipGenerator(int row, int column);

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
    CategoryToolTipGenerator getSeriesToolTipGenerator(int series);

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator);

    /**
     * Sets the tool tip generator for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator, boolean notify);

    /**
     * Returns the default tool tip generator (the "layer 2" generator).
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setDefaultToolTipGenerator(CategoryToolTipGenerator)
     */
    CategoryToolTipGenerator getDefaultToolTipGenerator();

    /**
     * Sets the default tool tip generator and sends a
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered
     * listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultToolTipGenerator()
     */
    void setDefaultToolTipGenerator(CategoryToolTipGenerator generator);

    /**
     * Sets the default tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered
     * listeners if requested.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultToolTipGenerator()
     */
    void setDefaultToolTipGenerator(CategoryToolTipGenerator generator, boolean notify);

    //// ITEM LABEL FONT  //////////////////////////////////////////////////////
    /**
     * Returns the font for an item label.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The font (never {@code null}).
     */
    Font getItemLabelFont(int row, int column);

    /**
     * Returns the font for all the item labels in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The font (possibly {@code null}).
     *
     * @see #setSeriesItemLabelFont(int, Font)
     */
    Font getSeriesItemLabelFont(int series);

    /**
     * Sets the item label font for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param font  the font ({@code null} permitted).
     *
     * @see #getSeriesItemLabelFont(int)
     */
    void setSeriesItemLabelFont(int series, Font font);

    /**
     * Sets the item label font for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param font  the font ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesItemLabelFont(int)
     */
    void setSeriesItemLabelFont(int series, Font font, boolean notify);

    /**
     * Returns the default item label font (this is used when no other font
     * setting is available).
     *
     * @return The font (never {@code null}).
     *
     * @see #setDefaultItemLabelFont(Font)
     */
    Font getDefaultItemLabelFont();

    /**
     * Sets the default item label font and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getDefaultItemLabelFont()
     */
    void setDefaultItemLabelFont(Font font);

    /**
     * Sets the default item label font and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param font  the font ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemLabelFont()
     */
    void setDefaultItemLabelFont(Font font, boolean notify);

    //// ITEM LABEL PAINT  /////////////////////////////////////////////////////
    /**
     * Returns the paint used to draw an item label.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemLabelPaint(int row, int column);

    /**
     * Returns the paint used to draw the item labels for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesItemLabelPaint(int, Paint)
     */
    Paint getSeriesItemLabelPaint(int series);

    /**
     * Sets the item label paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series (zero based index).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesItemLabelPaint(int)
     */
    void setSeriesItemLabelPaint(int series, Paint paint);

    /**
     * Sets the item label paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series (zero based index).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesItemLabelPaint(int)
     */
    void setSeriesItemLabelPaint(int series, Paint paint, boolean notify);

    /**
     * Returns the default item label paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultItemLabelPaint(Paint)
     */
    Paint getDefaultItemLabelPaint();

    /**
     * Sets the default item label paint and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultItemLabelPaint()
     */
    void setDefaultItemLabelPaint(Paint paint);

    /**
     * Sets the default item label paint and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemLabelPaint()
     */
    void setDefaultItemLabelPaint(Paint paint, boolean notify);

    // POSITIVE ITEM LABEL POSITION...
    /**
     * Returns the item label position for positive values.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The item label position (never {@code null}).
     */
    ItemLabelPosition getPositiveItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for all positive values in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item label position.
     *
     * @see #setSeriesPositiveItemLabelPosition(int, ItemLabelPosition)
     */
    ItemLabelPosition getSeriesPositiveItemLabelPosition(int series);

    /**
     * Sets the item label position for all positive values in a series and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     *
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position);

    /**
     * Sets the item label position for all positive values in a series and (if
     * requested) sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     * @param notify  notify registered listeners?
     *
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position, boolean notify);

    /**
     * Returns the default positive item label position.
     *
     * @return The position.
     *
     * @see #setDefaultPositiveItemLabelPosition(ItemLabelPosition)
     */
    ItemLabelPosition getDefaultPositiveItemLabelPosition();

    /**
     * Sets the default positive item label position.
     *
     * @param position  the position.
     *
     * @see #getDefaultPositiveItemLabelPosition()
     */
    void setDefaultPositiveItemLabelPosition(ItemLabelPosition position);

    /**
     * Sets the default positive item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position.
     * @param notify  notify registered listeners?
     *
     * @see #getDefaultPositiveItemLabelPosition()
     */
    void setDefaultPositiveItemLabelPosition(ItemLabelPosition position, boolean notify);

    // NEGATIVE ITEM LABEL POSITION...
    /**
     * Returns the item label position for negative values.  This method can be
     * overridden to provide customisation of the item label position for
     * individual data items.
     *
     * @param row  the row index (zero-based).
     * @param column  the column (zero-based).
     *
     * @return The item label position.
     */
    ItemLabelPosition getNegativeItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for all negative values in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item label position.
     *
     * @see #setSeriesNegativeItemLabelPosition(int, ItemLabelPosition)
     */
    ItemLabelPosition getSeriesNegativeItemLabelPosition(int series);

    /**
     * Sets the item label position for negative values in a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     *
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position);

    /**
     * Sets the item label position for negative values in a series and (if
     * requested) sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     * @param notify  notify registered listeners?
     *
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position, boolean notify);

    /**
     * Returns the default item label position for negative values.
     *
     * @return The position.
     *
     * @see #setDefaultNegativeItemLabelPosition(ItemLabelPosition)
     */
    ItemLabelPosition getDefaultNegativeItemLabelPosition();

    /**
     * Sets the default item label position for negative values and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position.
     *
     * @see #getDefaultNegativeItemLabelPosition()
     */
    void setDefaultNegativeItemLabelPosition(ItemLabelPosition position);

    /**
     * Sets the default negative item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position.
     * @param notify  notify registered listeners?
     *
     * @see #getDefaultNegativeItemLabelPosition()
     */
    void setDefaultNegativeItemLabelPosition(ItemLabelPosition position, boolean notify);

    // CREATE ENTITIES
    /**
     * Returns a flag that determines whether an entity is generated
     * for the specified item.  The standard implementation of this method
     * will typically return the flag for the series or, if that is
     * {@code null}, the value returned by {@link #getDefaultCreateEntities()}.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    boolean getItemCreateEntity(int series, int item);

    /**
     * Returns a boolean indicating whether entities should be created
     * for the items in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean (possibly {@code null}).
     */
    Boolean getSeriesCreateEntities(int series);

    /**
     * Sets a flag that indicates whether entities should be created during
     * rendering for the items in the specified series, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param create  the new flag value ({@code null} permitted).
     */
    void setSeriesCreateEntities(int series, Boolean create);

    /**
     * Sets a flag that indicates whether entities should be created during
     * rendering for the items in the specified series, and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param create  the new flag value ({@code null} permitted).
     * @param notify  send change event?
     */
    void setSeriesCreateEntities(int series, Boolean create, boolean notify);

    /**
     * Returns the default value for the flag that controls whether
     * an entity is created for an item during rendering.
     *
     * @return A boolean.
     */
    boolean getDefaultCreateEntities();

    /**
     * Sets the default setting for whether entities should be created
     * for items during rendering, and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param create  the new flag value.
     */
    void setDefaultCreateEntities(boolean create);

    /**
     * Sets the default setting for whether entities should be created
     * for items during rendering, and sends a {@link RendererChangeEvent} to
     * all registered listeners if requested.
     *
     * @param create  the new flag value.
     * @param notify  send change event?
     */
    void setDefaultCreateEntities(boolean create, boolean notify);

    // ITEM URL GENERATOR
    /**
     * Returns the URL generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item URL generator.
     */
    CategoryURLGenerator getItemURLGenerator(int series, int item);

    /**
     * Returns the item URL generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The URL generator.
     *
     * @see #setSeriesItemURLGenerator(int, CategoryURLGenerator)
     */
    CategoryURLGenerator getSeriesItemURLGenerator(int series);

    /**
     * Sets the item URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator);

    /**
     * Sets the item URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator, boolean notify);

    /**
     * Returns the default item URL generator.
     *
     * @return The item URL generator (possibly {@code null}).
     *
     * @see #setDefaultItemURLGenerator(CategoryURLGenerator)
     */
    CategoryURLGenerator getDefaultItemURLGenerator();

    /**
     * Sets the default item URL generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     *
     * @see #getDefaultItemURLGenerator()
     */
    void setDefaultItemURLGenerator(CategoryURLGenerator generator);

    /**
     * Sets the default item URL generator and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemURLGenerator()
     */
    void setDefaultItemURLGenerator(CategoryURLGenerator generator, boolean notify);

    /**
     * Returns a legend item for a series.  This method can return
     * {@code null}, in which case the series will have no entry in the
     * legend.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series (zero-based index).
     *
     * @return The legend item (possibly {@code null}).
     */
    LegendItem getLegendItem(int datasetIndex, int series);

    /**
     * Draws a background for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    void drawBackground(Graphics2D g2, CategoryPlot<?, ?> plot, Rectangle2D dataArea);

    /**
     * Draws an outline for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    void drawOutline(Graphics2D g2, CategoryPlot<?, ?> plot, Rectangle2D dataArea);

    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device.
     * @param state  state information for one chart.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot<?, ?> plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset<?, ?> dataset, int row, int column, int pass);

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data.
     * @param value  the value.
     */
    void drawDomainGridline(Graphics2D g2, CategoryPlot<?, ?> plot, Rectangle2D dataArea, double value);

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data.
     * @param value  the value.
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the line stroke ({@code null} not permitted).
     */
    void drawRangeLine(Graphics2D g2, CategoryPlot<?, ?> plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke);

    /**
     * Draws a line (or some other marker) to indicate a particular category on
     * the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the category axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data.
     *
     * @see #drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker,
     *     Rectangle2D)
     */
    void drawDomainMarker(Graphics2D g2, CategoryPlot<?, ?> plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea);

    /**
     * Draws a line (or some other marker) to indicate a particular value on
     * the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data.
     *
     * @see #drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis,
     *     CategoryMarker, Rectangle2D)
     */
    void drawRangeMarker(Graphics2D g2, CategoryPlot<?, ?> plot, ValueAxis axis, Marker marker, Rectangle2D dataArea);

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
    double getItemMiddle(Comparable<?> rowKey, Comparable<?> columnKey, CategoryDataset<?, ?> dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge);
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
 * -----------
 * XYPlot.java
 * -----------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Gideon Krause;
 *                   Klaus Rheinwald;
 *                   Xavier Poinsard;
 *                   Richard Atkinson;
 *                   Arnaud Lelievre;
 *                   Nicolas Brodu;
 *                   Eduardo Ramalho;
 *                   Sergei Ivanov;
 *                   Richard West, Advanced Micro Devices, Inc.;
 *                   Ulrich Voigt - patches 1997549 and 2686040;
 *                   Peter Kolb - patches 1934255, 2603321 and 2809117;
 *                   Andrew Mickish - patch 1868749;
 *
 */
/**
 * A general class for plotting data in the form of (x, y) pairs.  This plot can
 * use data from any class that implements the {@link XYDataset} interface.
 * <P>
 * {@code XYPlot} makes use of an {@link XYItemRenderer} to draw each point
 * on the plot.  By using different renderers, various chart types can be
 * produced.
 * <p>
 * The {@link org.jfree.chart.ChartFactory} class contains static methods for
 * creating pre-configured charts.
 *
 * @param <S>The type for the series keys.
 */
public class XYPlot<S extends Comparable<S>> extends Plot implements ValueAxisPlot, Pannable, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7044148245716569264L;

    /**
     * The default grid line stroke.
     */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);

    /**
     * The default grid line paint.
     */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.LIGHT_GRAY;

    /**
     * The default crosshair visibility.
     */
    public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;

    /**
     * The default crosshair stroke.
     */
    public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;

    /**
     * The default crosshair paint.
     */
    public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.BLUE;

    /**
     * The resourceBundle for the localization.
     */
    protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * The plot orientation.
     */
    private PlotOrientation orientation;

    /**
     * The offset between the data area and the axes.
     */
    private RectangleInsets axisOffset;

    /**
     * The domain axis / axes (used for the x-values).
     */
    private Map<Integer, ValueAxis> domainAxes;

    /**
     * The domain axis locations.
     */
    private Map<Integer, AxisLocation> domainAxisLocations;

    /**
     * The range axis (used for the y-values).
     */
    private Map<Integer, ValueAxis> rangeAxes;

    /**
     * The range axis location.
     */
    private Map<Integer, AxisLocation> rangeAxisLocations;

    /**
     * Storage for the datasets.
     */
    private Map<Integer, XYDataset<S>> datasets;

    /**
     * Storage for the renderers.
     */
    private Map<Integer, XYItemRenderer> renderers;

    /**
     * Storage for the mapping between datasets/renderers and domain axes.  The
     * keys in the map are Integer objects, corresponding to the dataset
     * index.  The values in the map are List objects containing Integer
     * objects (corresponding to the axis indices).  If the map contains no
     * entry for a dataset, it is assumed to map to the primary domain axis
     * (index = 0).
     */
    private Map<Integer, List<Integer>> datasetToDomainAxesMap;

    /**
     * Storage for the mapping between datasets/renderers and range axes.  The
     * keys in the map are Integer objects, corresponding to the dataset
     * index.  The values in the map are List objects containing Integer
     * objects (corresponding to the axis indices).  If the map contains no
     * entry for a dataset, it is assumed to map to the primary domain axis
     * (index = 0).
     */
    private Map<Integer, List<Integer>> datasetToRangeAxesMap;

    /**
     * The origin point for the quadrants (if drawn).
     */
    private transient Point2D quadrantOrigin = new Point2D.Double(0.0, 0.0);

    /**
     * The paint used for each quadrant.
     */
    private transient Paint[] quadrantPaint = new Paint[] { null, null, null, null };

    /**
     * A flag that controls whether the domain grid-lines are visible.
     */
    private boolean domainGridlinesVisible;

    /**
     * The stroke used to draw the domain grid-lines.
     */
    private transient Stroke domainGridlineStroke;

    /**
     * The paint used to draw the domain grid-lines.
     */
    private transient Paint domainGridlinePaint;

    /**
     * A flag that controls whether the range grid-lines are visible.
     */
    private boolean rangeGridlinesVisible;

    /**
     * The stroke used to draw the range grid-lines.
     */
    private transient Stroke rangeGridlineStroke;

    /**
     * The paint used to draw the range grid-lines.
     */
    private transient Paint rangeGridlinePaint;

    /**
     * A flag that controls whether the domain minor grid-lines are visible.
     */
    private boolean domainMinorGridlinesVisible;

    /**
     * The stroke used to draw the domain minor grid-lines.
     */
    private transient Stroke domainMinorGridlineStroke;

    /**
     * The paint used to draw the domain minor grid-lines.
     */
    private transient Paint domainMinorGridlinePaint;

    /**
     * A flag that controls whether the range minor grid-lines are visible.
     */
    private boolean rangeMinorGridlinesVisible;

    /**
     * The stroke used to draw the range minor grid-lines.
     */
    private transient Stroke rangeMinorGridlineStroke;

    /**
     * The paint used to draw the range minor grid-lines.
     */
    private transient Paint rangeMinorGridlinePaint;

    /**
     * A flag that controls whether the zero baseline against the domain
     * axis is visible.
     */
    private boolean domainZeroBaselineVisible;

    /**
     * The stroke used for the zero baseline against the domain axis.
     */
    private transient Stroke domainZeroBaselineStroke;

    /**
     * The paint used for the zero baseline against the domain axis.
     */
    private transient Paint domainZeroBaselinePaint;

    /**
     * A flag that controls whether the zero baseline against the range
     * axis is visible.
     */
    private boolean rangeZeroBaselineVisible;

    /**
     * The stroke used for the zero baseline against the range axis.
     */
    private transient Stroke rangeZeroBaselineStroke;

    /**
     * The paint used for the zero baseline against the range axis.
     */
    private transient Paint rangeZeroBaselinePaint;

    /**
     * A flag that controls whether a domain crosshair is drawn.
     */
    private boolean domainCrosshairVisible;

    /**
     * The domain crosshair value.
     */
    private double domainCrosshairValue;

    /**
     * The pen/brush used to draw the crosshair (if any).
     */
    private transient Stroke domainCrosshairStroke;

    /**
     * The color used to draw the crosshair (if any).
     */
    private transient Paint domainCrosshairPaint;

    /**
     * A flag that controls whether the crosshair locks onto actual
     * data points.
     */
    private boolean domainCrosshairLockedOnData = true;

    /**
     * A flag that controls whether a range crosshair is drawn.
     */
    private boolean rangeCrosshairVisible;

    /**
     * The range crosshair value.
     */
    private double rangeCrosshairValue;

    /**
     * The pen/brush used to draw the crosshair (if any).
     */
    private transient Stroke rangeCrosshairStroke;

    /**
     * The color used to draw the crosshair (if any).
     */
    private transient Paint rangeCrosshairPaint;

    /**
     * A flag that controls whether the crosshair locks onto actual
     * data points.
     */
    private boolean rangeCrosshairLockedOnData = true;

    /**
     * A map of lists of foreground markers (optional) for the domain axes.
     */
    private Map<Integer, List<Marker>> foregroundDomainMarkers;

    /**
     * A map of lists of background markers (optional) for the domain axes.
     */
    private Map<Integer, List<Marker>> backgroundDomainMarkers;

    /**
     * A map of lists of foreground markers (optional) for the range axes.
     */
    private Map<Integer, List<Marker>> foregroundRangeMarkers;

    /**
     * A map of lists of background markers (optional) for the range axes.
     */
    private Map<Integer, List<Marker>> backgroundRangeMarkers;

    /**
     * A (possibly empty) list of annotations for the plot.  The list should
     * be initialised in the constructor and never allowed to be
     * {@code null}.
     */
    private List<XYAnnotation> annotations;

    /**
     * The paint used for the domain tick bands (if any).
     */
    private transient Paint domainTickBandPaint;

    /**
     * The paint used for the range tick bands (if any).
     */
    private transient Paint rangeTickBandPaint;

    /**
     * The fixed domain axis space.
     */
    private AxisSpace fixedDomainAxisSpace;

    /**
     * The fixed range axis space.
     */
    private AxisSpace fixedRangeAxisSpace;

    /**
     * The order of the dataset rendering (REVERSE draws the primary dataset
     * last so that it appears to be on top).
     */
    private DatasetRenderingOrder datasetRenderingOrder = DatasetRenderingOrder.REVERSE;

    /**
     * The order of the series rendering (REVERSE draws the primary series
     * last so that it appears to be on top).
     */
    private SeriesRenderingOrder seriesRenderingOrder = SeriesRenderingOrder.REVERSE;

    /**
     * The weight for this plot (only relevant if this is a subplot in a
     * combined plot).
     */
    private int weight;

    /**
     * An optional collection of legend items that can be returned by the
     * getLegendItems() method.
     */
    private LegendItemCollection fixedLegendItems;

    /**
     * A flag that controls whether panning is enabled for the domain
     * axis/axes.
     */
    private boolean domainPannable;

    /**
     * A flag that controls whether panning is enabled for the range
     * axis/axes.
     */
    private boolean rangePannable;

    /**
     * The shadow generator ({@code null} permitted).
     */
    private ShadowGenerator shadowGenerator;

    /**
     * Creates a new {@code XYPlot} instance with no dataset, no axes and
     * no renderer.  You should specify these items before using the plot.
     */
    public XYPlot() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot with the specified dataset, axes and renderer.  Any
     * of the arguments can be {@code null}, but in that case you should
     * take care to specify the value before using the plot (otherwise a
     * {@code NullPointerException} may be thrown).
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param domainAxis  the domain axis ({@code null} permitted).
     * @param rangeAxis  the range axis ({@code null} permitted).
     * @param renderer  the renderer ({@code null} permitted).
     */
    public XYPlot(XYDataset<S> dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
        super();
        this.orientation = PlotOrientation.VERTICAL;
        // only relevant when this is a subplot
        this.weight = 1;
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        // allocate storage for datasets, axes and renderers (all optional)
        this.domainAxes = new HashMap<>();
        this.domainAxisLocations = new HashMap<>();
        this.foregroundDomainMarkers = new HashMap<>();
        this.backgroundDomainMarkers = new HashMap<>();
        this.rangeAxes = new HashMap<>();
        this.rangeAxisLocations = new HashMap<>();
        this.foregroundRangeMarkers = new HashMap<>();
        this.backgroundRangeMarkers = new HashMap<>();
        this.datasets = new HashMap<>();
        this.renderers = new HashMap<>();
        this.datasetToDomainAxesMap = new TreeMap<>();
        this.datasetToRangeAxesMap = new TreeMap<>();
        this.annotations = new ArrayList<>();
        if (dataset != null) {
            dataset.addChangeListener(this);
            this.datasets.put(0, dataset);
        }
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
            this.renderers.put(0, renderer);
        }
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
            this.domainAxes.put(0, domainAxis);
            mapDatasetToDomainAxis(0, 0);
        }
        this.domainAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
            this.rangeAxes.put(0, rangeAxis);
            mapDatasetToRangeAxis(0, 0);
        }
        this.rangeAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
        configureDomainAxes();
        configureRangeAxes();
        this.domainGridlinesVisible = true;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.domainMinorGridlinesVisible = false;
        this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainMinorGridlinePaint = Color.WHITE;
        this.domainZeroBaselineVisible = false;
        this.domainZeroBaselinePaint = Color.BLACK;
        this.domainZeroBaselineStroke = new BasicStroke(0.5f);
        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = false;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.WHITE;
        this.rangeZeroBaselineVisible = false;
        this.rangeZeroBaselinePaint = Color.BLACK;
        this.rangeZeroBaselineStroke = new BasicStroke(0.5f);
        this.domainCrosshairVisible = false;
        this.domainCrosshairValue = 0.0;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = false;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.shadowGenerator = null;
    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    @Override
    public String getPlotType() {
        return localizationResources.getString("XY_Plot");
    }

    /**
     * Returns the orientation of the plot.
     *
     * @return The orientation (never {@code null}).
     *
     * @see #setOrientation(PlotOrientation)
     */
    @Override
    public PlotOrientation getOrientation() {
        return this.orientation;
    }

    /**
     * Sets the orientation for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param orientation  the orientation ({@code null} not allowed).
     *
     * @see #getOrientation()
     */
    public void setOrientation(PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        if (orientation != this.orientation) {
            this.orientation = orientation;
            fireChangeEvent();
        }
    }

    /**
     * Returns the axis offset.
     *
     * @return The axis offset (never {@code null}).
     *
     * @see #setAxisOffset(RectangleInsets)
     */
    public RectangleInsets getAxisOffset() {
        return this.axisOffset;
    }

    /**
     * Sets the axis offsets (gap between the data area and the axes) and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param offset  the offset ({@code null} not permitted).
     *
     * @see #getAxisOffset()
     */
    public void setAxisOffset(RectangleInsets offset) {
        Args.nullNotPermitted(offset, "offset");
        this.axisOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the domain axis with index 0.  If the domain axis for this plot
     * is {@code null}, then the method will return the parent plot's
     * domain axis (if there is a parent plot).
     *
     * @return The domain axis (possibly {@code null}).
     *
     * @see #getDomainAxis(int)
     * @see #setDomainAxis(ValueAxis)
     */
    public ValueAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    /**
     * Returns the domain axis with the specified index, or {@code null} if
     * there is no axis with that index.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setDomainAxis(int, ValueAxis)
     */
    public ValueAxis getDomainAxis(int index) {
        ValueAxis result = this.domainAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> xy = (XYPlot<S>) parent;
                result = xy.getDomainAxis(index);
            }
        }
        return result;
    }

    /**
     * Returns a map containing the domain axes that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the domain axes that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, ValueAxis> getDomainAxes() {
        return Collections.unmodifiableMap(this.domainAxes);
    }

    /**
     * Sets the domain axis for the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param axis  the new axis ({@code null} permitted).
     *
     * @see #getDomainAxis()
     * @see #setDomainAxis(int, ValueAxis)
     */
    public void setDomainAxis(ValueAxis axis) {
        setDomainAxis(0, axis);
    }

    /**
     * Sets a domain axis and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getDomainAxis(int)
     * @see #setRangeAxis(int, ValueAxis)
     */
    public void setDomainAxis(int index, ValueAxis axis) {
        setDomainAxis(index, axis, true);
    }

    /**
     * Sets a domain axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     * @param notify  notify listeners?
     *
     * @see #getDomainAxis(int)
     */
    public void setDomainAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getDomainAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.domainAxes.put(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Sets the domain axes for this plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param axes  the axes ({@code null} not permitted).
     *
     * @see #setRangeAxes(ValueAxis[])
     */
    public void setDomainAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the location of the primary domain axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #setDomainAxisLocation(AxisLocation)
     */
    public AxisLocation getDomainAxisLocation() {
        return this.domainAxisLocations.get(0);
    }

    /**
     * Sets the location of the primary domain axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getDomainAxisLocation()
     */
    public void setDomainAxisLocation(AxisLocation location) {
        // delegate...
        setDomainAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDomainAxisLocation()
     */
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setDomainAxisLocation(0, location, notify);
    }

    /**
     * Returns the edge for the primary domain axis (taking into account the
     * plot's orientation).
     *
     * @return The edge.
     *
     * @see #getDomainAxisLocation()
     * @see #getOrientation()
     */
    public RectangleEdge getDomainAxisEdge() {
        return Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
    }

    /**
     * Returns the number of domain axes.
     *
     * @return The axis count.
     *
     * @see #getRangeAxisCount()
     */
    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    /**
     * Clears the domain axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @see #clearRangeAxes()
     */
    public void clearDomainAxes() {
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the domain axes.
     */
    public void configureDomainAxes() {
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }

    /**
     * Returns the location for a domain axis.  If this hasn't been set
     * explicitly, the method returns the location that is opposite to the
     * primary domain axis location.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The location (never {@code null}).
     *
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = this.domainAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getDomainAxisLocation());
        }
        return result;
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted for index
     *     0).
     *
     * @see #getDomainAxisLocation(int)
     */
    public void setDomainAxisLocation(int index, AxisLocation location) {
        // delegate...
        setDomainAxisLocation(index, location, true);
    }

    /**
     * Sets the axis location for a domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index (must be &gt;= 0).
     * @param location  the location ({@code null} not permitted for
     *     index 0).
     * @param notify  notify listeners?
     *
     * @see #getDomainAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation, boolean)
     */
    public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
        }
        this.domainAxisLocations.put(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the edge for a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     *
     * @see #getRangeAxisEdge(int)
     */
    public RectangleEdge getDomainAxisEdge(int index) {
        AxisLocation location = getDomainAxisLocation(index);
        return Plot.resolveDomainAxisLocation(location, this.orientation);
    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * {@code null}, then the method will return the parent plot's range
     * axis (if there is a parent plot).
     *
     * @return The range axis.
     *
     * @see #getRangeAxis(int)
     * @see #setRangeAxis(ValueAxis)
     */
    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    /**
     * Sets the range axis for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getRangeAxis()
     * @see #setRangeAxis(int, ValueAxis)
     */
    public void setRangeAxis(ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
        }
        // plot is likely registered as a listener with the existing axis...
        ValueAxis existing = getRangeAxis();
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.rangeAxes.put(0, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        fireChangeEvent();
    }

    /**
     * Returns the location of the primary range axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #setRangeAxisLocation(AxisLocation)
     */
    public AxisLocation getRangeAxisLocation() {
        return this.rangeAxisLocations.get(0);
    }

    /**
     * Sets the location of the primary range axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getRangeAxisLocation()
     */
    public void setRangeAxisLocation(AxisLocation location) {
        // delegate...
        setRangeAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the primary range axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getRangeAxisLocation()
     */
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setRangeAxisLocation(0, location, notify);
    }

    /**
     * Returns the edge for the primary range axis.
     *
     * @return The range axis edge.
     *
     * @see #getRangeAxisLocation()
     * @see #getOrientation()
     */
    public RectangleEdge getRangeAxisEdge() {
        return Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
    }

    /**
     * Returns the range axis with the specified index, or {@code null} if
     * there is no axis with that index.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setRangeAxis(int, ValueAxis)
     */
    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = this.rangeAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> xy = (XYPlot<S>) parent;
                result = xy.getRangeAxis(index);
            }
        }
        return result;
    }

    /**
     * Returns a map containing the range axes that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the range axes that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, ValueAxis> getRangeAxes() {
        return Collections.unmodifiableMap(this.rangeAxes);
    }

    /**
     * Sets a range axis and sends a {@link PlotChangeEvent} to all registered
     * listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getRangeAxis(int)
     */
    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, true);
    }

    /**
     * Sets a range axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getRangeAxis(int)
     */
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getRangeAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.rangeAxes.put(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Sets the range axes for this plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param axes  the axes ({@code null} not permitted).
     *
     * @see #setDomainAxes(ValueAxis[])
     */
    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the number of range axes.
     *
     * @return The axis count.
     *
     * @see #getDomainAxisCount()
     */
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    /**
     * Clears the range axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @see #clearDomainAxes()
     */
    public void clearRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the range axes.
     *
     * @see #configureDomainAxes()
     */
    public void configureRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }

    /**
     * Returns the location for a range axis.  If this hasn't been set
     * explicitly, the method returns the location that is opposite to the
     * primary range axis location.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The location (never {@code null}).
     *
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = this.rangeAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getRangeAxisLocation());
        }
        return result;
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} permitted).
     *
     * @see #getRangeAxisLocation(int)
     */
    public void setRangeAxisLocation(int index, AxisLocation location) {
        // delegate...
        setRangeAxisLocation(index, location, true);
    }

    /**
     * Sets the axis location for a domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted for index 0).
     * @param notify  notify listeners?
     *
     * @see #getRangeAxisLocation(int)
     * @see #setDomainAxisLocation(int, AxisLocation, boolean)
     */
    public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
        if (index == 0 && location == null) {
            throw new IllegalArgumentException("Null 'location' for index 0 not permitted.");
        }
        this.rangeAxisLocations.put(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the edge for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     *
     * @see #getRangeAxisLocation(int)
     * @see #getOrientation()
     */
    public RectangleEdge getRangeAxisEdge(int index) {
        AxisLocation location = getRangeAxisLocation(index);
        return Plot.resolveRangeAxisLocation(location, this.orientation);
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly {@code null}).
     *
     * @see #getDataset(int)
     * @see #setDataset(XYDataset)
     */
    public XYDataset<S> getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset with the specified index, or {@code null} if there
     * is no dataset with that index.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(int, XYDataset)
     */
    public XYDataset<S> getDataset(int index) {
        return this.datasets.get(index);
    }

    /**
     * Returns a map containing the datasets that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the datasets that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, XYDataset<S>> getDatasets() {
        return Collections.unmodifiableMap(this.datasets);
    }

    /**
     * Sets the primary dataset for the plot, replacing the existing dataset if
     * there is one.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     * @see #setDataset(int, XYDataset)
     */
    public void setDataset(XYDataset<S> dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot and sends a change event to all registered
     * listeners.
     *
     * @param index  the dataset index (must be &gt;= 0).
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset(int)
     */
    public void setDataset(int index, XYDataset<S> dataset) {
        XYDataset<S> existing = getDataset(index);
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
     * @return The index or -1.
     */
    public int indexOf(XYDataset<S> dataset) {
        for (Map.Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            if (dataset == entry.getValue()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Maps a dataset to a particular domain axis.  All data will be plotted
     * against axis zero by default, no mapping is required for this case.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index.
     *
     * @see #mapDatasetToRangeAxis(int, int)
     */
    public void mapDatasetToDomainAxis(int index, int axisIndex) {
        List<Integer> axisIndices = new ArrayList<>(1);
        axisIndices.add(axisIndex);
        mapDatasetToDomainAxes(index, axisIndices);
    }

    /**
     * Maps the specified dataset to the axes in the list.  Note that the
     * conversion of data values into Java2D space is always performed using
     * the first axis in the list.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndices  the axis indices ({@code null} permitted).
     */
    public void mapDatasetToDomainAxes(int index, List<Integer> axisIndices) {
        Args.requireNonNegative(index, "index");
        checkAxisIndices(axisIndices);
        this.datasetToDomainAxesMap.put(index, new ArrayList<>(axisIndices));
        // fake a dataset change event to update axes...
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    /**
     * Maps a dataset to a particular range axis.  All data will be plotted
     * against axis zero by default, no mapping is required for this case.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index.
     *
     * @see #mapDatasetToDomainAxis(int, int)
     */
    public void mapDatasetToRangeAxis(int index, int axisIndex) {
        List<Integer> axisIndices = new ArrayList<>(1);
        axisIndices.add(axisIndex);
        mapDatasetToRangeAxes(index, axisIndices);
    }

    /**
     * Maps the specified dataset to the axes in the list.  Note that the
     * conversion of data values into Java2D space is always performed using
     * the first axis in the list.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndices  the axis indices ({@code null} permitted).
     */
    public void mapDatasetToRangeAxes(int index, List<Integer> axisIndices) {
        Args.requireNonNegative(index, "index");
        checkAxisIndices(axisIndices);
        this.datasetToRangeAxesMap.put(index, new ArrayList<>(axisIndices));
        // fake a dataset change event to update axes...
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    /**
     * This method is used to perform argument checking on the list of
     * axis indices passed to mapDatasetToDomainAxes() and
     * mapDatasetToRangeAxes().
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
        int count = indices.size();
        if (count == 0) {
            throw new IllegalArgumentException("Empty list not permitted.");
        }
        Set<Integer> set = new HashSet<>();
        for (Integer item : indices) {
            if (set.contains(item)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            set.add(item);
        }
    }

    /**
     * Returns the number of renderer slots for this plot.
     *
     * @return The number of renderer slots.
     */
    public int getRendererCount() {
        return this.renderers.size();
    }

    /**
     * Returns the renderer for the primary dataset.
     *
     * @return The item renderer (possibly {@code null}).
     *
     * @see #setRenderer(XYItemRenderer)
     */
    public XYItemRenderer getRenderer() {
        return getRenderer(0);
    }

    /**
     * Returns the renderer with the specified index, or {@code null}.
     *
     * @param index  the renderer index (must be &gt;= 0).
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(int, XYItemRenderer)
     */
    public XYItemRenderer getRenderer(int index) {
        return this.renderers.get(index);
    }

    /**
     * Returns a map containing the renderers that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the renderers that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, XYItemRenderer> getRenderers() {
        return Collections.unmodifiableMap(this.renderers);
    }

    /**
     * Sets the renderer for the primary dataset and sends a change event to
     * all registered listeners.  If the renderer is set to {@code null},
     * no data will be displayed.
     *
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @see #getRenderer()
     */
    public void setRenderer(XYItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    /**
     * Sets the renderer for the dataset with the specified index and sends a
     * change event to all registered listeners.  Note that each dataset should
     * have its own renderer, you should not use one renderer for multiple
     * datasets.
     *
     * @param index  the index (must be &gt;= 0).
     * @param renderer  the renderer.
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, XYItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    /**
     * Sets the renderer for the dataset with the specified index and, if
     * requested, sends a change event to all registered listeners.  Note that
     * each dataset should have its own renderer, you should not use one
     * renderer for multiple datasets.
     *
     * @param index  the index (must be &gt;= 0).
     * @param renderer  the renderer.
     * @param notify  notify listeners?
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, XYItemRenderer renderer, boolean notify) {
        XYItemRenderer existing = getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.put(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        configureDomainAxes();
        configureRangeAxes();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Sets the renderers for this plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param renderers  the renderers ({@code null} not permitted).
     */
    public void setRenderers(XYItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the dataset rendering order.
     *
     * @return The order (never {@code null}).
     *
     * @see #setDatasetRenderingOrder(DatasetRenderingOrder)
     */
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.datasetRenderingOrder;
    }

    /**
     * Sets the rendering order and sends a {@link PlotChangeEvent} to all
     * registered listeners.  By default, the plot renders the primary dataset
     * last (so that the primary dataset overlays the secondary datasets).
     * You can reverse this if you want to.
     *
     * @param order  the rendering order ({@code null} not permitted).
     *
     * @see #getDatasetRenderingOrder()
     */
    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        Args.nullNotPermitted(order, "order");
        this.datasetRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the series rendering order.
     *
     * @return the order (never {@code null}).
     *
     * @see #setSeriesRenderingOrder(SeriesRenderingOrder)
     */
    public SeriesRenderingOrder getSeriesRenderingOrder() {
        return this.seriesRenderingOrder;
    }

    /**
     * Sets the series order and sends a {@link PlotChangeEvent} to all
     * registered listeners.  By default, the plot renders the primary series
     * last (so that the primary series appears to be on top).
     * You can reverse this if you want to.
     *
     * @param order  the rendering order ({@code null} not permitted).
     *
     * @see #getSeriesRenderingOrder()
     */
    public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
        Args.nullNotPermitted(order, "order");
        this.seriesRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the index of the specified renderer, or {@code -1} if the
     * renderer is not assigned to this plot.
     *
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @return The renderer index.
     */
    public int getIndexOf(XYItemRenderer renderer) {
        for (Map.Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() == renderer) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the renderer for the specified dataset (this is either the
     * renderer with the same index as the dataset or, if there isn't a
     * renderer with the same index, the default renderer).  If the dataset
     * does not belong to the plot, this method will return {@code null}.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The renderer (possibly {@code null}).
     */
    public XYItemRenderer getRendererForDataset(XYDataset<S> dataset) {
        int datasetIndex = indexOf(dataset);
        if (datasetIndex < 0) {
            return null;
        }
        XYItemRenderer result = this.renderers.get(datasetIndex);
        if (result == null) {
            result = getRenderer();
        }
        return result;
    }

    /**
     * Returns the weight for this plot when it is used as a subplot within a
     * combined plot.
     *
     * @return The weight.
     *
     * @see #setWeight(int)
     */
    public int getWeight() {
        return this.weight;
    }

    /**
     * Sets the weight for the plot and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param weight  the weight.
     *
     * @see #getWeight()
     */
    public void setWeight(int weight) {
        this.weight = weight;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the domain gridlines are visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setDomainGridlinesVisible(boolean)
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the domain grid-lines are
     * visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isDomainGridlinesVisible()
     */
    public void setDomainGridlinesVisible(boolean visible) {
        if (this.domainGridlinesVisible != visible) {
            this.domainGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns {@code true} if the domain minor gridlines are visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setDomainMinorGridlinesVisible(boolean)
     */
    public boolean isDomainMinorGridlinesVisible() {
        return this.domainMinorGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the domain minor grid-lines
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isDomainMinorGridlinesVisible()
     */
    public void setDomainMinorGridlinesVisible(boolean visible) {
        if (this.domainMinorGridlinesVisible != visible) {
            this.domainMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid-lines (if any) plotted against the
     * domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainGridlineStroke(Stroke)
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the domain axis, and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDomainGridlineStroke()
     */
    public void setDomainGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the stroke for the minor grid-lines (if any) plotted against the
     * domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainMinorGridlineStroke(Stroke)
     */
    public Stroke getDomainMinorGridlineStroke() {
        return this.domainMinorGridlineStroke;
    }

    /**
     * Sets the stroke for the minor grid lines plotted against the domain
     * axis, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDomainMinorGridlineStroke()
     */
    public void setDomainMinorGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the domain
     * axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainGridlinePaint(Paint)
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the domain axis, and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainGridlinePaint()
     */
    public void setDomainGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the minor grid lines (if any) plotted against the
     * domain axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainMinorGridlinePaint(Paint)
     */
    public Paint getDomainMinorGridlinePaint() {
        return this.domainMinorGridlinePaint;
    }

    /**
     * Sets the paint for the minor grid lines plotted against the domain axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainMinorGridlinePaint()
     */
    public void setDomainMinorGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainMinorGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the range axis grid is visible, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangeGridlinesVisible(boolean)
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the range axis grid lines
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isRangeGridlinesVisible()
     */
    public void setRangeGridlinesVisible(boolean visible) {
        if (this.rangeGridlinesVisible != visible) {
            this.rangeGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid lines (if any) plotted against the
     * range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeGridlineStroke(Stroke)
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the range axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeGridlineStroke()
     */
    public void setRangeGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.rangeGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the range
     * axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeGridlinePaint(Paint)
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeGridlinePaint()
     */
    public void setRangeGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the range axis minor grid is visible, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangeMinorGridlinesVisible(boolean)
     */
    public boolean isRangeMinorGridlinesVisible() {
        return this.rangeMinorGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the range axis minor grid
     * lines are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isRangeMinorGridlinesVisible()
     */
    public void setRangeMinorGridlinesVisible(boolean visible) {
        if (this.rangeMinorGridlinesVisible != visible) {
            this.rangeMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the minor grid lines (if any) plotted against the
     * range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeMinorGridlineStroke(Stroke)
     */
    public Stroke getRangeMinorGridlineStroke() {
        return this.rangeMinorGridlineStroke;
    }

    /**
     * Sets the stroke for the minor grid lines plotted against the range axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeMinorGridlineStroke()
     */
    public void setRangeMinorGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.rangeMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the minor grid lines (if any) plotted against the
     * range axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeMinorGridlinePaint(Paint)
     */
    public Paint getRangeMinorGridlinePaint() {
        return this.rangeMinorGridlinePaint;
    }

    /**
     * Sets the paint for the minor grid lines plotted against the range axis
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeMinorGridlinePaint()
     */
    public void setRangeMinorGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeMinorGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns a flag that controls whether a zero baseline is
     * displayed for the domain axis.
     *
     * @return A boolean.
     *
     * @see #setDomainZeroBaselineVisible(boolean)
     */
    public boolean isDomainZeroBaselineVisible() {
        return this.domainZeroBaselineVisible;
    }

    /**
     * Sets the flag that controls whether the zero baseline is
     * displayed for the domain axis, and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #isDomainZeroBaselineVisible()
     */
    public void setDomainZeroBaselineVisible(boolean visible) {
        this.domainZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used for the zero baseline against the domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainZeroBaselineStroke(Stroke)
     */
    public Stroke getDomainZeroBaselineStroke() {
        return this.domainZeroBaselineStroke;
    }

    /**
     * Sets the stroke for the zero baseline for the domain axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeZeroBaselineStroke()
     */
    public void setDomainZeroBaselineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the zero baseline (if any) plotted against the
     * domain axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainZeroBaselinePaint(Paint)
     */
    public Paint getDomainZeroBaselinePaint() {
        return this.domainZeroBaselinePaint;
    }

    /**
     * Sets the paint for the zero baseline plotted against the domain axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainZeroBaselinePaint()
     */
    public void setDomainZeroBaselinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainZeroBaselinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns a flag that controls whether a zero baseline is
     * displayed for the range axis.
     *
     * @return A boolean.
     *
     * @see #setRangeZeroBaselineVisible(boolean)
     */
    public boolean isRangeZeroBaselineVisible() {
        return this.rangeZeroBaselineVisible;
    }

    /**
     * Sets the flag that controls whether the zero baseline is
     * displayed for the range axis, and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #isRangeZeroBaselineVisible()
     */
    public void setRangeZeroBaselineVisible(boolean visible) {
        this.rangeZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used for the zero baseline against the range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeZeroBaselineStroke(Stroke)
     */
    public Stroke getRangeZeroBaselineStroke() {
        return this.rangeZeroBaselineStroke;
    }

    /**
     * Sets the stroke for the zero baseline for the range axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeZeroBaselineStroke()
     */
    public void setRangeZeroBaselineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.rangeZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the zero baseline (if any) plotted against the
     * range axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeZeroBaselinePaint(Paint)
     */
    public Paint getRangeZeroBaselinePaint() {
        return this.rangeZeroBaselinePaint;
    }

    /**
     * Sets the paint for the zero baseline plotted against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeZeroBaselinePaint()
     */
    public void setRangeZeroBaselinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeZeroBaselinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the paint used for the domain tick bands.  If this is
     * {@code null}, no tick bands will be drawn.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setDomainTickBandPaint(Paint)
     */
    public Paint getDomainTickBandPaint() {
        return this.domainTickBandPaint;
    }

    /**
     * Sets the paint for the domain tick bands.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getDomainTickBandPaint()
     */
    public void setDomainTickBandPaint(Paint paint) {
        this.domainTickBandPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the paint used for the range tick bands.  If this is
     * {@code null}, no tick bands will be drawn.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setRangeTickBandPaint(Paint)
     */
    public Paint getRangeTickBandPaint() {
        return this.rangeTickBandPaint;
    }

    /**
     * Sets the paint for the range tick bands.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getRangeTickBandPaint()
     */
    public void setRangeTickBandPaint(Paint paint) {
        this.rangeTickBandPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the origin for the quadrants that can be displayed on the plot.
     * This defaults to (0, 0).
     *
     * @return The origin point (never {@code null}).
     *
     * @see #setQuadrantOrigin(Point2D)
     */
    public Point2D getQuadrantOrigin() {
        return this.quadrantOrigin;
    }

    /**
     * Sets the quadrant origin and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param origin  the origin ({@code null} not permitted).
     *
     * @see #getQuadrantOrigin()
     */
    public void setQuadrantOrigin(Point2D origin) {
        Args.nullNotPermitted(origin, "origin");
        this.quadrantOrigin = origin;
        fireChangeEvent();
    }

    /**
     * Returns the paint used for the specified quadrant.
     *
     * @param index  the quadrant index (0-3).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setQuadrantPaint(int, Paint)
     */
    public Paint getQuadrantPaint(int index) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        return this.quadrantPaint[index];
    }

    /**
     * Sets the paint used for the specified quadrant and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the quadrant index (0-3).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getQuadrantPaint(int)
     */
    public void setQuadrantPaint(int index, Paint paint) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        this.quadrantPaint[index] = paint;
        fireChangeEvent();
    }

    /**
     * Adds a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #addDomainMarker(Marker, Layer)
     * @see #clearDomainMarkers()
     */
    public void addDomainMarker(Marker marker) {
        // defer argument checking...
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background).
     *
     * @see #addDomainMarker(int, Marker, Layer)
     */
    public void addDomainMarker(Marker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
    }

    /**
     * Clears all the (foreground and background) domain markers and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #addDomainMarker(int, Marker, Layer)
     */
    public void clearDomainMarkers() {
        if (this.backgroundDomainMarkers != null) {
            Set<Integer> keys = this.backgroundDomainMarkers.keySet();
            for (Integer key : keys) {
                clearDomainMarkers(key);
            }
            this.backgroundDomainMarkers.clear();
        }
        if (this.foregroundDomainMarkers != null) {
            Set<Integer> keys = this.foregroundDomainMarkers.keySet();
            for (Integer key : keys) {
                clearDomainMarkers(key);
            }
            this.foregroundDomainMarkers.clear();
        }
        fireChangeEvent();
    }

    /**
     * Clears the (foreground and background) domain markers for a particular
     * renderer and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the renderer index.
     *
     * @see #clearRangeMarkers(int)
     */
    public void clearDomainMarkers(int index) {
        if (this.backgroundDomainMarkers != null) {
            List<Marker> markers = this.backgroundDomainMarkers.get(index);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            List<Marker> markers = this.foregroundDomainMarkers.get(index);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    /**
     * Adds a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis (that the renderer is mapped to), however this is
     * entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     *
     * @see #clearDomainMarkers(int)
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public void addDomainMarker(int index, Marker marker, Layer layer) {
        addDomainMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for a specific dataset/renderer and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis (that the renderer is mapped to), however this is
     * entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners?
     */
    public void addDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        if (layer == Layer.FOREGROUND) {
            List<Marker> markers = this.foregroundDomainMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            List<Marker> markers = this.backgroundDomainMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Removes a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker  the marker.
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(Marker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(Marker marker, Layer layer) {
        return removeDomainMarker(0, marker, layer);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker.
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        List<Marker> markers;
        if (layer == Layer.FOREGROUND) {
            markers = this.foregroundDomainMarkers.get(index);
        } else {
            markers = this.backgroundDomainMarkers.get(index);
        }
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Adds a marker for the range axis and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #addRangeMarker(Marker, Layer)
     */
    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for the range axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background).
     *
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    /**
     * Clears all the range markers and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @see #clearRangeMarkers()
     */
    public void clearRangeMarkers() {
        if (this.backgroundRangeMarkers != null) {
            Set<Integer> keys = this.backgroundRangeMarkers.keySet();
            for (Integer key : keys) {
                clearRangeMarkers(key);
            }
            this.backgroundRangeMarkers.clear();
        }
        if (this.foregroundRangeMarkers != null) {
            Set<Integer> keys = this.foregroundRangeMarkers.keySet();
            for (Integer key : keys) {
                clearRangeMarkers(key);
            }
            this.foregroundRangeMarkers.clear();
        }
        fireChangeEvent();
    }

    /**
     * Adds a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     *
     * @see #clearRangeMarkers(int)
     * @see #addDomainMarker(int, Marker, Layer)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for a specific dataset/renderer and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners?
     */
    public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        if (layer == Layer.FOREGROUND) {
            List<Marker> markers = this.foregroundRangeMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            List<Marker> markers = this.backgroundRangeMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the (foreground and background) range markers for a particular
     * renderer.
     *
     * @param index  the renderer index.
     */
    public void clearRangeMarkers(int index) {
        if (this.backgroundRangeMarkers != null) {
            List<Marker> markers = this.backgroundRangeMarkers.get(index);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            List<Marker> markers = this.foregroundRangeMarkers.get(index);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        fireChangeEvent();
    }

    /**
     * Removes a marker for the range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker the marker.
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeRangeMarker(Marker marker) {
        return removeRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the range axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        List<Marker> markers;
        if (layer == Layer.FOREGROUND) {
            markers = this.foregroundRangeMarkers.get(index);
        } else {
            markers = this.backgroundRangeMarkers.get(index);
        }
        if (markers == null) {
            return false;
        }
        boolean removed = markers.remove(marker);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Adds an annotation to the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     *
     * @see #getAnnotations()
     * @see #removeAnnotation(XYAnnotation)
     */
    public void addAnnotation(XYAnnotation annotation) {
        addAnnotation(annotation, true);
    }

    /**
     * Adds an annotation to the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void addAnnotation(XYAnnotation annotation, boolean notify) {
        Args.nullNotPermitted(annotation, "annotation");
        this.annotations.add(annotation);
        annotation.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Removes an annotation from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     *
     * @return A boolean (indicates whether the annotation was removed).
     *
     * @see #addAnnotation(XYAnnotation)
     * @see #getAnnotations()
     */
    public boolean removeAnnotation(XYAnnotation annotation) {
        return removeAnnotation(annotation, true);
    }

    /**
     * Removes an annotation from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @return A boolean (indicates whether the annotation was removed).
     */
    public boolean removeAnnotation(XYAnnotation annotation, boolean notify) {
        Args.nullNotPermitted(annotation, "annotation");
        boolean removed = this.annotations.remove(annotation);
        annotation.removeChangeListener(this);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Returns the list of annotations.
     *
     * @return The list of annotations.
     *
     * @see #addAnnotation(XYAnnotation)
     */
    public List<XYAnnotation> getAnnotations() {
        return new ArrayList<>(this.annotations);
    }

    /**
     * Clears all the annotations and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @see #addAnnotation(XYAnnotation)
     */
    public void clearAnnotations() {
        for (XYAnnotation annotation : this.annotations) {
            annotation.removeChangeListener(this);
        }
        this.annotations.clear();
        fireChangeEvent();
    }

    /**
     * Returns the shadow generator for the plot, if any.
     *
     * @return The shadow generator (possibly {@code null}).
     */
    public ShadowGenerator getShadowGenerator() {
        return this.shadowGenerator;
    }

    /**
     * Sets the shadow generator for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     */
    public void setShadowGenerator(ShadowGenerator generator) {
        this.shadowGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Calculates the space required for all the axes in the plot.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     *
     * @return The required space.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = calculateRangeAxisSpace(g2, plotArea, space);
        Rectangle2D revPlotArea = space.shrink(plotArea, null);
        space = calculateDomainAxisSpace(g2, revPlotArea, space);
        return space;
    }

    /**
     * Calculates the space required for the domain axis/axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param space  a carrier for the result ({@code null} permitted).
     *
     * @return The required space.
     */
    protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        // reserve some space for the domain axis...
        if (this.fixedDomainAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
        } else {
            // reserve space for the domain axes...
            for (ValueAxis axis : this.domainAxes.values()) {
                if (axis != null) {
                    RectangleEdge edge = getDomainAxisEdge(findDomainAxisIndex(axis));
                    space = axis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }
        return space;
    }

    /**
     * Calculates the space required for the range axis/axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param space  a carrier for the result ({@code null} permitted).
     *
     * @return The required space.
     */
    protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
        if (space == null) {
            space = new AxisSpace();
        }
        // reserve some space for the range axis...
        if (this.fixedRangeAxisSpace != null) {
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
        } else {
            // reserve space for the range axes...
            for (ValueAxis axis : this.rangeAxes.values()) {
                if (axis != null) {
                    RectangleEdge edge = getRangeAxisEdge(findRangeAxisIndex(axis));
                    space = axis.reserveSpace(g2, this, plotArea, edge, space);
                }
            }
        }
        return space;
    }

    /**
     * Trims a rectangle to integer coordinates.
     *
     * @param rect  the incoming rectangle.
     *
     * @return A rectangle with integer coordinates.
     */
    private Rectangle integerise(Rectangle2D rect) {
        int x0 = (int) Math.ceil(rect.getMinX());
        int y0 = (int) Math.ceil(rect.getMinY());
        int x1 = (int) Math.floor(rect.getMaxX());
        int y1 = (int) Math.floor(rect.getMaxY());
        return new Rectangle(x0, y0, (x1 - x0), (y1 - y0));
    }

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        for (Entry<Integer, ValueAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // visit the renderers
        for (Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        visitor.visit(this);
    }

    /**
     * Draws the plot within the specified area on a graphics device.
     *
     * @param g2  the graphics device.
     * @param area  the plot area (in Java2D space).
     * @param anchor  an anchor point in Java2D space ({@code null}
     *                permitted).
     * @param parentState  the state from the parent plot, if there is one
     *                     ({@code null} permitted).
     * @param info  collects chart drawing information ({@code null}
     *              permitted).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // if the plot area is too small, just return...
        if ((area.getWidth() <= MINIMUM_WIDTH_TO_DRAW) || (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW)) {
            return;
        }
        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        dataArea = integerise(dataArea);
        if (dataArea.isEmpty()) {
            return;
        }
        createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }
        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        Map<Axis, AxisState> axisStateMap = drawAxes(g2, area, dataArea, info);
        PlotOrientation orient = getOrientation();
        // the anchor point is typically the point where the mouse last
        // clicked - the crosshairs will be driven off this point...
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);
        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                double x;
                if (orient == PlotOrientation.VERTICAL) {
                    x = domainAxis.java2DToValue(anchor.getX(), dataArea, getDomainAxisEdge());
                } else {
                    x = domainAxis.java2DToValue(anchor.getY(), dataArea, getDomainAxisEdge());
                }
                crosshairState.setAnchorX(x);
            }
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (orient == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
                } else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setCrosshairX(getDomainCrosshairValue());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        AxisState domainAxisState = axisStateMap.get(getDomainAxis());
        if (domainAxisState == null) {
            if (parentState != null) {
                domainAxisState = parentState.getSharedAxisStates().get(getDomainAxis());
            }
        }
        AxisState rangeAxisState = axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = parentState.getSharedAxisStates().get(getRangeAxis());
            }
        }
        if (domainAxisState != null) {
            drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
        }
        if (rangeAxisState != null) {
            drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
        }
        if (domainAxisState != null) {
            drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            drawZeroDomainBaseline(g2, dataArea);
        }
        if (rangeAxisState != null) {
            drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
            drawZeroRangeBaseline(g2, dataArea);
        }
        Graphics2D savedG2 = g2;
        BufferedImage dataImage = null;
        boolean suppressShadow = Boolean.TRUE.equals(g2.getRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION));
        if (this.shadowGenerator != null && !suppressShadow) {
            dataImage = new BufferedImage((int) dataArea.getWidth(), (int) dataArea.getHeight(), BufferedImage.TYPE_INT_ARGB);
            g2 = dataImage.createGraphics();
            g2.translate(-dataArea.getX(), -dataArea.getY());
            g2.setRenderingHints(savedG2.getRenderingHints());
        }
        // draw the markers that are associated with a specific dataset...
        for (XYDataset<S> dataset : this.datasets.values()) {
            int datasetIndex = indexOf(dataset);
            drawDomainMarkers(g2, dataArea, datasetIndex, Layer.BACKGROUND);
        }
        for (XYDataset<S> dataset : this.datasets.values()) {
            int datasetIndex = indexOf(dataset);
            drawRangeMarkers(g2, dataArea, datasetIndex, Layer.BACKGROUND);
        }
        // now draw annotations and render data items...
        boolean foundData = false;
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        List<Integer> rendererIndices = getRendererIndices(order);
        List<Integer> datasetIndices = getDatasetIndices(order);
        // draw background annotations
        for (int i : rendererIndices) {
            XYItemRenderer renderer = getRenderer(i);
            if (renderer != null) {
                ValueAxis domainAxis = getDomainAxisForDataset(i);
                ValueAxis rangeAxis = getRangeAxisForDataset(i);
                renderer.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, info);
            }
        }
        // render data items...
        for (int datasetIndex : datasetIndices) {
            foundData = render(g2, dataArea, datasetIndex, info, crosshairState) || foundData;
        }
        // draw foreground annotations
        for (int i : rendererIndices) {
            XYItemRenderer renderer = getRenderer(i);
            if (renderer != null) {
                ValueAxis domainAxis = getDomainAxisForDataset(i);
                ValueAxis rangeAxis = getRangeAxisForDataset(i);
                renderer.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, info);
            }
        }
        // draw domain crosshair if required...
        int datasetIndex = crosshairState.getDatasetIndex();
        ValueAxis xAxis = getDomainAxisForDataset(datasetIndex);
        RectangleEdge xAxisEdge = getDomainAxisEdge(getDomainAxisIndex(xAxis));
        if (!this.domainCrosshairLockedOnData && anchor != null) {
            double xx;
            if (orient == PlotOrientation.VERTICAL) {
                xx = xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge);
            } else {
                xx = xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
            }
            crosshairState.setCrosshairX(xx);
        }
        setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
        if (isDomainCrosshairVisible()) {
            double x = getDomainCrosshairValue();
            Paint paint = getDomainCrosshairPaint();
            Stroke stroke = getDomainCrosshairStroke();
            drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
        }
        // draw range crosshair if required...
        ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
        RectangleEdge yAxisEdge = getRangeAxisEdge(getRangeAxisIndex(yAxis));
        if (!this.rangeCrosshairLockedOnData && anchor != null) {
            double yy;
            if (orient == PlotOrientation.VERTICAL) {
                yy = yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge);
            } else {
                yy = yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
            }
            crosshairState.setCrosshairY(yy);
        }
        setRangeCrosshairValue(crosshairState.getCrosshairY(), false);
        if (isRangeCrosshairVisible()) {
            double y = getRangeCrosshairValue();
            Paint paint = getRangeCrosshairPaint();
            Stroke stroke = getRangeCrosshairStroke();
            drawRangeCrosshair(g2, dataArea, orient, y, yAxis, stroke, paint);
        }
        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }
        for (int i : rendererIndices) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i : rendererIndices) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        drawAnnotations(g2, dataArea, info);
        if (this.shadowGenerator != null && !suppressShadow) {
            BufferedImage shadowImage = this.shadowGenerator.createDropShadow(dataImage);
            g2 = savedG2;
            g2.drawImage(shadowImage, (int) dataArea.getX() + this.shadowGenerator.calculateOffsetX(), (int) dataArea.getY() + this.shadowGenerator.calculateOffsetY(), null);
            g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
        }
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);
    }

    /**
     * Returns the indices of the non-null datasets in the specified order.
     *
     * @param order  the order ({@code null} not permitted).
     *
     * @return The list of indices.
     */
    private List<Integer> getDatasetIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<>();
        for (Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            if (entry.getValue() != null) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);
        if (order == DatasetRenderingOrder.REVERSE) {
            Collections.reverse(result);
        }
        return result;
    }

    private List<Integer> getRendererIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<>();
        for (Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() != null) {
                result.add(entry.getKey());
            }
        }
        Collections.sort(result);
        if (order == DatasetRenderingOrder.REVERSE) {
            Collections.reverse(result);
        }
        return result;
    }

    /**
     * Draws the background for the plot.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     */
    @Override
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, this.orientation);
        drawQuadrants(g2, area);
        drawBackgroundImage(g2, area);
    }

    /**
     * Draws the quadrants.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     *
     * @see #setQuadrantOrigin(Point2D)
     * @see #setQuadrantPaint(int, Paint)
     */
    protected void drawQuadrants(Graphics2D g2, Rectangle2D area) {
        //  0 | 1
        //  --+--
        //  2 | 3
        boolean somethingToDraw = false;
        ValueAxis xAxis = getDomainAxis();
        if (xAxis == null) {
            // we can't draw quadrants without a valid x-axis
            return;
        }
        double x = xAxis.getRange().constrain(this.quadrantOrigin.getX());
        double xx = xAxis.valueToJava2D(x, area, getDomainAxisEdge());
        ValueAxis yAxis = getRangeAxis();
        if (yAxis == null) {
            // we can't draw quadrants without a valid y-axis
            return;
        }
        double y = yAxis.getRange().constrain(this.quadrantOrigin.getY());
        double yy = yAxis.valueToJava2D(y, area, getRangeAxisEdge());
        double xmin = xAxis.getLowerBound();
        double xxmin = xAxis.valueToJava2D(xmin, area, getDomainAxisEdge());
        double xmax = xAxis.getUpperBound();
        double xxmax = xAxis.valueToJava2D(xmax, area, getDomainAxisEdge());
        double ymin = yAxis.getLowerBound();
        double yymin = yAxis.valueToJava2D(ymin, area, getRangeAxisEdge());
        double ymax = yAxis.getUpperBound();
        double yymax = yAxis.valueToJava2D(ymax, area, getRangeAxisEdge());
        Rectangle2D[] r = new Rectangle2D[] { null, null, null, null };
        if (this.quadrantPaint[0] != null) {
            if (x > xmin && y < ymax) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[0] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmin, xx), Math.abs(yy - yymax), Math.abs(xx - xxmin));
                } else {
                    // PlotOrientation.VERTICAL
                    r[0] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymax, yy), Math.abs(xx - xxmin), Math.abs(yy - yymax));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[1] != null) {
            if (x < xmax && y < ymax) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[1] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmax, xx), Math.abs(yy - yymax), Math.abs(xx - xxmax));
                } else {
                    // PlotOrientation.VERTICAL
                    r[1] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymax, yy), Math.abs(xx - xxmax), Math.abs(yy - yymax));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[2] != null) {
            if (x > xmin && y > ymin) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[2] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmin, xx), Math.abs(yy - yymin), Math.abs(xx - xxmin));
                } else {
                    // PlotOrientation.VERTICAL
                    r[2] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymin, yy), Math.abs(xx - xxmin), Math.abs(yy - yymin));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[3] != null) {
            if (x < xmax && y > ymin) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[3] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmax, xx), Math.abs(yy - yymin), Math.abs(xx - xxmax));
                } else {
                    // PlotOrientation.VERTICAL
                    r[3] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymin, yy), Math.abs(xx - xxmax), Math.abs(yy - yymin));
                }
                somethingToDraw = true;
            }
        }
        if (somethingToDraw) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getBackgroundAlpha()));
            for (int i = 0; i < 4; i++) {
                if (this.quadrantPaint[i] != null && r[i] != null) {
                    g2.setPaint(this.quadrantPaint[i]);
                    g2.fill(r[i]);
                }
            }
            g2.setComposite(originalComposite);
        }
    }

    /**
     * Draws the domain tick bands, if any.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #setDomainTickBandPaint(Paint)
     */
    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        Paint bandPaint = getDomainTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis xAxis = getDomainAxis();
            double previous = xAxis.getLowerBound();
            for (ValueTick tick : ticks) {
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = xAxis.getUpperBound();
            if (fillBand) {
                getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, end);
            }
        }
    }

    /**
     * Draws the range tick bands, if any.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #setRangeTickBandPaint(Paint)
     */
    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        Paint bandPaint = getRangeTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis axis = getRangeAxis();
            double previous = axis.getLowerBound();
            for (ValueTick tick : ticks) {
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = axis.getUpperBound();
            if (fillBand) {
                getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, end);
            }
        }
    }

    /**
     * A utility method for drawing the axes.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param plotArea  the plot area ({@code null} not permitted).
     * @param dataArea  the data area ({@code null} not permitted).
     * @param plotState  collects information about the plot ({@code null}
     *                   permitted).
     *
     * @return A map containing the state for each axis drawn.
     */
    protected Map<Axis, AxisState> drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisCollection axisCollection = new AxisCollection();
        // add domain axes to lists...
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                int axisIndex = findDomainAxisIndex(axis);
                axisCollection.add(axis, getDomainAxisEdge(axisIndex));
            }
        }
        // add range axes to lists...
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                int axisIndex = findRangeAxisIndex(axis);
                axisCollection.add(axis, getRangeAxisEdge(axisIndex));
            }
        }
        Map<Axis, AxisState> axisStateMap = new HashMap<>();
        // draw the top axes
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtTop()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtBottom()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtLeft()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtRight()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        return axisStateMap;
    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
     * <P>
     * The {@code info} and {@code crosshairState} arguments may be
     * {@code null}.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param index  the dataset index.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  collects crosshair information
     *                        ({@code null} permitted).
     *
     * @return A flag that indicates whether any data was actually rendered.
     */
    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState) {
        boolean foundData = false;
        XYDataset<S> dataset = getDataset(index);
        if (!DatasetUtils.isEmptyOrNull(dataset)) {
            foundData = true;
            ValueAxis xAxis = getDomainAxisForDataset(index);
            ValueAxis yAxis = getRangeAxisForDataset(index);
            if (xAxis == null || yAxis == null) {
                // can't render anything without axes
                return foundData;
            }
            XYItemRenderer renderer = getRenderer(index);
            if (renderer == null) {
                renderer = getRenderer();
                if (renderer == null) {
                    // no default renderer available
                    return foundData;
                }
            }
            XYItemRendererState state = renderer.initialise(g2, dataArea, this, dataset, info);
            int passCount = renderer.getPassCount();
            SeriesRenderingOrder seriesOrder = getSeriesRenderingOrder();
            if (seriesOrder == SeriesRenderingOrder.REVERSE) {
                //render series in reverse order
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = seriesCount - 1; series >= 0; series--) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (lastItem == -1) {
                            continue;
                        }
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtils.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            } else {
                //render series in forward order
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtils.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            }
        }
        return foundData;
    }

    /**
     * Returns the domain axis for a dataset.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The axis.
     */
    public ValueAxis getDomainAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        ValueAxis valueAxis;
        List<Integer> axisIndices = this.datasetToDomainAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            valueAxis = getDomainAxis(axisIndex);
        } else {
            valueAxis = getDomainAxis(0);
        }
        return valueAxis;
    }

    /**
     * Returns the range axis for a dataset.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The axis.
     */
    public ValueAxis getRangeAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        ValueAxis valueAxis;
        List<Integer> axisIndices = this.datasetToRangeAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            valueAxis = getRangeAxis(axisIndex);
        } else {
            valueAxis = getRangeAxis(0);
        }
        return valueAxis;
    }

    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #drawRangeGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        // no renderer, no gridlines...
        if (getRenderer() == null) {
            return;
        }
        // draw the domain grid lines, if any...
        if (isDomainGridlinesVisible() || isDomainMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            for (ValueTick tick : ticks) {
                boolean paintLine = false;
                if ((tick.getTickType() == TickType.MINOR) && isDomainMinorGridlinesVisible()) {
                    gridStroke = getDomainMinorGridlineStroke();
                    gridPaint = getDomainMinorGridlinePaint();
                    paintLine = true;
                } else if ((tick.getTickType() == TickType.MAJOR) && isDomainGridlinesVisible()) {
                    gridStroke = getDomainGridlineStroke();
                    gridPaint = getDomainGridlinePaint();
                    paintLine = true;
                }
                XYItemRenderer r = getRenderer();
                if ((r instanceof AbstractXYItemRenderer) && paintLine) {
                    r.drawDomainLine(g2, this, getDomainAxis(), dataArea, tick.getValue(), gridPaint, gridStroke);
                }
            }
        }
    }

    /**
     * Draws the gridlines for the plot's primary range axis, if they are
     * visible.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     * @param ticks  the ticks.
     *
     * @see #drawDomainGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D area, List<ValueTick> ticks) {
        // no renderer, no gridlines...
        if (getRenderer() == null) {
            return;
        }
        // draw the range grid lines, if any...
        if (isRangeGridlinesVisible() || isRangeMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            ValueAxis axis = getRangeAxis();
            if (axis != null) {
                for (ValueTick tick : ticks) {
                    boolean paintLine = false;
                    if ((tick.getTickType() == TickType.MINOR) && isRangeMinorGridlinesVisible()) {
                        gridStroke = getRangeMinorGridlineStroke();
                        gridPaint = getRangeMinorGridlinePaint();
                        paintLine = true;
                    } else if ((tick.getTickType() == TickType.MAJOR) && isRangeGridlinesVisible()) {
                        gridStroke = getRangeGridlineStroke();
                        gridPaint = getRangeGridlinePaint();
                        paintLine = true;
                    }
                    if ((tick.getValue() != 0.0 || !isRangeZeroBaselineVisible()) && paintLine) {
                        getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, tick.getValue(), gridPaint, gridStroke);
                    }
                }
            }
        }
    }

    /**
     * Draws a baseline across the chart at value zero on the domain axis.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     *
     * @see #setDomainZeroBaselineVisible(boolean)
     */
    protected void drawZeroDomainBaseline(Graphics2D g2, Rectangle2D area) {
        if (isDomainZeroBaselineVisible() && getRenderer() != null) {
            getRenderer().drawDomainLine(g2, this, getDomainAxis(), area, 0.0, this.domainZeroBaselinePaint, this.domainZeroBaselineStroke);
        }
    }

    /**
     * Draws a baseline across the chart at value zero on the range axis.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     *
     * @see #setRangeZeroBaselineVisible(boolean)
     */
    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (isRangeZeroBaselineVisible()) {
            getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, 0.0, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
        }
    }

    /**
     * Draws the annotations for the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param info  the chart rendering info.
     */
    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        for (XYAnnotation annotation : this.annotations) {
            ValueAxis xAxis = getDomainAxis();
            ValueAxis yAxis = getRangeAxis();
            annotation.draw(g2, this, dataArea, xAxis, yAxis, 0, info);
        }
    }

    /**
     * Draws the domain markers (if any) for an axis and layer.  This method is
     * typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the dataset/renderer index.
     * @param layer  the layer (foreground or background).
     */
    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        // check that the renderer has a corresponding dataset (it doesn't
        // matter if the dataset is null)
        if (index >= getDatasetCount()) {
            return;
        }
        Collection<Marker> markers = getDomainMarkers(index, layer);
        ValueAxis axis = getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            for (Marker marker : markers) {
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    /**
     * Draws the range markers (if any) for a renderer and layer.  This method
     * is typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the renderer index.
     * @param layer  the layer (foreground or background).
     */
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        // check that the renderer has a corresponding dataset (it doesn't
        // matter if the dataset is null)
        if (index >= getDatasetCount()) {
            return;
        }
        Collection<Marker> markers = getRangeMarkers(index, layer);
        ValueAxis axis = getRangeAxisForDataset(index);
        if (markers != null && axis != null) {
            for (Marker marker : markers) {
                r.drawRangeMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    /**
     * Returns the list of domain markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of domain markers.
     *
     * @see #getRangeMarkers(Layer)
     */
    public Collection<Marker> getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    /**
     * Returns the list of range markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of range markers.
     *
     * @see #getDomainMarkers(Layer)
     */
    public Collection<Marker> getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
    }

    /**
     * Returns a collection of domain markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     *
     * @see #getRangeMarkers(int, Layer)
     */
    public Collection<Marker> getDomainMarkers(int index, Layer layer) {
        Collection<Marker> result = null;
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundDomainMarkers.get(index);
        } else if (layer == Layer.BACKGROUND) {
            result = this.backgroundDomainMarkers.get(index);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    /**
     * Returns a collection of range markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     *
     * @see #getDomainMarkers(int, Layer)
     */
    public Collection<Marker> getRangeMarkers(int index, Layer layer) {
        Collection<Marker> result = null;
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundRangeMarkers.get(index);
        } else if (layer == Layer.BACKGROUND) {
            result = this.backgroundRangeMarkers.get(index);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    /**
     * Utility method for drawing a horizontal line across the data area of the
     * plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param value  the coordinate, where to draw the line.
     * @param stroke  the stroke to use.
     * @param paint  the paint to use.
     */
    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = getRangeAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getDomainAxis();
        }
        if (axis.getRange().contains(value)) {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    /**
     * Draws a domain crosshair.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param orientation  the plot orientation.
     * @param value  the crosshair value.
     * @param axis  the axis against which the value is measured.
     * @param stroke  the stroke used to draw the crosshair line.
     * @param paint  the paint used to draw the crosshair line.
     */
    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (!axis.getRange().contains(value)) {
            return;
        }
        Line2D line;
        if (orientation == PlotOrientation.VERTICAL) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        } else {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        }
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Utility method for drawing a vertical line on the data area of the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param value  the coordinate, where to draw the line.
     * @param stroke  the stroke to use.
     * @param paint  the paint to use.
     */
    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = getDomainAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getRangeAxis();
        }
        if (axis.getRange().contains(value)) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    /**
     * Draws a range crosshair.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param orientation  the plot orientation.
     * @param value  the crosshair value.
     * @param axis  the axis against which the value is measured.
     * @param stroke  the stroke used to draw the crosshair line.
     * @param paint  the paint used to draw the crosshair line.
     */
    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (!axis.getRange().contains(value)) {
            return;
        }
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        Line2D line;
        if (orientation == PlotOrientation.HORIZONTAL) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        } else {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Handles a 'click' on the plot by updating the anchor values.
     *
     * @param x  the x-coordinate, where the click occurred, in Java2D space.
     * @param y  the y-coordinate, where the click occurred, in Java2D space.
     * @param info  object containing information about the plot dimensions.
     */
    @Override
    public void handleClick(int x, int y, PlotRenderingInfo info) {
        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            // set the anchor value for the horizontal axis...
            ValueAxis xaxis = getDomainAxis();
            if (xaxis != null) {
                double hvalue = xaxis.java2DToValue(x, info.getDataArea(), getDomainAxisEdge());
                setDomainCrosshairValue(hvalue);
            }
            // set the anchor value for the vertical axis...
            ValueAxis yaxis = getRangeAxis();
            if (yaxis != null) {
                double vvalue = yaxis.java2DToValue(y, info.getDataArea(), getRangeAxisEdge());
                setRangeCrosshairValue(vvalue);
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
    private List<XYDataset<S>> getDatasetsMappedToDomainAxis(Integer axisIndex) {
        Args.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset<S>> result = new ArrayList<>();
        for (Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            int index = entry.getKey();
            List<Integer> mappedAxes = this.datasetToDomainAxesMap.get(index);
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * A utility method that returns a list of datasets that are mapped to a
     * particular axis.
     *
     * @param axisIndex  the axis index ({@code null} not permitted).
     *
     * @return A list of datasets.
     */
    private List<XYDataset<S>> getDatasetsMappedToRangeAxis(Integer axisIndex) {
        Args.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset<S>> result = new ArrayList<>();
        for (Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            int index = entry.getKey();
            List<Integer> mappedAxes = this.datasetToRangeAxesMap.get(index);
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * Returns the index of the given domain axis.
     *
     * @param axis  the axis.
     *
     * @return The axis index.
     *
     * @see #getRangeAxisIndex(ValueAxis)
     */
    public int getDomainAxisIndex(ValueAxis axis) {
        int result = findDomainAxisIndex(axis);
        if (result < 0) {
            // try the parent plot
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> p = (XYPlot<S>) parent;
                result = p.getDomainAxisIndex(axis);
            }
        }
        return result;
    }

    private int findDomainAxisIndex(ValueAxis axis) {
        for (Map.Entry<Integer, ValueAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the index of the given range axis.
     *
     * @param axis  the axis.
     *
     * @return The axis index.
     *
     * @see #getDomainAxisIndex(ValueAxis)
     */
    public int getRangeAxisIndex(ValueAxis axis) {
        int result = findRangeAxisIndex(axis);
        if (result < 0) {
            // try the parent plot
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> p = (XYPlot<S>) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
        return result;
    }

    private int findRangeAxisIndex(ValueAxis axis) {
        for (Map.Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
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
        List<XYDataset<S>> mappedDatasets = new ArrayList<>();
        List<XYAnnotation> includedAnnotations = new ArrayList<>();
        boolean isDomainAxis = true;
        // is it a domain axis?
        int domainIndex = getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(getDatasetsMappedToDomainAxis(domainIndex));
            if (domainIndex == 0) {
                // grab the plot's annotations
                for (XYAnnotation annotation : this.annotations) {
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }
        // or is it a range axis?
        int rangeIndex = getRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            isDomainAxis = false;
            mappedDatasets.addAll(getDatasetsMappedToRangeAxis(rangeIndex));
            if (rangeIndex == 0) {
                for (XYAnnotation annotation : this.annotations) {
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }
        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
        for (XYDataset<S> d : mappedDatasets) {
            if (d != null) {
                XYItemRenderer r = getRendererForDataset(d);
                if (isDomainAxis) {
                    if (r != null) {
                        result = Range.combine(result, r.findDomainBounds(d));
                    } else {
                        result = Range.combine(result, DatasetUtils.findDomainBounds(d));
                    }
                } else {
                    if (r != null) {
                        result = Range.combine(result, r.findRangeBounds(d));
                    } else {
                        result = Range.combine(result, DatasetUtils.findRangeBounds(d));
                    }
                }
                if (r != null) {
                    for (XYAnnotation annotation : r.getAnnotations()) {
                        if (annotation instanceof XYAnnotationBoundsInfo) {
                            includedAnnotations.add(annotation);
                        }
                    }
                }
            }
        }
        for (XYAnnotation includedAnnotation : includedAnnotations) {
            XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo) includedAnnotation;
            if (xyabi.getIncludeInDataBounds()) {
                if (isDomainAxis) {
                    result = Range.combine(result, xyabi.getXRange());
                } else {
                    result = Range.combine(result, xyabi.getYRange());
                }
            }
        }
        return result;
    }

    /**
     * Receives notification of a change to an {@link Annotation} added to
     * this plot.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void annotationChanged(AnnotationChangeEvent event) {
        if (getParent() != null) {
            getParent().annotationChanged(event);
        } else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            notifyListeners(e);
        }
    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The axis ranges are updated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        configureDomainAxes();
        configureRangeAxes();
        if (getParent() != null) {
            getParent().datasetChanged(event);
        } else {
            PlotChangeEvent e = new PlotChangeEvent(this);
            e.setType(ChartChangeEventType.DATASET_UPDATED);
            notifyListeners(e);
        }
    }

    /**
     * Receives notification of a renderer change event.
     *
     * @param event  the event.
     */
    @Override
    public void rendererChanged(RendererChangeEvent event) {
        // if the event was caused by a change to series visibility, then
        // the axis ranges might need updating...
        if (event.getSeriesVisibilityChanged()) {
            configureDomainAxes();
            configureRangeAxes();
        }
        fireChangeEvent();
    }

    /**
     * Returns a flag indicating whether the domain crosshair is visible.
     *
     * @return The flag.
     *
     * @see #setDomainCrosshairVisible(boolean)
     */
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether the domain crosshair is visible
     * and, if the flag changes, sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the new value of the flag.
     *
     * @see #isDomainCrosshairVisible()
     */
    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns a flag indicating whether the crosshair should "lock-on"
     * to actual data values.
     *
     * @return The flag.
     *
     * @see #setDomainCrosshairLockedOnData(boolean)
     */
    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether the domain crosshair should
     * "lock-on" to actual data values.  If the flag value changes, this
     * method sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #isDomainCrosshairLockedOnData()
     */
    public void setDomainCrosshairLockedOnData(boolean flag) {
        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the domain crosshair value.
     *
     * @return The value.
     *
     * @see #setDomainCrosshairValue(double)
     */
    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    /**
     * Sets the domain crosshair value and sends a {@link PlotChangeEvent} to
     * all registered listeners (provided that the domain crosshair is visible).
     *
     * @param value  the value.
     *
     * @see #getDomainCrosshairValue()
     */
    public void setDomainCrosshairValue(double value) {
        setDomainCrosshairValue(value, true);
    }

    /**
     * Sets the domain crosshair value and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners (provided that the
     * domain crosshair is visible).
     *
     * @param value  the new value.
     * @param notify  notify listeners?
     *
     * @see #getDomainCrosshairValue()
     */
    public void setDomainCrosshairValue(double value, boolean notify) {
        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the {@link Stroke} used to draw the crosshair (if visible).
     *
     * @return The crosshair stroke (never {@code null}).
     *
     * @see #setDomainCrosshairStroke(Stroke)
     * @see #isDomainCrosshairVisible()
     * @see #getDomainCrosshairPaint()
     */
    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke  the new crosshair stroke ({@code null} not permitted).
     *
     * @see #getDomainCrosshairStroke()
     */
    public void setDomainCrosshairStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainCrosshairStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the domain crosshair paint.
     *
     * @return The crosshair paint (never {@code null}).
     *
     * @see #setDomainCrosshairPaint(Paint)
     * @see #isDomainCrosshairVisible()
     * @see #getDomainCrosshairStroke()
     */
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    /**
     * Sets the paint used to draw the crosshairs (if visible) and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint the new crosshair paint ({@code null} not permitted).
     *
     * @see #getDomainCrosshairPaint()
     */
    public void setDomainCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns a flag indicating whether the range crosshair is visible.
     *
     * @return The flag.
     *
     * @see #setRangeCrosshairVisible(boolean)
     * @see #isDomainCrosshairVisible()
     */
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether the range crosshair is visible.
     * If the flag value changes, this method sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param flag  the new value of the flag.
     *
     * @see #isRangeCrosshairVisible()
     */
    public void setRangeCrosshairVisible(boolean flag) {
        if (this.rangeCrosshairVisible != flag) {
            this.rangeCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns a flag indicating whether the crosshair should "lock-on"
     * to actual data values.
     *
     * @return The flag.
     *
     * @see #setRangeCrosshairLockedOnData(boolean)
     */
    public boolean isRangeCrosshairLockedOnData() {
        return this.rangeCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether the range crosshair should
     * "lock-on" to actual data values.  If the flag value changes, this method
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #isRangeCrosshairLockedOnData()
     */
    public void setRangeCrosshairLockedOnData(boolean flag) {
        if (this.rangeCrosshairLockedOnData != flag) {
            this.rangeCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the range crosshair value.
     *
     * @return The value.
     *
     * @see #setRangeCrosshairValue(double)
     */
    public double getRangeCrosshairValue() {
        return this.rangeCrosshairValue;
    }

    /**
     * Sets the range crosshair value.
     * <P>
     * Registered listeners are notified that the plot has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     *
     * @see #getRangeCrosshairValue()
     */
    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, true);
    }

    /**
     * Sets the range crosshair value and sends a {@link PlotChangeEvent} to
     * all registered listeners, but only if the crosshair is visible.
     *
     * @param value  the new value.
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getRangeCrosshairValue()
     */
    public void setRangeCrosshairValue(double value, boolean notify) {
        this.rangeCrosshairValue = value;
        if (isRangeCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke used to draw the crosshair (if visible).
     *
     * @return The crosshair stroke (never {@code null}).
     *
     * @see #setRangeCrosshairStroke(Stroke)
     * @see #isRangeCrosshairVisible()
     * @see #getRangeCrosshairPaint()
     */
    public Stroke getRangeCrosshairStroke() {
        return this.rangeCrosshairStroke;
    }

    /**
     * Sets the stroke used to draw the crosshairs (if visible) and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the new crosshair stroke ({@code null} not
     *         permitted).
     *
     * @see #getRangeCrosshairStroke()
     */
    public void setRangeCrosshairStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.rangeCrosshairStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the range crosshair paint.
     *
     * @return The crosshair paint (never {@code null}).
     *
     * @see #setRangeCrosshairPaint(Paint)
     * @see #isRangeCrosshairVisible()
     * @see #getRangeCrosshairStroke()
     */
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    /**
     * Sets the paint used to color the crosshairs (if visible) and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint the new crosshair paint ({@code null} not permitted).
     *
     * @see #getRangeCrosshairPaint()
     */
    public void setRangeCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the fixed domain axis space.
     *
     * @return The fixed domain axis space (possibly {@code null}).
     *
     * @see #setFixedDomainAxisSpace(AxisSpace)
     */
    public AxisSpace getFixedDomainAxisSpace() {
        return this.fixedDomainAxisSpace;
    }

    /**
     * Sets the fixed domain axis space and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param space  the space ({@code null} permitted).
     *
     * @see #getFixedDomainAxisSpace()
     */
    public void setFixedDomainAxisSpace(AxisSpace space) {
        setFixedDomainAxisSpace(space, true);
    }

    /**
     * Sets the fixed domain axis space and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param space  the space ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getFixedDomainAxisSpace()
     */
    public void setFixedDomainAxisSpace(AxisSpace space, boolean notify) {
        this.fixedDomainAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the fixed range axis space.
     *
     * @return The fixed range axis space (possibly {@code null}).
     *
     * @see #setFixedRangeAxisSpace(AxisSpace)
     */
    public AxisSpace getFixedRangeAxisSpace() {
        return this.fixedRangeAxisSpace;
    }

    /**
     * Sets the fixed range axis space and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param space  the space ({@code null} permitted).
     *
     * @see #getFixedRangeAxisSpace()
     */
    public void setFixedRangeAxisSpace(AxisSpace space) {
        setFixedRangeAxisSpace(space, true);
    }

    /**
     * Sets the fixed range axis space and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param space  the space ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getFixedRangeAxisSpace()
     */
    public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
        this.fixedRangeAxisSpace = space;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns {@code true} if panning is enabled for the domain axes,
     * and {@code false} otherwise.
     *
     * @return A boolean.
     */
    @Override
    public boolean isDomainPannable() {
        return this.domainPannable;
    }

    /**
     * Sets the flag that enables or disables panning of the plot along the
     * domain axes.
     *
     * @param pannable  the new flag value.
     */
    public void setDomainPannable(boolean pannable) {
        this.domainPannable = pannable;
    }

    /**
     * Returns {@code true} if panning is enabled for the range axis/axes,
     * and {@code false} otherwise.  The default value is {@code false}.
     *
     * @return A boolean.
     */
    @Override
    public boolean isRangePannable() {
        return this.rangePannable;
    }

    /**
     * Sets the flag that enables or disables panning of the plot along
     * the range axis/axes.
     *
     * @param pannable  the new flag value.
     */
    public void setRangePannable(boolean pannable) {
        this.rangePannable = pannable;
    }

    /**
     * Pans the domain axes by the specified percentage.
     *
     * @param percent  the distance to pan (as a percentage of the axis length).
     * @param info the plot info
     * @param source the source point where the pan action started.
     */
    @Override
    public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (!isDomainPannable()) {
            return;
        }
        int domainAxisCount = getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            ValueAxis axis = getDomainAxis(i);
            if (axis == null) {
                continue;
            }
            axis.pan(axis.isInverted() ? -percent : percent);
        }
    }

    /**
     * Pans the range axes by the specified percentage.
     *
     * @param percent  the distance to pan (as a percentage of the axis length).
     * @param info the plot info
     * @param source the source point where the pan action started.
     */
    @Override
    public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
        if (!isRangePannable()) {
            return;
        }
        int rangeAxisCount = getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = getRangeAxis(i);
            if (axis == null) {
                continue;
            }
            axis.pan(axis.isInverted() ? -percent : percent);
        }
    }

    /**
     * Multiplies the range on the domain axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point (in Java2D space).
     *
     * @see #zoomRangeAxes(double, PlotRenderingInfo, Point2D)
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        // delegate to other method
        zoomDomainAxes(factor, info, source, false);
    }

    /**
     * Multiplies the range on the domain axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point (in Java2D space).
     * @param useAnchor  use source point as zoom anchor?
     *
     * @see #zoomRangeAxes(double, PlotRenderingInfo, Point2D, boolean)
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        // perform the zoom on each domain axis
        for (ValueAxis xAxis : this.domainAxes.values()) {
            if (xAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceX = source.getX();
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    sourceX = source.getY();
                }
                double anchorX = xAxis.java2DToValue(sourceX, info.getDataArea(), getDomainAxisEdge());
                xAxis.resizeRange2(factor, anchorX);
            } else {
                xAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the domain axis/axes.  The new lower and upper bounds are
     * specified as percentages of the current axis range, where 0 percent is
     * the current lower bound and 100 percent is the current upper bound.
     *
     * @param lowerPercent  a percentage that determines the new lower bound
     *                      for the axis (e.g. 0.20 is twenty percent).
     * @param upperPercent  a percentage that determines the new upper bound
     *                      for the axis (e.g. 0.80 is eighty percent).
     * @param info  the plot rendering info.
     * @param source  the source point (ignored).
     *
     * @see #zoomRangeAxes(double, double, PlotRenderingInfo, Point2D)
     */
    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (ValueAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point.
     *
     * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        // delegate to other method
        zoomRangeAxes(factor, info, source, false);
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point.
     * @param useAnchor  a flag that controls whether the source point
     *         is used for the zoom anchor.
     *
     * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        // perform the zoom on each range axis
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceY = source.getY();
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    sourceY = source.getX();
                }
                double anchorY = yAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge());
                yAxis.resizeRange2(factor, anchorY);
            } else {
                yAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the range axes.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     * @param info  the plot rendering info.
     * @param source  the source point.
     *
     * @see #zoomDomainAxes(double, double, PlotRenderingInfo, Point2D)
     */
    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Returns {@code true}, indicating that the domain axis/axes for this
     * plot are zoomable.
     *
     * @return A boolean.
     *
     * @see #isRangeZoomable()
     */
    @Override
    public boolean isDomainZoomable() {
        return true;
    }

    /**
     * Returns {@code true}, indicating that the range axis/axes for this
     * plot are zoomable.
     *
     * @return A boolean.
     *
     * @see #isDomainZoomable()
     */
    @Override
    public boolean isRangeZoomable() {
        return true;
    }

    /**
     * Returns the number of series in the primary dataset for this plot.  If
     * the dataset is {@code null}, the method returns 0.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        int result = 0;
        XYDataset<S> dataset = getDataset();
        if (dataset != null) {
            result = dataset.getSeriesCount();
        }
        return result;
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
     * Returns the legend items for the plot.  Each legend item is generated by
     * the plot's renderer, since the renderer is responsible for the visual
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
        for (XYDataset<S> dataset : this.datasets.values()) {
            if (dataset == null) {
                continue;
            }
            int datasetIndex = indexOf(dataset);
            XYItemRenderer renderer = getRenderer(datasetIndex);
            if (renderer == null) {
                renderer = getRenderer(0);
            }
            if (renderer != null) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    if (renderer.isSeriesVisible(i) && renderer.isSeriesVisibleInLegend(i)) {
                        LegendItem item = renderer.getLegendItem(datasetIndex, i);
                        if (item != null) {
                            result.add(item);
                        }
                    }
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
        if (!(obj instanceof XYPlot)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        XYPlot<S> that = (XYPlot<S>) obj;
        if (this.weight != that.weight) {
            return false;
        }
        if (this.orientation != that.orientation) {
            return false;
        }
        if (!this.domainAxes.equals(that.domainAxes)) {
            return false;
        }
        if (!this.domainAxisLocations.equals(that.domainAxisLocations)) {
            return false;
        }
        if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (this.rangeGridlinesVisible != that.rangeGridlinesVisible) {
            return false;
        }
        if (this.domainMinorGridlinesVisible != that.domainMinorGridlinesVisible) {
            return false;
        }
        if (this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible) {
            return false;
        }
        if (this.domainZeroBaselineVisible != that.domainZeroBaselineVisible) {
            return false;
        }
        if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible) {
            return false;
        }
        if (this.domainCrosshairVisible != that.domainCrosshairVisible) {
            return false;
        }
        if (this.domainCrosshairValue != that.domainCrosshairValue) {
            return false;
        }
        if (this.domainCrosshairLockedOnData != that.domainCrosshairLockedOnData) {
            return false;
        }
        if (this.rangeCrosshairVisible != that.rangeCrosshairVisible) {
            return false;
        }
        if (this.rangeCrosshairValue != that.rangeCrosshairValue) {
            return false;
        }
        if (!Objects.equals(this.axisOffset, that.axisOffset)) {
            return false;
        }
        if (!Objects.equals(this.renderers, that.renderers)) {
            return false;
        }
        if (!Objects.equals(this.rangeAxes, that.rangeAxes)) {
            return false;
        }
        if (!this.rangeAxisLocations.equals(that.rangeAxisLocations)) {
            return false;
        }
        if (!Objects.equals(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeGridlineStroke, that.rangeGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.domainMinorGridlineStroke, that.domainMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainMinorGridlinePaint, that.domainMinorGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainZeroBaselinePaint, that.domainZeroBaselinePaint)) {
            return false;
        }
        if (!Objects.equals(this.domainZeroBaselineStroke, that.domainZeroBaselineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke)) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairStroke, that.domainCrosshairStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainCrosshairPaint, that.domainCrosshairPaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeCrosshairStroke, that.rangeCrosshairStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint)) {
            return false;
        }
        if (!Objects.equals(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
            return false;
        }
        if (!Objects.equals(this.backgroundDomainMarkers, that.backgroundDomainMarkers)) {
            return false;
        }
        if (!Objects.equals(this.foregroundRangeMarkers, that.foregroundRangeMarkers)) {
            return false;
        }
        if (!Objects.equals(this.backgroundRangeMarkers, that.backgroundRangeMarkers)) {
            return false;
        }
        if (!Objects.equals(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
            return false;
        }
        if (!Objects.equals(this.backgroundDomainMarkers, that.backgroundDomainMarkers)) {
            return false;
        }
        if (!Objects.equals(this.foregroundRangeMarkers, that.foregroundRangeMarkers)) {
            return false;
        }
        if (!Objects.equals(this.backgroundRangeMarkers, that.backgroundRangeMarkers)) {
            return false;
        }
        if (!Objects.equals(this.annotations, that.annotations)) {
            return false;
        }
        if (!Objects.equals(this.fixedLegendItems, that.fixedLegendItems)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainTickBandPaint, that.domainTickBandPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeTickBandPaint, that.rangeTickBandPaint)) {
            return false;
        }
        if (!this.quadrantOrigin.equals(that.quadrantOrigin)) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!PaintUtils.equal(this.quadrantPaint[i], that.quadrantPaint[i])) {
                return false;
            }
        }
        if (!Objects.equals(this.shadowGenerator, that.shadowGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.orientation);
        hash = 43 * hash + Objects.hashCode(this.axisOffset);
        hash = 43 * hash + Objects.hashCode(this.domainAxes);
        hash = 43 * hash + Objects.hashCode(this.domainAxisLocations);
        hash = 43 * hash + Objects.hashCode(this.rangeAxes);
        hash = 43 * hash + Objects.hashCode(this.rangeAxisLocations);
        hash = 43 * hash + Objects.hashCode(this.renderers);
        hash = 43 * hash + Objects.hashCode(this.datasetToDomainAxesMap);
        hash = 43 * hash + Objects.hashCode(this.datasetToRangeAxesMap);
        hash = 43 * hash + Objects.hashCode(this.quadrantOrigin);
        hash = 43 * hash + Arrays.deepHashCode(this.quadrantPaint);
        hash = 43 * hash + (this.domainGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.domainGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.domainGridlinePaint);
        hash = 43 * hash + (this.rangeGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.rangeGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeGridlinePaint);
        hash = 43 * hash + (this.domainMinorGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.domainMinorGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.domainMinorGridlinePaint);
        hash = 43 * hash + (this.rangeMinorGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.rangeMinorGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeMinorGridlinePaint);
        hash = 43 * hash + (this.domainZeroBaselineVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.domainZeroBaselineStroke);
        hash = 43 * hash + Objects.hashCode(this.domainZeroBaselinePaint);
        hash = 43 * hash + (this.rangeZeroBaselineVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.rangeZeroBaselineStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeZeroBaselinePaint);
        hash = 43 * hash + (this.domainCrosshairVisible ? 1 : 0);
        hash = 43 * hash + Long.hashCode(Double.doubleToLongBits(this.domainCrosshairValue));
        hash = 43 * hash + Objects.hashCode(this.domainCrosshairStroke);
        hash = 43 * hash + Objects.hashCode(this.domainCrosshairPaint);
        hash = 43 * hash + (this.domainCrosshairLockedOnData ? 1 : 0);
        hash = 43 * hash + (this.rangeCrosshairVisible ? 1 : 0);
        hash = 43 * hash + Long.hashCode(Double.doubleToLongBits(this.rangeCrosshairValue));
        hash = 43 * hash + Objects.hashCode(this.rangeCrosshairStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeCrosshairPaint);
        hash = 43 * hash + (this.rangeCrosshairLockedOnData ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.foregroundDomainMarkers);
        hash = 43 * hash + Objects.hashCode(this.backgroundDomainMarkers);
        hash = 43 * hash + Objects.hashCode(this.foregroundRangeMarkers);
        hash = 43 * hash + Objects.hashCode(this.backgroundRangeMarkers);
        hash = 43 * hash + Objects.hashCode(this.annotations);
        hash = 43 * hash + Objects.hashCode(this.domainTickBandPaint);
        hash = 43 * hash + Objects.hashCode(this.rangeTickBandPaint);
        hash = 43 * hash + this.weight;
        hash = 43 * hash + Objects.hashCode(this.fixedLegendItems);
        hash = 43 * hash + Objects.hashCode(this.shadowGenerator);
        return hash;
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
        @SuppressWarnings("unchecked")
        XYPlot<S> clone = (XYPlot<S>) super.clone();
        clone.domainAxes = CloneUtils.cloneMapValues(this.domainAxes);
        for (ValueAxis axis : clone.domainAxes.values()) {
            if (axis != null) {
                axis.setPlot(clone);
                axis.addChangeListener(clone);
            }
        }
        clone.rangeAxes = CloneUtils.cloneMapValues(this.rangeAxes);
        for (ValueAxis axis : clone.rangeAxes.values()) {
            if (axis != null) {
                axis.setPlot(clone);
                axis.addChangeListener(clone);
            }
        }
        clone.domainAxisLocations = new HashMap<>(this.domainAxisLocations);
        clone.rangeAxisLocations = new HashMap<>(this.rangeAxisLocations);
        // the datasets are not cloned, but listeners need to be added...
        clone.datasets = new HashMap<>(this.datasets);
        for (XYDataset<S> dataset : clone.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxesMap = new TreeMap<>();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap<>();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = CloneUtils.cloneMapValues(this.renderers);
        for (XYItemRenderer renderer : clone.renderers.values()) {
            if (renderer != null) {
                renderer.setPlot(clone);
                renderer.addChangeListener(clone);
            }
        }
        clone.foregroundDomainMarkers = CloneUtils.clone(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = CloneUtils.clone(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = CloneUtils.clone(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = CloneUtils.clone(this.backgroundRangeMarkers);
        clone.annotations = CloneUtils.cloneList(this.annotations);
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = CloneUtils.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = CloneUtils.clone(this.fixedRangeAxisSpace);
        }
        if (this.fixedLegendItems != null) {
            clone.fixedLegendItems = (LegendItemCollection) this.fixedLegendItems.clone();
        }
        clone.quadrantOrigin = CloneUtils.clone(this.quadrantOrigin);
        clone.quadrantPaint = this.quadrantPaint.clone();
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
        SerialUtils.writeStroke(this.domainGridlineStroke, stream);
        SerialUtils.writePaint(this.domainGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeGridlineStroke, stream);
        SerialUtils.writePaint(this.rangeGridlinePaint, stream);
        SerialUtils.writeStroke(this.domainMinorGridlineStroke, stream);
        SerialUtils.writePaint(this.domainMinorGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtils.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtils.writePaint(this.rangeZeroBaselinePaint, stream);
        SerialUtils.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtils.writePaint(this.domainCrosshairPaint, stream);
        SerialUtils.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtils.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtils.writePaint(this.domainTickBandPaint, stream);
        SerialUtils.writePaint(this.rangeTickBandPaint, stream);
        SerialUtils.writePoint2D(this.quadrantOrigin, stream);
        for (int i = 0; i < 4; i++) {
            SerialUtils.writePaint(this.quadrantPaint[i], stream);
        }
        SerialUtils.writeStroke(this.domainZeroBaselineStroke, stream);
        SerialUtils.writePaint(this.domainZeroBaselinePaint, stream);
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
        this.domainGridlineStroke = SerialUtils.readStroke(stream);
        this.domainGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeGridlineStroke = SerialUtils.readStroke(stream);
        this.rangeGridlinePaint = SerialUtils.readPaint(stream);
        this.domainMinorGridlineStroke = SerialUtils.readStroke(stream);
        this.domainMinorGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtils.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtils.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtils.readPaint(stream);
        this.domainCrosshairStroke = SerialUtils.readStroke(stream);
        this.domainCrosshairPaint = SerialUtils.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtils.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtils.readPaint(stream);
        this.domainTickBandPaint = SerialUtils.readPaint(stream);
        this.rangeTickBandPaint = SerialUtils.readPaint(stream);
        this.quadrantOrigin = SerialUtils.readPoint2D(stream);
        this.quadrantPaint = new Paint[4];
        for (int i = 0; i < 4; i++) {
            this.quadrantPaint[i] = SerialUtils.readPaint(stream);
        }
        this.domainZeroBaselineStroke = SerialUtils.readStroke(stream);
        this.domainZeroBaselinePaint = SerialUtils.readPaint(stream);
        // register the plot as a listener with its axes, datasets, and
        // renderers...
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        for (XYDataset<S> dataset : this.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (XYItemRenderer renderer : this.renderers.values()) {
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
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
 * -----------------
 * CategoryAxis.java
 * -----------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Pady Srinivasan (patch 1217634);
 *                   Peter Kolb (patches 2497611 and 2603321);
 *
 */
/**
 * An axis that displays categories.
 */
public class CategoryAxis extends Axis implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 5886554608114265863L;

    /**
     * The default margin for the axis (used for both lower and upper margins).
     */
    public static final double DEFAULT_AXIS_MARGIN = 0.05;

    /**
     * The default margin between categories (a percentage of the overall axis
     * length).
     */
    public static final double DEFAULT_CATEGORY_MARGIN = 0.20;

    /**
     * The amount of space reserved at the start of the axis.
     */
    private double lowerMargin;

    /**
     * The amount of space reserved at the end of the axis.
     */
    private double upperMargin;

    /**
     * The amount of space reserved between categories.
     */
    private double categoryMargin;

    /**
     * The maximum number of lines for category labels.
     */
    private int maximumCategoryLabelLines;

    /**
     * A ratio that is multiplied by the width of one category to determine the
     * maximum label width.
     */
    private float maximumCategoryLabelWidthRatio;

    /**
     * The category label offset.
     */
    private int categoryLabelPositionOffset;

    /**
     * A structure defining the category label positions for each axis
     * location.
     */
    private CategoryLabelPositions categoryLabelPositions;

    /**
     * Storage for tick label font overrides (if any).
     */
    private Map<Comparable, Font> tickLabelFontMap;

    /**
     * Storage for tick label paint overrides (if any).
     */
    private transient Map<Comparable, Paint> tickLabelPaintMap;

    /**
     * Storage for the category label tooltips (if any).
     */
    private Map<Comparable, String> categoryLabelToolTips;

    /**
     * Storage for the category label URLs (if any).
     */
    private Map<Comparable, String> categoryLabelURLs;

    /**
     * Creates a new category axis with no label.
     */
    public CategoryAxis() {
        this(null);
    }

    /**
     * Constructs a category axis, using default values where necessary.
     *
     * @param label  the axis label ({@code null} permitted).
     */
    public CategoryAxis(String label) {
        super(label);
        this.lowerMargin = DEFAULT_AXIS_MARGIN;
        this.upperMargin = DEFAULT_AXIS_MARGIN;
        this.categoryMargin = DEFAULT_CATEGORY_MARGIN;
        this.maximumCategoryLabelLines = 1;
        this.maximumCategoryLabelWidthRatio = 0.0f;
        this.categoryLabelPositionOffset = 4;
        this.categoryLabelPositions = CategoryLabelPositions.STANDARD;
        this.tickLabelFontMap = new HashMap<>();
        this.tickLabelPaintMap = new HashMap<>();
        this.categoryLabelToolTips = new HashMap<>();
        this.categoryLabelURLs = new HashMap<>();
    }

    /**
     * Returns the lower margin for the axis.
     *
     * @return The margin.
     *
     * @see #getUpperMargin()
     * @see #setLowerMargin(double)
     */
    public double getLowerMargin() {
        return this.lowerMargin;
    }

    /**
     * Sets the lower margin for the axis and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param margin  the margin as a percentage of the axis length (for
     *                example, 0.05 is five percent).
     *
     * @see #getLowerMargin()
     */
    public void setLowerMargin(double margin) {
        this.lowerMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the upper margin for the axis.
     *
     * @return The margin.
     *
     * @see #getLowerMargin()
     * @see #setUpperMargin(double)
     */
    public double getUpperMargin() {
        return this.upperMargin;
    }

    /**
     * Sets the upper margin for the axis and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @param margin  the margin as a percentage of the axis length (for
     *                example, 0.05 is five percent).
     *
     * @see #getUpperMargin()
     */
    public void setUpperMargin(double margin) {
        this.upperMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the category margin.
     *
     * @return The margin.
     *
     * @see #setCategoryMargin(double)
     */
    public double getCategoryMargin() {
        return this.categoryMargin;
    }

    /**
     * Sets the category margin and sends an {@link AxisChangeEvent} to all
     * registered listeners.  The overall category margin is distributed over
     * N-1 gaps, where N is the number of categories on the axis.
     *
     * @param margin  the margin as a percentage of the axis length (for
     *                example, 0.05 is five percent).
     *
     * @see #getCategoryMargin()
     */
    public void setCategoryMargin(double margin) {
        this.categoryMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the maximum number of lines to use for each category label.
     *
     * @return The maximum number of lines.
     *
     * @see #setMaximumCategoryLabelLines(int)
     */
    public int getMaximumCategoryLabelLines() {
        return this.maximumCategoryLabelLines;
    }

    /**
     * Sets the maximum number of lines to use for each category label and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param lines  the maximum number of lines.
     *
     * @see #getMaximumCategoryLabelLines()
     */
    public void setMaximumCategoryLabelLines(int lines) {
        this.maximumCategoryLabelLines = lines;
        fireChangeEvent();
    }

    /**
     * Returns the category label width ratio.
     *
     * @return The ratio.
     *
     * @see #setMaximumCategoryLabelWidthRatio(float)
     */
    public float getMaximumCategoryLabelWidthRatio() {
        return this.maximumCategoryLabelWidthRatio;
    }

    /**
     * Sets the maximum category label width ratio and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param ratio  the ratio.
     *
     * @see #getMaximumCategoryLabelWidthRatio()
     */
    public void setMaximumCategoryLabelWidthRatio(float ratio) {
        this.maximumCategoryLabelWidthRatio = ratio;
        fireChangeEvent();
    }

    /**
     * Returns the offset between the axis and the category labels (before
     * label positioning is taken into account).
     *
     * @return The offset (in Java2D units).
     *
     * @see #setCategoryLabelPositionOffset(int)
     */
    public int getCategoryLabelPositionOffset() {
        return this.categoryLabelPositionOffset;
    }

    /**
     * Sets the offset between the axis and the category labels (before label
     * positioning is taken into account) and sends a change event to all
     * registered listeners.
     *
     * @param offset  the offset (in Java2D units).
     *
     * @see #getCategoryLabelPositionOffset()
     */
    public void setCategoryLabelPositionOffset(int offset) {
        this.categoryLabelPositionOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the category label position specification (this contains label
     * positioning info for all four possible axis locations).
     *
     * @return The positions (never {@code null}).
     *
     * @see #setCategoryLabelPositions(CategoryLabelPositions)
     */
    public CategoryLabelPositions getCategoryLabelPositions() {
        return this.categoryLabelPositions;
    }

    /**
     * Sets the category label position specification for the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param positions  the positions ({@code null} not permitted).
     *
     * @see #getCategoryLabelPositions()
     */
    public void setCategoryLabelPositions(CategoryLabelPositions positions) {
        Args.nullNotPermitted(positions, "positions");
        this.categoryLabelPositions = positions;
        fireChangeEvent();
    }

    /**
     * Returns the font for the tick label for the given category.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @return The font (never {@code null}).
     *
     * @see #setTickLabelFont(Comparable, Font)
     */
    public Font getTickLabelFont(Comparable category) {
        Args.nullNotPermitted(category, "category");
        Font result = this.tickLabelFontMap.get(category);
        // if there is no specific font, use the general one...
        if (result == null) {
            result = getTickLabelFont();
        }
        return result;
    }

    /**
     * Sets the font for the tick label for the specified category and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param category  the category ({@code null} not permitted).
     * @param font  the font ({@code null} permitted).
     *
     * @see #getTickLabelFont(Comparable)
     */
    public void setTickLabelFont(Comparable category, Font font) {
        Args.nullNotPermitted(category, "category");
        if (font == null) {
            this.tickLabelFontMap.remove(category);
        } else {
            this.tickLabelFontMap.put(category, font);
        }
        fireChangeEvent();
    }

    /**
     * Returns the paint for the tick label for the given category.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @return The paint (never {@code null}).
     *
     * @see #setTickLabelPaint(Paint)
     */
    public Paint getTickLabelPaint(Comparable category) {
        Args.nullNotPermitted(category, "category");
        Paint result = this.tickLabelPaintMap.get(category);
        // if there is no specific paint, use the general one...
        if (result == null) {
            result = getTickLabelPaint();
        }
        return result;
    }

    /**
     * Sets the paint for the tick label for the specified category and sends
     * an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param category  the category ({@code null} not permitted).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getTickLabelPaint(Comparable)
     */
    public void setTickLabelPaint(Comparable category, Paint paint) {
        Args.nullNotPermitted(category, "category");
        if (paint == null) {
            this.tickLabelPaintMap.remove(category);
        } else {
            this.tickLabelPaintMap.put(category, paint);
        }
        fireChangeEvent();
    }

    /**
     * Adds a tooltip to the specified category and sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param category  the category ({@code null} not permitted).
     * @param tooltip  the tooltip text ({@code null} permitted).
     *
     * @see #removeCategoryLabelToolTip(Comparable)
     */
    public void addCategoryLabelToolTip(Comparable category, String tooltip) {
        Args.nullNotPermitted(category, "category");
        this.categoryLabelToolTips.put(category, tooltip);
        fireChangeEvent();
    }

    /**
     * Returns the tool tip text for the label belonging to the specified
     * category.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @return The tool tip text (possibly {@code null}).
     *
     * @see #addCategoryLabelToolTip(Comparable, String)
     * @see #removeCategoryLabelToolTip(Comparable)
     */
    public String getCategoryLabelToolTip(Comparable category) {
        Args.nullNotPermitted(category, "category");
        return this.categoryLabelToolTips.get(category);
    }

    /**
     * Removes the tooltip for the specified category and, if there was a value
     * associated with that category, sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @see #addCategoryLabelToolTip(Comparable, String)
     * @see #clearCategoryLabelToolTips()
     */
    public void removeCategoryLabelToolTip(Comparable category) {
        Args.nullNotPermitted(category, "category");
        if (this.categoryLabelToolTips.remove(category) != null) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the category label tooltips and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @see #addCategoryLabelToolTip(Comparable, String)
     * @see #removeCategoryLabelToolTip(Comparable)
     */
    public void clearCategoryLabelToolTips() {
        this.categoryLabelToolTips.clear();
        fireChangeEvent();
    }

    /**
     * Adds a URL (to be used in image maps) to the specified category and
     * sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param category  the category ({@code null} not permitted).
     * @param url  the URL text ({@code null} permitted).
     *
     * @see #removeCategoryLabelURL(Comparable)
     */
    public void addCategoryLabelURL(Comparable category, String url) {
        Args.nullNotPermitted(category, "category");
        this.categoryLabelURLs.put(category, url);
        fireChangeEvent();
    }

    /**
     * Returns the URL for the label belonging to the specified category.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @return The URL text (possibly {@code null}).
     *
     * @see #addCategoryLabelURL(Comparable, String)
     * @see #removeCategoryLabelURL(Comparable)
     */
    public String getCategoryLabelURL(Comparable category) {
        Args.nullNotPermitted(category, "category");
        return this.categoryLabelURLs.get(category);
    }

    /**
     * Removes the URL for the specified category and, if there was a URL
     * associated with that category, sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @see #addCategoryLabelURL(Comparable, String)
     * @see #clearCategoryLabelURLs()
     */
    public void removeCategoryLabelURL(Comparable category) {
        Args.nullNotPermitted(category, "category");
        if (this.categoryLabelURLs.remove(category) != null) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the category label URLs and sends an {@link AxisChangeEvent}
     * to all registered listeners.
     *
     * @see #addCategoryLabelURL(Comparable, String)
     * @see #removeCategoryLabelURL(Comparable)
     */
    public void clearCategoryLabelURLs() {
        this.categoryLabelURLs.clear();
        fireChangeEvent();
    }

    /**
     * Returns the Java 2D coordinate for a category.
     *
     * @param anchor  the anchor point ({@code null} not permitted).
     * @param category  the category index.
     * @param categoryCount  the category count.
     * @param area  the data area.
     * @param edge  the location of the axis.
     *
     * @return The coordinate.
     */
    public double getCategoryJava2DCoordinate(CategoryAnchor anchor, int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        Args.nullNotPermitted(anchor, "anchor");
        double result = 0.0;
        switch(anchor) {
            case START:
                result = getCategoryStart(category, categoryCount, area, edge);
                break;
            case MIDDLE:
                result = getCategoryMiddle(category, categoryCount, area, edge);
                break;
            case END:
                result = getCategoryEnd(category, categoryCount, area, edge);
                break;
            default:
                throw new IllegalStateException("Unexpected anchor value.");
        }
        return result;
    }

    /**
     * Returns the starting coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     * @param edge  the axis location.
     *
     * @return The coordinate.
     *
     * @see #getCategoryMiddle(int, int, Rectangle2D, RectangleEdge)
     * @see #getCategoryEnd(int, int, Rectangle2D, RectangleEdge)
     */
    public double getCategoryStart(int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double result = 0.0;
        if ((edge == RectangleEdge.TOP) || (edge == RectangleEdge.BOTTOM)) {
            result = area.getX() + area.getWidth() * getLowerMargin();
        } else if ((edge == RectangleEdge.LEFT) || (edge == RectangleEdge.RIGHT)) {
            result = area.getMinY() + area.getHeight() * getLowerMargin();
        }
        double categorySize = calculateCategorySize(categoryCount, area, edge);
        double categoryGapWidth = calculateCategoryGapSize(categoryCount, area, edge);
        result = result + category * (categorySize + categoryGapWidth);
        return result;
    }

    /**
     * Returns the middle coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     * @param edge  the axis location.
     *
     * @return The coordinate.
     *
     * @see #getCategoryStart(int, int, Rectangle2D, RectangleEdge)
     * @see #getCategoryEnd(int, int, Rectangle2D, RectangleEdge)
     */
    public double getCategoryMiddle(int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        if (category < 0 || category >= categoryCount) {
            throw new IllegalArgumentException("Invalid category index: " + category);
        }
        return getCategoryStart(category, categoryCount, area, edge) + calculateCategorySize(categoryCount, area, edge) / 2;
    }

    /**
     * Returns the end coordinate for the specified category.
     *
     * @param category  the category.
     * @param categoryCount  the number of categories.
     * @param area  the data area.
     * @param edge  the axis location.
     *
     * @return The coordinate.
     *
     * @see #getCategoryStart(int, int, Rectangle2D, RectangleEdge)
     * @see #getCategoryMiddle(int, int, Rectangle2D, RectangleEdge)
     */
    public double getCategoryEnd(int category, int categoryCount, Rectangle2D area, RectangleEdge edge) {
        return getCategoryStart(category, categoryCount, area, edge) + calculateCategorySize(categoryCount, area, edge);
    }

    /**
     * A convenience method that returns the axis coordinate for the centre of
     * a category.
     *
     * @param category  the category key ({@code null} not permitted).
     * @param categories  the categories ({@code null} not permitted).
     * @param area  the data area ({@code null} not permitted).
     * @param edge  the edge along which the axis lies ({@code null} not
     *     permitted).
     *
     * @return The centre coordinate.
     *
     * @see #getCategorySeriesMiddle(Comparable, Comparable, CategoryDataset,
     *     double, Rectangle2D, RectangleEdge)
     */
    public double getCategoryMiddle(Comparable category, List categories, Rectangle2D area, RectangleEdge edge) {
        Args.nullNotPermitted(categories, "categories");
        int categoryIndex = categories.indexOf(category);
        int categoryCount = categories.size();
        return getCategoryMiddle(categoryIndex, categoryCount, area, edge);
    }

    /**
     * Returns the middle coordinate (in Java2D space) for a series within a
     * category.
     *
     * @param category  the category ({@code null} not permitted).
     * @param seriesKey  the series key ({@code null} not permitted).
     * @param dataset  the dataset ({@code null} not permitted).
     * @param itemMargin  the item margin (0.0 &lt;= itemMargin &lt; 1.0);
     * @param area  the area ({@code null} not permitted).
     * @param edge  the edge ({@code null} not permitted).
     *
     * @return The coordinate in Java2D space.
     */
    public double getCategorySeriesMiddle(Comparable category, Comparable seriesKey, CategoryDataset dataset, double itemMargin, Rectangle2D area, RectangleEdge edge) {
        int categoryIndex = dataset.getColumnIndex(category);
        int categoryCount = dataset.getColumnCount();
        int seriesIndex = dataset.getRowIndex(seriesKey);
        int seriesCount = dataset.getRowCount();
        double start = getCategoryStart(categoryIndex, categoryCount, area, edge);
        double end = getCategoryEnd(categoryIndex, categoryCount, area, edge);
        double width = end - start;
        if (seriesCount == 1) {
            return start + width / 2.0;
        } else {
            double gap = (width * itemMargin) / (seriesCount - 1);
            double ww = (width * (1 - itemMargin)) / seriesCount;
            return start + (seriesIndex * (ww + gap)) + ww / 2.0;
        }
    }

    /**
     * Returns the middle coordinate (in Java2D space) for a series within a
     * category.
     *
     * @param categoryIndex  the category index.
     * @param categoryCount  the category count.
     * @param seriesIndex the series index.
     * @param seriesCount the series count.
     * @param itemMargin  the item margin (0.0 &lt;= itemMargin &lt; 1.0);
     * @param area  the area ({@code null} not permitted).
     * @param edge  the edge ({@code null} not permitted).
     *
     * @return The coordinate in Java2D space.
     */
    public double getCategorySeriesMiddle(int categoryIndex, int categoryCount, int seriesIndex, int seriesCount, double itemMargin, Rectangle2D area, RectangleEdge edge) {
        double start = getCategoryStart(categoryIndex, categoryCount, area, edge);
        double end = getCategoryEnd(categoryIndex, categoryCount, area, edge);
        double width = end - start;
        if (seriesCount == 1) {
            return start + width / 2.0;
        } else {
            double gap = (width * itemMargin) / (seriesCount - 1);
            double ww = (width * (1 - itemMargin)) / seriesCount;
            return start + (seriesIndex * (ww + gap)) + ww / 2.0;
        }
    }

    /**
     * Calculates the size (width or height, depending on the location of the
     * axis) of a category.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area within which the categories will be drawn.
     * @param edge  the axis location.
     *
     * @return The category size.
     */
    protected double calculateCategorySize(int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double result;
        double available = 0.0;
        if ((edge == RectangleEdge.TOP) || (edge == RectangleEdge.BOTTOM)) {
            available = area.getWidth();
        } else if ((edge == RectangleEdge.LEFT) || (edge == RectangleEdge.RIGHT)) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            result = available * (1 - getLowerMargin() - getUpperMargin() - getCategoryMargin());
            result = result / categoryCount;
        } else {
            result = available * (1 - getLowerMargin() - getUpperMargin());
        }
        return result;
    }

    /**
     * Calculates the size (width or height, depending on the location of the
     * axis) of a category gap.
     *
     * @param categoryCount  the number of categories.
     * @param area  the area within which the categories will be drawn.
     * @param edge  the axis location.
     *
     * @return The category gap width.
     */
    protected double calculateCategoryGapSize(int categoryCount, Rectangle2D area, RectangleEdge edge) {
        double result = 0.0;
        double available = 0.0;
        if ((edge == RectangleEdge.TOP) || (edge == RectangleEdge.BOTTOM)) {
            available = area.getWidth();
        } else if ((edge == RectangleEdge.LEFT) || (edge == RectangleEdge.RIGHT)) {
            available = area.getHeight();
        }
        if (categoryCount > 1) {
            result = available * getCategoryMargin() / (categoryCount - 1);
        }
        return result;
    }

    /**
     * Estimates the space required for the axis, given a specific drawing area.
     *
     * @param g2  the graphics device (used to obtain font information).
     * @param plot  the plot that the axis belongs to.
     * @param plotArea  the area within which the axis should be drawn.
     * @param edge  the axis location ({@code null} not permitted).
     * @param space  the space already reserved.
     *
     * @return The space required to draw the axis.
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
        // calculate the max size of the tick labels (if visible)...
        double tickLabelHeight = 0.0;
        double tickLabelWidth = 0.0;
        if (isTickLabelsVisible()) {
            g2.setFont(getTickLabelFont());
            AxisState state = new AxisState();
            // we call refresh ticks just to get the maximum width or height
            refreshTicks(g2, state, plotArea, edge);
            switch(edge) {
                case TOP:
                    tickLabelHeight = state.getMax();
                    break;
                case BOTTOM:
                    tickLabelHeight = state.getMax();
                    break;
                case LEFT:
                    tickLabelWidth = state.getMax();
                    break;
                case RIGHT:
                    tickLabelWidth = state.getMax();
                    break;
                default:
                    throw new IllegalStateException("Unexpected RectangleEdge value.");
            }
        }
        // get the axis label size and update the space object...
        Rectangle2D labelEnclosure = getLabelEnclosure(g2, edge);
        double labelHeight, labelWidth;
        if (RectangleEdge.isTopOrBottom(edge)) {
            labelHeight = labelEnclosure.getHeight();
            space.add(labelHeight + tickLabelHeight + this.categoryLabelPositionOffset, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            labelWidth = labelEnclosure.getWidth();
            space.add(labelWidth + tickLabelWidth + this.categoryLabelPositionOffset, edge);
        }
        return space;
    }

    /**
     * Configures the axis against the current plot.
     */
    @Override
    public void configure() {
        // nothing required
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axis should be drawn
     *                  ({@code null} not permitted).
     * @param dataArea  the area within which the plot is being drawn
     *                  ({@code null} not permitted).
     * @param edge  the location of the axis ({@code null} not permitted).
     * @param plotState  collects information about the plot
     *                   ({@code null} permitted).
     *
     * @return The axis state (never {@code null}).
     */
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            return new AxisState(cursor);
        }
        if (isAxisLineVisible()) {
            drawAxisLine(g2, cursor, dataArea, edge);
        }
        AxisState state = new AxisState(cursor);
        if (isTickMarksVisible()) {
            drawTickMarks(g2, cursor, dataArea, edge, state);
        }
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        // draw the category labels and axis label
        state = drawCategoryLabels(g2, plotArea, dataArea, edge, state, plotState);
        if (getAttributedLabel() != null) {
            state = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        } else {
            state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        }
        return state;
    }

    /**
     * Draws the category labels and returns the updated axis state.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param plotArea  the plot area ({@code null} not permitted).
     * @param dataArea  the area inside the axes ({@code null} not
     *                  permitted).
     * @param edge  the axis location ({@code null} not permitted).
     * @param state  the axis state ({@code null} not permitted).
     * @param plotState  collects information about the plot ({@code null}
     *                   permitted).
     *
     * @return The updated axis state (never {@code null}).
     */
    protected AxisState drawCategoryLabels(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
        Args.nullNotPermitted(state, "state");
        if (!isTickLabelsVisible()) {
            return state;
        }
        List ticks = refreshTicks(g2, state, plotArea, edge);
        state.setTicks(ticks);
        int categoryIndex = 0;
        for (Object o : ticks) {
            CategoryTick tick = (CategoryTick) o;
            g2.setFont(getTickLabelFont(tick.getCategory()));
            g2.setPaint(getTickLabelPaint(tick.getCategory()));
            CategoryLabelPosition position = this.categoryLabelPositions.getLabelPosition(edge);
            double x0 = 0.0;
            double x1 = 0.0;
            double y0 = 0.0;
            double y1 = 0.0;
            if (edge == RectangleEdge.TOP) {
                x0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                y1 = state.getCursor() - this.categoryLabelPositionOffset;
                y0 = y1 - state.getMax();
            } else if (edge == RectangleEdge.BOTTOM) {
                x0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                x1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                y0 = state.getCursor() + this.categoryLabelPositionOffset;
                y1 = y0 + state.getMax();
            } else if (edge == RectangleEdge.LEFT) {
                y0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                x1 = state.getCursor() - this.categoryLabelPositionOffset;
                x0 = x1 - state.getMax();
            } else if (edge == RectangleEdge.RIGHT) {
                y0 = getCategoryStart(categoryIndex, ticks.size(), dataArea, edge);
                y1 = getCategoryEnd(categoryIndex, ticks.size(), dataArea, edge);
                x0 = state.getCursor() + this.categoryLabelPositionOffset;
                x1 = x0 - state.getMax();
            }
            Rectangle2D area = new Rectangle2D.Double(x0, y0, (x1 - x0), (y1 - y0));
            Point2D anchorPoint = position.getCategoryAnchor().getAnchorPoint(area);
            TextBlock block = tick.getLabel();
            block.draw(g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getLabelAnchor(), (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getAngle());
            Shape bounds = block.calculateBounds(g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getLabelAnchor(), (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getAngle());
            if (plotState != null && plotState.getOwner() != null) {
                EntityCollection entities = plotState.getOwner().getEntityCollection();
                if (entities != null) {
                    String tooltip = getCategoryLabelToolTip(tick.getCategory());
                    String url = getCategoryLabelURL(tick.getCategory());
                    entities.add(new CategoryLabelEntity(tick.getCategory(), bounds, tooltip, url));
                }
            }
            categoryIndex++;
        }
        if (edge.equals(RectangleEdge.TOP)) {
            double h = state.getMax() + this.categoryLabelPositionOffset;
            state.cursorUp(h);
        } else if (edge.equals(RectangleEdge.BOTTOM)) {
            double h = state.getMax() + this.categoryLabelPositionOffset;
            state.cursorDown(h);
        } else if (edge == RectangleEdge.LEFT) {
            double w = state.getMax() + this.categoryLabelPositionOffset;
            state.cursorLeft(w);
        } else if (edge == RectangleEdge.RIGHT) {
            double w = state.getMax() + this.categoryLabelPositionOffset;
            state.cursorRight(w);
        }
        return state;
    }

    /**
     * Creates a temporary list of ticks that can be used when drawing the axis.
     *
     * @param g2  the graphics device (used to get font measurements).
     * @param state  the axis state.
     * @param dataArea  the area inside the axes.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    @Override
    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        // FIXME generics
        List ticks = new java.util.ArrayList();
        // sanity check for data area...
        if (dataArea.getHeight() <= 0.0 || dataArea.getWidth() < 0.0) {
            return ticks;
        }
        CategoryPlot plot = (CategoryPlot) getPlot();
        List categories = plot.getCategoriesForAxis(this);
        double max = 0.0;
        if (categories != null) {
            CategoryLabelPosition position = this.categoryLabelPositions.getLabelPosition(edge);
            float r = this.maximumCategoryLabelWidthRatio;
            if (r <= 0.0) {
                r = position.getWidthRatio();
            }
            float l;
            if (position.getWidthType() == CategoryLabelWidthType.CATEGORY) {
                l = (float) calculateCategorySize(categories.size(), dataArea, edge);
            } else {
                if (RectangleEdge.isLeftOrRight(edge)) {
                    l = (float) dataArea.getWidth();
                } else {
                    l = (float) dataArea.getHeight();
                }
            }
            int categoryIndex = 0;
            for (Object o : categories) {
                Comparable category = (Comparable) o;
                g2.setFont(getTickLabelFont(category));
                TextBlock label = createLabel(category, l * r, edge, g2);
                if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                    max = Math.max(max, calculateCategoryLabelHeight(label, position, getTickLabelInsets(), g2));
                } else if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT) {
                    max = Math.max(max, calculateCategoryLabelWidth(label, position, getTickLabelInsets(), g2));
                }
                Tick tick = new CategoryTick(category, label, position.getLabelAnchor(), position.getRotationAnchor(), position.getAngle());
                ticks.add(tick);
                categoryIndex = categoryIndex + 1;
            }
        }
        state.setMax(max);
        return ticks;
    }

    /**
     * Draws the tick marks.
     *
     * @param g2  the graphics target.
     * @param cursor  the cursor position (an offset when drawing multiple axes)
     * @param dataArea  the area for plotting the data.
     * @param edge  the location of the axis.
     * @param state  the axis state.
     */
    public void drawTickMarks(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge, AxisState state) {
        Plot p = getPlot();
        if (p == null) {
            return;
        }
        CategoryPlot plot = (CategoryPlot) p;
        double il = getTickMarkInsideLength();
        double ol = getTickMarkOutsideLength();
        Line2D line = new Line2D.Double();
        List<Comparable> categories = plot.getCategoriesForAxis(this);
        g2.setPaint(getTickMarkPaint());
        g2.setStroke(getTickMarkStroke());
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        if (edge.equals(RectangleEdge.TOP)) {
            for (Comparable category : categories) {
                double x = getCategoryMiddle(category, categories, dataArea, edge);
                line.setLine(x, cursor, x, cursor + il);
                g2.draw(line);
                line.setLine(x, cursor, x, cursor - ol);
                g2.draw(line);
            }
            state.cursorUp(ol);
        } else if (edge.equals(RectangleEdge.BOTTOM)) {
            for (Comparable category : categories) {
                double x = getCategoryMiddle(category, categories, dataArea, edge);
                line.setLine(x, cursor, x, cursor - il);
                g2.draw(line);
                line.setLine(x, cursor, x, cursor + ol);
                g2.draw(line);
            }
            state.cursorDown(ol);
        } else if (edge.equals(RectangleEdge.LEFT)) {
            for (Comparable category : categories) {
                double y = getCategoryMiddle(category, categories, dataArea, edge);
                line.setLine(cursor, y, cursor + il, y);
                g2.draw(line);
                line.setLine(cursor, y, cursor - ol, y);
                g2.draw(line);
            }
            state.cursorLeft(ol);
        } else if (edge.equals(RectangleEdge.RIGHT)) {
            for (Comparable category : categories) {
                double y = getCategoryMiddle(category, categories, dataArea, edge);
                line.setLine(cursor, y, cursor - il, y);
                g2.draw(line);
                line.setLine(cursor, y, cursor + ol, y);
                g2.draw(line);
            }
            state.cursorRight(ol);
        }
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Creates a label.
     *
     * @param category  the category.
     * @param width  the available width.
     * @param edge  the edge on which the axis appears.
     * @param g2  the graphics device.
     *
     * @return A label.
     */
    protected TextBlock createLabel(Comparable category, float width, RectangleEdge edge, Graphics2D g2) {
        TextBlock label = TextUtils.createTextBlock(category.toString(), getTickLabelFont(category), getTickLabelPaint(category), width, this.maximumCategoryLabelLines, new G2TextMeasurer(g2));
        return label;
    }

    /**
     * Calculates the width of a category label when rendered.
     *
     * @param label  the text block ({@code null} not permitted).
     * @param position  the position.
     * @param insets  the label insets.
     * @param g2  the graphics device.
     *
     * @return The width.
     */
    protected double calculateCategoryLabelWidth(TextBlock label, CategoryLabelPosition position, RectangleInsets insets, Graphics2D g2) {
        Size2D size = label.calculateDimensions(g2);
        Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
        Shape rotatedBox = ShapeUtils.rotateShape(box, position.getAngle(), 0.0f, 0.0f);
        double w = rotatedBox.getBounds2D().getWidth() + insets.getLeft() + insets.getRight();
        return w;
    }

    /**
     * Calculates the height of a category label when rendered.
     *
     * @param block  the text block ({@code null} not permitted).
     * @param position  the label position ({@code null} not permitted).
     * @param insets  the label insets ({@code null} not permitted).
     * @param g2  the graphics device ({@code null} not permitted).
     *
     * @return The height.
     */
    protected double calculateCategoryLabelHeight(TextBlock block, CategoryLabelPosition position, RectangleInsets insets, Graphics2D g2) {
        Size2D size = block.calculateDimensions(g2);
        Rectangle2D box = new Rectangle2D.Double(0.0, 0.0, size.getWidth(), size.getHeight());
        Shape rotatedBox = ShapeUtils.rotateShape(box, position.getAngle(), 0.0f, 0.0f);
        double h = rotatedBox.getBounds2D().getHeight() + insets.getTop() + insets.getBottom();
        return h;
    }

    /**
     * Creates a clone of the axis.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the axis does
     *         not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        CategoryAxis clone = (CategoryAxis) super.clone();
        clone.tickLabelFontMap = new HashMap<>(this.tickLabelFontMap);
        clone.tickLabelPaintMap = new HashMap<>(this.tickLabelPaintMap);
        clone.categoryLabelToolTips = new HashMap<>(this.categoryLabelToolTips);
        clone.categoryLabelURLs = new HashMap<>(this.categoryLabelToolTips);
        return clone;
    }

    /**
     * Tests this axis for equality with an arbitrary object.
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
        if (!(obj instanceof CategoryAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CategoryAxis that = (CategoryAxis) obj;
        if (that.lowerMargin != this.lowerMargin) {
            return false;
        }
        if (that.upperMargin != this.upperMargin) {
            return false;
        }
        if (that.categoryMargin != this.categoryMargin) {
            return false;
        }
        if (that.maximumCategoryLabelWidthRatio != this.maximumCategoryLabelWidthRatio) {
            return false;
        }
        if (that.categoryLabelPositionOffset != this.categoryLabelPositionOffset) {
            return false;
        }
        if (!Objects.equals(that.categoryLabelPositions, this.categoryLabelPositions)) {
            return false;
        }
        if (!Objects.equals(that.categoryLabelToolTips, this.categoryLabelToolTips)) {
            return false;
        }
        if (!Objects.equals(this.categoryLabelURLs, that.categoryLabelURLs)) {
            return false;
        }
        if (!Objects.equals(this.tickLabelFontMap, that.tickLabelFontMap)) {
            return false;
        }
        if (!PaintUtils.equal(this.tickLabelPaintMap, that.tickLabelPaintMap)) {
            return false;
        }
        return true;
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
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        writePaintMap(this.tickLabelPaintMap, stream);
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
        this.tickLabelPaintMap = readPaintMap(stream);
    }

    /**
     * Reads a {@code Map} of ({@code Comparable}, {@code Paint})
     * elements from a stream.
     *
     * @param in  the input stream.
     *
     * @return The map.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     *
     * @see #writePaintMap(Map, ObjectOutputStream)
     */
    private Map readPaintMap(ObjectInputStream in) throws IOException, ClassNotFoundException {
        boolean isNull = in.readBoolean();
        if (isNull) {
            return null;
        }
        Map result = new HashMap();
        int count = in.readInt();
        for (int i = 0; i < count; i++) {
            Comparable category = (Comparable) in.readObject();
            Paint paint = SerialUtils.readPaint(in);
            result.put(category, paint);
        }
        return result;
    }

    /**
     * Writes a map of ({@code Comparable}, {@code Paint})
     * elements to a stream.
     *
     * @param map  the map ({@code null} permitted).
     *
     * @param out
     * @throws IOException
     *
     * @see #readPaintMap(ObjectInputStream)
     */
    private void writePaintMap(Map map, ObjectOutputStream out) throws IOException {
        if (map == null) {
            out.writeBoolean(true);
        } else {
            out.writeBoolean(false);
            Set keys = map.keySet();
            int count = keys.size();
            out.writeInt(count);
            for (Object o : keys) {
                Comparable key = (Comparable) o;
                out.writeObject(key);
                SerialUtils.writePaint((Paint) map.get(key), out);
            }
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
 * -------------------------
 * TimeSeriesTableModel.java
 * -------------------------
 * (C) Copyright 2001-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * Wrapper around a time series to convert it to a table model for use in
 * a {@code JTable}.
 */
public class TimeSeriesTableModel extends AbstractTableModel implements SeriesChangeListener {

    /**
     * The series.
     */
    private TimeSeries series;

    /**
     * A flag that controls whether the series is editable.
     */
    private boolean editable;

    /**
     * The new time period.
     */
    private RegularTimePeriod newTimePeriod;

    /**
     * The new value.
     */
    private Number newValue;

    /**
     * Default constructor.
     */
    public TimeSeriesTableModel() {
        this(new TimeSeries("Untitled"));
    }

    /**
     * Constructs a table model for a time series.
     *
     * @param series  the time series.
     */
    public TimeSeriesTableModel(TimeSeries series) {
        this(series, false);
    }

    /**
     * Creates a table model based on a time series.
     *
     * @param series  the time series.
     * @param editable  if {@code true}, the table is editable.
     */
    public TimeSeriesTableModel(TimeSeries series, boolean editable) {
        this.series = series;
        this.series.addChangeListener(this);
        this.editable = editable;
    }

    /**
     * Returns the number of columns in the table model.  For this particular
     * model, the column count is fixed at 2.
     *
     * @return The column count.
     */
    @Override
    public int getColumnCount() {
        return 2;
    }

    /**
     * Returns the column class in the table model.
     *
     * @param column  the column index.
     *
     * @return The column class in the table model.
     */
    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return String.class;
        } else {
            if (column == 1) {
                return Double.class;
            } else {
                return null;
            }
        }
    }

    /**
     * Returns the name of a column
     *
     * @param column  the column index.
     *
     * @return The name of a column.
     */
    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Period:";
        } else {
            if (column == 1) {
                return "Value:";
            } else {
                return null;
            }
        }
    }

    /**
     * Returns the number of rows in the table model.
     *
     * @return The row count.
     */
    @Override
    public int getRowCount() {
        return this.series.getItemCount();
    }

    /**
     * Returns the data value for a cell in the table model.
     *
     * @param row  the row number.
     * @param column  the column number.
     *
     * @return The data value for a cell in the table model.
     */
    @Override
    public Object getValueAt(int row, int column) {
        if (row < this.series.getItemCount()) {
            if (column == 0) {
                return this.series.getTimePeriod(row);
            } else {
                if (column == 1) {
                    return this.series.getValue(row);
                } else {
                    return null;
                }
            }
        } else {
            if (column == 0) {
                return this.newTimePeriod;
            } else {
                if (column == 1) {
                    return this.newValue;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Returns a flag indicating whether the specified cell is editable.
     *
     * @param row  the row number.
     * @param column  the column number.
     *
     * @return {@code true} if the specified cell is editable.
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        if (this.editable) {
            if ((column == 0) || (column == 1)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Updates the time series.
     *
     * @param value  the new value.
     * @param row  the row.
     * @param column  the column.
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
        if (row < this.series.getItemCount()) {
            // update the time series appropriately
            if (column == 1) {
                try {
                    Double v = Double.valueOf(value.toString());
                    this.series.update(row, v);
                } catch (NumberFormatException nfe) {
                    System.err.println("Number format exception");
                }
            }
        } else {
            if (column == 0) {
                // this.series.getClass().valueOf(value.toString());
                this.newTimePeriod = null;
            } else if (column == 1) {
                this.newValue = Double.valueOf(value.toString());
            }
        }
    }

    /**
     * Receives notification that the time series has been changed.  Responds
     * by firing a table data change event.
     *
     * @param event  the event.
     */
    @Override
    public void seriesChanged(SeriesChangeEvent event) {
        fireTableDataChanged();
    }
}
