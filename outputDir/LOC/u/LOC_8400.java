package LOC.u;
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
 * Some utility methods for working with text in Java2D.
 */
public class TextUtils {

    /**
     * When this flag is set to {@code true}, strings will be drawn
     * as attributed strings with the attributes taken from the current font.
     * This allows for underlining, strike-out etc., but it means that
     * TextLayout will be used to render the text:
     * <p>
     * <a href="https://www.jfree.org/phpBB2/viewtopic.php?p=45459&highlight=#45459">https://www.jfree.org/phpBB2/viewtopic.php?p=45459&highlight=#45459"</a>
     */
    private static boolean drawStringsWithFontAttributes = false;

    /**
     * A flag that controls whether the rotated string workaround is
     * used.
     */
    private static boolean useDrawRotatedStringWorkaround = false;

    /**
     * A flag that controls whether the FontMetrics.getStringBounds() method
     * is used or a workaround is applied.
     */
    private static boolean useFontMetricsGetStringBounds = false;

    /**
     * Private constructor prevents object creation.
     */
    private TextUtils() {
        // prevent instantiation
    }

    /**
     * Creates a {@link TextBlock} from a {@code String}.  Line breaks
     * are added where the {@code String} contains '\n' characters.
     *
     * @param text  the text ({@code null} not permitted).
     * @param font  the font.
     * @param paint  the paint.
     *
     * @return A text block.
     */
    public static TextBlock createTextBlock(String text, Font font, Paint paint) {
        Args.nullNotPermitted(text, "text");
        TextBlock result = new TextBlock();
        String input = text;
        boolean moreInputToProcess = !text.isEmpty();
        int start = 0;
        while (moreInputToProcess) {
            int index = input.indexOf("\n");
            if (index > start) {
                String line = input.substring(start, index);
                if (index < input.length() - 1) {
                    result.addLine(line, font, paint);
                    input = input.substring(index + 1);
                } else {
                    moreInputToProcess = false;
                }
            } else if (index == start) {
                if (index < input.length() - 1) {
                    input = input.substring(index + 1);
                } else {
                    moreInputToProcess = false;
                }
            } else {
                result.addLine(input, font, paint);
                moreInputToProcess = false;
            }
        }
        return result;
    }

    /**
     * Creates a new text block from the given string, breaking the
     * text into lines so that the {@code maxWidth} value is respected.
     *
     * @param text  the text.
     * @param font  the font.
     * @param paint  the paint.
     * @param maxWidth  the maximum width for each line.
     * @param measurer  the text measurer.
     *
     * @return A text block.
     */
    public static TextBlock createTextBlock(String text, Font font, Paint paint, float maxWidth, TextMeasurer measurer) {
        return createTextBlock(text, font, paint, maxWidth, Integer.MAX_VALUE, measurer);
    }

    /**
     * Creates a new text block from the given string, breaking the
     * text into lines so that the {@code maxWidth} value is
     * respected.
     *
     * @param text  the text.
     * @param font  the font.
     * @param paint  the paint.
     * @param maxWidth  the maximum width for each line.
     * @param maxLines  the maximum number of lines.
     * @param measurer  the text measurer.
     *
     * @return A text block.
     */
    public static TextBlock createTextBlock(String text, Font font, Paint paint, float maxWidth, int maxLines, TextMeasurer measurer) {
        TextBlock result = new TextBlock();
        BreakIterator iterator = BreakIterator.getLineInstance();
        iterator.setText(text);
        int current = 0;
        int lines = 0;
        int length = text.length();
        while (current < length && lines < maxLines) {
            int next = nextLineBreak(text, current, maxWidth, iterator, measurer);
            if (next == BreakIterator.DONE) {
                result.addLine(text.substring(current), font, paint);
                return result;
            } else if (next == current) {
                // we must take one more character or we'll loop forever
                next++;
            }
            result.addLine(text.substring(current, next), font, paint);
            lines++;
            current = next;
            while (current < text.length() && text.charAt(current) == '\n') {
                current++;
            }
        }
        if (current < length) {
            TextLine lastLine = result.getLastLine();
            TextFragment lastFragment = lastLine.getLastTextFragment();
            String oldStr = lastFragment.getText();
            String newStr = "...";
            if (oldStr.length() > 3) {
                newStr = oldStr.substring(0, oldStr.length() - 3) + "...";
            }
            lastLine.removeFragment(lastFragment);
            TextFragment newFragment = new TextFragment(newStr, lastFragment.getFont(), lastFragment.getPaint());
            lastLine.addFragment(newFragment);
        }
        return result;
    }

    /**
     * Returns the character index of the next line break.  If the next
     * character is wider than {@code width]} this method will return
     * {@code start} - the caller should check for this case.
     *
     * @param text  the text ({@code null} not permitted).
     * @param start  the start index.
     * @param width  the target display width.
     * @param iterator  the word break iterator.
     * @param measurer  the text measurer.
     *
     * @return The index of the next line break.
     */
    private static int nextLineBreak(String text, int start, float width, BreakIterator iterator, TextMeasurer measurer) {
        // this method is (loosely) based on code in JFreeReport's
        // TextParagraph class
        int current = start;
        int end;
        float x = 0.0f;
        boolean firstWord = true;
        int newline = text.indexOf('\n', start);
        if (newline < 0) {
            newline = Integer.MAX_VALUE;
        }
        while (((end = iterator.following(current)) != BreakIterator.DONE)) {
            x += measurer.getStringWidth(text, current, end);
            if (x > width) {
                if (firstWord) {
                    while (measurer.getStringWidth(text, start, end) > width) {
                        end--;
                        if (end <= start) {
                            return end;
                        }
                    }
                    return end;
                } else {
                    end = iterator.previous();
                    return end;
                }
            } else {
                if (end > newline) {
                    return newline;
                }
            }
            // we found at least one word that fits ...
            firstWord = false;
            current = end;
        }
        return BreakIterator.DONE;
    }

    /**
     * Returns the bounds for the specified text.
     *
     * @param text  the text ({@code null} permitted).
     * @param g2  the graphics context (not {@code null}).
     * @param fm  the font metrics (not {@code null}).
     *
     * @return The text bounds ({@code null} if the {@code text}
     *         argument is {@code null}).
     */
    public static Rectangle2D getTextBounds(String text, Graphics2D g2, FontMetrics fm) {
        Rectangle2D bounds;
        if (TextUtils.useFontMetricsGetStringBounds) {
            bounds = fm.getStringBounds(text, g2);
            // getStringBounds() can return incorrect height for some Unicode
            // characters...see bug parade 6183356, let's replace it with
            // something correct
            LineMetrics lm = fm.getFont().getLineMetrics(text, g2.getFontRenderContext());
            bounds.setRect(bounds.getX(), bounds.getY(), bounds.getWidth(), lm.getHeight());
        } else {
            double width = fm.stringWidth(text);
            double height = fm.getHeight();
            bounds = new Rectangle2D.Double(0.0, -fm.getAscent(), width, height);
        }
        return bounds;
    }

    /**
     * Returns the bounds of an aligned string.
     *
     * @param text  the string ({@code null} not permitted).
     * @param g2  the graphics target ({@code null} not permitted).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     * @param anchor  the anchor point that will be aligned to
     *     {@code (x, y)} ({@code null} not permitted).
     *
     * @return The text bounds (never {@code null}).
     */
    public static Rectangle2D calcAlignedStringBounds(String text, Graphics2D g2, float x, float y, TextAnchor anchor) {
        Rectangle2D textBounds = new Rectangle2D.Double();
        float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        // adjust text bounds to match string position
        textBounds.setRect(x + adjust[0], y + adjust[1] + adjust[2], textBounds.getWidth(), textBounds.getHeight());
        return textBounds;
    }

    /**
     * Draws a string such that the specified anchor point is aligned to the
     * given (x, y) location.
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x coordinate (Java 2D).
     * @param y  the y coordinate (Java 2D).
     * @param anchor  the anchor location.
     *
     * @return The text bounds (adjusted for the text position).
     */
    public static Rectangle2D drawAlignedString(String text, Graphics2D g2, float x, float y, TextAnchor anchor) {
        Rectangle2D textBounds = new Rectangle2D.Double();
        float[] adjust = deriveTextBoundsAnchorOffsets(g2, text, anchor, textBounds);
        // adjust text bounds to match string position
        textBounds.setRect(x + adjust[0], y + adjust[1] + adjust[2], textBounds.getWidth(), textBounds.getHeight());
        if (!drawStringsWithFontAttributes) {
            g2.drawString(text, x + adjust[0], y + adjust[1]);
        } else {
            AttributedString as = new AttributedString(text, g2.getFont().getAttributes());
            g2.drawString(as.getIterator(), x + adjust[0], y + adjust[1]);
        }
        return textBounds;
    }

