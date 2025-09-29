package OCC.bo;
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
 * ------------------
 * AbstractBlock.java
 * ------------------
 * (C) Copyright 2004-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 * A convenience class for creating new classes that implement
 * the {@link Block} interface.
 */
public class AbstractBlock implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 7689852412141274563L;

    /**
     * The id for the block.
     */
    private String id;

    /**
     * The margin around the outside of the block.
     */
    private RectangleInsets margin;

    /**
     * The frame (or border) for the block.
     */
    private BlockFrame frame;

    /**
     * The padding between the block content and the border.
     */
    private RectangleInsets padding;

    /**
     * The natural width of the block (may be overridden if there are
     * constraints in sizing).
     */
    private double width;

    /**
     * The natural height of the block (may be overridden if there are
     * constraints in sizing).
     */
    private double height;

    /**
     * The current bounds for the block (position of the block in Java2D space).
     */
    private transient Rectangle2D bounds;

    /**
     * Creates a new block.
     */
    protected AbstractBlock() {
        this.id = null;
        this.width = 0.0;
        this.height = 0.0;
        this.bounds = new Rectangle2D.Float();
        this.margin = RectangleInsets.ZERO_INSETS;
        this.frame = BlockBorder.NONE;
        this.padding = RectangleInsets.ZERO_INSETS;
    }

    /**
     * Returns the id.
     *
     * @return The id (possibly {@code null}).
     *
     * @see #setID(String)
     */
    public String getID() {
        return this.id;
    }

    /**
     * Sets the id for the block.
     *
     * @param id  the id ({@code null} permitted).
     *
     * @see #getID()
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Returns the natural width of the block, if this is known in advance.
     * The actual width of the block may be overridden if layout constraints
     * make this necessary.
     *
     * @return The width.
     *
     * @see #setWidth(double)
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Sets the natural width of the block, if this is known in advance.
     *
     * @param width  the width (in Java2D units)
     *
     * @see #getWidth()
     */
    public void setWidth(double width) {
        this.width = width;
    }

    /**
     * Returns the natural height of the block, if this is known in advance.
     * The actual height of the block may be overridden if layout constraints
     * make this necessary.
     *
     * @return The height.
     *
     * @see #setHeight(double)
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Sets the natural width of the block, if this is known in advance.
     *
     * @param height  the width (in Java2D units)
     *
     * @see #getHeight()
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns the margin.
     *
     * @return The margin (never {@code null}).
     *
     * @see #getMargin()
     */
    public RectangleInsets getMargin() {
        return this.margin;
    }

    /**
     * Sets the margin (use {@link RectangleInsets#ZERO_INSETS} for no
     * padding).
     *
     * @param margin  the margin ({@code null} not permitted).
     *
     * @see #getMargin()
     */
    public void setMargin(RectangleInsets margin) {
        Args.nullNotPermitted(margin, "margin");
        this.margin = margin;
    }

    /**
     * Sets the margin.
     *
     * @param top  the top margin.
     * @param left  the left margin.
     * @param bottom  the bottom margin.
     * @param right  the right margin.
     *
     * @see #getMargin()
     */
    public void setMargin(double top, double left, double bottom, double right) {
        setMargin(new RectangleInsets(top, left, bottom, right));
    }

    /**
     * Sets a black border with the specified line widths.
     *
     * @param top  the top border line width.
     * @param left  the left border line width.
     * @param bottom  the bottom border line width.
     * @param right  the right border line width.
     */
    public void setBorder(double top, double left, double bottom, double right) {
        setFrame(new BlockBorder(top, left, bottom, right));
    }

    /**
     * Returns the current frame (border).
     *
     * @return The frame.
     *
     * @see #setFrame(BlockFrame)
     */
    public BlockFrame getFrame() {
        return this.frame;
    }

    /**
     * Sets the frame (or border).
     *
     * @param frame  the frame ({@code null} not permitted).
     *
     * @see #getFrame()
     */
    public void setFrame(BlockFrame frame) {
        Args.nullNotPermitted(frame, "frame");
        this.frame = frame;
    }

    /**
     * Returns the padding.
     *
     * @return The padding (never {@code null}).
     *
     * @see #setPadding(RectangleInsets)
     */
    public RectangleInsets getPadding() {
        return this.padding;
    }

    /**
     * Sets the padding (use {@link RectangleInsets#ZERO_INSETS} for no
     * padding).
     *
     * @param padding  the padding ({@code null} not permitted).
     *
     * @see #getPadding()
     */
    public void setPadding(RectangleInsets padding) {
        Args.nullNotPermitted(padding, "padding");
        this.padding = padding;
    }

    /**
     * Sets the padding.
     *
     * @param top  the top padding.
     * @param left  the left padding.
     * @param bottom  the bottom padding.
     * @param right  the right padding.
     */
    public void setPadding(double top, double left, double bottom, double right) {
        setPadding(new RectangleInsets(top, left, bottom, right));
    }

    /**
     * Returns the x-offset for the content within the block.
     *
     * @return The x-offset.
     *
     * @see #getContentYOffset()
     */
    public double getContentXOffset() {
        return this.margin.getLeft() + this.frame.getInsets().getLeft() + this.padding.getLeft();
    }

    /**
     * Returns the y-offset for the content within the block.
     *
     * @return The y-offset.
     *
     * @see #getContentXOffset()
     */
    public double getContentYOffset() {
        return this.margin.getTop() + this.frame.getInsets().getTop() + this.padding.getTop();
    }

    /**
     * Arranges the contents of the block, with no constraints, and returns
     * the block size.
     *
     * @param g2  the graphics device.
     *
     * @return The block size (in Java2D units, never {@code null}).
     */
    public Size2D arrange(Graphics2D g2) {
        return arrange(g2, RectangleConstraint.NONE);
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
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        Size2D base = new Size2D(getWidth(), getHeight());
        return constraint.calculateConstrainedSize(base);
    }

    /**
     * Returns the current bounds of the block.
     *
     * @return The bounds.
     *
     * @see #setBounds(Rectangle2D)
     */
    public Rectangle2D getBounds() {
        return this.bounds;
    }

    /**
     * Sets the bounds of the block.
     *
     * @param bounds  the bounds ({@code null} not permitted).
     *
     * @see #getBounds()
     */
    public void setBounds(Rectangle2D bounds) {
        Args.nullNotPermitted(bounds, "bounds");
        this.bounds = bounds;
    }

    /**
     * Calculate the width available for content after subtracting
     * the margin, border and padding space from the specified fixed
     * width.
     *
     * @param fixedWidth  the fixed width.
     *
     * @return The available space.
     *
     * @see #trimToContentHeight(double)
     */
    protected double trimToContentWidth(double fixedWidth) {
        double result = this.margin.trimWidth(fixedWidth);
        result = this.frame.getInsets().trimWidth(result);
        result = this.padding.trimWidth(result);
        return Math.max(result, 0.0);
    }

    /**
     * Calculate the height available for content after subtracting
     * the margin, border and padding space from the specified fixed
     * height.
     *
     * @param fixedHeight  the fixed height.
     *
     * @return The available space.
     *
     * @see #trimToContentWidth(double)
     */
    protected double trimToContentHeight(double fixedHeight) {
        double result = this.margin.trimHeight(fixedHeight);
        result = this.frame.getInsets().trimHeight(result);
        result = this.padding.trimHeight(result);
        return Math.max(result, 0.0);
    }

    /**
     * Returns a constraint for the content of this block that will result in
     * the bounds of the block matching the specified constraint.
     *
     * @param c  the outer constraint ({@code null} not permitted).
     *
     * @return The content constraint.
     */
    protected RectangleConstraint toContentConstraint(RectangleConstraint c) {
        Args.nullNotPermitted(c, "c");
        if (c.equals(RectangleConstraint.NONE)) {
            return c;
        }
        double w = c.getWidth();
        Range wr = c.getWidthRange();
        double h = c.getHeight();
        Range hr = c.getHeightRange();
        double ww = trimToContentWidth(w);
        double hh = trimToContentHeight(h);
        Range wwr = trimToContentWidth(wr);
        Range hhr = trimToContentHeight(hr);
        return new RectangleConstraint(ww, wwr, c.getWidthConstraintType(), hh, hhr, c.getHeightConstraintType());
    }

    private Range trimToContentWidth(Range r) {
        if (r == null) {
            return null;
        }
        double lowerBound = 0.0;
        double upperBound = Double.POSITIVE_INFINITY;
        if (r.getLowerBound() > 0.0) {
            lowerBound = trimToContentWidth(r.getLowerBound());
        }
        if (r.getUpperBound() < Double.POSITIVE_INFINITY) {
            upperBound = trimToContentWidth(r.getUpperBound());
        }
        return new Range(lowerBound, upperBound);
    }

    private Range trimToContentHeight(Range r) {
        if (r == null) {
            return null;
        }
        double lowerBound = 0.0;
        double upperBound = Double.POSITIVE_INFINITY;
        if (r.getLowerBound() > 0.0) {
            lowerBound = trimToContentHeight(r.getLowerBound());
        }
        if (r.getUpperBound() < Double.POSITIVE_INFINITY) {
            upperBound = trimToContentHeight(r.getUpperBound());
        }
        return new Range(lowerBound, upperBound);
    }

    /**
     * Adds the margin, border and padding to the specified content width.
     *
     * @param contentWidth  the content width.
     *
     * @return The adjusted width.
     */
    protected double calculateTotalWidth(double contentWidth) {
        double result = contentWidth;
        result = this.padding.extendWidth(result);
        result = this.frame.getInsets().extendWidth(result);
        result = this.margin.extendWidth(result);
        return result;
    }

    /**
     * Adds the margin, border and padding to the specified content height.
     *
     * @param contentHeight  the content height.
     *
     * @return The adjusted height.
     */
    protected double calculateTotalHeight(double contentHeight) {
        double result = contentHeight;
        result = this.padding.extendHeight(result);
        result = this.frame.getInsets().extendHeight(result);
        result = this.margin.extendHeight(result);
        return result;
    }

    /**
     * Reduces the specified area by the amount of space consumed
     * by the margin.
     *
     * @param area  the area ({@code null} not permitted).
     *
     * @return The trimmed area.
     */
    protected Rectangle2D trimMargin(Rectangle2D area) {
        // defer argument checking...
        this.margin.trim(area);
        return area;
    }

    /**
     * Reduces the specified area by the amount of space consumed
     * by the border.
     *
     * @param area  the area ({@code null} not permitted).
     *
     * @return The trimmed area.
     */
    protected Rectangle2D trimBorder(Rectangle2D area) {
        // defer argument checking...
        this.frame.getInsets().trim(area);
        return area;
    }

    /**
     * Reduces the specified area by the amount of space consumed
     * by the padding.
     *
     * @param area  the area ({@code null} not permitted).
     *
     * @return The trimmed area.
     */
    protected Rectangle2D trimPadding(Rectangle2D area) {
        // defer argument checking...
        this.padding.trim(area);
        return area;
    }

    /**
     * Draws the border around the perimeter of the specified area.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     */
    protected void drawBorder(Graphics2D g2, Rectangle2D area) {
        this.frame.draw(g2, area);
    }

    /**
     * Tests this block for equality with an arbitrary object.
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
        if (!(obj instanceof AbstractBlock)) {
            return false;
        }
        AbstractBlock that = (AbstractBlock) obj;
        if (!Objects.equals(this.id, that.id)) {
            return false;
        }
        if (!this.frame.equals(that.frame)) {
            return false;
        }
        if (!this.bounds.equals(that.bounds)) {
            return false;
        }
        if (!this.margin.equals(that.margin)) {
            return false;
        }
        if (!this.padding.equals(that.padding)) {
            return false;
        }
        if (this.height != that.height) {
            return false;
        }
        if (this.width != that.width) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.id);
        hash = 41 * hash + Objects.hashCode(this.margin);
        hash = 41 * hash + Objects.hashCode(this.frame);
        hash = 41 * hash + Objects.hashCode(this.padding);
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.width) ^ (Double.doubleToLongBits(this.width) >>> 32));
        hash = 41 * hash + (int) (Double.doubleToLongBits(this.height) ^ (Double.doubleToLongBits(this.height) >>> 32));
        hash = 41 * hash + Objects.hashCode(this.bounds);
        return hash;
    }

    /**
     * Returns a clone of this block.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem creating the
     *         clone.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractBlock clone = (AbstractBlock) super.clone();
        clone.bounds = (Rectangle2D) CloneUtils.clone(this.bounds);
        if (this.frame instanceof PublicCloneable) {
            PublicCloneable pc = (PublicCloneable) this.frame;
            clone.frame = (BlockFrame) pc.clone();
        }
        return clone;
    }

    /**
     * Provides serialization support.
     *
     * @param stream  the output stream.
     *
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writeShape(this.bounds, stream);
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
        this.bounds = (Rectangle2D) SerialUtils.readShape(stream);
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
 * WindItemRenderer.java
 * ---------------------
 * (C) Copyright 2001-2021, by Achilleus Mantzios and Contributors.
 *
 * Original Author:  Achilleus Mantzios;
 * Contributor(s):   David Gilbert;
 *
 */
