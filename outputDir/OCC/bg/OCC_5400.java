package OCC.bg;
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
 * -------------------------
 * XYDifferenceRenderer.java
 * -------------------------
 * (C) Copyright 2003-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Richard West, Advanced Micro Devices, Inc. (major rewrite
 *                   of difference drawing algorithm);
 *                   Patrick Schlott
 *                   Christoph Schroeder
 *                   Martin Hoeller
 *
 */
/**
 * A renderer for an {@link XYPlot} that highlights the differences between two
 * series.  The example shown here is generated by the
 * {@code DifferenceChartDemo1.java} program included in the JFreeChart
 * demo collection:
 * <br><br>
 * <img src="doc-files/XYDifferenceRendererSample.png"
 * alt="XYDifferenceRendererSample.png">
 */
public class XYDifferenceRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8447915602375584857L;

    /**
     * The paint used to highlight positive differences (y(0) > y(1)).
     */
    private transient Paint positivePaint;

    /**
     * The paint used to highlight negative differences (y(0) < y(1)).
     */
    private transient Paint negativePaint;

    /**
     * Display shapes at each point?
     */
    private boolean shapesVisible;

    /**
     * The shape to display in the legend item.
     */
    private transient Shape legendLine;

    /**
     * This flag controls whether the x-coordinates (in Java2D space)
     * are rounded to integers.  When set to true, this can avoid the vertical
     * striping that anti-aliasing can generate.  However, the rounding may not
     * be appropriate for output in high resolution formats (for example,
     * vector graphics formats such as SVG and PDF).
     */
    private boolean roundXCoordinates;

    /**
     * Creates a new renderer with default attributes.
     */
    public XYDifferenceRenderer() {
        this(Color.GREEN, Color.RED, false);
    }

    /**
     * Creates a new renderer.
     *
     * @param positivePaint  the highlight color for positive differences
     *                       ({@code null} not permitted).
     * @param negativePaint  the highlight color for negative differences
     *                       ({@code null} not permitted).
     * @param shapes  draw shapes?
     */
    public XYDifferenceRenderer(Paint positivePaint, Paint negativePaint, boolean shapes) {
        Args.nullNotPermitted(positivePaint, "positivePaint");
        Args.nullNotPermitted(negativePaint, "negativePaint");
        this.positivePaint = positivePaint;
        this.negativePaint = negativePaint;
        this.shapesVisible = shapes;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.roundXCoordinates = false;
    }

    /**
     * Returns the paint used to highlight positive differences.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPositivePaint(Paint)
     */
    public Paint getPositivePaint() {
        return this.positivePaint;
    }

    /**
     * Sets the paint used to highlight positive differences and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPositivePaint()
     */
    public void setPositivePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.positivePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to highlight negative differences.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setNegativePaint(Paint)
     */
    public Paint getNegativePaint() {
        return this.negativePaint;
    }

    /**
     * Sets the paint used to highlight negative differences.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getNegativePaint()
     */
    public void setNegativePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.negativePaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether shapes are drawn for each
     * data value.
     *
     * @return A boolean.
     *
     * @see #setShapesVisible(boolean)
     */
    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    /**
     * Sets a flag that controls whether shapes are drawn for each
     * data value, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #getShapesVisible()
     */
    public void setShapesVisible(boolean flag) {
        this.shapesVisible = flag;
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

    /**
     * Returns the flag that controls whether the x-coordinates (in
     * Java2D space) are rounded to integer values.
     *
     * @return The flag.
     *
     * @see #setRoundXCoordinates(boolean)
     */
    public boolean getRoundXCoordinates() {
        return this.roundXCoordinates;
    }

    /**
     * Sets the flag that controls whether the x-coordinates (in
     * Java2D space) are rounded to integer values, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param round  the new flag value.
     *
     * @see #getRoundXCoordinates()
     */
    public void setRoundXCoordinates(boolean round) {
        this.roundXCoordinates = round;
        fireChangeEvent();
    }

    /**
     * Initialises the renderer and returns a state object that should be
     * passed to subsequent calls to the drawItem() method.  This method will
     * be called before the first item is rendered, giving the renderer an
     * opportunity to initialise any state information it wants to maintain.
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return A state object.
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = super.initialise(g2, dataArea, plot, data, info);
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    /**
     * Returns {@code 2}, the number of passes required by the renderer.
     * The {@link XYPlot} will run through the dataset this number of times.
     *
     * @return The number of passes required by the renderer.
     */
    @Override
    public int getPassCount() {
        return 2;
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
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (pass == 0) {
            drawItemPass0(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState);
        } else if (pass == 1) {
            drawItemPass1(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState);
        }
    }

    /**
     * Draws the visual representation of a single data item, first pass.
     *
     * @param x_graphics  the graphics device.
     * @param x_dataArea  the area within which the data is being drawn.
     * @param x_info  collects information about the drawing.
     * @param x_plot  the plot (can be used to obtain standard color
     *                information etc).
     * @param x_domainAxis  the domain (horizontal) axis.
     * @param x_rangeAxis  the range (vertical) axis.
     * @param x_dataset  the dataset.
     * @param x_series  the series index (zero-based).
     * @param x_item  the item index (zero-based).
     * @param x_crosshairState  crosshair information for the plot
     *                          ({@code null} permitted).
     */
    protected void drawItemPass0(Graphics2D x_graphics, Rectangle2D x_dataArea, PlotRenderingInfo x_info, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, XYDataset x_dataset, int x_series, int x_item, CrosshairState x_crosshairState) {
        if (!((0 == x_series) && (0 == x_item))) {
            return;
        }
        boolean b_impliedZeroSubtrahend = (1 == x_dataset.getSeriesCount());
        // check if either series is a degenerate case (i.e. less than 2 points)
        if (isEitherSeriesDegenerate(x_dataset, b_impliedZeroSubtrahend)) {
            return;
        }
        // check if series are disjoint (i.e. domain-spans do not overlap)
        if (!b_impliedZeroSubtrahend && areSeriesDisjoint(x_dataset)) {
            return;
        }
        // polygon definitions
        LinkedList l_minuendXs = new LinkedList();
        LinkedList l_minuendYs = new LinkedList();
        LinkedList l_subtrahendXs = new LinkedList();
        LinkedList l_subtrahendYs = new LinkedList();
        LinkedList l_polygonXs = new LinkedList();
        LinkedList l_polygonYs = new LinkedList();
        // state
        int l_minuendItem = 0;
        int l_minuendItemCount = x_dataset.getItemCount(0);
        Double l_minuendCurX = null;
        Double l_minuendNextX = null;
        Double l_minuendCurY = null;
        Double l_minuendNextY = null;
        double l_minuendMaxY = Double.NEGATIVE_INFINITY;
        double l_minuendMinY = Double.POSITIVE_INFINITY;
        int l_subtrahendItem = 0;
        // actual value set below
        int l_subtrahendItemCount = 0;
        Double l_subtrahendCurX = null;
        Double l_subtrahendNextX = null;
        Double l_subtrahendCurY = null;
        Double l_subtrahendNextY = null;
        double l_subtrahendMaxY = Double.NEGATIVE_INFINITY;
        double l_subtrahendMinY = Double.POSITIVE_INFINITY;
        // if a subtrahend is not specified, assume it is zero
        if (b_impliedZeroSubtrahend) {
            l_subtrahendItem = 0;
            l_subtrahendItemCount = 2;
            l_subtrahendCurX = x_dataset.getXValue(0, 0);
            l_subtrahendNextX = x_dataset.getXValue(0, (l_minuendItemCount - 1));
            l_subtrahendCurY = 0.0;
            l_subtrahendNextY = 0.0;
            l_subtrahendMaxY = 0.0;
            l_subtrahendMinY = 0.0;
            l_subtrahendXs.add(l_subtrahendCurX);
            l_subtrahendYs.add(l_subtrahendCurY);
        } else {
            l_subtrahendItemCount = x_dataset.getItemCount(1);
        }
        boolean b_minuendDone = false;
        boolean b_minuendAdvanced = true;
        boolean b_minuendAtIntersect = false;
        boolean b_minuendFastForward = false;
        boolean b_subtrahendDone = false;
        boolean b_subtrahendAdvanced = true;
        boolean b_subtrahendAtIntersect = false;
        boolean b_subtrahendFastForward = false;
        boolean b_colinear = false;
        boolean b_positive;
        // coordinate pairs
        // current minuend point
        double l_x1 = 0.0, l_y1 = 0.0;
        // next minuend point
        double l_x2 = 0.0, l_y2 = 0.0;
        // current subtrahend point
        double l_x3 = 0.0, l_y3 = 0.0;
        // next subtrahend point
        double l_x4 = 0.0, l_y4 = 0.0;
        // fast-forward through leading tails
        boolean b_fastForwardDone = false;
        while (!b_fastForwardDone) {
            // get the x and y coordinates
            l_x1 = x_dataset.getXValue(0, l_minuendItem);
            l_y1 = x_dataset.getYValue(0, l_minuendItem);
            l_x2 = x_dataset.getXValue(0, l_minuendItem + 1);
            l_y2 = x_dataset.getYValue(0, l_minuendItem + 1);
            l_minuendCurX = l_x1;
            l_minuendCurY = l_y1;
            l_minuendNextX = l_x2;
            l_minuendNextY = l_y2;
            if (b_impliedZeroSubtrahend) {
                l_x3 = l_subtrahendCurX;
                l_y3 = l_subtrahendCurY;
                l_x4 = l_subtrahendNextX;
                l_y4 = l_subtrahendNextY;
            } else {
                l_x3 = x_dataset.getXValue(1, l_subtrahendItem);
                l_y3 = x_dataset.getYValue(1, l_subtrahendItem);
                l_x4 = x_dataset.getXValue(1, l_subtrahendItem + 1);
                l_y4 = x_dataset.getYValue(1, l_subtrahendItem + 1);
                l_subtrahendCurX = l_x3;
                l_subtrahendCurY = l_y3;
                l_subtrahendNextX = l_x4;
                l_subtrahendNextY = l_y4;
            }
            if (l_x2 <= l_x3) {
                // minuend needs to be fast forwarded
                l_minuendItem++;
                b_minuendFastForward = true;
                continue;
            }
            if (l_x4 <= l_x1) {
                // subtrahend needs to be fast forwarded
                l_subtrahendItem++;
                b_subtrahendFastForward = true;
                continue;
            }
            // check if initial polygon needs to be clipped
            if ((l_x3 < l_x1) && (l_x1 < l_x4)) {
                // project onto subtrahend
                double l_slope = (l_y4 - l_y3) / (l_x4 - l_x3);
                l_subtrahendCurX = l_minuendCurX;
                l_subtrahendCurY = (l_slope * l_x1) + (l_y3 - (l_slope * l_x3));
                l_subtrahendXs.add(l_subtrahendCurX);
                l_subtrahendYs.add(l_subtrahendCurY);
            }
            if ((l_x1 < l_x3) && (l_x3 < l_x2)) {
                // project onto minuend
                double l_slope = (l_y2 - l_y1) / (l_x2 - l_x1);
                l_minuendCurX = l_subtrahendCurX;
                l_minuendCurY = (l_slope * l_x3) + (l_y1 - (l_slope * l_x1));
                l_minuendXs.add(l_minuendCurX);
                l_minuendYs.add(l_minuendCurY);
            }
            l_minuendMaxY = l_minuendCurY;
            l_minuendMinY = l_minuendCurY;
            l_subtrahendMaxY = l_subtrahendCurY;
            l_subtrahendMinY = l_subtrahendCurY;
            b_fastForwardDone = true;
        }
        // start of algorithm
        while (!b_minuendDone && !b_subtrahendDone) {
            if (!b_minuendDone && !b_minuendFastForward && b_minuendAdvanced) {
                l_x1 = x_dataset.getXValue(0, l_minuendItem);
                l_y1 = x_dataset.getYValue(0, l_minuendItem);
                l_minuendCurX = l_x1;
                l_minuendCurY = l_y1;
                if (!b_minuendAtIntersect) {
                    l_minuendXs.add(l_minuendCurX);
                    l_minuendYs.add(l_minuendCurY);
                }
                l_minuendMaxY = Math.max(l_minuendMaxY, l_y1);
                l_minuendMinY = Math.min(l_minuendMinY, l_y1);
                l_x2 = x_dataset.getXValue(0, l_minuendItem + 1);
                l_y2 = x_dataset.getYValue(0, l_minuendItem + 1);
                l_minuendNextX = l_x2;
                l_minuendNextY = l_y2;
            }
            // never updated the subtrahend if it is implied to be zero
            if (!b_impliedZeroSubtrahend && !b_subtrahendDone && !b_subtrahendFastForward && b_subtrahendAdvanced) {
                l_x3 = x_dataset.getXValue(1, l_subtrahendItem);
                l_y3 = x_dataset.getYValue(1, l_subtrahendItem);
                l_subtrahendCurX = l_x3;
                l_subtrahendCurY = l_y3;
                if (!b_subtrahendAtIntersect) {
                    l_subtrahendXs.add(l_subtrahendCurX);
                    l_subtrahendYs.add(l_subtrahendCurY);
                }
                l_subtrahendMaxY = Math.max(l_subtrahendMaxY, l_y3);
                l_subtrahendMinY = Math.min(l_subtrahendMinY, l_y3);
                l_x4 = x_dataset.getXValue(1, l_subtrahendItem + 1);
                l_y4 = x_dataset.getYValue(1, l_subtrahendItem + 1);
                l_subtrahendNextX = l_x4;
                l_subtrahendNextY = l_y4;
            }
            // deassert b_*FastForward (only matters for 1st time through loop)
            b_minuendFastForward = false;
            b_subtrahendFastForward = false;
            Double l_intersectX = null;
            Double l_intersectY = null;
            boolean b_intersect = false;
            b_minuendAtIntersect = false;
            b_subtrahendAtIntersect = false;
            // check for intersect
            if ((l_x2 == l_x4) && (l_y2 == l_y4)) {
                // check if line segments are colinear
                if ((l_x1 == l_x3) && (l_y1 == l_y3)) {
                    b_colinear = true;
                } else {
                    // the intersect is at the next point for both the minuend
                    // and subtrahend
                    l_intersectX = l_x2;
                    l_intersectY = l_y2;
                    b_intersect = true;
                    b_minuendAtIntersect = true;
                    b_subtrahendAtIntersect = true;
                }
            } else {
                // compute common denominator
                double l_denominator = ((l_y4 - l_y3) * (l_x2 - l_x1)) - ((l_x4 - l_x3) * (l_y2 - l_y1));
                // compute common deltas
                double l_deltaY = l_y1 - l_y3;
                double l_deltaX = l_x1 - l_x3;
                // compute numerators
                double l_numeratorA = ((l_x4 - l_x3) * l_deltaY) - ((l_y4 - l_y3) * l_deltaX);
                double l_numeratorB = ((l_x2 - l_x1) * l_deltaY) - ((l_y2 - l_y1) * l_deltaX);
                // check if line segments are colinear
                if ((0 == l_numeratorA) && (0 == l_numeratorB) && (0 == l_denominator)) {
                    b_colinear = true;
                } else {
                    // check if previously colinear
                    if (b_colinear) {
                        // clear colinear points and flag
                        l_minuendXs.clear();
                        l_minuendYs.clear();
                        l_subtrahendXs.clear();
                        l_subtrahendYs.clear();
                        l_polygonXs.clear();
                        l_polygonYs.clear();
                        b_colinear = false;
                        // set new starting point for the polygon
                        boolean b_useMinuend = ((l_x3 <= l_x1) && (l_x1 <= l_x4));
                        l_polygonXs.add(b_useMinuend ? l_minuendCurX : l_subtrahendCurX);
                        l_polygonYs.add(b_useMinuend ? l_minuendCurY : l_subtrahendCurY);
                    }
                }
                // compute slope components
                double l_slopeA = l_numeratorA / l_denominator;
                double l_slopeB = l_numeratorB / l_denominator;
                // test if both grahphs have a vertical rise at the same x-value
                boolean b_vertical = (l_x1 == l_x2) && (l_x3 == l_x4) && (l_x2 == l_x4);
                // check if the line segments intersect
                if (((0 < l_slopeA) && (l_slopeA <= 1) && (0 < l_slopeB) && (l_slopeB <= 1)) || b_vertical) {
                    // compute the point of intersection
                    double l_xi;
                    double l_yi;
                    if (b_vertical) {
                        b_colinear = false;
                        l_xi = l_x2;
                        l_yi = l_x4;
                    } else {
                        l_xi = l_x1 + (l_slopeA * (l_x2 - l_x1));
                        l_yi = l_y1 + (l_slopeA * (l_y2 - l_y1));
                    }
                    l_intersectX = l_xi;
                    l_intersectY = l_yi;
                    b_intersect = true;
                    b_minuendAtIntersect = ((l_xi == l_x2) && (l_yi == l_y2));
                    b_subtrahendAtIntersect = ((l_xi == l_x4) && (l_yi == l_y4));
                    // advance minuend and subtrahend to intesect
                    l_minuendCurX = l_intersectX;
                    l_minuendCurY = l_intersectY;
                    l_subtrahendCurX = l_intersectX;
                    l_subtrahendCurY = l_intersectY;
                }
            }
            if (b_intersect) {
                // create the polygon
                // add the minuend's points to polygon
                l_polygonXs.addAll(l_minuendXs);
                l_polygonYs.addAll(l_minuendYs);
                // add intersection point to the polygon
                l_polygonXs.add(l_intersectX);
                l_polygonYs.add(l_intersectY);
                // add the subtrahend's points to the polygon in reverse
                Collections.reverse(l_subtrahendXs);
                Collections.reverse(l_subtrahendYs);
                l_polygonXs.addAll(l_subtrahendXs);
                l_polygonYs.addAll(l_subtrahendYs);
                // create an actual polygon
                b_positive = (l_subtrahendMaxY <= l_minuendMaxY) && (l_subtrahendMinY <= l_minuendMinY);
                createPolygon(x_graphics, x_dataArea, x_plot, x_domainAxis, x_rangeAxis, b_positive, l_polygonXs, l_polygonYs);
                // clear the point vectors
                l_minuendXs.clear();
                l_minuendYs.clear();
                l_subtrahendXs.clear();
                l_subtrahendYs.clear();
                l_polygonXs.clear();
                l_polygonYs.clear();
                // set the maxY and minY values to intersect y-value
                double l_y = l_intersectY;
                l_minuendMaxY = l_y;
                l_subtrahendMaxY = l_y;
                l_minuendMinY = l_y;
                l_subtrahendMinY = l_y;
                // add interection point to new polygon
                l_polygonXs.add(l_intersectX);
                l_polygonYs.add(l_intersectY);
            }
            // advance the minuend if needed
            if (l_x2 <= l_x4) {
                l_minuendItem++;
                b_minuendAdvanced = true;
            } else {
                b_minuendAdvanced = false;
            }
            // advance the subtrahend if needed
            if (l_x4 <= l_x2) {
                l_subtrahendItem++;
                b_subtrahendAdvanced = true;
            } else {
                b_subtrahendAdvanced = false;
            }
            b_minuendDone = (l_minuendItem == (l_minuendItemCount - 1));
            b_subtrahendDone = (l_subtrahendItem == (l_subtrahendItemCount - 1));
        }
        // check if the final polygon needs to be clipped
        if (b_minuendDone && (l_x3 < l_x2) && (l_x2 < l_x4)) {
            // project onto subtrahend
            double l_slope = (l_y4 - l_y3) / (l_x4 - l_x3);
            l_subtrahendNextX = l_minuendNextX;
            l_subtrahendNextY = (l_slope * l_x2) + (l_y3 - (l_slope * l_x3));
        }
        if (b_subtrahendDone && (l_x1 < l_x4) && (l_x4 < l_x2)) {
            // project onto minuend
            double l_slope = (l_y2 - l_y1) / (l_x2 - l_x1);
            l_minuendNextX = l_subtrahendNextX;
            l_minuendNextY = (l_slope * l_x4) + (l_y1 - (l_slope * l_x1));
        }
        // consider last point of minuend and subtrahend for determining
        // positivity
        l_minuendMaxY = Math.max(l_minuendMaxY, l_minuendNextY);
        l_subtrahendMaxY = Math.max(l_subtrahendMaxY, l_subtrahendNextY);
        l_minuendMinY = Math.min(l_minuendMinY, l_minuendNextY);
        l_subtrahendMinY = Math.min(l_subtrahendMinY, l_subtrahendNextY);
        // add the last point of the minuned and subtrahend
        l_minuendXs.add(l_minuendNextX);
        l_minuendYs.add(l_minuendNextY);
        l_subtrahendXs.add(l_subtrahendNextX);
        l_subtrahendYs.add(l_subtrahendNextY);
        // create the polygon
        // add the minuend's points to polygon
        l_polygonXs.addAll(l_minuendXs);
        l_polygonYs.addAll(l_minuendYs);
        // add the subtrahend's points to the polygon in reverse
        Collections.reverse(l_subtrahendXs);
        Collections.reverse(l_subtrahendYs);
        l_polygonXs.addAll(l_subtrahendXs);
        l_polygonYs.addAll(l_subtrahendYs);
        // create an actual polygon
        b_positive = (l_subtrahendMaxY <= l_minuendMaxY) && (l_subtrahendMinY <= l_minuendMinY);
        createPolygon(x_graphics, x_dataArea, x_plot, x_domainAxis, x_rangeAxis, b_positive, l_polygonXs, l_polygonYs);
    }

    /**
     * Draws the visual representation of a single data item, second pass.  In
     * the second pass, the renderer draws the lines and shapes for the
     * individual points in the two series.
     *
     * @param x_graphics  the graphics device.
     * @param x_dataArea  the area within which the data is being drawn.
     * @param x_info  collects information about the drawing.
     * @param x_plot  the plot (can be used to obtain standard color
     *         information etc).
     * @param x_domainAxis  the domain (horizontal) axis.
     * @param x_rangeAxis  the range (vertical) axis.
     * @param x_dataset  the dataset.
     * @param x_series  the series index (zero-based).
     * @param x_item  the item index (zero-based).
     * @param x_crosshairState  crosshair information for the plot
     *                          ({@code null} permitted).
     */
    protected void drawItemPass1(Graphics2D x_graphics, Rectangle2D x_dataArea, PlotRenderingInfo x_info, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, XYDataset x_dataset, int x_series, int x_item, CrosshairState x_crosshairState) {
        Shape l_entityArea = null;
        EntityCollection l_entities = null;
        if (null != x_info) {
            l_entities = x_info.getOwner().getEntityCollection();
        }
        Paint l_seriesPaint = getItemPaint(x_series, x_item);
        Stroke l_seriesStroke = getItemStroke(x_series, x_item);
        x_graphics.setPaint(l_seriesPaint);
        x_graphics.setStroke(l_seriesStroke);
        PlotOrientation l_orientation = x_plot.getOrientation();
        RectangleEdge l_domainAxisLocation = x_plot.getDomainAxisEdge();
        RectangleEdge l_rangeAxisLocation = x_plot.getRangeAxisEdge();
        double l_x0 = x_dataset.getXValue(x_series, x_item);
        double l_y0 = x_dataset.getYValue(x_series, x_item);
        double l_x1 = x_domainAxis.valueToJava2D(l_x0, x_dataArea, l_domainAxisLocation);
        double l_y1 = x_rangeAxis.valueToJava2D(l_y0, x_dataArea, l_rangeAxisLocation);
        if (getShapesVisible()) {
            Shape l_shape = getItemShape(x_series, x_item);
            if (l_orientation == PlotOrientation.HORIZONTAL) {
                l_shape = ShapeUtils.createTranslatedShape(l_shape, l_y1, l_x1);
            } else {
                l_shape = ShapeUtils.createTranslatedShape(l_shape, l_x1, l_y1);
            }
            if (l_shape.intersects(x_dataArea)) {
                x_graphics.setPaint(getItemPaint(x_series, x_item));
                x_graphics.fill(l_shape);
            }
            l_entityArea = l_shape;
        }
        // add an entity for the item...
        if (null != l_entities) {
            if (null == l_entityArea) {
                l_entityArea = new Rectangle2D.Double((l_x1 - 2), (l_y1 - 2), 4, 4);
            }
            String l_tip = null;
            XYToolTipGenerator l_tipGenerator = getToolTipGenerator(x_series, x_item);
            if (null != l_tipGenerator) {
                l_tip = l_tipGenerator.generateToolTip(x_dataset, x_series, x_item);
            }
            String l_url = null;
            XYURLGenerator l_urlGenerator = getURLGenerator();
            if (null != l_urlGenerator) {
                l_url = l_urlGenerator.generateURL(x_dataset, x_series, x_item);
            }
            XYItemEntity l_entity = new XYItemEntity(l_entityArea, x_dataset, x_series, x_item, l_tip, l_url);
            l_entities.add(l_entity);
        }
        // draw the item label if there is one...
        if (isItemLabelVisible(x_series, x_item)) {
            drawItemLabel(x_graphics, l_orientation, x_dataset, x_series, x_item, l_x1, l_y1, (l_y1 < 0.0));
        }
        int datasetIndex = x_plot.indexOf(x_dataset);
        updateCrosshairValues(x_crosshairState, l_x0, l_y0, datasetIndex, l_x1, l_y1, l_orientation);
        if (0 == x_item) {
            return;
        }
        double l_x2 = x_domainAxis.valueToJava2D(x_dataset.getXValue(x_series, (x_item - 1)), x_dataArea, l_domainAxisLocation);
        double l_y2 = x_rangeAxis.valueToJava2D(x_dataset.getYValue(x_series, (x_item - 1)), x_dataArea, l_rangeAxisLocation);
        Line2D l_line = null;
        if (PlotOrientation.HORIZONTAL == l_orientation) {
            l_line = new Line2D.Double(l_y1, l_x1, l_y2, l_x2);
        } else if (PlotOrientation.VERTICAL == l_orientation) {
            l_line = new Line2D.Double(l_x1, l_y1, l_x2, l_y2);
        }
        if ((null != l_line) && l_line.intersects(x_dataArea)) {
            x_graphics.setPaint(getItemPaint(x_series, x_item));
            x_graphics.setStroke(getItemStroke(x_series, x_item));
            x_graphics.draw(l_line);
        }
    }

    /**
     * Determines if a dataset is degenerate.  A degenerate dataset is a
     * dataset where either series has less than two (2) points.
     *
     * @param x_dataset  the dataset.
     * @param x_impliedZeroSubtrahend  if false, do not check the subtrahend
     *
     * @return true if the dataset is degenerate.
     */
    private boolean isEitherSeriesDegenerate(XYDataset x_dataset, boolean x_impliedZeroSubtrahend) {
        if (x_impliedZeroSubtrahend) {
            return (x_dataset.getItemCount(0) < 2);
        }
        return ((x_dataset.getItemCount(0) < 2) || (x_dataset.getItemCount(1) < 2));
    }

    /**
     * Determines if the two (2) series are disjoint.
     * Disjoint series do not overlap in the domain space.
     *
     * @param x_dataset  the dataset.
     *
     * @return true if the dataset is degenerate.
     */
    private boolean areSeriesDisjoint(XYDataset x_dataset) {
        int l_minuendItemCount = x_dataset.getItemCount(0);
        double l_minuendFirst = x_dataset.getXValue(0, 0);
        double l_minuendLast = x_dataset.getXValue(0, l_minuendItemCount - 1);
        int l_subtrahendItemCount = x_dataset.getItemCount(1);
        double l_subtrahendFirst = x_dataset.getXValue(1, 0);
        double l_subtrahendLast = x_dataset.getXValue(1, l_subtrahendItemCount - 1);
        return ((l_minuendLast < l_subtrahendFirst) || (l_subtrahendLast < l_minuendFirst));
    }

    /**
     * Draws the visual representation of a polygon
     *
     * @param x_graphics  the graphics device.
     * @param x_dataArea  the area within which the data is being drawn.
     * @param x_plot  the plot (can be used to obtain standard color
     *                information etc).
     * @param x_domainAxis  the domain (horizontal) axis.
     * @param x_rangeAxis  the range (vertical) axis.
     * @param x_positive  indicates if the polygon is positive (true) or
     *                    negative (false).
     * @param x_xValues  a linked list of the x values (expects values to be
     *                   of type Double).
     * @param x_yValues  a linked list of the y values (expects values to be
     *                   of type Double).
     */
    private void createPolygon(Graphics2D x_graphics, Rectangle2D x_dataArea, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, boolean x_positive, LinkedList x_xValues, LinkedList x_yValues) {
        PlotOrientation l_orientation = x_plot.getOrientation();
        RectangleEdge l_domainAxisLocation = x_plot.getDomainAxisEdge();
        RectangleEdge l_rangeAxisLocation = x_plot.getRangeAxisEdge();
        Object[] l_xValues = x_xValues.toArray();
        Object[] l_yValues = x_yValues.toArray();
        GeneralPath l_path = new GeneralPath();
        if (PlotOrientation.VERTICAL == l_orientation) {
            double l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[0]), x_dataArea, l_domainAxisLocation);
            if (this.roundXCoordinates) {
                l_x = Math.rint(l_x);
            }
            double l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[0]), x_dataArea, l_rangeAxisLocation);
            l_path.moveTo((float) l_x, (float) l_y);
            for (int i = 1; i < l_xValues.length; i++) {
                l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[i]), x_dataArea, l_domainAxisLocation);
                if (this.roundXCoordinates) {
                    l_x = Math.rint(l_x);
                }
                l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[i]), x_dataArea, l_rangeAxisLocation);
                l_path.lineTo((float) l_x, (float) l_y);
            }
            l_path.closePath();
        } else {
            double l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[0]), x_dataArea, l_domainAxisLocation);
            if (this.roundXCoordinates) {
                l_x = Math.rint(l_x);
            }
            double l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[0]), x_dataArea, l_rangeAxisLocation);
            l_path.moveTo((float) l_y, (float) l_x);
            for (int i = 1; i < l_xValues.length; i++) {
                l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[i]), x_dataArea, l_domainAxisLocation);
                if (this.roundXCoordinates) {
                    l_x = Math.rint(l_x);
                }
                l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[i]), x_dataArea, l_rangeAxisLocation);
                l_path.lineTo((float) l_y, (float) l_x);
            }
            l_path.closePath();
        }
        if (l_path.intersects(x_dataArea)) {
            x_graphics.setPaint(x_positive ? getPositivePaint() : getNegativePaint());
            x_graphics.fill(l_path);
        }
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
        XYPlot p = getPlot();
        if (p != null) {
            XYDataset dataset = p.getDataset(datasetIndex);
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
                    Paint paint = lookupSeriesPaint(series);
                    Stroke stroke = lookupSeriesStroke(series);
                    Shape line = getLegendLine();
                    result = new LegendItem(label, description, toolTipText, urlText, line, stroke, paint);
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
        }
        return result;
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
        if (!(obj instanceof XYDifferenceRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYDifferenceRenderer that = (XYDifferenceRenderer) obj;
        if (!PaintUtils.equal(this.positivePaint, that.positivePaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.negativePaint, that.negativePaint)) {
            return false;
        }
        if (this.shapesVisible != that.shapesVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (this.roundXCoordinates != that.roundXCoordinates) {
            return false;
        }
        return true;
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
        XYDifferenceRenderer clone = (XYDifferenceRenderer) super.clone();
        clone.legendLine = CloneUtils.clone(this.legendLine);
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
        SerialUtils.writePaint(this.positivePaint, stream);
        SerialUtils.writePaint(this.negativePaint, stream);
        SerialUtils.writeShape(this.legendLine, stream);
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
        this.positivePaint = SerialUtils.readPaint(stream);
        this.negativePaint = SerialUtils.readPaint(stream);
        this.legendLine = SerialUtils.readShape(stream);
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
 * ----------------
 * ExportUtils.java
 * ----------------
 * (C) Copyright 2014-present by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 */
/**
 * Utility functions for exporting charts to SVG and PDF format.
 */
public class ExportUtils {

    private ExportUtils() {
        // no need to instantiate
    }

    /**
     * Returns {@code true} if JFreeSVG is on the classpath, and
     * {@code false} otherwise.  The JFreeSVG library can be found at
     * https://www.jfree.org/jfreesvg/
     *
     * @return A boolean.
     */
    public static boolean isJFreeSVGAvailable() {
        Class<?> svgClass = null;
        try {
            svgClass = Class.forName("org.jfree.svg.SVGGraphics2D");
        } catch (ClassNotFoundException e) {
            // see if there is maybe an older version of JFreeSVG (different package name)
            try {
                svgClass = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            } catch (ClassNotFoundException e2) {
                // svgClass will be null so the function will return false
            }
        }
        return (svgClass != null);
    }

    /**
     * Returns {@code true} if OrsonPDF is on the classpath, and
     * {@code false} otherwise.  The OrsonPDF library can be found at
     * http://www.object-refinery.com/orsonpdf/
     *
     * @return A boolean.
     */
    public static boolean isOrsonPDFAvailable() {
        Class<?> pdfDocumentClass = null;
        try {
            pdfDocumentClass = Class.forName("com.orsonpdf.PDFDocument");
        } catch (ClassNotFoundException e) {
            // pdfDocumentClass will be null so the function will return false
        }
        return (pdfDocumentClass != null);
    }

    /**
     * Writes the current content to the specified file in SVG format.  This
     * will only work when the JFreeSVG library is found on the classpath.
     * Reflection is used to ensure there is no compile-time dependency on
     * JFreeSVG.
     *
     * @param drawable  the drawable ({@code null} not permitted).
     * @param w  the chart width.
     * @param h  the chart height.
     * @param file  the output file ({@code null} not permitted).
     */
    public static void writeAsSVG(Drawable drawable, int w, int h, File file) {
        if (!ExportUtils.isJFreeSVGAvailable()) {
            throw new IllegalStateException("JFreeSVG is not present on the classpath.");
        }
        Args.nullNotPermitted(drawable, "drawable");
        Args.nullNotPermitted(file, "file");
        try {
            Class<?> svg2Class = Class.forName("org.jfree.graphics2d.svg.SVGGraphics2D");
            Constructor<?> c1 = svg2Class.getConstructor(int.class, int.class);
            Graphics2D svg2 = (Graphics2D) c1.newInstance(w, h);
            Rectangle2D drawArea = new Rectangle2D.Double(0, 0, w, h);
            drawable.draw(svg2, drawArea);
            Class<?> svgUtilsClass = Class.forName("org.jfree.graphics2d.svg.SVGUtils");
            Method m1 = svg2Class.getMethod("getSVGElement", (Class[]) null);
            String element = (String) m1.invoke(svg2, (Object[]) null);
            Method m2 = svgUtilsClass.getMethod("writeToSVG", File.class, String.class);
            m2.invoke(svgUtilsClass, file, element);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Writes a {@link Drawable} to the specified file in PDF format.  This
     * will only work when the OrsonPDF library is found on the classpath.
     * Reflection is used to ensure there is no compile-time dependency on
     * OrsonPDF.
     *
     * @param drawable  the drawable ({@code null} not permitted).
     * @param w  the chart width.
     * @param h  the chart height.
     * @param file  the output file ({@code null} not permitted).
     */
    public static void writeAsPDF(Drawable drawable, int w, int h, File file) {
        if (!ExportUtils.isOrsonPDFAvailable()) {
            throw new IllegalStateException("OrsonPDF is not present on the classpath.");
        }
        Args.nullNotPermitted(drawable, "drawable");
        Args.nullNotPermitted(file, "file");
        try {
            Class<?> pdfDocClass = Class.forName("com.orsonpdf.PDFDocument");
            Object pdfDoc = pdfDocClass.getDeclaredConstructor().newInstance();
            Method m = pdfDocClass.getMethod("createPage", Rectangle2D.class);
            Rectangle2D rect = new Rectangle(w, h);
            Object page = m.invoke(pdfDoc, rect);
            Method m2 = page.getClass().getMethod("getGraphics2D");
            Graphics2D g2 = (Graphics2D) m2.invoke(page);
            Rectangle2D drawArea = new Rectangle2D.Double(0, 0, w, h);
            drawable.draw(g2, drawArea);
            Method m3 = pdfDocClass.getMethod("writeToFile", File.class);
            m3.invoke(pdfDoc, file);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Writes the current content to the specified file in PNG format.
     *
     * @param drawable  the drawable ({@code null} not permitted).
     * @param w  the chart width.
     * @param h  the chart height.
     * @param file  the output file ({@code null} not permitted).
     *
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if there is an I/O problem.
     */
    public static void writeAsPNG(Drawable drawable, int w, int h, File file) throws FileNotFoundException, IOException {
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        drawable.draw(g2, new Rectangle(w, h));
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            ImageIO.write(image, "png", out);
        }
    }

    /**
     * Writes the current content to the specified file in JPEG format.
     *
     * @param drawable  the drawable ({@code null} not permitted).
     * @param w  the chart width.
     * @param h  the chart height.
     * @param file  the output file ({@code null} not permitted).
     *
     * @throws FileNotFoundException if the file is not found.
     * @throws IOException if there is an I/O problem.
     */
    public static void writeAsJPEG(Drawable drawable, int w, int h, File file) throws FileNotFoundException, IOException {
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        drawable.draw(g2, new Rectangle(w, h));
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            ImageIO.write(image, "jpg", out);
        }
    }
}
