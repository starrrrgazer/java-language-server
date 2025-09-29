package OCC.bl;
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
 * ---------------------------------
 * StandardCategoryURLGenerator.java
 * ---------------------------------
 * (C) Copyright 2002-2021, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributors:     David Gilbert;
 *                   Cleland Early;
 *
 */
/**
 * A URL generator that can be assigned to a
 * {@link org.jfree.chart.renderer.category.CategoryItemRenderer}.
 */
public class StandardCategoryURLGenerator implements CategoryURLGenerator, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 2276668053074881909L;

    /**
     * Prefix to the URL
     */
    private String prefix = "index.html";

    /**
     * Series parameter name to go in each URL
     */
    private String seriesParameterName = "series";

    /**
     * Category parameter name to go in each URL
     */
    private String categoryParameterName = "category";

    /**
     * Creates a new generator with default settings.
     */
    public StandardCategoryURLGenerator() {
        super();
    }

    /**
     * Constructor that overrides default prefix to the URL.
     *
     * @param prefix  the prefix to the URL ({@code null} not permitted).
     */
    public StandardCategoryURLGenerator(String prefix) {
        Args.nullNotPermitted(prefix, "prefix");
        this.prefix = prefix;
    }

    /**
     * Constructor that overrides all the defaults.
     *
     * @param prefix  the prefix to the URL ({@code null} not permitted).
     * @param seriesParameterName  the name of the series parameter to go in
     *                             each URL ({@code null} not permitted).
     * @param categoryParameterName  the name of the category parameter to go in
     *                               each URL ({@code null} not permitted).
     */
    public StandardCategoryURLGenerator(String prefix, String seriesParameterName, String categoryParameterName) {
        Args.nullNotPermitted(prefix, "prefix");
        Args.nullNotPermitted(seriesParameterName, "seriesParameterName");
        Args.nullNotPermitted(categoryParameterName, "categoryParameterName");
        this.prefix = prefix;
        this.seriesParameterName = seriesParameterName;
        this.categoryParameterName = categoryParameterName;
    }

    /**
     * Generates a URL for a particular item within a series.
     *
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param category  the category index (zero-based).
     *
     * @return The generated URL.
     */
    @Override
    public String generateURL(CategoryDataset dataset, int series, int category) {
        String url = this.prefix;
        Comparable seriesKey = dataset.getRowKey(series);
        Comparable categoryKey = dataset.getColumnKey(category);
        boolean firstParameter = !url.contains("?");
        url += firstParameter ? "?" : "&amp;";
        try {
            url += this.seriesParameterName + "=" + URLEncoder.encode(seriesKey.toString(), "UTF-8");
            url += "&amp;" + this.categoryParameterName + "=" + URLEncoder.encode(categoryKey.toString(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            // this won't happen :)
            throw new RuntimeException(ex);
        }
        return url;
    }

    /**
     * Returns an independent copy of the URL generator.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException not thrown by this class, but
     *         subclasses (if any) might.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // all attributes are immutable, so we can just return the super.clone()
        // FIXME: in fact, the generator itself is immutable, so cloning is
        // not necessary
        return super.clone();
    }

    /**
     * Tests the generator for equality with an arbitrary object.
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
        if (!(obj instanceof StandardCategoryURLGenerator)) {
            return false;
        }
        StandardCategoryURLGenerator that = (StandardCategoryURLGenerator) obj;
        if (!Objects.equals(this.prefix, that.prefix)) {
            return false;
        }
        if (!Objects.equals(this.seriesParameterName, that.seriesParameterName)) {
            return false;
        }
        if (!Objects.equals(this.categoryParameterName, that.categoryParameterName)) {
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
        result = (this.prefix != null ? this.prefix.hashCode() : 0);
        result = 29 * result + (this.seriesParameterName != null ? this.seriesParameterName.hashCode() : 0);
        result = 29 * result + (this.categoryParameterName != null ? this.categoryParameterName.hashCode() : 0);
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
 * ---------------------
 * DefaultXYDataset.java
 * ---------------------
 * (C) Copyright 2006-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A default implementation of the {@link XYDataset} interface that stores
 * data values in arrays of double primitives.
 *
 * @param <S> the type of the series keys ({@code String} is commonly used).
 *
 * @since 1.0.2
 */
public class DefaultXYDataset<S extends Comparable<S>> extends AbstractXYDataset<S> implements XYDataset<S>, PublicCloneable {

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
     * Creates a new {@code DefaultXYDataset} instance, initially
     * containing no data.
     */
    public DefaultXYDataset() {
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
     * Returns the index of the series with the specified key, or -1 if there
     * is no such series in the dataset.
     *
     * @param seriesKey  the series key ({@code null} permitted).
     *
     * @return The index, or -1.
     */
    @Override
    public int indexOf(S seriesKey) {
        return this.seriesKeys.indexOf(seriesKey);
    }

    /**
     * Returns the order of the domain (x-) values in the dataset.  In this
     * implementation, we cannot guarantee that the x-values are ordered, so
     * this method returns {@code DomainOrder.NONE}.
     *
     * @return {@code DomainOrder.NONE}.
     */
    @Override
    public DomainOrder getDomainOrder() {
        return DomainOrder.NONE;
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
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
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
     * @see #getY(int, int)
     */
    @Override
    public double getYValue(int series, int item) {
        double[][] seriesData = this.seriesList.get(series);
        return seriesData[1][item];
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
     * @see #getX(int, int)
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
     * @param data  the data (must be an array with length 2, containing two
     *     arrays of equal length, the first containing the x-values and the
     *     second containing the y-values).
     */
    public void addSeries(S seriesKey, double[][] data) {
        if (seriesKey == null) {
            throw new IllegalArgumentException("The 'seriesKey' cannot be null.");
        }
        if (data == null) {
            throw new IllegalArgumentException("The 'data' is null.");
        }
        if (data.length != 2) {
            throw new IllegalArgumentException("The 'data' array must have length == 2.");
        }
        if (data[0].length != data[1].length) {
            throw new IllegalArgumentException("The 'data' array must contain two arrays with equal length.");
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
     * Removes a series from the dataset, then sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param seriesKey  the series key ({@code null} not permitted).
     */
    public void removeSeries(S seriesKey) {
        int seriesIndex = indexOf(seriesKey);
        if (seriesIndex >= 0) {
            this.seriesKeys.remove(seriesIndex);
            this.seriesList.remove(seriesIndex);
            notifyListeners(new DatasetChangeEvent(this, this));
        }
    }

    /**
     * Tests this {@code DefaultXYDataset} instance for equality with an
     * arbitrary object.  This method returns {@code true} if and only if:
     * <ul>
     * <li>{@code obj} is not {@code null};</li>
     * <li>{@code obj} is an instance of {@code DefaultXYDataset};</li>
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
        if (!(obj instanceof DefaultXYDataset)) {
            return false;
        }
        DefaultXYDataset that = (DefaultXYDataset) obj;
        if (!this.seriesKeys.equals(that.seriesKeys)) {
            return false;
        }
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] d1 = this.seriesList.get(i);
            double[][] d2 = (double[][]) that.seriesList.get(i);
            double[] d1x = d1[0];
            double[] d2x = d2[0];
            if (!Arrays.equals(d1x, d2x)) {
                return false;
            }
            double[] d1y = d1[1];
            double[] d2y = d2[1];
            if (!Arrays.equals(d1y, d2y)) {
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
     * Creates an independent copy of this dataset.
     *
     * @return The cloned dataset.
     *
     * @throws CloneNotSupportedException if there is a problem cloning the
     *     dataset (for instance, if a non-cloneable object is used for a
     *     series key).
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultXYDataset clone = (DefaultXYDataset) super.clone();
        clone.seriesKeys = new ArrayList(this.seriesKeys);
        clone.seriesList = new ArrayList(this.seriesList.size());
        for (int i = 0; i < this.seriesList.size(); i++) {
            double[][] data = this.seriesList.get(i);
            double[] x = data[0];
            double[] y = data[1];
            double[] xx = new double[x.length];
            double[] yy = new double[y.length];
            System.arraycopy(x, 0, xx, 0, x.length);
            System.arraycopy(y, 0, yy, 0, y.length);
            clone.seriesList.add(i, new double[][] { xx, yy });
        }
        return clone;
    }
}
