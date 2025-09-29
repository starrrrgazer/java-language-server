package DEF.cs;
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
 * SymbolAxis.java
 * ---------------
 * (C) Copyright 2002-present, by Anthony Boulestreau and Contributors.
 *
 * Original Author:  Anthony Boulestreau;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A standard linear value axis that replaces integer values with symbols.
 */
public class SymbolAxis extends NumberAxis implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7216330468770619716L;

    /**
     * The default grid band paint.
     */
    public static final Paint DEFAULT_GRID_BAND_PAINT = new Color(232, 234, 232, 128);

    /**
     * The default paint for alternate grid bands.
     */
    public static final // transparent
    Paint // transparent
    DEFAULT_GRID_BAND_ALTERNATE_PAINT = new Color(0, 0, 0, 0);

    /**
     * The list of symbols to display instead of the numeric values.
     */
    private List<String> symbols;

    /**
     * Flag that indicates whether grid bands are visible.
     */
    private boolean gridBandsVisible;

    /**
     * The paint used to color the grid bands (if the bands are visible).
     */
    private transient Paint gridBandPaint;

    /**
     * The paint used to fill the alternate grid bands.
     */
    private transient Paint gridBandAlternatePaint;

    /**
     * Constructs a symbol axis, using default attribute values where
     * necessary.
     *
     * @param label  the axis label ({@code null} permitted).
     * @param sv  the list of symbols to display instead of the numeric
     *            values.
     */
    public SymbolAxis(String label, String[] sv) {
        super(label);
        this.symbols = Arrays.asList(sv);
        this.gridBandsVisible = true;
        this.gridBandPaint = DEFAULT_GRID_BAND_PAINT;
        this.gridBandAlternatePaint = DEFAULT_GRID_BAND_ALTERNATE_PAINT;
        setAutoTickUnitSelection(false, false);
        setAutoRangeStickyZero(false);
    }

    /**
     * Returns an array of the symbols for the axis.
     *
     * @return The symbols.
     */
    public String[] getSymbols() {
        String[] result = new String[this.symbols.size()];
        return this.symbols.toArray(result);
    }

    /**
     * Sets the list of symbols to display instead of the numeric values.
     *
     * @param symbols List of symbols.
     */
    public void setSymbols(String[] symbols) {
        this.symbols = Arrays.asList(symbols);
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether grid bands are drawn for
     * the axis.  The default value is {@code true}.
     *
     * @return A boolean.
     *
     * @see #setGridBandsVisible(boolean)
     */
    public boolean isGridBandsVisible() {
        return this.gridBandsVisible;
    }

    /**
     * Sets the flag that controls whether grid bands are drawn for this
     * axis and notifies registered listeners that the axis has been modified.
     * Each band is the area between two adjacent gridlines
     * running perpendicular to the axis.  When the bands are drawn they are
     * filled with the colors {@link #getGridBandPaint()} and
     * {@link #getGridBandAlternatePaint()} in an alternating sequence.
     *
     * @param flag  the new setting.
     *
     * @see #isGridBandsVisible()
     */
    public void setGridBandsVisible(boolean flag) {
        this.gridBandsVisible = flag;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to color grid bands (two colors are used
     * alternately, the other is returned by
     * {@link #getGridBandAlternatePaint()}).  The default value is
     * {@link #DEFAULT_GRID_BAND_PAINT}.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setGridBandPaint(Paint)
     * @see #isGridBandsVisible()
     */
    public Paint getGridBandPaint() {
        return this.gridBandPaint;
    }

    /**
     * Sets the grid band paint and notifies registered listeners that the
     * axis has been changed.  See the {@link #setGridBandsVisible(boolean)}
     * method for more information about grid bands.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getGridBandPaint()
     */
    public void setGridBandPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.gridBandPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the second paint used to color grid bands (two colors are used
     * alternately, the other is returned by {@link #getGridBandPaint()}).
     * The default value is {@link #DEFAULT_GRID_BAND_ALTERNATE_PAINT}
     * (transparent).
     *
     * @return The paint (never {@code null}).
     *
     * @see #setGridBandAlternatePaint(Paint)
     */
    public Paint getGridBandAlternatePaint() {
        return this.gridBandAlternatePaint;
    }

    /**
     * Sets the grid band paint and notifies registered listeners that the
     * axis has been changed.  See the {@link #setGridBandsVisible(boolean)}
     * method for more information about grid bands.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getGridBandAlternatePaint()
     * @see #setGridBandPaint(Paint)
     */
    public void setGridBandAlternatePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.gridBandAlternatePaint = paint;
        fireChangeEvent();
    }

    /**
     * This operation is not supported by this axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot and axes should be drawn.
     * @param edge  the edge along which the axis is drawn.
     */
    @Override
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        throw new UnsupportedOperationException();
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the plot and axes should be drawn
     *                  ({@code null} not permitted).
     * @param dataArea  the area within which the data should be drawn
     *                  ({@code null} not permitted).
     * @param edge  the axis location ({@code null} not permitted).
     * @param plotState  collects information about the plot
     *                   ({@code null} permitted).
     *
     * @return The axis state (never {@code null}).
     */
    @Override
    public AxisState draw(Graphics2D g2, double cursor, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, PlotRenderingInfo plotState) {
        AxisState info = new AxisState(cursor);
        if (isVisible()) {
            info = super.draw(g2, cursor, plotArea, dataArea, edge, plotState);
        }
        if (this.gridBandsVisible) {
            drawGridBands(g2, plotArea, dataArea, edge, info.getTicks());
        }
        return info;
    }

    /**
     * Draws the grid bands - alternate bands are colored using
     * {@link #getGridBandPaint()} and {@link #getGridBandAlternatePaint()}.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param plotArea  the area within which the plot is drawn
     *     ({@code null} not permitted).
     * @param dataArea  the data area to which the axes are aligned
     *     ({@code null} not permitted).
     * @param edge  the edge to which the axis is aligned ({@code null} not
     *     permitted).
     * @param ticks  the ticks ({@code null} not permitted).
     */
    protected void drawGridBands(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, List<? extends Tick> ticks) {
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        if (RectangleEdge.isTopOrBottom(edge)) {
            drawGridBandsHorizontal(g2, plotArea, dataArea, true, ticks);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            drawGridBandsVertical(g2, plotArea, dataArea, true, ticks);
        }
        g2.setClip(savedClip);
    }

    /**
     * Draws the grid bands for the axis when it is at the top or bottom of
     * the plot.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param plotArea  the area within which the plot is drawn (not used here).
     * @param dataArea  the area for the data (to which the axes are aligned,
     *         {@code null} not permitted).
     * @param firstGridBandIsDark  True: the first grid band takes the
     *                             color of {@code gridBandPaint}.
     *                             False: the second grid band takes the
     *                             color of {@code gridBandPaint}.
     * @param ticks  a list of ticks ({@code null} not permitted).
     */
    protected void drawGridBandsHorizontal(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, List<? extends Tick> ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double yy = dataArea.getY();
        double xx1, xx2;
        //gets the outline stroke width of the plot
        double outlineStrokeWidth = 1.0;
        Stroke outlineStroke = getPlot().getOutlineStroke();
        if (outlineStroke instanceof BasicStroke) {
            outlineStrokeWidth = ((BasicStroke) outlineStroke).getLineWidth();
        }
        Iterator<? extends Tick> iterator = ticks.iterator();
        ValueTick tick;
        Rectangle2D band;
        while (iterator.hasNext()) {
            tick = (ValueTick) iterator.next();
            xx1 = valueToJava2D(tick.getValue() - 0.5d, dataArea, RectangleEdge.BOTTOM);
            xx2 = valueToJava2D(tick.getValue() + 0.5d, dataArea, RectangleEdge.BOTTOM);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }
            band = new Rectangle2D.Double(Math.min(xx1, xx2), yy + outlineStrokeWidth, Math.abs(xx2 - xx1), dataArea.getMaxY() - yy - outlineStrokeWidth);
            g2.fill(band);
            currentGridBandIsDark = !currentGridBandIsDark;
        }
    }

    /**
     * Draws the grid bands for an axis that is aligned to the left or
     * right of the data area (that is, a vertical axis).
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param plotArea  the area within which the plot is drawn (not used here).
     * @param dataArea  the area for the data (to which the axes are aligned,
     *         {@code null} not permitted).
     * @param firstGridBandIsDark  True: the first grid band takes the
     *                             color of {@code gridBandPaint}.
     *                             False: the second grid band takes the
     *                             color of {@code gridBandPaint}.
     * @param ticks  a list of ticks ({@code null} not permitted).
     */
    protected void drawGridBandsVertical(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, boolean firstGridBandIsDark, List<? extends Tick> ticks) {
        boolean currentGridBandIsDark = firstGridBandIsDark;
        double xx = dataArea.getX();
        double yy1, yy2;
        //gets the outline stroke width of the plot
        double outlineStrokeWidth = 1.0;
        Stroke outlineStroke = getPlot().getOutlineStroke();
        if (outlineStroke instanceof BasicStroke) {
            outlineStrokeWidth = ((BasicStroke) outlineStroke).getLineWidth();
        }
        Iterator<? extends Tick> iterator = ticks.iterator();
        ValueTick tick;
        Rectangle2D band;
        while (iterator.hasNext()) {
            tick = (ValueTick) iterator.next();
            yy1 = valueToJava2D(tick.getValue() + 0.5d, dataArea, RectangleEdge.LEFT);
            yy2 = valueToJava2D(tick.getValue() - 0.5d, dataArea, RectangleEdge.LEFT);
            if (currentGridBandIsDark) {
                g2.setPaint(this.gridBandPaint);
            } else {
                g2.setPaint(this.gridBandAlternatePaint);
            }
            band = new Rectangle2D.Double(xx + outlineStrokeWidth, Math.min(yy1, yy2), dataArea.getMaxX() - xx - outlineStrokeWidth, Math.abs(yy2 - yy1));
            g2.fill(band);
            currentGridBandIsDark = !currentGridBandIsDark;
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
            // ensure that all the symbols are displayed
            double upper = this.symbols.size() - 1;
            double lower = 0;
            double range = upper - lower;
            // ensure the autorange is at least <minRange> in size...
            double minRange = getAutoRangeMinimumSize();
            if (range < minRange) {
                upper = (upper + lower + minRange) / 2;
                lower = (upper + lower - minRange) / 2;
            }
            // this ensure that the grid bands will be displayed correctly.
            double upperMargin = 0.5;
            double lowerMargin = 0.5;
            if (getAutoRangeIncludesZero()) {
                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = 0.0;
                    } else {
                        upper = upper + upperMargin;
                    }
                    if (lower >= 0.0) {
                        lower = 0.0;
                    } else {
                        lower = lower - lowerMargin;
                    }
                } else {
                    upper = Math.max(0.0, upper + upperMargin);
                    lower = Math.min(0.0, lower - lowerMargin);
                }
            } else {
                if (getAutoRangeStickyZero()) {
                    if (upper <= 0.0) {
                        upper = Math.min(0.0, upper + upperMargin);
                    } else {
                        upper = upper + upperMargin * range;
                    }
                    if (lower >= 0.0) {
                        lower = Math.max(0.0, lower - lowerMargin);
                    } else {
                        lower = lower - lowerMargin;
                    }
                } else {
                    upper = upper + upperMargin;
                    lower = lower - lowerMargin;
                }
            }
            setRange(new Range(lower, upper), false, false);
        }
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    @Override
    public List<Tick> refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List<Tick> ticks = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            ticks = refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            ticks = refreshTicksVertical(g2, dataArea, edge);
        }
        return ticks;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data should be drawn.
     * @param edge  the location of the axis.
     *
     * @return The ticks.
     */
    @Override
    protected List<Tick> refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List<Tick> ticks = new java.util.ArrayList<>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0;
        double previousDrawnTickLabelLength = 0.0;
        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double xx = valueToJava2D(currentTickValue, dataArea, edge);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = valueToString(currentTickValue);
                }
                // avoid to draw overlapping tick labels
                Rectangle2D bounds = TextUtils.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = isVerticalTickLabels() ? bounds.getHeight() : bounds.getWidth();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
                    if (Math.abs(xx - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    // don't draw this tick label
                    tickLabel = "";
                } else {
                    // remember these values for next comparison
                    previousDrawnTickLabelPos = xx;
                    previousDrawnTickLabelLength = tickLabelLength;
                }
                TextAnchor anchor;
                TextAnchor rotationAnchor;
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
                ticks.add(tick);
            }
        }
        return ticks;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return The ticks.
     */
    @Override
    protected List<Tick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List<Tick> ticks = new java.util.ArrayList<>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        double size = getTickUnit().getSize();
        int count = calculateVisibleTickCount();
        double lowestTickValue = calculateLowestVisibleTickValue();
        double previousDrawnTickLabelPos = 0.0;
        double previousDrawnTickLabelLength = 0.0;
        if (count <= ValueAxis.MAXIMUM_TICK_COUNT) {
            for (int i = 0; i < count; i++) {
                double currentTickValue = lowestTickValue + (i * size);
                double yy = valueToJava2D(currentTickValue, dataArea, edge);
                String tickLabel;
                NumberFormat formatter = getNumberFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(currentTickValue);
                } else {
                    tickLabel = valueToString(currentTickValue);
                }
                // avoid to draw overlapping tick labels
                Rectangle2D bounds = TextUtils.getTextBounds(tickLabel, g2, g2.getFontMetrics());
                double tickLabelLength = isVerticalTickLabels() ? bounds.getWidth() : bounds.getHeight();
                boolean tickLabelsOverlapping = false;
                if (i > 0) {
                    double avgTickLabelLength = (previousDrawnTickLabelLength + tickLabelLength) / 2.0;
                    if (Math.abs(yy - previousDrawnTickLabelPos) < avgTickLabelLength) {
                        tickLabelsOverlapping = true;
                    }
                }
                if (tickLabelsOverlapping) {
                    // don't draw this tick label
                    tickLabel = "";
                } else {
                    // remember these values for next comparison
                    previousDrawnTickLabelPos = yy;
                    previousDrawnTickLabelLength = tickLabelLength;
                }
                TextAnchor anchor;
                TextAnchor rotationAnchor;
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
                Tick tick = new NumberTick(currentTickValue, tickLabel, anchor, rotationAnchor, angle);
                ticks.add(tick);
            }
        }
        return ticks;
    }

    /**
     * Converts a value to a string, using the list of symbols.
     *
     * @param value  value to convert.
     *
     * @return The symbol.
     */
    public String valueToString(double value) {
        String strToReturn;
        try {
            strToReturn = this.symbols.get((int) value);
        } catch (IndexOutOfBoundsException ex) {
            strToReturn = "";
        }
        return strToReturn;
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
        if (!(obj instanceof SymbolAxis)) {
            return false;
        }
        SymbolAxis that = (SymbolAxis) obj;
        if (!this.symbols.equals(that.symbols)) {
            return false;
        }
        if (this.gridBandsVisible != that.gridBandsVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.gridBandPaint, that.gridBandPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.gridBandAlternatePaint, that.gridBandAlternatePaint)) {
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
        SerialUtils.writePaint(this.gridBandPaint, stream);
        SerialUtils.writePaint(this.gridBandAlternatePaint, stream);
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
        this.gridBandPaint = SerialUtils.readPaint(stream);
        this.gridBandAlternatePaint = SerialUtils.readPaint(stream);
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
 * AbstractOverlay.java
 * --------------------
 * (C) Copyright 2009-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A base class for implementing overlays for a {@link ChartPanel}.
 *
 * @since 1.0.13
 */
public class AbstractOverlay {

    /**
     * Storage for registered change listeners.
     */
    private final transient EventListenerList changeListeners;

    /**
     * Default constructor.
     */
    public AbstractOverlay() {
        this.changeListeners = new EventListenerList();
    }

    /**
     * Registers an object for notification of changes to the overlay.
     *
     * @param listener  the listener ({@code null} not permitted).
     *
     * @see #removeChangeListener(OverlayChangeListener)
     */
    public void addChangeListener(OverlayChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.changeListeners.add(OverlayChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the overlay.
     *
     * @param listener  the listener ({@code null} not permitted)
     *
     * @see #addChangeListener(OverlayChangeListener)
     */
    public void removeChangeListener(OverlayChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.changeListeners.remove(OverlayChangeListener.class, listener);
    }

    /**
     * Sends a default {@link ChartChangeEvent} to all registered listeners.
     * <P>
     * This method is for convenience only.
     */
    public void fireOverlayChanged() {
        OverlayChangeEvent event = new OverlayChangeEvent(this);
        notifyListeners(event);
    }

    /**
     * Sends a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the
     *               notification.
     */
    protected void notifyListeners(OverlayChangeEvent event) {
        Object[] listeners = this.changeListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == OverlayChangeListener.class) {
                ((OverlayChangeListener) listeners[i + 1]).overlayChanged(event);
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
 * ------------------------------
 * YIntervalSeriesCollection.java
 * ------------------------------
 * (C) Copyright 2006-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A collection of {@link YIntervalSeries} objects.
 *
 * @param <S> The type for the series keys.
 *
 * @see YIntervalSeries
 */
public class YIntervalSeriesCollection<S extends Comparable<S>> extends AbstractIntervalXYDataset<S> implements IntervalXYDataset<S>, PublicCloneable, Serializable {

    /**
     * Storage for the data series.
     */
    private List<YIntervalSeries> data;

    /**
     * Creates a new instance of {@code YIntervalSeriesCollection}.
     */
    public YIntervalSeriesCollection() {
        this.data = new ArrayList<>();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void addSeries(YIntervalSeries<S> series) {
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
    public YIntervalSeries<S> getSeries(int series) {
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
        YIntervalSeries s = this.data.get(series);
        return s.getX(item);
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
        YIntervalSeries s = this.data.get(series);
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
        YIntervalSeries s = this.data.get(series);
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
        YIntervalSeries s = this.data.get(series);
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
        YIntervalSeries s = this.data.get(series);
        return s.getYValue(item);
    }

    /**
     * Returns the start x-value for an item within a series.  This method
     * maps directly to {@link #getX(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getStartX(int series, int item) {
        return getX(series, item);
    }

    /**
     * Returns the end x-value for an item within a series.  This method
     * maps directly to {@link #getX(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getEndX(int series, int item) {
        return getX(series, item);
    }

    /**
     * Returns the start y-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The start y-value.
     */
    @Override
    public Number getStartY(int series, int item) {
        YIntervalSeries s = this.data.get(series);
        return s.getYLowValue(item);
    }

    /**
     * Returns the end y-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The end y-value.
     */
    @Override
    public Number getEndY(int series, int item) {
        YIntervalSeries s = this.data.get(series);
        return s.getYHighValue(item);
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     */
    public void removeSeries(int series) {
        Args.requireInRange(series, "series", 0, this.data.size() - 1);
        YIntervalSeries ts = this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void removeSeries(YIntervalSeries<S> series) {
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
     */
    public void removeAllSeries() {
        // Unregister the collection as a change listener to each series in
        // the collection.
        for (YIntervalSeries series : this.data) {
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
        if (!(obj instanceof YIntervalSeriesCollection)) {
            return false;
        }
        YIntervalSeriesCollection<S> that = (YIntervalSeriesCollection) obj;
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
        YIntervalSeriesCollection<S> clone = (YIntervalSeriesCollection) super.clone();
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
 * ChartPanel.java
 * ---------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
 *                   Soren Caspersen;
 *                   Jonathan Nash;
 *                   Hans-Jurgen Greiner;
 *                   Andreas Schneider;
 *                   Daniel van Enckevort;
 *                   David M O'Donnell;
 *                   Arnaud Lelievre;
 *                   Matthias Rose;
 *                   Onno vd Akker;
 *                   Sergei Ivanov;
 *                   Ulrich Voigt - patch 2686040;
 *                   Alessandro Borges - patch 1460845;
 *                   Martin Hoeller;
 *                   Simon Legner - patch from bug 1129;
 */
/**
 * A Swing GUI component for displaying a {@link JFreeChart} object.
 * <P>
 * The panel registers with the chart to receive notification of changes to any
 * component of the chart.  The chart is redrawn automatically whenever this
 * notification is received.
 */
@SuppressWarnings("unused")
public class ChartPanel extends JPanel implements ChartChangeListener, ChartProgressListener, ActionListener, MouseListener, MouseMotionListener, OverlayChangeListener, Printable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 6046366297214274674L;

    /**
     * Default setting for buffer usage.  The default has been changed to
     * {@code true} from version 1.0.13 onwards, because of a severe
     * performance problem with drawing the zoom rectangle using XOR (which
     * now happens only when the buffer is NOT used).
     */
    public static final boolean DEFAULT_BUFFER_USED = true;

    /**
     * The default panel width.
     */
    public static final int DEFAULT_WIDTH = 1024;

    /**
     * The default panel height.
     */
    public static final int DEFAULT_HEIGHT = 768;

    /**
     * The default limit below which chart scaling kicks in.
     */
    public static final int DEFAULT_MINIMUM_DRAW_WIDTH = 300;

    /**
     * The default limit below which chart scaling kicks in.
     */
    public static final int DEFAULT_MINIMUM_DRAW_HEIGHT = 200;

    /**
     * The default limit above which chart scaling kicks in.
     */
    public static final int DEFAULT_MAXIMUM_DRAW_WIDTH = 1024;

    /**
     * The default limit above which chart scaling kicks in.
     */
    public static final int DEFAULT_MAXIMUM_DRAW_HEIGHT = 768;

    /**
     * Properties action command.
     */
    public static final String PROPERTIES_COMMAND = "PROPERTIES";

    /**
     * Copy action command.
     */
    public static final String COPY_COMMAND = "COPY";

    /**
     * Save action command.
     */
    public static final String SAVE_COMMAND = "SAVE";

    /**
     * Action command to save as PNG.
     */
    protected static final String SAVE_AS_PNG_COMMAND = "SAVE_AS_PNG";

    /**
     * Action command to save as PNG - use screen size
     */
    protected static final String SAVE_AS_PNG_SIZE_COMMAND = "SAVE_AS_PNG_SIZE";

    /**
     * Action command to save as SVG.
     */
    protected static final String SAVE_AS_SVG_COMMAND = "SAVE_AS_SVG";

    /**
     * Action command to save as PDF.
     */
    protected static final String SAVE_AS_PDF_COMMAND = "SAVE_AS_PDF";

    /**
     * Print action command.
     */
    public static final String PRINT_COMMAND = "PRINT";

    /**
     * Zoom in (both axes) action command.
     */
    public static final String ZOOM_IN_BOTH_COMMAND = "ZOOM_IN_BOTH";

    /**
     * Zoom in (domain axis only) action command.
     */
    public static final String ZOOM_IN_DOMAIN_COMMAND = "ZOOM_IN_DOMAIN";

    /**
     * Zoom in (range axis only) action command.
     */
    public static final String ZOOM_IN_RANGE_COMMAND = "ZOOM_IN_RANGE";

    /**
     * Zoom out (both axes) action command.
     */
    public static final String ZOOM_OUT_BOTH_COMMAND = "ZOOM_OUT_BOTH";

    /**
     * Zoom out (domain axis only) action command.
     */
    public static final String ZOOM_OUT_DOMAIN_COMMAND = "ZOOM_DOMAIN_BOTH";

    /**
     * Zoom out (range axis only) action command.
     */
    public static final String ZOOM_OUT_RANGE_COMMAND = "ZOOM_RANGE_BOTH";

    /**
     * Zoom reset (both axes) action command.
     */
    public static final String ZOOM_RESET_BOTH_COMMAND = "ZOOM_RESET_BOTH";

    /**
     * Zoom reset (domain axis only) action command.
     */
    public static final String ZOOM_RESET_DOMAIN_COMMAND = "ZOOM_RESET_DOMAIN";

    /**
     * Zoom reset (range axis only) action command.
     */
    public static final String ZOOM_RESET_RANGE_COMMAND = "ZOOM_RESET_RANGE";

    // default modifiers for zooming, private to avoid constant inlining,
    // publicly available through getDefaultDragModifiersEx()
    private static final int DEFAULT_DRAG_MODIFIERS_EX;

    // mask for all modifier keys to check for
    private static final int MODIFIERS_EX_MASK = InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.ALT_DOWN_MASK;

    static {
        int dragModifiers = InputEvent.CTRL_DOWN_MASK;
        // for MacOSX we can't use the CTRL key for mouse drags, see:
        // http://developer.apple.com/qa/qa2004/qa1362.html
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("mac os x")) {
            dragModifiers = InputEvent.ALT_DOWN_MASK;
        }
        DEFAULT_DRAG_MODIFIERS_EX = dragModifiers;
    }

    /**
     * The standard mouse button modifiers for alternative drag operations.
     * There are two kinds of mouse drag operations: pan and zoom.
     * To distinguish between them, one needs to require modifier keys
     * to be held down during the dragging.  However, some modifiers
     * may not be usable on all platforms.  For example, on Mac OS X
     * it is impossible to perform Ctrl-drags or right-drags, see
     * <a href="http://developer.apple.com/qa/qa2004/qa1362.html">http://developer.apple.com/qa/qa2004/qa1362.html</a>.
     * This function returns a non-zero modifier usable for any platform:
     * Alt for Mac OS X, Ctrl for other platforms.  It is recommended
     * to use these modifiers for one operation, and zero modifiers for
     * the other.
     *
     * @return modifiers mask, as in {@link InputEvent#getModifiersEx()}
     * @see #setPanModifiersEx(int, int)
     * @see #setZoomModifiersEx(int, int)
     * @see #setDefaultPanModifiersEx(int)
     * @see #setDefaultZoomModifiersEx(int)
     */
    public static int getDefaultDragModifiersEx() {
        return DEFAULT_DRAG_MODIFIERS_EX;
    }

    /**
     * The chart that is displayed in the panel.
     */
    protected JFreeChart chart;

    /**
     * Storage for registered (chart) mouse listeners.
     */
    protected transient EventListenerList chartMouseListeners;

    /**
     * A flag that controls whether the off-screen buffer is used.
     */
    protected boolean useBuffer;

    /**
     * A flag that indicates that the buffer should be refreshed.
     */
    protected boolean refreshBuffer;

    /**
     * A buffer for the rendered chart.
     */
    protected transient Image chartBuffer;

    /**
     * The height of the chart buffer.
     */
    protected int chartBufferHeight;

    /**
     * The width of the chart buffer.
     */
    protected int chartBufferWidth;

    /**
     * The minimum width for drawing a chart (uses scaling for smaller widths).
     */
    protected int minimumDrawWidth;

    /**
     * The minimum height for drawing a chart (uses scaling for smaller
     * heights).
     */
    protected int minimumDrawHeight;

    /**
     * The maximum width for drawing a chart (uses scaling for bigger
     * widths).
     */
    protected int maximumDrawWidth;

    /**
     * The maximum height for drawing a chart (uses scaling for bigger
     * heights).
     */
    protected int maximumDrawHeight;

    /**
     * The popup menu for the frame.
     */
    protected JPopupMenu popup;

    /**
     * The drawing info collected the last time the chart was drawn.
     */
    protected ChartRenderingInfo info;

    /**
     * The chart anchor point.
     */
    protected Point2D anchor;

    /**
     * The scale factor used to draw the chart.
     */
    protected double scaleX;

    /**
     * The scale factor used to draw the chart.
     */
    protected double scaleY;

    /**
     * The plot orientation.
     */
    protected PlotOrientation orientation = PlotOrientation.VERTICAL;

    /**
     * A flag that controls whether domain zooming is enabled.
     */
    protected boolean domainZoomable = false;

    /**
     * A flag that controls whether range zooming is enabled.
     */
    protected boolean rangeZoomable = false;

    /**
     * A strategy to handle zoom rectangle processing and painting.
     */
    private SelectionZoomStrategy selectionZoomStrategy = new DefaultSelectionZoomStrategy();

    /**
     * Menu item for zooming in on a chart (both axes).
     */
    protected JMenuItem zoomInBothMenuItem;

    /**
     * Menu item for zooming in on a chart (domain axis).
     */
    protected JMenuItem zoomInDomainMenuItem;

    /**
     * Menu item for zooming in on a chart (range axis).
     */
    protected JMenuItem zoomInRangeMenuItem;

    /**
     * Menu item for zooming out on a chart.
     */
    protected JMenuItem zoomOutBothMenuItem;

    /**
     * Menu item for zooming out on a chart (domain axis).
     */
    protected JMenuItem zoomOutDomainMenuItem;

    /**
     * Menu item for zooming out on a chart (range axis).
     */
    protected JMenuItem zoomOutRangeMenuItem;

    /**
     * Menu item for resetting the zoom (both axes).
     */
    protected JMenuItem zoomResetBothMenuItem;

    /**
     * Menu item for resetting the zoom (domain axis only).
     */
    protected JMenuItem zoomResetDomainMenuItem;

    /**
     * Menu item for resetting the zoom (range axis only).
     */
    protected JMenuItem zoomResetRangeMenuItem;

    /**
     * The default directory for saving charts to file.
     */
    protected File defaultDirectoryForSaveAs;

    /**
     * A flag that controls whether file extensions are enforced.
     */
    protected boolean enforceFileExtensions;

    /**
     * A flag that indicates if original tooltip delays are changed.
     */
    protected boolean ownToolTipDelaysActive;

    /**
     * Original initial tooltip delay of ToolTipManager.sharedInstance().
     */
    protected int originalToolTipInitialDelay;

    /**
     * Original reshow tooltip delay of ToolTipManager.sharedInstance().
     */
    protected int originalToolTipReshowDelay;

    /**
     * Original dismiss tooltip delay of ToolTipManager.sharedInstance().
     */
    protected int originalToolTipDismissDelay;

    /**
     * Own initial tooltip delay to be used in this chart panel.
     */
    protected int ownToolTipInitialDelay;

    /**
     * Own reshow tooltip delay to be used in this chart panel.
     */
    protected int ownToolTipReshowDelay;

    /**
     * Own dismiss tooltip delay to be used in this chart panel.
     */
    protected int ownToolTipDismissDelay;

    /**
     * The factor used to zoom in on an axis range.
     */
    protected double zoomInFactor = 0.5;

    /**
     * The factor used to zoom out on an axis range.
     */
    protected double zoomOutFactor = 2.0;

    /**
     * A flag that controls whether zoom operations are centred on the
     * current anchor point, or the centre point of the relevant axis.
     */
    protected boolean zoomAroundAnchor;

    /**
     * The resourceBundle for the localization.
     */
    protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.LocalizationBundle");

    /**
     * Temporary storage for the width and height of the chart
     * drawing area during panning.
     */
    protected double panW, panH;

    /**
     * The last mouse position during panning.
     */
    protected Point panLast;

    /**
     * The default mask for mouse events to trigger panning.
     * Since 2.0.0, this mask uses extended modifiers, as returned
     * by {@link InputEvent#getModifiersEx()}.
     * Only used if no button-specific modifiers were set in
     * {@link #panButtonMasks}.
     */
    protected int panMask = getDefaultDragModifiersEx();

    /**
     * The default mask for mouse events to trigger zooming.
     *
     * @since 2.0.0
     */
    protected int zoomMask = 0;

    /**
     * The masks for mouse events to trigger panning, per mouse button.
     *
     * @since 2.0.0
     */
    protected final Map<Integer, Integer> panButtonMasks = new HashMap<>(3);

    /**
     * The masks for mouse events to trigger zooming, per mouse button.
     *
     * @since 2.0.0
     */
    protected final Map<Integer, Integer> zoomButtonMasks = new HashMap<>(3);

    /**
     * A list of overlays for the panel.
     */
    protected List<Overlay> overlays;

    /**
     * Constructs a panel that displays the specified chart.
     *
     * @param chart  the chart.
     */
    public ChartPanel(JFreeChart chart) {
        this(// properties
        chart, // properties
        DEFAULT_WIDTH, // properties
        DEFAULT_HEIGHT, // properties
        DEFAULT_MINIMUM_DRAW_WIDTH, // properties
        DEFAULT_MINIMUM_DRAW_HEIGHT, // properties
        DEFAULT_MAXIMUM_DRAW_WIDTH, // properties
        DEFAULT_MAXIMUM_DRAW_HEIGHT, // properties
        DEFAULT_BUFFER_USED, // save
        true, // print
        true, // zoom
        true, // tooltips
        true, true);
    }

    /**
     * Constructs a panel containing a chart.  The {@code useBuffer} flag
     * controls whether an offscreen {@code BufferedImage} is
     * maintained for the chart.  If the buffer is used, more memory is
     * consumed, but panel repaints will be a lot quicker in cases where the
     * chart itself hasn't changed (for example, when another frame is moved
     * to reveal the panel).  WARNING: If you set the {@code useBuffer}
     * flag to false, note that the mouse zooming rectangle will (in that case)
     * be drawn using XOR, and there is a SEVERE performance problem with that
     * on JRE6 on Windows.
     *
     * @param chart  the chart.
     * @param useBuffer  a flag controlling whether an off-screen buffer
     *                   is used (read the warning above before setting this
     *                   to {@code false}).
     */
    public ChartPanel(JFreeChart chart, boolean useBuffer) {
        this(// properties
        chart, // properties
        DEFAULT_WIDTH, // properties
        DEFAULT_HEIGHT, // properties
        DEFAULT_MINIMUM_DRAW_WIDTH, // properties
        DEFAULT_MINIMUM_DRAW_HEIGHT, // properties
        DEFAULT_MAXIMUM_DRAW_WIDTH, // properties
        DEFAULT_MAXIMUM_DRAW_HEIGHT, // properties
        useBuffer, // save
        true, // print
        true, // zoom
        true, // tooltips
        true, true);
    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param properties  a flag indicating whether the chart property
     *                    editor should be available via the popup menu.
     * @param save  a flag indicating whether save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether zoom options should
     *              be added to the popup menu.
     * @param tooltips  a flag indicating whether tooltips should be
     *                  enabled for the chart.
     */
    public ChartPanel(JFreeChart chart, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this(chart, DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_MINIMUM_DRAW_WIDTH, DEFAULT_MINIMUM_DRAW_HEIGHT, DEFAULT_MAXIMUM_DRAW_WIDTH, DEFAULT_MAXIMUM_DRAW_HEIGHT, DEFAULT_BUFFER_USED, properties, save, print, zoom, tooltips);
    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param width  the preferred width of the panel.
     * @param height  the preferred height of the panel.
     * @param minimumDrawWidth  the minimum drawing width.
     * @param minimumDrawHeight  the minimum drawing height.
     * @param maximumDrawWidth  the maximum drawing width.
     * @param maximumDrawHeight  the maximum drawing height.
     * @param useBuffer  a flag that indicates whether to use the off-screen
     *                   buffer to improve performance (at the expense of
     *                   memory).
     * @param properties  a flag indicating whether the chart property
     *                    editor should be available via the popup menu.
     * @param save  a flag indicating whether save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether zoom options should be
     *              added to the popup menu.
     * @param tooltips  a flag indicating whether tooltips should be
     *                  enabled for the chart.
     */
    public ChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean save, boolean print, boolean zoom, boolean tooltips) {
        this(chart, width, height, minimumDrawWidth, minimumDrawHeight, maximumDrawWidth, maximumDrawHeight, useBuffer, properties, true, save, print, zoom, tooltips);
    }

    /**
     * Constructs a JFreeChart panel.
     *
     * @param chart  the chart.
     * @param width  the preferred width of the panel.
     * @param height  the preferred height of the panel.
     * @param minimumDrawWidth  the minimum drawing width.
     * @param minimumDrawHeight  the minimum drawing height.
     * @param maximumDrawWidth  the maximum drawing width.
     * @param maximumDrawHeight  the maximum drawing height.
     * @param useBuffer  a flag that indicates whether to use the off-screen
     *                   buffer to improve performance (at the expense of
     *                   memory).
     * @param properties  a flag indicating whether the chart property
     *                    editor should be available via the popup menu.
     * @param copy  a flag indicating whether a copy option should be
     *              available via the popup menu.
     * @param save  a flag indicating whether save options should be
     *              available via the popup menu.
     * @param print  a flag indicating whether the print option
     *               should be available via the popup menu.
     * @param zoom  a flag indicating whether zoom options should be
     *              added to the popup menu.
     * @param tooltips  a flag indicating whether tooltips should be
     *                  enabled for the chart.
     */
    public ChartPanel(JFreeChart chart, int width, int height, int minimumDrawWidth, int minimumDrawHeight, int maximumDrawWidth, int maximumDrawHeight, boolean useBuffer, boolean properties, boolean copy, boolean save, boolean print, boolean zoom, boolean tooltips) {
        setChart(chart);
        this.chartMouseListeners = new EventListenerList();
        this.info = new ChartRenderingInfo();
        setPreferredSize(new Dimension(width, height));
        this.useBuffer = useBuffer;
        this.refreshBuffer = false;
        this.minimumDrawWidth = minimumDrawWidth;
        this.minimumDrawHeight = minimumDrawHeight;
        this.maximumDrawWidth = maximumDrawWidth;
        this.maximumDrawHeight = maximumDrawHeight;
        // set up popup menu...
        this.popup = null;
        if (properties || copy || save || print || zoom) {
            this.popup = createPopupMenu(properties, copy, save, print, zoom);
        }
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
        enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK);
        setDisplayToolTips(tooltips);
        addMouseListener(this);
        addMouseMotionListener(this);
        this.defaultDirectoryForSaveAs = null;
        this.enforceFileExtensions = true;
        // initialize ChartPanel-specific tool tip delays with
        // values the from ToolTipManager.sharedInstance()
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        this.ownToolTipInitialDelay = ttm.getInitialDelay();
        this.ownToolTipDismissDelay = ttm.getDismissDelay();
        this.ownToolTipReshowDelay = ttm.getReshowDelay();
        this.zoomAroundAnchor = false;
        this.overlays = new ArrayList<>();
    }

    /**
     * Returns the chart contained in the panel.
     *
     * @return The chart (possibly {@code null}).
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Sets the chart that is displayed in the panel.
     *
     * @param chart  the chart ({@code null} permitted).
     */
    public void setChart(JFreeChart chart) {
        // stop listening for changes to the existing chart
        if (this.chart != null) {
            this.chart.removeChangeListener(this);
            this.chart.removeProgressListener(this);
        }
        // add the new chart
        this.chart = chart;
        if (chart != null) {
            this.chart.addChangeListener(this);
            this.chart.addProgressListener(this);
            Plot plot = chart.getPlot();
            this.domainZoomable = false;
            this.rangeZoomable = false;
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable) plot;
                this.domainZoomable = z.isDomainZoomable();
                this.rangeZoomable = z.isRangeZoomable();
                this.orientation = z.getOrientation();
            }
        } else {
            this.domainZoomable = false;
            this.rangeZoomable = false;
        }
        if (this.useBuffer) {
            this.refreshBuffer = true;
        }
        repaint();
    }

    /**
     * Returns the minimum drawing width for charts.
     * <P>
     * If the width available on the panel is less than this, then the chart is
     * drawn at the minimum width then scaled down to fit.
     *
     * @return The minimum drawing width.
     */
    public int getMinimumDrawWidth() {
        return this.minimumDrawWidth;
    }

    /**
     * Sets the minimum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is
     * less than this amount, the chart will be drawn using the minimum width
     * then scaled down to fit the available space.
     *
     * @param width  The width.
     */
    public void setMinimumDrawWidth(int width) {
        this.minimumDrawWidth = width;
    }

    /**
     * Returns the maximum drawing width for charts.
     * <P>
     * If the width available on the panel is greater than this, then the chart
     * is drawn at the maximum width then scaled up to fit.
     *
     * @return The maximum drawing width.
     */
    public int getMaximumDrawWidth() {
        return this.maximumDrawWidth;
    }

    /**
     * Sets the maximum drawing width for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available width is
     * greater than this amount, the chart will be drawn using the maximum
     * width then scaled up to fit the available space.
     *
     * @param width  The width.
     */
    public void setMaximumDrawWidth(int width) {
        this.maximumDrawWidth = width;
    }

    /**
     * Returns the minimum drawing height for charts.
     * <P>
     * If the height available on the panel is less than this, then the chart
     * is drawn at the minimum height then scaled down to fit.
     *
     * @return The minimum drawing height.
     */
    public int getMinimumDrawHeight() {
        return this.minimumDrawHeight;
    }

    /**
     * Sets the minimum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is
     * less than this amount, the chart will be drawn using the minimum height
     * then scaled down to fit the available space.
     *
     * @param height  The height.
     */
    public void setMinimumDrawHeight(int height) {
        this.minimumDrawHeight = height;
    }

    /**
     * Returns the maximum drawing height for charts.
     * <P>
     * If the height available on the panel is greater than this, then the
     * chart is drawn at the maximum height then scaled up to fit.
     *
     * @return The maximum drawing height.
     */
    public int getMaximumDrawHeight() {
        return this.maximumDrawHeight;
    }

    /**
     * Sets the maximum drawing height for the chart on this panel.
     * <P>
     * At the time the chart is drawn on the panel, if the available height is
     * greater than this amount, the chart will be drawn using the maximum
     * height then scaled up to fit the available space.
     *
     * @param height  The height.
     */
    public void setMaximumDrawHeight(int height) {
        this.maximumDrawHeight = height;
    }

    /**
     * Returns the X scale factor for the chart.  This will be 1.0 if no
     * scaling has been used.
     *
     * @return The scale factor.
     */
    public double getScaleX() {
        return this.scaleX;
    }

    /**
     * Returns the Y scale factory for the chart.  This will be 1.0 if no
     * scaling has been used.
     *
     * @return The scale factor.
     */
    public double getScaleY() {
        return this.scaleY;
    }

    /**
     * Returns the anchor point.
     *
     * @return The anchor point (possibly {@code null}).
     */
    public Point2D getAnchor() {
        return this.anchor;
    }

    /**
     * Sets the anchor point.  This method is provided for the use of
     * subclasses, not end users.
     *
     * @param anchor  the anchor point ({@code null} permitted).
     */
    protected void setAnchor(Point2D anchor) {
        this.anchor = anchor;
    }

    /**
     * Returns the popup menu.
     *
     * @return The popup menu.
     */
    public JPopupMenu getPopupMenu() {
        return this.popup;
    }

    /**
     * Sets the popup menu for the panel.
     *
     * @param popup  the popup menu ({@code null} permitted).
     */
    public void setPopupMenu(JPopupMenu popup) {
        this.popup = popup;
    }

    /**
     * Returns the chart rendering info from the most recent chart redraw.
     *
     * @return The chart rendering info.
     */
    public ChartRenderingInfo getChartRenderingInfo() {
        return this.info;
    }

    /**
     * A convenience method that switches on mouse-based zooming.
     *
     * @param flag  {@code true} enables zooming and rectangle fill on
     *              zoom.
     */
    public void setMouseZoomable(boolean flag) {
        setMouseZoomable(flag, true);
    }

    /**
     * A convenience method that switches on mouse-based zooming.
     *
     * @param flag  {@code true} if zooming enabled
     * @param fillRectangle  {@code true} if zoom rectangle is filled,
     *                       false if rectangle is shown as outline only.
     */
    public void setMouseZoomable(boolean flag, boolean fillRectangle) {
        setDomainZoomable(flag);
        setRangeZoomable(flag);
        setFillZoomRectangle(fillRectangle);
    }

    /**
     * Sets default modifier keys for pan operations for all mouse buttons.
     * Modifiers for a specific button can be set with
     * {@link #setPanModifiersEx(int, int)}.  If there are none set for
     * a certain button, it will use the modifiers passed to this function,
     * defaulting to {@link #getDefaultDragModifiersEx()} if this function
     * was never called.
     * <p>
     * Only {@link InputEvent#SHIFT_DOWN_MASK}, {@link InputEvent#CTRL_DOWN_MASK},
     * {@link InputEvent#META_DOWN_MASK} and {@link InputEvent#ALT_DOWN_MASK} are
     * checked.  To avoid platform-specific problems, it is recommended to use
     * {@link #getDefaultDragModifiersEx()} for one operation, and zero modifiers
     * for the other.
     * <p>
     * If the same modifiers are set for both zooming and panning,
     * panning will be performed.
     *
     * @param modifiersEx modifier keys, as returned by {@link InputEvent#getModifiersEx()}
     */
    public void setDefaultPanModifiersEx(int modifiersEx) {
        this.panMask = modifiersEx;
    }

    /**
     * Sets default modifier keys for zoom operations for all mouse buttons.
     * Modifiers for a specific button can be set with
     * {@link #setZoomModifiersEx(int, int)}.  If there are none set for
     * a certain button, it will use the modifiers passed to this function,
     * defaulting to zero (no modifiers) if this function was never called.
     * <p>
     * Only {@link InputEvent#SHIFT_DOWN_MASK}, {@link InputEvent#CTRL_DOWN_MASK},
     * {@link InputEvent#META_DOWN_MASK} and {@link InputEvent#ALT_DOWN_MASK} are
     * checked.  To avoid platform-specific problems, it is recommended to use
     * {@link #getDefaultDragModifiersEx()} for one operation, and zero modifiers
     * for the other.
     * <p>
     * If the same modifiers are set for both zooming and panning,
     * panning will be performed.
     *
     * @param modifiersEx modifier keys, as returned by {@link InputEvent#getModifiersEx()}
     */
    public void setDefaultZoomModifiersEx(int modifiersEx) {
        this.zoomMask = modifiersEx;
    }

    /**
     * Sets modifier keys for panning with a specific mouse button. If there are
     * none set for a certain button with this function, default modifiers set
     * with {@link #setDefaultPanModifiersEx(int)} will be used, defaulting to
     * {@link #getDefaultDragModifiersEx()} if none were set either.<p>
     * Only {@link InputEvent#SHIFT_DOWN_MASK}, {@link InputEvent#CTRL_DOWN_MASK},
     * {@link InputEvent#META_DOWN_MASK} and {@link InputEvent#ALT_DOWN_MASK} are
     * checked.  To avoid platform-specific problems, it is recommended to use
     * {@link #getDefaultDragModifiersEx()} for one operation, and zero modifiers
     * for the other.
     * <p>
     * If the same modifiers are set for both zooming and panning,
     * panning will be performed.
     *
     * @param mouseButton  the mouse button
     * @param modifiersEx modifier keys, as returned by {@link InputEvent#getModifiersEx()}
     */
    public void setPanModifiersEx(int mouseButton, int modifiersEx) {
        panButtonMasks.put(mouseButton, modifiersEx);
    }

    /**
     * Sets modifier keys for zooming with a specific mouse button.
     * If there are none set for a certain button with this function,
     * default modifiers set with {@link #setDefaultZoomModifiersEx(int)}
     * will be used, defaulting to zero (no modifiers)
     * if none were set either.
     * <p>
     * Only {@link InputEvent#SHIFT_DOWN_MASK}, {@link InputEvent#CTRL_DOWN_MASK},
     * {@link InputEvent#META_DOWN_MASK} and {@link InputEvent#ALT_DOWN_MASK} are
     * checked.  To avoid platform-specific problems, it is recommended to use
     * {@link #getDefaultDragModifiersEx()} for one operation, and zero modifiers
     * for the other.
     * <p>
     * If the same modifiers are set for both zooming and panning,
     * panning will be performed.
     *
     * @param mouseButton  the mouse button.
     * @param modifiersEx modifier keys, as returned by {@link InputEvent#getModifiersEx()}
     */
    public void setZoomModifiersEx(int mouseButton, int modifiersEx) {
        zoomButtonMasks.put(mouseButton, modifiersEx);
    }

    /**
     * Returns the flag that determines whether zooming is enabled for
     * the domain axis.
     *
     * @return A boolean.
     */
    public boolean isDomainZoomable() {
        return this.domainZoomable;
    }

    /**
     * Sets the flag that controls whether zooming is enabled for the
     * domain axis.  A check is made to ensure that the current plot supports
     * zooming for the domain values.
     *
     * @param flag  {@code true} enables zooming if possible.
     */
    public void setDomainZoomable(boolean flag) {
        if (flag) {
            Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable) plot;
                this.domainZoomable = z.isDomainZoomable();
            }
        } else {
            this.domainZoomable = false;
        }
    }

    /**
     * Returns the flag that determines whether zooming is enabled for
     * the range axis.
     *
     * @return A boolean.
     */
    public boolean isRangeZoomable() {
        return this.rangeZoomable;
    }

    /**
     * A flag that controls mouse-based zooming on the vertical axis.
     *
     * @param flag  {@code true} enables zooming.
     */
    public void setRangeZoomable(boolean flag) {
        if (flag) {
            Plot plot = this.chart.getPlot();
            if (plot instanceof Zoomable) {
                Zoomable z = (Zoomable) plot;
                this.rangeZoomable = z.isRangeZoomable();
            }
        } else {
            this.rangeZoomable = false;
        }
    }

    /**
     * Returns a strategy used to control and draw zoom rectangle.
     *
     * @return A zoom rectangle strategy.
     */
    public SelectionZoomStrategy getSelectionZoomStrategy() {
        return selectionZoomStrategy;
    }

    /**
     * A strategy used to control and draw zoom rectangle.
     *
     * @param selectionZoomStrategy  A zoom rectangle strategy.
     */
    public void setSelectionZoomStrategy(SelectionZoomStrategy selectionZoomStrategy) {
        this.selectionZoomStrategy = selectionZoomStrategy;
    }

    /**
     * Returns the flag that controls whether the zoom rectangle is
     * filled when drawn.
     *
     * @return A boolean.
     */
    public boolean getFillZoomRectangle() {
        return this.selectionZoomStrategy.getFillZoomRectangle();
    }

    /**
     * A flag that controls how the zoom rectangle is drawn.
     *
     * @param flag  {@code true} instructs to fill the rectangle on
     *              zoom, otherwise it will be outlined.
     */
    public void setFillZoomRectangle(boolean flag) {
        this.selectionZoomStrategy.setFillZoomRectangle(flag);
    }

    /**
     * Returns the zoom trigger distance.  This controls how far the mouse must
     * move before a zoom action is triggered.
     *
     * @return The distance (in Java2D units).
     */
    public int getZoomTriggerDistance() {
        return this.selectionZoomStrategy.getZoomTriggerDistance();
    }

    /**
     * Sets the zoom trigger distance.  This controls how far the mouse must
     * move before a zoom action is triggered.
     *
     * @param distance  the distance (in Java2D units).
     */
    public void setZoomTriggerDistance(int distance) {
        this.selectionZoomStrategy.setZoomTriggerDistance(distance);
    }

    /**
     * Returns the default directory for the "save as" option.
     *
     * @return The default directory (possibly {@code null}).
     */
    public File getDefaultDirectoryForSaveAs() {
        return this.defaultDirectoryForSaveAs;
    }

    /**
     * Sets the default directory for the "save as" option.  If you set this
     * to {@code null}, the user's default directory will be used.
     *
     * @param directory  the directory ({@code null} permitted).
     */
    public void setDefaultDirectoryForSaveAs(File directory) {
        if (directory != null) {
            if (!directory.isDirectory()) {
                throw new IllegalArgumentException("The 'directory' argument is not a directory.");
            }
        }
        this.defaultDirectoryForSaveAs = directory;
    }

    /**
     * Returns {@code true} if file extensions should be enforced, and
     * {@code false} otherwise.
     *
     * @return The flag.
     *
     * @see #setEnforceFileExtensions(boolean)
     */
    public boolean isEnforceFileExtensions() {
        return this.enforceFileExtensions;
    }

    /**
     * Sets a flag that controls whether file extensions are enforced.
     *
     * @param enforce  the new flag value.
     *
     * @see #isEnforceFileExtensions()
     */
    public void setEnforceFileExtensions(boolean enforce) {
        this.enforceFileExtensions = enforce;
    }

    /**
     * Returns the flag that controls whether zoom operations are
     * centered around the current anchor point.
     *
     * @return A boolean.
     *
     * @see #setZoomAroundAnchor(boolean)
     */
    public boolean getZoomAroundAnchor() {
        return this.zoomAroundAnchor;
    }

    /**
     * Sets the flag that controls whether zoom operations are
     * centered around the current anchor point.
     *
     * @param zoomAroundAnchor  the new flag value.
     *
     * @see #getZoomAroundAnchor()
     */
    public void setZoomAroundAnchor(boolean zoomAroundAnchor) {
        this.zoomAroundAnchor = zoomAroundAnchor;
    }

    /**
     * Returns the zoom rectangle fill paint.
     *
     * @return The zoom rectangle fill paint (never {@code null}).
     *
     * @see #setZoomFillPaint(java.awt.Paint)
     * @see #setFillZoomRectangle(boolean)
     */
    public Paint getZoomFillPaint() {
        return selectionZoomStrategy.getZoomFillPaint();
    }

    /**
     * Sets the zoom rectangle fill paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getZoomFillPaint()
     * @see #getFillZoomRectangle()
     */
    public void setZoomFillPaint(Paint paint) {
        selectionZoomStrategy.setZoomFillPaint(paint);
    }

    /**
     * Returns the zoom rectangle outline paint.
     *
     * @return The zoom rectangle outline paint (never {@code null}).
     *
     * @see #setZoomOutlinePaint(java.awt.Paint)
     * @see #setFillZoomRectangle(boolean)
     */
    public Paint getZoomOutlinePaint() {
        return selectionZoomStrategy.getZoomOutlinePaint();
    }

    /**
     * Sets the zoom rectangle outline paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getZoomOutlinePaint()
     * @see #getFillZoomRectangle()
     */
    public void setZoomOutlinePaint(Paint paint) {
        this.selectionZoomStrategy.setZoomOutlinePaint(paint);
    }

    /**
     * The mouse wheel handler.
     */
    protected MouseWheelHandler mouseWheelHandler;

    /**
     * Returns {@code true} if the mouse wheel handler is enabled, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     */
    public boolean isMouseWheelEnabled() {
        return this.mouseWheelHandler != null;
    }

    /**
     * Enables or disables mouse wheel support for the panel.
     *
     * @param flag  a boolean.
     */
    public void setMouseWheelEnabled(boolean flag) {
        if (flag && this.mouseWheelHandler == null) {
            this.mouseWheelHandler = new MouseWheelHandler(this);
        } else if (!flag && this.mouseWheelHandler != null) {
            this.removeMouseWheelListener(this.mouseWheelHandler);
            this.mouseWheelHandler = null;
        }
    }

    /**
     * Add an overlay to the panel.
     *
     * @param overlay  the overlay ({@code null} not permitted).
     */
    public void addOverlay(Overlay overlay) {
        Args.nullNotPermitted(overlay, "overlay");
        this.overlays.add(overlay);
        overlay.addChangeListener(this);
        repaint();
    }

    /**
     * Removes an overlay from the panel.
     *
     * @param overlay  the overlay to remove ({@code null} not permitted).
     */
    public void removeOverlay(Overlay overlay) {
        Args.nullNotPermitted(overlay, "overlay");
        boolean removed = this.overlays.remove(overlay);
        if (removed) {
            overlay.removeChangeListener(this);
            repaint();
        }
    }

    /**
     * Handles a change to an overlay by repainting the panel.
     *
     * @param event  the event.
     */
    @Override
    public void overlayChanged(OverlayChangeEvent event) {
        repaint();
    }

    /**
     * Switches the display of tooltips for the panel on or off.  Note that
     * tooltips can only be displayed if the chart has been configured to
     * generate tooltip items.
     *
     * @param flag  {@code true} to enable tooltips, {@code false} to
     *              disable tooltips.
     */
    public void setDisplayToolTips(boolean flag) {
        if (flag) {
            ToolTipManager.sharedInstance().registerComponent(this);
        } else {
            ToolTipManager.sharedInstance().unregisterComponent(this);
        }
    }

    /**
     * Returns a string for the tooltip.
     *
     * @param e  the mouse event.
     *
     * @return A tool tip or {@code null} if no tooltip is available.
     */
    @Override
    public String getToolTipText(MouseEvent e) {
        String result = null;
        if (this.info != null) {
            EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                Insets insets = getInsets();
                ChartEntity entity = entities.getEntity((int) ((e.getX() - insets.left) / this.scaleX), (int) ((e.getY() - insets.top) / this.scaleY));
                if (entity != null) {
                    result = entity.getToolTipText();
                }
            }
        }
        return result;
    }

    /**
     * Translates a Java2D point on the chart to a screen location.
     *
     * @param java2DPoint  the Java2D point.
     *
     * @return The screen location.
     */
    public Point translateJava2DToScreen(Point2D java2DPoint) {
        Insets insets = getInsets();
        int x = (int) (java2DPoint.getX() * this.scaleX + insets.left);
        int y = (int) (java2DPoint.getY() * this.scaleY + insets.top);
        return new Point(x, y);
    }

    /**
     * Translates a panel (component) location to a Java2D point.
     *
     * @param screenPoint  the screen location ({@code null} not
     *                     permitted).
     *
     * @return The Java2D coordinates.
     */
    public Point2D translateScreenToJava2D(Point screenPoint) {
        Insets insets = getInsets();
        double x = (screenPoint.getX() - insets.left) / this.scaleX;
        double y = (screenPoint.getY() - insets.top) / this.scaleY;
        return new Point2D.Double(x, y);
    }

    /**
     * Applies any scaling that is in effect for the chart drawing to the
     * given rectangle.
     *
     * @param rect  the rectangle ({@code null} not permitted).
     *
     * @return A new scaled rectangle.
     */
    public Rectangle2D scale(Rectangle2D rect) {
        Insets insets = getInsets();
        double x = rect.getX() * getScaleX() + insets.left;
        double y = rect.getY() * getScaleY() + insets.top;
        double w = rect.getWidth() * getScaleX();
        double h = rect.getHeight() * getScaleY();
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns the chart entity at a given point.
     * <P>
     * This method will return null if there is (a) no entity at the given
     * point, or (b) no entity collection has been generated.
     *
     * @param viewX  the x-coordinate.
     * @param viewY  the y-coordinate.
     *
     * @return The chart entity (possibly {@code null}).
     */
    public ChartEntity getEntityForPoint(int viewX, int viewY) {
        ChartEntity result = null;
        if (this.info != null) {
            Insets insets = getInsets();
            double x = (viewX - insets.left) / this.scaleX;
            double y = (viewY - insets.top) / this.scaleY;
            EntityCollection entities = this.info.getEntityCollection();
            result = entities != null ? entities.getEntity(x, y) : null;
        }
        return result;
    }

    /**
     * Returns the flag that controls whether the offscreen buffer
     * needs to be refreshed.
     *
     * @return A boolean.
     */
    public boolean getRefreshBuffer() {
        return this.refreshBuffer;
    }

    /**
     * Sets the refresh buffer flag.  This flag is used to avoid unnecessary
     * redrawing of the chart when the offscreen image buffer is used.
     *
     * @param flag  {@code true} indicates that the buffer should be
     *              refreshed.
     */
    public void setRefreshBuffer(boolean flag) {
        this.refreshBuffer = flag;
    }

    /**
     * Paints the component by drawing the chart to fill the entire component,
     * but allowing for the insets (which will be non-zero if a border has been
     * set for this component).  To increase performance (at the expense of
     * memory), an off-screen buffer image can be used.
     *
     * @param g  the graphics device for drawing on.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.chart == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        // first determine the size of the chart rendering area...
        Dimension size = getSize();
        Insets insets = getInsets();
        Rectangle2D available = new Rectangle2D.Double(insets.left, insets.top, size.getWidth() - insets.left - insets.right, size.getHeight() - insets.top - insets.bottom);
        // work out if scaling is required...
        boolean scale = false;
        double drawWidth = available.getWidth();
        double drawHeight = available.getHeight();
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        if (drawWidth < this.minimumDrawWidth) {
            this.scaleX = drawWidth / this.minimumDrawWidth;
            drawWidth = this.minimumDrawWidth;
            scale = true;
        } else if (drawWidth > this.maximumDrawWidth) {
            this.scaleX = drawWidth / this.maximumDrawWidth;
            drawWidth = this.maximumDrawWidth;
            scale = true;
        }
        if (drawHeight < this.minimumDrawHeight) {
            this.scaleY = drawHeight / this.minimumDrawHeight;
            drawHeight = this.minimumDrawHeight;
            scale = true;
        } else if (drawHeight > this.maximumDrawHeight) {
            this.scaleY = drawHeight / this.maximumDrawHeight;
            drawHeight = this.maximumDrawHeight;
            scale = true;
        }
        Rectangle2D chartArea = new Rectangle2D.Double(0.0, 0.0, drawWidth, drawHeight);
        // are we using the chart buffer?
        if (this.useBuffer) {
            // for better rendering on the HiDPI monitors upscaling the buffer to the "native" resoution
            // instead of using logical one provided by Swing
            final AffineTransform globalTransform = ((Graphics2D) g).getTransform();
            final double globalScaleX = globalTransform.getScaleX();
            final double globalScaleY = globalTransform.getScaleY();
            final int scaledWidth = (int) (available.getWidth() * globalScaleX);
            final int scaledHeight = (int) (available.getHeight() * globalScaleY);
            // do we need to resize the buffer?
            if ((this.chartBuffer == null) || (this.chartBufferWidth != scaledWidth) || (this.chartBufferHeight != scaledHeight)) {
                this.chartBufferWidth = scaledWidth;
                this.chartBufferHeight = scaledHeight;
                GraphicsConfiguration gc = g2.getDeviceConfiguration();
                this.chartBuffer = gc.createCompatibleImage(this.chartBufferWidth, this.chartBufferHeight, Transparency.TRANSLUCENT);
                this.refreshBuffer = true;
            }
            // do we need to redraw the buffer?
            if (this.refreshBuffer) {
                // clear the flag
                this.refreshBuffer = false;
                // scale graphics of the buffer to the same value as global
                // Swing graphics - this allow to paint all elements as usual
                // but applies all necessary smoothing
                Graphics2D bufferG2 = (Graphics2D) this.chartBuffer.getGraphics();
                bufferG2.scale(globalScaleX, globalScaleY);
                Rectangle2D bufferArea = new Rectangle2D.Double(0, 0, available.getWidth(), available.getHeight());
                // make the background of the buffer clear and transparent
                Composite savedComposite = bufferG2.getComposite();
                bufferG2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
                Rectangle r = new Rectangle(0, 0, (int) available.getWidth(), (int) available.getHeight());
                bufferG2.fill(r);
                bufferG2.setComposite(savedComposite);
                if (scale) {
                    AffineTransform saved = bufferG2.getTransform();
                    AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                    bufferG2.transform(st);
                    this.chart.draw(bufferG2, chartArea, this.anchor, this.info);
                    bufferG2.setTransform(saved);
                } else {
                    this.chart.draw(bufferG2, bufferArea, this.anchor, this.info);
                }
                bufferG2.dispose();
            }
            // zap the buffer onto the panel...
            g2.drawImage(this.chartBuffer, insets.left, insets.top, (int) available.getWidth(), (int) available.getHeight(), this);
            // bug#187
            g2.addRenderingHints(this.chart.getRenderingHints());
        } else {
            // redrawing the chart every time...
            AffineTransform saved = g2.getTransform();
            g2.translate(insets.left, insets.top);
            if (scale) {
                AffineTransform st = AffineTransform.getScaleInstance(this.scaleX, this.scaleY);
                g2.transform(st);
            }
            this.chart.draw(g2, chartArea, this.anchor, this.info);
            g2.setTransform(saved);
        }
        for (Overlay overlay : this.overlays) {
            overlay.paintOverlay(g2, this);
        }
        // redraw the zoom rectangle (if present) - if useBuffer is false,
        // we use XOR so we can XOR the rectangle away again without redrawing
        // the chart
        selectionZoomStrategy.drawZoomRectangle(g2, !this.useBuffer);
        g2.dispose();
        this.anchor = null;
    }

    /**
     * Receives notification of changes to the chart, and redraws the chart.
     *
     * @param event  details of the chart change event.
     */
    @Override
    public void chartChanged(ChartChangeEvent event) {
        this.refreshBuffer = true;
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            this.orientation = z.getOrientation();
        }
        repaint();
    }

    /**
     * Receives notification of a chart progress event.
     *
     * @param event  the event.
     */
    @Override
    public void chartProgress(ChartProgressEvent event) {
        // does nothing - override if necessary
    }

    /**
     * Handles action events generated by the popup menu.
     *
     * @param event  the event.
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        // many of the zoom methods need a screen location - all we have is
        // the zoomPoint, but it might be null.  Here we grab the x and y
        // coordinates, or use defaults...
        double screenX = -1.0;
        double screenY = -1.0;
        Point2D zoomPoint = this.selectionZoomStrategy.getZoomPoint();
        if (zoomPoint != null) {
            screenX = zoomPoint.getX();
            screenY = zoomPoint.getY();
        }
        switch(command) {
            case PROPERTIES_COMMAND:
                doEditChartProperties();
                break;
            case COPY_COMMAND:
                doCopy();
                break;
            case SAVE_AS_PNG_COMMAND:
                try {
                    doSaveAs();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "I/O error occurred.", localizationResources.getString("Save_as_PNG"), JOptionPane.WARNING_MESSAGE);
                }
                break;
            case SAVE_AS_PNG_SIZE_COMMAND:
                try {
                    final Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
                    doSaveAs(ss.width, ss.height);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(ChartPanel.this, "I/O error occurred.", localizationResources.getString("Save_as_PNG"), JOptionPane.WARNING_MESSAGE);
                }
                break;
            case SAVE_AS_SVG_COMMAND:
                try {
                    saveAsSVG(null);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "I/O error occurred.", localizationResources.getString("Save_as_SVG"), JOptionPane.WARNING_MESSAGE);
                }
                break;
            case SAVE_AS_PDF_COMMAND:
                saveAsPDF(null);
                break;
            case PRINT_COMMAND:
                createChartPrintJob();
                break;
            case ZOOM_IN_BOTH_COMMAND:
                zoomInBoth(screenX, screenY);
                break;
            case ZOOM_IN_DOMAIN_COMMAND:
                zoomInDomain(screenX, screenY);
                break;
            case ZOOM_IN_RANGE_COMMAND:
                zoomInRange(screenX, screenY);
                break;
            case ZOOM_OUT_BOTH_COMMAND:
                zoomOutBoth(screenX, screenY);
                break;
            case ZOOM_OUT_DOMAIN_COMMAND:
                zoomOutDomain(screenX, screenY);
                break;
            case ZOOM_OUT_RANGE_COMMAND:
                zoomOutRange(screenX, screenY);
                break;
            case ZOOM_RESET_BOTH_COMMAND:
                restoreAutoBounds();
                break;
            case ZOOM_RESET_DOMAIN_COMMAND:
                restoreAutoDomainBounds();
                break;
            case ZOOM_RESET_RANGE_COMMAND:
                restoreAutoRangeBounds();
                break;
        }
    }

    /**
     * Handles a 'mouse entered' event. This method changes the tooltip delays
     * of ToolTipManager.sharedInstance() to the possibly different values set
     * for this chart panel.
     *
     * @param e  the mouse event.
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (!this.ownToolTipDelaysActive) {
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            this.originalToolTipInitialDelay = ttm.getInitialDelay();
            ttm.setInitialDelay(this.ownToolTipInitialDelay);
            this.originalToolTipReshowDelay = ttm.getReshowDelay();
            ttm.setReshowDelay(this.ownToolTipReshowDelay);
            this.originalToolTipDismissDelay = ttm.getDismissDelay();
            ttm.setDismissDelay(this.ownToolTipDismissDelay);
            this.ownToolTipDelaysActive = true;
        }
    }

    /**
     * Handles a 'mouse exited' event. This method resets the tooltip delays of
     * ToolTipManager.sharedInstance() to their
     * original values in effect before mouseEntered()
     *
     * @param e  the mouse event.
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (this.ownToolTipDelaysActive) {
            // restore original tooltip dealys
            ToolTipManager ttm = ToolTipManager.sharedInstance();
            ttm.setInitialDelay(this.originalToolTipInitialDelay);
            ttm.setReshowDelay(this.originalToolTipReshowDelay);
            ttm.setDismissDelay(this.originalToolTipDismissDelay);
            this.ownToolTipDelaysActive = false;
        }
    }

    /**
     * Handles a 'mouse pressed' event.
     * <P>
     * This event is the popup trigger on Unix/Linux.  For Windows, the popup
     * trigger is the 'mouse released' event.
     *
     * @param e  The mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (this.chart == null) {
            return;
        }
        Plot plot = this.chart.getPlot();
        int button = e.getButton();
        int mods = e.getModifiersEx();
        if ((mods & MODIFIERS_EX_MASK) == panButtonMasks.getOrDefault(button, panMask)) {
            // can we pan this plot?
            if (plot instanceof Pannable) {
                Pannable pannable = (Pannable) plot;
                if (pannable.isDomainPannable() || pannable.isRangePannable()) {
                    Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
                    if (screenDataArea != null && screenDataArea.contains(e.getPoint())) {
                        this.panW = screenDataArea.getWidth();
                        this.panH = screenDataArea.getHeight();
                        this.panLast = e.getPoint();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                }
                // the actual panning occurs later in the mouseDragged()
                // method
            }
        } else if (!this.selectionZoomStrategy.isActivated()) {
            if ((mods & MODIFIERS_EX_MASK) == zoomButtonMasks.getOrDefault(button, zoomMask)) {
                Rectangle2D screenDataArea = getScreenDataArea(e.getX(), e.getY());
                if (screenDataArea != null) {
                    Point2D zoomPoint = getPointInRectangle(e.getX(), e.getY(), screenDataArea);
                    selectionZoomStrategy.setZoomPoint(zoomPoint);
                } else {
                    selectionZoomStrategy.setZoomPoint(null);
                }
            }
            if (e.isPopupTrigger()) {
                if (this.popup != null) {
                    displayPopupMenu(e.getX(), e.getY());
                }
            }
        }
    }

    /**
     * Returns a point based on (x, y) but constrained to be within the bounds
     * of the given rectangle.  This method could be moved to JCommon.
     *
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param area  the rectangle ({@code null} not permitted).
     *
     * @return A point within the rectangle.
     */
    protected Point2D getPointInRectangle(int x, int y, Rectangle2D area) {
        double xx = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
        double yy = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
        return new Point2D.Double(xx, yy);
    }

    /**
     * Handles a 'mouse dragged' event.
     *
     * @param e  the mouse event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // if the popup menu has already been triggered, then ignore dragging...
        if (this.popup != null && this.popup.isShowing()) {
            return;
        }
        // handle panning if we have a start point
        if (this.panLast != null) {
            double dx = e.getX() - this.panLast.getX();
            double dy = e.getY() - this.panLast.getY();
            if (dx == 0.0 && dy == 0.0) {
                return;
            }
            double wPercent = -dx / this.panW;
            double hPercent = dy / this.panH;
            boolean old = this.chart.getPlot().isNotify();
            this.chart.getPlot().setNotify(false);
            Pannable p = (Pannable) this.chart.getPlot();
            if (p.getOrientation() == PlotOrientation.VERTICAL) {
                p.panDomainAxes(wPercent, this.info.getPlotInfo(), this.panLast);
                p.panRangeAxes(hPercent, this.info.getPlotInfo(), this.panLast);
            } else {
                p.panDomainAxes(hPercent, this.info.getPlotInfo(), this.panLast);
                p.panRangeAxes(wPercent, this.info.getPlotInfo(), this.panLast);
            }
            this.panLast = e.getPoint();
            this.chart.getPlot().setNotify(old);
            return;
        }
        // if no initial zoom point was set, ignore dragging...
        if (this.selectionZoomStrategy.getZoomPoint() == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) getGraphics();
        // erase the previous zoom rectangle (if any).  We only need to do
        // this is we are using XOR mode, which we do when we're not using
        // the buffer (if there is a buffer, then at the end of this method we
        // just trigger a repaint)
        if (!this.useBuffer) {
            selectionZoomStrategy.drawZoomRectangle(g2, true);
        }
        boolean hZoom, vZoom;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            hZoom = this.rangeZoomable;
            vZoom = this.domainZoomable;
        } else {
            hZoom = this.domainZoomable;
            vZoom = this.rangeZoomable;
        }
        Point2D zoomPoint = this.selectionZoomStrategy.getZoomPoint();
        Rectangle2D scaledDataArea = getScreenDataArea((int) zoomPoint.getX(), (int) zoomPoint.getY());
        selectionZoomStrategy.updateZoomRectangleSelection(e, hZoom, vZoom, scaledDataArea);
        // Draw the new zoom rectangle...
        if (this.useBuffer) {
            repaint();
        } else {
            // with no buffer, we use XOR to draw the rectangle "over" the
            // chart...
            selectionZoomStrategy.drawZoomRectangle(g2, true);
        }
        g2.dispose();
    }

    /**
     * Handles a 'mouse released' event.  On Windows, we need to check if this
     * is a popup trigger, but only if we haven't already been tracking a zoom
     * rectangle.
     *
     * @param e  information about the event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // if we've been panning, we need to reset now that the mouse is
        // released...
        if (this.panLast != null) {
            this.panLast = null;
            setCursor(Cursor.getDefaultCursor());
        } else if (this.selectionZoomStrategy.isActivated()) {
            boolean hZoom, vZoom;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                hZoom = this.rangeZoomable;
                vZoom = this.domainZoomable;
            } else {
                hZoom = this.domainZoomable;
                vZoom = this.rangeZoomable;
            }
            Point2D zoomPoint = this.selectionZoomStrategy.getZoomPoint();
            boolean zoomTrigger1 = hZoom && Math.abs(e.getX() - zoomPoint.getX()) >= this.selectionZoomStrategy.getZoomTriggerDistance();
            boolean zoomTrigger2 = vZoom && Math.abs(e.getY() - zoomPoint.getY()) >= this.selectionZoomStrategy.getZoomTriggerDistance();
            if (zoomTrigger1 || zoomTrigger2) {
                if ((hZoom && (e.getX() < zoomPoint.getX())) || (vZoom && (e.getY() < zoomPoint.getY()))) {
                    restoreAutoBounds();
                } else {
                    Rectangle2D screenDataArea = getScreenDataArea((int) zoomPoint.getX(), (int) zoomPoint.getY());
                    Rectangle2D zoomArea = selectionZoomStrategy.getZoomRectangle(hZoom, vZoom, screenDataArea);
                    zoom(zoomArea);
                }
                this.selectionZoomStrategy.reset();
            } else {
                // erase the zoom rectangle
                Graphics2D g2 = (Graphics2D) getGraphics();
                if (this.useBuffer) {
                    repaint();
                } else {
                    selectionZoomStrategy.drawZoomRectangle(g2, true);
                }
                g2.dispose();
                this.selectionZoomStrategy.reset();
            }
        } else if (e.isPopupTrigger()) {
            if (this.popup != null) {
                displayPopupMenu(e.getX(), e.getY());
            }
        }
    }

    /**
     * Receives notification of mouse clicks on the panel. These are
     * translated and passed on to any registered {@link ChartMouseListener}s.
     *
     * @param event  Information about the mouse event.
     */
    @Override
    public void mouseClicked(MouseEvent event) {
        Insets insets = getInsets();
        int x = (int) ((event.getX() - insets.left) / this.scaleX);
        int y = (int) ((event.getY() - insets.top) / this.scaleY);
        this.anchor = new Point2D.Double(x, y);
        if (this.chart == null) {
            return;
        }
        // force a redraw
        this.chart.setNotify(true);
        // new entity code...
        Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }
        ChartEntity entity = null;
        if (this.info != null) {
            EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                entity = entities.getEntity(x, y);
            }
        }
        ChartMouseEvent chartEvent = new ChartMouseEvent(getChart(), event, entity);
        for (int i = listeners.length - 1; i >= 0; i -= 1) {
            ((ChartMouseListener) listeners[i]).chartMouseClicked(chartEvent);
        }
    }

    /**
     * Implementation of the MouseMotionListener's method.
     *
     * @param e  the event.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        Graphics2D g2 = (Graphics2D) getGraphics();
        g2.dispose();
        Object[] listeners = this.chartMouseListeners.getListeners(ChartMouseListener.class);
        if (listeners.length == 0) {
            return;
        }
        Insets insets = getInsets();
        int x = (int) ((e.getX() - insets.left) / this.scaleX);
        int y = (int) ((e.getY() - insets.top) / this.scaleY);
        ChartEntity entity = null;
        if (this.info != null) {
            EntityCollection entities = this.info.getEntityCollection();
            if (entities != null) {
                entity = entities.getEntity(x, y);
            }
        }
        // we can only generate events if the panel's chart is not null
        // (see bug report 1556951)
        if (this.chart != null) {
            ChartMouseEvent event = new ChartMouseEvent(getChart(), e, entity);
            for (int i = listeners.length - 1; i >= 0; i -= 1) {
                ((ChartMouseListener) listeners[i]).chartMouseMoved(event);
            }
        }
    }

    /**
     * Zooms in on an anchor point (specified in screen coordinate space).
     *
     * @param x  the x value (in screen coordinates).
     * @param y  the y value (in screen coordinates).
     */
    public void zoomInBoth(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        zoomInDomain(x, y);
        zoomInRange(x, y);
        plot.setNotify(savedNotify);
    }

    /**
     * Decreases the length of the domain axis, centered about the given
     * coordinate on the screen.  The length of the domain axis is reduced
     * by the value of {@link #getZoomInFactor()}.
     *
     * @param x  the x coordinate (in screen coordinates).
     * @param y  the y-coordinate (in screen coordinates).
     */
    public void zoomInDomain(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomDomainAxes(this.zoomInFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Decreases the length of the range axis, centered about the given
     * coordinate on the screen.  The length of the range axis is reduced by
     * the value of {@link #getZoomInFactor()}.
     *
     * @param x  the x-coordinate (in screen coordinates).
     * @param y  the y coordinate (in screen coordinates).
     */
    public void zoomInRange(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomRangeAxes(this.zoomInFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Zooms out on an anchor point (specified in screen coordinate space).
     *
     * @param x  the x value (in screen coordinates).
     * @param y  the y value (in screen coordinates).
     */
    public void zoomOutBoth(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        zoomOutDomain(x, y);
        zoomOutRange(x, y);
        plot.setNotify(savedNotify);
    }

    /**
     * Increases the length of the domain axis, centered about the given
     * coordinate on the screen.  The length of the domain axis is increased
     * by the value of {@link #getZoomOutFactor()}.
     *
     * @param x  the x coordinate (in screen coordinates).
     * @param y  the y-coordinate (in screen coordinates).
     */
    public void zoomOutDomain(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomDomainAxes(this.zoomOutFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Increases the length the range axis, centered about the given
     * coordinate on the screen.  The length of the range axis is increased
     * by the value of {@link #getZoomOutFactor()}.
     *
     * @param x  the x coordinate (in screen coordinates).
     * @param y  the y-coordinate (in screen coordinates).
     */
    public void zoomOutRange(double x, double y) {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            Zoomable z = (Zoomable) plot;
            z.zoomRangeAxes(this.zoomOutFactor, this.info.getPlotInfo(), translateScreenToJava2D(new Point((int) x, (int) y)), this.zoomAroundAnchor);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Zooms in on a selected region.
     *
     * @param selection  the selected region.
     */
    public void zoom(Rectangle2D selection) {
        // get the origin of the zoom selection in the Java2D space used for
        // drawing the chart (that is, before any scaling to fit the panel)
        Point2D selectOrigin = translateScreenToJava2D(new Point((int) Math.ceil(selection.getX()), (int) Math.ceil(selection.getY())));
        PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        Rectangle2D scaledDataArea = getScreenDataArea((int) selection.getCenterX(), (int) selection.getCenterY());
        if ((selection.getHeight() > 0) && (selection.getWidth() > 0)) {
            double hLower = (selection.getMinX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double hUpper = (selection.getMaxX() - scaledDataArea.getMinX()) / scaledDataArea.getWidth();
            double vLower = (scaledDataArea.getMaxY() - selection.getMaxY()) / scaledDataArea.getHeight();
            double vUpper = (scaledDataArea.getMaxY() - selection.getMinY()) / scaledDataArea.getHeight();
            Plot p = this.chart.getPlot();
            if (p instanceof Zoomable) {
                // here we tweak the notify flag on the plot so that only
                // one notification happens even though we update multiple
                // axes...
                boolean savedNotify = p.isNotify();
                p.setNotify(false);
                Zoomable z = (Zoomable) p;
                if (z.getOrientation() == PlotOrientation.HORIZONTAL) {
                    z.zoomDomainAxes(vLower, vUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(hLower, hUpper, plotInfo, selectOrigin);
                } else {
                    z.zoomDomainAxes(hLower, hUpper, plotInfo, selectOrigin);
                    z.zoomRangeAxes(vLower, vUpper, plotInfo, selectOrigin);
                }
                p.setNotify(savedNotify);
            }
        }
    }

    /**
     * Restores the auto-range calculation on both axes.
     */
    public void restoreAutoBounds() {
        Plot plot = this.chart.getPlot();
        if (plot == null) {
            return;
        }
        // here we tweak the notify flag on the plot so that only
        // one notification happens even though we update multiple
        // axes...
        boolean savedNotify = plot.isNotify();
        plot.setNotify(false);
        restoreAutoDomainBounds();
        restoreAutoRangeBounds();
        plot.setNotify(savedNotify);
    }

    /**
     * Restores the auto-range calculation on the domain axis.
     */
    public void restoreAutoDomainBounds() {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            // we need to guard against this.zoomPoint being null
            Point2D zoomPoint = this.selectionZoomStrategy.getZoomPoint();
            Point2D zp = zoomPoint != null ? zoomPoint : new Point();
            z.zoomDomainAxes(0.0, this.info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Restores the auto-range calculation on the range axis.
     */
    public void restoreAutoRangeBounds() {
        Plot plot = this.chart.getPlot();
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            // here we tweak the notify flag on the plot so that only
            // one notification happens even though we update multiple
            // axes...
            boolean savedNotify = plot.isNotify();
            plot.setNotify(false);
            // we need to guard against this.zoomPoint being null
            Point2D zoomPoint = this.selectionZoomStrategy.getZoomPoint();
            Point2D zp = zoomPoint != null ? zoomPoint : new Point();
            z.zoomRangeAxes(0.0, this.info.getPlotInfo(), zp);
            plot.setNotify(savedNotify);
        }
    }

    /**
     * Returns the data area for the chart (the area inside the axes) with the
     * current scaling applied (that is, the area as it appears on screen).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea() {
        Rectangle2D dataArea = this.info.getPlotInfo().getDataArea();
        Insets insets = getInsets();
        double x = dataArea.getX() * this.scaleX + insets.left;
        double y = dataArea.getY() * this.scaleY + insets.top;
        double w = dataArea.getWidth() * this.scaleX;
        double h = dataArea.getHeight() * this.scaleY;
        return new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Returns the data area (the area inside the axes) for the plot or subplot,
     * with the current scaling applied.
     *
     * @param x  the x-coordinate (for subplot selection).
     * @param y  the y-coordinate (for subplot selection).
     *
     * @return The scaled data area.
     */
    public Rectangle2D getScreenDataArea(int x, int y) {
        PlotRenderingInfo plotInfo = this.info.getPlotInfo();
        Rectangle2D result;
        if (plotInfo.getSubplotCount() == 0) {
            result = getScreenDataArea();
        } else {
            // get the origin of the zoom selection in the Java2D space used for
            // drawing the chart (that is, before any scaling to fit the panel)
            Point2D selectOrigin = translateScreenToJava2D(new Point(x, y));
            int subplotIndex = plotInfo.getSubplotIndex(selectOrigin);
            if (subplotIndex == -1) {
                return null;
            }
            result = scale(plotInfo.getSubplotInfo(subplotIndex).getDataArea());
        }
        return result;
    }

    /**
     * Returns the initial tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the initial delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getInitialDelay()
     */
    public int getInitialDelay() {
        return this.ownToolTipInitialDelay;
    }

    /**
     * Returns the reshow tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the reshow  delay value, in milliseconds.
     *
     * @see javax.swing.ToolTipManager#getReshowDelay()
     */
    public int getReshowDelay() {
        return this.ownToolTipReshowDelay;
    }

    /**
     * Returns the dismissal tooltip delay value used inside this chart panel.
     *
     * @return An integer representing the dismissal delay value, in
     *         milliseconds.
     *
     * @see javax.swing.ToolTipManager#getDismissDelay()
     */
    public int getDismissDelay() {
        return this.ownToolTipDismissDelay;
    }

    /**
     * Specifies the initial delay value for this chart panel.
     *
     * @param delay  the number of milliseconds to delay (after the cursor has
     *               paused) before displaying.
     *
     * @see javax.swing.ToolTipManager#setInitialDelay(int)
     */
    public void setInitialDelay(int delay) {
        this.ownToolTipInitialDelay = delay;
    }

    /**
     * Specifies the amount of time before the user has to wait initialDelay
     * milliseconds before a tooltip will be shown.
     *
     * @param delay  time in milliseconds
     *
     * @see javax.swing.ToolTipManager#setReshowDelay(int)
     */
    public void setReshowDelay(int delay) {
        this.ownToolTipReshowDelay = delay;
    }

    /**
     * Specifies the dismissal delay value for this chart panel.
     *
     * @param delay the number of milliseconds to delay before taking away the
     *              tooltip
     *
     * @see javax.swing.ToolTipManager#setDismissDelay(int)
     */
    public void setDismissDelay(int delay) {
        this.ownToolTipDismissDelay = delay;
    }

    /**
     * Returns the zoom in factor.
     *
     * @return The zoom in factor.
     *
     * @see #setZoomInFactor(double)
     */
    public double getZoomInFactor() {
        return this.zoomInFactor;
    }

    /**
     * Sets the zoom in factor.
     *
     * @param factor  the factor.
     *
     * @see #getZoomInFactor()
     */
    public void setZoomInFactor(double factor) {
        this.zoomInFactor = factor;
    }

    /**
     * Returns the zoom out factor.
     *
     * @return The zoom out factor.
     *
     * @see #setZoomOutFactor(double)
     */
    public double getZoomOutFactor() {
        return this.zoomOutFactor;
    }

    /**
     * Sets the zoom out factor.
     *
     * @param factor  the factor.
     *
     * @see #getZoomOutFactor()
     */
    public void setZoomOutFactor(double factor) {
        this.zoomOutFactor = factor;
    }

    /**
     * Displays a dialog that allows the user to edit the properties for the
     * current chart.
     */
    public void doEditChartProperties() {
        ChartEditor editor = ChartEditorManager.getChartEditor(this.chart);
        int result = JOptionPane.showConfirmDialog(this, editor, localizationResources.getString("Chart_Properties"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            editor.updateChart(this.chart);
        }
    }

    /**
     * Copies the current chart to the system clipboard.
     */
    public void doCopy() {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Insets insets = getInsets();
        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;
        ChartTransferable selection = new ChartTransferable(this.chart, w, h, getMinimumDrawWidth(), getMinimumDrawHeight(), getMaximumDrawWidth(), getMaximumDrawHeight(), true);
        systemClipboard.setContents(selection, null);
    }

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart
     * in PNG format.
     *
     * @throws IOException if there is an I/O error.
     */
    public void doSaveAs() throws IOException {
        doSaveAs(-1, -1);
    }

    /**
     * Opens a file chooser and gives the user an opportunity to save the chart
     * in PNG format.
     *
     * @param w  the width for the saved image (if less than or equal to zero,
     *      the panel width will be used);
     * @param h  the height for the PNG image (if less than or equal to zero,
     *      the panel height will be used);
     *
     * @throws IOException if there is an I/O error.
     */
    public void doSaveAs(int w, int h) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("PNG_Image_Files"), "png");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getPath();
            if (isEnforceFileExtensions()) {
                if (!filename.endsWith(".png")) {
                    filename = filename + ".png";
                }
            }
            if (w <= 0) {
                w = getWidth();
            }
            if (h <= 0) {
                h = getHeight();
            }
            ChartUtils.saveChartAsPNG(new File(filename), this.chart, w, h);
        }
    }

    /**
     * Saves the chart in SVG format (a filechooser will be displayed so that
     * the user can specify the filename).  Note that this method only works
     * if the JFreeSVG library is on the classpath...if this library is not
     * present, the method will fail.
     *
     * @param f  the file.
     *
     * @throws IOException if there is an exception.
     */
    protected void saveAsSVG(File f) throws IOException {
        File file = f;
        if (file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("SVG_Files"), "svg");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions()) {
                    if (!filename.endsWith(".svg")) {
                        filename = filename + ".svg";
                    }
                }
                file = new File(filename);
                if (file.exists()) {
                    String fileExists = localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE");
                    int response = JOptionPane.showConfirmDialog(this, fileExists, localizationResources.getString("Save_as_SVG"), JOptionPane.OK_CANCEL_OPTION);
                    if (response == JOptionPane.CANCEL_OPTION) {
                        file = null;
                    }
                }
            }
        }
        if (file != null) {
            // use reflection to get the SVG string
            String svg = generateSVG(getWidth(), getHeight());
            BufferedWriter writer = null;
            Exception originalException = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" \"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n");
                writer.write(svg + "\n");
                writer.flush();
            } catch (Exception e) {
                originalException = e;
            }
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                RuntimeException th = new RuntimeException(ex);
                if (originalException != null)
                    th.addSuppressed(originalException);
                throw th;
            }
        }
    }

    /**
     * Generates a string containing a rendering of the chart in SVG format.
     * This feature is only supported if the JFreeSVG library is included on
     * the classpath.
     *
     * @param width  the width.
     * @param height  the height.
     *
     * @return A string containing an SVG element for the current chart, or
     *     {@code null} if there is a problem with the method invocation
     *     by reflection.
     */
    protected String generateSVG(int width, int height) {
        Graphics2D g2 = createSVGGraphics2D(width, height);
        if (g2 == null) {
            throw new IllegalStateException("JFreeSVG library is not present.");
        }
        // we suppress shadow generation, because SVG is a vector format and
        // the shadow effect is applied via bitmap effects...
        g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
        String svg = null;
        Rectangle2D drawArea = new Rectangle2D.Double(0, 0, width, height);
        this.chart.draw(g2, drawArea);
        try {
            Method m = g2.getClass().getMethod("getSVGElement");
            svg = (String) m.invoke(g2);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            // null will be returned
        }
        return svg;
    }

    /**
     * Creates an {@code SVGGraphics2D} instance (from JFreeSVG) using reflection.
     * If JFreeSVG is not on the classpath, this method returns {@code null}.
     *
     * @param w  the width.
     * @param h  the height.
     *
     * @return An {@code SVGGraphics2D} instance or {@code null}.
     */
    protected Graphics2D createSVGGraphics2D(int w, int h) {
        try {
            Class<?> svgGraphics2d = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            Constructor<?> ctor = svgGraphics2d.getConstructor(int.class, int.class);
            return (Graphics2D) ctor.newInstance(w, h);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            return null;
        }
    }

    /**
     * Saves the chart in PDF format (a filechooser will be displayed so that
     * the user can specify the filename).  Note that this method only works
     * if the OrsonPDF library is on the classpath...if this library is not
     * present, the method will fail.
     *
     * @param f  the file.
     */
    protected void saveAsPDF(File f) {
        File file = f;
        if (file == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(this.defaultDirectoryForSaveAs);
            FileNameExtensionFilter filter = new FileNameExtensionFilter(localizationResources.getString("PDF_Files"), "pdf");
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setFileFilter(filter);
            int option = fileChooser.showSaveDialog(this);
            if (option == JFileChooser.APPROVE_OPTION) {
                String filename = fileChooser.getSelectedFile().getPath();
                if (isEnforceFileExtensions()) {
                    if (!filename.endsWith(".pdf")) {
                        filename = filename + ".pdf";
                    }
                }
                file = new File(filename);
                if (file.exists()) {
                    String fileExists = localizationResources.getString("FILE_EXISTS_CONFIRM_OVERWRITE");
                    int response = JOptionPane.showConfirmDialog(this, fileExists, localizationResources.getString("Save_as_PDF"), JOptionPane.OK_CANCEL_OPTION);
                    if (response == JOptionPane.CANCEL_OPTION) {
                        file = null;
                    }
                }
            }
        }
        if (file != null) {
            writeAsPDF(file, getWidth(), getHeight());
        }
    }

    /**
     * Writes the current chart to the specified file in PDF format.  This
     * will only work when the OrsonPDF library is found on the classpath.
     * Reflection is used to ensure there is no compile-time dependency on
     * OrsonPDF (which is non-free software).
     *
     * @param file  the output file ({@code null} not permitted).
     * @param w  the chart width.
     * @param h  the chart height.
     */
    private void writeAsPDF(File file, int w, int h) {
        if (!ChartUtils.isOrsonPDFAvailable()) {
            throw new IllegalStateException("OrsonPDF is not present on the classpath.");
        }
        Args.nullNotPermitted(file, "file");
        try {
            Class<?> pdfDocClass = Class.forName("com.orsonpdf.PDFDocument");
            Object pdfDoc = pdfDocClass.getDeclaredConstructor().newInstance();
            Method m = pdfDocClass.getMethod("createPage", Rectangle2D.class);
            Rectangle2D rect = new Rectangle(w, h);
            Object page = m.invoke(pdfDoc, rect);
            Method m2 = page.getClass().getMethod("getGraphics2D");
            Graphics2D g2 = (Graphics2D) m2.invoke(page);
            // we suppress shadow generation, because PDF is a vector format and
            // the shadow effect is applied via bitmap effects...
            g2.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);
            Rectangle2D drawArea = new Rectangle2D.Double(0, 0, w, h);
            this.chart.draw(g2, drawArea);
            Method m3 = pdfDocClass.getMethod("writeToFile", File.class);
            m3.invoke(pdfDoc, file);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Creates a print job for the chart.
     */
    public void createChartPrintJob() {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        PageFormat pf2 = job.pageDialog(pf);
        if (pf2 != pf) {
            job.setPrintable(this, pf2);
            if (job.printDialog()) {
                try {
                    job.print();
                } catch (PrinterException e) {
                    JOptionPane.showMessageDialog(this, e);
                }
            }
        }
    }

    /**
     * Prints the chart on a single page.
     *
     * @param g  the graphics context.
     * @param pf  the page format to use.
     * @param pageIndex  the index of the page. If not {@code 0}, nothing
     *                   gets printed.
     *
     * @return The result of printing.
     */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex != 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D g2 = (Graphics2D) g;
        double x = pf.getImageableX();
        double y = pf.getImageableY();
        double w = pf.getImageableWidth();
        double h = pf.getImageableHeight();
        this.chart.draw(g2, new Rectangle2D.Double(x, y, w, h), this.anchor, null);
        return PAGE_EXISTS;
    }

    /**
     * Adds a listener to the list of objects listening for chart mouse events.
     *
     * @param listener  the listener ({@code null} not permitted).
     */
    public void addChartMouseListener(ChartMouseListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.chartMouseListeners.add(ChartMouseListener.class, listener);
    }

    /**
     * Removes a listener from the list of objects listening for chart mouse
     * events.
     *
     * @param listener  the listener.
     */
    public void removeChartMouseListener(ChartMouseListener listener) {
        this.chartMouseListeners.remove(ChartMouseListener.class, listener);
    }

    /**
     * Returns an array of the listeners of the given type registered with the
     * panel.
     *
     * @param listenerType  the listener type.
     *
     * @return An array of listeners.
     */
    @Override
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        if (listenerType == ChartMouseListener.class) {
            // fetch listeners from local storage
            return this.chartMouseListeners.getListeners(listenerType);
        } else {
            return super.getListeners(listenerType);
        }
    }

    /**
     * Creates a popup menu for the panel.  This method includes code that
     * auto-detects JFreeSVG and OrsonPDF (via reflection) and, if they are
     * present (and the {@code save} argument is {@code true}, adds a menu item
     * for each.
     *
     * @param properties  include a menu item for the chart property editor.
     * @param copy include a menu item for copying to the clipboard.
     * @param save  include one or more menu items for saving the chart to
     *     supported image formats.
     * @param print  include a menu item for printing the chart.
     * @param zoom  include menu items for zooming.
     *
     * @return The popup menu.
     */
    protected JPopupMenu createPopupMenu(boolean properties, boolean copy, boolean save, boolean print, boolean zoom) {
        JPopupMenu result = new JPopupMenu(localizationResources.getString("Chart") + ":");
        boolean separator = false;
        if (properties) {
            JMenuItem propertiesItem = new JMenuItem(localizationResources.getString("Properties..."));
            propertiesItem.setActionCommand(PROPERTIES_COMMAND);
            propertiesItem.addActionListener(this);
            result.add(propertiesItem);
            separator = true;
        }
        if (copy) {
            if (separator) {
                result.addSeparator();
            }
            JMenuItem copyItem = new JMenuItem(localizationResources.getString("Copy"));
            copyItem.setActionCommand(COPY_COMMAND);
            copyItem.addActionListener(this);
            result.add(copyItem);
            separator = !save;
        }
        if (save) {
            if (separator) {
                result.addSeparator();
            }
            JMenu saveSubMenu = new JMenu(localizationResources.getString("Save_as"));
            // PNG - current res
            {
                JMenuItem pngItem = new JMenuItem(localizationResources.getString("PNG..."));
                pngItem.setActionCommand(SAVE_AS_PNG_COMMAND);
                pngItem.addActionListener(this);
                saveSubMenu.add(pngItem);
            }
            // PNG - screen res
            {
                final Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
                final String pngName = "PNG (" + ss.width + "x" + ss.height + ") ...";
                JMenuItem pngItem = new JMenuItem(pngName);
                pngItem.setActionCommand(SAVE_AS_PNG_SIZE_COMMAND);
                pngItem.addActionListener(this);
                saveSubMenu.add(pngItem);
            }
            if (ChartUtils.isJFreeSVGAvailable()) {
                JMenuItem svgItem = new JMenuItem(localizationResources.getString("SVG..."));
                svgItem.setActionCommand(SAVE_AS_SVG_COMMAND);
                svgItem.addActionListener(this);
                saveSubMenu.add(svgItem);
            }
            if (ChartUtils.isOrsonPDFAvailable()) {
                JMenuItem pdfItem = new JMenuItem(localizationResources.getString("PDF..."));
                pdfItem.setActionCommand(SAVE_AS_PDF_COMMAND);
                pdfItem.addActionListener(this);
                saveSubMenu.add(pdfItem);
            }
            result.add(saveSubMenu);
            separator = true;
        }
        if (print) {
            if (separator) {
                result.addSeparator();
            }
            JMenuItem printItem = new JMenuItem(localizationResources.getString("Print..."));
            printItem.setActionCommand(PRINT_COMMAND);
            printItem.addActionListener(this);
            result.add(printItem);
            separator = true;
        }
        if (zoom) {
            if (separator) {
                result.addSeparator();
            }
            JMenu zoomInMenu = new JMenu(localizationResources.getString("Zoom_In"));
            this.zoomInBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomInBothMenuItem.setActionCommand(ZOOM_IN_BOTH_COMMAND);
            this.zoomInBothMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInBothMenuItem);
            zoomInMenu.addSeparator();
            this.zoomInDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomInDomainMenuItem.setActionCommand(ZOOM_IN_DOMAIN_COMMAND);
            this.zoomInDomainMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInDomainMenuItem);
            this.zoomInRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomInRangeMenuItem.setActionCommand(ZOOM_IN_RANGE_COMMAND);
            this.zoomInRangeMenuItem.addActionListener(this);
            zoomInMenu.add(this.zoomInRangeMenuItem);
            result.add(zoomInMenu);
            JMenu zoomOutMenu = new JMenu(localizationResources.getString("Zoom_Out"));
            this.zoomOutBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomOutBothMenuItem.setActionCommand(ZOOM_OUT_BOTH_COMMAND);
            this.zoomOutBothMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutBothMenuItem);
            zoomOutMenu.addSeparator();
            this.zoomOutDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomOutDomainMenuItem.setActionCommand(ZOOM_OUT_DOMAIN_COMMAND);
            this.zoomOutDomainMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutDomainMenuItem);
            this.zoomOutRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomOutRangeMenuItem.setActionCommand(ZOOM_OUT_RANGE_COMMAND);
            this.zoomOutRangeMenuItem.addActionListener(this);
            zoomOutMenu.add(this.zoomOutRangeMenuItem);
            result.add(zoomOutMenu);
            JMenu autoRangeMenu = new JMenu(localizationResources.getString("Auto_Range"));
            this.zoomResetBothMenuItem = new JMenuItem(localizationResources.getString("All_Axes"));
            this.zoomResetBothMenuItem.setActionCommand(ZOOM_RESET_BOTH_COMMAND);
            this.zoomResetBothMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetBothMenuItem);
            autoRangeMenu.addSeparator();
            this.zoomResetDomainMenuItem = new JMenuItem(localizationResources.getString("Domain_Axis"));
            this.zoomResetDomainMenuItem.setActionCommand(ZOOM_RESET_DOMAIN_COMMAND);
            this.zoomResetDomainMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetDomainMenuItem);
            this.zoomResetRangeMenuItem = new JMenuItem(localizationResources.getString("Range_Axis"));
            this.zoomResetRangeMenuItem.setActionCommand(ZOOM_RESET_RANGE_COMMAND);
            this.zoomResetRangeMenuItem.addActionListener(this);
            autoRangeMenu.add(this.zoomResetRangeMenuItem);
            result.addSeparator();
            result.add(autoRangeMenu);
        }
        return result;
    }

    /**
     * The idea is to modify the zooming options depending on the type of chart
     * being displayed by the panel.
     *
     * @param x  horizontal position of the popup.
     * @param y  vertical position of the popup.
     */
    protected void displayPopupMenu(int x, int y) {
        if (this.popup == null) {
            return;
        }
        // go through each zoom menu item and decide whether to
        // enable it...
        boolean isDomainZoomable = false;
        boolean isRangeZoomable = false;
        Plot plot = (this.chart != null ? this.chart.getPlot() : null);
        if (plot instanceof Zoomable) {
            Zoomable z = (Zoomable) plot;
            isDomainZoomable = z.isDomainZoomable();
            isRangeZoomable = z.isRangeZoomable();
        }
        if (this.zoomInDomainMenuItem != null) {
            this.zoomInDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomOutDomainMenuItem != null) {
            this.zoomOutDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomResetDomainMenuItem != null) {
            this.zoomResetDomainMenuItem.setEnabled(isDomainZoomable);
        }
        if (this.zoomInRangeMenuItem != null) {
            this.zoomInRangeMenuItem.setEnabled(isRangeZoomable);
        }
        if (this.zoomOutRangeMenuItem != null) {
            this.zoomOutRangeMenuItem.setEnabled(isRangeZoomable);
        }
        if (this.zoomResetRangeMenuItem != null) {
            this.zoomResetRangeMenuItem.setEnabled(isRangeZoomable);
        }
        if (this.zoomInBothMenuItem != null) {
            this.zoomInBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
        }
        if (this.zoomOutBothMenuItem != null) {
            this.zoomOutBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
        }
        if (this.zoomResetBothMenuItem != null) {
            this.zoomResetBothMenuItem.setEnabled(isDomainZoomable && isRangeZoomable);
        }
        this.popup.show(this, x, y);
    }

    /**
     * Updates the UI for a LookAndFeel change.
     */
    @Override
    public void updateUI() {
        // here we need to update the UI for the popup menu, if the panel
        // has one...
        if (this.popup != null) {
            SwingUtilities.updateComponentTreeUI(this.popup);
        }
        super.updateUI();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException  if there is an I/O error.
     */
    protected void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream.
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    protected void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        // we create a new but empty chartMouseListeners list
        this.chartMouseListeners = new EventListenerList();
        // register as a listener with sub-components...
        if (this.chart != null) {
            this.chart.addChangeListener(this);
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
 * OHLCSeriesCollection.java
 * -------------------------
 * (C) Copyright 2006-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A collection of {@link OHLCSeries} objects.
 *
 * @param <S> the series key type.
 *
 * @see OHLCSeries
 */
public class OHLCSeriesCollection<S extends Comparable<S>> extends AbstractXYDataset<S> implements OHLCDataset<S>, Serializable {

    /**
     * Storage for the data series.
     */
    private List<OHLCSeries<S>> data;

    /**
     * The position for x-values within the time period.
     */
    private TimePeriodAnchor xPosition = TimePeriodAnchor.MIDDLE;

    /**
     * Creates a new instance of {@code OHLCSeriesCollection}.
     */
    public OHLCSeriesCollection() {
        super();
        this.data = new ArrayList<>();
    }

    /**
     * Returns the position within each time period that is used for the X
     * value when the collection is used as an {@link XYDataset}.
     *
     * @return The anchor position (never {@code null}).
     *
     * @since 1.0.11
     */
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    /**
     * Sets the position within each time period that is used for the X values
     * when the collection is used as an {@link XYDataset}, then sends a
     * {@link DatasetChangeEvent} is sent to all registered listeners.
     *
     * @param anchor  the anchor position ({@code null} not permitted).
     *
     * @since 1.0.11
     */
    public void setXPosition(TimePeriodAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void addSeries(OHLCSeries<S> series) {
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
    public OHLCSeries<S> getSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds");
        }
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
     * Returns the x-value for a time period.
     *
     * @param period  the time period ({@code null} not permitted).
     *
     * @return The x-value.
     */
    protected synchronized long getX(RegularTimePeriod period) {
        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond();
        } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond();
        } else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond();
        }
        return result;
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
        OHLCSeries<S> s = this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        RegularTimePeriod period = di.getPeriod();
        return getX(period);
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
    public Number getY(int series, int item) {
        OHLCSeries<S> s = this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getYValue();
    }

    /**
     * Returns the open-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The open-value.
     */
    @Override
    public double getOpenValue(int series, int item) {
        OHLCSeries<S> s = this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getOpenValue();
    }

    /**
     * Returns the open-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The open-value.
     */
    @Override
    public Number getOpen(int series, int item) {
        return getOpenValue(series, item);
    }

    /**
     * Returns the close-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The close-value.
     */
    @Override
    public double getCloseValue(int series, int item) {
        OHLCSeries<S> s = this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getCloseValue();
    }

    /**
     * Returns the close-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The close-value.
     */
    @Override
    public Number getClose(int series, int item) {
        return getCloseValue(series, item);
    }

    /**
     * Returns the high-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The high-value.
     */
    @Override
    public double getHighValue(int series, int item) {
        OHLCSeries<S> s = this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getHighValue();
    }

    /**
     * Returns the high-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The high-value.
     */
    @Override
    public Number getHigh(int series, int item) {
        return getHighValue(series, item);
    }

    /**
     * Returns the low-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The low-value.
     */
    @Override
    public double getLowValue(int series, int item) {
        OHLCSeries<S> s = this.data.get(series);
        OHLCItem di = (OHLCItem) s.getDataItem(item);
        return di.getLowValue();
    }

    /**
     * Returns the low-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The low-value.
     */
    @Override
    public Number getLow(int series, int item) {
        return getLowValue(series, item);
    }

    /**
     * Returns {@code null} always, because this dataset doesn't record
     * any volume data.
     *
     * @param series  the series index (ignored).
     * @param item  the item index (ignored).
     *
     * @return {@code null}.
     */
    @Override
    public Number getVolume(int series, int item) {
        return null;
    }

    /**
     * Returns {@code Double.NaN} always, because this dataset doesn't
     * record any volume data.
     *
     * @param series  the series index (ignored).
     * @param item  the item index (ignored).
     *
     * @return {@code Double.NaN}.
     */
    @Override
    public double getVolumeValue(int series, int item) {
        return Double.NaN;
    }

    /**
     * Removes the series with the specified index and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param index  the series index.
     *
     * @since 1.0.14
     */
    public void removeSeries(int index) {
        OHLCSeries<S> series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }
    }

    /**
     * Removes the specified series from the dataset and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     *
     * @return {@code true} if the series was removed, and
     *     {@code false} otherwise.
     *
     * @since 1.0.14
     */
    public boolean removeSeries(OHLCSeries<S> series) {
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
     *
     * @since 1.0.14
     */
    public void removeAllSeries() {
        if (this.data.isEmpty()) {
            // nothing to do
            return;
        }
        // deregister the collection as a change listener to each series in the
        // collection
        for (OHLCSeries<S> series : this.data) {
            series.removeChangeListener(this);
        }
        // remove all the series from the collection and notify listeners.
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
        if (!(obj instanceof OHLCSeriesCollection)) {
            return false;
        }
        OHLCSeriesCollection that = (OHLCSeriesCollection) obj;
        if (!this.xPosition.equals(that.xPosition)) {
            return false;
        }
        return Objects.equals(this.data, that.data);
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 137;
        result = HashUtils.hashCode(result, this.xPosition);
        for (OHLCSeries<S> series : this.data) {
            result = HashUtils.hashCode(result, series);
        }
        return result;
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
        OHLCSeriesCollection clone = (OHLCSeriesCollection) super.clone();
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
 * -----------------------
 * IntervalXYDelegate.java
 * -----------------------
 * (C) Copyright 2004-2021, by Andreas Schroeder and Contributors.
 *
 * Original Author:  Andreas Schroeder;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A delegate that handles the specification or automatic calculation of the
 * interval surrounding the x-values in a dataset.  This is used to extend
 * a regular {@link XYDataset} to support the {@link IntervalXYDataset}
 * interface.
 * <p>
 * The decorator pattern was not used because of the several possibly
 * implemented interfaces of the decorated instance (e.g.
 * {@link TableXYDataset}, {@link RangeInfo}, {@link DomainInfo} etc.).
 * <p>
 * The width can be set manually or calculated automatically. The switch
 * autoWidth allows to determine which behavior is used. The auto width
 * calculation tries to find the smallest gap between two x-values in the
 * dataset.  If there is only one item in the series, the auto width
 * calculation fails and falls back on the manually set interval width (which
 * is itself defaulted to 1.0).
 */
public class IntervalXYDelegate implements DatasetChangeListener, DomainInfo, Serializable, Cloneable, PublicCloneable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -685166711639592857L;

    /**
     * The dataset to enhance.
     */
    private XYDataset dataset;

    /**
     * A flag to indicate whether the width should be calculated automatically.
     */
    private boolean autoWidth;

    /**
     * A value between 0.0 and 1.0 that indicates the position of the x-value
     * within the interval.
     */
    private double intervalPositionFactor;

    /**
     * The fixed interval width (defaults to 1.0).
     */
    private double fixedIntervalWidth;

    /**
     * The automatically calculated interval width.
     */
    private double autoIntervalWidth;

    /**
     * Creates a new delegate that.
     *
     * @param dataset  the underlying dataset ({@code null} not permitted).
     */
    public IntervalXYDelegate(XYDataset dataset) {
        this(dataset, true);
    }

    /**
     * Creates a new delegate for the specified dataset.
     *
     * @param dataset  the underlying dataset ({@code null} not permitted).
     * @param autoWidth  a flag that controls whether the interval width is
     *                   calculated automatically.
     */
    public IntervalXYDelegate(XYDataset dataset, boolean autoWidth) {
        Args.nullNotPermitted(dataset, "dataset");
        this.dataset = dataset;
        this.autoWidth = autoWidth;
        this.intervalPositionFactor = 0.5;
        this.autoIntervalWidth = Double.POSITIVE_INFINITY;
        this.fixedIntervalWidth = 1.0;
    }

    /**
     * Returns {@code true} if the interval width is automatically
     * calculated, and {@code false} otherwise.
     *
     * @return A boolean.
     */
    public boolean isAutoWidth() {
        return this.autoWidth;
    }

    /**
     * Sets the flag that indicates whether the interval width is automatically
     * calculated.  If the flag is set to {@code true}, the interval is
     * recalculated.
     * <p>
     * Note: recalculating the interval amounts to changing the data values
     * represented by the dataset.  The calling dataset must fire an
     * appropriate {@link DatasetChangeEvent}.
     *
     * @param b  a boolean.
     */
    public void setAutoWidth(boolean b) {
        this.autoWidth = b;
        if (b) {
            this.autoIntervalWidth = recalculateInterval();
        }
    }

    /**
     * Returns the interval position factor.
     *
     * @return The interval position factor.
     */
    public double getIntervalPositionFactor() {
        return this.intervalPositionFactor;
    }

    /**
     * Sets the interval position factor.  This controls how the interval is
     * aligned to the x-value.  For a value of 0.5, the interval is aligned
     * with the x-value in the center.  For a value of 0.0, the interval is
     * aligned with the x-value at the lower end of the interval, and for a
     * value of 1.0, the interval is aligned with the x-value at the upper
     * end of the interval.
     * <br><br>
     * Note that changing the interval position factor amounts to changing the
     * data values represented by the dataset.  Therefore, the dataset that is
     * using this delegate is responsible for generating the
     * appropriate {@link DatasetChangeEvent}.
     *
     * @param d  the new interval position factor (in the range
     *           {@code 0.0} to {@code 1.0} inclusive).
     */
    public void setIntervalPositionFactor(double d) {
        if (d < 0.0 || 1.0 < d) {
            throw new IllegalArgumentException("Argument 'd' outside valid range.");
        }
        this.intervalPositionFactor = d;
    }

    /**
     * Returns the fixed interval width.
     *
     * @return The fixed interval width.
     */
    public double getFixedIntervalWidth() {
        return this.fixedIntervalWidth;
    }

    /**
     * Sets the fixed interval width and, as a side effect, sets the
     * {@code autoWidth} flag to {@code false}.
     * <br><br>
     * Note that changing the interval width amounts to changing the data
     * values represented by the dataset.  Therefore, the dataset
     * that is using this delegate is responsible for generating the
     * appropriate {@link DatasetChangeEvent}.
     *
     * @param w  the width (negative values not permitted).
     */
    public void setFixedIntervalWidth(double w) {
        if (w < 0.0) {
            throw new IllegalArgumentException("Negative 'w' argument.");
        }
        this.fixedIntervalWidth = w;
        this.autoWidth = false;
    }

    /**
     * Returns the interval width.  This method will return either the
     * auto calculated interval width or the manually specified interval
     * width, depending on the {@link #isAutoWidth()} result.
     *
     * @return The interval width to use.
     */
    public double getIntervalWidth() {
        if (isAutoWidth() && !Double.isInfinite(this.autoIntervalWidth)) {
            // everything is fine: autoWidth is on, and an autoIntervalWidth
            // was set.
            return this.autoIntervalWidth;
        } else {
            // either autoWidth is off or autoIntervalWidth was not set.
            return this.fixedIntervalWidth;
        }
    }

    /**
     * Returns the start value of the x-interval for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The start value of the x-interval (possibly {@code null}).
     *
     * @see #getStartXValue(int, int)
     */
    public Number getStartX(int series, int item) {
        Number startX = null;
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            startX = x.doubleValue() - (getIntervalPositionFactor() * getIntervalWidth());
        }
        return startX;
    }

    /**
     * Returns the start value of the x-interval for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The start value of the x-interval.
     *
     * @see #getStartX(int, int)
     */
    public double getStartXValue(int series, int item) {
        return this.dataset.getXValue(series, item) - getIntervalPositionFactor() * getIntervalWidth();
    }

    /**
     * Returns the end value of the x-interval for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The end value of the x-interval (possibly {@code null}).
     *
     * @see #getEndXValue(int, int)
     */
    public Number getEndX(int series, int item) {
        Number endX = null;
        Number x = this.dataset.getX(series, item);
        if (x != null) {
            endX = x.doubleValue() + ((1.0 - getIntervalPositionFactor()) * getIntervalWidth());
        }
        return endX;
    }

    /**
     * Returns the end value of the x-interval for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The end value of the x-interval.
     *
     * @see #getEndX(int, int)
     */
    public double getEndXValue(int series, int item) {
        return this.dataset.getXValue(series, item) + (1.0 - getIntervalPositionFactor()) * getIntervalWidth();
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
        double result = Double.NaN;
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getLowerBound();
        }
        return result;
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
        double result = Double.NaN;
        Range r = getDomainBounds(includeInterval);
        if (r != null) {
            result = r.getUpperBound();
        }
        return result;
    }

    /**
     * Returns the range of the values in the dataset's domain, including
     * or excluding the interval around each x-value as specified.
     *
     * @param includeInterval  a flag that determines whether the
     *                         x-interval should be taken into account.
     *
     * @return The range.
     */
    @Override
    public Range getDomainBounds(boolean includeInterval) {
        // first get the range without the interval, then expand it for the
        // interval width
        Range range = DatasetUtils.findDomainBounds(this.dataset, false);
        if (includeInterval && range != null) {
            double lowerAdj = getIntervalWidth() * getIntervalPositionFactor();
            double upperAdj = getIntervalWidth() - lowerAdj;
            range = new Range(range.getLowerBound() - lowerAdj, range.getUpperBound() + upperAdj);
        }
        return range;
    }

    /**
     * Handles events from the dataset by recalculating the interval if
     * necessary.
     *
     * @param e  the event.
     */
    @Override
    public void datasetChanged(DatasetChangeEvent e) {
        // TODO: by coding the event with some information about what changed
        // in the dataset, we could make the recalculation of the interval
        // more efficient in some cases (for instance, if the change is
        // just an update to a y-value, then the x-interval doesn't need
        // updating)...
        if (this.autoWidth) {
            this.autoIntervalWidth = recalculateInterval();
        }
    }

    /**
     * Recalculate the minimum width "from scratch".
     *
     * @return The minimum width.
     */
    private double recalculateInterval() {
        double result = Double.POSITIVE_INFINITY;
        int seriesCount = this.dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            result = Math.min(result, calculateIntervalForSeries(series));
        }
        return result;
    }

    /**
     * Calculates the interval width for a given series.
     *
     * @param series  the series index.
     *
     * @return The interval width.
     */
    private double calculateIntervalForSeries(int series) {
        double result = Double.POSITIVE_INFINITY;
        int itemCount = this.dataset.getItemCount(series);
        if (itemCount > 1) {
            double prev = this.dataset.getXValue(series, 0);
            for (int item = 1; item < itemCount; item++) {
                double x = this.dataset.getXValue(series, item);
                result = Math.min(result, x - prev);
                prev = x;
            }
        }
        return result;
    }

    /**
     * Tests the delegate for equality with an arbitrary object.  The
     * equality test considers two delegates to be equal if they would
     * calculate the same intervals for any given dataset (for this reason, the
     * dataset itself is NOT included in the equality test, because it is just
     * a reference back to the current 'owner' of the delegate).
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
        if (!(obj instanceof IntervalXYDelegate)) {
            return false;
        }
        IntervalXYDelegate that = (IntervalXYDelegate) obj;
        if (this.autoWidth != that.autoWidth) {
            return false;
        }
        if (this.intervalPositionFactor != that.intervalPositionFactor) {
            return false;
        }
        if (this.fixedIntervalWidth != that.fixedIntervalWidth) {
            return false;
        }
        return true;
    }

    /**
     * @return A clone of this delegate.
     *
     * @throws CloneNotSupportedException if the object cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = HashUtils.hashCode(hash, this.autoWidth);
        hash = HashUtils.hashCode(hash, this.intervalPositionFactor);
        hash = HashUtils.hashCode(hash, this.fixedIntervalWidth);
        return hash;
    }
}
