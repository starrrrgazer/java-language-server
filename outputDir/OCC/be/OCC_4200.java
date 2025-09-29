package OCC.be;
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
 * FlowPlot.java
 * -------------
 * (C) Copyright 2021-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 */
/**
 * A plot for visualising flows defined in a {@link FlowDataset}.  This enables
 * the production of a type of Sankey chart.  The example shown here is
 * produced by the {@code FlowPlotDemo1.java} program included in the JFreeChart
 * Demo Collection:
 * <img src="doc-files/FlowPlotDemo1.svg" width="600" height="400" alt="FlowPlotDemo1.svg">
 *
 * @param <K> the data key type.
 */
public class FlowPlot<K extends Comparable<K>> extends Plot implements Cloneable, PublicCloneable, Serializable {

    /**
     * The source of data.
     */
    private FlowDataset<K> dataset;

    /**
     * The node width in Java 2D user-space units.
     */
    private double nodeWidth = 20.0;

    /**
     * The gap between nodes (expressed as a percentage of the plot height).
     */
    private double nodeMargin = 0.01;

    /**
     * The percentage of the plot width to assign to a gap between the nodes
     * and the flow representation.
     */
    private double flowMargin = 0.005;

    /**
     * Stores colors for specific nodes - if there isn't a color in here for
     * the node, the default node color will be used (unless the color swatch
     * is active).
     */
    private Map<NodeKey<K>, Color> nodeColorMap;

    /**
     * Node colors.
     */
    private List<Color> nodeColorSwatch;

    /**
     * A pointer into the color swatch.
     */
    private int nodeColorSwatchPointer;

    /**
     * The default node color if nothing is defined in the nodeColorMap.
     */
    private Color defaultNodeColor;

    /**
     * Default node label font.
     */
    private Font defaultNodeLabelFont;

    /**
     * Default node label paint.
     */
    private Paint defaultNodeLabelPaint;

    /**
     * Default node label alignment.
     */
    private VerticalAlignment nodeLabelAlignment;

    /**
     * The x-offset for node labels.
     */
    private double nodeLabelOffsetX;

    /**
     * The y-offset for node labels.
     */
    private double nodeLabelOffsetY;

    /**
     * The tool tip generator - if null, no tool tips will be displayed.
     */
    private FlowLabelGenerator toolTipGenerator;

    /**
     * Creates a new instance that will source data from the specified dataset.
     *
     * @param dataset  the dataset.
     */
    public FlowPlot(FlowDataset<K> dataset) {
        super();
        this.dataset = dataset;
        if (dataset != null) {
            dataset.addChangeListener(this);
        }
        this.nodeColorMap = new HashMap<>();
        this.nodeColorSwatch = new ArrayList<>();
        this.defaultNodeColor = Color.GRAY;
        this.defaultNodeLabelFont = new Font(Font.DIALOG, Font.BOLD, 12);
        this.defaultNodeLabelPaint = Color.BLACK;
        this.nodeLabelAlignment = VerticalAlignment.CENTER;
        this.nodeLabelOffsetX = 2.0;
        this.nodeLabelOffsetY = 2.0;
        this.toolTipGenerator = new StandardFlowLabelGenerator();
    }

    /**
     * Returns a string identifying the plot type.
     *
     * @return A string identifying the plot type.
     */
    @Override
    public String getPlotType() {
        return "FlowPlot";
    }

    /**
     * Returns a reference to the dataset.
     *
     * @return A reference to the dataset (possibly {@code null}).
     */
    public FlowDataset<K> getDataset() {
        return this.dataset;
    }

    /**
     * Sets the dataset for the plot and sends a change notification to all
     * registered listeners.
     *
     * @param dataset  the dataset ({@code null} permitted).
     */
    public void setDataset(FlowDataset<K> dataset) {
        this.dataset = dataset;
        fireChangeEvent();
    }

    /**
     * Returns the node margin (expressed as a percentage of the available
     * plotting space) which is the gap between nodes (sources or destinations).
     * The initial (default) value is {@code 0.01} (1 percent).
     *
     * @return The node margin.
     */
    public double getNodeMargin() {
        return this.nodeMargin;
    }

