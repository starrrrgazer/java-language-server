package LOC.g;
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
 * --------------
 * Crosshair.java
 * --------------
 * (C) Copyright 2009-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A {@code Crosshair} represents a value on a chart and is usually displayed
 * as a line perpendicular to the x or y-axis (and sometimes including a label
 * that shows the crosshair value as text).  Instances of this class are used
 * to store the cross hair value plus the visual characteristics of the line
 * that will be rendered once the instance is added to a
 * {@link CrosshairOverlay} (or {@code CrosshairOverlayFX} if you are using
 * the JavaFX extensions for JFreeChart).
 * <br><br>
 * Crosshairs support a property change mechanism which is used by JFreeChart
 * to automatically repaint the overlay whenever a crosshair attribute is
 * updated.
 */
public class Crosshair implements Cloneable, PublicCloneable, Serializable {

    /**
     * Flag controlling visibility.
     */
    private boolean visible;

    /**
     * The crosshair value.
     */
    private double value;

    /**
     * The paint for the crosshair line.
     */
    private transient Paint paint;

    /**
     * The stroke for the crosshair line.
     */
    private transient Stroke stroke;

    /**
     * A flag that controls whether the crosshair has a label
     * visible.
     */
    private boolean labelVisible;

    /**
     * The label anchor.
     */
    private RectangleAnchor labelAnchor;

    /**
     * A label generator.
     */
    private CrosshairLabelGenerator labelGenerator;

    /**
     * The x-offset in Java2D units.
     */
    private double labelXOffset;

    /**
     * The y-offset in Java2D units.
     */
    private double labelYOffset;

    /**
     * The label padding.
     */
    private RectangleInsets labelPadding;

    /**
     * The label font.
     */
    private Font labelFont;

    /**
     * The label paint.
     */
    private transient Paint labelPaint;

    /**
     * The label background paint.
     */
    private transient Paint labelBackgroundPaint;

    /**
     * A flag that controls the visibility of the label outline.
     */
    private boolean labelOutlineVisible;

    /**
     * The label outline stroke.
     */
    private transient Stroke labelOutlineStroke;

    /**
     * The label outline paint.
     */
    private transient Paint labelOutlinePaint;

    /**
     * Property change support.
     */
    private transient PropertyChangeSupport pcs;

    /**
     * Creates a new crosshair with value 0.0.
     */
    public Crosshair() {
        this(0.0);
    }

    /**
     * Creates a new crosshair with the specified value.
     *
     * @param value  the value.
     */
    public Crosshair(double value) {
        this(value, Color.BLACK, new BasicStroke(1.0f));
    }

    /**
     * Creates a new crosshair value with the specified value and line style.
     *
     * @param value  the value.
     * @param paint  the line paint ({@code null} not permitted).
     * @param stroke  the line stroke ({@code null} not permitted).
     */
    public Crosshair(double value, Paint paint, Stroke stroke) {
        Args.nullNotPermitted(paint, "paint");
        Args.nullNotPermitted(stroke, "stroke");
        this.visible = true;
        this.value = value;
        this.paint = paint;
        this.stroke = stroke;
        this.labelVisible = false;
        this.labelGenerator = new StandardCrosshairLabelGenerator();
        this.labelAnchor = RectangleAnchor.BOTTOM_LEFT;
        this.labelXOffset = 5.0;
        this.labelYOffset = 5.0;
        this.labelPadding = RectangleInsets.ZERO_INSETS;
        this.labelFont = new Font("Tahoma", Font.PLAIN, 12);
        this.labelPaint = Color.BLACK;
        this.labelBackgroundPaint = new Color(0, 0, 255, 63);
        this.labelOutlineVisible = true;
        this.labelOutlinePaint = Color.BLACK;
        this.labelOutlineStroke = new BasicStroke(0.5f);
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Returns the flag that indicates whether the crosshair is currently visible.
     *
     * @return A boolean.
     *
     * @see #setVisible(boolean)
     */
    public boolean isVisible() {
        return this.visible;
    }

    /**
     * Sets the flag that controls the visibility of the crosshair and sends
     * a proerty change event (with the name 'visible') to all registered
     * listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isVisible()
     */
    public void setVisible(boolean visible) {
        boolean old = this.visible;
        this.visible = visible;
        this.pcs.firePropertyChange("visible", old, visible);
    }

    /**
     * Returns the crosshair value.
     *
     * @return The crosshair value.
     *
     * @see #setValue(double)
     */
    public double getValue() {
        return this.value;
    }

    /**
     * Sets the crosshair value and sends a property change event with the name
     * 'value' to all registered listeners.
     *
     * @param value  the value.
     *
     * @see #getValue()
     */
    public void setValue(double value) {
        Double oldValue = this.value;
        this.value = value;
        this.pcs.firePropertyChange("value", oldValue, value);
    }

    /**
     * Returns the paint for the crosshair line.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPaint(java.awt.Paint)
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint for the crosshair line and sends a property change event
     * with the name "paint" to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        Paint old = this.paint;
        this.paint = paint;
        this.pcs.firePropertyChange("paint", old, paint);
    }

    /**
     * Returns the stroke for the crosshair line.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setStroke(java.awt.Stroke)
     */
    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Sets the stroke for the crosshair line and sends a property change event
     * with the name "stroke" to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getStroke()
     */
    public void setStroke(Stroke stroke) {
        Stroke old = this.stroke;
        this.stroke = stroke;
        this.pcs.firePropertyChange("stroke", old, stroke);
    }

    /**
     * Returns the flag that controls whether a label is drawn for this crosshair.
     *
     * @return A boolean.
     *
     * @see #setLabelVisible(boolean)
     */
    public boolean isLabelVisible() {
        return this.labelVisible;
    }

    /**
     * Sets the flag that controls whether a label is drawn for the
     * crosshair and sends a property change event (with the name
     * 'labelVisible') to all registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isLabelVisible()
     */
    public void setLabelVisible(boolean visible) {
        boolean old = this.labelVisible;
        this.labelVisible = visible;
        this.pcs.firePropertyChange("labelVisible", old, visible);
    }

    /**
     * Returns the crosshair label generator.
     *
     * @return The label crosshair generator (never {@code null}).
     *
     * @see #setLabelGenerator(org.jfree.chart.labels.CrosshairLabelGenerator)
     */
    public CrosshairLabelGenerator getLabelGenerator() {
        return this.labelGenerator;
    }

    /**
     * Sets the crosshair label generator and sends a property change event
     * (with the name 'labelGenerator') to all registered listeners.
     *
     * @param generator  the new generator ({@code null} not permitted).
     *
     * @see #getLabelGenerator()
     */
    public void setLabelGenerator(CrosshairLabelGenerator generator) {
        Args.nullNotPermitted(generator, "generator");
        CrosshairLabelGenerator old = this.labelGenerator;
        this.labelGenerator = generator;
        this.pcs.firePropertyChange("labelGenerator", old, generator);
    }

    /**
     * Returns the label anchor point.
     *
     * @return the label anchor point (never {@code null}).
     *
     * @see #setLabelAnchor(RectangleAnchor)
     */
    public RectangleAnchor getLabelAnchor() {
        return this.labelAnchor;
    }

    /**
     * Sets the label anchor point and sends a property change event (with the
     * name 'labelAnchor') to all registered listeners.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     *
     * @see #getLabelAnchor()
     */
    public void setLabelAnchor(RectangleAnchor anchor) {
        RectangleAnchor old = this.labelAnchor;
        this.labelAnchor = anchor;
        this.pcs.firePropertyChange("labelAnchor", old, anchor);
    }

    /**
     * Returns the x-offset for the label (in Java2D units).
     *
     * @return The x-offset.
     *
     * @see #setLabelXOffset(double)
     */
    public double getLabelXOffset() {
        return this.labelXOffset;
    }

    /**
     * Sets the x-offset and sends a property change event (with the name
     * 'labelXOffset') to all registered listeners.
     *
     * @param offset  the new offset.
     *
     * @see #getLabelXOffset()
     */
    public void setLabelXOffset(double offset) {
        Double old = this.labelXOffset;
        this.labelXOffset = offset;
        this.pcs.firePropertyChange("labelXOffset", old, offset);
    }

    /**
     * Returns the y-offset for the label (in Java2D units).
     *
     * @return The y-offset.
     *
     * @see #setLabelYOffset(double)
     */
    public double getLabelYOffset() {
        return this.labelYOffset;
    }

    /**
     * Sets the y-offset and sends a property change event (with the name
     * 'labelYOffset') to all registered listeners.
     *
     * @param offset  the new offset.
     *
     * @see #getLabelYOffset()
     */
    public void setLabelYOffset(double offset) {
        Double old = this.labelYOffset;
        this.labelYOffset = offset;
        this.pcs.firePropertyChange("labelYOffset", old, offset);
    }

    /**
     * Returns the label padding.
     *
     * @return The label padding (never {@code null}).
     * @see #setLabelPadding
     */
    public RectangleInsets getLabelPadding() {
        return labelPadding;
    }

    /**
     * Sets the label padding and sends a property change event (with the name
     * 'labelPadding') to all registered listeners.
     *
     * @param padding the padding ({@code null} not permitted).
     * @see #getLabelPadding()
     */
    public void setLabelPadding(RectangleInsets padding) {
        Args.nullNotPermitted(padding, "padding");
        RectangleInsets old = this.labelPadding;
        this.labelPadding = padding;
        this.pcs.firePropertyChange("labelPadding", old, padding);
    }

    /**
     * Returns the label font.
     *
     * @return The label font (never {@code null}).
     *
     * @see #setLabelFont(java.awt.Font)
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font and sends a property change event (with the name
     * 'labelFont') to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getLabelFont()
     */
    public void setLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        Font old = this.labelFont;
        this.labelFont = font;
        this.pcs.firePropertyChange("labelFont", old, font);
    }

