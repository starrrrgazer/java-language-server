package NOD.ae;
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
 * ---------------------------
 * XYLineAndShapeRenderer.java
 * ---------------------------
 * (C) Copyright 2004-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A renderer that connects data points with lines and/or draws shapes at each
 * data point.  This renderer is designed for use with the {@link XYPlot}
 * class.  The example shown here is generated by
 * the {@code XYLineAndShapeRendererDemo2.java} program included in the
 * JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/XYLineAndShapeRendererSample.png"
 * alt="XYLineAndShapeRendererSample.png">
 */
public class XYLineAndShapeRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -7435246895986425885L;

    /**
     * A table of flags that control (per series) whether lines are
     * visible.
     */
    private Map<Integer, Boolean> seriesLinesVisibleMap;

    /**
     * The default value returned by the getLinesVisible() method.
     */
    private boolean defaultLinesVisible;

    /**
     * The shape that is used to represent a line in the legend.
     */
    private transient Shape legendLine;

    /**
     * A table of flags that control (per series) whether shapes are
     * visible.
     */
    private Map<Integer, Boolean> seriesShapesVisibleMap;

    /**
     * The default value returned by the getShapeVisible() method.
     */
    private boolean defaultShapesVisible;

    /**
     * A table of flags that control (per series) whether shapes are
     * filled.
     */
    private Map<Integer, Boolean> seriesShapesFilledMap;

    /**
     * The default value returned by the getShapeFilled() method.
     */
    private boolean defaultShapesFilled;

    /**
     * A flag that controls whether outlines are drawn for shapes.
     */
    private boolean drawOutlines;

    /**
     * A flag that controls whether the fill paint is used for filling
     * shapes.
     */
    private boolean useFillPaint;

    /**
     * A flag that controls whether the outline paint is used for drawing shape
     * outlines.
     */
    private boolean useOutlinePaint;

    /**
     * A flag that controls whether each series is drawn as a single
     * path.
     */
    private boolean drawSeriesLineAsPath;

    /**
     * Creates a new renderer with both lines and shapes visible.
     */
    public XYLineAndShapeRenderer() {
        this(true, true);
    }

    /**
     * Creates a new renderer.
     *
     * @param lines  lines visible?
     * @param shapes  shapes visible?
     */
    public XYLineAndShapeRenderer(boolean lines, boolean shapes) {
        this.seriesLinesVisibleMap = new HashMap<>();
        this.defaultLinesVisible = lines;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.seriesShapesVisibleMap = new HashMap<>();
        this.defaultShapesVisible = shapes;
        // use item paint for fills by default
        this.useFillPaint = false;
        this.seriesShapesFilledMap = new HashMap<>();
        this.defaultShapesFilled = true;
        this.drawOutlines = true;
        // use item paint for outlines by
        this.useOutlinePaint = false;
        // default, not outline paint
        this.drawSeriesLineAsPath = false;
    }

    /**
     * Returns a flag that controls whether each series is drawn as a
     * single path.  The default value is {@code false}.
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
     * single path and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDrawSeriesLineAsPath()
     */
    public void setDrawSeriesLineAsPath(boolean flag) {
        if (this.drawSeriesLineAsPath != flag) {
            this.drawSeriesLineAsPath = flag;
            fireChangeEvent();
        }
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
        return 2;
    }

    // LINES VISIBLE
    /**
     * Returns the flag used to control whether the shape for an item is
     * visible.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemLineVisible(int series, int item) {
        Boolean flag = getSeriesLinesVisible(series);
        if (flag != null) {
            return flag;
        }
        return this.defaultLinesVisible;
    }

    /**
     * Returns the flag used to control whether the lines for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesLinesVisible(int, Boolean)
     */
    public Boolean getSeriesLinesVisible(int series) {
        return this.seriesLinesVisibleMap.get(series);
    }

    /**
     * Sets the 'lines visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag ({@code null} permitted).
     *
     * @see #getSeriesLinesVisible(int)
     */
    public void setSeriesLinesVisible(int series, Boolean flag) {
        this.seriesLinesVisibleMap.put(series, flag);
        fireChangeEvent();
    }

    /**
     * Sets the 'lines visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     *
     * @see #getSeriesLinesVisible(int)
     */
    public void setSeriesLinesVisible(int series, boolean visible) {
        setSeriesLinesVisible(series, Boolean.valueOf(visible));
    }

    /**
     * Returns the default 'lines visible' attribute.
     *
     * @return The default flag.
     *
     * @see #setDefaultLinesVisible(boolean)
     */
    public boolean getDefaultLinesVisible() {
        return this.defaultLinesVisible;
    }

    /**
     * Sets the default 'lines visible' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDefaultLinesVisible()
     */
    public void setDefaultLinesVisible(boolean flag) {
        this.defaultLinesVisible = flag;
        fireChangeEvent();
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

    // SHAPES VISIBLE
    /**
     * Returns the flag used to control whether the shape for an item is
     * visible.
     * <p>
     * The default implementation passes control to the
     * {@code getSeriesShapesVisible()} method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeVisible(int series, int item) {
        Boolean flag = getSeriesShapesVisible(series);
        if (flag != null) {
            return flag;
        }
        return this.defaultShapesVisible;
    }

    /**
     * Returns the flag used to control whether the shapes for a series
     * are visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesShapesVisible(int, Boolean)
     */
    public Boolean getSeriesShapesVisible(int series) {
        return this.seriesShapesVisibleMap.get(series);
    }

    /**
     * Sets the 'shapes visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     *
     * @see #getSeriesShapesVisible(int)
     */
    public void setSeriesShapesVisible(int series, boolean visible) {
        setSeriesShapesVisible(series, Boolean.valueOf(visible));
    }

    /**
     * Sets the 'shapes visible' flag for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param flag  the flag.
     *
     * @see #getSeriesShapesVisible(int)
     */
    public void setSeriesShapesVisible(int series, Boolean flag) {
        this.seriesShapesVisibleMap.put(series, flag);
        fireChangeEvent();
    }

    /**
     * Returns the default 'shape visible' attribute.
     *
     * @return The default flag.
     *
     * @see #setDefaultShapesVisible(boolean)
     */
    public boolean getDefaultShapesVisible() {
        return this.defaultShapesVisible;
    }

    /**
     * Sets the default 'shapes visible' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDefaultShapesVisible()
     */
    public void setDefaultShapesVisible(boolean flag) {
        this.defaultShapesVisible = flag;
        fireChangeEvent();
    }

    // SHAPES FILLED
    /**
     * Returns the flag used to control whether the shape for an item
     * is filled.
     * <p>
     * The default implementation passes control to the
     * {@code getSeriesShapesFilled} method. You can override this method
     * if you require different behaviour.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    public boolean getItemShapeFilled(int series, int item) {
        Boolean flag = getSeriesShapesFilled(series);
        if (flag != null) {
            return flag;
        }
        return this.defaultShapesFilled;
    }

    /**
     * Returns the flag used to control whether the shapes for a series
     * are filled.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesShapesFilled(int, Boolean)
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
    public void setSeriesShapesFilled(int series, boolean flag) {
        setSeriesShapesFilled(series, Boolean.valueOf(flag));
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
     * Returns the default 'shape filled' attribute.
     *
     * @return The default flag.
     *
     * @see #setDefaultShapesFilled(boolean)
     */
    public boolean getDefaultShapesFilled() {
        return this.defaultShapesFilled;
    }

    /**
     * Sets the default 'shapes filled' flag and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getDefaultShapesFilled()
     */
    public void setDefaultShapesFilled(boolean flag) {
        this.defaultShapesFilled = flag;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if outlines should be drawn for shapes, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setDrawOutlines(boolean)
     */
    public boolean getDrawOutlines() {
        return this.drawOutlines;
    }

    /**
     * Sets the flag that controls whether outlines are drawn for
     * shapes, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     * <P>
     * In some cases, shapes look better if they do NOT have an outline, but
     * this flag allows you to set your own preference.
     *
     * @param flag  the flag.
     *
     * @see #getDrawOutlines()
     */
    public void setDrawOutlines(boolean flag) {
        this.drawOutlines = flag;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the renderer should use the fill paint
     * setting to fill shapes, and {@code false} if it should just
     * use the regular paint.
     * <p>
     * Refer to {@code XYLineAndShapeRendererDemo2.java} to see the
     * effect of this flag.
     *
     * @return A boolean.
     *
     * @see #setUseFillPaint(boolean)
     * @see #getUseOutlinePaint()
     */
    public boolean getUseFillPaint() {
        return this.useFillPaint;
    }

    /**
     * Sets the flag that controls whether the fill paint is used to fill
     * shapes, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #getUseFillPaint()
     */
    public void setUseFillPaint(boolean flag) {
        this.useFillPaint = flag;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the renderer should use the outline paint
     * setting to draw shape outlines, and {@code false} if it should just
     * use the regular paint.
     *
     * @return A boolean.
     *
     * @see #setUseOutlinePaint(boolean)
     * @see #getUseFillPaint()
     */
    public boolean getUseOutlinePaint() {
        return this.useOutlinePaint;
    }

    /**
     * Sets the flag that controls whether the outline paint is used to draw
     * shape outlines, and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * <p>
     * Refer to {@code XYLineAndShapeRendererDemo2.java} to see the
     * effect of this flag.
     *
     * @param flag  the flag.
     *
     * @see #getUseOutlinePaint()
     */
    public void setUseOutlinePaint(boolean flag) {
        this.useOutlinePaint = flag;
        fireChangeEvent();
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
            this.seriesPath = new GeneralPath();
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
         * This method is called by the {@link XYPlot} at the start of each
         * series pass.  We reset the state for the current series.
         *
         * @param dataset  the dataset.
         * @param series  the series index.
         * @param firstItem  the first item index for this pass.
         * @param lastItem  the last item index for this pass.
         * @param pass  the current pass index.
         * @param passCount  the number of passes.
         */
        @Override
        public void startSeriesPass(XYDataset dataset, int series, int firstItem, int lastItem, int pass, int passCount) {
            this.seriesPath.reset();
            this.lastPointGood = false;
            super.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
        }
    }

    /**
     * Initialises the renderer.
     * <P>
     * This method will be called before the first item is rendered, giving the
     * renderer an opportunity to initialise any state information it wants to
     * maintain.  The renderer can do nothing if it chooses.
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
        return new State(info);
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
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
        // do nothing if item is not visible
        if (!getItemVisible(series, item)) {
            return;
        }
        // first pass draws the background (lines, for instance)
        if (isLinePass(pass)) {
            if (getItemLineVisible(series, item)) {
                if (this.drawSeriesLineAsPath) {
                    drawPrimaryLineAsPath(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                } else {
                    drawPrimaryLine(state, g2, plot, dataset, pass, series, item, domainAxis, rangeAxis, dataArea);
                }
            }
        } else // second pass adds shapes where the items are ..
        if (isItemPass(pass)) {
            // setup for collecting optional entity info...
            EntityCollection entities = null;
            if (info != null && info.getOwner() != null) {
                entities = info.getOwner().getEntityCollection();
            }
            drawSecondaryPass(g2, plot, dataset, pass, series, item, domainAxis, dataArea, rangeAxis, crosshairState, entities);
        }
    }

    /**
     * Returns {@code true} if the specified pass is the one for drawing
     * lines.
     *
     * @param pass  the pass.
     *
     * @return A boolean.
     */
    protected boolean isLinePass(int pass) {
        return pass == 0;
    }

    /**
     * Returns {@code true} if the specified pass is the one for drawing
     * items.
     *
     * @param pass  the pass.
     *
     * @return A boolean.
     */
    protected boolean isItemPass(int pass) {
        return pass == 1;
    }

    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     */
    protected void drawPrimaryLine(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        if (item == 0) {
            return;
        }
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        double x0 = dataset.getXValue(series, item - 1);
        double y0 = dataset.getYValue(series, item - 1);
        if (Double.isNaN(y0) || Double.isNaN(x0)) {
            return;
        }
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX0 = domainAxis.valueToJava2D(x0, dataArea, xAxisLocation);
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, yAxisLocation);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        // only draw if we have good values
        if (Double.isNaN(transX0) || Double.isNaN(transY0) || Double.isNaN(transX1) || Double.isNaN(transY1)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        boolean visible;
        if (orientation == PlotOrientation.HORIZONTAL) {
            state.workingLine.setLine(transY0, transX0, transY1, transX1);
        } else if (orientation == PlotOrientation.VERTICAL) {
            state.workingLine.setLine(transX0, transY0, transX1, transY1);
        }
        visible = LineUtils.clipLine(state.workingLine, dataArea);
        if (visible) {
            drawFirstPassShape(g2, pass, series, item, state.workingLine);
        }
    }

    /**
     * Draws the first pass shape.
     *
     * @param g2  the graphics device.
     * @param pass  the pass.
     * @param series  the series index.
     * @param item  the item index.
     * @param shape  the shape.
     */
    protected void drawFirstPassShape(Graphics2D g2, int pass, int series, int item, Shape shape) {
        g2.setStroke(getItemStroke(series, item));
        g2.setPaint(getItemPaint(series, item));
        g2.draw(shape);
    }

    /**
     * Draws the item (first pass). This method draws the lines
     * connecting the items. Instead of drawing separate lines,
     * a {@code GeneralPath} is constructed and drawn at the end of
     * the series painting.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plot  the plot (can be used to obtain standard color information
     *              etc).
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataArea  the area within which the data is being drawn.
     */
    protected void drawPrimaryLineAsPath(XYItemRendererState state, Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, ValueAxis rangeAxis, Rectangle2D dataArea) {
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        State s = (State) state;
        // update path to reflect latest point
        if (!Double.isNaN(transX1) && !Double.isNaN(transY1)) {
            float x = (float) transX1;
            float y = (float) transY1;
            PlotOrientation orientation = plot.getOrientation();
            if (orientation == PlotOrientation.HORIZONTAL) {
                x = (float) transY1;
                y = (float) transX1;
            }
            if (s.isLastPointGood()) {
                s.seriesPath.lineTo(x, y);
            } else {
                s.seriesPath.moveTo(x, y);
            }
            s.setLastPointGood(true);
        } else {
            s.setLastPointGood(false);
        }
        // if this is the last item, draw the path ...
        if (item == s.getLastItemIndex()) {
            // draw path
            drawFirstPassShape(g2, pass, series, item, s.seriesPath);
        }
    }

    /**
     * Draws the item shapes and adds chart entities (second pass). This method
     * draws the shapes which mark the item positions. If {@code entities}
     * is not {@code null} it will be populated with entity information
     * for points that fall within the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param dataArea  the area within which the data is being drawn.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param pass  the pass.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  the crosshair state.
     * @param entities the entity collection.
     */
    protected void drawSecondaryPass(Graphics2D g2, XYPlot plot, XYDataset dataset, int pass, int series, int item, ValueAxis domainAxis, Rectangle2D dataArea, ValueAxis rangeAxis, CrosshairState crosshairState, EntityCollection entities) {
        Shape entityArea = null;
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        if (Double.isNaN(y1) || Double.isNaN(x1)) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, xAxisLocation);
        double transY1 = rangeAxis.valueToJava2D(y1, dataArea, yAxisLocation);
        if (getItemShapeVisible(series, item)) {
            Shape shape = getItemShape(series, item);
            if (orientation == PlotOrientation.HORIZONTAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transY1, transX1);
            } else if (orientation == PlotOrientation.VERTICAL) {
                shape = ShapeUtils.createTranslatedShape(shape, transX1, transY1);
            }
            entityArea = shape;
            if (shape.intersects(dataArea)) {
                if (getItemShapeFilled(series, item)) {
                    if (this.useFillPaint) {
                        g2.setPaint(getItemFillPaint(series, item));
                    } else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.fill(shape);
                }
                if (this.drawOutlines) {
                    if (getUseOutlinePaint()) {
                        g2.setPaint(getItemOutlinePaint(series, item));
                    } else {
                        g2.setPaint(getItemPaint(series, item));
                    }
                    g2.setStroke(getItemOutlineStroke(series, item));
                    g2.draw(shape);
                }
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
        // add an entity for the item, but only if it falls within the data
        // area...
        if (entities != null && ShapeUtils.isPointInRect(dataArea, xx, yy)) {
            addEntity(entities, entityArea, dataset, series, item, xx, yy);
        }
    }

    /**
     * Returns a legend item for the specified series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series (possibly {@code null}).
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        XYPlot plot = getPlot();
        if (plot == null) {
            return null;
        }
        XYDataset dataset = plot.getDataset(datasetIndex);
        if (dataset == null) {
            return null;
        }
        if (!getItemVisible(series, 0)) {
            return null;
        }
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
        boolean shapeIsVisible = getItemShapeVisible(series, 0);
        Shape shape = lookupLegendShape(series);
        boolean shapeIsFilled = getItemShapeFilled(series, 0);
        Paint fillPaint = (this.useFillPaint ? lookupSeriesFillPaint(series) : lookupSeriesPaint(series));
        boolean shapeOutlineVisible = this.drawOutlines;
        Paint outlinePaint = (this.useOutlinePaint ? lookupSeriesOutlinePaint(series) : lookupSeriesPaint(series));
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        boolean lineVisible = getItemLineVisible(series, 0);
        Stroke lineStroke = lookupSeriesStroke(series);
        Paint linePaint = lookupSeriesPaint(series);
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, shapeIsVisible, shape, shapeIsFilled, fillPaint, shapeOutlineVisible, outlinePaint, outlineStroke, lineVisible, this.legendLine, lineStroke, linePaint);
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setSeriesKey(dataset.getSeriesKey(series));
        result.setSeriesIndex(series);
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        return result;
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the clone cannot be created.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XYLineAndShapeRenderer clone = (XYLineAndShapeRenderer) super.clone();
        clone.seriesLinesVisibleMap = new HashMap<>(this.seriesLinesVisibleMap);
        clone.legendLine = CloneUtils.clone(this.legendLine);
        clone.seriesShapesVisibleMap = new HashMap<>(this.seriesShapesVisibleMap);
        clone.seriesShapesFilledMap = new HashMap<>(this.seriesShapesFilledMap);
        return clone;
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
        if (!(obj instanceof XYLineAndShapeRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYLineAndShapeRenderer that = (XYLineAndShapeRenderer) obj;
        if (!Objects.equals(this.seriesLinesVisibleMap, that.seriesLinesVisibleMap)) {
            return false;
        }
        if (this.defaultLinesVisible != that.defaultLinesVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (!Objects.equals(this.seriesShapesVisibleMap, that.seriesShapesVisibleMap)) {
            return false;
        }
        if (this.defaultShapesVisible != that.defaultShapesVisible) {
            return false;
        }
        if (!Objects.equals(this.seriesShapesFilledMap, that.seriesShapesFilledMap)) {
            return false;
        }
        if (this.defaultShapesFilled != that.defaultShapesFilled) {
            return false;
        }
        if (this.drawOutlines != that.drawOutlines) {
            return false;
        }
        if (this.useOutlinePaint != that.useOutlinePaint) {
            return false;
        }
        if (this.useFillPaint != that.useFillPaint) {
            return false;
        }
        if (this.drawSeriesLineAsPath != that.drawSeriesLineAsPath) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + seriesLinesVisibleMap.hashCode();
        result = 31 * result + (defaultLinesVisible ? 1 : 0);
        result = 31 * result + seriesShapesVisibleMap.hashCode();
        result = 31 * result + (defaultShapesVisible ? 1 : 0);
        result = 31 * result + seriesShapesFilledMap.hashCode();
        result = 31 * result + (defaultShapesFilled ? 1 : 0);
        result = 31 * result + (drawOutlines ? 1 : 0);
        result = 31 * result + (useFillPaint ? 1 : 0);
        result = 31 * result + (useOutlinePaint ? 1 : 0);
        result = 31 * result + (drawSeriesLineAsPath ? 1 : 0);
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
 * -----------
 * Minute.java
 * -----------
 * (C) Copyright 2001-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * Represents a minute.  This class is immutable, which is a requirement for
 * all {@link RegularTimePeriod} subclasses.
 */
public class Minute extends RegularTimePeriod implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 2144572840034842871L;

    /**
     * Useful constant for the first minute in a day.
     */
    public static final int FIRST_MINUTE_IN_HOUR = 0;

    /**
     * Useful constant for the last minute in a day.
     */
    public static final int LAST_MINUTE_IN_HOUR = 59;

    /**
     * The day.
     */
    private final Day day;

    /**
     * The hour in which the minute falls.
     */
    private final byte hour;

    /**
     * The minute.
     */
    private final byte minute;

    /**
     * The first millisecond.
     */
    private long firstMillisecond;

    /**
     * The last millisecond.
     */
    private long lastMillisecond;

    /**
     * Constructs a new Minute, based on the system date/time.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     */
    public Minute() {
        this(new Date());
    }

    /**
     * Constructs a new Minute.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param minute  the minute (0 to 59).
     * @param hour  the hour ({@code null} not permitted).
     */
    public Minute(int minute, Hour hour) {
        super();
        Args.nullNotPermitted(hour, "hour");
        this.minute = (byte) minute;
        this.hour = (byte) hour.getHour();
        this.day = hour.getDay();
        peg(getCalendarInstance());
    }

    /**
     * Constructs a new instance, based on the supplied date/time.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param time  the time ({@code null} not permitted).
     *
     * @see #Minute(Date, TimeZone, Locale)
     */
    public Minute(Date time) {
        // defer argument checking
        this(time, getCalendarInstance());
    }

    /**
     * Constructs a new Minute, based on the supplied date/time and timezone.
     *
     * @param time  the time ({@code null} not permitted).
     * @param zone  the time zone ({@code null} not permitted).
     * @param locale  the locale ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    public Minute(Date time, TimeZone zone, Locale locale) {
        super();
        Args.nullNotPermitted(time, "time");
        Args.nullNotPermitted(zone, "zone");
        Args.nullNotPermitted(locale, "locale");
        Calendar calendar = Calendar.getInstance(zone, locale);
        calendar.setTime(time);
        int min = calendar.get(Calendar.MINUTE);
        this.minute = (byte) min;
        this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.day = new Day(time, zone, locale);
        peg(calendar);
    }

    /**
     * Constructs a new instance, based on a particular date/time.
     * The time zone and locale are determined by the {@code calendar}
     * parameter.
     *
     * @param time the date/time ({@code null} not permitted).
     * @param calendar the calendar to use for calculations ({@code null} not permitted).
     */
    public Minute(Date time, Calendar calendar) {
        super();
        Args.nullNotPermitted(time, "time");
        Args.nullNotPermitted(calendar, "calendar");
        calendar.setTime(time);
        int min = calendar.get(Calendar.MINUTE);
        this.minute = (byte) min;
        this.hour = (byte) calendar.get(Calendar.HOUR_OF_DAY);
        this.day = new Day(time, calendar);
        peg(calendar);
    }

    /**
     * Creates a new minute.
     * The time zone and locale are determined by the calendar
     * returned by {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @param minute  the minute (0-59).
     * @param hour  the hour (0-23).
     * @param day  the day (1-31).
     * @param month  the month (1-12).
     * @param year  the year (1900-9999).
     */
    public Minute(int minute, int hour, int day, int month, int year) {
        this(minute, new Hour(hour, new Day(day, month, year)));
    }

    /**
     * Returns the day.
     *
     * @return The day.
     *
     * @since 1.0.3
     */
    public Day getDay() {
        return this.day;
    }

    /**
     * Returns the hour.
     *
     * @return The hour (never {@code null}).
     */
    public Hour getHour() {
        return new Hour(this.hour, this.day);
    }

    /**
     * Returns the hour.
     *
     * @return The hour.
     *
     * @since 1.0.3
     */
    public int getHourValue() {
        return this.hour;
    }

    /**
     * Returns the minute.
     *
     * @return The minute.
     */
    public int getMinute() {
        return this.minute;
    }

    /**
     * Returns the first millisecond of the minute.  This will be determined
     * relative to the time zone specified in the constructor, or in the
     * calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The first millisecond of the minute.
     *
     * @see #getLastMillisecond()
     */
    @Override
    public long getFirstMillisecond() {
        return this.firstMillisecond;
    }

    /**
     * Returns the last millisecond of the minute.  This will be
     * determined relative to the time zone specified in the constructor, or
     * in the calendar instance passed in the most recent call to the
     * {@link #peg(Calendar)} method.
     *
     * @return The last millisecond of the minute.
     *
     * @see #getFirstMillisecond()
     */
    @Override
    public long getLastMillisecond() {
        return this.lastMillisecond;
    }

    /**
     * Recalculates the start date/time and end date/time for this time period
     * relative to the supplied calendar (which incorporates a time zone).
     *
     * @param calendar  the calendar ({@code null} not permitted).
     *
     * @since 1.0.3
     */
    @Override
    public void peg(Calendar calendar) {
        this.firstMillisecond = getFirstMillisecond(calendar);
        this.lastMillisecond = getLastMillisecond(calendar);
    }

    /**
     * Returns the minute preceding this one.
     * No matter what time zone and locale this instance was created with,
     * the returned instance will use the default calendar for time
     * calculations, obtained with {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @return The minute preceding this one.
     */
    @Override
    public RegularTimePeriod previous() {
        Minute result;
        if (this.minute != FIRST_MINUTE_IN_HOUR) {
            result = new Minute(this.minute - 1, getHour());
        } else {
            Hour h = (Hour) getHour().previous();
            if (h != null) {
                result = new Minute(LAST_MINUTE_IN_HOUR, h);
            } else {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns the minute following this one.
     * No matter what time zone and locale this instance was created with,
     * the returned instance will use the default calendar for time
     * calculations, obtained with {@link RegularTimePeriod#getCalendarInstance()}.
     *
     * @return The minute following this one.
     */
    @Override
    public RegularTimePeriod next() {
        Minute result;
        if (this.minute != LAST_MINUTE_IN_HOUR) {
            result = new Minute(this.minute + 1, getHour());
        } else {
            // we are at the last minute in the hour...
            Hour nextHour = (Hour) getHour().next();
            if (nextHour != null) {
                result = new Minute(FIRST_MINUTE_IN_HOUR, nextHour);
            } else {
                result = null;
            }
        }
        return result;
    }

    /**
     * Returns a serial index number for the minute.
     *
     * @return The serial index number.
     */
    @Override
    public long getSerialIndex() {
        long hourIndex = this.day.getSerialIndex() * 24L + this.hour;
        return hourIndex * 60L + this.minute;
    }

    /**
     * Returns the first millisecond of the minute.
     *
     * @param calendar  the calendar which defines the timezone
     *     ({@code null} not permitted).
     *
     * @return The first millisecond.
     *
     * @throws NullPointerException if {@code calendar} is
     *     {@code null}.
     */
    @Override
    public long getFirstMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Returns the last millisecond of the minute.
     *
     * @param calendar  the calendar / timezone ({@code null} not
     *     permitted).
     *
     * @return The last millisecond.
     *
     * @throws NullPointerException if {@code calendar} is
     *     {@code null}.
     */
    @Override
    public long getLastMillisecond(Calendar calendar) {
        int year = this.day.getYear();
        int month = this.day.getMonth() - 1;
        int d = this.day.getDayOfMonth();
        calendar.clear();
        calendar.set(year, month, d, this.hour, this.minute, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Tests the equality of this object against an arbitrary Object.
     * <P>
     * This method will return true ONLY if the object is a Minute object
     * representing the same minute as this instance.
     *
     * @param obj  the object to compare ({@code null} permitted).
     *
     * @return {@code true} if the minute and hour value of this and the
     *      object are the same.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Minute)) {
            return false;
        }
        Minute that = (Minute) obj;
        if (this.minute != that.minute) {
            return false;
        }
        if (this.hour != that.hour) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this object instance.  The approach described
     * by Joshua Bloch in "Effective Java" has been used here:
     * <p>
     * {@code http://developer.java.sun.com/developer/Books/effectivejava
     * /Chapter3.pdf}
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + this.minute;
        result = 37 * result + this.hour;
        result = 37 * result + this.day.hashCode();
        return result;
    }

    /**
     * Returns an integer indicating the order of this Minute object relative
     * to the specified object:
     * <p>
     * negative == before, zero == same, positive == after.
     *
     * @param o1  object to compare.
     *
     * @return negative == before, zero == same, positive == after.
     */
    @Override
    public int compareTo(TimePeriod o1) {
        int result;
        // CASE 1 : Comparing to another Minute object
        // -------------------------------------------
        if (o1 instanceof Minute) {
            Minute m = (Minute) o1;
            result = getHour().compareTo(m.getHour());
            if (result == 0) {
                result = this.minute - m.getMinute();
            }
        } else // CASE 2 : Comparing to another TimePeriod object
        // -----------------------------------------------
        if (o1 instanceof RegularTimePeriod) {
            // more difficult case - evaluate later...
            result = 0;
        } else // CASE 3 : Comparing to a non-TimePeriod object
        // ---------------------------------------------
        {
            // consider time periods to be ordered after general objects
            result = 1;
        }
        return result;
    }

    /**
     * Creates a Minute instance by parsing a string.  The string is assumed to
     * be in the format "YYYY-MM-DD HH:MM", perhaps with leading or trailing
     * whitespace.
     *
     * @param s  the minute string to parse.
     *
     * @return {@code null}, if the string is not parseable, the minute
     *      otherwise.
     */
    public static Minute parseMinute(String s) {
        Minute result = null;
        s = s.trim();
        String daystr = s.substring(0, Math.min(10, s.length()));
        Day day = Day.parseDay(daystr);
        if (day != null) {
            String hmstr = s.substring(Math.min(daystr.length() + 1, s.length()));
            hmstr = hmstr.trim();
            String hourstr = hmstr.substring(0, Math.min(2, hmstr.length()));
            int hour = Integer.parseInt(hourstr);
            if ((hour >= 0) && (hour <= 23)) {
                String minstr = hmstr.substring(Math.min(hourstr.length() + 1, hmstr.length()));
                int minute = Integer.parseInt(minstr);
                if ((minute >= 0) && (minute <= 59)) {
                    result = new Minute(minute, new Hour(hour, day));
                }
            }
        }
        return result;
    }
}