    /**
     * Sets the node margin and sends a change notification to all registered
     * listeners.
     *
     * @param margin  the margin (expressed as a percentage).
     */
    public void setNodeMargin(double margin) {
        Args.requireNonNegative(margin, "margin");
        this.nodeMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the flow margin.  This determines the gap between the graphic
     * representation of the nodes (sources and destinations) and the curved
     * flow representation.  This is expressed as a percentage of the plot
     * width so that it remains proportional as the plot is resized.  The
     * initial (default) value is {@code 0.005} (0.5 percent).
     *
     * @return The flow margin.
     */
    public double getFlowMargin() {
        return this.flowMargin;
    }

    /**
     * Sets the flow margin and sends a change notification to all registered
     * listeners.
     *
     * @param margin  the margin (must be 0.0 or higher).
     */
    public void setFlowMargin(double margin) {
        Args.requireNonNegative(margin, "margin");
        this.flowMargin = margin;
        fireChangeEvent();
    }

    /**
     * Returns the width of the source and destination nodes, expressed in
     * Java2D user-space units.  The initial (default) value is {@code 20.0}.
     *
     * @return The width.
     */
    public double getNodeWidth() {
        return this.nodeWidth;
    }

    /**
     * Sets the width for the source and destination nodes and sends a change
     * notification to all registered listeners.
     *
     * @param width  the width.
     */
    public void setNodeWidth(double width) {
        this.nodeWidth = width;
        fireChangeEvent();
    }

    /**
     * Returns the list of colors that will be used to autopopulate the node
     * colors when they are first rendered.  If the list is empty, no color
     * will be assigned to the node so, unless it is manually set, the default
     * color will apply.  This method returns a copy of the list, modifying
     * the returned list will not affect the plot.
     *
     * @return The list of colors (possibly empty, but never {@code null}).
     */
    public List<Color> getNodeColorSwatch() {
        return new ArrayList<>(this.nodeColorSwatch);
    }

    /**
     * Sets the color swatch for the plot and sends a change
     * notification to all registered listeners.
     *
     * @param colors  the list of colors ({@code null} not permitted).
     */
    public void setNodeColorSwatch(List<Color> colors) {
        Args.nullNotPermitted(colors, "colors");
        this.nodeColorSwatch = colors;
        fireChangeEvent();
    }

    /**
     * Returns the fill color for the specified node.
     *
     * @param nodeKey  the node key ({@code null} not permitted).
     *
     * @return The fill color (possibly {@code null}).
     */
    public Color getNodeFillColor(NodeKey<K> nodeKey) {
        return this.nodeColorMap.get(nodeKey);
    }

    /**
     * Sets the fill color for the specified node and sends a change
     * notification to all registered listeners.
     *
     * @param nodeKey  the node key ({@code null} not permitted).
     * @param color  the fill color ({@code null} permitted).
     */
    public void setNodeFillColor(NodeKey<K> nodeKey, Color color) {
        this.nodeColorMap.put(nodeKey, color);
        fireChangeEvent();
    }

    /**
     * Returns the default node color.  This is used when no specific node color
     * has been specified.  The initial (default) value is {@code Color.GRAY}.
     *
     * @return The default node color (never {@code null}).
     */
    public Color getDefaultNodeColor() {
        return this.defaultNodeColor;
    }

    /**
     * Sets the default node color and sends a change event to registered
     * listeners.
     *
     * @param color  the color ({@code null} not permitted).
     */
    public void setDefaultNodeColor(Color color) {
        Args.nullNotPermitted(color, "color");
        this.defaultNodeColor = color;
        fireChangeEvent();
    }

    /**
     * Returns the default font used to display labels for the source and
     * destination nodes.  The initial (default) value is
     * {@code Font(Font.DIALOG, Font.BOLD, 12)}.
     *
     * @return The default font (never {@code null}).
     */
    public Font getDefaultNodeLabelFont() {
        return this.defaultNodeLabelFont;
    }

    /**
     * Sets the default font used to display labels for the source and
     * destination nodes and sends a change notification to all registered
     * listeners.
     *
     * @param font  the font ({@code null} not permitted).
     */
    public void setDefaultNodeLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.defaultNodeLabelFont = font;
        fireChangeEvent();
    }

    /**
     * Returns the default paint used to display labels for the source and
     * destination nodes.  The initial (default) value is {@code Color.BLACK}.
     *
     * @return The default paint (never {@code null}).
     */
    public Paint getDefaultNodeLabelPaint() {
        return this.defaultNodeLabelPaint;
    }

    /**
     * Sets the default paint used to display labels for the source and
     * destination nodes and sends a change notification to all registered
     * listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    public void setDefaultNodeLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.defaultNodeLabelPaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the vertical alignment of the node labels relative to the node.
     * The initial (default) value is {@link VerticalAlignment#CENTER}.
     *
     * @return The alignment (never {@code null}).
     */
    public VerticalAlignment getNodeLabelAlignment() {
        return this.nodeLabelAlignment;
    }

    /**
     * Sets the vertical alignment of the node labels and sends a change
     * notification to all registered listeners.
     *
     * @param alignment  the new alignment ({@code null} not permitted).
     */
    public void setNodeLabelAlignment(VerticalAlignment alignment) {
        Args.nullNotPermitted(alignment, "alignment");
        this.nodeLabelAlignment = alignment;
        fireChangeEvent();
    }

    /**
     * Returns the x-offset for the node labels.
     *
     * @return The x-offset for the node labels.
     */
    public double getNodeLabelOffsetX() {
        return this.nodeLabelOffsetX;
    }