    /**
     * Returns the label paint.  The default value is {@code Color.BLACK}.
     *
     * @return The label paint (never {@code null}).
     *
     * @see #setLabelPaint
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the label paint and sends a property change event (with the name
     * 'labelPaint') to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelPaint()
     */
    public void setLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        Paint old = this.labelPaint;
        this.labelPaint = paint;
        this.pcs.firePropertyChange("labelPaint", old, paint);
    }

    /**
     * Returns the label background paint.
     *
     * @return The label background paint (possibly {@code null}).
     *
     * @see #setLabelBackgroundPaint(java.awt.Paint)
     */
    public Paint getLabelBackgroundPaint() {
        return this.labelBackgroundPaint;
    }

    /**
     * Sets the label background paint and sends a property change event with
     * the name 'labelBackgroundPaint') to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getLabelBackgroundPaint()
     */
    public void setLabelBackgroundPaint(Paint paint) {
        Paint old = this.labelBackgroundPaint;
        this.labelBackgroundPaint = paint;
        this.pcs.firePropertyChange("labelBackgroundPaint", old, paint);
    }

    /**
     * Returns the flag that controls the visibility of the label outline.
     * The default value is {@code true}.
     *
     * @return A boolean.
     *
     * @see #setLabelOutlineVisible(boolean)
     */
    public boolean isLabelOutlineVisible() {
        return this.labelOutlineVisible;
    }

    /**
     * Sets the flag that controls the visibility of the label outlines and
     * sends a property change event (with the name "labelOutlineVisible") to
     * all registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isLabelOutlineVisible()
     */
    public void setLabelOutlineVisible(boolean visible) {
        boolean old = this.labelOutlineVisible;
        this.labelOutlineVisible = visible;
        this.pcs.firePropertyChange("labelOutlineVisible", old, visible);
    }

    /**
     * Returns the label outline paint.
     *
     * @return The label outline paint (never {@code null}).
     *
     * @see #setLabelOutlinePaint(java.awt.Paint)
     */
    public Paint getLabelOutlinePaint() {
        return this.labelOutlinePaint;
    }

    /**
     * Sets the label outline paint and sends a property change event (with the
     * name "labelOutlinePaint") to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelOutlinePaint()
     */
    public void setLabelOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        Paint old = this.labelOutlinePaint;
        this.labelOutlinePaint = paint;
        this.pcs.firePropertyChange("labelOutlinePaint", old, paint);
    }

    /**
     * Returns the label outline stroke. The default value is
     * {@code BasicStroke(0.5)}.
     *
     * @return The label outline stroke (never {@code null}).
     *
     * @see #setLabelOutlineStroke(java.awt.Stroke)
     */
    public Stroke getLabelOutlineStroke() {
        return this.labelOutlineStroke;
    }

    /**
     * Sets the label outline stroke and sends a property change event (with
     * the name 'labelOutlineStroke') to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getLabelOutlineStroke()
     */
    public void setLabelOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        Stroke old = this.labelOutlineStroke;
        this.labelOutlineStroke = stroke;
        this.pcs.firePropertyChange("labelOutlineStroke", old, stroke);
    }

    /**
     * Tests this crosshair for equality with an arbitrary object.
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
        if (!(obj instanceof Crosshair)) {
            return false;
        }
        Crosshair that = (Crosshair) obj;
        if (this.visible != that.visible) {
            return false;
        }
        if (Double.compare(this.value, that.value) != 0) {
            return false;
        }
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        if (!this.stroke.equals(that.stroke)) {
            return false;
        }
        if (this.labelVisible != that.labelVisible) {
            return false;
        }
        if (!this.labelGenerator.equals(that.labelGenerator)) {
            return false;
        }
        if (!this.labelAnchor.equals(that.labelAnchor)) {
            return false;
        }
        if (Double.compare(this.labelXOffset, that.labelXOffset) != 0) {
            return false;
        }
        if (Double.compare(this.labelYOffset, that.labelYOffset) != 0) {
            return false;
        }
        if (!this.labelPadding.equals(that.labelPadding)) {
            return false;
        }
        if (!this.labelFont.equals(that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelBackgroundPaint, that.labelBackgroundPaint)) {
            return false;
        }
        if (this.labelOutlineVisible != that.labelOutlineVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.labelOutlinePaint, that.labelOutlinePaint)) {
            return false;
        }
        if (!this.labelOutlineStroke.equals(that.labelOutlineStroke)) {
            return false;
        }
        // can't find any difference
        return true;
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = HashUtils.hashCode(hash, this.visible);
        hash = HashUtils.hashCode(hash, this.value);
        hash = HashUtils.hashCode(hash, this.paint);
        hash = HashUtils.hashCode(hash, this.stroke);
        hash = HashUtils.hashCode(hash, this.labelVisible);
        hash = HashUtils.hashCode(hash, this.labelAnchor);
        hash = HashUtils.hashCode(hash, this.labelGenerator);
        hash = HashUtils.hashCode(hash, this.labelXOffset);
        hash = HashUtils.hashCode(hash, this.labelYOffset);
        hash = HashUtils.hashCode(hash, this.labelPadding);
        hash = HashUtils.hashCode(hash, this.labelFont);
        hash = HashUtils.hashCode(hash, this.labelPaint);
        hash = HashUtils.hashCode(hash, this.labelBackgroundPaint);
        hash = HashUtils.hashCode(hash, this.labelOutlineVisible);
        hash = HashUtils.hashCode(hash, this.labelOutlineStroke);
        hash = HashUtils.hashCode(hash, this.labelOutlinePaint);
        return hash;
    }

    /**
     * Returns an independent copy of this instance.
     *
     * @return An independent copy of this instance.
     *
     * @throws java.lang.CloneNotSupportedException if there is a problem with
     *         cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        // FIXME: clone generator
        return super.clone();
    }

    /**
     * Adds a property change listener.
     *
     * @param l  the listener.
     *
     * @see #removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        this.pcs.addPropertyChangeListener(l);
    }

    /**
     * Removes a property change listener.
     *
     * @param l  the listener.
     *
     * @see #addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        this.pcs.removePropertyChangeListener(l);
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
        SerialUtils.writePaint(this.paint, stream);
        SerialUtils.writeStroke(this.stroke, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
        SerialUtils.writePaint(this.labelBackgroundPaint, stream);
        SerialUtils.writeStroke(this.labelOutlineStroke, stream);
        SerialUtils.writePaint(this.labelOutlinePaint, stream);
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
        this.paint = SerialUtils.readPaint(stream);
        this.stroke = SerialUtils.readStroke(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
        this.labelBackgroundPaint = SerialUtils.readPaint(stream);
        this.labelOutlineStroke = SerialUtils.readStroke(stream);
        this.labelOutlinePaint = SerialUtils.readPaint(stream);
        this.pcs = new PropertyChangeSupport(this);
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
 * --------------------------------
 * SlidingGanttCategoryDataset.java
 * --------------------------------
 * (C) Copyright 2008-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 09-May-2008 : Version 1 (DG);
 *
 */
