package LOC.v;
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
 * AbstractRenderer.java
 * ---------------------
 * (C) Copyright 2002-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Nicolas Brodu;
 *                   Yuri Blankenstein;
 *
 */
/**
 * Base class providing common services for renderers.  Most methods that update
 * attributes of the renderer will fire a {@link RendererChangeEvent}, which
 * normally means the plot that owns the renderer will receive notification that
 * the renderer has been changed (the plot will, in turn, notify the chart).
 * <p>
 * <b>Subclassing</b>
 * If you create your own renderer that is a subclass of this, you should take
 * care to ensure that the renderer implements cloning correctly, to ensure
 * that {@link JFreeChart} instances that use your renderer are also
 * cloneable.  It is recommended that you also implement the
 * {@link PublicCloneable} interface to provide simple access to the clone
 * method.
 */
public abstract class AbstractRenderer implements ChartElement, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -828267569428206075L;

    /**
     * Zero represented as a {@code double}.
     */
    public static final Double ZERO = 0.0;

    /**
     * The default paint.
     */
    public static final Paint DEFAULT_PAINT = Color.BLUE;

    /**
     * The default outline paint.
     */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.GRAY;

    /**
     * The default stroke.
     */
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);

    /**
     * The default outline stroke.
     */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(1.0f);

    /**
     * The default shape.
     */
    public static final Shape DEFAULT_SHAPE = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);

    /**
     * The default value label font.
     */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * The default value label paint.
     */
    public static final Paint DEFAULT_VALUE_LABEL_PAINT = Color.BLACK;

    /**
     * The default item label insets.
     */
    public static final RectangleInsets DEFAULT_ITEM_LABEL_INSETS = new RectangleInsets(2.0, 2.0, 2.0, 2.0);

    /**
     * A list of flags that controls whether each series is visible.
     */
    private Map<Integer, Boolean> seriesVisibleMap;

    /**
     * The default visibility for all series.
     */
    private boolean defaultSeriesVisible;

    /**
     * A list of flags that controls whether each series is visible in
     * the legend.
     */
    private Map<Integer, Boolean> seriesVisibleInLegendMap;

    /**
     * The default visibility for each series in the legend.
     */
    private boolean defaultSeriesVisibleInLegend;

    /**
     * The paint for each series.
     */
    private transient Map<Integer, Paint> seriesPaintMap;

    /**
     * A flag that controls whether the paintList is autopopulated
     * in the {@link #lookupSeriesPaint(int)} method.
     */
    private boolean autoPopulateSeriesPaint;

    /**
     * The default paint, used when there is no paint assigned for a series.
     */
    private transient Paint defaultPaint;

    /**
     * The fill paint list.
     */
    private transient Map<Integer, Paint> seriesFillPaintMap;

    /**
     * A flag that controls whether the fillPaintList is autopopulated
     * in the {@link #lookupSeriesFillPaint(int)} method.
     */
    private boolean autoPopulateSeriesFillPaint;

    /**
     * The base fill paint.
     */
    private transient Paint defaultFillPaint;

    /**
     * The outline paint list.
     */
    private transient Map<Integer, Paint> seriesOutlinePaintMap;

    /**
     * A flag that controls whether the outlinePaintList is
     * autopopulated in the {@link #lookupSeriesOutlinePaint(int)} method.
     */
    private boolean autoPopulateSeriesOutlinePaint;

    /**
     * The base outline paint.
     */
    private transient Paint defaultOutlinePaint;

    /**
     * The stroke list.
     */
    private transient Map<Integer, Stroke> seriesStrokeMap;

    /**
     * A flag that controls whether the strokeList is autopopulated
     * in the {@link #lookupSeriesStroke(int)} method.
     */
    private boolean autoPopulateSeriesStroke;

    /**
     * The base stroke.
     */
    private transient Stroke defaultStroke;

    /**
     * The outline stroke list.
     */
    private transient Map<Integer, Stroke> seriesOutlineStrokeMap;

    /**
     * The base outline stroke.
     */
    private transient Stroke defaultOutlineStroke;

    /**
     * A flag that controls whether the outlineStrokeList is
     * autopopulated in the {@link #lookupSeriesOutlineStroke(int)} method.
     */
    private boolean autoPopulateSeriesOutlineStroke;

    /**
     * The shapes to use for specific series.
     */
    private Map<Integer, Shape> seriesShapeMap;

    /**
     * A flag that controls whether the series shapes are autopopulated
     * in the {@link #lookupSeriesShape(int)} method.
     */
    private boolean autoPopulateSeriesShape;

    /**
     * The base shape.
     */
    private transient Shape defaultShape;

    /**
     * Visibility of the item labels PER series.
     */
    private Map<Integer, Boolean> seriesItemLabelsVisibleMap;

    /**
     * The base item labels visible.
     */
    private boolean defaultItemLabelsVisible;

    /**
     * The item label font list (one font per series).
     */
    private Map<Integer, Font> itemLabelFontMap;

    /**
     * The base item label font.
     */
    private Font defaultItemLabelFont;

    /**
     * The item label paint list (one paint per series).
     */
    private transient Map<Integer, Paint> itemLabelPaints;

    /**
     * The base item label paint.
     */
    private transient Paint defaultItemLabelPaint;

    /**
     * Option to use contrast colors for item labels
     */
    private boolean computeItemLabelContrastColor;

    /**
     * The positive item label position (per series).
     */
    private Map<Integer, ItemLabelPosition> positiveItemLabelPositionMap;

    /**
     * The fallback positive item label position.
     */
    private ItemLabelPosition defaultPositiveItemLabelPosition;

    /**
     * The negative item label position (per series).
     */
    private Map<Integer, ItemLabelPosition> negativeItemLabelPositionMap;

    /**
     * The fallback negative item label position.
     */
    private ItemLabelPosition defaultNegativeItemLabelPosition;

    /**
     * The item label insets.
     */
    private RectangleInsets itemLabelInsets;

    /**
     * Flags that control whether entities are generated for each
     * series.  This will be overridden by 'createEntities'.
     */
    private Map<Integer, Boolean> seriesCreateEntitiesMap;

    /**
     * The default flag that controls whether entities are generated.
     * This flag is used when both the above flags return null.
     */
    private boolean defaultCreateEntities;

    /**
     * The per-series legend shape settings.
     */
    private Map<Integer, Shape> seriesLegendShapes;

    /**
     * The base shape for legend items.  If this is {@code null}, the
     * series shape will be used.
     */
    private transient Shape defaultLegendShape;

    /**
     * A special flag that, if true, will cause the getLegendItem() method
     * to configure the legend shape as if it were a line.
     */
    private boolean treatLegendShapeAsLine;

    /**
     * The per-series legend text font.
     */
    private Map<Integer, Font> legendTextFontMap;

    /**
     * The base legend font.
     */
    private Font defaultLegendTextFont;

    /**
     * The per series legend text paint settings.
     */
    private transient Map<Integer, Paint> legendTextPaints;

    /**
     * The default paint for the legend text items. If this is
     * {@code null}, the {@link LegendTitle} class will determine the
     * text paint to use.
     */
    private transient Paint defaultLegendTextPaint;

    /**
     * A flag that controls whether the renderer will include the
     * non-visible series when calculating the data bounds.
     */
    private boolean dataBoundsIncludesVisibleSeriesOnly = true;

    /**
     * The default radius for the entity 'hotspot'
     */
    private int defaultEntityRadius;

    /**
     * Storage for registered change listeners.
     */
    private transient EventListenerList listenerList;

    /**
     * An event for re-use.
     */
    private transient RendererChangeEvent event;

    /**
     * Default constructor.
     */
    public AbstractRenderer() {
        this.seriesVisibleMap = new HashMap<>();
        this.defaultSeriesVisible = true;
        this.seriesVisibleInLegendMap = new HashMap<>();
        this.defaultSeriesVisibleInLegend = true;
        this.seriesPaintMap = new HashMap<>();
        this.defaultPaint = DEFAULT_PAINT;
        this.autoPopulateSeriesPaint = true;
        this.seriesFillPaintMap = new HashMap<>();
        this.defaultFillPaint = Color.WHITE;
        this.autoPopulateSeriesFillPaint = false;
        this.seriesOutlinePaintMap = new HashMap<>();
        this.defaultOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.autoPopulateSeriesOutlinePaint = false;
        this.seriesStrokeMap = new HashMap<>();
        this.defaultStroke = DEFAULT_STROKE;
        this.autoPopulateSeriesStroke = true;
        this.seriesOutlineStrokeMap = new HashMap<>();
        this.defaultOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.autoPopulateSeriesOutlineStroke = false;
        this.seriesShapeMap = new HashMap<>();
        this.defaultShape = DEFAULT_SHAPE;
        this.autoPopulateSeriesShape = true;
        this.seriesItemLabelsVisibleMap = new HashMap<>();
        this.defaultItemLabelsVisible = false;
        this.itemLabelInsets = DEFAULT_ITEM_LABEL_INSETS;
        this.itemLabelFontMap = new HashMap<>();
        this.defaultItemLabelFont = new Font("SansSerif", Font.PLAIN, 10);
        this.itemLabelPaints = new HashMap<>();
        this.defaultItemLabelPaint = Color.BLACK;
        this.computeItemLabelContrastColor = false;
        this.positiveItemLabelPositionMap = new HashMap<>();
        this.defaultPositiveItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        this.negativeItemLabelPositionMap = new HashMap<>();
        this.defaultNegativeItemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        this.seriesCreateEntitiesMap = new HashMap<>();
        this.defaultCreateEntities = true;
        this.defaultEntityRadius = 3;
        this.seriesLegendShapes = new HashMap<>();
        this.defaultLegendShape = null;
        this.treatLegendShapeAsLine = false;
        this.legendTextFontMap = new HashMap<>();
        this.defaultLegendTextFont = null;
        this.legendTextPaints = new HashMap<>();
        this.defaultLegendTextPaint = null;
        this.listenerList = new EventListenerList();
    }

    /**
     * Receives a chart element visitor.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier.
     */
    public abstract DrawingSupplier getDrawingSupplier();

    /**
     * Adds a {@code KEY_BEGIN_ELEMENT} hint to the graphics target.  This
     * hint is recognised by <b>JFreeSVG</b> (in theory it could be used by
     * other {@code Graphics2D} implementations also).
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param key  the key ({@code null} not permitted).
     *
     * @see #endElementGroup(java.awt.Graphics2D)
     */
    protected void beginElementGroup(Graphics2D g2, ItemKey key) {
        Args.nullNotPermitted(key, "key");
        Map<String, String> m = new HashMap<>(1);
        m.put("ref", key.toJSONString());
        g2.setRenderingHint(ChartHints.KEY_BEGIN_ELEMENT, m);
    }

    /**
     * Adds a {@code KEY_END_ELEMENT} hint to the graphics target.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     *
     * @see #beginElementGroup(java.awt.Graphics2D, org.jfree.data.ItemKey)
     */
    protected void endElementGroup(Graphics2D g2) {
        g2.setRenderingHint(ChartHints.KEY_END_ELEMENT, Boolean.TRUE);
    }

    // SERIES VISIBLE (not yet respected by all renderers)
    /**
     * Returns a boolean that indicates whether the specified item
     * should be drawn.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return A boolean.
     */
    public boolean getItemVisible(int series, int item) {
        return isSeriesVisible(series);
    }

    /**
     * Returns a boolean that indicates whether the specified series
     * should be drawn.  In fact this method should be named
     * lookupSeriesVisible() to be consistent with the other series
     * attributes and avoid confusion with the getSeriesVisible() method.
     *
     * @param series  the series index.
     *
     * @return A boolean.
     */
    public boolean isSeriesVisible(int series) {
        boolean result = this.defaultSeriesVisible;
        Boolean b = this.seriesVisibleMap.get(series);
        if (b != null) {
            result = b;
        }
        return result;
    }

    /**
     * Returns the flag that controls whether a series is visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesVisible(int, Boolean)
     */
    public Boolean getSeriesVisible(int series) {
        return this.seriesVisibleMap.get(series);
    }

    /**
     * Sets the flag that controls whether a series is visible and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #getSeriesVisible(int)
     */
    public void setSeriesVisible(int series, Boolean visible) {
        setSeriesVisible(series, visible, true);
    }

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
    public void setSeriesVisible(int series, Boolean visible, boolean notify) {
        this.seriesVisibleMap.put(series, visible);
        if (notify) {
            // we create an event with a special flag set...the purpose of
            // this is to communicate to the plot (the default receiver of
            // the event) that series visibility has changed so the axis
            // ranges might need updating...
            RendererChangeEvent e = new RendererChangeEvent(this, true);
            notifyListeners(e);
        }
    }

    /**
     * Returns the default visibility for all series.
     *
     * @return The default visibility.
     *
     * @see #setDefaultSeriesVisible(boolean)
     */
    public boolean getDefaultSeriesVisible() {
        return this.defaultSeriesVisible;
    }

    /**
     * Sets the default series visibility and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultSeriesVisible()
     */
    public void setDefaultSeriesVisible(boolean visible) {
        // defer argument checking...
        setDefaultSeriesVisible(visible, true);
    }

    /**
     * Sets the default series visibility and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultSeriesVisible()
     */
    public void setDefaultSeriesVisible(boolean visible, boolean notify) {
        this.defaultSeriesVisible = visible;
        if (notify) {
            // we create an event with a special flag set...the purpose of
            // this is to communicate to the plot (the default receiver of
            // the event) that series visibility has changed so the axis
            // ranges might need updating...
            RendererChangeEvent e = new RendererChangeEvent(this, true);
            notifyListeners(e);
        }
    }

    // SERIES VISIBLE IN LEGEND (not yet respected by all renderers)
    /**
     * Returns {@code true} if the series should be shown in the legend,
     * and {@code false} otherwise.
     *
     * @param series  the series index.
     *
     * @return A boolean.
     */
    public boolean isSeriesVisibleInLegend(int series) {
        boolean result = this.defaultSeriesVisibleInLegend;
        Boolean b = this.seriesVisibleInLegendMap.get(series);
        if (b != null) {
            result = b;
        }
        return result;
    }

    /**
     * Returns the flag that controls whether a series is visible in the
     * legend.  This method returns only the "per series" settings - to
     * incorporate the default settings as well, you need to use the
     * {@link #isSeriesVisibleInLegend(int)} method.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesVisibleInLegend(int, Boolean)
     */
    public Boolean getSeriesVisibleInLegend(int series) {
        return this.seriesVisibleInLegendMap.get(series);
    }

    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #getSeriesVisibleInLegend(int)
     */
    public void setSeriesVisibleInLegend(int series, Boolean visible) {
        setSeriesVisibleInLegend(series, visible, true);
    }

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
    public void setSeriesVisibleInLegend(int series, Boolean visible, boolean notify) {
        this.seriesVisibleInLegendMap.put(series, visible);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default visibility in the legend for all series.
     *
     * @return The default visibility.
     *
     * @see #setDefaultSeriesVisibleInLegend(boolean)
     */
    public boolean getDefaultSeriesVisibleInLegend() {
        return this.defaultSeriesVisibleInLegend;
    }

    /**
     * Sets the default visibility in the legend and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultSeriesVisibleInLegend()
     */
    public void setDefaultSeriesVisibleInLegend(boolean visible) {
        // defer argument checking...
        setDefaultSeriesVisibleInLegend(visible, true);
    }

    /**
     * Sets the default visibility in the legend and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultSeriesVisibleInLegend()
     */
    public void setDefaultSeriesVisibleInLegend(boolean visible, boolean notify) {
        this.defaultSeriesVisibleInLegend = visible;
        if (notify) {
            fireChangeEvent();
        }
    }

    // PAINT
    /**
     * Returns the paint used to fill data items as they are drawn.
     * (this is typically the same for an entire series).
     * <p>
     * The default implementation passes control to the
     * {@code lookupSeriesPaint()} method. You can override this method
     * if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    public Paint getItemPaint(int row, int column) {
        return lookupSeriesPaint(row);
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    public Paint lookupSeriesPaint(int series) {
        Paint seriesPaint = getSeriesPaint(series);
        if (seriesPaint == null && this.autoPopulateSeriesPaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesPaint = supplier.getNextPaint();
                setSeriesPaint(series, seriesPaint, false);
            }
        }
        if (seriesPaint == null) {
            seriesPaint = this.defaultPaint;
        }
        return seriesPaint;
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesPaint(int, Paint)
     */
    public Paint getSeriesPaint(int series) {
        return this.seriesPaintMap.get(series);
    }

    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesPaint(int)
     */
    public void setSeriesPaint(int series, Paint paint) {
        setSeriesPaint(series, paint, true);
    }

    /**
     * Sets the paint used for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index.
     * @param paint  the paint ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesPaint(int)
     */
    public void setSeriesPaint(int series, Paint paint, boolean notify) {
        this.seriesPaintMap.put(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the series paint settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify  notify listeners?
     */
    public void clearSeriesPaints(boolean notify) {
        this.seriesPaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default paint.
     *
     * @return The default paint (never {@code null}).
     *
     * @see #setDefaultPaint(Paint)
     */
    public Paint getDefaultPaint() {
        return this.defaultPaint;
    }

    /**
     * Sets the default paint and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultPaint()
     */
    public void setDefaultPaint(Paint paint) {
        // defer argument checking...
        setDefaultPaint(paint, true);
    }

    /**
     * Sets the default series paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultPaint()
     */
    public void setDefaultPaint(Paint paint, boolean notify) {
        this.defaultPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the series paint list is
     * automatically populated when {@link #lookupSeriesPaint(int)} is called.
     *
     * @return A boolean.
     *
     * @see #setAutoPopulateSeriesPaint(boolean)
     */
    public boolean getAutoPopulateSeriesPaint() {
        return this.autoPopulateSeriesPaint;
    }

    /**
     * Sets the flag that controls whether the series paint list is
     * automatically populated when {@link #lookupSeriesPaint(int)} is called.
     *
     * @param auto  the new flag value.
     *
     * @see #getAutoPopulateSeriesPaint()
     */
    public void setAutoPopulateSeriesPaint(boolean auto) {
        this.autoPopulateSeriesPaint = auto;
    }

    //// FILL PAINT //////////////////////////////////////////////////////////
    /**
     * Returns the paint used to fill data items as they are drawn.  The
     * default implementation passes control to the
     * {@link #lookupSeriesFillPaint(int)} method - you can override this
     * method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    public Paint getItemFillPaint(int row, int column) {
        return lookupSeriesFillPaint(row);
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (never {@code null}).
     */
    public Paint lookupSeriesFillPaint(int series) {
        Paint seriesFillPaint = getSeriesFillPaint(series);
        if (seriesFillPaint == null && this.autoPopulateSeriesFillPaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesFillPaint = supplier.getNextFillPaint();
                setSeriesFillPaint(series, seriesFillPaint, false);
            }
        }
        if (seriesFillPaint == null) {
            seriesFillPaint = this.defaultFillPaint;
        }
        return seriesFillPaint;
    }

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (never {@code null}).
     *
     * @see #setSeriesFillPaint(int, Paint)
     */
    public Paint getSeriesFillPaint(int series) {
        return this.seriesFillPaintMap.get(series);
    }

    /**
     * Sets the paint used for a series fill and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesFillPaint(int)
     */
    public void setSeriesFillPaint(int series, Paint paint) {
        setSeriesFillPaint(series, paint, true);
    }

    /**
     * Sets the paint used to fill a series and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesFillPaint(int)
     */
    public void setSeriesFillPaint(int series, Paint paint, boolean notify) {
        this.seriesFillPaintMap.put(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default fill paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultFillPaint(Paint)
     */
    public Paint getDefaultFillPaint() {
        return this.defaultFillPaint;
    }

    /**
     * Sets the default fill paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultFillPaint()
     */
    public void setDefaultFillPaint(Paint paint) {
        // defer argument checking...
        setDefaultFillPaint(paint, true);
    }

    /**
     * Sets the default fill paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultFillPaint()
     */
    public void setDefaultFillPaint(Paint paint, boolean notify) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultFillPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the series fill paint list
     * is automatically populated when {@link #lookupSeriesFillPaint(int)} is
     * called.
     *
     * @return A boolean.
     *
     * @see #setAutoPopulateSeriesFillPaint(boolean)
     */
    public boolean getAutoPopulateSeriesFillPaint() {
        return this.autoPopulateSeriesFillPaint;
    }

    /**
     * Sets the flag that controls whether the series fill paint list is
     * automatically populated when {@link #lookupSeriesFillPaint(int)} is
     * called.
     *
     * @param auto  the new flag value.
     *
     * @see #getAutoPopulateSeriesFillPaint()
     */
    public void setAutoPopulateSeriesFillPaint(boolean auto) {
        this.autoPopulateSeriesFillPaint = auto;
    }

    // OUTLINE PAINT //////////////////////////////////////////////////////////
    /**
     * Returns the paint used to outline data items as they are drawn.
     * (this is typically the same for an entire series).
     * <p>
     * The default implementation passes control to the
     * {@link #lookupSeriesOutlinePaint} method.  You can override this method
     * if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    public Paint getItemOutlinePaint(int row, int column) {
        return lookupSeriesOutlinePaint(row);
    }

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (never {@code null}).
     */
    public Paint lookupSeriesOutlinePaint(int series) {
        Paint seriesOutlinePaint = getSeriesOutlinePaint(series);
        if (seriesOutlinePaint == null && this.autoPopulateSeriesOutlinePaint) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                seriesOutlinePaint = supplier.getNextOutlinePaint();
                setSeriesOutlinePaint(series, seriesOutlinePaint, false);
            }
        }
        if (seriesOutlinePaint == null) {
            seriesOutlinePaint = this.defaultOutlinePaint;
        }
        return seriesOutlinePaint;
    }

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesOutlinePaint(int, Paint)
     */
    public Paint getSeriesOutlinePaint(int series) {
        return this.seriesOutlinePaintMap.get(series);
    }

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesOutlinePaint(int)
     */
    public void setSeriesOutlinePaint(int series, Paint paint) {
        setSeriesOutlinePaint(series, paint, true);
    }

    /**
     * Sets the paint used to draw the outline for a series and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesOutlinePaint(int)
     */
    public void setSeriesOutlinePaint(int series, Paint paint, boolean notify) {
        this.seriesOutlinePaintMap.put(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default outline paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultOutlinePaint(Paint)
     */
    public Paint getDefaultOutlinePaint() {
        return this.defaultOutlinePaint;
    }

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultOutlinePaint()
     */
    public void setDefaultOutlinePaint(Paint paint) {
        // defer argument checking...
        setDefaultOutlinePaint(paint, true);
    }

    /**
     * Sets the default outline paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultOutlinePaint()
     */
    public void setDefaultOutlinePaint(Paint paint, boolean notify) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultOutlinePaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the series outline paint
     * list is automatically populated when
     * {@link #lookupSeriesOutlinePaint(int)} is called.
     *
     * @return A boolean.
     *
     * @see #setAutoPopulateSeriesOutlinePaint(boolean)
     */
    public boolean getAutoPopulateSeriesOutlinePaint() {
        return this.autoPopulateSeriesOutlinePaint;
    }

    /**
     * Sets the flag that controls whether the series outline paint list
     * is automatically populated when {@link #lookupSeriesOutlinePaint(int)}
     * is called.
     *
     * @param auto  the new flag value.
     *
     * @see #getAutoPopulateSeriesOutlinePaint()
     */
    public void setAutoPopulateSeriesOutlinePaint(boolean auto) {
        this.autoPopulateSeriesOutlinePaint = auto;
    }

    // STROKE
    /**
     * Returns the stroke used to draw data items.
     * <p>
     * The default implementation passes control to the getSeriesStroke method.
     * You can override this method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getItemStroke(int row, int column) {
        return lookupSeriesStroke(row);
    }

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke lookupSeriesStroke(int series) {
        Stroke result = getSeriesStroke(series);
        if (result == null && this.autoPopulateSeriesStroke) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextStroke();
                setSeriesStroke(series, result, false);
            }
        }
        if (result == null) {
            result = this.defaultStroke;
        }
        return result;
    }

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setSeriesStroke(int, Stroke)
     */
    public Stroke getSeriesStroke(int series) {
        return this.seriesStrokeMap.get(series);
    }

    /**
     * Sets the stroke used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getSeriesStroke(int)
     */
    public void setSeriesStroke(int series, Stroke stroke) {
        setSeriesStroke(series, stroke, true);
    }

    /**
     * Sets the stroke for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesStroke(int)
     */
    public void setSeriesStroke(int series, Stroke stroke, boolean notify) {
        this.seriesStrokeMap.put(series, stroke);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the series stroke settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify  notify listeners?
     */
    public void clearSeriesStrokes(boolean notify) {
        this.seriesStrokeMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default stroke.
     *
     * @return The default stroke (never {@code null}).
     *
     * @see #setDefaultStroke(Stroke)
     */
    public Stroke getDefaultStroke() {
        return this.defaultStroke;
    }

    /**
     * Sets the default stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultStroke()
     */
    public void setDefaultStroke(Stroke stroke) {
        // defer argument checking...
        setDefaultStroke(stroke, true);
    }

    /**
     * Sets the base stroke and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultStroke()
     */
    public void setDefaultStroke(Stroke stroke, boolean notify) {
        Args.nullNotPermitted(stroke, "stroke");
        this.defaultStroke = stroke;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the series stroke list is
     * automatically populated when {@link #lookupSeriesStroke(int)} is called.
     *
     * @return A boolean.
     *
     * @see #setAutoPopulateSeriesStroke(boolean)
     */
    public boolean getAutoPopulateSeriesStroke() {
        return this.autoPopulateSeriesStroke;
    }

    /**
     * Sets the flag that controls whether the series stroke list is
     * automatically populated when {@link #lookupSeriesStroke(int)} is called.
     *
     * @param auto  the new flag value.
     *
     * @see #getAutoPopulateSeriesStroke()
     */
    public void setAutoPopulateSeriesStroke(boolean auto) {
        this.autoPopulateSeriesStroke = auto;
    }

    // OUTLINE STROKE
    /**
     * Returns the stroke used to outline data items.  The default
     * implementation passes control to the
     * {@link #lookupSeriesOutlineStroke(int)} method. You can override this
     * method if you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getItemOutlineStroke(int row, int column) {
        return lookupSeriesOutlineStroke(row);
    }

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke lookupSeriesOutlineStroke(int series) {
        Stroke result = getSeriesOutlineStroke(series);
        if (result == null && this.autoPopulateSeriesOutlineStroke) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextOutlineStroke();
                setSeriesOutlineStroke(series, result, false);
            }
        }
        if (result == null) {
            result = this.defaultOutlineStroke;
        }
        return result;
    }

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setSeriesOutlineStroke(int, Stroke)
     */
    public Stroke getSeriesOutlineStroke(int series) {
        return this.seriesOutlineStrokeMap.get(series);
    }

    /**
     * Sets the outline stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke) {
        setSeriesOutlineStroke(series, stroke, true);
    }

    /**
     * Sets the outline stroke for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index.
     * @param stroke  the stroke ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesOutlineStroke(int)
     */
    public void setSeriesOutlineStroke(int series, Stroke stroke, boolean notify) {
        this.seriesOutlineStrokeMap.put(series, stroke);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default outline stroke.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDefaultOutlineStroke(Stroke)
     */
    public Stroke getDefaultOutlineStroke() {
        return this.defaultOutlineStroke;
    }

    /**
     * Sets the default outline stroke and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultOutlineStroke()
     */
    public void setDefaultOutlineStroke(Stroke stroke) {
        setDefaultOutlineStroke(stroke, true);
    }

    /**
     * Sets the default outline stroke and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getDefaultOutlineStroke()
     */
    public void setDefaultOutlineStroke(Stroke stroke, boolean notify) {
        Args.nullNotPermitted(stroke, "stroke");
        this.defaultOutlineStroke = stroke;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the series outline stroke
     * list is automatically populated when
     * {@link #lookupSeriesOutlineStroke(int)} is called.
     *
     * @return A boolean.
     *
     * @see #setAutoPopulateSeriesOutlineStroke(boolean)
     */
    public boolean getAutoPopulateSeriesOutlineStroke() {
        return this.autoPopulateSeriesOutlineStroke;
    }

    /**
     * Sets the flag that controls whether the series outline stroke list
     * is automatically populated when {@link #lookupSeriesOutlineStroke(int)}
     * is called.
     *
     * @param auto  the new flag value.
     *
     * @see #getAutoPopulateSeriesOutlineStroke()
     */
    public void setAutoPopulateSeriesOutlineStroke(boolean auto) {
        this.autoPopulateSeriesOutlineStroke = auto;
    }

    // SHAPE
    /**
     * Returns a shape used to represent a data item.
     * <p>
     * The default implementation passes control to the
     * {@link #lookupSeriesShape(int)} method. You can override this method if
     * you require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The shape (never {@code null}).
     */
    public Shape getItemShape(int row, int column) {
        return lookupSeriesShape(row);
    }

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The shape (never {@code null}).
     */
    public Shape lookupSeriesShape(int series) {
        Shape result = getSeriesShape(series);
        if (result == null && this.autoPopulateSeriesShape) {
            DrawingSupplier supplier = getDrawingSupplier();
            if (supplier != null) {
                result = supplier.getNextShape();
                setSeriesShape(series, result, false);
            }
        }
        if (result == null) {
            result = this.defaultShape;
        }
        return result;
    }

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The shape (possibly {@code null}).
     *
     * @see #setSeriesShape(int, Shape)
     */
    public Shape getSeriesShape(int series) {
        return this.seriesShapeMap.get(series);
    }

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape ({@code null} permitted).
     *
     * @see #getSeriesShape(int)
     */
    public void setSeriesShape(int series, Shape shape) {
        setSeriesShape(series, shape, true);
    }

    /**
     * Sets the shape for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param shape  the shape ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesShape(int)
     */
    public void setSeriesShape(int series, Shape shape, boolean notify) {
        this.seriesShapeMap.put(series, shape);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the series shape settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearSeriesShapes(boolean notify) {
        this.seriesShapeMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default shape.
     *
     * @return The shape (never {@code null}).
     *
     * @see #setDefaultShape(Shape)
     */
    public Shape getDefaultShape() {
        return this.defaultShape;
    }

    /**
     * Sets the default shape and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param shape  the shape ({@code null} not permitted).
     *
     * @see #getDefaultShape()
     */
    public void setDefaultShape(Shape shape) {
        // defer argument checking...
        setDefaultShape(shape, true);
    }

    /**
     * Sets the default shape and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shape  the shape ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultShape()
     */
    public void setDefaultShape(Shape shape, boolean notify) {
        Args.nullNotPermitted(shape, "shape");
        this.defaultShape = shape;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the series shape list is
     * automatically populated when {@link #lookupSeriesShape(int)} is called.
     *
     * @return A boolean.
     *
     * @see #setAutoPopulateSeriesShape(boolean)
     */
    public boolean getAutoPopulateSeriesShape() {
        return this.autoPopulateSeriesShape;
    }

    /**
     * Sets the flag that controls whether the series shape list is
     * automatically populated when {@link #lookupSeriesShape(int)} is called.
     *
     * @param auto  the new flag value.
     *
     * @see #getAutoPopulateSeriesShape()
     */
    public void setAutoPopulateSeriesShape(boolean auto) {
        this.autoPopulateSeriesShape = auto;
    }

    // ITEM LABEL VISIBILITY...
    /**
     * Returns {@code true} if an item label is visible, and
     * {@code false} otherwise.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return A boolean.
     */
    public boolean isItemLabelVisible(int row, int column) {
        return isSeriesItemLabelsVisible(row);
    }

    /**
     * Returns {@code true} if the item labels for a series are visible,
     * and {@code false} otherwise.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     */
    public boolean isSeriesItemLabelsVisible(int series) {
        Boolean b = this.seriesItemLabelsVisibleMap.get(series);
        if (b == null) {
            return this.defaultItemLabelsVisible;
        }
        return b;
    }

    /**
     * Sets a flag that controls the visibility of the item labels for a series,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     */
    public void setSeriesItemLabelsVisible(int series, boolean visible) {
        setSeriesItemLabelsVisible(series, Boolean.valueOf(visible));
    }

    /**
     * Sets the visibility of the item labels for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible) {
        setSeriesItemLabelsVisible(series, visible, true);
    }

    /**
     * Sets the visibility of item labels for a series and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the visible flag.
     * @param notify  a flag that controls whether listeners are
     *                notified.
     */
    public void setSeriesItemLabelsVisible(int series, Boolean visible, boolean notify) {
        this.seriesItemLabelsVisibleMap.put(series, visible);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the visibility of item labels for a series settings for this
     * renderer and, if requested, sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearSeriesItemLabelsVisible(boolean notify) {
        this.seriesItemLabelsVisibleMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the base setting for item label visibility.  A {@code null}
     * result should be interpreted as equivalent to {@code Boolean.FALSE}.
     *
     * @return A flag (possibly {@code null}).
     *
     * @see #setDefaultItemLabelsVisible(boolean)
     */
    public boolean getDefaultItemLabelsVisible() {
        return this.defaultItemLabelsVisible;
    }

    /**
     * Sets the base flag that controls whether item labels are visible,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultItemLabelsVisible()
     */
    public void setDefaultItemLabelsVisible(boolean visible) {
        setDefaultItemLabelsVisible(visible, true);
    }

    /**
     * Sets the base visibility for item labels and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag ({@code null} is permitted, and viewed
     *     as equivalent to {@code Boolean.FALSE}).
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getDefaultItemLabelsVisible()
     */
    public void setDefaultItemLabelsVisible(boolean visible, boolean notify) {
        this.defaultItemLabelsVisible = visible;
        if (notify) {
            fireChangeEvent();
        }
    }

    //// ITEM LABEL FONT //////////////////////////////////////////////////////
    /**
     * Returns the font for an item label.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The font (never {@code null}).
     */
    public Font getItemLabelFont(int row, int column) {
        Font result = getSeriesItemLabelFont(row);
        if (result == null) {
            result = this.defaultItemLabelFont;
        }
        return result;
    }

    /**
     * Returns the font for all the item labels in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The font (possibly {@code null}).
     *
     * @see #setSeriesItemLabelFont(int, Font)
     */
    public Font getSeriesItemLabelFont(int series) {
        return this.itemLabelFontMap.get(series);
    }

    /**
     * Sets the item label font for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param font  the font ({@code null} permitted).
     *
     * @see #getSeriesItemLabelFont(int)
     */
    public void setSeriesItemLabelFont(int series, Font font) {
        setSeriesItemLabelFont(series, font, true);
    }

    /**
     * Sets the item label font for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param font  the font ({@code null} permitted).
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getSeriesItemLabelFont(int)
     */
    public void setSeriesItemLabelFont(int series, Font font, boolean notify) {
        this.itemLabelFontMap.put(series, font);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the item label font settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearSeriesItemLabelFonts(boolean notify) {
        this.itemLabelFontMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default item label font (this is used when no other font
     * setting is available).
     *
     * @return The font (never {@code null}).
     *
     * @see #setDefaultItemLabelFont(Font)
     */
    public Font getDefaultItemLabelFont() {
        return this.defaultItemLabelFont;
    }

    /**
     * Sets the default item label font and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getDefaultItemLabelFont()
     */
    public void setDefaultItemLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        setDefaultItemLabelFont(font, true);
    }

    /**
     * Sets the base item label font and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getDefaultItemLabelFont()
     */
    public void setDefaultItemLabelFont(Font font, boolean notify) {
        this.defaultItemLabelFont = font;
        if (notify) {
            fireChangeEvent();
        }
    }

    //// ITEM LABEL PAINT  ////////////////////////////////////////////////////
    /**
     * Returns {@code true} if contrast colors are automatically computed for
     * item labels.
     *
     * @return {@code true} if contrast colors are automatically computed for
     *         item labels.
     */
    public boolean isComputeItemLabelContrastColor() {
        return computeItemLabelContrastColor;
    }

    /**
     * If {@code auto} is set to {@code true} and
     * {@link #getItemPaint(int, int)} returns an instance of {@link Color}, a
     * {@link ChartColor#getContrastColor(Color) contrast color} is computed and
     * used for the item label.
     *
     * @param auto {@code true} if contrast colors should be computed for item
     *             labels.
     * @see #getItemLabelPaint(int, int)
     */
    public void setComputeItemLabelContrastColor(boolean auto) {
        this.computeItemLabelContrastColor = auto;
    }

    /**
     * Returns the paint used to draw an item label.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The paint (never {@code null}).
     */
    public Paint getItemLabelPaint(int row, int column) {
        Paint result = null;
        if (this.computeItemLabelContrastColor) {
            Paint itemPaint = getItemPaint(row, column);
            if (itemPaint instanceof Color) {
                result = ChartColor.getContrastColor((Color) itemPaint);
            }
        }
        if (result == null) {
            result = getSeriesItemLabelPaint(row);
        }
        if (result == null) {
            result = this.defaultItemLabelPaint;
        }
        return result;
    }

    /**
     * Returns the paint used to draw the item labels for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesItemLabelPaint(int, Paint)
     */
    public Paint getSeriesItemLabelPaint(int series) {
        return this.itemLabelPaints.get(series);
    }

    /**
     * Sets the item label paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series (zero based index).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesItemLabelPaint(int)
     */
    public void setSeriesItemLabelPaint(int series, Paint paint) {
        setSeriesItemLabelPaint(series, paint, true);
    }

    /**
     * Sets the item label paint for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getSeriesItemLabelPaint(int)
     */
    public void setSeriesItemLabelPaint(int series, Paint paint, boolean notify) {
        this.itemLabelPaints.put(series, paint);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the item label paint settings for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearSeriesItemLabelPaints(boolean notify) {
        this.itemLabelPaints.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default item label paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultItemLabelPaint(Paint)
     */
    public Paint getDefaultItemLabelPaint() {
        return this.defaultItemLabelPaint;
    }

    /**
     * Sets the default item label paint and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultItemLabelPaint()
     */
    public void setDefaultItemLabelPaint(Paint paint) {
        // defer argument checking...
        setDefaultItemLabelPaint(paint, true);
    }

    /**
     * Sets the default item label paint and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners..
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  a flag that controls whether listeners are
     *                notified.
     *
     * @see #getDefaultItemLabelPaint()
     */
    public void setDefaultItemLabelPaint(Paint paint, boolean notify) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultItemLabelPaint = paint;
        if (notify) {
            fireChangeEvent();
        }
    }

    // POSITIVE ITEM LABEL POSITION...
    /**
     * Returns the item label position for positive values.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The item label position (never {@code null}).
     *
     * @see #getNegativeItemLabelPosition(int, int)
     */
    public ItemLabelPosition getPositiveItemLabelPosition(int row, int column) {
        return getSeriesPositiveItemLabelPosition(row);
    }

    /**
     * Returns the item label position for all positive values in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item label position (never {@code null}).
     *
     * @see #setSeriesPositiveItemLabelPosition(int, ItemLabelPosition)
     */
    public ItemLabelPosition getSeriesPositiveItemLabelPosition(int series) {
        // otherwise look up the position table
        ItemLabelPosition position = this.positiveItemLabelPositionMap.get(series);
        if (position == null) {
            position = this.defaultPositiveItemLabelPosition;
        }
        return position;
    }

    /**
     * Sets the item label position for all positive values in a series and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     *
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    public void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position) {
        setSeriesPositiveItemLabelPosition(series, position, true);
    }

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
    public void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position, boolean notify) {
        this.positiveItemLabelPositionMap.put(series, position);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the item label position for all positive values for series
     * settings for this renderer and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearSeriesPositiveItemLabelPositions(boolean notify) {
        this.positiveItemLabelPositionMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default positive item label position.
     *
     * @return The position (never {@code null}).
     *
     * @see #setDefaultPositiveItemLabelPosition(ItemLabelPosition)
     */
    public ItemLabelPosition getDefaultPositiveItemLabelPosition() {
        return this.defaultPositiveItemLabelPosition;
    }

    /**
     * Sets the default positive item label position.
     *
     * @param position  the position ({@code null} not permitted).
     *
     * @see #getDefaultPositiveItemLabelPosition()
     */
    public void setDefaultPositiveItemLabelPosition(ItemLabelPosition position) {
        // defer argument checking...
        setDefaultPositiveItemLabelPosition(position, true);
    }

    /**
     * Sets the default positive item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     * @param notify  notify registered listeners?
     *
     * @see #getDefaultPositiveItemLabelPosition()
     */
    public void setDefaultPositiveItemLabelPosition(ItemLabelPosition position, boolean notify) {
        Args.nullNotPermitted(position, "position");
        this.defaultPositiveItemLabelPosition = position;
        if (notify) {
            fireChangeEvent();
        }
    }

    // NEGATIVE ITEM LABEL POSITION...
    /**
     * Returns the item label position for negative values.  This method can be
     * overridden to provide customisation of the item label position for
     * individual data items.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The item label position (never {@code null}).
     *
     * @see #getPositiveItemLabelPosition(int, int)
     */
    public ItemLabelPosition getNegativeItemLabelPosition(int row, int column) {
        return getSeriesNegativeItemLabelPosition(row);
    }

    /**
     * Returns the item label position for all negative values in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item label position (never {@code null}).
     *
     * @see #setSeriesNegativeItemLabelPosition(int, ItemLabelPosition)
     */
    public ItemLabelPosition getSeriesNegativeItemLabelPosition(int series) {
        // otherwise look up the position list
        ItemLabelPosition position = this.negativeItemLabelPositionMap.get(series);
        if (position == null) {
            position = this.defaultNegativeItemLabelPosition;
        }
        return position;
    }

    /**
     * Sets the item label position for negative values in a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     *
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    public void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position) {
        setSeriesNegativeItemLabelPosition(series, position, true);
    }

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
    public void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position, boolean notify) {
        this.negativeItemLabelPositionMap.put(series, position);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the base item label position for negative values.
     *
     * @return The position (never {@code null}).
     *
     * @see #setDefaultNegativeItemLabelPosition(ItemLabelPosition)
     */
    public ItemLabelPosition getDefaultNegativeItemLabelPosition() {
        return this.defaultNegativeItemLabelPosition;
    }

    /**
     * Sets the default item label position for negative values and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     *
     * @see #getDefaultNegativeItemLabelPosition()
     */
    public void setDefaultNegativeItemLabelPosition(ItemLabelPosition position) {
        setDefaultNegativeItemLabelPosition(position, true);
    }

    /**
     * Sets the default negative item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     * @param notify  notify registered listeners?
     *
     * @see #getDefaultNegativeItemLabelPosition()
     */
    public void setDefaultNegativeItemLabelPosition(ItemLabelPosition position, boolean notify) {
        Args.nullNotPermitted(position, "position");
        this.defaultNegativeItemLabelPosition = position;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the item label insets.
     *
     * @return The item label insets.
     */
    public RectangleInsets getItemLabelInsets() {
        return itemLabelInsets;
    }

    /**
     * Sets the item label insets.
     *
     * @param itemLabelInsets the insets
     */
    public void setItemLabelInsets(RectangleInsets itemLabelInsets) {
        Args.nullNotPermitted(itemLabelInsets, "itemLabelInsets");
        this.itemLabelInsets = itemLabelInsets;
        fireChangeEvent();
    }

    /**
     * Returns a boolean that indicates whether the specified item
     * should have a chart entity created for it.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return A boolean.
     */
    public boolean getItemCreateEntity(int series, int item) {
        Boolean b = getSeriesCreateEntities(series);
        if (b != null) {
            return b;
        }
        // otherwise...
        return this.defaultCreateEntities;
    }

    /**
     * Returns the flag that controls whether entities are created for a
     * series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesCreateEntities(int, Boolean)
     */
    public Boolean getSeriesCreateEntities(int series) {
        return this.seriesCreateEntitiesMap.get(series);
    }

    /**
     * Sets the flag that controls whether entities are created for a series,
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param create  the flag ({@code null} permitted).
     *
     * @see #getSeriesCreateEntities(int)
     */
    public void setSeriesCreateEntities(int series, Boolean create) {
        setSeriesCreateEntities(series, create, true);
    }

    /**
     * Sets the flag that controls whether entities are created for a series
     * and, if requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index.
     * @param create  the flag ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesCreateEntities(int)
     */
    public void setSeriesCreateEntities(int series, Boolean create, boolean notify) {
        this.seriesCreateEntitiesMap.put(series, create);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default flag for creating entities.
     *
     * @return The default flag for creating entities.
     *
     * @see #setDefaultCreateEntities(boolean)
     */
    public boolean getDefaultCreateEntities() {
        return this.defaultCreateEntities;
    }

    /**
     * Sets the default flag that controls whether entities are created
     * for a series, and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param create  the flag.
     *
     * @see #getDefaultCreateEntities()
     */
    public void setDefaultCreateEntities(boolean create) {
        // defer argument checking...
        setDefaultCreateEntities(create, true);
    }

    /**
     * Sets the default flag that controls whether entities are created and,
     * if requested, sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param create  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultCreateEntities()
     */
    public void setDefaultCreateEntities(boolean create, boolean notify) {
        this.defaultCreateEntities = create;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the radius of the circle used for the default entity area
     * when no area is specified.
     *
     * @return A radius.
     *
     * @see #setDefaultEntityRadius(int)
     */
    public int getDefaultEntityRadius() {
        return this.defaultEntityRadius;
    }

    /**
     * Sets the radius of the circle used for the default entity area
     * when no area is specified.
     *
     * @param radius  the radius.
     *
     * @see #getDefaultEntityRadius()
     */
    public void setDefaultEntityRadius(int radius) {
        this.defaultEntityRadius = radius;
    }

    /**
     * Performs a lookup for the legend shape.
     *
     * @param series  the series index.
     *
     * @return The shape (possibly {@code null}).
     */
    public Shape lookupLegendShape(int series) {
        Shape result = getLegendShape(series);
        if (result == null) {
            result = this.defaultLegendShape;
        }
        if (result == null) {
            result = lookupSeriesShape(series);
        }
        return result;
    }

    /**
     * Returns the legend shape defined for the specified series (possibly
     * {@code null}).
     *
     * @param series  the series index.
     *
     * @return The shape (possibly {@code null}).
     *
     * @see #lookupLegendShape(int)
     */
    public Shape getLegendShape(int series) {
        return this.seriesLegendShapes.get(series);
    }

    /**
     * Sets the shape used for the legend item for the specified series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index.
     * @param shape  the shape ({@code null} permitted).
     */
    public void setLegendShape(int series, Shape shape) {
        this.seriesLegendShapes.put(series, shape);
        fireChangeEvent();
    }

    /**
     * Clears the series legend shapes for this renderer and, if requested,
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearLegendShapes(boolean notify) {
        this.seriesLegendShapes.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default legend shape, which may be {@code null}.
     *
     * @return The default legend shape.
     */
    public Shape getDefaultLegendShape() {
        return this.defaultLegendShape;
    }

    /**
     * Sets the default legend shape and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shape  the shape ({@code null} permitted).
     */
    public void setDefaultLegendShape(Shape shape) {
        this.defaultLegendShape = shape;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the legend shape is
     * treated as a line when creating legend items.
     *
     * @return A boolean.
     */
    protected boolean getTreatLegendShapeAsLine() {
        return this.treatLegendShapeAsLine;
    }

    /**
     * Sets the flag that controls whether the legend shape is
     * treated as a line when creating legend items.
     *
     * @param treatAsLine  the new flag value.
     */
    protected void setTreatLegendShapeAsLine(boolean treatAsLine) {
        if (this.treatLegendShapeAsLine != treatAsLine) {
            this.treatLegendShapeAsLine = treatAsLine;
            fireChangeEvent();
        }
    }

    /**
     * Performs a lookup for the legend text font.
     *
     * @param series  the series index.
     *
     * @return The font (possibly {@code null}).
     */
    public Font lookupLegendTextFont(int series) {
        Font result = getLegendTextFont(series);
        if (result == null) {
            result = this.defaultLegendTextFont;
        }
        return result;
    }

    /**
     * Returns the legend text font defined for the specified series (possibly
     * {@code null}).
     *
     * @param series  the series index.
     *
     * @return The font (possibly {@code null}).
     *
     * @see #lookupLegendTextFont(int)
     */
    public Font getLegendTextFont(int series) {
        return this.legendTextFontMap.get(series);
    }

    /**
     * Sets the font used for the legend text for the specified series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index.
     * @param font  the font ({@code null} permitted).
     */
    public void setLegendTextFont(int series, Font font) {
        this.legendTextFontMap.put(series, font);
        fireChangeEvent();
    }

    /**
     * Clears the font used for the legend text for series settings for this
     * renderer and, if requested, sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearLegendTextFonts(boolean notify) {
        this.legendTextFontMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default legend text font, which may be {@code null}.
     *
     * @return The default legend text font.
     */
    public Font getDefaultLegendTextFont() {
        return this.defaultLegendTextFont;
    }

    /**
     * Sets the default legend text font and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param font  the font ({@code null} permitted).
     */
    public void setDefaultLegendTextFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.defaultLegendTextFont = font;
        fireChangeEvent();
    }

    /**
     * Performs a lookup for the legend text paint.
     *
     * @param series  the series index.
     *
     * @return The paint (possibly {@code null}).
     */
    public Paint lookupLegendTextPaint(int series) {
        Paint result = getLegendTextPaint(series);
        if (result == null) {
            result = this.defaultLegendTextPaint;
        }
        return result;
    }

    /**
     * Returns the legend text paint defined for the specified series (possibly
     * {@code null}).
     *
     * @param series  the series index.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #lookupLegendTextPaint(int)
     */
    public Paint getLegendTextPaint(int series) {
        return this.legendTextPaints.get(series);
    }

    /**
     * Sets the paint used for the legend text for the specified series, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index.
     * @param paint  the paint ({@code null} permitted).
     */
    public void setLegendTextPaint(int series, Paint paint) {
        this.legendTextPaints.put(series, paint);
        fireChangeEvent();
    }

    /**
     * Clears the paint used for the legend text for series settings for this
     * renderer and, if requested, sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param notify notify listeners?
     */
    public void clearLegendTextPaints(boolean notify) {
        this.legendTextPaints.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default legend text paint, which may be {@code null}.
     *
     * @return The default legend text paint.
     */
    public Paint getDefaultLegendTextPaint() {
        return this.defaultLegendTextPaint;
    }

    /**
     * Sets the default legend text paint and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     */
    public void setDefaultLegendTextPaint(Paint paint) {
        this.defaultLegendTextPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the data bounds reported
     * by this renderer will exclude non-visible series.
     *
     * @return A boolean.
     */
    public boolean getDataBoundsIncludesVisibleSeriesOnly() {
        return this.dataBoundsIncludesVisibleSeriesOnly;
    }

    /**
     * Sets the flag that controls whether the data bounds reported
     * by this renderer will exclude non-visible series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visibleOnly  include only visible series.
     */
    public void setDataBoundsIncludesVisibleSeriesOnly(boolean visibleOnly) {
        this.dataBoundsIncludesVisibleSeriesOnly = visibleOnly;
        notifyListeners(new RendererChangeEvent(this, true));
    }

    /**
     * The adjacent offset.
     */
    private static final double ADJ = Math.cos(Math.PI / 6.0);

    /**
     * The opposite offset.
     */
    private static final double OPP = Math.sin(Math.PI / 6.0);

    /**
     * Calculates the item label anchor point.
     *
     * @param anchor  the anchor.
     * @param x  the x coordinate.
     * @param y  the y coordinate.
     * @param orientation  the plot orientation.
     *
     * @return The anchor point (never {@code null}).
     */
    protected Point2D calculateLabelAnchorPoint(ItemLabelAnchor anchor, double x, double y, PlotOrientation orientation) {
        Args.nullNotPermitted(anchor, "anchor");
        Point2D result = null;
        if (anchor == ItemLabelAnchor.CENTER) {
            result = new Point2D.Double(x, y);
        } else if (anchor == ItemLabelAnchor.INSIDE1) {
            result = new Point2D.Double(x + OPP * this.itemLabelInsets.getLeft(), y - ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE2) {
            result = new Point2D.Double(x + ADJ * this.itemLabelInsets.getLeft(), y - OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE3) {
            result = new Point2D.Double(x + this.itemLabelInsets.getLeft(), y);
        } else if (anchor == ItemLabelAnchor.INSIDE4) {
            result = new Point2D.Double(x + ADJ * this.itemLabelInsets.getLeft(), y + OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE5) {
            result = new Point2D.Double(x + OPP * this.itemLabelInsets.getLeft(), y + ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE6) {
            result = new Point2D.Double(x, y + this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE7) {
            result = new Point2D.Double(x - OPP * this.itemLabelInsets.getLeft(), y + ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE8) {
            result = new Point2D.Double(x - ADJ * this.itemLabelInsets.getLeft(), y + OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE9) {
            result = new Point2D.Double(x - this.itemLabelInsets.getLeft(), y);
        } else if (anchor == ItemLabelAnchor.INSIDE10) {
            result = new Point2D.Double(x - ADJ * this.itemLabelInsets.getLeft(), y - OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE11) {
            result = new Point2D.Double(x - OPP * this.itemLabelInsets.getLeft(), y - ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.INSIDE12) {
            result = new Point2D.Double(x, y - this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE1) {
            result = new Point2D.Double(x + 2.0 * OPP * this.itemLabelInsets.getLeft(), y - 2.0 * ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE2) {
            result = new Point2D.Double(x + 2.0 * ADJ * this.itemLabelInsets.getLeft(), y - 2.0 * OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE3) {
            result = new Point2D.Double(x + 2.0 * this.itemLabelInsets.getLeft(), y);
        } else if (anchor == ItemLabelAnchor.OUTSIDE4) {
            result = new Point2D.Double(x + 2.0 * ADJ * this.itemLabelInsets.getLeft(), y + 2.0 * OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE5) {
            result = new Point2D.Double(x + 2.0 * OPP * this.itemLabelInsets.getLeft(), y + 2.0 * ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE6) {
            result = new Point2D.Double(x, y + 2.0 * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE7) {
            result = new Point2D.Double(x - 2.0 * OPP * this.itemLabelInsets.getLeft(), y + 2.0 * ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE8) {
            result = new Point2D.Double(x - 2.0 * ADJ * this.itemLabelInsets.getLeft(), y + 2.0 * OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE9) {
            result = new Point2D.Double(x - 2.0 * this.itemLabelInsets.getLeft(), y);
        } else if (anchor == ItemLabelAnchor.OUTSIDE10) {
            result = new Point2D.Double(x - 2.0 * ADJ * this.itemLabelInsets.getLeft(), y - 2.0 * OPP * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE11) {
            result = new Point2D.Double(x - 2.0 * OPP * this.itemLabelInsets.getLeft(), y - 2.0 * ADJ * this.itemLabelInsets.getTop());
        } else if (anchor == ItemLabelAnchor.OUTSIDE12) {
            result = new Point2D.Double(x, y - 2.0 * this.itemLabelInsets.getTop());
        }
        return result;
    }

    /**
     * Registers an object to receive notification of changes to the renderer.
     *
     * @param listener  the listener ({@code null} not permitted).
     *
     * @see #removeChangeListener(RendererChangeListener)
     */
    public void addChangeListener(RendererChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.listenerList.add(RendererChangeListener.class, listener);
    }

    /**
     * Deregisters an object so that it no longer receives
     * notification of changes to the renderer.
     *
     * @param listener  the object ({@code null} not permitted).
     *
     * @see #addChangeListener(RendererChangeListener)
     */
    public void removeChangeListener(RendererChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.listenerList.remove(RendererChangeListener.class, listener);
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
        List<Object> list = Arrays.asList(this.listenerList.getListenerList());
        return list.contains(listener);
    }

    /**
     * Sends a {@link RendererChangeEvent} to all registered listeners.
     */
    protected void fireChangeEvent() {
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Notifies all registered listeners that the renderer has been modified.
     *
     * @param event  information about the change event.
     */
    public void notifyListeners(RendererChangeEvent event) {
        Object[] ls = this.listenerList.getListenerList();
        for (int i = ls.length - 2; i >= 0; i -= 2) {
            if (ls[i] == RendererChangeListener.class) {
                ((RendererChangeListener) ls[i + 1]).rendererChanged(event);
            }
        }
    }

    /**
     * Tests this renderer for equality with another object.
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
        if (!(obj instanceof AbstractRenderer)) {
            return false;
        }
        AbstractRenderer that = (AbstractRenderer) obj;
        if (this.dataBoundsIncludesVisibleSeriesOnly != that.dataBoundsIncludesVisibleSeriesOnly) {
            return false;
        }
        if (this.treatLegendShapeAsLine != that.treatLegendShapeAsLine) {
            return false;
        }
        if (this.defaultEntityRadius != that.defaultEntityRadius) {
            return false;
        }
        if (!this.seriesVisibleMap.equals(that.seriesVisibleMap)) {
            return false;
        }
        if (this.defaultSeriesVisible != that.defaultSeriesVisible) {
            return false;
        }
        if (!this.seriesVisibleInLegendMap.equals(that.seriesVisibleInLegendMap)) {
            return false;
        }
        if (this.defaultSeriesVisibleInLegend != that.defaultSeriesVisibleInLegend) {
            return false;
        }
        if (!PaintUtils.equal(this.seriesPaintMap, that.seriesPaintMap)) {
            return false;
        }
        if (this.autoPopulateSeriesPaint != that.autoPopulateSeriesPaint) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultPaint, that.defaultPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.seriesFillPaintMap, that.seriesFillPaintMap)) {
            return false;
        }
        if (this.autoPopulateSeriesFillPaint != that.autoPopulateSeriesFillPaint) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultFillPaint, that.defaultFillPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.seriesOutlinePaintMap, that.seriesOutlinePaintMap)) {
            return false;
        }
        if (this.autoPopulateSeriesOutlinePaint != that.autoPopulateSeriesOutlinePaint) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultOutlinePaint, that.defaultOutlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.seriesStrokeMap, that.seriesStrokeMap)) {
            return false;
        }
        if (this.autoPopulateSeriesStroke != that.autoPopulateSeriesStroke) {
            return false;
        }
        if (!Objects.equals(this.defaultStroke, that.defaultStroke)) {
            return false;
        }
        if (!Objects.equals(this.seriesOutlineStrokeMap, that.seriesOutlineStrokeMap)) {
            return false;
        }
        if (this.autoPopulateSeriesOutlineStroke != that.autoPopulateSeriesOutlineStroke) {
            return false;
        }
        if (!Objects.equals(this.defaultOutlineStroke, that.defaultOutlineStroke)) {
            return false;
        }
        if (!ShapeUtils.equal(this.seriesShapeMap, that.seriesShapeMap)) {
            return false;
        }
        if (this.autoPopulateSeriesShape != that.autoPopulateSeriesShape) {
            return false;
        }
        if (!ShapeUtils.equal(this.defaultShape, that.defaultShape)) {
            return false;
        }
        if (!Objects.equals(this.seriesItemLabelsVisibleMap, that.seriesItemLabelsVisibleMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemLabelsVisible, that.defaultItemLabelsVisible)) {
            return false;
        }
        if (!Objects.equals(this.itemLabelFontMap, that.itemLabelFontMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemLabelFont, that.defaultItemLabelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.itemLabelPaints, that.itemLabelPaints)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultItemLabelPaint, that.defaultItemLabelPaint)) {
            return false;
        }
        if (!Objects.equals(this.positiveItemLabelPositionMap, that.positiveItemLabelPositionMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultPositiveItemLabelPosition, that.defaultPositiveItemLabelPosition)) {
            return false;
        }
        if (!Objects.equals(this.negativeItemLabelPositionMap, that.negativeItemLabelPositionMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultNegativeItemLabelPosition, that.defaultNegativeItemLabelPosition)) {
            return false;
        }
        if (!Objects.equals(this.seriesCreateEntitiesMap, that.seriesCreateEntitiesMap)) {
            return false;
        }
        if (this.defaultCreateEntities != that.defaultCreateEntities) {
            return false;
        }
        if (!ShapeUtils.equal(this.seriesLegendShapes, that.seriesLegendShapes)) {
            return false;
        }
        if (!ShapeUtils.equal(this.defaultLegendShape, that.defaultLegendShape)) {
            return false;
        }
        if (!Objects.equals(this.legendTextFontMap, that.legendTextFontMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultLegendTextFont, that.defaultLegendTextFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.legendTextPaints, that.legendTextPaints)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultLegendTextPaint, that.defaultLegendTextPaint)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hashcode for the renderer.
     *
     * @return The hashcode.
     */
    @Override
    public int hashCode() {
        int result = 193;
        result = HashUtils.hashCode(result, this.seriesVisibleMap);
        result = HashUtils.hashCode(result, this.defaultSeriesVisible);
        result = HashUtils.hashCode(result, this.seriesVisibleInLegendMap);
        result = HashUtils.hashCode(result, this.defaultSeriesVisibleInLegend);
        result = HashUtils.hashCode(result, this.seriesPaintMap);
        result = HashUtils.hashCode(result, this.defaultPaint);
        result = HashUtils.hashCode(result, this.seriesFillPaintMap);
        result = HashUtils.hashCode(result, this.defaultFillPaint);
        result = HashUtils.hashCode(result, this.seriesOutlinePaintMap);
        result = HashUtils.hashCode(result, this.defaultOutlinePaint);
        result = HashUtils.hashCode(result, this.seriesStrokeMap);
        result = HashUtils.hashCode(result, this.defaultStroke);
        result = HashUtils.hashCode(result, this.seriesOutlineStrokeMap);
        result = HashUtils.hashCode(result, this.defaultOutlineStroke);
        // shapeList
        // baseShape
        result = HashUtils.hashCode(result, this.seriesItemLabelsVisibleMap);
        result = HashUtils.hashCode(result, this.defaultItemLabelsVisible);
        // itemLabelFontList
        // baseItemLabelFont
        // itemLabelPaintList
        // baseItemLabelPaint
        // positiveItemLabelPositionList
        // basePositiveItemLabelPosition
        // negativeItemLabelPositionList
        // baseNegativeItemLabelPosition
        // itemLabelAnchorOffset
        // createEntityList
        // baseCreateEntities
        return result;
    }

    /**
     * Returns an independent copy of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the renderer
     *         does not support cloning.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractRenderer clone = (AbstractRenderer) super.clone();
        if (this.seriesVisibleMap != null) {
            clone.seriesVisibleMap = new HashMap<>(this.seriesVisibleMap);
        }
        if (this.seriesVisibleInLegendMap != null) {
            clone.seriesVisibleInLegendMap = new HashMap<>(this.seriesVisibleInLegendMap);
        }
        // 'paint' : immutable, no need to clone reference
        if (this.seriesPaintMap != null) {
            clone.seriesPaintMap = new HashMap<>(this.seriesPaintMap);
        }
        // 'basePaint' : immutable, no need to clone reference
        if (this.seriesFillPaintMap != null) {
            clone.seriesFillPaintMap = new HashMap<>(this.seriesFillPaintMap);
        }
        // 'outlinePaint' : immutable, no need to clone reference
        if (this.seriesOutlinePaintMap != null) {
            clone.seriesOutlinePaintMap = new HashMap<>(this.seriesOutlinePaintMap);
        }
        // 'baseOutlinePaint' : immutable, no need to clone reference
        // 'stroke' : immutable, no need to clone reference
        if (this.seriesStrokeMap != null) {
            clone.seriesStrokeMap = CloneUtils.cloneMapValues(this.seriesStrokeMap);
        }
        // 'baseStroke' : immutable, no need to clone reference
        // 'outlineStroke' : immutable, no need to clone reference
        if (this.seriesOutlineStrokeMap != null) {
            clone.seriesOutlineStrokeMap = CloneUtils.cloneMapValues(this.seriesOutlineStrokeMap);
        }
        // 'baseOutlineStroke' : immutable, no need to clone reference
        if (this.seriesShapeMap != null) {
            clone.seriesShapeMap = ShapeUtils.cloneMap(this.seriesShapeMap);
        }
        clone.defaultShape = CloneUtils.clone(this.defaultShape);
        // 'seriesItemLabelsVisibleMap' : immutable, no need to clone reference
        if (this.seriesItemLabelsVisibleMap != null) {
            clone.seriesItemLabelsVisibleMap = new HashMap<>(this.seriesItemLabelsVisibleMap);
        }
        // 'basePaint' : immutable, no need to clone reference
        // 'itemLabelFont' : immutable, no need to clone reference
        if (this.itemLabelFontMap != null) {
            clone.itemLabelFontMap = new HashMap<>(this.itemLabelFontMap);
        }
        // 'baseItemLabelFont' : immutable, no need to clone reference
        // 'itemLabelPaint' : immutable, no need to clone reference
        if (this.itemLabelPaints != null) {
            clone.itemLabelPaints = new HashMap<>(this.itemLabelPaints);
        }
        // 'baseItemLabelPaint' : immutable, no need to clone reference
        if (this.positiveItemLabelPositionMap != null) {
            clone.positiveItemLabelPositionMap = new HashMap<>(this.positiveItemLabelPositionMap);
        }
        if (this.negativeItemLabelPositionMap != null) {
            clone.negativeItemLabelPositionMap = new HashMap<>(this.negativeItemLabelPositionMap);
        }
        if (this.seriesCreateEntitiesMap != null) {
            clone.seriesCreateEntitiesMap = new HashMap<>(this.seriesCreateEntitiesMap);
        }
        if (this.seriesLegendShapes != null) {
            clone.seriesLegendShapes = ShapeUtils.cloneMap(this.seriesLegendShapes);
        }
        if (this.legendTextFontMap != null) {
            // Font objects are immutable so just shallow copy the map
            clone.legendTextFontMap = new HashMap<>(this.legendTextFontMap);
        }
        if (this.legendTextPaints != null) {
            clone.legendTextPaints = new HashMap<>(this.legendTextPaints);
        }
        clone.listenerList = new EventListenerList();
        clone.event = null;
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
        SerialUtils.writeMapOfPaint(this.seriesPaintMap, stream);
        SerialUtils.writePaint(this.defaultPaint, stream);
        SerialUtils.writeMapOfPaint(this.seriesFillPaintMap, stream);
        SerialUtils.writePaint(this.defaultFillPaint, stream);
        SerialUtils.writeMapOfPaint(this.seriesOutlinePaintMap, stream);
        SerialUtils.writePaint(this.defaultOutlinePaint, stream);
        SerialUtils.writeMapOfStroke(this.seriesStrokeMap, stream);
        SerialUtils.writeStroke(this.defaultStroke, stream);
        SerialUtils.writeMapOfStroke(this.seriesOutlineStrokeMap, stream);
        SerialUtils.writeStroke(this.defaultOutlineStroke, stream);
        SerialUtils.writeShape(this.defaultShape, stream);
        SerialUtils.writeMapOfPaint(this.itemLabelPaints, stream);
        SerialUtils.writePaint(this.defaultItemLabelPaint, stream);
        SerialUtils.writeShape(this.defaultLegendShape, stream);
        SerialUtils.writeMapOfPaint(this.legendTextPaints, stream);
        SerialUtils.writePaint(this.defaultLegendTextPaint, stream);
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
        this.seriesPaintMap = SerialUtils.readMapOfPaint(stream);
        this.defaultPaint = SerialUtils.readPaint(stream);
        this.seriesFillPaintMap = SerialUtils.readMapOfPaint(stream);
        this.defaultFillPaint = SerialUtils.readPaint(stream);
        this.seriesOutlinePaintMap = SerialUtils.readMapOfPaint(stream);
        this.defaultOutlinePaint = SerialUtils.readPaint(stream);
        this.seriesStrokeMap = SerialUtils.readMapOfStroke(stream);
        this.defaultStroke = SerialUtils.readStroke(stream);
        this.seriesOutlineStrokeMap = SerialUtils.readMapOfStroke(stream);
        this.defaultOutlineStroke = SerialUtils.readStroke(stream);
        this.defaultShape = SerialUtils.readShape(stream);
        this.itemLabelPaints = SerialUtils.readMapOfPaint(stream);
        this.defaultItemLabelPaint = SerialUtils.readPaint(stream);
        this.defaultLegendShape = SerialUtils.readShape(stream);
        this.legendTextPaints = SerialUtils.readMapOfPaint(stream);
        this.defaultLegendTextPaint = SerialUtils.readPaint(stream);
        // listeners are not restored automatically, but storage must be
        // provided...
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
 * --------------------
 * AttrStringUtils.java
 * --------------------
 * (C) Copyright 2013-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 * 
 */
/**
 * Some {@code AttributedString} utilities.
 *
 * @since 1.0.16
 */
public class AttrStringUtils {

    private AttrStringUtils() {
        // no need to instantiate this class
    }

    /**
     * Returns the bounds for the attributed string.
     *
     * @param text  the attributed string ({@code null} not permitted).
     * @param g2  the graphics target ({@code null} not permitted).
     *
     * @return The bounds (never {@code null}).
     *
     * @since 1.0.18
     */
    public static Rectangle2D getTextBounds(AttributedString text, Graphics2D g2) {
        TextLayout tl = new TextLayout(text.getIterator(), g2.getFontRenderContext());
        return tl.getBounds();
    }

    /**
     * Draws the attributed string at {@code (x, y)}, rotated by the
     * specified angle about {@code (x, y)}.
     *
     * @param text  the attributed string ({@code null} not permitted).
     * @param g2  the graphics output target.
     * @param angle  the angle.
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     *
     * @since 1.0.16
     */
    public static void drawRotatedString(AttributedString text, Graphics2D g2, double angle, float x, float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }

    /**
     * Draws the attributed string at {@code (textX, textY)}, rotated by
     * the specified angle about {@code (rotateX, rotateY)}.
     *
     * @param text  the attributed string ({@code null} not permitted).
     * @param g2  the graphics output target.
     * @param textX  the x-coordinate for the text.
     * @param textY  the y-coordinate for the text.
     * @param angle  the rotation angle (in radians).
     * @param rotateX  the x-coordinate for the rotation point.
     * @param rotateY  the y-coordinate for the rotation point.
     *
     * @since 1.0.16
     */
    public static void drawRotatedString(AttributedString text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        Args.nullNotPermitted(text, "text");
        AffineTransform saved = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        g2.transform(rotate);
        TextLayout tl = new TextLayout(text.getIterator(), g2.getFontRenderContext());
        tl.draw(g2, textX, textY);
        g2.setTransform(saved);
    }

    /**
     * Draws the string anchored to {@code (x, y)}, rotated by the
     * specified angle about {@code (rotationX, rotationY)}.
     *
     * @param text  the text ({@code null} not permitted).
     * @param g2  the graphics target.
     * @param x  the x-coordinate for the text location.
     * @param y  the y-coordinate for the text location.
     * @param textAnchor  the text anchor point.
     * @param angle  the rotation (in radians).
     * @param rotationX  the x-coordinate for the rotation point.
     * @param rotationY  the y-coordinate for the rotation point.
     *
     * @since 1.0.16
     */
    public static void drawRotatedString(AttributedString text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, float rotationX, float rotationY) {
        Args.nullNotPermitted(text, "text");
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor, null);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, rotationX, rotationY);
    }

    /**
     * Draws a rotated string.
     *
     * @param text  the text to draw.
     * @param g2  the graphics target.
     * @param x  the x-coordinate for the text location.
     * @param y  the y-coordinate for the text location.
     * @param textAnchor  the text anchor point.
     * @param angle  the rotation (in radians).
     * @param rotationAnchor  the rotation anchor point.
     *
     * @since 1.0.16
     */
    public static void drawRotatedString(AttributedString text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        Args.nullNotPermitted(text, "text");
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor, null);
        float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
    }

    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, AttributedString text, TextAnchor anchor, Rectangle2D textBounds) {
        TextLayout layout = new TextLayout(text.getIterator(), g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        float[] result = new float[3];
        float ascent = layout.getAscent();
        result[2] = -ascent;
        float halfAscent = ascent / 2.0f;
        float descent = layout.getDescent();
        float leading = layout.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (isHorizontalCenter(anchor)) {
            xAdj = (float) -bounds.getWidth() / 2.0f;
        } else if (isHorizontalRight(anchor)) {
            xAdj = (float) -bounds.getWidth();
        }
        if (isTop(anchor)) {
            //yAdj = -descent - leading + (float) bounds.getHeight();
            yAdj = (float) bounds.getHeight();
        } else if (isHalfAscent(anchor)) {
            yAdj = halfAscent;
        } else if (isHalfHeight(anchor)) {
            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);
        } else if (isBaseline(anchor)) {
            yAdj = 0.0f;
        } else if (isBottom(anchor)) {
            yAdj = -descent - leading;
        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    /**
     * A utility method that calculates the rotation anchor offsets for a
     * string.  These offsets are relative to the text starting coordinate
     * (BASELINE_LEFT).
     *
     * @param g2  the graphics device.
     * @param text  the text.
     * @param anchor  the anchor point.
     *
     * @return  The offsets.
     */
    private static float[] deriveRotationAnchorOffsets(Graphics2D g2, AttributedString text, TextAnchor anchor) {
        float[] result = new float[2];
        TextLayout layout = new TextLayout(text.getIterator(), g2.getFontRenderContext());
        Rectangle2D bounds = layout.getBounds();
        float ascent = layout.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = layout.getDescent();
        float leading = layout.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (isHorizontalLeft(anchor)) {
            xAdj = 0.0f;
        } else if (isHorizontalCenter(anchor)) {
            xAdj = (float) bounds.getWidth() / 2.0f;
        } else if (isHorizontalRight(anchor)) {
            xAdj = (float) bounds.getWidth();
        }
        if (isTop(anchor)) {
            yAdj = descent + leading - (float) bounds.getHeight();
        } else if (isHalfHeight(anchor)) {
            yAdj = descent + leading - (float) (bounds.getHeight() / 2.0);
        } else if (isHalfAscent(anchor)) {
            yAdj = -halfAscent;
        } else if (isBaseline(anchor)) {
            yAdj = 0.0f;
        } else if (isBottom(anchor)) {
            yAdj = descent + leading;
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    private static boolean isTop(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_LEFT) || anchor.equals(TextAnchor.TOP_CENTER) || anchor.equals(TextAnchor.TOP_RIGHT);
    }

    private static boolean isBaseline(TextAnchor anchor) {
        return anchor.equals(TextAnchor.BASELINE_LEFT) || anchor.equals(TextAnchor.BASELINE_CENTER) || anchor.equals(TextAnchor.BASELINE_RIGHT);
    }

    private static boolean isHalfAscent(TextAnchor anchor) {
        return anchor.equals(TextAnchor.HALF_ASCENT_LEFT) || anchor.equals(TextAnchor.HALF_ASCENT_CENTER) || anchor.equals(TextAnchor.HALF_ASCENT_RIGHT);
    }

    private static boolean isHalfHeight(TextAnchor anchor) {
        return anchor.equals(TextAnchor.CENTER_LEFT) || anchor.equals(TextAnchor.CENTER) || anchor.equals(TextAnchor.CENTER_RIGHT);
    }

    private static boolean isBottom(TextAnchor anchor) {
        return anchor.equals(TextAnchor.BOTTOM_LEFT) || anchor.equals(TextAnchor.BOTTOM_CENTER) || anchor.equals(TextAnchor.BOTTOM_RIGHT);
    }

    private static boolean isHorizontalLeft(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_LEFT) || anchor.equals(TextAnchor.CENTER_LEFT) || anchor.equals(TextAnchor.HALF_ASCENT_LEFT) || anchor.equals(TextAnchor.BASELINE_LEFT) || anchor.equals(TextAnchor.BOTTOM_LEFT);
    }

    private static boolean isHorizontalCenter(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_CENTER) || anchor.equals(TextAnchor.CENTER) || anchor.equals(TextAnchor.HALF_ASCENT_CENTER) || anchor.equals(TextAnchor.BASELINE_CENTER) || anchor.equals(TextAnchor.BOTTOM_CENTER);
    }

    private static boolean isHorizontalRight(TextAnchor anchor) {
        return anchor.equals(TextAnchor.TOP_RIGHT) || anchor.equals(TextAnchor.CENTER_RIGHT) || anchor.equals(TextAnchor.HALF_ASCENT_RIGHT) || anchor.equals(TextAnchor.BASELINE_RIGHT) || anchor.equals(TextAnchor.BOTTOM_RIGHT);
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
 * DefaultTitleEditor.java
 * -----------------------
 * (C) Copyright 2005-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Arnaud Lelievre;
 *                   Daniel Gredler;
 *
 */
/**
 * A panel for editing the properties of a chart title.
 */
class DefaultTitleEditor extends JPanel implements ActionListener {

    /**
     * whether to display the title on the chart.
     */
    private boolean showTitle;

    /**
     * The checkbox to indicate whether to display the title.
     */
    private final JCheckBox showTitleCheckBox;

    /**
     * A field for displaying/editing the title text.
     */
    private final JTextField titleField;

    /**
     * The font used to draw the title.
     */
    private Font titleFont;

    /**
     * A field for displaying a description of the title font.
     */
    private final JTextField fontfield;

    /**
     * The button to use to select a new title font.
     */
    private final JButton selectFontButton;

    /**
     * The paint (color) used to draw the title.
     */
    private final PaintSample titlePaint;

    /**
     * The button to use to select a new paint (color) to draw the title.
     */
    private final JButton selectPaintButton;

    /**
     * The resourceBundle for the localization.
     */
    protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.editor.LocalizationBundle");

    /**
     * Standard constructor: builds a panel for displaying/editing the
     * properties of the specified title.
     *
     * @param title  the title, which should be changed.
     */
    public DefaultTitleEditor(Title title) {
        TextTitle t = (title != null ? (TextTitle) title : new TextTitle(localizationResources.getString("Title")));
        this.showTitle = (title != null);
        this.titleFont = t.getFont();
        this.titleField = new JTextField(t.getText());
        this.titlePaint = new PaintSample(t.getPaint());
        setLayout(new BorderLayout());
        JPanel general = new JPanel(new BorderLayout());
        general.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), localizationResources.getString("General")));
        JPanel interior = new JPanel(new LCBLayout(4));
        interior.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        interior.add(new JLabel(localizationResources.getString("Show_Title")));
        this.showTitleCheckBox = new JCheckBox();
        this.showTitleCheckBox.setSelected(this.showTitle);
        this.showTitleCheckBox.setActionCommand("ShowTitle");
        this.showTitleCheckBox.addActionListener(this);
        interior.add(new JPanel());
        interior.add(this.showTitleCheckBox);
        JLabel titleLabel = new JLabel(localizationResources.getString("Text"));
        interior.add(titleLabel);
        interior.add(this.titleField);
        interior.add(new JPanel());
        JLabel fontLabel = new JLabel(localizationResources.getString("Font"));
        this.fontfield = new FontDisplayField(this.titleFont);
        this.selectFontButton = new JButton(localizationResources.getString("Select..."));
        this.selectFontButton.setActionCommand("SelectFont");
        this.selectFontButton.addActionListener(this);
        interior.add(fontLabel);
        interior.add(this.fontfield);
        interior.add(this.selectFontButton);
        JLabel colorLabel = new JLabel(localizationResources.getString("Color"));
        this.selectPaintButton = new JButton(localizationResources.getString("Select..."));
        this.selectPaintButton.setActionCommand("SelectPaint");
        this.selectPaintButton.addActionListener(this);
        interior.add(colorLabel);
        interior.add(this.titlePaint);
        interior.add(this.selectPaintButton);
        this.enableOrDisableControls();
        general.add(interior);
        add(general, BorderLayout.NORTH);
    }

    /**
     * Returns the title text entered in the panel.
     *
     * @return The title text entered in the panel.
     */
    public String getTitleText() {
        return this.titleField.getText();
    }

    /**
     * Returns the font selected in the panel.
     *
     * @return The font selected in the panel.
     */
    public Font getTitleFont() {
        return this.titleFont;
    }

    /**
     * Returns the paint selected in the panel.
     *
     * @return The paint selected in the panel.
     */
    public Paint getTitlePaint() {
        return this.titlePaint.getPaint();
    }

    /**
     * Handles button clicks by passing control to an appropriate handler
     * method.
     *
     * @param event  the event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("SelectFont")) {
            attemptFontSelection();
        } else if (command.equals("SelectPaint")) {
            attemptPaintSelection();
        } else if (command.equals("ShowTitle")) {
            attemptModifyShowTitle();
        }
    }

    /**
     * Presents a font selection dialog to the user.
     */
    public void attemptFontSelection() {
        FontChooserPanel panel = new FontChooserPanel(this.titleFont);
        int result = JOptionPane.showConfirmDialog(this, panel, localizationResources.getString("Font_Selection"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            this.titleFont = panel.getSelectedFont();
            this.fontfield.setText(this.titleFont.getFontName() + " " + this.titleFont.getSize());
        }
    }

    /**
     * Allow the user the opportunity to select a Paint object.  For now, we
     * just use the standard color chooser - all colors are Paint objects, but
     * not all Paint objects are colors (later we can implement a more general
     * Paint chooser).
     */
    public void attemptPaintSelection() {
        Paint p = this.titlePaint.getPaint();
        Color defaultColor = (p instanceof Color ? (Color) p : Color.BLUE);
        Color c = JColorChooser.showDialog(this, localizationResources.getString("Title_Color"), defaultColor);
        if (c != null) {
            this.titlePaint.setPaint(c);
        }
    }

    /**
     * Allow the user the opportunity to change whether the title is
     * displayed on the chart or not.
     */
    private void attemptModifyShowTitle() {
        this.showTitle = this.showTitleCheckBox.isSelected();
        this.enableOrDisableControls();
    }

    /**
     * If we are supposed to show the title, the controls are enabled.
     * If we are not supposed to show the title, the controls are disabled.
     */
    private void enableOrDisableControls() {
        boolean enabled = (this.showTitle == true);
        this.titleField.setEnabled(enabled);
        this.selectFontButton.setEnabled(enabled);
        this.selectPaintButton.setEnabled(enabled);
    }

    /**
     * Sets the properties of the specified title to match the properties
     * defined on this panel.
     *
     * @param chart  the chart whose title is to be modified.
     */
    public void setTitleProperties(JFreeChart chart) {
        if (this.showTitle) {
            TextTitle title = chart.getTitle();
            if (title == null) {
                title = new TextTitle();
                chart.setTitle(title);
            }
            title.setText(getTitleText());
            title.setFont(getTitleFont());
            title.setPaint(getTitlePaint());
        } else {
            chart.setTitle((TextTitle) null);
        }
    }
}