    /**
     * Sets the x-offset for the node labels and sends a change notification
     * to all registered listeners.
     *
     * @param offsetX  the node label x-offset in Java2D units.
     */
    public void setNodeLabelOffsetX(double offsetX) {
        this.nodeLabelOffsetX = offsetX;
        fireChangeEvent();
    }

    /**
     * Returns the y-offset for the node labels.
     *
     * @return The y-offset for the node labels.
     */
    public double getNodeLabelOffsetY() {
        return nodeLabelOffsetY;
    }

    /**
     * Sets the y-offset for the node labels and sends a change notification
     * to all registered listeners.
     *
     * @param offsetY  the node label y-offset in Java2D units.
     */
    public void setNodeLabelOffsetY(double offsetY) {
        this.nodeLabelOffsetY = offsetY;
        fireChangeEvent();
    }

    /**
     * Returns the tool tip generator that creates the strings that are
     * displayed as tool tips for the flows displayed in the plot.
     *
     * @return The tool tip generator (possibly {@code null}).
     */
    public FlowLabelGenerator getToolTipGenerator() {
        return this.toolTipGenerator;
    }

    /**
     * Sets the tool tip generator and sends a change notification to all
     * registered listeners.  If the generator is set to {@code null}, no tool
     * tips will be displayed for the flows.
     *
     * @param generator  the new generator ({@code null} permitted).
     */
    public void setToolTipGenerator(FlowLabelGenerator generator) {
        this.toolTipGenerator = generator;
        fireChangeEvent();
    }

