package NOD.af;
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
 * JFreeChart.java
 * ---------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Andrzej Porebski;
 *                   David Li;
 *                   Wolfgang Irler;
 *                   Christian W. Zuckschwerdt;
 *                   Klaus Rheinwald;
 *                   Nicolas Brodu;
 *                   Peter Kolb (patch 2603321);
 *
 * NOTE: The above list of contributors lists only the people that have
 * contributed to this source file (JFreeChart.java) - for a list of ALL
 * contributors to the project, please see the README.txt file.
 *
 */
/**
 * A chart class implemented using the Java 2D APIs.  The current version
 * supports bar charts, line charts, pie charts and xy plots (including time
 * series data).
 * <P>
 * JFreeChart coordinates several objects to achieve its aim of being able to
 * draw a chart on a Java 2D graphics device: a list of {@link Title} objects
 * (which often includes the chart's legend), a {@link Plot} and a
 * {@link org.jfree.data.general.Dataset} (the plot in turn manages a
 * domain axis and a range axis).
 * <P>
 * You should use a {@link ChartPanel} to display a chart in a GUI.
 * <P>
 * The {@link ChartFactory} class contains static methods for creating
 * 'ready-made' charts.
 *
 * @see ChartPanel
 * @see ChartFactory
 * @see Title
 * @see Plot
 */
