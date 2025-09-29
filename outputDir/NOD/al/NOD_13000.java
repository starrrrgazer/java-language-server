package NOD.al;
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
 * AbstractXYItemRenderer.java
 * ---------------------------
 * (C) Copyright 2002-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Richard Atkinson;
 *                   Focus Computer Services Limited;
 *                   Tim Bardzil;
 *                   Sergei Ivanov;
 *                   Peter Kolb (patch 2809117);
 *                   Martin Krauskopf;
 */
/**
 * A base class that can be used to create new {@link XYItemRenderer}
 * implementations.
 * <p>
 * <b>Subclassing</b>
 * If you create your own subclass of this renderer, please refer to the
 * Javadocs for {@link AbstractRenderer} for important information about
 * cloning.
 */
public abstract class AbstractXYItemRenderer extends AbstractRenderer implements XYItemRenderer, AnnotationChangeListener, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 8019124836026607990L;

    /**
     * The plot.
     */
    private XYPlot plot;

    /**
     * A list of item label generators (one per series).
     */
    private Map<Integer, XYItemLabelGenerator> itemLabelGeneratorMap;

    /**
     * The default item label generator.
     */
    private XYItemLabelGenerator defaultItemLabelGenerator;

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
     * Annotations to be drawn in the background layer ('underneath' the data
     * items).
     */
    private List<XYAnnotation> backgroundAnnotations;

    /**
     * Annotations to be drawn in the foreground layer ('on top' of the data
     * items).
     */
    private List<XYAnnotation> foregroundAnnotations;

    /**
     * The legend item label generator.
     */
    private XYSeriesLabelGenerator legendItemLabelGenerator;

    /**
     * The legend item tool tip generator.
     */
    private XYSeriesLabelGenerator legendItemToolTipGenerator;

    /**
     * The legend item URL generator.
     */
    private XYSeriesLabelGenerator legendItemURLGenerator;

    /**
     * Creates a renderer where the tooltip generator and the URL generator are
     * both {@code null}.
     */
    protected AbstractXYItemRenderer() {
        super();
        this.itemLabelGeneratorMap = new HashMap<>();
        this.toolTipGeneratorMap = new HashMap<>();
        this.urlGenerator = null;
        this.backgroundAnnotations = new ArrayList<>();
        this.foregroundAnnotations = new ArrayList<>();
        this.legendItemLabelGenerator = new StandardXYSeriesLabelGenerator("{0}");
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
        return 1;
    }

    /**
     * Returns the plot that the renderer is assigned to.
     *
     * @return The plot (possibly {@code null}).
     */
    @Override
    public XYPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that the renderer is assigned to.
     *
     * @param plot  the plot ({@code null} permitted).
     */
    @Override
    public void setPlot(XYPlot plot) {
        this.plot = plot;
    }

    /**
     * Initialises the renderer and returns a state object that should be
     * passed to all subsequent calls to the drawItem() method.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to
     * maintain.  The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param dataset  the dataset.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return The renderer state (never {@code null}).
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset dataset, PlotRenderingInfo info) {
        return new XYItemRendererState(info);
    }

    /**
     * Adds a {@code KEY_BEGIN_ELEMENT} hint to the graphics target.  This
     * hint is recognised by <b>JFreeSVG</b> (in theory it could be used by
     * other {@code Graphics2D} implementations also).
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param seriesKey  the series key that identifies the element
     *     ({@code null} not permitted).
     * @param itemIndex  the item index.
     */
    protected void beginElementGroup(Graphics2D g2, Comparable seriesKey, int itemIndex) {
        beginElementGroup(g2, new XYItemKey(seriesKey, itemIndex));
    }

    // ITEM LABEL GENERATOR
    /**
     * Returns the label generator for a data item.  This implementation simply
     * passes control to the {@link #getSeriesItemLabelGenerator(int)} method.
     * If, for some reason, you want a different generator for individual
     * items, you can override this method.
     *
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public XYItemLabelGenerator getItemLabelGenerator(int series, int item) {
        // otherwise look up the generator table
        XYItemLabelGenerator generator = this.itemLabelGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultItemLabelGenerator;
        }
        return generator;
    }

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public XYItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        return this.itemLabelGeneratorMap.get(series);
    }

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     */
    @Override
    public void setSeriesItemLabelGenerator(int series, XYItemLabelGenerator generator) {
        this.itemLabelGeneratorMap.put(series, generator);
        fireChangeEvent();
    }

    /**
     * Returns the default item label generator.
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public XYItemLabelGenerator getDefaultItemLabelGenerator() {
        return this.defaultItemLabelGenerator;
    }

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     */
    @Override
    public void setDefaultItemLabelGenerator(XYItemLabelGenerator generator) {
        this.defaultItemLabelGenerator = generator;
        fireChangeEvent();
    }

    // TOOL TIP GENERATOR
    /**
     * Returns the tool tip generator for a data item.  If, for some reason,
     * you want a different generator for individual items, you can override
     * this method.
     *
     * @param series  the series index (zero based).
     * @param item  the item index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public XYToolTipGenerator getToolTipGenerator(int series, int item) {
        // otherwise look up the generator table
        XYToolTipGenerator generator = this.toolTipGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultToolTipGenerator;
        }
        return generator;
    }

    /**
     * Returns the tool tip generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public XYToolTipGenerator getSeriesToolTipGenerator(int series) {
        return this.toolTipGeneratorMap.get(series);
    }

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     */
    @Override
    public void setSeriesToolTipGenerator(int series, XYToolTipGenerator generator) {
        this.toolTipGeneratorMap.put(series, generator);
        fireChangeEvent();
    }

    /**
     * Returns the default tool tip generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setDefaultToolTipGenerator(XYToolTipGenerator)
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
     *
     * @see #getDefaultToolTipGenerator()
     */
    @Override
    public void setDefaultToolTipGenerator(XYToolTipGenerator generator) {
        this.defaultToolTipGenerator = generator;
        fireChangeEvent();
    }

    // URL GENERATOR
    /**
     * Returns the URL generator for HTML image maps.
     *
     * @return The URL generator (possibly {@code null}).
     */
    @Override
    public XYURLGenerator getURLGenerator() {
        return this.urlGenerator;
    }

    /**
     * Sets the URL generator for HTML image maps and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param urlGenerator  the URL generator ({@code null} permitted).
     */
    @Override
    public void setURLGenerator(XYURLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
        fireChangeEvent();
    }

    /**
     * Adds an annotation and sends a {@link RendererChangeEvent} to all
     * registered listeners.  The annotation is added to the foreground
     * layer.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     */
    @Override
    public void addAnnotation(XYAnnotation annotation) {
        // defer argument checking
        addAnnotation(annotation, Layer.FOREGROUND);
    }

    /**
     * Adds an annotation to the specified layer and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param layer  the layer ({@code null} not permitted).
     */
    @Override
    public void addAnnotation(XYAnnotation annotation, Layer layer) {
        Args.nullNotPermitted(annotation, "annotation");
        Args.nullNotPermitted(layer, "layer");
        switch(layer) {
            case FOREGROUND:
                this.foregroundAnnotations.add(annotation);
                annotation.addChangeListener(this);
                fireChangeEvent();
                break;
            case BACKGROUND:
                this.backgroundAnnotations.add(annotation);
                annotation.addChangeListener(this);
                fireChangeEvent();
                break;
            default:
                // should never get here
                throw new RuntimeException("Unknown layer.");
        }
    }

    /**
     * Removes the specified annotation and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param annotation  the annotation to remove ({@code null} not
     *                    permitted).
     *
     * @return A boolean to indicate whether the annotation was
     *         successfully removed.
     */
    @Override
    public boolean removeAnnotation(XYAnnotation annotation) {
        boolean removed = this.foregroundAnnotations.remove(annotation);
        removed = removed & this.backgroundAnnotations.remove(annotation);
        annotation.removeChangeListener(this);
        fireChangeEvent();
        return removed;
    }

    /**
     * Removes all annotations and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     */
    @Override
    public void removeAnnotations() {
        for (XYAnnotation annotation : this.foregroundAnnotations) {
            annotation.removeChangeListener(this);
        }
        for (XYAnnotation annotation : this.backgroundAnnotations) {
            annotation.removeChangeListener(this);
        }
        this.foregroundAnnotations.clear();
        this.backgroundAnnotations.clear();
        fireChangeEvent();
    }

    /**
     * Receives notification of a change to an {@link Annotation} added to
     * this renderer.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void annotationChanged(AnnotationChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Returns a collection of the annotations that are assigned to the
     * renderer.
     *
     * @return A collection of annotations (possibly empty but never
     *     {@code null}).
     */
    @Override
    public Collection<XYAnnotation> getAnnotations() {
        List<XYAnnotation> result = new ArrayList<>(this.foregroundAnnotations);
        result.addAll(this.backgroundAnnotations);
        return result;
    }

    /**
     * Returns the legend item label generator.
     *
     * @return The label generator (never {@code null}).
     *
     * @see #setLegendItemLabelGenerator(XYSeriesLabelGenerator)
     */
    @Override
    public XYSeriesLabelGenerator getLegendItemLabelGenerator() {
        return this.legendItemLabelGenerator;
    }

    /**
     * Sets the legend item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} not permitted).
     *
     * @see #getLegendItemLabelGenerator()
     */
    @Override
    public void setLegendItemLabelGenerator(XYSeriesLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        this.legendItemLabelGenerator = generator;
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
     * Returns the lower and upper bounds (range) of the x-values in the
     * specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range ({@code null} if the dataset is {@code null}
     *         or empty).
     *
     * @see #findRangeBounds(XYDataset)
     */
    @Override
    public Range findDomainBounds(XYDataset dataset) {
        return findDomainBounds(dataset, false);
    }

    /**
     * Returns the lower and upper bounds (range) of the x-values in the
     * specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param includeInterval  include the interval (if any) for the dataset?
     *
     * @return The range ({@code null} if the dataset is {@code null}
     *         or empty).
     */
    protected Range findDomainBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (getDataBoundsIncludesVisibleSeriesOnly()) {
            List visibleSeriesKeys = new ArrayList();
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; s++) {
                if (isSeriesVisible(s)) {
                    visibleSeriesKeys.add(dataset.getSeriesKey(s));
                }
            }
            return DatasetUtils.findDomainBounds(dataset, visibleSeriesKeys, includeInterval);
        }
        return DatasetUtils.findDomainBounds(dataset, includeInterval);
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range ({@code null} if the dataset is {@code null}
     *         or empty).
     *
     * @see #findDomainBounds(XYDataset)
     */
    @Override
    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param includeInterval  include the interval (if any) for the dataset?
     *
     * @return The range ({@code null} if the dataset is {@code null}
     *         or empty).
     */
    protected Range findRangeBounds(XYDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (getDataBoundsIncludesVisibleSeriesOnly()) {
            List visibleSeriesKeys = new ArrayList();
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; s++) {
                if (isSeriesVisible(s)) {
                    visibleSeriesKeys.add(dataset.getSeriesKey(s));
                }
            }
            // the bounds should be calculated using just the items within
            // the current range of the x-axis...if there is one
            Range xRange = null;
            XYPlot p = getPlot();
            if (p != null) {
                ValueAxis xAxis = null;
                int index = p.getIndexOf(this);
                if (index >= 0) {
                    xAxis = this.plot.getDomainAxisForDataset(index);
                }
                if (xAxis != null) {
                    xRange = xAxis.getRange();
                }
            }
            if (xRange == null) {
                xRange = new Range(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
            }
            return DatasetUtils.findRangeBounds(dataset, visibleSeriesKeys, xRange, includeInterval);
        }
        return DatasetUtils.findRangeBounds(dataset, includeInterval);
    }

    /**
     * Returns a (possibly empty) collection of legend items for the series
     * that this renderer is responsible for drawing.
     *
     * @return The legend item collection (never {@code null}).
     */
    @Override
    public LegendItemCollection getLegendItems() {
        if (this.plot == null) {
            return new LegendItemCollection();
        }
        LegendItemCollection result = new LegendItemCollection();
        int index = this.plot.getIndexOf(this);
        XYDataset dataset = this.plot.getDataset(index);
        if (dataset != null) {
            int seriesCount = dataset.getSeriesCount();
            for (int i = 0; i < seriesCount; i++) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        }
        return result;
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
        String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
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
        Paint paint = lookupSeriesPaint(series);
        LegendItem item = new LegendItem(label, paint);
        item.setToolTipText(toolTipText);
        item.setURLText(urlText);
        item.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            item.setLabelPaint(labelPaint);
        }
        item.setSeriesKey(dataset.getSeriesKey(series));
        item.setSeriesIndex(series);
        item.setDataset(dataset);
        item.setDatasetIndex(datasetIndex);
        if (getTreatLegendShapeAsLine()) {
            item.setLineVisible(true);
            item.setLine(shape);
            item.setLinePaint(paint);
            item.setShapeVisible(false);
        } else {
            Paint outlinePaint = lookupSeriesOutlinePaint(series);
            Stroke outlineStroke = lookupSeriesOutlineStroke(series);
            item.setOutlinePaint(outlinePaint);
            item.setOutlineStroke(outlineStroke);
        }
        return item;
    }

    /**
     * Fills a band between two values on the axis.  This can be used to color
     * bands between the grid lines.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the domain axis.
     * @param dataArea  the data area.
     * @param start  the start value.
     * @param end  the end value.
     */
    @Override
    public void fillDomainGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double start, double end) {
        double x1 = axis.valueToJava2D(start, dataArea, plot.getDomainAxisEdge());
        double x2 = axis.valueToJava2D(end, dataArea, plot.getDomainAxisEdge());
        Rectangle2D band;
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            band = new Rectangle2D.Double(Math.min(x1, x2), dataArea.getMinY(), Math.abs(x2 - x1), dataArea.getHeight());
        } else {
            band = new Rectangle2D.Double(dataArea.getMinX(), Math.min(x1, x2), dataArea.getWidth(), Math.abs(x2 - x1));
        }
        Paint paint = plot.getDomainTickBandPaint();
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }
    }

    /**
     * Fills a band between two values on the range axis.  This can be used to
     * color bands between the grid lines.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the range axis.
     * @param dataArea  the data area.
     * @param start  the start value.
     * @param end  the end value.
     */
    @Override
    public void fillRangeGridBand(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double start, double end) {
        double y1 = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
        double y2 = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
        Rectangle2D band;
        if (plot.getOrientation() == PlotOrientation.VERTICAL) {
            band = new Rectangle2D.Double(dataArea.getMinX(), Math.min(y1, y2), dataArea.getWidth(), Math.abs(y2 - y1));
        } else {
            band = new Rectangle2D.Double(Math.min(y1, y2), dataArea.getMinY(), Math.abs(y2 - y1), dataArea.getHeight());
        }
        Paint paint = plot.getRangeTickBandPaint();
        if (paint != null) {
            g2.setPaint(paint);
            g2.fill(band);
        }
    }

    /**
     * Draws a line perpendicular to the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data.
     * @param value  the value at which the grid line should be drawn.
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the stroke ({@code null} not permitted).
     */
    @Override
    public void drawDomainLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
        if (orientation.isHorizontal()) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        } else if (orientation.isVertical()) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        }
        g2.setPaint(paint);
        g2.setStroke(stroke);
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Draws a line perpendicular to the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data.
     * @param value  the value at which the grid line should be drawn.
     * @param paint  the paint.
     * @param stroke  the stroke.
     */
    @Override
    public void drawRangeLine(Graphics2D g2, XYPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
        Range range = axis.getRange();
        if (!range.contains(value)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        Line2D line = null;
        double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
        }
        g2.setPaint(paint);
        g2.setStroke(stroke);
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Draws a line on the chart perpendicular to the x-axis to mark
     * a value or range of values.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    @Override
    public void drawDomainMarker(Graphics2D g2, XYPlot plot, ValueAxis domainAxis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = domainAxis.getRange();
            if (!range.contains(value)) {
                return;
            }
            double v = domainAxis.valueToJava2D(value, dataArea, plot.getDomainAxisEdge());
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            switch(orientation) {
                case HORIZONTAL:
                    line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                    break;
                case VERTICAL:
                    line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                    break;
                default:
                    throw new IllegalStateException("Unrecognised orientation.");
            }
            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coords = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                Rectangle2D r = TextUtils.calcAlignedStringBounds(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(r);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = domainAxis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }
            double start2d = domainAxis.valueToJava2D(start, dataArea, plot.getDomainAxisEdge());
            double end2d = domainAxis.valueToJava2D(end, dataArea, plot.getDomainAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);
            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                // clip top and bottom bounds to data area
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
            } else if (orientation == PlotOrientation.VERTICAL) {
                // clip left and right bounds to data area
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
            }
            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            } else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            // now draw the outlines, if visible...
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                if (orientation == PlotOrientation.VERTICAL) {
                    Line2D line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                } else {
                    // PlotOrientation.HORIZONTAL
                    Line2D line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                }
            }
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coords = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                Rectangle2D r = TextUtils.calcAlignedStringBounds(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(r);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
    }

    /**
     * Calculates the {@code (x, y)} coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker area.
     * @param markerOffset  the marker label offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        }
        return anchor.getAnchorPoint(anchorRect);
    }

    /**
     * Draws a line on the chart perpendicular to the y-axis to mark a value
     * or range of values.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param rangeAxis  the range axis.
     * @param marker  the marker line.
     * @param dataArea  the axis data area.
     */
    @Override
    public void drawRangeMarker(Graphics2D g2, XYPlot plot, ValueAxis rangeAxis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = rangeAxis.getRange();
            if (!range.contains(value)) {
                return;
            }
            double v = rangeAxis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            PlotOrientation orientation = plot.getOrientation();
            Line2D line = null;
            switch(orientation) {
                case HORIZONTAL:
                    line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
                    break;
                case VERTICAL:
                    line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
                    break;
                default:
                    throw new IllegalStateException("Unrecognised orientation.");
            }
            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coords = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                Rectangle2D r = TextUtils.calcAlignedStringBounds(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(r);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = rangeAxis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }
            double start2d = rangeAxis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
            double end2d = rangeAxis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
            double low = Math.min(start2d, end2d);
            double high = Math.max(start2d, end2d);
            PlotOrientation orientation = plot.getOrientation();
            Rectangle2D rect = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                // clip left and right bounds to data area
                low = Math.max(low, dataArea.getMinX());
                high = Math.min(high, dataArea.getMaxX());
                rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
            } else if (orientation == PlotOrientation.VERTICAL) {
                // clip top and bottom bounds to data area
                low = Math.max(low, dataArea.getMinY());
                high = Math.min(high, dataArea.getMaxY());
                rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
            }
            final Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            Paint p = marker.getPaint();
            if (p instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) p;
                GradientPaintTransformer t = im.getGradientPaintTransformer();
                if (t != null) {
                    gp = t.transform(gp, rect);
                }
                g2.setPaint(gp);
            } else {
                g2.setPaint(p);
            }
            g2.fill(rect);
            // now draw the outlines, if visible...
            if (im.getOutlinePaint() != null && im.getOutlineStroke() != null) {
                if (orientation == PlotOrientation.VERTICAL) {
                    Line2D line = new Line2D.Double();
                    double x0 = dataArea.getMinX();
                    double x1 = dataArea.getMaxX();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(x0, start2d, x1, start2d);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(x0, end2d, x1, end2d);
                        g2.draw(line);
                    }
                } else {
                    // PlotOrientation.HORIZONTAL
                    Line2D line = new Line2D.Double();
                    double y0 = dataArea.getMinY();
                    double y1 = dataArea.getMaxY();
                    g2.setPaint(im.getOutlinePaint());
                    g2.setStroke(im.getOutlineStroke());
                    if (range.contains(start)) {
                        line.setLine(start2d, y0, start2d, y1);
                        g2.draw(line);
                    }
                    if (range.contains(end)) {
                        line.setLine(end2d, y0, end2d, y1);
                        g2.draw(line);
                    }
                }
            }
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coords = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
                Rectangle2D r = TextUtils.calcAlignedStringBounds(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(r);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coords.getX(), (float) coords.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(originalComposite);
        }
    }

    /**
     * Calculates the (x, y) coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the marker area.
     * @param markerOffset  the marker offset.
     * @param labelOffsetForRange  ??
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    private Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetForRange, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetForRange, LengthAdjustmentType.CONTRACT);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetForRange);
        }
        return anchor.getAnchorPoint(anchorRect);
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the renderer does not support
     *         cloning.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractXYItemRenderer clone = (AbstractXYItemRenderer) super.clone();
        // 'plot' : just retain reference, not a deep copy
        clone.itemLabelGeneratorMap = CloneUtils.cloneMapValues(this.itemLabelGeneratorMap);
        clone.defaultItemLabelGenerator = CloneUtils.clone(this.defaultItemLabelGenerator);
        clone.toolTipGeneratorMap = CloneUtils.cloneMapValues(this.toolTipGeneratorMap);
        clone.defaultToolTipGenerator = CloneUtils.clone(this.defaultToolTipGenerator);
        clone.legendItemLabelGenerator = CloneUtils.clone(this.legendItemLabelGenerator);
        clone.legendItemToolTipGenerator = CloneUtils.clone(this.legendItemToolTipGenerator);
        clone.legendItemURLGenerator = CloneUtils.clone(this.legendItemURLGenerator);
        clone.foregroundAnnotations = CloneUtils.cloneList(this.foregroundAnnotations);
        clone.backgroundAnnotations = CloneUtils.cloneList(this.backgroundAnnotations);
        return clone;
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
        if (!(obj instanceof AbstractXYItemRenderer)) {
            return false;
        }
        AbstractXYItemRenderer that = (AbstractXYItemRenderer) obj;
        if (!this.itemLabelGeneratorMap.equals(that.itemLabelGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemLabelGenerator, that.defaultItemLabelGenerator)) {
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
        if (!this.foregroundAnnotations.equals(that.foregroundAnnotations)) {
            return false;
        }
        if (!this.backgroundAnnotations.equals(that.backgroundAnnotations)) {
            return false;
        }
        if (!Objects.equals(this.legendItemLabelGenerator, that.legendItemLabelGenerator)) {
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

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + itemLabelGeneratorMap.hashCode();
        result = 31 * result + (defaultItemLabelGenerator != null ? defaultItemLabelGenerator.hashCode() : 0);
        result = 31 * result + toolTipGeneratorMap.hashCode();
        result = 31 * result + (defaultToolTipGenerator != null ? defaultToolTipGenerator.hashCode() : 0);
        result = 31 * result + (urlGenerator != null ? urlGenerator.hashCode() : 0);
        result = 31 * result + (legendItemLabelGenerator != null ? legendItemLabelGenerator.hashCode() : 0);
        result = 31 * result + (legendItemToolTipGenerator != null ? legendItemToolTipGenerator.hashCode() : 0);
        result = 31 * result + (legendItemURLGenerator != null ? legendItemURLGenerator.hashCode() : 0);
        return result;
    }

    /**
     * Returns the drawing supplier from the plot.
     *
     * @return The drawing supplier (possibly {@code null}).
     */
    @Override
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result = null;
        XYPlot p = getPlot();
        if (p != null) {
            result = p.getDrawingSupplier();
        }
        return result;
    }

    /**
     * Considers the current (x, y) coordinate and updates the crosshair point
     * if it meets the criteria (usually means the (x, y) coordinate is the
     * closest to the anchor point so far).
     *
     * @param crosshairState  the crosshair state ({@code null} permitted,
     *                        but the method does nothing in that case).
     * @param x  the x-value (in data space).
     * @param y  the y-value (in data space).
     * @param datasetIndex  the index of the dataset for the point.
     * @param transX  the x-value translated to Java2D space.
     * @param transY  the y-value translated to Java2D space.
     * @param orientation  the plot orientation ({@code null} not permitted).
     */
    protected void updateCrosshairValues(CrosshairState crosshairState, double x, double y, int datasetIndex, double transX, double transY, PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        if (crosshairState != null) {
            // do we need to update the crosshair values?
            if (this.plot.isDomainCrosshairLockedOnData()) {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    // both axes
                    crosshairState.updateCrosshairPoint(x, y, datasetIndex, transX, transY, orientation);
                } else {
                    // just the domain axis...
                    crosshairState.updateCrosshairX(x, transX, datasetIndex);
                }
            } else {
                if (this.plot.isRangeCrosshairLockedOnData()) {
                    // just the range axis...
                    crosshairState.updateCrosshairY(y, transY, datasetIndex);
                }
            }
        }
    }

    /**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param negative  indicates a negative value (which affects the item
     *                  label position).
     */
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, XYDataset dataset, int series, int item, double x, double y, boolean negative) {
        XYItemLabelGenerator generator = getItemLabelGenerator(series, item);
        if (generator != null) {
            Font labelFont = getItemLabelFont(series, item);
            Paint paint = getItemLabelPaint(series, item);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, series, item);
            // get the label position..
            ItemLabelPosition position;
            if (!negative) {
                position = getPositiveItemLabelPosition(series, item);
            } else {
                position = getNegativeItemLabelPosition(series, item);
            }
            // work out the label anchor point...
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtils.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    /**
     * Draws all the annotations for the specified layer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param layer  the layer ({@code null} not permitted).
     * @param info  the plot rendering info.
     */
    @Override
    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, Layer layer, PlotRenderingInfo info) {
        Args.nullNotPermitted(layer, "layer");
        List<XYAnnotation> toDraw = new ArrayList<>();
        switch(layer) {
            case FOREGROUND:
                toDraw.addAll(this.foregroundAnnotations);
                break;
            case BACKGROUND:
                toDraw.addAll(this.backgroundAnnotations);
                break;
            default:
                // should not get here
                throw new RuntimeException("Unknown layer.");
        }
        int index = this.plot.getIndexOf(this);
        for (XYAnnotation annotation : toDraw) {
            annotation.draw(g2, this.plot, dataArea, domainAxis, rangeAxis, index, info);
        }
    }

    /**
     * Adds an entity to the collection.  Note the the {@code entityX} and
     * {@code entityY} coordinates are in Java2D space, should already be
     * adjusted for the plot orientation, and will only be used if
     * {@code hotspot} is {@code null}.
     *
     * @param entities  the entity collection being populated.
     * @param hotspot  the entity area (if {@code null} a default will be
     *              used).
     * @param dataset  the dataset.
     * @param series  the series.
     * @param item  the item.
     * @param entityX  the entity x-coordinate (in Java2D space, only used if
     *         {@code hotspot} is {@code null}).
     * @param entityY  the entity y-coordinate (in Java2D space, only used if
     *         {@code hotspot} is {@code null}).
     */
    protected void addEntity(EntityCollection entities, Shape hotspot, XYDataset dataset, int series, int item, double entityX, double entityY) {
        if (!getItemCreateEntity(series, item)) {
            return;
        }
        // if not hotspot is provided, we create a default based on the
        // provided data coordinates (which are already in Java2D space)
        if (hotspot == null) {
            double r = getDefaultEntityRadius();
            double w = r * 2;
            hotspot = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
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
     * Utility method delegating to {@link GeneralPath#moveTo} taking double as
     * parameters.
     *
     * @param hotspot  the region under construction ({@code null} not
     *           permitted);
     * @param x  the x coordinate;
     * @param y  the y coordinate;
     */
    protected static void moveTo(GeneralPath hotspot, double x, double y) {
        hotspot.moveTo((float) x, (float) y);
    }

    /**
     * Utility method delegating to {@link GeneralPath#lineTo} taking double as
     * parameters.
     *
     * @param hotspot  the region under construction ({@code null} not
     *           permitted);
     * @param x  the x coordinate;
     * @param y  the y coordinate;
     */
    protected static void lineTo(GeneralPath hotspot, double x, double y) {
        hotspot.lineTo((float) x, (float) y);
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
 * LayeredBarRenderer.java
 * -----------------------
 * (C) Copyright 2003-2021, by Arnaud Lelievre and Contributors.
 *
 * Original Author:  Arnaud Lelievre (for Garden);
 * Contributor(s):   David Gilbert;
 *                   Zoheb Borbora;
 *
 */
/**
 * A {@link CategoryItemRenderer} that represents data using bars which are
 * superimposed.  The example shown here is generated by the
 * {@code LayeredBarChartDemo1.java} program included in the JFreeChart
 * Demo Collection:
 * <br><br>
 * <img src="doc-files/LayeredBarRendererSample.png"
 * alt="LayeredBarRendererSample.png">
 */
public class LayeredBarRenderer extends BarRenderer implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8716572894780469487L;

    /**
     * A list of the width of each series bar.
     */
    protected Map<Integer, Double> seriesBarWidths;

    /**
     * Default constructor.
     */
    public LayeredBarRenderer() {
        super();
        this.seriesBarWidths = new HashMap<>();
    }

    /**
     * Returns the bar width for a series, or {@code Double.NaN} if no
     * width has been set.
     *
     * @param series  the series index (zero based).
     *
     * @return The width for the series (1.0=100%, it is the maximum).
     */
    public double getSeriesBarWidth(int series) {
        double result = Double.NaN;
        Number n = (Number) this.seriesBarWidths.get(series);
        if (n != null) {
            result = n.doubleValue();
        }
        return result;
    }

    /**
     * Sets the width of the bars of a series.
     *
     * @param series  the series index (zero based).
     * @param width  the width of the series bar in percentage (1.0=100%, it is
     *               the maximum).
     */
    public void setSeriesBarWidth(int series, double width) {
        this.seriesBarWidths.put(series, width);
    }

    /**
     * Calculates the bar width and stores it in the renderer state.
     *
     * @param plot  the plot.
     * @param dataArea  the data area.
     * @param rendererIndex  the renderer index.
     * @param state  the renderer state.
     */
    @Override
    protected void calculateBarWidth(CategoryPlot plot, Rectangle2D dataArea, int rendererIndex, CategoryItemRendererState state) {
        // calculate the bar width - this calculation differs from the
        // BarRenderer calculation because the bars are layered on top of one
        // another, so there is effectively only one bar per category for
        // the purpose of the bar width calculation
        CategoryAxis domainAxis = getDomainAxis(plot, rendererIndex);
        CategoryDataset dataset = plot.getDataset(rendererIndex);
        if (dataset != null) {
            int columns = dataset.getColumnCount();
            int rows = dataset.getRowCount();
            double space = 0.0;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                space = dataArea.getHeight();
            } else if (orientation == PlotOrientation.VERTICAL) {
                space = dataArea.getWidth();
            }
            double maxWidth = space * getMaximumBarWidth();
            double categoryMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin);
            if ((rows * columns) > 0) {
                state.setBarWidth(Math.min(used / (dataset.getColumnCount()), maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
    }

    /**
     * Draws the bar for one item in the dataset.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain (category) axis.
     * @param rangeAxis  the range (value) axis.
     * @param data  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset data, int row, int column, int pass) {
        PlotOrientation orientation = plot.getOrientation();
        if (orientation.isHorizontal()) {
            drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, row, column);
        } else if (orientation.isVertical()) {
            drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, data, row, column);
        }
    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawHorizontalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        // X
        double value = dataValue.doubleValue();
        double base = getBase();
        double lclip = getLowerClip();
        double uclip = getUpperClip();
        if (uclip <= 0.0) {
            // cases 1, 2, 3 and 4
            if (value >= uclip) {
                // bar is not visible
                return;
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        } else if (lclip <= 0.0) {
            // cases 5, 6, 7 and 8
            if (value >= uclip) {
                value = uclip;
            } else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        } else {
            // cases 9, 10, 11 and 12
            if (value <= lclip) {
                // bar is not visible
                return;
            }
            base = lclip;
            if (value >= uclip) {
                value = uclip;
            }
        }
        RectangleEdge edge = plot.getRangeAxisEdge();
        double transX1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transX2 = rangeAxis.valueToJava2D(value, dataArea, edge);
        double rectX = Math.min(transX1, transX2);
        double rectWidth = Math.abs(transX2 - transX1);
        // Y
        double rectY = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        int seriesCount = getRowCount();
        // draw the bar...
        double shift = 0.0;
        double rectHeight;
        double widthFactor = 1.0;
        double seriesBarWidth = getSeriesBarWidth(row);
        if (!Double.isNaN(seriesBarWidth)) {
            widthFactor = seriesBarWidth;
        }
        rectHeight = widthFactor * state.getBarWidth();
        rectY = rectY + (1 - widthFactor) * state.getBarWidth() / 2.0;
        if (seriesCount > 1) {
            shift = rectHeight * 0.20 / (seriesCount - 1);
        }
        Rectangle2D bar = new Rectangle2D.Double(rectX, (rectY + ((seriesCount - 1 - row) * shift)), rectWidth, (rectHeight - (seriesCount - 1 - row) * shift * 2));
        if (state.getElementHinting()) {
            beginElementGroup(g2, dataset.getRowKey(row), dataset.getColumnKey(column));
        }
        Paint itemPaint = getItemPaint(row, column);
        GradientPaintTransformer t = getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);
        // draw the outline...
        if (isDrawBarOutline() && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            Stroke stroke = getItemOutlineStroke(row, column);
            Paint paint = getItemOutlinePaint(row, column);
            if (stroke != null && paint != null) {
                g2.setStroke(stroke);
                g2.setPaint(paint);
                g2.draw(bar);
            }
        }
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < base);
        }
        // collect entity and tool tip information...
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }
    }

    /**
     * Draws the bar for a single (series, category) data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    protected void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        // nothing is drawn for null values...
        Number dataValue = dataset.getValue(row, column);
        if (dataValue == null) {
            return;
        }
        // BAR X
        double rectX = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge()) - state.getBarWidth() / 2.0;
        int seriesCount = getRowCount();
        // BAR Y
        double value = dataValue.doubleValue();
        double base = getBase();
        double lclip = getLowerClip();
        double uclip = getUpperClip();
        if (uclip <= 0.0) {
            // cases 1, 2, 3 and 4
            if (value >= uclip) {
                // bar is not visible
                return;
            }
            base = uclip;
            if (value <= lclip) {
                value = lclip;
            }
        } else if (lclip <= 0.0) {
            // cases 5, 6, 7 and 8
            if (value >= uclip) {
                value = uclip;
            } else {
                if (value <= lclip) {
                    value = lclip;
                }
            }
        } else {
            // cases 9, 10, 11 and 12
            if (value <= lclip) {
                // bar is not visible
                return;
            }
            base = getLowerClip();
            if (value >= uclip) {
                value = uclip;
            }
        }
        RectangleEdge edge = plot.getRangeAxisEdge();
        double transY1 = rangeAxis.valueToJava2D(base, dataArea, edge);
        double transY2 = rangeAxis.valueToJava2D(value, dataArea, edge);
        double rectY = Math.min(transY2, transY1);
        double rectWidth;
        double rectHeight = Math.abs(transY2 - transY1);
        // draw the bar...
        double shift = 0.0;
        double widthFactor = 1.0;
        double seriesBarWidth = getSeriesBarWidth(row);
        if (!Double.isNaN(seriesBarWidth)) {
            widthFactor = seriesBarWidth;
        }
        rectWidth = widthFactor * state.getBarWidth();
        rectX = rectX + (1 - widthFactor) * state.getBarWidth() / 2.0;
        if (seriesCount > 1) {
            // needs to be improved !!!
            shift = rectWidth * 0.20 / (seriesCount - 1);
        }
        Rectangle2D bar = new Rectangle2D.Double((rectX + ((seriesCount - 1 - row) * shift)), rectY, (rectWidth - (seriesCount - 1 - row) * shift * 2), rectHeight);
        if (state.getElementHinting()) {
            beginElementGroup(g2, dataset.getRowKey(row), dataset.getColumnKey(column));
        }
        Paint itemPaint = getItemPaint(row, column);
        GradientPaintTransformer t = getGradientPaintTransformer();
        if (t != null && itemPaint instanceof GradientPaint) {
            itemPaint = t.transform((GradientPaint) itemPaint, bar);
        }
        g2.setPaint(itemPaint);
        g2.fill(bar);
        if (isDrawBarOutline() && state.getBarWidth() > BAR_OUTLINE_WIDTH_THRESHOLD) {
            g2.setStroke(getItemOutlineStroke(row, column));
            g2.setPaint(getItemOutlinePaint(row, column));
            g2.draw(bar);
        }
        if (state.getElementHinting()) {
            endElementGroup(g2);
        }
        // draw the item labels if there are any...
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null && isItemLabelVisible(row, column)) {
            drawItemLabel(g2, dataset, row, column, plot, generator, bar, value < base);
        }
        // collect entity and tool tip information...
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, bar);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.seriesBarWidths);
        return hash;
    }

    /**
     * Tests the entity for equality with an arbitrary object.
     *
     * @param obj  the object to test against ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LayeredBarRenderer other = (LayeredBarRenderer) obj;
        if (!Objects.equals(this.seriesBarWidths, other.seriesBarWidths)) {
            return false;
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
 * -------------
 * DateAxis.java
 * -------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *                   David Li;
 *                   Michael Rauch;
 *                   Bill Kelemen;
 *                   Pawel Pabis;
 *                   Chris Boek;
 *                   Peter Kolb (patches 1934255 and 2603321);
 *                   Andrew Mickish (patch 1870189);
 *                   Fawad Halim (bug 2201869);
 *
 */
/**
 * The base class for axes that display dates.  You will find it easier to
 * understand how this axis works if you bear in mind that it really
 * displays/measures integer (or long) data, where the integers are
 * milliseconds since midnight, 1-Jan-1970.  When displaying tick labels, the
 * millisecond values are converted back to dates using a {@code DateFormat}
 * instance.
 * <P>
 * You can also create a {@link org.jfree.chart.axis.Timeline} and supply in
 * the constructor to create an axis that only contains certain domain values.
 * For example, this allows you to create a date axis that only contains
 * working days.
 */
public class DateAxis extends ValueAxis implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -1013460999649007604L;

    /**
     * The default axis range.
     */
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();

    /**
     * The default minimum auto range size.
     */
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;

    /**
     * The default anchor date.
     */
    public static final Date DEFAULT_ANCHOR_DATE = new Date();

    /**
     * The current tick unit.
     */
    private DateTickUnit tickUnit;

    /**
     * The override date format.
     */
    private DateFormat dateFormatOverride;

    /**
     * Tick marks can be displayed at the start or the middle of the time
     * period.
     */
    private DateTickMarkPosition tickMarkPosition = DateTickMarkPosition.START;

    /**
     * A timeline that includes all milliseconds (as defined by
     * {@code java.util.Date}) in the real time line.
     */
    private static class DefaultTimeline implements Timeline, Serializable {

        /**
         * Converts a millisecond into a timeline value.
         *
         * @param millisecond  the millisecond.
         *
         * @return The timeline value.
         */
        @Override
        public long toTimelineValue(long millisecond) {
            return millisecond;
        }

        /**
         * Converts a date into a timeline value.
         *
         * @param date  the domain value.
         *
         * @return The timeline value.
         */
        @Override
        public long toTimelineValue(Date date) {
            return date.getTime();
        }

        /**
         * Converts a timeline value into a millisecond (as encoded by
         * {@code java.util.Date}).
         *
         * @param value  the value.
         *
         * @return The millisecond.
         */
        @Override
        public long toMillisecond(long value) {
            return value;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value.
         *
         * @param millisecond  the millisecond.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainValue(long millisecond) {
            return true;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value.
         *
         * @param date  the date.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainValue(Date date) {
            return true;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value range.
         *
         * @param from  the start value.
         * @param to  the end value.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainRange(long from, long to) {
            return true;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value range.
         *
         * @param from  the start date.
         * @param to  the end date.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainRange(Date from, Date to) {
            return true;
        }

        /**
         * Tests an object for equality with this instance.
         *
         * @param object  the object.
         *
         * @return A boolean.
         */
        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }
            if (object instanceof DefaultTimeline) {
                return true;
            }
            return false;
        }
    }

    /**
     * A static default timeline shared by all standard DateAxis
     */
    private static final Timeline DEFAULT_TIMELINE = new DefaultTimeline();

    /**
     * The time zone for the axis.
     */
    private TimeZone timeZone;

    /**
     * The locale for the axis ({@code null} is not permitted).
     */
    private Locale locale;

    /**
     * Our underlying timeline.
     */
    private Timeline timeline;

    /**
     * Creates a date axis with no label.
     */
    public DateAxis() {
        this(null);
    }

    /**
     * Creates a date axis with the specified label.
     *
     * @param label  the axis label ({@code null} permitted).
     */
    public DateAxis(String label) {
        this(label, TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Creates a date axis.
     *
     * @param label  the axis label ({@code null} permitted).
     * @param zone  the time zone.
     * @param locale  the locale ({@code null} not permitted).
     */
    public DateAxis(String label, TimeZone zone, Locale locale) {
        super(label, DateAxis.createStandardDateTickUnits(zone, locale));
        this.tickUnit = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat());
        setAutoRangeMinimumSize(DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.locale = locale;
        this.timeline = DEFAULT_TIMELINE;
    }

    /**
     * Returns the time zone for the axis.
     *
     * @return The time zone (never {@code null}).
     *
     * @see #setTimeZone(TimeZone)
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Sets the time zone for the axis and sends an {@link AxisChangeEvent} to
     * all registered listeners.
     *
     * @param zone  the time zone ({@code null} not permitted).
     *
     * @see #getTimeZone()
     */
    public void setTimeZone(TimeZone zone) {
        Args.nullNotPermitted(zone, "zone");
        this.timeZone = zone;
        setStandardTickUnits(createStandardDateTickUnits(zone, this.locale));
        fireChangeEvent();
    }

    /**
     * Returns the locale for this axis.
     *
     * @return The locale (never {@code null}).
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Sets the locale for the axis and sends a change event to all registered
     * listeners.
     *
     * @param locale  the new locale ({@code null} not permitted).
     */
    public void setLocale(Locale locale) {
        Args.nullNotPermitted(locale, "locale");
        this.locale = locale;
        setStandardTickUnits(createStandardDateTickUnits(this.timeZone, this.locale));
        fireChangeEvent();
    }

    /**
     * Returns the underlying timeline used by this axis.
     *
     * @return The timeline.
     */
    public Timeline getTimeline() {
        return this.timeline;
    }

    /**
     * Sets the underlying timeline to use for this axis.  If the timeline is
     * changed, an {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param timeline  the timeline.
     */
    public void setTimeline(Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            fireChangeEvent();
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
     * @return The tick unit (possibly {@code null}).
     *
     * @see #setTickUnit(DateTickUnit)
     * @see ValueAxis#isAutoTickUnitSelection()
     */
    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis.  The auto-tick-unit-selection flag is
     * set to {@code false}, and registered listeners are notified that
     * the axis has been changed.
     *
     * @param unit  the tick unit.
     *
     * @see #getTickUnit()
     * @see #setTickUnit(DateTickUnit, boolean, boolean)
     */
    public void setTickUnit(DateTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit attribute and, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param unit  the new tick unit.
     * @param notify  notify registered listeners?
     * @param turnOffAutoSelection  turn off auto selection?
     *
     * @see #getTickUnit()
     */
    public void setTickUnit(DateTickUnit unit, boolean notify, boolean turnOffAutoSelection) {
        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the date format override.  If this is non-null, then it will be
     * used to format the dates on the axis.
     *
     * @return The formatter (possibly {@code null}).
     */
    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    /**
     * Sets the date format override and sends an {@link AxisChangeEvent} to
     * all registered listeners.  If this is non-null, then it will be
     * used to format the dates on the axis.
     *
     * @param formatter  the date formatter ({@code null} permitted).
     */
    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        fireChangeEvent();
    }

    /**
     * Sets the upper and lower bounds for the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect,
     * the auto-range flag is set to false.
     *
     * @param range  the new range ({@code null} not permitted).
     */
    @Override
    public void setRange(Range range) {
        setRange(range, true, true);
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
        Args.nullNotPermitted(range, "range");
        // usually the range will be a DateRange, but if it isn't do a
        // conversion...
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(Date lower, Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    @Override
    public void setRange(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    /**
     * Returns the earliest date visible on the axis.
     *
     * @return The date.
     *
     * @see #setMinimumDate(Date)
     * @see #getMaximumDate()
     */
    public Date getMinimumDate() {
        Date result;
        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getLowerDate();
        } else {
            result = new Date((long) range.getLowerBound());
        }
        return result;
    }

    /**
     * Sets the minimum date visible on the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  If
     * {@code date} is on or after the current maximum date for
     * the axis, the maximum date will be shifted to preserve the current
     * length of the axis.
     *
     * @param date  the date ({@code null} not permitted).
     *
     * @see #getMinimumDate()
     * @see #setMaximumDate(Date)
     */
    public void setMinimumDate(Date date) {
        Args.nullNotPermitted(date, "date");
        // check the new minimum date relative to the current maximum date
        Date maxDate = getMaximumDate();
        long maxMillis = maxDate.getTime();
        long newMinMillis = date.getTime();
        if (maxMillis <= newMinMillis) {
            Date oldMin = getMinimumDate();
            long length = maxMillis - oldMin.getTime();
            maxDate = new Date(newMinMillis + length);
        }
        setRange(new DateRange(date, maxDate), true, false);
        fireChangeEvent();
    }

    /**
     * Returns the latest date visible on the axis.
     *
     * @return The date.
     *
     * @see #setMaximumDate(Date)
     * @see #getMinimumDate()
     */
    public Date getMaximumDate() {
        Date result;
        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getUpperDate();
        } else {
            result = new Date((long) range.getUpperBound());
        }
        return result;
    }

    /**
     * Sets the maximum date visible on the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  If
     * {@code maximumDate} is on or before the current minimum date for
     * the axis, the minimum date will be shifted to preserve the current
     * length of the axis.
     *
     * @param maximumDate  the date ({@code null} not permitted).
     *
     * @see #getMinimumDate()
     * @see #setMinimumDate(Date)
     */
    public void setMaximumDate(Date maximumDate) {
        Args.nullNotPermitted(maximumDate, "maximumDate");
        // check the new maximum date relative to the current minimum date
        Date minDate = getMinimumDate();
        long minMillis = minDate.getTime();
        long newMaxMillis = maximumDate.getTime();
        if (minMillis >= newMaxMillis) {
            Date oldMax = getMaximumDate();
            long length = oldMax.getTime() - minMillis;
            minDate = new Date(newMaxMillis - length);
        }
        setRange(new DateRange(minDate, maximumDate), true, false);
        fireChangeEvent();
    }

    /**
     * Returns the tick mark position (start, middle or end of the time period).
     *
     * @return The position (never {@code null}).
     */
    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    /**
     * Sets the tick mark position (start, middle or end of the time period)
     * and sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     */
    public void setTickMarkPosition(DateTickMarkPosition position) {
        Args.nullNotPermitted(position, "position");
        this.tickMarkPosition = position;
        fireChangeEvent();
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
     * Returns {@code true} if the axis hides this value, and
     * {@code false} otherwise.
     *
     * @param millis  the data value.
     *
     * @return A value.
     */
    public boolean isHiddenValue(long millis) {
        return (!this.timeline.containsDomainValue(new Date(millis)));
    }

    /**
     * Translates the data value to the display coordinates (Java 2D User Space)
     * of the chart.
     *
     * @param value  the date to be plotted.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return The coordinate corresponding to the supplied data value.
     */
    @Override
    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        value = this.timeline.toTimelineValue((long) value);
        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double result = 0.0;
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
     * Translates a date to Java2D coordinates, based on the range displayed by
     * this axis for the specified data area.
     *
     * @param date  the date.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return The coordinate corresponding to the supplied date.
     */
    public double dateToJava2D(Date date, Rectangle2D area, RectangleEdge edge) {
        double value = date.getTime();
        return valueToJava2D(value, area, edge);
    }

    /**
     * Translates a Java2D coordinate into the corresponding data value.  To
     * perform this translation, you need to know the area used for plotting
     * data, and which edge the axis is located on.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return A data value.
     */
    @Override
    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        double result;
        if (isInverted()) {
            result = axisMax - ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        } else {
            result = axisMin + ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        }
        return this.timeline.toMillisecond((long) result);
    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return The value of the lowest visible tick on the axis.
     */
    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {
        return nextStandardDate(getMinimumDate(), unit);
    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return The value of the highest visible tick on the axis.
     */
    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {
        return previousStandardDate(getMaximumDate(), unit);
    }

    /**
     * Returns the previous "standard" date, for a given date and tick unit.
     *
     * @param date  the reference date.
     * @param unit  the tick unit.
     *
     * @return The previous "standard" date.
     */
    protected Date previousStandardDate(Date date, DateTickUnit unit) {
        int milliseconds;
        int seconds;
        int minutes;
        int hours;
        int days;
        int months;
        int years;
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(date);
        int count = unit.getMultiple();
        int current = calendar.get(unit.getCalendarField());
        int value = count * (current / count);
        if (DateTickUnitType.MILLISECOND.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
            calendar.set(years, months, days, hours, minutes, seconds);
            calendar.set(Calendar.MILLISECOND, value);
            Date mm = calendar.getTime();
            if (mm.getTime() >= date.getTime()) {
                calendar.set(Calendar.MILLISECOND, value - count);
                mm = calendar.getTime();
            }
            return mm;
        } else if (DateTickUnitType.SECOND.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                milliseconds = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                milliseconds = 500;
            } else {
                milliseconds = 999;
            }
            calendar.set(Calendar.MILLISECOND, milliseconds);
            calendar.set(years, months, days, hours, minutes, value);
            Date dd = calendar.getTime();
            if (dd.getTime() >= date.getTime()) {
                calendar.set(Calendar.SECOND, value - count);
                dd = calendar.getTime();
            }
            return dd;
        } else if (DateTickUnitType.MINUTE.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                seconds = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                seconds = 30;
            } else {
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, days, hours, value, seconds);
            Date d0 = calendar.getTime();
            if (d0.getTime() >= date.getTime()) {
                calendar.set(Calendar.MINUTE, value - count);
                d0 = calendar.getTime();
            }
            return d0;
        } else if (DateTickUnitType.HOUR.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                minutes = 0;
                seconds = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                minutes = 30;
                seconds = 0;
            } else {
                minutes = 59;
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, days, value, minutes, seconds);
            Date d1 = calendar.getTime();
            if (d1.getTime() >= date.getTime()) {
                calendar.set(Calendar.HOUR_OF_DAY, value - count);
                d1 = calendar.getTime();
            }
            return d1;
        } else if (DateTickUnitType.DAY.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                hours = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                hours = 12;
            } else {
                hours = 23;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, value, hours, 0, 0);
            // long result = calendar.getTimeInMillis();
            // won't work with JDK 1.3
            Date d2 = calendar.getTime();
            if (d2.getTime() >= date.getTime()) {
                calendar.set(Calendar.DATE, value - count);
                d2 = calendar.getTime();
            }
            return d2;
        } else if (DateTickUnitType.MONTH.equals(unit.getUnitType())) {
            value = count * ((current + 1) / count) - 1;
            years = calendar.get(Calendar.YEAR);
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, value, 1, 0, 0, 0);
            Month month = new Month(calendar.getTime(), this.timeZone, this.locale);
            Date standardDate = calculateDateForPosition(month, this.tickMarkPosition);
            long millis = standardDate.getTime();
            if (millis >= date.getTime()) {
                for (int i = 0; i < count; i++) {
                    month = (Month) month.previous();
                }
                // need to peg the month in case the time zone isn't the
                // default - see bug 2078057
                month.peg(Calendar.getInstance(this.timeZone));
                standardDate = calculateDateForPosition(month, this.tickMarkPosition);
            }
            return standardDate;
        } else if (DateTickUnitType.YEAR.equals(unit.getUnitType())) {
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                months = 0;
                days = 1;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                months = 6;
                days = 1;
            } else {
                months = 11;
                days = 31;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(value, months, days, 0, 0, 0);
            Date d3 = calendar.getTime();
            if (d3.getTime() >= date.getTime()) {
                calendar.set(Calendar.YEAR, value - count);
                d3 = calendar.getTime();
            }
            return d3;
        }
        return null;
    }

    /**
     * Returns a {@link java.util.Date} corresponding to the specified position
     * within a {@link RegularTimePeriod}.
     *
     * @param period  the period.
     * @param position  the position ({@code null} not permitted).
     *
     * @return A date.
     */
    private Date calculateDateForPosition(RegularTimePeriod period, DateTickMarkPosition position) {
        Args.nullNotPermitted(period, "period");
        Date result = null;
        if (position == DateTickMarkPosition.START) {
            result = new Date(period.getFirstMillisecond());
        } else if (position == DateTickMarkPosition.MIDDLE) {
            result = new Date(period.getMiddleMillisecond());
        } else if (position == DateTickMarkPosition.END) {
            result = new Date(period.getLastMillisecond());
        }
        return result;
    }

    /**
     * Returns the first "standard" date (based on the specified field and
     * units).
     *
     * @param date  the reference date.
     * @param unit  the date tick unit.
     *
     * @return The next "standard" date.
     */
    protected Date nextStandardDate(Date date, DateTickUnit unit) {
        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getMultiple());
        return calendar.getTime();
    }

    /**
     * Returns a collection of standard date tick units that uses the default
     * time zone.  This collection will be used by default, but you are free
     * to create your own collection if you want to (see the
     * {@link ValueAxis#setStandardTickUnits(TickUnitSource)} method inherited
     * from the {@link ValueAxis} class).
     *
     * @return A collection of standard date tick units.
     */
    public static TickUnitSource createStandardDateTickUnits() {
        return createStandardDateTickUnits(TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Returns a collection of standard date tick units.  This collection will
     * be used by default, but you are free to create your own collection if
     * you want to (see the
     * {@link ValueAxis#setStandardTickUnits(TickUnitSource)} method inherited
     * from the {@link ValueAxis} class).
     *
     * @param zone  the time zone ({@code null} not permitted).
     * @param locale  the locale ({@code null} not permitted).
     *
     * @return A collection of standard date tick units.
     */
    public static TickUnitSource createStandardDateTickUnits(TimeZone zone, Locale locale) {
        Args.nullNotPermitted(zone, "zone");
        Args.nullNotPermitted(locale, "locale");
        TickUnits units = new TickUnits();
        // date formatters
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS", locale);
        DateFormat f2 = new SimpleDateFormat("HH:mm:ss", locale);
        DateFormat f3 = new SimpleDateFormat("HH:mm", locale);
        DateFormat f4 = new SimpleDateFormat("d-MMM, HH:mm", locale);
        DateFormat f5 = new SimpleDateFormat("d-MMM", locale);
        DateFormat f6 = new SimpleDateFormat("MMM-yyyy", locale);
        DateFormat f7 = new SimpleDateFormat("yyyy", locale);
        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        f3.setTimeZone(zone);
        f4.setTimeZone(zone);
        f5.setTimeZone(zone);
        f6.setTimeZone(zone);
        f7.setTimeZone(zone);
        // milliseconds
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 5, DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 10, DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 25, DateTickUnitType.MILLISECOND, 5, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 50, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 100, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 250, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 500, DateTickUnitType.MILLISECOND, 50, f1));
        // seconds
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 1, DateTickUnitType.MILLISECOND, 50, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 5, DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 10, DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 30, DateTickUnitType.SECOND, 5, f2));
        // minutes
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 1, DateTickUnitType.SECOND, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 2, DateTickUnitType.SECOND, 10, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 5, DateTickUnitType.MINUTE, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 10, DateTickUnitType.MINUTE, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 15, DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 20, DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 30, DateTickUnitType.MINUTE, 5, f3));
        // hours
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 1, DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 2, DateTickUnitType.MINUTE, 10, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 4, DateTickUnitType.MINUTE, 30, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 6, DateTickUnitType.HOUR, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 12, DateTickUnitType.HOUR, 1, f4));
        // days
        units.add(new DateTickUnit(DateTickUnitType.DAY, 1, DateTickUnitType.HOUR, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 2, DateTickUnitType.HOUR, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 7, DateTickUnitType.DAY, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 15, DateTickUnitType.DAY, 1, f5));
        // months
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 1, DateTickUnitType.DAY, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 2, DateTickUnitType.DAY, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 3, DateTickUnitType.MONTH, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 4, DateTickUnitType.MONTH, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 6, DateTickUnitType.MONTH, 1, f6));
        // years
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 1, DateTickUnitType.MONTH, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 2, DateTickUnitType.MONTH, 3, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 5, DateTickUnitType.YEAR, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 10, DateTickUnitType.YEAR, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 25, DateTickUnitType.YEAR, 5, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 50, DateTickUnitType.YEAR, 10, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 100, DateTickUnitType.YEAR, 20, f7));
        return units;
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
                r = new DateRange();
            }
            long upper = this.timeline.toTimelineValue((long) r.getUpperBound());
            long lower;
            long fixedAutoRange = (long) getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            } else {
                lower = this.timeline.toTimelineValue((long) r.getLowerBound());
                double range = upper - lower;
                long minRange = (long) getAutoRangeMinimumSize();
                if (range < minRange) {
                    long expand = (long) (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }
                upper = upper + (long) (range * getUpperMargin());
                lower = lower - (long) (range * getLowerMargin());
            }
            upper = this.timeline.toMillisecond(upper);
            lower = this.timeline.toMillisecond(lower);
            DateRange dr = new DateRange(new Date(lower), new Date(upper));
            setRange(dr, false, false);
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
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    /**
     * Selects an appropriate tick size for the axis.  The strategy is to
     * display as many ticks as possible (selected from a collection of
     * 'standard' tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double zero = valueToJava2D(0.0, dataArea, edge);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = valueToJava2D(unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);
        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();
        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = valueToJava2D(unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);
        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }

    /**
     * Selects an appropriate tick size for the axis.  The strategy is to
     * display as many ticks as possible (selected from a collection of
     * 'standard' tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        double zero = valueToJava2D(0.0, dataArea, edge);
        // start with a unit that is at least 1/10th of the axis length
        double estimate1 = getRange().getLength() / 10.0;
        DateTickUnit candidate1 = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate1);
        double labelHeight1 = estimateMaximumTickLabelHeight(g2, candidate1);
        double y1 = valueToJava2D(candidate1.getSize(), dataArea, edge);
        double candidate1UnitHeight = Math.abs(y1 - zero);
        // now extrapolate based on label height and unit height...
        double estimate2 = (labelHeight1 / candidate1UnitHeight) * candidate1.getSize();
        DateTickUnit candidate2 = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate2);
        double labelHeight2 = estimateMaximumTickLabelHeight(g2, candidate2);
        double y2 = valueToJava2D(candidate2.getSize(), dataArea, edge);
        double unit2Height = Math.abs(y2 - zero);
        // make final selection...
        DateTickUnit finalUnit;
        if (labelHeight2 < unit2Height) {
            finalUnit = candidate2;
        } else {
            finalUnit = (DateTickUnit) tickUnits.getLargerTickUnit(candidate2);
        }
        setTickUnit(finalUnit, false, false);
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
    private double estimateMaximumTickLabelWidth(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of
            // the font)...
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr, upperStr;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
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
    private double estimateMaximumTickLabelHeight(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (!isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of
            // the font)...
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr, upperStr;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
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
    public List<? extends Tick> refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List<? extends Tick> result = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    /**
     * Corrects the given tick date for the position setting.
     *
     * @param time  the tick date/time.
     * @param unit  the tick unit.
     * @param position  the tick position.
     *
     * @return The adjusted time.
     */
    private Date correctTickDateForPosition(Date time, DateTickUnit unit, DateTickMarkPosition position) {
        Date result = time;
        if (unit.getUnitType().equals(DateTickUnitType.MONTH)) {
            result = calculateDateForPosition(new Month(time, this.timeZone, this.locale), position);
        } else if (unit.getUnitType().equals(DateTickUnitType.YEAR)) {
            result = calculateDateForPosition(new Year(time, this.timeZone, this.locale), position);
        }
        return result;
    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List<? extends Tick> refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List<DateTick> result = new ArrayList<>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        boolean hasRolled = false;
        while (tickDate.before(upperDate)) {
            // could add a flag to make the following correction optional...
            if (!hasRolled) {
                tickDate = correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            }
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - distance * minorTick / minorTickSpaces;
                if (minorTickTime > 0 && getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            if (!isHiddenValue(tickDate.getTime())) {
                // work out the value, label and position
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                } else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
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
                DateTick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                hasRolled = false;
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    long minorTickTime = currentTickTime + (nextTickTime - currentTickTime) * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                        result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                    }
                }
            } else {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                hasRolled = true;
            }
        }
        return result;
    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List<? extends Tick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List<DateTick> result = new ArrayList<>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        boolean hasRolled = false;
        while (tickDate.before(upperDate)) {
            // could add a flag to make the following correction optional...
            if (!hasRolled) {
                tickDate = correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            }
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - distance * minorTick / minorTickSpaces;
                if (minorTickTime > 0 && getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            if (!isHiddenValue(tickDate.getTime())) {
                // work out the value, label and position
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                } else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor, rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        angle = -Math.PI / 2.0;
                    } else {
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
                DateTick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                hasRolled = false;
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    long minorTickTime = currentTickTime + (nextTickTime - currentTickTime) * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                        result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                    }
                }
            } else {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                hasRolled = true;
            }
        }
        return result;
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be
     *                  drawn ({@code null} not permitted).
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
        // if the axis is not visible, don't draw it...
        if (!isVisible()) {
            AxisState state = new AxisState(cursor);
            // even though the axis is not visible, we need to refresh ticks in
            // case the grid is being drawn...
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        // draw the tick marks and labels...
        AxisState state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        // draw the axis label (note that 'state' is passed in *and*
        // returned)...
        if (getAttributedLabel() != null) {
            state = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        } else {
            state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        }
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;
    }

    /**
     * Zooms in on the current range (zoom-in stops once the axis length
     * reaches the equivalent of one millisecond).
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    @Override
    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.timeline.toTimelineValue((long) getRange().getLowerBound());
        double end = this.timeline.toTimelineValue((long) getRange().getUpperBound());
        double length = end - start;
        Range adjusted;
        long adjStart, adjEnd;
        if (isInverted()) {
            adjStart = (long) (start + (length * (1 - upperPercent)));
            adjEnd = (long) (start + (length * (1 - lowerPercent)));
        } else {
            adjStart = (long) (start + length * lowerPercent);
            adjEnd = (long) (start + length * upperPercent);
        }
        // when zooming to sub-millisecond ranges, it can be the case that
        // adjEnd == adjStart...and we can't have an axis with zero length
        // so we apply this instead:
        if (adjEnd <= adjStart) {
            adjEnd = adjStart + 1L;
        }
        adjusted = new DateRange(this.timeline.toMillisecond(adjStart), this.timeline.toMillisecond(adjEnd));
        setRange(adjusted);
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
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        DateAxis that = (DateAxis) obj;
        if (!Objects.equals(this.timeZone, that.timeZone)) {
            return false;
        }
        if (!Objects.equals(this.locale, that.locale)) {
            return false;
        }
        if (!Objects.equals(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!Objects.equals(this.dateFormatOverride, that.dateFormatOverride)) {
            return false;
        }
        if (!Objects.equals(this.tickMarkPosition, that.tickMarkPosition)) {
            return false;
        }
        if (!Objects.equals(this.timeline, that.timeline)) {
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
     * Returns a clone of the object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the axis does
     *         not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DateAxis clone = (DateAxis) super.clone();
        // 'dateTickUnit' is immutable : no need to clone
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride = (DateFormat) this.dateFormatOverride.clone();
        }
        // 'tickMarkPosition' is immutable : no need to clone
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
 * MinMaxCategoryRenderer.java
 * ---------------------------
 * (C) Copyright 2002-present, by David Gilbert.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   David Gilbert;
 *                   Christian W. Zuckschwerdt;
 *                   Nicolas Brodu (for Astrium and EADS Corporate Research
 *                   Center);
 */