/**
 * A {@link GanttCategoryDataset} implementation that presents a subset of the
 * categories in an underlying dataset.  The index of the first "visible"
 * category can be modified, which provides a means of "sliding" through
 * the categories in the underlying dataset.
 *
 * @param <R> the row key type.
 * @param <C> the column key type.
 * @since 1.0.10
 */
public class SlidingGanttCategoryDataset<R extends Comparable<R>, C extends Comparable<C>> extends AbstractDataset implements GanttCategoryDataset<R, C> {

    /**
     * The underlying dataset.
     */
    private GanttCategoryDataset<R, C> underlying;

    /**
     * The index of the first category to present.
     */
    private int firstCategoryIndex;

    /**
     * The maximum number of categories to present.
     */
    private int maximumCategoryCount;

    /**
     * Creates a new instance.
     *
     * @param underlying  the underlying dataset ({@code null} not
     *     permitted).
     * @param firstColumn  the index of the first visible column from the
     *     underlying dataset.
     * @param maxColumns  the maximumColumnCount.
     */
    public SlidingGanttCategoryDataset(GanttCategoryDataset<R, C> underlying, int firstColumn, int maxColumns) {
        super();
        this.underlying = underlying;
        this.firstCategoryIndex = firstColumn;
        this.maximumCategoryCount = maxColumns;
    }

    /**
     * Returns the underlying dataset that was supplied to the constructor.
     *
     * @return The underlying dataset (never {@code null}).
     */
    public GanttCategoryDataset<R, C> getUnderlyingDataset() {
        return this.underlying;
    }

    /**
     * Returns the index of the first visible category.
     *
     * @return The index.
     *
     * @see #setFirstCategoryIndex(int)
     */
    public int getFirstCategoryIndex() {
        return this.firstCategoryIndex;
    }

    /**
     * Sets the index of the first category that should be used from the
     * underlying dataset, and sends a {@link DatasetChangeEvent} to all
     * registered listeners.
     *
     * @param first  the index.
     *
     * @see #getFirstCategoryIndex()
     */
    public void setFirstCategoryIndex(int first) {
        if (first < 0 || first >= this.underlying.getColumnCount()) {
            throw new IllegalArgumentException("Invalid index.");
        }
        this.firstCategoryIndex = first;
        fireDatasetChanged();
    }

    /**
     * Returns the maximum category count.
     *
     * @return The maximum category count.
     *
     * @see #setMaximumCategoryCount(int)
     */
    public int getMaximumCategoryCount() {
        return this.maximumCategoryCount;
    }