public class JFreeChart implements Drawable, TitleChangeListener, PlotChangeListener, ChartElement, Serializable, Cloneable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -3470703747817429120L;

    /**
     * The default font for titles.
     */
    public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);

    /**
     * The default background color.
     */
    public static final Paint DEFAULT_BACKGROUND_PAINT = UIManager.getColor("Panel.background");

    /**
     * The default background image.
     */
    public static final Image DEFAULT_BACKGROUND_IMAGE = null;

    /**
     * The default background image alignment.
     */
    public static final RectangleAlignment DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = RectangleAlignment.FILL;

    /**
     * The default background image alpha.
     */
    public static final float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5f;

    /**
     * The key for a rendering hint that can suppress the generation of a
     * shadow effect when drawing the chart.  The hint value must be a
     * Boolean.
     */
    public static final RenderingHints.Key KEY_SUPPRESS_SHADOW_GENERATION = new RenderingHints.Key(0) {

        @Override
        public boolean isCompatibleValue(Object val) {
            return val instanceof Boolean;
        }
    };

    /**
     * Rendering hints that will be used for chart drawing.  This should never
     * be {@code null}.
     */
    private transient RenderingHints renderingHints;

    /**
     * The chart id (optional, will be used by JFreeSVG export).
     */
    private String id;

    /**
     * A flag that controls whether the chart border is drawn.
     */
    private boolean borderVisible;

    /**
     * The stroke used to draw the chart border (if visible).
     */
    private transient Stroke borderStroke;

    /**
     * The paint used to draw the chart border (if visible).
     */
    private transient Paint borderPaint;

    /**
     * The padding between the chart border and the chart drawing area.
     */
    private RectangleInsets padding;

    /**
     * The chart title (optional).
     */
    private TextTitle title;

    /**
     * The chart subtitles (zero, one or many).  This field should never be
     * {@code null}.
     */
    private List<Title> subtitles;

    /**
     * Draws the visual representation of the data.
     */
    private Plot plot;

    /**
     * Paint used to draw the background of the chart.
     */
    private transient Paint backgroundPaint;

    /**
     * An optional background image for the chart.
     */
    // todo: not serialized yet
    private transient Image backgroundImage;

    /**
     * The alignment for the background image.
     */
    private RectangleAlignment backgroundImageAlignment = RectangleAlignment.FILL;

    /**
     * The alpha transparency for the background image.
     */
    private float backgroundImageAlpha = 0.5f;

    /**
     * Storage for registered change listeners.
     */
    private transient EventListenerList changeListeners;

    /**
     * Storage for registered progress listeners.
     */
    private transient EventListenerList progressListeners;

    /**
     * A flag that can be used to enable/disable notification of chart change
     * events.
     */
    private boolean notify;

    /**
     * A flag that controls whether rendering hints that identify
     * chart element should be added during rendering.  This defaults to false
     * and it should only be enabled if the output target will use the hints.
     * JFreeSVG is one output target that supports these hints.
     */
    private boolean elementHinting;

    /**
     * Creates a new chart based on the supplied plot.  The chart will have
     * a legend added automatically, but no title (although you can easily add
     * one later).
     * <br><br>
     * Note that the  {@link ChartFactory} class contains a range
     * of static methods that will return ready-made charts, and often this
     * is a more convenient way to create charts than using this constructor.
     *
     * @param plot  the plot ({@code null} not permitted).
     */
    public JFreeChart(Plot plot) {
        this(null, null, plot, true);
    }

    /**
     * Creates a new chart with the given title and plot.  A default font
     * ({@link #DEFAULT_TITLE_FONT}) is used for the title, and the chart will
     * have a legend added automatically.
     * <br><br>
     * Note that the {@link ChartFactory} class contains a range
     * of static methods that will return ready-made charts, and often this
     * is a more convenient way to create charts than using this constructor.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param plot  the plot ({@code null} not permitted).
     */
    public JFreeChart(String title, Plot plot) {
        this(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
    }

    /**
     * Creates a new chart with the given title and plot.  The
     * {@code createLegend} argument specifies whether a legend
     * should be added to the chart.
     * <br><br>
     * Note that the  {@link ChartFactory} class contains a range
     * of static methods that will return ready-made charts, and often this
     * is a more convenient way to create charts than using this constructor.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param titleFont  the font for displaying the chart title
     *                   ({@code null} permitted).
     * @param plot  controller of the visual representation of the data
     *              ({@code null} not permitted).
     * @param createLegend  a flag indicating whether a legend should
     *                      be created for the chart.
     */
    public JFreeChart(String title, Font titleFont, Plot plot, boolean createLegend) {
        Args.nullNotPermitted(plot, "plot");
        this.id = null;
        plot.setChart(this);
        // create storage for listeners...
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        // default is to notify listeners when the
        this.notify = true;
        // chart changes
        this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // added the following hint because of
        // http://stackoverflow.com/questions/7785082/
        this.renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        this.borderVisible = false;
        this.borderStroke = new BasicStroke(1.0f);
        this.borderPaint = Color.BLACK;
        this.padding = RectangleInsets.ZERO_INSETS;
        this.plot = plot;
        plot.addChangeListener(this);
        this.subtitles = new ArrayList<>();
        // create a legend, if requested...
        if (createLegend) {
            LegendTitle legend = new LegendTitle(this.plot);
            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
            legend.setBackgroundPaint(Color.WHITE);
            legend.setPosition(RectangleEdge.BOTTOM);
            this.subtitles.add(legend);
            legend.addChangeListener(this);
        }
        // add the chart title, if one has been specified...
        if (title != null) {
            if (titleFont == null) {
                titleFont = DEFAULT_TITLE_FONT;
            }
            this.title = new TextTitle(title, titleFont);
            this.title.addChangeListener(this);
        }
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;
        this.backgroundImageAlignment = DEFAULT_BACKGROUND_IMAGE_ALIGNMENT;
        this.backgroundImageAlpha = DEFAULT_BACKGROUND_IMAGE_ALPHA;
    }

    /**
     * Returns the ID for the chart.
     *
     * @return The ID for the chart (possibly {@code null}).
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets the ID for the chart.
     *
     * @param id  the id ({@code null} permitted).
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Returns the flag that controls whether rendering hints
     * ({@link ChartHints#KEY_BEGIN_ELEMENT} and
     * {@link ChartHints#KEY_END_ELEMENT}) that identify chart elements are
     * added during rendering.  The default value is {@code false}.
     *
     * @return A boolean.
     *
     * @see #setElementHinting(boolean)
     */
    public boolean getElementHinting() {
        return this.elementHinting;
    }

    /**
     * Sets the flag that controls whether rendering hints
     * ({@link ChartHints#KEY_BEGIN_ELEMENT} and
     * {@link ChartHints#KEY_END_ELEMENT}) that identify chart elements are
     * added during rendering.
     *
     * @param hinting  the new flag value.
     *
     * @see #getElementHinting()
     */
    public void setElementHinting(boolean hinting) {
        this.elementHinting = hinting;
    }

    /**
     * Returns the collection of rendering hints for the chart.
     *
     * @return The rendering hints for the chart (never {@code null}).
     *
     * @see #setRenderingHints(RenderingHints)
     */
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    /**
     * Sets the rendering hints for the chart.  These will be added (using the
     * {@code Graphics2D.addRenderingHints()} method) near the start of the
     * {@code JFreeChart.draw()} method.
     *
     * @param renderingHints  the rendering hints ({@code null} not permitted).
     *
     * @see #getRenderingHints()
     */
    public void setRenderingHints(RenderingHints renderingHints) {
        Args.nullNotPermitted(renderingHints, "renderingHints");
        this.renderingHints = renderingHints;
        fireChartChanged();
    }

    /**
     * Returns a flag that controls whether a border is drawn around the
     * outside of the chart.
     *
     * @return A boolean.
     *
     * @see #setBorderVisible(boolean)
     */
    public boolean isBorderVisible() {
        return this.borderVisible;
    }

    /**
     * Sets a flag that controls whether a border is drawn around the
     * outside of the chart.
     *
     * @param visible  the flag.
     *
     * @see #isBorderVisible()
     */
    public void setBorderVisible(boolean visible) {
        this.borderVisible = visible;
        fireChartChanged();
    }

    /**
     * Returns the stroke used to draw the chart border (if visible).
     *
     * @return The border stroke.
     *
     * @see #setBorderStroke(Stroke)
     */
    public Stroke getBorderStroke() {
        return this.borderStroke;
    }

    /**
     * Sets the stroke used to draw the chart border (if visible).
     *
     * @param stroke  the stroke.
     *
     * @see #getBorderStroke()
     */
    public void setBorderStroke(Stroke stroke) {
        this.borderStroke = stroke;
        fireChartChanged();
    }

    /**
     * Returns the paint used to draw the chart border (if visible).
     *
     * @return The border paint.
     *
     * @see #setBorderPaint(Paint)
     */
    public Paint getBorderPaint() {
        return this.borderPaint;
    }

    /**
     * Sets the paint used to draw the chart border (if visible).
     *
     * @param paint  the paint.
     *
     * @see #getBorderPaint()
     */
    public void setBorderPaint(Paint paint) {
        this.borderPaint = paint;
        fireChartChanged();
    }

    /**
     * Returns the padding between the chart border and the chart drawing area.
     *
     * @return The padding (never {@code null}).
     *
     * @see #setPadding(RectangleInsets)
     */
    public RectangleInsets getPadding() {
        return this.padding;
    }

    /**
     * Sets the padding between the chart border and the chart drawing area,
     * and sends a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param padding  the padding ({@code null} not permitted).
     *
     * @see #getPadding()
     */
    public void setPadding(RectangleInsets padding) {
        Args.nullNotPermitted(padding, "padding");
        this.padding = padding;
        notifyListeners(new ChartChangeEvent(this));
    }

    /**
     * Returns the main chart title.  Very often a chart will have just one
     * title, so we make this case simple by providing accessor methods for
     * the main title.  However, multiple titles are supported - see the
     * {@link #addSubtitle(Title)} method.
     *
     * @return The chart title (possibly {@code null}).
     *
     * @see #setTitle(TextTitle)
     */
    public TextTitle getTitle() {
        return this.title;
    }

    /**
     * Sets the main title for the chart and sends a {@link ChartChangeEvent}
     * to all registered listeners.  If you do not want a title for the
     * chart, set it to {@code null}.  If you want more than one title on
     * a chart, use the {@link #addSubtitle(Title)} method.
     *
     * @param title  the title ({@code null} permitted).
     *
     * @see #getTitle()
     */
    public void setTitle(TextTitle title) {
        if (this.title != null) {
            this.title.removeChangeListener(this);
        }
        this.title = title;
        if (title != null) {
            title.addChangeListener(this);
        }
        fireChartChanged();
    }

    /**
     * Sets the chart title and sends a {@link ChartChangeEvent} to all
     * registered listeners.  This is a convenience method that ends up calling
     * the {@link #setTitle(TextTitle)} method.  If there is an existing title,
     * its text is updated, otherwise a new title using the default font is
     * added to the chart.  If {@code text} is {@code null} the chart
     * title is set to {@code null}.
     *
     * @param text  the title text ({@code null} permitted).
     *
     * @see #getTitle()
     */
    public void setTitle(String text) {
        if (text != null) {
            if (this.title == null) {
                setTitle(new TextTitle(text, JFreeChart.DEFAULT_TITLE_FONT));
            } else {
                this.title.setText(text);
            }
        } else {
            setTitle((TextTitle) null);
        }
    }

    /**
     * Adds a legend to the plot and sends a {@link ChartChangeEvent} to all
     * registered listeners.
     *
     * @param legend  the legend ({@code null} not permitted).
     *
     * @see #removeLegend()
     */
    public void addLegend(LegendTitle legend) {
        addSubtitle(legend);
    }

    /**
     * Returns the legend for the chart, if there is one.  Note that a chart
     * can have more than one legend - this method returns the first.
     *
     * @return The legend (possibly {@code null}).
     *
     * @see #getLegend(int)
     */
    public LegendTitle getLegend() {
        return getLegend(0);
    }

    /**
     * Returns the nth legend for a chart, or {@code null}.
     *
     * @param index  the legend index (zero-based).
     *
     * @return The legend (possibly {@code null}).
     *
     * @see #addLegend(LegendTitle)
     */
    public LegendTitle getLegend(int index) {
        int seen = 0;
        for (Title subtitle : this.subtitles) {
            if (subtitle instanceof LegendTitle) {
                if (seen == index) {
                    return (LegendTitle) subtitle;
                } else {
                    seen++;
                }
            }
        }
        return null;
    }

    /**
     * Removes the first legend in the chart and sends a
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @see #getLegend()
     */
    public void removeLegend() {
        removeSubtitle(getLegend());
    }

    /**
     * Returns a new list containing all the subtitles for the chart.
     *
     * @return The subtitle list (possibly empty, but never {@code null}).
     *
     * @see #setSubtitles(List)
     */
    public List<Title> getSubtitles() {
        return new ArrayList<>(this.subtitles);
    }

    /**
     * Sets the title list for the chart (completely replaces any existing
     * titles) and sends a {@link ChartChangeEvent} to all registered
     * listeners.
     *
     * @param subtitles  the new list of subtitles ({@code null} not
     *                   permitted).
     *
     * @see #getSubtitles()
     */
    public void setSubtitles(List<Title> subtitles) {
        Args.nullNotPermitted(subtitles, "subtitles");
        setNotify(false);
        clearSubtitles();
        for (Title t : subtitles) {
            if (t != null) {
                addSubtitle(t);
            }
        }
        // this fires a ChartChangeEvent
        setNotify(true);
    }

    /**
     * Returns the number of titles for the chart.
     *
     * @return The number of titles for the chart.
     *
     * @see #getSubtitles()
     */
    public int getSubtitleCount() {
        return this.subtitles.size();
    }

    /**
     * Returns a chart subtitle.
     *
     * @param index  the index of the chart subtitle (zero based).
     *
     * @return A chart subtitle.
     *
     * @see #addSubtitle(Title)
     */
    public Title getSubtitle(int index) {
        if ((index < 0) || (index >= getSubtitleCount())) {
            throw new IllegalArgumentException("Index out of range.");
        }
        return this.subtitles.get(index);
    }

    /**
     * Adds a chart subtitle, and notifies registered listeners that the chart
     * has been modified.
     *
     * @param subtitle  the subtitle ({@code null} not permitted).
     *
     * @see #getSubtitle(int)
     */
    public void addSubtitle(Title subtitle) {
        Args.nullNotPermitted(subtitle, "subtitle");
        this.subtitles.add(subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }

    /**
     * Adds a subtitle at a particular position in the subtitle list, and sends
     * a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param index  the index (in the range 0 to {@link #getSubtitleCount()}).
     * @param subtitle  the subtitle to add ({@code null} not permitted).
     */
    public void addSubtitle(int index, Title subtitle) {
        Args.requireInRange(index, "index", 0, getSubtitleCount());
        Args.nullNotPermitted(subtitle, "subtitle");
        this.subtitles.add(index, subtitle);
        subtitle.addChangeListener(this);
        fireChartChanged();
    }

    /**
     * Clears all subtitles from the chart and sends a {@link ChartChangeEvent}
     * to all registered listeners.
     *
     * @see #addSubtitle(Title)
     */
    public void clearSubtitles() {
        for (Title t : this.subtitles) {
            t.removeChangeListener(this);
        }
        this.subtitles.clear();
        fireChartChanged();
    }

    /**
     * Removes the specified subtitle and sends a {@link ChartChangeEvent} to
     * all registered listeners.
     *
     * @param title  the title.
     *
     * @see #addSubtitle(Title)
     */
    public void removeSubtitle(Title title) {
        this.subtitles.remove(title);
        fireChartChanged();
    }

    /**
     * Returns the plot for the chart.  The plot is a class responsible for
     * coordinating the visual representation of the data, including the axes
     * (if any).
     *
     * @return The plot.
     */
    public Plot getPlot() {
        return this.plot;
    }

    /**
     * Returns a flag that indicates whether anti-aliasing is used when
     * the chart is drawn.
     *
     * @return The flag.
     *
     * @see #setAntiAlias(boolean)
     */
    public boolean getAntiAlias() {
        Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
        return RenderingHints.VALUE_ANTIALIAS_ON.equals(val);
    }

    /**
     * Sets a flag that indicates whether anti-aliasing is used when the
     * chart is drawn.
     * <P>
     * Anti-aliasing usually improves the appearance of charts, but is slower.
     *
     * @param flag  the new value of the flag.
     *
     * @see #getAntiAlias()
     */
    public void setAntiAlias(boolean flag) {
        Object hint = flag ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
        this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, hint);
        fireChartChanged();
    }

    /**
     * Returns the current value stored in the rendering hints table for
     * {@link RenderingHints#KEY_TEXT_ANTIALIASING}.
     *
     * @return The hint value (possibly {@code null}).
     *
     * @see #setTextAntiAlias(Object)
     */
    public Object getTextAntiAlias() {
        return this.renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
    }

    /**
     * Sets the value in the rendering hints table for
     * {@link RenderingHints#KEY_TEXT_ANTIALIASING} to either
     * {@link RenderingHints#VALUE_TEXT_ANTIALIAS_ON} or
     * {@link RenderingHints#VALUE_TEXT_ANTIALIAS_OFF}, then sends a
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @param flag  the new value of the flag.
     *
     * @see #getTextAntiAlias()
     * @see #setTextAntiAlias(Object)
     */
    public void setTextAntiAlias(boolean flag) {
        if (flag) {
            setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }

    /**
     * Sets the value in the rendering hints table for
     * {@link RenderingHints#KEY_TEXT_ANTIALIASING} and sends a
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @param val  the new value ({@code null} permitted).
     *
     * @see #getTextAntiAlias()
     * @see #setTextAntiAlias(boolean)
     */
    public void setTextAntiAlias(Object val) {
        this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, val);
        notifyListeners(new ChartChangeEvent(this));
    }

    /**
     * Returns the paint used for the chart background.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setBackgroundPaint(Paint)
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the paint used to fill the chart background and sends a
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getBackgroundPaint()
     */
    public void setBackgroundPaint(Paint paint) {
        if (this.backgroundPaint != null) {
            if (!this.backgroundPaint.equals(paint)) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        } else {
            if (paint != null) {
                this.backgroundPaint = paint;
                fireChartChanged();
            }
        }
    }

    /**
     * Returns the background image for the chart, or {@code null} if
     * there is no image.
     *
     * @return The image (possibly {@code null}).
     *
     * @see #setBackgroundImage(Image)
     */
    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    /**
     * Sets the background image for the chart and sends a
     * {@link ChartChangeEvent} to all registered listeners.
     *
     * @param image  the image ({@code null} permitted).
     *
     * @see #getBackgroundImage()
     */
    public void setBackgroundImage(Image image) {
        if (this.backgroundImage != null) {
            if (!this.backgroundImage.equals(image)) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        } else {
            if (image != null) {
                this.backgroundImage = image;
                fireChartChanged();
            }
        }
    }

    /**
     * Returns the background image alignment.
     *
     * @return The alignment (never {@code null}).
     *
     * @see #setBackgroundImageAlignment(RectangleAlignment)
     */
    public RectangleAlignment getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    /**
     * Sets the background alignment and sends a change notification to all
     * registered listeners.
     *
     * @param alignment  the alignment ({@code null} not permitted).
     *
     * @see #getBackgroundImageAlignment()
     */
    public void setBackgroundImageAlignment(RectangleAlignment alignment) {
        Args.nullNotPermitted(alignment, "alignment");
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChartChanged();
        }
    }

    /**
     * Returns the alpha-transparency for the chart's background image.
     *
     * @return The alpha-transparency.
     *
     * @see #setBackgroundImageAlpha(float)
     */
    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    /**
     * Sets the alpha-transparency for the chart's background image.
     * Registered listeners are notified that the chart has been changed.
     *
     * @param alpha  the alpha value.
     *
     * @see #getBackgroundImageAlpha()
     */
    public void setBackgroundImageAlpha(float alpha) {
        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChartChanged();
        }
    }

    /**
     * Returns a flag that controls whether change events are sent to
     * registered listeners.
     *
     * @return A boolean.
     *
     * @see #setNotify(boolean)
     */
    public boolean isNotify() {
        return this.notify;
    }

    /**
     * Sets a flag that controls whether listeners receive
     * {@link ChartChangeEvent} notifications.
     *
     * @param notify  a boolean.
     *
     * @see #isNotify()
     */
    public void setNotify(boolean notify) {
        this.notify = notify;
        // if the flag is being set to true, there may be queued up changes...
        if (notify) {
            notifyListeners(new ChartChangeEvent(this));
        }
    }

    @Override
    public void receive(ChartElementVisitor visitor) {
        this.title.receive(visitor);
        this.subtitles.forEach(subtitle -> {
            subtitle.receive(visitor);
        });
        this.plot.receive(visitor);
        visitor.visit(this);
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the chart should be drawn.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null, null);
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).  This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the chart should be drawn.
     * @param info  records info about the drawing (null means collect no info).
     */
    public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
        draw(g2, area, null, info);
    }

    /**
     * Draws the chart on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * This method is the focus of the entire JFreeChart library.
     *
     * @param g2  the graphics device.
     * @param chartArea  the area within which the chart should be drawn.
     * @param anchor  the anchor point (in Java2D space) for the chart
     *                ({@code null} permitted).
     * @param info  records info about the drawing (null means collect no info).
     */
    public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
        notifyListeners(new ChartProgressEvent(this, this, ChartProgressEventType.DRAWING_STARTED, 0));
        if (this.elementHinting) {
            Map<String, String> m = new HashMap<>();
            if (this.id != null) {
                m.put("id", this.id);
            }
            m.put("ref", "JFREECHART_TOP_LEVEL");
            g2.setRenderingHint(ChartHints.KEY_BEGIN_ELEMENT, m);
        }
        EntityCollection entities = null;
        // record the chart area, if info is requested...
        if (info != null) {
            info.clear();
            info.setChartArea(chartArea);
            entities = info.getEntityCollection();
        }
        if (entities != null) {
            entities.add(new JFreeChartEntity((Rectangle2D) chartArea.clone(), this));
        }
        // ensure no drawing occurs outside chart area...
        Shape savedClip = g2.getClip();
        g2.clip(chartArea);
        g2.addRenderingHints(this.renderingHints);
        // draw the chart background...
        if (this.backgroundPaint != null) {
            g2.setPaint(this.backgroundPaint);
            g2.fill(chartArea);
        }
        if (this.backgroundImage != null) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.backgroundImageAlpha));
            Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null));
            this.backgroundImageAlignment.align(dest, chartArea);
            g2.drawImage(this.backgroundImage, (int) dest.getX(), (int) dest.getY(), (int) dest.getWidth(), (int) dest.getHeight(), null);
            g2.setComposite(originalComposite);
        }
        if (isBorderVisible()) {
            Paint paint = getBorderPaint();
            Stroke stroke = getBorderStroke();
            if (paint != null && stroke != null) {
                Rectangle2D borderArea = new Rectangle2D.Double(chartArea.getX(), chartArea.getY(), chartArea.getWidth() - 1.0, chartArea.getHeight() - 1.0);
                g2.setPaint(paint);
                g2.setStroke(stroke);
                g2.draw(borderArea);
            }
        }
        // draw the title and subtitles...
        Rectangle2D nonTitleArea = new Rectangle2D.Double();
        nonTitleArea.setRect(chartArea);
        this.padding.trim(nonTitleArea);
        if (this.title != null && this.title.isVisible()) {
            EntityCollection e = drawTitle(this.title, g2, nonTitleArea, (entities != null));
            if (e != null && entities != null) {
                entities.addAll(e);
            }
        }
        for (Title currentTitle : this.subtitles) {
            if (currentTitle.isVisible()) {
                EntityCollection e = drawTitle(currentTitle, g2, nonTitleArea, (entities != null));
                if (e != null && entities != null) {
                    entities.addAll(e);
                }
            }
        }
        Rectangle2D plotArea = nonTitleArea;
        // draw the plot (axes and data visualisation)
        PlotRenderingInfo plotInfo = null;
        if (info != null) {
            plotInfo = info.getPlotInfo();
        }
        this.plot.draw(g2, plotArea, anchor, null, plotInfo);
        g2.setClip(savedClip);
        if (this.elementHinting) {
            g2.setRenderingHint(ChartHints.KEY_END_ELEMENT, Boolean.TRUE);
        }
        notifyListeners(new ChartProgressEvent(this, this, ChartProgressEventType.DRAWING_FINISHED, 100));
    }

    /**
     * Creates a rectangle that is aligned to the frame.
     *
     * @param dimensions  the dimensions for the rectangle.
     * @param frame  the frame to align to.
     * @param hAlign  the horizontal alignment ({@code null} not permitted).
     * @param vAlign  the vertical alignment ({@code null} not permitted).
     *
     * @return A rectangle.
     */
    private Rectangle2D createAlignedRectangle2D(Size2D dimensions, Rectangle2D frame, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
        Args.nullNotPermitted(hAlign, "hAlign");
        Args.nullNotPermitted(vAlign, "vAlign");
        double x = Double.NaN;
        double y = Double.NaN;
        switch(hAlign) {
            case LEFT:
                x = frame.getX();
                break;
            case CENTER:
                x = frame.getCenterX() - (dimensions.width / 2.0);
                break;
            case RIGHT:
                x = frame.getMaxX() - dimensions.width;
                break;
            default:
                throw new IllegalStateException("Unexpected enum value " + hAlign);
        }
        switch(vAlign) {
            case TOP:
                y = frame.getY();
                break;
            case CENTER:
                y = frame.getCenterY() - (dimensions.height / 2.0);
                break;
            case BOTTOM:
                y = frame.getMaxY() - dimensions.height;
                break;
            default:
                throw new IllegalStateException("Unexpected enum value " + hAlign);
        }
        return new Rectangle2D.Double(x, y, dimensions.width, dimensions.height);
    }

    /**
     * Draws a title.  The title should be drawn at the top, bottom, left or
     * right of the specified area, and the area should be updated to reflect
     * the amount of space used by the title.
     *
     * @param t  the title ({@code null} not permitted).
     * @param g2  the graphics device ({@code null} not permitted).
     * @param area  the chart area, excluding any existing titles
     *              ({@code null} not permitted).
     * @param entities  a flag that controls whether an entity
     *                  collection is returned for the title.
     *
     * @return An entity collection for the title (possibly {@code null}).
     */
    protected EntityCollection drawTitle(Title t, Graphics2D g2, Rectangle2D area, boolean entities) {
        Args.nullNotPermitted(t, "t");
        Args.nullNotPermitted(area, "area");
        Rectangle2D titleArea;
        RectangleEdge position = t.getPosition();
        double ww = area.getWidth();
        if (ww <= 0.0) {
            return null;
        }
        double hh = area.getHeight();
        if (hh <= 0.0) {
            return null;
        }
        RectangleConstraint constraint = new RectangleConstraint(ww, new Range(0.0, ww), LengthConstraintType.RANGE, hh, new Range(0.0, hh), LengthConstraintType.RANGE);
        Object retValue = null;
        BlockParams p = new BlockParams();
        p.setGenerateEntities(entities);
        switch(position) {
            case TOP:
                {
                    Size2D size = t.arrange(g2, constraint);
                    titleArea = createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.TOP);
                    retValue = t.draw(g2, titleArea, p);
                    area.setRect(area.getX(), Math.min(area.getY() + size.height, area.getMaxY()), area.getWidth(), Math.max(area.getHeight() - size.height, 0));
                    break;
                }
            case BOTTOM:
                {
                    Size2D size = t.arrange(g2, constraint);
                    titleArea = createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
                    retValue = t.draw(g2, titleArea, p);
                    area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);
                    break;
                }
            case RIGHT:
                {
                    Size2D size = t.arrange(g2, constraint);
                    titleArea = createAlignedRectangle2D(size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment());
                    retValue = t.draw(g2, titleArea, p);
                    area.setRect(area.getX(), area.getY(), area.getWidth() - size.width, area.getHeight());
                    break;
                }
            case LEFT:
                {
                    Size2D size = t.arrange(g2, constraint);
                    titleArea = createAlignedRectangle2D(size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment());
                    retValue = t.draw(g2, titleArea, p);
                    area.setRect(area.getX() + size.width, area.getY(), area.getWidth() - size.width, area.getHeight());
                    break;
                }
            default:
                {
                    throw new RuntimeException("Unrecognised title position.");
                }
        }
        EntityCollection result = null;
        if (retValue instanceof EntityBlockResult) {
            EntityBlockResult ebr = (EntityBlockResult) retValue;
            result = ebr.getEntityCollection();
        }
        return result;
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height) {
        return createBufferedImage(width, height, null);
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     * @param info  carries back chart state information ({@code null}
     *              permitted).
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {
        return createBufferedImage(width, height, BufferedImage.TYPE_INT_ARGB, info);
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param width  the width.
     * @param height  the height.
     * @param imageType  the image type.
     * @param info  carries back chart state information ({@code null}
     *              permitted).
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int width, int height, int imageType, ChartRenderingInfo info) {
        BufferedImage image = new BufferedImage(width, height, imageType);
        Graphics2D g2 = image.createGraphics();
        draw(g2, new Rectangle2D.Double(0, 0, width, height), null, info);
        g2.dispose();
        return image;
    }

    /**
     * Creates and returns a buffered image into which the chart has been drawn.
     *
     * @param imageWidth  the image width.
     * @param imageHeight  the image height.
     * @param drawWidth  the width for drawing the chart (will be scaled to
     *                   fit image).
     * @param drawHeight  the height for drawing the chart (will be scaled to
     *                    fit image).
     * @param info  optional object for collection chart dimension and entity
     *              information.
     *
     * @return A buffered image.
     */
    public BufferedImage createBufferedImage(int imageWidth, int imageHeight, double drawWidth, double drawHeight, ChartRenderingInfo info) {
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        double scaleX = imageWidth / drawWidth;
        double scaleY = imageHeight / drawHeight;
        AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
        g2.transform(st);
        draw(g2, new Rectangle2D.Double(0, 0, drawWidth, drawHeight), null, info);
        g2.dispose();
        return image;
    }

    /**
     * Handles a 'click' on the chart.  JFreeChart is not a UI component, so
     * some other object (for example, {@link ChartPanel}) needs to capture
     * the click event and pass it onto the JFreeChart object.
     * If you are not using JFreeChart in a client application, then this
     * method is not required.
     *
     * @param x  x-coordinate of the click (in Java2D space).
     * @param y  y-coordinate of the click (in Java2D space).
     * @param info  contains chart dimension and entity information
     *              ({@code null} not permitted).
     */
    public void handleClick(int x, int y, ChartRenderingInfo info) {
        // pass the click on to the plot...
        // rely on the plot to post a plot change event and redraw the chart...
        this.plot.handleClick(x, y, info.getPlotInfo());
    }

    /**
     * Registers an object for notification of changes to the chart.
     *
     * @param listener  the listener ({@code null} not permitted).
     *
     * @see #removeChangeListener(ChartChangeListener)
     */
    public void addChangeListener(ChartChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.changeListeners.add(ChartChangeListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener  the listener ({@code null} not permitted)
     *
     * @see #addChangeListener(ChartChangeListener)
     */
    public void removeChangeListener(ChartChangeListener listener) {
        Args.nullNotPermitted(listener, "listener");
        this.changeListeners.remove(ChartChangeListener.class, listener);
    }

    /**
     * Sends a default {@link ChartChangeEvent} to all registered listeners.
     * <P>
     * This method is for convenience only.
     */
    public void fireChartChanged() {
        ChartChangeEvent event = new ChartChangeEvent(this);
        notifyListeners(event);
    }

    /**
     * Sends a {@link ChartChangeEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the
     *               notification.
     */
    protected void notifyListeners(ChartChangeEvent event) {
        if (this.notify) {
            Object[] listeners = this.changeListeners.getListenerList();
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ChartChangeListener.class) {
                    ((ChartChangeListener) listeners[i + 1]).chartChanged(event);
                }
            }
        }
    }

    /**
     * Registers an object for notification of progress events relating to the
     * chart.
     *
     * @param listener  the object being registered.
     *
     * @see #removeProgressListener(ChartProgressListener)
     */
    public void addProgressListener(ChartProgressListener listener) {
        this.progressListeners.add(ChartProgressListener.class, listener);
    }

    /**
     * Deregisters an object for notification of changes to the chart.
     *
     * @param listener  the object being deregistered.
     *
     * @see #addProgressListener(ChartProgressListener)
     */
    public void removeProgressListener(ChartProgressListener listener) {
        this.progressListeners.remove(ChartProgressListener.class, listener);
    }

    /**
     * Sends a {@link ChartProgressEvent} to all registered listeners.
     *
     * @param event  information about the event that triggered the
     *               notification.
     */
    protected void notifyListeners(ChartProgressEvent event) {
        Object[] listeners = this.progressListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChartProgressListener.class) {
                ((ChartProgressListener) listeners[i + 1]).chartProgress(event);
            }
        }
    }

    /**
     * Receives notification that a chart title has changed, and passes this
     * on to registered listeners.
     *
     * @param event  information about the chart title change.
     */
    @Override
    public void titleChanged(TitleChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Receives notification that the plot has changed, and passes this on to
     * registered listeners.
     *
     * @param event  information about the plot change.
     */
    @Override
    public void plotChanged(PlotChangeEvent event) {
        event.setChart(this);
        notifyListeners(event);
    }

    /**
     * Tests this chart for equality with another object.
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
        if (!(obj instanceof JFreeChart)) {
            return false;
        }
        JFreeChart that = (JFreeChart) obj;
        if (!this.renderingHints.equals(that.renderingHints)) {
            return false;
        }
        if (this.borderVisible != that.borderVisible) {
            return false;
        }
        if (!Objects.equals(this.borderStroke, that.borderStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.borderPaint, that.borderPaint)) {
            return false;
        }
        if (!this.padding.equals(that.padding)) {
            return false;
        }
        if (!Objects.equals(this.title, that.title)) {
            return false;
        }
        if (!Objects.equals(this.subtitles, that.subtitles)) {
            return false;
        }
        if (!Objects.equals(this.plot, that.plot)) {
            return false;
        }
        if (!PaintUtils.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
        }
        if (!Objects.equals(this.backgroundImage, that.backgroundImage)) {
            return false;
        }
        if (this.backgroundImageAlignment != that.backgroundImageAlignment) {
            return false;
        }
        if (this.backgroundImageAlpha != that.backgroundImageAlpha) {
            return false;
        }
        if (this.notify != that.notify) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.renderingHints);
        hash = 59 * hash + (this.borderVisible ? 1 : 0);
        hash = 59 * hash + Objects.hashCode(this.borderStroke);
        hash = 59 * hash + Objects.hashCode(this.borderPaint);
        hash = 59 * hash + Objects.hashCode(this.padding);
        hash = 59 * hash + Objects.hashCode(this.title);
        hash = 59 * hash + Objects.hashCode(this.subtitles);
        hash = 59 * hash + Objects.hashCode(this.plot);
        hash = 59 * hash + Objects.hashCode(this.backgroundPaint);
        hash = 59 * hash + Objects.hashCode(this.backgroundImage);
        hash = 59 * hash + Objects.hashCode(this.backgroundImageAlignment);
        hash = 59 * hash + Float.floatToIntBits(this.backgroundImageAlpha);
        hash = 59 * hash + (this.notify ? 1 : 0);
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
        SerialUtils.writeStroke(this.borderStroke, stream);
        SerialUtils.writePaint(this.borderPaint, stream);
        SerialUtils.writePaint(this.backgroundPaint, stream);
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
        this.borderStroke = SerialUtils.readStroke(stream);
        this.borderPaint = SerialUtils.readPaint(stream);
        this.backgroundPaint = SerialUtils.readPaint(stream);
        this.progressListeners = new EventListenerList();
        this.changeListeners = new EventListenerList();
        this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        // register as a listener with sub-components...
        if (this.title != null) {
            this.title.addChangeListener(this);
        }
        for (int i = 0; i < getSubtitleCount(); i++) {
            getSubtitle(i).addChangeListener(this);
        }
        this.plot.addChangeListener(this);
    }

    /**
     * Clones the object, and takes care of listeners.
     * Note: caller shall register its own listeners on cloned graph.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the chart is not cloneable.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        JFreeChart chart = (JFreeChart) super.clone();
        chart.renderingHints = (RenderingHints) this.renderingHints.clone();
        // private boolean borderVisible;
        // private transient Stroke borderStroke;
        // private transient Paint borderPaint;
        if (this.title != null) {
            chart.title = (TextTitle) this.title.clone();
            chart.title.addChangeListener(chart);
        }
        chart.subtitles = new ArrayList<>();
        for (int i = 0; i < getSubtitleCount(); i++) {
            Title subtitle = (Title) getSubtitle(i).clone();
            chart.subtitles.add(subtitle);
            subtitle.addChangeListener(chart);
        }
        if (this.plot != null) {
            chart.plot = (Plot) this.plot.clone();
            chart.plot.addChangeListener(chart);
        }
        chart.progressListeners = new EventListenerList();
        chart.changeListeners = new EventListenerList();
        return chart;
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
 * ---------------
 * ChartUtils.java
 * ---------------
 * (C) Copyright 2001-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Wolfgang Irler;
 *                   Richard Atkinson;
 *                   Xavier Poinsard;
 *
 */
/**
 * A collection of utility methods for JFreeChart.  Includes methods for
 * converting charts to image formats (PNG and JPEG) plus creating simple HTML
 * image maps.
 *
 * @see ImageMapUtils
 */
public abstract class ChartUtils {

    private ChartUtils() {
        // no requirement to instantiate
    }

    /**
     * Returns {@code true} if JFreeSVG is on the classpath, and
     * {@code false} otherwise.  The JFreeSVG library can be found at
     * <a href="https://www.jfree.org/jfreesvg/">https://www.jfree.org/jfreesvg/</a>
     *
     * @return A boolean.
     *
     * @since 2.0.0
     */
    public static boolean isJFreeSVGAvailable() {
        Class<?> svgGraphics2DClass = null;
        try {
            svgGraphics2DClass = Class.forName("org.jfree.svg.SVGGraphics2D");
        } catch (ClassNotFoundException e) {
            // svgGraphics2DClass will be null so the function will return false
        }
        return svgGraphics2DClass != null;
    }

    /**
     * Returns {@code true} if OrsonPDF is on the classpath, and
     * {@code false} otherwise.  The OrsonPDF library can be found at
     * http://www.object-refinery.com/orsonpdf/
     *
     * @return A boolean.
     *
     * @since 2.0.0
     */
    public static boolean isOrsonPDFAvailable() {
        Class<?> pdfDocumentClass = null;
        try {
            pdfDocumentClass = Class.forName("com.orsonpdf.PDFDocument");
        } catch (ClassNotFoundException e) {
            // pdfDocument class will be null so the function will return false
        }
        return (pdfDocumentClass != null);
    }

    /**
     * Applies the current theme to the specified chart.  This method is
     * provided for convenience, the theme itself is stored in the
     * {@link ChartFactory} class.
     *
     * @param chart  the chart ({@code null} not permitted).
     */
    public static void applyCurrentTheme(JFreeChart chart) {
        ChartFactory.getChartTheme().apply(chart);
    }

    /**
     * Writes a chart to an output stream in PNG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height) throws IOException {
        // defer argument checking...
        writeChartAsPNG(out, chart, width, height, null);
    }

    /**
     * Writes a chart to an output stream in PNG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param encodeAlpha  encode alpha?
     * @param compression  the compression level (0-9).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, boolean encodeAlpha, int compression) throws IOException {
        // defer argument checking...
        ChartUtils.writeChartAsPNG(out, chart, width, height, null, encodeAlpha, compression);
    }

    /**
     * Writes a chart to an output stream in PNG format.  This method allows
     * you to pass in a {@link ChartRenderingInfo} object, to collect
     * information about the chart dimensions/entities.  You will need this
     * info if you want to create an HTML image map.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        Args.nullNotPermitted(chart, "chart");
        BufferedImage bufferedImage = chart.createBufferedImage(width, height, info);
        EncoderUtil.writeBufferedImage(bufferedImage, ImageFormat.PNG, out);
    }

    /**
     * Writes a chart to an output stream in PNG format.  This method allows
     * you to pass in a {@link ChartRenderingInfo} object, to collect
     * information about the chart dimensions/entities.  You will need this
     * info if you want to create an HTML image map.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  carries back chart rendering info ({@code null}
     *              permitted).
     * @param encodeAlpha  encode alpha?
     * @param compression  the PNG compression level (0-9).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, ChartRenderingInfo info, boolean encodeAlpha, int compression) throws IOException {
        Args.nullNotPermitted(out, "out");
        Args.nullNotPermitted(chart, "chart");
        BufferedImage chartImage = chart.createBufferedImage(width, height, BufferedImage.TYPE_INT_ARGB, info);
        ChartUtils.writeBufferedImageAsPNG(out, chartImage, encodeAlpha, compression);
    }

    /**
     * Writes a scaled version of a chart to an output stream in PNG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the unscaled chart width.
     * @param height  the unscaled chart height.
     * @param widthScaleFactor  the horizontal scale factor.
     * @param heightScaleFactor  the vertical scale factor.
     *
     * @throws IOException if there are any I/O problems.
     */
    public static void writeScaledChartAsPNG(OutputStream out, JFreeChart chart, int width, int height, int widthScaleFactor, int heightScaleFactor) throws IOException {
        Args.nullNotPermitted(out, "out");
        Args.nullNotPermitted(chart, "chart");
        double desiredWidth = width * widthScaleFactor;
        double desiredHeight = height * heightScaleFactor;
        double defaultWidth = width;
        double defaultHeight = height;
        boolean scale = false;
        // get desired width and height from somewhere then...
        if ((widthScaleFactor != 1) || (heightScaleFactor != 1)) {
            scale = true;
        }
        double scaleX = desiredWidth / defaultWidth;
        double scaleY = desiredHeight / defaultHeight;
        BufferedImage image = new BufferedImage((int) desiredWidth, (int) desiredHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        if (scale) {
            AffineTransform saved = g2.getTransform();
            g2.transform(AffineTransform.getScaleInstance(scaleX, scaleY));
            chart.draw(g2, new Rectangle2D.Double(0, 0, defaultWidth, defaultHeight), null, null);
            g2.setTransform(saved);
            g2.dispose();
        } else {
            chart.draw(g2, new Rectangle2D.Double(0, 0, defaultWidth, defaultHeight), null, null);
        }
        out.write(encodeAsPNG(image));
    }

    /**
     * Saves a chart to the specified file in PNG format.
     *
     * @param file  the file name ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height) throws IOException {
        // defer argument checking...
        saveChartAsPNG(file, chart, width, height, null);
    }

    /**
     * Saves a chart to a file in PNG format.  This method allows you to pass
     * in a {@link ChartRenderingInfo} object, to collect information about the
     * chart dimensions/entities.  You will need this info if you want to
     * create an HTML image map.
     *
     * @param file  the file ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        Args.nullNotPermitted(file, "file");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            ChartUtils.writeChartAsPNG(out, chart, width, height, info);
        }
    }

    /**
     * Saves a chart to a file in PNG format.  This method allows you to pass
     * in a {@link ChartRenderingInfo} object, to collect information about the
     * chart dimensions/entities.  You will need this info if you want to
     * create an HTML image map.
     *
     * @param file  the file ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     * @param encodeAlpha  encode alpha?
     * @param compression  the PNG compression level (0-9).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsPNG(File file, JFreeChart chart, int width, int height, ChartRenderingInfo info, boolean encodeAlpha, int compression) throws IOException {
        Args.nullNotPermitted(file, "file");
        Args.nullNotPermitted(chart, "chart");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            writeChartAsPNG(out, chart, width, height, info, encodeAlpha, compression);
        }
    }

    /**
     * Writes a chart to an output stream in JPEG format.  Please note that
     * JPEG is a poor format for chart images, use PNG if possible.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out, JFreeChart chart, int width, int height) throws IOException {
        // defer argument checking...
        writeChartAsJPEG(out, chart, width, height, null);
    }

    /**
     * Writes a chart to an output stream in JPEG format.  Please note that
     * JPEG is a poor format for chart images, use PNG if possible.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param quality  the quality setting.
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out, float quality, JFreeChart chart, int width, int height) throws IOException {
        // defer argument checking...
        ChartUtils.writeChartAsJPEG(out, quality, chart, width, height, null);
    }

    /**
     * Writes a chart to an output stream in JPEG format. This method allows
     * you to pass in a {@link ChartRenderingInfo} object, to collect
     * information about the chart dimensions/entities.  You will need this
     * info if you want to create an HTML image map.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        Args.nullNotPermitted(out, "out");
        Args.nullNotPermitted(chart, "chart");
        BufferedImage image = chart.createBufferedImage(width, height, BufferedImage.TYPE_INT_RGB, info);
        EncoderUtil.writeBufferedImage(image, ImageFormat.JPEG, out);
    }

    /**
     * Writes a chart to an output stream in JPEG format.  This method allows
     * you to pass in a {@link ChartRenderingInfo} object, to collect
     * information about the chart dimensions/entities.  You will need this
     * info if you want to create an HTML image map.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param quality  the output quality (0.0f to 1.0f).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeChartAsJPEG(OutputStream out, float quality, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        Args.nullNotPermitted(out, "out");
        Args.nullNotPermitted(chart, "chart");
        BufferedImage image = chart.createBufferedImage(width, height, BufferedImage.TYPE_INT_RGB, info);
        EncoderUtil.writeBufferedImage(image, ImageFormat.JPEG, out, quality);
    }

    /**
     * Saves a chart to a file in JPEG format.
     *
     * @param file  the file ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file, JFreeChart chart, int width, int height) throws IOException {
        // defer argument checking...
        saveChartAsJPEG(file, chart, width, height, null);
    }

    /**
     * Saves a chart to a file in JPEG format.
     *
     * @param file  the file ({@code null} not permitted).
     * @param quality  the JPEG quality setting.
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file, float quality, JFreeChart chart, int width, int height) throws IOException {
        // defer argument checking...
        saveChartAsJPEG(file, quality, chart, width, height, null);
    }

    /**
     * Saves a chart to a file in JPEG format.  This method allows you to pass
     * in a {@link ChartRenderingInfo} object, to collect information about the
     * chart dimensions/entities.  You will need this info if you want to
     * create an HTML image map.
     *
     * @param file  the file name ({@code null} not permitted).
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        Args.nullNotPermitted(file, "file");
        Args.nullNotPermitted(chart, "chart");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            writeChartAsJPEG(out, chart, width, height, info);
        }
    }

    /**
     * Saves a chart to a file in JPEG format.  This method allows you to pass
     * in a {@link ChartRenderingInfo} object, to collect information about the
     * chart dimensions/entities.  You will need this info if you want to
     * create an HTML image map.
     *
     * @param file  the file name ({@code null} not permitted).
     * @param quality  the quality setting.
     * @param chart  the chart ({@code null} not permitted).
     * @param width  the image width.
     * @param height  the image height.
     * @param info  the chart rendering info ({@code null} permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void saveChartAsJPEG(File file, float quality, JFreeChart chart, int width, int height, ChartRenderingInfo info) throws IOException {
        Args.nullNotPermitted(file, "file");
        Args.nullNotPermitted(chart, "chart");
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            writeChartAsJPEG(out, quality, chart, width, height, info);
        }
    }

    /**
     * Writes a {@link BufferedImage} to an output stream in JPEG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param image  the image ({@code null} not permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsJPEG(OutputStream out, BufferedImage image) throws IOException {
        // defer argument checking...
        writeBufferedImageAsJPEG(out, 0.75f, image);
    }

    /**
     * Writes a {@link BufferedImage} to an output stream in JPEG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param quality  the image quality (0.0f to 1.0f).
     * @param image  the image ({@code null} not permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsJPEG(OutputStream out, float quality, BufferedImage image) throws IOException {
        EncoderUtil.writeBufferedImage(image, ImageFormat.JPEG, out, quality);
    }

    /**
     * Writes a {@link BufferedImage} to an output stream in PNG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param image  the image ({@code null} not permitted).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage image) throws IOException {
        EncoderUtil.writeBufferedImage(image, ImageFormat.PNG, out);
    }

    /**
     * Writes a {@link BufferedImage} to an output stream in PNG format.
     *
     * @param out  the output stream ({@code null} not permitted).
     * @param image  the image ({@code null} not permitted).
     * @param encodeAlpha  encode alpha?
     * @param compression  the compression level (0-9).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeBufferedImageAsPNG(OutputStream out, BufferedImage image, boolean encodeAlpha, int compression) throws IOException {
        EncoderUtil.writeBufferedImage(image, ImageFormat.PNG, out, compression, encodeAlpha);
    }

    /**
     * Encodes a {@link BufferedImage} to PNG format.
     *
     * @param image  the image ({@code null} not permitted).
     *
     * @return A byte array in PNG format.
     *
     * @throws IOException if there is an I/O problem.
     */
    public static byte[] encodeAsPNG(BufferedImage image) throws IOException {
        return EncoderUtil.encode(image, ImageFormat.PNG);
    }

    /**
     * Encodes a {@link BufferedImage} to PNG format.
     *
     * @param image  the image ({@code null} not permitted).
     * @param encodeAlpha  encode alpha?
     * @param compression  the PNG compression level (0-9).
     *
     * @return The byte array in PNG format.
     *
     * @throws IOException if there is an I/O problem.
     */
    public static byte[] encodeAsPNG(BufferedImage image, boolean encodeAlpha, int compression) throws IOException {
        return EncoderUtil.encode(image, ImageFormat.PNG, compression, encodeAlpha);
    }

    /**
     * Writes an image map to an output stream.
     *
     * @param writer  the writer ({@code null} not permitted).
     * @param name  the map name ({@code null} not permitted).
     * @param info  the chart rendering info ({@code null} not permitted).
     * @param useOverLibForToolTips  whether to use OverLIB for tooltips
     *                               (http://www.bosrup.com/web/overlib/).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, boolean useOverLibForToolTips) throws IOException {
        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator;
        if (useOverLibForToolTips) {
            toolTipTagFragmentGenerator = new OverLIBToolTipTagFragmentGenerator();
        } else {
            toolTipTagFragmentGenerator = new StandardToolTipTagFragmentGenerator();
        }
        ImageMapUtils.writeImageMap(writer, name, info, toolTipTagFragmentGenerator, new StandardURLTagFragmentGenerator());
    }

    /**
     * Writes an image map to the specified writer.
     *
     * @param writer  the writer ({@code null} not permitted).
     * @param name  the map name ({@code null} not permitted).
     * @param info  the chart rendering info ({@code null} not permitted).
     * @param toolTipTagFragmentGenerator  a generator for the HTML fragment
     *     that will contain the tooltip text ({@code null} not permitted
     *     if {@code info} contains tooltip information).
     * @param urlTagFragmentGenerator  a generator for the HTML fragment that
     *     will contain the URL reference ({@code null} not permitted if
     *     {@code info} contains URLs).
     *
     * @throws IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) throws IOException {
        writer.println(ImageMapUtils.getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator));
    }

    /**
     * Creates an HTML image map.  This method maps to
     * {@link ImageMapUtils#getImageMap(String, ChartRenderingInfo,
     * ToolTipTagFragmentGenerator, URLTagFragmentGenerator)}, using default
     * generators.
     *
     * @param name  the map name ({@code null} not permitted).
     * @param info  the chart rendering info ({@code null} not permitted).
     *
     * @return The map tag.
     */
    public static String getImageMap(String name, ChartRenderingInfo info) {
        return ImageMapUtils.getImageMap(name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }

    /**
     * Creates an HTML image map.  This method maps directly to
     * {@link ImageMapUtils#getImageMap(String, ChartRenderingInfo,
     * ToolTipTagFragmentGenerator, URLTagFragmentGenerator)}.
     *
     * @param name  the map name ({@code null} not permitted).
     * @param info  the chart rendering info ({@code null} not permitted).
     * @param toolTipTagFragmentGenerator  a generator for the HTML fragment
     *     that will contain the tooltip text ({@code null} not permitted
     *     if {@code info} contains tooltip information).
     * @param urlTagFragmentGenerator  a generator for the HTML fragment that
     *     will contain the URL reference ({@code null} not permitted if
     *     {@code info} contains URLs).
     *
     * @return The map tag.
     */
    public static String getImageMap(String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) {
        return ImageMapUtils.getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator);
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
 * MeterNeedle.java
 * ----------------
 * (C) Copyright 2002-present, by the Australian Antarctic Division and
 *                          Contributors.
 *
 * Original Author:  Bryan Scott (for the Australian Antarctic Division);
 * Contributor(s):   David Gilbert;
 *                   Nicolas Brodu (for Astrium and EADS Corporate Research
 *                   Center);
 *
 */
/**
 * The base class used to represent the needle on a
 * {@link org.jfree.chart.plot.compass.CompassPlot}.
 */
public abstract class MeterNeedle implements Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 5203064851510951052L;

    /**
     * The outline paint.
     */
    private transient Paint outlinePaint = Color.BLACK;

    /**
     * The outline stroke.
     */
    private transient Stroke outlineStroke = new BasicStroke(2);

    /**
     * The fill paint.
     */
    private transient Paint fillPaint = null;

    /**
     * The highlight paint.
     */
    private transient Paint highlightPaint = null;

    /**
     * The size.
     */
    private int size = 5;

    /**
     * Scalar to apply to locate the rotation x point.
     */
    private double rotateX = 0.5;

    /**
     * Scalar to apply to locate the rotation y point.
     */
    private double rotateY = 0.5;

    /**
     * A transform.
     */
    protected static AffineTransform transform = new AffineTransform();

    /**
     * Creates a new needle.
     */
    public MeterNeedle() {
        this(null, null, null);
    }

    /**
     * Creates a new needle.
     *
     * @param outline  the outline paint ({@code null} permitted).
     * @param fill  the fill paint ({@code null} permitted).
     * @param highlight  the highlight paint ({@code null} permitted).
     */
    public MeterNeedle(Paint outline, Paint fill, Paint highlight) {
        this.fillPaint = fill;
        this.highlightPaint = highlight;
        this.outlinePaint = outline;
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint.
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint.
     *
     * @param p  the new paint.
     */
    public void setOutlinePaint(Paint p) {
        if (p != null) {
            this.outlinePaint = p;
        }
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke.
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke.
     *
     * @param s  the new stroke.
     */
    public void setOutlineStroke(Stroke s) {
        if (s != null) {
            this.outlineStroke = s;
        }
    }

    /**
     * Returns the fill paint.
     *
     * @return The fill paint.
     */
    public Paint getFillPaint() {
        return this.fillPaint;
    }

    /**
     * Sets the fill paint.
     *
     * @param p  the fill paint.
     */
    public void setFillPaint(Paint p) {
        if (p != null) {
            this.fillPaint = p;
        }
    }

    /**
     * Returns the highlight paint.
     *
     * @return The highlight paint.
     */
    public Paint getHighlightPaint() {
        return this.highlightPaint;
    }

    /**
     * Sets the highlight paint.
     *
     * @param p  the highlight paint.
     */
    public void setHighlightPaint(Paint p) {
        if (p != null) {
            this.highlightPaint = p;
        }
    }

    /**
     * Returns the scalar used for determining the rotation x value.
     *
     * @return The x rotate scalar.
     */
    public double getRotateX() {
        return this.rotateX;
    }

    /**
     * Sets the rotateX value.
     *
     * @param x  the new value.
     */
    public void setRotateX(double x) {
        this.rotateX = x;
    }

    /**
     * Sets the rotateY value.
     *
     * @param y  the new value.
     */
    public void setRotateY(double y) {
        this.rotateY = y;
    }

    /**
     * Returns the scalar used for determining the rotation y value.
     *
     * @return The y rotate scalar.
     */
    public double getRotateY() {
        return this.rotateY;
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea) {
        draw(g2, plotArea, 0);
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param angle  the angle.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, double angle) {
        Point2D.Double pt = new Point2D.Double();
        pt.setLocation(plotArea.getMinX() + this.rotateX * plotArea.getWidth(), plotArea.getMinY() + this.rotateY * plotArea.getHeight());
        draw(g2, plotArea, pt, angle);
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    public void draw(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle) {
        Paint savePaint = g2.getColor();
        Stroke saveStroke = g2.getStroke();
        drawNeedle(g2, plotArea, rotate, Math.toRadians(angle));
        g2.setStroke(saveStroke);
        g2.setPaint(savePaint);
    }

    /**
     * Draws the needle.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param rotate  the rotation point.
     * @param angle  the angle.
     */
    protected abstract void drawNeedle(Graphics2D g2, Rectangle2D plotArea, Point2D rotate, double angle);

    /**
     * Displays a shape.
     *
     * @param g2  the graphics device.
     * @param shape  the shape.
     */
    protected void defaultDisplay(Graphics2D g2, Shape shape) {
        if (this.fillPaint != null) {
            g2.setPaint(this.fillPaint);
            g2.fill(shape);
        }
        if (this.outlinePaint != null) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            g2.draw(shape);
        }
    }

    /**
     * Returns the size.
     *
     * @return The size.
     */
    public int getSize() {
        return this.size;
    }

    /**
     * Sets the size.
     *
     * @param pixels  the new size.
     */
    public void setSize(int pixels) {
        this.size = pixels;
    }

    /**
     * Returns the transform.
     *
     * @return The transform.
     */
    public AffineTransform getTransform() {
        return MeterNeedle.transform;
    }

    /**
     * Tests another object for equality with this object.
     *
     * @param obj the object to test ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeterNeedle)) {
            return false;
        }
        MeterNeedle that = (MeterNeedle) obj;
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.fillPaint, that.fillPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.highlightPaint, that.highlightPaint)) {
            return false;
        }
        if (this.size != that.size) {
            return false;
        }
        if (this.rotateX != that.rotateX) {
            return false;
        }
        if (this.rotateY != that.rotateY) {
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
        int result = HashUtils.hashCode(193, this.fillPaint);
        result = HashUtils.hashCode(result, this.highlightPaint);
        result = HashUtils.hashCode(result, this.outlinePaint);
        result = HashUtils.hashCode(result, this.outlineStroke);
        result = HashUtils.hashCode(result, this.rotateX);
        result = HashUtils.hashCode(result, this.rotateY);
        result = HashUtils.hashCode(result, this.size);
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
        SerialUtils.writeStroke(this.outlineStroke, stream);
        SerialUtils.writePaint(this.outlinePaint, stream);
        SerialUtils.writePaint(this.fillPaint, stream);
        SerialUtils.writePaint(this.highlightPaint, stream);
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
        this.outlineStroke = SerialUtils.readStroke(stream);
        this.outlinePaint = SerialUtils.readPaint(stream);
        this.fillPaint = SerialUtils.readPaint(stream);
        this.highlightPaint = SerialUtils.readPaint(stream);
    }
}