/**
 * Renderer for drawing min max plot. This renderer draws all the series under
 * the same category in the same x position using {@code objectIcon} and
 * a line from the maximum value to the minimum value. For use with the
 * {@link CategoryPlot} class. The example shown here is generated by
 * the {@code MinMaxCategoryPlotDemo1.java} program included in the
 * JFreeChart Demo Collection:
 * <br><br>
 * <img src="doc-files/MinMaxCategoryRendererSample.png"
 * alt="MinMaxCategoryRendererSample.png">
 */
public class MinMaxCategoryRenderer extends AbstractCategoryItemRenderer {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 2935615937671064911L;

    /**
     * A flag indicating whether lines are drawn between XY points.
     */
    private boolean plotLines = false;

    /**
     * The paint of the line between the minimum value and the maximum value.
     */
    private transient Paint groupPaint = Color.BLACK;

    /**
     * The stroke of the line between the minimum value and the maximum value.
     */
    private transient Stroke groupStroke = new BasicStroke(1.0f);

    /**
     * The icon used to indicate the minimum value.
     */
    private transient Icon minIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, Color.BLACK);

    /**
     * The icon used to indicate the maximum value.
     */
    private transient Icon maxIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, Color.BLACK);

    /**
     * The icon used to indicate the values.
     */
    private transient Icon objectIcon = getIcon(new Line2D.Double(-4, 0, 4, 0), false, true);

    /**
     * The last category.
     */
    private int lastCategory = -1;

    /**
     * The minimum.
     */
    private double min;

    /**
     * The maximum.
     */
    private double max;

    /**
     * Default constructor.
     */
    public MinMaxCategoryRenderer() {
        super();
    }

    /**
     * Gets whether lines are drawn between category points.
     *
     * @return boolean true if line will be drawn between sequenced categories,
     *         otherwise false.
     *
     * @see #setDrawLines(boolean)
     */
    public boolean isDrawLines() {
        return this.plotLines;
    }

    /**
     * Sets the flag that controls whether lines are drawn to connect
     * the items within a series and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param draw  the new value of the flag.
     *
     * @see #isDrawLines()
     */
    public void setDrawLines(boolean draw) {
        if (this.plotLines != draw) {
            this.plotLines = draw;
            fireChangeEvent();
        }
    }

    /**
     * Returns the paint used to draw the line between the minimum and maximum
     * value items in each category.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setGroupPaint(Paint)
     */
    public Paint getGroupPaint() {
        return this.groupPaint;
    }

    /**
     * Sets the paint used to draw the line between the minimum and maximum
     * value items in each category and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getGroupPaint()
     */
    public void setGroupPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.groupPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw the line between the minimum and maximum
     * value items in each category.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setGroupStroke(Stroke)
     */
    public Stroke getGroupStroke() {
        return this.groupStroke;
    }

    /**
     * Sets the stroke of the line between the minimum value and the maximum
     * value and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param stroke the new stroke ({@code null} not permitted).
     */
    public void setGroupStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.groupStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the icon drawn for each data item.
     *
     * @return The icon (never {@code null}).
     *
     * @see #setObjectIcon(Icon)
     */
    public Icon getObjectIcon() {
        return this.objectIcon;
    }

    /**
     * Sets the icon drawn for each data item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param icon  the icon.
     *
     * @see #getObjectIcon()
     */
    public void setObjectIcon(Icon icon) {
        Args.nullNotPermitted(icon, "icon");
        this.objectIcon = icon;
        fireChangeEvent();
    }

    /**
     * Returns the icon displayed for the maximum value data item within each
     * category.
     *
     * @return The icon (never {@code null}).
     *
     * @see #setMaxIcon(Icon)
     */
    public Icon getMaxIcon() {
        return this.maxIcon;
    }

    /**
     * Sets the icon displayed for the maximum value data item within each
     * category and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param icon  the icon ({@code null} not permitted).
     *
     * @see #getMaxIcon()
     */
    public void setMaxIcon(Icon icon) {
        Args.nullNotPermitted(icon, "icon");
        this.maxIcon = icon;
        fireChangeEvent();
    }

    /**
     * Returns the icon displayed for the minimum value data item within each
     * category.
     *
     * @return The icon (never {@code null}).
     *
     * @see #setMinIcon(Icon)
     */
    public Icon getMinIcon() {
        return this.minIcon;
    }

    /**
     * Sets the icon displayed for the minimum value data item within each
     * category and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param icon  the icon ({@code null} not permitted).
     *
     * @see #getMinIcon()
     */
    public void setMinIcon(Icon icon) {
        Args.nullNotPermitted(icon, "icon");
        this.minIcon = icon;
        fireChangeEvent();
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area in which the data is drawn.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        // first check the number we are plotting...
        Number value = dataset.getValue(row, column);
        if (value != null) {
            // current data point...
            double x1 = domainAxis.getCategoryMiddle(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double y1 = rangeAxis.valueToJava2D(value.doubleValue(), dataArea, plot.getRangeAxisEdge());
            Shape hotspot = new Rectangle2D.Double(x1 - 4, y1 - 4, 8.0, 8.0);
            g2.setPaint(getItemPaint(row, column));
            g2.setStroke(getItemStroke(row, column));
            PlotOrientation orient = plot.getOrientation();
            if (orient == PlotOrientation.VERTICAL) {
                this.objectIcon.paintIcon(null, g2, (int) x1, (int) y1);
            } else {
                this.objectIcon.paintIcon(null, g2, (int) y1, (int) x1);
            }
            if (this.lastCategory == column) {
                if (this.min > value.doubleValue()) {
                    this.min = value.doubleValue();
                }
                if (this.max < value.doubleValue()) {
                    this.max = value.doubleValue();
                }
                // last series, so we are ready to draw the min and max
                if (dataset.getRowCount() - 1 == row) {
                    g2.setPaint(this.groupPaint);
                    g2.setStroke(this.groupStroke);
                    double minY = rangeAxis.valueToJava2D(this.min, dataArea, plot.getRangeAxisEdge());
                    double maxY = rangeAxis.valueToJava2D(this.max, dataArea, plot.getRangeAxisEdge());
                    if (orient == PlotOrientation.VERTICAL) {
                        g2.draw(new Line2D.Double(x1, minY, x1, maxY));
                        this.minIcon.paintIcon(null, g2, (int) x1, (int) minY);
                        this.maxIcon.paintIcon(null, g2, (int) x1, (int) maxY);
                    } else {
                        g2.draw(new Line2D.Double(minY, x1, maxY, x1));
                        this.minIcon.paintIcon(null, g2, (int) minY, (int) x1);
                        this.maxIcon.paintIcon(null, g2, (int) maxY, (int) x1);
                    }
                }
            } else {
                // reset the min and max
                this.lastCategory = column;
                this.min = value.doubleValue();
                this.max = value.doubleValue();
            }
            // connect to the previous point
            if (this.plotLines) {
                if (column != 0) {
                    Number previousValue = dataset.getValue(row, column - 1);
                    if (previousValue != null) {
                        // previous data point...
                        double previous = previousValue.doubleValue();
                        double x0 = domainAxis.getCategoryMiddle(column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge());
                        double y0 = rangeAxis.valueToJava2D(previous, dataArea, plot.getRangeAxisEdge());
                        g2.setPaint(getItemPaint(row, column));
                        g2.setStroke(getItemStroke(row, column));
                        Line2D line;
                        if (orient == PlotOrientation.VERTICAL) {
                            line = new Line2D.Double(x0, y0, x1, y1);
                        } else {
                            line = new Line2D.Double(y0, x0, y1, x1);
                        }
                        g2.draw(line);
                    }
                }
            }
            // add an item entity, if this information is being collected
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, hotspot);
            }
        }
    }

    /**
     * Tests this instance for equality with an arbitrary object.  The icon
     * fields are NOT included in the test, so this implementation is a little
     * weak.
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
        if (!(obj instanceof MinMaxCategoryRenderer)) {
            return false;
        }
        MinMaxCategoryRenderer that = (MinMaxCategoryRenderer) obj;
        if (this.plotLines != that.plotLines) {
            return false;
        }
        if (!PaintUtils.equal(this.groupPaint, that.groupPaint)) {
            return false;
        }
        if (!this.groupStroke.equals(that.groupStroke)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns an icon.
     *
     * @param shape  the shape.
     * @param fillPaint  the fill paint.
     * @param outlinePaint  the outline paint.
     *
     * @return The icon.
     */
    private Icon getIcon(Shape shape, final Paint fillPaint, final Paint outlinePaint) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon() {

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fillPaint != null) {
                    g2.setPaint(fillPaint);
                    g2.fill(path);
                }
                if (outlinePaint != null) {
                    g2.setPaint(outlinePaint);
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
    }

    /**
     * Returns an icon from a shape.
     *
     * @param shape  the shape.
     * @param fill  the fill flag.
     * @param outline  the outline flag.
     *
     * @return The icon.
     */
    private Icon getIcon(Shape shape, final boolean fill, final boolean outline) {
        final int width = shape.getBounds().width;
        final int height = shape.getBounds().height;
        final GeneralPath path = new GeneralPath(shape);
        return new Icon() {

            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                path.transform(AffineTransform.getTranslateInstance(x, y));
                if (fill) {
                    g2.fill(path);
                }
                if (outline) {
                    g2.draw(path);
                }
                path.transform(AffineTransform.getTranslateInstance(-x, -y));
            }

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }
        };
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
        SerialUtils.writeStroke(this.groupStroke, stream);
        SerialUtils.writePaint(this.groupPaint, stream);
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
        this.groupStroke = SerialUtils.readStroke(stream);
        this.groupPaint = SerialUtils.readPaint(stream);
        this.minIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, Color.BLACK);
        this.maxIcon = getIcon(new Arc2D.Double(-4, -4, 8, 8, 0, 360, Arc2D.OPEN), null, Color.BLACK);
        this.objectIcon = getIcon(new Line2D.Double(-4, 0, 4, 0), false, true);
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
 * PaintUtils.java
 * ---------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributors:     -;
 */
