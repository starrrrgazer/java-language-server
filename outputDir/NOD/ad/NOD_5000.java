package NOD.ad;
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
 * WaferMapPlot.java
 * -----------------
 *
 * (C) Copyright 2003-2021, by Robert Redburn and Contributors.
 *
 * Original Author:  Robert Redburn;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A wafer map plot.
 */
public class WaferMapPlot extends Plot implements RendererChangeListener, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 4668320403707308155L;

    /**
     * The default grid line stroke.
     */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);

    /**
     * The default grid line paint.
     */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.LIGHT_GRAY;

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
     *  vertical = notch down
     *  horizontal = notch right
     */
    private final PlotOrientation orientation;

    /**
     * The dataset.
     */
    private WaferMapDataset dataset;

    /**
     * Object responsible for drawing the visual representation of each point
     * on the plot.
     */
    private WaferMapRenderer renderer;

    /**
     * Creates a new plot with no dataset.
     */
    public WaferMapPlot() {
        this(null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset ({@code null} permitted).
     */
    public WaferMapPlot(WaferMapDataset dataset) {
        this(dataset, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param renderer  the renderer ({@code null} permitted).
     */
    public WaferMapPlot(WaferMapDataset dataset, WaferMapRenderer renderer) {
        super();
        this.orientation = PlotOrientation.VERTICAL;
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    @Override
    public String getPlotType() {
        return ("WMAP_Plot");
    }

    /**
     * Returns the dataset
     *
     * @return The dataset (possibly {@code null}).
     */
    public WaferMapDataset getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset used by the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     */
    public void setDataset(WaferMapDataset dataset) {
        // if there is an existing dataset, remove the plot from the list of
        // change listeners...
        if (this.dataset != null) {
            this.dataset.removeChangeListener(this);
        }
        // set the new dataset, and register the chart as a change listener...
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        // send a dataset change event to self to trigger plot change event
        datasetChanged(new DatasetChangeEvent(this, dataset));
    }

    /**
     * Sets the item renderer, and notifies all listeners of a change to the
     * plot.  If the renderer is set to {@code null}, no chart will be
     * drawn.
     *
     * @param renderer  the new renderer ({@code null} permitted).
     */
    public void setRenderer(WaferMapRenderer renderer) {
        if (this.renderer != null) {
            this.renderer.removeChangeListener(this);
        }
        this.renderer = renderer;
        if (renderer != null) {
            renderer.setPlot(this);
        }
        fireChangeEvent();
    }

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        // FIXME : handle the renderer
        super.receive(visitor);
    }

    /**
     * Draws the wafermap view.
     *
     * @param g2  the graphics device.
     * @param area  the plot area.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param state  the plot state.
     * @param info  the plot rendering info.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState state, PlotRenderingInfo info) {
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
        drawChipGrid(g2, area);
        drawWaferEdge(g2, area);
    }

    /**
     * Calculates and draws the chip locations on the wafer.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    protected void drawChipGrid(Graphics2D g2, Rectangle2D plotArea) {
        Shape savedClip = g2.getClip();
        g2.setClip(getWaferEdge(plotArea));
        Rectangle2D chip = new Rectangle2D.Double();
        int xchips = 35;
        int ychips = 20;
        double space = 1d;
        if (this.dataset != null) {
            xchips = this.dataset.getMaxChipX() + 2;
            ychips = this.dataset.getMaxChipY() + 2;
            space = this.dataset.getChipSpace();
        }
        double startX = plotArea.getX();
        double startY = plotArea.getY();
        double chipWidth = 1d;
        double chipHeight = 1d;
        if (plotArea.getWidth() != plotArea.getHeight()) {
            double major, minor;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            }
            //set upperLeft point
            if (plotArea.getWidth() == minor) {
                // x is minor
                startY += (major - minor) / 2;
                chipWidth = (plotArea.getWidth() - (space * xchips - 1)) / xchips;
                chipHeight = (plotArea.getWidth() - (space * ychips - 1)) / ychips;
            } else {
                // y is minor
                startX += (major - minor) / 2;
                chipWidth = (plotArea.getHeight() - (space * xchips - 1)) / xchips;
                chipHeight = (plotArea.getHeight() - (space * ychips - 1)) / ychips;
            }
        }
        for (int x = 1; x <= xchips; x++) {
            double upperLeftX = (startX - chipWidth) + (chipWidth * x) + (space * (x - 1));
            for (int y = 1; y <= ychips; y++) {
                double upperLeftY = (startY - chipHeight) + (chipHeight * y) + (space * (y - 1));
                chip.setFrame(upperLeftX, upperLeftY, chipWidth, chipHeight);
                g2.setColor(Color.WHITE);
                if (this.dataset.getChipValue(x - 1, ychips - y - 1) != null) {
                    g2.setPaint(this.renderer.getChipColor(this.dataset.getChipValue(x - 1, ychips - y - 1)));
                }
                g2.fill(chip);
                g2.setColor(Color.LIGHT_GRAY);
                g2.draw(chip);
            }
        }
        g2.setClip(savedClip);
    }

    /**
     * Calculates the location of the waferedge.
     *
     * @param plotArea  the plot area.
     *
     * @return The wafer edge.
     */
    protected Ellipse2D getWaferEdge(Rectangle2D plotArea) {
        Ellipse2D edge = new Ellipse2D.Double();
        double diameter = plotArea.getWidth();
        double upperLeftX = plotArea.getX();
        double upperLeftY = plotArea.getY();
        //get major dimension
        if (plotArea.getWidth() != plotArea.getHeight()) {
            double major, minor;
            if (plotArea.getWidth() > plotArea.getHeight()) {
                major = plotArea.getWidth();
                minor = plotArea.getHeight();
            } else {
                major = plotArea.getHeight();
                minor = plotArea.getWidth();
            }
            //ellipse diameter is the minor dimension
            diameter = minor;
            //set upperLeft point
            if (plotArea.getWidth() == minor) {
                // x is minor
                upperLeftY = plotArea.getY() + (major - minor) / 2;
            } else {
                // y is minor
                upperLeftX = plotArea.getX() + (major - minor) / 2;
            }
        }
        edge.setFrame(upperLeftX, upperLeftY, diameter, diameter);
        return edge;
    }

    /**
     * Draws the waferedge, including the notch.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     */
    protected void drawWaferEdge(Graphics2D g2, Rectangle2D plotArea) {
        // draw the wafer
        Ellipse2D waferEdge = getWaferEdge(plotArea);
        g2.setColor(Color.BLACK);
        g2.draw(waferEdge);
        // calculate and draw the notch
        // horizontal orientation is considered notch right
        // vertical orientation is considered notch down
        Arc2D notch;
        Rectangle2D waferFrame = waferEdge.getFrame();
        double notchDiameter = waferFrame.getWidth() * 0.04;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            Rectangle2D notchFrame = new Rectangle2D.Double(waferFrame.getX() + waferFrame.getWidth() - (notchDiameter / 2), waferFrame.getY() + (waferFrame.getHeight() / 2) - (notchDiameter / 2), notchDiameter, notchDiameter);
            notch = new Arc2D.Double(notchFrame, 90d, 180d, Arc2D.OPEN);
        } else {
            Rectangle2D notchFrame = new Rectangle2D.Double(waferFrame.getX() + (waferFrame.getWidth() / 2) - (notchDiameter / 2), waferFrame.getY() + waferFrame.getHeight() - (notchDiameter / 2), notchDiameter, notchDiameter);
            notch = new Arc2D.Double(notchFrame, 0d, 180d, Arc2D.OPEN);
        }
        g2.setColor(Color.WHITE);
        g2.fill(notch);
        g2.setColor(Color.BLACK);
        g2.draw(notch);
    }

    /**
     * Return the legend items from the renderer.
     *
     * @return The legend items.
     */
    @Override
    public LegendItemCollection getLegendItems() {
        return this.renderer.getLegendCollection();
    }

    /**
     * Notifies all registered listeners of a renderer change.
     *
     * @param event  the event.
     */
    @Override
    public void rendererChanged(RendererChangeEvent event) {
        fireChangeEvent();
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
 * Plot.java
 * ---------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Sylvain Vieujot;
 *                   Jeremy Bowman;
 *                   Andreas Schneider;
 *                   Gideon Krause;
 *                   Nicolas Brodu;
 *                   Michal Krause;
 *                   Richard West, Advanced Micro Devices, Inc.;
 *                   Peter Kolb - patches 2603321, 2809117;
 * 
 */
/**
 * The base class for all plots in JFreeChart.  The {@link JFreeChart} class
 * delegates the drawing of axes and data to the plot.  This base class
 * provides facilities common to most plot types.
 */
public abstract class Plot implements ChartElement, AxisChangeListener, DatasetChangeListener, AnnotationChangeListener, MarkerChangeListener, LegendItemSource, PublicCloneable, Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8831571430103671324L;

    /**
     * Useful constant representing zero.
     */
    public static final Number ZERO = 0;

    /**
     * The default insets.
     */
    public static final RectangleInsets DEFAULT_INSETS = new RectangleInsets(4.0, 8.0, 4.0, 8.0);

    /**
     * The default outline stroke.
     */
    public static final Stroke DEFAULT_OUTLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    /**
     * The default outline color.
     */
    public static final Paint DEFAULT_OUTLINE_PAINT = Color.GRAY;

    /**
     * The default foreground alpha transparency.
     */
    public static final float DEFAULT_FOREGROUND_ALPHA = 1.0f;

    /**
     * The default background alpha transparency.
     */
    public static final float DEFAULT_BACKGROUND_ALPHA = 1.0f;

    /**
     * The default background color.
     */
    public static final Paint DEFAULT_BACKGROUND_PAINT = Color.WHITE;

    /**
     * The minimum width at which the plot should be drawn.
     */
    public static final int MINIMUM_WIDTH_TO_DRAW = 10;

    /**
     * The minimum height at which the plot should be drawn.
     */
    public static final int MINIMUM_HEIGHT_TO_DRAW = 10;

    /**
     * A default box shape for legend items.
     */
    public static final Shape DEFAULT_LEGEND_ITEM_BOX = new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0);

    /**
     * A default circle shape for legend items.
     */
    public static final Shape DEFAULT_LEGEND_ITEM_CIRCLE = new Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0);

    /**
     * The chart that the plot is assigned to.  It can be {@code null} if the
     * plot is not assigned to a chart yet, or if the plot is a subplot of a
     * another plot.
     */
    private JFreeChart chart;

    /**
     * The parent plot ({@code null} if this is the root plot).
     */
    private Plot parent;

    /**
     * The message to display if no data is available.
     */
    private String noDataMessage;

    /**
     * The font used to display the 'no data' message.
     */
    private Font noDataMessageFont;

    /**
     * The paint used to draw the 'no data' message.
     */
    private transient Paint noDataMessagePaint;

    /**
     * Amount of blank space around the plot area.
     */
    private RectangleInsets insets;

    /**
     * A flag that controls whether the plot outline is drawn.
     */
    private boolean outlineVisible;

    /**
     * The Stroke used to draw an outline around the plot.
     */
    private transient Stroke outlineStroke;

    /**
     * The Paint used to draw an outline around the plot.
     */
    private transient Paint outlinePaint;

    /**
     * An optional color used to fill the plot background.
     */
    private transient Paint backgroundPaint;

    /**
     * An optional image for the plot background.
     */
    // not currently serialized
    private transient Image backgroundImage;

    /**
     * The alignment for the background image.
     */
    private RectangleAlignment backgroundImageAlignment = RectangleAlignment.FILL;

    /**
     * The alpha value used to draw the background image.
     */
    private float backgroundImageAlpha = 0.5f;

    /**
     * The alpha-transparency for the plot.
     */
    private float foregroundAlpha;

    /**
     * The alpha transparency for the background paint.
     */
    private float backgroundAlpha;

    /**
     * The drawing supplier.
     */
    private DrawingSupplier drawingSupplier;

    /**
     * Storage for registered change listeners.
     */
    private transient EventListenerList listenerList;

    /**
     * A flag that controls whether the plot will notify listeners
     * of changes (defaults to true, but sometimes it is useful to disable
     * this).
     */
    private boolean notify;

    /**
     * Creates a new plot.
     */
    protected Plot() {
        this.chart = null;
        this.parent = null;
        this.insets = DEFAULT_INSETS;
        this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
        this.backgroundAlpha = DEFAULT_BACKGROUND_ALPHA;
        this.backgroundImage = null;
        this.outlineVisible = true;
        this.outlineStroke = DEFAULT_OUTLINE_STROKE;
        this.outlinePaint = DEFAULT_OUTLINE_PAINT;
        this.foregroundAlpha = DEFAULT_FOREGROUND_ALPHA;
        this.noDataMessage = null;
        this.noDataMessageFont = new Font("SansSerif", Font.PLAIN, 12);
        this.noDataMessagePaint = Color.BLACK;
        this.drawingSupplier = new DefaultDrawingSupplier();
        this.notify = true;
        this.listenerList = new EventListenerList();
    }

    /**
     * Returns the chart that this plot is assigned to.  This method can
     * return {@code null} if the plot is not yet assigned to a plot, or if the
     * plot is a subplot of another plot.
     *
     * @return The chart (possibly {@code null}).
     */
    public JFreeChart getChart() {
        return this.chart;
    }

    /**
     * Sets the chart that the plot is assigned to.  This method is not
     * intended for external use.
     *
     * @param chart  the chart ({@code null} permitted).
     */
    public void setChart(JFreeChart chart) {
        this.chart = chart;
    }

    /**
     * Fetches the element hinting flag from the chart that this plot is
     * assigned to.  If the plot is not assigned (directly or indirectly) to
     * a chart instance, this method will return {@code false}.
     *
     * @return A boolean.
     */
    public boolean fetchElementHintingFlag() {
        if (this.parent != null) {
            return this.parent.fetchElementHintingFlag();
        }
        if (this.chart != null) {
            return this.chart.getElementHinting();
        }
        return false;
    }

    /**
     * Returns the string that is displayed when the dataset is empty or
     * {@code null}.
     *
     * @return The 'no data' message ({@code null} possible).
     *
     * @see #setNoDataMessage(String)
     * @see #getNoDataMessageFont()
     * @see #getNoDataMessagePaint()
     */
    public String getNoDataMessage() {
        return this.noDataMessage;
    }

    /**
     * Sets the message that is displayed when the dataset is empty or
     * {@code null}, and sends a {@link PlotChangeEvent} to all registered
     * listeners.
     *
     * @param message  the message ({@code null} permitted).
     *
     * @see #getNoDataMessage()
     */
    public void setNoDataMessage(String message) {
        this.noDataMessage = message;
        fireChangeEvent();
    }

    /**
     * Returns the font used to display the 'no data' message.
     *
     * @return The font (never {@code null}).
     *
     * @see #setNoDataMessageFont(Font)
     * @see #getNoDataMessage()
     */
    public Font getNoDataMessageFont() {
        return this.noDataMessageFont;
    }

    /**
     * Sets the font used to display the 'no data' message and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getNoDataMessageFont()
     */
    public void setNoDataMessageFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.noDataMessageFont = font;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to display the 'no data' message.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setNoDataMessagePaint(Paint)
     * @see #getNoDataMessage()
     */
    public Paint getNoDataMessagePaint() {
        return this.noDataMessagePaint;
    }

    /**
     * Sets the paint used to display the 'no data' message and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getNoDataMessagePaint()
     */
    public void setNoDataMessagePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.noDataMessagePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns a short string describing the plot type.
     * <P>
     * Note: this gets used in the chart property editing user interface,
     * but there needs to be a better mechanism for identifying the plot type.
     *
     * @return A short string describing the plot type (never
     *     {@code null}).
     */
    public abstract String getPlotType();

    /**
     * Returns the parent plot (or {@code null} if this plot is not part
     * of a combined plot).
     *
     * @return The parent plot.
     *
     * @see #setParent(Plot)
     * @see #getRootPlot()
     */
    public Plot getParent() {
        return this.parent;
    }

    /**
     * Sets the parent plot.  This method is intended for internal use, you
     * shouldn't need to call it directly.
     *
     * @param parent  the parent plot ({@code null} permitted).
     *
     * @see #getParent()
     */
    public void setParent(Plot parent) {
        this.parent = parent;
    }

    /**
     * Returns the root plot.
     *
     * @return The root plot.
     *
     * @see #getParent()
     */
    public Plot getRootPlot() {
        Plot p = getParent();
        if (p == null) {
            return this;
        }
        return p.getRootPlot();
    }

    /**
     * Returns {@code true} if this plot is part of a combined plot
     * structure (that is, {@link #getParent()} returns a non-{@code null}
     * value), and {@code false} otherwise.
     *
     * @return {@code true} if this plot is part of a combined plot
     *         structure.
     *
     * @see #getParent()
     */
    public boolean isSubplot() {
        return (getParent() != null);
    }

    /**
     * Returns the insets for the plot area.
     *
     * @return The insets (never {@code null}).
     *
     * @see #setInsets(RectangleInsets)
     */
    public RectangleInsets getInsets() {
        return this.insets;
    }

    /**
     * Sets the insets for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param insets  the new insets ({@code null} not permitted).
     *
     * @see #getInsets()
     * @see #setInsets(RectangleInsets, boolean)
     */
    public void setInsets(RectangleInsets insets) {
        setInsets(insets, true);
    }

    /**
     * Sets the insets for the plot and, if requested,  and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param insets  the new insets ({@code null} not permitted).
     * @param notify  a flag that controls whether the registered listeners are
     *                notified.
     *
     * @see #getInsets()
     * @see #setInsets(RectangleInsets)
     */
    public void setInsets(RectangleInsets insets, boolean notify) {
        Args.nullNotPermitted(insets, "insets");
        if (!this.insets.equals(insets)) {
            this.insets = insets;
            if (notify) {
                fireChangeEvent();
            }
        }
    }

    /**
     * Returns the background color of the plot area.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setBackgroundPaint(Paint)
     */
    public Paint getBackgroundPaint() {
        return this.backgroundPaint;
    }

    /**
     * Sets the background color of the plot area and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getBackgroundPaint()
     */
    public void setBackgroundPaint(Paint paint) {
        if (paint == null) {
            if (this.backgroundPaint != null) {
                this.backgroundPaint = null;
                fireChangeEvent();
            }
        } else {
            if (this.backgroundPaint != null) {
                if (this.backgroundPaint.equals(paint)) {
                    // nothing to do
                    return;
                }
            }
            this.backgroundPaint = paint;
            fireChangeEvent();
        }
    }

    /**
     * Returns the alpha transparency of the plot area background.
     *
     * @return The alpha transparency.
     *
     * @see #setBackgroundAlpha(float)
     */
    public float getBackgroundAlpha() {
        return this.backgroundAlpha;
    }

    /**
     * Sets the alpha transparency of the plot area background, and notifies
     * registered listeners that the plot has been modified.
     *
     * @param alpha the new alpha value (in the range 0.0f to 1.0f).
     *
     * @see #getBackgroundAlpha()
     */
    public void setBackgroundAlpha(float alpha) {
        if (this.backgroundAlpha != alpha) {
            this.backgroundAlpha = alpha;
            fireChangeEvent();
        }
    }

    /**
     * Returns the drawing supplier for the plot.
     *
     * @return The drawing supplier (possibly {@code null}).
     *
     * @see #setDrawingSupplier(DrawingSupplier)
     */
    public DrawingSupplier getDrawingSupplier() {
        DrawingSupplier result;
        Plot p = getParent();
        if (p != null) {
            result = p.getDrawingSupplier();
        } else {
            result = this.drawingSupplier;
        }
        return result;
    }

    /**
     * Sets the drawing supplier for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.  The drawing
     * supplier is responsible for supplying a limitless (possibly repeating)
     * sequence of {@code Paint}, {@code Stroke} and
     * {@code Shape} objects that the plot's renderer(s) can use to
     * populate its (their) tables.
     *
     * @param supplier  the new supplier.
     *
     * @see #getDrawingSupplier()
     */
    public void setDrawingSupplier(DrawingSupplier supplier) {
        this.drawingSupplier = supplier;
        fireChangeEvent();
    }

    /**
     * Sets the drawing supplier for the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.  The drawing
     * supplier is responsible for supplying a limitless (possibly repeating)
     * sequence of {@code Paint}, {@code Stroke} and
     * {@code Shape} objects that the plot's renderer(s) can use to
     * populate its (their) tables.
     *
     * @param supplier  the new supplier.
     * @param notify  notify listeners?
     *
     * @see #getDrawingSupplier()
     */
    public void setDrawingSupplier(DrawingSupplier supplier, boolean notify) {
        this.drawingSupplier = supplier;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the background image that is used to fill the plot's background
     * area.
     *
     * @return The image (possibly {@code null}).
     *
     * @see #setBackgroundImage(Image)
     */
    public Image getBackgroundImage() {
        return this.backgroundImage;
    }

    /**
     * Sets the background image for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param image  the image ({@code null} permitted).
     *
     * @see #getBackgroundImage()
     */
    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        fireChangeEvent();
    }

    /**
     * Returns the background image alignment. The default value is
     * {@code RectangleAlignment.FILL}.
     *
     * @return The alignment (never {@code null}).
     *
     * @see #setBackgroundImageAlignment(RectangleAlignment)
     */
    public RectangleAlignment getBackgroundImageAlignment() {
        return this.backgroundImageAlignment;
    }

    /**
     * Sets the alignment for the background image and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param alignment  the alignment ({@code null} not permitted).
     *
     * @see #getBackgroundImageAlignment()
     */
    public void setBackgroundImageAlignment(RectangleAlignment alignment) {
        Args.nullNotPermitted(alignment, "alignment");
        if (this.backgroundImageAlignment != alignment) {
            this.backgroundImageAlignment = alignment;
            fireChangeEvent();
        }
    }

    /**
     * Returns the alpha transparency used to draw the background image.  This
     * is a value in the range 0.0f to 1.0f, where 0.0f is fully transparent
     * and 1.0f is fully opaque.
     *
     * @return The alpha transparency.
     *
     * @see #setBackgroundImageAlpha(float)
     */
    public float getBackgroundImageAlpha() {
        return this.backgroundImageAlpha;
    }

    /**
     * Sets the alpha transparency used when drawing the background image.
     *
     * @param alpha  the alpha transparency (in the range 0.0f to 1.0f, where
     *     0.0f is fully transparent, and 1.0f is fully opaque).
     *
     * @throws IllegalArgumentException if {@code alpha} is not within
     *     the specified range.
     *
     * @see #getBackgroundImageAlpha()
     */
    public void setBackgroundImageAlpha(float alpha) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f.");
        }
        if (this.backgroundImageAlpha != alpha) {
            this.backgroundImageAlpha = alpha;
            fireChangeEvent();
        }
    }

    /**
     * Returns the flag that controls whether the plot outline is
     * drawn.  The default value is {@code true}.  Note that for
     * historical reasons, the plot's outline paint and stroke can take on
     * {@code null} values, in which case the outline will not be drawn
     * even if this flag is set to {@code true}.
     *
     * @return The outline visibility flag.
     *
     * @see #setOutlineVisible(boolean)
     */
    public boolean isOutlineVisible() {
        return this.outlineVisible;
    }

    /**
     * Sets the flag that controls whether the plot's outline is
     * drawn, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param visible  the new flag value.
     *
     * @see #isOutlineVisible()
     */
    public void setOutlineVisible(boolean visible) {
        this.outlineVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to outline the plot area.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setOutlineStroke(Stroke)
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the stroke used to outline the plot area and sends a
     * {@link PlotChangeEvent} to all registered listeners. If you set this
     * attribute to {@code null}, no outline will be drawn.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getOutlineStroke()
     */
    public void setOutlineStroke(Stroke stroke) {
        if (stroke == null) {
            if (this.outlineStroke != null) {
                this.outlineStroke = null;
                fireChangeEvent();
            }
        } else {
            if (this.outlineStroke != null) {
                if (this.outlineStroke.equals(stroke)) {
                    // nothing to do
                    return;
                }
            }
            this.outlineStroke = stroke;
            fireChangeEvent();
        }
    }

    /**
     * Returns the color used to draw the outline of the plot area.
     *
     * @return The color (possibly {@code null}).
     *
     * @see #setOutlinePaint(Paint)
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the paint used to draw the outline of the plot area and sends a
     * {@link PlotChangeEvent} to all registered listeners.  If you set this
     * attribute to {@code null}, no outline will be drawn.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getOutlinePaint()
     */
    public void setOutlinePaint(Paint paint) {
        if (paint == null) {
            if (this.outlinePaint != null) {
                this.outlinePaint = null;
                fireChangeEvent();
            }
        } else {
            if (this.outlinePaint != null) {
                if (this.outlinePaint.equals(paint)) {
                    // nothing to do
                    return;
                }
            }
            this.outlinePaint = paint;
            fireChangeEvent();
        }
    }

    /**
     * Returns the alpha-transparency for the plot foreground.
     *
     * @return The alpha-transparency.
     *
     * @see #setForegroundAlpha(float)
     */
    public float getForegroundAlpha() {
        return this.foregroundAlpha;
    }

    /**
     * Sets the alpha-transparency for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param alpha  the new alpha transparency.
     *
     * @see #getForegroundAlpha()
     */
    public void setForegroundAlpha(float alpha) {
        if (this.foregroundAlpha != alpha) {
            this.foregroundAlpha = alpha;
            fireChangeEvent();
        }
    }

    /**
     * Returns the legend items for the plot.  By default, this method returns
     * {@code null}.  Subclasses should override to return a
     * {@link LegendItemCollection}.
     *
     * @return The legend items for the plot (possibly {@code null}).
     */
    @Override
    public LegendItemCollection getLegendItems() {
        return null;
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
     * {@link PlotChangeEvent} notifications.
     *
     * @param notify  a boolean.
     *
     * @see #isNotify()
     */
    public void setNotify(boolean notify) {
        this.notify = notify;
        // if the flag is being set to true, there may be queued up changes...
        if (notify) {
            notifyListeners(new PlotChangeEvent(this));
        }
    }

    /**
     * Registers an object for notification of changes to the plot.
     *
     * @param listener  the object to be registered.
     *
     * @see #removeChangeListener(PlotChangeListener)
     */
    public void addChangeListener(PlotChangeListener listener) {
        this.listenerList.add(PlotChangeListener.class, listener);
    }

    /**
     * Unregisters an object for notification of changes to the plot.
     *
     * @param listener  the object to be unregistered.
     *
     * @see #addChangeListener(PlotChangeListener)
     */
    public void removeChangeListener(PlotChangeListener listener) {
        this.listenerList.remove(PlotChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the plot has been modified.
     *
     * @param event  information about the change event.
     */
    public void notifyListeners(PlotChangeEvent event) {
        // if the 'notify' flag has been switched to false, we don't notify
        // the listeners
        if (!this.notify) {
            return;
        }
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PlotChangeListener.class) {
                ((PlotChangeListener) listeners[i + 1]).plotChanged(event);
            }
        }
    }

    /**
     * Sends a {@link PlotChangeEvent} to all registered listeners.
     */
    protected void fireChangeEvent() {
        notifyListeners(new PlotChangeEvent(this));
    }

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
     * Draws the plot within the specified area.  The anchor is a point on the
     * chart that is specified externally (for instance, it may be the last
     * point of the last mouse click performed by the user) - plots can use or
     * ignore this value as they see fit.
     * <br><br>
     * Subclasses need to provide an implementation of this method, obviously.
     *
     * @param g2  the graphics device.
     * @param area  the plot area.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  the parent state (if any, {@code null} permitted).
     * @param info  carries back plot rendering info.
     */
    public abstract void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info);

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
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        // some subclasses override this method completely, so don't put
        // anything here that *must* be done
        fillBackground(g2, area);
        drawBackgroundImage(g2, area);
    }

    /**
     * Fills the specified area with the background paint.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     *
     * @see #getBackgroundPaint()
     * @see #getBackgroundAlpha()
     * @see #fillBackground(Graphics2D, Rectangle2D, PlotOrientation)
     */
    protected void fillBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, PlotOrientation.VERTICAL);
    }

    /**
     * Fills the specified area with the background paint.  If the background
     * paint is an instance of {@code GradientPaint}, the gradient will
     * run in the direction suggested by the plot's orientation.
     *
     * @param g2  the graphics target.
     * @param area  the plot area.
     * @param orientation  the plot orientation ({@code null} not permitted).
     */
    protected void fillBackground(Graphics2D g2, Rectangle2D area, PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        if (this.backgroundPaint == null) {
            return;
        }
        Paint p = this.backgroundPaint;
        if (p instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) p;
            if (orientation == PlotOrientation.VERTICAL) {
                p = new GradientPaint((float) area.getCenterX(), (float) area.getMaxY(), gp.getColor1(), (float) area.getCenterX(), (float) area.getMinY(), gp.getColor2());
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                p = new GradientPaint((float) area.getMinX(), (float) area.getCenterY(), gp.getColor1(), (float) area.getMaxX(), (float) area.getCenterY(), gp.getColor2());
            }
        }
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.backgroundAlpha));
        g2.setPaint(p);
        g2.fill(area);
        g2.setComposite(originalComposite);
    }

    /**
     * Draws the background image (if there is one) aligned within the
     * specified area.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     *
     * @see #getBackgroundImage()
     * @see #getBackgroundImageAlignment()
     * @see #getBackgroundImageAlpha()
     */
    public void drawBackgroundImage(Graphics2D g2, Rectangle2D area) {
        if (this.backgroundImage == null) {
            // nothing to do
            return;
        }
        Composite savedComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.backgroundImageAlpha));
        Rectangle2D dest = new Rectangle2D.Double(0.0, 0.0, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null));
        this.backgroundImageAlignment.align(dest, area);
        Shape savedClip = g2.getClip();
        g2.clip(area);
        g2.drawImage(this.backgroundImage, (int) dest.getX(), (int) dest.getY(), (int) dest.getWidth() + 1, (int) dest.getHeight() + 1, null);
        g2.setClip(savedClip);
        g2.setComposite(savedComposite);
    }

    /**
     * Draws the plot outline.  This method will be called during the chart
     * drawing process and is declared public so that it can be accessed by the
     * renderers used by certain subclasses. You shouldn't need to call this
     * method directly.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    public void drawOutline(Graphics2D g2, Rectangle2D area) {
        if (!this.outlineVisible) {
            return;
        }
        if ((this.outlineStroke != null) && (this.outlinePaint != null)) {
            g2.setStroke(this.outlineStroke);
            g2.setPaint(this.outlinePaint);
            Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
            g2.draw(area);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
        }
    }

    /**
     * Draws a message to state that there is no data to plot.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    protected void drawNoDataMessage(Graphics2D g2, Rectangle2D area) {
        Shape savedClip = g2.getClip();
        g2.clip(area);
        String message = this.noDataMessage;
        if (message != null) {
            g2.setFont(this.noDataMessageFont);
            g2.setPaint(this.noDataMessagePaint);
            TextBlock block = TextUtils.createTextBlock(this.noDataMessage, this.noDataMessageFont, this.noDataMessagePaint, 0.9f * (float) area.getWidth(), new G2TextMeasurer(g2));
            block.draw(g2, (float) area.getCenterX(), (float) area.getCenterY(), TextBlockAnchor.CENTER);
        }
        g2.setClip(savedClip);
    }

    /**
     * Creates a plot entity that contains a reference to the plot and the
     * data area as shape.
     *
     * @param dataArea  the data area used as hot spot for the entity.
     * @param plotState  the plot rendering info containing a reference to the
     *     EntityCollection.
     * @param toolTip  the tool tip (defined in the respective Plot
     *     subclass) ({@code null} permitted).
     * @param urlText  the url (defined in the respective Plot subclass)
     *     ({@code null} permitted).
     */
    protected void createAndAddEntity(Rectangle2D dataArea, PlotRenderingInfo plotState, String toolTip, String urlText) {
        if (plotState != null && plotState.getOwner() != null) {
            EntityCollection e = plotState.getOwner().getEntityCollection();
            if (e != null) {
                e.add(new PlotEntity(dataArea, this, toolTip, urlText));
            }
        }
    }

    /**
     * Handles a 'click' on the plot.  Since the plot does not maintain any
     * information about where it has been drawn, the plot rendering info is
     * supplied as an argument so that the plot dimensions can be determined.
     *
     * @param x  the x coordinate (in Java2D space).
     * @param y  the y coordinate (in Java2D space).
     * @param info  an object containing information about the dimensions of
     *              the plot.
     */
    public void handleClick(int x, int y, PlotRenderingInfo info) {
        // provides a 'no action' default
    }

    /**
     * Performs a zoom on the plot.  Subclasses should override if zooming is
     * appropriate for the type of plot.
     *
     * @param percent  the zoom percentage.
     */
    public void zoom(double percent) {
        // do nothing by default.
    }

    /**
     * Receives notification of a change to an {@link Annotation} added to
     * this plot.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void annotationChanged(AnnotationChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Receives notification of a change to one of the plot's axes.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void axisChanged(AxisChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Receives notification of a change to the plot's dataset.
     * <P>
     * The plot reacts by passing on a plot change event to all registered
     * listeners.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        PlotChangeEvent newEvent = new PlotChangeEvent(this);
        newEvent.setType(ChartChangeEventType.DATASET_UPDATED);
        notifyListeners(newEvent);
    }

    /**
     * Receives notification of a change to a marker that is assigned to the
     * plot.
     *
     * @param event  the event.
     */
    @Override
    public void markerChanged(MarkerChangeEvent event) {
        fireChangeEvent();
    }

    /**
     * Adjusts the supplied x-value.
     *
     * @param x  the x-value.
     * @param w1  width 1.
     * @param w2  width 2.
     * @param edge  the edge (left or right).
     *
     * @return The adjusted x-value.
     */
    protected double getRectX(double x, double w1, double w2, RectangleEdge edge) {
        double result = x;
        if (edge == RectangleEdge.LEFT) {
            result = result + w1;
        } else if (edge == RectangleEdge.RIGHT) {
            result = result + w2;
        }
        return result;
    }

    /**
     * Adjusts the supplied y-value.
     *
     * @param y  the x-value.
     * @param h1  height 1.
     * @param h2  height 2.
     * @param edge  the edge (top or bottom).
     *
     * @return The adjusted y-value.
     */
    protected double getRectY(double y, double h1, double h2, RectangleEdge edge) {
        double result = y;
        if (edge == RectangleEdge.TOP) {
            result = result + h1;
        } else if (edge == RectangleEdge.BOTTOM) {
            result = result + h2;
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
        if (!(obj instanceof Plot)) {
            return false;
        }
        Plot that = (Plot) obj;
        if (!Objects.equals(this.noDataMessage, that.noDataMessage)) {
            return false;
        }
        if (!Objects.equals(this.noDataMessageFont, that.noDataMessageFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.noDataMessagePaint, that.noDataMessagePaint)) {
            return false;
        }
        if (!Objects.equals(this.insets, that.insets)) {
            return false;
        }
        if (this.outlineVisible != that.outlineVisible) {
            return false;
        }
        if (!Objects.equals(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
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
        if (this.foregroundAlpha != that.foregroundAlpha) {
            return false;
        }
        if (this.backgroundAlpha != that.backgroundAlpha) {
            return false;
        }
        if (!this.drawingSupplier.equals(that.drawingSupplier)) {
            return false;
        }
        if (this.notify != that.notify) {
            return false;
        }
        return true;
    }

    /**
     * Creates a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the plot does not
     *         support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Plot clone = (Plot) super.clone();
        // private Plot parent <-- don't clone the parent plot, but take care
        // childs in combined plots instead
        clone.drawingSupplier = CloneUtils.clone(this.drawingSupplier);
        clone.listenerList = new EventListenerList();
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
        SerialUtils.writePaint(this.noDataMessagePaint, stream);
        SerialUtils.writeStroke(this.outlineStroke, stream);
        SerialUtils.writePaint(this.outlinePaint, stream);
        // backgroundImage
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
        this.noDataMessagePaint = SerialUtils.readPaint(stream);
        this.outlineStroke = SerialUtils.readStroke(stream);
        this.outlinePaint = SerialUtils.readPaint(stream);
        // backgroundImage
        this.backgroundPaint = SerialUtils.readPaint(stream);
        this.listenerList = new EventListenerList();
    }

    /**
     * Resolves a domain axis location for a given plot orientation.
     *
     * @param location  the location ({@code null} not permitted).
     * @param orientation  the orientation ({@code null} not permitted).
     *
     * @return The edge (never {@code null}).
     */
    public static RectangleEdge resolveDomainAxisLocation(AxisLocation location, PlotOrientation orientation) {
        Args.nullNotPermitted(location, "location");
        Args.nullNotPermitted(orientation, "orientation");
        RectangleEdge result = null;
        switch(location) {
            case TOP_OR_RIGHT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.RIGHT;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.TOP;
                }
                break;
            case TOP_OR_LEFT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.LEFT;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.TOP;
                }
                break;
            case BOTTOM_OR_RIGHT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.RIGHT;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.BOTTOM;
                }
                break;
            case BOTTOM_OR_LEFT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.LEFT;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.BOTTOM;
                }
                break;
            default:
                break;
        }
        // the above should cover all the options...
        if (result == null) {
            throw new IllegalStateException("resolveDomainAxisLocation()");
        }
        return result;
    }

    /**
     * Resolves a range axis location for a given plot orientation.
     *
     * @param location  the location ({@code null} not permitted).
     * @param orientation  the orientation ({@code null} not permitted).
     *
     * @return The edge (never {@code null}).
     */
    public static RectangleEdge resolveRangeAxisLocation(AxisLocation location, PlotOrientation orientation) {
        Args.nullNotPermitted(location, "location");
        Args.nullNotPermitted(orientation, "orientation");
        RectangleEdge result = null;
        switch(location) {
            case TOP_OR_RIGHT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.TOP;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.RIGHT;
                }
                break;
            case TOP_OR_LEFT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.TOP;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.LEFT;
                }
                break;
            case BOTTOM_OR_RIGHT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.BOTTOM;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.RIGHT;
                }
                break;
            case BOTTOM_OR_LEFT:
                if (orientation == PlotOrientation.HORIZONTAL) {
                    result = RectangleEdge.BOTTOM;
                } else if (orientation == PlotOrientation.VERTICAL) {
                    result = RectangleEdge.LEFT;
                }
                break;
            default:
                break;
        }
        // the above should cover all the options...
        if (result == null) {
            throw new IllegalStateException("resolveRangeAxisLocation()");
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
 * ----------------------------------------
 * DirectionalGradientPaintTransformer.java
 * ----------------------------------------
 * (C) Copyright 2013-2021 by Peter Kolb and Contributors.
 *
 * Original Author:  Peter Kolb;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * Transforms a {@code GradientPaint} to range over the width of a target
 * shape.  The orientation of the resulting {@code GradientPaint}
 * depend on the coordinates of the original paint:
 *
 * <ul>
 * <li> If the original paint starts at 0,0 and ends at a point 0, y != 0,
 * the resulting paint will have a vertical orientation.
 * <li> If the original paint starts at 0,0 and ends at a point x !=0, 0,
 * the resulting paint will have a horizontal orientation.
 * <li> If the original paint starts at 0,0 and ends at a point x != 0, y != 0,
 * the resulting paint will have a diagonal orientation from the upper left to
 * the lower right edge. Lines of equal color will have a 45 ∞ angle,
 * pointing upwards from left to right.
 * <li> If the original paint starts at a point x != 0, y != 0,
 * the resulting paint will have a diagonal orientation from the lower left to
 * the upper right edge. Lines of equal color will have a 45 ∞ angle,
 * pointing downwards from left to right.
 * </ul>
 * <p>In all cases, the cyclic flag of the original paint will be taken into
 * account.</p>
 */
public class DirectionalGradientPaintTransformer implements GradientPaintTransformer {

    /**
     * Default constructor.
     */
    public DirectionalGradientPaintTransformer() {
        super();
    }

    /**
     * Transforms a {@code GradientPaint} instance to fit some target
     * shape.
     *
     * @param paint  the original paint (not {@code null}).
     * @param target  the reference area (not {@code null}).
     *
     * @return A transformed paint.
     */
    @Override
    public GradientPaint transform(GradientPaint paint, Shape target) {
        //get the coordinates of the original GradientPaint
        final double px1 = paint.getPoint1().getX();
        final double py1 = paint.getPoint1().getY();
        final double px2 = paint.getPoint2().getX();
        final double py2 = paint.getPoint2().getY();
        //get the coordinates of the shape that is to be filled
        final Rectangle2D bounds = target.getBounds();
        final float bx = (float) bounds.getX();
        final float by = (float) bounds.getY();
        final float bw = (float) bounds.getWidth();
        final float bh = (float) bounds.getHeight();
        //reserve variables to store the coordinates of the resulting GradientPaint
        float rx1, ry1, rx2, ry2;
        if (px1 == 0 && py1 == 0) {
            //start point is upper left corner
            rx1 = bx;
            ry1 = by;
            if (px2 != 0.0f && py2 != 0.0f) {
                //end point is lower right corner --> diagonal gradient
                float offset = (paint.isCyclic()) ? (bw + bh) / 4.0f : (bw + bh) / 2.0f;
                rx2 = bx + offset;
                ry2 = by + offset;
            } else {
                //end point is either lower left corner --> vertical gradient
                //or end point is upper right corner --> horizontal gradient
                rx2 = (px2 == 0) ? rx1 : (paint.isCyclic() ? (rx1 + bw / 2.0f) : (rx1 + bw));
                ry2 = (py2 == 0) ? ry1 : (paint.isCyclic() ? (ry1 + bh / 2.0f) : (ry1 + bh));
            }
        } else {
            //start point is lower left right corner --> diagonal gradient
            rx1 = bx;
            ry1 = by + bh;
            float offset = (paint.isCyclic()) ? (bw + bh) / 4.0f : (bw + bh) / 2.0f;
            rx2 = bx + offset;
            ry2 = by + bh - offset;
        }
        return new GradientPaint(rx1, ry1, paint.getColor1(), rx2, ry2, paint.getColor2(), paint.isCyclic());
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
 * Task.java
 * ---------
 * (C) Copyright 2003-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A simple representation of a task.  The task has a description and a
 * duration.  You can add sub-tasks to the task.
 */
public class Task implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 1094303785346988894L;

    /**
     * The task description.
     */
    private String description;

    /**
     * The time period for the task (estimated or actual).
     */
    private TimePeriod duration;

    /**
     * The percent complete ({@code null} is permitted).
     */
    private Double percentComplete;

    /**
     * Storage for the sub-tasks (if any).
     */
    private List subtasks;

    /**
     * Creates a new task.
     *
     * @param description  the task description ({@code null} not
     *                     permitted).
     * @param duration  the task duration ({@code null} permitted).
     */
    public Task(String description, TimePeriod duration) {
        Args.nullNotPermitted(description, "description");
        this.description = description;
        this.duration = duration;
        this.percentComplete = null;
        this.subtasks = new java.util.ArrayList();
    }

    /**
     * Creates a new task.
     *
     * @param description  the task description ({@code null} not
     *                     permitted).
     * @param start  the start date ({@code null} not permitted).
     * @param end  the end date ({@code null} not permitted).
     */
    public Task(String description, Date start, Date end) {
        this(description, new SimpleTimePeriod(start, end));
    }

    /**
     * Returns the task description.
     *
     * @return The task description (never {@code null}).
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the task description.
     *
     * @param description  the description ({@code null} not permitted).
     */
    public void setDescription(String description) {
        Args.nullNotPermitted(description, "description");
        this.description = description;
    }

    /**
     * Returns the duration (actual or estimated) of the task.
     *
     * @return The task duration (possibly {@code null}).
     */
    public TimePeriod getDuration() {
        return this.duration;
    }

    /**
     * Sets the task duration (actual or estimated).
     *
     * @param duration  the duration ({@code null} permitted).
     */
    public void setDuration(TimePeriod duration) {
        this.duration = duration;
    }

    /**
     * Returns the percentage complete for this task.
     *
     * @return The percentage complete (possibly {@code null}).
     */
    public Double getPercentComplete() {
        return this.percentComplete;
    }

    /**
     * Sets the percentage complete for the task.
     *
     * @param percent  the percentage ({@code null} permitted).
     */
    public void setPercentComplete(Double percent) {
        this.percentComplete = percent;
    }

    /**
     * Sets the percentage complete for the task.
     *
     * @param percent  the percentage.
     */
    public void setPercentComplete(double percent) {
        setPercentComplete(Double.valueOf(percent));
    }

    /**
     * Adds a sub-task to the task.
     *
     * @param subtask  the subtask ({@code null} not permitted).
     */
    public void addSubtask(Task subtask) {
        Args.nullNotPermitted(subtask, "subtask");
        this.subtasks.add(subtask);
    }

    /**
     * Removes a sub-task from the task.
     *
     * @param subtask  the subtask.
     */
    public void removeSubtask(Task subtask) {
        this.subtasks.remove(subtask);
    }

    /**
     * Returns the sub-task count.
     *
     * @return The sub-task count.
     */
    public int getSubtaskCount() {
        return this.subtasks.size();
    }

    /**
     * Returns a sub-task.
     *
     * @param index  the index.
     *
     * @return The sub-task.
     */
    public Task getSubtask(int index) {
        return (Task) this.subtasks.get(index);
    }

    /**
     * Tests this object for equality with an arbitrary object.
     *
     * @param object  the other object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Task)) {
            return false;
        }
        Task that = (Task) object;
        if (!Objects.equals(this.description, that.description)) {
            return false;
        }
        if (!Objects.equals(this.duration, that.duration)) {
            return false;
        }
        if (!Objects.equals(this.percentComplete, that.percentComplete)) {
            return false;
        }
        if (!Objects.equals(this.subtasks, that.subtasks)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.description);
        hash = 71 * hash + Objects.hashCode(this.duration);
        hash = 71 * hash + Objects.hashCode(this.percentComplete);
        hash = 71 * hash + Objects.hashCode(this.subtasks);
        return hash;
    }

    /**
     * Returns a clone of the task.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  never thrown by this class, but
     *         subclasses may not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Task clone = (Task) super.clone();
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
 * -------------------------------
 * XYIntervalSeriesCollection.java
 * -------------------------------
 * (C) Copyright 2006-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A collection of {@link XYIntervalSeries} objects.
 *
 * @param <S> The type for the series keys.
 *
 * @since 1.0.3
 *
 * @see XYIntervalSeries
 */
public class XYIntervalSeriesCollection<S extends Comparable<S>> extends AbstractIntervalXYDataset<S> implements IntervalXYDataset<S>, PublicCloneable, Serializable {

    /**
     * Storage for the data series.
     */
    private List<XYIntervalSeries<S>> data;

    /**
     * Creates a new instance of {@code XIntervalSeriesCollection}.
     */
    public XYIntervalSeriesCollection() {
        this.data = new ArrayList<>();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     */
    public void addSeries(XYIntervalSeries<S> series) {
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
    public XYIntervalSeries<S> getSeries(int series) {
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
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getX(item);
    }

    /**
     * Returns the start x-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public double getStartXValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getXLowValue(item);
    }

    /**
     * Returns the end x-value (as a double primitive) for an item within a
     * series.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The value.
     */
    @Override
    public double getEndXValue(int series, int item) {
        XYIntervalSeries<S> s = this.data.get(series);
        return s.getXHighValue(item);
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
        XYIntervalSeries<S> s = this.data.get(series);
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
        XYIntervalSeries<S> s = this.data.get(series);
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
        XYIntervalSeries<S> s = this.data.get(series);
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
        return getYValue(series, item);
    }

    /**
     * Returns the start x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getStartX(int series, int item) {
        return getStartXValue(series, item);
    }

    /**
     * Returns the end x-value for an item within a series.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The x-value.
     */
    @Override
    public Number getEndX(int series, int item) {
        return getEndXValue(series, item);
    }

    /**
     * Returns the start y-value for an item within a series.  This method
     * maps directly to {@link #getY(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The start y-value.
     */
    @Override
    public Number getStartY(int series, int item) {
        return getStartYValue(series, item);
    }

    /**
     * Returns the end y-value for an item within a series.  This method
     * maps directly to {@link #getY(int, int)}.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The end y-value.
     */
    @Override
    public Number getEndY(int series, int item) {
        return getEndYValue(series, item);
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     *
     * @since 1.0.10
     */
    public void removeSeries(int series) {
        if ((series < 0) || (series >= getSeriesCount())) {
            throw new IllegalArgumentException("Series index out of bounds.");
        }
        XYIntervalSeries<S> ts = this.data.get(series);
        ts.removeChangeListener(this);
        this.data.remove(series);
        fireDatasetChanged();
    }

    /**
     * Removes a series from the collection and sends a
     * {@link DatasetChangeEvent} to all registered listeners.
     *
     * @param series  the series ({@code null} not permitted).
     *
     * @since 1.0.10
     */
    public void removeSeries(XYIntervalSeries<S> series) {
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
     *
     * @since 1.0.10
     */
    public void removeAllSeries() {
        // Unregister the collection as a change listener to each series in
        // the collection.
        for (int i = 0; i < this.data.size(); i++) {
            XYIntervalSeries<S> series = this.data.get(i);
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
        if (!(obj instanceof XYIntervalSeriesCollection)) {
            return false;
        }
        XYIntervalSeriesCollection<S> that = (XYIntervalSeriesCollection) obj;
        return Objects.equals(this.data, that.data);
    }

    /**
     * Returns a clone of this dataset.
     *
     * @return A clone of this dataset.
     *
     * @throws CloneNotSupportedException if there is a problem cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        XYIntervalSeriesCollection clone = (XYIntervalSeriesCollection) super.clone();
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
 * --------------
 * AxisSpace.java
 * --------------
 * (C) Copyright 2003-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A record that contains the space required at each edge of a plot.
 */
public class AxisSpace implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -2490732595134766305L;

    /**
     * The top space.
     */
    private double top;

    /**
     * The bottom space.
     */
    private double bottom;

    /**
     * The left space.
     */
    private double left;

    /**
     * The right space.
     */
    private double right;

    /**
     * Creates a new axis space record.
     */
    public AxisSpace() {
        this.top = 0.0;
        this.bottom = 0.0;
        this.left = 0.0;
        this.right = 0.0;
    }

    /**
     * Returns the space reserved for axes at the top of the plot area.
     *
     * @return The space (in Java2D units).
     */
    public double getTop() {
        return this.top;
    }

    /**
     * Sets the space reserved for axes at the top of the plot area.
     *
     * @param space  the space (in Java2D units).
     */
    public void setTop(double space) {
        this.top = space;
    }

    /**
     * Returns the space reserved for axes at the bottom of the plot area.
     *
     * @return The space (in Java2D units).
     */
    public double getBottom() {
        return this.bottom;
    }

    /**
     * Sets the space reserved for axes at the bottom of the plot area.
     *
     * @param space  the space (in Java2D units).
     */
    public void setBottom(double space) {
        this.bottom = space;
    }

    /**
     * Returns the space reserved for axes at the left of the plot area.
     *
     * @return The space (in Java2D units).
     */
    public double getLeft() {
        return this.left;
    }

    /**
     * Sets the space reserved for axes at the left of the plot area.
     *
     * @param space  the space (in Java2D units).
     */
    public void setLeft(double space) {
        this.left = space;
    }

    /**
     * Returns the space reserved for axes at the right of the plot area.
     *
     * @return The space (in Java2D units).
     */
    public double getRight() {
        return this.right;
    }

    /**
     * Sets the space reserved for axes at the right of the plot area.
     *
     * @param space  the space (in Java2D units).
     */
    public void setRight(double space) {
        this.right = space;
    }

    /**
     * Adds space to the top, bottom, left or right edge of the plot area.
     *
     * @param space  the space (in Java2D units).
     * @param edge  the edge ({@code null} not permitted).
     */
    public void add(double space, RectangleEdge edge) {
        Args.nullNotPermitted(edge, "edge");
        if (edge == RectangleEdge.TOP) {
            this.top += space;
        } else if (edge == RectangleEdge.BOTTOM) {
            this.bottom += space;
        } else if (edge == RectangleEdge.LEFT) {
            this.left += space;
        } else if (edge == RectangleEdge.RIGHT) {
            this.right += space;
        } else {
            throw new IllegalStateException("Unrecognised 'edge' argument.");
        }
    }

    /**
     * Ensures that this object reserves at least as much space as another.
     *
     * @param space  the other space.
     */
    public void ensureAtLeast(AxisSpace space) {
        this.top = Math.max(this.top, space.top);
        this.bottom = Math.max(this.bottom, space.bottom);
        this.left = Math.max(this.left, space.left);
        this.right = Math.max(this.right, space.right);
    }

    /**
     * Ensures there is a minimum amount of space at the edge corresponding to
     * the specified axis location.
     *
     * @param space  the space.
     * @param edge  the location.
     */
    public void ensureAtLeast(double space, RectangleEdge edge) {
        if (edge == RectangleEdge.TOP) {
            if (this.top < space) {
                this.top = space;
            }
        } else if (edge == RectangleEdge.BOTTOM) {
            if (this.bottom < space) {
                this.bottom = space;
            }
        } else if (edge == RectangleEdge.LEFT) {
            if (this.left < space) {
                this.left = space;
            }
        } else if (edge == RectangleEdge.RIGHT) {
            if (this.right < space) {
                this.right = space;
            }
        } else {
            throw new IllegalStateException("AxisSpace.ensureAtLeast(): unrecognised AxisLocation.");
        }
    }

    /**
     * Shrinks an area by the space attributes.
     *
     * @param area  the area to shrink.
     * @param result  an optional carrier for the result.
     *
     * @return The result.
     */
    public Rectangle2D shrink(Rectangle2D area, Rectangle2D result) {
        if (result == null) {
            result = new Rectangle2D.Double();
        }
        result.setRect(area.getX() + this.left, area.getY() + this.top, area.getWidth() - this.left - this.right, area.getHeight() - this.top - this.bottom);
        return result;
    }

    /**
     * Expands an area by the amount of space represented by this object.
     *
     * @param area  the area to expand.
     * @param result  an optional carrier for the result.
     *
     * @return The result.
     */
    public Rectangle2D expand(Rectangle2D area, Rectangle2D result) {
        if (result == null) {
            result = new Rectangle2D.Double();
        }
        result.setRect(area.getX() - this.left, area.getY() - this.top, area.getWidth() + this.left + this.right, area.getHeight() + this.top + this.bottom);
        return result;
    }

    /**
     * Calculates the reserved area.
     *
     * @param area  the area.
     * @param edge  the edge.
     *
     * @return The reserved area.
     */
    public Rectangle2D reserved(Rectangle2D area, RectangleEdge edge) {
        Rectangle2D result = null;
        if (edge == RectangleEdge.TOP) {
            result = new Rectangle2D.Double(area.getX(), area.getY(), area.getWidth(), this.top);
        } else if (edge == RectangleEdge.BOTTOM) {
            result = new Rectangle2D.Double(area.getX(), area.getMaxY() - this.top, area.getWidth(), this.bottom);
        } else if (edge == RectangleEdge.LEFT) {
            result = new Rectangle2D.Double(area.getX(), area.getY(), this.left, area.getHeight());
        } else if (edge == RectangleEdge.RIGHT) {
            result = new Rectangle2D.Double(area.getMaxX() - this.right, area.getY(), this.right, area.getHeight());
        }
        return result;
    }

    /**
     * Returns a clone of the object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException This class won't throw this exception,
     *         but subclasses (if any) might.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Tests this object for equality with another object.
     *
     * @param obj  the object to compare against.
     *
     * @return {@code true} or {@code false}.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AxisSpace)) {
            return false;
        }
        AxisSpace that = (AxisSpace) obj;
        if (this.top != that.top) {
            return false;
        }
        if (this.bottom != that.bottom) {
            return false;
        }
        if (this.left != that.left) {
            return false;
        }
        if (this.right != that.right) {
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
        int result = 23;
        long l = Double.doubleToLongBits(this.top);
        result = 37 * result + (int) (l ^ (l >>> 32));
        l = Double.doubleToLongBits(this.bottom);
        result = 37 * result + (int) (l ^ (l >>> 32));
        l = Double.doubleToLongBits(this.left);
        result = 37 * result + (int) (l ^ (l >>> 32));
        l = Double.doubleToLongBits(this.right);
        result = 37 * result + (int) (l ^ (l >>> 32));
        return result;
    }

    /**
     * Returns a string representing the object (for debugging purposes).
     *
     * @return A string.
     */
    @Override
    public String toString() {
        return super.toString() + "[left=" + this.left + ",right=" + this.right + ",top=" + this.top + ",bottom=" + this.bottom + "]";
    }
}