    /**
     * Draws the flow plot within the specified area of the supplied graphics
     * target {@code g2}.
     *
     * @param g2  the graphics target ({@code null} not permitted).
     * @param area  the plot area ({@code null} not permitted).
     * @param anchor  the anchor point (ignored).
     * @param parentState  the parent state (ignored).
     * @param info  the plot rendering info.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
        Args.nullNotPermitted(g2, "g2");
        Args.nullNotPermitted(area, "area");
        EntityCollection entities = null;
        if (info != null) {
            info.setPlotArea(area);
            entities = info.getOwner().getEntityCollection();
        }
        RectangleInsets insets = getInsets();
        insets.trim(area);
        if (info != null) {
            info.setDataArea(area);
        }
        // use default JFreeChart background handling
        drawBackground(g2, area);
        // we need to ensure there is space to show all the inflows and all
        // the outflows at each node group, so first we calculate the max
        // flow space required - for each node in the group, consider the
        // maximum of the inflow and the outflow
        double flow2d = Double.POSITIVE_INFINITY;
        double nodeMargin2d = this.nodeMargin * area.getHeight();
        int stageCount = this.dataset.getStageCount();
        for (int stage = 0; stage < this.dataset.getStageCount(); stage++) {
            List<K> sources = this.dataset.getSources(stage);
            int nodeCount = sources.size();
            double flowTotal = 0.0;
            for (K source : sources) {
                double inflow = FlowDatasetUtils.calculateInflow(this.dataset, source, stage);
                double outflow = FlowDatasetUtils.calculateOutflow(this.dataset, source, stage);
                flowTotal = flowTotal + Math.max(inflow, outflow);
            }
            if (flowTotal > 0.0) {
                double availableH = area.getHeight() - (nodeCount - 1) * nodeMargin2d;
                flow2d = Math.min(availableH / flowTotal, flow2d);
            }
            if (stage == this.dataset.getStageCount() - 1) {
                // check inflows to the final destination nodes...
                List<K> destinations = this.dataset.getDestinations(stage);
                int destinationCount = destinations.size();
                flowTotal = 0.0;
                for (K destination : destinations) {
                    double inflow = FlowDatasetUtils.calculateInflow(this.dataset, destination, stage + 1);
                    flowTotal = flowTotal + inflow;
                }
                if (flowTotal > 0.0) {
                    double availableH = area.getHeight() - (destinationCount - 1) * nodeMargin2d;
                    flow2d = Math.min(availableH / flowTotal, flow2d);
                }
            }
        }
        double stageWidth = (area.getWidth() - ((stageCount + 1) * this.nodeWidth)) / stageCount;
        double flowOffset = area.getWidth() * this.flowMargin;
        Map<NodeKey<K>, Rectangle2D> nodeRects = new HashMap<>();
        boolean hasNodeSelections = FlowDatasetUtils.hasNodeSelections(this.dataset);
        boolean hasFlowSelections = FlowDatasetUtils.hasFlowSelections(this.dataset);
        // iterate over all the stages, we can render the source node rects and
        // the flows ... we should add the destination node rects last, then
        // in a final pass add the labels
        for (int stage = 0; stage < this.dataset.getStageCount(); stage++) {
            double stageLeft = area.getX() + (stage + 1) * this.nodeWidth + (stage * stageWidth);
            double stageRight = stageLeft + stageWidth;
            // calculate the source node and flow rectangles
            Map<FlowKey<K>, Rectangle2D> sourceFlowRects = new HashMap<>();
            double nodeY = area.getY();
            for (K source : this.dataset.getSources(stage)) {
                double inflow = FlowDatasetUtils.calculateInflow(dataset, source, stage);
                double outflow = FlowDatasetUtils.calculateOutflow(dataset, source, stage);
                double nodeHeight = (Math.max(inflow, outflow) * flow2d);
                Rectangle2D nodeRect = new Rectangle2D.Double(stageLeft - nodeWidth, nodeY, nodeWidth, nodeHeight);
                if (entities != null) {
                    entities.add(new NodeEntity(new NodeKey<>(stage, source), nodeRect, source.toString()));
                }
                nodeRects.put(new NodeKey<>(stage, source), nodeRect);
                double y = nodeY;
                for (K destination : this.dataset.getDestinations(stage)) {
                    Number flow = this.dataset.getFlow(stage, source, destination);
                    if (flow != null) {
                        double height = flow.doubleValue() * flow2d;
                        Rectangle2D rect = new Rectangle2D.Double(stageLeft - nodeWidth, y, nodeWidth, height);
                        sourceFlowRects.put(new FlowKey<>(stage, source, destination), rect);
                        y = y + height;
                    }
                }
                nodeY = nodeY + nodeHeight + nodeMargin2d;
            }
            // calculate the destination rectangles
            Map<FlowKey<K>, Rectangle2D> destFlowRects = new HashMap<>();
            nodeY = area.getY();
            for (K destination : this.dataset.getDestinations(stage)) {
                double inflow = FlowDatasetUtils.calculateInflow(dataset, destination, stage + 1);
                double outflow = FlowDatasetUtils.calculateOutflow(dataset, destination, stage + 1);
                double nodeHeight = Math.max(inflow, outflow) * flow2d;
                nodeRects.put(new NodeKey<>(stage + 1, destination), new Rectangle2D.Double(stageRight, nodeY, nodeWidth, nodeHeight));
                double y = nodeY;
                for (K source : this.dataset.getSources(stage)) {
                    Number flow = this.dataset.getFlow(stage, source, destination);
                    if (flow != null) {
                        double height = flow.doubleValue() * flow2d;
                        Rectangle2D rect = new Rectangle2D.Double(stageRight, y, nodeWidth, height);
                        y = y + height;
                        destFlowRects.put(new FlowKey<>(stage, source, destination), rect);
                    }
                }
                nodeY = nodeY + nodeHeight + nodeMargin2d;
            }
            for (K source : this.dataset.getSources(stage)) {
                NodeKey<K> nodeKey = new NodeKey<>(stage, source);
                Rectangle2D nodeRect = nodeRects.get(nodeKey);
                Color ncol = lookupNodeColor(nodeKey);
                if (hasNodeSelections) {
                    if (!Boolean.TRUE.equals(dataset.getNodeProperty(nodeKey, NodeKey.SELECTED_PROPERTY_KEY))) {
                        int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                        ncol = new Color(g, g, g, ncol.getAlpha());
                    }
                }
                g2.setPaint(ncol);
                g2.fill(nodeRect);
                for (K destination : this.dataset.getDestinations(stage)) {
                    FlowKey<K> flowKey = new FlowKey<>(stage, source, destination);
                    Rectangle2D sourceRect = sourceFlowRects.get(flowKey);
                    if (sourceRect == null) {
                        continue;
                    }
                    Rectangle2D destRect = destFlowRects.get(flowKey);
                    Path2D connect = new Path2D.Double();
                    connect.moveTo(sourceRect.getMaxX() + flowOffset, sourceRect.getMinY());
                    connect.curveTo(stageLeft + stageWidth / 2.0, sourceRect.getMinY(), stageLeft + stageWidth / 2.0, destRect.getMinY(), destRect.getX() - flowOffset, destRect.getMinY());
                    connect.lineTo(destRect.getX() - flowOffset, destRect.getMaxY());
                    connect.curveTo(stageLeft + stageWidth / 2.0, destRect.getMaxY(), stageLeft + stageWidth / 2.0, sourceRect.getMaxY(), sourceRect.getMaxX() + flowOffset, sourceRect.getMaxY());
                    connect.closePath();
                    Color nc = lookupNodeColor(nodeKey);
                    if (hasFlowSelections) {
                        if (!Boolean.TRUE.equals(dataset.getFlowProperty(flowKey, FlowKey.SELECTED_PROPERTY_KEY))) {
                            int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                            nc = new Color(g, g, g, ncol.getAlpha());
                        }
                    }
                    GradientPaint gp = new GradientPaint((float) sourceRect.getMaxX(), 0, nc, (float) destRect.getMinX(), 0, new Color(nc.getRed(), nc.getGreen(), nc.getBlue(), 128));
                    Composite saved = g2.getComposite();
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                    g2.setPaint(gp);
                    g2.fill(connect);
                    if (entities != null) {
                        String toolTip = null;
                        if (this.toolTipGenerator != null) {
                            toolTip = this.toolTipGenerator.generateLabel(this.dataset, flowKey);
                        }
                        entities.add(new FlowEntity<>(flowKey, connect, toolTip, ""));
                    }
                    g2.setComposite(saved);
                }
            }
        }
        // now draw the destination nodes
        int lastStage = this.dataset.getStageCount() - 1;
        for (K destination : this.dataset.getDestinations(lastStage)) {
            NodeKey<K> nodeKey = new NodeKey<>(lastStage + 1, destination);
            Rectangle2D nodeRect = nodeRects.get(nodeKey);
            if (nodeRect != null) {
                Color ncol = lookupNodeColor(nodeKey);
                if (hasNodeSelections) {
                    if (!Boolean.TRUE.equals(dataset.getNodeProperty(nodeKey, NodeKey.SELECTED_PROPERTY_KEY))) {
                        int g = (ncol.getRed() + ncol.getGreen() + ncol.getBlue()) / 3;
                        ncol = new Color(g, g, g, ncol.getAlpha());
                    }
                }
                g2.setPaint(ncol);
                g2.fill(nodeRect);
                if (entities != null) {
                    entities.add(new NodeEntity(new NodeKey<>(lastStage + 1, destination), nodeRect, destination.toString()));
                }
            }
        }
        // now draw all the labels over top of everything else
        g2.setFont(this.defaultNodeLabelFont);
        g2.setPaint(this.defaultNodeLabelPaint);
        for (NodeKey<K> key : nodeRects.keySet()) {
            Rectangle2D r = nodeRects.get(key);
            if (key.getStage() < this.dataset.getStageCount()) {
                TextUtils.drawAlignedString(key.getNode().toString(), g2, (float) (r.getMaxX() + flowOffset + this.nodeLabelOffsetX), (float) labelY(r), TextAnchor.CENTER_LEFT);
            } else {
                TextUtils.drawAlignedString(key.getNode().toString(), g2, (float) (r.getX() - flowOffset - this.nodeLabelOffsetX), (float) labelY(r), TextAnchor.CENTER_RIGHT);
            }
        }
    }

    /**
     * Performs a lookup on the color for the specified node.
     *
     * @param nodeKey  the node key ({@code null} not permitted).
     *
     * @return The node color.
     */
    protected Color lookupNodeColor(NodeKey<K> nodeKey) {
        Color result = this.nodeColorMap.get(nodeKey);
        if (result == null) {
            // if the color swatch is non-empty, we use it to autopopulate
            // the node colors...
            if (!this.nodeColorSwatch.isEmpty()) {
                // look through previous stages to see if this source key is already seen
                for (int s = 0; s < nodeKey.getStage(); s++) {
                    for (K key : dataset.getSources(s)) {
                        if (nodeKey.getNode().equals(key)) {
                            Color color = this.nodeColorMap.get(new NodeKey<>(s, key));
                            setNodeFillColor(nodeKey, color);
                            return color;
                        }
                    }
                }
                result = this.nodeColorSwatch.get(Math.min(this.nodeColorSwatchPointer, this.nodeColorSwatch.size() - 1));
                this.nodeColorSwatchPointer++;
                if (this.nodeColorSwatchPointer > this.nodeColorSwatch.size() - 1) {
                    this.nodeColorSwatchPointer = 0;
                }
                setNodeFillColor(nodeKey, result);
                return result;
            } else {
                result = this.defaultNodeColor;
            }
        }
        return result;
    }