/**
 * Utility code that relates to {@code Paint} objects.
 */
public class PaintUtils {

    /**
     * Private constructor prevents object creation.
     */
    private PaintUtils() {
    }

    /**
     * Returns {@code true} if the two {@code Paint} objects are equal
     * OR both {@code null}.  This method handles
     * {@code GradientPaint}, {@code LinearGradientPaint} and
     * {@code RadialGradientPaint} as a special cases, since those classes do
     * not override the {@code equals()} method.
     *
     * @param p1  paint 1 ({@code null} permitted).
     * @param p2  paint 2 ({@code null} permitted).
     *
     * @return A boolean.
     */
    public static boolean equal(Paint p1, Paint p2) {
        if (p1 == p2) {
            return true;
        }
        // handle cases where either or both arguments are null
        if (p1 == null) {
            return (p2 == null);
        }
        if (p2 == null) {
            return false;
        }
        // handle GradientPaint as a special case...
        if (p1 instanceof GradientPaint && p2 instanceof GradientPaint) {
            GradientPaint gp1 = (GradientPaint) p1;
            GradientPaint gp2 = (GradientPaint) p2;
            return gp1.getColor1().equals(gp2.getColor1()) && gp1.getColor2().equals(gp2.getColor2()) && gp1.getPoint1().equals(gp2.getPoint1()) && gp1.getPoint2().equals(gp2.getPoint2()) && gp1.isCyclic() == gp2.isCyclic() && gp1.getTransparency() == gp2.getTransparency();
        } else if (p1 instanceof LinearGradientPaint && p2 instanceof LinearGradientPaint) {
            LinearGradientPaint lgp1 = (LinearGradientPaint) p1;
            LinearGradientPaint lgp2 = (LinearGradientPaint) p2;
            return lgp1.getStartPoint().equals(lgp2.getStartPoint()) && lgp1.getEndPoint().equals(lgp2.getEndPoint()) && Arrays.equals(lgp1.getFractions(), lgp2.getFractions()) && Arrays.equals(lgp1.getColors(), lgp2.getColors()) && lgp1.getCycleMethod() == lgp2.getCycleMethod() && lgp1.getColorSpace() == lgp2.getColorSpace() && lgp1.getTransform().equals(lgp2.getTransform());
        } else if (p1 instanceof RadialGradientPaint && p2 instanceof RadialGradientPaint) {
            RadialGradientPaint rgp1 = (RadialGradientPaint) p1;
            RadialGradientPaint rgp2 = (RadialGradientPaint) p2;
            return rgp1.getCenterPoint().equals(rgp2.getCenterPoint()) && rgp1.getRadius() == rgp2.getRadius() && rgp1.getFocusPoint().equals(rgp2.getFocusPoint()) && Arrays.equals(rgp1.getFractions(), rgp2.getFractions()) && Arrays.equals(rgp1.getColors(), rgp2.getColors()) && rgp1.getCycleMethod() == rgp2.getCycleMethod() && rgp1.getColorSpace() == rgp2.getColorSpace() && rgp1.getTransform().equals(rgp2.getTransform());
        } else {
            return p1.equals(p2);
        }
    }

