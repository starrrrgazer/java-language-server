package DEF.ch;
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
 * ---------------------------------
 * AbstractCategoryItemRenderer.java
 * ---------------------------------
 * (C) Copyright 2002-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Richard Atkinson;
 *                   Peter Kolb (patch 2497611);
 * 
 */
/**
 * An abstract base class that you can use to implement a new
 * {@link CategoryItemRenderer}.  When you create a new
 * {@link CategoryItemRenderer} you are not required to extend this class,
 * but it makes the job easier.
 */
public abstract class AbstractCategoryItemRenderer extends AbstractRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 1247553218442497391L;

    /**
     * The plot that the renderer is assigned to.
     */
    private CategoryPlot plot;

    /**
     * A list of item label generators (one per series).
     */
    private Map<Integer, CategoryItemLabelGenerator> itemLabelGeneratorMap;

    /**
     * The default item label generator.
     */
    private CategoryItemLabelGenerator defaultItemLabelGenerator;

    /**
     * A list of tool tip generators (one per series).
     */
    private Map<Integer, CategoryToolTipGenerator> toolTipGeneratorMap;

    /**
     * The default tool tip generator.
     */
    private CategoryToolTipGenerator defaultToolTipGenerator;

    /**
     * A list of item label generators (one per series).
     */
    private Map<Integer, CategoryURLGenerator> itemURLGeneratorMap;

    /**
     * The default item label generator.
     */
    private CategoryURLGenerator defaultItemURLGenerator;

    /**
     * The legend item label generator.
     */
    private CategorySeriesLabelGenerator legendItemLabelGenerator;

    /**
     * The legend item tool tip generator.
     */
    private CategorySeriesLabelGenerator legendItemToolTipGenerator;

    /**
     * The legend item URL generator.
     */
    private CategorySeriesLabelGenerator legendItemURLGenerator;

    /**
     * The number of rows in the dataset (temporary record).
     */
    private transient int rowCount;

    /**
     * The number of columns in the dataset (temporary record).
     */
    private transient int columnCount;

    /**
     * Creates a new renderer with no tool tip generator and no URL generator.
     * The defaults (no tool tip or URL generators) have been chosen to
     * minimise the processing required to generate a default chart.  If you
     * require tool tips or URLs, then you can easily add the required
     * generators.
     */
    protected AbstractCategoryItemRenderer() {
        this.itemLabelGeneratorMap = new HashMap<>();
        this.toolTipGeneratorMap = new HashMap<>();
        this.itemURLGeneratorMap = new HashMap<>();
        this.legendItemLabelGenerator = new StandardCategorySeriesLabelGenerator();
    }

    /**
     * Returns the number of passes through the dataset required by the
     * renderer.  This method returns {@code 1}, subclasses should
     * override if they need more passes.
     *
     * @return The pass count.
     */
    @Override
    public int getPassCount() {
        return 1;
    }

    /**
     * Returns the plot that the renderer has been assigned to (where
     * {@code null} indicates that the renderer is not currently assigned
     * to a plot).
     *
     * @return The plot (possibly {@code null}).
     *
     * @see #setPlot(CategoryPlot)
     */
    @Override
    public CategoryPlot getPlot() {
        return this.plot;
    }

    /**
     * Sets the plot that the renderer has been assigned to.  This method is
     * usually called by the {@link CategoryPlot}, in normal usage you
     * shouldn't need to call this method directly.
     *
     * @param plot  the plot ({@code null} not permitted).
     *
     * @see #getPlot()
     */
    @Override
    public void setPlot(CategoryPlot plot) {
        Args.nullNotPermitted(plot, "plot");
        this.plot = plot;
    }

    // ITEM LABEL GENERATOR
    /**
     * Returns the item label generator for a data item.  This implementation
     * simply passes control to the {@link #getSeriesItemLabelGenerator(int)}
     * method.  If, for some reason, you want a different generator for
     * individual items, you can override this method.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public CategoryItemLabelGenerator getItemLabelGenerator(int row, int column) {
        return getSeriesItemLabelGenerator(row);
    }

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)
     */
    @Override
    public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {
        // otherwise look up the generator table
        CategoryItemLabelGenerator generator = this.itemLabelGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultItemLabelGenerator;
        }
        return generator;
    }

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    @Override
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
        setSeriesItemLabelGenerator(series, generator, true);
    }

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    @Override
    public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator, boolean notify) {
        this.itemLabelGeneratorMap.put(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default item label generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setDefaultItemLabelGenerator(CategoryItemLabelGenerator)
     */
    @Override
    public CategoryItemLabelGenerator getDefaultItemLabelGenerator() {
        return this.defaultItemLabelGenerator;
    }

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultItemLabelGenerator()
     */
    @Override
    public void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator) {
        setDefaultItemLabelGenerator(generator, true);
    }

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultItemLabelGenerator()
     */
    @Override
    public void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator, boolean notify) {
        this.defaultItemLabelGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    // TOOL TIP GENERATOR
    /**
     * Returns the tool tip generator that should be used for the specified
     * item.  This method looks up the generator using the "three-layer"
     * approach outlined in the general description of this interface.  You
     * can override this method if you want to return a different generator per
     * item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The generator (possibly {@code null}).
     */
    @Override
    public CategoryToolTipGenerator getToolTipGenerator(int row, int column) {
        CategoryToolTipGenerator result = getSeriesToolTipGenerator(row);
        if (result == null) {
            result = this.defaultToolTipGenerator;
        }
        return result;
    }

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
    @Override
    public CategoryToolTipGenerator getSeriesToolTipGenerator(int series) {
        return this.toolTipGeneratorMap.get(series);
    }

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    @Override
    public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator) {
        setSeriesToolTipGenerator(series, generator, true);
    }

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    @Override
    public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator, boolean notify) {
        this.toolTipGeneratorMap.put(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default tool tip generator (the "layer 2" generator).
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setDefaultToolTipGenerator(CategoryToolTipGenerator)
     */
    @Override
    public CategoryToolTipGenerator getDefaultToolTipGenerator() {
        return this.defaultToolTipGenerator;
    }

    /**
     * Sets the default tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultToolTipGenerator()
     */
    @Override
    public void setDefaultToolTipGenerator(CategoryToolTipGenerator generator) {
        setDefaultToolTipGenerator(generator, true);
    }

    /**
     * Sets the default tool tip generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultToolTipGenerator()
     */
    @Override
    public void setDefaultToolTipGenerator(CategoryToolTipGenerator generator, boolean notify) {
        this.defaultToolTipGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    // URL GENERATOR
    /**
     * Returns the URL generator for a data item.  This method just calls the
     * getSeriesItemURLGenerator method, but you can override this behaviour if
     * you want to.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The URL generator.
     */
    @Override
    public CategoryURLGenerator getItemURLGenerator(int row, int column) {
        return getSeriesItemURLGenerator(row);
    }

    /**
     * Returns the URL generator for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The URL generator for the series.
     *
     * @see #setSeriesItemURLGenerator(int, CategoryURLGenerator)
     */
    @Override
    public CategoryURLGenerator getSeriesItemURLGenerator(int series) {
        // otherwise look up the generator table
        CategoryURLGenerator generator = this.itemURLGeneratorMap.get(series);
        if (generator == null) {
            generator = this.defaultItemURLGenerator;
        }
        return generator;
    }

    /**
     * Sets the URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    @Override
    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator) {
        setSeriesItemURLGenerator(series, generator, true);
    }

    /**
     * Sets the URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero based).
     * @param generator  the generator.
     * @param notify  notify listeners?
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    @Override
    public void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator, boolean notify) {
        this.itemURLGeneratorMap.put(series, generator);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the default item URL generator.
     *
     * @return The item URL generator.
     *
     * @see #setDefaultItemURLGenerator(CategoryURLGenerator)
     */
    @Override
    public CategoryURLGenerator getDefaultItemURLGenerator() {
        return this.defaultItemURLGenerator;
    }

    /**
     * Sets the default item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     *
     * @see #getDefaultItemURLGenerator()
     */
    @Override
    public void setDefaultItemURLGenerator(CategoryURLGenerator generator) {
        setDefaultItemURLGenerator(generator, true);
    }

    /**
     * Sets the default item URL generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getDefaultItemURLGenerator()
     */
    @Override
    public void setDefaultItemURLGenerator(CategoryURLGenerator generator, boolean notify) {
        this.defaultItemURLGenerator = generator;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the number of rows in the dataset.  This value is updated in the
     * {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return The row count.
     */
    public int getRowCount() {
        return this.rowCount;
    }

    /**
     * Returns the number of columns in the dataset.  This value is updated in
     * the {@link AbstractCategoryItemRenderer#initialise} method.
     *
     * @return The column count.
     */
    public int getColumnCount() {
        return this.columnCount;
    }

    /**
     * Creates a new state instance---this method is called from the
     * {@link #initialise(Graphics2D, Rectangle2D, CategoryPlot, int,
     * PlotRenderingInfo)} method.  Subclasses can override this method if
     * they need to use a subclass of {@link CategoryItemRendererState}.
     *
     * @param info  collects plot rendering info ({@code null} permitted).
     *
     * @return The new state instance (never {@code null}).
     */
    protected CategoryItemRendererState createState(PlotRenderingInfo info) {
        return new CategoryItemRendererState(info);
    }

    /**
     * Initialises the renderer and returns a state object that will be used
     * for the remainder of the drawing process for a single chart.  The state
     * object allows for the fact that the renderer may be used simultaneously
     * by multiple threads (each thread will work with a separate state object).
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  an object for returning information about the structure of
     *              the plot ({@code null} permitted).
     *
     * @return The renderer state.
     */
    @Override
    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        setPlot(plot);
        CategoryDataset data = plot.getDataset(rendererIndex);
        if (data != null) {
            this.rowCount = data.getRowCount();
            this.columnCount = data.getColumnCount();
        } else {
            this.rowCount = 0;
            this.columnCount = 0;
        }
        CategoryItemRendererState state = createState(info);
        state.setElementHinting(plot.fetchElementHintingFlag());
        int[] visibleSeriesTemp = new int[this.rowCount];
        int visibleSeriesCount = 0;
        for (int row = 0; row < this.rowCount; row++) {
            if (isSeriesVisible(row)) {
                visibleSeriesTemp[visibleSeriesCount] = row;
                visibleSeriesCount++;
            }
        }
        int[] visibleSeries = new int[visibleSeriesCount];
        System.arraycopy(visibleSeriesTemp, 0, visibleSeries, 0, visibleSeriesCount);
        state.setVisibleSeriesArray(visibleSeries);
        return state;
    }

    /**
     * Adds a {@code KEY_BEGIN_ELEMENT} hint to the graphics target.  This
     * hint is recognised by <b>JFreeSVG</b> (in theory it could be used by
     * other {@code Graphics2D} implementations also).
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param rowKey  the row key that identifies the element ({@code null} not
     *     permitted).
     * @param columnKey  the column key that identifies the element
     *     ({@code null} not permitted).
     */
    protected void beginElementGroup(Graphics2D g2, Comparable rowKey, Comparable columnKey) {
        beginElementGroup(g2, new KeyedValues2DItemKey(rowKey, columnKey));
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range (or {@code null} if the dataset is
     *         {@code null} or empty).
     */
    @Override
    public Range findRangeBounds(CategoryDataset dataset) {
        return findRangeBounds(dataset, false);
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param includeInterval  include the y-interval if the dataset has one.
     *
     * @return The range ({@code null} if the dataset is {@code null} or empty).
     */
    protected Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
        if (dataset == null) {
            return null;
        }
        if (getDataBoundsIncludesVisibleSeriesOnly()) {
            List visibleSeriesKeys = new ArrayList();
            int seriesCount = dataset.getRowCount();
            for (int s = 0; s < seriesCount; s++) {
                if (isSeriesVisible(s)) {
                    visibleSeriesKeys.add(dataset.getRowKey(s));
                }
            }
            return DatasetUtils.findRangeBounds(dataset, visibleSeriesKeys, includeInterval);
        } else {
            return DatasetUtils.findRangeBounds(dataset, includeInterval);
        }
    }

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
    @Override
    public double getItemMiddle(Comparable<?> rowKey, Comparable<?> columnKey, CategoryDataset<?, ?> dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
        return axis.getCategoryMiddle(columnKey, dataset.getColumnKeys(), area, edge);
    }

    /**
     * Draws a background for the data area.  The default implementation just
     * gets the plot to draw the background, but some renderers will override
     * this behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    @Override
    public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        plot.drawBackground(g2, dataArea);
    }

    /**
     * Draws an outline for the data area.  The default implementation just
     * gets the plot to draw the outline, but some renderers will override this
     * behaviour.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    @Override
    public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
        plot.drawOutline(g2, dataArea);
    }

    /**
     * Draws a grid line against the domain axis.
     * <P>
     * Note that this default implementation assumes that the horizontal axis
     * is the domain axis. If this is not the case, you will need to override
     * this method.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data.
     * @param value  the Java2D value at which the grid line should be drawn.
     */
    @Override
    public void drawDomainGridline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value) {
        Line2D line = null;
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
        } else if (orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
        }
        Paint paint = plot.getDomainGridlinePaint();
        if (paint == null) {
            paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT;
        }
        g2.setPaint(paint);
        Stroke stroke = plot.getDomainGridlineStroke();
        if (stroke == null) {
            stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE;
        }
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
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the stroke ({@code null} not permitted).
     */
    @Override
    public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
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
     * Draws a marker for the domain axis.
     *
     * @param g2  the graphics device (not {@code null}).
     * @param plot  the plot (not {@code null}).
     * @param axis  the range axis (not {@code null}).
     * @param marker  the marker to be drawn (not {@code null}).
     * @param dataArea  the area inside the axes (not {@code null}).
     *
     * @see #drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker,
     *     Rectangle2D)
     */
    @Override
    public void drawDomainMarker(Graphics2D g2, CategoryPlot plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea) {
        Comparable category = marker.getKey();
        CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
        int columnIndex = dataset.getColumnIndex(category);
        if (columnIndex < 0) {
            return;
        }
        final Composite savedComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
        PlotOrientation orientation = plot.getOrientation();
        Rectangle2D bounds;
        if (marker.getDrawAsLine()) {
            double v = axis.getCategoryMiddle(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else {
                throw new IllegalStateException();
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            bounds = line.getBounds2D();
        } else {
            double v0 = axis.getCategoryStart(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            double v1 = axis.getCategoryEnd(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
            Rectangle2D area = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                area = new Rectangle2D.Double(dataArea.getMinX(), v0, dataArea.getWidth(), (v1 - v0));
            } else if (orientation == PlotOrientation.VERTICAL) {
                area = new Rectangle2D.Double(v0, dataArea.getMinY(), (v1 - v0), dataArea.getHeight());
            }
            g2.setPaint(marker.getPaint());
            g2.fill(area);
            bounds = area;
        }
        String label = marker.getLabel();
        RectangleAnchor anchor = marker.getLabelAnchor();
        if (label != null) {
            Font labelFont = marker.getLabelFont();
            g2.setFont(labelFont);
            g2.setPaint(marker.getLabelPaint());
            Point2D coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, bounds, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
            TextUtils.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
        }
        g2.setComposite(savedComposite);
    }

    /**
     * Draws a marker for the range axis.
     *
     * @param g2  the graphics device (not {@code null}).
     * @param plot  the plot (not {@code null}).
     * @param axis  the range axis (not {@code null}).
     * @param marker  the marker to be drawn (not {@code null}).
     * @param dataArea  the area inside the axes (not {@code null}).
     *
     * @see #drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis,
     *     CategoryMarker, Rectangle2D)
     */
    @Override
    public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
        if (marker instanceof ValueMarker) {
            ValueMarker vm = (ValueMarker) marker;
            double value = vm.getValue();
            Range range = axis.getRange();
            if (!range.contains(value)) {
                return;
            }
            final Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            PlotOrientation orientation = plot.getOrientation();
            double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
            Line2D line = null;
            if (orientation == PlotOrientation.HORIZONTAL) {
                line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
            } else if (orientation == PlotOrientation.VERTICAL) {
                line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
            } else {
                throw new IllegalStateException();
            }
            g2.setPaint(marker.getPaint());
            g2.setStroke(marker.getStroke());
            g2.draw(line);
            String label = marker.getLabel();
            RectangleAnchor anchor = marker.getLabelAnchor();
            if (label != null) {
                Font labelFont = marker.getLabelFont();
                g2.setFont(labelFont);
                Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
                Rectangle2D rect = TextUtils.calcAlignedStringBounds(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
                g2.setPaint(marker.getLabelBackgroundColor());
                g2.fill(rect);
                g2.setPaint(marker.getLabelPaint());
                TextUtils.drawAlignedString(label, g2, (float) coordinates.getX(), (float) coordinates.getY(), marker.getLabelTextAnchor());
            }
            g2.setComposite(savedComposite);
        } else if (marker instanceof IntervalMarker) {
            IntervalMarker im = (IntervalMarker) marker;
            double start = im.getStartValue();
            double end = im.getEndValue();
            Range range = axis.getRange();
            if (!(range.intersects(start, end))) {
                return;
            }
            final Composite savedComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, marker.getAlpha()));
            double start2d = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
            double end2d = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
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
            g2.setComposite(savedComposite);
        }
    }

    /**
     * Calculates the {@code (x, y)} coordinates for drawing the label for a
     * marker on the range axis.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker.
     * @param markerOffset  the marker offset.
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
     * Calculates the (x, y) coordinates for drawing a marker label.
     *
     * @param g2  the graphics device.
     * @param orientation  the plot orientation.
     * @param dataArea  the data area.
     * @param markerArea  the rectangle surrounding the marker.
     * @param markerOffset  the marker offset.
     * @param labelOffsetType  the label offset type.
     * @param anchor  the label anchor.
     *
     * @return The coordinates for drawing the marker label.
     */
    protected Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
        Rectangle2D anchorRect = null;
        if (orientation == PlotOrientation.HORIZONTAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
        } else if (orientation == PlotOrientation.VERTICAL) {
            anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
        }
        return anchor.getAnchorPoint(anchorRect);
    }

    /**
     * Returns a legend item for a series.  This default implementation will
     * return {@code null} if {@link #isSeriesVisible(int)} or
     * {@link #isSeriesVisibleInLegend(int)} returns {@code false}.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item (possibly {@code null}).
     *
     * @see #getLegendItems()
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        CategoryPlot p = getPlot();
        if (p == null) {
            return null;
        }
        // check that a legend item needs to be displayed...
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }
        CategoryDataset dataset = p.getDataset(datasetIndex);
        String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
        String description = label;
        String toolTipText = null;
        if (this.legendItemToolTipGenerator != null) {
            toolTipText = this.legendItemToolTipGenerator.generateLabel(dataset, series);
        }
        String urlText = null;
        if (this.legendItemURLGenerator != null) {
            urlText = this.legendItemURLGenerator.generateLabel(dataset, series);
        }
        Shape shape = lookupLegendShape(series);
        Paint paint = lookupSeriesPaint(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        LegendItem item = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        item.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            item.setLabelPaint(labelPaint);
        }
        item.setSeriesKey(dataset.getRowKey(series));
        item.setSeriesIndex(series);
        item.setDataset(dataset);
        item.setDatasetIndex(datasetIndex);
        return item;
    }

    /**
     * Tests this renderer for equality with another object.
     *
     * @param obj  the object.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AbstractCategoryItemRenderer)) {
            return false;
        }
        AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer) obj;
        if (!Objects.equals(this.itemLabelGeneratorMap, that.itemLabelGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemLabelGenerator, that.defaultItemLabelGenerator)) {
            return false;
        }
        if (!Objects.equals(this.toolTipGeneratorMap, that.toolTipGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultToolTipGenerator, that.defaultToolTipGenerator)) {
            return false;
        }
        if (!Objects.equals(this.itemURLGeneratorMap, that.itemURLGeneratorMap)) {
            return false;
        }
        if (!Objects.equals(this.defaultItemURLGenerator, that.defaultItemURLGenerator)) {
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

    /**
     * Returns a hash code for the renderer.
     *
     * @return The hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
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
        CategoryPlot cp = getPlot();
        if (cp != null) {
            result = cp.getDrawingSupplier();
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
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param value  the data value.
     * @param datasetIndex  the dataset index.
     * @param transX  the x-value translated to Java2D space.
     * @param transY  the y-value translated to Java2D space.
     * @param orientation  the plot orientation ({@code null} not permitted).
     */
    protected void updateCrosshairValues(CategoryCrosshairState crosshairState, Comparable rowKey, Comparable columnKey, double value, int datasetIndex, double transX, double transY, PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        if (crosshairState != null) {
            if (this.plot.isRangeCrosshairLockedOnData()) {
                // both axes
                crosshairState.updateCrosshairPoint(rowKey, columnKey, value, datasetIndex, transX, transY, orientation);
            } else {
                crosshairState.updateCrosshairX(rowKey, columnKey, datasetIndex, transX, orientation);
            }
        }
    }

    /**
     * Draws an item label.
     *
     * @param g2  the graphics device.
     * @param orientation  the orientation.
     * @param dataset  the dataset.
     * @param row  the row.
     * @param column  the column.
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param negative  indicates a negative value (which affects the item
     *                  label position).
     */
    protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, CategoryDataset dataset, int row, int column, double x, double y, boolean negative) {
        CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
        if (generator != null) {
            Font labelFont = getItemLabelFont(row, column);
            Paint paint = getItemLabelPaint(row, column);
            g2.setFont(labelFont);
            g2.setPaint(paint);
            String label = generator.generateLabel(dataset, row, column);
            ItemLabelPosition position;
            if (!negative) {
                position = getPositiveItemLabelPosition(row, column);
            } else {
                position = getNegativeItemLabelPosition(row, column);
            }
            Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
            TextUtils.drawRotatedString(label, g2, (float) anchorPoint.getX(), (float) anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
        }
    }

    /**
     * Returns an independent copy of the renderer.  The {@code plot}
     * reference is shallow copied.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  can be thrown if one of the objects
     *         belonging to the renderer does not support cloning (for example,
     *         an item label generator).
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer) super.clone();
        if (this.itemLabelGeneratorMap != null) {
            clone.itemLabelGeneratorMap = CloneUtils.cloneMapValues(this.itemLabelGeneratorMap);
        }
        if (this.defaultItemLabelGenerator != null) {
            if (this.defaultItemLabelGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.defaultItemLabelGenerator;
                clone.defaultItemLabelGenerator = (CategoryItemLabelGenerator) pc.clone();
            } else {
                throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
            }
        }
        if (this.toolTipGeneratorMap != null) {
            clone.toolTipGeneratorMap = CloneUtils.cloneMapValues(this.toolTipGeneratorMap);
        }
        if (this.defaultToolTipGenerator != null) {
            if (this.defaultToolTipGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.defaultToolTipGenerator;
                clone.defaultToolTipGenerator = (CategoryToolTipGenerator) pc.clone();
            } else {
                throw new CloneNotSupportedException("Default tool tip generator not cloneable.");
            }
        }
        if (this.itemURLGeneratorMap != null) {
            clone.itemURLGeneratorMap = CloneUtils.cloneMapValues(this.itemURLGeneratorMap);
        }
        if (this.defaultItemURLGenerator != null) {
            if (this.defaultItemURLGenerator instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) this.defaultItemURLGenerator;
                clone.defaultItemURLGenerator = (CategoryURLGenerator) pc.clone();
            } else {
                throw new CloneNotSupportedException("Default item URL generator not cloneable.");
            }
        }
        if (this.legendItemLabelGenerator instanceof PublicCloneable) {
            clone.legendItemLabelGenerator = (CategorySeriesLabelGenerator) CloneUtils.clone((Object) this.legendItemLabelGenerator);
        }
        if (this.legendItemToolTipGenerator instanceof PublicCloneable) {
            clone.legendItemToolTipGenerator = (CategorySeriesLabelGenerator) CloneUtils.clone((Object) this.legendItemToolTipGenerator);
        }
        if (this.legendItemURLGenerator instanceof PublicCloneable) {
            clone.legendItemURLGenerator = (CategorySeriesLabelGenerator) CloneUtils.clone((Object) this.legendItemURLGenerator);
        }
        return clone;
    }

    /**
     * Returns a domain axis for a plot.
     *
     * @param plot  the plot.
     * @param index  the axis index.
     *
     * @return A domain axis.
     */
    protected CategoryAxis getDomainAxis(CategoryPlot plot, int index) {
        CategoryAxis result = plot.getDomainAxis(index);
        if (result == null) {
            result = plot.getDomainAxis();
        }
        return result;
    }

    /**
     * Returns a range axis for a plot.
     *
     * @param plot  the plot.
     * @param index  the axis index.
     *
     * @return A range axis.
     */
    protected ValueAxis getRangeAxis(CategoryPlot plot, int index) {
        ValueAxis result = plot.getRangeAxis(index);
        if (result == null) {
            result = plot.getRangeAxis();
        }
        return result;
    }

    /**
     * Returns a (possibly empty) collection of legend items for the series
     * that this renderer is responsible for drawing.
     *
     * @return The legend item collection (never {@code null}).
     *
     * @see #getLegendItem(int, int)
     */
    @Override
    public LegendItemCollection getLegendItems() {
        LegendItemCollection result = new LegendItemCollection();
        if (this.plot == null) {
            return result;
        }
        int index = this.plot.getIndexOf(this);
        CategoryDataset dataset = this.plot.getDataset(index);
        if (dataset == null) {
            return result;
        }
        int seriesCount = dataset.getRowCount();
        if (plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
            for (int i = 0; i < seriesCount; i++) {
                if (isSeriesVisibleInLegend(i)) {
                    LegendItem item = getLegendItem(index, i);
                    if (item != null) {
                        result.add(item);
                    }
                }
            }
        } else {
            for (int i = seriesCount - 1; i >= 0; i--) {
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
     * Returns the legend item label generator.
     *
     * @return The label generator (never {@code null}).
     *
     * @see #setLegendItemLabelGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
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
    public void setLegendItemLabelGenerator(CategorySeriesLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        this.legendItemLabelGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend item tool tip generator.
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
        return this.legendItemToolTipGenerator;
    }

    /**
     * Sets the legend item tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #setLegendItemToolTipGenerator(CategorySeriesLabelGenerator)
     */
    public void setLegendItemToolTipGenerator(CategorySeriesLabelGenerator generator) {
        this.legendItemToolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Returns the legend item URL generator.
     *
     * @return The URL generator (possibly {@code null}).
     *
     * @see #setLegendItemURLGenerator(CategorySeriesLabelGenerator)
     */
    public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
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
    public void setLegendItemURLGenerator(CategorySeriesLabelGenerator generator) {
        this.legendItemURLGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Adds an entity with the specified hotspot.
     *
     * @param entities  the entity collection.
     * @param dataset  the dataset.
     * @param row  the row index.
     * @param column  the column index.
     * @param hotspot  the hotspot ({@code null} not permitted).
     */
    protected void addItemEntity(EntityCollection entities, CategoryDataset dataset, int row, int column, Shape hotspot) {
        Args.nullNotPermitted(hotspot, "hotspot");
        if (!getItemCreateEntity(row, column)) {
            return;
        }
        String tip = null;
        CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
        if (tipster != null) {
            tip = tipster.generateToolTip(dataset, row, column);
        }
        String url = null;
        CategoryURLGenerator urlster = getItemURLGenerator(row, column);
        if (urlster != null) {
            url = urlster.generateURL(dataset, row, column);
        }
        CategoryItemEntity entity = new CategoryItemEntity(hotspot, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column));
        entities.add(entity);
    }

    /**
     * Adds an entity to the collection.
     *
     * @param entities  the entity collection being populated.
     * @param hotspot  the entity area (if {@code null} a default will be
     *              used).
     * @param dataset  the dataset.
     * @param row  the series.
     * @param column  the item.
     * @param entityX  the entity's center x-coordinate in user space (only
     *                 used if {@code area} is {@code null}).
     * @param entityY  the entity's center y-coordinate in user space (only
     *                 used if {@code area} is {@code null}).
     */
    protected void addEntity(EntityCollection entities, Shape hotspot, CategoryDataset dataset, int row, int column, double entityX, double entityY) {
        if (!getItemCreateEntity(row, column)) {
            return;
        }
        Shape s = hotspot;
        if (hotspot == null) {
            double r = getDefaultEntityRadius();
            double w = r * 2;
            if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
                s = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
            } else {
                s = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
            }
        }
        String tip = null;
        CategoryToolTipGenerator generator = getToolTipGenerator(row, column);
        if (generator != null) {
            tip = generator.generateToolTip(dataset, row, column);
        }
        String url = null;
        CategoryURLGenerator urlster = getItemURLGenerator(row, column);
        if (urlster != null) {
            url = urlster.generateURL(dataset, row, column);
        }
        CategoryItemEntity entity = new CategoryItemEntity(s, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column));
        entities.add(entity);
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
 * CyclicNumberAxis.java
 * ---------------------
 * (C) Copyright 2003-2021, by Nicolas Brodu and Contributors.
 *
 * Original Author:  Nicolas Brodu;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * This class extends NumberAxis and handles cycling.
 *
 * Traditional representation of data in the range x0..x1
 * <pre>
 * |-------------------------|
 * x0                       x1
 * </pre>
 *
 * Here, the range bounds are at the axis extremities.
 * With cyclic axis, however, the time is split in
 * "cycles", or "time frames", or the same duration : the period.
 *
 * A cycle axis cannot by definition handle a larger interval
 * than the period : <pre>x1 - x0 &gt;= period</pre>. Thus, at most a full
 * period can be represented with such an axis.
 *
 * The cycle bound is the number between x0 and x1 which marks
 * the beginning of new time frame:
 * <pre>
 * |---------------------|----------------------------|
 * x0                   cb                           x1
 * &lt;---previous cycle---&gt;&lt;-------current cycle--------&gt;
 * </pre>
 *
 * It is actually a multiple of the period, plus optionally
 * a start offset: <pre>cb = n * period + offset</pre>
 *
 * Thus, by definition, two consecutive cycle bounds
 * period apart, which is precisely why it is called a
 * period.
 *
 * The visual representation of a cyclic axis is like that:
 * <pre>
 * |----------------------------|---------------------|
 * cb                         x1|x0                  cb
 * &lt;-------current cycle--------&gt;&lt;---previous cycle---&gt;
 * </pre>
 *
 * The cycle bound is at the axis ends, then current
 * cycle is shown, then the last cycle. When using
 * dynamic data, the visual effect is the current cycle
 * erases the last cycle as x grows. Then, the next cycle
 * bound is reached, and the process starts over, erasing
 * the previous cycle.
 *
 * A Cyclic item renderer is provided to do exactly this.
 */
public class CyclicNumberAxis extends NumberAxis {

    /**
     * For serialization.
     */
    static final long serialVersionUID = -7514160997164582554L;

    /**
     * The default axis line stroke.
     */
    public static Stroke DEFAULT_ADVANCE_LINE_STROKE = new BasicStroke(1.0f);

    /**
     * The default axis line paint.
     */
    public static final Paint DEFAULT_ADVANCE_LINE_PAINT = Color.GRAY;

    /**
     * The offset.
     */
    protected double offset;

    /**
     * The period.
     */
    protected double period;

    /**
     * ??.
     */
    protected boolean boundMappedToLastCycle;

    /**
     * A flag that controls whether the advance line is visible.
     */
    protected boolean advanceLineVisible;

    /**
     * The advance line stroke.
     */
    protected transient Stroke advanceLineStroke = DEFAULT_ADVANCE_LINE_STROKE;

    /**
     * The advance line paint.
     */
    protected transient Paint advanceLinePaint;

    private transient boolean internalMarkerWhenTicksOverlap;

    private transient Tick internalMarkerCycleBoundTick;

    /**
     * Creates a CycleNumberAxis with the given period.
     *
     * @param period  the period.
     */
    public CyclicNumberAxis(double period) {
        this(period, 0.0);
    }

    /**
     * Creates a CycleNumberAxis with the given period and offset.
     *
     * @param period  the period.
     * @param offset  the offset.
     */
    public CyclicNumberAxis(double period, double offset) {
        this(period, offset, null);
    }

    /**
     * Creates a named CycleNumberAxis with the given period.
     *
     * @param period  the period.
     * @param label  the label.
     */
    public CyclicNumberAxis(double period, String label) {
        this(0, period, label);
    }

    /**
     * Creates a named CycleNumberAxis with the given period and offset.
     *
     * @param period  the period.
     * @param offset  the offset.
     * @param label  the label.
     */
    public CyclicNumberAxis(double period, double offset, String label) {
        super(label);
        this.period = period;
        this.offset = offset;
        setFixedAutoRange(period);
        this.advanceLineVisible = true;
        this.advanceLinePaint = DEFAULT_ADVANCE_LINE_PAINT;
    }

    /**
     * The advance line is the line drawn at the limit of the current cycle,
     * when erasing the previous cycle.
     *
     * @return A boolean.
     */
    public boolean isAdvanceLineVisible() {
        return this.advanceLineVisible;
    }

    /**
     * The advance line is the line drawn at the limit of the current cycle,
     * when erasing the previous cycle.
     *
     * @param visible  the flag.
     */
    public void setAdvanceLineVisible(boolean visible) {
        this.advanceLineVisible = visible;
    }

    /**
     * The advance line is the line drawn at the limit of the current cycle,
     * when erasing the previous cycle.
     *
     * @return The paint (never {@code null}).
     */
    public Paint getAdvanceLinePaint() {
        return this.advanceLinePaint;
    }

    /**
     * The advance line is the line drawn at the limit of the current cycle,
     * when erasing the previous cycle.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setAdvanceLinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.advanceLinePaint = paint;
    }

    /**
     * The advance line is the line drawn at the limit of the current cycle,
     * when erasing the previous cycle.
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getAdvanceLineStroke() {
        return this.advanceLineStroke;
    }

    /**
     * The advance line is the line drawn at the limit of the current cycle,
     * when erasing the previous cycle.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     */
    public void setAdvanceLineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.advanceLineStroke = stroke;
    }

    /**
     * The cycle bound can be associated either with the current or with the
     * last cycle.  It's up to the user's choice to decide which, as this is
     * just a convention.  By default, the cycle bound is mapped to the current
     * cycle.
     * <br>
     * Note that this has no effect on visual appearance, as the cycle bound is
     * mapped successively for both axis ends. Use this function for correct
     * results in translateValueToJava2D.
     *
     * @return {@code true} if the cycle bound is mapped to the last
     *         cycle, {@code false} if it is bound to the current cycle
     *         (default)
     */
    public boolean isBoundMappedToLastCycle() {
        return this.boundMappedToLastCycle;
    }

    /**
     * The cycle bound can be associated either with the current or with the
     * last cycle.  It's up to the user's choice to decide which, as this is
     * just a convention. By default, the cycle bound is mapped to the current
     * cycle.
     * <br>
     * Note that this has no effect on visual appearance, as the cycle bound is
     * mapped successively for both axis ends. Use this function for correct
     * results in valueToJava2D.
     *
     * @param boundMappedToLastCycle Set it to true to map the cycle bound to
     *        the last cycle.
     */
    public void setBoundMappedToLastCycle(boolean boundMappedToLastCycle) {
        this.boundMappedToLastCycle = boundMappedToLastCycle;
    }

    /**
     * Selects a tick unit when the axis is displayed horizontally.
     *
     * @param g2  the graphics device.
     * @param drawArea  the drawing area.
     * @param dataArea  the data area.
     * @param edge  the side of the rectangle on which the axis is displayed.
     */
    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        // Compute number of labels
        double n = getRange().getLength() * tickLabelWidth / dataArea.getWidth();
        setTickUnit((NumberTickUnit) getStandardTickUnits().getCeilingTickUnit(n), false, false);
    }

    /**
     * Selects a tick unit when the axis is displayed vertically.
     *
     * @param g2  the graphics device.
     * @param drawArea  the drawing area.
     * @param dataArea  the data area.
     * @param edge  the side of the rectangle on which the axis is displayed.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D drawArea, Rectangle2D dataArea, RectangleEdge edge) {
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        // Compute number of labels
        double n = getRange().getLength() * tickLabelWidth / dataArea.getHeight();
        setTickUnit((NumberTickUnit) getStandardTickUnits().getCeilingTickUnit(n), false, false);
    }

    /**
     * A special Number tick that also hold information about the cycle bound
     * mapping for this tick.  This is especially useful for having a tick at
     * each axis end with the cycle bound value.  See also
     * isBoundMappedToLastCycle()
     */
    protected static class CycleBoundTick extends NumberTick {

        /**
         * Map to last cycle.
         */
        public boolean mapToLastCycle;

        /**
         * Creates a new tick.
         *
         * @param mapToLastCycle  map to last cycle?
         * @param number  the number.
         * @param label  the label.
         * @param textAnchor  the text anchor.
         * @param rotationAnchor  the rotation anchor.
         * @param angle  the rotation angle.
         */
        public CycleBoundTick(boolean mapToLastCycle, Number number, String label, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
            super(number, label, textAnchor, rotationAnchor, angle);
            this.mapToLastCycle = mapToLastCycle;
        }
    }

    /**
     * Calculates the anchor point for a tick.
     *
     * @param tick  the tick.
     * @param cursor  the cursor.
     * @param dataArea  the data area.
     * @param edge  the side on which the axis is displayed.
     *
     * @return The anchor point.
     */
    @Override
    protected float[] calculateAnchorPoint(ValueTick tick, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
        if (tick instanceof CycleBoundTick) {
            boolean mapsav = this.boundMappedToLastCycle;
            this.boundMappedToLastCycle = ((CycleBoundTick) tick).mapToLastCycle;
            float[] ret = super.calculateAnchorPoint(tick, cursor, dataArea, edge);
            this.boundMappedToLastCycle = mapsav;
            return ret;
        }
        return super.calculateAnchorPoint(tick, cursor, dataArea, edge);
    }

    /**
     * Builds a list of ticks for the axis.  This method is called when the
     * axis is at the top or bottom of the chart (so the axis is "horizontal").
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param edge  the edge.
     *
     * @return A list of ticks.
     */
    @Override
    protected List refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new java.util.ArrayList();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        double unit = getTickUnit().getSize();
        double cycleBound = getCycleBound();
        double currentTickValue = Math.ceil(cycleBound / unit) * unit;
        double upperValue = getRange().getUpperBound();
        boolean cycled = false;
        boolean boundMapping = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = false;
        CycleBoundTick lastTick = null;
        float lastX = 0.0f;
        if (upperValue == cycleBound) {
            currentTickValue = calculateLowestVisibleTickValue();
            cycled = true;
            this.boundMappedToLastCycle = true;
        }
        while (currentTickValue <= upperValue) {
            // Cycle when necessary
            boolean cyclenow = false;
            if ((currentTickValue + unit > upperValue) && !cycled) {
                cyclenow = true;
            }
            double xx = valueToJava2D(currentTickValue, dataArea, edge);
            String tickLabel;
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(currentTickValue);
            } else {
                tickLabel = getTickUnit().valueToString(currentTickValue);
            }
            float x = (float) xx;
            TextAnchor anchor;
            TextAnchor rotationAnchor;
            double angle = 0.0;
            if (isVerticalTickLabels()) {
                if (edge == RectangleEdge.TOP) {
                    angle = Math.PI / 2.0;
                } else {
                    angle = -Math.PI / 2.0;
                }
                anchor = TextAnchor.CENTER_RIGHT;
                // If tick overlap when cycling, update last tick too
                if ((lastTick != null) && (lastX == x) && (currentTickValue != cycleBound)) {
                    anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
                    result.remove(result.size() - 1);
                    result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                    this.internalMarkerWhenTicksOverlap = true;
                    anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
                }
                rotationAnchor = anchor;
            } else {
                if (edge == RectangleEdge.TOP) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if ((lastTick != null) && (lastX == x) && (currentTickValue != cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                } else {
                    anchor = TextAnchor.TOP_CENTER;
                    if ((lastTick != null) && (lastX == x) && (currentTickValue != cycleBound)) {
                        anchor = isInverted() ? TextAnchor.TOP_LEFT : TextAnchor.TOP_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.TOP_LEFT;
                    }
                    rotationAnchor = anchor;
                }
            }
            CycleBoundTick tick = new CycleBoundTick(this.boundMappedToLastCycle, currentTickValue, tickLabel, anchor, rotationAnchor, angle);
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            result.add(tick);
            lastTick = tick;
            lastX = x;
            currentTickValue += unit;
            if (cyclenow) {
                currentTickValue = calculateLowestVisibleTickValue();
                upperValue = cycleBound;
                cycled = true;
                this.boundMappedToLastCycle = true;
            }
        }
        this.boundMappedToLastCycle = boundMapping;
        return result;
    }

    /**
     * Builds a list of ticks for the axis.  This method is called when the
     * axis is at the left or right of the chart (so the axis is "vertical").
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param edge  the edge.
     *
     * @return A list of ticks.
     */
    protected List refreshVerticalTicks(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List result = new java.util.ArrayList();
        result.clear();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        double unit = getTickUnit().getSize();
        double cycleBound = getCycleBound();
        double currentTickValue = Math.ceil(cycleBound / unit) * unit;
        double upperValue = getRange().getUpperBound();
        boolean cycled = false;
        boolean boundMapping = this.boundMappedToLastCycle;
        this.boundMappedToLastCycle = true;
        NumberTick lastTick = null;
        float lastY = 0.0f;
        if (upperValue == cycleBound) {
            currentTickValue = calculateLowestVisibleTickValue();
            cycled = true;
            this.boundMappedToLastCycle = true;
        }
        while (currentTickValue <= upperValue) {
            // Cycle when necessary
            boolean cyclenow = false;
            if ((currentTickValue + unit > upperValue) && !cycled) {
                cyclenow = true;
            }
            double yy = valueToJava2D(currentTickValue, dataArea, edge);
            String tickLabel;
            NumberFormat formatter = getNumberFormatOverride();
            if (formatter != null) {
                tickLabel = formatter.format(currentTickValue);
            } else {
                tickLabel = getTickUnit().valueToString(currentTickValue);
            }
            float y = (float) yy;
            TextAnchor anchor;
            TextAnchor rotationAnchor;
            double angle = 0.0;
            if (isVerticalTickLabels()) {
                if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if ((lastTick != null) && (lastY == y) && (currentTickValue != cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                    angle = -Math.PI / 2.0;
                } else {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    if ((lastTick != null) && (lastY == y) && (currentTickValue != cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.BOTTOM_LEFT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.BOTTOM_RIGHT;
                    }
                    rotationAnchor = anchor;
                    angle = Math.PI / 2.0;
                }
            } else {
                if (edge == RectangleEdge.LEFT) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    if ((lastTick != null) && (lastY == y) && (currentTickValue != cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_RIGHT : TextAnchor.TOP_RIGHT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.TOP_RIGHT : TextAnchor.BOTTOM_RIGHT;
                    }
                    rotationAnchor = anchor;
                } else {
                    anchor = TextAnchor.CENTER_LEFT;
                    if ((lastTick != null) && (lastY == y) && (currentTickValue != cycleBound)) {
                        anchor = isInverted() ? TextAnchor.BOTTOM_LEFT : TextAnchor.TOP_LEFT;
                        result.remove(result.size() - 1);
                        result.add(new CycleBoundTick(this.boundMappedToLastCycle, lastTick.getNumber(), lastTick.getText(), anchor, anchor, lastTick.getAngle()));
                        this.internalMarkerWhenTicksOverlap = true;
                        anchor = isInverted() ? TextAnchor.TOP_LEFT : TextAnchor.BOTTOM_LEFT;
                    }
                    rotationAnchor = anchor;
                }
            }
            CycleBoundTick tick = new CycleBoundTick(this.boundMappedToLastCycle, currentTickValue, tickLabel, anchor, rotationAnchor, angle);
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            result.add(tick);
            lastTick = tick;
            lastY = y;
            if (currentTickValue == cycleBound) {
                this.internalMarkerCycleBoundTick = tick;
            }
            currentTickValue += unit;
            if (cyclenow) {
                currentTickValue = calculateLowestVisibleTickValue();
                upperValue = cycleBound;
                cycled = true;
                this.boundMappedToLastCycle = false;
            }
        }
        this.boundMappedToLastCycle = boundMapping;
        return result;
    }

    /**
     * Converts a coordinate from Java 2D space to data space.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param dataArea  the data area.
     * @param edge  the edge.
     *
     * @return The data value.
     */
    @Override
    public double java2DToValue(double java2DValue, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        double vmax = range.getUpperBound();
        double vp = getCycleBound();
        double jmin = 0.0;
        double jmax = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            jmin = dataArea.getMinX();
            jmax = dataArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            jmin = dataArea.getMaxY();
            jmax = dataArea.getMinY();
        }
        if (isInverted()) {
            double jbreak = jmax - (vmax - vp) * (jmax - jmin) / this.period;
            if (java2DValue >= jbreak) {
                return vp + (jmax - java2DValue) * this.period / (jmax - jmin);
            } else {
                return vp - (java2DValue - jmin) * this.period / (jmax - jmin);
            }
        } else {
            double jbreak = (vmax - vp) * (jmax - jmin) / this.period + jmin;
            if (java2DValue <= jbreak) {
                return vp + (java2DValue - jmin) * this.period / (jmax - jmin);
            } else {
                return vp - (jmax - java2DValue) * this.period / (jmax - jmin);
            }
        }
    }

    /**
     * Translates a value from data space to Java 2D space.
     *
     * @param value  the data value.
     * @param dataArea  the data area.
     * @param edge  the edge.
     *
     * @return The Java 2D value.
     */
    @Override
    public double valueToJava2D(double value, Rectangle2D dataArea, RectangleEdge edge) {
        Range range = getRange();
        double vmin = range.getLowerBound();
        double vmax = range.getUpperBound();
        double vp = getCycleBound();
        if ((value < vmin) || (value > vmax)) {
            return Double.NaN;
        }
        double jmin = 0.0;
        double jmax = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            jmin = dataArea.getMinX();
            jmax = dataArea.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            jmax = dataArea.getMinY();
            jmin = dataArea.getMaxY();
        }
        if (isInverted()) {
            if (value == vp) {
                return this.boundMappedToLastCycle ? jmin : jmax;
            } else if (value > vp) {
                return jmax - (value - vp) * (jmax - jmin) / this.period;
            } else {
                return jmin + (vp - value) * (jmax - jmin) / this.period;
            }
        } else {
            if (value == vp) {
                return this.boundMappedToLastCycle ? jmax : jmin;
            } else if (value >= vp) {
                return jmin + (value - vp) * (jmax - jmin) / this.period;
            } else {
                return jmax - (vp - value) * (jmax - jmin) / this.period;
            }
        }
    }

    /**
     * Centers the range about the given value.
     *
     * @param value  the data value.
     */
    @Override
    public void centerRange(double value) {
        setRange(value - this.period / 2.0, value + this.period / 2.0);
    }

    /**
     * This function is nearly useless since the auto range is fixed for this
     * class to the period.  The period is extended if necessary to fit the
     * minimum size.
     *
     * @param size  the size.
     * @param notify  notify?
     *
     * @see org.jfree.chart.axis.ValueAxis#setAutoRangeMinimumSize(double,
     *      boolean)
     */
    @Override
    public void setAutoRangeMinimumSize(double size, boolean notify) {
        if (size > this.period) {
            this.period = size;
        }
        super.setAutoRangeMinimumSize(size, notify);
    }

    /**
     * The auto range is fixed for this class to the period by default.
     * This function will thus set a new period.
     *
     * @param length  the length.
     *
     * @see org.jfree.chart.axis.ValueAxis#setFixedAutoRange(double)
     */
    @Override
    public void setFixedAutoRange(double length) {
        this.period = length;
        super.setFixedAutoRange(length);
    }

    /**
     * Sets a new axis range. The period is extended to fit the range size, if
     * necessary.
     *
     * @param range  the range.
     * @param turnOffAutoRange  switch off the auto range.
     * @param notify notify?
     *
     * @see org.jfree.chart.axis.ValueAxis#setRange(Range, boolean, boolean)
     */
    @Override
    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        double size = range.getUpperBound() - range.getLowerBound();
        if (size > this.period) {
            this.period = size;
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    /**
     * The cycle bound is defined as the higest value x such that
     * "offset + period * i = x", with i and integer and x &lt;
     * range.getUpperBound() This is the value which is at both ends of the
     * axis :  x...up|low...x
     * The values from x to up are the valued in the current cycle.
     * The values from low to x are the valued in the previous cycle.
     *
     * @return The cycle bound.
     */
    public double getCycleBound() {
        return Math.floor((getRange().getUpperBound() - this.offset) / this.period) * this.period + this.offset;
    }

    /**
     * The cycle bound is a multiple of the period, plus optionally a start
     * offset.
     * <pre>cb = n * period + offset</pre>
     *
     * @return The current offset.
     *
     * @see #getCycleBound()
     */
    public double getOffset() {
        return this.offset;
    }

    /**
     * The cycle bound is a multiple of the period, plus optionally a start
     * offset.
     * <pre>cb = n * period + offset</pre>
     *
     * @param offset The offset to set.
     *
     * @see #getCycleBound()
     */
    public void setOffset(double offset) {
        this.offset = offset;
    }

    /**
     * The cycle bound is a multiple of the period, plus optionally a start
     * offset.
     * <pre>cb = n * period + offset</pre>
     *
     * @return The current period.
     *
     * @see #getCycleBound()
     */
    public double getPeriod() {
        return this.period;
    }

    /**
     * The cycle bound is a multiple of the period, plus optionally a start
     * offset.
     * <pre>cb = n * period + offset</pre>
     *
     * @param period The period to set.
     *
     * @see #getCycleBound()
     */
    public void setPeriod(double period) {
        this.period = period;
    }

    /**
     * Draws the tick marks and labels.
     *
     * @param g2  the graphics device.
     * @param cursor  the cursor.
     * @param plotArea  the plot area.
     * @param dataArea  the area inside the axes.
     * @param edge  the side on which the axis is displayed.
     *
     * @return The axis state.
     */
    @Override
    protected AxisState drawTickMarksAndLabels(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge) {
        this.internalMarkerWhenTicksOverlap = false;
        AxisState ret = super.drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        // continue and separate the labels only if necessary
        if (!this.internalMarkerWhenTicksOverlap) {
            return ret;
        }
        double ol;
        FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
        if (isVerticalTickLabels()) {
            ol = fm.getMaxAdvance();
        } else {
            ol = fm.getHeight();
        }
        double il = 0;
        if (isTickMarksVisible()) {
            float xx = (float) valueToJava2D(getRange().getUpperBound(), dataArea, edge);
            Line2D mark = null;
            g2.setStroke(getTickMarkStroke());
            g2.setPaint(getTickMarkPaint());
            if (edge == RectangleEdge.LEFT) {
                mark = new Line2D.Double(cursor - ol, xx, cursor + il, xx);
            } else if (edge == RectangleEdge.RIGHT) {
                mark = new Line2D.Double(cursor + ol, xx, cursor - il, xx);
            } else if (edge == RectangleEdge.TOP) {
                mark = new Line2D.Double(xx, cursor - ol, xx, cursor + il);
            } else if (edge == RectangleEdge.BOTTOM) {
                mark = new Line2D.Double(xx, cursor + ol, xx, cursor - il);
            }
            g2.draw(mark);
        }
        return ret;
    }

    /**
     * Draws the axis.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor position.
     * @param plotArea  the plot area ({@code null} not permitted).
     * @param dataArea  the data area ({@code null} not permitted).
     * @param edge  the edge ({@code null} not permitted).
     * @param plotState  collects information about the plot
     *                   ({@code null} permitted).
     *
     * @return The axis state (never {@code null}).
     */
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState ret = super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
        if (isAdvanceLineVisible()) {
            double xx = valueToJava2D(getRange().getUpperBound(), dataArea, edge);
            Line2D mark = null;
            g2.setStroke(getAdvanceLineStroke());
            g2.setPaint(getAdvanceLinePaint());
            if (edge == RectangleEdge.LEFT) {
                mark = new Line2D.Double(cursor, xx, cursor + dataArea.getWidth(), xx);
            } else if (edge == RectangleEdge.RIGHT) {
                mark = new Line2D.Double(cursor - dataArea.getWidth(), xx, cursor, xx);
            } else if (edge == RectangleEdge.TOP) {
                mark = new Line2D.Double(xx, cursor + dataArea.getHeight(), xx, cursor);
            } else if (edge == RectangleEdge.BOTTOM) {
                mark = new Line2D.Double(xx, cursor, xx, cursor - dataArea.getHeight());
            }
            g2.draw(mark);
        }
        return ret;
    }

    /**
     * Reserve some space on each axis side because we draw a centered label at
     * each extremity.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param plotArea  the plot area.
     * @param edge  the edge.
     * @param space  the space already reserved.
     *
     * @return The reserved space.
     */
    @Override
    public AxisSpace reserveSpace(Graphics2D g2, Plot plot, Rectangle2D plotArea, RectangleEdge edge, AxisSpace space) {
        this.internalMarkerCycleBoundTick = null;
        AxisSpace ret = super.reserveSpace(g2, plot, plotArea, edge, space);
        if (this.internalMarkerCycleBoundTick == null) {
            return ret;
        }
        FontMetrics fm = g2.getFontMetrics(getTickLabelFont());
        Rectangle2D r = TextUtils.getTextBounds(this.internalMarkerCycleBoundTick.getText(), g2, fm);
        if (RectangleEdge.isTopOrBottom(edge)) {
            if (isVerticalTickLabels()) {
                space.add(r.getHeight() / 2, RectangleEdge.RIGHT);
            } else {
                space.add(r.getWidth() / 2, RectangleEdge.RIGHT);
            }
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            if (isVerticalTickLabels()) {
                space.add(r.getWidth() / 2, RectangleEdge.TOP);
            } else {
                space.add(r.getHeight() / 2, RectangleEdge.TOP);
            }
        }
        return ret;
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
        SerialUtils.writePaint(this.advanceLinePaint, stream);
        SerialUtils.writeStroke(this.advanceLineStroke, stream);
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
        this.advanceLinePaint = SerialUtils.readPaint(stream);
        this.advanceLineStroke = SerialUtils.readStroke(stream);
    }

    /**
     * Tests the axis for equality with another object.
     *
     * @param obj  the object to test against.
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CyclicNumberAxis)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        CyclicNumberAxis that = (CyclicNumberAxis) obj;
        if (this.period != that.period) {
            return false;
        }
        if (this.offset != that.offset) {
            return false;
        }
        if (!PaintUtils.equal(this.advanceLinePaint, that.advanceLinePaint)) {
            return false;
        }
        if (!Objects.equals(this.advanceLineStroke, that.advanceLineStroke)) {
            return false;
        }
        if (this.advanceLineVisible != that.advanceLineVisible) {
            return false;
        }
        if (this.boundMappedToLastCycle != that.boundMappedToLastCycle) {
            return false;
        }
        return true;
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
 * -------------------
 * XYAreaRenderer.java
 * -------------------
 * (C) Copyright 2002-2021, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert;
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *                   Martin Krauskopf;
 *                   Ulrich Voigt (patch #312);
 */
/**
 * Area item renderer for an {@link XYPlot}.  This class can draw (a) shapes at
 * each point, or (b) lines between points, or (c) both shapes and lines,
 * or (d) filled areas, or (e) filled areas and shapes. The example shown here
 * is generated by the {@code XYAreaRendererDemo1.java} program included
 * in the JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/XYAreaRendererSample.png" alt="XYAreaRendererSample.png">
 */
public class XYAreaRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -4481971353973876747L;

    /**
     * A state object used by this renderer.
     */
    static class XYAreaRendererState extends XYItemRendererState {

        /**
         * Working storage for the area under one series.
         */
        public GeneralPath area;

        /**
         * Working line that can be recycled.
         */
        public Line2D line;

        /**
         * Creates a new state.
         *
         * @param info  the plot rendering info.
         */
        public XYAreaRendererState(PlotRenderingInfo info) {
            super(info);
            this.area = new GeneralPath();
            this.line = new Line2D.Double();
        }
    }

    /**
     * Useful constant for specifying the type of rendering (shapes only).
     */
    public static final int SHAPES = 1;

    /**
     * Useful constant for specifying the type of rendering (lines only).
     */
    public static final int LINES = 2;

    /**
     * Useful constant for specifying the type of rendering (shapes and lines).
     */
    public static final int SHAPES_AND_LINES = 3;

    /**
     * Useful constant for specifying the type of rendering (area only).
     */
    public static final int AREA = 4;

    /**
     * Useful constant for specifying the type of rendering (area and shapes).
     */
    public static final int AREA_AND_SHAPES = 5;

    /**
     * A flag indicating whether shapes are drawn at each XY point.
     */
    private boolean plotShapes;

    /**
     * A flag indicating whether lines are drawn between XY points.
     */
    private boolean plotLines;

    /**
     * A flag indicating whether Area are drawn at each XY point.
     */
    private boolean plotArea;

    /**
     * A flag that controls whether the outline is shown.
     */
    private boolean showOutline;

    /**
     * The shape used to represent an area in each legend item (this should
     * never be {@code null}).
     */
    private transient Shape legendArea;

    /**
     * A flag that can be set to specify that the fill paint should be used
     * to fill the area under the renderer.
     */
    private boolean useFillPaint;

    /**
     * A transformer that is applied to the paint used to fill under the
     * area *if* it is an instance of GradientPaint.
     */
    private GradientPaintTransformer gradientTransformer;

    /**
     * Constructs a new renderer.
     */
    public XYAreaRenderer() {
        this(AREA);
    }

    /**
     * Constructs a new renderer.
     *
     * @param type  the type of the renderer.
     */
    public XYAreaRenderer(int type) {
        this(type, null, null);
    }

    /**
     * Constructs a new renderer.  To specify the type of renderer, use one of
     * the constants: {@code SHAPES}, {@code LINES}, {@code SHAPES_AND_LINES},
     * {@code AREA} or {@code AREA_AND_SHAPES}.
     *
     * @param type  the type of renderer.
     * @param toolTipGenerator  the tool tip generator ({@code null} permitted).
     * @param urlGenerator  the URL generator ({@code null} permitted).
     */
    public XYAreaRenderer(int type, XYToolTipGenerator toolTipGenerator, XYURLGenerator urlGenerator) {
        super();
        setDefaultToolTipGenerator(toolTipGenerator);
        setURLGenerator(urlGenerator);
        if (type == SHAPES) {
            this.plotShapes = true;
        }
        if (type == LINES) {
            this.plotLines = true;
        }
        if (type == SHAPES_AND_LINES) {
            this.plotShapes = true;
            this.plotLines = true;
        }
        if (type == AREA) {
            this.plotArea = true;
        }
        if (type == AREA_AND_SHAPES) {
            this.plotArea = true;
            this.plotShapes = true;
        }
        this.showOutline = false;
        GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(3.0f, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
        this.useFillPaint = false;
        this.gradientTransformer = new StandardGradientPaintTransformer();
    }

    /**
     * Returns true if shapes are being plotted by the renderer.
     *
     * @return {@code true} if shapes are being plotted by the renderer.
     */
    public boolean getPlotShapes() {
        return this.plotShapes;
    }

    /**
     * Returns true if lines are being plotted by the renderer.
     *
     * @return {@code true} if lines are being plotted by the renderer.
     */
    public boolean getPlotLines() {
        return this.plotLines;
    }

    /**
     * Returns true if Area is being plotted by the renderer.
     *
     * @return {@code true} if Area is being plotted by the renderer.
     */
    public boolean getPlotArea() {
        return this.plotArea;
    }

    /**
     * Returns a flag that controls whether outlines of the areas are
     * drawn.
     *
     * @return The flag.
     *
     * @see #setOutline(boolean)
     */
    public boolean isOutline() {
        return this.showOutline;
    }

    /**
     * Sets a flag that controls whether outlines of the areas are drawn
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param show  the flag.
     *
     * @see #isOutline()
     */
    public void setOutline(boolean show) {
        this.showOutline = show;
        fireChangeEvent();
    }

    /**
     * Returns the shape used to represent an area in the legend.
     *
     * @return The legend area (never {@code null}).
     */
    public Shape getLegendArea() {
        return this.legendArea;
    }

    /**
     * Sets the shape used as an area in each legend item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param area  the area ({@code null} not permitted).
     */
    public void setLegendArea(Shape area) {
        Args.nullNotPermitted(area, "area");
        this.legendArea = area;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the series fill paint is used to
     * fill the area under the line.
     *
     * @return A boolean.
     */
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    /**
     * Sets the flag that controls whether the series fill paint is
     * used to fill the area under the line and sends a
     * {@link RendererChangeEvent} to all listeners.
     *
     * @param use  the new flag value.
     */
    public void setUseFillPaint(boolean use) {
        this.useFillPaint = use;
        fireChangeEvent();
    }

    /**
     * Returns the gradient paint transformer.
     *
     * @return The gradient paint transformer (never {@code null}).
     */
    public GradientPaintTransformer getGradientTransformer() {
        return this.gradientTransformer;
    }

    /**
     * Sets the gradient paint transformer and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param transformer  the transformer ({@code null} not permitted).
     */
    public void setGradientTransformer(GradientPaintTransformer transformer) {
        Args.nullNotPermitted(transformer, "transformer");
        this.gradientTransformer = transformer;
        fireChangeEvent();
    }

    /**
     * Initialises the renderer and returns a state object that should be
     * passed to all subsequent calls to the drawItem() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return A state object for use by the renderer.
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYAreaRendererState state = new XYAreaRendererState(info);
        // in the rendering process, there is special handling for item
        // zero, so we can't support processing of visible data items only
        state.setProcessVisibleItemsOnly(false);
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
        LegendItem result = null;
        XYPlot xyplot = getPlot();
        if (xyplot != null) {
            XYDataset dataset = xyplot.getDataset(datasetIndex);
            if (dataset != null) {
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
                Paint paint = lookupSeriesPaint(series);
                result = new LegendItem(label, description, toolTipText, urlText, this.legendArea, paint);
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
        if (!getItemVisible(series, item)) {
            return;
        }
        XYAreaRendererState areaState = (XYAreaRendererState) state;
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1)) {
            y1 = 0.0;
        }
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, plot.getRangeAxisEdge());
        // get the previous point and the next point so we can calculate a
        // "hot spot" for the area (used by the chart entity)...
        int itemCount = dataset.getItemCount(series);
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
        double x2 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
        double transY2 = rangeAxis.valueToJava2D(y2, dataArea, plot.getRangeAxisEdge());
        double transZero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
        if (item == 0) {
            // create a new area polygon for the series
            areaState.area = new GeneralPath();
            // the first point is (x, 0)
            double zero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
            if (plot.getOrientation().isVertical()) {
                moveTo(areaState.area, transX1, zero);
            } else if (plot.getOrientation().isHorizontal()) {
                moveTo(areaState.area, zero, transX1);
            }
        }
        // Add each point to Area (x, y)
        if (plot.getOrientation().isVertical()) {
            lineTo(areaState.area, transX1, transY1);
        } else if (plot.getOrientation().isHorizontal()) {
            lineTo(areaState.area, transY1, transX1);
        }
        PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        Stroke stroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);
        Shape shape;
        if (getPlotShapes()) {
            shape = getItemShape(series, item);
            if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transX1, transY1);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transY1, transX1);
            }
            g2.draw(shape);
        }
        if (getPlotLines()) {
            if (item > 0) {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    areaState.line.setLine(transX0, transY0, transX1, transY1);
                } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    areaState.line.setLine(transY0, transX0, transY1, transX1);
                }
                g2.draw(areaState.line);
            }
        }
        // Check if the item is the last item for the series.
        // and number of items > 0.  We can't draw an area for a single point.
        if (getPlotArea() && item > 0 && item == (itemCount - 1)) {
            if (orientation == PlotOrientation.VERTICAL) {
                // Add the last point (x,0)
                lineTo(areaState.area, transX1, transZero);
                areaState.area.closePath();
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                // Add the last point (x,0)
                lineTo(areaState.area, transZero, transX1);
                areaState.area.closePath();
            }
            if (this.useFillPaint) {
                paint = lookupSeriesFillPaint(series);
                g2.setPaint(paint);
            }
            if (paint instanceof GradientPaint) {
                GradientPaint gp = (GradientPaint) paint;
                GradientPaint adjGP = this.gradientTransformer.transform(gp, dataArea);
                g2.setPaint(adjGP);
            }
            g2.fill(areaState.area);
            // draw an outline around the Area.
            if (isOutline()) {
                Shape area = areaState.area;
                // Java2D has some issues drawing dashed lines around "large"
                // geometrical shapes - for example, see bug 6620013 in the
                // Java bug database.  So, we'll check if the outline is
                // dashed and, if it is, do our own clipping before drawing
                // the outline...
                Stroke outlineStroke = lookupSeriesOutlineStroke(series);
                if (outlineStroke instanceof BasicStroke) {
                    BasicStroke bs = (BasicStroke) outlineStroke;
                    if (bs.getDashArray() != null) {
                        Area poly = new Area(areaState.area);
                        // we make the clip region slightly larger than the
                        // dataArea so that the clipped edges don't show lines
                        // on the chart
                        Area clip = new Area(new Rectangle2D.Double(dataArea.getX() - 5.0, dataArea.getY() - 5.0, dataArea.getWidth() + 10.0, dataArea.getHeight() + 10.0));
                        poly.intersect(clip);
                        area = poly;
                    }
                }
                // end of workaround
                g2.setStroke(outlineStroke);
                g2.setPaint(lookupSeriesOutlinePaint(series));
                g2.draw(area);
            }
        }
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(crosshairState, x1, y1, datasetIndex, transX1, transY1, orientation);
        // collect entity and tool tip information...
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            GeneralPath hotspot = new GeneralPath();
            if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                moveTo(hotspot, transZero, ((transX0 + transX1) / 2.0));
                lineTo(hotspot, ((transY0 + transY1) / 2.0), ((transX0 + transX1) / 2.0));
                lineTo(hotspot, transY1, transX1);
                lineTo(hotspot, ((transY1 + transY2) / 2.0), ((transX1 + transX2) / 2.0));
                lineTo(hotspot, transZero, ((transX1 + transX2) / 2.0));
            } else {
                // vertical orientation
                moveTo(hotspot, ((transX0 + transX1) / 2.0), transZero);
                lineTo(hotspot, ((transX0 + transX1) / 2.0), ((transY0 + transY1) / 2.0));
                lineTo(hotspot, transX1, transY1);
                lineTo(hotspot, ((transX1 + transX2) / 2.0), ((transY1 + transY2) / 2.0));
                lineTo(hotspot, ((transX1 + transX2) / 2.0), transZero);
            }
            hotspot.closePath();
            // limit the entity hotspot area to the data area
            Area dataAreaHotspot = new Area(hotspot);
            dataAreaHotspot.intersect(new Area(dataArea));
            if (dataAreaHotspot.isEmpty() == false) {
                addEntity(entities, dataAreaHotspot, dataset, series, item, 0.0, 0.0);
            }
        }
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
        XYAreaRenderer clone = (XYAreaRenderer) super.clone();
        clone.legendArea = CloneUtils.clone(this.legendArea);
        return clone;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
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
        if (!(obj instanceof XYAreaRenderer)) {
            return false;
        }
        XYAreaRenderer that = (XYAreaRenderer) obj;
        if (this.plotArea != that.plotArea) {
            return false;
        }
        if (this.plotLines != that.plotLines) {
            return false;
        }
        if (this.plotShapes != that.plotShapes) {
            return false;
        }
        if (this.showOutline != that.showOutline) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (!this.gradientTransformer.equals(that.gradientTransformer)) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendArea, that.legendArea)) {
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
        int result = super.hashCode();
        result = HashUtils.hashCode(result, this.plotArea);
        result = HashUtils.hashCode(result, this.plotLines);
        result = HashUtils.hashCode(result, this.plotShapes);
        result = HashUtils.hashCode(result, this.useFillPaint);
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
        this.legendArea = SerialUtils.readShape(stream);
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
        SerialUtils.writeShape(this.legendArea, stream);
    }
}