    /**
     * Sets the maximum category count and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param max  the maximum.
     *
     * @see #getMaximumCategoryCount()
     */
    public void setMaximumCategoryCount(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("Requires 'max' >= 0.");
        }
        this.maximumCategoryCount = max;
        fireDatasetChanged();
    }

    /**
     * Returns the index of the last column for this dataset, or -1.
     *
     * @return The index.
     */
    private int lastCategoryIndex() {
        if (this.maximumCategoryCount == 0) {
            return -1;
        }
        return Math.min(this.firstCategoryIndex + this.maximumCategoryCount, this.underlying.getColumnCount()) - 1;
    }

    /**
     * Returns the index for the specified column key.
     *
     * @param key  the key.
     *
     * @return The column index, or -1 if the key is not recognised.
     */
    @Override
    public int getColumnIndex(C key) {
        int index = this.underlying.getColumnIndex(key);
        if (index >= this.firstCategoryIndex && index <= lastCategoryIndex()) {
            return index - this.firstCategoryIndex;
        }
        // we didn't find the key
        return -1;
    }

    /**
     * Returns the column key for a given index.
     *
     * @param column  the column index (zero-based).
     *
     * @return The column key.
     *
     * @throws IndexOutOfBoundsException if {@code row} is out of bounds.
     */
    @Override
    public C getColumnKey(int column) {
        return this.underlying.getColumnKey(column + this.firstCategoryIndex);
    }

    /**
     * Returns the column keys.
     *
     * @return The keys.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public List<C> getColumnKeys() {
        List<C> result = new ArrayList<>();
        int last = lastCategoryIndex();
        for (int i = this.firstCategoryIndex; i < last; i++) {
            result.add(this.underlying.getColumnKey(i));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the row index for a given key.
     *
     * @param key  the row key.
     *
     * @return The row index, or {@code -1} if the key is unrecognised.
     */
    @Override
    public int getRowIndex(R key) {
        return this.underlying.getRowIndex(key);
    }

    /**
     * Returns the row key for a given index.
     *
     * @param row  the row index (zero-based).
     *
     * @return The row key.
     *
     * @throws IndexOutOfBoundsException if {@code row} is out of bounds.
     */
    @Override
    public R getRowKey(int row) {
        return this.underlying.getRowKey(row);
    }

    /**
     * Returns the row keys.
     *
     * @return The keys.
     */
    @Override
    public List<R> getRowKeys() {
        return this.underlying.getRowKeys();
    }

    /**
     * Returns the value for a pair of keys.
     *
     * @param rowKey  the row key ({@code null} not permitted).
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @return The value (possibly {@code null}).
     *
     * @throws UnknownKeyException if either key is not defined in the dataset.
     */
    @Override
    public Number getValue(R rowKey, C columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getValue(r, c + this.firstCategoryIndex);
        }
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return The column count.
     */
    @Override
    public int getColumnCount() {
        int last = lastCategoryIndex();
        if (last == -1) {
            return 0;
        } else {
            return Math.max(last - this.firstCategoryIndex + 1, 0);
        }
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return The row count.
     */
    @Override
    public int getRowCount() {
        return this.underlying.getRowCount();
    }

    /**
     * Returns a value from the table.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The value (possibly {@code null}).
     */
    @Override
    public Number getValue(int row, int column) {
        return this.underlying.getValue(row, column + this.firstCategoryIndex);
    }

    /**
     * Returns the percent complete for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The percent complete.
     */
    @Override
    public Number getPercentComplete(R rowKey, C columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getPercentComplete(r, c + this.firstCategoryIndex);
        }
    }

    /**
     * Returns the percentage complete value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the sub-interval.
     *
     * @return The percent complete value (possibly {@code null}).
     *
     * @see #getPercentComplete(int, int, int)
     */
    @Override
    public Number getPercentComplete(R rowKey, C columnKey, int subinterval) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getPercentComplete(r, c + this.firstCategoryIndex, subinterval);
        }
    }

    /**
     * Returns the end value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the sub-interval.
     *
     * @return The end value (possibly {@code null}).
     *
     * @see #getStartValue(Comparable, Comparable, int)
     */
    @Override
    public Number getEndValue(R rowKey, C columnKey, int subinterval) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getEndValue(r, c + this.firstCategoryIndex, subinterval);
        }
    }

    /**
     * Returns the end value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the sub-interval.
     *
     * @return The end value (possibly {@code null}).
     *
     * @see #getStartValue(int, int, int)
     */
    @Override
    public Number getEndValue(int row, int column, int subinterval) {
        return this.underlying.getEndValue(row, column + this.firstCategoryIndex, subinterval);
    }

    /**
     * Returns the percent complete for a given item.
     *
     * @param series  the row index (zero-based).
     * @param category  the column index (zero-based).
     *
     * @return The percent complete.
     */
    @Override
    public Number getPercentComplete(int series, int category) {
        return this.underlying.getPercentComplete(series, category + this.firstCategoryIndex);
    }

    /**
     * Returns the percentage complete value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the sub-interval.
     *
     * @return The percent complete value (possibly {@code null}).
     *
     * @see #getPercentComplete(Comparable, Comparable, int)
     */
    @Override
    public Number getPercentComplete(int row, int column, int subinterval) {
        return this.underlying.getPercentComplete(row, column + this.firstCategoryIndex, subinterval);
    }

    /**
     * Returns the start value of a sub-interval for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param subinterval  the sub-interval.
     *
     * @return The start value (possibly {@code null}).
     *
     * @see #getEndValue(Comparable, Comparable, int)
     */
    @Override
    public Number getStartValue(R rowKey, C columnKey, int subinterval) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getStartValue(r, c + this.firstCategoryIndex, subinterval);
        }
    }

    /**
     * Returns the start value of a sub-interval for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param subinterval  the sub-interval index (zero-based).
     *
     * @return The start value (possibly {@code null}).
     *
     * @see #getEndValue(int, int, int)
     */
    @Override
    public Number getStartValue(int row, int column, int subinterval) {
        return this.underlying.getStartValue(row, column + this.firstCategoryIndex, subinterval);
    }

    /**
     * Returns the number of sub-intervals for a given item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     *
     * @return The sub-interval count.
     *
     * @see #getSubIntervalCount(int, int)
     */
    @Override
    public int getSubIntervalCount(R rowKey, C columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getSubIntervalCount(r, c + this.firstCategoryIndex);
        }
    }

    /**
     * Returns the number of sub-intervals for a given item.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The sub-interval count.
     *
     * @see #getSubIntervalCount(Comparable, Comparable)
     */
    @Override
    public int getSubIntervalCount(int row, int column) {
        return this.underlying.getSubIntervalCount(row, column + this.firstCategoryIndex);
    }

    /**
     * Returns the start value for the interval for a given series and category.
     *
     * @param rowKey  the series key.
     * @param columnKey  the category key.
     *
     * @return The start value (possibly {@code null}).
     *
     * @see #getEndValue(Comparable, Comparable)
     */
    @Override
    public Number getStartValue(R rowKey, C columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getStartValue(r, c + this.firstCategoryIndex);
        }
    }

    /**
     * Returns the start value for the interval for a given series and category.
     *
     * @param row  the series (zero-based index).
     * @param column  the category (zero-based index).
     *
     * @return The start value (possibly {@code null}).
     *
     * @see #getEndValue(int, int)
     */
    @Override
    public Number getStartValue(int row, int column) {
        return this.underlying.getStartValue(row, column + this.firstCategoryIndex);
    }

    /**
     * Returns the end value for the interval for a given series and category.
     *
     * @param rowKey  the series key.
     * @param columnKey  the category key.
     *
     * @return The end value (possibly {@code null}).
     *
     * @see #getStartValue(Comparable, Comparable)
     */
    @Override
    public Number getEndValue(R rowKey, C columnKey) {
        int r = getRowIndex(rowKey);
        int c = getColumnIndex(columnKey);
        if (c == -1) {
            throw new UnknownKeyException("Unknown columnKey: " + columnKey);
        } else if (r == -1) {
            throw new UnknownKeyException("Unknown rowKey: " + rowKey);
        } else {
            return this.underlying.getEndValue(r, c + this.firstCategoryIndex);
        }
    }

    /**
     * Returns the end value for the interval for a given series and category.
     *
     * @param series  the series (zero-based index).
     * @param category  the category (zero-based index).
     *
     * @return The end value (possibly {@code null}).
     */
    @Override
    public Number getEndValue(int series, int category) {
        return this.underlying.getEndValue(series, category + this.firstCategoryIndex);
    }

    /**
     * Tests this {@code SlidingGanttCategoryDataset} instance for equality
     * with an arbitrary object.
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
        if (!(obj instanceof SlidingGanttCategoryDataset)) {
            return false;
        }
        SlidingGanttCategoryDataset<R, C> that = (SlidingGanttCategoryDataset<R, C>) obj;
        if (this.firstCategoryIndex != that.firstCategoryIndex) {
            return false;
        }
        if (this.maximumCategoryCount != that.maximumCategoryCount) {
            return false;
        }
        if (!this.underlying.equals(that.underlying)) {
            return false;
        }
        return true;
    }

    /**
     * Returns an independent copy of the dataset.  Note that:
     * <ul>
     * <li>the underlying dataset is only cloned if it implements the
     * {@link PublicCloneable} interface;</li>
     * <li>the listeners registered with this dataset are not carried over to
     * the cloned dataset.</li>
     * </ul>
     *
     * @return An independent copy of the dataset.
     *
     * @throws CloneNotSupportedException if the dataset cannot be cloned for
     *         any reason.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        SlidingGanttCategoryDataset<R, C> clone = (SlidingGanttCategoryDataset<R, C>) super.clone();
        if (this.underlying instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.underlying;
            clone.underlying = (GanttCategoryDataset<R, C>) pc.clone();
        }
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
 * --------------------
 * XYAreaRenderer2.java
 * --------------------
 * (C) Copyright 2004-2021, by Hari and Contributors.
 *
 * Original Author:  Hari (ourhari@hotmail.com);
 * Contributor(s):   David Gilbert;
 *                   Richard Atkinson;
 *                   Christian W. Zuckschwerdt;
 *                   Martin Krauskopf;
 *                   Ulrich Voigt (patch #312);
 */
