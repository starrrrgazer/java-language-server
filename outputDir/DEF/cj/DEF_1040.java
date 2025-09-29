package DEF.cj;
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
 * CategoryPlot.java
 * -----------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jeremy Bowman;
 *                   Arnaud Lelievre;
 *                   Richard West, Advanced Micro Devices, Inc.;
 *                   Ulrich Voigt - patch 2686040;
 *                   Peter Kolb - patches 2603321 and 2809117;
 *
 */
/**
 * A general plotting class that uses data from a {@link CategoryDataset} and
 * renders each data item using a {@link CategoryItemRenderer}.
 *
 * @param <R> the row key type
 * @param <C> the column key type
 */
public class CategoryPlot<R extends Comparable<R>, C extends Comparable<C>> extends Plot implements ValueAxisPlot, Pannable, Zoomable, AnnotationChangeListener, RendererChangeListener, Cloneable, PublicCloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -3537691700434728188L;

    /**
     * The default visibility of the grid lines plotted against the domain
     * axis.
     */
    public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;

    /**
     * The default visibility of the grid lines plotted against the range
     * axis.
     */
    public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;

    /**
     * The default grid line stroke.
     */
    public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, new float[] { 2.0f, 2.0f }, 0.0f);

    /**
     * The default grid line paint.
     */
    public static final Paint DEFAULT_GRIDLINE_PAINT = Color.LIGHT_GRAY;

    /**
     * The default value label font.
     */
    public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", Font.PLAIN, 10);

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
     * Storage for the domain axes.
     */
    private Map<Integer, CategoryAxis> domainAxes;

    /**
     * Storage for the domain axis locations.
     */
    private Map<Integer, AxisLocation> domainAxisLocations;

    /**
     * A flag that controls whether the shared domain axis is drawn
     * (only relevant when the plot is being used as a subplot).
     */
    private boolean drawSharedDomainAxis;

    /**
     * Storage for the range axes.
     */
    private Map<Integer, ValueAxis> rangeAxes;

    /**
     * Storage for the range axis locations.
     */
    private Map<Integer, AxisLocation> rangeAxisLocations;

    /**
     * Storage for the datasets.
     */
    private Map<Integer, CategoryDataset<R, C>> datasets;

    /**
     * Storage for keys that map each dataset to one or more domain axes.
     * Typically a dataset is rendered using the scale of a single axis, but
     * a dataset can contribute to the "auto-range" of any number of axes.
     */
    private Map<Integer, List<Integer>> datasetToDomainAxesMap;

    /**
     * Storage for keys that map each dataset to one or more range axes.
     * Typically a dataset is rendered using the scale of a single axis, but
     * a dataset can contribute to the "auto-range" of any number of axes.
     */
    private Map<Integer, List<Integer>> datasetToRangeAxesMap;

    /**
     * Storage for the renderers.
     */
    private Map<Integer, CategoryItemRenderer> renderers;

    /**
     * The dataset rendering order.
     */
    private DatasetRenderingOrder renderingOrder = DatasetRenderingOrder.REVERSE;

    /**
     * Controls the order in which the columns are traversed when rendering the
     * data items.
     */
    private SortOrder columnRenderingOrder = SortOrder.ASCENDING;

    /**
     * Controls the order in which the rows are traversed when rendering the
     * data items.
     */
    private SortOrder rowRenderingOrder = SortOrder.ASCENDING;

    /**
     * A flag that controls whether the grid-lines for the domain axis are
     * visible.
     */
    private boolean domainGridlinesVisible;

    /**
     * The position of the domain gridlines relative to the category.
     */
    private CategoryAnchor domainGridlinePosition;

    /**
     * The stroke used to draw the domain grid-lines.
     */
    private transient Stroke domainGridlineStroke;

    /**
     * The paint used to draw the domain  grid-lines.
     */
    private transient Paint domainGridlinePaint;

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
     * A flag that controls whether the grid-lines for the range axis are
     * visible.
     */
    private boolean rangeGridlinesVisible;

    /**
     * The stroke used to draw the range axis grid-lines.
     */
    private transient Stroke rangeGridlineStroke;

    /**
     * The paint used to draw the range axis grid-lines.
     */
    private transient Paint rangeGridlinePaint;

    /**
     * A flag that controls whether gridlines are shown for the minor
     * tick values on the primary range axis.
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
     * The anchor value.
     */
    private double anchorValue;

    /**
     * The index for the dataset that the crosshairs are linked to (this
     * determines which axes the crosshairs are plotted against).
     */
    private int crosshairDatasetIndex;

    /**
     * A flag that controls the visibility of the domain crosshair.
     */
    private boolean domainCrosshairVisible;

    /**
     * The row key for the crosshair point.
     */
    private R domainCrosshairRowKey;

    /**
     * The column key for the crosshair point.
     */
    private C domainCrosshairColumnKey;

    /**
     * The stroke used to draw the domain crosshair if it is visible.
     */
    private transient Stroke domainCrosshairStroke;

    /**
     * The paint used to draw the domain crosshair if it is visible.
     */
    private transient Paint domainCrosshairPaint;

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
     * A map containing lists of markers for the domain axes.
     */
    private Map<Integer, Collection<CategoryMarker>> foregroundDomainMarkers;

    /**
     * A map containing lists of markers for the domain axes.
     */
    private Map<Integer, Collection<CategoryMarker>> backgroundDomainMarkers;

    /**
     * A map containing lists of markers for the range axes.
     */
    private Map<Integer, Collection<Marker>> foregroundRangeMarkers;

    /**
     * A map containing lists of markers for the range axes.
     */
    private Map<Integer, Collection<Marker>> backgroundRangeMarkers;

    /**
     * A (possibly empty) list of annotations for the plot.  The list should
     * be initialised in the constructor and never allowed to be {@code null}.
     */
    private List<CategoryAnnotation> annotations;

    /**
     * The weight for the plot (only relevant when the plot is used as a subplot
     * within a combined plot).
     */
    private int weight;

    /**
     * The fixed space for the domain axis.
     */
    private AxisSpace fixedDomainAxisSpace;

    /**
     * The fixed space for the range axis.
     */
    private AxisSpace fixedRangeAxisSpace;

    /**
     * An optional collection of legend items that can be returned by the
     * getLegendItems() method.
     */
    private LegendItemCollection fixedLegendItems;

    /**
     * A flag that controls whether panning is enabled for the
     * range axis/axes.
     */
    private boolean rangePannable;

    /**
     * The shadow generator for the plot ({@code null} permitted).
     */
    private ShadowGenerator shadowGenerator;

    /**
     * Default constructor.
     */
    public CategoryPlot() {
        this(null, null, null, null);
    }

    /**
     * Creates a new plot.
     *
     * @param dataset  the dataset ({@code null} permitted).
     * @param domainAxis  the domain axis ({@code null} permitted).
     * @param rangeAxis  the range axis ({@code null} permitted).
     * @param renderer  the item renderer ({@code null} permitted).
     */
    public CategoryPlot(CategoryDataset<R, C> dataset, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
        super();
        this.orientation = PlotOrientation.VERTICAL;
        // allocate storage for dataset, axes and renderers
        this.domainAxes = new HashMap<>();
        this.domainAxisLocations = new HashMap<>();
        this.rangeAxes = new HashMap<>();
        this.rangeAxisLocations = new HashMap<>();
        this.datasetToDomainAxesMap = new TreeMap<>();
        this.datasetToRangeAxesMap = new TreeMap<>();
        this.renderers = new HashMap<>();
        this.datasets = new HashMap<>();
        this.datasets.put(0, dataset);
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.axisOffset = RectangleInsets.ZERO_INSETS;
        this.domainAxisLocations.put(0, AxisLocation.BOTTOM_OR_LEFT);
        this.rangeAxisLocations.put(0, AxisLocation.TOP_OR_LEFT);
        this.renderers.put(0, renderer);
        if (renderer != null) {
            renderer.setPlot(this);
            renderer.addChangeListener(this);
        }
        this.domainAxes.put(0, domainAxis);
        mapDatasetToDomainAxis(0, 0);
        if (domainAxis != null) {
            domainAxis.setPlot(this);
            domainAxis.addChangeListener(this);
        }
        this.drawSharedDomainAxis = false;
        this.rangeAxes.put(0, rangeAxis);
        mapDatasetToRangeAxis(0, 0);
        if (rangeAxis != null) {
            rangeAxis.setPlot(this);
            rangeAxis.addChangeListener(this);
        }
        configureDomainAxes();
        configureRangeAxes();
        this.domainGridlinesVisible = DEFAULT_DOMAIN_GRIDLINES_VISIBLE;
        this.domainGridlinePosition = CategoryAnchor.MIDDLE;
        this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeZeroBaselineVisible = false;
        this.rangeZeroBaselinePaint = Color.BLACK;
        this.rangeZeroBaselineStroke = new BasicStroke(0.5f);
        this.rangeGridlinesVisible = DEFAULT_RANGE_GRIDLINES_VISIBLE;
        this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
        this.rangeMinorGridlinesVisible = false;
        this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
        this.rangeMinorGridlinePaint = Color.WHITE;
        this.foregroundDomainMarkers = new HashMap<>();
        this.backgroundDomainMarkers = new HashMap<>();
        this.foregroundRangeMarkers = new HashMap<>();
        this.backgroundRangeMarkers = new HashMap<>();
        this.anchorValue = 0.0;
        this.domainCrosshairVisible = false;
        this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.rangeCrosshairVisible = DEFAULT_CROSSHAIR_VISIBLE;
        this.rangeCrosshairValue = 0.0;
        this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
        this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
        this.annotations = new ArrayList<>();
        this.rangePannable = false;
        this.shadowGenerator = null;
    }

    /**
     * Returns a string describing the type of plot.
     *
     * @return The type.
     */
    @Override
    public String getPlotType() {
        return localizationResources.getString("Category_Plot");
    }

    /**
     * Returns the orientation of the plot.
     *
     * @return The orientation of the plot (never {@code null}).
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
     * @param orientation  the orientation ({@code null} not permitted).
     *
     * @see #getOrientation()
     */
    public void setOrientation(PlotOrientation orientation) {
        Args.nullNotPermitted(orientation, "orientation");
        this.orientation = orientation;
        fireChangeEvent();
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
     * Sets the axis offsets (gap between the data area and the axes) and
     * sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the domain axis for the plot.  If the domain axis for this plot
     * is {@code null}, then the method will return the parent plot's
     * domain axis (if there is a parent plot).
     *
     * @return The domain axis ({@code null} permitted).
     *
     * @see #setDomainAxis(CategoryAxis)
     */
    public CategoryAxis getDomainAxis() {
        return getDomainAxis(0);
    }

    /**
     * Returns a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     *
     * @see #setDomainAxis(int, CategoryAxis)
     */
    public CategoryAxis getDomainAxis(int index) {
        CategoryAxis result = this.domainAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                @SuppressWarnings("unchecked")
                CategoryPlot<R, C> cp = (CategoryPlot) parent;
                result = cp.getDomainAxis(index);
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
    public Map<Integer, CategoryAxis> getDomainAxes() {
        return Collections.unmodifiableMap(this.domainAxes);
    }

    /**
     * Sets the domain axis for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param axis  the axis ({@code null} permitted).
     *
     * @see #getDomainAxis()
     */
    public void setDomainAxis(CategoryAxis axis) {
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
     */
    public void setDomainAxis(int index, CategoryAxis axis) {
        setDomainAxis(index, axis, true);
    }

    /**
     * Sets a domain axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis ({@code null} permitted).
     * @param notify  notify listeners?
     */
    public void setDomainAxis(int index, CategoryAxis axis, boolean notify) {
        CategoryAxis existing = this.domainAxes.get(index);
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
    public void setDomainAxes(CategoryAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setDomainAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the index of the specified axis, or {@code -1} if the axis
     * is not assigned to the plot.
     *
     * @param axis  the axis ({@code null} not permitted).
     *
     * @return The axis index.
     *
     * @see #getDomainAxis(int)
     * @see #getRangeAxisIndex(ValueAxis)
     */
    public int getDomainAxisIndex(CategoryAxis axis) {
        Args.nullNotPermitted(axis, "axis");
        for (Entry<Integer, CategoryAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the domain axis location for the primary domain axis.
     *
     * @return The location (never {@code null}).
     *
     * @see #getRangeAxisLocation()
     */
    public AxisLocation getDomainAxisLocation() {
        return getDomainAxisLocation(0);
    }

    /**
     * Returns the location for a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     *
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public AxisLocation getDomainAxisLocation(int index) {
        AxisLocation result = this.domainAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getDomainAxisLocation(0));
        }
        return result;
    }

    /**
     * Sets the location of the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param location  the axis location ({@code null} not permitted).
     *
     * @see #getDomainAxisLocation()
     * @see #setDomainAxisLocation(int, AxisLocation)
     */
    public void setDomainAxisLocation(AxisLocation location) {
        // delegate...
        setDomainAxisLocation(0, location, true);
    }

    /**
     * Sets the location of the domain axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the axis location ({@code null} not permitted).
     * @param notify  a flag that controls whether listeners are notified.
     */
    public void setDomainAxisLocation(AxisLocation location, boolean notify) {
        // delegate...
        setDomainAxisLocation(0, location, notify);
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     *
     * @see #getDomainAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public void setDomainAxisLocation(int index, AxisLocation location) {
        // delegate...
        setDomainAxisLocation(index, location, true);
    }

    /**
     * Sets the location for a domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
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
     * Returns the domain axis edge.  This is derived from the axis location
     * and the plot orientation.
     *
     * @return The edge (never {@code null}).
     */
    public RectangleEdge getDomainAxisEdge() {
        return getDomainAxisEdge(0);
    }

    /**
     * Returns the edge for a domain axis.
     *
     * @param index  the axis index.
     *
     * @return The edge (never {@code null}).
     */
    public RectangleEdge getDomainAxisEdge(int index) {
        RectangleEdge result;
        AxisLocation location = getDomainAxisLocation(index);
        if (location != null) {
            result = Plot.resolveDomainAxisLocation(location, this.orientation);
        } else {
            result = RectangleEdge.opposite(getDomainAxisEdge(0));
        }
        return result;
    }

    /**
     * Returns the number of domain axes.
     *
     * @return The axis count.
     */
    public int getDomainAxisCount() {
        return this.domainAxes.size();
    }

    /**
     * Clears the domain axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     */
    public void clearDomainAxes() {
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.removeChangeListener(this);
            }
        }
        this.domainAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the domain axes.
     */
    public void configureDomainAxes() {
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.configure();
            }
        }
    }

    /**
     * Returns the range axis for the plot.  If the range axis for this plot is
     * null, then the method will return the parent plot's range axis (if there
     * is a parent plot).
     *
     * @return The range axis (possibly {@code null}).
     */
    public ValueAxis getRangeAxis() {
        return getRangeAxis(0);
    }

    /**
     * Returns a range axis.
     *
     * @param index  the axis index.
     *
     * @return The axis ({@code null} possible).
     */
    public ValueAxis getRangeAxis(int index) {
        ValueAxis result = this.rangeAxes.get(index);
        if (result == null) {
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                @SuppressWarnings("unchecked")
                CategoryPlot<R, C> cp = (CategoryPlot) parent;
                result = cp.getRangeAxis(index);
            }
        }
        return result;
    }

    /**
     * Returns a map containing the range axes that are assigned to this plot.
     * The map is unmodifiable.
     *
     * @return A map containing the domain axes that are assigned to the plot
     *     (never {@code null}).
     *
     * @since 1.5.4
     */
    public Map<Integer, ValueAxis> getRangeAxes() {
        return Collections.unmodifiableMap(this.rangeAxes);
    }

    /**
     * Sets the range axis for the plot and sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param axis  the axis ({@code null} permitted).
     */
    public void setRangeAxis(ValueAxis axis) {
        setRangeAxis(0, axis);
    }

    /**
     * Sets a range axis and sends a {@link PlotChangeEvent} to all registered
     * listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     */
    public void setRangeAxis(int index, ValueAxis axis) {
        setRangeAxis(index, axis, true);
    }

    /**
     * Sets a range axis and, if requested, sends a {@link PlotChangeEvent} to
     * all registered listeners.
     *
     * @param index  the axis index.
     * @param axis  the axis.
     * @param notify  notify listeners?
     */
    public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
        ValueAxis existing = this.rangeAxes.get(index);
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
     * @see #setDomainAxes(CategoryAxis[])
     */
    public void setRangeAxes(ValueAxis[] axes) {
        for (int i = 0; i < axes.length; i++) {
            setRangeAxis(i, axes[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the index of the specified axis, or {@code -1} if the axis
     * is not assigned to the plot.
     *
     * @param axis  the axis ({@code null} not permitted).
     *
     * @return The axis index.
     *
     * @see #getRangeAxis(int)
     * @see #getDomainAxisIndex(CategoryAxis)
     */
    public int getRangeAxisIndex(ValueAxis axis) {
        Args.nullNotPermitted(axis, "axis");
        int result = findRangeAxisIndex(axis);
        if (result < 0) {
            // try the parent plot
            Plot parent = getParent();
            if (parent instanceof CategoryPlot) {
                @SuppressWarnings("unchecked")
                CategoryPlot<R, C> p = (CategoryPlot) parent;
                result = p.getRangeAxisIndex(axis);
            }
        }
        return result;
    }

    private int findRangeAxisIndex(ValueAxis axis) {
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() == axis) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the range axis location.
     *
     * @return The location (never {@code null}).
     */
    public AxisLocation getRangeAxisLocation() {
        return getRangeAxisLocation(0);
    }

    /**
     * Returns the location for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The location.
     *
     * @see #setRangeAxisLocation(int, AxisLocation)
     */
    public AxisLocation getRangeAxisLocation(int index) {
        AxisLocation result = this.rangeAxisLocations.get(index);
        if (result == null) {
            result = AxisLocation.getOpposite(getRangeAxisLocation(0));
        }
        return result;
    }

    /**
     * Sets the location of the range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     *
     * @see #setRangeAxisLocation(AxisLocation, boolean)
     * @see #setDomainAxisLocation(AxisLocation)
     */
    public void setRangeAxisLocation(AxisLocation location) {
        // defer argument checking...
        setRangeAxisLocation(location, true);
    }

    /**
     * Sets the location of the range axis and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param location  the location ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #setDomainAxisLocation(AxisLocation, boolean)
     */
    public void setRangeAxisLocation(AxisLocation location, boolean notify) {
        setRangeAxisLocation(0, location, notify);
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
     *
     * @see #getRangeAxisLocation(int)
     * @see #setRangeAxisLocation(int, AxisLocation, boolean)
     */
    public void setRangeAxisLocation(int index, AxisLocation location) {
        setRangeAxisLocation(index, location, true);
    }

    /**
     * Sets the location for a range axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param index  the axis index.
     * @param location  the location.
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
     * Returns the edge where the primary range axis is located.
     *
     * @return The edge (never {@code null}).
     */
    public RectangleEdge getRangeAxisEdge() {
        return getRangeAxisEdge(0);
    }

    /**
     * Returns the edge for a range axis.
     *
     * @param index  the axis index.
     *
     * @return The edge.
     */
    public RectangleEdge getRangeAxisEdge(int index) {
        AxisLocation location = getRangeAxisLocation(index);
        return Plot.resolveRangeAxisLocation(location, this.orientation);
    }

    /**
     * Returns the number of range axes.
     *
     * @return The axis count.
     */
    public int getRangeAxisCount() {
        return this.rangeAxes.size();
    }

    /**
     * Clears the range axes from the plot and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     */
    public void clearRangeAxes() {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.removeChangeListener(this);
            }
        }
        this.rangeAxes.clear();
        fireChangeEvent();
    }

    /**
     * Configures the range axes.
     */
    public void configureRangeAxes() {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.configure();
            }
        }
    }

    /**
     * Returns the primary dataset for the plot.
     *
     * @return The primary dataset (possibly {@code null}).
     *
     * @see #setDataset(CategoryDataset)
     */
    public CategoryDataset<R, C> getDataset() {
        return getDataset(0);
    }

    /**
     * Returns the dataset with the given index, or {@code null} if there is
     * no dataset.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The dataset (possibly {@code null}).
     *
     * @see #setDataset(int, CategoryDataset)
     */
    public CategoryDataset<R, C> getDataset(int index) {
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
    public Map<Integer, CategoryDataset<R, C>> getDatasets() {
        return Collections.unmodifiableMap(this.datasets);
    }

    /**
     * Sets the dataset for the plot, replacing the existing dataset, if there
     * is one.  This method also calls the
     * {@link #datasetChanged(DatasetChangeEvent)} method, which adjusts the
     * axis ranges if necessary and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset()
     */
    public void setDataset(CategoryDataset<R, C> dataset) {
        setDataset(0, dataset);
    }

    /**
     * Sets a dataset for the plot and sends a change notification to all
     * registered listeners.
     *
     * @param index  the dataset index (must be &gt;= 0).
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @see #getDataset(int)
     */
    public void setDataset(int index, CategoryDataset<R, C> dataset) {
        CategoryDataset<R, C> existing = this.datasets.get(index);
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
    public int indexOf(CategoryDataset<R, C> dataset) {
        for (Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
            if (entry.getValue() == dataset) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Maps a dataset to a particular domain axis.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index (zero-based).
     *
     * @see #getDomainAxisForDataset(int)
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
     * This method is used to perform argument checking on the list of
     * axis indices passed to mapDatasetToDomainAxes() and
     * mapDatasetToRangeAxes().
     *
     * @param indices  the list of indices ({@code null} permitted).
     */
    private void checkAxisIndices(List<Integer> indices) {
        // indices can be:
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
        HashSet<Integer> set = new HashSet<>();
        for (Integer item : indices) {
            if (!set.add(item)) {
                throw new IllegalArgumentException("Indices must be unique.");
            }
            ;
        }
    }

    /**
     * Returns the domain axis for a dataset.  You can change the axis for a
     * dataset using the {@link #mapDatasetToDomainAxis(int, int)} method.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The domain axis.
     *
     * @see #mapDatasetToDomainAxis(int, int)
     */
    public CategoryAxis getDomainAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        CategoryAxis axis;
        List<Integer> axisIndices = this.datasetToDomainAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            Integer axisIndex = axisIndices.get(0);
            axis = getDomainAxis(axisIndex);
        } else {
            axis = getDomainAxis(0);
        }
        return axis;
    }

    /**
     * Maps a dataset to a particular range axis.
     *
     * @param index  the dataset index (zero-based).
     * @param axisIndex  the axis index (zero-based).
     *
     * @see #getRangeAxisForDataset(int)
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
     * Returns the range axis for a dataset.  You can change the axis for a
     * dataset using the {@link #mapDatasetToRangeAxis(int, int)} method.
     *
     * @param index  the dataset index (must be &gt;= 0).
     *
     * @return The range axis.
     *
     * @see #mapDatasetToRangeAxis(int, int)
     */
    public ValueAxis getRangeAxisForDataset(int index) {
        Args.requireNonNegative(index, "index");
        ValueAxis axis;
        List<Integer> axisIndices = this.datasetToRangeAxesMap.get(index);
        if (axisIndices != null) {
            // the first axis in the list is used for data <--> Java2D
            axis = getRangeAxis(axisIndices.get(0));
        } else {
            axis = getRangeAxis(0);
        }
        return axis;
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
     * Returns a reference to the renderer for the plot.
     *
     * @return The renderer.
     *
     * @see #setRenderer(CategoryItemRenderer)
     */
    public CategoryItemRenderer getRenderer() {
        return getRenderer(0);
    }

    /**
     * Returns the renderer at the given index.
     *
     * @param index  the renderer index.
     *
     * @return The renderer (possibly {@code null}).
     *
     * @see #setRenderer(int, CategoryItemRenderer)
     */
    public CategoryItemRenderer getRenderer(int index) {
        CategoryItemRenderer renderer = this.renderers.get(index);
        if (renderer == null) {
            return this.renderers.get(0);
        }
        return renderer;
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
    public Map<Integer, CategoryItemRenderer> getRenderers() {
        return Collections.unmodifiableMap(this.renderers);
    }

    /**
     * Sets the renderer at index 0 (sometimes referred to as the "primary"
     * renderer) and sends a change event to all registered listeners.
     *
     * @param renderer  the renderer ({@code null} permitted.
     *
     * @see #getRenderer()
     */
    public void setRenderer(CategoryItemRenderer renderer) {
        setRenderer(0, renderer, true);
    }

    /**
     * Sets the renderer at index 0 (sometimes referred to as the "primary"
     * renderer) and, if requested, sends a change event to all registered
     * listeners.
     * <p>
     * You can set the renderer to {@code null}, but this is not
     * recommended because:
     * <ul>
     *   <li>no data will be displayed;</li>
     *   <li>the plot background will not be painted;</li>
     * </ul>
     *
     * @param renderer  the renderer ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getRenderer()
     */
    public void setRenderer(CategoryItemRenderer renderer, boolean notify) {
        setRenderer(0, renderer, notify);
    }

    /**
     * Sets the renderer to use for the dataset with the specified index and
     * sends a change event to all registered listeners.  Note that each
     * dataset should have its own renderer, you should not use one renderer
     * for multiple datasets.
     *
     * @param index  the index.
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @see #getRenderer(int)
     * @see #setRenderer(int, CategoryItemRenderer, boolean)
     */
    public void setRenderer(int index, CategoryItemRenderer renderer) {
        setRenderer(index, renderer, true);
    }

    /**
     * Sets the renderer to use for the dataset with the specified index and,
     * if requested, sends a change event to all registered listeners.  Note
     * that each dataset should have its own renderer, you should not use one
     * renderer for multiple datasets.
     *
     * @param index  the index.
     * @param renderer  the renderer ({@code null} permitted).
     * @param notify  notify listeners?
     *
     * @see #getRenderer(int)
     */
    public void setRenderer(int index, CategoryItemRenderer renderer, boolean notify) {
        CategoryItemRenderer existing = this.renderers.get(index);
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
     * @param renderers  the renderers.
     */
    public void setRenderers(CategoryItemRenderer[] renderers) {
        for (int i = 0; i < renderers.length; i++) {
            setRenderer(i, renderers[i], false);
        }
        fireChangeEvent();
    }

    /**
     * Returns the renderer for the specified dataset.  If the dataset doesn't
     * belong to the plot, this method will return {@code null}.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The renderer (possibly {@code null}).
     */
    public CategoryItemRenderer getRendererForDataset(CategoryDataset<R, C> dataset) {
        int datasetIndex = indexOf(dataset);
        if (datasetIndex < 0) {
            return null;
        }
        CategoryItemRenderer renderer = this.renderers.get(datasetIndex);
        if (renderer == null) {
            return getRenderer();
        }
        return renderer;
    }

    /**
     * Returns the index of the specified renderer, or {@code -1} if the
     * renderer is not assigned to this plot.
     *
     * @param renderer  the renderer ({@code null} permitted).
     *
     * @return The renderer index.
     */
    public int getIndexOf(CategoryItemRenderer renderer) {
        for (Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() == renderer) {
                return entry.getKey();
            }
        }
        return -1;
    }

    /**
     * Returns the dataset rendering order.
     *
     * @return The order (never {@code null}).
     *
     * @see #setDatasetRenderingOrder(DatasetRenderingOrder)
     */
    public DatasetRenderingOrder getDatasetRenderingOrder() {
        return this.renderingOrder;
    }

    /**
     * Sets the rendering order and sends a {@link PlotChangeEvent} to all
     * registered listeners.  By default, the plot renders the primary dataset
     * last (so that the primary dataset overlays the secondary datasets).  You
     * can reverse this if you want to.
     *
     * @param order  the rendering order ({@code null} not permitted).
     *
     * @see #getDatasetRenderingOrder()
     */
    public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
        Args.nullNotPermitted(order, "order");
        this.renderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the order in which the columns are rendered.  The default value
     * is {@code SortOrder.ASCENDING}.
     *
     * @return The column rendering order (never {@code null}).
     *
     * @see #setColumnRenderingOrder(SortOrder)
     */
    public SortOrder getColumnRenderingOrder() {
        return this.columnRenderingOrder;
    }

    /**
     * Sets the column order in which the items in each dataset should be
     * rendered and sends a {@link PlotChangeEvent} to all registered
     * listeners.  Note that this affects the order in which items are drawn,
     * NOT their position in the chart.
     *
     * @param order  the order ({@code null} not permitted).
     *
     * @see #getColumnRenderingOrder()
     * @see #setRowRenderingOrder(SortOrder)
     */
    public void setColumnRenderingOrder(SortOrder order) {
        Args.nullNotPermitted(order, "order");
        this.columnRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the order in which the rows should be rendered.  The default
     * value is {@code SortOrder.ASCENDING}.
     *
     * @return The order (never {@code null}).
     *
     * @see #setRowRenderingOrder(SortOrder)
     */
    public SortOrder getRowRenderingOrder() {
        return this.rowRenderingOrder;
    }

    /**
     * Sets the row order in which the items in each dataset should be
     * rendered and sends a {@link PlotChangeEvent} to all registered
     * listeners.  Note that this affects the order in which items are drawn,
     * NOT their position in the chart.
     *
     * @param order  the order ({@code null} not permitted).
     *
     * @see #getRowRenderingOrder()
     * @see #setColumnRenderingOrder(SortOrder)
     */
    public void setRowRenderingOrder(SortOrder order) {
        Args.nullNotPermitted(order, "order");
        this.rowRenderingOrder = order;
        fireChangeEvent();
    }

    /**
     * Returns the flag that controls whether the domain grid-lines are visible.
     *
     * @return The {@code true} or {@code false}.
     *
     * @see #setDomainGridlinesVisible(boolean)
     */
    public boolean isDomainGridlinesVisible() {
        return this.domainGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether grid-lines are drawn against
     * the domain axis.
     * <p>
     * If the flag value changes, a {@link PlotChangeEvent} is sent to all
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
     * Returns the position used for the domain gridlines.
     *
     * @return The gridline position (never {@code null}).
     *
     * @see #setDomainGridlinePosition(CategoryAnchor)
     */
    public CategoryAnchor getDomainGridlinePosition() {
        return this.domainGridlinePosition;
    }

    /**
     * Sets the position used for the domain gridlines and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     *
     * @see #getDomainGridlinePosition()
     */
    public void setDomainGridlinePosition(CategoryAnchor position) {
        Args.nullNotPermitted(position, "position");
        this.domainGridlinePosition = position;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw grid-lines against the domain axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainGridlineStroke(Stroke)
     */
    public Stroke getDomainGridlineStroke() {
        return this.domainGridlineStroke;
    }

    /**
     * Sets the stroke used to draw grid-lines against the domain axis and
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
     * Returns the paint used to draw grid-lines against the domain axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainGridlinePaint(Paint)
     */
    public Paint getDomainGridlinePaint() {
        return this.domainGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid-lines (if any) against the domain
     * axis and sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the flag that controls whether the range grid-lines are visible.
     *
     * @return The flag.
     *
     * @see #setRangeGridlinesVisible(boolean)
     */
    public boolean isRangeGridlinesVisible() {
        return this.rangeGridlinesVisible;
    }

    /**
     * Sets the flag that controls whether grid-lines are drawn against
     * the range axis.  If the flag changes value, a {@link PlotChangeEvent} is
     * sent to all registered listeners.
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
     * Returns the stroke used to draw the grid-lines against the range axis.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setRangeGridlineStroke(Stroke)
     */
    public Stroke getRangeGridlineStroke() {
        return this.rangeGridlineStroke;
    }

    /**
     * Sets the stroke used to draw the grid-lines against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners.
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
     * Returns the paint used to draw the grid-lines against the range axis.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeGridlinePaint(Paint)
     */
    public Paint getRangeGridlinePaint() {
        return this.rangeGridlinePaint;
    }

    /**
     * Sets the paint used to draw the grid lines against the range axis and
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
     * Returns the legend items for the plot.  By default, this method creates
     * a legend item for each series in each of the datasets.  You can change
     * this behaviour by overriding this method.
     *
     * @return The legend items.
     */
    @Override
    public LegendItemCollection getLegendItems() {
        if (this.fixedLegendItems != null) {
            return this.fixedLegendItems;
        }
        LegendItemCollection result = new LegendItemCollection();
        // get the legend items for the datasets...
        for (CategoryDataset<R, C> dataset : this.datasets.values()) {
            if (dataset != null) {
                int datasetIndex = indexOf(dataset);
                CategoryItemRenderer renderer = getRenderer(datasetIndex);
                if (renderer != null) {
                    result.addAll(renderer.getLegendItems());
                }
            }
        }
        return result;
    }

    /**
     * Handles a 'click' on the plot by updating the anchor value.
     *
     * @param x  x-coordinate of the click (in Java2D space).
     * @param y  y-coordinate of the click (in Java2D space).
     * @param info  information about the plot's dimensions.
     */
    @Override
    public void handleClick(int x, int y, PlotRenderingInfo info) {
        Rectangle2D dataArea = info.getDataArea();
        if (dataArea.contains(x, y)) {
            // set the anchor value for the range axis...
            double java2D = 0.0;
            if (this.orientation == PlotOrientation.HORIZONTAL) {
                java2D = x;
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                java2D = y;
            }
            RectangleEdge edge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
            double value = getRangeAxis().java2DToValue(java2D, info.getDataArea(), edge);
            setAnchorValue(value);
            setRangeCrosshairValue(value);
        }
    }

    /**
     * Zooms (in or out) on the plot's value axis.
     * <p>
     * If the value 0.0 is passed in as the zoom percent, the auto-range
     * calculation for the axis is restored (which sets the range to include
     * the minimum and maximum data values, thus displaying all the data).
     *
     * @param percent  the zoom amount.
     */
    @Override
    public void zoom(double percent) {
        if (percent > 0.0) {
            double range = getRangeAxis().getRange().getLength();
            double scaledRange = range * percent;
            getRangeAxis().setRange(this.anchorValue - scaledRange / 2.0, this.anchorValue + scaledRange / 2.0);
        } else {
            getRangeAxis().setAutoRange(true);
        }
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
     * The range axis bounds will be recalculated if necessary.
     *
     * @param event  information about the event (not used here).
     */
    @Override
    public void datasetChanged(DatasetChangeEvent event) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.configure();
            }
        }
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
        Plot parent = getParent();
        if (parent != null) {
            if (parent instanceof RendererChangeListener) {
                RendererChangeListener rcl = (RendererChangeListener) parent;
                rcl.rendererChanged(event);
            } else {
                // this should never happen with the existing code, but throw
                // an exception in case future changes make it possible...
                throw new RuntimeException("The renderer has changed and I don't know what to do!");
            }
        } else {
            configureRangeAxes();
            PlotChangeEvent e = new PlotChangeEvent(this);
            notifyListeners(e);
        }
    }

    /**
     * Adds a marker for display (in the foreground) against the domain axis and
     * sends a {@link PlotChangeEvent} to all registered listeners. Typically a
     * marker will be drawn by the renderer as a line perpendicular to the
     * domain axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #removeDomainMarker(CategoryMarker)
     */
    public void addDomainMarker(CategoryMarker marker) {
        addDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for display against the domain axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.  Typically a marker
     * will be drawn by the renderer as a line perpendicular to the domain
     * axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null}
     *               not permitted).
     *
     * @see #removeDomainMarker(CategoryMarker, Layer)
     */
    public void addDomainMarker(CategoryMarker marker, Layer layer) {
        addDomainMarker(0, marker, layer);
    }

    /**
     * Adds a marker for display by a particular renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a domain axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer ({@code null} not permitted).
     *
     * @see #removeDomainMarker(int, CategoryMarker, Layer)
     */
    public void addDomainMarker(int index, CategoryMarker marker, Layer layer) {
        addDomainMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for display by a particular renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a domain axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @see #removeDomainMarker(int, CategoryMarker, Layer, boolean)
     */
    public void addDomainMarker(int index, CategoryMarker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Args.nullNotPermitted(layer, "layer");
        Collection<CategoryMarker> markers;
        if (layer == Layer.FOREGROUND) {
            markers = this.foregroundDomainMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.foregroundDomainMarkers.put(index, markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = this.backgroundDomainMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.backgroundDomainMarkers.put(index, markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears all the domain markers for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #clearRangeMarkers()
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
     * Returns the list of domain markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of domain markers.
     */
    public Collection<CategoryMarker> getDomainMarkers(Layer layer) {
        return getDomainMarkers(0, layer);
    }

    /**
     * Returns a collection of domain markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
     */
    public Collection<CategoryMarker> getDomainMarkers(int index, Layer layer) {
        Collection<CategoryMarker> result = null;
        Integer key = index;
        if (layer == Layer.FOREGROUND) {
            result = this.foregroundDomainMarkers.get(key);
        } else if (layer == Layer.BACKGROUND) {
            result = this.backgroundDomainMarkers.get(key);
        }
        if (result != null) {
            result = Collections.unmodifiableCollection(result);
        }
        return result;
    }

    /**
     * Clears all the domain markers for the specified renderer.
     *
     * @param index  the renderer index.
     *
     * @see #clearRangeMarkers(int)
     */
    public void clearDomainMarkers(int index) {
        Integer key = index;
        if (this.backgroundDomainMarkers != null) {
            Collection<CategoryMarker> markers = this.backgroundDomainMarkers.get(key);
            if (markers != null) {
                for (CategoryMarker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundDomainMarkers != null) {
            Collection<CategoryMarker> markers = this.foregroundDomainMarkers.get(key);
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
     * Removes a marker for the domain axis and sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param marker  the marker.
     *
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(CategoryMarker marker) {
        return removeDomainMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Removes a marker for the domain axis in the specified layer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param marker the marker ({@code null} not permitted).
     * @param layer the layer (foreground or background).
     *
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(CategoryMarker marker, Layer layer) {
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
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(int index, CategoryMarker marker, Layer layer) {
        return removeDomainMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and, if requested,
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index the dataset/renderer index.
     * @param marker the marker.
     * @param layer the layer (foreground or background).
     * @param notify  notify listeners?
     *
     * @return A boolean indicating whether the marker was actually removed.
     */
    public boolean removeDomainMarker(int index, CategoryMarker marker, Layer layer, boolean notify) {
        Collection<CategoryMarker> markers;
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
     * Adds a marker for display (in the foreground) against the range axis and
     * sends a {@link PlotChangeEvent} to all registered listeners. Typically a
     * marker will be drawn by the renderer as a line perpendicular to the
     * range axis, however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     *
     * @see #removeRangeMarker(Marker)
     */
    public void addRangeMarker(Marker marker) {
        addRangeMarker(marker, Layer.FOREGROUND);
    }

    /**
     * Adds a marker for display against the range axis and sends a
     * {@link PlotChangeEvent} to all registered listeners.  Typically a marker
     * will be drawn by the renderer as a line perpendicular to the range axis,
     * however this is entirely up to the renderer.
     *
     * @param marker  the marker ({@code null} not permitted).
     * @param layer  the layer (foreground or background) ({@code null}
     *               not permitted).
     *
     * @see #removeRangeMarker(Marker, Layer)
     */
    public void addRangeMarker(Marker marker, Layer layer) {
        addRangeMarker(0, marker, layer);
    }

    /**
     * Adds a marker for display by a particular renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a range axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker.
     * @param layer  the layer.
     *
     * @see #removeRangeMarker(int, Marker, Layer)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer) {
        addRangeMarker(index, marker, layer, true);
    }

    /**
     * Adds a marker for display by a particular renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     * <P>
     * Typically a marker will be drawn by the renderer as a line perpendicular
     * to a range axis, however this is entirely up to the renderer.
     *
     * @param index  the renderer index.
     * @param marker  the marker.
     * @param layer  the layer.
     * @param notify  notify listeners?
     *
     * @see #removeRangeMarker(int, Marker, Layer, boolean)
     */
    public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Collection<Marker> markers;
        if (layer == Layer.FOREGROUND) {
            markers = this.foregroundRangeMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.foregroundRangeMarkers.put(index, markers);
            }
            markers.add(marker);
        } else if (layer == Layer.BACKGROUND) {
            markers = this.backgroundRangeMarkers.get(index);
            if (markers == null) {
                markers = new ArrayList<>();
                this.backgroundRangeMarkers.put(index, markers);
            }
            markers.add(marker);
        }
        marker.addChangeListener(this);
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Clears all the range markers for the plot and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @see #clearDomainMarkers()
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
     * Returns the list of range markers (read only) for the specified layer.
     *
     * @param layer  the layer (foreground or background).
     *
     * @return The list of range markers.
     *
     * @see #getRangeMarkers(int, Layer)
     */
    public Collection<Marker> getRangeMarkers(Layer layer) {
        return getRangeMarkers(0, layer);
    }

    /**
     * Returns a collection of range markers for a particular renderer and
     * layer.
     *
     * @param index  the renderer index.
     * @param layer  the layer.
     *
     * @return A collection of markers (possibly {@code null}).
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
     * Clears all the range markers for the specified renderer.
     *
     * @param index  the renderer index.
     *
     * @see #clearDomainMarkers(int)
     */
    public void clearRangeMarkers(int index) {
        Integer key = index;
        if (this.backgroundRangeMarkers != null) {
            Collection<Marker> markers = this.backgroundRangeMarkers.get(key);
            if (markers != null) {
                for (Marker m : markers) {
                    m.removeChangeListener(this);
                }
                markers.clear();
            }
        }
        if (this.foregroundRangeMarkers != null) {
            Collection<Marker> markers = this.foregroundRangeMarkers.get(key);
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
     *
     * @see #addRangeMarker(Marker)
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
     *
     * @see #addRangeMarker(Marker, Layer)
     */
    public boolean removeRangeMarker(Marker marker, Layer layer) {
        return removeRangeMarker(0, marker, layer);
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
     *
     * @see #addRangeMarker(int, Marker, Layer)
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
        return removeRangeMarker(index, marker, layer, true);
    }

    /**
     * Removes a marker for a specific dataset/renderer and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the dataset/renderer index.
     * @param marker  the marker.
     * @param layer  the layer (foreground or background).
     * @param notify  notify listeners.
     *
     * @return A boolean indicating whether the marker was actually
     *         removed.
     *
     * @see #addRangeMarker(int, Marker, Layer, boolean)
     */
    public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
        Args.nullNotPermitted(marker, "marker");
        Collection<Marker> markers;
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
     * Returns the flag that controls whether the domain crosshair is
     * displayed by the plot.
     *
     * @return A boolean.
     *
     * @see #setDomainCrosshairVisible(boolean)
     */
    public boolean isDomainCrosshairVisible() {
        return this.domainCrosshairVisible;
    }

    /**
     * Sets the flag that controls whether the domain crosshair is
     * displayed by the plot, and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param flag  the new flag value.
     *
     * @see #isDomainCrosshairVisible()
     * @see #setRangeCrosshairVisible(boolean)
     */
    public void setDomainCrosshairVisible(boolean flag) {
        if (this.domainCrosshairVisible != flag) {
            this.domainCrosshairVisible = flag;
            fireChangeEvent();
        }
    }

    /**
     * Returns the row key for the domain crosshair.
     *
     * @return The row key.
     */
    public R getDomainCrosshairRowKey() {
        return this.domainCrosshairRowKey;
    }

    /**
     * Sets the row key for the domain crosshair and sends a
     * {PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     */
    public void setDomainCrosshairRowKey(R key) {
        setDomainCrosshairRowKey(key, true);
    }

    /**
     * Sets the row key for the domain crosshair and, if requested, sends a
     * {PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     * @param notify  notify listeners?
     */
    public void setDomainCrosshairRowKey(R key, boolean notify) {
        this.domainCrosshairRowKey = key;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the column key for the domain crosshair.
     *
     * @return The column key.
     */
    public C getDomainCrosshairColumnKey() {
        return this.domainCrosshairColumnKey;
    }

    /**
     * Sets the column key for the domain crosshair and sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     */
    public void setDomainCrosshairColumnKey(C key) {
        setDomainCrosshairColumnKey(key, true);
    }

    /**
     * Sets the column key for the domain crosshair and, if requested, sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param key  the key.
     * @param notify  notify listeners?
     */
    public void setDomainCrosshairColumnKey(C key, boolean notify) {
        this.domainCrosshairColumnKey = key;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the dataset index for the crosshair.
     *
     * @return The dataset index.
     */
    public int getCrosshairDatasetIndex() {
        return this.crosshairDatasetIndex;
    }

    /**
     * Sets the dataset index for the crosshair and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     */
    public void setCrosshairDatasetIndex(int index) {
        setCrosshairDatasetIndex(index, true);
    }

    /**
     * Sets the dataset index for the crosshair and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param index  the index.
     * @param notify  notify listeners?
     */
    public void setCrosshairDatasetIndex(int index, boolean notify) {
        this.crosshairDatasetIndex = index;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the paint used to draw the domain crosshair.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDomainCrosshairPaint(Paint)
     * @see #getDomainCrosshairStroke()
     */
    public Paint getDomainCrosshairPaint() {
        return this.domainCrosshairPaint;
    }

    /**
     * Sets the paint used to draw the domain crosshair.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDomainCrosshairPaint()
     */
    public void setDomainCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.domainCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the stroke used to draw the domain crosshair.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDomainCrosshairStroke(Stroke)
     * @see #getDomainCrosshairPaint()
     */
    public Stroke getDomainCrosshairStroke() {
        return this.domainCrosshairStroke;
    }

    /**
     * Sets the stroke used to draw the domain crosshair, and sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDomainCrosshairStroke()
     */
    public void setDomainCrosshairStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.domainCrosshairStroke = stroke;
    }

    /**
     * Returns a flag indicating whether the range crosshair is visible.
     *
     * @return The flag.
     *
     * @see #setRangeCrosshairVisible(boolean)
     */
    public boolean isRangeCrosshairVisible() {
        return this.rangeCrosshairVisible;
    }

    /**
     * Sets the flag indicating whether the range crosshair is visible.
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
     * "lock-on" to actual data values, and sends a {@link PlotChangeEvent}
     * to all registered listeners.
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
     * Sets the range crosshair value and, if the crosshair is visible, sends
     * a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param value  the new value.
     *
     * @see #getRangeCrosshairValue()
     */
    public void setRangeCrosshairValue(double value) {
        setRangeCrosshairValue(value, true);
    }

    /**
     * Sets the range crosshair value and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners (but only if the
     * crosshair is visible).
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
     * Returns the pen-style ({@code Stroke}) used to draw the crosshair
     * (if visible).
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
     * Sets the pen-style ({@code Stroke}) used to draw the range
     * crosshair (if visible), and sends a {@link PlotChangeEvent} to all
     * registered listeners.
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
     * Returns the paint used to draw the range crosshair.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setRangeCrosshairPaint(Paint)
     * @see #isRangeCrosshairVisible()
     * @see #getRangeCrosshairStroke()
     */
    public Paint getRangeCrosshairPaint() {
        return this.rangeCrosshairPaint;
    }

    /**
     * Sets the paint used to draw the range crosshair (if visible) and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getRangeCrosshairPaint()
     */
    public void setRangeCrosshairPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.rangeCrosshairPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the list of annotations.
     *
     * @return The list of annotations (never {@code null}).
     *
     * @see #addAnnotation(CategoryAnnotation)
     * @see #clearAnnotations()
     */
    public List<CategoryAnnotation> getAnnotations() {
        return this.annotations;
    }

    /**
     * Adds an annotation to the plot and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     *
     * @see #removeAnnotation(CategoryAnnotation)
     */
    public void addAnnotation(CategoryAnnotation annotation) {
        addAnnotation(annotation, true);
    }

    /**
     * Adds an annotation to the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     */
    public void addAnnotation(CategoryAnnotation annotation, boolean notify) {
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
     * @see #addAnnotation(CategoryAnnotation)
     */
    public boolean removeAnnotation(CategoryAnnotation annotation) {
        return removeAnnotation(annotation, true);
    }

    /**
     * Removes an annotation from the plot and, if requested, sends a
     * {@link PlotChangeEvent} to all registered listeners.
     *
     * @param annotation  the annotation ({@code null} not permitted).
     * @param notify  notify listeners?
     *
     * @return A boolean (indicates whether the annotation was removed).
     */
    public boolean removeAnnotation(CategoryAnnotation annotation, boolean notify) {
        Args.nullNotPermitted(annotation, "annotation");
        boolean removed = this.annotations.remove(annotation);
        annotation.removeChangeListener(this);
        if (removed && notify) {
            fireChangeEvent();
        }
        return removed;
    }

    /**
     * Clears all the annotations and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     */
    public void clearAnnotations() {
        for (CategoryAnnotation annotation : this.annotations) {
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
            if (this.orientation.isHorizontal()) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
            } else if (this.orientation.isVertical()) {
                space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            }
        } else {
            // reserve space for the primary domain axis...
            RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
            if (this.drawSharedDomainAxis) {
                space = getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space);
            }
            // reserve space for any domain axes...
            for (CategoryAxis xAxis : this.domainAxes.values()) {
                if (xAxis != null) {
                    int i = getDomainAxisIndex(xAxis);
                    RectangleEdge edge = getDomainAxisEdge(i);
                    space = xAxis.reserveSpace(g2, this, plotArea, edge, space);
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
            if (this.orientation.isHorizontal()) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
            } else if (this.orientation == PlotOrientation.VERTICAL) {
                space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
                space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
            }
        } else {
            // reserve space for the range axes (if any)...
            for (ValueAxis yAxis : this.rangeAxes.values()) {
                if (yAxis != null) {
                    int i = findRangeAxisIndex(yAxis);
                    RectangleEdge edge = getRangeAxisEdge(i);
                    space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
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
     * Calculates the space required for the axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     *
     * @return The space required for the axes.
     */
    protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
        AxisSpace space = new AxisSpace();
        space = calculateRangeAxisSpace(g2, plotArea, space);
        space = calculateDomainAxisSpace(g2, plotArea, space);
        return space;
    }

    /**
     * Receives a chart element visitor.  Many plot subclasses will override
     * this method to handle their subcomponents.
     *
     * @param visitor  the visitor ({@code null} not permitted).
     */
    @Override
    public void receive(ChartElementVisitor visitor) {
        // visit the domain axes
        for (Entry<Integer, CategoryAxis> entry : this.domainAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // visit the range axes
        for (Entry<Integer, ValueAxis> entry : this.rangeAxes.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // visit the renderers
        for (Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
            if (entry.getValue() != null) {
                entry.getValue().receive(visitor);
            }
        }
        // and finally this plot
        visitor.visit(this);
    }

    /**
     * Draws the plot on a Java 2D graphics device (such as the screen or a
     * printer).
     * <P>
     * At your option, you may supply an instance of {@link PlotRenderingInfo}.
     * If you do, it will be populated with information about the drawing,
     * including various plot dimensions and tooltip info.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot (including axes) should
     *              be drawn.
     * @param anchor  the anchor point ({@code null} permitted).
     * @param parentState  the state from the parent plot, if there is one.
     * @param state  collects info as the chart is drawn (possibly
     *               {@code null}).
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo state) {
        // if the plot area is too small, just return...
        boolean b1 = (area.getWidth() <= MINIMUM_WIDTH_TO_DRAW);
        boolean b2 = (area.getHeight() <= MINIMUM_HEIGHT_TO_DRAW);
        if (b1 || b2) {
            return;
        }
        // record the plot area...
        if (state == null) {
            // if the incoming state is null, no information will be passed
            // back to the caller - but we create a temporary state to record
            // the plot area, since that is used later by the axes
            state = new PlotRenderingInfo(null);
        }
        state.setPlotArea(area);
        // adjust the drawing area for the plot insets (if any)...
        RectangleInsets insets = getInsets();
        insets.trim(area);
        // calculate the data area...
        AxisSpace space = calculateAxisSpace(g2, area);
        Rectangle2D dataArea = space.shrink(area, null);
        this.axisOffset.trim(dataArea);
        dataArea = integerise(dataArea);
        if (dataArea.isEmpty()) {
            return;
        }
        state.setDataArea(dataArea);
        createAndAddEntity((Rectangle2D) dataArea.clone(), state, null, null);
        // if there is a renderer, it draws the background, otherwise use the
        // default background...
        if (getRenderer() != null) {
            getRenderer().drawBackground(g2, this, dataArea);
        } else {
            drawBackground(g2, dataArea);
        }
        Map<Axis, AxisState> axisStateMap = drawAxes(g2, area, dataArea, state);
        // the anchor point is typically the point where the mouse last
        // clicked - the crosshairs will be driven off this point...
        if (anchor != null && !dataArea.contains(anchor)) {
            anchor = ShapeUtils.getPointInRectangle(anchor.getX(), anchor.getY(), dataArea);
        }
        CategoryCrosshairState<R, C> crosshairState = new CategoryCrosshairState<>();
        crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
        crosshairState.setAnchor(anchor);
        // specify the anchor X and Y coordinates in Java2D space, for the
        // cases where these are not updated during rendering (i.e. no lock
        // on data)
        crosshairState.setAnchorX(Double.NaN);
        crosshairState.setAnchorY(Double.NaN);
        if (anchor != null) {
            ValueAxis rangeAxis = getRangeAxis();
            if (rangeAxis != null) {
                double y;
                if (getOrientation() == PlotOrientation.VERTICAL) {
                    y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
                } else {
                    y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
                }
                crosshairState.setAnchorY(y);
            }
        }
        crosshairState.setRowKey(getDomainCrosshairRowKey());
        crosshairState.setColumnKey(getDomainCrosshairColumnKey());
        crosshairState.setCrosshairY(getRangeCrosshairValue());
        // don't let anyone draw outside the data area
        Shape savedClip = g2.getClip();
        g2.clip(dataArea);
        drawDomainGridlines(g2, dataArea);
        AxisState rangeAxisState = axisStateMap.get(getRangeAxis());
        if (rangeAxisState == null) {
            if (parentState != null) {
                rangeAxisState = parentState.getSharedAxisStates().get(getRangeAxis());
            }
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
        // draw the markers...
        for (CategoryItemRenderer renderer : this.renderers.values()) {
            int i = getIndexOf(renderer);
            drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        for (CategoryItemRenderer renderer : this.renderers.values()) {
            int i = getIndexOf(renderer);
            drawRangeMarkers(g2, dataArea, i, Layer.BACKGROUND);
        }
        // now render data items...
        boolean foundData = false;
        // set up the alpha-transparency...
        Composite originalComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, getForegroundAlpha()));
        DatasetRenderingOrder order = getDatasetRenderingOrder();
        List<Integer> datasetIndices = getDatasetIndices(order);
        for (int i : datasetIndices) {
            foundData = render(g2, dataArea, i, state, crosshairState) || foundData;
        }
        // draw the foreground markers...
        List<Integer> rendererIndices = getRendererIndices(order);
        for (int i : rendererIndices) {
            drawDomainMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        for (int i : rendererIndices) {
            drawRangeMarkers(g2, dataArea, i, Layer.FOREGROUND);
        }
        // draw the annotations (if any)...
        drawAnnotations(g2, dataArea);
        if (this.shadowGenerator != null && !suppressShadow) {
            BufferedImage shadowImage = this.shadowGenerator.createDropShadow(dataImage);
            g2 = savedG2;
            g2.drawImage(shadowImage, (int) dataArea.getX() + this.shadowGenerator.calculateOffsetX(), (int) dataArea.getY() + this.shadowGenerator.calculateOffsetY(), null);
            g2.drawImage(dataImage, (int) dataArea.getX(), (int) dataArea.getY(), null);
        }
        g2.setClip(savedClip);
        g2.setComposite(originalComposite);
        if (!foundData) {
            drawNoDataMessage(g2, dataArea);
        }
        int datasetIndex = crosshairState.getDatasetIndex();
        setCrosshairDatasetIndex(datasetIndex, false);
        // draw domain crosshair if required...
        R rowKey = crosshairState.getRowKey();
        C columnKey = crosshairState.getColumnKey();
        setDomainCrosshairRowKey(rowKey, false);
        setDomainCrosshairColumnKey(columnKey, false);
        if (isDomainCrosshairVisible() && columnKey != null) {
            Paint paint = getDomainCrosshairPaint();
            Stroke stroke = getDomainCrosshairStroke();
            drawDomainCrosshair(g2, dataArea, this.orientation, datasetIndex, rowKey, columnKey, stroke, paint);
        }
        // draw range crosshair if required...
        ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
        RectangleEdge yAxisEdge = getRangeAxisEdge();
        if (!this.rangeCrosshairLockedOnData && anchor != null) {
            double yy;
            if (getOrientation() == PlotOrientation.VERTICAL) {
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
            drawRangeCrosshair(g2, dataArea, getOrientation(), y, yAxis, stroke, paint);
        }
        // draw an outline around the plot area...
        if (isOutlineVisible()) {
            if (getRenderer() != null) {
                getRenderer().drawOutline(g2, this, dataArea);
            } else {
                drawOutline(g2, dataArea);
            }
        }
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
        for (Map.Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
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
     * Returns the indices of the non-null renderers for the plot, in the
     * specified order.
     *
     * @param order  the rendering order {@code null} not permitted).
     *
     * @return A list of indices.
     */
    private List<Integer> getRendererIndices(DatasetRenderingOrder order) {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, CategoryItemRenderer> entry : this.renderers.entrySet()) {
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
     * Draws the plot background (the background color and/or image).
     * <P>
     * This method will be called during the chart drawing process and is
     * declared public so that it can be accessed by the renderers used by
     * certain subclasses.  You shouldn't need to call this method directly.
     *
     * @param g2  the graphics device.
     * @param area  the area within which the plot should be drawn.
     */
    @Override
    public void drawBackground(Graphics2D g2, Rectangle2D area) {
        fillBackground(g2, area, this.orientation);
        drawBackgroundImage(g2, area);
    }

    /**
     * A utility method for drawing the plot's axes.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param dataArea  the data area.
     * @param plotState  collects information about the plot ({@code null}
     *                   permitted).
     *
     * @return A map containing the axis states.
     */
    protected Map<Axis, AxisState> drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
        AxisCollection axisCollection = new AxisCollection();
        // add domain axes to lists...
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                int index = getDomainAxisIndex(xAxis);
                axisCollection.add(xAxis, getDomainAxisEdge(index));
            }
        }
        // add range axes to lists...
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                int index = findRangeAxisIndex(yAxis);
                axisCollection.add(yAxis, getRangeAxisEdge(index));
            }
        }
        Map<Axis, AxisState> axisStateMap = new HashMap<>();
        // draw the top axes
        double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtTop()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        // draw the bottom axes
        cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
        for (Axis axis : axisCollection.getAxesAtBottom()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        // draw the left axes
        cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtLeft()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        // draw the right axes
        cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
        for (Axis axis : axisCollection.getAxesAtRight()) {
            if (axis != null) {
                AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
                cursor = axisState.getCursor();
                axisStateMap.put(axis, axisState);
            }
        }
        return axisStateMap;
    }

    /**
     * Draws a representation of a dataset within the dataArea region using the
     * appropriate renderer.
     *
     * @param g2  the graphics device.
     * @param dataArea  the region in which the data is to be drawn.
     * @param index  the dataset and renderer index.
     * @param info  an optional object for collection dimension information.
     * @param crosshairState  a state object for tracking crosshair info
     *        ({@code null} permitted).
     *
     * @return A boolean that indicates whether real data was found.
     */
    public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CategoryCrosshairState<R, C> crosshairState) {
        boolean foundData = false;
        CategoryDataset<R, C> currentDataset = getDataset(index);
        CategoryItemRenderer renderer = getRenderer(index);
        CategoryAxis domainAxis = getDomainAxisForDataset(index);
        ValueAxis rangeAxis = getRangeAxisForDataset(index);
        boolean hasData = !DatasetUtils.isEmptyOrNull(currentDataset);
        if (hasData && renderer != null) {
            foundData = true;
            CategoryItemRendererState state = renderer.initialise(g2, dataArea, this, index, info);
            state.setCrosshairState(crosshairState);
            int columnCount = currentDataset.getColumnCount();
            int rowCount = currentDataset.getRowCount();
            int passCount = renderer.getPassCount();
            for (int pass = 0; pass < passCount; pass++) {
                if (this.columnRenderingOrder == SortOrder.ASCENDING) {
                    for (int column = 0; column < columnCount; column++) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        } else {
                            for (int row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                } else {
                    for (int column = columnCount - 1; column >= 0; column--) {
                        if (this.rowRenderingOrder == SortOrder.ASCENDING) {
                            for (int row = 0; row < rowCount; row++) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        } else {
                            for (int row = rowCount - 1; row >= 0; row--) {
                                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass);
                            }
                        }
                    }
                }
            }
        }
        return foundData;
    }

    /**
     * Draws the domain gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     *
     * @see #drawRangeGridlines(Graphics2D, Rectangle2D, List)
     */
    protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea) {
        if (!isDomainGridlinesVisible()) {
            return;
        }
        CategoryAnchor anchor = getDomainGridlinePosition();
        RectangleEdge domainAxisEdge = getDomainAxisEdge();
        CategoryDataset<R, C> dataset = getDataset();
        if (dataset == null) {
            return;
        }
        CategoryAxis axis = getDomainAxis();
        if (axis != null) {
            int columnCount = dataset.getColumnCount();
            for (int c = 0; c < columnCount; c++) {
                double xx = axis.getCategoryJava2DCoordinate(anchor, c, columnCount, dataArea, domainAxisEdge);
                CategoryItemRenderer renderer1 = getRenderer();
                if (renderer1 != null) {
                    renderer1.drawDomainGridline(g2, this, dataArea, xx);
                }
            }
        }
    }

    /**
     * Draws the range gridlines for the plot, if they are visible.
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param dataArea  the area inside the axes ({@code null} not permitted).
     * @param ticks  the ticks.
     *
     * @see #drawDomainGridlines(Graphics2D, Rectangle2D)
     */
    protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List<ValueTick> ticks) {
        // draw the range grid lines, if any...
        if (!isRangeGridlinesVisible() && !isRangeMinorGridlinesVisible()) {
            return;
        }
        // no axis, no gridlines...
        ValueAxis axis = getRangeAxis();
        if (axis == null) {
            return;
        }
        // no renderer, no gridlines...
        CategoryItemRenderer r = getRenderer();
        if (r == null) {
            return;
        }
        Stroke gridStroke = null;
        Paint gridPaint = null;
        boolean paintLine;
        for (ValueTick tick : ticks) {
            paintLine = false;
            if ((tick.getTickType() == TickType.MINOR) && isRangeMinorGridlinesVisible()) {
                gridStroke = getRangeMinorGridlineStroke();
                gridPaint = getRangeMinorGridlinePaint();
                paintLine = true;
            } else if ((tick.getTickType() == TickType.MAJOR) && isRangeGridlinesVisible()) {
                gridStroke = getRangeGridlineStroke();
                gridPaint = getRangeGridlinePaint();
                paintLine = true;
            }
            if (((tick.getValue() != 0.0) || !isRangeZeroBaselineVisible()) && paintLine) {
                r.drawRangeLine(g2, this, axis, dataArea, tick.getValue(), gridPaint, gridStroke);
            }
        }
    }

    /**
     * Draws a base line across the chart at value zero on the range axis.
     *
     * @param g2  the graphics device.
     * @param area  the data area.
     *
     * @see #setRangeZeroBaselineVisible(boolean)
     */
    protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
        if (!isRangeZeroBaselineVisible()) {
            return;
        }
        CategoryItemRenderer r = getRenderer();
        r.drawRangeLine(g2, this, getRangeAxis(), area, 0.0, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke);
    }

    /**
     * Draws the annotations.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     */
    protected void drawAnnotations(Graphics2D g2, Rectangle2D dataArea) {
        if (getAnnotations() != null) {
            for (CategoryAnnotation annotation : getAnnotations()) {
                annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
            }
        }
    }

    /**
     * Draws the domain markers (if any) for an axis and layer.  This method is
     * typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the renderer index.
     * @param layer  the layer (foreground or background).
     *
     * @see #drawRangeMarkers(Graphics2D, Rectangle2D, int, Layer)
     */
    protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        CategoryItemRenderer r = getRenderer(index);
        if (r == null) {
            return;
        }
        Collection<CategoryMarker> markers = getDomainMarkers(index, layer);
        CategoryAxis axis = getDomainAxisForDataset(index);
        if (markers != null && axis != null) {
            for (CategoryMarker marker : markers) {
                r.drawDomainMarker(g2, this, axis, marker, dataArea);
            }
        }
    }

    /**
     * Draws the range markers (if any) for an axis and layer.  This method is
     * typically called from within the draw() method.
     *
     * @param g2  the graphics device.
     * @param dataArea  the data area.
     * @param index  the renderer index.
     * @param layer  the layer (foreground or background).
     *
     * @see #drawDomainMarkers(Graphics2D, Rectangle2D, int, Layer)
     */
    protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
        CategoryItemRenderer r = getRenderer(index);
        if (r == null) {
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
     * Utility method for drawing a line perpendicular to the range axis (used
     * for crosshairs).
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param value  the data value.
     * @param stroke  the line stroke ({@code null} not permitted).
     * @param paint  the line paint ({@code null} not permitted).
     */
    protected void drawRangeLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
        double java2D = getRangeAxis().valueToJava2D(value, dataArea, getRangeAxisEdge());
        Line2D line = null;
        if (this.orientation == PlotOrientation.HORIZONTAL) {
            line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
        } else if (this.orientation == PlotOrientation.VERTICAL) {
            line = new Line2D.Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
    }

    /**
     * Draws a domain crosshair.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param orientation  the plot orientation.
     * @param datasetIndex  the dataset index.
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param stroke  the stroke used to draw the crosshair line.
     * @param paint  the paint used to draw the crosshair line.
     *
     * @see #drawRangeCrosshair(Graphics2D, Rectangle2D, PlotOrientation,
     *     double, ValueAxis, Stroke, Paint)
     */
    protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, int datasetIndex, R rowKey, C columnKey, Stroke stroke, Paint paint) {
        CategoryDataset<R, C> dataset = getDataset(datasetIndex);
        CategoryAxis axis = getDomainAxisForDataset(datasetIndex);
        CategoryItemRenderer renderer = getRenderer(datasetIndex);
        Line2D line;
        if (orientation == PlotOrientation.VERTICAL) {
            double xx = renderer.getItemMiddle(rowKey, columnKey, dataset, axis, dataArea, RectangleEdge.BOTTOM);
            line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
        } else {
            double yy = renderer.getItemMiddle(rowKey, columnKey, dataset, axis, dataArea, RectangleEdge.LEFT);
            line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
        }
        g2.setStroke(stroke);
        g2.setPaint(paint);
        g2.draw(line);
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
     *
     * @see #drawDomainCrosshair(Graphics2D, Rectangle2D, PlotOrientation, int,
     *      Comparable, Comparable, Stroke, Paint)
     */
    protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
        if (!axis.getRange().contains(value)) {
            return;
        }
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
    }

    /**
     * Returns the range of data values that will be plotted against the range
     * axis.  If the dataset is {@code null}, this method returns
     * {@code null}.
     *
     * @param axis  the axis.
     *
     * @return The data range.
     */
    @Override
    public Range getDataRange(ValueAxis axis) {
        Range result = null;
        List<CategoryDataset<R, C>> mappedDatasets = new ArrayList<>();
        int rangeIndex = findRangeAxisIndex(axis);
        if (rangeIndex >= 0) {
            mappedDatasets.addAll(datasetsMappedToRangeAxis(rangeIndex));
        } else if (axis == getRangeAxis()) {
            mappedDatasets.addAll(datasetsMappedToRangeAxis(0));
        }
        // iterate through the datasets that map to the axis and get the union
        // of the ranges.
        for (CategoryDataset<R, C> d : mappedDatasets) {
            CategoryItemRenderer r = getRendererForDataset(d);
            if (r != null) {
                result = Range.combine(result, r.findRangeBounds(d));
            }
        }
        return result;
    }

    /**
     * Returns a list of the datasets that are mapped to the axis with the
     * specified index.
     *
     * @param axisIndex  the axis index.
     *
     * @return The list (possibly empty, but never {@code null}).
     */
    private List<CategoryDataset<R, C>> datasetsMappedToDomainAxis(int axisIndex) {
        List<CategoryDataset<R, C>> result = new ArrayList<>();
        for (Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
            CategoryDataset<R, C> dataset = entry.getValue();
            if (dataset == null) {
                continue;
            }
            Integer datasetIndex = entry.getKey();
            List<Integer> mappedAxes = this.datasetToDomainAxesMap.get(datasetIndex);
            if (mappedAxes == null) {
                if (axisIndex == 0) {
                    result.add(dataset);
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(dataset);
                }
            }
        }
        return result;
    }

    /**
     * A utility method that returns a list of datasets that are mapped to a
     * given range axis.
     *
     * @param axisIndex  the axis index.
     *
     * @return The list (possibly empty, but never {@code null}).
     */
    private List<CategoryDataset<R, C>> datasetsMappedToRangeAxis(int axisIndex) {
        List<CategoryDataset<R, C>> result = new ArrayList<>();
        for (Entry<Integer, CategoryDataset<R, C>> entry : this.datasets.entrySet()) {
            Integer datasetIndex = entry.getKey();
            CategoryDataset<R, C> dataset = entry.getValue();
            List<Integer> mappedAxes = this.datasetToRangeAxesMap.get(datasetIndex);
            if (mappedAxes == null) {
                if (axisIndex == 0) {
                    result.add(dataset);
                }
            } else {
                if (mappedAxes.contains(axisIndex)) {
                    result.add(dataset);
                }
            }
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
     * Sets the fixed domain axis space and sends a {@link PlotChangeEvent} to
     * all registered listeners.
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
     * Sets the fixed range axis space and sends a {@link PlotChangeEvent} to
     * all registered listeners.
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
     * Returns a list of the categories in the plot's primary dataset.
     *
     * @return A list of the categories in the plot's primary dataset.
     *
     * @see #getCategoriesForAxis(CategoryAxis)
     */
    public List<C> getCategories() {
        List<C> result = null;
        if (getDataset() != null) {
            result = Collections.unmodifiableList(getDataset().getColumnKeys());
        }
        return result;
    }

    /**
     * Returns a list of the categories that should be displayed for the
     * specified axis.
     *
     * @param axis  the axis ({@code null} not permitted)
     *
     * @return The categories.
     */
    public List<C> getCategoriesForAxis(CategoryAxis axis) {
        List<C> result = new ArrayList<>();
        int axisIndex = getDomainAxisIndex(axis);
        for (CategoryDataset<R, C> dataset : datasetsMappedToDomainAxis(axisIndex)) {
            // add the unique categories from this dataset
            for (int i = 0; i < dataset.getColumnCount(); i++) {
                C category = dataset.getColumnKey(i);
                if (!result.contains(category)) {
                    result.add(category);
                }
            }
        }
        return result;
    }

    /**
     * Returns the flag that controls whether the shared domain axis is
     * drawn for each subplot.
     *
     * @return A boolean.
     *
     * @see #setDrawSharedDomainAxis(boolean)
     */
    public boolean getDrawSharedDomainAxis() {
        return this.drawSharedDomainAxis;
    }

    /**
     * Sets the flag that controls whether the shared domain axis is drawn when
     * this plot is being used as a subplot.
     *
     * @param draw  a boolean.
     *
     * @see #getDrawSharedDomainAxis()
     */
    public void setDrawSharedDomainAxis(boolean draw) {
        this.drawSharedDomainAxis = draw;
        fireChangeEvent();
    }

    /**
     * Returns {@code false} always, because the plot cannot be panned
     * along the domain axis/axes.
     *
     * @return A boolean.
     *
     * @see #isRangePannable()
     */
    @Override
    public boolean isDomainPannable() {
        return false;
    }

    /**
     * Returns {@code true} if panning is enabled for the range axes,
     * and {@code false} otherwise.
     *
     * @return A boolean.
     *
     * @see #setRangePannable(boolean)
     * @see #isDomainPannable()
     */
    @Override
    public boolean isRangePannable() {
        return this.rangePannable;
    }

    /**
     * Sets the flag that enables or disables panning of the plot along
     * the range axes.
     *
     * @param pannable  the new flag value.
     *
     * @see #isRangePannable()
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
        // do nothing, because the plot is not pannable along the domain axes
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
        for (ValueAxis axis : this.rangeAxes.values()) {
            if (axis == null) {
                continue;
            }
            double length = axis.getRange().getLength();
            double adj = percent * length;
            if (axis.isInverted()) {
                adj = -adj;
            }
            axis.setRange(axis.getLowerBound() + adj, axis.getUpperBound() + adj);
        }
    }

    /**
     * Returns {@code false} to indicate that the domain axes are not
     * zoomable.
     *
     * @return A boolean.
     *
     * @see #isRangeZoomable()
     */
    @Override
    public boolean isDomainZoomable() {
        return false;
    }

    /**
     * Returns {@code true} to indicate that the range axes are zoomable.
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
     * This method does nothing, because {@code CategoryPlot} doesn't
     * support zooming on the domain.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {
        // can't zoom domain axis
    }

    /**
     * This method does nothing, because {@code CategoryPlot} doesn't
     * support zooming on the domain.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        // can't zoom domain axis
    }

    /**
     * This method does nothing, because {@code CategoryPlot} doesn't
     * support zooming on the domain.
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
        // can't zoom domain axis
    }

    /**
     * Multiplies the range on the range axis/axes by the specified factor.
     *
     * @param factor  the zoom factor.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
        // delegate to other method
        zoomRangeAxes(factor, state, source, false);
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
        for (ValueAxis rangeAxis : this.rangeAxes.values()) {
            if (rangeAxis == null) {
                continue;
            }
            if (useAnchor) {
                // get the relevant source coordinate given the plot orientation
                double sourceY = source.getY();
                if (this.orientation.isHorizontal()) {
                    sourceY = source.getX();
                }
                double anchorY = rangeAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge());
                rangeAxis.resizeRange2(factor, anchorY);
            } else {
                rangeAxis.resizeRange(factor);
            }
        }
    }

    /**
     * Zooms in on the range axes.
     *
     * @param lowerPercent  the lower bound.
     * @param upperPercent  the upper bound.
     * @param state  the plot state.
     * @param source  the source point (in Java2D space) for the zoom.
     */
    @Override
    public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.zoomRange(lowerPercent, upperPercent);
            }
        }
    }

    /**
     * Returns the anchor value.
     *
     * @return The anchor value.
     *
     * @see #setAnchorValue(double)
     */
    public double getAnchorValue() {
        return this.anchorValue;
    }

    /**
     * Sets the anchor value and sends a {@link PlotChangeEvent} to all
     * registered listeners.
     *
     * @param value  the anchor value.
     *
     * @see #getAnchorValue()
     */
    public void setAnchorValue(double value) {
        setAnchorValue(value, true);
    }

    /**
     * Sets the anchor value and, if requested, sends a {@link PlotChangeEvent}
     * to all registered listeners.
     *
     * @param value  the value.
     * @param notify  notify listeners?
     *
     * @see #getAnchorValue()
     */
    public void setAnchorValue(double value, boolean notify) {
        this.anchorValue = value;
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Tests the plot for equality with an arbitrary object.
     *
     * @param obj  the object to test against ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CategoryPlot)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        CategoryPlot<R, C> that = (CategoryPlot) obj;
        if (this.orientation != that.orientation) {
            return false;
        }
        if (!Objects.equals(this.axisOffset, that.axisOffset)) {
            return false;
        }
        if (!Objects.equals(this.domainAxes, that.domainAxes)) {
            return false;
        }
        if (!Objects.equals(this.domainAxisLocations, that.domainAxisLocations)) {
            return false;
        }
        if (this.drawSharedDomainAxis != that.drawSharedDomainAxis) {
            return false;
        }
        if (!Objects.equals(this.rangeAxes, that.rangeAxes)) {
            return false;
        }
        if (!Objects.equals(this.rangeAxisLocations, that.rangeAxisLocations)) {
            return false;
        }
        if (!Objects.equals(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap)) {
            return false;
        }
        if (!Objects.equals(this.renderers, that.renderers)) {
            return false;
        }
        if (!Objects.equals(this.renderingOrder, that.renderingOrder)) {
            return false;
        }
        if (this.columnRenderingOrder != that.columnRenderingOrder) {
            return false;
        }
        if (!Objects.equals(this.rowRenderingOrder, that.rowRenderingOrder)) {
            return false;
        }
        if (this.domainGridlinesVisible != that.domainGridlinesVisible) {
            return false;
        }
        if (this.rangePannable != that.rangePannable) {
            return false;
        }
        if (this.domainGridlinePosition != that.domainGridlinePosition) {
            return false;
        }
        if (!Objects.equals(this.domainGridlineStroke, that.domainGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainGridlinePaint, that.domainGridlinePaint)) {
            return false;
        }
        if (this.rangeGridlinesVisible != that.rangeGridlinesVisible) {
            return false;
        }
        if (!Objects.equals(this.rangeGridlineStroke, that.rangeGridlineStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeGridlinePaint, that.rangeGridlinePaint)) {
            return false;
        }
        if (this.anchorValue != that.anchorValue) {
            return false;
        }
        if (this.rangeCrosshairVisible != that.rangeCrosshairVisible) {
            return false;
        }
        if (this.rangeCrosshairValue != that.rangeCrosshairValue) {
            return false;
        }
        if (!Objects.equals(this.rangeCrosshairStroke, that.rangeCrosshairStroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint)) {
            return false;
        }
        if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData) {
            return false;
        }
        if (!Objects.equals(this.foregroundDomainMarkers, that.foregroundDomainMarkers)) {
            return false;
        }
        if (!Objects.equals(this.datasets, that.datasets)) {
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
        if (this.weight != that.weight) {
            return false;
        }
        if (!Objects.equals(this.fixedDomainAxisSpace, that.fixedDomainAxisSpace)) {
            return false;
        }
        if (!Objects.equals(this.fixedRangeAxisSpace, that.fixedRangeAxisSpace)) {
            return false;
        }
        if (!Objects.equals(this.fixedLegendItems, that.fixedLegendItems)) {
            return false;
        }
        if (this.domainCrosshairVisible != that.domainCrosshairVisible) {
            return false;
        }
        if (this.crosshairDatasetIndex != that.crosshairDatasetIndex) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairColumnKey, that.domainCrosshairColumnKey)) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairRowKey, that.domainCrosshairRowKey)) {
            return false;
        }
        if (!PaintUtils.equal(this.domainCrosshairPaint, that.domainCrosshairPaint)) {
            return false;
        }
        if (!Objects.equals(this.domainCrosshairStroke, that.domainCrosshairStroke)) {
            return false;
        }
        if (this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke)) {
            return false;
        }
        if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible) {
            return false;
        }
        if (!PaintUtils.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint)) {
            return false;
        }
        if (!Objects.equals(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke)) {
            return false;
        }
        if (!Objects.equals(this.shadowGenerator, that.shadowGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 71 * hash + Objects.hashCode(this.orientation);
        hash = 71 * hash + Objects.hashCode(this.axisOffset);
        hash = 71 * hash + Objects.hashCode(this.domainAxes);
        hash = 71 * hash + Objects.hashCode(this.domainAxisLocations);
        hash = 71 * hash + (this.drawSharedDomainAxis ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeAxes);
        hash = 71 * hash + Objects.hashCode(this.rangeAxisLocations);
        hash = 71 * hash + Objects.hashCode(this.datasets);
        hash = 71 * hash + Objects.hashCode(this.datasetToDomainAxesMap);
        hash = 71 * hash + Objects.hashCode(this.datasetToRangeAxesMap);
        hash = 71 * hash + Objects.hashCode(this.renderers);
        hash = 71 * hash + Objects.hashCode(this.renderingOrder);
        hash = 71 * hash + Objects.hashCode(this.columnRenderingOrder);
        hash = 71 * hash + Objects.hashCode(this.rowRenderingOrder);
        hash = 71 * hash + (this.domainGridlinesVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.domainGridlinePosition);
        hash = 71 * hash + Objects.hashCode(this.domainGridlineStroke);
        hash = 71 * hash + Objects.hashCode(this.domainGridlinePaint);
        hash = 71 * hash + (this.rangeZeroBaselineVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeZeroBaselineStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeZeroBaselinePaint);
        hash = 71 * hash + (this.rangeGridlinesVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeGridlineStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeGridlinePaint);
        hash = 71 * hash + (this.rangeMinorGridlinesVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.rangeMinorGridlineStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeMinorGridlinePaint);
        hash = 71 * hash + Long.hashCode(Double.doubleToLongBits(this.anchorValue));
        hash = 71 * hash + this.crosshairDatasetIndex;
        hash = 71 * hash + (this.domainCrosshairVisible ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairRowKey);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairColumnKey);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairStroke);
        hash = 71 * hash + Objects.hashCode(this.domainCrosshairPaint);
        hash = 71 * hash + (this.rangeCrosshairVisible ? 1 : 0);
        hash = 71 * hash + Long.hashCode(Double.doubleToLongBits(this.rangeCrosshairValue));
        hash = 71 * hash + Objects.hashCode(this.rangeCrosshairStroke);
        hash = 71 * hash + Objects.hashCode(this.rangeCrosshairPaint);
        hash = 71 * hash + (this.rangeCrosshairLockedOnData ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.foregroundDomainMarkers);
        hash = 71 * hash + Objects.hashCode(this.backgroundDomainMarkers);
        hash = 71 * hash + Objects.hashCode(this.foregroundRangeMarkers);
        hash = 71 * hash + Objects.hashCode(this.backgroundRangeMarkers);
        hash = 71 * hash + Objects.hashCode(this.annotations);
        hash = 71 * hash + this.weight;
        hash = 71 * hash + Objects.hashCode(this.fixedDomainAxisSpace);
        hash = 71 * hash + Objects.hashCode(this.fixedRangeAxisSpace);
        hash = 71 * hash + Objects.hashCode(this.fixedLegendItems);
        hash = 71 * hash + (this.rangePannable ? 1 : 0);
        hash = 71 * hash + Objects.hashCode(this.shadowGenerator);
        return hash;
    }

    /**
     * Returns a clone of the plot.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException  if the cloning is not supported.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        @SuppressWarnings("unchecked")
        CategoryPlot<R, C> clone = (CategoryPlot) super.clone();
        clone.domainAxes = CloneUtils.cloneMapValues(this.domainAxes);
        for (CategoryAxis axis : clone.domainAxes.values()) {
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
        // AxisLocation is immutable, so we can just copy the maps
        clone.domainAxisLocations = new HashMap<>(this.domainAxisLocations);
        clone.rangeAxisLocations = new HashMap<>(this.rangeAxisLocations);
        clone.datasets = new HashMap<>(this.datasets);
        for (CategoryDataset<R, C> dataset : clone.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(clone);
            }
        }
        clone.datasetToDomainAxesMap = new TreeMap<>();
        clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
        clone.datasetToRangeAxesMap = new TreeMap<>();
        clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
        clone.renderers = CloneUtils.cloneMapValues(this.renderers);
        for (CategoryItemRenderer renderer : clone.renderers.values()) {
            if (renderer != null) {
                renderer.setPlot(clone);
                renderer.addChangeListener(clone);
            }
        }
        if (this.fixedDomainAxisSpace != null) {
            clone.fixedDomainAxisSpace = CloneUtils.clone(this.fixedDomainAxisSpace);
        }
        if (this.fixedRangeAxisSpace != null) {
            clone.fixedRangeAxisSpace = CloneUtils.clone(this.fixedRangeAxisSpace);
        }
        clone.annotations = CloneUtils.cloneList(this.annotations);
        clone.foregroundDomainMarkers = CloneUtils.cloneMapValues(this.foregroundDomainMarkers);
        clone.backgroundDomainMarkers = CloneUtils.cloneMapValues(this.backgroundDomainMarkers);
        clone.foregroundRangeMarkers = CloneUtils.cloneMapValues(this.foregroundRangeMarkers);
        clone.backgroundRangeMarkers = CloneUtils.cloneMapValues(this.backgroundRangeMarkers);
        if (this.fixedLegendItems != null) {
            clone.fixedLegendItems = CloneUtils.clone(this.fixedLegendItems);
        }
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
        SerialUtils.writeStroke(this.rangeCrosshairStroke, stream);
        SerialUtils.writePaint(this.rangeCrosshairPaint, stream);
        SerialUtils.writeStroke(this.domainCrosshairStroke, stream);
        SerialUtils.writePaint(this.domainCrosshairPaint, stream);
        SerialUtils.writeStroke(this.rangeMinorGridlineStroke, stream);
        SerialUtils.writePaint(this.rangeMinorGridlinePaint, stream);
        SerialUtils.writeStroke(this.rangeZeroBaselineStroke, stream);
        SerialUtils.writePaint(this.rangeZeroBaselinePaint, stream);
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
        this.rangeCrosshairStroke = SerialUtils.readStroke(stream);
        this.rangeCrosshairPaint = SerialUtils.readPaint(stream);
        this.domainCrosshairStroke = SerialUtils.readStroke(stream);
        this.domainCrosshairPaint = SerialUtils.readPaint(stream);
        this.rangeMinorGridlineStroke = SerialUtils.readStroke(stream);
        this.rangeMinorGridlinePaint = SerialUtils.readPaint(stream);
        this.rangeZeroBaselineStroke = SerialUtils.readStroke(stream);
        this.rangeZeroBaselinePaint = SerialUtils.readPaint(stream);
        for (CategoryAxis xAxis : this.domainAxes.values()) {
            if (xAxis != null) {
                xAxis.setPlot(this);
                xAxis.addChangeListener(this);
            }
        }
        for (ValueAxis yAxis : this.rangeAxes.values()) {
            if (yAxis != null) {
                yAxis.setPlot(this);
                yAxis.addChangeListener(this);
            }
        }
        for (CategoryDataset<R, C> dataset : this.datasets.values()) {
            if (dataset != null) {
                dataset.addChangeListener(this);
            }
        }
        for (CategoryItemRenderer renderer : this.renderers.values()) {
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