    /**
     * Computes the y-coordinate for a node label taking into account the
     * current alignment settings.
     *
     * @param r  the node rectangle.
     *
     * @return The y-coordinate for the label.
     */
    private double labelY(Rectangle2D r) {
        if (this.nodeLabelAlignment == VerticalAlignment.TOP) {
            return r.getY() + this.nodeLabelOffsetY;
        } else if (this.nodeLabelAlignment == VerticalAlignment.BOTTOM) {
            return r.getMaxY() - this.nodeLabelOffsetY;
        } else {
            return r.getCenterY();
        }
    }

    /**
     * Tests this plot for equality with an arbitrary object.  Note that, for
     * the purposes of this equality test, the dataset is ignored.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FlowPlot)) {
            return false;
        }
        FlowPlot<K> that = (FlowPlot<K>) obj;
        if (!this.defaultNodeColor.equals(that.defaultNodeColor)) {
            return false;
        }
        if (!this.nodeColorMap.equals(that.nodeColorMap)) {
            return false;
        }
        if (!this.nodeColorSwatch.equals(that.nodeColorSwatch)) {
            return false;
        }
        if (!this.defaultNodeLabelFont.equals(that.defaultNodeLabelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.defaultNodeLabelPaint, that.defaultNodeLabelPaint)) {
            return false;
        }
        if (this.flowMargin != that.flowMargin) {
            return false;
        }
        if (this.nodeMargin != that.nodeMargin) {
            return false;
        }
        if (this.nodeWidth != that.nodeWidth) {
            return false;
        }
        if (this.nodeLabelOffsetX != that.nodeLabelOffsetX) {
            return false;
        }
        if (this.nodeLabelOffsetY != that.nodeLabelOffsetY) {
            return false;
        }
        if (this.nodeLabelAlignment != that.nodeLabelAlignment) {
            return false;
        }
        if (!Objects.equals(this.toolTipGenerator, that.toolTipGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a hashcode for this instance.
     *
     * @return A hashcode.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeWidth));
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeMargin));
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.flowMargin));
        hash = 83 * hash + Objects.hashCode(this.nodeColorMap);
        hash = 83 * hash + Objects.hashCode(this.nodeColorSwatch);
        hash = 83 * hash + Objects.hashCode(this.defaultNodeColor);
        hash = 83 * hash + Objects.hashCode(this.defaultNodeLabelFont);
        hash = 83 * hash + Objects.hashCode(this.defaultNodeLabelPaint);
        hash = 83 * hash + Objects.hashCode(this.nodeLabelAlignment);
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeLabelOffsetX));
        hash = 83 * hash + Long.hashCode(Double.doubleToLongBits(this.nodeLabelOffsetY));
        hash = 83 * hash + Objects.hashCode(this.toolTipGenerator);
        return hash;
    }

    /**
     * Returns an independent copy of this {@code FlowPlot} instance (note,
     * however, that the dataset is NOT cloned).
     *
     * @return A close of this instance.
     *
     * @throws CloneNotSupportedException if there is a problem cloning
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        FlowPlot<K> clone = (FlowPlot<K>) super.clone();
        clone.nodeColorMap = new HashMap<>(this.nodeColorMap);
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
 * -----------
 * Marker.java
 * -----------
 * (C) Copyright 2002-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Nicolas Brodu;
 */
/**
 * The base class for markers that can be added to plots to highlight a value
 * or range of values.
 */
public abstract class Marker implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -734389651405327166L;