/**
 * Area item renderer for an {@link XYPlot}. The example shown here is
 * generated by the {@code XYAreaRenderer2Demo1.java} program included in
 * the JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/XYAreaRenderer2Sample.png"
 * alt="XYAreaRenderer2Sample.png">
 */
public class XYAreaRenderer2 extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -7378069681579984133L;

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
     * Constructs a new renderer.
     */
    public XYAreaRenderer2() {
        this(null, null);
    }

    /**
     * Constructs a new renderer.
     *
     * @param labelGenerator  the tool tip generator to use ({@code null}
     *     permitted).
     * @param urlGenerator  the URL generator ({@code null} permitted).
     */
    public XYAreaRenderer2(XYToolTipGenerator labelGenerator, XYURLGenerator urlGenerator) {
        super();
        this.showOutline = false;
        setDefaultToolTipGenerator(labelGenerator);
        setURLGenerator(urlGenerator);
        GeneralPath area = new GeneralPath();
        area.moveTo(0.0f, -4.0f);
        area.lineTo(3.0f, -2.0f);
        area.lineTo(4.0f, 4.0f);
        area.lineTo(-4.0f, 4.0f);
        area.lineTo(-3.0f, -2.0f);
        area.closePath();
        this.legendArea = area;
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
     * Sets a flag that controls whether outlines of the areas are
     * drawn, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
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
     *
     * @see #setLegendArea(Shape)
     */
    public Shape getLegendArea() {
        return this.legendArea;
    }

    /**
     * Sets the shape used as an area in each legend item and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param area  the area ({@code null} not permitted).
     *
     * @see #getLegendArea()
     */
    public void setLegendArea(Shape area) {
        Args.nullNotPermitted(area, "area");
        this.legendArea = area;
        fireChangeEvent();
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
        if (!getItemVisible(series, item)) {
            return;
        }
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
        double x0 = dataset.getXValue(series, Math.max(item - 1, 0));
        double y0 = dataset.getYValue(series, Math.max(item - 1, 0));
        if (Double.isNaN(y0)) {
            y0 = 0.0;
        }
        double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
        double transY0 = rangeAxis.valueToJava2D(y0, dataArea, plot.getRangeAxisEdge());
        int itemCount = dataset.getItemCount(series);
        double x2 = dataset.getXValue(series, Math.min(item + 1, itemCount - 1));
        double y2 = dataset.getYValue(series, Math.min(item + 1, itemCount - 1));
        if (Double.isNaN(y2)) {
            y2 = 0.0;
        }
        double transX2 = domainAxis.valueToJava2D(x2, dataArea, plot.getDomainAxisEdge());
        double transY2 = rangeAxis.valueToJava2D(y2, dataArea, plot.getRangeAxisEdge());
        double transZero = rangeAxis.valueToJava2D(0.0, dataArea, plot.getRangeAxisEdge());
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
        PlotOrientation orientation = plot.getOrientation();
        Paint paint = getItemPaint(series, item);
        Stroke stroke = getItemStroke(series, item);
        g2.setPaint(paint);
        g2.setStroke(stroke);
        // Check if the item is the last item for the series.
        // and number of items > 0.  We can't draw an area for a single point.
        g2.fill(hotspot);
        // draw an outline around the Area.
        if (isOutline()) {
            g2.setStroke(lookupSeriesOutlineStroke(series));
            g2.setPaint(lookupSeriesOutlinePaint(series));
            g2.draw(hotspot);
        }
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(crosshairState, x1, y1, datasetIndex, transX1, transY1, orientation);
        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                // limit the entity hotspot area to the data area
                Area dataAreaHotspot = new Area(hotspot);
                dataAreaHotspot.intersect(new Area(dataArea));
                if (!dataAreaHotspot.isEmpty()) {
                    String tip = null;
                    XYToolTipGenerator generator = getToolTipGenerator(series, item);
                    if (generator != null) {
                        tip = generator.generateToolTip(dataset, series, item);
                    }
                    String url = null;
                    if (getURLGenerator() != null) {
                        url = getURLGenerator().generateURL(dataset, series, item);
                    }
                    XYItemEntity entity = new XYItemEntity(dataAreaHotspot, dataset, series, item, tip, url);
                    entities.add(entity);
                }
            }
        }
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} not permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYAreaRenderer2)) {
            return false;
        }
        XYAreaRenderer2 that = (XYAreaRenderer2) obj;
        if (this.showOutline != that.showOutline) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendArea, that.legendArea)) {
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
        XYAreaRenderer2 clone = (XYAreaRenderer2) super.clone();
        clone.legendArea = CloneUtils.clone(this.legendArea);
        return clone;
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
 * LegendItem.java
 * ---------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Luke Quinane;
 *
 */
/**
 * A temporary storage object for recording the properties of a legend item,
 * without any consideration for layout issues.
 */
public class LegendItem implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -797214582948827144L;

    /**
     * The dataset.
     */
    private Dataset dataset;

    /**
     * The series key.
     */
    private Comparable seriesKey;

    /**
     * The dataset index.
     */
    private int datasetIndex;

    /**
     * The series index.
     */
    private int series;

    /**
     * The label.
     */
    private final String label;

    /**
     * The label font ({@code null} is permitted).
     */
    private Font labelFont;

    /**
     * The label paint ({@code null} is permitted).
     */
    private transient Paint labelPaint;

    /**
     * The attributed label (if null, fall back to the regular label).
     */
    private transient AttributedString attributedLabel;

    /**
     * The description (not currently used - could be displayed as a tool tip).
     */
    private String description;

    /**
     * The tool tip text.
     */
    private String toolTipText;

    /**
     * The url text.
     */
    private String urlText;

    /**
     * A flag that controls whether the shape is visible.
     */
    private boolean shapeVisible;

    /**
     * The shape.
     */
    private transient Shape shape;

    /**
     * A flag that controls whether the shape is filled.
     */
    private final boolean shapeFilled;

    /**
     * The paint.
     */
    private transient Paint fillPaint;

    /**
     * A gradient paint transformer.
     */
    private GradientPaintTransformer fillPaintTransformer;

    /**
     * A flag that controls whether the shape outline is visible.
     */
    private final boolean shapeOutlineVisible;

    /**
     * The outline paint.
     */
    private transient Paint outlinePaint;

    /**
     * The outline stroke.
     */
    private transient Stroke outlineStroke;

    /**
     * A flag that controls whether the line is visible.
     */
    private boolean lineVisible;

    /**
     * The line.
     */
    private transient Shape line;

    /**
     * The stroke.
     */
    private transient Stroke lineStroke;

    /**
     * The line paint.
     */
    private transient Paint linePaint;

    /**
     * The shape must be non-null for a LegendItem - if no shape is required,
     * use this.
     */
    private static final Shape UNUSED_SHAPE = new Line2D.Float();

    /**
     * The stroke must be non-null for a LegendItem - if no stroke is required,
     * use this.
     */
    private static final Stroke UNUSED_STROKE = new BasicStroke(0.0f);

    /**
     * Creates a legend item with the specified label.  The remaining
     * attributes take default values.
     *
     * @param label  the label ({@code null} not permitted).
     */
    public LegendItem(String label) {
        this(label, Color.BLACK);
    }

    /**
     * Creates a legend item with the specified label and fill paint.  The
     * remaining attributes take default values.
     *
     * @param label  the label ({@code null} not permitted).
     * @param paint  the paint ({@code null} not permitted).
     */
    public LegendItem(String label, Paint paint) {
        this(label, null, null, null, new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0), paint);
    }

    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined */
        false, Color.BLACK, UNUSED_STROKE, /* line visible */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item with a filled and outlined shape.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param outlinePaint  the outline paint ({@code null} not
     *                      permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint, Stroke outlineStroke, Paint outlinePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined = */
        true, outlinePaint, outlineStroke, /* line visible */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item using a line.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the line stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, Shape line, Stroke lineStroke, Paint linePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        false, UNUSED_SHAPE, /* shape filled = */
        false, Color.BLACK, /* shape outlined = */
        false, Color.BLACK, UNUSED_STROKE, /* line visible = */
        true, line, lineStroke, linePaint);
    }

    /**
     * Creates a new legend item.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description (not currently used,
     *        {@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shapeVisible  a flag that controls whether the shape is
     *                      displayed.
     * @param shape  the shape ({@code null} permitted).
     * @param shapeFilled  a flag that controls whether the shape is
     *                     filled.
     * @param fillPaint  the fill paint ({@code null} not permitted).
     * @param shapeOutlineVisible  a flag that controls whether the
     *                             shape is outlined.
     * @param outlinePaint  the outline paint ({@code null} not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param lineVisible  a flag that controls whether the line is
     *                     visible.
     * @param line  the line.
     * @param lineStroke  the stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(String label, String description, String toolTipText, String urlText, boolean shapeVisible, Shape shape, boolean shapeFilled, Paint fillPaint, boolean shapeOutlineVisible, Paint outlinePaint, Stroke outlineStroke, boolean lineVisible, Shape line, Stroke lineStroke, Paint linePaint) {
        Args.nullNotPermitted(label, "label");
        Args.nullNotPermitted(fillPaint, "fillPaint");
        Args.nullNotPermitted(lineStroke, "lineStroke");
        Args.nullNotPermitted(outlinePaint, "outlinePaint");
        Args.nullNotPermitted(outlineStroke, "outlineStroke");
        this.label = label;
        this.labelPaint = null;
        this.attributedLabel = null;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    /**
     * Creates a legend item with a filled shape.  The shape is not outlined,
     * and no line is visible.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined = */
        false, Color.BLACK, UNUSED_STROKE, /* line visible = */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item with a filled and outlined shape.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shape  the shape ({@code null} not permitted).
     * @param fillPaint  the paint used to fill the shape ({@code null}
     *                   not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param outlinePaint  the outline paint ({@code null} not
     *                      permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape shape, Paint fillPaint, Stroke outlineStroke, Paint outlinePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        true, shape, /* shape filled = */
        true, fillPaint, /* shape outlined = */
        true, outlinePaint, outlineStroke, /* line visible = */
        false, UNUSED_SHAPE, UNUSED_STROKE, Color.BLACK);
    }

    /**
     * Creates a legend item using a line.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description ({@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the line stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, Shape line, Stroke lineStroke, Paint linePaint) {
        this(label, description, toolTipText, urlText, /* shape visible = */
        false, UNUSED_SHAPE, /* shape filled = */
        false, Color.BLACK, /* shape outlined = */
        false, Color.BLACK, UNUSED_STROKE, /* line visible = */
        true, line, lineStroke, linePaint);
    }

    /**
     * Creates a new legend item.
     *
     * @param label  the label ({@code null} not permitted).
     * @param description  the description (not currently used,
     *        {@code null} permitted).
     * @param toolTipText  the tool tip text ({@code null} permitted).
     * @param urlText  the URL text ({@code null} permitted).
     * @param shapeVisible  a flag that controls whether the shape is
     *                      displayed.
     * @param shape  the shape ({@code null} permitted).
     * @param shapeFilled  a flag that controls whether the shape is
     *                     filled.
     * @param fillPaint  the fill paint ({@code null} not permitted).
     * @param shapeOutlineVisible  a flag that controls whether the
     *                             shape is outlined.
     * @param outlinePaint  the outline paint ({@code null} not permitted).
     * @param outlineStroke  the outline stroke ({@code null} not
     *                       permitted).
     * @param lineVisible  a flag that controls whether the line is
     *                     visible.
     * @param line  the line ({@code null} not permitted).
     * @param lineStroke  the stroke ({@code null} not permitted).
     * @param linePaint  the line paint ({@code null} not permitted).
     */
    public LegendItem(AttributedString label, String description, String toolTipText, String urlText, boolean shapeVisible, Shape shape, boolean shapeFilled, Paint fillPaint, boolean shapeOutlineVisible, Paint outlinePaint, Stroke outlineStroke, boolean lineVisible, Shape line, Stroke lineStroke, Paint linePaint) {
        Args.nullNotPermitted(label, "label");
        Args.nullNotPermitted(fillPaint, "fillPaint");
        Args.nullNotPermitted(lineStroke, "lineStroke");
        Args.nullNotPermitted(line, "line");
        Args.nullNotPermitted(linePaint, "linePaint");
        Args.nullNotPermitted(outlinePaint, "outlinePaint");
        Args.nullNotPermitted(outlineStroke, "outlineStroke");
        this.label = characterIteratorToString(label.getIterator());
        this.attributedLabel = label;
        this.description = description;
        this.shapeVisible = shapeVisible;
        this.shape = shape;
        this.shapeFilled = shapeFilled;
        this.fillPaint = fillPaint;
        this.fillPaintTransformer = new StandardGradientPaintTransformer();
        this.shapeOutlineVisible = shapeOutlineVisible;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.lineVisible = lineVisible;
        this.line = line;
        this.lineStroke = lineStroke;
        this.linePaint = linePaint;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    /**
     * Returns a string containing the characters from the given iterator.
     *
     * @param iterator  the iterator ({@code null} not permitted).
     *
     * @return A string.
     */
    private String characterIteratorToString(CharacterIterator iterator) {
        int endIndex = iterator.getEndIndex();
        int beginIndex = iterator.getBeginIndex();
        int count = endIndex - beginIndex;
        if (count <= 0) {
            return "";
        }
        char[] chars = new char[count];
        int i = 0;
        char c = iterator.first();
        while (c != CharacterIterator.DONE) {
            chars[i] = c;
            i++;
            c = iterator.next();
        }
        return new String(chars);
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset.
     *
     * @see #setDatasetIndex(int)
     */
    public Dataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset.
     *
     * @param dataset  the dataset.
     */
    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    /**
     * Returns the dataset index for this legend item.
     *
     * @return The dataset index.
     *
     * @see #setDatasetIndex(int)
     * @see #getDataset()
     */
    public int getDatasetIndex() {
        return this.datasetIndex;
    }

    /**
     * Sets the dataset index for this legend item.
     *
     * @param index  the index.
     *
     * @see #getDatasetIndex()
     */
    public void setDatasetIndex(int index) {
        this.datasetIndex = index;
    }

    /**
     * Returns the series key.
     *
     * @return The series key.
     *
     * @see #setSeriesKey(Comparable)
     */
    public Comparable getSeriesKey() {
        return this.seriesKey;
    }

    /**
     * Sets the series key.
     *
     * @param key  the series key.
     */
    public void setSeriesKey(Comparable key) {
        this.seriesKey = key;
    }

    /**
     * Returns the series index for this legend item.
     *
     * @return The series index.
     */
    public int getSeriesIndex() {
        return this.series;
    }

    /**
     * Sets the series index for this legend item.
     *
     * @param index  the index.
     */
    public void setSeriesIndex(int index) {
        this.series = index;
    }

    /**
     * Returns the label.
     *
     * @return The label (never {@code null}).
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Returns the label font.
     *
     * @return The label font (possibly {@code null}).
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font.
     *
     * @param font  the font ({@code null} permitted).
     */
    public void setLabelFont(Font font) {
        this.labelFont = font;
    }

    /**
     * Returns the paint used to draw the label.
     *
     * @return The paint (possibly {@code null}).
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the paint used to draw the label.
     *
     * @param paint  the paint ({@code null} permitted).
     */
    public void setLabelPaint(Paint paint) {
        this.labelPaint = paint;
    }

    /**
     * Returns the attributed label.
     *
     * @return The attributed label (possibly {@code null}).
     */
    public AttributedString getAttributedLabel() {
        return this.attributedLabel;
    }

    /**
     * Returns the description for the legend item.
     *
     * @return The description (possibly {@code null}).
     *
     * @see #setDescription(java.lang.String)
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for this legend item.
     *
     * @param text  the description ({@code null} permitted).
     *
     * @see #getDescription()
     */
    public void setDescription(String text) {
        this.description = text;
    }

    /**
     * Returns the tool tip text.
     *
     * @return The tool tip text (possibly {@code null}).
     *
     * @see #setToolTipText(java.lang.String)
     */
    public String getToolTipText() {
        return this.toolTipText;
    }

    /**
     * Sets the tool tip text for this legend item.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getToolTipText()
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    /**
     * Returns the URL text.
     *
     * @return The URL text (possibly {@code null}).
     *
     * @see #setURLText(java.lang.String)
     */
    public String getURLText() {
        return this.urlText;
    }

    /**
     * Sets the URL text.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getURLText()
     */
    public void setURLText(String text) {
        this.urlText = text;
    }

    /**
     * Returns a flag that indicates whether the shape is visible.
     *
     * @return A boolean.
     *
     * @see #setShapeVisible(boolean)
     */
    public boolean isShapeVisible() {
        return this.shapeVisible;
    }

    /**
     * Sets the flag that controls whether the shape is visible.
     *
     * @param visible  the new flag value.
     *
     * @see #isShapeVisible()
     * @see #isLineVisible()
     */
    public void setShapeVisible(boolean visible) {
        this.shapeVisible = visible;
    }

    /**
     * Returns the shape used to label the series represented by this legend
     * item.
     *
     * @return The shape (never {@code null}).
     *
     * @see #setShape(java.awt.Shape)
     */
    public Shape getShape() {
        return this.shape;
    }

    /**
     * Sets the shape for the legend item.
     *
     * @param shape  the shape ({@code null} not permitted).
     *
     * @see #getShape()
     */
    public void setShape(Shape shape) {
        Args.nullNotPermitted(shape, "shape");
        this.shape = shape;
    }

    /**
     * Returns a flag that controls whether the shape is filled.
     *
     * @return A boolean.
     */
    public boolean isShapeFilled() {
        return this.shapeFilled;
    }

    /**
     * Returns the fill paint.
     *
     * @return The fill paint (never {@code null}).
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Sets the fill paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setFillPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.fillPaint = paint;
    }

    /**
     * Returns the flag that controls whether the shape outline
     * is visible.
     *
     * @return A boolean.
     */
    public boolean isShapeOutlineVisible() {
        return this.shapeOutlineVisible;
    }

    /**
     * Returns the line stroke for the series.
     *
     * @return The stroke (never {@code null}).
     */
    public Stroke getLineStroke() {
        return this.lineStroke;
    }

    /**
     * Sets the line stroke.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     */
    public void setLineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.lineStroke = stroke;
    }

    /**
     * Returns the paint used for lines.
     *
     * @return The paint (never {@code null}).
     */
    public Paint getLinePaint() {
        return this.linePaint;
    }

    /**
     * Sets the line paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setLinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.linePaint = paint;
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (never {@code null}).
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setOutlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.outlinePaint = paint;
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (never {@code null}).
     *
     * @see #setOutlineStroke(java.awt.Stroke)
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getOutlineStroke()
     */
    public void setOutlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.outlineStroke = stroke;
    }

    /**
     * Returns a flag that indicates whether the line is visible.
     *
     * @return A boolean.
     *
     * @see #setLineVisible(boolean)
     */
    public boolean isLineVisible() {
        return this.lineVisible;
    }

    /**
     * Sets the flag that controls whether the line shape is visible for
     * this legend item.
     *
     * @param visible  the new flag value.
     *
     * @see #isLineVisible()
     */
    public void setLineVisible(boolean visible) {
        this.lineVisible = visible;
    }

    /**
     * Returns the line.
     *
     * @return The line (never {@code null}).
     *
     * @see #setLine(java.awt.Shape)
     * @see #isLineVisible()
     */
    public Shape getLine() {
        return this.line;
    }

    /**
     * Sets the line.
     *
     * @param line  the line ({@code null} not permitted).
     *
     * @see #getLine()
     */
    public void setLine(Shape line) {
        Args.nullNotPermitted(line, "line");
        this.line = line;
    }

    /**
     * Returns the transformer used when the fill paint is an instance of
     * {@code GradientPaint}.
     *
     * @return The transformer (never {@code null}).
     *
     * @see #setFillPaintTransformer(GradientPaintTransformer)
     */
    public GradientPaintTransformer getFillPaintTransformer() {
        return this.fillPaintTransformer;
    }

    /**
     * Sets the transformer used when the fill paint is an instance of
     * {@code GradientPaint}.
     *
     * @param transformer  the transformer ({@code null} not permitted).
     *
     * @see #getFillPaintTransformer()
     */
    public void setFillPaintTransformer(GradientPaintTransformer transformer) {
        Args.nullNotPermitted(transformer, "transformer");
        this.fillPaintTransformer = transformer;
    }

    /**
     * Tests this item for equality with an arbitrary object.
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
        if (!(obj instanceof LegendItem)) {
            return false;
        }
        LegendItem that = (LegendItem) obj;
        if (this.datasetIndex != that.datasetIndex) {
            return false;
        }
        if (this.series != that.series) {
            return false;
        }
        if (!this.label.equals(that.label)) {
            return false;
        }
        if (!AttributedStringUtils.equal(this.attributedLabel, that.attributedLabel)) {
            return false;
        }
        if (!Objects.equals(this.description, that.description)) {
            return false;
        }
        if (this.shapeVisible != that.shapeVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.shape, that.shape)) {
            return false;
        }
        if (this.shapeFilled != that.shapeFilled) {
            return false;
        }
        if (!PaintUtils.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!Objects.equals(this.fillPaintTransformer, that.fillPaintTransformer)) {
            return false;
        }
        if (this.shapeOutlineVisible != that.shapeOutlineVisible) {
            return false;
        }
        if (!this.outlineStroke.equals(that.outlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!this.lineVisible == that.lineVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.line, that.line)) {
            return false;
        }
        if (!this.lineStroke.equals(that.lineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.linePaint, that.linePaint)) {
            return false;
        }
        if (!Objects.equals(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.datasetIndex;
        hash = 79 * hash + this.series;
        hash = 79 * hash + Objects.hashCode(this.label);
        hash = 79 * hash + Objects.hashCode(this.labelFont);
        hash = 79 * hash + Objects.hashCode(this.labelPaint);
        hash = 79 * hash + Objects.hashCode(this.attributedLabel);
        hash = 79 * hash + Objects.hashCode(this.description);
        hash = 79 * hash + (this.shapeVisible ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.shape);
        hash = 79 * hash + (this.shapeFilled ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.fillPaint);
        hash = 79 * hash + Objects.hashCode(this.fillPaintTransformer);
        hash = 79 * hash + (this.shapeOutlineVisible ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.outlinePaint);
        hash = 79 * hash + Objects.hashCode(this.outlineStroke);
        hash = 79 * hash + (this.lineVisible ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(this.line);
        hash = 79 * hash + Objects.hashCode(this.lineStroke);
        hash = 79 * hash + Objects.hashCode(this.linePaint);
        return hash;
    }

    /**
     * Returns an independent copy of this object (except that the clone will
     * still reference the same dataset as the original {@code LegendItem}).
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the legend item cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        LegendItem clone = (LegendItem) super.clone();
        if (this.seriesKey instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.seriesKey;
            clone.seriesKey = (Comparable) pc.clone();
        }
        // FIXME: Clone the attributed string if it is not null
        clone.shape = CloneUtils.clone(this.shape);
        if (this.fillPaintTransformer instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.fillPaintTransformer;
            clone.fillPaintTransformer = (GradientPaintTransformer) pc.clone();
        }
        clone.line = CloneUtils.clone(this.line);
        return clone;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream ({@code null} not permitted).
     *
     * @throws IOException  if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeAttributedString(this.attributedLabel, stream);
        SerialUtils.writeShape(this.shape, stream);
        SerialUtils.writePaint(this.fillPaint, stream);
        SerialUtils.writeStroke(this.outlineStroke, stream);
        SerialUtils.writePaint(this.outlinePaint, stream);
        SerialUtils.writeShape(this.line, stream);
        SerialUtils.writeStroke(this.lineStroke, stream);
        SerialUtils.writePaint(this.linePaint, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the input stream ({@code null} not permitted).
     *
     * @throws IOException  if there is an I/O error.
     * @throws ClassNotFoundException  if there is a classpath problem.
     */
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.attributedLabel = SerialUtils.readAttributedString(stream);
        this.shape = SerialUtils.readShape(stream);
        this.fillPaint = SerialUtils.readPaint(stream);
        this.outlineStroke = SerialUtils.readStroke(stream);
        this.outlinePaint = SerialUtils.readPaint(stream);
        this.line = SerialUtils.readShape(stream);
        this.lineStroke = SerialUtils.readStroke(stream);
        this.linePaint = SerialUtils.readPaint(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
    }
}
