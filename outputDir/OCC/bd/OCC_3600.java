package OCC.bd;
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
 * ---------------------
 * WindItemRenderer.java
 * ---------------------
 * (C) Copyright 2001-2021, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A specialised renderer for displaying wind intensity/direction data.
 * The example shown here is generated by the {@code WindChartDemo1.java}
 * program included in the JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/WindItemRendererSample.png"
 * alt="WindItemRendererSample.png">
 */
class WindItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 8078914101916976844L;

    /**
     * Creates a new renderer.
     */
    public WindItemRenderer() {
        super();
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plotArea  the area within which the plot is being drawn.
     * @param info  optional information collection.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the horizontal axis.
     * @param rangeAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D plotArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        WindDataset windData = (WindDataset) dataset;
        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        // get the data point...
        Number x = windData.getX(series, item);
        Number windDir = windData.getWindDirection(series, item);
        Number wforce = windData.getWindForce(series, item);
        double windForce = wforce.doubleValue();
        double wdirt = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0);
        double ax1, ax2, ay1, ay2, rax2, ray2;
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        ax1 = domainAxis.valueToJava2D(x.doubleValue(), plotArea, domainAxisLocation);
        ay1 = rangeAxis.valueToJava2D(0.0, plotArea, rangeAxisLocation);
        rax2 = x.doubleValue() + (windForce * Math.cos(wdirt) * 8000000.0);
        ray2 = windForce * Math.sin(wdirt);
        ax2 = domainAxis.valueToJava2D(rax2, plotArea, domainAxisLocation);
        ay2 = rangeAxis.valueToJava2D(ray2, plotArea, rangeAxisLocation);
        int diri = windDir.intValue();
        int forcei = wforce.intValue();
        String dirforce = diri + "-" + forcei;
        Line2D line = new Line2D.Double(ax1, ay1, ax2, ay2);
        g2.draw(line);
        g2.setPaint(Color.BLUE);
        g2.setFont(new Font("Dialog", 1, 9));
        g2.drawString(dirforce, (float) ax1, (float) ay1);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        double alx2, aly2, arx2, ary2;
        double ralx2, raly2, rarx2, rary2;
        double aldir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 - 5.0);
        ralx2 = wforce.doubleValue() * Math.cos(aldir) * 8000000 * 0.8 + x.doubleValue();
        raly2 = wforce.doubleValue() * Math.sin(aldir) * 0.8;
        alx2 = domainAxis.valueToJava2D(ralx2, plotArea, domainAxisLocation);
        aly2 = rangeAxis.valueToJava2D(raly2, plotArea, rangeAxisLocation);
        line = new Line2D.Double(alx2, aly2, ax2, ay2);
        g2.draw(line);
        double ardir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 + 5.0);
        rarx2 = wforce.doubleValue() * Math.cos(ardir) * 8000000 * 0.8 + x.doubleValue();
        rary2 = wforce.doubleValue() * Math.sin(ardir) * 0.8;
        arx2 = domainAxis.valueToJava2D(rarx2, plotArea, domainAxisLocation);
        ary2 = rangeAxis.valueToJava2D(rary2, plotArea, rangeAxisLocation);
        line = new Line2D.Double(arx2, ary2, ax2, ay2);
        g2.draw(line);
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
        return super.clone();
    }
}
