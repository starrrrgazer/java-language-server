package NOD.as;
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