    /**
     * The paint (null is not allowed).
     */
    private transient Paint paint;

    /**
     * The stroke (null is not allowed).
     */
    private transient Stroke stroke;

    /**
     * The outline paint.
     */
    private transient Paint outlinePaint;

    /**
     * The outline stroke.
     */
    private transient Stroke outlineStroke;

    /**
     * The alpha transparency.
     */
    private float alpha;

    /**
     * The label.
     */
    private String label = null;

    /**
     * The label font.
     */
    private Font labelFont;

    /**
     * The label paint.
     */
    private transient Paint labelPaint;

    /**
     * The label background color.
     */
    private Color labelBackgroundColor;

    /**
     * The label position.
     */
    private RectangleAnchor labelAnchor;

    /**
     * The text anchor for the label.
     */
    private TextAnchor labelTextAnchor;

    /**
     * The label offset from the marker rectangle (see also labelOffsetType).
     */
    private RectangleInsets labelOffset;

    /**
     * The offset type for the label (see also labelOffset).
     */
    private LengthAdjustmentType labelOffsetType;

    /**
     * Storage for registered change listeners.
     */
    private transient EventListenerList listenerList;

    /**
     * Creates a new marker with default attributes.
     */
    protected Marker() {
        this(Color.GRAY);
    }

    /**
     * Constructs a new marker.
     *
     * @param paint  the paint ({@code null} not permitted).
     */
    protected Marker(Paint paint) {
        this(paint, new BasicStroke(0.5f), Color.GRAY, new BasicStroke(0.5f), 0.80f);
    }

