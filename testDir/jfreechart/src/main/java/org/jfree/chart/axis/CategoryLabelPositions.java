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
 * CategoryLabelPositions.java
 * ---------------------------
 * (C) Copyright 2004-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * Records the label positions for a category axis.  Instances of this class
 * are immutable.
 */
public class CategoryLabelPositions implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8999557901920364580L;

    /**
     * STANDARD category label positions.
     */
    public static final CategoryLabelPositions STANDARD = new CategoryLabelPositions(new // TOP
    CategoryLabelPosition(// TOP
    RectangleAnchor.BOTTOM, TextBlockAnchor.BOTTOM_CENTER), new // BOTTOM
    CategoryLabelPosition(// BOTTOM
    RectangleAnchor.TOP, TextBlockAnchor.TOP_CENTER), new // LEFT
    CategoryLabelPosition(// LEFT
    RectangleAnchor.RIGHT, // LEFT
    TextBlockAnchor.CENTER_RIGHT, // LEFT
    CategoryLabelWidthType.RANGE, 0.30f), new // RIGHT
    CategoryLabelPosition(// RIGHT
    RectangleAnchor.LEFT, // RIGHT
    TextBlockAnchor.CENTER_LEFT, // RIGHT
    CategoryLabelWidthType.RANGE, 0.30f));

    /**
     * UP_90 category label positions.
     */
    public static final CategoryLabelPositions UP_90 = new CategoryLabelPositions(new // TOP
    CategoryLabelPosition(// TOP
    RectangleAnchor.BOTTOM, // TOP
    TextBlockAnchor.CENTER_LEFT, // TOP
    TextAnchor.CENTER_LEFT, // TOP
    -Math.PI / 2.0, // TOP
    CategoryLabelWidthType.RANGE, 0.30f), new // BOTTOM
    CategoryLabelPosition(// BOTTOM
    RectangleAnchor.TOP, // BOTTOM
    TextBlockAnchor.CENTER_RIGHT, // BOTTOM
    TextAnchor.CENTER_RIGHT, // BOTTOM
    -Math.PI / 2.0, // BOTTOM
    CategoryLabelWidthType.RANGE, 0.30f), new // LEFT
    CategoryLabelPosition(// LEFT
    RectangleAnchor.RIGHT, // LEFT
    TextBlockAnchor.BOTTOM_CENTER, // LEFT
    TextAnchor.BOTTOM_CENTER, // LEFT
    -Math.PI / 2.0, // LEFT
    CategoryLabelWidthType.CATEGORY, 0.9f), new // RIGHT
    CategoryLabelPosition(// RIGHT
    RectangleAnchor.LEFT, // RIGHT
    TextBlockAnchor.TOP_CENTER, // RIGHT
    TextAnchor.TOP_CENTER, // RIGHT
    -Math.PI / 2.0, // RIGHT
    CategoryLabelWidthType.CATEGORY, 0.90f));

    /**
     * DOWN_90 category label positions.
     */
    public static final CategoryLabelPositions DOWN_90 = new CategoryLabelPositions(new // TOP
    CategoryLabelPosition(// TOP
    RectangleAnchor.BOTTOM, // TOP
    TextBlockAnchor.CENTER_RIGHT, // TOP
    TextAnchor.CENTER_RIGHT, // TOP
    Math.PI / 2.0, // TOP
    CategoryLabelWidthType.RANGE, 0.30f), new // BOTTOM
    CategoryLabelPosition(// BOTTOM
    RectangleAnchor.TOP, // BOTTOM
    TextBlockAnchor.CENTER_LEFT, // BOTTOM
    TextAnchor.CENTER_LEFT, // BOTTOM
    Math.PI / 2.0, // BOTTOM
    CategoryLabelWidthType.RANGE, 0.30f), new // LEFT
    CategoryLabelPosition(// LEFT
    RectangleAnchor.RIGHT, // LEFT
    TextBlockAnchor.TOP_CENTER, // LEFT
    TextAnchor.TOP_CENTER, // LEFT
    Math.PI / 2.0, // LEFT
    CategoryLabelWidthType.CATEGORY, 0.90f), new // RIGHT
    CategoryLabelPosition(// RIGHT
    RectangleAnchor.LEFT, // RIGHT
    TextBlockAnchor.BOTTOM_CENTER, // RIGHT
    TextAnchor.BOTTOM_CENTER, // RIGHT
    Math.PI / 2.0, // RIGHT
    CategoryLabelWidthType.CATEGORY, 0.90f));

    /**
     * UP_45 category label positions.
     */
    public static final CategoryLabelPositions UP_45 = createUpRotationLabelPositions(Math.PI / 4.0);

    /**
     * DOWN_45 category label positions.
     */
    public static final CategoryLabelPositions DOWN_45 = createDownRotationLabelPositions(Math.PI / 4.0);

    /**
     * Creates a new instance where the category labels angled upwards by the
     * specified amount.
     *
     * @param angle  the rotation angle (should be &lt; Math.PI / 2.0).
     *
     * @return A category label position specification.
     */
    public static CategoryLabelPositions createUpRotationLabelPositions(double angle) {
        return new CategoryLabelPositions(new // TOP
        CategoryLabelPosition(// TOP
        RectangleAnchor.BOTTOM, // TOP
        TextBlockAnchor.BOTTOM_LEFT, // TOP
        TextAnchor.BOTTOM_LEFT, // TOP
        -angle, // TOP
        CategoryLabelWidthType.RANGE, 0.50f), new // BOTTOM
        CategoryLabelPosition(// BOTTOM
        RectangleAnchor.TOP, // BOTTOM
        TextBlockAnchor.TOP_RIGHT, // BOTTOM
        TextAnchor.TOP_RIGHT, // BOTTOM
        -angle, // BOTTOM
        CategoryLabelWidthType.RANGE, 0.50f), new // LEFT
        CategoryLabelPosition(// LEFT
        RectangleAnchor.RIGHT, // LEFT
        TextBlockAnchor.BOTTOM_RIGHT, // LEFT
        TextAnchor.BOTTOM_RIGHT, // LEFT
        -angle, // LEFT
        CategoryLabelWidthType.RANGE, 0.50f), new // RIGHT
        CategoryLabelPosition(// RIGHT
        RectangleAnchor.LEFT, // RIGHT
        TextBlockAnchor.TOP_LEFT, // RIGHT
        TextAnchor.TOP_LEFT, // RIGHT
        -angle, // RIGHT
        CategoryLabelWidthType.RANGE, 0.50f));
    }

    /**
     * Creates a new instance where the category labels angled downwards by the
     * specified amount.
     *
     * @param angle  the rotation angle (should be &lt; Math.PI / 2.0).
     *
     * @return A category label position specification.
     */
    public static CategoryLabelPositions createDownRotationLabelPositions(double angle) {
        return new CategoryLabelPositions(new // TOP
        CategoryLabelPosition(// TOP
        RectangleAnchor.BOTTOM, // TOP
        TextBlockAnchor.BOTTOM_RIGHT, // TOP
        TextAnchor.BOTTOM_RIGHT, // TOP
        angle, // TOP
        CategoryLabelWidthType.RANGE, 0.50f), new // BOTTOM
        CategoryLabelPosition(// BOTTOM
        RectangleAnchor.TOP, // BOTTOM
        TextBlockAnchor.TOP_LEFT, // BOTTOM
        TextAnchor.TOP_LEFT, // BOTTOM
        angle, // BOTTOM
        CategoryLabelWidthType.RANGE, 0.50f), new // LEFT
        CategoryLabelPosition(// LEFT
        RectangleAnchor.RIGHT, // LEFT
        TextBlockAnchor.TOP_RIGHT, // LEFT
        TextAnchor.TOP_RIGHT, // LEFT
        angle, // LEFT
        CategoryLabelWidthType.RANGE, 0.50f), new // RIGHT
        CategoryLabelPosition(// RIGHT
        RectangleAnchor.LEFT, // RIGHT
        TextBlockAnchor.BOTTOM_LEFT, // RIGHT
        TextAnchor.BOTTOM_LEFT, // RIGHT
        angle, // RIGHT
        CategoryLabelWidthType.RANGE, 0.50f));
    }

    /**
     * The label positioning details used when an axis is at the top of a
     * chart.
     */
    private final CategoryLabelPosition positionForAxisAtTop;

    /**
     * The label positioning details used when an axis is at the bottom of a
     * chart.
     */
    private final CategoryLabelPosition positionForAxisAtBottom;

    /**
     * The label positioning details used when an axis is at the left of a
     * chart.
     */
    private final CategoryLabelPosition positionForAxisAtLeft;

    /**
     * The label positioning details used when an axis is at the right of a
     * chart.
     */
    private final CategoryLabelPosition positionForAxisAtRight;

    /**
     * Default constructor.
     */
    public CategoryLabelPositions() {
        this.positionForAxisAtTop = new CategoryLabelPosition();
        this.positionForAxisAtBottom = new CategoryLabelPosition();
        this.positionForAxisAtLeft = new CategoryLabelPosition();
        this.positionForAxisAtRight = new CategoryLabelPosition();
    }

    /**
     * Creates a new position specification.
     *
     * @param top  the label position info used when an axis is at the top
     *             ({@code null} not permitted).
     * @param bottom  the label position info used when an axis is at the
     *                bottom ({@code null} not permitted).
     * @param left  the label position info used when an axis is at the left
     *              ({@code null} not permitted).
     * @param right  the label position info used when an axis is at the right
     *               ({@code null} not permitted).
     */
    public CategoryLabelPositions(CategoryLabelPosition top, CategoryLabelPosition bottom, CategoryLabelPosition left, CategoryLabelPosition right) {
        Args.nullNotPermitted(top, "top");
        Args.nullNotPermitted(bottom, "bottom");
        Args.nullNotPermitted(left, "left");
        Args.nullNotPermitted(right, "right");
        this.positionForAxisAtTop = top;
        this.positionForAxisAtBottom = bottom;
        this.positionForAxisAtLeft = left;
        this.positionForAxisAtRight = right;
    }

    /**
     * Returns the category label position specification for an axis at the
     * given location.
     *
     * @param edge  the axis location.
     *
     * @return The category label position specification.
     */
    public CategoryLabelPosition getLabelPosition(RectangleEdge edge) {
        CategoryLabelPosition result = null;
        if (edge == RectangleEdge.TOP) {
            result = this.positionForAxisAtTop;
        } else if (edge == RectangleEdge.BOTTOM) {
            result = this.positionForAxisAtBottom;
        } else if (edge == RectangleEdge.LEFT) {
            result = this.positionForAxisAtLeft;
        } else if (edge == RectangleEdge.RIGHT) {
            result = this.positionForAxisAtRight;
        }
        return result;
    }

    /**
     * Returns a new instance based on an existing instance but with the top
     * position changed.
     *
     * @param base  the base ({@code null} not permitted).
     * @param top  the top position ({@code null} not permitted).
     *
     * @return A new instance (never {@code null}).
     */
    public static CategoryLabelPositions replaceTopPosition(CategoryLabelPositions base, CategoryLabelPosition top) {
        Args.nullNotPermitted(base, "base");
        Args.nullNotPermitted(top, "top");
        return new CategoryLabelPositions(top, base.getLabelPosition(RectangleEdge.BOTTOM), base.getLabelPosition(RectangleEdge.LEFT), base.getLabelPosition(RectangleEdge.RIGHT));
    }

    /**
     * Returns a new instance based on an existing instance but with the bottom
     * position changed.
     *
     * @param base  the base ({@code null} not permitted).
     * @param bottom  the bottom position ({@code null} not permitted).
     *
     * @return A new instance (never {@code null}).
     */
    public static CategoryLabelPositions replaceBottomPosition(CategoryLabelPositions base, CategoryLabelPosition bottom) {
        Args.nullNotPermitted(base, "base");
        Args.nullNotPermitted(bottom, "bottom");
        return new CategoryLabelPositions(base.getLabelPosition(RectangleEdge.TOP), bottom, base.getLabelPosition(RectangleEdge.LEFT), base.getLabelPosition(RectangleEdge.RIGHT));
    }

    /**
     * Returns a new instance based on an existing instance but with the left
     * position changed.
     *
     * @param base  the base ({@code null} not permitted).
     * @param left  the left position ({@code null} not permitted).
     *
     * @return A new instance (never {@code null}).
     */
    public static CategoryLabelPositions replaceLeftPosition(CategoryLabelPositions base, CategoryLabelPosition left) {
        Args.nullNotPermitted(base, "base");
        Args.nullNotPermitted(left, "left");
        return new CategoryLabelPositions(base.getLabelPosition(RectangleEdge.TOP), base.getLabelPosition(RectangleEdge.BOTTOM), left, base.getLabelPosition(RectangleEdge.RIGHT));
    }

    /**
     * Returns a new instance based on an existing instance but with the right
     * position changed.
     *
     * @param base  the base ({@code null} not permitted).
     * @param right  the right position ({@code null} not permitted).
     *
     * @return A new instance (never {@code null}).
     */
    public static CategoryLabelPositions replaceRightPosition(CategoryLabelPositions base, CategoryLabelPosition right) {
        Args.nullNotPermitted(base, "base");
        Args.nullNotPermitted(right, "right");
        return new CategoryLabelPositions(base.getLabelPosition(RectangleEdge.TOP), base.getLabelPosition(RectangleEdge.BOTTOM), base.getLabelPosition(RectangleEdge.LEFT), right);
    }

    /**
     * Returns {@code true} if this object is equal to the specified
     * object, and {@code false} otherwise.
     *
     * @param obj  the other object.
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CategoryLabelPositions)) {
            return false;
        }
        CategoryLabelPositions that = (CategoryLabelPositions) obj;
        if (!this.positionForAxisAtTop.equals(that.positionForAxisAtTop)) {
            return false;
        }
        if (!this.positionForAxisAtBottom.equals(that.positionForAxisAtBottom)) {
            return false;
        }
        if (!this.positionForAxisAtLeft.equals(that.positionForAxisAtLeft)) {
            return false;
        }
        if (!this.positionForAxisAtRight.equals(that.positionForAxisAtRight)) {
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
        int result = 19;
        result = 37 * result + this.positionForAxisAtTop.hashCode();
        result = 37 * result + this.positionForAxisAtBottom.hashCode();
        result = 37 * result + this.positionForAxisAtLeft.hashCode();
        result = 37 * result + this.positionForAxisAtRight.hashCode();
        return result;
    }
}
