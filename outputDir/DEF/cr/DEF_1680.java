package DEF.cr;
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
 * XYPlot.java
 * -----------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Craig MacFarlane;
 *                   Mark Watson (www.markwatson.com);
 *                   Jonathan Nash;
 *                   Gideon Krause;
 *                   Klaus Rheinwald;
 *                   Xavier Poinsard;
 *                   Richard Atkinson;
 *                   Arnaud Lelievre;
 *                   Nicolas Brodu;
 *                   Eduardo Ramalho;
 *                   Sergei Ivanov;
 *                   Richard West, Advanced Micro Devices, Inc.;
 *                   Ulrich Voigt - patches 1997549 and 2686040;
 *                   Peter Kolb - patches 1934255, 2603321 and 2809117;
 *                   Andrew Mickish - patch 1868749;
 *
 */
/**
 * A general class for plotting data in the form of (x, y) pairs.  This plot can
 * use data from any class that implements the {@link XYDataset} interface.
 * <P>
 * {@code XYPlot} makes use of an {@link XYItemRenderer} to draw each point
 * on the plot.  By using different renderers, various chart types can be
 * produced.
 * <p>
 * The {@link org.jfree.chart.ChartFactory} class contains static methods for
 * creating pre-configured charts.
 *
 * @param <S>The type for the series keys.
 */