/**
 * A specialised renderer for displaying wind intensity/direction data.
 * The example shown here is generated by the {@code WindChartDemo1.java}
 * program included in the JFreeChart demo collection:
 * <br><br>
 * <img src="doc-files/WindItemRendererSample.png"
 * alt="WindItemRendererSample.png">
 */
public class WindItemRenderer extends AbstractXYItemRenderer implements XYItemRenderer, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 8078914101916976844L;

    /**
     * Creates a new renderer.
     */
    public WindItemRenderer() {
        super();
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param plotArea  the area within which the plot is being drawn.
     * @param info  optional information collection.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the horizontal axis.
     * @param rangeAxis  the vertical axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D plotArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        WindDataset windData = (WindDataset) dataset;
        Paint seriesPaint = getItemPaint(series, item);
        Stroke seriesStroke = getItemStroke(series, item);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        // get the data point...
        Number x = windData.getX(series, item);
        Number windDir = windData.getWindDirection(series, item);
        Number wforce = windData.getWindForce(series, item);
        double windForce = wforce.doubleValue();
        double wdirt = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0);
        double ax1, ax2, ay1, ay2, rax2, ray2;
        RectangleEdge domainAxisLocation = plot.getDomainAxisEdge();
        RectangleEdge rangeAxisLocation = plot.getRangeAxisEdge();
        ax1 = domainAxis.valueToJava2D(x.doubleValue(), plotArea, domainAxisLocation);
        ay1 = rangeAxis.valueToJava2D(0.0, plotArea, rangeAxisLocation);
        rax2 = x.doubleValue() + (windForce * Math.cos(wdirt) * 8000000.0);
        ray2 = windForce * Math.sin(wdirt);
        ax2 = domainAxis.valueToJava2D(rax2, plotArea, domainAxisLocation);
        ay2 = rangeAxis.valueToJava2D(ray2, plotArea, rangeAxisLocation);
        int diri = windDir.intValue();
        int forcei = wforce.intValue();
        String dirforce = diri + "-" + forcei;
        Line2D line = new Line2D.Double(ax1, ay1, ax2, ay2);
        g2.draw(line);
        g2.setPaint(Color.BLUE);
        g2.setFont(new Font("Dialog", 1, 9));
        g2.drawString(dirforce, (float) ax1, (float) ay1);
        g2.setPaint(seriesPaint);
        g2.setStroke(seriesStroke);
        double alx2, aly2, arx2, ary2;
        double ralx2, raly2, rarx2, rary2;
        double aldir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 - 5.0);
        ralx2 = wforce.doubleValue() * Math.cos(aldir) * 8000000 * 0.8 + x.doubleValue();
        raly2 = wforce.doubleValue() * Math.sin(aldir) * 0.8;
        alx2 = domainAxis.valueToJava2D(ralx2, plotArea, domainAxisLocation);
        aly2 = rangeAxis.valueToJava2D(raly2, plotArea, rangeAxisLocation);
        line = new Line2D.Double(alx2, aly2, ax2, ay2);
        g2.draw(line);
        double ardir = Math.toRadians(windDir.doubleValue() * (-30.0) - 90.0 + 5.0);
        rarx2 = wforce.doubleValue() * Math.cos(ardir) * 8000000 * 0.8 + x.doubleValue();
        rary2 = wforce.doubleValue() * Math.sin(ardir) * 0.8;
        arx2 = domainAxis.valueToJava2D(rarx2, plotArea, domainAxisLocation);
        ary2 = rangeAxis.valueToJava2D(rary2, plotArea, rangeAxisLocation);
        line = new Line2D.Double(arx2, ary2, ax2, ay2);
        g2.draw(line);
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
