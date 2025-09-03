package LOC.s;
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
class XYPlot<S extends Comparable<S>> extends Plot implements ValueAxisPlot, Pannable, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable {

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
 * -----------------
 * AreaRenderer.java
 * -----------------
 * (C) Copyright 2002-2021, by Jon Iles and Contributors.
 *
 * Original Author:  Jon Iles;
 * Contributor(s):   David Gilbert;
 *                   Christian W. Zuckschwerdt;
 *
 */
/**
 * A category item renderer that draws area charts.  You can use this renderer
 * with the {@link CategoryPlot} class.  The example shown here is generated
 * by the {@code AreaChartDemo1.java} program included in the JFreeChart
 * Demo Collection:
 * <br><br>
 * <img src="doc-files/AreaRendererSample.png" alt="AreaRendererSample.png">
 */
class AreaRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -4231878281385812757L;

    /**
     * A flag that controls how the ends of the areas are drawn.
     */
    private AreaRendererEndType endType;

    /**
     * Creates a new renderer.
     */
    public AreaRenderer() {
        super();
        this.endType = AreaRendererEndType.TAPER;
        setDefaultLegendShape(new Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0));
    }

    /**
     * Returns a token that controls how the renderer draws the end points.
     * The default value is {@link AreaRendererEndType#TAPER}.
     *
     * @return The end type (never {@code null}).
     *
     * @see #setEndType
     */
    public AreaRendererEndType getEndType() {
        return this.endType;
    }

    /**
     * Sets a token that controls how the renderer draws the end points, and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param type  the end type ({@code null} not permitted).
     *
     * @see #getEndType()
     */
    public void setEndType(AreaRendererEndType type) {
        Args.nullNotPermitted(type, "type");
        this.endType = type;
        fireChangeEvent();
    }

    /**
     * Returns a legend item for a series.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return The legend item.
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        // if there is no plot, there is no dataset to access...
        CategoryPlot cp = getPlot();
        if (cp == null) {
            return null;
        }
        // check that a legend item needs to be displayed...
        if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series)) {
            return null;
        }
        CategoryDataset dataset = cp.getDataset(datasetIndex);
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
        Paint paint = lookupSeriesPaint(series);
        Paint outlinePaint = lookupSeriesOutlinePaint(series);
        Stroke outlineStroke = lookupSeriesOutlineStroke(series);
        LegendItem result = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
        result.setLabelFont(lookupLegendTextFont(series));
        Paint labelPaint = lookupLegendTextPaint(series);
        if (labelPaint != null) {
            result.setLabelPaint(labelPaint);
        }
        result.setDataset(dataset);
        result.setDatasetIndex(datasetIndex);
        result.setSeriesKey(dataset.getRowKey(series));
        result.setSeriesIndex(series);
        return result;
    }

    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the dataset.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, int pass) {
        // do nothing if item is not visible or null
        if (!getItemVisible(row, column)) {
            return;
        }
        Number value = dataset.getValue(row, column);
        if (value == null) {
            return;
        }
        PlotOrientation orientation = plot.getOrientation();
        RectangleEdge axisEdge = plot.getDomainAxisEdge();
        int count = dataset.getColumnCount();
        float x0 = (float) domainAxis.getCategoryStart(column, count, dataArea, axisEdge);
        float x1 = (float) domainAxis.getCategoryMiddle(column, count, dataArea, axisEdge);
        float x2 = (float) domainAxis.getCategoryEnd(column, count, dataArea, axisEdge);
        x0 = Math.round(x0);
        x1 = Math.round(x1);
        x2 = Math.round(x2);
        if (this.endType == AreaRendererEndType.TRUNCATE) {
            if (column == 0) {
                x0 = x1;
            } else if (column == getColumnCount() - 1) {
                x2 = x1;
            }
        }
        double yy1 = value.doubleValue();
        double yy0 = 0.0;
        if (this.endType == AreaRendererEndType.LEVEL) {
            yy0 = yy1;
        }
        if (column > 0) {
            Number n0 = dataset.getValue(row, column - 1);
            if (n0 != null) {
                yy0 = (n0.doubleValue() + yy1) / 2.0;
            }
        }
        double yy2 = 0.0;
        if (column < dataset.getColumnCount() - 1) {
            Number n2 = dataset.getValue(row, column + 1);
            if (n2 != null) {
                yy2 = (n2.doubleValue() + yy1) / 2.0;
            }
        } else if (this.endType == AreaRendererEndType.LEVEL) {
            yy2 = yy1;
        }
        RectangleEdge edge = plot.getRangeAxisEdge();
        float y0 = (float) rangeAxis.valueToJava2D(yy0, dataArea, edge);
        float y1 = (float) rangeAxis.valueToJava2D(yy1, dataArea, edge);
        float y2 = (float) rangeAxis.valueToJava2D(yy2, dataArea, edge);
        float yz = (float) rangeAxis.valueToJava2D(0.0, dataArea, edge);
        double labelXX = x1;
        double labelYY = y1;
        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));
        GeneralPath area = new GeneralPath();
        if (orientation == PlotOrientation.VERTICAL) {
            area.moveTo(x0, yz);
            area.lineTo(x0, y0);
            area.lineTo(x1, y1);
            area.lineTo(x2, y2);
            area.lineTo(x2, yz);
        } else if (orientation == PlotOrientation.HORIZONTAL) {
            area.moveTo(yz, x0);
            area.lineTo(y0, x0);
            area.lineTo(y1, x1);
            area.lineTo(y2, x2);
            area.lineTo(yz, x2);
            double temp = labelXX;
            labelXX = labelYY;
            labelYY = temp;
        }
        area.closePath();
        g2.setPaint(getItemPaint(row, column));
        g2.fill(area);
        // draw the item labels if there are any...
        if (isItemLabelVisible(row, column)) {
            drawItemLabel(g2, orientation, dataset, row, column, labelXX, labelYY, (value.doubleValue() < 0.0));
        }
        // submit the current data point as a crosshair candidate
        int datasetIndex = plot.indexOf(dataset);
        updateCrosshairValues(state.getCrosshairState(), dataset.getRowKey(row), dataset.getColumnKey(column), yy1, datasetIndex, x1, y1, orientation);
        // add an item entity, if this information is being collected
        EntityCollection entities = state.getEntityCollection();
        if (entities != null) {
            addItemEntity(entities, dataset, row, column, area);
        }
    }

    /**
     * Tests this instance for equality with an arbitrary object.
     *
     * @param obj  the object to test ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AreaRenderer)) {
            return false;
        }
        AreaRenderer that = (AreaRenderer) obj;
        if (!this.endType.equals(that.endType)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns an independent copy of the renderer.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  should not happen.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