    /**
     * Constructs a new marker.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the stroke ({@code null} not permitted).
     * @param outlinePaint  the outline paint ({@code null} permitted).
     * @param outlineStroke  the outline stroke ({@code null} permitted).
     * @param alpha  the alpha transparency (must be in the range 0.0f to
     *     1.0f).
     *
     * @throws IllegalArgumentException if {@code paint} or
     *     {@code stroke} is {@code null}, or {@code alpha} is
     *     not in the specified range.
     */
    protected Marker(Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
        Args.nullNotPermitted(paint, "paint");
        Args.nullNotPermitted(stroke, "stroke");
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f");
        }
        this.paint = paint;
        this.stroke = stroke;
        this.outlinePaint = outlinePaint;
        this.outlineStroke = outlineStroke;
        this.alpha = alpha;
        this.labelFont = new Font("SansSerif", Font.PLAIN, 9);
        this.labelPaint = Color.BLACK;
        this.labelBackgroundColor = new Color(100, 100, 100, 100);
        this.labelAnchor = RectangleAnchor.TOP_LEFT;
        this.labelOffset = new RectangleInsets(3.0, 3.0, 3.0, 3.0);
        this.labelOffsetType = LengthAdjustmentType.CONTRACT;
        this.labelTextAnchor = TextAnchor.CENTER;
        this.listenerList = new EventListenerList();
    }

    /**
     * Returns the paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPaint(Paint)
     */
    public Paint getPaint() {
        return this.paint;
    }

    /**
     * Sets the paint and sends a {@link MarkerChangeEvent} to all registered
     * listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.paint = paint;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the stroke.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setStroke(Stroke)
     */
    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Sets the stroke and sends a {@link MarkerChangeEvent} to all registered
     * listeners.
     *
     * @param stroke  the stroke ({@code null}not permitted).
     *
     * @see #getStroke()
     */
    public void setStroke(Stroke stroke) {
        Args.nullNotPermitted(stroke, "stroke");
        this.stroke = stroke;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the outline paint.
     *
     * @return The outline paint (possibly {@code null}).
     *
     * @see #setOutlinePaint(Paint)
     */
    public Paint getOutlinePaint() {
        return this.outlinePaint;
    }

    /**
     * Sets the outline paint and sends a {@link MarkerChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getOutlinePaint()
     */
    public void setOutlinePaint(Paint paint) {
        this.outlinePaint = paint;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the outline stroke.
     *
     * @return The outline stroke (possibly {@code null}).
     *
     * @see #setOutlineStroke(Stroke)
     */
    public Stroke getOutlineStroke() {
        return this.outlineStroke;
    }

    /**
     * Sets the outline stroke and sends a {@link MarkerChangeEvent} to all
     * registered listeners.
     *
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getOutlineStroke()
     */
    public void setOutlineStroke(Stroke stroke) {
        this.outlineStroke = stroke;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the alpha transparency.
     *
     * @return The alpha transparency.
     *
     * @see #setAlpha(float)
     */
    public float getAlpha() {
        return this.alpha;
    }

    /**
     * Sets the alpha transparency that should be used when drawing the
     * marker, and sends a {@link MarkerChangeEvent} to all registered
     * listeners.  The alpha transparency is a value in the range 0.0f
     * (completely transparent) to 1.0f (completely opaque).
     *
     * @param alpha  the alpha transparency (must be in the range 0.0f to
     *     1.0f).
     *
     * @throws IllegalArgumentException if {@code alpha} is not in the
     *     specified range.
     *
     * @see #getAlpha()
     */
    public void setAlpha(float alpha) {
        if (alpha < 0.0f || alpha > 1.0f) {
            throw new IllegalArgumentException("The 'alpha' value must be in the range 0.0f to 1.0f");
        }
        this.alpha = alpha;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label (if {@code null} no label is displayed).
     *
     * @return The label (possibly {@code null}).
     *
     * @see #setLabel(String)
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label (if {@code null} no label is displayed) and sends a
     * {@link MarkerChangeEvent} to all registered listeners.
     *
     * @param label  the label ({@code null} permitted).
     *
     * @see #getLabel()
     */
    public void setLabel(String label) {
        this.label = label;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label font.
     *
     * @return The label font (never {@code null}).
     *
     * @see #setLabelFont(Font)
     */
    public Font getLabelFont() {
        return this.labelFont;
    }

    /**
     * Sets the label font and sends a {@link MarkerChangeEvent} to all
     * registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getLabelFont()
     */
    public void setLabelFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.labelFont = font;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label paint.
     *
     * @return The label paint (never {@code null}).
     *
     * @see #setLabelPaint(Paint)
     */
    public Paint getLabelPaint() {
        return this.labelPaint;
    }

    /**
     * Sets the label paint and sends a {@link MarkerChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getLabelPaint()
     */
    public void setLabelPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.labelPaint = paint;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label background color.  The default value is
     * {@code Color(100, 100, 100, 100)}..
     *
     * @return The label background color (never {@code null}).
     */
    public Color getLabelBackgroundColor() {
        return this.labelBackgroundColor;
    }

    /**
     * Sets the label background color.
     *
     * @param color  the color ({@code null} not permitted).
     */
    public void setLabelBackgroundColor(Color color) {
        Args.nullNotPermitted(color, "color");
        this.labelBackgroundColor = color;
    }

    /**
     * Returns the label anchor.  This defines the position of the label
     * anchor, relative to the bounds of the marker.
     *
     * @return The label anchor (never {@code null}).
     *
     * @see #setLabelAnchor(RectangleAnchor)
     */
    public RectangleAnchor getLabelAnchor() {
        return this.labelAnchor;
    }

    /**
     * Sets the label anchor and sends a {@link MarkerChangeEvent} to all
     * registered listeners.  The anchor defines the position of the label
     * anchor, relative to the bounds of the marker.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     *
     * @see #getLabelAnchor()
     */
    public void setLabelAnchor(RectangleAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.labelAnchor = anchor;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label offset.
     *
     * @return The label offset (never {@code null}).
     *
     * @see #setLabelOffset(RectangleInsets)
     */
    public RectangleInsets getLabelOffset() {
        return this.labelOffset;
    }

    /**
     * Sets the label offset and sends a {@link MarkerChangeEvent} to all
     * registered listeners.
     *
     * @param offset  the label offset ({@code null} not permitted).
     *
     * @see #getLabelOffset()
     */
    public void setLabelOffset(RectangleInsets offset) {
        Args.nullNotPermitted(offset, "offset");
        this.labelOffset = offset;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label offset type.
     *
     * @return The type (never {@code null}).
     *
     * @see #setLabelOffsetType(LengthAdjustmentType)
     */
    public LengthAdjustmentType getLabelOffsetType() {
        return this.labelOffsetType;
    }

    /**
     * Sets the label offset type and sends a {@link MarkerChangeEvent} to all
     * registered listeners.
     *
     * @param adj  the type ({@code null} not permitted).
     *
     * @see #getLabelOffsetType()
     */
    public void setLabelOffsetType(LengthAdjustmentType adj) {
        Args.nullNotPermitted(adj, "adj");
        this.labelOffsetType = adj;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Returns the label text anchor.
     *
     * @return The label text anchor (never {@code null}).
     *
     * @see #setLabelTextAnchor(TextAnchor)
     */
    public TextAnchor getLabelTextAnchor() {
        return this.labelTextAnchor;
    }

    /**
     * Sets the label text anchor and sends a {@link MarkerChangeEvent} to
     * all registered listeners.
     *
     * @param anchor  the label text anchor ({@code null} not permitted).
     *
     * @see #getLabelTextAnchor()
     */
    public void setLabelTextAnchor(TextAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.labelTextAnchor = anchor;
        notifyListeners(new MarkerChangeEvent(this));
    }

    /**
     * Registers an object for notification of changes to the marker.
     *
     * @param listener  the object to be registered.
     *
     * @see #removeChangeListener(MarkerChangeListener)
     */
    public void addChangeListener(MarkerChangeListener listener) {
        this.listenerList.add(MarkerChangeListener.class, listener);
    }

    /**
     * Unregisters an object for notification of changes to the marker.
     *
     * @param listener  the object to be unregistered.
     *
     * @see #addChangeListener(MarkerChangeListener)
     */
    public void removeChangeListener(MarkerChangeListener listener) {
        this.listenerList.remove(MarkerChangeListener.class, listener);
    }

    /**
     * Notifies all registered listeners that the marker has been modified.
     *
     * @param event  information about the change event.
     */
    public void notifyListeners(MarkerChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == MarkerChangeListener.class) {
                ((MarkerChangeListener) listeners[i + 1]).markerChanged(event);
            }
        }
    }

    /**
     * Returns an array containing all the listeners of the specified type.
     *
     * @param <T> the event listener type.
     * @param listenerType  the listener type.
     *
     * @return The array of listeners.
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
        return this.listenerList.getListeners(listenerType);
    }

    /**
     * Tests the marker for equality with an arbitrary object.
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
        if (!(obj instanceof Marker)) {
            return false;
        }
        Marker that = (Marker) obj;
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        if (!Objects.equals(this.stroke, that.stroke)) {
            return false;
        }
        if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
        }
        if (!Objects.equals(this.outlineStroke, that.outlineStroke)) {
            return false;
        }
        if (this.alpha != that.alpha) {
            return false;
        }
        if (!Objects.equals(this.label, that.label)) {
            return false;
        }
        if (!Objects.equals(this.labelFont, that.labelFont)) {
            return false;
        }
        if (!PaintUtils.equal(this.labelPaint, that.labelPaint)) {
            return false;
        }
        if (!this.labelBackgroundColor.equals(that.labelBackgroundColor)) {
            return false;
        }
        if (this.labelAnchor != that.labelAnchor) {
            return false;
        }
        if (this.labelTextAnchor != that.labelTextAnchor) {
            return false;
        }
        if (!Objects.equals(this.labelOffset, that.labelOffset)) {
            return false;
        }
        if (!this.labelOffsetType.equals(that.labelOffsetType)) {
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
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.label);
        hash = 29 * hash + Objects.hashCode(this.labelAnchor);
        hash = 29 * hash + Objects.hashCode(this.labelTextAnchor);
        return hash;
    }

    /**
     * Creates a clone of the marker.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException never.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
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
        SerialUtils.writePaint(this.outlinePaint, stream);
        SerialUtils.writeStroke(this.outlineStroke, stream);
        SerialUtils.writePaint(this.labelPaint, stream);
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
        this.outlinePaint = SerialUtils.readPaint(stream);
        this.outlineStroke = SerialUtils.readStroke(stream);
        this.labelPaint = SerialUtils.readPaint(stream);
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
