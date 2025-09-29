package DEF.cd;
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
 * ---------------
 * LegendItem.java
 * ---------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Luke Quinane;
 *
 */
/**
 * A temporary storage object for recording the properties of a legend item,
 * without any consideration for layout issues.
 */
public class LegendItem implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -797214582948827144L;

    /**
     * The dataset.
     */
    private Dataset dataset;

    /**
     * The series key.
     */
    private Comparable seriesKey;

    /**
     * The dataset index.
     */
    private int datasetIndex;

    /**
     * The series index.
     */
    private int series;

    /**
     * The label.
     */
    private final String label;

    /**
     * The label font ({@code null} is permitted).
     */
    private Font labelFont;

    /**
     * The label paint ({@code null} is permitted).
     */
    private transient Paint labelPaint;

    /**
     * The attributed label (if null, fall back to the regular label).
     */
    private transient AttributedString attributedLabel;

    /**
     * The description (not currently used - could be displayed as a tool tip).
     */
    private String description;

    /**
     * The tool tip text.
     */
    private String toolTipText;

    /**
     * The url text.
     */
    private String urlText;

    /**
     * A flag that controls whether the shape is visible.
     */
    private boolean shapeVisible;

    /**
     * The shape.
     */
    private transient Shape shape;

    /**
     * A flag that controls whether the shape is filled.
     */
    private final boolean shapeFilled;

    /**
     * The paint.
     */
    private transient Paint fillPaint;

    /**
     * A gradient paint transformer.
     */
    private GradientPaintTransformer fillPaintTransformer;

    /**
     * A flag that controls whether the shape outline is visible.
     */
    private final boolean shapeOutlineVisible;

    /**
     * The outline paint.
     */
    private transient Paint outlinePaint;

    /**
     * The outline stroke.
     */
    private transient Stroke outlineStroke;

    /**
     * A flag that controls whether the line is visible.
     */
    private boolean lineVisible;

    /**
     * The line.
     */
    private transient Shape line;

    /**
     * The stroke.
     */
    private transient Stroke lineStroke;

    /**
     * The line paint.
     */
    private transient Paint linePaint;

    /**
     * The shape must be non-null for a LegendItem - if no shape is required,
     * use this.
     */
    private static final Shape UNUSED_SHAPE = new Line2D.Float();

    /**
     * The stroke must be non-null for a LegendItem - if no stroke is required,
     * use this.
     */
    private static final Stroke UNUSED_STROKE = new BasicStroke(0.0f);

    /**
     * Creates a legend item with the specified label.  The remaining
     * attributes take default values.
     *
     * @param label  the label ({@code null} not permitted).
     */
    public LegendItem(String label) {
        this(label, Color.BLACK);
    }

    /**
     * Creates a legend item with the specified label and fill paint.  The
     * remaining attributes take default values.
     *
     * @param label  the label ({@code null} not permitted).
     * @param paint  the paint ({@code null} not permitted).
     */
    public LegendItem(String label, Paint paint) {
        this(label, null, null, null, new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0), paint);
    }

    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined */
        false, Color.BLACK, UNUSED_STROKE, /* line visible */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item with a filled and outlined shape.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param outlinePaint  the outline paint ({@code null} not
     *                      permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint, Stroke outlineStroke, Paint outlinePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined = */
        true, outlinePaint, outlineStroke, /* line visible */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item using a line.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the line stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, Shape line, Stroke lineStroke, Paint linePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        false, UNUSED_SHAPE, /* shape filled = */
        false, Color.BLACK, /* shape outlined = */
        false, Color.BLACK, UNUSED_STROKE, /* line visible = */
        true, line, lineStroke, linePaint);
    }

    /**
     * Creates a new legend item.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description (not currently used,
     *        {@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shapeVisible  a flag that controls whether the shape is
     *                      displayed.
     * @param shape  the shape ({@code null} permitted).
     * @param shapeFilled  a flag that controls whether the shape is
     *                     filled.
     * @param fillPaint  the fill paint ({@code null} not permitted).
     * @param shapeOutlineVisible  a flag that controls whether the
     *                             shape is outlined.
     * @param outlinePaint  the outline paint ({@code null} not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param lineVisible  a flag that controls whether the line is
     *                     visible.
     * @param line  the line.
     * @param lineStroke  the stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, boolean shapeVisible, Shape shape, boolean shapeFilled, Paint fillPaint, boolean shapeOutlineVisible, Paint outlinePaint, Stroke outlineStroke, boolean lineVisible, Shape line, Stroke lineStroke, Paint linePaint) {
        Args.nullNotPermitted(label, "label");
        Args.nullNotPermitted(fillPaint, "fillPaint");
        Args.nullNotPermitted(lineStroke, "lineStroke");
        Args.nullNotPermitted(outlinePaint, "outlinePaint");
        Args.nullNotPermitted(outlineStroke, "outlineStroke");
        this.label = label;
        this.labelPaint = null;
        this.attributedLabel = null;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined = */
        false, Color.BLACK, UNUSED_STROKE, /* line visible = */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item with a filled and outlined shape.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param outlinePaint  the outline paint ({@code null} not
     *                      permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint, Stroke outlineStroke, Paint outlinePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined = */
        true, outlinePaint, outlineStroke, /* line visible = */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item using a line.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the line stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape line, Stroke lineStroke, Paint linePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        false, UNUSED_SHAPE, /* shape filled = */
        false, Color.BLACK, /* shape outlined = */
        false, Color.BLACK, UNUSED_STROKE, /* line visible = */
        true, line, lineStroke, linePaint);
    }

    /**
     * Creates a new legend item.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description (not currently used,
     *        {@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shapeVisible  a flag that controls whether the shape is
     *                      displayed.
     * @param shape  the shape ({@code null} permitted).
     * @param shapeFilled  a flag that controls whether the shape is
     *                     filled.
     * @param fillPaint  the fill paint ({@code null} not permitted).
     * @param shapeOutlineVisible  a flag that controls whether the
     *                             shape is outlined.
     * @param outlinePaint  the outline paint ({@code null} not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param lineVisible  a flag that controls whether the line is
     *                     visible.
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, boolean shapeVisible, Shape shape, boolean shapeFilled, Paint fillPaint, boolean shapeOutlineVisible, Paint outlinePaint, Stroke outlineStroke, boolean lineVisible, Shape line, Stroke lineStroke, Paint linePaint) {
        Args.nullNotPermitted(label, "label");
        Args.nullNotPermitted(fillPaint, "fillPaint");
        Args.nullNotPermitted(lineStroke, "lineStroke");
        Args.nullNotPermitted(line, "line");
        Args.nullNotPermitted(linePaint, "linePaint");
        Args.nullNotPermitted(outlinePaint, "outlinePaint");
        Args.nullNotPermitted(outlineStroke, "outlineStroke");
        this.label = characterIteratorToString(label.getIterator());
        this.attributedLabel = label;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    /**
     * Returns a string containing the characters from the given iterator.
     *
     * @param iterator  the iterator ({@code null} not permitted).
     *
     * @return A string.
     */
    private String characterIteratorToString(CharacterIterator iterator) {
        int endIndex = iterator.getEndIndex();
        int beginIndex = iterator.getBeginIndex();
        int count = endIndex - beginIndex;
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        int i = 0;
        char c = iterator.first();
        while (c != CharacterIterator.DONE) {
            chars[i] = c;
            i++;
            c = iterator.next();
        }
        return new String(chars);
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset.
     *
     * @see #setDatasetIndex(int)
     */
    public Dataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset.
     *
     * @param dataset  the dataset.
     */
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns the dataset index for this legend item.
     *
     * @return The dataset index.
     *
     * @see #setDatasetIndex(int)
     * @see #getDataset()
     */
    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    /**
     * Sets the dataset index for this legend item.
     *
     * @param index  the index.
     *
     * @see #getDatasetIndex()
     */
    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
    }

    /**
     * Returns the series key.
     *
     * @return The series key.
     *
     * @see #setSeriesKey(Comparable)
     */
    public Comparable getSeriesKey() {
        return this.seriesKey;
    }

    /**
     * Sets the series key.
     *
     * @param key  the series key.
     */
    public void setSeriesKey(Comparable key) {
        this.seriesKey = key;
    }

    /**
     * Returns the series index for this legend item.
     *
     * @return The series index.
     */
    public int getSeriesIndex() {
        return this.series;
    }

    /**
     * Sets the series index for this legend item.
     *
     * @param index  the index.
     */
    public void setSeriesIndex(int index) {
        this.series = index;
    }

    /**
     * Returns the label.
     *
     * @return The label (never {@code null}).
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the label font.
     *
     * @return The label font (possibly {@code null}).
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font.
     *
     * @param font  the font ({@code null} permitted).
     */
    public void setLabelFont(Font font) {
        this.labelFont = font;
    }

    /**
     * Returns the paint used to draw the label.
     *
     * @return The paint (possibly {@code null}).
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the paint used to draw the label.
     *
     * @param paint  the paint ({@code null} permitted).
     */
    public void setLabelPaint(Paint paint) {
        this.labelPaint = paint;
    }

    /**
     * Returns the attributed label.
     *
     * @return The attributed label (possibly {@code null}).
     */
    public AttributedString getAttributedLabel() {
        return this.attributedLabel;
    }

    /**
     * Returns the description for the legend item.
     *
     * @return The description (possibly {@code null}).
     *
     * @see #setDescription(java.lang.String)
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for this legend item.
     *
     * @param text  the description ({@code null} permitted).
     *
     * @see #getDescription()
     */
    public void setDescription(String text) {
        this.description = text;
    }

    /**
     * Returns the tool tip text.
     *
     * @return The tool tip text (possibly {@code null}).
     *
     * @see #setToolTipText(java.lang.String)
     */
    public String getToolTipText() {
        return this.toolTipText;
    }

    /**
     * Sets the tool tip text for this legend item.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getToolTipText()
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    /**
     * Returns the URL text.
     *
     * @return The URL text (possibly {@code null}).
     *
     * @see #setURLText(java.lang.String)
     */
    public String getURLText() {
        return this.urlText;
    }

    /**
     * Sets the URL text.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getURLText()
     */
    public void setURLText(String text) {
        this.urlText = text;
    }

    /**
     * Returns a flag that indicates whether the shape is visible.
     *
     * @return A boolean.
     *
     * @see #setShapeVisible(boolean)
     */
    public boolean isShapeVisible() {
        return this.shapeVisible;
    }

    /**
     * Sets the flag that controls whether the shape is visible.
     *
     * @param visible  the new flag value.
     *
     * @see #isShapeVisible()
     * @see #isLineVisible()
     */
    public void setShapeVisible(boolean visible) {
        this.shapeVisible = visible;
    }

    /**
     * Returns the shape used to label the series represented by this legend
     * item.
     *
     * @return The shape (never {@code null}).
     *
     * @see #setShape(java.awt.Shape)
     */
    public Shape getShape() {
        return this.shape;
    }

    /**
     * Sets the shape for the legend item.
     *
     * @param shape  the shape ({@code null} not permitted).
     *
     * @see #getShape()
     */
    public void setShape(Shape shape) {
        Args.nullNotPermitted(shape, "shape");
        this.shape = shape;
    }

    /**
     * Returns a flag that controls whether the shape is filled.
     *
     * @return A boolean.
     */
    public boolean isShapeFilled() {
        return this.shapeFilled;
    }

    /**
     * Returns the fill paint.
     *
     * @return The fill paint (never {@code null}).
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Sets the fill paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setFillPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.fillPaint = paint;
    }

    /**
     * Returns the flag that controls whether the shape outline
     * is visible.
     *
     * @return A boolean.
     */
    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }

    /**
     * Returns the line stroke for the series.
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getLineStroke() {
        return this.lineStroke;
    }

    /**
     * Sets the line stroke.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     */
    public void setLineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.lineStroke = stroke;
    }

    /**
     * Returns the paint used for lines.
     *
     * @return The paint (never {@code null}).
     */
    public Paint getLinePaint() {
        return this.linePaint;
    }

    /**
     * Sets the line paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setLinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.linePaint = paint;
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (never {@code null}).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.outlinePaint = paint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (never {@code null}).
     *
     * @see #setOutlineStroke(java.awt.Stroke)
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getOutlineStroke()
     */
    public void setOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.outlineStroke = stroke;
    }

    /**
     * Returns a flag that indicates whether the line is visible.
     *
     * @return A boolean.
     *
     * @see #setLineVisible(boolean)
     */
    public boolean isLineVisible() {
        return this.lineVisible;
    }

    /**
     * Sets the flag that controls whether the line shape is visible for
     * this legend item.
     *
     * @param visible  the new flag value.
     *
     * @see #isLineVisible()
     */
    public void setLineVisible(boolean visible) {
        this.lineVisible = visible;
    }

    /**
     * Returns the line.
     *
     * @return The line (never {@code null}).
     *
     * @see #setLine(java.awt.Shape)
     * @see #isLineVisible()
     */
    public Shape getLine() {
        return this.line;
    }

    /**
     * Sets the line.
     *
     * @param line  the line ({@code null} not permitted).
     *
     * @see #getLine()
     */
    public void setLine(Shape line) {
        Args.nullNotPermitted(line, "line");
        this.line = line;
    }

    /**
     * Returns the transformer used when the fill paint is an instance of
     * {@code GradientPaint}.
     *
     * @return The transformer (never {@code null}).
     *
     * @see #setFillPaintTransformer(GradientPaintTransformer)
     */
    public GradientPaintTransformer getFillPaintTransformer() {
        return this.fillPaintTransformer;
    }

    /**
     * Sets the transformer used when the fill paint is an instance of
     * {@code GradientPaint}.
     *
     * @param transformer  the transformer ({@code null} not permitted).
     *
     * @see #getFillPaintTransformer()
     */
    public void setFillPaintTransformer(GradientPaintTransformer transformer) {
        Args.nullNotPermitted(transformer, "transformer");
        this.fillPaintTransformer = transformer;
    }

    /**
     * Tests this item for equality with an arbitrary object.
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
        if (!(obj instanceof LegendItem)) {
            return false;
        }
        LegendItem that = (LegendItem) obj;
        if (this.datasetIndex != that.datasetIndex) {
            return false;
        }
        if (this.series != that.series) {
            return false;
        }
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!AttributedStringUtils.equal(this.attributedLabel, that.attributedLabel)) {
            return false;
        }
        if (!Objects.equals(this.description, that.description)) {
            return false;
        }
        if (this.shapeVisible != that.shapeVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.shape, that.shape)) {
            return false;
        }
        if (this.shapeFilled != that.shapeFilled) {
            return false;
        }
        if (!PaintUtils.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!Objects.equals(this.fillPaintTransformer, that.fillPaintTransformer)) {
            return false;
        }
        if (this.shapeOutlineVisible != that.shapeOutlineVisible) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!this.lineVisible == that.lineVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.line, that.line)) {
            return false;
        }
        if (!this.lineStroke.equals(that.lineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.linePaint, that.linePaint)) {
            return false;
        }
        if (!Objects.equals(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.datasetIndex;
        hash = 79 * hash + this.series;
        hash = 79 * hash + Objects.hashCode(this.label);
        hash = 79 * hash + Objects.hashCode(this.labelFont);
        hash = 79 * hash + Objects.hashCode(this.labelPaint);
        hash = 79 * hash + Objects.hashCode(this.attributedLabel);
        hash = 79 * hash + Objects.hashCode(this.description);
        hash = 79 * hash + (this.shapeVisible ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.shape);
        hash = 79 * hash + (this.shapeFilled ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.fillPaint);
        hash = 79 * hash + Objects.hashCode(this.fillPaintTransformer);
        hash = 79 * hash + (this.shapeOutlineVisible ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.outlinePaint);
        hash = 79 * hash + Objects.hashCode(this.outlineStroke);
        hash = 79 * hash + (this.lineVisible ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.line);
        hash = 79 * hash + Objects.hashCode(this.lineStroke);
        hash = 79 * hash + Objects.hashCode(this.linePaint);
        return hash;
    }

    /**
     * Returns an independent copy of this object (except that the clone will
     * still reference the same dataset as the original {@code LegendItem}).
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the legend item cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        LegendItem clone = (LegendItem) super.clone();
        if (this.seriesKey instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.seriesKey;
            clone.seriesKey = (Comparable) pc.clone();
        }
        // FIXME: Clone the attributed string if it is not null
        clone.shape = CloneUtils.clone(this.shape);
        if (this.fillPaintTransformer instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.fillPaintTransformer;
            clone.fillPaintTransformer = (GradientPaintTransformer) pc.clone();
        }
        clone.line = CloneUtils.clone(this.line);
        return clone;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream ({@code null} not permitted).
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeAttributedString(this.attributedLabel, stream);
        SerialUtils.writeShape(this.shape, stream);
        SerialUtils.writePaint(this.fillPaint, stream);
        SerialUtils.writeStroke(this.outlineStroke, stream);
        SerialUtils.writePaint(this.outlinePaint, stream);
        SerialUtils.writeShape(this.line, stream);
        SerialUtils.writeStroke(this.lineStroke, stream);
        SerialUtils.writePaint(this.linePaint, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
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
        this.attributedLabel = SerialUtils.readAttributedString(stream);
        this.shape = SerialUtils.readShape(stream);
        this.fillPaint = SerialUtils.readPaint(stream);
        this.outlineStroke = SerialUtils.readStroke(stream);
        this.outlinePaint = SerialUtils.readPaint(stream);
        this.line = SerialUtils.readShape(stream);
        this.lineStroke = SerialUtils.readStroke(stream);
        this.linePaint = SerialUtils.readPaint(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
    }
}