    /**
     * Returns {@code true} if the two maps contain the same set of entries and
     * {@code false} otherwise.
     *
     * @param <K>  the key type.
     * @param map1  the first map.
     * @param map2  the second map.
     *
     * @return A boolean.
     */
    public static <K extends Comparable<K>> boolean equal(Map<K, Paint> map1, Map<K, Paint> map2) {
        if (!map1.keySet().equals(map2.keySet())) {
            return false;
        }
        for (K key : map1.keySet()) {
            Paint p1 = map1.get(key);
            Paint p2 = map2.get(key);
            if (!PaintUtils.equal(p1, p2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts a color into a string. If the color is equal to one of the
     * defined constant colors, that name is returned instead. Otherwise the
     * color is returned as hex-string.
     *
     * @param c the color.
     * @return the string for this color.
     */
    public static String colorToString(Color c) {
        try {
            Field[] fields = Color.class.getFields();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                if (Modifier.isPublic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                    final String name = f.getName();
                    final Object oColor = f.get(null);
                    if (oColor instanceof Color) {
                        if (c.equals(oColor)) {
                            return name;
                        }
                    }
                }
            }
        } catch (Exception e) {
            //
        }
        // no defined constant color, so this must be a user defined color
        final String color = Integer.toHexString(c.getRGB() & 0x00ffffff);
        final StringBuilder retval = new StringBuilder(7);
        retval.append('#');
        final int fillUp = 6 - color.length();
        retval.append("0".repeat(fillUp));
        retval.append(color);
        return retval.toString();
    }

    /**
     * Converts a given string into a color.
     *
     * @param value the string, either a name or a hex-string.
     * @return the color.
     */
    public static Color stringToColor(String value) {
        if (value == null) {
            return Color.BLACK;
        }
        try {
            // get color by hex or octal value
            return Color.decode(value);
        } catch (NumberFormatException nfe) {
            // if we can't decode lets try to get it by name
            try {
                // try to get a color by name using reflection
                final Field f = Color.class.getField(value);
                return (Color) f.get(null);
            } catch (Exception ce) {
                // if we can't get any color return black
                return Color.BLACK;
            }
        }
    }
}
