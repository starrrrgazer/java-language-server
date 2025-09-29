package OCC.bn;
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
 * ---------------
 * NumberAxis.java
 * ---------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Laurence Vanhelsuwe;
 *                   Peter Kolb (patches 1934255 and 2603321);
 * 
 */
/**
 * An axis for displaying numerical data.
 * <P>
 * If the axis is set up to automatically determine its range to fit the data,
 * you can ensure that the range includes zero (statisticians usually prefer
 * this) by setting the {@code autoRangeIncludesZero} flag to
 * {@code true}.
 * <P>
 * The {@code NumberAxis} class has a mechanism for automatically
 * selecting a tick unit that is appropriate for the current axis range.
 */
public class NumberAxis extends ValueAxis implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 2805933088476185789L;

    /**
     * The default value for the autoRangeIncludesZero flag.
     */
    public static final boolean DEFAULT_AUTO_RANGE_INCLUDES_ZERO = true;

    /**
     * The default value for the autoRangeStickyZero flag.
     */
    public static final boolean DEFAULT_AUTO_RANGE_STICKY_ZERO = true;

    /**
     * The default tick unit.
     */
    public static final NumberTickUnit DEFAULT_TICK_UNIT = new NumberTickUnit(1.0, new DecimalFormat("0"));

    /**
     * The default setting for the vertical tick labels flag.
     */
    public static final boolean DEFAULT_VERTICAL_TICK_LABELS = false;

    /**
     * The range type (can be used to force the axis to display only positive
     * values or only negative values).
     */
    private RangeType rangeType;

    /**
     * A flag that affects the axis range when the range is determined
     * automatically.  If the auto range does NOT include zero and this flag
     * is TRUE, then the range is changed to include zero.
     */
    private boolean autoRangeIncludesZero;

    /**
     * A flag that affects the size of the margins added to the axis range when
     * the range is determined automatically.  If the value 0 falls within the
     * margin and this flag is TRUE, then the margin is truncated at zero.
     */
    private boolean autoRangeStickyZero;

    /**
     * The tick unit for the axis.
     */
    private NumberTickUnit tickUnit;

    /**
     * The override number format.
     */
    private NumberFormat numberFormatOverride;

    /**
     * An optional band for marking regions on the axis.
     */
    private MarkerAxisBand markerBand;

    /**
     * Default constructor.
     */
    public NumberAxis() {
        this(null);
    }

    /**
     * Constructs a number axis, using default values where necessary.
     *
     * @param label  the axis label ({@code null} permitted).
     */
    public NumberAxis(String label) {
        super(label, NumberAxis.createStandardTickUnits());
        this.rangeType = RangeType.FULL;
        this.autoRangeIncludesZero = DEFAULT_AUTO_RANGE_INCLUDES_ZERO;
        this.autoRangeStickyZero = DEFAULT_AUTO_RANGE_STICKY_ZERO;
        this.tickUnit = DEFAULT_TICK_UNIT;
        this.numberFormatOverride = null;
        this.markerBand = null;
    }

    /**
     * Returns the axis range type.
     *
     * @return The axis range type (never {@code null}).
     *
     * @see #setRangeType(RangeType)
     */
    public RangeType getRangeType() {
        return this.rangeType;
    }

    /**
     * Sets the axis range type.
     *
     * @param rangeType  the range type ({@code null} not permitted).
     *
     * @see #getRangeType()
     */
    public void setRangeType(RangeType rangeType) {
        Args.nullNotPermitted(rangeType, "rangeType");
        this.rangeType = rangeType;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the flag that indicates whether the automatic axis range
     * (if indeed it is determined automatically) is forced to include zero.
     *
     * @return The flag.
     */
    public boolean getAutoRangeIncludesZero() {
        return this.autoRangeIncludesZero;
    }

    /**
     * Sets the flag that indicates whether the axis range, if
     * automatically calculated, is forced to include zero.
     * <p>
     * If the flag is changed to {@code true}, the axis range is
     * recalculated.
     * <p>
     * Any change to the flag will trigger an {@link AxisChangeEvent}.
     *
     * @param flag  the new value of the flag.
     *
     * @see #getAutoRangeIncludesZero()
     */
    public void setAutoRangeIncludesZero(boolean flag) {
        if (this.autoRangeIncludesZero != flag) {
            this.autoRangeIncludesZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns a flag that affects the auto-range when zero falls outside the
     * data range but inside the margins defined for the axis.
     *
     * @return The flag.
     *
     * @see #setAutoRangeStickyZero(boolean)
     */
    public boolean getAutoRangeStickyZero() {
        return this.autoRangeStickyZero;
    }

    /**
     * Sets a flag that affects the auto-range when zero falls outside the data
     * range but inside the margins defined for the axis.
     *
     * @param flag  the new flag.
     *
     * @see #getAutoRangeStickyZero()
     */
    public void setAutoRangeStickyZero(boolean flag) {
        if (this.autoRangeStickyZero != flag) {
            this.autoRangeStickyZero = flag;
            if (isAutoRange()) {
                autoAdjustRange();
            }
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the tick unit for the axis.
     * <p>
     * Note: if the {@code autoTickUnitSelection} flag is
     * {@code true} the tick unit may be changed while the axis is being
     * drawn, so in that case the return value from this method may be
     * irrelevant if the method is called before the axis has been drawn.
     *
     * @return The tick unit for the axis.
     *
     * @see #setTickUnit(NumberTickUnit)
     * @see ValueAxis#isAutoTickUnitSelection()
     */
    public NumberTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis and sends an {@link AxisChangeEvent} to
     * all registered listeners.  A side effect of calling this method is that
     * the "auto-select" feature for tick units is switched off (you can
     * restore it using the {@link ValueAxis#setAutoTickUnitSelection(boolean)}
     * method).
     *
     * @param unit  the new tick unit ({@code null} not permitted).
     *
     * @see #getTickUnit()
     * @see #setTickUnit(NumberTickUnit, boolean, boolean)
     */
    public void setTickUnit(NumberTickUnit unit) {
        // defer argument checking...
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit for the axis and, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.  In addition, an
     * option is provided to turn off the "auto-select" feature for tick units
     * (you can restore it using the
     * {@link ValueAxis#setAutoTickUnitSelection(boolean)} method).
     *
     * @param unit  the new tick unit ({@code null} not permitted).
     * @param notify  notify listeners?
     * @param turnOffAutoSelect  turn off the auto-tick selection?
     */
    public void setTickUnit(NumberTickUnit unit, boolean notify, boolean turnOffAutoSelect) {
        Args.nullNotPermitted(unit, "unit");
        this.tickUnit = unit;
        if (turnOffAutoSelect) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            notifyListeners(new AxisChangeEvent(this));
        }
    }

    /**
     * Returns the number format override.  If this is non-null, then it will
     * be used to format the numbers on the axis.
     *
     * @return The number formatter (possibly {@code null}).
     *
     * @see #setNumberFormatOverride(NumberFormat)
     */
    public NumberFormat getNumberFormatOverride() {
        return this.numberFormatOverride;
    }

    /**
     * Sets the number format override.  If this is non-null, then it will be
     * used to format the numbers on the axis.
     *
     * @param formatter  the number formatter ({@code null} permitted).
     *
     * @see #getNumberFormatOverride()
     */
    public void setNumberFormatOverride(NumberFormat formatter) {
        this.numberFormatOverride = formatter;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Returns the (optional) marker band for the axis.
     *
     * @return The marker band (possibly {@code null}).
     *
     * @see #setMarkerBand(MarkerAxisBand)
     */
    public MarkerAxisBand getMarkerBand() {
        return this.markerBand;
    }

    /**
     * Sets the marker band for the axis.
     * <P>
     * The marker band is optional, leave it set to {@code null} if you
     * don't require it.
     *
     * @param band the new band ({@code null} permitted).
     *
     * @see #getMarkerBand()
     */
    public void setMarkerBand(MarkerAxisBand band) {
        this.markerBand = band;
        notifyListeners(new AxisChangeEvent(this));
    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has
     * auto-scaling, then sets the maximum and minimum values.
     */
    @Override
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
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
            double upper = r.getUpperBound();
            double lower = r.getLowerBound();
            if (this.rangeType == RangeType.POSITIVE) {
                lower = Math.max(0.0, lower);
                upper = Math.max(0.0, upper);
            } else if (this.rangeType == RangeType.NEGATIVE) {
                lower = Math.min(0.0, lower);
                upper = Math.min(0.0, upper);
            }
            if (getAutoRangeIncludesZero()) {
                lower = Math.min(lower, 0.0);
                upper = Math.max(upper, 0.0);
            }
            double range = upper - lower;
            // if fixed auto range, then derive lower bound...
            double fixedAutoRange = getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            } else {
                // ensure the autorange is at least <minRange> in size...
                double minRange = getAutoRangeMinimumSize();
                if (range < minRange) {
                    double expand = (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                    if (lower == upper) {
                        // see bug report 1549218
                        double adjust = Math.abs(lower) / 10.0;
                        lower = lower - adjust;
                        upper = upper + adjust;
                    }
                    if (this.rangeType == RangeType.POSITIVE) {
                        if (lower < 0.0) {
                            upper = upper - lower;
                            lower = 0.0;
                        }
                    } else if (this.rangeType == RangeType.NEGATIVE) {
                        if (upper > 0.0) {
                            lower = lower - upper;
                            upper = 0.0;
                        }
                    }
                }
                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + getUpperMargin() * range);
                    } else {
                        upper = upper + getUpperMargin() * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - getLowerMargin() * range);
                    } else {
                        lower = lower - getLowerMargin() * range;
                    }
                } else {
                    upper = upper + getUpperMargin() * range;
                    lower = lower - getLowerMargin() * range;
                }
            }
            setRange(new Range(lower, upper), false, false);
        }
    }

    /**
     * Converts a data value to a coordinate in Java2D space, assuming that the
     * axis runs along one edge of the specified dataArea.
     * <p>
     * Note that it is possible for the coordinate to fall outside the plotArea.
     *
     * @param value  the data value.
     * @param area  the area for plotting the data.
     * @param edge  the axis location.
     *
     * @return The Java2D coordinate.
     *
     * @see #java2DToValue(double, Rectangle2D, RectangleEdge)
     */
    @Override
    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            max = area.getMinY();
            min = area.getMaxY();
        }
        if (isInverted()) {
            return max - ((value - axisMin) / (axisMax - axisMin)) * (max - min);
        } else {
            return min + ((value - axisMin) / (axisMax - axisMin)) * (max - min);
        }
    }

    /**
     * Converts a coordinate in Java2D space to the corresponding data value,
     * assuming that the axis runs along one edge of the specified dataArea.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the area in which the data is plotted.
     * @param edge  the location.
     *
     * @return The data value.
     *
     * @see #valueToJava2D(double, Rectangle2D, RectangleEdge)
     */
    @Override
    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        Range range = getRange();
        double axisMin = range.getLowerBound();
        double axisMax = range.getUpperBound();
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        if (isInverted()) {
            return axisMax - (java2DValue - min) / (max - min) * (axisMax - axisMin);
        } else {
            return axisMin + (java2DValue - min) / (max - min) * (axisMax - axisMin);
        }
    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @return The value of the lowest visible tick on the axis.
     *
     * @see #calculateHighestVisibleTickValue()
     */
    protected double calculateLowestVisibleTickValue() {
        double unit = getTickUnit().getSize();
        double index = Math.ceil(getRange().getLowerBound() / unit);
        return index * unit;
    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @return The value of the highest visible tick on the axis.
     *
     * @see #calculateLowestVisibleTickValue()
     */
    protected double calculateHighestVisibleTickValue() {
        double unit = getTickUnit().getSize();
        double index = Math.floor(getRange().getUpperBound() / unit);
        return index * unit;
    }

    /**
     * Calculates the number of visible ticks.
     *
     * @return The number of visible ticks on the axis.
     */
    protected int calculateVisibleTickCount() {
        double unit = getTickUnit().getSize();
        Range range = getRange();
        return (int) (Math.floor(range.getUpperBound() / unit) - Math.ceil(range.getLowerBound() / unit) + 1);
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be drawn
     *                  ({@code null} not permitted).
     * @param dataArea  the area within which the data should be drawn
     *                  ({@code null} not permitted).
     * @param edge  the location of the axis ({@code null} not permitted).
     * @param plotState  collects information about the plot
     *                   ({@code null} permitted).
     *
     * @return The axis state (never {@code null}).
     */
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState state;
        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            state = new AxisState(cursor);
            // even though the axis is not visible, we need ticks for the
            // gridlines...
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        // draw the tick marks and labels...
        state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        if (getAttributedLabel() != null) {
            state = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        } else {
            state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        }
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;
    }

    /**
     * Creates the standard tick units.
     * <P>
     * If you don't like these defaults, create your own instance of TickUnits
     * and then pass it to the setStandardTickUnits() method in the
     * NumberAxis class.
     *
     * @return The standard tick units.
     *
     * @see #setStandardTickUnits(TickUnitSource)
     * @see #createIntegerTickUnits()
     */
    public static TickUnitSource createStandardTickUnits() {
        return new NumberTickUnitSource();
    }

    /**
     * Returns a collection of tick units for integer values.
     *
     * @return A collection of tick units for integer values.
     *
     * @see #setStandardTickUnits(TickUnitSource)
     * @see #createStandardTickUnits()
     */
    public static TickUnitSource createIntegerTickUnits() {
        return new NumberTickUnitSource(true);
    }

    /**
     * Creates a collection of standard tick units.  The supplied locale is
     * used to create the number formatter (a localised instance of
     * {@code NumberFormat}).
     * <P>
     * If you don't like these defaults, create your own instance of
     * {@link TickUnits} and then pass it to the
     * {@code setStandardTickUnits()} method.
     *
     * @param locale  the locale.
     *
     * @return A tick unit collection.
     *
     * @see #setStandardTickUnits(TickUnitSource)
     */
    public static TickUnitSource createStandardTickUnits(Locale locale) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return new NumberTickUnitSource(false, numberFormat);
    }

    /**
     * Returns a collection of tick units for integer values.
     * Uses a given Locale to create the DecimalFormats.
     *
     * @param locale the locale to use to represent Numbers.
     *
     * @return A collection of tick units for integer values.
     *
     * @see #setStandardTickUnits(TickUnitSource)
     */
    public static TickUnitSource createIntegerTickUnits(Locale locale) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
        return new NumberTickUnitSource(true, numberFormat);
    }

    /**
     * Estimates the maximum tick label height.
     *
     * @param g2  the graphics device.
     *
     * @return The maximum height.
     */
    protected double estimateMaximumTickLabelHeight(Graphics2D g2) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        result += tickLabelFont.getLineMetrics("123", frc).getHeight();
        return result;
    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified
     * tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we
     * just look at two values: the lower bound and the upper bound for the
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     */
    protected double estimateMaximumTickLabelWidth(Graphics2D g2, TickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of the
            // font)...
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = getTickLabelFont().getLineMetrics("0", frc);
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
            Range range = getRange();
            double lower = range.getLowerBound();
            double upper = range.getUpperBound();
            String lowerStr, upperStr;
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.valueToString(lower);
                upperStr = unit.valueToString(upper);
            }
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        TickUnit unit = getTickUnit();
        TickUnitSource tickUnitSource = getStandardTickUnits();
        // we should start with the current tick unit if it gives a count in
        // the range 3 to 40 otherwise estimate one that will give a count <= 10
        double length = getRange().getLength();
        int count = (int) (length / unit.getSize());
        if (count < 3 || count > 40) {
            unit = tickUnitSource.getCeilingTickUnit(length / 10);
        }
        // now consider the label size relative to the width of the tick unit
        // and make a guess at the ideal size
        TickUnit unit1 = tickUnitSource.getCeilingTickUnit(unit);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit1);
        double unit1Width = lengthToJava2D(unit1.getSize(), dataArea, edge);
        NumberTickUnit unit2 = (NumberTickUnit) unit1;
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();
        // due to limitations of double precision, when you zoom very far into
        // a chart, eventually the visible axis range will get reported as
        // having length 0, and then 'guess' above will be infinite ... in that
        // case we'll just stick with the tick unit we have, it's better than
        // throwing an exception
        // https://github.com/jfree/jfreechart/issues/64
        if (Double.isFinite(guess)) {
            unit2 = (NumberTickUnit) tickUnitSource.getCeilingTickUnit(guess);
            double unit2Width = lengthToJava2D(unit2.getSize(), dataArea, edge);
            tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
            if (tickLabelWidth > unit2Width) {
                unit2 = (NumberTickUnit) tickUnitSource.getLargerTickUnit(unit2);
            }
        }
        setTickUnit(unit2, false, false);
    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelHeight = estimateMaximumTickLabelHeight(g2);
        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double unitHeight = lengthToJava2D(unit1.getSize(), dataArea, edge);
        double guess;
        if (unitHeight > 0) {
            // then extrapolate...
            guess = (tickLabelHeight / unitHeight) * unit1.getSize();
        } else {
            guess = getRange().getLength() / 20.0;
        }
        NumberTickUnit unit2 = (NumberTickUnit) tickUnits.getCeilingTickUnit(guess);
        double unit2Height = lengthToJava2D(unit2.getSize(), dataArea, edge);
        tickLabelHeight = estimateMaximumTickLabelHeight(g2);
        if (tickLabelHeight > unit2Height) {
            unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    @Override
    public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new java.util.ArrayList();
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new java.util.ArrayList();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        TickUnit tu = getTickUnit();
        double size = tu.getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = tu.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                double minorTickValue = lowestTickValue - size * minorTick / minorTickSpaces;
                if (getRange().contains(minorTickValue)) {
                    result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                TextAnchor anchor, rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = Math.PI / 2.0;
                    } else {
                        angle = -Math.PI / 2.0;
                    }
                } else {
                    if (edge == RectangleEdge.TOP) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    } else {
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                }
                Tick tick = new NumberTick(currentTickValue, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                double nextTickValue = lowestTickValue + ((i + 1) * size);
                for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    double minorTickValue = currentTickValue + (nextTickValue - currentTickValue) * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickValue)) {
                        result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new java.util.ArrayList();
        result.clear();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        TickUnit tu = getTickUnit();
        double size = tu.getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = tu.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                double minorTickValue = lowestTickValue - size * minorTick / minorTickSpaces;
                if (getRange().contains(minorTickValue)) {
                    result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = getTickUnit().valueToString(currentTickValue);
                }
                TextAnchor anchor;
                TextAnchor rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = -Math.PI / 2.0;
                    } else {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                        angle = Math.PI / 2.0;
                    }
                } else {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    } else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }
                Tick tick = new NumberTick(currentTickValue, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                double nextTickValue = lowestTickValue + ((i + 1) * size);
                for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    double minorTickValue = currentTickValue + (nextTickValue - currentTickValue) * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickValue)) {
                        result.add(new NumberTick(TickType.MINOR, minorTickValue, "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns a clone of the axis.
     *
     * @return A clone
     *
     * @throws CloneNotSupportedException if some component of the axis does
     *         not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        NumberAxis clone = (NumberAxis) super.clone();
        if (this.numberFormatOverride != null) {
            clone.numberFormatOverride = (NumberFormat) this.numberFormatOverride.clone();
        }
        return clone;
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
        if (!(obj instanceof NumberAxis)) {
            return false;
        }
        NumberAxis that = (NumberAxis) obj;
        if (this.autoRangeIncludesZero != that.autoRangeIncludesZero) {
            return false;
        }
        if (this.autoRangeStickyZero != that.autoRangeStickyZero) {
            return false;
        }
        if (!Objects.equals(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!Objects.equals(this.numberFormatOverride, that.numberFormatOverride)) {
            return false;
        }
        if (!this.rangeType.equals(that.rangeType)) {
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
 * IntervalCategoryToolTipGenerator.java
 * -------------------------------------
 * (C) Copyright 2004-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A tooltip generator for plots that use data from an
 * {@link IntervalCategoryDataset}.
 */
public class IntervalCategoryToolTipGenerator extends StandardCategoryToolTipGenerator {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -3853824986520333437L;

    /**
     * The default format string.
     */
    public static final String DEFAULT_TOOL_TIP_FORMAT_STRING = "({0}, {1}) = {3} - {4}";

    /**
     * Creates a new generator with a default number formatter.
     */
    public IntervalCategoryToolTipGenerator() {
        super(DEFAULT_TOOL_TIP_FORMAT_STRING, NumberFormat.getInstance());
    }

    /**
     * Creates a new generator with the specified number formatter.
     *
     * @param labelFormat  the label format string ({@code null} not
     *                     permitted).
     * @param formatter  the number formatter ({@code null} not permitted).
     */
    public IntervalCategoryToolTipGenerator(String labelFormat, NumberFormat formatter) {
        super(labelFormat, formatter);
    }

    /**
     * Creates a new generator with the specified date formatter.
     *
     * @param labelFormat  the label format string ({@code null} not
     *                     permitted).
     * @param formatter  the date formatter ({@code null} not permitted).
     */
    public IntervalCategoryToolTipGenerator(String labelFormat, DateFormat formatter) {
        super(labelFormat, formatter);
    }

    /**
     * Creates the array of items that can be passed to the
     * {@code MessageFormat} class for creating labels.
     *
     * @param dataset  the dataset ({@code null} not permitted).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The items (never {@code null}).
     */
    @Override
    protected Object[] createItemArray(CategoryDataset dataset, int row, int column) {
        Object[] result = new Object[5];
        result[0] = dataset.getRowKey(row).toString();
        result[1] = dataset.getColumnKey(column).toString();
        Number value = dataset.getValue(row, column);
        if (getNumberFormat() != null) {
            result[2] = getNumberFormat().format(value);
        } else if (getDateFormat() != null) {
            result[2] = getDateFormat().format(value);
        }
        if (dataset instanceof IntervalCategoryDataset) {
            IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
            Number start = icd.getStartValue(row, column);
            Number end = icd.getEndValue(row, column);
            if (getNumberFormat() != null) {
                result[3] = getNumberFormat().format(start);
                result[4] = getNumberFormat().format(end);
            } else if (getDateFormat() != null) {
                result[3] = getDateFormat().format(start);
                result[4] = getDateFormat().format(end);
            }
        }
        return result;
    }

    /**
     * Tests this tool tip generator for equality with an arbitrary
     * object.
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
        if (!(obj instanceof IntervalCategoryToolTipGenerator)) {
            return false;
        }
        // no fields to test
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
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
 * ------------
 * PiePlot.java
 * ------------
 * (C) Copyright 2000-2021, by Andrzej Porebski and Contributors.
 *
 * Original Author:  Andrzej Porebski;
 * Contributor(s):   David Gilbert;
 *                   Martin Cordova (percentages in labels);
 *                   Richard Atkinson (URL support for image maps);
 *                   Christian W. Zuckschwerdt;
 *                   Arnaud Lelievre;
 *                   Martin Hilpert (patch 1891849);
 *                   Andreas Schroeder (very minor);
 *                   Christoph Beck (bug 2121818);
 *                   Tracy Hiltbrand (Added generics for bug fix);
 * 
 */
/**
 * A plot that displays data in the form of a pie chart, using data from any
 * class that implements the {@link PieDataset} interface.
 * The example shown here is generated by the {@code PieChartDemo2.java}
 * program included in the JFreeChart Demo Collection:
 * <br><br>
 * <img src="doc-files/PieChartDemo2.svg" alt="PieChartDemo2.svg">
 * <P>
 * Special notes:
 * <ol>
 * <li>the default starting point is 12 o'clock and the pie sections proceed
 * in a clockwise direction, but these settings can be changed;</li>
 * <li>negative values in the dataset are ignored;</li>
 * <li>there are utility methods for creating a {@link PieDataset} from a
 * {@link org.jfree.data.category.CategoryDataset};</li>
 * </ol>
 *
 * @param <K> Key type for PieDataset
 *
 * @see Plot
 * @see PieDataset
 */
public class PiePlot<K extends Comparable<K>> extends Plot implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -795612466005590431L;

    /**
     * The default interior gap.
     */
    public static final double DEFAULT_INTERIOR_GAP = 0.08;

    /**
     * The maximum interior gap (currently 40%).
     */
    public static final double MAX_INTERIOR_GAP = 0.40;

    /**
     * The default starting angle for the pie chart.
     */
    public static final double DEFAULT_START_ANGLE = 90.0;

    /**
     * The default section label font.
     */
    public static final Font DEFAULT_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

    /**
     * The default section label paint.
     */
    public static final Paint DEFAULT_LABEL_PAINT = Color.BLACK;

    /**
     * The default section label background paint.
     */
    public static final Paint DEFAULT_LABEL_BACKGROUND_PAINT = new Color(255, 255, 192);

    /**
     * The default section label outline paint.
     */
    public static final Paint DEFAULT_LABEL_OUTLINE_PAINT = Color.BLACK;

    /**
     * The default section label outline stroke.
     */
    public static final Stroke DEFAULT_LABEL_OUTLINE_STROKE = new BasicStroke(0.5f);

    /**
     * The default section label shadow paint.
     */
    public static final Paint DEFAULT_LABEL_SHADOW_PAINT = new Color(151, 151, 151, 128);

    /**
     * The default minimum arc angle to draw.
     */
    public static final double DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW = 0.00001;

    /**
     * The dataset for the pie chart.
     */
    private PieDataset<K> dataset;

    /**
     * The pie index (used by the {@link MultiplePiePlot} class).
     */
    private int pieIndex;

    /**
     * The amount of space left around the outside of the pie plot, expressed
     * as a percentage of the plot area width and height.
     */
    private double interiorGap;

    /**
     * Flag determining whether to draw an ellipse or a perfect circle.
     */
    private boolean circular;

    /**
     * The starting angle.
     */
    private double startAngle;

    /**
     * The direction for the pie segments.
     */
    private Rotation direction;

    /**
     * The section paint map.
     */
    private Map<K, Paint> sectionPaintMap;

    /**
     * The default section paint (fallback).
     */
    private transient Paint defaultSectionPaint;

    /**
     * A flag that controls whether the section paint is auto-populated
     * from the drawing supplier.
     */
    private boolean autoPopulateSectionPaint;

    /**
     * A flag that controls whether an outline is drawn for each
     * section in the plot.
     */
    private boolean sectionOutlinesVisible;

    /**
     * The section outline paint map.
     */
    private Map<K, Paint> sectionOutlinePaintMap;

    /**
     * The default section outline paint (fallback).
     */
    private transient Paint defaultSectionOutlinePaint;

    /**
     * A flag that controls whether the section outline paint is
     * auto-populated from the drawing supplier.
     */
    private boolean autoPopulateSectionOutlinePaint;

    /**
     * The section outline stroke map.
     */
    private Map<K, Stroke> sectionOutlineStrokeMap;

    /**
     * The default section outline stroke (fallback).
     */
    private transient Stroke defaultSectionOutlineStroke;

    /**
     * A flag that controls whether the section outline stroke is
     * auto-populated from the drawing supplier.
     */
    private boolean autoPopulateSectionOutlineStroke;

    /**
     * The shadow paint.
     */
    private transient Paint shadowPaint = Color.GRAY;

    /**
     * The x-offset for the shadow effect.
     */
    private double shadowXOffset = 4.0f;

    /**
     * The y-offset for the shadow effect.
     */
    private double shadowYOffset = 4.0f;

    /**
     * The percentage amount to explode each pie section.
     */
    private Map<K, Double> explodePercentages;

    /**
     * The section label generator.
     */
    private PieSectionLabelGenerator labelGenerator;

    /**
     * The font used to display the section labels.
     */
    private Font labelFont;

    /**
     * The color used to draw the section labels.
     */
    private transient Paint labelPaint;

    /**
     * The color used to draw the background of the section labels.  If this
     * is {@code null}, the background is not filled.
     */
    private transient Paint labelBackgroundPaint;

    /**
     * The paint used to draw the outline of the section labels
     * ({@code null} permitted).
     */
    private transient Paint labelOutlinePaint;

    /**
     * The stroke used to draw the outline of the section labels
     * ({@code null} permitted).
     */
    private transient Stroke labelOutlineStroke;

    /**
     * The paint used to draw the shadow for the section labels
     * ({@code null} permitted).
     */
    private transient Paint labelShadowPaint;

    /**
     * A flag that controls whether simple or extended labels are used.
     */
    private boolean simpleLabels = true;

    /**
     * The padding between the labels and the label outlines.  This is not
     * allowed to be {@code null}.
     */
    private RectangleInsets labelPadding;

    /**
     * The simple label offset.
     */
    private RectangleInsets simpleLabelOffset;

    /**
     * The maximum label width as a percentage of the plot width.
     */
    private double maximumLabelWidth = 0.14;

    /**
     * The gap between the labels and the link corner, as a percentage of the
     * plot width.
     */
    private double labelGap = 0.025;

    /**
     * A flag that controls whether the label links are drawn.
     */
    private boolean labelLinksVisible;

    /**
     * The label link style.
     */
    private PieLabelLinkStyle labelLinkStyle = PieLabelLinkStyle.STANDARD;

    /**
     * The link margin.
     */
    private double labelLinkMargin = 0.025;

    /**
     * The paint used for the label linking lines.
     */
    private transient Paint labelLinkPaint = Color.BLACK;

    /**
     * The stroke used for the label linking lines.
     */
    private transient Stroke labelLinkStroke = new BasicStroke(0.5f);

    /**
     * The pie section label distributor.
     */
    private AbstractPieLabelDistributor labelDistributor;

    /**
     * The tooltip generator.
     */
    private PieToolTipGenerator toolTipGenerator;

    /**
     * The URL generator.
     */
    private PieURLGenerator urlGenerator;

    /**
     * The legend label generator.
     */
    private PieSectionLabelGenerator legendLabelGenerator;

    /**
     * A tool tip generator for the legend.
     */
    private PieSectionLabelGenerator<K> legendLabelToolTipGenerator;

    /**
     * A URL generator for the legend items (optional).
     */
    private PieURLGenerator legendLabelURLGenerator;

    /**
     * A flag that controls whether {@code null} values are ignored.
     */
    private boolean ignoreNullValues;

    /**
     * A flag that controls whether zero values are ignored.
     */
    private boolean ignoreZeroValues;

    /**
     * The legend item shape.
     */
    private transient Shape legendItemShape;

    /**
     * The smallest arc angle that will get drawn (this is to avoid a bug in
     * various Java implementations that causes the JVM to crash).  See this
     * link for details:
     *
     * https://www.jfree.org/phpBB2/viewtopic.php?t=2707
     *
     * ...and this bug report in the Java Bug Parade:
     *
     * http://developer.java.sun.com/developer/bugParade/bugs/4836495.html
     */
    private double minimumArcAngleToDraw;

    /**
     * The shadow generator for the plot ({@code null} permitted).
     */
    private ShadowGenerator shadowGenerator;

    /**
     * The resourceBundle for the localization.
     */
    protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * This debug flag controls whether an outline is drawn showing the
     * interior of the plot region.  This is drawn as a lightGray rectangle
     * showing the padding provided by the 'interiorGap' setting.
     */
    static final boolean DEBUG_DRAW_INTERIOR = false;

    /**
     * This debug flag controls whether an outline is drawn showing the
     * link area (in blue) and link ellipse (in yellow).  This controls where
     * the label links have 'elbow' points.
     */
    static final boolean DEBUG_DRAW_LINK_AREA = false;

    /**
     * This debug flag controls whether an outline is drawn showing
     * the pie area (in green).
     */
    static final boolean DEBUG_DRAW_PIE_AREA = false;

    /**
     * Creates a new plot.  The dataset is initially set to {@code null}.
     */
    public PiePlot() {
        this(null);
    }

    /**
     * Creates a plot that will draw a pie chart for the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     */
    public PiePlot(PieDataset<K> dataset) {
        super();
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.pieIndex = 0;
        this.interiorGap = DEFAULT_INTERIOR_GAP;
        this.circular = true;
        this.startAngle = DEFAULT_START_ANGLE;
        this.direction = Rotation.CLOCKWISE;
        this.minimumArcAngleToDraw = DEFAULT_MINIMUM_ARC_ANGLE_TO_DRAW;
        this.sectionPaintMap = new HashMap<>();
        this.defaultSectionPaint = Color.GRAY;
        this.autoPopulateSectionPaint = true;
        this.sectionOutlinesVisible = true;
        this.sectionOutlinePaintMap = new HashMap<>();
        this.defaultSectionOutlinePaint = DEFAULT_OUTLINE_PAINT;
        this.autoPopulateSectionOutlinePaint = false;
        this.sectionOutlineStrokeMap = new HashMap<>();
        this.defaultSectionOutlineStroke = DEFAULT_OUTLINE_STROKE;
        this.autoPopulateSectionOutlineStroke = false;
        this.explodePercentages = new TreeMap<>();
        this.labelGenerator = new StandardPieSectionLabelGenerator();
        this.labelFont = DEFAULT_LABEL_FONT;
        this.labelPaint = DEFAULT_LABEL_PAINT;
        this.labelBackgroundPaint = DEFAULT_LABEL_BACKGROUND_PAINT;
        this.labelOutlinePaint = DEFAULT_LABEL_OUTLINE_PAINT;
        this.labelOutlineStroke = DEFAULT_LABEL_OUTLINE_STROKE;
        this.labelShadowPaint = DEFAULT_LABEL_SHADOW_PAINT;
        this.labelLinksVisible = true;
        this.labelDistributor = new PieLabelDistributor(0);
        this.simpleLabels = false;
        this.simpleLabelOffset = new RectangleInsets(UnitType.RELATIVE, 0.18, 0.18, 0.18, 0.18);
        this.labelPadding = new RectangleInsets(2, 2, 2, 2);
        this.toolTipGenerator = null;
        this.urlGenerator = null;
        this.legendLabelGenerator = new StandardPieSectionLabelGenerator();
        this.legendLabelToolTipGenerator = null;
        this.legendLabelURLGenerator = null;
        this.legendItemShape = Plot.DEFAULT_LEGEND_ITEM_CIRCLE;
        this.ignoreNullValues = false;
        this.ignoreZeroValues = false;
        this.shadowGenerator = null;
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(PieDataset)
     */
    public PieDataset<K> getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset and sends a {@link DatasetChangeEvent} to 'this'.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     */
    public void setDataset(PieDataset<K> dataset) {
        // if there is an existing dataset, remove the plot from the list of
        // change listeners...
        PieDataset<K> existing = this.dataset;
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        // send a dataset change event to self...
        DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
        datasetChanged(event);
    }

    /**
     * Returns the pie index (this is used by the {@link MultiplePiePlot} class
     * to track subplots).
     *
     * @return The pie index.
     *
     * @see #setPieIndex(int)
     */
    public int getPieIndex() {
        return this.pieIndex;
    }

    /**
     * Sets the pie index (this is used by the {@link MultiplePiePlot} class to
     * track subplots).
     *
     * @param index  the index.
     *
     * @see #getPieIndex()
     */
    public void setPieIndex(int index) {
        this.pieIndex = index;
    }

    /**
     * Returns the start angle for the first pie section.  This is measured in
     * degrees starting from 3 o'clock and measuring anti-clockwise.
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
     * registered listeners.  The initial default value is 90 degrees, which
     * corresponds to 12 o'clock.  A value of zero corresponds to 3 o'clock...
     * this is the encoding used by Java's Arc2D class.
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
     * Returns the direction in which the pie sections are drawn (clockwise or
     * anti-clockwise).
     *
     * @return The direction (never {@code null}).
     *
     * @see #setDirection(Rotation)
     */
    public Rotation getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction in which the pie sections are drawn and sends a
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
     * registered listeners.  This controls the space between the edges of the
     * pie plot and the plot area itself (the region where the section labels
     * appear).
     *
     * @param percent  the gap (as a percentage of the available drawing space).
     *
     * @see #getInteriorGap()
     */
    public void setInteriorGap(double percent) {
        if ((percent < 0.0) || (percent > MAX_INTERIOR_GAP)) {
            throw new IllegalArgumentException("Invalid 'percent' (" + percent + ") argument.");
        }
        if (this.interiorGap != percent) {
            this.interiorGap = percent;
            fireChangeEvent();
        }
    }

    /**
     * Returns a flag indicating whether the pie chart is circular, or
     * stretched into an elliptical shape.
     *
     * @return A flag indicating whether the pie chart is circular.
     *
     * @see #setCircular(boolean)
     */
    public boolean isCircular() {
        return this.circular;
    }

    /**
     * A flag indicating whether the pie chart is circular, or stretched into
     * an elliptical shape.
     *
     * @param flag  the new value.
     *
     * @see #isCircular()
     */
    public void setCircular(boolean flag) {
        setCircular(flag, true);
    }

    /**
     * Sets the circular attribute and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param circular  the new value of the flag.
     * @param notify  notify listeners?
     *
     * @see #isCircular()
     */
    public void setCircular(boolean circular, boolean notify) {
        this.circular = circular;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether {@code null} values in the
     * dataset are ignored.
     *
     * @return A boolean.
     *
     * @see #setIgnoreNullValues(boolean)
     */
    public boolean getIgnoreNullValues() {
        return this.ignoreNullValues;
    }

    /**
     * Sets a flag that controls whether {@code null} values are ignored,
     * and sends a {@link PlotChangeEvent} to all registered listeners.  At
     * present, this only affects whether the key is presented in the
     * legend.
     *
     * @param flag  the flag.
     *
     * @see #getIgnoreNullValues()
     * @see #setIgnoreZeroValues(boolean)
     */
    public void setIgnoreNullValues(boolean flag) {
        this.ignoreNullValues = flag;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether zero values in the
     * dataset are ignored.
     *
     * @return A boolean.
     *
     * @see #setIgnoreZeroValues(boolean)
     */
    public boolean getIgnoreZeroValues() {
        return this.ignoreZeroValues;
    }

    /**
     * Sets a flag that controls whether zero values are ignored,
     * and sends a {@link PlotChangeEvent} to all registered listeners.  This
     * only affects whether a label appears for the non-visible
     * pie section.
     *
     * @param flag  the flag.
     *
     * @see #getIgnoreZeroValues()
     * @see #setIgnoreNullValues(boolean)
     */
    public void setIgnoreZeroValues(boolean flag) {
        this.ignoreZeroValues = flag;
        fireChangeEvent();
    }

    //// SECTION PAINT ////////////////////////////////////////////////////////
    /**
     * Returns the paint for the specified section.  This is equivalent to
     * {@code lookupSectionPaint(section, getAutoPopulateSectionPaint())}.
     *
     * @param key  the section key.
     *
     * @return The paint for the specified section.
     *
     * @see #lookupSectionPaint(K, boolean)
     */
    protected Paint lookupSectionPaint(K key) {
        return lookupSectionPaint(key, getAutoPopulateSectionPaint());
    }

    /**
     * Returns the paint for the specified section.  The lookup involves these
     * steps:
     * <ul>
     * <li>if {@link #getSectionPaint(K)} is non-{@code null} return it;</li>
     * <li>if {@link #getSectionPaint(K)} is {@code null} but
     *         {@code autoPopulate} is {@code true}, attempt to fetch
     *         a new paint from the drawing supplier
     *         ({@link #getDrawingSupplier()});
     * <li>if all else fails, return {@link #getDefaultSectionPaint()}.
     * </ul>
     *
     * @param key  the section key.
     * @param autoPopulate  a flag that controls whether the drawing supplier
     *     is used to auto-populate the section paint settings.
     *
     * @return The paint.
     */
    protected Paint lookupSectionPaint(K key, boolean autoPopulate) {
        // if not, check if there is a paint defined for the specified key
        Paint result = this.sectionPaintMap.get(key);
        if (result != null) {
            return result;
        }
        // nothing defined - do we autoPopulate?
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextPaint();
                this.sectionPaintMap.put(key, result);
            } else {
                result = this.defaultSectionPaint;
            }
        } else {
            result = this.defaultSectionPaint;
        }
        return result;
    }

    /**
     * Returns a key for the specified section. The preferred way of doing this
     * now is to link the attributes directly to the section key (there are new
     * methods for this, starting from version 1.0.3).
     *
     * @param section  the section index.
     *
     * @return The key.
     */
    protected K getSectionKey(int section) {
        K key = null;
        if (this.dataset != null) {
            if (section >= 0 && section < this.dataset.getItemCount()) {
                key = this.dataset.getKey(section);
            }
        }
        return key;
    }

    /**
     * Returns the paint associated with the specified key, or
     * {@code null} if there is no paint associated with the key.
     *
     * @param key  the key ({@code null} not permitted).
     *
     * @return The paint associated with the specified key, or
     *     {@code null}.
     *
     * @throws IllegalArgumentException if {@code key} is
     *     {@code null}.
     *
     * @see #setSectionPaint(K, Paint)
     */
    public Paint getSectionPaint(K key) {
        // null argument check delegated...
        return this.sectionPaintMap.get(key);
    }

    /**
     * Sets the paint associated with the specified key, and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key ({@code null} not permitted).
     * @param paint  the paint.
     *
     * @throws IllegalArgumentException if {@code key} is
     *     {@code null}.
     *
     * @see #getSectionPaint(K)
     */
    public void setSectionPaint(K key, Paint paint) {
        // null argument check delegated...
        this.sectionPaintMap.put(key, paint);
        fireChangeEvent();
    }

    /**
     * Clears the section paint settings for this plot and, if requested, sends
     * a {@link PlotChangeEvent} to all registered listeners.  Be aware that
     * if the {@code autoPopulateSectionPaint} flag is set, the section
     * paints may be repopulated using the same colours as before.
     *
     * @param notify  notify listeners?
     *
     * @see #autoPopulateSectionPaint
     */
    public void clearSectionPaints(boolean notify) {
        this.sectionPaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default section paint.  This is used when no other paint is
     * defined, which is rare.  The default value is {@code Color.GRAY}.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultSectionPaint(Paint)
     */
    public Paint getDefaultSectionPaint() {
        return this.defaultSectionPaint;
    }

    /**
     * Sets the default section paint and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultSectionPaint()
     */
    public void setDefaultSectionPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultSectionPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the section paint is
     * auto-populated by the {@link #lookupSectionPaint(K)} method.
     *
     * @return A boolean.
     */
    public boolean getAutoPopulateSectionPaint() {
        return this.autoPopulateSectionPaint;
    }

    /**
     * Sets the flag that controls whether the section paint is
     * auto-populated by the {@link #lookupSectionPaint(K)} method,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param auto  auto-populate?
     */
    public void setAutoPopulateSectionPaint(boolean auto) {
        this.autoPopulateSectionPaint = auto;
        fireChangeEvent();
    }

    //// SECTION OUTLINE PAINT ////////////////////////////////////////////////
    /**
     * Returns the flag that controls whether the outline is drawn for
     * each pie section.
     *
     * @return The flag that controls whether the outline is drawn for
     *         each pie section.
     *
     * @see #setSectionOutlinesVisible(boolean)
     */
    public boolean getSectionOutlinesVisible() {
        return this.sectionOutlinesVisible;
    }

    /**
     * Sets the flag that controls whether the outline is drawn for
     * each pie section, and sends a {@link PlotChangeEvent} to all registered
     * listeners.
     *
     * @param visible  the flag.
     *
     * @see #getSectionOutlinesVisible()
     */
    public void setSectionOutlinesVisible(boolean visible) {
        this.sectionOutlinesVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the outline paint for the specified section.  This is equivalent
     * to {@code lookupSectionPaint(section,
     * getAutoPopulateSectionOutlinePaint())}.
     *
     * @param key  the section key.
     *
     * @return The paint for the specified section.
     *
     * @see #lookupSectionOutlinePaint(K, boolean)
     */
    protected Paint lookupSectionOutlinePaint(K key) {
        return lookupSectionOutlinePaint(key, getAutoPopulateSectionOutlinePaint());
    }

    /**
     * Returns the outline paint for the specified section.  The lookup
     * involves these steps:
     * <ul>
     * <li>if {@link #getSectionOutlinePaint(K)} is non-{@code null} return it;</li>
     * <li>if {@link #getSectionOutlinePaint(K)} is {@code null} but
     *         {@code autoPopulate} is {@code true}, attempt to fetch
     *         a new outline paint from the drawing supplier
     *         ({@link #getDrawingSupplier()});
     * <li>if all else fails, return {@link #getDefaultSectionOutlinePaint()}.
     * </ul>
     *
     * @param key  the section key.
     * @param autoPopulate  a flag that controls whether the drawing supplier
     *     is used to auto-populate the section outline paint settings.
     *
     * @return The paint.
     */
    protected Paint lookupSectionOutlinePaint(K key, boolean autoPopulate) {
        // if not, check if there is a paint defined for the specified key
        Paint result = this.sectionOutlinePaintMap.get(key);
        if (result != null) {
            return result;
        }
        // nothing defined - do we autoPopulate?
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextOutlinePaint();
                this.sectionOutlinePaintMap.put(key, result);
            } else {
                result = this.defaultSectionOutlinePaint;
            }
        } else {
            result = this.defaultSectionOutlinePaint;
        }
        return result;
    }

    /**
     * Returns the outline paint associated with the specified key, or
     * {@code null} if there is no paint associated with the key.
     *
     * @param key  the key ({@code null} not permitted).
     *
     * @return The paint associated with the specified key, or {@code null}.
     *
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     *
     * @see #setSectionOutlinePaint(K, Paint)
     */
    public Paint getSectionOutlinePaint(K key) {
        // null argument check delegated...
        return this.sectionOutlinePaintMap.get(key);
    }

    /**
     * Sets the outline paint associated with the specified key, and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key ({@code null} not permitted).
     * @param paint  the paint.
     *
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     *
     * @see #getSectionOutlinePaint(K)
     */
    public void setSectionOutlinePaint(K key, Paint paint) {
        // null argument check delegated...
        this.sectionOutlinePaintMap.put(key, paint);
        fireChangeEvent();
    }

    /**
     * Clears the section outline paint settings for this plot and, if
     * requested, sends a {@link PlotChangeEvent} to all registered listeners.
     * Be aware that if the {@code autoPopulateSectionPaint} flag is set,
     * the section paints may be repopulated using the same colours as before.
     *
     * @param notify  notify listeners?
     *
     * @see #autoPopulateSectionOutlinePaint
     */
    public void clearSectionOutlinePaints(boolean notify) {
        this.sectionOutlinePaintMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default section paint.  This is used when no other paint is
     * available.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultSectionOutlinePaint(Paint)
     */
    public Paint getDefaultSectionOutlinePaint() {
        return this.defaultSectionOutlinePaint;
    }

    /**
     * Sets the default section paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultSectionOutlinePaint()
     */
    public void setDefaultSectionOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultSectionOutlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the section outline paint
     * is auto-populated by the {@link #lookupSectionOutlinePaint(K)}
     * method.
     *
     * @return A boolean.
     */
    public boolean getAutoPopulateSectionOutlinePaint() {
        return this.autoPopulateSectionOutlinePaint;
    }

    /**
     * Sets the flag that controls whether the section outline paint is
     * auto-populated by the {@link #lookupSectionOutlinePaint(K)}
     * method, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param auto  auto-populate?
     */
    public void setAutoPopulateSectionOutlinePaint(boolean auto) {
        this.autoPopulateSectionOutlinePaint = auto;
        fireChangeEvent();
    }

    //// SECTION OUTLINE STROKE ///////////////////////////////////////////////
    /**
     * Returns the outline stroke for the specified section.  This is
     * equivalent to {@code lookupSectionOutlineStroke(section,
     * getAutoPopulateSectionOutlineStroke())}.
     *
     * @param key  the section key.
     *
     * @return The stroke for the specified section.
     *
     * @see #lookupSectionOutlineStroke(K, boolean)
     */
    protected Stroke lookupSectionOutlineStroke(K key) {
        return lookupSectionOutlineStroke(key, getAutoPopulateSectionOutlineStroke());
    }

    /**
     * Returns the outline stroke for the specified section.  The lookup
     * involves these steps:
     * <ul>
     * <li>if {@link #getSectionOutlineStroke(K)} is non-{@code null} return it;</li>
     * <li>if {@link #getSectionOutlineStroke(K)} is {@code null} but
     *         {@code autoPopulate} is {@code true}, attempt to fetch
     *         a new outline stroke from the drawing supplier
     *         ({@link #getDrawingSupplier()});
     * <li>if all else fails, return {@link #getDefaultSectionOutlineStroke()}.
     * </ul>
     *
     * @param key  the section key.
     * @param autoPopulate  a flag that controls whether the drawing supplier
     *     is used to auto-populate the section outline stroke settings.
     *
     * @return The stroke.
     */
    protected Stroke lookupSectionOutlineStroke(K key, boolean autoPopulate) {
        // if not, check if there is a stroke defined for the specified key
        Stroke result = this.sectionOutlineStrokeMap.get(key);
        if (result != null) {
            return result;
        }
        // nothing defined - do we autoPopulate?
        if (autoPopulate) {
            DrawingSupplier ds = getDrawingSupplier();
            if (ds != null) {
                result = ds.getNextOutlineStroke();
                this.sectionOutlineStrokeMap.put(key, result);
            } else {
                result = this.defaultSectionOutlineStroke;
            }
        } else {
            result = this.defaultSectionOutlineStroke;
        }
        return result;
    }

    /**
     * Returns the outline stroke associated with the specified key, or
     * {@code null} if there is no stroke associated with the key.
     *
     * @param key  the key ({@code null} not permitted).
     *
     * @return The stroke associated with the specified key, or {@code null}.
     *
     * @throws IllegalArgumentException if {@code key} is
     *     {@code null}.
     *
     * @see #setSectionOutlineStroke(K, Stroke)
     */
    public Stroke getSectionOutlineStroke(K key) {
        // null argument check delegated...
        return this.sectionOutlineStrokeMap.get(key);
    }

    /**
     * Sets the outline stroke associated with the specified key, and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key ({@code null} not permitted).
     * @param stroke  the stroke.
     *
     * @throws IllegalArgumentException if {@code key} is
     *     {@code null}.
     *
     * @see #getSectionOutlineStroke(K)
     */
    public void setSectionOutlineStroke(K key, Stroke stroke) {
        // null argument check delegated...
        this.sectionOutlineStrokeMap.put(key, stroke);
        fireChangeEvent();
    }

    /**
     * Clears the section outline stroke settings for this plot and, if
     * requested, sends a {@link PlotChangeEvent} to all registered listeners.
     * Be aware that if the {@code autoPopulateSectionPaint} flag is set,
     * the section paints may be repopulated using the same colours as before.
     *
     * @param notify  notify listeners?
     *
     * @see #autoPopulateSectionOutlineStroke
     */
    public void clearSectionOutlineStrokes(boolean notify) {
        this.sectionOutlineStrokeMap.clear();
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default section stroke.  This is used when no other stroke is
     * available.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDefaultSectionOutlineStroke(Stroke)
     */
    public Stroke getDefaultSectionOutlineStroke() {
        return this.defaultSectionOutlineStroke;
    }

    /**
     * Sets the default section stroke.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultSectionOutlineStroke()
     */
    public void setDefaultSectionOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.defaultSectionOutlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the section outline stroke
     * is auto-populated by the {@link #lookupSectionOutlinePaint(K)}
     * method.
     *
     * @return A boolean.
     */
    public boolean getAutoPopulateSectionOutlineStroke() {
        return this.autoPopulateSectionOutlineStroke;
    }

    /**
     * Sets the flag that controls whether the section outline stroke is
     * auto-populated by the {@link #lookupSectionOutlineStroke(K)}
     * method, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param auto  auto-populate?
     */
    public void setAutoPopulateSectionOutlineStroke(boolean auto) {
        this.autoPopulateSectionOutlineStroke = auto;
        fireChangeEvent();
    }

    /**
     * Returns the shadow paint.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setShadowPaint(Paint)
     */
    public Paint getShadowPaint() {
        return this.shadowPaint;
    }

    /**
     * Sets the shadow paint and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getShadowPaint()
     */
    public void setShadowPaint(Paint paint) {
        this.shadowPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the x-offset for the shadow effect.
     *
     * @return The offset (in Java2D units).
     *
     * @see #setShadowXOffset(double)
     */
    public double getShadowXOffset() {
        return this.shadowXOffset;
    }

    /**
     * Sets the x-offset for the shadow effect and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param offset  the offset (in Java2D units).
     *
     * @see #getShadowXOffset()
     */
    public void setShadowXOffset(double offset) {
        this.shadowXOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the y-offset for the shadow effect.
     *
     * @return The offset (in Java2D units).
     *
     * @see #setShadowYOffset(double)
     */
    public double getShadowYOffset() {
        return this.shadowYOffset;
    }

    /**
     * Sets the y-offset for the shadow effect and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param offset  the offset (in Java2D units).
     *
     * @see #getShadowYOffset()
     */
    public void setShadowYOffset(double offset) {
        this.shadowYOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the amount that the section with the specified key should be
     * exploded.
     *
     * @param key  the key ({@code null} not permitted).
     *
     * @return The amount that the section with the specified key should be
     *     exploded.
     *
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     *
     * @see #setExplodePercent(K, double)
     */
    public double getExplodePercent(K key) {
        double result = 0.0;
        if (this.explodePercentages != null) {
            Number percent = (Number) this.explodePercentages.get(key);
            if (percent != null) {
                result = percent.doubleValue();
            }
        }
        return result;
    }

    /**
     * Sets the amount that a pie section should be exploded and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the section key ({@code null} not permitted).
     * @param percent  the explode percentage (0.30 = 30 percent).
     *
     * @see #getExplodePercent(K)
     */
    public void setExplodePercent(K key, double percent) {
        Args.nullNotPermitted(key, "key");
        if (this.explodePercentages == null) {
            this.explodePercentages = new TreeMap<>();
        }
        this.explodePercentages.put(key, percent);
        fireChangeEvent();
    }

    /**
     * Returns the maximum explode percent.
     *
     * @return The percent.
     */
    public double getMaximumExplodePercent() {
        if (this.dataset == null) {
            return 0.0;
        }
        double result = 0.0;
        for (K key : this.dataset.getKeys()) {
            Double explode = this.explodePercentages.get(key);
            if (explode != null) {
                result = Math.max(result, explode);
            }
        }
        return result;
    }

    /**
     * Returns the section label generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setLabelGenerator(PieSectionLabelGenerator)
     */
    public PieSectionLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    /**
     * Sets the section label generator and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getLabelGenerator()
     */
    public void setLabelGenerator(PieSectionLabelGenerator generator) {
        this.labelGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the gap between the edge of the pie and the labels, expressed as
     * a percentage of the plot width.
     *
     * @return The gap (a percentage, where 0.05 = five percent).
     *
     * @see #setLabelGap(double)
     */
    public double getLabelGap() {
        return this.labelGap;
    }

    /**
     * Sets the gap between the edge of the pie and the labels (expressed as a
     * percentage of the plot width) and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param gap  the gap (a percentage, where 0.05 = five percent).
     *
     * @see #getLabelGap()
     */
    public void setLabelGap(double gap) {
        this.labelGap = gap;
        fireChangeEvent();
    }

    /**
     * Returns the maximum label width as a percentage of the plot width.
     *
     * @return The width (a percentage, where 0.20 = 20 percent).
     *
     * @see #setMaximumLabelWidth(double)
     */
    public double getMaximumLabelWidth() {
        return this.maximumLabelWidth;
    }

    /**
     * Sets the maximum label width as a percentage of the plot width and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param width  the width (a percentage, where 0.20 = 20 percent).
     *
     * @see #getMaximumLabelWidth()
     */
    public void setMaximumLabelWidth(double width) {
        this.maximumLabelWidth = width;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether label linking lines are
     * visible.
     *
     * @return A boolean.
     *
     * @see #setLabelLinksVisible(boolean)
     */
    public boolean getLabelLinksVisible() {
        return this.labelLinksVisible;
    }

    /**
     * Sets the flag that controls whether label linking lines are
     * visible and sends a {@link PlotChangeEvent} to all registered listeners.
     * Please take care when hiding the linking lines - depending on the data
     * values, the labels can be displayed some distance away from the
     * corresponding pie section.
     *
     * @param visible  the flag.
     *
     * @see #getLabelLinksVisible()
     */
    public void setLabelLinksVisible(boolean visible) {
        this.labelLinksVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the label link style.
     *
     * @return The label link style (never {@code null}).
     *
     * @see #setLabelLinkStyle(PieLabelLinkStyle)
     */
    public PieLabelLinkStyle getLabelLinkStyle() {
        return this.labelLinkStyle;
    }

    /**
     * Sets the label link style and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param style  the new style ({@code null} not permitted).
     *
     * @see #getLabelLinkStyle()
     */
    public void setLabelLinkStyle(PieLabelLinkStyle style) {
        Args.nullNotPermitted(style, "style");
        this.labelLinkStyle = style;
        fireChangeEvent();
    }

    /**
     * Returns the margin (expressed as a percentage of the width or height)
     * between the edge of the pie and the link point.
     *
     * @return The link margin (as a percentage, where 0.05 is five percent).
     *
     * @see #setLabelLinkMargin(double)
     */
    public double getLabelLinkMargin() {
        return this.labelLinkMargin;
    }

    /**
     * Sets the link margin and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param margin  the margin.
     *
     * @see #getLabelLinkMargin()
     */
    public void setLabelLinkMargin(double margin) {
        this.labelLinkMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the paint used for the lines that connect pie sections to their
     * corresponding labels.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setLabelLinkPaint(Paint)
     */
    public Paint getLabelLinkPaint() {
        return this.labelLinkPaint;
    }

    /**
     * Sets the paint used for the lines that connect pie sections to their
     * corresponding labels, and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelLinkPaint()
     */
    public void setLabelLinkPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.labelLinkPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used for the label linking lines.
     *
     * @return The stroke.
     *
     * @see #setLabelLinkStroke(Stroke)
     */
    public Stroke getLabelLinkStroke() {
        return this.labelLinkStroke;
    }

    /**
     * Sets the link stroke and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param stroke  the stroke.
     *
     * @see #getLabelLinkStroke()
     */
    public void setLabelLinkStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.labelLinkStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the distance that the end of the label link is embedded into
     * the plot, expressed as a percentage of the plot's radius.
     * <br><br>
     * This method is overridden in the {@link RingPlot} class to resolve
     * bug 2121818.
     *
     * @return {@code 0.10}.
     */
    protected double getLabelLinkDepth() {
        return 0.1;
    }

    /**
     * Returns the section label font.
     *
     * @return The font (never {@code null}).
     *
     * @see #setLabelFont(Font)
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the section label font and sends a {@link PlotChangeEvent} to all
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
     * Returns the section label paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setLabelPaint(Paint)
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the section label paint and sends a {@link PlotChangeEvent} to all
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
     * Returns the section label background paint.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setLabelBackgroundPaint(Paint)
     */
    public Paint getLabelBackgroundPaint() {
        return this.labelBackgroundPaint;
    }

    /**
     * Sets the section label background paint and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getLabelBackgroundPaint()
     */
    public void setLabelBackgroundPaint(Paint paint) {
        this.labelBackgroundPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the section label outline paint.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setLabelOutlinePaint(Paint)
     */
    public Paint getLabelOutlinePaint() {
        return this.labelOutlinePaint;
    }

    /**
     * Sets the section label outline paint and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getLabelOutlinePaint()
     */
    public void setLabelOutlinePaint(Paint paint) {
        this.labelOutlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the section label outline stroke.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setLabelOutlineStroke(Stroke)
     */
    public Stroke getLabelOutlineStroke() {
        return this.labelOutlineStroke;
    }

    /**
     * Sets the section label outline stroke and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getLabelOutlineStroke()
     */
    public void setLabelOutlineStroke(Stroke stroke) {
        this.labelOutlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the section label shadow paint.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setLabelShadowPaint(Paint)
     */
    public Paint getLabelShadowPaint() {
        return this.labelShadowPaint;
    }

    /**
     * Sets the section label shadow paint and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getLabelShadowPaint()
     */
    public void setLabelShadowPaint(Paint paint) {
        this.labelShadowPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the label padding.
     *
     * @return The label padding (never {@code null}).
     *
     * @see #setLabelPadding(RectangleInsets)
     */
    public RectangleInsets getLabelPadding() {
        return this.labelPadding;
    }

    /**
     * Sets the padding between each label and its outline and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param padding  the padding ({@code null} not permitted).
     *
     * @see #getLabelPadding()
     */
    public void setLabelPadding(RectangleInsets padding) {
        Args.nullNotPermitted(padding, "padding");
        this.labelPadding = padding;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether simple or extended labels are
     * displayed on the plot.
     *
     * @return A boolean.
     */
    public boolean getSimpleLabels() {
        return this.simpleLabels;
    }

    /**
     * Sets the flag that controls whether simple or extended labels are
     * displayed on the plot, and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param simple  the new flag value.
     */
    public void setSimpleLabels(boolean simple) {
        this.simpleLabels = simple;
        fireChangeEvent();
    }

    /**
     * Returns the offset used for the simple labels, if they are displayed.
     *
     * @return The offset (never {@code null}).
     *
     * @see #setSimpleLabelOffset(RectangleInsets)
     */
    public RectangleInsets getSimpleLabelOffset() {
        return this.simpleLabelOffset;
    }

    /**
     * Sets the offset for the simple labels and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param offset  the offset ({@code null} not permitted).
     *
     * @see #getSimpleLabelOffset()
     */
    public void setSimpleLabelOffset(RectangleInsets offset) {
        Args.nullNotPermitted(offset, "offset");
        this.simpleLabelOffset = offset;
        fireChangeEvent();
    }

    /**
     * Returns the object responsible for the vertical layout of the pie
     * section labels.
     *
     * @return The label distributor (never {@code null}).
     */
    public AbstractPieLabelDistributor getLabelDistributor() {
        return this.labelDistributor;
    }

    /**
     * Sets the label distributor and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param distributor  the distributor ({@code null} not permitted).
     */
    public void setLabelDistributor(AbstractPieLabelDistributor distributor) {
        Args.nullNotPermitted(distributor, "distributor");
        this.labelDistributor = distributor;
        fireChangeEvent();
    }

    /**
     * Returns the tool tip generator, an object that is responsible for
     * generating the text items used for tool tips by the plot.  If the
     * generator is {@code null}, no tool tips will be created.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setToolTipGenerator(PieToolTipGenerator)
     */
    public PieToolTipGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator and sends a {@link PlotChangeEvent} to all
     * registered listeners.  Set the generator to {@code null} if you
     * don't want any tool tips.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getToolTipGenerator()
     */
    public void setToolTipGenerator(PieToolTipGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the URL generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setURLGenerator(PieURLGenerator)
     */
    public PieURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getURLGenerator()
     */
    public void setURLGenerator(PieURLGenerator generator) {
        this.urlGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the minimum arc angle that will be drawn.  Pie sections for an
     * angle smaller than this are not drawn, to avoid a JDK bug.
     *
     * @return The minimum angle.
     *
     * @see #setMinimumArcAngleToDraw(double)
     */
    public double getMinimumArcAngleToDraw() {
        return this.minimumArcAngleToDraw;
    }

    /**
     * Sets the minimum arc angle that will be drawn.  Pie sections for an
     * angle smaller than this are not drawn, to avoid a JDK bug.  See this
     * link for details:
     * <br><br>
     * <a href="https://www.jfree.org/phpBB2/viewtopic.php?t=2707">
     * https://www.jfree.org/phpBB2/viewtopic.php?t=2707</a>
     * <br><br>
     * ...and this bug report in the Java Bug Parade:
     * <br><br>
     * <a href=
     * "http://developer.java.sun.com/developer/bugParade/bugs/4836495.html">
     * http://developer.java.sun.com/developer/bugParade/bugs/4836495.html</a>
     *
     * @param angle  the minimum angle.
     *
     * @see #getMinimumArcAngleToDraw()
     */
    public void setMinimumArcAngleToDraw(double angle) {
        this.minimumArcAngleToDraw = angle;
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
     * Returns the legend label generator.
     *
     * @return The legend label generator (never {@code null}).
     *
     * @see #setLegendLabelGenerator(PieSectionLabelGenerator)
     */
    public PieSectionLabelGenerator getLegendLabelGenerator() {
        return this.legendLabelGenerator;
    }

    /**
     * Sets the legend label generator and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param generator  the generator ({@code null} not permitted).
     *
     * @see #getLegendLabelGenerator()
     */
    public void setLegendLabelGenerator(PieSectionLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        this.legendLabelGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend label tool tip generator.
     *
     * @return The legend label tool tip generator (possibly {@code null}).
     *
     * @see #setLegendLabelToolTipGenerator(PieSectionLabelGenerator)
     */
    public PieSectionLabelGenerator<K> getLegendLabelToolTipGenerator() {
        return this.legendLabelToolTipGenerator;
    }

    /**
     * Sets the legend label tool tip generator and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getLegendLabelToolTipGenerator()
     */
    public void setLegendLabelToolTipGenerator(PieSectionLabelGenerator generator) {
        this.legendLabelToolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend label URL generator.
     *
     * @return The legend label URL generator (possibly {@code null}).
     *
     * @see #setLegendLabelURLGenerator(PieURLGenerator)
     */
    public PieURLGenerator getLegendLabelURLGenerator() {
        return this.legendLabelURLGenerator;
    }

    /**
     * Sets the legend label URL generator and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getLegendLabelURLGenerator()
     */
    public void setLegendLabelURLGenerator(PieURLGenerator generator) {
        this.legendLabelURLGenerator = generator;
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
     * {@link PlotChangeEvent} to all registered listeners.  Note that this is
     * a bitmap drop-shadow generation facility and is separate from the
     * vector based show option that is controlled via the
     * {@link #setShadowPaint(java.awt.Paint)} method.
     *
     * @param generator  the generator ({@code null} permitted).
     */
    public void setShadowGenerator(ShadowGenerator generator) {
        this.shadowGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Handles a mouse wheel rotation (this method is intended for use by the
     * {@code MouseWheelHandler} class).
     *
     * @param rotateClicks  the number of rotate clicks on the the mouse wheel.
     */
    public void handleMouseWheelRotation(int rotateClicks) {
        setStartAngle(this.startAngle + rotateClicks * 4.0);
    }

    /**
     * Initialises the drawing procedure.  This method will be called before
     * the first item is rendered, giving the plot an opportunity to initialise
     * any state information it wants to maintain.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area ({@code null} not permitted).
     * @param plot  the plot.
     * @param index  the secondary index ({@code null} for primary
     *               renderer).
     * @param info  collects chart rendering information for return to caller.
     *
     * @return A state object (maintains state information relevant to one
     *         chart drawing).
     */
    public PiePlotState initialise(Graphics2D g2, Rectangle2D plotArea, PiePlot<?> plot, Integer index, PlotRenderingInfo info) {
        PiePlotState state = new PiePlotState(info);
        state.setPassesRequired(2);
        if (this.dataset != null) {
            state.setTotal(DatasetUtils.calculatePieDatasetTotal(plot.getDataset()));
        }
        state.setLatestAngle(plot.getStartAngle());
        return state;
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param info  collects info about the drawing
     *              ({@code null} permitted).
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
            Graphics2D savedG2 = g2;
            boolean suppressShadow = Boolean.TRUE.equals(g2.getRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION));
            BufferedImage dataImage = null;
            if (this.shadowGenerator != null && !suppressShadow) {
                dataImage = new BufferedImage((int) area.getWidth(), (int) area.getHeight(), BufferedImage.TYPE_INT_ARGB);
                g2 = dataImage.createGraphics();
                g2.translate(-area.getX(), -area.getY());
                g2.setRenderingHints(savedG2.getRenderingHints());
            }
            drawPie(g2, area, info);
            if (this.shadowGenerator != null && !suppressShadow) {
                BufferedImage shadowImage = this.shadowGenerator.createDropShadow(dataImage);
                g2 = savedG2;
                g2.drawImage(shadowImage, (int) area.getX() + this.shadowGenerator.calculateOffsetX(), (int) area.getY() + this.shadowGenerator.calculateOffsetY(), null);
                g2.drawImage(dataImage, (int) area.getX(), (int) area.getY(), null);
            }
        } else {
            drawNoDataMessage(g2, area);
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, area);
    }

    /**
     * Draws the pie.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param info  chart rendering info.
     */
    protected void drawPie(Graphics2D g2, Rectangle2D plotArea, PlotRenderingInfo info) {
        PiePlotState state = initialise(g2, plotArea, this, null, info);
        // adjust the plot area for interior spacing and labels...
        double labelReserve = 0.0;
        if (this.labelGenerator != null && !this.simpleLabels) {
            labelReserve = this.labelGap + this.maximumLabelWidth;
        }
        double gapHorizontal = plotArea.getWidth() * labelReserve * 2.0;
        double gapVertical = plotArea.getHeight() * this.interiorGap * 2.0;
        if (DEBUG_DRAW_INTERIOR) {
            double hGap = plotArea.getWidth() * this.interiorGap;
            double vGap = plotArea.getHeight() * this.interiorGap;
            double igx1 = plotArea.getX() + hGap;
            double igx2 = plotArea.getMaxX() - hGap;
            double igy1 = plotArea.getY() + vGap;
            double igy2 = plotArea.getMaxY() - vGap;
            g2.setPaint(Color.GRAY);
            g2.draw(new Rectangle2D.Double(igx1, igy1, igx2 - igx1, igy2 - igy1));
        }
        double linkX = plotArea.getX() + gapHorizontal / 2;
        double linkY = plotArea.getY() + gapVertical / 2;
        double linkW = plotArea.getWidth() - gapHorizontal;
        double linkH = plotArea.getHeight() - gapVertical;
        // make the link area a square if the pie chart is to be circular...
        if (this.circular) {
            double min = Math.min(linkW, linkH) / 2;
            linkX = (linkX + linkX + linkW) / 2 - min;
            linkY = (linkY + linkY + linkH) / 2 - min;
            linkW = 2 * min;
            linkH = 2 * min;
        }
        // the link area defines the dog leg points for the linking lines to
        // the labels
        Rectangle2D linkArea = new Rectangle2D.Double(linkX, linkY, linkW, linkH);
        state.setLinkArea(linkArea);
        if (DEBUG_DRAW_LINK_AREA) {
            g2.setPaint(Color.BLUE);
            g2.draw(linkArea);
            g2.setPaint(Color.YELLOW);
            g2.draw(new Ellipse2D.Double(linkArea.getX(), linkArea.getY(), linkArea.getWidth(), linkArea.getHeight()));
        }
        // the explode area defines the max circle/ellipse for the exploded
        // pie sections.  it is defined by shrinking the linkArea by the
        // linkMargin factor.
        double lm = 0.0;
        if (!this.simpleLabels) {
            lm = this.labelLinkMargin;
        }
        double hh = linkArea.getWidth() * lm * 2.0;
        double vv = linkArea.getHeight() * lm * 2.0;
        Rectangle2D explodeArea = new Rectangle2D.Double(linkX + hh / 2.0, linkY + vv / 2.0, linkW - hh, linkH - vv);
        state.setExplodedPieArea(explodeArea);
        // the pie area defines the circle/ellipse for regular pie sections.
        // it is defined by shrinking the explodeArea by the explodeMargin
        // factor.
        double maximumExplodePercent = getMaximumExplodePercent();
        double percent = maximumExplodePercent / (1.0 + maximumExplodePercent);
        double h1 = explodeArea.getWidth() * percent;
        double v1 = explodeArea.getHeight() * percent;
        Rectangle2D pieArea = new Rectangle2D.Double(explodeArea.getX() + h1 / 2.0, explodeArea.getY() + v1 / 2.0, explodeArea.getWidth() - h1, explodeArea.getHeight() - v1);
        if (DEBUG_DRAW_PIE_AREA) {
            g2.setPaint(Color.GREEN);
            g2.draw(pieArea);
        }
        state.setPieArea(pieArea);
        state.setPieCenterX(pieArea.getCenterX());
        state.setPieCenterY(pieArea.getCenterY());
        state.setPieWRadius(pieArea.getWidth() / 2.0);
        state.setPieHRadius(pieArea.getHeight() / 2.0);
        // plot the data (unless the dataset is null)...
        if ((this.dataset != null) && (this.dataset.getKeys().size() > 0)) {
            List<K> keys = this.dataset.getKeys();
            double totalValue = DatasetUtils.calculatePieDatasetTotal(this.dataset);
            int passesRequired = state.getPassesRequired();
            for (int pass = 0; pass < passesRequired; pass++) {
                double runningTotal = 0.0;
                for (int section = 0; section < keys.size(); section++) {
                    Number n = this.dataset.getValue(section);
                    if (n != null) {
                        double value = n.doubleValue();
                        if (value > 0.0) {
                            runningTotal += value;
                            drawItem(g2, section, explodeArea, state, pass);
                        }
                    }
                }
            }
            if (this.simpleLabels) {
                drawSimpleLabels(g2, keys, totalValue, plotArea, linkArea, state);
            } else {
                drawLabels(g2, keys, totalValue, plotArea, linkArea, state);
            }
        } else {
            drawNoDataMessage(g2, plotArea);
        }
    }

    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param section  the section index.
     * @param dataArea  the data plot area.
     * @param state  state information for one chart.
     * @param currentPass  the current pass index.
     */
    protected void drawItem(Graphics2D g2, int section, Rectangle2D dataArea, PiePlotState state, int currentPass) {
        Number n = this.dataset.getValue(section);
        if (n == null) {
            return;
        }
        double value = n.doubleValue();
        double angle1 = 0.0;
        double angle2 = 0.0;
        if (this.direction == Rotation.CLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 - value / state.getTotal() * 360.0;
        } else if (this.direction == Rotation.ANTICLOCKWISE) {
            angle1 = state.getLatestAngle();
            angle2 = angle1 + value / state.getTotal() * 360.0;
        } else {
            throw new IllegalStateException("Rotation type not recognised.");
        }
        double angle = (angle2 - angle1);
        if (Math.abs(angle) > getMinimumArcAngleToDraw()) {
            double ep = 0.0;
            double mep = getMaximumExplodePercent();
            if (mep > 0.0) {
                ep = getExplodePercent(dataset.getKey(section)) / mep;
            }
            Rectangle2D arcBounds = getArcBounds(state.getPieArea(), state.getExplodedPieArea(), angle1, angle, ep);
            Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle, Arc2D.PIE);
            if (currentPass == 0) {
                if (this.shadowPaint != null && this.shadowGenerator == null) {
                    Shape shadowArc = ShapeUtils.createTranslatedShape(arc, (float) this.shadowXOffset, (float) this.shadowYOffset);
                    g2.setPaint(this.shadowPaint);
                    g2.fill(shadowArc);
                }
            } else if (currentPass == 1) {
                K key = getSectionKey(section);
                Paint paint = lookupSectionPaint(key, state);
                g2.setPaint(paint);
                g2.fill(arc);
                Paint outlinePaint = lookupSectionOutlinePaint(key);
                Stroke outlineStroke = lookupSectionOutlineStroke(key);
                if (this.sectionOutlinesVisible) {
                    g2.setPaint(outlinePaint);
                    g2.setStroke(outlineStroke);
                    g2.draw(arc);
                }
                // update the linking line target for later
                // add an entity for the pie section
                if (state.getInfo() != null) {
                    EntityCollection entities = state.getEntityCollection();
                    if (entities != null) {
                        String tip = null;
                        if (this.toolTipGenerator != null) {
                            tip = this.toolTipGenerator.generateToolTip(this.dataset, key);
                        }
                        String url = null;
                        if (this.urlGenerator != null) {
                            url = this.urlGenerator.generateURL(this.dataset, key, this.pieIndex);
                        }
                        PieSectionEntity entity = new PieSectionEntity(arc, this.dataset, this.pieIndex, section, key, tip, url);
                        entities.add(entity);
                    }
                }
            }
        }
        state.setLatestAngle(angle2);
    }

    /**
     * Draws the pie section labels in the simple form.
     *
     * @param g2  the graphics device.
     * @param keys  the section keys.
     * @param totalValue  the total value for all sections in the pie.
     * @param plotArea  the plot area.
     * @param pieArea  the area containing the pie.
     * @param state  the plot state.
     */
    protected void drawSimpleLabels(Graphics2D g2, List<K> keys, double totalValue, Rectangle2D plotArea, Rectangle2D pieArea, PiePlotState state) {
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        Rectangle2D labelsArea = this.simpleLabelOffset.createInsetRectangle(pieArea);
        double runningTotal = 0.0;
        for (K key : keys) {
            boolean include;
            double v = 0.0;
            Number n = getDataset().getValue(key);
            if (n == null) {
                include = !getIgnoreNullValues();
            } else {
                v = n.doubleValue();
                include = getIgnoreZeroValues() ? v > 0.0 : v >= 0.0;
            }
            if (include) {
                runningTotal = runningTotal + v;
                // work out the mid angle (0 - 90 and 270 - 360) = right,
                // otherwise left
                double mid = getStartAngle() + (getDirection().getFactor() * ((runningTotal - v / 2.0) * 360) / totalValue);
                Arc2D arc = new Arc2D.Double(labelsArea, getStartAngle(), mid - getStartAngle(), Arc2D.OPEN);
                int x = (int) arc.getEndPoint().getX();
                int y = (int) arc.getEndPoint().getY();
                PieSectionLabelGenerator myLabelGenerator = getLabelGenerator();
                if (myLabelGenerator == null) {
                    continue;
                }
                String label = myLabelGenerator.generateSectionLabel(this.dataset, key);
                if (label == null) {
                    continue;
                }
                g2.setFont(this.labelFont);
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D bounds = TextUtils.getTextBounds(label, g2, fm);
                Rectangle2D out = this.labelPadding.createOutsetRectangle(bounds);
                Shape bg = ShapeUtils.createTranslatedShape(out, x - bounds.getCenterX(), y - bounds.getCenterY());
                if (this.labelShadowPaint != null && this.shadowGenerator == null) {
                    Shape shadow = ShapeUtils.createTranslatedShape(bg, this.shadowXOffset, this.shadowYOffset);
                    g2.setPaint(this.labelShadowPaint);
                    g2.fill(shadow);
                }
                if (this.labelBackgroundPaint != null) {
                    g2.setPaint(this.labelBackgroundPaint);
                    g2.fill(bg);
                }
                if (this.labelOutlinePaint != null && this.labelOutlineStroke != null) {
                    g2.setPaint(this.labelOutlinePaint);
                    g2.setStroke(this.labelOutlineStroke);
                    g2.draw(bg);
                }
                g2.setPaint(this.labelPaint);
                g2.setFont(this.labelFont);
                TextUtils.drawAlignedString(label, g2, x, y, TextAnchor.CENTER);
            }
        }
        g2.setComposite(originalComposite);
    }

    /**
     * Draws the labels for the pie sections.
     *
     * @param g2  the graphics device.
     * @param keys  the keys.
     * @param totalValue  the total value.
     * @param plotArea  the plot area.
     * @param linkArea  the link area.
     * @param state  the state.
     */
    protected void drawLabels(Graphics2D g2, List<K> keys, double totalValue, Rectangle2D plotArea, Rectangle2D linkArea, PiePlotState state) {
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        // classify the keys according to which side the label will appear...
        DefaultKeyedValues leftKeys = new DefaultKeyedValues();
        DefaultKeyedValues rightKeys = new DefaultKeyedValues();
        double runningTotal = 0.0;
        for (K key : keys) {
            boolean include;
            double v = 0.0;
            Number n = this.dataset.getValue(key);
            if (n == null) {
                include = !this.ignoreNullValues;
            } else {
                v = n.doubleValue();
                include = this.ignoreZeroValues ? v > 0.0 : v >= 0.0;
            }
            if (include) {
                runningTotal = runningTotal + v;
                // work out the mid angle (0 - 90 and 270 - 360) = right,
                // otherwise left
                double mid = this.startAngle + (this.direction.getFactor() * ((runningTotal - v / 2.0) * 360) / totalValue);
                if (Math.cos(Math.toRadians(mid)) < 0.0) {
                    leftKeys.addValue(key, mid);
                } else {
                    rightKeys.addValue(key, mid);
                }
            }
        }
        g2.setFont(getLabelFont());
        // calculate the max label width from the plot dimensions, because
        // a circular pie can leave a lot more room for labels...
        double marginX = plotArea.getX();
        double gap = plotArea.getWidth() * this.labelGap;
        double ww = linkArea.getX() - gap - marginX;
        float labelWidth = (float) this.labelPadding.trimWidth(ww);
        // draw the labels...
        if (this.labelGenerator != null) {
            drawLeftLabels(leftKeys, g2, plotArea, linkArea, labelWidth, state);
            drawRightLabels(rightKeys, g2, plotArea, linkArea, labelWidth, state);
        }
        g2.setComposite(originalComposite);
    }

    /**
     * Draws the left labels.
     *
     * @param leftKeys  a collection of keys and angles (to the middle of the
     *         section, in degrees) for the sections on the left side of the
     *         plot.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param linkArea  the link area.
     * @param maxLabelWidth  the maximum label width.
     * @param state  the state.
     */
    protected void drawLeftLabels(KeyedValues<K> leftKeys, Graphics2D g2, Rectangle2D plotArea, Rectangle2D linkArea, float maxLabelWidth, PiePlotState state) {
        this.labelDistributor.clear();
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / 2.0;
        for (int i = 0; i < leftKeys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(this.dataset, leftKeys.getKey(i));
            if (label != null) {
                TextBlock block = TextUtils.createTextBlock(label, this.labelFont, this.labelPaint, maxLabelWidth, new G2TextMeasurer(g2));
                TextBox labelBox = new TextBox(block);
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                if (this.shadowGenerator == null) {
                    labelBox.setShadowPaint(this.labelShadowPaint);
                } else {
                    labelBox.setShadowPaint(null);
                }
                labelBox.setInteriorGap(this.labelPadding);
                double theta = Math.toRadians(leftKeys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY() - Math.sin(theta) * verticalLinkRadius;
                double hh = labelBox.getHeight(g2);
                this.labelDistributor.addPieLabelRecord(new PieLabelRecord(leftKeys.getKey(i), theta, baseY, labelBox, hh, lGap / 2.0 + lGap / 2.0 * -Math.cos(theta), 1.0 - getLabelLinkDepth() + getExplodePercent(leftKeys.getKey(i))));
            }
        }
        double hh = plotArea.getHeight();
        double gap = hh * getInteriorGap();
        this.labelDistributor.distributeLabels(plotArea.getMinY() + gap, hh - 2 * gap);
        for (int i = 0; i < this.labelDistributor.getItemCount(); i++) {
            drawLeftLabel(g2, state, this.labelDistributor.getPieLabelRecord(i));
        }
    }

    /**
     * Draws the right labels.
     *
     * @param keys  the keys.
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param linkArea  the link area.
     * @param maxLabelWidth  the maximum label width.
     * @param state  the state.
     */
    protected void drawRightLabels(KeyedValues<K> keys, Graphics2D g2, Rectangle2D plotArea, Rectangle2D linkArea, float maxLabelWidth, PiePlotState state) {
        // draw the right labels...
        this.labelDistributor.clear();
        double lGap = plotArea.getWidth() * this.labelGap;
        double verticalLinkRadius = state.getLinkArea().getHeight() / 2.0;
        for (int i = 0; i < keys.getItemCount(); i++) {
            String label = this.labelGenerator.generateSectionLabel(this.dataset, keys.getKey(i));
            if (label != null) {
                TextBlock block = TextUtils.createTextBlock(label, this.labelFont, this.labelPaint, maxLabelWidth, new G2TextMeasurer(g2));
                TextBox labelBox = new TextBox(block);
                labelBox.setBackgroundPaint(this.labelBackgroundPaint);
                labelBox.setOutlinePaint(this.labelOutlinePaint);
                labelBox.setOutlineStroke(this.labelOutlineStroke);
                if (this.shadowGenerator == null) {
                    labelBox.setShadowPaint(this.labelShadowPaint);
                } else {
                    labelBox.setShadowPaint(null);
                }
                labelBox.setInteriorGap(this.labelPadding);
                double theta = Math.toRadians(keys.getValue(i).doubleValue());
                double baseY = state.getPieCenterY() - Math.sin(theta) * verticalLinkRadius;
                double hh = labelBox.getHeight(g2);
                this.labelDistributor.addPieLabelRecord(new PieLabelRecord(keys.getKey(i), theta, baseY, labelBox, hh, lGap / 2.0 + lGap / 2.0 * Math.cos(theta), 1.0 - getLabelLinkDepth() + getExplodePercent(keys.getKey(i))));
            }
        }
        double hh = plotArea.getHeight();
        //hh * getInteriorGap();
        double gap = 0.00;
        this.labelDistributor.distributeLabels(plotArea.getMinY() + gap, hh - 2 * gap);
        for (int i = 0; i < this.labelDistributor.getItemCount(); i++) {
            drawRightLabel(g2, state, this.labelDistributor.getPieLabelRecord(i));
        }
    }

    /**
     * Returns a collection of legend items for the pie chart.
     *
     * @return The legend items (never {@code null}).
     */
    @Override
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.dataset == null) {
            return result;
        }
        List<K> keys = this.dataset.getKeys();
        int section = 0;
        Shape shape = getLegendItemShape();
        for (K key : keys) {
            Number n = this.dataset.getValue(key);
            boolean include;
            if (n == null) {
                include = !this.ignoreNullValues;
            } else {
                double v = n.doubleValue();
                if (v == 0.0) {
                    include = !this.ignoreZeroValues;
                } else {
                    include = v > 0.0;
                }
            }
            if (include) {
                String label = this.legendLabelGenerator.generateSectionLabel(this.dataset, key);
                if (label != null) {
                    String description = label;
                    String toolTipText = null;
                    if (this.legendLabelToolTipGenerator != null) {
                        toolTipText = this.legendLabelToolTipGenerator.generateSectionLabel(this.dataset, key);
                    }
                    String urlText = null;
                    if (this.legendLabelURLGenerator != null) {
                        urlText = this.legendLabelURLGenerator.generateURL(this.dataset, key, this.pieIndex);
                    }
                    Paint paint = lookupSectionPaint(key);
                    Paint outlinePaint = lookupSectionOutlinePaint(key);
                    Stroke outlineStroke = lookupSectionOutlineStroke(key);
                    LegendItem item = new // line not visible
                    LegendItem(// line not visible
                    label, // line not visible
                    description, // line not visible
                    toolTipText, // line not visible
                    urlText, // line not visible
                    true, // line not visible
                    shape, // line not visible
                    true, // line not visible
                    paint, // line not visible
                    true, // line not visible
                    outlinePaint, // line not visible
                    outlineStroke, false, new Line2D.Float(), new BasicStroke(), Color.BLACK);
                    item.setDataset(getDataset());
                    item.setSeriesIndex(this.dataset.getIndex(key));
                    item.setSeriesKey(key);
                    result.add(item);
                }
                section++;
            } else {
                section++;
            }
        }
        return result;
    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return The plot type.
     */
    @Override
    public String getPlotType() {
        return localizationResources.getString("Pie_Plot");
    }

    /**
     * Returns a rectangle that can be used to create a pie section (taking
     * into account the amount by which the pie section is 'exploded').
     *
     * @param unexploded  the area inside which the unexploded pie sections are
     *                    drawn.
     * @param exploded  the area inside which the exploded pie sections are
     *                  drawn.
     * @param angle  the start angle.
     * @param extent  the extent of the arc.
     * @param explodePercent  the amount by which the pie section is exploded.
     *
     * @return A rectangle that can be used to create a pie section.
     */
    protected Rectangle2D getArcBounds(Rectangle2D unexploded, Rectangle2D exploded, double angle, double extent, double explodePercent) {
        if (explodePercent == 0.0) {
            return unexploded;
        }
        Arc2D arc1 = new Arc2D.Double(unexploded, angle, extent / 2, Arc2D.OPEN);
        Point2D point1 = arc1.getEndPoint();
        Arc2D.Double arc2 = new Arc2D.Double(exploded, angle, extent / 2, Arc2D.OPEN);
        Point2D point2 = arc2.getEndPoint();
        double deltaX = (point1.getX() - point2.getX()) * explodePercent;
        double deltaY = (point1.getY() - point2.getY()) * explodePercent;
        return new Rectangle2D.Double(unexploded.getX() - deltaX, unexploded.getY() - deltaY, unexploded.getWidth(), unexploded.getHeight());
    }

    /**
     * Draws a section label on the left side of the pie chart.
     *
     * @param g2  the graphics device.
     * @param state  the state.
     * @param record  the label record.
     */
    protected void drawLeftLabel(Graphics2D g2, PiePlotState state, PieLabelRecord record) {
        double anchorX = state.getLinkArea().getMinX();
        double targetX = anchorX - record.getGap();
        double targetY = record.getAllocatedY();
        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + Math.cos(theta) * state.getPieWRadius() * record.getLinkPercent();
            double linkY = state.getPieCenterY() - Math.sin(theta) * state.getPieHRadius() * record.getLinkPercent();
            double elbowX = state.getPieCenterX() + Math.cos(theta) * state.getLinkArea().getWidth() / 2.0;
            double elbowY = state.getPieCenterY() - Math.sin(theta) * state.getLinkArea().getHeight() / 2.0;
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            PieLabelLinkStyle style = getLabelLinkStyle();
            if (style.equals(PieLabelLinkStyle.STANDARD)) {
                g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
            } else if (style.equals(PieLabelLinkStyle.QUAD_CURVE)) {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY);
                g2.draw(q);
                g2.draw(new Line2D.Double(elbowX, elbowY, linkX, linkY));
            } else if (style.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
                CubicCurve2D c = new CubicCurve2D.Float();
                c.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY, linkX, linkY);
                g2.draw(c);
            }
        }
        TextBox tb = record.getLabel();
        tb.draw(g2, (float) targetX, (float) targetY, RectangleAnchor.RIGHT);
    }

    /**
     * Draws a section label on the right side of the pie chart.
     *
     * @param g2  the graphics device.
     * @param state  the state.
     * @param record  the label record.
     */
    protected void drawRightLabel(Graphics2D g2, PiePlotState state, PieLabelRecord record) {
        double anchorX = state.getLinkArea().getMaxX();
        double targetX = anchorX + record.getGap();
        double targetY = record.getAllocatedY();
        if (this.labelLinksVisible) {
            double theta = record.getAngle();
            double linkX = state.getPieCenterX() + Math.cos(theta) * state.getPieWRadius() * record.getLinkPercent();
            double linkY = state.getPieCenterY() - Math.sin(theta) * state.getPieHRadius() * record.getLinkPercent();
            double elbowX = state.getPieCenterX() + Math.cos(theta) * state.getLinkArea().getWidth() / 2.0;
            double elbowY = state.getPieCenterY() - Math.sin(theta) * state.getLinkArea().getHeight() / 2.0;
            double anchorY = elbowY;
            g2.setPaint(this.labelLinkPaint);
            g2.setStroke(this.labelLinkStroke);
            PieLabelLinkStyle style = getLabelLinkStyle();
            if (style.equals(PieLabelLinkStyle.STANDARD)) {
                g2.draw(new Line2D.Double(linkX, linkY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, elbowX, elbowY));
                g2.draw(new Line2D.Double(anchorX, anchorY, targetX, targetY));
            } else if (style.equals(PieLabelLinkStyle.QUAD_CURVE)) {
                QuadCurve2D q = new QuadCurve2D.Float();
                q.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY);
                g2.draw(q);
                g2.draw(new Line2D.Double(elbowX, elbowY, linkX, linkY));
            } else if (style.equals(PieLabelLinkStyle.CUBIC_CURVE)) {
                CubicCurve2D c = new CubicCurve2D.Float();
                c.setCurve(targetX, targetY, anchorX, anchorY, elbowX, elbowY, linkX, linkY);
                g2.draw(c);
            }
        }
        TextBox tb = record.getLabel();
        tb.draw(g2, (float) targetX, (float) targetY, RectangleAnchor.LEFT);
    }

    /**
     * Returns the center for the specified section.
     * Checks to see if the section is exploded and recalculates the
     * new center if so.
     *
     * @param state  PiePlotState
     * @param key  section key.
     *
     * @return The center for the specified section.
     */
    protected Point2D getArcCenter(PiePlotState state, K key) {
        Point2D center = new Point2D.Double(state.getPieCenterX(), state.getPieCenterY());
        double ep = getExplodePercent(key);
        double mep = getMaximumExplodePercent();
        if (mep > 0.0) {
            ep = ep / mep;
        }
        if (ep != 0) {
            Rectangle2D pieArea = state.getPieArea();
            Rectangle2D expPieArea = state.getExplodedPieArea();
            double angle1, angle2;
            Number n = this.dataset.getValue(key);
            double value = n.doubleValue();
            if (this.direction == Rotation.CLOCKWISE) {
                angle1 = state.getLatestAngle();
                angle2 = angle1 - value / state.getTotal() * 360.0;
            } else if (this.direction == Rotation.ANTICLOCKWISE) {
                angle1 = state.getLatestAngle();
                angle2 = angle1 + value / state.getTotal() * 360.0;
            } else {
                throw new IllegalStateException("Rotation type not recognised.");
            }
            double angle = (angle2 - angle1);
            Arc2D arc1 = new Arc2D.Double(pieArea, angle1, angle / 2, Arc2D.OPEN);
            Point2D point1 = arc1.getEndPoint();
            Arc2D.Double arc2 = new Arc2D.Double(expPieArea, angle1, angle / 2, Arc2D.OPEN);
            Point2D point2 = arc2.getEndPoint();
            double deltaX = (point1.getX() - point2.getX()) * ep;
            double deltaY = (point1.getY() - point2.getY()) * ep;
            center = new Point2D.Double(state.getPieCenterX() - deltaX, state.getPieCenterY() - deltaY);
        }
        return center;
    }

    /**
     * Returns the paint for the specified section. This is equivalent to
     * {@code lookupSectionPaint(section)}.  Checks to see if the user set the
     * {@code Paint} to be of type {@code RadialGradientPaint} and if so it
     * adjusts the center and radius to match the Pie.
     *
     * @param key  the section key.
     * @param state  PiePlotState.
     *
     * @return The paint for the specified section.
     */
    protected Paint lookupSectionPaint(K key, PiePlotState state) {
        Paint paint = lookupSectionPaint(key, getAutoPopulateSectionPaint());
        // for a RadialGradientPaint we adjust the center and radius to match
        // the current pie segment...
        if (paint instanceof RadialGradientPaint) {
            RadialGradientPaint rgp = (RadialGradientPaint) paint;
            Point2D center = getArcCenter(state, key);
            float radius = (float) Math.max(state.getPieHRadius(), state.getPieWRadius());
            float[] fractions = rgp.getFractions();
            Color[] colors = rgp.getColors();
            paint = new RadialGradientPaint(center, radius, fractions, colors);
        }
        return paint;
    }

    /**
     * Tests this plot for equality with an arbitrary object.  Note that the
     * plot's dataset is NOT included in the test for equality.
     *
     * @param obj  the object to test against ({@code null} permitted).
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        PiePlot that = (PiePlot) obj;
        if (this.pieIndex != that.pieIndex) {
            return false;
        }
        if (this.interiorGap != that.interiorGap) {
            return false;
        }
        if (this.circular != that.circular) {
            return false;
        }
        if (this.startAngle != that.startAngle) {
            return false;
        }
        if (this.direction != that.direction) {
            return false;
        }
        if (this.ignoreZeroValues != that.ignoreZeroValues) {
            return false;
        }
        if (this.ignoreNullValues != that.ignoreNullValues) {
            return false;
        }
        if (!PaintUtils.equal(this.sectionPaintMap, that.sectionPaintMap)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultSectionPaint, that.defaultSectionPaint)) {
            return false;
        }
        if (this.sectionOutlinesVisible != that.sectionOutlinesVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.sectionOutlinePaintMap, that.sectionOutlinePaintMap)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultSectionOutlinePaint, that.defaultSectionOutlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.sectionOutlineStrokeMap, that.sectionOutlineStrokeMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultSectionOutlineStroke, that.defaultSectionOutlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.shadowPaint, that.shadowPaint)) {
            return false;
        }
        if (!(this.shadowXOffset == that.shadowXOffset)) {
            return false;
        }
        if (!(this.shadowYOffset == that.shadowYOffset)) {
            return false;
        }
        if (!Objects.equals(this.explodePercentages, that.explodePercentages)) {
            return false;
        }
        if (!Objects.equals(this.labelGenerator, that.labelGenerator)) {
            return false;
        }
        if (!Objects.equals(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelBackgroundPaint, that.labelBackgroundPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelOutlinePaint, that.labelOutlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.labelOutlineStroke, that.labelOutlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelShadowPaint, that.labelShadowPaint)) {
            return false;
        }
        if (this.simpleLabels != that.simpleLabels) {
            return false;
        }
        if (!this.simpleLabelOffset.equals(that.simpleLabelOffset)) {
            return false;
        }
        if (!this.labelPadding.equals(that.labelPadding)) {
            return false;
        }
        if (!(this.maximumLabelWidth == that.maximumLabelWidth)) {
            return false;
        }
        if (!(this.labelGap == that.labelGap)) {
            return false;
        }
        if (!(this.labelLinkMargin == that.labelLinkMargin)) {
            return false;
        }
        if (this.labelLinksVisible != that.labelLinksVisible) {
            return false;
        }
        if (!this.labelLinkStyle.equals(that.labelLinkStyle)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelLinkPaint, that.labelLinkPaint)) {
            return false;
        }
        if (!Objects.equals(this.labelLinkStroke, that.labelLinkStroke)) {
            return false;
        }
        if (!Objects.equals(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.urlGenerator, that.urlGenerator)) {
            return false;
        }
        if (!(this.minimumArcAngleToDraw == that.minimumArcAngleToDraw)) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendItemShape, that.legendItemShape)) {
            return false;
        }
        if (!Objects.equals(this.legendLabelGenerator, that.legendLabelGenerator)) {
            return false;
        }
        if (!Objects.equals(this.legendLabelToolTipGenerator, that.legendLabelToolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.legendLabelURLGenerator, that.legendLabelURLGenerator)) {
            return false;
        }
        if (this.autoPopulateSectionPaint != that.autoPopulateSectionPaint) {
            return false;
        }
        if (this.autoPopulateSectionOutlinePaint != that.autoPopulateSectionOutlinePaint) {
            return false;
        }
        if (this.autoPopulateSectionOutlineStroke != that.autoPopulateSectionOutlineStroke) {
            return false;
        }
        if (!Objects.equals(this.shadowGenerator, that.shadowGenerator)) {
            return false;
        }
        // can't find any difference...
        return true;
    }

    /**
     * Generates a hashcode.  Note that, as with the equals method, the dataset
     * is NOT included in the hashcode.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + this.pieIndex;
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.interiorGap) ^ (Double.doubleToLongBits(this.interiorGap) >>> 32));
        hash = 73 * hash + (this.circular ? 1 : 0);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.startAngle) ^ (Double.doubleToLongBits(this.startAngle) >>> 32));
        hash = 73 * hash + Objects.hashCode(this.direction);
        hash = 73 * hash + Objects.hashCode(this.sectionPaintMap);
        hash = 73 * hash + Objects.hashCode(this.defaultSectionPaint);
        hash = 73 * hash + (this.autoPopulateSectionPaint ? 1 : 0);
        hash = 73 * hash + (this.sectionOutlinesVisible ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.sectionOutlinePaintMap);
        hash = 73 * hash + Objects.hashCode(this.defaultSectionOutlinePaint);
        hash = 73 * hash + (this.autoPopulateSectionOutlinePaint ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.sectionOutlineStrokeMap);
        hash = 73 * hash + Objects.hashCode(this.defaultSectionOutlineStroke);
        hash = 73 * hash + (this.autoPopulateSectionOutlineStroke ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.shadowPaint);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.shadowXOffset) ^ (Double.doubleToLongBits(this.shadowXOffset) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.shadowYOffset) ^ (Double.doubleToLongBits(this.shadowYOffset) >>> 32));
        hash = 73 * hash + Objects.hashCode(this.explodePercentages);
        hash = 73 * hash + Objects.hashCode(this.labelGenerator);
        hash = 73 * hash + Objects.hashCode(this.labelFont);
        hash = 73 * hash + Objects.hashCode(this.labelPaint);
        hash = 73 * hash + Objects.hashCode(this.labelBackgroundPaint);
        hash = 73 * hash + Objects.hashCode(this.labelOutlinePaint);
        hash = 73 * hash + Objects.hashCode(this.labelOutlineStroke);
        hash = 73 * hash + Objects.hashCode(this.labelShadowPaint);
        hash = 73 * hash + (this.simpleLabels ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.labelPadding);
        hash = 73 * hash + Objects.hashCode(this.simpleLabelOffset);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.maximumLabelWidth) ^ (Double.doubleToLongBits(this.maximumLabelWidth) >>> 32));
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.labelGap) ^ (Double.doubleToLongBits(this.labelGap) >>> 32));
        hash = 73 * hash + (this.labelLinksVisible ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.labelLinkStyle);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.labelLinkMargin) ^ (Double.doubleToLongBits(this.labelLinkMargin) >>> 32));
        hash = 73 * hash + Objects.hashCode(this.labelLinkPaint);
        hash = 73 * hash + Objects.hashCode(this.labelLinkStroke);
        hash = 73 * hash + Objects.hashCode(this.toolTipGenerator);
        hash = 73 * hash + Objects.hashCode(this.urlGenerator);
        hash = 73 * hash + Objects.hashCode(this.legendLabelGenerator);
        hash = 73 * hash + Objects.hashCode(this.legendLabelToolTipGenerator);
        hash = 73 * hash + Objects.hashCode(this.legendLabelURLGenerator);
        hash = 73 * hash + (this.ignoreNullValues ? 1 : 0);
        hash = 73 * hash + (this.ignoreZeroValues ? 1 : 0);
        hash = 73 * hash + Objects.hashCode(this.legendItemShape);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.minimumArcAngleToDraw) ^ (Double.doubleToLongBits(this.minimumArcAngleToDraw) >>> 32));
        hash = 73 * hash + Objects.hashCode(this.shadowGenerator);
        return hash;
    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the plot does
     *         not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PiePlot clone = (PiePlot) super.clone();
        clone.sectionPaintMap = new HashMap<>(this.sectionPaintMap);
        clone.sectionOutlinePaintMap = new HashMap<>(this.sectionOutlinePaintMap);
        clone.sectionOutlineStrokeMap = new HashMap<>(this.sectionOutlineStrokeMap);
        clone.explodePercentages = new TreeMap<>(this.explodePercentages);
        if (this.labelGenerator != null) {
            clone.labelGenerator = CloneUtils.clone(this.labelGenerator);
        }
        if (clone.dataset != null) {
            clone.dataset.addChangeListener(clone);
        }
        clone.urlGenerator = CloneUtils.copy(this.urlGenerator);
        clone.legendItemShape = CloneUtils.clone(this.legendItemShape);
        clone.legendLabelGenerator = CloneUtils.copy(this.legendLabelGenerator);
        clone.legendLabelToolTipGenerator = CloneUtils.clone(this.legendLabelToolTipGenerator);
        clone.legendLabelURLGenerator = CloneUtils.copy(this.legendLabelURLGenerator);
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
        SerialUtils.writePaint(this.defaultSectionPaint, stream);
        SerialUtils.writePaint(this.defaultSectionOutlinePaint, stream);
        SerialUtils.writeStroke(this.defaultSectionOutlineStroke, stream);
        SerialUtils.writePaint(this.shadowPaint, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
        SerialUtils.writePaint(this.labelBackgroundPaint, stream);
        SerialUtils.writePaint(this.labelOutlinePaint, stream);
        SerialUtils.writeStroke(this.labelOutlineStroke, stream);
        SerialUtils.writePaint(this.labelShadowPaint, stream);
        SerialUtils.writePaint(this.labelLinkPaint, stream);
        SerialUtils.writeStroke(this.labelLinkStroke, stream);
        SerialUtils.writeShape(this.legendItemShape, stream);
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
        this.defaultSectionPaint = SerialUtils.readPaint(stream);
        this.defaultSectionOutlinePaint = SerialUtils.readPaint(stream);
        this.defaultSectionOutlineStroke = SerialUtils.readStroke(stream);
        this.shadowPaint = SerialUtils.readPaint(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
        this.labelBackgroundPaint = SerialUtils.readPaint(stream);
        this.labelOutlinePaint = SerialUtils.readPaint(stream);
        this.labelOutlineStroke = SerialUtils.readStroke(stream);
        this.labelShadowPaint = SerialUtils.readPaint(stream);
        this.labelLinkPaint = SerialUtils.readPaint(stream);
        this.labelLinkStroke = SerialUtils.readStroke(stream);
        this.legendItemShape = SerialUtils.readShape(stream);
    }
}
