package NOD.ap;
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
 * BoxAndWhiskerRenderer.java
 * --------------------------
 * (C) Copyright 2003-present, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for the Australian Institute of Marine
 *                   Science);
 * Contributor(s):   David Gilbert;
 *                   Tim Bardzil;
 *                   Rob Van der Sanden (patches 1866446 and 1888422);
 *                   Peter Becker (patches 2868585 and 2868608);
 *                   Martin Krauskopf (patch 3421088);
 *                   Martin Hoeller;
 *                   John Matthews;
 *
 */
/**
 * A box-and-whisker renderer.  This renderer requires a
 * {@link BoxAndWhiskerCategoryDataset} and is for use with the
 * {@link CategoryPlot} class.  The example shown here is generated
 * by the {@code BoxAndWhiskerChartDemo1.java} program included in the
 * JFreeChart Demo Collection:
 * <br><br>
 * <img src="doc-files/BoxAndWhiskerRendererSample.png"
 * alt="BoxAndWhiskerRendererSample.png">
 */
public class BoxAndWhiskerRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 632027470694481177L;

    /**
     * The color used to paint the median line and average marker.
     */
    private transient Paint artifactPaint;

    /**
     * A flag that controls whether the box is filled.
     */
    private boolean fillBox;

    /**
     * The margin between items (boxes) within a category.
     */
    private double itemMargin;

    /**
     * The maximum bar width as percentage of the available space in the plot.
     * Take care with the encoding - for example, 0.05 is five percent.
     */
    private double maximumBarWidth;

    /**
     * A flag that controls whether the median indicator is drawn.
     */
    private boolean medianVisible;

    /**
     * A flag that controls whether the mean indicator is drawn.
     */
    private boolean meanVisible;

    /**
     * A flag that controls whether the maxOutlier is visible.
     */
    private boolean maxOutlierVisible;

    /**
     * A flag that controls whether the minOutlier is visible.
     */
    private boolean minOutlierVisible;

    /**
     * A flag that, if {@code true}, causes the whiskers to be drawn
     * using the outline paint for the series.  The default value is
     * {@code false} and in that case the regular series paint is used.
     */
    private boolean useOutlinePaintForWhiskers;

    /**
     * The width of the whiskers as fraction of the bar width.
     */
    private double whiskerWidth;

    /**
     * Default constructor.
     */
    public BoxAndWhiskerRenderer() {
        this.artifactPaint = Color.BLACK;
        this.fillBox = true;
        this.itemMargin = 0.20;
        this.maximumBarWidth = 1.0;
        this.medianVisible = true;
        this.meanVisible = true;
        this.minOutlierVisible = true;
        this.maxOutlierVisible = true;
        this.useOutlinePaintForWhiskers = false;
        this.whiskerWidth = 1.0;
        setDefaultLegendShape(new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0));
    }

    /**
     * Returns the paint used to color the median and average markers.
     *
     * @return The paint used to draw the median and average markers (never
     *     {@code null}).
     *
     * @see #setArtifactPaint(Paint)
     */
    public Paint getArtifactPaint() {
        return this.artifactPaint;
    }

    /**
     * Sets the paint used to color the median and average markers and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getArtifactPaint()
     */
    public void setArtifactPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.artifactPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the box is filled.
     *
     * @return A boolean.
     *
     * @see #setFillBox(boolean)
     */
    public boolean getFillBox() {
        return this.fillBox;
    }

    /**
     * Sets the flag that controls whether the box is filled and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getFillBox()
     */
    public void setFillBox(boolean flag) {
        this.fillBox = flag;
        fireChangeEvent();
    }

    /**
     * Returns the item margin.  This is a percentage of the available space
     * that is allocated to the space between items in the chart.
     *
     * @return The margin.
     *
     * @see #setItemMargin(double)
     */
    public double getItemMargin() {
        return this.itemMargin;
    }

    /**
     * Sets the item margin and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param margin  the margin (a percentage).
     *
     * @see #getItemMargin()
     */
    public void setItemMargin(double margin) {
        this.itemMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the maximum bar width as a percentage of the available drawing
     * space.  Take care with the encoding, for example 0.10 is ten percent.
     *
     * @return The maximum bar width.
     *
     * @see #setMaximumBarWidth(double)
     */
    public double getMaximumBarWidth() {
        return this.maximumBarWidth;
    }

    /**
     * Sets the maximum bar width, which is specified as a percentage of the
     * available space for all bars, and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param percent  the maximum bar width (a percentage, where 0.10 is ten
     *     percent).
     *
     * @see #getMaximumBarWidth()
     */
    public void setMaximumBarWidth(double percent) {
        this.maximumBarWidth = percent;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the mean indicator is
     * draw for each item.
     *
     * @return A boolean.
     *
     * @see #setMeanVisible(boolean)
     */
    public boolean isMeanVisible() {
        return this.meanVisible;
    }

    /**
     * Sets the flag that controls whether the mean indicator is drawn
     * for each item, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isMeanVisible()
     */
    public void setMeanVisible(boolean visible) {
        if (this.meanVisible == visible) {
            return;
        }
        this.meanVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the median indicator is
     * draw for each item.
     *
     * @return A boolean.
     *
     * @see #setMedianVisible(boolean)
     */
    public boolean isMedianVisible() {
        return this.medianVisible;
    }

    /**
     * Sets the flag that controls whether the median indicator is drawn
     * for each item, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isMedianVisible()
     */
    public void setMedianVisible(boolean visible) {
        if (this.medianVisible == visible) {
            return;
        }
        this.medianVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the minimum outlier is
     * draw for each item.
     *
     * @return A boolean.
     *
     * @see #setMinOutlierVisible(boolean)
     *
     * @since 1.5.2
     */
    public boolean isMinOutlierVisible() {
        return this.minOutlierVisible;
    }

    /**
     * Sets the flag that controls whether the minimum outlier is drawn
     * for each item, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isMinOutlierVisible()
     *
     * @since 1.5.2
     */
    public void setMinOutlierVisible(boolean visible) {
        if (this.minOutlierVisible == visible) {
            return;
        }
        this.minOutlierVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the maximum outlier is
     * draw for each item.
     *
     * @return A boolean.
     *
     * @see #setMaxOutlierVisible(boolean)
     *
     * @since 1.5.2
     */
    public boolean isMaxOutlierVisible() {
        return this.maxOutlierVisible;
    }

    /**
     * Sets the flag that controls whether the maximum outlier is drawn
     * for each item, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isMaxOutlierVisible()
     *
     * @since 1.5.2
     */
    public void setMaxOutlierVisible(boolean visible) {
        if (this.maxOutlierVisible == visible) {
            return;
        }
        this.maxOutlierVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the flag that, if {@code true}, causes the whiskers to
     * be drawn using the series outline paint.
     *
     * @return A boolean.
     */
    public boolean getUseOutlinePaintForWhiskers() {
        return this.useOutlinePaintForWhiskers;
    }

    /**
     * Sets the flag that, if {@code true}, causes the whiskers to
     * be drawn using the series outline paint, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the new flag value.
     */
    public void setUseOutlinePaintForWhiskers(boolean flag) {
        if (this.useOutlinePaintForWhiskers == flag) {
            return;
        }
        this.useOutlinePaintForWhiskers = flag;
        fireChangeEvent();
    }

    /**
     * Returns the width of the whiskers as fraction of the bar width.
     *
     * @return The width of the whiskers.
     *
     * @see #setWhiskerWidth(double)
     */
    public double getWhiskerWidth() {
        return this.whiskerWidth;
    }

    /**
     * Sets the width of the whiskers as a fraction of the bar width and sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param width  a value between 0 and 1 indicating how wide the
     *     whisker is supposed to be compared to the bar.
     * @see #getWhiskerWidth()
     * @see CategoryItemRendererState#getBarWidth()
     */
    public void setWhiskerWidth(double width) {
        if (width < 0 || width > 1) {
            throw new IllegalArgumentException("Value for whisker width out of range");
        }
        if (width == this.whiskerWidth) {
            return;
        }
        this.whiskerWidth = width;
        fireChangeEvent();
    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item (possibly {@code null}).
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }
        // check that a legend item needs to be displayed...
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }
        CategoryDataset dataset = cp.getDataset(datasetIndex);
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
        Paint paint = lookupSeriesPaint(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getRowKey(series));
        result.setSeriesIndex(series);
        return result;
    }

    /**
     * Returns the range of values from the specified dataset that the
     * renderer will require to display all the data.
     *
     * @param dataset  the dataset.
     *
     * @return The range.
     */
    @Override
    public Range findRangeBounds(CategoryDataset dataset) {
        return super.findRangeBounds(dataset, true);
    }

    /**
     * Initialises the renderer.  This method gets called once at the start of
     * the process of drawing a chart.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be plotted.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  collects chart rendering information for return to caller.
     *
     * @return The renderer state.
     */
    @Override
    public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
        CategoryItemRendererState state = super.initialise(g2, dataArea, plot, rendererIndex, info);
        // calculate the box width
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
            double currentItemMargin = 0.0;
            if (columns > 1) {
                categoryMargin = domainAxis.getCategoryMargin();
            }
            if (rows > 1) {
                currentItemMargin = getItemMargin();
            }
            double used = space * (1 - domainAxis.getLowerMargin() - domainAxis.getUpperMargin() - categoryMargin - currentItemMargin);
            if ((rows * columns) > 0) {
                state.setBarWidth(Math.min(used / (dataset.getColumnCount() * dataset.getRowCount()), maxWidth));
            } else {
                state.setBarWidth(Math.min(used, maxWidth));
            }
        }
        return state;
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
     * @param dataset  the data (must be an instance of
     *                 {@link BoxAndWhiskerCategoryDataset}).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        // do nothing if item is not visible
        if (!getItemVisible(row, column)) {
            return;
        }
        if (!(dataset instanceof BoxAndWhiskerCategoryDataset)) {
            throw new IllegalArgumentException("BoxAndWhiskerRenderer.drawItem() : the data should be " + "of type BoxAndWhiskerCategoryDataset only.");
        }
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        } else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, dataset, row, column);
        }
    }

    /**
     * Draws the visual representation of a single data item when the plot has
     * a horizontal orientation.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset (must be an instance of
     *                 {@link BoxAndWhiskerCategoryDataset}).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawHorizontalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;
        double categoryEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryWidth = Math.abs(categoryEnd - categoryStart);
        double yy = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = dataArea.getHeight() * getItemMargin() / (categoryCount * (seriesCount - 1));
            double usedWidth = (state.getBarWidth() * seriesCount) + (seriesGap * (seriesCount - 1));
            // offset the start of the boxes if the total width used is smaller
            // than the category width
            double offset = (categoryWidth - usedWidth) / 2;
            yy = yy + offset + (row * (state.getBarWidth() + seriesGap));
        } else {
            // offset the start of the box if the box width is smaller than
            // the category width
            double offset = (categoryWidth - state.getBarWidth()) / 2;
            yy = yy + offset;
        }
        g2.setPaint(getItemPaint(row, column));
        Stroke s = getItemStroke(row, column);
        g2.setStroke(s);
        RectangleEdge location = plot.getRangeAxisEdge();
        Number xQ1 = bawDataset.getQ1Value(row, column);
        Number xQ3 = bawDataset.getQ3Value(row, column);
        Number xMax = bawDataset.getMaxRegularValue(row, column);
        Number xMin = bawDataset.getMinRegularValue(row, column);
        Shape box = null;
        if (xQ1 != null && xQ3 != null && xMax != null && xMin != null) {
            double xxQ1 = rangeAxis.valueToJava2D(xQ1.doubleValue(), dataArea, location);
            double xxQ3 = rangeAxis.valueToJava2D(xQ3.doubleValue(), dataArea, location);
            double xxMax = rangeAxis.valueToJava2D(xMax.doubleValue(), dataArea, location);
            double xxMin = rangeAxis.valueToJava2D(xMin.doubleValue(), dataArea, location);
            double yymid = yy + state.getBarWidth() / 2.0;
            double halfW = (state.getBarWidth() / 2.0) * this.whiskerWidth;
            // draw the box...
            box = new Rectangle2D.Double(Math.min(xxQ1, xxQ3), yy, Math.abs(xxQ1 - xxQ3), state.getBarWidth());
            if (this.fillBox) {
                g2.fill(box);
            }
            Paint outlinePaint = getItemOutlinePaint(row, column);
            if (this.useOutlinePaintForWhiskers) {
                g2.setPaint(outlinePaint);
            }
            // draw the upper shadow...
            g2.draw(new Line2D.Double(xxMax, yymid, xxQ3, yymid));
            g2.draw(new Line2D.Double(xxMax, yymid - halfW, xxMax, yymid + halfW));
            // draw the lower shadow...
            g2.draw(new Line2D.Double(xxMin, yymid, xxQ1, yymid));
            g2.draw(new Line2D.Double(xxMin, yymid - halfW, xxMin, yymid + halfW));
            g2.setStroke(getItemOutlineStroke(row, column));
            g2.setPaint(outlinePaint);
            g2.draw(box);
        }
        // draw mean - SPECIAL AIMS REQUIREMENT...
        g2.setPaint(this.artifactPaint);
        // average radius
        double aRadius;
        if (this.meanVisible) {
            Number xMean = bawDataset.getMeanValue(row, column);
            if (xMean != null) {
                double xxMean = rangeAxis.valueToJava2D(xMean.doubleValue(), dataArea, location);
                aRadius = state.getBarWidth() / 4;
                // here we check that the average marker will in fact be
                // visible before drawing it...
                if ((xxMean > (dataArea.getMinX() - aRadius)) && (xxMean < (dataArea.getMaxX() + aRadius))) {
                    Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xxMean - aRadius, yy + aRadius, aRadius * 2, aRadius * 2);
                    g2.fill(avgEllipse);
                    g2.draw(avgEllipse);
                }
            }
        }
        // draw median...
        if (this.medianVisible) {
            Number xMedian = bawDataset.getMedianValue(row, column);
            if (xMedian != null) {
                double xxMedian = rangeAxis.valueToJava2D(xMedian.doubleValue(), dataArea, location);
                g2.draw(new Line2D.Double(xxMedian, yy, xxMedian, yy + state.getBarWidth()));
            }
        }
        // collect entity and tool tip information...
        if (state.getInfo() != null && box != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, box);
            }
        }
    }

    /**
     * Draws the visual representation of a single data item when the plot has
     * a vertical orientation.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset (must be an instance of
     *                 {@link BoxAndWhiskerCategoryDataset}).
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column) {
        BoxAndWhiskerCategoryDataset bawDataset = (BoxAndWhiskerCategoryDataset) dataset;
        double categoryEnd = domainAxis.getCategoryEnd(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryStart = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, plot.getDomainAxisEdge());
        double categoryWidth = categoryEnd - categoryStart;
        double xx = categoryStart;
        int seriesCount = getRowCount();
        int categoryCount = getColumnCount();
        if (seriesCount > 1) {
            double seriesGap = dataArea.getWidth() * getItemMargin() / (categoryCount * (seriesCount - 1));
            double usedWidth = (state.getBarWidth() * seriesCount) + (seriesGap * (seriesCount - 1));
            // offset the start of the boxes if the total width used is smaller
            // than the category width
            double offset = (categoryWidth - usedWidth) / 2;
            xx = xx + offset + (row * (state.getBarWidth() + seriesGap));
        } else {
            // offset the start of the box if the box width is smaller than the
            // category width
            double offset = (categoryWidth - state.getBarWidth()) / 2;
            xx = xx + offset;
        }
        double yyAverage;
        double yyOutlier;
        Paint itemPaint = getItemPaint(row, column);
        g2.setPaint(itemPaint);
        Stroke s = getItemStroke(row, column);
        g2.setStroke(s);
        // average radius
        double aRadius = 0;
        RectangleEdge location = plot.getRangeAxisEdge();
        Number yQ1 = bawDataset.getQ1Value(row, column);
        Number yQ3 = bawDataset.getQ3Value(row, column);
        Number yMax = bawDataset.getMaxRegularValue(row, column);
        Number yMin = bawDataset.getMinRegularValue(row, column);
        Shape box = null;
        if (yQ1 != null && yQ3 != null && yMax != null && yMin != null) {
            double yyQ1 = rangeAxis.valueToJava2D(yQ1.doubleValue(), dataArea, location);
            double yyQ3 = rangeAxis.valueToJava2D(yQ3.doubleValue(), dataArea, location);
            double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
            double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
            double xxmid = xx + state.getBarWidth() / 2.0;
            double halfW = (state.getBarWidth() / 2.0) * this.whiskerWidth;
            // draw the body...
            box = new Rectangle2D.Double(xx, Math.min(yyQ1, yyQ3), state.getBarWidth(), Math.abs(yyQ1 - yyQ3));
            if (this.fillBox) {
                g2.fill(box);
            }
            Paint outlinePaint = getItemOutlinePaint(row, column);
            if (this.useOutlinePaintForWhiskers) {
                g2.setPaint(outlinePaint);
            }
            // draw the upper shadow...
            g2.draw(new Line2D.Double(xxmid, yyMax, xxmid, yyQ3));
            g2.draw(new Line2D.Double(xxmid - halfW, yyMax, xxmid + halfW, yyMax));
            // draw the lower shadow...
            g2.draw(new Line2D.Double(xxmid, yyMin, xxmid, yyQ1));
            g2.draw(new Line2D.Double(xxmid - halfW, yyMin, xxmid + halfW, yyMin));
            g2.setStroke(getItemOutlineStroke(row, column));
            g2.setPaint(outlinePaint);
            g2.draw(box);
        }
        g2.setPaint(this.artifactPaint);
        // draw mean - SPECIAL AIMS REQUIREMENT...
        if (this.meanVisible) {
            Number yMean = bawDataset.getMeanValue(row, column);
            if (yMean != null) {
                yyAverage = rangeAxis.valueToJava2D(yMean.doubleValue(), dataArea, location);
                aRadius = state.getBarWidth() / 4;
                // here we check that the average marker will in fact be
                // visible before drawing it...
                if ((yyAverage > (dataArea.getMinY() - aRadius)) && (yyAverage < (dataArea.getMaxY() + aRadius))) {
                    Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx + aRadius, yyAverage - aRadius, aRadius * 2, aRadius * 2);
                    g2.fill(avgEllipse);
                    g2.draw(avgEllipse);
                }
            }
        }
        // draw median...
        if (this.medianVisible) {
            Number yMedian = bawDataset.getMedianValue(row, column);
            if (yMedian != null) {
                double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
                g2.draw(new Line2D.Double(xx, yyMedian, xx + state.getBarWidth(), yyMedian));
            }
        }
        // draw yOutliers...
        double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location) + aRadius;
        double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location) - aRadius;
        g2.setPaint(itemPaint);
        // draw outliers
        // outlier radius
        double oRadius = state.getBarWidth() / 3;
        List<Outlier> outliers = new ArrayList<>();
        OutlierListCollection outlierListCollection = new OutlierListCollection();
        // From outlier array sort out which are outliers and put these into a
        // list If there are any farouts, set the flag on the
        // OutlierListCollection
        List<? extends Number> yOutliers = bawDataset.getOutliers(row, column);
        if (yOutliers != null) {
            for (Object yOutlier : yOutliers) {
                double outlier = ((Number) yOutlier).doubleValue();
                Number minOutlier = bawDataset.getMinOutlier(row, column);
                Number maxOutlier = bawDataset.getMaxOutlier(row, column);
                Number minRegular = bawDataset.getMinRegularValue(row, column);
                Number maxRegular = bawDataset.getMaxRegularValue(row, column);
                if (outlier > maxOutlier.doubleValue()) {
                    outlierListCollection.setHighFarOut(true);
                } else if (outlier < minOutlier.doubleValue()) {
                    outlierListCollection.setLowFarOut(true);
                } else if (outlier > maxRegular.doubleValue()) {
                    yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    outliers.add(new Outlier(xx + state.getBarWidth() / 2.0, yyOutlier, oRadius));
                } else if (outlier < minRegular.doubleValue()) {
                    yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                    outliers.add(new Outlier(xx + state.getBarWidth() / 2.0, yyOutlier, oRadius));
                }
                Collections.sort(outliers);
            }
            // Process outliers. Each outlier is either added to the
            // appropriate outlier list or a new outlier list is made
            for (Outlier outlier : outliers) {
                outlierListCollection.add(outlier);
            }
            for (Iterator iterator = outlierListCollection.iterator(); iterator.hasNext(); ) {
                OutlierList list = (OutlierList) iterator.next();
                Outlier outlier = list.getAveragedOutlier();
                Point2D point = outlier.getPoint();
                if (list.isMultiple()) {
                    drawMultipleEllipse(point, state.getBarWidth(), oRadius, g2);
                } else {
                    drawEllipse(point, oRadius, g2);
                }
            }
            // draw farout indicators
            if (isMaxOutlierVisible() && outlierListCollection.isHighFarOut()) {
                drawHighFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, maxAxisValue);
            }
            if (isMinOutlierVisible() && outlierListCollection.isLowFarOut()) {
                drawLowFarOut(aRadius / 2.0, g2, xx + state.getBarWidth() / 2.0, minAxisValue);
            }
        }
        // collect entity and tool tip information...
        if (state.getInfo() != null && box != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                addItemEntity(entities, dataset, row, column, box);
            }
        }
    }

    /**
     * Draws a dot to represent an outlier.
     *
     * @param point  the location.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    private void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        Ellipse2D dot = new Ellipse2D.Double(point.getX() + oRadius / 2, point.getY(), oRadius, oRadius);
        g2.draw(dot);
    }

    /**
     * Draws two dots to represent the average value of more than one outlier.
     *
     * @param point  the location
     * @param boxWidth  the box width.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    private void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, Graphics2D g2) {
        Ellipse2D dot1 = new Ellipse2D.Double(point.getX() - (boxWidth / 2) + oRadius, point.getY(), oRadius, oRadius);
        Ellipse2D dot2 = new Ellipse2D.Double(point.getX() + (boxWidth / 2), point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }

    /**
     * Draws a triangle to indicate the presence of far-out values.
     *
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x coordinate.
     * @param m  the y coordinate.
     */
    private void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    /**
     * Draws a triangle to indicate the presence of far-out values.
     *
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x coordinate.
     * @param m  the y coordinate.
     */
    private void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
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
        if (!(obj instanceof BoxAndWhiskerRenderer)) {
            return false;
        }
        BoxAndWhiskerRenderer that = (BoxAndWhiskerRenderer) obj;
        if (this.fillBox != that.fillBox) {
            return false;
        }
        if (this.itemMargin != that.itemMargin) {
            return false;
        }
        if (this.maximumBarWidth != that.maximumBarWidth) {
            return false;
        }
        if (this.meanVisible != that.meanVisible) {
            return false;
        }
        if (this.medianVisible != that.medianVisible) {
            return false;
        }
        if (this.minOutlierVisible != that.minOutlierVisible) {
            return false;
        }
        if (this.maxOutlierVisible != that.maxOutlierVisible) {
            return false;
        }
        if (this.useOutlinePaintForWhiskers != that.useOutlinePaintForWhiskers) {
            return false;
        }
        if (this.whiskerWidth != that.whiskerWidth) {
            return false;
        }
        if (!PaintUtils.equal(this.artifactPaint, that.artifactPaint)) {
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
        SerialUtils.writePaint(this.artifactPaint, stream);
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
        this.artifactPaint = SerialUtils.readPaint(stream);
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
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jeremy Bowman;
 *                   Arnaud Lelievre;
 *                   Richard West, Advanced Micro Devices, Inc.;
 *                   Ulrich Voigt - patch 2686040;
 *                   Peter Kolb - patches 2603321 and 2809117;
 *
 */
/**
 * A general plotting class that uses data from a {@link CategoryDataset} and
 * renders each data item using a {@link CategoryItemRenderer}.
 *
 * @param <R> the row key type
 * @param <C> the column key type
 */
public class CategoryPlot<R extends Comparable<R>, C extends Comparable<C>> extends Plot implements ValueAxisPlot, Pannable, Zoomable, AnnotationChangeListener, RendererChangeListener, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -3537691700434728188L;

    /**
     * The default visibility of the grid lines plotted against the domain
     * axis.
     */
    public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;

    /**
     * The default visibility of the grid lines plotted against the range
     * axis.
     */
    public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;

    /**
     * The default grid line stroke.
     */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);

    /**
     * The default grid line paint.
     */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.LIGHT_GRAY;

    /**
     * The default value label font.
     */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

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
     * Storage for the domain axes.
     */
    private Map<Integer, CategoryAxis> domainAxes;

    /**
     * Storage for the domain axis locations.
     */
    private Map<Integer, AxisLocation> domainAxisLocations;

    /**
     * A flag that controls whether the shared domain axis is drawn
     * (only relevant when the plot is being used as a subplot).
     */
    private boolean drawSharedDomainAxis;

    /**
     * Storage for the range axes.
     */
    private Map<Integer, ValueAxis> rangeAxes;

    /**
     * Storage for the range axis locations.
     */
    private Map<Integer, AxisLocation> rangeAxisLocations;

    /**
     * Storage for the datasets.
     */
    private Map<Integer, CategoryDataset<R, C>> datasets;

    /**
     * Storage for keys that map each dataset to one or more domain axes.
     * Typically a dataset is rendered using the scale of a single axis, but
     * a dataset can contribute to the "auto-range" of any number of axes.
     */
    private Map<Integer, List<Integer>> datasetToDomainAxesMap;

    /**
     * Storage for keys that map each dataset to one or more range axes.
     * Typically a dataset is rendered using the scale of a single axis, but
     * a dataset can contribute to the "auto-range" of any number of axes.
     */
    private Map<Integer, List<Integer>> datasetToRangeAxesMap;

    /**
     * Storage for the renderers.
     */
    private Map<Integer, CategoryItemRenderer> renderers;

    /**
     * The dataset rendering order.
     */
    private DatasetRenderingOrder renderingOrder = DatasetRenderingOrder.REVERSE;

    /**
     * Controls the order in which the columns are traversed when rendering the
     * data items.
     */
    private SortOrder columnRenderingOrder = SortOrder.ASCENDING;

    /**
     * Controls the order in which the rows are traversed when rendering the
     * data items.
     */
    private SortOrder rowRenderingOrder = SortOrder.ASCENDING;

    /**
     * A flag that controls whether the grid-lines for the domain axis are
     * visible.
     */
    private boolean domainGridlinesVisible;

    /**
     * The position of the domain gridlines relative to the category.
     */
    private CategoryAnchor domainGridlinePosition;

    /**
     * The stroke used to draw the domain grid-lines.
     */
    private transient Stroke domainGridlineStroke;

    /**
     * The paint used to draw the domain  grid-lines.
     */
    private transient Paint domainGridlinePaint;

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
     * A flag that controls whether the grid-lines for the range axis are
     * visible.
     */
    private boolean rangeGridlinesVisible;

    /**
     * The stroke used to draw the range axis grid-lines.
     */
    private transient Stroke rangeGridlineStroke;

    /**
     * The paint used to draw the range axis grid-lines.
     */
    private transient Paint rangeGridlinePaint;

    /**
     * A flag that controls whether gridlines are shown for the minor
     * tick values on the primary range axis.
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
     * The anchor value.
     */
    private double anchorValue;

    /**
     * The index for the dataset that the crosshairs are linked to (this
     * determines which axes the crosshairs are plotted against).
     */
    private int crosshairDatasetIndex;

    /**
     * A flag that controls the visibility of the domain crosshair.
     */
    private boolean domainCrosshairVisible;

    /**
     * The row key for the crosshair point.
     */
    private R domainCrosshairRowKey;

    /**
     * The column key for the crosshair point.
     */
    private C domainCrosshairColumnKey;

    /**
     * The stroke used to draw the domain crosshair if it is visible.
     */
    private transient Stroke domainCrosshairStroke;

    /**
     * The paint used to draw the domain crosshair if it is visible.
     */
    private transient Paint domainCrosshairPaint;

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
     * A map containing lists of markers for the domain axes.
     */
    private Map<Integer, Collection<CategoryMarker>> foregroundDomainMarkers;

    /**
     * A map containing lists of markers for the domain axes.
     */
    private Map<Integer, Collection<CategoryMarker>> backgroundDomainMarkers;

    /**
     * A map containing lists of markers for the range axes.
     */
    private Map<Integer, Collection<Marker>> foregroundRangeMarkers;

    /**
     * A map containing lists of markers for the range axes.
     */
    private Map<Integer, Collection<Marker>> backgroundRangeMarkers;

    /**
     * A (possibly empty) list of annotations for the plot.  The list should
     * be initialised in the constructor and never allowed to be {@code null}.
     */
    private List<CategoryAnnotation> annotations;

    /**
     * The weight for the plot (only relevant when the plot is used as a subplot
     * within a combined plot).
     */
    private int weight;

    /**
     * The fixed space for the domain axis.
     */
    private AxisSpace fixedDomainAxisSpace;

    /**
     * The fixed space for the range axis.
     */
    private AxisSpace fixedRangeAxisSpace;

    /**
     * An optional collection of legend items that can be returned by the
     * getLegendItems() method.
     */
    private LegendItemCollection fixedLegendItems;

    /**
     * A flag that controls whether panning is enabled for the
     * range axis/axes.
     */
    private boolean rangePannable;

    /**
     * The shadow generator for the plot ({@code null} permitted).
     */
    private ShadowGenerator shadowGenerator;

    /**
     * Default constructor.
     */
    public CategoryPlot() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param domainAxis  the domain axis ({@code null} permitted).
     * @param rangeAxis  the range axis ({@code null} permitted).
     * @param renderer  the item renderer ({@code null} permitted).
     */
    public CategoryPlot(CategoryDataset<R, C> dataset, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
        super();
        this.orientation = PlotOrientation.VERTICAL;
        // allocate storage for dataset, axes and renderers
        this.domainAxes = new HashMap<>();
        this.domainAxisLocations = new HashMap<>();
        this.rangeAxes = new HashMap<>();
        this.rangeAxisLocations = new HashMap<>();
        this.datasetToDomainAxesMap = new TreeMap<>();
        this.datasetToRangeAxesMap = new TreeMap<>();
        this.renderers = new HashMap<>();
        this.datasets = new HashMap<>();
        this.datasets.put(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        this.domainAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
        this.rangeAxisLocations.put(0, AxisLocation.TOP_OR_LEFT);
        this.renderers.put(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.domainAxes.put(0, domainAxis);
        mapDatasetToDomainAxis(0, 0);
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.drawSharedDomainAxis = false;
        this.rangeAxes.put(0, rangeAxis);
        mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        configureDomainAxes();
        configureRangeAxes();
        this.domainGridlinesVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.domainGridlinePosition = CategoryAnchor.MIDDLE;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeZeroBaselineVisible = false;
        this.rangeZeroBaselinePaint = Color.BLACK;
        this.rangeZeroBaselineStroke = new BasicStroke(0.5f);
        this.rangeGridlinesVisible = DEFAULT_RANGE_GRIDLINES_VISIBLE;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = false;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.WHITE;
        this.foregroundDomainMarkers = new HashMap<>();
        this.backgroundDomainMarkers = new HashMap<>();
        this.foregroundRangeMarkers = new HashMap<>();
        this.backgroundRangeMarkers = new HashMap<>();
        this.anchorValue = 0.0;
        this.domainCrosshairVisible = false;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.annotations = new ArrayList<>();
        this.rangePannable = false;
        this.shadowGenerator = null;
    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return The type.
     */
    @Override
    public String getPlotType() {
        return localizationResources.getString("Category_Plot");
    }

    /**
     * Returns the orientation of the plot.
     *
     * @return The orientation of the plot (never {@code null}).
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
     * @param orientation  the orientation ({@code null} not permitted).
     *
     * @see #getOrientation()
     */
    public void setOrientation(PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        this.orientation = orientation;
        fireChangeEvent();
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
     * Sets the axis offsets (gap between the data area and the axes) and
     * sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is {@code null}, then the method will return the parent plot's
     * domain axis (if there is a parent plot).
     *
     * @return The domain axis ({@code null} permitted).
     *
     * @see #setDomainAxis(CategoryAxis)
     */
    public CategoryAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    /**
     * Returns a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setDomainAxis(int, CategoryAxis)
     */
    public CategoryAxis getDomainAxis(int index) {
        CategoryAxis result = this.domainAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                @SuppressWarnings("unchecked")
                CategoryPlot<R, C> cp = (CategoryPlot) parent;
                result = cp.getDomainAxis(index);
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
    public Map<Integer, CategoryAxis> getDomainAxes() {
        return Collections.unmodifiableMap(this.domainAxes);
    }

    /**
     * Sets the domain axis for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getDomainAxis()
     */
    public void setDomainAxis(CategoryAxis axis) {
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
     */
    public void setDomainAxis(int index, CategoryAxis axis) {
        setDomainAxis(index, axis, true);
    }

    /**
     * Sets a domain axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     * @param notify  notify listeners?
     */
    public void setDomainAxis(int index, CategoryAxis axis, boolean notify) {
        CategoryAxis existing = this.domainAxes.get(index);
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
    public void setDomainAxes(CategoryAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the index of the specified axis, or {@code -1} if the axis
     * is not assigned to the plot.
     *
     * @param axis  the axis ({@code null} not permitted).
     *
     * @return The axis index.
     *
     * @see #getDomainAxis(int)
     * @see #getRangeAxisIndex(ValueAxis)
     */
    public int getDomainAxisIndex(CategoryAxis axis) {
        Args.nullNotPermitted(axis, "axis");
        for (Entry<Integer, CategoryAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the domain axis location for the primary domain axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #getRangeAxisLocation()
     */
    public AxisLocation getDomainAxisLocation() {
        return getDomainAxisLocation(0);
    }

    /**
     * Returns the location for a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     *
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = this.domainAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getDomainAxisLocation(0));
        }
        return result;
    }

    /**
     * Sets the location of the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param location  the axis location ({@code null} not permitted).
     *
     * @see #getDomainAxisLocation()
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public void setDomainAxisLocation(AxisLocation location) {
        // delegate...
        setDomainAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the axis location ({@code null} not permitted).
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setDomainAxisLocation(0, location, notify);
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     *
     * @see #getDomainAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public void setDomainAxisLocation(int index, AxisLocation location) {
        // delegate...
        setDomainAxisLocation(index, location, true);
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
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
     * Returns the domain axis edge.  This is derived from the axis location
     * and the plot orientation.
     *
     * @return The edge (never {@code null}).
     */
    public RectangleEdge getDomainAxisEdge() {
        return getDomainAxisEdge(0);
    }

    /**
     * Returns the edge for a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The edge (never {@code null}).
     */
    public RectangleEdge getDomainAxisEdge(int index) {
        RectangleEdge result;
        AxisLocation location = getDomainAxisLocation(index);
        if (location != null) {
            result = Plot.resolveDomainAxisLocation(location, this.orientation);
        } else {
            result = RectangleEdge.opposite(getDomainAxisEdge(0));
        }
        return result;
    }

    /**
     * Returns the number of domain axes.
     *
     * @return The axis count.
     */
    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    /**
     * Clears the domain axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     */
    public void clearDomainAxes() {
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the domain axes.
     */
    public void configureDomainAxes() {
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.configure();
            }
        }
    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if there
     * is a parent plot).
     *
     * @return The range axis (possibly {@code null}).
     */
    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    /**
     * Returns a range axis.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     */
    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = this.rangeAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                @SuppressWarnings("unchecked")
                CategoryPlot<R, C> cp = (CategoryPlot) parent;
                result = cp.getRangeAxis(index);
            }
        }
        return result;
    }

    /**
     * Returns a map containing the range axes that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the domain axes that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, ValueAxis> getRangeAxes() {
        return Collections.unmodifiableMap(this.rangeAxes);
    }

    /**
     * Sets the range axis for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param axis  the axis ({@code null} permitted).
     */
    public void setRangeAxis(ValueAxis axis) {
        setRangeAxis(0, axis);
    }

    /**
     * Sets a range axis and sends a {@link PlotChangeEvent} to all registered
     * listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     */
    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, true);
    }

    /**
     * Sets a range axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     * @param notify  notify listeners?
     */
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = this.rangeAxes.get(index);
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
     * @see #setDomainAxes(CategoryAxis[])
     */
    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the index of the specified axis, or {@code -1} if the axis
     * is not assigned to the plot.
     *
     * @param axis  the axis ({@code null} not permitted).
     *
     * @return The axis index.
     *
     * @see #getRangeAxis(int)
     * @see #getDomainAxisIndex(CategoryAxis)
     */
    public int getRangeAxisIndex(ValueAxis axis) {
        Args.nullNotPermitted(axis, "axis");
        int result = findRangeAxisIndex(axis);
        if (result < 0) {
            // try the parent plot
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                @SuppressWarnings("unchecked")
                CategoryPlot<R, C> p = (CategoryPlot) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
        return result;
    }

    private int findRangeAxisIndex(ValueAxis axis) {
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the range axis location.
     *
     * @return The location (never {@code null}).
     */
    public AxisLocation getRangeAxisLocation() {
        return getRangeAxisLocation(0);
    }

    /**
     * Returns the location for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     *
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = this.rangeAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getRangeAxisLocation(0));
        }
        return result;
    }

    /**
     * Sets the location of the range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #setRangeAxisLocation(AxisLocation, boolean)
     * @see #setDomainAxisLocation(AxisLocation)
     */
    public void setRangeAxisLocation(AxisLocation location) {
        // defer argument checking...
        setRangeAxisLocation(location, true);
    }

    /**
     * Sets the location of the range axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #setDomainAxisLocation(AxisLocation, boolean)
     */
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        setRangeAxisLocation(0, location, notify);
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     *
     * @see #getRangeAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation, boolean)
     */
    public void setRangeAxisLocation(int index, AxisLocation location) {
        setRangeAxisLocation(index, location, true);
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
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
     * Returns the edge where the primary range axis is located.
     *
     * @return The edge (never {@code null}).
     */
    public RectangleEdge getRangeAxisEdge() {
        return getRangeAxisEdge(0);
    }

    /**
     * Returns the edge for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     */
    public RectangleEdge getRangeAxisEdge(int index) {
        AxisLocation location = getRangeAxisLocation(index);
        return Plot.resolveRangeAxisLocation(location, this.orientation);
    }

    /**
     * Returns the number of range axes.
     *
     * @return The axis count.
     */
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    /**
     * Clears the range axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     */
    public void clearRangeAxes() {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the range axes.
     */
    public void configureRangeAxes() {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.configure();
            }
        }
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly {@code null}).
     *
     * @see #setDataset(CategoryDataset)
     */
    public CategoryDataset<R, C> getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset with the given index, or {@code null} if there is
     * no dataset.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(int, CategoryDataset)
     */
    public CategoryDataset<R, C> getDataset(int index) {
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
    public Map<Integer, CategoryDataset<R, C>> getDatasets() {
        return Collections.unmodifiableMap(this.datasets);
    }

    /**
     * Sets the dataset for the plot, replacing the existing dataset, if there
     * is one.  This method also calls the
     * {@link #datasetChanged(DatasetChangeEvent)} method, which adjusts the
     * axis ranges if necessary and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     */
    public void setDataset(CategoryDataset<R, C> dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot and sends a change notification to all
     * registered listeners.
     *
     * @param index  the dataset index (must be &gt;= 0).
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset(int)
     */
    public void setDataset(int index, CategoryDataset<R, C> dataset) {
        CategoryDataset<R, C> existing = this.datasets.get(index);
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
    public int indexOf(CategoryDataset<R, C> dataset) {
        for (Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
            if (entry.getValue() == dataset) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Maps a dataset to a particular domain axis.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index (zero-based).
     *
     * @see #getDomainAxisForDataset(int)
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
     * This method is used to perform argument checking on the list of
     * axis indices passed to mapDatasetToDomainAxes() and
     * mapDatasetToRangeAxes().
     *
     * @param indices  the list of indices ({@code null} permitted).
     */
    private void checkAxisIndices(List<Integer> indices) {
        // indices can be:
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
        HashSet<Integer> set = new HashSet<>();
        for (Integer item : indices) {
            if (!set.add(item)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            ;
        }
    }

    /**
     * Returns the domain axis for a dataset.  You can change the axis for a
     * dataset using the {@link #mapDatasetToDomainAxis(int, int)} method.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The domain axis.
     *
     * @see #mapDatasetToDomainAxis(int, int)
     */
    public CategoryAxis getDomainAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        CategoryAxis axis;
        List<Integer> axisIndices = this.datasetToDomainAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            axis = getDomainAxis(axisIndex);
        } else {
            axis = getDomainAxis(0);
        }
        return axis;
    }

    /**
     * Maps a dataset to a particular range axis.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index (zero-based).
     *
     * @see #getRangeAxisForDataset(int)
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
     * Returns the range axis for a dataset.  You can change the axis for a
     * dataset using the {@link #mapDatasetToRangeAxis(int, int)} method.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The range axis.
     *
     * @see #mapDatasetToRangeAxis(int, int)
     */
    public ValueAxis getRangeAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        ValueAxis axis;
        List<Integer> axisIndices = this.datasetToRangeAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            axis = getRangeAxis(axisIndices.get(0));
        } else {
            axis = getRangeAxis(0);
        }
        return axis;
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
     * Returns a reference to the renderer for the plot.
     *
     * @return The renderer.
     *
     * @see #setRenderer(CategoryItemRenderer)
     */
    public CategoryItemRenderer getRenderer() {
        return getRenderer(0);
    }

    /**
     * Returns the renderer at the given index.
     *
     * @param index  the renderer index.
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(int, CategoryItemRenderer)
     */
    public CategoryItemRenderer getRenderer(int index) {
        CategoryItemRenderer renderer = this.renderers.get(index);
        if (renderer == null) {
            return this.renderers.get(0);
        }
        return renderer;
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
    public Map<Integer, CategoryItemRenderer> getRenderers() {
        return Collections.unmodifiableMap(this.renderers);
    }

    /**
     * Sets the renderer at index 0 (sometimes referred to as the "primary"
     * renderer) and sends a change event to all registered listeners.
     *
     * @param renderer  the renderer ({@code null} permitted.
     *
     * @see #getRenderer()
     */
    public void setRenderer(CategoryItemRenderer renderer) {
        setRenderer(0, renderer, true);
    }

    /**
     * Sets the renderer at index 0 (sometimes referred to as the "primary"
     * renderer) and, if requested, sends a change event to all registered
     * listeners.
     * <p>
     * You can set the renderer to {@code null}, but this is not
     * recommended because:
     * <ul>
     *   <li>no data will be displayed;</li>
     *   <li>the plot background will not be painted;</li>
     * </ul>
     *
     * @param renderer  the renderer ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getRenderer()
     */
    public void setRenderer(CategoryItemRenderer renderer, boolean notify) {
        setRenderer(0, renderer, notify);
    }

    /**
     * Sets the renderer to use for the dataset with the specified index and
     * sends a change event to all registered listeners.  Note that each
     * dataset should have its own renderer, you should not use one renderer
     * for multiple datasets.
     *
     * @param index  the index.
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @see #getRenderer(int)
     * @see #setRenderer(int, CategoryItemRenderer, boolean)
     */
    public void setRenderer(int index, CategoryItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    /**
     * Sets the renderer to use for the dataset with the specified index and,
     * if requested, sends a change event to all registered listeners.  Note
     * that each dataset should have its own renderer, you should not use one
     * renderer for multiple datasets.
     *
     * @param index  the index.
     * @param renderer  the renderer ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, CategoryItemRenderer renderer, boolean notify) {
        CategoryItemRenderer existing = this.renderers.get(index);
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
     * @param renderers  the renderers.
     */
    public void setRenderers(CategoryItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the renderer for the specified dataset.  If the dataset doesn't
     * belong to the plot, this method will return {@code null}.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The renderer (possibly {@code null}).
     */
    public CategoryItemRenderer getRendererForDataset(CategoryDataset<R, C> dataset) {
        int datasetIndex = indexOf(dataset);
        if (datasetIndex < 0) {
            return null;
        }
        CategoryItemRenderer renderer = this.renderers.get(datasetIndex);
        if (renderer == null) {
            return getRenderer();
        }
        return renderer;
    }

    /**
     * Returns the index of the specified renderer, or {@code -1} if the
     * renderer is not assigned to this plot.
     *
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @return The renderer index.
     */
    public int getIndexOf(CategoryItemRenderer renderer) {
        for (Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() == renderer) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the dataset rendering order.
     *
     * @return The order (never {@code null}).
     *
     * @see #setDatasetRenderingOrder(DatasetRenderingOrder)
     */
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.renderingOrder;
    }

    /**
     * Sets the rendering order and sends a {@link PlotChangeEvent} to all
     * registered listeners.  By default, the plot renders the primary dataset
     * last (so that the primary dataset overlays the secondary datasets).  You
     * can reverse this if you want to.
     *
     * @param order  the rendering order ({@code null} not permitted).
     *
     * @see #getDatasetRenderingOrder()
     */
    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        Args.nullNotPermitted(order, "order");
        this.renderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the order in which the columns are rendered.  The default value
     * is {@code SortOrder.ASCENDING}.
     *
     * @return The column rendering order (never {@code null}).
     *
     * @see #setColumnRenderingOrder(SortOrder)
     */
    public SortOrder getColumnRenderingOrder() {
        return this.columnRenderingOrder;
    }

    /**
     * Sets the column order in which the items in each dataset should be
     * rendered and sends a {@link PlotChangeEvent} to all registered
     * listeners.  Note that this affects the order in which items are drawn,
     * NOT their position in the chart.
     *
     * @param order  the order ({@code null} not permitted).
     *
     * @see #getColumnRenderingOrder()
     * @see #setRowRenderingOrder(SortOrder)
     */
    public void setColumnRenderingOrder(SortOrder order) {
        Args.nullNotPermitted(order, "order");
        this.columnRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the order in which the rows should be rendered.  The default
     * value is {@code SortOrder.ASCENDING}.
     *
     * @return The order (never {@code null}).
     *
     * @see #setRowRenderingOrder(SortOrder)
     */
    public SortOrder getRowRenderingOrder() {
        return this.rowRenderingOrder;
    }

    /**
     * Sets the row order in which the items in each dataset should be
     * rendered and sends a {@link PlotChangeEvent} to all registered
     * listeners.  Note that this affects the order in which items are drawn,
     * NOT their position in the chart.
     *
     * @param order  the order ({@code null} not permitted).
     *
     * @see #getRowRenderingOrder()
     * @see #setColumnRenderingOrder(SortOrder)
     */
    public void setRowRenderingOrder(SortOrder order) {
        Args.nullNotPermitted(order, "order");
        this.rowRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the domain grid-lines are visible.
     *
     * @return The {@code true} or {@code false}.
     *
     * @see #setDomainGridlinesVisible(boolean)
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether grid-lines are drawn against
     * the domain axis.
     * <p>
     * If the flag value changes, a {@link PlotChangeEvent} is sent to all
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
     * Returns the position used for the domain gridlines.
     *
     * @return The gridline position (never {@code null}).
     *
     * @see #setDomainGridlinePosition(CategoryAnchor)
     */
    public CategoryAnchor getDomainGridlinePosition() {
        return this.domainGridlinePosition;
    }

    /**
     * Sets the position used for the domain gridlines and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     *
     * @see #getDomainGridlinePosition()
     */
    public void setDomainGridlinePosition(CategoryAnchor position) {
        Args.nullNotPermitted(position, "position");
        this.domainGridlinePosition = position;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw grid-lines against the domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainGridlineStroke(Stroke)
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke used to draw grid-lines against the domain axis and
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
     * Returns the paint used to draw grid-lines against the domain axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainGridlinePaint(Paint)
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid-lines (if any) against the domain
     * axis and sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the flag that controls whether the range grid-lines are visible.
     *
     * @return The flag.
     *
     * @see #setRangeGridlinesVisible(boolean)
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether grid-lines are drawn against
     * the range axis.  If the flag changes value, a {@link PlotChangeEvent} is
     * sent to all registered listeners.
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
     * Returns the stroke used to draw the grid-lines against the range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeGridlineStroke(Stroke)
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke used to draw the grid-lines against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the paint used to draw the grid-lines against the range axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeGridlinePaint(Paint)
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid lines against the range axis and
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
     * Returns the legend items for the plot.  By default, this method creates
     * a legend item for each series in each of the datasets.  You can change
     * this behaviour by overriding this method.
     *
     * @return The legend items.
     */
    @Override
    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        // get the legend items for the datasets...
        for (CategoryDataset<R, C> dataset : this.datasets.values()) {
            if (dataset != null) {
                int datasetIndex = indexOf(dataset);
                CategoryItemRenderer renderer = getRenderer(datasetIndex);
                if (renderer != null) {
                    result.addAll(renderer.getLegendItems());
                }
            }
        }
        return result;
    }

    /**
     * Handles a 'click' on the plot by updating the anchor value.
     *
     * @param x  x-coordinate of the click (in Java2D space).
     * @param y  y-coordinate of the click (in Java2D space).
     * @param info  information about the plot's dimensions.
     */
    @Override
    public void handleClick(int x, int y, PlotRenderingInfo info) {
        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            // set the anchor value for the range axis...
            double java2D = 0.0;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                java2D = x;
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                java2D = y;
            }
            RectangleEdge edge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
            double value = getRangeAxis().java2DToValue(java2D, info.getDataArea(), edge);
            setAnchorValue(value);
            setRangeCrosshairValue(value);
        }
    }

    /**
     * Zooms (in or out) on the plot's value axis.
     * <p>
     * If the value 0.0 is passed in as the zoom percent, the auto-range
     * calculation for the axis is restored (which sets the range to include
     * the minimum and maximum data values, thus displaying all the data).
     *
     * @param percent  the zoom amount.
     */
    @Override
    public void zoom(double percent) {
        if (percent > 0.0) {
            double range = getRangeAxis().getRange().getLength();
            double scaledRange = range * percent;
            getRangeAxis().setRange(this.anchorValue - scaledRange / 2.0, this.anchorValue + scaledRange / 2.0);
        } else {
            getRangeAxis().setAutoRange(true);
        }
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
     * The range axis bounds will be recalculated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.configure();
            }
        }
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
        Plot parent = getParent();
        if (parent != null) {
            if (parent instanceof RendererChangeListener) {
                RendererChangeListener rcl = (RendererChangeListener) parent;
                rcl.rendererChanged(event);
            } else {
                // this should never happen with the existing code, but throw
                // an exception in case future changes make it possible...
                throw new RuntimeException("The renderer has changed and I don't know what to do!");
            }
        } else {
            configureRangeAxes();
            PlotChangeEvent e = new PlotChangeEvent(this);
            notifyListeners(e);
        }
    }

    /**
     * Adds a marker for display (in the foreground) against the domain axis and
     * sends a {@link PlotChangeEvent} to all registered listeners. Typically a
     * marker will be drawn by the renderer as a line perpendicular to the
     * domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #removeDomainMarker(CategoryMarker)
     */
    public void addDomainMarker(CategoryMarker marker) {
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for display against the domain axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.  Typically a marker
     * will be drawn by the renderer as a line perpendicular to the domain
     * axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null}
     *               not permitted).
     *
     * @see #removeDomainMarker(CategoryMarker, Layer)
     */
    public void addDomainMarker(CategoryMarker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
    }

    /**
     * Adds a marker for display by a particular renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a domain axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer ({@code null} not permitted).
     *
     * @see #removeDomainMarker(int, CategoryMarker, Layer)
     */
    public void addDomainMarker(int index, CategoryMarker marker, Layer layer) {
        addDomainMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for display by a particular renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a domain axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #removeDomainMarker(int, CategoryMarker, Layer, boolean)
     */
    public void addDomainMarker(int index, CategoryMarker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        Collection<CategoryMarker> markers;
        if (layer == Layer.FOREGROUND) {
            markers = this.foregroundDomainMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.foregroundDomainMarkers.put(index, markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = this.backgroundDomainMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.backgroundDomainMarkers.put(index, markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears all the domain markers for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #clearRangeMarkers()
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
     * Returns the list of domain markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of domain markers.
     */
    public Collection<CategoryMarker> getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    /**
     * Returns a collection of domain markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     */
    public Collection<CategoryMarker> getDomainMarkers(int index, Layer layer) {
        Collection<CategoryMarker> result = null;
        Integer key = index;
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundDomainMarkers.get(key);
        } else if (layer == Layer.BACKGROUND) {
            result = this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    /**
     * Clears all the domain markers for the specified renderer.
     *
     * @param index  the renderer index.
     *
     * @see #clearRangeMarkers(int)
     */
    public void clearDomainMarkers(int index) {
        Integer key = index;
        if (this.backgroundDomainMarkers != null) {
            Collection<CategoryMarker> markers = this.backgroundDomainMarkers.get(key);
            if (markers != null) {
                for (CategoryMarker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundDomainMarkers != null) {
            Collection<CategoryMarker> markers = this.foregroundDomainMarkers.get(key);
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
     * Removes a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker  the marker.
     *
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(CategoryMarker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(CategoryMarker marker, Layer layer) {
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
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(int index, CategoryMarker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker.
     * @param layer the layer (foreground or background).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(int index, CategoryMarker marker, Layer layer, boolean notify) {
        Collection<CategoryMarker> markers;
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
     * Adds a marker for display (in the foreground) against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners. Typically a
     * marker will be drawn by the renderer as a line perpendicular to the
     * range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #removeRangeMarker(Marker)
     */
    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for display against the range axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.  Typically a marker
     * will be drawn by the renderer as a line perpendicular to the range axis,
     * however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null}
     *               not permitted).
     *
     * @see #removeRangeMarker(Marker, Layer)
     */
    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    /**
     * Adds a marker for display by a particular renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a range axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker.
     * @param layer  the layer.
     *
     * @see #removeRangeMarker(int, Marker, Layer)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for display by a particular renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a range axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker.
     * @param layer  the layer.
     * @param notify  notify listeners?
     *
     * @see #removeRangeMarker(int, Marker, Layer, boolean)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Collection<Marker> markers;
        if (layer == Layer.FOREGROUND) {
            markers = this.foregroundRangeMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.foregroundRangeMarkers.put(index, markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = this.backgroundRangeMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.backgroundRangeMarkers.put(index, markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears all the range markers for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #clearDomainMarkers()
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
     * Returns the list of range markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of range markers.
     *
     * @see #getRangeMarkers(int, Layer)
     */
    public Collection<Marker> getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
    }

    /**
     * Returns a collection of range markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
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
     * Clears all the range markers for the specified renderer.
     *
     * @param index  the renderer index.
     *
     * @see #clearDomainMarkers(int)
     */
    public void clearRangeMarkers(int index) {
        Integer key = index;
        if (this.backgroundRangeMarkers != null) {
            Collection<Marker> markers = this.backgroundRangeMarkers.get(key);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            Collection<Marker> markers = this.foregroundRangeMarkers.get(key);
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
     *
     * @see #addRangeMarker(Marker)
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
     *
     * @see #addRangeMarker(Marker, Layer)
     */
    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
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
     *
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners.
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     *
     * @see #addRangeMarker(int, Marker, Layer, boolean)
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Collection<Marker> markers;
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
     * Returns the flag that controls whether the domain crosshair is
     * displayed by the plot.
     *
     * @return A boolean.
     *
     * @see #setDomainCrosshairVisible(boolean)
     */
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    /**
     * Sets the flag that controls whether the domain crosshair is
     * displayed by the plot, and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the new flag value.
     *
     * @see #isDomainCrosshairVisible()
     * @see #setRangeCrosshairVisible(boolean)
     */
    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the row key for the domain crosshair.
     *
     * @return The row key.
     */
    public R getDomainCrosshairRowKey() {
        return this.domainCrosshairRowKey;
    }

    /**
     * Sets the row key for the domain crosshair and sends a
     * {PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     */
    public void setDomainCrosshairRowKey(R key) {
        setDomainCrosshairRowKey(key, true);
    }

    /**
     * Sets the row key for the domain crosshair and, if requested, sends a
     * {PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     * @param notify  notify listeners?
     */
    public void setDomainCrosshairRowKey(R key, boolean notify) {
        this.domainCrosshairRowKey = key;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the column key for the domain crosshair.
     *
     * @return The column key.
     */
    public C getDomainCrosshairColumnKey() {
        return this.domainCrosshairColumnKey;
    }

    /**
     * Sets the column key for the domain crosshair and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     */
    public void setDomainCrosshairColumnKey(C key) {
        setDomainCrosshairColumnKey(key, true);
    }

    /**
     * Sets the column key for the domain crosshair and, if requested, sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     * @param notify  notify listeners?
     */
    public void setDomainCrosshairColumnKey(C key, boolean notify) {
        this.domainCrosshairColumnKey = key;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the dataset index for the crosshair.
     *
     * @return The dataset index.
     */
    public int getCrosshairDatasetIndex() {
        return this.crosshairDatasetIndex;
    }

    /**
     * Sets the dataset index for the crosshair and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     */
    public void setCrosshairDatasetIndex(int index) {
        setCrosshairDatasetIndex(index, true);
    }

    /**
     * Sets the dataset index for the crosshair and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     * @param notify  notify listeners?
     */
    public void setCrosshairDatasetIndex(int index, boolean notify) {
        this.crosshairDatasetIndex = index;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the paint used to draw the domain crosshair.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainCrosshairPaint(Paint)
     * @see #getDomainCrosshairStroke()
     */
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    /**
     * Sets the paint used to draw the domain crosshair.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainCrosshairPaint()
     */
    public void setDomainCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw the domain crosshair.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainCrosshairStroke(Stroke)
     * @see #getDomainCrosshairPaint()
     */
    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    /**
     * Sets the stroke used to draw the domain crosshair, and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDomainCrosshairStroke()
     */
    public void setDomainCrosshairStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainCrosshairStroke = stroke;
    }

    /**
     * Returns a flag indicating whether the range crosshair is visible.
     *
     * @return The flag.
     *
     * @see #setRangeCrosshairVisible(boolean)
     */
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether the range crosshair is visible.
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
     * "lock-on" to actual data values, and sends a {@link PlotChangeEvent}
     * to all registered listeners.
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
     * Sets the range crosshair value and, if the crosshair is visible, sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param value  the new value.
     *
     * @see #getRangeCrosshairValue()
     */
    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, true);
    }

    /**
     * Sets the range crosshair value and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners (but only if the
     * crosshair is visible).
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
     * Returns the pen-style ({@code Stroke}) used to draw the crosshair
     * (if visible).
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
     * Sets the pen-style ({@code Stroke}) used to draw the range
     * crosshair (if visible), and sends a {@link PlotChangeEvent} to all
     * registered listeners.
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
     * Returns the paint used to draw the range crosshair.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeCrosshairPaint(Paint)
     * @see #isRangeCrosshairVisible()
     * @see #getRangeCrosshairStroke()
     */
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    /**
     * Sets the paint used to draw the range crosshair (if visible) and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeCrosshairPaint()
     */
    public void setRangeCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the list of annotations.
     *
     * @return The list of annotations (never {@code null}).
     *
     * @see #addAnnotation(CategoryAnnotation)
     * @see #clearAnnotations()
     */
    public List<CategoryAnnotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Adds an annotation to the plot and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     *
     * @see #removeAnnotation(CategoryAnnotation)
     */
    public void addAnnotation(CategoryAnnotation annotation) {
        addAnnotation(annotation, true);
    }

    /**
     * Adds an annotation to the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void addAnnotation(CategoryAnnotation annotation, boolean notify) {
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
     * @see #addAnnotation(CategoryAnnotation)
     */
    public boolean removeAnnotation(CategoryAnnotation annotation) {
        return removeAnnotation(annotation, true);
    }

    /**
     * Removes an annotation from the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @return A boolean (indicates whether the annotation was removed).
     */
    public boolean removeAnnotation(CategoryAnnotation annotation, boolean notify) {
        Args.nullNotPermitted(annotation, "annotation");
        boolean removed = this.annotations.remove(annotation);
        annotation.removeChangeListener(this);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Clears all the annotations and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     */
    public void clearAnnotations() {
        for (CategoryAnnotation annotation : this.annotations) {
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
            if (this.orientation.isHorizontal()) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
            } else if (this.orientation.isVertical()) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
        } else {
            // reserve space for the primary domain axis...
            RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
            if (this.drawSharedDomainAxis) {
                space = getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space);
            }
            // reserve space for any domain axes...
            for (CategoryAxis xAxis : this.domainAxes.values()) {
                if (xAxis != null) {
                    int i = getDomainAxisIndex(xAxis);
                    RectangleEdge edge = getDomainAxisEdge(i);
                    space = xAxis.reserveSpace(g2, this, plotArea, edge, space);
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
            if (this.orientation.isHorizontal()) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
        } else {
            // reserve space for the range axes (if any)...
            for (ValueAxis yAxis : this.rangeAxes.values()) {
                if (yAxis != null) {
                    int i = findRangeAxisIndex(yAxis);
                    RectangleEdge edge = getRangeAxisEdge(i);
                    space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
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
     * Calculates the space required for the axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     *
     * @return The space required for the axes.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = calculateRangeAxisSpace(g2, plotArea, space);
        space = calculateDomainAxisSpace(g2, plotArea, space);
        return space;
    }

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        // visit the domain axes
        for (Entry<Integer, CategoryAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // visit the range axes
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // visit the renderers
        for (Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // and finally this plot
        visitor.visit(this);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * At your option, you may supply an instance of {@link PlotRenderingInfo}.
     * If you do, it will be populated with information about the drawing,
     * including various plot dimensions and tooltip info.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot (including axes) should
     *              be drawn.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param state  collects info as the chart is drawn (possibly
     *               {@code null}).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo state) {
        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }
        // record the plot area...
        if (state == null) {
            // if the incoming state is null, no information will be passed
            // back to the caller - but we create a temporary state to record
            // the plot area, since that is used later by the axes
            state = new PlotRenderingInfo(null);
        }
        state.setPlotArea(area);
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        // calculate the data area...
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        dataArea = integerise(dataArea);
        if (dataArea.isEmpty()) {
            return;
        }
        state.setDataArea(dataArea);
        createAndAddEntity((Rectangle2D) dataArea.clone(), state, null, null);
        // if there is a renderer, it draws the background, otherwise use the
        // default background...
        if (getRenderer() != null) {
            getRenderer().drawBackground(g2, this, dataArea);
        } else {
            drawBackground(g2, dataArea);
        }
        Map<Axis, AxisState> axisStateMap = drawAxes(g2, area, dataArea, state);
        // the anchor point is typically the point where the mouse last
        // clicked - the crosshairs will be driven off this point...
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = ShapeUtils.getPointInRectangle(anchor.getX(), anchor.getY(), dataArea);
        }
        CategoryCrosshairState<R, C> crosshairState = new CategoryCrosshairState<>();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);
        // specify the anchor X and Y coordinates in Java2D space, for the
        // cases where these are not updated during rendering (i.e. no lock
        // on data)
        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (getOrientation() == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
                } else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setRowKey(getDomainCrosshairRowKey());
        crosshairState.setColumnKey(getDomainCrosshairColumnKey());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        // don't let anyone draw outside the data area
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        drawDomainGridlines(g2, dataArea);
        AxisState rangeAxisState = axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = parentState.getSharedAxisStates().get(getRangeAxis());
            }
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
        // draw the markers...
        for (CategoryItemRenderer renderer : this.renderers.values()) {
            int i = getIndexOf(renderer);
            drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        for (CategoryItemRenderer renderer : this.renderers.values()) {
            int i = getIndexOf(renderer);
            drawRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        // now render data items...
        boolean foundData = false;
        // set up the alpha-transparency...
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        List<Integer> datasetIndices = getDatasetIndices(order);
        for (int i : datasetIndices) {
            foundData = render(g2, dataArea, i, state, crosshairState) || foundData;
        }
        // draw the foreground markers...
        List<Integer> rendererIndices = getRendererIndices(order);
        for (int i : rendererIndices) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i : rendererIndices) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        // draw the annotations (if any)...
        drawAnnotations(g2, dataArea);
        if (this.shadowGenerator != null && !suppressShadow) {
            BufferedImage shadowImage = this.shadowGenerator.createDropShadow(dataImage);
            g2 = savedG2;
            g2.drawImage(shadowImage, (int) dataArea.getX() + this.shadowGenerator.calculateOffsetX(), (int) dataArea.getY() + this.shadowGenerator.calculateOffsetY(), null);
            g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }
        int datasetIndex = crosshairState.getDatasetIndex();
        setCrosshairDatasetIndex(datasetIndex, false);
        // draw domain crosshair if required...
        R rowKey = crosshairState.getRowKey();
        C columnKey = crosshairState.getColumnKey();
        setDomainCrosshairRowKey(rowKey, false);
        setDomainCrosshairColumnKey(columnKey, false);
        if (isDomainCrosshairVisible() && columnKey != null) {
            Paint paint = getDomainCrosshairPaint();
            Stroke stroke = getDomainCrosshairStroke();
            drawDomainCrosshair(g2, dataArea, this.orientation, datasetIndex, rowKey, columnKey, stroke, paint);
        }
        // draw range crosshair if required...
        ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
        RectangleEdge yAxisEdge = getRangeAxisEdge();
        if (!this.rangeCrosshairLockedOnData && anchor != null) {
            double yy;
            if (getOrientation() == PlotOrientation.VERTICAL) {
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
            drawRangeCrosshair(g2, dataArea, getOrientation(), y, yAxis, stroke, paint);
        }
        // draw an outline around the plot area...
        if (isOutlineVisible()) {
            if (getRenderer() != null) {
                getRenderer().drawOutline(g2, this, dataArea);
            } else {
                drawOutline(g2, dataArea);
            }
        }
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
        for (Map.Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
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
     * Returns the indices of the non-null renderers for the plot, in the
     * specified order.
     *
     * @param order  the rendering order {@code null} not permitted).
     *
     * @return A list of indices.
     */
    private List<Integer> getRendererIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
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
     * Draws the plot background (the background color and/or image).
     * <P>
     * This method will be called during the chart drawing process and is
     * declared public so that it can be accessed by the renderers used by
     * certain subclasses.  You shouldn't need to call this method directly.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    @Override
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, this.orientation);
        drawBackgroundImage(g2, area);
    }

    /**
     * A utility method for drawing the plot's axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param plotState  collects information about the plot ({@code null}
     *                   permitted).
     *
     * @return A map containing the axis states.
     */
    protected Map<Axis, AxisState> drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisCollection axisCollection = new AxisCollection();
        // add domain axes to lists...
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                int index = getDomainAxisIndex(xAxis);
                axisCollection.add(xAxis, getDomainAxisEdge(index));
            }
        }
        // add range axes to lists...
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                int index = findRangeAxisIndex(yAxis);
                axisCollection.add(yAxis, getRangeAxisEdge(index));
            }
        }
        Map<Axis, AxisState> axisStateMap = new HashMap<>();
        // draw the top axes
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtTop()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtBottom()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtLeft()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtRight()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        return axisStateMap;
    }

    /**
     * Draws a representation of a dataset within the dataArea region using the
     * appropriate renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param index  the dataset and renderer index.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  a state object for tracking crosshair info
     *        ({@code null} permitted).
     *
     * @return A boolean that indicates whether real data was found.
     */
    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CategoryCrosshairState<R, C> crosshairState) {
        boolean foundData = false;
        CategoryDataset<R, C> currentDataset = getDataset(index);
        CategoryItemRenderer renderer = getRenderer(index);
        CategoryAxis domainAxis = getDomainAxisForDataset(index);
        ValueAxis rangeAxis = getRangeAxisForDataset(index);
        boolean hasData = !DatasetUtils.isEmptyOrNull(currentDataset);
        if (hasData && renderer != null) {
            foundData = true;
            CategoryItemRendererState state = renderer.initialise(g2, dataArea, this, index, info);
            state.setCrosshairState(crosshairState);
            int columnCount = currentDataset.getColumnCount();
            int rowCount = currentDataset.getRowCount();
            int passCount = renderer.getPassCount();
            for (int pass = 0; pass < passCount; pass++) {
                if (this.columnRenderingOrder == SortOrder.ASCENDING) {
                    for (int column = 0; column < columnCount; column++) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        } else {
                            for (int row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                } else {
                    for (int column = columnCount - 1; column >= 0; column--) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        } else {
                            for (int row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                }
            }
        }
        return foundData;
    }

    /**
     * Draws the domain gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     *
     * @see #drawRangeGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea) {
        if (!isDomainGridlinesVisible()) {
            return;
        }
        CategoryAnchor anchor = getDomainGridlinePosition();
        RectangleEdge domainAxisEdge = getDomainAxisEdge();
        CategoryDataset<R, C> dataset = getDataset();
        if (dataset == null) {
            return;
        }
        CategoryAxis axis = getDomainAxis();
        if (axis != null) {
            int columnCount = dataset.getColumnCount();
            for (int c = 0; c < columnCount; c++) {
                double xx = axis.getCategoryJava2DCoordinate(anchor, c, columnCount, dataArea, domainAxisEdge);
                CategoryItemRenderer renderer1 = getRenderer();
                if (renderer1 != null) {
                    renderer1.drawDomainGridline(g2, this, dataArea, xx);
                }
            }
        }
    }

    /**
     * Draws the range gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param dataArea  the area inside the axes ({@code null} not permitted).
     * @param ticks  the ticks.
     *
     * @see #drawDomainGridlines(Graphics2D, Rectangle2D)
     */
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        // draw the range grid lines, if any...
        if (!isRangeGridlinesVisible() && !isRangeMinorGridlinesVisible()) {
            return;
        }
        // no axis, no gridlines...
        ValueAxis axis = getRangeAxis();
        if (axis == null) {
            return;
        }
        // no renderer, no gridlines...
        CategoryItemRenderer r = getRenderer();
        if (r == null) {
            return;
        }
        Stroke gridStroke = null;
        Paint gridPaint = null;
        boolean paintLine;
        for (ValueTick tick : ticks) {
            paintLine = false;
            if ((tick.getTickType() == TickType.MINOR) && isRangeMinorGridlinesVisible()) {
                gridStroke = getRangeMinorGridlineStroke();
                gridPaint = getRangeMinorGridlinePaint();
                paintLine = true;
            } else if ((tick.getTickType() == TickType.MAJOR) && isRangeGridlinesVisible()) {
                gridStroke = getRangeGridlineStroke();
                gridPaint = getRangeGridlinePaint();
                paintLine = true;
            }
            if (((tick.getValue() != 0.0) || !isRangeZeroBaselineVisible()) && paintLine) {
                r.drawRangeLine(g2, this, axis, dataArea, tick.getValue(), gridPaint, gridStroke);
            }
        }
    }

    /**
     * Draws a base line across the chart at value zero on the range axis.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     *
     * @see #setRangeZeroBaselineVisible(boolean)
     */
    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (!isRangeZeroBaselineVisible()) {
            return;
        }
        CategoryItemRenderer r = getRenderer();
        r.drawRangeLine(g2, this, getRangeAxis(), area, 0.0, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
    }

    /**
     * Draws the annotations.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    protected void drawAnnotations(Graphics2D g2, Rectangle2D dataArea) {
        if (getAnnotations() != null) {
            for (CategoryAnnotation annotation : getAnnotations()) {
                annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
            }
        }
    }

    /**
     * Draws the domain markers (if any) for an axis and layer.  This method is
     * typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the renderer index.
     * @param layer  the layer (foreground or background).
     *
     * @see #drawRangeMarkers(Graphics2D, Rectangle2D, int, Layer)
     */
    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        CategoryItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        Collection<CategoryMarker> markers = getDomainMarkers(index, layer);
        CategoryAxis axis = getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            for (CategoryMarker marker : markers) {
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    /**
     * Draws the range markers (if any) for an axis and layer.  This method is
     * typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the renderer index.
     * @param layer  the layer (foreground or background).
     *
     * @see #drawDomainMarkers(Graphics2D, Rectangle2D, int, Layer)
     */
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        CategoryItemRenderer r = getRenderer(index);
        if (r == null) {
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
     * Utility method for drawing a line perpendicular to the range axis (used
     * for crosshairs).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param value  the data value.
     * @param stroke  the line stroke ({@code null} not permitted).
     * @param paint  the line paint ({@code null} not permitted).
     */
    protected void drawRangeLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        double java2D = getRangeAxis().valueToJava2D(value, dataArea, getRangeAxisEdge());
        Line2D line = null;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
        } else if (this.orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }

    /**
     * Draws a domain crosshair.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param orientation  the plot orientation.
     * @param datasetIndex  the dataset index.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param stroke  the stroke used to draw the crosshair line.
     * @param paint  the paint used to draw the crosshair line.
     *
     * @see #drawRangeCrosshair(Graphics2D, Rectangle2D, PlotOrientation,
     *     double, ValueAxis, Stroke, Paint)
     */
    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, int datasetIndex, R rowKey, C columnKey, Stroke stroke, Paint paint) {
        CategoryDataset<R, C> dataset = getDataset(datasetIndex);
        CategoryAxis axis = getDomainAxisForDataset(datasetIndex);
        CategoryItemRenderer renderer = getRenderer(datasetIndex);
        Line2D line;
        if (orientation == PlotOrientation.VERTICAL) {
            double xx = renderer.getItemMiddle(rowKey, columnKey, dataset, axis, dataArea, RectangleEdge.BOTTOM);
            line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        } else {
            double yy = renderer.getItemMiddle(rowKey, columnKey, dataset, axis, dataArea, RectangleEdge.LEFT);
            line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
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
     *
     * @see #drawDomainCrosshair(Graphics2D, Rectangle2D, PlotOrientation, int,
     *      Comparable, Comparable, Stroke, Paint)
     */
    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (!axis.getRange().contains(value)) {
            return;
        }
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
    }

    /**
     * Returns the range of data values that will be plotted against the range
     * axis.  If the dataset is {@code null}, this method returns
     * {@code null}.
     *
     * @param axis  the axis.
     *
     * @return The data range.
     */
    @Override
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        List<CategoryDataset<R, C>> mappedDatasets = new ArrayList<>();
        int rangeIndex = findRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            mappedDatasets.addAll(datasetsMappedToRangeAxis(rangeIndex));
        } else if (axis == getRangeAxis()) {
            mappedDatasets.addAll(datasetsMappedToRangeAxis(0));
        }
        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
        for (CategoryDataset<R, C> d : mappedDatasets) {
            CategoryItemRenderer r = getRendererForDataset(d);
            if (r != null) {
                result = Range.combine(result, r.findRangeBounds(d));
            }
        }
        return result;
    }

    /**
     * Returns a list of the datasets that are mapped to the axis with the
     * specified index.
     *
     * @param axisIndex  the axis index.
     *
     * @return The list (possibly empty, but never {@code null}).
     */
    private List<CategoryDataset<R, C>> datasetsMappedToDomainAxis(int axisIndex) {
        List<CategoryDataset<R, C>> result = new ArrayList<>();
        for (Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
            CategoryDataset<R, C> dataset = entry.getValue();
            if (dataset == null) {
                continue;
            }
            Integer datasetIndex = entry.getKey();
            List<Integer> mappedAxes = this.datasetToDomainAxesMap.get(datasetIndex);
            if (mappedAxes == null) {
                if (axisIndex == 0) {
                    result.add(dataset);
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(dataset);
                }
            }
        }
        return result;
    }

    /**
     * A utility method that returns a list of datasets that are mapped to a
     * given range axis.
     *
     * @param axisIndex  the axis index.
     *
     * @return The list (possibly empty, but never {@code null}).
     */
    private List<CategoryDataset<R, C>> datasetsMappedToRangeAxis(int axisIndex) {
        List<CategoryDataset<R, C>> result = new ArrayList<>();
        for (Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
            Integer datasetIndex = entry.getKey();
            CategoryDataset<R, C> dataset = entry.getValue();
            List<Integer> mappedAxes = this.datasetToRangeAxesMap.get(datasetIndex);
            if (mappedAxes == null) {
                if (axisIndex == 0) {
                    result.add(dataset);
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(dataset);
                }
            }
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
     * Sets the fixed domain axis space and sends a {@link PlotChangeEvent} to
     * all registered listeners.
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
     * Sets the fixed range axis space and sends a {@link PlotChangeEvent} to
     * all registered listeners.
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
     * Returns a list of the categories in the plot's primary dataset.
     *
     * @return A list of the categories in the plot's primary dataset.
     *
     * @see #getCategoriesForAxis(CategoryAxis)
     */
    public List<C> getCategories() {
        List<C> result = null;
        if (getDataset() != null) {
            result = Collections.unmodifiableList(getDataset().getColumnKeys());
        }
        return result;
    }

    /**
     * Returns a list of the categories that should be displayed for the
     * specified axis.
     *
     * @param axis  the axis ({@code null} not permitted)
     *
     * @return The categories.
     */
    public List<C> getCategoriesForAxis(CategoryAxis axis) {
        List<C> result = new ArrayList<>();
        int axisIndex = getDomainAxisIndex(axis);
        for (CategoryDataset<R, C> dataset : datasetsMappedToDomainAxis(axisIndex)) {
            // add the unique categories from this dataset
            for (int i = 0; i < dataset.getColumnCount(); i++) {
                C category = dataset.getColumnKey(i);
                if (!result.contains(category)) {
                    result.add(category);
                }
            }
        }
        return result;
    }

    /**
     * Returns the flag that controls whether the shared domain axis is
     * drawn for each subplot.
     *
     * @return A boolean.
     *
     * @see #setDrawSharedDomainAxis(boolean)
     */
    public boolean getDrawSharedDomainAxis() {
        return this.drawSharedDomainAxis;
    }

    /**
     * Sets the flag that controls whether the shared domain axis is drawn when
     * this plot is being used as a subplot.
     *
     * @param draw  a boolean.
     *
     * @see #getDrawSharedDomainAxis()
     */
    public void setDrawSharedDomainAxis(boolean draw) {
        this.drawSharedDomainAxis = draw;
        fireChangeEvent();
    }

    /**
     * Returns {@code false} always, because the plot cannot be panned
     * along the domain axis/axes.
     *
     * @return A boolean.
     *
     * @see #isRangePannable()
     */
    @Override
    public boolean isDomainPannable() {
        return false;
    }

    /**
     * Returns {@code true} if panning is enabled for the range axes,
     * and {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangePannable(boolean)
     * @see #isDomainPannable()
     */
    @Override
    public boolean isRangePannable() {
        return this.rangePannable;
    }

    /**
     * Sets the flag that enables or disables panning of the plot along
     * the range axes.
     *
     * @param pannable  the new flag value.
     *
     * @see #isRangePannable()
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
        // do nothing, because the plot is not pannable along the domain axes
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
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis == null) {
                continue;
            }
            double length = axis.getRange().getLength();
            double adj = percent * length;
            if (axis.isInverted()) {
                adj = -adj;
            }
            axis.setRange(axis.getLowerBound() + adj, axis.getUpperBound() + adj);
        }
    }

    /**
     * Returns {@code false} to indicate that the domain axes are not
     * zoomable.
     *
     * @return A boolean.
     *
     * @see #isRangeZoomable()
     */
    @Override
    public boolean isDomainZoomable() {
        return false;
    }

    /**
     * Returns {@code true} to indicate that the range axes are zoomable.
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
     * This method does nothing, because {@code CategoryPlot} doesn't
     * support zooming on the domain.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
        // can't zoom domain axis
    }

    /**
     * This method does nothing, because {@code CategoryPlot} doesn't
     * support zooming on the domain.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        // can't zoom domain axis
    }

    /**
     * This method does nothing, because {@code CategoryPlot} doesn't
     * support zooming on the domain.
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
        // can't zoom domain axis
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        // delegate to other method
        zoomRangeAxes(factor, state, source, false);
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
        for (ValueAxis rangeAxis : this.rangeAxes.values()) {
            if (rangeAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceY = source.getY();
                if (this.orientation.isHorizontal()) {
                    sourceY = source.getX();
                }
                double anchorY = rangeAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge());
                rangeAxis.resizeRange2(factor, anchorY);
            } else {
                rangeAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the range axes.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Returns the anchor value.
     *
     * @return The anchor value.
     *
     * @see #setAnchorValue(double)
     */
    public double getAnchorValue() {
        return this.anchorValue;
    }

    /**
     * Sets the anchor value and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param value  the anchor value.
     *
     * @see #getAnchorValue()
     */
    public void setAnchorValue(double value) {
        setAnchorValue(value, true);
    }

    /**
     * Sets the anchor value and, if requested, sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param value  the value.
     * @param notify  notify listeners?
     *
     * @see #getAnchorValue()
     */
    public void setAnchorValue(double value, boolean notify) {
        this.anchorValue = value;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Tests the plot for equality with an arbitrary object.
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
        if (!(obj instanceof CategoryPlot)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        CategoryPlot<R, C> that = (CategoryPlot) obj;
        if (this.orientation != that.orientation) {
            return false;
        }
        if (!Objects.equals(this.axisOffset, that.axisOffset)) {
            return false;
        }
        if (!Objects.equals(this.domainAxes, that.domainAxes)) {
            return false;
        }
        if (!Objects.equals(this.domainAxisLocations, that.domainAxisLocations)) {
            return false;
        }
        if (this.drawSharedDomainAxis != that.drawSharedDomainAxis) {
            return false;
        }
        if (!Objects.equals(this.rangeAxes, that.rangeAxes)) {
            return false;
        }
        if (!Objects.equals(this.rangeAxisLocations, that.rangeAxisLocations)) {
            return false;
        }
        if (!Objects.equals(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.renderers, that.renderers)) {
            return false;
        }
        if (!Objects.equals(this.renderingOrder, that.renderingOrder)) {
            return false;
        }
        if (this.columnRenderingOrder != that.columnRenderingOrder) {
            return false;
        }
        if (!Objects.equals(this.rowRenderingOrder, that.rowRenderingOrder)) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (this.rangePannable != that.rangePannable) {
            return false;
        }
        if (this.domainGridlinePosition != that.domainGridlinePosition) {
            return false;
        }
        if (!Objects.equals(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (this.rangeGridlinesVisible != that.rangeGridlinesVisible) {
            return false;
        }
        if (!Objects.equals(this.rangeGridlineStroke, that.rangeGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        if (this.anchorValue != that.anchorValue) {
            return false;
        }
        if (this.rangeCrosshairVisible != that.rangeCrosshairVisible) {
            return false;
        }
        if (this.rangeCrosshairValue != that.rangeCrosshairValue) {
            return false;
        }
        if (!Objects.equals(this.rangeCrosshairStroke, that.rangeCrosshairStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint)) {
            return false;
        }
        if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData) {
            return false;
        }
        if (!Objects.equals(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
            return false;
        }
        if (!Objects.equals(this.datasets, that.datasets)) {
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
        if (this.weight != that.weight) {
            return false;
        }
        if (!Objects.equals(this.fixedDomainAxisSpace, that.fixedDomainAxisSpace)) {
            return false;
        }
        if (!Objects.equals(this.fixedRangeAxisSpace, that.fixedRangeAxisSpace)) {
            return false;
        }
        if (!Objects.equals(this.fixedLegendItems, that.fixedLegendItems)) {
            return false;
        }
        if (this.domainCrosshairVisible != that.domainCrosshairVisible) {
            return false;
        }
        if (this.crosshairDatasetIndex != that.crosshairDatasetIndex) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairColumnKey, that.domainCrosshairColumnKey)) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairRowKey, that.domainCrosshairRowKey)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainCrosshairPaint, that.domainCrosshairPaint)) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairStroke, that.domainCrosshairStroke)) {
            return false;
        }
        if (this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke)) {
            return false;
        }
        if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke)) {
            return false;
        }
        if (!Objects.equals(this.shadowGenerator, that.shadowGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 71 * hash + Objects.hashCode(this.orientation);
        hash = 71 * hash + Objects.hashCode(this.axisOffset);
        hash = 71 * hash + Objects.hashCode(this.domainAxes);
        hash = 71 * hash + Objects.hashCode(this.domainAxisLocations);
        hash = 71 * hash + (this.drawSharedDomainAxis ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeAxes);
        hash = 71 * hash + Objects.hashCode(this.rangeAxisLocations);
        hash = 71 * hash + Objects.hashCode(this.datasets);
        hash = 71 * hash + Objects.hashCode(this.datasetToDomainAxesMap);
        hash = 71 * hash + Objects.hashCode(this.datasetToRangeAxesMap);
        hash = 71 * hash + Objects.hashCode(this.renderers);
        hash = 71 * hash + Objects.hashCode(this.renderingOrder);
        hash = 71 * hash + Objects.hashCode(this.columnRenderingOrder);
        hash = 71 * hash + Objects.hashCode(this.rowRenderingOrder);
        hash = 71 * hash + (this.domainGridlinesVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.domainGridlinePosition);
        hash = 71 * hash + Objects.hashCode(this.domainGridlineStroke);
        hash = 71 * hash + Objects.hashCode(this.domainGridlinePaint);
        hash = 71 * hash + (this.rangeZeroBaselineVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeZeroBaselineStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeZeroBaselinePaint);
        hash = 71 * hash + (this.rangeGridlinesVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeGridlineStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeGridlinePaint);
        hash = 71 * hash + (this.rangeMinorGridlinesVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeMinorGridlineStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeMinorGridlinePaint);
        hash = 71 * hash + Long.hashCode(Double.doubleToLongBits(this.anchorValue));
        hash = 71 * hash + this.crosshairDatasetIndex;
        hash = 71 * hash + (this.domainCrosshairVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairRowKey);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairColumnKey);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairStroke);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairPaint);
        hash = 71 * hash + (this.rangeCrosshairVisible ? 1 : 0);
        hash = 71 * hash + Long.hashCode(Double.doubleToLongBits(this.rangeCrosshairValue));
        hash = 71 * hash + Objects.hashCode(this.rangeCrosshairStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeCrosshairPaint);
        hash = 71 * hash + (this.rangeCrosshairLockedOnData ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.foregroundDomainMarkers);
        hash = 71 * hash + Objects.hashCode(this.backgroundDomainMarkers);
        hash = 71 * hash + Objects.hashCode(this.foregroundRangeMarkers);
        hash = 71 * hash + Objects.hashCode(this.backgroundRangeMarkers);
        hash = 71 * hash + Objects.hashCode(this.annotations);
        hash = 71 * hash + this.weight;
        hash = 71 * hash + Objects.hashCode(this.fixedDomainAxisSpace);
        hash = 71 * hash + Objects.hashCode(this.fixedRangeAxisSpace);
        hash = 71 * hash + Objects.hashCode(this.fixedLegendItems);
        hash = 71 * hash + (this.rangePannable ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.shadowGenerator);
        return hash;
    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the cloning is not supported.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        CategoryPlot<R, C> clone = (CategoryPlot) super.clone();
        clone.domainAxes = CloneUtils.cloneMapValues(this.domainAxes);
        for (CategoryAxis axis : clone.domainAxes.values()) {
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
        // AxisLocation is immutable, so we can just copy the maps
        clone.domainAxisLocations = new HashMap<>(this.domainAxisLocations);
        clone.rangeAxisLocations = new HashMap<>(this.rangeAxisLocations);
        clone.datasets = new HashMap<>(this.datasets);
        for (CategoryDataset<R, C> dataset : clone.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxesMap = new TreeMap<>();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap<>();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = CloneUtils.cloneMapValues(this.renderers);
        for (CategoryItemRenderer renderer : clone.renderers.values()) {
            if (renderer != null) {
                renderer.setPlot(clone);
                renderer.addChangeListener(clone);
            }
        }
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = CloneUtils.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = CloneUtils.clone(this.fixedRangeAxisSpace);
        }
        clone.annotations = CloneUtils.cloneList(this.annotations);
        clone.foregroundDomainMarkers = CloneUtils.cloneMapValues(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = CloneUtils.cloneMapValues(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = CloneUtils.cloneMapValues(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = CloneUtils.cloneMapValues(this.backgroundRangeMarkers);
        if (this.fixedLegendItems != null) {
            clone.fixedLegendItems = CloneUtils.clone(this.fixedLegendItems);
        }
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
        SerialUtils.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtils.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtils.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtils.writePaint(this.domainCrosshairPaint, stream);
        SerialUtils.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtils.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtils.writePaint(this.rangeZeroBaselinePaint, stream);
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
        this.rangeCrosshairStroke = SerialUtils.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtils.readPaint(stream);
        this.domainCrosshairStroke = SerialUtils.readStroke(stream);
        this.domainCrosshairPaint = SerialUtils.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtils.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtils.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtils.readPaint(stream);
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.setPlot(this);
                xAxis.addChangeListener(this);
            }
        }
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.setPlot(this);
                yAxis.addChangeListener(this);
            }
        }
        for (CategoryDataset<R, C> dataset : this.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (CategoryItemRenderer renderer : this.renderers.values()) {
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
 */
/**
 * A component for choosing a stroke from a list of available strokes.  This
 * class needs work.
 */
public class StrokeChooserPanel extends JPanel {

    /**
     * A combo for selecting the stroke.
     */
    private JComboBox selector;

    /**
     * Creates a panel containing a combo-box that allows the user to select
     * one stroke from a list of available strokes.
     *
     * @param current  the current stroke sample.
     * @param available  an array of 'available' stroke samples.
     */
    public StrokeChooserPanel(StrokeSample current, StrokeSample[] available) {
        setLayout(new BorderLayout());
        // we've changed the behaviour here to populate the combo box
        // with Stroke objects directly - ideally we'd change the signature
        // of the constructor too...maybe later.
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (int i = 0; i < available.length; i++) {
            model.addElement(available[i].getStroke());
        }
        this.selector = new JComboBox(model);
        this.selector.setSelectedItem(current.getStroke());
        this.selector.setRenderer(new StrokeSample(null));
        add(this.selector);
        // Changes due to focus problems!! DZ
        this.selector.addActionListener((ActionEvent evt) -> {
            getSelector().transferFocus();
        });
    }

    /**
     * Returns the selector component.
     *
     * @return Returns the selector.
     */
    protected final JComboBox getSelector() {
        return this.selector;
    }

    /**
     * Returns the selected stroke.
     *
     * @return The selected stroke (possibly {@code null}).
     */
    public Stroke getSelectedStroke() {
        return (Stroke) this.selector.getSelectedItem();
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