public class XYPlot<S extends Comparable<S>> extends Plot implements ValueAxisPlot, Pannable, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7044148245716569264L;

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
     */
    private PlotOrientation orientation;

    /**
     * The offset between the data area and the axes.
     */
    private RectangleInsets axisOffset;

    /**
     * The domain axis / axes (used for the x-values).
     */
    private Map<Integer, ValueAxis> domainAxes;

    /**
     * The domain axis locations.
     */
    private Map<Integer, AxisLocation> domainAxisLocations;

    /**
     * The range axis (used for the y-values).
     */
    private Map<Integer, ValueAxis> rangeAxes;

    /**
     * The range axis location.
     */
    private Map<Integer, AxisLocation> rangeAxisLocations;

    /**
     * Storage for the datasets.
     */
    private Map<Integer, XYDataset<S>> datasets;

    /**
     * Storage for the renderers.
     */
    private Map<Integer, XYItemRenderer> renderers;

    /**
     * Storage for the mapping between datasets/renderers and domain axes.  The
     * keys in the map are Integer objects, corresponding to the dataset
     * index.  The values in the map are List objects containing Integer
     * objects (corresponding to the axis indices).  If the map contains no
     * entry for a dataset, it is assumed to map to the primary domain axis
     * (index = 0).
     */
    private Map<Integer, List<Integer>> datasetToDomainAxesMap;

    /**
     * Storage for the mapping between datasets/renderers and range axes.  The
     * keys in the map are Integer objects, corresponding to the dataset
     * index.  The values in the map are List objects containing Integer
     * objects (corresponding to the axis indices).  If the map contains no
     * entry for a dataset, it is assumed to map to the primary domain axis
     * (index = 0).
     */
    private Map<Integer, List<Integer>> datasetToRangeAxesMap;

    /**
     * The origin point for the quadrants (if drawn).
     */
    private transient Point2D quadrantOrigin = new Point2D.Double(0.0, 0.0);

    /**
     * The paint used for each quadrant.
     */
    private transient Paint[] quadrantPaint = new Paint[] { null, null, null, null };

    /**
     * A flag that controls whether the domain grid-lines are visible.
     */
    private boolean domainGridlinesVisible;

    /**
     * The stroke used to draw the domain grid-lines.
     */
    private transient Stroke domainGridlineStroke;

    /**
     * The paint used to draw the domain grid-lines.
     */
    private transient Paint domainGridlinePaint;

    /**
     * A flag that controls whether the range grid-lines are visible.
     */
    private boolean rangeGridlinesVisible;

    /**
     * The stroke used to draw the range grid-lines.
     */
    private transient Stroke rangeGridlineStroke;

    /**
     * The paint used to draw the range grid-lines.
     */
    private transient Paint rangeGridlinePaint;

    /**
     * A flag that controls whether the domain minor grid-lines are visible.
     */
    private boolean domainMinorGridlinesVisible;

    /**
     * The stroke used to draw the domain minor grid-lines.
     */
    private transient Stroke domainMinorGridlineStroke;

    /**
     * The paint used to draw the domain minor grid-lines.
     */
    private transient Paint domainMinorGridlinePaint;

    /**
     * A flag that controls whether the range minor grid-lines are visible.
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
     * A flag that controls whether the zero baseline against the domain
     * axis is visible.
     */
    private boolean domainZeroBaselineVisible;

    /**
     * The stroke used for the zero baseline against the domain axis.
     */
    private transient Stroke domainZeroBaselineStroke;

    /**
     * The paint used for the zero baseline against the domain axis.
     */
    private transient Paint domainZeroBaselinePaint;

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
     * A flag that controls whether a domain crosshair is drawn.
     */
    private boolean domainCrosshairVisible;

    /**
     * The domain crosshair value.
     */
    private double domainCrosshairValue;

    /**
     * The pen/brush used to draw the crosshair (if any).
     */
    private transient Stroke domainCrosshairStroke;

    /**
     * The color used to draw the crosshair (if any).
     */
    private transient Paint domainCrosshairPaint;

    /**
     * A flag that controls whether the crosshair locks onto actual
     * data points.
     */
    private boolean domainCrosshairLockedOnData = true;

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
     * A map of lists of foreground markers (optional) for the domain axes.
     */
    private Map<Integer, List<Marker>> foregroundDomainMarkers;

    /**
     * A map of lists of background markers (optional) for the domain axes.
     */
    private Map<Integer, List<Marker>> backgroundDomainMarkers;

    /**
     * A map of lists of foreground markers (optional) for the range axes.
     */
    private Map<Integer, List<Marker>> foregroundRangeMarkers;

    /**
     * A map of lists of background markers (optional) for the range axes.
     */
    private Map<Integer, List<Marker>> backgroundRangeMarkers;

    /**
     * A (possibly empty) list of annotations for the plot.  The list should
     * be initialised in the constructor and never allowed to be
     * {@code null}.
     */
    private List<XYAnnotation> annotations;

    /**
     * The paint used for the domain tick bands (if any).
     */
    private transient Paint domainTickBandPaint;

    /**
     * The paint used for the range tick bands (if any).
     */
    private transient Paint rangeTickBandPaint;

    /**
     * The fixed domain axis space.
     */
    private AxisSpace fixedDomainAxisSpace;

    /**
     * The fixed range axis space.
     */
    private AxisSpace fixedRangeAxisSpace;

    /**
     * The order of the dataset rendering (REVERSE draws the primary dataset
     * last so that it appears to be on top).
     */
    private DatasetRenderingOrder datasetRenderingOrder = DatasetRenderingOrder.REVERSE;

    /**
     * The order of the series rendering (REVERSE draws the primary series
     * last so that it appears to be on top).
     */
    private SeriesRenderingOrder seriesRenderingOrder = SeriesRenderingOrder.REVERSE;

    /**
     * The weight for this plot (only relevant if this is a subplot in a
     * combined plot).
     */
    private int weight;

    /**
     * An optional collection of legend items that can be returned by the
     * getLegendItems() method.
     */
    private LegendItemCollection fixedLegendItems;

    /**
     * A flag that controls whether panning is enabled for the domain
     * axis/axes.
     */
    private boolean domainPannable;

    /**
     * A flag that controls whether panning is enabled for the range
     * axis/axes.
     */
    private boolean rangePannable;

    /**
     * The shadow generator ({@code null} permitted).
     */
    private ShadowGenerator shadowGenerator;

    /**
     * Creates a new {@code XYPlot} instance with no dataset, no axes and
     * no renderer.  You should specify these items before using the plot.
     */
    public XYPlot() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot with the specified dataset, axes and renderer.  Any
     * of the arguments can be {@code null}, but in that case you should
     * take care to specify the value before using the plot (otherwise a
     * {@code NullPointerException} may be thrown).
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param domainAxis  the domain axis ({@code null} permitted).
     * @param rangeAxis  the range axis ({@code null} permitted).
     * @param renderer  the renderer ({@code null} permitted).
     */
    public XYPlot(XYDataset<S> dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
        super();
        this.orientation = PlotOrientation.VERTICAL;
        // only relevant when this is a subplot
        this.weight = 1;
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        // allocate storage for datasets, axes and renderers (all optional)
        this.domainAxes = new HashMap<>();
        this.domainAxisLocations = new HashMap<>();
        this.foregroundDomainMarkers = new HashMap<>();
        this.backgroundDomainMarkers = new HashMap<>();
        this.rangeAxes = new HashMap<>();
        this.rangeAxisLocations = new HashMap<>();
        this.foregroundRangeMarkers = new HashMap<>();
        this.backgroundRangeMarkers = new HashMap<>();
        this.datasets = new HashMap<>();
        this.renderers = new HashMap<>();
        this.datasetToDomainAxesMap = new TreeMap<>();
        this.datasetToRangeAxesMap = new TreeMap<>();
        this.annotations = new ArrayList<>();
        if (dataset != null) {
            dataset.addChangeListener(this);
            this.datasets.put(0, dataset);
        }
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
            this.renderers.put(0, renderer);
        }
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
            this.domainAxes.put(0, domainAxis);
            mapDatasetToDomainAxis(0, 0);
        }
        this.domainAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
            this.rangeAxes.put(0, rangeAxis);
            mapDatasetToRangeAxis(0, 0);
        }
        this.rangeAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
        configureDomainAxes();
        configureRangeAxes();
        this.domainGridlinesVisible = true;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.domainMinorGridlinesVisible = false;
        this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainMinorGridlinePaint = Color.WHITE;
        this.domainZeroBaselineVisible = false;
        this.domainZeroBaselinePaint = Color.BLACK;
        this.domainZeroBaselineStroke = new BasicStroke(0.5f);
        this.rangeGridlinesVisible = true;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = false;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.WHITE;
        this.rangeZeroBaselineVisible = false;
        this.rangeZeroBaselinePaint = Color.BLACK;
        this.rangeZeroBaselineStroke = new BasicStroke(0.5f);
        this.domainCrosshairVisible = false;
        this.domainCrosshairValue = 0.0;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = false;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.shadowGenerator = null;
    }

    /**
     * Returns the plot type as a string.
     *
     * @return A short string describing the type of plot.
     */
    @Override
    public String getPlotType() {
        return localizationResources.getString("XY_Plot");
    }

    /**
     * Returns the orientation of the plot.
     *
     * @return The orientation (never {@code null}).
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
     * @param orientation  the orientation ({@code null} not allowed).
     *
     * @see #getOrientation()
     */
    public void setOrientation(PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        if (orientation != this.orientation) {
            this.orientation = orientation;
            fireChangeEvent();
        }
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
     * Sets the axis offsets (gap between the data area and the axes) and sends
     * a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the domain axis with index 0.  If the domain axis for this plot
     * is {@code null}, then the method will return the parent plot's
     * domain axis (if there is a parent plot).
     *
     * @return The domain axis (possibly {@code null}).
     *
     * @see #getDomainAxis(int)
     * @see #setDomainAxis(ValueAxis)
     */
    public ValueAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    /**
     * Returns the domain axis with the specified index, or {@code null} if
     * there is no axis with that index.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setDomainAxis(int, ValueAxis)
     */
    public ValueAxis getDomainAxis(int index) {
        ValueAxis result = this.domainAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> xy = (XYPlot<S>) parent;
                result = xy.getDomainAxis(index);
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
    public Map<Integer, ValueAxis> getDomainAxes() {
        return Collections.unmodifiableMap(this.domainAxes);
    }

    /**
     * Sets the domain axis for the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param axis  the new axis ({@code null} permitted).
     *
     * @see #getDomainAxis()
     * @see #setDomainAxis(int, ValueAxis)
     */
    public void setDomainAxis(ValueAxis axis) {
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
     * @see #setRangeAxis(int, ValueAxis)
     */
    public void setDomainAxis(int index, ValueAxis axis) {
        setDomainAxis(index, axis, true);
    }

    /**
     * Sets a domain axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     * @param notify  notify listeners?
     *
     * @see #getDomainAxis(int)
     */
    public void setDomainAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getDomainAxis(index);
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
    public void setDomainAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the location of the primary domain axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #setDomainAxisLocation(AxisLocation)
     */
    public AxisLocation getDomainAxisLocation() {
        return this.domainAxisLocations.get(0);
    }

    /**
     * Sets the location of the primary domain axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getDomainAxisLocation()
     */
    public void setDomainAxisLocation(AxisLocation location) {
        // delegate...
        setDomainAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getDomainAxisLocation()
     */
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setDomainAxisLocation(0, location, notify);
    }

    /**
     * Returns the edge for the primary domain axis (taking into account the
     * plot's orientation).
     *
     * @return The edge.
     *
     * @see #getDomainAxisLocation()
     * @see #getOrientation()
     */
    public RectangleEdge getDomainAxisEdge() {
        return Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
    }

    /**
     * Returns the number of domain axes.
     *
     * @return The axis count.
     *
     * @see #getRangeAxisCount()
     */
    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    /**
     * Clears the domain axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @see #clearRangeAxes()
     */
    public void clearDomainAxes() {
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the domain axes.
     */
    public void configureDomainAxes() {
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }

    /**
     * Returns the location for a domain axis.  If this hasn't been set
     * explicitly, the method returns the location that is opposite to the
     * primary domain axis location.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The location (never {@code null}).
     *
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = this.domainAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getDomainAxisLocation());
        }
        return result;
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted for index
     *     0).
     *
     * @see #getDomainAxisLocation(int)
     */
    public void setDomainAxisLocation(int index, AxisLocation location) {
        // delegate...
        setDomainAxisLocation(index, location, true);
    }

    /**
     * Sets the axis location for a domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index (must be &gt;= 0).
     * @param location  the location ({@code null} not permitted for
     *     index 0).
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
     * Returns the edge for a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     *
     * @see #getRangeAxisEdge(int)
     */
    public RectangleEdge getDomainAxisEdge(int index) {
        AxisLocation location = getDomainAxisLocation(index);
        return Plot.resolveDomainAxisLocation(location, this.orientation);
    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * {@code null}, then the method will return the parent plot's range
     * axis (if there is a parent plot).
     *
     * @return The range axis.
     *
     * @see #getRangeAxis(int)
     * @see #setRangeAxis(ValueAxis)
     */
    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    /**
     * Sets the range axis for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getRangeAxis()
     * @see #setRangeAxis(int, ValueAxis)
     */
    public void setRangeAxis(ValueAxis axis) {
        if (axis != null) {
            axis.setPlot(this);
        }
        // plot is likely registered as a listener with the existing axis...
        ValueAxis existing = getRangeAxis();
        if (existing != null) {
            existing.removeChangeListener(this);
        }
        this.rangeAxes.put(0, axis);
        if (axis != null) {
            axis.configure();
            axis.addChangeListener(this);
        }
        fireChangeEvent();
    }

    /**
     * Returns the location of the primary range axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #setRangeAxisLocation(AxisLocation)
     */
    public AxisLocation getRangeAxisLocation() {
        return this.rangeAxisLocations.get(0);
    }

    /**
     * Sets the location of the primary range axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #getRangeAxisLocation()
     */
    public void setRangeAxisLocation(AxisLocation location) {
        // delegate...
        setRangeAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the primary range axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #getRangeAxisLocation()
     */
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setRangeAxisLocation(0, location, notify);
    }

    /**
     * Returns the edge for the primary range axis.
     *
     * @return The range axis edge.
     *
     * @see #getRangeAxisLocation()
     * @see #getOrientation()
     */
    public RectangleEdge getRangeAxisEdge() {
        return Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
    }

    /**
     * Returns the range axis with the specified index, or {@code null} if
     * there is no axis with that index.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setRangeAxis(int, ValueAxis)
     */
    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = this.rangeAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> xy = (XYPlot<S>) parent;
                result = xy.getRangeAxis(index);
            }
        }
        return result;
    }

    /**
     * Returns a map containing the range axes that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the range axes that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, ValueAxis> getRangeAxes() {
        return Collections.unmodifiableMap(this.rangeAxes);
    }

    /**
     * Sets a range axis and sends a {@link PlotChangeEvent} to all registered
     * listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getRangeAxis(int)
     */
    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, true);
    }

    /**
     * Sets a range axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getRangeAxis(int)
     */
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = getRangeAxis(index);
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
     * @see #setDomainAxes(ValueAxis[])
     */
    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the number of range axes.
     *
     * @return The axis count.
     *
     * @see #getDomainAxisCount()
     */
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    /**
     * Clears the range axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @see #clearDomainAxes()
     */
    public void clearRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the range axes.
     *
     * @see #configureDomainAxes()
     */
    public void configureRangeAxes() {
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.configure();
            }
        }
    }

    /**
     * Returns the location for a range axis.  If this hasn't been set
     * explicitly, the method returns the location that is opposite to the
     * primary range axis location.
     *
     * @param index  the axis index (must be &gt;= 0).
     *
     * @return The location (never {@code null}).
     *
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = this.rangeAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getRangeAxisLocation());
        }
        return result;
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} permitted).
     *
     * @see #getRangeAxisLocation(int)
     */
    public void setRangeAxisLocation(int index, AxisLocation location) {
        // delegate...
        setRangeAxisLocation(index, location, true);
    }

    /**
     * Sets the axis location for a domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location ({@code null} not permitted for index 0).
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
     * Returns the edge for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     *
     * @see #getRangeAxisLocation(int)
     * @see #getOrientation()
     */
    public RectangleEdge getRangeAxisEdge(int index) {
        AxisLocation location = getRangeAxisLocation(index);
        return Plot.resolveRangeAxisLocation(location, this.orientation);
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly {@code null}).
     *
     * @see #getDataset(int)
     * @see #setDataset(XYDataset)
     */
    public XYDataset<S> getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset with the specified index, or {@code null} if there
     * is no dataset with that index.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(int, XYDataset)
     */
    public XYDataset<S> getDataset(int index) {
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
    public Map<Integer, XYDataset<S>> getDatasets() {
        return Collections.unmodifiableMap(this.datasets);
    }

    /**
     * Sets the primary dataset for the plot, replacing the existing dataset if
     * there is one.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     * @see #setDataset(int, XYDataset)
     */
    public void setDataset(XYDataset<S> dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot and sends a change event to all registered
     * listeners.
     *
     * @param index  the dataset index (must be &gt;= 0).
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset(int)
     */
    public void setDataset(int index, XYDataset<S> dataset) {
        XYDataset<S> existing = getDataset(index);
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
     * @return The index or -1.
     */
    public int indexOf(XYDataset<S> dataset) {
        for (Map.Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            if (dataset == entry.getValue()) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Maps a dataset to a particular domain axis.  All data will be plotted
     * against axis zero by default, no mapping is required for this case.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index.
     *
     * @see #mapDatasetToRangeAxis(int, int)
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
     * Maps a dataset to a particular range axis.  All data will be plotted
     * against axis zero by default, no mapping is required for this case.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index.
     *
     * @see #mapDatasetToDomainAxis(int, int)
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
     * This method is used to perform argument checking on the list of
     * axis indices passed to mapDatasetToDomainAxes() and
     * mapDatasetToRangeAxes().
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
        int count = indices.size();
        if (count == 0) {
            throw new IllegalArgumentException("Empty list not permitted.");
        }
        Set<Integer> set = new HashSet<>();
        for (Integer item : indices) {
            if (set.contains(item)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            set.add(item);
        }
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
     * Returns the renderer for the primary dataset.
     *
     * @return The item renderer (possibly {@code null}).
     *
     * @see #setRenderer(XYItemRenderer)
     */
    public XYItemRenderer getRenderer() {
        return getRenderer(0);
    }

    /**
     * Returns the renderer with the specified index, or {@code null}.
     *
     * @param index  the renderer index (must be &gt;= 0).
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(int, XYItemRenderer)
     */
    public XYItemRenderer getRenderer(int index) {
        return this.renderers.get(index);
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
    public Map<Integer, XYItemRenderer> getRenderers() {
        return Collections.unmodifiableMap(this.renderers);
    }

    /**
     * Sets the renderer for the primary dataset and sends a change event to
     * all registered listeners.  If the renderer is set to {@code null},
     * no data will be displayed.
     *
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @see #getRenderer()
     */
    public void setRenderer(XYItemRenderer renderer) {
        setRenderer(0, renderer);
    }

    /**
     * Sets the renderer for the dataset with the specified index and sends a
     * change event to all registered listeners.  Note that each dataset should
     * have its own renderer, you should not use one renderer for multiple
     * datasets.
     *
     * @param index  the index (must be &gt;= 0).
     * @param renderer  the renderer.
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, XYItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    /**
     * Sets the renderer for the dataset with the specified index and, if
     * requested, sends a change event to all registered listeners.  Note that
     * each dataset should have its own renderer, you should not use one
     * renderer for multiple datasets.
     *
     * @param index  the index (must be &gt;= 0).
     * @param renderer  the renderer.
     * @param notify  notify listeners?
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, XYItemRenderer renderer, boolean notify) {
        XYItemRenderer existing = getRenderer(index);
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
     * @param renderers  the renderers ({@code null} not permitted).
     */
    public void setRenderers(XYItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the dataset rendering order.
     *
     * @return The order (never {@code null}).
     *
     * @see #setDatasetRenderingOrder(DatasetRenderingOrder)
     */
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.datasetRenderingOrder;
    }

    /**
     * Sets the rendering order and sends a {@link PlotChangeEvent} to all
     * registered listeners.  By default, the plot renders the primary dataset
     * last (so that the primary dataset overlays the secondary datasets).
     * You can reverse this if you want to.
     *
     * @param order  the rendering order ({@code null} not permitted).
     *
     * @see #getDatasetRenderingOrder()
     */
    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        Args.nullNotPermitted(order, "order");
        this.datasetRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the series rendering order.
     *
     * @return the order (never {@code null}).
     *
     * @see #setSeriesRenderingOrder(SeriesRenderingOrder)
     */
    public SeriesRenderingOrder getSeriesRenderingOrder() {
        return this.seriesRenderingOrder;
    }

    /**
     * Sets the series order and sends a {@link PlotChangeEvent} to all
     * registered listeners.  By default, the plot renders the primary series
     * last (so that the primary series appears to be on top).
     * You can reverse this if you want to.
     *
     * @param order  the rendering order ({@code null} not permitted).
     *
     * @see #getSeriesRenderingOrder()
     */
    public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
        Args.nullNotPermitted(order, "order");
        this.seriesRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the index of the specified renderer, or {@code -1} if the
     * renderer is not assigned to this plot.
     *
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @return The renderer index.
     */
    public int getIndexOf(XYItemRenderer renderer) {
        for (Map.Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() == renderer) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the renderer for the specified dataset (this is either the
     * renderer with the same index as the dataset or, if there isn't a
     * renderer with the same index, the default renderer).  If the dataset
     * does not belong to the plot, this method will return {@code null}.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The renderer (possibly {@code null}).
     */
    public XYItemRenderer getRendererForDataset(XYDataset<S> dataset) {
        int datasetIndex = indexOf(dataset);
        if (datasetIndex < 0) {
            return null;
        }
        XYItemRenderer result = this.renderers.get(datasetIndex);
        if (result == null) {
            result = getRenderer();
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
     * Returns {@code true} if the domain gridlines are visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setDomainGridlinesVisible(boolean)
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the domain grid-lines are
     * visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
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
     * Returns {@code true} if the domain minor gridlines are visible, and
     * {@code false} otherwise.
     *
     * @return {@code true} or {@code false}.
     *
     * @see #setDomainMinorGridlinesVisible(boolean)
     */
    public boolean isDomainMinorGridlinesVisible() {
        return this.domainMinorGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the domain minor grid-lines
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
     *
     * @param visible  the new value of the flag.
     *
     * @see #isDomainMinorGridlinesVisible()
     */
    public void setDomainMinorGridlinesVisible(boolean visible) {
        if (this.domainMinorGridlinesVisible != visible) {
            this.domainMinorGridlinesVisible = visible;
            fireChangeEvent();
        }
    }

    /**
     * Returns the stroke for the grid-lines (if any) plotted against the
     * domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainGridlineStroke(Stroke)
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the domain axis, and
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
     * Returns the stroke for the minor grid-lines (if any) plotted against the
     * domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainMinorGridlineStroke(Stroke)
     */
    public Stroke getDomainMinorGridlineStroke() {
        return this.domainMinorGridlineStroke;
    }

    /**
     * Sets the stroke for the minor grid lines plotted against the domain
     * axis, and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDomainMinorGridlineStroke()
     */
    public void setDomainMinorGridlineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainMinorGridlineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the grid lines (if any) plotted against the domain
     * axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainGridlinePaint(Paint)
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the domain axis, and
     * sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the paint for the minor grid lines (if any) plotted against the
     * domain axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainMinorGridlinePaint(Paint)
     */
    public Paint getDomainMinorGridlinePaint() {
        return this.domainMinorGridlinePaint;
    }

    /**
     * Sets the paint for the minor grid lines plotted against the domain axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainMinorGridlinePaint()
     */
    public void setDomainMinorGridlinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainMinorGridlinePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns {@code true} if the range axis grid is visible, and
     * {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangeGridlinesVisible(boolean)
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether the range axis grid lines
     * are visible.
     * <p>
     * If the flag value is changed, a {@link PlotChangeEvent} is sent to all
     * registered listeners.
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
     * Returns the stroke for the grid lines (if any) plotted against the
     * range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeGridlineStroke(Stroke)
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke for the grid lines plotted against the range axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the paint for the grid lines (if any) plotted against the range
     * axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeGridlinePaint(Paint)
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint for the grid lines plotted against the range axis and
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
     * Returns a flag that controls whether a zero baseline is
     * displayed for the domain axis.
     *
     * @return A boolean.
     *
     * @see #setDomainZeroBaselineVisible(boolean)
     */
    public boolean isDomainZeroBaselineVisible() {
        return this.domainZeroBaselineVisible;
    }

    /**
     * Sets the flag that controls whether the zero baseline is
     * displayed for the domain axis, and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #isDomainZeroBaselineVisible()
     */
    public void setDomainZeroBaselineVisible(boolean visible) {
        this.domainZeroBaselineVisible = visible;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used for the zero baseline against the domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainZeroBaselineStroke(Stroke)
     */
    public Stroke getDomainZeroBaselineStroke() {
        return this.domainZeroBaselineStroke;
    }

    /**
     * Sets the stroke for the zero baseline for the domain axis,
     * and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getRangeZeroBaselineStroke()
     */
    public void setDomainZeroBaselineStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainZeroBaselineStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the paint for the zero baseline (if any) plotted against the
     * domain axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainZeroBaselinePaint(Paint)
     */
    public Paint getDomainZeroBaselinePaint() {
        return this.domainZeroBaselinePaint;
    }

    /**
     * Sets the paint for the zero baseline plotted against the domain axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainZeroBaselinePaint()
     */
    public void setDomainZeroBaselinePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainZeroBaselinePaint = paint;
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
     * Returns the paint used for the domain tick bands.  If this is
     * {@code null}, no tick bands will be drawn.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setDomainTickBandPaint(Paint)
     */
    public Paint getDomainTickBandPaint() {
        return this.domainTickBandPaint;
    }

    /**
     * Sets the paint for the domain tick bands.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getDomainTickBandPaint()
     */
    public void setDomainTickBandPaint(Paint paint) {
        this.domainTickBandPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the paint used for the range tick bands.  If this is
     * {@code null}, no tick bands will be drawn.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setRangeTickBandPaint(Paint)
     */
    public Paint getRangeTickBandPaint() {
        return this.rangeTickBandPaint;
    }

    /**
     * Sets the paint for the range tick bands.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getRangeTickBandPaint()
     */
    public void setRangeTickBandPaint(Paint paint) {
        this.rangeTickBandPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the origin for the quadrants that can be displayed on the plot.
     * This defaults to (0, 0).
     *
     * @return The origin point (never {@code null}).
     *
     * @see #setQuadrantOrigin(Point2D)
     */
    public Point2D getQuadrantOrigin() {
        return this.quadrantOrigin;
    }

    /**
     * Sets the quadrant origin and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param origin  the origin ({@code null} not permitted).
     *
     * @see #getQuadrantOrigin()
     */
    public void setQuadrantOrigin(Point2D origin) {
        Args.nullNotPermitted(origin, "origin");
        this.quadrantOrigin = origin;
        fireChangeEvent();
    }

    /**
     * Returns the paint used for the specified quadrant.
     *
     * @param index  the quadrant index (0-3).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setQuadrantPaint(int, Paint)
     */
    public Paint getQuadrantPaint(int index) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        return this.quadrantPaint[index];
    }

    /**
     * Sets the paint used for the specified quadrant and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the quadrant index (0-3).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getQuadrantPaint(int)
     */
    public void setQuadrantPaint(int index, Paint paint) {
        if (index < 0 || index > 3) {
            throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3.");
        }
        this.quadrantPaint[index] = paint;
        fireChangeEvent();
    }

    /**
     * Adds a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #addDomainMarker(Marker, Layer)
     * @see #clearDomainMarkers()
     */
    public void addDomainMarker(Marker marker) {
        // defer argument checking...
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background).
     *
     * @see #addDomainMarker(int, Marker, Layer)
     */
    public void addDomainMarker(Marker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
    }

    /**
     * Clears all the (foreground and background) domain markers and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #addDomainMarker(int, Marker, Layer)
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
     * Clears the (foreground and background) domain markers for a particular
     * renderer and sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the renderer index.
     *
     * @see #clearRangeMarkers(int)
     */
    public void clearDomainMarkers(int index) {
        if (this.backgroundDomainMarkers != null) {
            List<Marker> markers = this.backgroundDomainMarkers.get(index);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            List<Marker> markers = this.foregroundDomainMarkers.get(index);
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
     * Adds a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis (that the renderer is mapped to), however this is
     * entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     *
     * @see #clearDomainMarkers(int)
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public void addDomainMarker(int index, Marker marker, Layer layer) {
        addDomainMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for a specific dataset/renderer and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the domain axis (that the renderer is mapped to), however this is
     * entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners?
     */
    public void addDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        if (layer == Layer.FOREGROUND) {
            List<Marker> markers = this.foregroundDomainMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            List<Marker> markers = this.backgroundDomainMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Removes a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker  the marker.
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(Marker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(Marker marker, Layer layer) {
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
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
        List<Marker> markers;
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
     * Adds a marker for the range axis and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #addRangeMarker(Marker, Layer)
     */
    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for the range axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background).
     *
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    /**
     * Clears all the range markers and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @see #clearRangeMarkers()
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
     * Adds a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     *
     * @see #clearRangeMarkers(int)
     * @see #addDomainMarker(int, Marker, Layer)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for a specific dataset/renderer and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to the range axis, however this is entirely up to the renderer.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners?
     */
    public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        if (layer == Layer.FOREGROUND) {
            List<Marker> markers = this.foregroundRangeMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            List<Marker> markers = this.backgroundRangeMarkers.computeIfAbsent(index, k -> new ArrayList<>());
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears the (foreground and background) range markers for a particular
     * renderer.
     *
     * @param index  the renderer index.
     */
    public void clearRangeMarkers(int index) {
        if (this.backgroundRangeMarkers != null) {
            List<Marker> markers = this.backgroundRangeMarkers.get(index);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            List<Marker> markers = this.foregroundRangeMarkers.get(index);
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
     */
    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        List<Marker> markers;
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
     * Adds an annotation to the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     *
     * @see #getAnnotations()
     * @see #removeAnnotation(XYAnnotation)
     */
    public void addAnnotation(XYAnnotation annotation) {
        addAnnotation(annotation, true);
    }

    /**
     * Adds an annotation to the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void addAnnotation(XYAnnotation annotation, boolean notify) {
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
     * @see #addAnnotation(XYAnnotation)
     * @see #getAnnotations()
     */
    public boolean removeAnnotation(XYAnnotation annotation) {
        return removeAnnotation(annotation, true);
    }

    /**
     * Removes an annotation from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @return A boolean (indicates whether the annotation was removed).
     */
    public boolean removeAnnotation(XYAnnotation annotation, boolean notify) {
        Args.nullNotPermitted(annotation, "annotation");
        boolean removed = this.annotations.remove(annotation);
        annotation.removeChangeListener(this);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Returns the list of annotations.
     *
     * @return The list of annotations.
     *
     * @see #addAnnotation(XYAnnotation)
     */
    public List<XYAnnotation> getAnnotations() {
        return new ArrayList<>(this.annotations);
    }

    /**
     * Clears all the annotations and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @see #addAnnotation(XYAnnotation)
     */
    public void clearAnnotations() {
        for (XYAnnotation annotation : this.annotations) {
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
     * Calculates the space required for all the axes in the plot.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     *
     * @return The required space.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = calculateRangeAxisSpace(g2, plotArea, space);
        Rectangle2D revPlotArea = space.shrink(plotArea, null);
        space = calculateDomainAxisSpace(g2, revPlotArea, space);
        return space;
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
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
        } else {
            // reserve space for the domain axes...
            for (ValueAxis axis : this.domainAxes.values()) {
                if (axis != null) {
                    RectangleEdge edge = getDomainAxisEdge(findDomainAxisIndex(axis));
                    space = axis.reserveSpace(g2, this, plotArea, edge, space);
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
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
        } else {
            // reserve space for the range axes...
            for (ValueAxis axis : this.rangeAxes.values()) {
                if (axis != null) {
                    RectangleEdge edge = getRangeAxisEdge(findRangeAxisIndex(axis));
                    space = axis.reserveSpace(g2, this, plotArea, edge, space);
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
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        for (Entry<Integer, ValueAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // visit the renderers
        for (Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        visitor.visit(this);
    }

    /**
     * Draws the plot within the specified area on a graphics device.
     *
     * @param g2  the graphics device.
     * @param area  the plot area (in Java2D space).
     * @param anchor  an anchor point in Java2D space ({@code null}
     *                permitted).
     * @param parentState  the state from the parent plot, if there is one
     *                     ({@code null} permitted).
     * @param info  collects chart drawing information ({@code null}
     *              permitted).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        // if the plot area is too small, just return...
        if ((area.getWidth() <= MINIMUM_WIDTH_TO_DRAW) || (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW)) {
            return;
        }
        // record the plot area...
        if (info != null) {
            info.setPlotArea(area);
        }
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        dataArea = integerise(dataArea);
        if (dataArea.isEmpty()) {
            return;
        }
        createAndAddEntity((Rectangle2D) dataArea.clone(), info, null, null);
        if (info != null) {
            info.setDataArea(dataArea);
        }
        // draw the plot background and axes...
        drawBackground(g2, dataArea);
        Map<Axis, AxisState> axisStateMap = drawAxes(g2, area, dataArea, info);
        PlotOrientation orient = getOrientation();
        // the anchor point is typically the point where the mouse last
        // clicked - the crosshairs will be driven off this point...
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = null;
        }
        CrosshairState crosshairState = new CrosshairState();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);
        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis domainAxis = getDomainAxis();
            if (domainAxis != null) {
                double x;
                if (orient == PlotOrientation.VERTICAL) {
                    x = domainAxis.java2DToValue(anchor.getX(), dataArea, getDomainAxisEdge());
                } else {
                    x = domainAxis.java2DToValue(anchor.getY(), dataArea, getDomainAxisEdge());
                }
                crosshairState.setAnchorX(x);
            }
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (orient == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
                } else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setCrosshairX(getDomainCrosshairValue());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        Shape originalClip = g2.getClip();
        Composite originalComposite = g2.getComposite();
        g2.clip(dataArea);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        AxisState domainAxisState = axisStateMap.get(getDomainAxis());
        if (domainAxisState == null) {
            if (parentState != null) {
                domainAxisState = parentState.getSharedAxisStates().get(getDomainAxis());
            }
        }
        AxisState rangeAxisState = axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = parentState.getSharedAxisStates().get(getRangeAxis());
            }
        }
        if (domainAxisState != null) {
            drawDomainTickBands(g2, dataArea, domainAxisState.getTicks());
        }
        if (rangeAxisState != null) {
            drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks());
        }
        if (domainAxisState != null) {
            drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
            drawZeroDomainBaseline(g2, dataArea);
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
        // draw the markers that are associated with a specific dataset...
        for (XYDataset<S> dataset : this.datasets.values()) {
            int datasetIndex = indexOf(dataset);
            drawDomainMarkers(g2, dataArea, datasetIndex, Layer.BACKGROUND);
        }
        for (XYDataset<S> dataset : this.datasets.values()) {
            int datasetIndex = indexOf(dataset);
            drawRangeMarkers(g2, dataArea, datasetIndex, Layer.BACKGROUND);
        }
        // now draw annotations and render data items...
        boolean foundData = false;
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        List<Integer> rendererIndices = getRendererIndices(order);
        List<Integer> datasetIndices = getDatasetIndices(order);
        // draw background annotations
        for (int i : rendererIndices) {
            XYItemRenderer renderer = getRenderer(i);
            if (renderer != null) {
                ValueAxis domainAxis = getDomainAxisForDataset(i);
                ValueAxis rangeAxis = getRangeAxisForDataset(i);
                renderer.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, info);
            }
        }
        // render data items...
        for (int datasetIndex : datasetIndices) {
            foundData = render(g2, dataArea, datasetIndex, info, crosshairState) || foundData;
        }
        // draw foreground annotations
        for (int i : rendererIndices) {
            XYItemRenderer renderer = getRenderer(i);
            if (renderer != null) {
                ValueAxis domainAxis = getDomainAxisForDataset(i);
                ValueAxis rangeAxis = getRangeAxisForDataset(i);
                renderer.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, info);
            }
        }
        // draw domain crosshair if required...
        int datasetIndex = crosshairState.getDatasetIndex();
        ValueAxis xAxis = getDomainAxisForDataset(datasetIndex);
        RectangleEdge xAxisEdge = getDomainAxisEdge(getDomainAxisIndex(xAxis));
        if (!this.domainCrosshairLockedOnData && anchor != null) {
            double xx;
            if (orient == PlotOrientation.VERTICAL) {
                xx = xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge);
            } else {
                xx = xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
            }
            crosshairState.setCrosshairX(xx);
        }
        setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
        if (isDomainCrosshairVisible()) {
            double x = getDomainCrosshairValue();
            Paint paint = getDomainCrosshairPaint();
            Stroke stroke = getDomainCrosshairStroke();
            drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
        }
        // draw range crosshair if required...
        ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
        RectangleEdge yAxisEdge = getRangeAxisEdge(getRangeAxisIndex(yAxis));
        if (!this.rangeCrosshairLockedOnData && anchor != null) {
            double yy;
            if (orient == PlotOrientation.VERTICAL) {
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
            drawRangeCrosshair(g2, dataArea, orient, y, yAxis, stroke, paint);
        }
        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }
        for (int i : rendererIndices) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i : rendererIndices) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        drawAnnotations(g2, dataArea, info);
        if (this.shadowGenerator != null && !suppressShadow) {
            BufferedImage shadowImage = this.shadowGenerator.createDropShadow(dataImage);
            g2 = savedG2;
            g2.drawImage(shadowImage, (int) dataArea.getX() + this.shadowGenerator.calculateOffsetX(), (int) dataArea.getY() + this.shadowGenerator.calculateOffsetY(), null);
            g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
        }
        g2.setClip(originalClip);
        g2.setComposite(originalComposite);
        drawOutline(g2, dataArea);
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
        for (Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
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

    private List<Integer> getRendererIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<>();
        for (Entry<Integer, XYItemRenderer> entry : this.renderers.entrySet()) {
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
     * Draws the background for the plot.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     */
    @Override
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, this.orientation);
        drawQuadrants(g2, area);
        drawBackgroundImage(g2, area);
    }

    /**
     * Draws the quadrants.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     *
     * @see #setQuadrantOrigin(Point2D)
     * @see #setQuadrantPaint(int, Paint)
     */
    protected void drawQuadrants(Graphics2D g2, Rectangle2D area) {
        //  0 | 1
        //  --+--
        //  2 | 3
        boolean somethingToDraw = false;
        ValueAxis xAxis = getDomainAxis();
        if (xAxis == null) {
            // we can't draw quadrants without a valid x-axis
            return;
        }
        double x = xAxis.getRange().constrain(this.quadrantOrigin.getX());
        double xx = xAxis.valueToJava2D(x, area, getDomainAxisEdge());
        ValueAxis yAxis = getRangeAxis();
        if (yAxis == null) {
            // we can't draw quadrants without a valid y-axis
            return;
        }
        double y = yAxis.getRange().constrain(this.quadrantOrigin.getY());
        double yy = yAxis.valueToJava2D(y, area, getRangeAxisEdge());
        double xmin = xAxis.getLowerBound();
        double xxmin = xAxis.valueToJava2D(xmin, area, getDomainAxisEdge());
        double xmax = xAxis.getUpperBound();
        double xxmax = xAxis.valueToJava2D(xmax, area, getDomainAxisEdge());
        double ymin = yAxis.getLowerBound();
        double yymin = yAxis.valueToJava2D(ymin, area, getRangeAxisEdge());
        double ymax = yAxis.getUpperBound();
        double yymax = yAxis.valueToJava2D(ymax, area, getRangeAxisEdge());
        Rectangle2D[] r = new Rectangle2D[] { null, null, null, null };
        if (this.quadrantPaint[0] != null) {
            if (x > xmin && y < ymax) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[0] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmin, xx), Math.abs(yy - yymax), Math.abs(xx - xxmin));
                } else {
                    // PlotOrientation.VERTICAL
                    r[0] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymax, yy), Math.abs(xx - xxmin), Math.abs(yy - yymax));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[1] != null) {
            if (x < xmax && y < ymax) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[1] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmax, xx), Math.abs(yy - yymax), Math.abs(xx - xxmax));
                } else {
                    // PlotOrientation.VERTICAL
                    r[1] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymax, yy), Math.abs(xx - xxmax), Math.abs(yy - yymax));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[2] != null) {
            if (x > xmin && y > ymin) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[2] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmin, xx), Math.abs(yy - yymin), Math.abs(xx - xxmin));
                } else {
                    // PlotOrientation.VERTICAL
                    r[2] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymin, yy), Math.abs(xx - xxmin), Math.abs(yy - yymin));
                }
                somethingToDraw = true;
            }
        }
        if (this.quadrantPaint[3] != null) {
            if (x < xmax && y > ymin) {
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    r[3] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmax, xx), Math.abs(yy - yymin), Math.abs(xx - xxmax));
                } else {
                    // PlotOrientation.VERTICAL
                    r[3] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymin, yy), Math.abs(xx - xxmax), Math.abs(yy - yymin));
                }
                somethingToDraw = true;
            }
        }
        if (somethingToDraw) {
            Composite originalComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getBackgroundAlpha()));
            for (int i = 0; i < 4; i++) {
                if (this.quadrantPaint[i] != null && r[i] != null) {
                    g2.setPaint(this.quadrantPaint[i]);
                    g2.fill(r[i]);
                }
            }
            g2.setComposite(originalComposite);
        }
    }

    /**
     * Draws the domain tick bands, if any.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #setDomainTickBandPaint(Paint)
     */
    public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        Paint bandPaint = getDomainTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis xAxis = getDomainAxis();
            double previous = xAxis.getLowerBound();
            for (ValueTick tick : ticks) {
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = xAxis.getUpperBound();
            if (fillBand) {
                getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, end);
            }
        }
    }

    /**
     * Draws the range tick bands, if any.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #setRangeTickBandPaint(Paint)
     */
    public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        Paint bandPaint = getRangeTickBandPaint();
        if (bandPaint != null) {
            boolean fillBand = false;
            ValueAxis axis = getRangeAxis();
            double previous = axis.getLowerBound();
            for (ValueTick tick : ticks) {
                double current = tick.getValue();
                if (fillBand) {
                    getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, current);
                }
                previous = current;
                fillBand = !fillBand;
            }
            double end = axis.getUpperBound();
            if (fillBand) {
                getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, end);
            }
        }
    }

    /**
     * A utility method for drawing the axes.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param plotArea  the plot area ({@code null} not permitted).
     * @param dataArea  the data area ({@code null} not permitted).
     * @param plotState  collects information about the plot ({@code null}
     *                   permitted).
     *
     * @return A map containing the state for each axis drawn.
     */
    protected Map<Axis, AxisState> drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisCollection axisCollection = new AxisCollection();
        // add domain axes to lists...
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                int axisIndex = findDomainAxisIndex(axis);
                axisCollection.add(axis, getDomainAxisEdge(axisIndex));
            }
        }
        // add range axes to lists...
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                int axisIndex = findRangeAxisIndex(axis);
                axisCollection.add(axis, getRangeAxisEdge(axisIndex));
            }
        }
        Map<Axis, AxisState> axisStateMap = new HashMap<>();
        // draw the top axes
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtTop()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtBottom()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtLeft()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtRight()) {
            AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
            cursor = info.getCursor();
            axisStateMap.put(axis, info);
        }
        return axisStateMap;
    }

    /**
     * Draws a representation of the data within the dataArea region, using the
     * current renderer.
     * <P>
     * The {@code info} and {@code crosshairState} arguments may be
     * {@code null}.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param index  the dataset index.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  collects crosshair information
     *                        ({@code null} permitted).
     *
     * @return A flag that indicates whether any data was actually rendered.
     */
    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState) {
        boolean foundData = false;
        XYDataset<S> dataset = getDataset(index);
        if (!DatasetUtils.isEmptyOrNull(dataset)) {
            foundData = true;
            ValueAxis xAxis = getDomainAxisForDataset(index);
            ValueAxis yAxis = getRangeAxisForDataset(index);
            if (xAxis == null || yAxis == null) {
                // can't render anything without axes
                return foundData;
            }
            XYItemRenderer renderer = getRenderer(index);
            if (renderer == null) {
                renderer = getRenderer();
                if (renderer == null) {
                    // no default renderer available
                    return foundData;
                }
            }
            XYItemRendererState state = renderer.initialise(g2, dataArea, this, dataset, info);
            int passCount = renderer.getPassCount();
            SeriesRenderingOrder seriesOrder = getSeriesRenderingOrder();
            if (seriesOrder == SeriesRenderingOrder.REVERSE) {
                //render series in reverse order
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = seriesCount - 1; series >= 0; series--) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (lastItem == -1) {
                            continue;
                        }
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtils.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            } else {
                //render series in forward order
                for (int pass = 0; pass < passCount; pass++) {
                    int seriesCount = dataset.getSeriesCount();
                    for (int series = 0; series < seriesCount; series++) {
                        int firstItem = 0;
                        int lastItem = dataset.getItemCount(series) - 1;
                        if (state.getProcessVisibleItemsOnly()) {
                            int[] itemBounds = RendererUtils.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
                            firstItem = Math.max(itemBounds[0] - 1, 0);
                            lastItem = Math.min(itemBounds[1] + 1, lastItem);
                        }
                        state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                        for (int item = firstItem; item <= lastItem; item++) {
                            renderer.drawItem(g2, state, dataArea, info, this, xAxis, yAxis, dataset, series, item, crosshairState, pass);
                        }
                        state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
                    }
                }
            }
        }
        return foundData;
    }

    /**
     * Returns the domain axis for a dataset.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The axis.
     */
    public ValueAxis getDomainAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        ValueAxis valueAxis;
        List<Integer> axisIndices = this.datasetToDomainAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            valueAxis = getDomainAxis(axisIndex);
        } else {
            valueAxis = getDomainAxis(0);
        }
        return valueAxis;
    }

    /**
     * Returns the range axis for a dataset.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The axis.
     */
    public ValueAxis getRangeAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        ValueAxis valueAxis;
        List<Integer> axisIndices = this.datasetToRangeAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            valueAxis = getRangeAxis(axisIndex);
        } else {
            valueAxis = getRangeAxis(0);
        }
        return valueAxis;
    }

    /**
     * Draws the gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param ticks  the ticks.
     *
     * @see #drawRangeGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        // no renderer, no gridlines...
        if (getRenderer() == null) {
            return;
        }
        // draw the domain grid lines, if any...
        if (isDomainGridlinesVisible() || isDomainMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            for (ValueTick tick : ticks) {
                boolean paintLine = false;
                if ((tick.getTickType() == TickType.MINOR) && isDomainMinorGridlinesVisible()) {
                    gridStroke = getDomainMinorGridlineStroke();
                    gridPaint = getDomainMinorGridlinePaint();
                    paintLine = true;
                } else if ((tick.getTickType() == TickType.MAJOR) && isDomainGridlinesVisible()) {
                    gridStroke = getDomainGridlineStroke();
                    gridPaint = getDomainGridlinePaint();
                    paintLine = true;
                }
                XYItemRenderer r = getRenderer();
                if ((r instanceof AbstractXYItemRenderer) && paintLine) {
                    r.drawDomainLine(g2, this, getDomainAxis(), dataArea, tick.getValue(), gridPaint, gridStroke);
                }
            }
        }
    }

    /**
     * Draws the gridlines for the plot's primary range axis, if they are
     * visible.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     * @param ticks  the ticks.
     *
     * @see #drawDomainGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D area, List<ValueTick> ticks) {
        // no renderer, no gridlines...
        if (getRenderer() == null) {
            return;
        }
        // draw the range grid lines, if any...
        if (isRangeGridlinesVisible() || isRangeMinorGridlinesVisible()) {
            Stroke gridStroke = null;
            Paint gridPaint = null;
            ValueAxis axis = getRangeAxis();
            if (axis != null) {
                for (ValueTick tick : ticks) {
                    boolean paintLine = false;
                    if ((tick.getTickType() == TickType.MINOR) && isRangeMinorGridlinesVisible()) {
                        gridStroke = getRangeMinorGridlineStroke();
                        gridPaint = getRangeMinorGridlinePaint();
                        paintLine = true;
                    } else if ((tick.getTickType() == TickType.MAJOR) && isRangeGridlinesVisible()) {
                        gridStroke = getRangeGridlineStroke();
                        gridPaint = getRangeGridlinePaint();
                        paintLine = true;
                    }
                    if ((tick.getValue() != 0.0 || !isRangeZeroBaselineVisible()) && paintLine) {
                        getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, tick.getValue(), gridPaint, gridStroke);
                    }
                }
            }
        }
    }

    /**
     * Draws a baseline across the chart at value zero on the domain axis.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     *
     * @see #setDomainZeroBaselineVisible(boolean)
     */
    protected void drawZeroDomainBaseline(Graphics2D g2, Rectangle2D area) {
        if (isDomainZeroBaselineVisible() && getRenderer() != null) {
            getRenderer().drawDomainLine(g2, this, getDomainAxis(), area, 0.0, this.domainZeroBaselinePaint, this.domainZeroBaselineStroke);
        }
    }

    /**
     * Draws a baseline across the chart at value zero on the range axis.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     *
     * @see #setRangeZeroBaselineVisible(boolean)
     */
    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (isRangeZeroBaselineVisible()) {
            getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, 0.0, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
        }
    }

    /**
     * Draws the annotations for the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param info  the chart rendering info.
     */
    public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
        for (XYAnnotation annotation : this.annotations) {
            ValueAxis xAxis = getDomainAxis();
            ValueAxis yAxis = getRangeAxis();
            annotation.draw(g2, this, dataArea, xAxis, yAxis, 0, info);
        }
    }

    /**
     * Draws the domain markers (if any) for an axis and layer.  This method is
     * typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the dataset/renderer index.
     * @param layer  the layer (foreground or background).
     */
    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        // check that the renderer has a corresponding dataset (it doesn't
        // matter if the dataset is null)
        if (index >= getDatasetCount()) {
            return;
        }
        Collection<Marker> markers = getDomainMarkers(index, layer);
        ValueAxis axis = getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            for (Marker marker : markers) {
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    /**
     * Draws the range markers (if any) for a renderer and layer.  This method
     * is typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the renderer index.
     * @param layer  the layer (foreground or background).
     */
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        XYItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        // check that the renderer has a corresponding dataset (it doesn't
        // matter if the dataset is null)
        if (index >= getDatasetCount()) {
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
     * Returns the list of domain markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of domain markers.
     *
     * @see #getRangeMarkers(Layer)
     */
    public Collection<Marker> getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    /**
     * Returns the list of range markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of range markers.
     *
     * @see #getDomainMarkers(Layer)
     */
    public Collection<Marker> getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
    }

    /**
     * Returns a collection of domain markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     *
     * @see #getRangeMarkers(int, Layer)
     */
    public Collection<Marker> getDomainMarkers(int index, Layer layer) {
        Collection<Marker> result = null;
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundDomainMarkers.get(index);
        } else if (layer == Layer.BACKGROUND) {
            result = this.backgroundDomainMarkers.get(index);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    /**
     * Returns a collection of range markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     *
     * @see #getDomainMarkers(int, Layer)
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
     * Utility method for drawing a horizontal line across the data area of the
     * plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param value  the coordinate, where to draw the line.
     * @param stroke  the stroke to use.
     * @param paint  the paint to use.
     */
    protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = getRangeAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getDomainAxis();
        }
        if (axis.getRange().contains(value)) {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
    }

    /**
     * Draws a domain crosshair.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param orientation  the plot orientation.
     * @param value  the crosshair value.
     * @param axis  the axis against which the value is measured.
     * @param stroke  the stroke used to draw the crosshair line.
     * @param paint  the paint used to draw the crosshair line.
     */
    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (!axis.getRange().contains(value)) {
            return;
        }
        Line2D line;
        if (orientation == PlotOrientation.VERTICAL) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        } else {
            double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
            line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        }
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Utility method for drawing a vertical line on the data area of the plot.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param value  the coordinate, where to draw the line.
     * @param stroke  the stroke to use.
     * @param paint  the paint to use.
     */
    protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        ValueAxis axis = getDomainAxis();
        if (getOrientation() == PlotOrientation.HORIZONTAL) {
            axis = getRangeAxis();
        }
        if (axis.getRange().contains(value)) {
            double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
            Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
            g2.setStroke(stroke);
            g2.setPaint(paint);
            g2.draw(line);
        }
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
     */
    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (!axis.getRange().contains(value)) {
            return;
        }
        Object saved = g2.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
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
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, saved);
    }

    /**
     * Handles a 'click' on the plot by updating the anchor values.
     *
     * @param x  the x-coordinate, where the click occurred, in Java2D space.
     * @param y  the y-coordinate, where the click occurred, in Java2D space.
     * @param info  object containing information about the plot dimensions.
     */
    @Override
    public void handleClick(int x, int y, PlotRenderingInfo info) {
        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            // set the anchor value for the horizontal axis...
            ValueAxis xaxis = getDomainAxis();
            if (xaxis != null) {
                double hvalue = xaxis.java2DToValue(x, info.getDataArea(), getDomainAxisEdge());
                setDomainCrosshairValue(hvalue);
            }
            // set the anchor value for the vertical axis...
            ValueAxis yaxis = getRangeAxis();
            if (yaxis != null) {
                double vvalue = yaxis.java2DToValue(y, info.getDataArea(), getRangeAxisEdge());
                setRangeCrosshairValue(vvalue);
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
    private List<XYDataset<S>> getDatasetsMappedToDomainAxis(Integer axisIndex) {
        Args.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset<S>> result = new ArrayList<>();
        for (Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            int index = entry.getKey();
            List<Integer> mappedAxes = this.datasetToDomainAxesMap.get(index);
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * A utility method that returns a list of datasets that are mapped to a
     * particular axis.
     *
     * @param axisIndex  the axis index ({@code null} not permitted).
     *
     * @return A list of datasets.
     */
    private List<XYDataset<S>> getDatasetsMappedToRangeAxis(Integer axisIndex) {
        Args.nullNotPermitted(axisIndex, "axisIndex");
        List<XYDataset<S>> result = new ArrayList<>();
        for (Entry<Integer, XYDataset<S>> entry : this.datasets.entrySet()) {
            int index = entry.getKey();
            List<Integer> mappedAxes = this.datasetToRangeAxesMap.get(index);
            if (mappedAxes == null) {
                if (axisIndex.equals(ZERO)) {
                    result.add(entry.getValue());
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(entry.getValue());
                }
            }
        }
        return result;
    }

    /**
     * Returns the index of the given domain axis.
     *
     * @param axis  the axis.
     *
     * @return The axis index.
     *
     * @see #getRangeAxisIndex(ValueAxis)
     */
    public int getDomainAxisIndex(ValueAxis axis) {
        int result = findDomainAxisIndex(axis);
        if (result < 0) {
            // try the parent plot
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> p = (XYPlot<S>) parent;
                result = p.getDomainAxisIndex(axis);
            }
        }
        return result;
    }

    private int findDomainAxisIndex(ValueAxis axis) {
        for (Map.Entry<Integer, ValueAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the index of the given range axis.
     *
     * @param axis  the axis.
     *
     * @return The axis index.
     *
     * @see #getDomainAxisIndex(ValueAxis)
     */
    public int getRangeAxisIndex(ValueAxis axis) {
        int result = findRangeAxisIndex(axis);
        if (result < 0) {
            // try the parent plot
            Plot parent = getParent();
            if (parent instanceof XYPlot) {
                @SuppressWarnings("unchecked")
                XYPlot<S> p = (XYPlot<S>) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
        return result;
    }

    private int findRangeAxisIndex(ValueAxis axis) {
        for (Map.Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
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
        List<XYDataset<S>> mappedDatasets = new ArrayList<>();
        List<XYAnnotation> includedAnnotations = new ArrayList<>();
        boolean isDomainAxis = true;
        // is it a domain axis?
        int domainIndex = getDomainAxisIndex(axis);
        if (domainIndex >= 0) {
            isDomainAxis = true;
            mappedDatasets.addAll(getDatasetsMappedToDomainAxis(domainIndex));
            if (domainIndex == 0) {
                // grab the plot's annotations
                for (XYAnnotation annotation : this.annotations) {
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }
        // or is it a range axis?
        int rangeIndex = getRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            isDomainAxis = false;
            mappedDatasets.addAll(getDatasetsMappedToRangeAxis(rangeIndex));
            if (rangeIndex == 0) {
                for (XYAnnotation annotation : this.annotations) {
                    if (annotation instanceof XYAnnotationBoundsInfo) {
                        includedAnnotations.add(annotation);
                    }
                }
            }
        }
        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
        for (XYDataset<S> d : mappedDatasets) {
            if (d != null) {
                XYItemRenderer r = getRendererForDataset(d);
                if (isDomainAxis) {
                    if (r != null) {
                        result = Range.combine(result, r.findDomainBounds(d));
                    } else {
                        result = Range.combine(result, DatasetUtils.findDomainBounds(d));
                    }
                } else {
                    if (r != null) {
                        result = Range.combine(result, r.findRangeBounds(d));
                    } else {
                        result = Range.combine(result, DatasetUtils.findRangeBounds(d));
                    }
                }
                if (r != null) {
                    for (XYAnnotation annotation : r.getAnnotations()) {
                        if (annotation instanceof XYAnnotationBoundsInfo) {
                            includedAnnotations.add(annotation);
                        }
                    }
                }
            }
        }
        for (XYAnnotation includedAnnotation : includedAnnotations) {
            XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo) includedAnnotation;
            if (xyabi.getIncludeInDataBounds()) {
                if (isDomainAxis) {
                    result = Range.combine(result, xyabi.getXRange());
                } else {
                    result = Range.combine(result, xyabi.getYRange());
                }
            }
        }
        return result;
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
     * The axis ranges are updated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        configureDomainAxes();
        configureRangeAxes();
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
        // if the event was caused by a change to series visibility, then
        // the axis ranges might need updating...
        if (event.getSeriesVisibilityChanged()) {
            configureDomainAxes();
            configureRangeAxes();
        }
        fireChangeEvent();
    }

    /**
     * Returns a flag indicating whether the domain crosshair is visible.
     *
     * @return The flag.
     *
     * @see #setDomainCrosshairVisible(boolean)
     */
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether the domain crosshair is visible
     * and, if the flag changes, sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the new value of the flag.
     *
     * @see #isDomainCrosshairVisible()
     */
    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns a flag indicating whether the crosshair should "lock-on"
     * to actual data values.
     *
     * @return The flag.
     *
     * @see #setDomainCrosshairLockedOnData(boolean)
     */
    public boolean isDomainCrosshairLockedOnData() {
        return this.domainCrosshairLockedOnData;
    }

    /**
     * Sets the flag indicating whether the domain crosshair should
     * "lock-on" to actual data values.  If the flag value changes, this
     * method sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param flag  the flag.
     *
     * @see #isDomainCrosshairLockedOnData()
     */
    public void setDomainCrosshairLockedOnData(boolean flag) {
        if (this.domainCrosshairLockedOnData != flag) {
            this.domainCrosshairLockedOnData = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the domain crosshair value.
     *
     * @return The value.
     *
     * @see #setDomainCrosshairValue(double)
     */
    public double getDomainCrosshairValue() {
        return this.domainCrosshairValue;
    }

    /**
     * Sets the domain crosshair value and sends a {@link PlotChangeEvent} to
     * all registered listeners (provided that the domain crosshair is visible).
     *
     * @param value  the value.
     *
     * @see #getDomainCrosshairValue()
     */
    public void setDomainCrosshairValue(double value) {
        setDomainCrosshairValue(value, true);
    }

    /**
     * Sets the domain crosshair value and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners (provided that the
     * domain crosshair is visible).
     *
     * @param value  the new value.
     * @param notify  notify listeners?
     *
     * @see #getDomainCrosshairValue()
     */
    public void setDomainCrosshairValue(double value, boolean notify) {
        this.domainCrosshairValue = value;
        if (isDomainCrosshairVisible() && notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the {@link Stroke} used to draw the crosshair (if visible).
     *
     * @return The crosshair stroke (never {@code null}).
     *
     * @see #setDomainCrosshairStroke(Stroke)
     * @see #isDomainCrosshairVisible()
     * @see #getDomainCrosshairPaint()
     */
    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    /**
     * Sets the Stroke used to draw the crosshairs (if visible) and notifies
     * registered listeners that the axis has been modified.
     *
     * @param stroke  the new crosshair stroke ({@code null} not permitted).
     *
     * @see #getDomainCrosshairStroke()
     */
    public void setDomainCrosshairStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainCrosshairStroke = stroke;
        fireChangeEvent();
    }

    /**
     * Returns the domain crosshair paint.
     *
     * @return The crosshair paint (never {@code null}).
     *
     * @see #setDomainCrosshairPaint(Paint)
     * @see #isDomainCrosshairVisible()
     * @see #getDomainCrosshairStroke()
     */
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    /**
     * Sets the paint used to draw the crosshairs (if visible) and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint the new crosshair paint ({@code null} not permitted).
     *
     * @see #getDomainCrosshairPaint()
     */
    public void setDomainCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns a flag indicating whether the range crosshair is visible.
     *
     * @return The flag.
     *
     * @see #setRangeCrosshairVisible(boolean)
     * @see #isDomainCrosshairVisible()
     */
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether the range crosshair is visible.
     * If the flag value changes, this method sends a {@link PlotChangeEvent}
     * to all registered listeners.
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
     * "lock-on" to actual data values.  If the flag value changes, this method
     * sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Sets the range crosshair value.
     * <P>
     * Registered listeners are notified that the plot has been modified, but
     * only if the crosshair is visible.
     *
     * @param value  the new value.
     *
     * @see #getRangeCrosshairValue()
     */
    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, true);
    }

    /**
     * Sets the range crosshair value and sends a {@link PlotChangeEvent} to
     * all registered listeners, but only if the crosshair is visible.
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
     * Returns the stroke used to draw the crosshair (if visible).
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
     * Sets the stroke used to draw the crosshairs (if visible) and sends a
     * {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the range crosshair paint.
     *
     * @return The crosshair paint (never {@code null}).
     *
     * @see #setRangeCrosshairPaint(Paint)
     * @see #isRangeCrosshairVisible()
     * @see #getRangeCrosshairStroke()
     */
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    /**
     * Sets the paint used to color the crosshairs (if visible) and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint the new crosshair paint ({@code null} not permitted).
     *
     * @see #getRangeCrosshairPaint()
     */
    public void setRangeCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeCrosshairPaint = paint;
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
     * Sets the fixed domain axis space and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
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
     * Sets the fixed range axis space and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
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
     * Returns {@code true} if panning is enabled for the domain axes,
     * and {@code false} otherwise.
     *
     * @return A boolean.
     */
    @Override
    public boolean isDomainPannable() {
        return this.domainPannable;
    }

    /**
     * Sets the flag that enables or disables panning of the plot along the
     * domain axes.
     *
     * @param pannable  the new flag value.
     */
    public void setDomainPannable(boolean pannable) {
        this.domainPannable = pannable;
    }

    /**
     * Returns {@code true} if panning is enabled for the range axis/axes,
     * and {@code false} otherwise.  The default value is {@code false}.
     *
     * @return A boolean.
     */
    @Override
    public boolean isRangePannable() {
        return this.rangePannable;
    }

    /**
     * Sets the flag that enables or disables panning of the plot along
     * the range axis/axes.
     *
     * @param pannable  the new flag value.
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
        if (!isDomainPannable()) {
            return;
        }
        int domainAxisCount = getDomainAxisCount();
        for (int i = 0; i < domainAxisCount; i++) {
            ValueAxis axis = getDomainAxis(i);
            if (axis == null) {
                continue;
            }
            axis.pan(axis.isInverted() ? -percent : percent);
        }
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
        int rangeAxisCount = getRangeAxisCount();
        for (int i = 0; i < rangeAxisCount; i++) {
            ValueAxis axis = getRangeAxis(i);
            if (axis == null) {
                continue;
            }
            axis.pan(axis.isInverted() ? -percent : percent);
        }
    }

    /**
     * Multiplies the range on the domain axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point (in Java2D space).
     *
     * @see #zoomRangeAxes(double, PlotRenderingInfo, Point2D)
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
        // delegate to other method
        zoomDomainAxes(factor, info, source, false);
    }

    /**
     * Multiplies the range on the domain axis/axes by the specified factor.
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
        // perform the zoom on each domain axis
        for (ValueAxis xAxis : this.domainAxes.values()) {
            if (xAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceX = source.getX();
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    sourceX = source.getY();
                }
                double anchorX = xAxis.java2DToValue(sourceX, info.getDataArea(), getDomainAxisEdge());
                xAxis.resizeRange2(factor, anchorX);
            } else {
                xAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the domain axis/axes.  The new lower and upper bounds are
     * specified as percentages of the current axis range, where 0 percent is
     * the current lower bound and 100 percent is the current upper bound.
     *
     * @param lowerPercent  a percentage that determines the new lower bound
     *                      for the axis (e.g. 0.20 is twenty percent).
     * @param upperPercent  a percentage that determines the new upper bound
     *                      for the axis (e.g. 0.80 is eighty percent).
     * @param info  the plot rendering info.
     * @param source  the source point (ignored).
     *
     * @see #zoomRangeAxes(double, double, PlotRenderingInfo, Point2D)
     */
    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (ValueAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param info  the plot rendering info.
     * @param source  the source point.
     *
     * @see #zoomDomainAxes(double, PlotRenderingInfo, Point2D, boolean)
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
        // delegate to other method
        zoomRangeAxes(factor, info, source, false);
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
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceY = source.getY();
                if (this.orientation == PlotOrientation.HORIZONTAL) {
                    sourceY = source.getX();
                }
                double anchorY = yAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge());
                yAxis.resizeRange2(factor, anchorY);
            } else {
                yAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the range axes.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     * @param info  the plot rendering info.
     * @param source  the source point.
     *
     * @see #zoomDomainAxes(double, double, PlotRenderingInfo, Point2D)
     */
    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Returns {@code true}, indicating that the domain axis/axes for this
     * plot are zoomable.
     *
     * @return A boolean.
     *
     * @see #isRangeZoomable()
     */
    @Override
    public boolean isDomainZoomable() {
        return true;
    }

    /**
     * Returns {@code true}, indicating that the range axis/axes for this
     * plot are zoomable.
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
     * Returns the number of series in the primary dataset for this plot.  If
     * the dataset is {@code null}, the method returns 0.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        int result = 0;
        XYDataset<S> dataset = getDataset();
        if (dataset != null) {
            result = dataset.getSeriesCount();
        }
        return result;
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
     * Returns the legend items for the plot.  Each legend item is generated by
     * the plot's renderer, since the renderer is responsible for the visual
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
        for (XYDataset<S> dataset : this.datasets.values()) {
            if (dataset == null) {
                continue;
            }
            int datasetIndex = indexOf(dataset);
            XYItemRenderer renderer = getRenderer(datasetIndex);
            if (renderer == null) {
                renderer = getRenderer(0);
            }
            if (renderer != null) {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    if (renderer.isSeriesVisible(i) && renderer.isSeriesVisibleInLegend(i)) {
                        LegendItem item = renderer.getLegendItem(datasetIndex, i);
                        if (item != null) {
                            result.add(item);
                        }
                    }
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
        if (!(obj instanceof XYPlot)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        XYPlot<S> that = (XYPlot<S>) obj;
        if (this.weight != that.weight) {
            return false;
        }
        if (this.orientation != that.orientation) {
            return false;
        }
        if (!this.domainAxes.equals(that.domainAxes)) {
            return false;
        }
        if (!this.domainAxisLocations.equals(that.domainAxisLocations)) {
            return false;
        }
        if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (this.rangeGridlinesVisible != that.rangeGridlinesVisible) {
            return false;
        }
        if (this.domainMinorGridlinesVisible != that.domainMinorGridlinesVisible) {
            return false;
        }
        if (this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible) {
            return false;
        }
        if (this.domainZeroBaselineVisible != that.domainZeroBaselineVisible) {
            return false;
        }
        if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible) {
            return false;
        }
        if (this.domainCrosshairVisible != that.domainCrosshairVisible) {
            return false;
        }
        if (this.domainCrosshairValue != that.domainCrosshairValue) {
            return false;
        }
        if (this.domainCrosshairLockedOnData != that.domainCrosshairLockedOnData) {
            return false;
        }
        if (this.rangeCrosshairVisible != that.rangeCrosshairVisible) {
            return false;
        }
        if (this.rangeCrosshairValue != that.rangeCrosshairValue) {
            return false;
        }
        if (!Objects.equals(this.axisOffset, that.axisOffset)) {
            return false;
        }
        if (!Objects.equals(this.renderers, that.renderers)) {
            return false;
        }
        if (!Objects.equals(this.rangeAxes, that.rangeAxes)) {
            return false;
        }
        if (!this.rangeAxisLocations.equals(that.rangeAxisLocations)) {
            return false;
        }
        if (!Objects.equals(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeGridlineStroke, that.rangeGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.domainMinorGridlineStroke, that.domainMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainMinorGridlinePaint, that.domainMinorGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainZeroBaselinePaint, that.domainZeroBaselinePaint)) {
            return false;
        }
        if (!Objects.equals(this.domainZeroBaselineStroke, that.domainZeroBaselineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke)) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairStroke, that.domainCrosshairStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainCrosshairPaint, that.domainCrosshairPaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeCrosshairStroke, that.rangeCrosshairStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint)) {
            return false;
        }
        if (!Objects.equals(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
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
        if (!Objects.equals(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
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
        if (!Objects.equals(this.fixedLegendItems, that.fixedLegendItems)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainTickBandPaint, that.domainTickBandPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeTickBandPaint, that.rangeTickBandPaint)) {
            return false;
        }
        if (!this.quadrantOrigin.equals(that.quadrantOrigin)) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (!PaintUtils.equal(this.quadrantPaint[i], that.quadrantPaint[i])) {
                return false;
            }
        }
        if (!Objects.equals(this.shadowGenerator, that.shadowGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.orientation);
        hash = 43 * hash + Objects.hashCode(this.axisOffset);
        hash = 43 * hash + Objects.hashCode(this.domainAxes);
        hash = 43 * hash + Objects.hashCode(this.domainAxisLocations);
        hash = 43 * hash + Objects.hashCode(this.rangeAxes);
        hash = 43 * hash + Objects.hashCode(this.rangeAxisLocations);
        hash = 43 * hash + Objects.hashCode(this.renderers);
        hash = 43 * hash + Objects.hashCode(this.datasetToDomainAxesMap);
        hash = 43 * hash + Objects.hashCode(this.datasetToRangeAxesMap);
        hash = 43 * hash + Objects.hashCode(this.quadrantOrigin);
        hash = 43 * hash + Arrays.deepHashCode(this.quadrantPaint);
        hash = 43 * hash + (this.domainGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.domainGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.domainGridlinePaint);
        hash = 43 * hash + (this.rangeGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.rangeGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeGridlinePaint);
        hash = 43 * hash + (this.domainMinorGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.domainMinorGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.domainMinorGridlinePaint);
        hash = 43 * hash + (this.rangeMinorGridlinesVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.rangeMinorGridlineStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeMinorGridlinePaint);
        hash = 43 * hash + (this.domainZeroBaselineVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.domainZeroBaselineStroke);
        hash = 43 * hash + Objects.hashCode(this.domainZeroBaselinePaint);
        hash = 43 * hash + (this.rangeZeroBaselineVisible ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.rangeZeroBaselineStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeZeroBaselinePaint);
        hash = 43 * hash + (this.domainCrosshairVisible ? 1 : 0);
        hash = 43 * hash + Long.hashCode(Double.doubleToLongBits(this.domainCrosshairValue));
        hash = 43 * hash + Objects.hashCode(this.domainCrosshairStroke);
        hash = 43 * hash + Objects.hashCode(this.domainCrosshairPaint);
        hash = 43 * hash + (this.domainCrosshairLockedOnData ? 1 : 0);
        hash = 43 * hash + (this.rangeCrosshairVisible ? 1 : 0);
        hash = 43 * hash + Long.hashCode(Double.doubleToLongBits(this.rangeCrosshairValue));
        hash = 43 * hash + Objects.hashCode(this.rangeCrosshairStroke);
        hash = 43 * hash + Objects.hashCode(this.rangeCrosshairPaint);
        hash = 43 * hash + (this.rangeCrosshairLockedOnData ? 1 : 0);
        hash = 43 * hash + Objects.hashCode(this.foregroundDomainMarkers);
        hash = 43 * hash + Objects.hashCode(this.backgroundDomainMarkers);
        hash = 43 * hash + Objects.hashCode(this.foregroundRangeMarkers);
        hash = 43 * hash + Objects.hashCode(this.backgroundRangeMarkers);
        hash = 43 * hash + Objects.hashCode(this.annotations);
        hash = 43 * hash + Objects.hashCode(this.domainTickBandPaint);
        hash = 43 * hash + Objects.hashCode(this.rangeTickBandPaint);
        hash = 43 * hash + this.weight;
        hash = 43 * hash + Objects.hashCode(this.fixedLegendItems);
        hash = 43 * hash + Objects.hashCode(this.shadowGenerator);
        return hash;
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
        @SuppressWarnings("unchecked")
        XYPlot<S> clone = (XYPlot<S>) super.clone();
        clone.domainAxes = CloneUtils.cloneMapValues(this.domainAxes);
        for (ValueAxis axis : clone.domainAxes.values()) {
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
        clone.domainAxisLocations = new HashMap<>(this.domainAxisLocations);
        clone.rangeAxisLocations = new HashMap<>(this.rangeAxisLocations);
        // the datasets are not cloned, but listeners need to be added...
        clone.datasets = new HashMap<>(this.datasets);
        for (XYDataset<S> dataset : clone.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxesMap = new TreeMap<>();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap<>();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = CloneUtils.cloneMapValues(this.renderers);
        for (XYItemRenderer renderer : clone.renderers.values()) {
            if (renderer != null) {
                renderer.setPlot(clone);
                renderer.addChangeListener(clone);
            }
        }
        clone.foregroundDomainMarkers = CloneUtils.clone(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = CloneUtils.clone(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = CloneUtils.clone(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = CloneUtils.clone(this.backgroundRangeMarkers);
        clone.annotations = CloneUtils.cloneList(this.annotations);
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = CloneUtils.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = CloneUtils.clone(this.fixedRangeAxisSpace);
        }
        if (this.fixedLegendItems != null) {
            clone.fixedLegendItems = (LegendItemCollection) this.fixedLegendItems.clone();
        }
        clone.quadrantOrigin = CloneUtils.clone(this.quadrantOrigin);
        clone.quadrantPaint = this.quadrantPaint.clone();
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
        SerialUtils.writeStroke(this.domainMinorGridlineStroke, stream);
        SerialUtils.writePaint(this.domainMinorGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtils.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtils.writePaint(this.rangeZeroBaselinePaint, stream);
        SerialUtils.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtils.writePaint(this.domainCrosshairPaint, stream);
        SerialUtils.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtils.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtils.writePaint(this.domainTickBandPaint, stream);
        SerialUtils.writePaint(this.rangeTickBandPaint, stream);
        SerialUtils.writePoint2D(this.quadrantOrigin, stream);
        for (int i = 0; i < 4; i++) {
            SerialUtils.writePaint(this.quadrantPaint[i], stream);
        }
        SerialUtils.writeStroke(this.domainZeroBaselineStroke, stream);
        SerialUtils.writePaint(this.domainZeroBaselinePaint, stream);
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
        this.domainMinorGridlineStroke = SerialUtils.readStroke(stream);
        this.domainMinorGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtils.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtils.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtils.readPaint(stream);
        this.domainCrosshairStroke = SerialUtils.readStroke(stream);
        this.domainCrosshairPaint = SerialUtils.readPaint(stream);
        this.rangeCrosshairStroke = SerialUtils.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtils.readPaint(stream);
        this.domainTickBandPaint = SerialUtils.readPaint(stream);
        this.rangeTickBandPaint = SerialUtils.readPaint(stream);
        this.quadrantOrigin = SerialUtils.readPoint2D(stream);
        this.quadrantPaint = new Paint[4];
        for (int i = 0; i < 4; i++) {
            this.quadrantPaint[i] = SerialUtils.readPaint(stream);
        }
        this.domainZeroBaselineStroke = SerialUtils.readStroke(stream);
        this.domainZeroBaselinePaint = SerialUtils.readPaint(stream);
        // register the plot as a listener with its axes, datasets, and
        // renderers...
        for (ValueAxis axis : this.domainAxes.values()) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis != null) {
                axis.setPlot(this);
                axis.addChangeListener(this);
            }
        }
        for (XYDataset<S> dataset : this.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (XYItemRenderer renderer : this.renderers.values()) {
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
 * -------------
 * XYSeries.java
 * -------------
 * (C) Copyright 2001-present, David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Aaron Metzger;
 *                   Jonathan Gabbai;
 *                   Richard Atkinson;
 *                   Michel Santos;
 *                   Ted Schwartz (fix for bug 1955483);
 */
/**
 * Represents a sequence of zero or more data items in the form (x, y).  By
 * default, items in the series will be sorted into ascending order by x-value,
 * and duplicate x-values are permitted.  Both the sorting and duplicate
 * defaults can be changed in the constructor.  Y-values can be
 * {@code null} to represent missing values.
 *
 * @param <K> the series key type.
 */
public class XYSeries<K extends Comparable<K>> extends Series<K> implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -5908509288197150436L;

    // In version 0.9.12, in response to several developer requests, I changed
    // the 'data' attribute from 'private' to 'protected', so that others can
    // make subclasses that work directly with the underlying data structure.
    /**
     * Storage for the data items in the series.
     */
    protected List<XYDataItem> data;

    /**
     * The maximum number of items for the series.
     */
    private int maximumItemCount = Integer.MAX_VALUE;

    /**
     * A flag that controls whether the items are automatically sorted
     * (by x-value ascending).
     */
    private final boolean autoSort;

    /**
     * A flag that controls whether duplicate x-values are allowed.
     */
    private final boolean allowDuplicateXValues;

    /**
     * The lowest x-value in the series, excluding Double.NaN values.
     */
    private double minX;

    /**
     * The highest x-value in the series, excluding Double.NaN values.
     */
    private double maxX;

    /**
     * The lowest y-value in the series, excluding Double.NaN values.
     */
    private double minY;

    /**
     * The highest y-value in the series, excluding Double.NaN values.
     */
    private double maxY;

    /**
     * Creates a new empty series.  By default, items added to the series will
     * be sorted into ascending order by x-value, and duplicate x-values will
     * be allowed (these defaults can be modified with another constructor).
     *
     * @param key  the series key ({@code null} not permitted).
     */
    public XYSeries(K key) {
        this(key, true, true);
    }

    /**
     * Constructs a new empty series, with the auto-sort flag set as requested,
     * and duplicate values allowed.
     *
     * @param key  the series key ({@code null} not permitted).
     * @param autoSort  a flag that controls whether the items in the
     *                  series are sorted.
     */
    public XYSeries(K key, boolean autoSort) {
        this(key, autoSort, true);
    }

    /**
     * Constructs a new xy-series that contains no data.  You can specify
     * whether duplicate x-values are allowed for the series.
     *
     * @param key  the series key ({@code null} not permitted).
     * @param autoSort  a flag that controls whether the items in the
     *                  series are sorted.
     * @param allowDuplicateXValues  a flag that controls whether duplicate
     *                               x-values are allowed.
     */
    public XYSeries(K key, boolean autoSort, boolean allowDuplicateXValues) {
        super(key);
        this.data = new java.util.ArrayList<>();
        this.autoSort = autoSort;
        this.allowDuplicateXValues = allowDuplicateXValues;
        this.minX = Double.NaN;
        this.maxX = Double.NaN;
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
    }

    /**
     * Returns the smallest x-value in the series, ignoring any Double.NaN
     * values.  This method returns Double.NaN if there is no smallest x-value
     * (for example, when the series is empty).
     *
     * @return The smallest x-value.
     *
     * @see #getMaxX()
     *
     * @since 1.0.13
     */
    public double getMinX() {
        return this.minX;
    }

    /**
     * Returns the largest x-value in the series, ignoring any Double.NaN
     * values.  This method returns Double.NaN if there is no largest x-value
     * (for example, when the series is empty).
     *
     * @return The largest x-value.
     *
     * @see #getMinX()
     *
     * @since 1.0.13
     */
    public double getMaxX() {
        return this.maxX;
    }

    /**
     * Returns the smallest y-value in the series, ignoring any null and
     * Double.NaN values.  This method returns Double.NaN if there is no
     * smallest y-value (for example, when the series is empty).
     *
     * @return The smallest y-value.
     *
     * @see #getMaxY()
     *
     * @since 1.0.13
     */
    public double getMinY() {
        return this.minY;
    }

    /**
     * Returns the largest y-value in the series, ignoring any Double.NaN
     * values.  This method returns Double.NaN if there is no largest y-value
     * (for example, when the series is empty).
     *
     * @return The largest y-value.
     *
     * @see #getMinY()
     *
     * @since 1.0.13
     */
    public double getMaxY() {
        return this.maxY;
    }

    /**
     * Updates the cached values for the minimum and maximum data values.
     *
     * @param item  the item added ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    private void updateBoundsForAddedItem(XYDataItem item) {
        double x = item.getXValue();
        this.minX = minIgnoreNaN(this.minX, x);
        this.maxX = maxIgnoreNaN(this.maxX, x);
        if (item.getY() != null) {
            double y = item.getYValue();
            this.minY = minIgnoreNaN(this.minY, y);
            this.maxY = maxIgnoreNaN(this.maxY, y);
        }
    }

    /**
     * Updates the cached values for the minimum and maximum data values on
     * the basis that the specified item has just been removed.
     *
     * @param item  the item added ({@code null} not permitted).
     *
     * @since 1.0.13
     */
    private void updateBoundsForRemovedItem(XYDataItem item) {
        boolean itemContributesToXBounds = false;
        boolean itemContributesToYBounds = false;
        double x = item.getXValue();
        if (!Double.isNaN(x)) {
            if (x <= this.minX || x >= this.maxX) {
                itemContributesToXBounds = true;
            }
        }
        if (item.getY() != null) {
            double y = item.getYValue();
            if (!Double.isNaN(y)) {
                if (y <= this.minY || y >= this.maxY) {
                    itemContributesToYBounds = true;
                }
            }
        }
        if (itemContributesToYBounds) {
            findBoundsByIteration();
        } else if (itemContributesToXBounds) {
            if (getAutoSort()) {
                this.minX = getX(0).doubleValue();
                this.maxX = getX(getItemCount() - 1).doubleValue();
            } else {
                findBoundsByIteration();
            }
        }
    }

    /**
     * Finds the bounds of the x and y values for the series, by iterating
     * through all the data items.
     *
     * @since 1.0.13
     */
    private void findBoundsByIteration() {
        this.minX = Double.NaN;
        this.maxX = Double.NaN;
        this.minY = Double.NaN;
        this.maxY = Double.NaN;
        for (XYDataItem item : this.data) {
            updateBoundsForAddedItem(item);
        }
    }

    /**
     * Returns the flag that controls whether the items in the series are
     * automatically sorted.  There is no setter for this flag, it must be
     * defined in the series constructor.
     *
     * @return A boolean.
     */
    public boolean getAutoSort() {
        return this.autoSort;
    }

    /**
     * Returns a flag that controls whether duplicate x-values are allowed.
     * This flag can only be set in the constructor.
     *
     * @return A boolean.
     */
    public boolean getAllowDuplicateXValues() {
        return this.allowDuplicateXValues;
    }

    /**
     * Returns the number of items in the series.
     *
     * @return The item count.
     *
     * @see #getItems()
     */
    @Override
    public int getItemCount() {
        return this.data.size();
    }

    /**
     * Returns the list of data items for the series (the list contains
     * {@link XYDataItem} objects and is unmodifiable).
     *
     * @return The list of data items.
     */
    public List<XYDataItem> getItems() {
        return Collections.unmodifiableList(this.data);
    }

    /**
     * Returns the maximum number of items that will be retained in the series.
     * The default value is {@code Integer.MAX_VALUE}.
     *
     * @return The maximum item count.
     *
     * @see #setMaximumItemCount(int)
     */
    public int getMaximumItemCount() {
        return this.maximumItemCount;
    }

    /**
     * Sets the maximum number of items that will be retained in the series.
     * If you add a new item to the series such that the number of items will
     * exceed the maximum item count, then the first element in the series is
     * automatically removed, ensuring that the maximum item count is not
     * exceeded.
     * <p>
     * Typically this value is set before the series is populated with data,
     * but if it is applied later, it may cause some items to be removed from
     * the series (in which case a {@link SeriesChangeEvent} will be sent to
     * all registered listeners).
     *
     * @param maximum  the maximum number of items for the series.
     */
    public void setMaximumItemCount(int maximum) {
        this.maximumItemCount = maximum;
        int remove = this.data.size() - maximum;
        if (remove > 0) {
            this.data.subList(0, remove).clear();
            findBoundsByIteration();
            fireSeriesChanged();
        }
    }

    /**
     * Adds a data item to the series and sends a {@link SeriesChangeEvent} to
     * all registered listeners.
     *
     * @param item  the (x, y) item ({@code null} not permitted).
     */
    public void add(XYDataItem item) {
        // argument checking delegated...
        add(item, true);
    }

    /**
     * Adds a data item to the series and sends a {@link SeriesChangeEvent} to
     * all registered listeners.
     *
     * @param x  the x value.
     * @param y  the y value.
     */
    public void add(double x, double y) {
        add(Double.valueOf(x), Double.valueOf(y), true);
    }

    /**
     * Adds a data item to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param x  the x value.
     * @param y  the y value.
     * @param notify  a flag that controls whether a
     *                {@link SeriesChangeEvent} is sent to all registered
     *                listeners.
     */
    public void add(double x, double y, boolean notify) {
        add(Double.valueOf(x), Double.valueOf(y), notify);
    }

    /**
     * Adds a data item to the series and sends a {@link SeriesChangeEvent} to
     * all registered listeners.  The unusual pairing of parameter types is to
     * make it easier to add {@code null} y-values.
     *
     * @param x  the x value.
     * @param y  the y value ({@code null} permitted).
     */
    public void add(double x, Number y) {
        add(Double.valueOf(x), y);
    }

    /**
     * Adds a data item to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.  The unusual
     * pairing of parameter types is to make it easier to add null y-values.
     *
     * @param x  the x value.
     * @param y  the y value ({@code null} permitted).
     * @param notify  a flag that controls whether a
     *                {@link SeriesChangeEvent} is sent to all registered
     *                listeners.
     */
    public void add(double x, Number y, boolean notify) {
        add(Double.valueOf(x), y, notify);
    }

    /**
     * Adds a new data item to the series (in the correct position if the
     * {@code autoSort} flag is set for the series) and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     * <P>
     * Throws an exception if the x-value is a duplicate AND the
     * allowDuplicateXValues flag is false.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     *
     * @throws SeriesException if the x-value is a duplicate and the
     *     {@code allowDuplicateXValues} flag is not set for this series.
     */
    public void add(Number x, Number y) {
        // argument checking delegated...
        add(x, y, true);
    }

    /**
     * Adds new data to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     * <P>
     * Throws an exception if the x-value is a duplicate AND the
     * allowDuplicateXValues flag is false.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     * @param notify  a flag the controls whether a
     *                {@link SeriesChangeEvent} is sent to all registered
     *                listeners.
     */
    public void add(Number x, Number y, boolean notify) {
        // delegate argument checking to XYDataItem...
        XYDataItem item = new XYDataItem(x, y);
        add(item, notify);
    }

    /**
     * Adds a data item to the series and, if requested, sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param item  the (x, y) item ({@code null} not permitted).
     * @param notify  a flag that controls whether a
     *                {@link SeriesChangeEvent} is sent to all registered
     *                listeners.
     */
    public void add(XYDataItem item, boolean notify) {
        Args.nullNotPermitted(item, "item");
        XYDataItem clone = (XYDataItem) item.clone();
        if (this.autoSort) {
            int index = Collections.binarySearch(this.data, item);
            if (index < 0) {
                this.data.add(-index - 1, clone);
            } else {
                if (this.allowDuplicateXValues) {
                    // need to make sure we are adding *after* any duplicates
                    int size = this.data.size();
                    while (index < size && item.compareTo(this.data.get(index)) == 0) {
                        index++;
                    }
                    if (index < this.data.size()) {
                        this.data.add(index, clone);
                    } else {
                        this.data.add(clone);
                    }
                } else {
                    throw new SeriesException("X-value already exists.");
                }
            }
        } else {
            if (!this.allowDuplicateXValues) {
                // can't allow duplicate values, so we need to check whether
                // there is an item with the given x-value already
                int index = indexOf(item.getX());
                if (index >= 0) {
                    throw new SeriesException("X-value already exists.");
                }
            }
            this.data.add(item);
        }
        updateBoundsForAddedItem(item);
        if (getItemCount() > this.maximumItemCount) {
            XYDataItem removed = this.data.remove(0);
            updateBoundsForRemovedItem(removed);
        }
        if (notify) {
            fireSeriesChanged();
        }
    }

    /**
     * Deletes a range of items from the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param start  the start index (zero-based).
     * @param end  the end index (zero-based).
     */
    public void delete(int start, int end) {
        this.data.subList(start, end + 1).clear();
        findBoundsByIteration();
        fireSeriesChanged();
    }

    /**
     * Removes the item at the specified index and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     *
     * @return The item removed.
     */
    public XYDataItem remove(int index) {
        XYDataItem removed = this.data.remove(index);
        updateBoundsForRemovedItem(removed);
        fireSeriesChanged();
        return removed;
    }

    /**
     * Removes an item with the specified x-value and sends a
     * {@link SeriesChangeEvent} to all registered listeners.  Note that when
     * a series permits multiple items with the same x-value, this method
     * could remove any one of the items with that x-value.
     *
     * @param x  the x-value.
     *
     * @return The item removed.
     */
    public XYDataItem remove(Number x) {
        return remove(indexOf(x));
    }

    /**
     * Removes all data items from the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     */
    public void clear() {
        if (!this.data.isEmpty()) {
            this.data.clear();
            this.minX = Double.NaN;
            this.maxX = Double.NaN;
            this.minY = Double.NaN;
            this.maxY = Double.NaN;
            fireSeriesChanged();
        }
    }

    /**
     * Returns a copy of the data item with the specified index.
     *
     * @param index  the index.
     *
     * @return The data item with the specified index.
     */
    public XYDataItem getDataItem(int index) {
        XYDataItem item = this.data.get(index);
        return (XYDataItem) item.clone();
    }

    /**
     * Return the data item with the specified index.
     *
     * @param index  the index.
     *
     * @return The data item with the specified index.
     *
     * @since 1.0.14
     */
    XYDataItem getRawDataItem(int index) {
        return this.data.get(index);
    }

    /**
     * Returns the x-value at the specified index.
     *
     * @param index  the index (zero-based).
     *
     * @return The x-value (never {@code null}).
     */
    public Number getX(int index) {
        return getRawDataItem(index).getX();
    }

    /**
     * Returns the y-value at the specified index.
     *
     * @param index  the index (zero-based).
     *
     * @return The y-value (possibly {@code null}).
     */
    public Number getY(int index) {
        return getRawDataItem(index).getY();
    }

    /**
     * A function to find the minimum of two values, but ignoring any
     * Double.NaN values.
     *
     * @param a  the first value.
     * @param b  the second value.
     *
     * @return The minimum of the two values.
     */
    private double minIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.min(a, b);
    }

    /**
     * A function to find the maximum of two values, but ignoring any
     * Double.NaN values.
     *
     * @param a  the first value.
     * @param b  the second value.
     *
     * @return The maximum of the two values.
     */
    private double maxIgnoreNaN(double a, double b) {
        if (Double.isNaN(a)) {
            return b;
        }
        if (Double.isNaN(b)) {
            return a;
        }
        return Math.max(a, b);
    }

    /**
     * Updates the value of an item in the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param index  the item (zero based index).
     * @param y  the new value ({@code null} permitted).
     *
     * @since 1.0.1
     */
    public void updateByIndex(int index, Number y) {
        XYDataItem item = getRawDataItem(index);
        // figure out if we need to iterate through all the y-values
        boolean iterate = false;
        double oldY = item.getYValue();
        if (!Double.isNaN(oldY)) {
            iterate = oldY <= this.minY || oldY >= this.maxY;
        }
        item.setY(y);
        if (iterate) {
            findBoundsByIteration();
        } else if (y != null) {
            double yy = y.doubleValue();
            this.minY = minIgnoreNaN(this.minY, yy);
            this.maxY = maxIgnoreNaN(this.maxY, yy);
        }
        fireSeriesChanged();
    }

    /**
     * Updates an item in the series.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     *
     * @throws SeriesException if there is no existing item with the specified
     *         x-value.
     */
    public void update(Number x, Number y) {
        int index = indexOf(x);
        if (index < 0) {
            throw new SeriesException("No observation for x = " + x);
        }
        updateByIndex(index, y);
    }

    /**
     * Adds or updates an item in the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param x  the x-value.
     * @param y  the y-value.
     *
     * @return The item that was overwritten, if any.
     *
     * @since 1.0.10
     */
    public XYDataItem addOrUpdate(double x, double y) {
        return addOrUpdate(Double.valueOf(x), Double.valueOf(y));
    }

    /**
     * Adds or updates an item in the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param x  the x-value ({@code null} not permitted).
     * @param y  the y-value ({@code null} permitted).
     *
     * @return A copy of the overwritten data item, or {@code null} if no
     *         item was overwritten.
     */
    public XYDataItem addOrUpdate(Number x, Number y) {
        // defer argument checking
        return addOrUpdate(new XYDataItem(x, y));
    }

    /**
     * Adds or updates an item in the series and sends a
     * {@link SeriesChangeEvent} to all registered listeners.
     *
     * @param item  the data item ({@code null} not permitted).
     *
     * @return A copy of the overwritten data item, or {@code null} if no
     *         item was overwritten.
     *
     * @since 1.0.14
     */
    public XYDataItem addOrUpdate(XYDataItem item) {
        Args.nullNotPermitted(item, "item");
        if (this.allowDuplicateXValues) {
            add(item);
            return null;
        }
        // if we get to here, we know that duplicate X values are not permitted
        XYDataItem overwritten = null;
        int index = indexOf(item.getX());
        if (index >= 0) {
            XYDataItem existing = this.data.get(index);
            overwritten = (XYDataItem) existing.clone();
            // figure out if we need to iterate through all the y-values
            boolean iterate = false;
            double oldY = existing.getYValue();
            if (!Double.isNaN(oldY)) {
                iterate = oldY <= this.minY || oldY >= this.maxY;
            }
            existing.setY(item.getY());
            if (iterate) {
                findBoundsByIteration();
            } else if (item.getY() != null) {
                double yy = item.getY().doubleValue();
                this.minY = minIgnoreNaN(this.minY, yy);
                this.maxY = maxIgnoreNaN(this.maxY, yy);
            }
        } else {
            // if the series is sorted, the negative index is a result from
            // Collections.binarySearch() and tells us where to insert the
            // new item...otherwise it will be just -1 and we should just
            // append the value to the list...
            item = (XYDataItem) item.clone();
            if (this.autoSort) {
                this.data.add(-index - 1, item);
            } else {
                this.data.add(item);
            }
            updateBoundsForAddedItem(item);
            // check if this addition will exceed the maximum item count...
            if (getItemCount() > this.maximumItemCount) {
                XYDataItem removed = this.data.remove(0);
                updateBoundsForRemovedItem(removed);
            }
        }
        fireSeriesChanged();
        return overwritten;
    }

    /**
     * Returns the index of the item with the specified x-value, or a negative
     * index if the series does not contain an item with that x-value.  Be
     * aware that for an unsorted series, the index is found by iterating
     * through all items in the series.
     *
     * @param x  the x-value ({@code null} not permitted).
     *
     * @return The index.
     */
    public int indexOf(Number x) {
        if (this.autoSort) {
            return Collections.binarySearch(this.data, new XYDataItem(x, null));
        } else {
            for (int i = 0; i < this.data.size(); i++) {
                XYDataItem item = this.data.get(i);
                if (item.getX().equals(x)) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * Returns a new array containing the x and y values from this series.
     *
     * @return A new array containing the x and y values from this series.
     *
     * @since 1.0.4
     */
    public double[][] toArray() {
        int itemCount = getItemCount();
        double[][] result = new double[2][itemCount];
        for (int i = 0; i < itemCount; i++) {
            result[0][i] = this.getX(i).doubleValue();
            Number y = getY(i);
            if (y != null) {
                result[1][i] = y.doubleValue();
            } else {
                result[1][i] = Double.NaN;
            }
        }
        return result;
    }

    /**
     * Returns a clone of the series.
     *
     * @return A clone of the series.
     *
     * @throws CloneNotSupportedException if there is a cloning problem.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object clone() throws CloneNotSupportedException {
        XYSeries<K> clone = (XYSeries) super.clone();
        clone.data = CloneUtils.cloneList(this.data);
        return clone;
    }

    /**
     * Creates a new series by copying a subset of the data in this time series.
     *
     * @param start  the index of the first item to copy.
     * @param end  the index of the last item to copy.
     *
     * @return A series containing a copy of this series from start until end.
     *
     * @throws CloneNotSupportedException if there is a cloning problem.
     */
    @SuppressWarnings("unchecked")
    public XYSeries<K> createCopy(int start, int end) throws CloneNotSupportedException {
        XYSeries<K> copy = (XYSeries) super.clone();
        copy.data = new ArrayList<>();
        if (!this.data.isEmpty()) {
            for (int index = start; index <= end; index++) {
                XYDataItem item = this.data.get(index);
                XYDataItem clone = CloneUtils.clone(item);
                try {
                    copy.add(clone);
                } catch (SeriesException e) {
                    throw new RuntimeException("Unable to add cloned data item.", e);
                }
            }
        }
        return copy;
    }

    /**
     * Tests this series for equality with an arbitrary object.
     *
     * @param obj  the object to test against for equality
     *             ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof XYSeries)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYSeries<K> that = (XYSeries) obj;
        if (this.maximumItemCount != that.maximumItemCount) {
            return false;
        }
        if (this.autoSort != that.autoSort) {
            return false;
        }
        if (this.allowDuplicateXValues != that.allowDuplicateXValues) {
            return false;
        }
        if (!Objects.equals(this.data, that.data)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code.
     *
     * @return A hash code.
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        // it is too slow to look at every data item, so let's just look at
        // the first, middle and last items...
        int count = getItemCount();
        if (count > 0) {
            XYDataItem item = getRawDataItem(0);
            result = 29 * result + item.hashCode();
        }
        if (count > 1) {
            XYDataItem item = getRawDataItem(count - 1);
            result = 29 * result + item.hashCode();
        }
        if (count > 2) {
            XYDataItem item = getRawDataItem(count / 2);
            result = 29 * result + item.hashCode();
        }
        result = 29 * result + this.maximumItemCount;
        result = 29 * result + (this.autoSort ? 1 : 0);
        result = 29 * result + (this.allowDuplicateXValues ? 1 : 0);
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
 * ----------------------------
 * XYBoxAndWhiskerRenderer.java
 * ----------------------------
 * (C) Copyright 2003-present, by David Browning and Contributors.
 *
 * Original Author:  David Browning (for Australian Institute of Marine
 *                   Science);
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A renderer that draws box-and-whisker items on an {@link XYPlot}.  This
 * renderer requires a {@link BoxAndWhiskerXYDataset}).  The example shown here
 * is generated by the{@code BoxAndWhiskerChartDemo2.java} program
 * included in the JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/XYBoxAndWhiskerRendererSample.png"
 * alt="XYBoxAndWhiskerRendererSample.png">
 * <P>
 * This renderer does not include any code to calculate the crosshair point.
 */
public class XYBoxAndWhiskerRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8020170108532232324L;

    /**
     * The box width.
     */
    private double boxWidth;

    /**
     * The paint used to fill the box.
     */
    private transient Paint boxPaint;

    /**
     * A flag that controls whether the box is filled.
     */
    private boolean fillBox;

    /**
     * The paint used to draw various artifacts such as outliers, farout
     * symbol, average ellipse and median line.
     */
    private transient Paint artifactPaint = Color.BLACK;

    /**
     * Creates a new renderer for box and whisker charts.
     */
    public XYBoxAndWhiskerRenderer() {
        this(-1.0);
    }

    /**
     * Creates a new renderer for box and whisker charts.
     * <P>
     * Use -1 for the box width if you prefer the width to be calculated
     * automatically.
     *
     * @param boxWidth  the box width.
     */
    public XYBoxAndWhiskerRenderer(double boxWidth) {
        super();
        this.boxWidth = boxWidth;
        this.boxPaint = Color.GREEN;
        this.fillBox = true;
        setDefaultToolTipGenerator(new BoxAndWhiskerXYToolTipGenerator());
    }

    /**
     * Returns the width of each box.
     *
     * @return The box width.
     *
     * @see #setBoxWidth(double)
     */
    public double getBoxWidth() {
        return this.boxWidth;
    }

    /**
     * Sets the box width and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     * <P>
     * If you set the width to a negative value, the renderer will calculate
     * the box width automatically based on the space available on the chart.
     *
     * @param width  the width.
     *
     * @see #getBoxWidth()
     */
    public void setBoxWidth(double width) {
        if (width != this.boxWidth) {
            this.boxWidth = width;
            fireChangeEvent();
        }
    }

    /**
     * Returns the paint used to fill boxes.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setBoxPaint(Paint)
     */
    public Paint getBoxPaint() {
        return this.boxPaint;
    }

    /**
     * Sets the paint used to fill boxes and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getBoxPaint()
     */
    public void setBoxPaint(Paint paint) {
        this.boxPaint = paint;
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
     * @see #setFillBox(boolean)
     */
    public void setFillBox(boolean flag) {
        this.fillBox = flag;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to paint the various artifacts such as outliers,
     * farout symbol, median line and the averages ellipse.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setArtifactPaint(Paint)
     */
    public Paint getArtifactPaint() {
        return this.artifactPaint;
    }

    /**
     * Sets the paint used to paint the various artifacts such as outliers,
     * farout symbol, median line and the averages ellipse, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
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
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range ({@code null} if the dataset is {@code null}
     *         or empty).
     *
     * @see #findDomainBounds(XYDataset)
     */
    @Override
    public Range findRangeBounds(XYDataset dataset) {
        return findRangeBounds(dataset, true);
    }

    /**
     * Returns the box paint or, if this is {@code null}, the item
     * paint.
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return The paint used to fill the box for the specified item (never
     *         {@code null}).
     */
    protected Paint lookupBoxPaint(int series, int item) {
        Paint p = getBoxPaint();
        if (p != null) {
            return p;
        } else {
            // TODO: could change this to itemFillPaint().  For backwards
            // compatibility, it might require a useFillPaint flag.
            return getItemPaint(series, item);
        }
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset (must be an instance of
     *                 {@link BoxAndWhiskerXYDataset}).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        PlotOrientation orientation = plot.getOrientation();
        if (orientation == PlotOrientation.HORIZONTAL) {
            drawHorizontalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
        } else if (orientation == PlotOrientation.VERTICAL) {
            drawVerticalItem(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
        }
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset (must be an instance of
     *                 {@link BoxAndWhiskerXYDataset}).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    public void drawHorizontalItem(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        BoxAndWhiskerXYDataset boxAndWhiskerData = (BoxAndWhiskerXYDataset) dataset;
        Number x = boxAndWhiskerData.getX(series, item);
        Number yMax = boxAndWhiskerData.getMaxRegularValue(series, item);
        Number yMin = boxAndWhiskerData.getMinRegularValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getMeanValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1Value(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3Value(series, item);
        double xx = domainAxis.valueToJava2D(x.doubleValue(), dataArea, plot.getDomainAxisEdge());
        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
        double yyAverage = 0.0;
        if (yAverage != null) {
            yyAverage = rangeAxis.valueToJava2D(yAverage.doubleValue(), dataArea, location);
        }
        double yyQ1Median = rangeAxis.valueToJava2D(yQ1Median.doubleValue(), dataArea, location);
        double yyQ3Median = rangeAxis.valueToJava2D(yQ3Median.doubleValue(), dataArea, location);
        double exactBoxWidth = getBoxWidth();
        double width = exactBoxWidth;
        double dataAreaX = dataArea.getHeight();
        double maxBoxPercent = 0.1;
        double maxBoxWidth = dataAreaX * maxBoxPercent;
        if (exactBoxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactBoxWidth = dataAreaX / itemCount * 4.5 / 7;
            if (exactBoxWidth < 3) {
                width = 3;
            } else if (exactBoxWidth > maxBoxWidth) {
                width = maxBoxWidth;
            } else {
                width = exactBoxWidth;
            }
        }
        g2.setPaint(getItemPaint(series, item));
        Stroke s = getItemStroke(series, item);
        g2.setStroke(s);
        // draw the upper shadow
        g2.draw(new Line2D.Double(yyMax, xx, yyQ3Median, xx));
        g2.draw(new Line2D.Double(yyMax, xx - width / 2, yyMax, xx + width / 2));
        // draw the lower shadow
        g2.draw(new Line2D.Double(yyMin, xx, yyQ1Median, xx));
        g2.draw(new Line2D.Double(yyMin, xx - width / 2, yyMin, xx + width / 2));
        // draw the body
        Shape box;
        if (yyQ1Median < yyQ3Median) {
            box = new Rectangle2D.Double(yyQ1Median, xx - width / 2, yyQ3Median - yyQ1Median, width);
        } else {
            box = new Rectangle2D.Double(yyQ3Median, xx - width / 2, yyQ1Median - yyQ3Median, width);
        }
        if (this.fillBox) {
            g2.setPaint(lookupBoxPaint(series, item));
            g2.fill(box);
        }
        g2.setStroke(getItemOutlineStroke(series, item));
        g2.setPaint(getItemOutlinePaint(series, item));
        g2.draw(box);
        // draw median
        g2.setPaint(getArtifactPaint());
        g2.draw(new Line2D.Double(yyMedian, xx - width / 2, yyMedian, xx + width / 2));
        // draw average - SPECIAL AIMS REQUIREMENT
        if (yAverage != null) {
            double aRadius = width / 4;
            // here we check that the average marker will in fact be visible
            // before drawing it...
            if ((yyAverage > (dataArea.getMinX() - aRadius)) && (yyAverage < (dataArea.getMaxX() + aRadius))) {
                Ellipse2D.Double avgEllipse = new Ellipse2D.Double(yyAverage - aRadius, xx - aRadius, aRadius * 2, aRadius * 2);
                g2.fill(avgEllipse);
                g2.draw(avgEllipse);
            }
        }
        // FIXME: draw outliers
        // add an entity for the item...
        if (entities != null && box.intersects(dataArea)) {
            addEntity(entities, box, dataset, series, item, yyAverage, xx);
        }
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area within which the plot is being drawn.
     * @param info  collects info about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset (must be an instance of
     *                 {@link BoxAndWhiskerXYDataset}).
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    public void drawVerticalItem(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        // setup for collecting optional entity info...
        EntityCollection entities = null;
        if (info != null) {
            entities = info.getOwner().getEntityCollection();
        }
        BoxAndWhiskerXYDataset boxAndWhiskerData = (BoxAndWhiskerXYDataset) dataset;
        Number x = boxAndWhiskerData.getX(series, item);
        Number yMax = boxAndWhiskerData.getMaxRegularValue(series, item);
        Number yMin = boxAndWhiskerData.getMinRegularValue(series, item);
        Number yMedian = boxAndWhiskerData.getMedianValue(series, item);
        Number yAverage = boxAndWhiskerData.getMeanValue(series, item);
        Number yQ1Median = boxAndWhiskerData.getQ1Value(series, item);
        Number yQ3Median = boxAndWhiskerData.getQ3Value(series, item);
        List yOutliers = boxAndWhiskerData.getOutliers(series, item);
        // yOutliers can be null, but we'd prefer it to be an empty list in
        // that case...
        if (yOutliers == null) {
            yOutliers = Collections.EMPTY_LIST;
        }
        double xx = domainAxis.valueToJava2D(x.doubleValue(), dataArea, plot.getDomainAxisEdge());
        RectangleEdge location = plot.getRangeAxisEdge();
        double yyMax = rangeAxis.valueToJava2D(yMax.doubleValue(), dataArea, location);
        double yyMin = rangeAxis.valueToJava2D(yMin.doubleValue(), dataArea, location);
        double yyMedian = rangeAxis.valueToJava2D(yMedian.doubleValue(), dataArea, location);
        double yyAverage = 0.0;
        if (yAverage != null) {
            yyAverage = rangeAxis.valueToJava2D(yAverage.doubleValue(), dataArea, location);
        }
        double yyQ1Median = rangeAxis.valueToJava2D(yQ1Median.doubleValue(), dataArea, location);
        double yyQ3Median = rangeAxis.valueToJava2D(yQ3Median.doubleValue(), dataArea, location);
        double yyOutlier;
        double exactBoxWidth = getBoxWidth();
        double width = exactBoxWidth;
        double dataAreaX = dataArea.getMaxX() - dataArea.getMinX();
        double maxBoxPercent = 0.1;
        double maxBoxWidth = dataAreaX * maxBoxPercent;
        if (exactBoxWidth <= 0.0) {
            int itemCount = boxAndWhiskerData.getItemCount(series);
            exactBoxWidth = dataAreaX / itemCount * 4.5 / 7;
            if (exactBoxWidth < 3) {
                width = 3;
            } else if (exactBoxWidth > maxBoxWidth) {
                width = maxBoxWidth;
            } else {
                width = exactBoxWidth;
            }
        }
        g2.setPaint(getItemPaint(series, item));
        Stroke s = getItemStroke(series, item);
        g2.setStroke(s);
        // draw the upper shadow
        g2.draw(new Line2D.Double(xx, yyMax, xx, yyQ3Median));
        g2.draw(new Line2D.Double(xx - width / 2, yyMax, xx + width / 2, yyMax));
        // draw the lower shadow
        g2.draw(new Line2D.Double(xx, yyMin, xx, yyQ1Median));
        g2.draw(new Line2D.Double(xx - width / 2, yyMin, xx + width / 2, yyMin));
        // draw the body
        Shape box;
        if (yyQ1Median > yyQ3Median) {
            box = new Rectangle2D.Double(xx - width / 2, yyQ3Median, width, yyQ1Median - yyQ3Median);
        } else {
            box = new Rectangle2D.Double(xx - width / 2, yyQ1Median, width, yyQ3Median - yyQ1Median);
        }
        if (this.fillBox) {
            g2.setPaint(lookupBoxPaint(series, item));
            g2.fill(box);
        }
        g2.setStroke(getItemOutlineStroke(series, item));
        g2.setPaint(getItemOutlinePaint(series, item));
        g2.draw(box);
        // draw median
        g2.setPaint(getArtifactPaint());
        g2.draw(new Line2D.Double(xx - width / 2, yyMedian, xx + width / 2, yyMedian));
        // average radius
        double aRadius = 0;
        // outlier radius
        double oRadius = width / 3;
        // draw average - SPECIAL AIMS REQUIREMENT
        if (yAverage != null) {
            aRadius = width / 4;
            // here we check that the average marker will in fact be visible
            // before drawing it...
            if ((yyAverage > (dataArea.getMinY() - aRadius)) && (yyAverage < (dataArea.getMaxY() + aRadius))) {
                Ellipse2D.Double avgEllipse = new Ellipse2D.Double(xx - aRadius, yyAverage - aRadius, aRadius * 2, aRadius * 2);
                g2.fill(avgEllipse);
                g2.draw(avgEllipse);
            }
        }
        List outliers = new ArrayList();
        OutlierListCollection outlierListCollection = new OutlierListCollection();
        /* From outlier array sort out which are outliers and put these into
         * an arraylist. If there are any farouts, set the flag on the
         * OutlierListCollection
         */
        for (int i = 0; i < yOutliers.size(); i++) {
            double outlier = ((Number) yOutliers.get(i)).doubleValue();
            if (outlier > boxAndWhiskerData.getMaxOutlier(series, item).doubleValue()) {
                outlierListCollection.setHighFarOut(true);
            } else if (outlier < boxAndWhiskerData.getMinOutlier(series, item).doubleValue()) {
                outlierListCollection.setLowFarOut(true);
            } else if (outlier > boxAndWhiskerData.getMaxRegularValue(series, item).doubleValue()) {
                yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                outliers.add(new Outlier(xx, yyOutlier, oRadius));
            } else if (outlier < boxAndWhiskerData.getMinRegularValue(series, item).doubleValue()) {
                yyOutlier = rangeAxis.valueToJava2D(outlier, dataArea, location);
                outliers.add(new Outlier(xx, yyOutlier, oRadius));
            }
            Collections.sort(outliers);
        }
        // Process outliers. Each outlier is either added to the appropriate
        // outlier list or a new outlier list is made
        for (Iterator iterator = outliers.iterator(); iterator.hasNext(); ) {
            Outlier outlier = (Outlier) iterator.next();
            outlierListCollection.add(outlier);
        }
        // draw yOutliers
        double maxAxisValue = rangeAxis.valueToJava2D(rangeAxis.getUpperBound(), dataArea, location) + aRadius;
        double minAxisValue = rangeAxis.valueToJava2D(rangeAxis.getLowerBound(), dataArea, location) - aRadius;
        // draw outliers
        for (Iterator iterator = outlierListCollection.iterator(); iterator.hasNext(); ) {
            OutlierList list = (OutlierList) iterator.next();
            Outlier outlier = list.getAveragedOutlier();
            Point2D point = outlier.getPoint();
            if (list.isMultiple()) {
                drawMultipleEllipse(point, width, oRadius, g2);
            } else {
                drawEllipse(point, oRadius, g2);
            }
        }
        // draw farout
        if (outlierListCollection.isHighFarOut()) {
            drawHighFarOut(aRadius, g2, xx, maxAxisValue);
        }
        if (outlierListCollection.isLowFarOut()) {
            drawLowFarOut(aRadius, g2, xx, minAxisValue);
        }
        // add an entity for the item...
        if (entities != null && box.intersects(dataArea)) {
            addEntity(entities, box, dataset, series, item, xx, yyAverage);
        }
    }

    /**
     * Draws an ellipse to represent an outlier.
     *
     * @param point  the location.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    protected void drawEllipse(Point2D point, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot = new Ellipse2D.Double(point.getX() + oRadius / 2, point.getY(), oRadius, oRadius);
        g2.draw(dot);
    }

    /**
     * Draws two ellipses to represent overlapping outliers.
     *
     * @param point  the location.
     * @param boxWidth  the box width.
     * @param oRadius  the radius.
     * @param g2  the graphics device.
     */
    protected void drawMultipleEllipse(Point2D point, double boxWidth, double oRadius, Graphics2D g2) {
        Ellipse2D.Double dot1 = new Ellipse2D.Double(point.getX() - (boxWidth / 2) + oRadius, point.getY(), oRadius, oRadius);
        Ellipse2D.Double dot2 = new Ellipse2D.Double(point.getX() + (boxWidth / 2), point.getY(), oRadius, oRadius);
        g2.draw(dot1);
        g2.draw(dot2);
    }

    /**
     * Draws a triangle to indicate the presence of far out values.
     *
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x value.
     * @param m  the max y value.
     */
    protected void drawHighFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m + side, xx + side, m + side));
        g2.draw(new Line2D.Double(xx - side, m + side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m + side, xx, m));
    }

    /**
     * Draws a triangle to indicate the presence of far out values.
     *
     * @param aRadius  the radius.
     * @param g2  the graphics device.
     * @param xx  the x value.
     * @param m  the min y value.
     */
    protected void drawLowFarOut(double aRadius, Graphics2D g2, double xx, double m) {
        double side = aRadius * 2;
        g2.draw(new Line2D.Double(xx - side, m - side, xx + side, m - side));
        g2.draw(new Line2D.Double(xx - side, m - side, xx, m));
        g2.draw(new Line2D.Double(xx + side, m - side, xx, m));
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
        if (!(obj instanceof XYBoxAndWhiskerRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYBoxAndWhiskerRenderer that = (XYBoxAndWhiskerRenderer) obj;
        if (this.boxWidth != that.getBoxWidth()) {
            return false;
        }
        if (!PaintUtils.equal(this.boxPaint, that.boxPaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.artifactPaint, that.artifactPaint)) {
            return false;
        }
        if (this.fillBox != that.fillBox) {
            return false;
        }
        return true;
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
        SerialUtils.writePaint(this.boxPaint, stream);
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
        this.boxPaint = SerialUtils.readPaint(stream);
        this.artifactPaint = SerialUtils.readPaint(stream);
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
        return super.clone();
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
 * --------------------------
 * StackedXYAreaRenderer.java
 * --------------------------
 * (C) Copyright 2003-2021, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   Christian W. Zuckschwerdt;
 *                   David Gilbert;
 *                   Ulrich Voigt (patch #312);
 *
 */
/**
 * A stacked area renderer for the {@link XYPlot} class.
 * <br><br>
 * The example shown here is generated by the
 * {@code StackedXYAreaRendererDemo1.java} program included in the
 * JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/StackedXYAreaRendererSample.png"
 * alt="StackedXYAreaRendererSample.png">
 * <br><br>
 * SPECIAL NOTE:  This renderer does not currently handle negative data values
 * correctly.  This should get fixed at some point, but the current workaround
 * is to use the {@link StackedXYAreaRenderer2} class instead.
 */
public class StackedXYAreaRenderer extends XYAreaRenderer implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 5217394318178570889L;

    /**
     * A state object for use by this renderer.
     */
    static class StackedXYAreaRendererState extends XYItemRendererState {

        /**
         * The area for the current series.
         */
        private Polygon seriesArea;

        /**
         * The line.
         */
        private Line2D line;

        /**
         * The points from the last series.
         */
        private Stack lastSeriesPoints;

        /**
         * The points for the current series.
         */
        private Stack currentSeriesPoints;

        /**
         * Creates a new state for the renderer.
         *
         * @param info  the plot rendering info.
         */
        public StackedXYAreaRendererState(PlotRenderingInfo info) {
            super(info);
            this.seriesArea = null;
            this.line = new Line2D.Double();
            this.lastSeriesPoints = new Stack();
            this.currentSeriesPoints = new Stack();
        }

        /**
         * Returns the series area.
         *
         * @return The series area.
         */
        public Polygon getSeriesArea() {
            return this.seriesArea;
        }

        /**
         * Sets the series area.
         *
         * @param area  the area.
         */
        public void setSeriesArea(Polygon area) {
            this.seriesArea = area;
        }

        /**
         * Returns the working line.
         *
         * @return The working line.
         */
        public Line2D getLine() {
            return this.line;
        }

        /**
         * Returns the current series points.
         *
         * @return The current series points.
         */
        public Stack getCurrentSeriesPoints() {
            return this.currentSeriesPoints;
        }

        /**
         * Sets the current series points.
         *
         * @param points  the points.
         */
        public void setCurrentSeriesPoints(Stack points) {
            this.currentSeriesPoints = points;
        }

        /**
         * Returns the last series points.
         *
         * @return The last series points.
         */
        public Stack getLastSeriesPoints() {
            return this.lastSeriesPoints;
        }

        /**
         * Sets the last series points.
         *
         * @param points  the points.
         */
        public void setLastSeriesPoints(Stack points) {
            this.lastSeriesPoints = points;
        }
    }

    /**
     * Custom Paint for drawing all shapes, if null defaults to series shapes
     */
    private transient Paint shapePaint = null;

    /**
     * Custom Stroke for drawing all shapes, if null defaults to series
     * strokes.
     */
    private transient Stroke shapeStroke = null;

    /**
     * Creates a new renderer.
     */
    public StackedXYAreaRenderer() {
        this(AREA);
    }

    /**
     * Constructs a new renderer.
     *
     * @param type  the type of the renderer.
     */
    public StackedXYAreaRenderer(int type) {
        this(type, null, null);
    }

    /**
     * Constructs a new renderer.  To specify the type of renderer, use one of
     * the constants: {@code SHAPES}, {@code LINES}, {@code SHAPES_AND_LINES},
     * {@code AREA} or {@code AREA_AND_SHAPES}.
     *
     * @param type  the type of renderer.
     * @param labelGenerator  the tool tip generator ({@code null} permitted).
     * @param urlGenerator  the URL generator ({@code null} permitted).
     */
    public StackedXYAreaRenderer(int type, XYToolTipGenerator labelGenerator, XYURLGenerator urlGenerator) {
        super(type, labelGenerator, urlGenerator);
    }

    /**
     * Returns the paint used for rendering shapes, or {@code null} if
     * using series paints.
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setShapePaint(Paint)
     */
    public Paint getShapePaint() {
        return this.shapePaint;
    }

    /**
     * Sets the paint for rendering shapes and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shapePaint  the paint ({@code null} permitted).
     *
     * @see #getShapePaint()
     */
    public void setShapePaint(Paint shapePaint) {
        this.shapePaint = shapePaint;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used for rendering shapes, or {@code null} if
     * using series strokes.
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setShapeStroke(Stroke)
     */
    public Stroke getShapeStroke() {
        return this.shapeStroke;
    }

    /**
     * Sets the stroke for rendering shapes and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param shapeStroke  the stroke ({@code null} permitted).
     *
     * @see #getShapeStroke()
     */
    public void setShapeStroke(Stroke shapeStroke) {
        this.shapeStroke = shapeStroke;
        fireChangeEvent();
    }

    /**
     * Initialises the renderer. This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return A state object that should be passed to subsequent calls to the
     *         drawItem() method.
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = new StackedXYAreaRendererState(info);
        // in the rendering process, there is special handling for item
        // zero, so we can't support processing of visible data items only
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    /**
     * Returns the number of passes required by the renderer.
     *
     * @return 2.
     */
    @Override
    public int getPassCount() {
        return 2;
    }

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range ([0.0, 0.0] if the dataset contains no values, and
     *         {@code null} if the dataset is {@code null}).
     *
     * @throws ClassCastException if {@code dataset} is not an instance
     *         of {@link TableXYDataset}.
     */
    @Override
    public Range findRangeBounds(XYDataset dataset) {
        if (dataset != null) {
            return DatasetUtils.findStackedRangeBounds((TableXYDataset) dataset);
        } else {
            return null;
        }
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
     * @param crosshairState  information about crosshairs on a plot.
     * @param pass  the pass index.
     *
     * @throws ClassCastException if {@code state} is not an instance of
     *         {@code StackedXYAreaRendererState} or {@code dataset}
     *         is not an instance of {@link TableXYDataset}.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        PlotOrientation orientation = plot.getOrientation();
        StackedXYAreaRendererState areaState = (StackedXYAreaRendererState) state;
        // Get the item count for the series, so that we can know which is the
        // end of the series.
        TableXYDataset tdataset = (TableXYDataset) dataset;
        int itemCount = tdataset.getItemCount();
        // get the data point...
        double x1 = dataset.getXValue(series, item);
        double y1 = dataset.getYValue(series, item);
        boolean nullPoint = false;
        if (Double.isNaN(y1)) {
            y1 = 0.0;
            nullPoint = true;
        }
        //  Get height adjustment based on stack and translate to Java2D values
        double ph1 = getPreviousHeight(tdataset, series, item);
        double transX1 = domainAxis.valueToJava2D(x1, dataArea, plot.getDomainAxisEdge());
        double transY1 = rangeAxis.valueToJava2D(y1 + ph1, dataArea, plot.getRangeAxisEdge());
        //  Get series Paint and Stroke
        Paint seriesPaint = getItemPaint(series, item);
        Paint seriesFillPaint = seriesPaint;
        if (getUseFillPaint()) {
            seriesFillPaint = getItemFillPaint(series, item);
        }
        Stroke seriesStroke = getItemStroke(series, item);
        if (pass == 0) {
            //  On first pass render the areas, line and outlines
            if (item == 0) {
                // Create a new Area for the series
                areaState.setSeriesArea(new Polygon());
                areaState.setLastSeriesPoints(areaState.getCurrentSeriesPoints());
                areaState.setCurrentSeriesPoints(new Stack());
                // start from previous height (ph1)
                double transY2 = rangeAxis.valueToJava2D(ph1, dataArea, plot.getRangeAxisEdge());
                // The first point is (x, 0)
                if (orientation == PlotOrientation.VERTICAL) {
                    areaState.getSeriesArea().addPoint((int) transX1, (int) transY2);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    areaState.getSeriesArea().addPoint((int) transY2, (int) transX1);
                }
            }
            // Add each point to Area (x, y)
            if (orientation == PlotOrientation.VERTICAL) {
                Point point = new Point((int) transX1, (int) transY1);
                areaState.getSeriesArea().addPoint((int) point.getX(), (int) point.getY());
                areaState.getCurrentSeriesPoints().push(point);
            } else if (orientation == PlotOrientation.HORIZONTAL) {
                areaState.getSeriesArea().addPoint((int) transY1, (int) transX1);
            }
            if (getPlotLines()) {
                if (item > 0) {
                    // get the previous data point...
                    double x0 = dataset.getXValue(series, item - 1);
                    double y0 = dataset.getYValue(series, item - 1);
                    double ph0 = getPreviousHeight(tdataset, series, item - 1);
                    double transX0 = domainAxis.valueToJava2D(x0, dataArea, plot.getDomainAxisEdge());
                    double transY0 = rangeAxis.valueToJava2D(y0 + ph0, dataArea, plot.getRangeAxisEdge());
                    if (orientation == PlotOrientation.VERTICAL) {
                        areaState.getLine().setLine(transX0, transY0, transX1, transY1);
                    } else if (orientation == PlotOrientation.HORIZONTAL) {
                        areaState.getLine().setLine(transY0, transX0, transY1, transX1);
                    }
                    g2.setPaint(seriesPaint);
                    g2.setStroke(seriesStroke);
                    g2.draw(areaState.getLine());
                }
            }
            // Check if the item is the last item for the series and number of
            // items > 0.  We can't draw an area for a single point.
            if (getPlotArea() && item > 0 && item == (itemCount - 1)) {
                double transY2 = rangeAxis.valueToJava2D(ph1, dataArea, plot.getRangeAxisEdge());
                if (orientation == PlotOrientation.VERTICAL) {
                    // Add the last point (x,0)
                    areaState.getSeriesArea().addPoint((int) transX1, (int) transY2);
                } else if (orientation == PlotOrientation.HORIZONTAL) {
                    // Add the last point (x,0)
                    areaState.getSeriesArea().addPoint((int) transY2, (int) transX1);
                }
                // Add points from last series to complete the base of the
                // polygon
                if (series != 0) {
                    Stack points = areaState.getLastSeriesPoints();
                    while (!points.empty()) {
                        Point point = (Point) points.pop();
                        areaState.getSeriesArea().addPoint((int) point.getX(), (int) point.getY());
                    }
                }
                //  Fill the polygon
                g2.setPaint(seriesFillPaint);
                g2.setStroke(seriesStroke);
                g2.fill(areaState.getSeriesArea());
                //  Draw an outline around the Area.
                if (isOutline()) {
                    g2.setStroke(lookupSeriesOutlineStroke(series));
                    g2.setPaint(lookupSeriesOutlinePaint(series));
                    g2.draw(areaState.getSeriesArea());
                }
            }
            int datasetIndex = plot.indexOf(dataset);
            updateCrosshairValues(crosshairState, x1, ph1 + y1, datasetIndex, transX1, transY1, orientation);
        } else if (pass == 1) {
            // On second pass render shapes and collect entity and tooltip
            // information
            Shape shape = null;
            if (getPlotShapes()) {
                shape = getItemShape(series, item);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    shape = ShapeUtils.createTranslatedShape(shape, transX1, transY1);
                } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    shape = ShapeUtils.createTranslatedShape(shape, transY1, transX1);
                }
                if (!nullPoint) {
                    if (getShapePaint() != null) {
                        g2.setPaint(getShapePaint());
                    } else {
                        g2.setPaint(seriesPaint);
                    }
                    if (getShapeStroke() != null) {
                        g2.setStroke(getShapeStroke());
                    } else {
                        g2.setStroke(seriesStroke);
                    }
                    g2.draw(shape);
                }
            } else {
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    shape = new Rectangle2D.Double(transX1 - 3, transY1 - 3, 6.0, 6.0);
                } else if (plot.getOrientation() == PlotOrientation.HORIZONTAL) {
                    shape = new Rectangle2D.Double(transY1 - 3, transX1 - 3, 6.0, 6.0);
                }
            }
            // collect entity and tool tip information...
            if (state.getInfo() != null) {
                EntityCollection entities = state.getEntityCollection();
                if (entities != null && shape != null && !nullPoint) {
                    // limit the entity hotspot area to the data area
                    Area dataAreaHotspot = new Area(shape);
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
    }

    /**
     * Calculates the stacked value of the all series up to, but not including
     * {@code series} for the specified item. It returns 0.0 if
     * {@code series} is the first series, i.e. 0.
     *
     * @param dataset  the dataset.
     * @param series  the series.
     * @param index  the index.
     *
     * @return The cumulative value for all series' values up to but excluding
     *         {@code series} for {@code index}.
     */
    protected double getPreviousHeight(TableXYDataset dataset, int series, int index) {
        double result = 0.0;
        for (int i = 0; i < series; i++) {
            double value = dataset.getYValue(i, index);
            if (!Double.isNaN(value)) {
                result += value;
            }
        }
        return result;
    }

    /**
     * Tests the renderer for equality with an arbitrary object.
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
        if (!(obj instanceof StackedXYAreaRenderer) || !super.equals(obj)) {
            return false;
        }
        StackedXYAreaRenderer that = (StackedXYAreaRenderer) obj;
        if (!PaintUtils.equal(this.shapePaint, that.shapePaint)) {
            return false;
        }
        if (!Objects.equals(this.shapeStroke, that.shapeStroke)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if the renderer cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        this.shapePaint = SerialUtils.readPaint(stream);
        this.shapeStroke = SerialUtils.readStroke(stream);
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
        SerialUtils.writePaint(this.shapePaint, stream);
        SerialUtils.writeStroke(this.shapeStroke, stream);
    }
}