    /**
     * A utility method that calculates the anchor offsets for a string.
     * Normally, the (x, y) coordinate for drawing text is a point on the
     * baseline at the left of the text string.  If you add these offsets to
     * (x, y) and draw the string, then the anchor point should coincide with
     * the (x, y) point.
     *
     * @param g2  the graphics device (not {@code null}).
     * @param text  the text.
     * @param anchor  the anchor point.
     * @param textBounds  the text bounds (if not {@code null}, this
     *                    object will be updated by this method to match the
     *                    string bounds).
     *
     * @return  The offsets.
     */
    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor, Rectangle2D textBounds) {
        float[] result = new float[3];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);
        Rectangle2D bounds = TextUtils.getTextBounds(text, g2, fm);
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        result[2] = -ascent;
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor.isHorizontalCenter()) {
            xAdj = (float) -bounds.getWidth() / 2.0f;
        } else if (anchor.isRight()) {
            xAdj = (float) -bounds.getWidth();
        }
        if (anchor.isTop()) {
            yAdj = -descent - leading + (float) bounds.getHeight();
        } else if (anchor.isHalfAscent()) {
            yAdj = halfAscent;
        } else if (anchor.isVerticalCenter()) {
            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);
        } else if (anchor.isBaseline()) {
            yAdj = 0.0f;
        } else if (anchor.isBottom()) {
            yAdj = -metrics.getDescent() - metrics.getLeading();
        }
        if (textBounds != null) {
            textBounds.setRect(bounds);
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    /**
     * A utility method for drawing rotated text.
     * <P>
     * A common rotation is -Math.PI/2 which draws text 'vertically' (with the
     * top of the characters on the left).
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param angle  the angle of the (clockwise) rotation (in radians).
     * @param x  the x-coordinate.
     * @param y  the y-coordinate.
     */
    public static void drawRotatedString(String text, Graphics2D g2, double angle, float x, float y) {
        drawRotatedString(text, g2, x, y, angle, x, y);
    }

    /**
     * A utility method for drawing rotated text.
     * <P>
     * A common rotation is -Math.PI/2 which draws text 'vertically' (with the
     * top of the characters on the left).
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param textX  the x-coordinate for the text (before rotation).
     * @param textY  the y-coordinate for the text (before rotation).
     * @param angle  the angle of the (clockwise) rotation (in radians).
     * @param rotateX  the point about which the text is rotated.
     * @param rotateY  the point about which the text is rotated.
     */
    public static void drawRotatedString(String text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        if (text == null || text.isEmpty()) {
            return;
        }
        if (angle == 0.0) {
            drawAlignedString(text, g2, textX, textY, TextAnchor.BASELINE_LEFT);
            return;
        }
        AffineTransform saved = g2.getTransform();
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        g2.transform(rotate);
        if (useDrawRotatedStringWorkaround) {
            // workaround for JDC bug ID 4312117 and others...
            TextLayout tl = new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
            tl.draw(g2, textX, textY);
        } else {
            if (!drawStringsWithFontAttributes) {
                g2.drawString(text, textX, textY);
            } else {
                AttributedString as = new AttributedString(text, g2.getFont().getAttributes());
                g2.drawString(as.getIterator(), textX, textY);
            }
        }
        g2.setTransform(saved);
    }

    /**
     * Draws a string that is aligned by one anchor point and rotated about
     * another anchor point.
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x-coordinate for positioning the text.
     * @param y  the y-coordinate for positioning the text.
     * @param textAnchor  the text anchor.
     * @param angle  the rotation angle.
     * @param rotationX  the x-coordinate for the rotation anchor point.
     * @param rotationY  the y-coordinate for the rotation anchor point.
     */
    public static void drawRotatedString(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, float rotationX, float rotationY) {
        if (text == null || text.isEmpty()) {
            return;
        }
        if (angle == 0.0) {
            drawAlignedString(text, g2, x, y, textAnchor);
        } else {
            float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
            drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, rotationX, rotationY);
        }
    }

    /**
     * Draws a string that is aligned by one anchor point and rotated about
     * another anchor point.
     *
     * @param text  the text.
     * @param g2  the graphics device.
     * @param x  the x-coordinate for positioning the text.
     * @param y  the y-coordinate for positioning the text.
     * @param textAnchor  the text anchor.
     * @param angle  the rotation angle (in radians).
     * @param rotationAnchor  the rotation anchor.
     */
    public static void drawRotatedString(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        if (text == null || text.isEmpty()) {
            return;
        }
        if (angle == 0.0) {
            drawAlignedString(text, g2, x, y, textAnchor);
        } else {
            float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
            float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
            drawRotatedString(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
        }
    }

    /**
     * Returns a shape that represents the bounds of the string after the
     * specified rotation has been applied.
     *
     * @param text  the text ({@code null} permitted).
     * @param g2  the graphics device.
     * @param x  the x coordinate for the anchor point.
     * @param y  the y coordinate for the anchor point.
     * @param textAnchor  the text anchor.
     * @param angle  the angle.
     * @param rotationAnchor  the rotation anchor.
     *
     * @return The bounds (possibly {@code null}).
     */
    public static Shape calculateRotatedStringBounds(String text, Graphics2D g2, float x, float y, TextAnchor textAnchor, double angle, TextAnchor rotationAnchor) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        float[] textAdj = deriveTextBoundsAnchorOffsets(g2, text, textAnchor);
        float[] rotateAdj = deriveRotationAnchorOffsets(g2, text, rotationAnchor);
        return calculateRotatedStringBounds(text, g2, x + textAdj[0], y + textAdj[1], angle, x + textAdj[0] + rotateAdj[0], y + textAdj[1] + rotateAdj[1]);
    }

    /**
     * A utility method that calculates the anchor offsets for a string.
     * Normally, the (x, y) coordinate for drawing text is a point on the
     * baseline at the left of the text string.  If you add these offsets to
     * (x, y) and draw the string, then the anchor point should coincide with
     * the (x, y) point.
     *
     * @param g2  the graphics device (not {@code null}).
     * @param text  the text.
     * @param anchor  the anchor point.
     *
     * @return  The offsets.
     */
    private static float[] deriveTextBoundsAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);
        Rectangle2D bounds = getTextBounds(text, g2, fm);
        LineMetrics metrics = f.getLineMetrics(text, frc);
        float ascent = metrics.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor.isHorizontalCenter()) {
            xAdj = (float) -bounds.getWidth() / 2.0f;
        } else if (anchor.isRight()) {
            xAdj = (float) -bounds.getWidth();
        }
        if (anchor.isTop()) {
            yAdj = -descent - leading + (float) bounds.getHeight();
        } else if (anchor.isHalfAscent()) {
            yAdj = halfAscent;
        } else if (anchor.isVerticalCenter()) {
            yAdj = -descent - leading + (float) (bounds.getHeight() / 2.0);
        } else if (anchor.isBaseline()) {
            yAdj = 0.0f;
        } else if (anchor.isBottom()) {
            yAdj = -metrics.getDescent() - metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    /**
     * A utility method that calculates the rotation anchor offsets for a
     * string.  These offsets are relative to the text starting coordinate
     * ({@code BASELINE_LEFT}).
     *
     * @param g2  the graphics device.
     * @param text  the text.
     * @param anchor  the anchor point.
     *
     * @return The offsets.
     */
    private static float[] deriveRotationAnchorOffsets(Graphics2D g2, String text, TextAnchor anchor) {
        float[] result = new float[2];
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics metrics = g2.getFont().getLineMetrics(text, frc);
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = TextUtils.getTextBounds(text, g2, fm);
        float ascent = metrics.getAscent();
        float halfAscent = ascent / 2.0f;
        float descent = metrics.getDescent();
        float leading = metrics.getLeading();
        float xAdj = 0.0f;
        float yAdj = 0.0f;
        if (anchor.isLeft()) {
            xAdj = 0.0f;
        } else if (anchor.isHorizontalCenter()) {
            xAdj = (float) bounds.getWidth() / 2.0f;
        } else if (anchor.isRight()) {
            xAdj = (float) bounds.getWidth();
        }
        if (anchor.isTop()) {
            yAdj = descent + leading - (float) bounds.getHeight();
        } else if (anchor.isVerticalCenter()) {
            yAdj = descent + leading - (float) (bounds.getHeight() / 2.0);
        } else if (anchor.isHalfAscent()) {
            yAdj = -halfAscent;
        } else if (anchor.isBaseline()) {
            yAdj = 0.0f;
        } else if (anchor.isBottom()) {
            yAdj = metrics.getDescent() + metrics.getLeading();
        }
        result[0] = xAdj;
        result[1] = yAdj;
        return result;
    }

    /**
     * Returns a shape that represents the bounds of the string after the
     * specified rotation has been applied.
     *
     * @param text  the text ({@code null} permitted).
     * @param g2  the graphics device.
     * @param textX  the x coordinate for the text.
     * @param textY  the y coordinate for the text.
     * @param angle  the angle.
     * @param rotateX  the x coordinate for the rotation point.
     * @param rotateY  the y coordinate for the rotation point.
     *
     * @return The bounds ({@code null} if {@code text} is
     *         {@code null} or has zero length).
     */
    public static Shape calculateRotatedStringBounds(String text, Graphics2D g2, float textX, float textY, double angle, float rotateX, float rotateY) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D bounds = TextUtils.getTextBounds(text, g2, fm);
        AffineTransform translate = AffineTransform.getTranslateInstance(textX, textY);
        Shape translatedBounds = translate.createTransformedShape(bounds);
        AffineTransform rotate = AffineTransform.getRotateInstance(angle, rotateX, rotateY);
        return rotate.createTransformedShape(translatedBounds);
    }

    /**
     * Returns the flag that controls whether the FontMetrics.getStringBounds()
     * method is used or not.  If you are having trouble with label alignment
     * or positioning, try changing the value of this flag.
     *
     * @return A boolean.
     */
    public static boolean getUseFontMetricsGetStringBounds() {
        return useFontMetricsGetStringBounds;
    }

    /**
     * Sets the flag that controls whether the FontMetrics.getStringBounds()
     * method is used or not.  If you are having trouble with label alignment
     * or positioning, try changing the value of this flag.
     *
     * @param use  the flag.
     */
    public static void setUseFontMetricsGetStringBounds(boolean use) {
        useFontMetricsGetStringBounds = use;
    }

    /**
     * Returns the flag that controls whether a workaround is used for
     * drawing rotated strings.
     *
     * @return A boolean.
     */
    public static boolean isUseDrawRotatedStringWorkaround() {
        return useDrawRotatedStringWorkaround;
    }

    /**
     * Sets the flag that controls whether a workaround is used for
     * drawing rotated strings.  The related bug is on Sun's bug parade
     * (id 4312117) and the workaround involves using a {@code TextLayout}
     * instance to draw the text instead of calling the
     * {@code drawString()} method in the {@code Graphics2D} class.
     *
     * @param use  the new flag value.
     */
    public static void setUseDrawRotatedStringWorkaround(boolean use) {
        TextUtils.useDrawRotatedStringWorkaround = use;
    }

    /**
     * Returns the flag that controls whether strings are drawn using
     * the current font attributes (such as underlining, strikethrough etc.).
     * The default value is {@code false}.
     *
     * @return A boolean.
     */
    public static boolean getDrawStringsWithFontAttributes() {
        return TextUtils.drawStringsWithFontAttributes;
    }

    /**
     * Sets the flag that controls whether strings are drawn using the
     * current font attributes.  This is a hack to allow underlining of titles
     * without big changes to the API.  See
     * <a href="https://www.jfree.org/phpBB2/viewtopic.php?p=45459&amp;highlight=#45459">this forum post</a>.
     *
     * @param b  the new flag value.
     */
    public static void setDrawStringsWithFontAttributes(boolean b) {
        TextUtils.drawStringsWithFontAttributes = b;
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
 * --------------
 * PolarPlot.java
 * --------------
 * (C) Copyright 2004-present, by Solution Engineering, Inc. and Contributors.
 *
 * Original Author:  Daniel Bridenbecker, Solution Engineering, Inc.;
 * Contributor(s):   David Gilbert;
 *                   Martin Hoeller (patches 1871902 and 2850344);
 * 
 */
/**
 * Plots data that is in (theta, radius) pairs where theta equal to zero is
 * due north and increases clockwise.
 */
public class PolarPlot extends Plot implements ValueAxisPlot, Zoomable, RendererChangeListener, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 3794383185924179525L;

    /**
     * The default margin.
     */
    private static final int DEFAULT_MARGIN = 20;

    /**
     * The annotation margin.
     */
    private static final double ANNOTATION_MARGIN = 7.0;

    /**
     * The default angle tick unit size.
     */
    public static final double DEFAULT_ANGLE_TICK_UNIT_SIZE = 45.0;

    /**
     * The default angle offset.
     */
    public static final double DEFAULT_ANGLE_OFFSET = -90.0;

    /**
     * The default grid line stroke.
     */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);

    /**
     * The default grid line paint.
     */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.GRAY;

    /**
     * The resourceBundle for the localization.
     */
    protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");

    /**
     * The angles that are marked with gridlines.
     */
    private List<ValueTick> angleTicks;

    /**
     * The range axis (used for the y-values).
     */
    private Map<Integer, ValueAxis> axes;

    /**
     * The axis locations.
     */
    private final Map<Integer, PolarAxisLocation> axisLocations;

    /**
     * Storage for the datasets.
     */
    private Map<Integer, XYDataset> datasets;

    /**
     * Storage for the renderers.
     */
    private Map<Integer, PolarItemRenderer> renderers;

    /**
     * The tick unit that controls the spacing between the angular grid lines.
     */
    private TickUnit angleTickUnit;

    /**
     * An offset for the angles, to start with 0 degrees at north, east, south
     * or west.
     */
    private double angleOffset;

    /**
     * A flag indicating if the angles increase counterclockwise or clockwise.
     */
    private boolean counterClockwise;

    /**
     * A flag that controls whether the angle labels are visible.
     */
    private boolean angleLabelsVisible = true;

    /**
     * The font used to display the angle labels - never null.
     */
    private Font angleLabelFont = new Font("SansSerif", Font.PLAIN, 12);

    /**
     * The paint used to display the angle labels.
     */
    private transient Paint angleLabelPaint = Color.BLACK;

    /**
     * A flag that controls whether the angular grid-lines are visible.
     */
    private boolean angleGridlinesVisible;

    /**
     * The stroke used to draw the angular grid-lines.
     */
    private transient Stroke angleGridlineStroke;

    /**
     * The paint used to draw the angular grid-lines.
     */
    private transient Paint angleGridlinePaint;

    /**
     * A flag that controls whether the radius grid-lines are visible.
     */
    private boolean radiusGridlinesVisible;

    /**
     * The stroke used to draw the radius grid-lines.
     */
    private transient Stroke radiusGridlineStroke;

    /**
     * The paint used to draw the radius grid-lines.
     */
    private transient Paint radiusGridlinePaint;

    /**
     * A flag that controls whether the radial minor grid-lines are visible.
     */
    private boolean radiusMinorGridlinesVisible;

    /**
     * The annotations for the plot.
     */
    private List<String> cornerTextItems = new ArrayList<>();

    /**
     * The actual margin in pixels.
     */
    private int margin;

    /**
     * An optional collection of legend items that can be returned by the
     * getLegendItems() method.
     */
    private LegendItemCollection fixedLegendItems;

    /**
     * Storage for the mapping between datasets/renderers and range axes.  The
     * keys in the map are Integer objects, corresponding to the dataset
     * index.  The values in the map are List&lt;Integer&gt; instances (corresponding
     * to the axis indices).  If the map contains no
     * entry for a dataset, it is assumed to map to the primary domain axis
     * (index = 0).
     */
    private final Map<Integer, List<Integer>> datasetToAxesMap;

    /**
     * Default constructor.
     */
    public PolarPlot() {
        this(null, null, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param radiusAxis  the radius axis ({@code null} permitted).
     * @param renderer  the renderer ({@code null} permitted).
     */
    public PolarPlot(XYDataset dataset, ValueAxis radiusAxis, PolarItemRenderer renderer) {
        super();
        this.datasets = new HashMap<>();
        this.datasets.put(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.angleTickUnit = new NumberTickUnit(DEFAULT_ANGLE_TICK_UNIT_SIZE);
        this.axes = new HashMap<>();
        this.datasetToAxesMap = new TreeMap<>();
        this.axes.put(0, radiusAxis);
        if (radiusAxis != null) {
            radiusAxis.setPlot(this);
            radiusAxis.addChangeListener(this);
        }
        // define the default locations for up to 8 axes...
        this.axisLocations = new HashMap<>();
        this.axisLocations.put(0, PolarAxisLocation.EAST_ABOVE);
        this.axisLocations.put(1, PolarAxisLocation.NORTH_LEFT);
        this.axisLocations.put(2, PolarAxisLocation.WEST_BELOW);
        this.axisLocations.put(3, PolarAxisLocation.SOUTH_RIGHT);
        this.axisLocations.put(4, PolarAxisLocation.EAST_BELOW);
        this.axisLocations.put(5, PolarAxisLocation.NORTH_RIGHT);
        this.axisLocations.put(6, PolarAxisLocation.WEST_ABOVE);
        this.axisLocations.put(7, PolarAxisLocation.SOUTH_LEFT);
        this.renderers = new HashMap<>();
        this.renderers.put(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.angleOffset = DEFAULT_ANGLE_OFFSET;
        this.counterClockwise = false;
        this.angleGridlinesVisible = true;
        this.angleGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.angleGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.radiusGridlinesVisible = true;
        this.radiusMinorGridlinesVisible = true;
        this.radiusGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.radiusGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.margin = DEFAULT_MARGIN;
    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    @Override
    public String getPlotType() {
        return PolarPlot.localizationResources.getString("Polar_Plot");
    }

    /**
     * Returns the primary axis for the plot.
     *
     * @return The primary axis (possibly {@code null}).
     *
     * @see #setAxis(ValueAxis)
     */
    public ValueAxis getAxis() {
        return getAxis(0);
    }

    /**
     * Returns an axis for the plot.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setAxis(int, ValueAxis)
     */
    public ValueAxis getAxis(int index) {
        return this.axes.get(index);
    }

    /**
     * Sets the primary axis for the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param axis  the new primary axis ({@code null} permitted).
     */
    public void setAxis(ValueAxis axis) {
        setAxis(0, axis);
    }

    /**
     * Sets an axis for the plot and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getAxis(int)
     */
    public void setAxis(int index, ValueAxis axis) {
        setAxis(index, axis, true);
    }

    /**
     * Sets an axis for the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getAxis(int)
     */
    public void setAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getAxis(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        if (axis != null) {
            axis.setPlot(this);
        }
        this.axes.put(index, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the location of the primary axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #setAxisLocation(PolarAxisLocation)
     */
    public PolarAxisLocation getAxisLocation() {
        return getAxisLocation(0);
    }

    /**
     * Returns the location for an axis.
     *
     * @param index  the axis index.
     *
     * @return The location (possibly {@code null}).
     *
     * @see #setAxisLocation(int, PolarAxisLocation)
     */
    public PolarAxisLocation getAxisLocation(int index) {
        return this.axisLocations.get(index);
    }

    /**
     * Sets the location of the primary axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getAxisLocation()
     */
    public void setAxisLocation(PolarAxisLocation location) {
        // delegate argument checks...
        setAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the primary axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getAxisLocation()
     */
    public void setAxisLocation(PolarAxisLocation location, boolean notify) {
        // delegate...
        setAxisLocation(0, location, notify);
    }

    /**
     * Sets the location for an axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getAxisLocation(int)
     */
    public void setAxisLocation(int index, PolarAxisLocation location) {
        // delegate...
        setAxisLocation(index, location, true);
    }

    /**
     * Sets the axis location for an axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void setAxisLocation(int index, PolarAxisLocation location, boolean notify) {
        Args.nullNotPermitted(location, "location");
        this.axisLocations.put(index, location);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the number of domain axes.
     *
     * @return The axis count.
     */
    public int getAxisCount() {
        return this.axes.size();
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly {@code null}).
     *
     * @see #setDataset(XYDataset)
     */
    public XYDataset getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset with the specified index, if any.
     *
     * @param index  the dataset index.
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(int, XYDataset)
     */
    public XYDataset getDataset(int index) {
        return this.datasets.get(index);
    }

    /**
     * Sets the primary dataset for the plot, replacing the existing dataset
     * if there is one, and sends a {@code link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     */
    public void setDataset(XYDataset dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot, replacing the existing dataset at the same
     * index if there is one, and sends a {@code link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the dataset index.
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset(int)
     */
    public void setDataset(int index, XYDataset dataset) {
        XYDataset existing = getDataset(index);
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
    public int indexOf(XYDataset dataset) {
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            if (entry.getValue() == dataset) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the primary renderer.
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(PolarItemRenderer)
     */
    public PolarItemRenderer getRenderer() {
        return getRenderer(0);
    }

    /**
     * Returns the renderer at the specified index, if there is one.
     *
     * @param index  the renderer index.
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(int, PolarItemRenderer)
     */
    public PolarItemRenderer getRenderer(int index) {
        return this.renderers.get(index);
    }

    /**
     * Sets the primary renderer, and notifies all listeners of a change to the
     * plot.  If the renderer is set to {@code null}, no data items will
     * be drawn for the corresponding dataset.
     *
     * @param renderer  the new renderer ({@code null} permitted).
     *
     * @see #getRenderer()
     */
    public void setRenderer(PolarItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    /**
     * Sets a renderer and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param index  the index.
     * @param renderer  the renderer.
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, PolarItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    /**
     * Sets a renderer and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the index.
     * @param renderer  the renderer.
     * @param notify  notify listeners?
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, PolarItemRenderer renderer, boolean notify) {
        PolarItemRenderer existing = getRenderer(index);
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.renderers.put(index, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the tick unit that controls the spacing of the angular grid
     * lines.
     *
     * @return The tick unit (never {@code null}).
     */
    public TickUnit getAngleTickUnit() {
        return this.angleTickUnit;
    }

    /**
     * Sets the tick unit that controls the spacing of the angular grid
     * lines, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param unit  the tick unit ({@code null} not permitted).
     */
    public void setAngleTickUnit(TickUnit unit) {
        Args.nullNotPermitted(unit, "unit");
        this.angleTickUnit = unit;
        fireChangeEvent();
    }

    /**
     * Returns the offset that is used for all angles.
     *
     * @return The offset for the angles.
     */
    public double getAngleOffset() {
        return this.angleOffset;
    }

    /**
     * Sets the offset that is used for all angles and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * This is useful to let 0 degrees be at the north, east, south or west
     * side of the chart.
     *
     * @param offset The offset
     */
    public void setAngleOffset(double offset) {
        this.angleOffset = offset;
        fireChangeEvent();
    }

    /**
     * Get the direction for growing angle degrees.
     *
     * @return {@code true} if angle increases counterclockwise,
     *         {@code false} otherwise.
     */
    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    /**
     * Sets the flag for increasing angle degrees direction.
     *
     * {@code true} for counterclockwise, {@code false} for
     * clockwise.
     *
     * @param counterClockwise The flag.
     */
    public void setCounterClockwise(boolean counterClockwise) {
        this.counterClockwise = counterClockwise;
    }

    /**
     * Returns a flag that controls whether the angle labels are visible.
     *
     * @return A boolean.
     *
     * @see #setAngleLabelsVisible(boolean)
     */
    public boolean isAngleLabelsVisible() {
        return this.angleLabelsVisible;
    }

    /**
     * Sets the flag that controls whether the angle labels are visible,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #isAngleLabelsVisible()
     */
    public void setAngleLabelsVisible(boolean visible) {
        if (this.angleLabelsVisible != visible) {
            this.angleLabelsVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the font used to display the angle labels.
     *
     * @return A font (never {@code null}).
     *
     * @see #setAngleLabelFont(Font)
     */
    public Font getAngleLabelFont() {
        return this.angleLabelFont;
    }

    /**
     * Sets the font used to display the angle labels and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getAngleLabelFont()
     */
    public void setAngleLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.angleLabelFont = font;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to display the angle labels.
     *
     * @return A paint (never {@code null}).
     *
     * @see #setAngleLabelPaint(Paint)
     */
    public Paint getAngleLabelPaint() {
        return this.angleLabelPaint;
    }

    /**
     * Sets the paint used to display the angle labels and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setAngleLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.angleLabelPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the angular gridlines are visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setAngleGridlinesVisible(boolean)
     */
    public boolean isAngleGridlinesVisible() {
        return this.angleGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the angular grid-lines are
     * visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isAngleGridlinesVisible()
     */
    public void setAngleGridlinesVisible(boolean visible) {
        if (this.angleGridlinesVisible != visible) {
            this.angleGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid-lines (if any) plotted against the
     * angular axis.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setAngleGridlineStroke(Stroke)
     */
    public Stroke getAngleGridlineStroke() {
        return this.angleGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the angular axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getAngleGridlineStroke()
     */
    public void setAngleGridlineStroke(Stroke stroke) {
        this.angleGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the
     * angular axis.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setAngleGridlinePaint(Paint)
     */
    public Paint getAngleGridlinePaint() {
        return this.angleGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the angular axis.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getAngleGridlinePaint()
     */
    public void setAngleGridlinePaint(Paint paint) {
        this.angleGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the radius axis grid is visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setRadiusGridlinesVisible(boolean)
     */
    public boolean isRadiusGridlinesVisible() {
        return this.radiusGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the radius axis grid lines
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isRadiusGridlinesVisible()
     */
    public void setRadiusGridlinesVisible(boolean visible) {
        if (this.radiusGridlinesVisible != visible) {
            this.radiusGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid lines (if any) plotted against the
     * radius axis.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setRadiusGridlineStroke(Stroke)
     */
    public Stroke getRadiusGridlineStroke() {
        return this.radiusGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the radius axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getRadiusGridlineStroke()
     */
    public void setRadiusGridlineStroke(Stroke stroke) {
        this.radiusGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the radius
     * axis.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setRadiusGridlinePaint(Paint)
     */
    public Paint getRadiusGridlinePaint() {
        return this.radiusGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the radius axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <p>
     * If you set this to {@code null}, no grid lines will be drawn.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getRadiusGridlinePaint()
     */
    public void setRadiusGridlinePaint(Paint paint) {
        this.radiusGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Return the current value of the flag indicating if radial minor
     * grid-lines will be drawn or not.
     *
     * @return Returns {@code true} if radial minor grid-lines are drawn.
     */
    public boolean isRadiusMinorGridlinesVisible() {
        return this.radiusMinorGridlinesVisible;
    }

    /**
     * Set the flag that determines if radial minor grid-lines will be drawn,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param flag {@code true} to draw the radial minor grid-lines,
     *             {@code false} to hide them.
     */
    public void setRadiusMinorGridlinesVisible(boolean flag) {
        this.radiusMinorGridlinesVisible = flag;
        fireChangeEvent();
    }

    /**
     * Returns the margin around the plot area.
     *
     * @return The actual margin in pixels.
     */
    public int getMargin() {
        return this.margin;
    }

    /**
     * Set the margin around the plot area and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param margin The new margin in pixels.
     */
    public void setMargin(int margin) {
        this.margin = margin;
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
     * Add text to be displayed in the lower right hand corner and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param text  the text to display ({@code null} not permitted).
     *
     * @see #removeCornerTextItem(String)
     */
    public void addCornerTextItem(String text) {
        Args.nullNotPermitted(text, "text");
        this.cornerTextItems.add(text);
        fireChangeEvent();
    }

    /**
     * Remove the given text from the list of corner text items and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param text  the text to remove ({@code null} ignored).
     *
     * @see #addCornerTextItem(String)
     */
    public void removeCornerTextItem(String text) {
        boolean removed = this.cornerTextItems.remove(text);
        if (removed) {
            fireChangeEvent();
        }
    }

    /**
     * Clear the list of corner text items and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @see #addCornerTextItem(String)
     * @see #removeCornerTextItem(String)
     */
    public void clearCornerTextItems() {
        if (!this.cornerTextItems.isEmpty()) {
            this.cornerTextItems.clear();
            fireChangeEvent();
        }
    }

    /**
     * Generates a list of tick values for the angular tick marks.
     *
     * @return A list of {@link NumberTick} instances.
     */
    protected List<ValueTick> refreshAngleTicks() {
        List<ValueTick> ticks = new ArrayList<>();
        for (double currentTickVal = 0.0; currentTickVal < 360.0; currentTickVal += this.angleTickUnit.getSize()) {
            TextAnchor ta = calculateTextAnchor(currentTickVal);
            NumberTick tick = new NumberTick(currentTickVal, this.angleTickUnit.valueToString(currentTickVal), ta, TextAnchor.CENTER, 0.0);
            ticks.add(tick);
        }
        return ticks;
    }

    /**
     * Calculate the text position for the given degrees.
     *
     * @param angleDegrees  the angle in degrees.
     *
     * @return The optimal text anchor.
     */
    protected TextAnchor calculateTextAnchor(double angleDegrees) {
        TextAnchor ta = TextAnchor.CENTER;
        // normalize angle
        double offset = this.angleOffset;
        while (offset < 0.0) {
            offset += 360.0;
        }
        double normalizedAngle = (((this.counterClockwise ? -1 : 1) * angleDegrees) + offset) % 360;
        while (this.counterClockwise && (normalizedAngle < 0.0)) {
            normalizedAngle += 360.0;
        }
        if (normalizedAngle == 0.0) {
            ta = TextAnchor.CENTER_LEFT;
        } else if (normalizedAngle > 0.0 && normalizedAngle < 90.0) {
            ta = TextAnchor.TOP_LEFT;
        } else if (normalizedAngle == 90.0) {
            ta = TextAnchor.TOP_CENTER;
        } else if (normalizedAngle > 90.0 && normalizedAngle < 180.0) {
            ta = TextAnchor.TOP_RIGHT;
        } else if (normalizedAngle == 180) {
            ta = TextAnchor.CENTER_RIGHT;
        } else if (normalizedAngle > 180.0 && normalizedAngle < 270.0) {
            ta = TextAnchor.BOTTOM_RIGHT;
        } else if (normalizedAngle == 270) {
            ta = TextAnchor.BOTTOM_CENTER;
        } else if (normalizedAngle > 270.0 && normalizedAngle < 360.0) {
            ta = TextAnchor.BOTTOM_LEFT;
        }
        return ta;
    }

    /**
     * Maps a dataset to a particular axis.  All data will be plotted
     * against axis zero by default, no mapping is required for this case.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index.
     */
    public void mapDatasetToAxis(int index, int axisIndex) {
        List<Integer> axisIndices = new ArrayList<>(1);
        axisIndices.add(axisIndex);
        mapDatasetToAxes(index, axisIndices);
    }

    /**
     * Maps the specified dataset to the axes in the list.  Note that the
     * conversion of data values into Java2D space is always performed using
     * the first axis in the list.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndices  the axis indices ({@code null} permitted).
     */
    public void mapDatasetToAxes(int index, List<Integer> axisIndices) {
        if (index < 0) {
            throw new IllegalArgumentException("Requires 'index' >= 0.");
        }
        checkAxisIndices(axisIndices);
        Integer key = index;
        this.datasetToAxesMap.put(key, new ArrayList<>(axisIndices));
        // fake a dataset change event to update axes...
        datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
    }

    /**
     * This method is used to perform argument checking on the list of
     * axis indices passed to mapDatasetToAxes().
     *
     * @param indices  the list of indices ({@code null} permitted).
     */
    private void checkAxisIndices(List<Integer> indices) {
        // axisIndices can be:
        // 1.  null;
        // 2.  non-empty, containing only Integer objects that are unique.
        if (indices == null) {
            // OK
            return;
        }
        if (indices.isEmpty()) {
            throw new IllegalArgumentException("Empty list not permitted.");
        }
        Set<Integer> set = new HashSet<>();
        for (Integer i : indices) {
            if (set.contains(i)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            set.add(i);
        }
    }

    /**
     * Returns the axis for a dataset.
     *
     * @param index  the dataset index.
     *
     * @return The axis.
     */
    public ValueAxis getAxisForDataset(int index) {
        ValueAxis valueAxis;
        List<Integer> axisIndices = this.datasetToAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            valueAxis = getAxis(axisIndex);
        } else {
            valueAxis = getAxis(0);
        }
        return valueAxis;
    }

    /**
     * Returns the index of the given axis.
     *
     * @param axis  the axis.
     *
     * @return The axis index or -1 if axis is not used in this plot.
     */
    public int getAxisIndex(ValueAxis axis) {
        for (Entry<Integer, ValueAxis> entry : this.axes.entrySet()) {
            if (axis.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        // try the parent plot
        Plot parent = getParent();
        if (parent instanceof PolarPlot) {
            PolarPlot p = (PolarPlot) parent;
            return p.getAxisIndex(axis);
        }
        return -1;
    }

    /**
     * Returns the index of the specified renderer, or {@code -1} if the
     * renderer is not assigned to this plot.
     *
     * @param renderer  the renderer ({@code null} not permitted).
     *
     * @return The renderer index.
     */
    public int getIndexOf(PolarItemRenderer renderer) {
        Args.nullNotPermitted(renderer, "renderer");
        for (Entry<Integer, PolarItemRenderer> entry : this.renderers.entrySet()) {
            if (renderer.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        // FIXME: handle axes and renderers
        visitor.visit(this);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This plot relies on a {@link PolarItemRenderer} to draw each
     * item in the plot.  This allows the visual representation of the data to
     * be changed easily.
     * <P>
     * The optional info argument collects information about the rendering of
     * the plot (dimensions, tooltip information etc).  Just pass in
     * {@code null} if you do not need this information.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot (including axes and
     *              labels) should be drawn.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  ignored.
     * @param info  collects chart drawing information ({@code null}
     *              permitted).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }
        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        Rectangle2D dataArea = area;
        if (info != null) {
            info.setDataArea(dataArea);
        }
        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        int axisCount = this.axes.size();
        AxisState state = null;
        for (int i = 0; i < axisCount; i++) {
            ValueAxis axis = getAxis(i);
            if (axis != null) {
                PolarAxisLocation location = this.axisLocations.get(i);
                AxisState s = drawAxis(axis, location, g2, dataArea);
                if (i == 0) {
                    state = s;
                }
            }
        }
        // now for each dataset, get the renderer and the appropriate axis
        // and render the dataset...
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        this.angleTicks = refreshAngleTicks();
        drawGridlines(g2, dataArea, this.angleTicks, state.getTicks());
        render(g2, dataArea, info);
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);
        drawCornerTextItems(g2, dataArea);
    }

    /**
     * Draws the corner text items.
     *
     * @param g2  the drawing surface.
     * @param area  the area.
     */
    protected void drawCornerTextItems(Graphics2D g2, Rectangle2D area) {
        if (this.cornerTextItems.isEmpty()) {
            return;
        }
        g2.setColor(Color.BLACK);
        double width = 0.0;
        double height = 0.0;
        for (String msg : this.cornerTextItems) {
            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D bounds = TextUtils.getTextBounds(msg, g2, fm);
            width = Math.max(width, bounds.getWidth());
            height += bounds.getHeight();
        }
        double xadj = ANNOTATION_MARGIN * 2.0;
        double yadj = ANNOTATION_MARGIN;
        width += xadj;
        height += yadj;
        double x = area.getMaxX() - width;
        double y = area.getMaxY() - height;
        g2.drawRect((int) x, (int) y, (int) width, (int) height);
        x += ANNOTATION_MARGIN;
        for (String msg : this.cornerTextItems) {
            Rectangle2D bounds = TextUtils.getTextBounds(msg, g2, g2.getFontMetrics());
            y += bounds.getHeight();
            g2.drawString(msg, (int) x, (int) y);
        }
    }

    /**
     * Draws the axis with the specified index.
     *
     * @param axis  the axis.
     * @param location  the axis location.
     * @param g2  the graphics target.
     * @param plotArea  the plot area.
     *
     * @return The axis state.
     */
    protected AxisState drawAxis(ValueAxis axis, PolarAxisLocation location, Graphics2D g2, Rectangle2D plotArea) {
        double centerX = plotArea.getCenterX();
        double centerY = plotArea.getCenterY();
        double r = Math.min(plotArea.getWidth() / 2.0, plotArea.getHeight() / 2.0) - this.margin;
        double x = centerX - r;
        double y = centerY - r;
        Rectangle2D dataArea = null;
        AxisState result = null;
        if (location == PolarAxisLocation.NORTH_RIGHT) {
            dataArea = new Rectangle2D.Double(x, y, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.RIGHT, null);
        } else if (location == PolarAxisLocation.NORTH_LEFT) {
            dataArea = new Rectangle2D.Double(centerX, y, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.LEFT, null);
        } else if (location == PolarAxisLocation.SOUTH_LEFT) {
            dataArea = new Rectangle2D.Double(centerX, centerY, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.LEFT, null);
        } else if (location == PolarAxisLocation.SOUTH_RIGHT) {
            dataArea = new Rectangle2D.Double(x, centerY, r, r);
            result = axis.draw(g2, centerX, plotArea, dataArea, RectangleEdge.RIGHT, null);
        } else if (location == PolarAxisLocation.EAST_ABOVE) {
            dataArea = new Rectangle2D.Double(centerX, centerY, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.TOP, null);
        } else if (location == PolarAxisLocation.EAST_BELOW) {
            dataArea = new Rectangle2D.Double(centerX, y, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.BOTTOM, null);
        } else if (location == PolarAxisLocation.WEST_ABOVE) {
            dataArea = new Rectangle2D.Double(x, centerY, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.TOP, null);
        } else if (location == PolarAxisLocation.WEST_BELOW) {
            dataArea = new Rectangle2D.Double(x, y, r, r);
            result = axis.draw(g2, centerY, plotArea, dataArea, RectangleEdge.BOTTOM, null);
        }
        return result;
    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current m_Renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param info  an optional object for collection dimension
     *              information ({@code null} permitted).
     */
    protected void render(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        // now get the data and plot it (the visual representation will depend
        // on the m_Renderer that has been set)...
        boolean hasData = false;
        int datasetCount = this.datasets.size();
        for (int i = datasetCount - 1; i >= 0; i--) {
            XYDataset dataset = getDataset(i);
            if (dataset == null) {
                continue;
            }
            PolarItemRenderer renderer = getRenderer(i);
            if (renderer == null) {
                continue;
            }
            if (!DatasetUtils.isEmptyOrNull(dataset)) {
                hasData = true;
                int seriesCount = dataset.getSeriesCount();
                for (int series = 0; series < seriesCount; series++) {
                    renderer.drawSeries(g2, dataArea, info, this, dataset, series);
                }
            }
        }
        if (!hasData) {
            drawNoDataMessage(g2, dataArea);
        }
    }

    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param angularTicks  the ticks for the angular axis.
     * @param radialTicks  the ticks for the radial axis.
     */
    protected void drawGridlines(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> angularTicks, List<ValueTick> radialTicks) {
        PolarItemRenderer renderer = getRenderer();
        // no renderer, no gridlines...
        if (renderer == null) {
            return;
        }
        // draw the domain grid lines, if any...
        if (isAngleGridlinesVisible()) {
            Stroke gridStroke = getAngleGridlineStroke();
            Paint gridPaint = getAngleGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                renderer.drawAngularGridLines(g2, this, angularTicks, dataArea);
            }
        }
        // draw the radius grid lines, if any...
        if (isRadiusGridlinesVisible()) {
            Stroke gridStroke = getRadiusGridlineStroke();
            Paint gridPaint = getRadiusGridlinePaint();
            if ((gridStroke != null) && (gridPaint != null)) {
                List<ValueTick> ticks = buildRadialTicks(radialTicks);
                renderer.drawRadialGridLines(g2, this, getAxis(), ticks, dataArea);
            }
        }
    }

    /**
     * Create a list of ticks based on the given list and plot properties.
     * Only ticks of a specific type may be in the result list.
     *
     * @param allTicks A list of all available ticks for the primary axis.
     *        {@code null} not permitted.
     * @return Ticks to use for radial gridlines.
     */
    protected List<ValueTick> buildRadialTicks(List<ValueTick> allTicks) {
        List<ValueTick> ticks = new ArrayList<>();
        for (ValueTick tick : allTicks) {
            if (isRadiusMinorGridlinesVisible() || TickType.MAJOR.equals(tick.getTickType())) {
                ticks.add(tick);
            }
        }
        return ticks;
    }

    /**
     * Zooms the axis ranges by the specified percentage about the anchor point.
     *
     * @param percent  the amount of the zoom.
     */
    @Override
    public void zoom(double percent) {
        for (int axisIdx = 0; axisIdx < getAxisCount(); axisIdx++) {
            final ValueAxis axis = getAxis(axisIdx);
            if (axis != null) {
                if (percent > 0.0) {
                    double radius = axis.getUpperBound();
                    double scaledRadius = radius * percent;
                    axis.setUpperBound(scaledRadius);
                    axis.setAutoRange(false);
                } else {
                    axis.setAutoRange(true);
                }
            }
        }
    }

    /**
     * A utility method that returns a list of datasets that are mapped to a
     * particular axis.
     *
     * @param axisIndex  the axis index ({@code null} not permitted).
     *
     * @return A list of datasets.
     */
    private List<XYDataset> getDatasetsMappedToAxis(Integer axisIndex) {
        Args.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset> result = new ArrayList<>();
        for (Entry<Integer, XYDataset> entry : this.datasets.entrySet()) {
            List<Integer> mappedAxes = this.datasetToAxesMap.get(entry.getKey());
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(getDataset(entry.getKey()));
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(getDataset(entry.getKey()));
                }
            }
        }
        return result;
    }

    /**
     * Returns the range for the specified axis.
     *
     * @param axis  the axis.
     *
     * @return The range.
     */
    @Override
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        List<XYDataset> mappedDatasets = new ArrayList<>();
        int axisIndex = getAxisIndex(axis);
        if (axisIndex >= 0) {
            mappedDatasets = getDatasetsMappedToAxis(axisIndex);
        }
        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
        for (XYDataset dataset : mappedDatasets) {
            if (dataset != null) {
                // FIXME better ask the renderer instead of DatasetUtilities
                result = Range.combine(result, DatasetUtils.findRangeBounds(dataset));
            }
        }
        return result;
    }

    /**
     * Receives notification of a change to the plot's m_Dataset.
     * <P>
     * The axis ranges are updated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        for (int i = 0; i < this.axes.size(); i++) {
            final ValueAxis axis = this.axes.get(i);
            if (axis != null) {
                axis.configure();
            }
        }
        if (getParent() != null) {
            getParent().datasetChanged(event);
        } else {
            super.datasetChanged(event);
        }
    }

    /**
     * Notifies all registered listeners of a property change.
     * <P>
     * One source of property change events is the plot's m_Renderer.
     *
     * @param event  information about the property change.
     */
    @Override
    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Returns the legend items for the plot.  Each legend item is generated by
     * the plot's m_Renderer, since the m_Renderer is responsible for the visual
     * representation of the data.
     *
     * @return The legend items.
     */
    @Override
    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        int count = this.datasets.size();
        for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
            XYDataset dataset = getDataset(datasetIndex);
            PolarItemRenderer renderer = getRenderer(datasetIndex);
            if (dataset != null && renderer != null) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    LegendItem item = renderer.getLegendItem(i);
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Tests this plot for equality with another object.
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
        if (!(obj instanceof PolarPlot)) {
            return false;
        }
        PolarPlot that = (PolarPlot) obj;
        if (!this.axes.equals(that.axes)) {
            return false;
        }
        if (!this.axisLocations.equals(that.axisLocations)) {
            return false;
        }
        if (!this.renderers.equals(that.renderers)) {
            return false;
        }
        if (!this.angleTickUnit.equals(that.angleTickUnit)) {
            return false;
        }
        if (this.angleGridlinesVisible != that.angleGridlinesVisible) {
            return false;
        }
        if (this.angleOffset != that.angleOffset) {
            return false;
        }
        if (this.counterClockwise != that.counterClockwise) {
            return false;
        }
        if (this.angleLabelsVisible != that.angleLabelsVisible) {
            return false;
        }
        if (!this.angleLabelFont.equals(that.angleLabelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.angleLabelPaint, that.angleLabelPaint)) {
            return false;
        }
        if (!Objects.equals(this.angleGridlineStroke, that.angleGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.angleGridlinePaint, that.angleGridlinePaint)) {
            return false;
        }
        if (this.radiusGridlinesVisible != that.radiusGridlinesVisible) {
            return false;
        }
        if (!Objects.equals(this.radiusGridlineStroke, that.radiusGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.radiusGridlinePaint, that.radiusGridlinePaint)) {
            return false;
        }
        if (this.radiusMinorGridlinesVisible != that.radiusMinorGridlinesVisible) {
            return false;
        }
        if (!this.cornerTextItems.equals(that.cornerTextItems)) {
            return false;
        }
        if (this.margin != that.margin) {
            return false;
        }
        if (!Objects.equals(this.fixedLegendItems, that.fixedLegendItems)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  this can occur if some component of
     *         the plot cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        PolarPlot clone = (PolarPlot) super.clone();
        clone.axes = CloneUtils.clone(this.axes);
        for (int i = 0; i < this.axes.size(); i++) {
            ValueAxis axis = this.axes.get(i);
            if (axis != null) {
                ValueAxis clonedAxis = (ValueAxis) axis.clone();
                clone.axes.put(i, clonedAxis);
                clonedAxis.setPlot(clone);
                clonedAxis.addChangeListener(clone);
            }
        }
        // the datasets are not cloned, but listeners need to be added...
        clone.datasets = CloneUtils.clone(this.datasets);
        for (int i = 0; i < clone.datasets.size(); ++i) {
            XYDataset d = getDataset(i);
            if (d != null) {
                d.addChangeListener(clone);
            }
        }
        clone.renderers = CloneUtils.clone(this.renderers);
        for (int i = 0; i < this.renderers.size(); i++) {
            PolarItemRenderer renderer2 = this.renderers.get(i);
            if (renderer2 instanceof PublicCloneable) {
                PublicCloneable pc = (PublicCloneable) renderer2;
                PolarItemRenderer rc = (PolarItemRenderer) pc.clone();
                clone.renderers.put(i, rc);
                rc.setPlot(clone);
                rc.addChangeListener(clone);
            }
        }
        clone.cornerTextItems = new ArrayList<>(this.cornerTextItems);
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
        SerialUtils.writeStroke(this.angleGridlineStroke, stream);
        SerialUtils.writePaint(this.angleGridlinePaint, stream);
        SerialUtils.writeStroke(this.radiusGridlineStroke, stream);
        SerialUtils.writePaint(this.radiusGridlinePaint, stream);
        SerialUtils.writePaint(this.angleLabelPaint, stream);
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
        this.angleGridlineStroke = SerialUtils.readStroke(stream);
        this.angleGridlinePaint = SerialUtils.readPaint(stream);
        this.radiusGridlineStroke = SerialUtils.readStroke(stream);
        this.radiusGridlinePaint = SerialUtils.readPaint(stream);
        this.angleLabelPaint = SerialUtils.readPaint(stream);
        int rangeAxisCount = this.axes.size();
        for (int i = 0; i < rangeAxisCount; i++) {
            Axis axis = this.axes.get(i);
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        int datasetCount = this.datasets.size();
        for (int i = 0; i < datasetCount; i++) {
            Dataset dataset = this.datasets.get(i);
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        int rendererCount = this.renderers.size();
        for (int i = 0; i < rendererCount; i++) {
            PolarItemRenderer renderer = this.renderers.get(i);
            if (renderer != null) {
                renderer.addChangeListener(this);
            }
        }
    }

    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
        // do nothing
    }

    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     * @param useAnchor  use source point as zoom anchor?
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source, boolean useAnchor) {
        // do nothing
    }

    /**
     * This method is required by the {@link Zoomable} interface, but since
     * the plot does not have any domain axes, it does nothing.
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        // do nothing
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        zoom(factor);
    }

    /**
     * Multiplies the range on the range axis by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point (in Java2D space).
     * @param useAnchor  use source point as zoom anchor?
     *
     * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
        // get the source coordinate - this plot has always a VERTICAL
        // orientation
        final double sourceX = source.getX();
        for (int axisIdx = 0; axisIdx < getAxisCount(); axisIdx++) {
            final ValueAxis axis = getAxis(axisIdx);
            if (axis != null) {
                if (useAnchor) {
                    double anchorX = axis.java2DToValue(sourceX, info.getDataArea(), RectangleEdge.BOTTOM);
                    axis.resizeRange(factor, anchorX);
                } else {
                    axis.resizeRange(factor);
                }
            }
        }
    }

    /**
     * Zooms in on the range axes.
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D coordinates).
     */
    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        zoom((upperPercent + lowerPercent) / 2.0);
    }

    /**
     * Returns {@code false} always.
     *
     * @return {@code false} always.
     */
    @Override
    public boolean isDomainZoomable() {
        return false;
    }

    /**
     * Returns {@code true} to indicate that the range axis is zoomable.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isRangeZoomable() {
        return true;
    }

    /**
     * Returns the orientation of the plot.
     *
     * @return The orientation.
     */
    @Override
    public PlotOrientation getOrientation() {
        return PlotOrientation.HORIZONTAL;
    }

    /**
     * Translates a (theta, radius) pair into Java2D coordinates.  If
     * {@code radius} is less than the lower bound of the axis, then
     * this method returns the centre point.
     *
     * @param angleDegrees  the angle in degrees.
     * @param radius  the radius.
     * @param axis  the axis.
     * @param dataArea  the data area.
     *
     * @return A point in Java2D space.
     */
    public Point translateToJava2D(double angleDegrees, double radius, ValueAxis axis, Rectangle2D dataArea) {
        if (counterClockwise) {
            angleDegrees = -angleDegrees;
        }
        double radians = Math.toRadians(angleDegrees + this.angleOffset);
        double minx = dataArea.getMinX() + this.margin;
        double maxx = dataArea.getMaxX() - this.margin;
        double miny = dataArea.getMinY() + this.margin;
        double maxy = dataArea.getMaxY() - this.margin;
        double halfWidth = (maxx - minx) / 2.0;
        double halfHeight = (maxy - miny) / 2.0;
        double midX = minx + halfWidth;
        double midY = miny + halfHeight;
        double l = Math.min(halfWidth, halfHeight);
        Rectangle2D quadrant = new Rectangle2D.Double(midX, midY, l, l);
        double axisMin = axis.getLowerBound();
        double adjustedRadius = Math.max(radius, axisMin);
        double length = axis.valueToJava2D(adjustedRadius, quadrant, RectangleEdge.BOTTOM) - midX;
        float x = (float) (midX + Math.cos(radians) * length);
        float y = (float) (midY + Math.sin(radians) * length);
        int ix = Math.round(x);
        int iy = Math.round(y);
        return new Point(ix, iy);
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
 * -----------------------------------
 * DefaultIntervalCategoryDataset.java
 * -----------------------------------
 * (C) Copyright 2002-2021, by Jeremy Bowman and Contributors.
 *
 * Original Author:  Jeremy Bowman;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A convenience class that provides a default implementation of the
 * {@link IntervalCategoryDataset} interface.
 * <p>
 * The standard constructor accepts data in a two dimensional array where the
 * first dimension is the series, and the second dimension is the category.
 */
public class DefaultIntervalCategoryDataset extends AbstractSeriesDataset implements IntervalCategoryDataset {

    /**
     * The series keys.
     */
    private Comparable[] seriesKeys;

    /**
     * The category keys.
     */
    private Comparable[] categoryKeys;

    /**
     * Storage for the start value data.
     */
    private Number[][] startData;

    /**
     * Storage for the end value data.
     */
    private Number[][] endData;

    /**
     * Creates a new dataset using the specified data values and automatically
     * generated series and category keys.
     *
     * @param starts  the starting values for the intervals ({@code null}
     *                not permitted).
     * @param ends  the ending values for the intervals ({@code null} not
     *                permitted).
     */
    public DefaultIntervalCategoryDataset(double[][] starts, double[][] ends) {
        this(DataUtils.createNumberArray2D(starts), DataUtils.createNumberArray2D(ends));
    }

    /**
     * Constructs a dataset and populates it with data from the array.
     * <p>
     * The arrays are indexed as data[series][category].  Series and category
     * names are automatically generated - you can change them using the
     * {@link #setSeriesKeys(Comparable[])} and
     * {@link #setCategoryKeys(Comparable[])} methods.
     *
     * @param starts  the start values data.
     * @param ends  the end values data.
     */
    public DefaultIntervalCategoryDataset(Number[][] starts, Number[][] ends) {
        this(null, null, starts, ends);
    }

    /**
     * Constructs a DefaultIntervalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series.
     * <p>
     * Category names are generated automatically ("Category 1", "Category 2",
     * etc).
     *
     * @param seriesNames  the series names (if {@code null}, series names
     *         will be generated automatically).
     * @param starts  the start values data, indexed as data[series][category].
     * @param ends  the end values data, indexed as data[series][category].
     */
    public DefaultIntervalCategoryDataset(String[] seriesNames, Number[][] starts, Number[][] ends) {
        this(seriesNames, null, starts, ends);
    }

    /**
     * Constructs a DefaultIntervalCategoryDataset, populates it with data
     * from the arrays, and uses the supplied names for the series and the
     * supplied objects for the categories.
     *
     * @param seriesKeys  the series keys (if {@code null}, series keys
     *         will be generated automatically).
     * @param categoryKeys  the category keys (if {@code null}, category
     *         keys will be generated automatically).
     * @param starts  the start values data, indexed as data[series][category].
     * @param ends  the end values data, indexed as data[series][category].
     */
    public DefaultIntervalCategoryDataset(Comparable[] seriesKeys, Comparable[] categoryKeys, Number[][] starts, Number[][] ends) {
        this.startData = starts;
        this.endData = ends;
        if (starts != null && ends != null) {
            ResourceBundle resources = ResourceBundle.getBundle("org.jfree.data.resources.DataPackageResources");
            int seriesCount = starts.length;
            if (seriesCount != ends.length) {
                String errMsg = "DefaultIntervalCategoryDataset: the number " + "of series in the start value dataset does " + "not match the number of series in the end " + "value dataset.";
                throw new IllegalArgumentException(errMsg);
            }
            if (seriesCount > 0) {
                // set up the series names...
                if (seriesKeys != null) {
                    if (seriesKeys.length != seriesCount) {
                        throw new IllegalArgumentException("The number of series keys does not " + "match the number of series in the data.");
                    }
                    this.seriesKeys = seriesKeys;
                } else {
                    String prefix = resources.getString("series.default-prefix") + " ";
                    this.seriesKeys = generateKeys(seriesCount, prefix);
                }
                // set up the category names...
                int categoryCount = starts[0].length;
                if (categoryCount != ends[0].length) {
                    String errMsg = "DefaultIntervalCategoryDataset: the " + "number of categories in the start value " + "dataset does not match the number of " + "categories in the end value dataset.";
                    throw new IllegalArgumentException(errMsg);
                }
                if (categoryKeys != null) {
                    if (categoryKeys.length != categoryCount) {
                        throw new IllegalArgumentException("The number of category keys does not match " + "the number of categories in the data.");
                    }
                    this.categoryKeys = categoryKeys;
                } else {
                    String prefix = resources.getString("categories.default-prefix") + " ";
                    this.categoryKeys = generateKeys(categoryCount, prefix);
                }
            } else {
                this.seriesKeys = new Comparable[0];
                this.categoryKeys = new Comparable[0];
            }
        }
    }

    /**
     * Returns the number of series in the dataset (possibly zero).
     *
     * @return The number of series in the dataset.
     *
     * @see #getRowCount()
     * @see #getCategoryCount()
     */
    @Override
    public int getSeriesCount() {
        int result = 0;
        if (this.startData != null) {
            result = this.startData.length;
        }
        return result;
    }

    /**
     * Returns a series index.
     *
     * @param seriesKey  the series key.
     *
     * @return The series index.
     *
     * @see #getRowIndex(Comparable)
     * @see #getSeriesKey(int)
     */
    public int getSeriesIndex(Comparable seriesKey) {
        int result = -1;
        for (int i = 0; i < this.seriesKeys.length; i++) {
            if (seriesKey.equals(this.seriesKeys[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the name of the specified series.
     *
     * @param series  the index of the required series (zero-based).
     *
     * @return The name of the specified series.
     *
     * @see #getSeriesIndex(Comparable)
     */
    @Override
    public Comparable getSeriesKey(int series) {
        if ((series >= getSeriesCount()) || (series < 0)) {
            throw new IllegalArgumentException("No such series : " + series);
        }
        return this.seriesKeys[series];
    }

    /**
     * Sets the names of the series in the dataset.
     *
     * @param seriesKeys  the new keys ({@code null} not permitted, the
     *         length of the array must match the number of series in the
     *         dataset).
     *
     * @see #setCategoryKeys(Comparable[])
     */
    public void setSeriesKeys(Comparable[] seriesKeys) {
        Args.nullNotPermitted(seriesKeys, "seriesKeys");
        if (seriesKeys.length != getSeriesCount()) {
            throw new IllegalArgumentException("The number of series keys does not match the data.");
        }
        this.seriesKeys = seriesKeys;
        fireDatasetChanged();
    }

    /**
     * Returns the number of categories in the dataset.
     *
     * @return The number of categories in the dataset.
     *
     * @see #getColumnCount()
     */
    public int getCategoryCount() {
        int result = 0;
        if (this.startData != null) {
            if (getSeriesCount() > 0) {
                result = this.startData[0].length;
            }
        }
        return result;
    }

    /**
     * Returns a list of the categories in the dataset.  This method supports
     * the {@link CategoryDataset} interface.
     *
     * @return A list of the categories in the dataset.
     *
     * @see #getRowKeys()
     */
    @Override
    public List getColumnKeys() {
        // the CategoryDataset interface expects a list of categories, but
        // we've stored them in an array...
        if (this.categoryKeys == null) {
            return new ArrayList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(this.categoryKeys));
        }
    }

    /**
     * Sets the categories for the dataset.
     *
     * @param categoryKeys  an array of objects representing the categories in
     *                      the dataset.
     *
     * @see #getRowKeys()
     * @see #setSeriesKeys(Comparable[])
     */
    public void setCategoryKeys(Comparable[] categoryKeys) {
        Args.nullNotPermitted(categoryKeys, "categoryKeys");
        if (categoryKeys.length != getCategoryCount()) {
            throw new IllegalArgumentException("The number of categories does not match the data.");
        }
        for (int i = 0; i < categoryKeys.length; i++) {
            if (categoryKeys[i] == null) {
                throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setCategoryKeys(): " + "null category not permitted.");
            }
        }
        this.categoryKeys = categoryKeys;
        fireDatasetChanged();
    }

    /**
     * Returns the data value for one category in a series.
     * <P>
     * This method is part of the CategoryDataset interface.  Not particularly
     * meaningful for this class...returns the end value.
     *
     * @param series    The required series (zero based index).
     * @param category  The required category.
     *
     * @return The data value for one category in a series (null possible).
     *
     * @see #getEndValue(Comparable, Comparable)
     */
    @Override
    public Number getValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex < 0) {
            throw new UnknownKeyException("Unknown 'category' key.");
        }
        return getValue(seriesIndex, itemIndex);
    }

    /**
     * Returns the data value for one category in a series.
     * <P>
     * This method is part of the CategoryDataset interface.  Not particularly
     * meaningful for this class...returns the end value.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return The data value for one category in a series (null possible).
     *
     * @see #getEndValue(int, int)
     */
    @Override
    public Number getValue(int series, int category) {
        return getEndValue(series, category);
    }

    /**
     * Returns the start data value for one category in a series.
     *
     * @param series  the required series.
     * @param category  the required category.
     *
     * @return The start data value for one category in a series
     *         (possibly {@code null}).
     *
     * @see #getStartValue(int, int)
     */
    @Override
    public Number getStartValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex < 0) {
            throw new UnknownKeyException("Unknown 'category' key.");
        }
        return getStartValue(seriesIndex, itemIndex);
    }

    /**
     * Returns the start data value for one category in a series.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return The start data value for one category in a series
     *         (possibly {@code null}).
     *
     * @see #getStartValue(Comparable, Comparable)
     */
    @Override
    public Number getStartValue(int series, int category) {
        // check arguments...
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "series index out of range.");
        }
        if ((category < 0) || (category >= getCategoryCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "category index out of range.");
        }
        // fetch the value...
        return this.startData[series][category];
    }

    /**
     * Returns the end data value for one category in a series.
     *
     * @param series  the required series.
     * @param category  the required category.
     *
     * @return The end data value for one category in a series (null possible).
     *
     * @see #getEndValue(int, int)
     */
    @Override
    public Number getEndValue(Comparable series, Comparable category) {
        int seriesIndex = getSeriesIndex(series);
        if (seriesIndex < 0) {
            throw new UnknownKeyException("Unknown 'series' key.");
        }
        int itemIndex = getColumnIndex(category);
        if (itemIndex < 0) {
            throw new UnknownKeyException("Unknown 'category' key.");
        }
        return getEndValue(seriesIndex, itemIndex);
    }

    /**
     * Returns the end data value for one category in a series.
     *
     * @param series  the required series (zero based index).
     * @param category  the required category.
     *
     * @return The end data value for one category in a series (null possible).
     *
     * @see #getEndValue(Comparable, Comparable)
     */
    @Override
    public Number getEndValue(int series, int category) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "series index out of range.");
        }
        if ((category < 0) || (category >= getCategoryCount())) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.getValue(): " + "category index out of range.");
        }
        return this.endData[series][category];
    }

    /**
     * Sets the start data value for one category in a series.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @param value The value.
     *
     * @see #setEndValue(int, Comparable, Number)
     */
    public void setStartValue(int series, Comparable category, Number value) {
        // does the series exist?
        if ((series < 0) || (series > getSeriesCount() - 1)) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "series outside valid range.");
        }
        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "unrecognised category.");
        }
        // update the data...
        this.startData[series][categoryIndex] = value;
        fireDatasetChanged();
    }

    /**
     * Sets the end data value for one category in a series.
     *
     * @param series  the series (zero-based index).
     * @param category  the category.
     *
     * @param value the value.
     *
     * @see #setStartValue(int, Comparable, Number)
     */
    public void setEndValue(int series, Comparable category, Number value) {
        // does the series exist?
        if ((series < 0) || (series > getSeriesCount() - 1)) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "series outside valid range.");
        }
        // is the category valid?
        int categoryIndex = getCategoryIndex(category);
        if (categoryIndex < 0) {
            throw new IllegalArgumentException("DefaultIntervalCategoryDataset.setValue: " + "unrecognised category.");
        }
        // update the data...
        this.endData[series][categoryIndex] = value;
        fireDatasetChanged();
    }

    /**
     * Returns the index for the given category.
     *
     * @param category  the category ({@code null} not permitted).
     *
     * @return The index.
     *
     * @see #getColumnIndex(Comparable)
     */
    public int getCategoryIndex(Comparable category) {
        int result = -1;
        for (int i = 0; i < this.categoryKeys.length; i++) {
            if (category.equals(this.categoryKeys[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Generates an array of keys, by appending a space plus an integer
     * (starting with 1) to the supplied prefix string.
     *
     * @param count  the number of keys required.
     * @param prefix  the name prefix.
     *
     * @return An array of <i>prefixN</i> with N = { 1 .. count}.
     */
    private Comparable[] generateKeys(int count, String prefix) {
        Comparable[] result = new Comparable[count];
        String name;
        for (int i = 0; i < count; i++) {
            name = prefix + (i + 1);
            result[i] = name;
        }
        return result;
    }

    /**
     * Returns a column key.
     *
     * @param column  the column index.
     *
     * @return The column key.
     *
     * @see #getRowKey(int)
     */
    @Override
    public Comparable getColumnKey(int column) {
        return this.categoryKeys[column];
    }

    /**
     * Returns a column index.
     *
     * @param columnKey  the column key ({@code null} not permitted).
     *
     * @return The column index.
     *
     * @see #getCategoryIndex(Comparable)
     */
    @Override
    public int getColumnIndex(Comparable columnKey) {
        Args.nullNotPermitted(columnKey, "columnKey");
        return getCategoryIndex(columnKey);
    }

    /**
     * Returns a row index.
     *
     * @param rowKey  the row key.
     *
     * @return The row index.
     *
     * @see #getSeriesIndex(Comparable)
     */
    @Override
    public int getRowIndex(Comparable rowKey) {
        return getSeriesIndex(rowKey);
    }

    /**
     * Returns a list of the series in the dataset.  This method supports the
     * {@link CategoryDataset} interface.
     *
     * @return A list of the series in the dataset.
     *
     * @see #getColumnKeys()
     */
    @Override
    public List getRowKeys() {
        // the CategoryDataset interface expects a list of series, but
        // we've stored them in an array...
        if (this.seriesKeys == null) {
            return new java.util.ArrayList();
        } else {
            return Collections.unmodifiableList(Arrays.asList(this.seriesKeys));
        }
    }

    /**
     * Returns the name of the specified series.
     *
     * @param row  the index of the required row/series (zero-based).
     *
     * @return The name of the specified series.
     *
     * @see #getColumnKey(int)
     */
    @Override
    public Comparable getRowKey(int row) {
        if ((row >= getRowCount()) || (row < 0)) {
            throw new IllegalArgumentException("The 'row' argument is out of bounds.");
        }
        return this.seriesKeys[row];
    }

    /**
     * Returns the number of categories in the dataset.  This method is part of
     * the {@link CategoryDataset} interface.
     *
     * @return The number of categories in the dataset.
     *
     * @see #getCategoryCount()
     * @see #getRowCount()
     */
    @Override
    public int getColumnCount() {
        return this.categoryKeys.length;
    }

    /**
     * Returns the number of series in the dataset (possibly zero).
     *
     * @return The number of series in the dataset.
     *
     * @see #getSeriesCount()
     * @see #getColumnCount()
     */
    @Override
    public int getRowCount() {
        return this.seriesKeys.length;
    }

    /**
     * Tests this dataset for equality with an arbitrary object.
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
        if (!(obj instanceof DefaultIntervalCategoryDataset)) {
            return false;
        }
        DefaultIntervalCategoryDataset that = (DefaultIntervalCategoryDataset) obj;
        if (!Arrays.equals(this.seriesKeys, that.seriesKeys)) {
            return false;
        }
        if (!Arrays.equals(this.categoryKeys, that.categoryKeys)) {
            return false;
        }
        if (!equal(this.startData, that.startData)) {
            return false;
        }
        if (!equal(this.endData, that.endData)) {
            return false;
        }
        // seem to be the same...
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Arrays.deepHashCode(this.seriesKeys);
        hash = 61 * hash + Arrays.deepHashCode(this.categoryKeys);
        hash = 61 * hash + Arrays.deepHashCode(this.startData);
        hash = 61 * hash + Arrays.deepHashCode(this.endData);
        return hash;
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning the
     *         dataset.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DefaultIntervalCategoryDataset clone = (DefaultIntervalCategoryDataset) super.clone();
        clone.categoryKeys = (Comparable[]) this.categoryKeys.clone();
        clone.seriesKeys = (Comparable[]) this.seriesKeys.clone();
        clone.startData = clone(this.startData);
        clone.endData = clone(this.endData);
        return clone;
    }

    /**
     * Tests two double[][] arrays for equality.
     *
     * @param array1  the first array ({@code null} permitted).
     * @param array2  the second arrray ({@code null} permitted).
     *
     * @return A boolean.
     */
    private static boolean equal(Number[][] array1, Number[][] array2) {
        if (array1 == null) {
            return (array2 == null);
        }
        if (array2 == null) {
            return false;
        }
        if (array1.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array1.length; i++) {
            if (!Arrays.equals(array1[i], array2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clones a two dimensional array of {@code Number} objects.
     *
     * @param array  the array ({@code null} not permitted).
     *
     * @return A clone of the array.
     */
    private static Number[][] clone(Number[][] array) {
        Args.nullNotPermitted(array, "array");
        Number[][] result = new Number[array.length][];
        for (int i = 0; i < array.length; i++) {
            Number[] child = array[i];
            Number[] copychild = new Number[child.length];
            System.arraycopy(child, 0, copychild, 0, child.length);
            result[i] = copychild;
        }
        return result;
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
 * CategoryDatasetHandler.java
 * ---------------------------
 * (C) Copyright 2003-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A SAX handler for reading a {@link CategoryDataset} from an XML file.
 */
public class CategoryDatasetHandler extends RootHandler implements DatasetTags {

    /**
     * The dataset under construction.
     */
    private DefaultCategoryDataset<String, String> dataset;

    /**
     * Creates a new handler.
     */
    public CategoryDatasetHandler() {
        super();
        this.dataset = null;
    }

    /**
     * Returns the dataset.
     *
     * @return The dataset.
     */
    public CategoryDataset<String, String> getDataset() {
        return this.dataset;
    }

    /**
     * Adds an item to the dataset.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param value  the value.
     */
    public void addItem(String rowKey, String columnKey, Number value) {
        this.dataset.addValue(value, rowKey, columnKey);
    }

    /**
     * The start of an element.
     *
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     * @param atts  the element attributes.
     *
     * @throws SAXException for errors.
     */
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.startElement(namespaceURI, localName, qName, atts);
        } else if (qName.equals(CATEGORYDATASET_TAG)) {
            this.dataset = new DefaultCategoryDataset<>();
        } else if (qName.equals(SERIES_TAG)) {
            CategorySeriesHandler subhandler = new CategorySeriesHandler(this);
            getSubHandlers().push(subhandler);
            subhandler.startElement(namespaceURI, localName, qName, atts);
        } else {
            throw new SAXException("Element not recognised: " + qName);
        }
    }

    /**
     * The end of an element.
     *
     * @param namespaceURI  the namespace.
     * @param localName  the element name.
     * @param qName  the element name.
     *
     * @throws SAXException for errors.
     */
    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        DefaultHandler current = getCurrentHandler();
        if (current != this) {
            current.endElement(namespaceURI, localName, qName);
        }
    }
}
