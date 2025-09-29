package DEF.ci;
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
 * -------------------------
 * CategoryItemRenderer.java
 * -------------------------
 *
 * (C) Copyright 2001-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Mark Watson (www.markwatson.com);
 *
 */
/**
 * A plug-in object that is used by the {@link CategoryPlot} class to display
 * individual data items from a {@link CategoryDataset}.
 * <p>
 * This interface defines the methods that must be provided by all renderers.
 * If you are implementing a custom renderer, you should consider extending the
 * {@link AbstractCategoryItemRenderer} class.
 * <p>
 * Most renderer attributes are defined using a two layer approach.  When
 * looking up an attribute (for example, the outline paint) the renderer first
 * checks to see if there is a setting that applies to a specific series
 * that the renderer draws.  If there is, that setting is used, but if it is
 * {@code null} the renderer looks up the default setting.  Some attributes
 * allow the base setting to be {@code null}, while other attributes enforce
 * non-{@code null} values.
 */
public interface CategoryItemRenderer extends ChartElement, LegendItemSource {

    /**
     * Returns the number of passes through the dataset required by the
     * renderer.  Usually this will be one, but some renderers may use
     * a second or third pass to overlay items on top of things that were
     * drawn in an earlier pass.
     *
     * @return The pass count.
     */
    int getPassCount();

    /**
     * Returns the plot that the renderer has been assigned to (where
     * {@code null} indicates that the renderer is not currently assigned
     * to a plot).
     *
     * @return The plot (possibly {@code null}).
     *
     * @see #setPlot(CategoryPlot)
     */
    CategoryPlot<?, ?> getPlot();

    /**
     * Sets the plot that the renderer has been assigned to.  This method is
     * usually called by the {@link CategoryPlot}, in normal usage you
     * shouldn't need to call this method directly.
     *
     * @param plot  the plot ({@code null} not permitted).
     *
     * @see #getPlot()
     */
    void setPlot(CategoryPlot<?, ?> plot);

    /**
     * Adds a change listener.
     *
     * @param listener  the listener.
     *
     * @see #removeChangeListener(RendererChangeListener)
     */
    void addChangeListener(RendererChangeListener listener);

    /**
     * Removes a change listener.
     *
     * @param listener  the listener.
     *
     * @see #addChangeListener(RendererChangeListener)
     */
    void removeChangeListener(RendererChangeListener listener);

    /**
     * Returns the range of values the renderer requires to display all the
     * items from the specified dataset.
     *
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return The range (or {@code null} if the dataset is
     *         {@code null} or empty).
     */
    Range findRangeBounds(CategoryDataset<?, ?> dataset);

    /**
     * Initialises the renderer.  This method will be called before the first
     * item is rendered, giving the renderer an opportunity to initialise any
     * state information it wants to maintain. The renderer can do nothing if
     * it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param rendererIndex  the renderer index.
     * @param info  collects chart rendering information for return to caller.
     *
     * @return A state object (maintains state information relevant to one
     *         chart drawing).
     */
    CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot<?, ?> plot, int rendererIndex, PlotRenderingInfo info);

    /**
     * Returns a boolean that indicates whether the specified item
     * should be drawn (this is typically used to hide an entire series).
     *
     * @param series  the series index.
     * @param item  the item index.
     *
     * @return A boolean.
     */
    boolean getItemVisible(int series, int item);

    /**
     * Returns a boolean that indicates whether the specified series
     * should be drawn (this is typically used to hide an entire series).
     *
     * @param series  the series index.
     *
     * @return A boolean.
     */
    boolean isSeriesVisible(int series);

    /**
     * Returns the flag that controls whether a series is visible.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesVisible(int, Boolean)
     */
    Boolean getSeriesVisible(int series);

    /**
     * Sets the flag that controls whether a series is visible and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #getSeriesVisible(int)
     */
    void setSeriesVisible(int series, Boolean visible);

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
    void setSeriesVisible(int series, Boolean visible, boolean notify);

    /**
     * Returns the default visibility for all series.
     *
     * @return The default visibility.
     *
     * @see #setDefaultSeriesVisible(boolean)
     */
    boolean getDefaultSeriesVisible();

    /**
     * Sets the default visibility and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultSeriesVisible()
     */
    void setDefaultSeriesVisible(boolean visible);

    /**
     * Sets the default visibility and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultSeriesVisible()
     */
    void setDefaultSeriesVisible(boolean visible, boolean notify);

    // SERIES VISIBLE IN LEGEND (not yet respected by all renderers)
    /**
     * Returns {@code true} if the series should be shown in the legend,
     * and {@code false} otherwise.
     *
     * @param series  the series index.
     *
     * @return A boolean.
     */
    boolean isSeriesVisibleInLegend(int series);

    /**
     * Returns the flag that controls whether a series is visible in the
     * legend.  This method returns only the "per series" settings - to
     * incorporate the override and base settings as well, you need to use the
     * {@link #isSeriesVisibleInLegend(int)} method.
     *
     * @param series  the series index (zero-based).
     *
     * @return The flag (possibly {@code null}).
     *
     * @see #setSeriesVisibleInLegend(int, Boolean)
     */
    Boolean getSeriesVisibleInLegend(int series);

    /**
     * Sets the flag that controls whether a series is visible in the legend
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #getSeriesVisibleInLegend(int)
     */
    void setSeriesVisibleInLegend(int series, Boolean visible);

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
    void setSeriesVisibleInLegend(int series, Boolean visible, boolean notify);

    /**
     * Returns the default visibility in the legend for all series.
     *
     * @return The default visibility.
     *
     * @see #setDefaultSeriesVisibleInLegend(boolean)
     */
    boolean getDefaultSeriesVisibleInLegend();

    /**
     * Sets the default visibility in the legend and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultSeriesVisibleInLegend()
     */
    void setDefaultSeriesVisibleInLegend(boolean visible);

    /**
     * Sets the default visibility in the legend and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility.
     * @param notify  notify listeners?
     *
     * @see #getDefaultSeriesVisibleInLegend()
     */
    void setDefaultSeriesVisibleInLegend(boolean visible, boolean notify);

    //// PAINT /////////////////////////////////////////////////////////////////
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemPaint(int row, int column);

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series index (zero-based).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesPaint(int, Paint)
     */
    Paint getSeriesPaint(int series);

    /**
     * Sets the paint used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesPaint(int)
     */
    void setSeriesPaint(int series, Paint paint);

    /**
     * Sets the paint used for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesPaint(int)
     */
    void setSeriesPaint(int series, Paint paint, boolean notify);

    /**
     * Returns the default paint.  During rendering, a renderer will first look
     * up the series paint and, if this is {@code null}, it will use the
     * default paint.
     *
     * @return The default paint (never {@code null}).
     *
     * @see #setDefaultPaint(Paint)
     */
    Paint getDefaultPaint();

    /**
     * Sets the default paint and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultPaint()
     */
    void setDefaultPaint(Paint paint);

    /**
     * Sets the default paint and sends a {@link RendererChangeEvent} to all
     * registered listeners if requested.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultPaint()
     */
    void setDefaultPaint(Paint paint, boolean notify);

    //// FILL PAINT /////////////////////////////////////////////////////////
    /**
     * Returns the paint used to fill data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemFillPaint(int row, int column);

    /**
     * Returns the paint used to fill an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesFillPaint(int, Paint)
     */
    Paint getSeriesFillPaint(int series);

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesFillPaint(int)
     */
    void setSeriesFillPaint(int series, Paint paint);

    /**
     * Returns the default outline paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultFillPaint(Paint)
     */
    Paint getDefaultFillPaint();

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultFillPaint()
     */
    void setDefaultFillPaint(Paint paint);

    //// OUTLINE PAINT /////////////////////////////////////////////////////////
    /**
     * Returns the paint used to outline data items as they are drawn.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemOutlinePaint(int row, int column);

    /**
     * Returns the paint used to outline an item drawn by the renderer.
     *
     * @param series  the series (zero-based index).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesOutlinePaint(int, Paint)
     */
    Paint getSeriesOutlinePaint(int series);

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesOutlinePaint(int)
     */
    void setSeriesOutlinePaint(int series, Paint paint);

    /**
     * Sets the paint used for a series outline and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesOutlinePaint(int)
     */
    void setSeriesOutlinePaint(int series, Paint paint, boolean notify);

    /**
     * Returns the default outline paint.  During rendering, the renderer
     * will look up the series outline paint and, if this is {@code null}, it
     * will use the default outline paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultOutlinePaint(Paint)
     */
    Paint getDefaultOutlinePaint();

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultOutlinePaint()
     */
    void setDefaultOutlinePaint(Paint paint);

    /**
     * Sets the default outline paint and sends a {@link RendererChangeEvent} to
     * all registered listeners if requested.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  send a change event?
     *
     * @see #getDefaultOutlinePaint()
     */
    void setDefaultOutlinePaint(Paint paint, boolean notify);

    //// STROKE ////////////////////////////////////////////////////////////////
    /**
     * Returns the stroke used to draw data items.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    Stroke getItemStroke(int row, int column);

    /**
     * Returns the stroke used to draw the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setSeriesStroke(int, Stroke)
     */
    Stroke getSeriesStroke(int series);

    /**
     * Sets the stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getSeriesStroke(int)
     */
    void setSeriesStroke(int series, Stroke stroke);

    /**
     * Sets the stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesStroke(int)
     */
    void setSeriesStroke(int series, Stroke stroke, boolean notify);

    /**
     * Returns the default stroke.
     *
     * @return The default stroke (never {@code null}).
     *
     * @see #setDefaultStroke(Stroke)
     */
    Stroke getDefaultStroke();

    /**
     * Sets the default stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultStroke()
     */
    void setDefaultStroke(Stroke stroke);

    /**
     * Sets the default stroke and sends a {@link RendererChangeEvent} to all
     * registered listeners if requested.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultStroke()
     */
    void setDefaultStroke(Stroke stroke, boolean notify);

    //// OUTLINE STROKE ////////////////////////////////////////////////////////
    /**
     * Returns the stroke used to outline data items.
     * <p>
     * The default implementation passes control to the
     * lookupSeriesOutlineStroke method.  You can override this method if you
     * require different behaviour.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The stroke (never {@code null}).
     */
    Stroke getItemOutlineStroke(int row, int column);

    /**
     * Returns the stroke used to outline the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The stroke (possibly {@code null}).
     *
     * @see #setSeriesOutlineStroke(int, Stroke)
     */
    Stroke getSeriesOutlineStroke(int series);

    /**
     * Sets the outline stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     *
     * @see #getSeriesOutlineStroke(int)
     */
    void setSeriesOutlineStroke(int series, Stroke stroke);

    /**
     * Sets the outline stroke used for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param stroke  the stroke ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesOutlineStroke(int)
     */
    void setSeriesOutlineStroke(int series, Stroke stroke, boolean notify);

    /**
     * Returns the default outline stroke.
     *
     * @return The stroke (never {@code null}).
     *
     * @see #setDefaultOutlineStroke(Stroke)
     */
    Stroke getDefaultOutlineStroke();

    /**
     * Sets the default outline stroke and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     *
     * @see #getDefaultOutlineStroke()
     */
    void setDefaultOutlineStroke(Stroke stroke);

    /**
     * Sets the default outline stroke and sends a {@link RendererChangeEvent} to
     * all registered listeners if requested.
     *
     * @param stroke  the stroke ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultOutlineStroke()
     */
    void setDefaultOutlineStroke(Stroke stroke, boolean notify);

    //// SHAPE /////////////////////////////////////////////////////////////////
    /**
     * Returns a shape used to represent a data item.
     *
     * @param row  the row (or series) index (zero-based).
     * @param column  the column (or category) index (zero-based).
     *
     * @return The shape (never {@code null}).
     */
    Shape getItemShape(int row, int column);

    /**
     * Returns a shape used to represent the items in a series.
     *
     * @param series  the series (zero-based index).
     *
     * @return The shape (possibly {@code null}).
     *
     * @see #setSeriesShape(int, Shape)
     */
    Shape getSeriesShape(int series);

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape ({@code null} permitted).
     *
     * @see #getSeriesShape(int)
     */
    void setSeriesShape(int series, Shape shape);

    /**
     * Sets the shape used for a series and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param shape  the shape ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesShape(int)
     */
    void setSeriesShape(int series, Shape shape, boolean notify);

    /**
     * Returns the default shape.
     *
     * @return The shape (never {@code null}).
     *
     * @see #setDefaultShape(Shape)
     */
    Shape getDefaultShape();

    /**
     * Sets the default shape and sends a {@link RendererChangeEvent} to all
     * registered listeners.
     *
     * @param shape  the shape ({@code null} not permitted).
     *
     * @see #getDefaultShape()
     */
    void setDefaultShape(Shape shape);

    /**
     * Sets the default shape and sends a {@link RendererChangeEvent} to all
     * registered listeners if requested.
     *
     * @param shape  the shape ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultShape()
     */
    void setDefaultShape(Shape shape, boolean notify);

    // ITEM LABELS VISIBLE
    /**
     * Returns {@code true} if an item label is visible, and
     * {@code false} otherwise.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return A boolean.
     */
    boolean isItemLabelVisible(int row, int column);

    /**
     * Returns {@code true} if the item labels for a series are visible,
     * and {@code false} otherwise.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean.
     *
     * @see #setSeriesItemLabelsVisible(int, Boolean)
     */
    boolean isSeriesItemLabelsVisible(int series);

    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag.
     *
     * @see #isSeriesItemLabelsVisible(int)
     */
    void setSeriesItemLabelsVisible(int series, boolean visible);

    /**
     * Sets a flag that controls the visibility of the item labels for a series.
     *
     * @param series  the series index (zero-based).
     * @param visible  the flag ({@code null} permitted).
     *
     * @see #isSeriesItemLabelsVisible(int)
     */
    void setSeriesItemLabelsVisible(int series, Boolean visible);

    /**
     * Sets the visibility of item labels for a series and, if requested, sends
     * a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param visible  the visible flag.
     * @param notify  a flag that controls whether listeners are notified.
     *
     * @see #isSeriesItemLabelsVisible(int)
     */
    void setSeriesItemLabelsVisible(int series, Boolean visible, boolean notify);

    /**
     * Returns the default setting for item label visibility.  A {@code null}
     * result should be interpreted as equivalent to {@code Boolean.FALSE}
     * (this is an error in the API design, the return value should have been
     * a boolean primitive).
     *
     * @return A flag (possibly {@code null}).
     *
     * @see #setDefaultItemLabelsVisible(boolean)
     */
    boolean getDefaultItemLabelsVisible();

    /**
     * Sets the default flag that controls whether item labels are visible
     * and sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the flag.
     *
     * @see #getDefaultItemLabelsVisible()
     */
    void setDefaultItemLabelsVisible(boolean visible);

    /**
     * Sets the default visibility for item labels and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param visible  the visibility flag.
     * @param notify  a flag that controls whether listeners are notified.
     *
     * @see #getDefaultItemLabelsVisible()
     */
    void setDefaultItemLabelsVisible(boolean visible, boolean notify);

    // ITEM LABEL GENERATOR
    /**
     * Returns the item label generator for the specified data item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The generator (possibly {@code null}).
     */
    CategoryItemLabelGenerator getItemLabelGenerator(int series, int item);

    /**
     * Returns the item label generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The label generator (possibly {@code null}).
     *
     * @see #setSeriesItemLabelGenerator(int, CategoryItemLabelGenerator)
     */
    CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series);

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator);

    /**
     * Sets the item label generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator.
     * @param notify  send change event?
     *
     * @see #getSeriesItemLabelGenerator(int)
     */
    void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator, boolean notify);

    /**
     * Returns the default item label generator.
     *
     * @return The generator (possibly {@code null}).
     *
     * @see #setDefaultItemLabelGenerator(CategoryItemLabelGenerator)
     */
    CategoryItemLabelGenerator getDefaultItemLabelGenerator();

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultItemLabelGenerator()
     */
    void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator);

    /**
     * Sets the default item label generator and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemLabelGenerator()
     */
    void setDefaultItemLabelGenerator(CategoryItemLabelGenerator generator, boolean notify);

    // TOOL TIP GENERATOR
    /**
     * Returns the tool tip generator that should be used for the specified
     * item.  This method looks up the generator using the "three-layer"
     * approach outlined in the general description of this interface.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The generator (possibly {@code null}).
     */
    CategoryToolTipGenerator getToolTipGenerator(int row, int column);

    /**
     * Returns the tool tip generator for the specified series (a "layer 1"
     * generator).
     *
     * @param series  the series index (zero-based).
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setSeriesToolTipGenerator(int, CategoryToolTipGenerator)
     */
    CategoryToolTipGenerator getSeriesToolTipGenerator(int series);

    /**
     * Sets the tool tip generator for a series and sends a
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered
     * listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator);

    /**
     * Sets the tool tip generator for a series and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesToolTipGenerator(int)
     */
    void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator, boolean notify);

    /**
     * Returns the default tool tip generator (the "layer 2" generator).
     *
     * @return The tool tip generator (possibly {@code null}).
     *
     * @see #setDefaultToolTipGenerator(CategoryToolTipGenerator)
     */
    CategoryToolTipGenerator getDefaultToolTipGenerator();

    /**
     * Sets the default tool tip generator and sends a
     * {@link org.jfree.chart.event.RendererChangeEvent} to all registered
     * listeners.
     *
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getDefaultToolTipGenerator()
     */
    void setDefaultToolTipGenerator(CategoryToolTipGenerator generator);

    /**
     * Sets the default tool tip generator and sends a
     * {@link RendererChangeEvent} to all registered
     * listeners if requested.
     *
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultToolTipGenerator()
     */
    void setDefaultToolTipGenerator(CategoryToolTipGenerator generator, boolean notify);

    //// ITEM LABEL FONT  //////////////////////////////////////////////////////
    /**
     * Returns the font for an item label.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The font (never {@code null}).
     */
    Font getItemLabelFont(int row, int column);

    /**
     * Returns the font for all the item labels in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The font (possibly {@code null}).
     *
     * @see #setSeriesItemLabelFont(int, Font)
     */
    Font getSeriesItemLabelFont(int series);

    /**
     * Sets the item label font for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param font  the font ({@code null} permitted).
     *
     * @see #getSeriesItemLabelFont(int)
     */
    void setSeriesItemLabelFont(int series, Font font);

    /**
     * Sets the item label font for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param font  the font ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesItemLabelFont(int)
     */
    void setSeriesItemLabelFont(int series, Font font, boolean notify);

    /**
     * Returns the default item label font (this is used when no other font
     * setting is available).
     *
     * @return The font (never {@code null}).
     *
     * @see #setDefaultItemLabelFont(Font)
     */
    Font getDefaultItemLabelFont();

    /**
     * Sets the default item label font and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getDefaultItemLabelFont()
     */
    void setDefaultItemLabelFont(Font font);

    /**
     * Sets the default item label font and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param font  the font ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemLabelFont()
     */
    void setDefaultItemLabelFont(Font font, boolean notify);

    //// ITEM LABEL PAINT  /////////////////////////////////////////////////////
    /**
     * Returns the paint used to draw an item label.
     *
     * @param row  the row index (zero based).
     * @param column  the column index (zero based).
     *
     * @return The paint (never {@code null}).
     */
    Paint getItemLabelPaint(int row, int column);

    /**
     * Returns the paint used to draw the item labels for a series.
     *
     * @param series  the series index (zero based).
     *
     * @return The paint (possibly {@code null}).
     *
     * @see #setSeriesItemLabelPaint(int, Paint)
     */
    Paint getSeriesItemLabelPaint(int series);

    /**
     * Sets the item label paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series (zero based index).
     * @param paint  the paint ({@code null} permitted).
     *
     * @see #getSeriesItemLabelPaint(int)
     */
    void setSeriesItemLabelPaint(int series, Paint paint);

    /**
     * Sets the item label paint for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series (zero based index).
     * @param paint  the paint ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesItemLabelPaint(int)
     */
    void setSeriesItemLabelPaint(int series, Paint paint, boolean notify);

    /**
     * Returns the default item label paint.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setDefaultItemLabelPaint(Paint)
     */
    Paint getDefaultItemLabelPaint();

    /**
     * Sets the default item label paint and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getDefaultItemLabelPaint()
     */
    void setDefaultItemLabelPaint(Paint paint);

    /**
     * Sets the default item label paint and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param paint  the paint ({@code null} not permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemLabelPaint()
     */
    void setDefaultItemLabelPaint(Paint paint, boolean notify);

    // POSITIVE ITEM LABEL POSITION...
    /**
     * Returns the item label position for positive values.
     *
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     *
     * @return The item label position (never {@code null}).
     */
    ItemLabelPosition getPositiveItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for all positive values in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item label position.
     *
     * @see #setSeriesPositiveItemLabelPosition(int, ItemLabelPosition)
     */
    ItemLabelPosition getSeriesPositiveItemLabelPosition(int series);

    /**
     * Sets the item label position for all positive values in a series and
     * sends a {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     *
     * @see #getSeriesPositiveItemLabelPosition(int)
     */
    void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position);

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
    void setSeriesPositiveItemLabelPosition(int series, ItemLabelPosition position, boolean notify);

    /**
     * Returns the default positive item label position.
     *
     * @return The position.
     *
     * @see #setDefaultPositiveItemLabelPosition(ItemLabelPosition)
     */
    ItemLabelPosition getDefaultPositiveItemLabelPosition();

    /**
     * Sets the default positive item label position.
     *
     * @param position  the position.
     *
     * @see #getDefaultPositiveItemLabelPosition()
     */
    void setDefaultPositiveItemLabelPosition(ItemLabelPosition position);

    /**
     * Sets the default positive item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position.
     * @param notify  notify registered listeners?
     *
     * @see #getDefaultPositiveItemLabelPosition()
     */
    void setDefaultPositiveItemLabelPosition(ItemLabelPosition position, boolean notify);

    // NEGATIVE ITEM LABEL POSITION...
    /**
     * Returns the item label position for negative values.  This method can be
     * overridden to provide customisation of the item label position for
     * individual data items.
     *
     * @param row  the row index (zero-based).
     * @param column  the column (zero-based).
     *
     * @return The item label position.
     */
    ItemLabelPosition getNegativeItemLabelPosition(int row, int column);

    /**
     * Returns the item label position for all negative values in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item label position.
     *
     * @see #setSeriesNegativeItemLabelPosition(int, ItemLabelPosition)
     */
    ItemLabelPosition getSeriesNegativeItemLabelPosition(int series);

    /**
     * Sets the item label position for negative values in a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param position  the position ({@code null} permitted).
     *
     * @see #getSeriesNegativeItemLabelPosition(int)
     */
    void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position);

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
    void setSeriesNegativeItemLabelPosition(int series, ItemLabelPosition position, boolean notify);

    /**
     * Returns the default item label position for negative values.
     *
     * @return The position.
     *
     * @see #setDefaultNegativeItemLabelPosition(ItemLabelPosition)
     */
    ItemLabelPosition getDefaultNegativeItemLabelPosition();

    /**
     * Sets the default item label position for negative values and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position.
     *
     * @see #getDefaultNegativeItemLabelPosition()
     */
    void setDefaultNegativeItemLabelPosition(ItemLabelPosition position);

    /**
     * Sets the default negative item label position and, if requested, sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param position  the position.
     * @param notify  notify registered listeners?
     *
     * @see #getDefaultNegativeItemLabelPosition()
     */
    void setDefaultNegativeItemLabelPosition(ItemLabelPosition position, boolean notify);

    // CREATE ENTITIES
    /**
     * Returns a flag that determines whether an entity is generated
     * for the specified item.  The standard implementation of this method
     * will typically return the flag for the series or, if that is
     * {@code null}, the value returned by {@link #getDefaultCreateEntities()}.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return A boolean.
     */
    boolean getItemCreateEntity(int series, int item);

    /**
     * Returns a boolean indicating whether entities should be created
     * for the items in a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return A boolean (possibly {@code null}).
     */
    Boolean getSeriesCreateEntities(int series);

    /**
     * Sets a flag that indicates whether entities should be created during
     * rendering for the items in the specified series, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param create  the new flag value ({@code null} permitted).
     */
    void setSeriesCreateEntities(int series, Boolean create);

    /**
     * Sets a flag that indicates whether entities should be created during
     * rendering for the items in the specified series, and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param create  the new flag value ({@code null} permitted).
     * @param notify  send change event?
     */
    void setSeriesCreateEntities(int series, Boolean create, boolean notify);

    /**
     * Returns the default value for the flag that controls whether
     * an entity is created for an item during rendering.
     *
     * @return A boolean.
     */
    boolean getDefaultCreateEntities();

    /**
     * Sets the default setting for whether entities should be created
     * for items during rendering, and sends a {@link RendererChangeEvent} to
     * all registered listeners.
     *
     * @param create  the new flag value.
     */
    void setDefaultCreateEntities(boolean create);

    /**
     * Sets the default setting for whether entities should be created
     * for items during rendering, and sends a {@link RendererChangeEvent} to
     * all registered listeners if requested.
     *
     * @param create  the new flag value.
     * @param notify  send change event?
     */
    void setDefaultCreateEntities(boolean create, boolean notify);

    // ITEM URL GENERATOR
    /**
     * Returns the URL generator for an item.
     *
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     *
     * @return The item URL generator.
     */
    CategoryURLGenerator getItemURLGenerator(int series, int item);

    /**
     * Returns the item URL generator for a series.
     *
     * @param series  the series index (zero-based).
     *
     * @return The URL generator.
     *
     * @see #setSeriesItemURLGenerator(int, CategoryURLGenerator)
     */
    CategoryURLGenerator getSeriesItemURLGenerator(int series);

    /**
     * Sets the item URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator);

    /**
     * Sets the item URL generator for a series and sends a
     * {@link RendererChangeEvent} to all registered listeners if requested.
     *
     * @param series  the series index (zero-based).
     * @param generator  the generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getSeriesItemURLGenerator(int)
     */
    void setSeriesItemURLGenerator(int series, CategoryURLGenerator generator, boolean notify);

    /**
     * Returns the default item URL generator.
     *
     * @return The item URL generator (possibly {@code null}).
     *
     * @see #setDefaultItemURLGenerator(CategoryURLGenerator)
     */
    CategoryURLGenerator getDefaultItemURLGenerator();

    /**
     * Sets the default item URL generator and sends a {@link RendererChangeEvent}
     * to all registered listeners.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     *
     * @see #getDefaultItemURLGenerator()
     */
    void setDefaultItemURLGenerator(CategoryURLGenerator generator);

    /**
     * Sets the default item URL generator and sends a {@link RendererChangeEvent}
     * to all registered listeners if requested.
     *
     * @param generator  the item URL generator ({@code null} permitted).
     * @param notify  send change event?
     *
     * @see #getDefaultItemURLGenerator()
     */
    void setDefaultItemURLGenerator(CategoryURLGenerator generator, boolean notify);

    /**
     * Returns a legend item for a series.  This method can return
     * {@code null}, in which case the series will have no entry in the
     * legend.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series (zero-based index).
     *
     * @return The legend item (possibly {@code null}).
     */
    LegendItem getLegendItem(int datasetIndex, int series);

    /**
     * Draws a background for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    void drawBackground(Graphics2D g2, CategoryPlot<?, ?> plot, Rectangle2D dataArea);

    /**
     * Draws an outline for the data area.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the data area.
     */
    void drawOutline(Graphics2D g2, CategoryPlot<?, ?> plot, Rectangle2D dataArea);

    /**
     * Draws a single data item.
     *
     * @param g2  the graphics device.
     * @param state  state information for one chart.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     * @param pass  the pass index.
     */
    void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot<?, ?> plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset<?, ?> dataset, int row, int column, int pass);

    /**
     * Draws a grid line against the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param dataArea  the area for plotting data.
     * @param value  the value.
     */
    void drawDomainGridline(Graphics2D g2, CategoryPlot<?, ?> plot, Rectangle2D dataArea, double value);

    /**
     * Draws a grid line against the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param dataArea  the area for plotting data.
     * @param value  the value.
     * @param paint  the paint ({@code null} not permitted).
     * @param stroke  the line stroke ({@code null} not permitted).
     */
    void drawRangeLine(Graphics2D g2, CategoryPlot<?, ?> plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke);

    /**
     * Draws a line (or some other marker) to indicate a particular category on
     * the domain axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the category axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data.
     *
     * @see #drawRangeMarker(Graphics2D, CategoryPlot, ValueAxis, Marker,
     *     Rectangle2D)
     */
    void drawDomainMarker(Graphics2D g2, CategoryPlot<?, ?> plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea);

    /**
     * Draws a line (or some other marker) to indicate a particular value on
     * the range axis.
     *
     * @param g2  the graphics device.
     * @param plot  the plot.
     * @param axis  the value axis.
     * @param marker  the marker.
     * @param dataArea  the area for plotting data.
     *
     * @see #drawDomainMarker(Graphics2D, CategoryPlot, CategoryAxis,
     *     CategoryMarker, Rectangle2D)
     */
    void drawRangeMarker(Graphics2D g2, CategoryPlot<?, ?> plot, ValueAxis axis, Marker marker, Rectangle2D dataArea);

    /**
     * Returns the Java2D coordinate for the middle of the specified data item.
     *
     * @param rowKey  the row key.
     * @param columnKey  the column key.
     * @param dataset  the dataset.
     * @param axis  the axis.
     * @param area  the data area.
     * @param edge  the edge along which the axis lies.
     *
     * @return The Java2D coordinate for the middle of the item.
     */
    double getItemMiddle(Comparable<?> rowKey, Comparable<?> columnKey, CategoryDataset<?, ?> dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge);
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
 * CrosshairOverlay.java
 * ---------------------
 * (C) Copyright 2011-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   John Matthews, Michal Wozniak;
 *
 */
/**
 * An overlay for a {@link ChartPanel} that draws crosshairs on a chart.  If
 * you are using the JavaFX extensions for JFreeChart, then you should use
 * the {@code CrosshairOverlayFX} class.
 */
public class CrosshairOverlay extends AbstractOverlay implements Overlay, PropertyChangeListener, PublicCloneable, Cloneable, Serializable {

    /**
     * Storage for the crosshairs along the x-axis.
     */
    protected List<Crosshair> xCrosshairs;

    /**
     * Storage for the crosshairs along the y-axis.
     */
    protected List<Crosshair> yCrosshairs;

    /**
     * Creates a new overlay that initially contains no crosshairs.
     */
    public CrosshairOverlay() {
        super();
        this.xCrosshairs = new ArrayList<>();
        this.yCrosshairs = new ArrayList<>();
    }

    /**
     * Adds a crosshair against the domain axis (x-axis) and sends an
     * {@link OverlayChangeEvent} to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #removeDomainCrosshair(org.jfree.chart.plot.Crosshair)
     * @see #addRangeCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void addDomainCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        this.xCrosshairs.add(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    /**
     * Removes a domain axis crosshair and sends an {@link OverlayChangeEvent}
     * to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #addDomainCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void removeDomainCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        if (this.xCrosshairs.remove(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    /**
     * Clears all the domain crosshairs from the overlay and sends an
     * {@link OverlayChangeEvent} to all registered listeners (unless there
     * were no crosshairs to begin with).
     */
    public void clearDomainCrosshairs() {
        if (this.xCrosshairs.isEmpty()) {
            // nothing to do - avoids firing change event
            return;
        }
        for (Crosshair c : getDomainCrosshairs()) {
            this.xCrosshairs.remove(c);
            c.removePropertyChangeListener(this);
        }
        fireOverlayChanged();
    }

    /**
     * Returns a new list containing the domain crosshairs for this overlay.
     *
     * @return A list of crosshairs.
     */
    public List<Crosshair> getDomainCrosshairs() {
        return new ArrayList<>(this.xCrosshairs);
    }

    /**
     * Adds a crosshair against the range axis and sends an
     * {@link OverlayChangeEvent} to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     */
    public void addRangeCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        this.yCrosshairs.add(crosshair);
        crosshair.addPropertyChangeListener(this);
        fireOverlayChanged();
    }

    /**
     * Removes a range axis crosshair and sends an {@link OverlayChangeEvent}
     * to all registered listeners.
     *
     * @param crosshair  the crosshair ({@code null} not permitted).
     *
     * @see #addRangeCrosshair(org.jfree.chart.plot.Crosshair)
     */
    public void removeRangeCrosshair(Crosshair crosshair) {
        Args.nullNotPermitted(crosshair, "crosshair");
        if (this.yCrosshairs.remove(crosshair)) {
            crosshair.removePropertyChangeListener(this);
            fireOverlayChanged();
        }
    }

    /**
     * Clears all the range crosshairs from the overlay and sends an
     * {@link OverlayChangeEvent} to all registered listeners (unless there
     * were no crosshairs to begin with).
     */
    public void clearRangeCrosshairs() {
        if (this.yCrosshairs.isEmpty()) {
            // nothing to do - avoids change notification
            return;
        }
        for (Crosshair c : getRangeCrosshairs()) {
            this.yCrosshairs.remove(c);
            c.removePropertyChangeListener(this);
        }
        fireOverlayChanged();
    }

    /**
     * Returns a new list containing the range crosshairs for this overlay.
     *
     * @return A list of crosshairs.
     */
    public List<Crosshair> getRangeCrosshairs() {
        return new ArrayList<>(this.yCrosshairs);
    }

    /**
     * Receives a property change event (typically a change in one of the
     * crosshairs).
     *
     * @param e  the event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        fireOverlayChanged();
    }

    /**
     * Renders the crosshairs in the overlay on top of the chart that has just
     * been rendered in the specified {@code chartPanel}.  This method is
     * called by the JFreeChart framework, you won't normally call it from
     * user code.
     *
     * @param g2  the graphics target.
     * @param chartPanel  the chart panel.
     */
    @Override
    public void paintOverlay(Graphics2D g2, ChartPanel chartPanel) {
        Shape savedClip = g2.getClip();
        Rectangle2D dataArea = chartPanel.getScreenDataArea();
        g2.clip(dataArea);
        JFreeChart chart = chartPanel.getChart();
        XYPlot plot = (XYPlot) chart.getPlot();
        ValueAxis xAxis = plot.getDomainAxis();
        RectangleEdge xAxisEdge = plot.getDomainAxisEdge();
        for (Crosshair ch : getDomainCrosshairs()) {
            if (ch.isVisible()) {
                double x = ch.getValue();
                double xx = xAxis.valueToJava2D(x, dataArea, xAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawVerticalCrosshair(g2, dataArea, xx, ch);
                } else {
                    drawHorizontalCrosshair(g2, dataArea, xx, ch);
                }
            }
        }
        ValueAxis yAxis = plot.getRangeAxis();
        RectangleEdge yAxisEdge = plot.getRangeAxisEdge();
        for (Crosshair ch : getRangeCrosshairs()) {
            if (ch.isVisible()) {
                double y = ch.getValue();
                double yy = yAxis.valueToJava2D(y, dataArea, yAxisEdge);
                if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                    drawHorizontalCrosshair(g2, dataArea, yy, ch);
                } else {
                    drawVerticalCrosshair(g2, dataArea, yy, ch);
                }
            }
        }
        g2.setClip(savedClip);
    }

    /**
     * Draws a crosshair horizontally across the plot.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param y  the y-value in Java2D space.
     * @param crosshair  the crosshair.
     */
    protected void drawHorizontalCrosshair(Graphics2D g2, Rectangle2D dataArea, double y, Crosshair crosshair) {
        if (y >= dataArea.getMinY() && y <= dataArea.getMaxY()) {
            Line2D line = new Line2D.Double(dataArea.getMinX(), y, dataArea.getMaxX(), y);
            Paint savedPaint = g2.getPaint();
            Stroke savedStroke = g2.getStroke();
            g2.setPaint(crosshair.getPaint());
            g2.setStroke(crosshair.getStroke());
            g2.draw(line);
            if (crosshair.isLabelVisible()) {
                String label = crosshair.getLabelGenerator().generateLabel(crosshair);
                if (label != null && !label.isEmpty()) {
                    Font savedFont = g2.getFont();
                    g2.setFont(crosshair.getLabelFont());
                    RectangleAnchor anchor = crosshair.getLabelAnchor();
                    RectangleInsets padding = crosshair.getLabelPadding();
                    Point2D pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                    float xx = (float) pt.getX();
                    float yy = (float) pt.getY();
                    TextAnchor alignPt = textAlignPtForLabelAnchorH(anchor);
                    Shape hotspot = TextUtils.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                    hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    if (!dataArea.contains(hotspot.getBounds2D())) {
                        anchor = flipAnchorV(anchor);
                        pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                        xx = (float) pt.getX();
                        yy = (float) pt.getY();
                        if (anchor == RectangleAnchor.CENTER || alignPt.isHalfAscent()) {
                            double labelHeight = hotspot.getBounds2D().getHeight();
                            double minY = dataArea.getY() + (labelHeight + padding.getTop() - padding.getBottom()) / 2.0;
                            double maxY = dataArea.getY() + dataArea.getHeight() - (labelHeight + padding.getBottom() - padding.getTop()) / 2.0;
                            if (yy < minY) {
                                yy = (float) (minY);
                            } else if (yy > maxY) {
                                yy = (float) (maxY);
                            }
                        }
                        alignPt = textAlignPtForLabelAnchorH(anchor);
                        hotspot = TextUtils.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                        hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    }
                    g2.setPaint(crosshair.getLabelBackgroundPaint());
                    g2.fill(hotspot);
                    if (crosshair.isLabelOutlineVisible()) {
                        g2.setPaint(crosshair.getLabelOutlinePaint());
                        g2.setStroke(crosshair.getLabelOutlineStroke());
                        g2.draw(hotspot);
                    }
                    g2.setPaint(crosshair.getLabelPaint());
                    TextUtils.drawAlignedString(label, g2, xx, yy, alignPt);
                    g2.setFont(savedFont);
                }
            }
            g2.setPaint(savedPaint);
            g2.setStroke(savedStroke);
        }
    }

    /**
     * Draws a crosshair vertically on the plot.
     *
     * @param g2  the graphics target.
     * @param dataArea  the data area.
     * @param x  the x-value in Java2D space.
     * @param crosshair  the crosshair.
     */
    protected void drawVerticalCrosshair(Graphics2D g2, Rectangle2D dataArea, double x, Crosshair crosshair) {
        if (x >= dataArea.getMinX() && x <= dataArea.getMaxX()) {
            Line2D line = new Line2D.Double(x, dataArea.getMinY(), x, dataArea.getMaxY());
            Paint savedPaint = g2.getPaint();
            Stroke savedStroke = g2.getStroke();
            g2.setPaint(crosshair.getPaint());
            g2.setStroke(crosshair.getStroke());
            g2.draw(line);
            if (crosshair.isLabelVisible()) {
                String label = crosshair.getLabelGenerator().generateLabel(crosshair);
                if (label != null && !label.isEmpty()) {
                    Font savedFont = g2.getFont();
                    g2.setFont(crosshair.getLabelFont());
                    RectangleAnchor anchor = crosshair.getLabelAnchor();
                    RectangleInsets padding = crosshair.getLabelPadding();
                    Point2D pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                    float xx = (float) pt.getX();
                    float yy = (float) pt.getY();
                    TextAnchor alignPt = textAlignPtForLabelAnchorV(anchor);
                    Shape hotspot = TextUtils.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                    hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    if (!dataArea.contains(hotspot.getBounds2D())) {
                        anchor = flipAnchorH(anchor);
                        pt = calculateLabelPoint(line, anchor, crosshair.getLabelXOffset(), crosshair.getLabelYOffset(), padding);
                        xx = (float) pt.getX();
                        yy = (float) pt.getY();
                        if (alignPt.isHorizontalCenter()) {
                            double labelWidth = hotspot.getBounds2D().getWidth();
                            double minX = dataArea.getX() + (labelWidth + padding.getLeft() - padding.getRight()) / 2.0;
                            double maxX = dataArea.getX() + dataArea.getWidth() - (labelWidth + padding.getRight() - padding.getLeft()) / 2.0;
                            if (xx < minX) {
                                xx = (float) (minX);
                            } else if (xx > maxX) {
                                xx = (float) (maxX);
                            }
                        }
                        alignPt = textAlignPtForLabelAnchorV(anchor);
                        hotspot = TextUtils.calculateRotatedStringBounds(label, g2, xx, yy, alignPt, 0.0, TextAnchor.CENTER);
                        hotspot = padding.createOutsetRectangle(hotspot.getBounds2D());
                    }
                    g2.setPaint(crosshair.getLabelBackgroundPaint());
                    g2.fill(hotspot);
                    if (crosshair.isLabelOutlineVisible()) {
                        g2.setPaint(crosshair.getLabelOutlinePaint());
                        g2.setStroke(crosshair.getLabelOutlineStroke());
                        g2.draw(hotspot);
                    }
                    g2.setPaint(crosshair.getLabelPaint());
                    TextUtils.drawAlignedString(label, g2, xx, yy, alignPt);
                    g2.setFont(savedFont);
                }
            }
            g2.setPaint(savedPaint);
            g2.setStroke(savedStroke);
        }
    }

    /**
     * Calculates the anchor point for a label.
     *
     * @param line  the line for the crosshair.
     * @param anchor  the anchor point.
     * @param deltaX  the x-offset.
     * @param deltaY  the y-offset.
     * @param padding the label padding
     *
     * @return The anchor point.
     */
    private Point2D calculateLabelPoint(Line2D line, RectangleAnchor anchor, double deltaX, double deltaY, RectangleInsets padding) {
        double x, y;
        boolean left = (anchor == RectangleAnchor.BOTTOM_LEFT || anchor == RectangleAnchor.LEFT || anchor == RectangleAnchor.TOP_LEFT);
        boolean right = (anchor == RectangleAnchor.BOTTOM_RIGHT || anchor == RectangleAnchor.RIGHT || anchor == RectangleAnchor.TOP_RIGHT);
        boolean top = (anchor == RectangleAnchor.TOP_LEFT || anchor == RectangleAnchor.TOP || anchor == RectangleAnchor.TOP_RIGHT);
        boolean bottom = (anchor == RectangleAnchor.BOTTOM_LEFT || anchor == RectangleAnchor.BOTTOM || anchor == RectangleAnchor.BOTTOM_RIGHT);
        Rectangle rect = line.getBounds();
        // we expect the line to be vertical or horizontal
        if (line.getX1() == line.getX2()) {
            // vertical
            x = line.getX1();
            y = (line.getY1() + line.getY2()) / 2.0;
            if (left) {
                x = x - deltaX - padding.getRight();
            } else if (right) {
                x = x + deltaX + padding.getLeft();
            } else {
                x = x + (padding.getLeft() - padding.getRight()) / 2.0;
            }
            if (top) {
                y = Math.min(line.getY1(), line.getY2()) + deltaY + padding.getTop();
            } else if (bottom) {
                y = Math.max(line.getY1(), line.getY2()) - deltaY - padding.getBottom();
            } else {
                y = y + (padding.getTop() - padding.getBottom()) / 2.0;
            }
        } else {
            // horizontal
            x = (line.getX1() + line.getX2()) / 2.0;
            y = line.getY1();
            if (left) {
                x = Math.min(line.getX1(), line.getX2()) + deltaX + padding.getLeft();
            } else if (right) {
                x = Math.max(line.getX1(), line.getX2()) - deltaX - padding.getRight();
            } else {
                x = x + (padding.getLeft() - padding.getRight()) / 2.0;
            }
            if (top) {
                y = y - deltaY - padding.getBottom();
            } else if (bottom) {
                y = y + deltaY + padding.getTop();
            } else {
                y = y + (padding.getTop() - padding.getBottom()) / 2.0;
            }
        }
        return new Point2D.Double(x, y);
    }

    /**
     * Returns the text anchor that is used to align a label to its anchor
     * point.
     *
     * @param anchor  the anchor.
     *
     * @return The text alignment point.
     */
    private TextAnchor textAlignPtForLabelAnchorV(RectangleAnchor anchor) {
        TextAnchor result = TextAnchor.CENTER;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = TextAnchor.TOP_RIGHT;
        } else if (anchor.equals(RectangleAnchor.TOP)) {
            result = TextAnchor.TOP_CENTER;
        } else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = TextAnchor.TOP_LEFT;
        } else if (anchor.equals(RectangleAnchor.LEFT)) {
            result = TextAnchor.HALF_ASCENT_RIGHT;
        } else if (anchor.equals(RectangleAnchor.RIGHT)) {
            result = TextAnchor.HALF_ASCENT_LEFT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = TextAnchor.BOTTOM_RIGHT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM)) {
            result = TextAnchor.BOTTOM_CENTER;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = TextAnchor.BOTTOM_LEFT;
        }
        return result;
    }

    /**
     * Returns the text anchor that is used to align a label to its anchor
     * point.
     *
     * @param anchor  the anchor.
     *
     * @return The text alignment point.
     */
    private TextAnchor textAlignPtForLabelAnchorH(RectangleAnchor anchor) {
        TextAnchor result = TextAnchor.CENTER;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = TextAnchor.BOTTOM_LEFT;
        } else if (anchor.equals(RectangleAnchor.TOP)) {
            result = TextAnchor.BOTTOM_CENTER;
        } else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = TextAnchor.BOTTOM_RIGHT;
        } else if (anchor.equals(RectangleAnchor.LEFT)) {
            result = TextAnchor.HALF_ASCENT_LEFT;
        } else if (anchor.equals(RectangleAnchor.RIGHT)) {
            result = TextAnchor.HALF_ASCENT_RIGHT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = TextAnchor.TOP_LEFT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM)) {
            result = TextAnchor.TOP_CENTER;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = TextAnchor.TOP_RIGHT;
        }
        return result;
    }

    private RectangleAnchor flipAnchorH(RectangleAnchor anchor) {
        RectangleAnchor result = anchor;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = RectangleAnchor.TOP_RIGHT;
        } else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = RectangleAnchor.TOP_LEFT;
        } else if (anchor.equals(RectangleAnchor.LEFT)) {
            result = RectangleAnchor.RIGHT;
        } else if (anchor.equals(RectangleAnchor.RIGHT)) {
            result = RectangleAnchor.LEFT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = RectangleAnchor.BOTTOM_RIGHT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = RectangleAnchor.BOTTOM_LEFT;
        }
        return result;
    }

    private RectangleAnchor flipAnchorV(RectangleAnchor anchor) {
        RectangleAnchor result = anchor;
        if (anchor.equals(RectangleAnchor.TOP_LEFT)) {
            result = RectangleAnchor.BOTTOM_LEFT;
        } else if (anchor.equals(RectangleAnchor.TOP_RIGHT)) {
            result = RectangleAnchor.BOTTOM_RIGHT;
        } else if (anchor.equals(RectangleAnchor.TOP)) {
            result = RectangleAnchor.BOTTOM;
        } else if (anchor.equals(RectangleAnchor.BOTTOM)) {
            result = RectangleAnchor.TOP;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_LEFT)) {
            result = RectangleAnchor.TOP_LEFT;
        } else if (anchor.equals(RectangleAnchor.BOTTOM_RIGHT)) {
            result = RectangleAnchor.TOP_RIGHT;
        }
        return result;
    }

    /**
     * Tests this overlay for equality with an arbitrary object.
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
        if (!(obj instanceof CrosshairOverlay)) {
            return false;
        }
        CrosshairOverlay that = (CrosshairOverlay) obj;
        if (!this.xCrosshairs.equals(that.xCrosshairs)) {
            return false;
        }
        if (!this.yCrosshairs.equals(that.yCrosshairs)) {
            return false;
        }
        return true;
    }

    /**
     * Returns a clone of this instance.
     *
     * @return A clone of this instance.
     *
     * @throws java.lang.CloneNotSupportedException if there is some problem
     *     with the cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        CrosshairOverlay clone = (CrosshairOverlay) super.clone();
        clone.xCrosshairs = (List) CloneUtils.cloneList(this.xCrosshairs);
        clone.yCrosshairs = (List) CloneUtils.cloneList(this.yCrosshairs);
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
 * -------------
 * DateAxis.java
 * -------------
 * (C) Copyright 2000-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Jonathan Nash;
 *                   David Li;
 *                   Michael Rauch;
 *                   Bill Kelemen;
 *                   Pawel Pabis;
 *                   Chris Boek;
 *                   Peter Kolb (patches 1934255 and 2603321);
 *                   Andrew Mickish (patch 1870189);
 *                   Fawad Halim (bug 2201869);
 *
 */
/**
 * The base class for axes that display dates.  You will find it easier to
 * understand how this axis works if you bear in mind that it really
 * displays/measures integer (or long) data, where the integers are
 * milliseconds since midnight, 1-Jan-1970.  When displaying tick labels, the
 * millisecond values are converted back to dates using a {@code DateFormat}
 * instance.
 * <P>
 * You can also create a {@link org.jfree.chart.axis.Timeline} and supply in
 * the constructor to create an axis that only contains certain domain values.
 * For example, this allows you to create a date axis that only contains
 * working days.
 */
public class DateAxis extends ValueAxis implements Cloneable, Serializable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -1013460999649007604L;

    /**
     * The default axis range.
     */
    public static final DateRange DEFAULT_DATE_RANGE = new DateRange();

    /**
     * The default minimum auto range size.
     */
    public static final double DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS = 2.0;

    /**
     * The default anchor date.
     */
    public static final Date DEFAULT_ANCHOR_DATE = new Date();

    /**
     * The current tick unit.
     */
    private DateTickUnit tickUnit;

    /**
     * The override date format.
     */
    private DateFormat dateFormatOverride;

    /**
     * Tick marks can be displayed at the start or the middle of the time
     * period.
     */
    private DateTickMarkPosition tickMarkPosition = DateTickMarkPosition.START;

    /**
     * A timeline that includes all milliseconds (as defined by
     * {@code java.util.Date}) in the real time line.
     */
    private static class DefaultTimeline implements Timeline, Serializable {

        /**
         * Converts a millisecond into a timeline value.
         *
         * @param millisecond  the millisecond.
         *
         * @return The timeline value.
         */
        @Override
        public long toTimelineValue(long millisecond) {
            return millisecond;
        }

        /**
         * Converts a date into a timeline value.
         *
         * @param date  the domain value.
         *
         * @return The timeline value.
         */
        @Override
        public long toTimelineValue(Date date) {
            return date.getTime();
        }

        /**
         * Converts a timeline value into a millisecond (as encoded by
         * {@code java.util.Date}).
         *
         * @param value  the value.
         *
         * @return The millisecond.
         */
        @Override
        public long toMillisecond(long value) {
            return value;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value.
         *
         * @param millisecond  the millisecond.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainValue(long millisecond) {
            return true;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value.
         *
         * @param date  the date.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainValue(Date date) {
            return true;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value range.
         *
         * @param from  the start value.
         * @param to  the end value.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainRange(long from, long to) {
            return true;
        }

        /**
         * Returns {@code true} if the timeline includes the specified
         * domain value range.
         *
         * @param from  the start date.
         * @param to  the end date.
         *
         * @return {@code true}.
         */
        @Override
        public boolean containsDomainRange(Date from, Date to) {
            return true;
        }

        /**
         * Tests an object for equality with this instance.
         *
         * @param object  the object.
         *
         * @return A boolean.
         */
        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object == this) {
                return true;
            }
            if (object instanceof DefaultTimeline) {
                return true;
            }
            return false;
        }
    }

    /**
     * A static default timeline shared by all standard DateAxis
     */
    private static final Timeline DEFAULT_TIMELINE = new DefaultTimeline();

    /**
     * The time zone for the axis.
     */
    private TimeZone timeZone;

    /**
     * The locale for the axis ({@code null} is not permitted).
     */
    private Locale locale;

    /**
     * Our underlying timeline.
     */
    private Timeline timeline;

    /**
     * Creates a date axis with no label.
     */
    public DateAxis() {
        this(null);
    }

    /**
     * Creates a date axis with the specified label.
     *
     * @param label  the axis label ({@code null} permitted).
     */
    public DateAxis(String label) {
        this(label, TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Creates a date axis.
     *
     * @param label  the axis label ({@code null} permitted).
     * @param zone  the time zone.
     * @param locale  the locale ({@code null} not permitted).
     */
    public DateAxis(String label, TimeZone zone, Locale locale) {
        super(label, DateAxis.createStandardDateTickUnits(zone, locale));
        this.tickUnit = new DateTickUnit(DateTickUnitType.DAY, 1, new SimpleDateFormat());
        setAutoRangeMinimumSize(DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS);
        setRange(DEFAULT_DATE_RANGE, false, false);
        this.dateFormatOverride = null;
        this.timeZone = zone;
        this.locale = locale;
        this.timeline = DEFAULT_TIMELINE;
    }

    /**
     * Returns the time zone for the axis.
     *
     * @return The time zone (never {@code null}).
     *
     * @see #setTimeZone(TimeZone)
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * Sets the time zone for the axis and sends an {@link AxisChangeEvent} to
     * all registered listeners.
     *
     * @param zone  the time zone ({@code null} not permitted).
     *
     * @see #getTimeZone()
     */
    public void setTimeZone(TimeZone zone) {
        Args.nullNotPermitted(zone, "zone");
        this.timeZone = zone;
        setStandardTickUnits(createStandardDateTickUnits(zone, this.locale));
        fireChangeEvent();
    }

    /**
     * Returns the locale for this axis.
     *
     * @return The locale (never {@code null}).
     */
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * Sets the locale for the axis and sends a change event to all registered
     * listeners.
     *
     * @param locale  the new locale ({@code null} not permitted).
     */
    public void setLocale(Locale locale) {
        Args.nullNotPermitted(locale, "locale");
        this.locale = locale;
        setStandardTickUnits(createStandardDateTickUnits(this.timeZone, this.locale));
        fireChangeEvent();
    }

    /**
     * Returns the underlying timeline used by this axis.
     *
     * @return The timeline.
     */
    public Timeline getTimeline() {
        return this.timeline;
    }

    /**
     * Sets the underlying timeline to use for this axis.  If the timeline is
     * changed, an {@link AxisChangeEvent} is sent to all registered listeners.
     *
     * @param timeline  the timeline.
     */
    public void setTimeline(Timeline timeline) {
        if (this.timeline != timeline) {
            this.timeline = timeline;
            fireChangeEvent();
        }
    }

    /**
     * Returns the tick unit for the axis.
     * <p>
     * Note: if the {@code autoTickUnitSelection} flag is
     * {@code true} the tick unit may be changed while the axis is being
     * drawn, so in that case the return value from this method may be
     * irrelevant if the method is called before the axis has been drawn.
     *
     * @return The tick unit (possibly {@code null}).
     *
     * @see #setTickUnit(DateTickUnit)
     * @see ValueAxis#isAutoTickUnitSelection()
     */
    public DateTickUnit getTickUnit() {
        return this.tickUnit;
    }

    /**
     * Sets the tick unit for the axis.  The auto-tick-unit-selection flag is
     * set to {@code false}, and registered listeners are notified that
     * the axis has been changed.
     *
     * @param unit  the tick unit.
     *
     * @see #getTickUnit()
     * @see #setTickUnit(DateTickUnit, boolean, boolean)
     */
    public void setTickUnit(DateTickUnit unit) {
        setTickUnit(unit, true, true);
    }

    /**
     * Sets the tick unit attribute and, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.
     *
     * @param unit  the new tick unit.
     * @param notify  notify registered listeners?
     * @param turnOffAutoSelection  turn off auto selection?
     *
     * @see #getTickUnit()
     */
    public void setTickUnit(DateTickUnit unit, boolean notify, boolean turnOffAutoSelection) {
        this.tickUnit = unit;
        if (turnOffAutoSelection) {
            setAutoTickUnitSelection(false, false);
        }
        if (notify) {
            fireChangeEvent();
        }
    }

    /**
     * Returns the date format override.  If this is non-null, then it will be
     * used to format the dates on the axis.
     *
     * @return The formatter (possibly {@code null}).
     */
    public DateFormat getDateFormatOverride() {
        return this.dateFormatOverride;
    }

    /**
     * Sets the date format override and sends an {@link AxisChangeEvent} to
     * all registered listeners.  If this is non-null, then it will be
     * used to format the dates on the axis.
     *
     * @param formatter  the date formatter ({@code null} permitted).
     */
    public void setDateFormatOverride(DateFormat formatter) {
        this.dateFormatOverride = formatter;
        fireChangeEvent();
    }

    /**
     * Sets the upper and lower bounds for the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect,
     * the auto-range flag is set to false.
     *
     * @param range  the new range ({@code null} not permitted).
     */
    @Override
    public void setRange(Range range) {
        setRange(range, true, true);
    }

    /**
     * Sets the range for the axis, if requested, sends an
     * {@link AxisChangeEvent} to all registered listeners.  As a side-effect,
     * the auto-range flag is set to {@code false} (optional).
     *
     * @param range  the range ({@code null} not permitted).
     * @param turnOffAutoRange  a flag that controls whether the auto
     *                          range is turned off.
     * @param notify  a flag that controls whether listeners are
     *                notified.
     */
    @Override
    public void setRange(Range range, boolean turnOffAutoRange, boolean notify) {
        Args.nullNotPermitted(range, "range");
        // usually the range will be a DateRange, but if it isn't do a
        // conversion...
        if (!(range instanceof DateRange)) {
            range = new DateRange(range);
        }
        super.setRange(range, turnOffAutoRange, notify);
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    public void setRange(Date lower, Date upper) {
        if (lower.getTime() >= upper.getTime()) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    /**
     * Sets the axis range and sends an {@link AxisChangeEvent} to all
     * registered listeners.
     *
     * @param lower  the lower bound for the axis.
     * @param upper  the upper bound for the axis.
     */
    @Override
    public void setRange(double lower, double upper) {
        if (lower >= upper) {
            throw new IllegalArgumentException("Requires 'lower' < 'upper'.");
        }
        setRange(new DateRange(lower, upper));
    }

    /**
     * Returns the earliest date visible on the axis.
     *
     * @return The date.
     *
     * @see #setMinimumDate(Date)
     * @see #getMaximumDate()
     */
    public Date getMinimumDate() {
        Date result;
        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getLowerDate();
        } else {
            result = new Date((long) range.getLowerBound());
        }
        return result;
    }

    /**
     * Sets the minimum date visible on the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  If
     * {@code date} is on or after the current maximum date for
     * the axis, the maximum date will be shifted to preserve the current
     * length of the axis.
     *
     * @param date  the date ({@code null} not permitted).
     *
     * @see #getMinimumDate()
     * @see #setMaximumDate(Date)
     */
    public void setMinimumDate(Date date) {
        Args.nullNotPermitted(date, "date");
        // check the new minimum date relative to the current maximum date
        Date maxDate = getMaximumDate();
        long maxMillis = maxDate.getTime();
        long newMinMillis = date.getTime();
        if (maxMillis <= newMinMillis) {
            Date oldMin = getMinimumDate();
            long length = maxMillis - oldMin.getTime();
            maxDate = new Date(newMinMillis + length);
        }
        setRange(new DateRange(date, maxDate), true, false);
        fireChangeEvent();
    }

    /**
     * Returns the latest date visible on the axis.
     *
     * @return The date.
     *
     * @see #setMaximumDate(Date)
     * @see #getMinimumDate()
     */
    public Date getMaximumDate() {
        Date result;
        Range range = getRange();
        if (range instanceof DateRange) {
            DateRange r = (DateRange) range;
            result = r.getUpperDate();
        } else {
            result = new Date((long) range.getUpperBound());
        }
        return result;
    }

    /**
     * Sets the maximum date visible on the axis and sends an
     * {@link AxisChangeEvent} to all registered listeners.  If
     * {@code maximumDate} is on or before the current minimum date for
     * the axis, the minimum date will be shifted to preserve the current
     * length of the axis.
     *
     * @param maximumDate  the date ({@code null} not permitted).
     *
     * @see #getMinimumDate()
     * @see #setMinimumDate(Date)
     */
    public void setMaximumDate(Date maximumDate) {
        Args.nullNotPermitted(maximumDate, "maximumDate");
        // check the new maximum date relative to the current minimum date
        Date minDate = getMinimumDate();
        long minMillis = minDate.getTime();
        long newMaxMillis = maximumDate.getTime();
        if (minMillis >= newMaxMillis) {
            Date oldMax = getMaximumDate();
            long length = oldMax.getTime() - minMillis;
            minDate = new Date(newMaxMillis - length);
        }
        setRange(new DateRange(minDate, maximumDate), true, false);
        fireChangeEvent();
    }

    /**
     * Returns the tick mark position (start, middle or end of the time period).
     *
     * @return The position (never {@code null}).
     */
    public DateTickMarkPosition getTickMarkPosition() {
        return this.tickMarkPosition;
    }

    /**
     * Sets the tick mark position (start, middle or end of the time period)
     * and sends an {@link AxisChangeEvent} to all registered listeners.
     *
     * @param position  the position ({@code null} not permitted).
     */
    public void setTickMarkPosition(DateTickMarkPosition position) {
        Args.nullNotPermitted(position, "position");
        this.tickMarkPosition = position;
        fireChangeEvent();
    }

    /**
     * Configures the axis to work with the specified plot.  If the axis has
     * auto-scaling, then sets the maximum and minimum values.
     */
    @Override
    public void configure() {
        if (isAutoRange()) {
            autoAdjustRange();
        }
    }

    /**
     * Returns {@code true} if the axis hides this value, and
     * {@code false} otherwise.
     *
     * @param millis  the data value.
     *
     * @return A value.
     */
    public boolean isHiddenValue(long millis) {
        return (!this.timeline.containsDomainValue(new Date(millis)));
    }

    /**
     * Translates the data value to the display coordinates (Java 2D User Space)
     * of the chart.
     *
     * @param value  the date to be plotted.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return The coordinate corresponding to the supplied data value.
     */
    @Override
    public double valueToJava2D(double value, Rectangle2D area, RectangleEdge edge) {
        value = this.timeline.toTimelineValue((long) value);
        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double result = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            double minX = area.getX();
            double maxX = area.getMaxX();
            if (isInverted()) {
                result = maxX + ((value - axisMin) / (axisMax - axisMin)) * (minX - maxX);
            } else {
                result = minX + ((value - axisMin) / (axisMax - axisMin)) * (maxX - minX);
            }
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            double minY = area.getMinY();
            double maxY = area.getMaxY();
            if (isInverted()) {
                result = minY + (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            } else {
                result = maxY - (((value - axisMin) / (axisMax - axisMin)) * (maxY - minY));
            }
        }
        return result;
    }

    /**
     * Translates a date to Java2D coordinates, based on the range displayed by
     * this axis for the specified data area.
     *
     * @param date  the date.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return The coordinate corresponding to the supplied date.
     */
    public double dateToJava2D(Date date, Rectangle2D area, RectangleEdge edge) {
        double value = date.getTime();
        return valueToJava2D(value, area, edge);
    }

    /**
     * Translates a Java2D coordinate into the corresponding data value.  To
     * perform this translation, you need to know the area used for plotting
     * data, and which edge the axis is located on.
     *
     * @param java2DValue  the coordinate in Java2D space.
     * @param area  the rectangle (in Java2D space) where the data is to be
     *              plotted.
     * @param edge  the axis location.
     *
     * @return A data value.
     */
    @Override
    public double java2DToValue(double java2DValue, Rectangle2D area, RectangleEdge edge) {
        DateRange range = (DateRange) getRange();
        double axisMin = this.timeline.toTimelineValue(range.getLowerMillis());
        double axisMax = this.timeline.toTimelineValue(range.getUpperMillis());
        double min = 0.0;
        double max = 0.0;
        if (RectangleEdge.isTopOrBottom(edge)) {
            min = area.getX();
            max = area.getMaxX();
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            min = area.getMaxY();
            max = area.getY();
        }
        double result;
        if (isInverted()) {
            result = axisMax - ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        } else {
            result = axisMin + ((java2DValue - min) / (max - min) * (axisMax - axisMin));
        }
        return this.timeline.toMillisecond((long) result);
    }

    /**
     * Calculates the value of the lowest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return The value of the lowest visible tick on the axis.
     */
    public Date calculateLowestVisibleTickValue(DateTickUnit unit) {
        return nextStandardDate(getMinimumDate(), unit);
    }

    /**
     * Calculates the value of the highest visible tick on the axis.
     *
     * @param unit  date unit to use.
     *
     * @return The value of the highest visible tick on the axis.
     */
    public Date calculateHighestVisibleTickValue(DateTickUnit unit) {
        return previousStandardDate(getMaximumDate(), unit);
    }

    /**
     * Returns the previous "standard" date, for a given date and tick unit.
     *
     * @param date  the reference date.
     * @param unit  the tick unit.
     *
     * @return The previous "standard" date.
     */
    protected Date previousStandardDate(Date date, DateTickUnit unit) {
        int milliseconds;
        int seconds;
        int minutes;
        int hours;
        int days;
        int months;
        int years;
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(date);
        int count = unit.getMultiple();
        int current = calendar.get(unit.getCalendarField());
        int value = count * (current / count);
        if (DateTickUnitType.MILLISECOND.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
            calendar.set(years, months, days, hours, minutes, seconds);
            calendar.set(Calendar.MILLISECOND, value);
            Date mm = calendar.getTime();
            if (mm.getTime() >= date.getTime()) {
                calendar.set(Calendar.MILLISECOND, value - count);
                mm = calendar.getTime();
            }
            return mm;
        } else if (DateTickUnitType.SECOND.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                milliseconds = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                milliseconds = 500;
            } else {
                milliseconds = 999;
            }
            calendar.set(Calendar.MILLISECOND, milliseconds);
            calendar.set(years, months, days, hours, minutes, value);
            Date dd = calendar.getTime();
            if (dd.getTime() >= date.getTime()) {
                calendar.set(Calendar.SECOND, value - count);
                dd = calendar.getTime();
            }
            return dd;
        } else if (DateTickUnitType.MINUTE.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            hours = calendar.get(Calendar.HOUR_OF_DAY);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                seconds = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                seconds = 30;
            } else {
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, days, hours, value, seconds);
            Date d0 = calendar.getTime();
            if (d0.getTime() >= date.getTime()) {
                calendar.set(Calendar.MINUTE, value - count);
                d0 = calendar.getTime();
            }
            return d0;
        } else if (DateTickUnitType.HOUR.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            days = calendar.get(Calendar.DATE);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                minutes = 0;
                seconds = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                minutes = 30;
                seconds = 0;
            } else {
                minutes = 59;
                seconds = 59;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, days, value, minutes, seconds);
            Date d1 = calendar.getTime();
            if (d1.getTime() >= date.getTime()) {
                calendar.set(Calendar.HOUR_OF_DAY, value - count);
                d1 = calendar.getTime();
            }
            return d1;
        } else if (DateTickUnitType.DAY.equals(unit.getUnitType())) {
            years = calendar.get(Calendar.YEAR);
            months = calendar.get(Calendar.MONTH);
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                hours = 0;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                hours = 12;
            } else {
                hours = 23;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, months, value, hours, 0, 0);
            // long result = calendar.getTimeInMillis();
            // won't work with JDK 1.3
            Date d2 = calendar.getTime();
            if (d2.getTime() >= date.getTime()) {
                calendar.set(Calendar.DATE, value - count);
                d2 = calendar.getTime();
            }
            return d2;
        } else if (DateTickUnitType.MONTH.equals(unit.getUnitType())) {
            value = count * ((current + 1) / count) - 1;
            years = calendar.get(Calendar.YEAR);
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(years, value, 1, 0, 0, 0);
            Month month = new Month(calendar.getTime(), this.timeZone, this.locale);
            Date standardDate = calculateDateForPosition(month, this.tickMarkPosition);
            long millis = standardDate.getTime();
            if (millis >= date.getTime()) {
                for (int i = 0; i < count; i++) {
                    month = (Month) month.previous();
                }
                // need to peg the month in case the time zone isn't the
                // default - see bug 2078057
                month.peg(Calendar.getInstance(this.timeZone));
                standardDate = calculateDateForPosition(month, this.tickMarkPosition);
            }
            return standardDate;
        } else if (DateTickUnitType.YEAR.equals(unit.getUnitType())) {
            if (this.tickMarkPosition == DateTickMarkPosition.START) {
                months = 0;
                days = 1;
            } else if (this.tickMarkPosition == DateTickMarkPosition.MIDDLE) {
                months = 6;
                days = 1;
            } else {
                months = 11;
                days = 31;
            }
            calendar.clear(Calendar.MILLISECOND);
            calendar.set(value, months, days, 0, 0, 0);
            Date d3 = calendar.getTime();
            if (d3.getTime() >= date.getTime()) {
                calendar.set(Calendar.YEAR, value - count);
                d3 = calendar.getTime();
            }
            return d3;
        }
        return null;
    }

    /**
     * Returns a {@link java.util.Date} corresponding to the specified position
     * within a {@link RegularTimePeriod}.
     *
     * @param period  the period.
     * @param position  the position ({@code null} not permitted).
     *
     * @return A date.
     */
    private Date calculateDateForPosition(RegularTimePeriod period, DateTickMarkPosition position) {
        Args.nullNotPermitted(period, "period");
        Date result = null;
        if (position == DateTickMarkPosition.START) {
            result = new Date(period.getFirstMillisecond());
        } else if (position == DateTickMarkPosition.MIDDLE) {
            result = new Date(period.getMiddleMillisecond());
        } else if (position == DateTickMarkPosition.END) {
            result = new Date(period.getLastMillisecond());
        }
        return result;
    }

    /**
     * Returns the first "standard" date (based on the specified field and
     * units).
     *
     * @param date  the reference date.
     * @param unit  the date tick unit.
     *
     * @return The next "standard" date.
     */
    protected Date nextStandardDate(Date date, DateTickUnit unit) {
        Date previous = previousStandardDate(date, unit);
        Calendar calendar = Calendar.getInstance(this.timeZone, this.locale);
        calendar.setTime(previous);
        calendar.add(unit.getCalendarField(), unit.getMultiple());
        return calendar.getTime();
    }

    /**
     * Returns a collection of standard date tick units that uses the default
     * time zone.  This collection will be used by default, but you are free
     * to create your own collection if you want to (see the
     * {@link ValueAxis#setStandardTickUnits(TickUnitSource)} method inherited
     * from the {@link ValueAxis} class).
     *
     * @return A collection of standard date tick units.
     */
    public static TickUnitSource createStandardDateTickUnits() {
        return createStandardDateTickUnits(TimeZone.getDefault(), Locale.getDefault());
    }

    /**
     * Returns a collection of standard date tick units.  This collection will
     * be used by default, but you are free to create your own collection if
     * you want to (see the
     * {@link ValueAxis#setStandardTickUnits(TickUnitSource)} method inherited
     * from the {@link ValueAxis} class).
     *
     * @param zone  the time zone ({@code null} not permitted).
     * @param locale  the locale ({@code null} not permitted).
     *
     * @return A collection of standard date tick units.
     */
    public static TickUnitSource createStandardDateTickUnits(TimeZone zone, Locale locale) {
        Args.nullNotPermitted(zone, "zone");
        Args.nullNotPermitted(locale, "locale");
        TickUnits units = new TickUnits();
        // date formatters
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss.SSS", locale);
        DateFormat f2 = new SimpleDateFormat("HH:mm:ss", locale);
        DateFormat f3 = new SimpleDateFormat("HH:mm", locale);
        DateFormat f4 = new SimpleDateFormat("d-MMM, HH:mm", locale);
        DateFormat f5 = new SimpleDateFormat("d-MMM", locale);
        DateFormat f6 = new SimpleDateFormat("MMM-yyyy", locale);
        DateFormat f7 = new SimpleDateFormat("yyyy", locale);
        f1.setTimeZone(zone);
        f2.setTimeZone(zone);
        f3.setTimeZone(zone);
        f4.setTimeZone(zone);
        f5.setTimeZone(zone);
        f6.setTimeZone(zone);
        f7.setTimeZone(zone);
        // milliseconds
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 5, DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 10, DateTickUnitType.MILLISECOND, 1, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 25, DateTickUnitType.MILLISECOND, 5, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 50, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 100, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 250, DateTickUnitType.MILLISECOND, 10, f1));
        units.add(new DateTickUnit(DateTickUnitType.MILLISECOND, 500, DateTickUnitType.MILLISECOND, 50, f1));
        // seconds
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 1, DateTickUnitType.MILLISECOND, 50, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 5, DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 10, DateTickUnitType.SECOND, 1, f2));
        units.add(new DateTickUnit(DateTickUnitType.SECOND, 30, DateTickUnitType.SECOND, 5, f2));
        // minutes
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 1, DateTickUnitType.SECOND, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 2, DateTickUnitType.SECOND, 10, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 5, DateTickUnitType.MINUTE, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 10, DateTickUnitType.MINUTE, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 15, DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 20, DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.MINUTE, 30, DateTickUnitType.MINUTE, 5, f3));
        // hours
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 1, DateTickUnitType.MINUTE, 5, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 2, DateTickUnitType.MINUTE, 10, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 4, DateTickUnitType.MINUTE, 30, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 6, DateTickUnitType.HOUR, 1, f3));
        units.add(new DateTickUnit(DateTickUnitType.HOUR, 12, DateTickUnitType.HOUR, 1, f4));
        // days
        units.add(new DateTickUnit(DateTickUnitType.DAY, 1, DateTickUnitType.HOUR, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 2, DateTickUnitType.HOUR, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 7, DateTickUnitType.DAY, 1, f5));
        units.add(new DateTickUnit(DateTickUnitType.DAY, 15, DateTickUnitType.DAY, 1, f5));
        // months
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 1, DateTickUnitType.DAY, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 2, DateTickUnitType.DAY, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 3, DateTickUnitType.MONTH, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 4, DateTickUnitType.MONTH, 1, f6));
        units.add(new DateTickUnit(DateTickUnitType.MONTH, 6, DateTickUnitType.MONTH, 1, f6));
        // years
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 1, DateTickUnitType.MONTH, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 2, DateTickUnitType.MONTH, 3, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 5, DateTickUnitType.YEAR, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 10, DateTickUnitType.YEAR, 1, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 25, DateTickUnitType.YEAR, 5, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 50, DateTickUnitType.YEAR, 10, f7));
        units.add(new DateTickUnit(DateTickUnitType.YEAR, 100, DateTickUnitType.YEAR, 20, f7));
        return units;
    }

    /**
     * Rescales the axis to ensure that all data is visible.
     */
    @Override
    protected void autoAdjustRange() {
        Plot plot = getPlot();
        if (plot == null) {
            // no plot, no data
            return;
        }
        if (plot instanceof ValueAxisPlot) {
            ValueAxisPlot vap = (ValueAxisPlot) plot;
            Range r = vap.getDataRange(this);
            if (r == null) {
                r = new DateRange();
            }
            long upper = this.timeline.toTimelineValue((long) r.getUpperBound());
            long lower;
            long fixedAutoRange = (long) getFixedAutoRange();
            if (fixedAutoRange > 0.0) {
                lower = upper - fixedAutoRange;
            } else {
                lower = this.timeline.toTimelineValue((long) r.getLowerBound());
                double range = upper - lower;
                long minRange = (long) getAutoRangeMinimumSize();
                if (range < minRange) {
                    long expand = (long) (minRange - range) / 2;
                    upper = upper + expand;
                    lower = lower - expand;
                }
                upper = upper + (long) (range * getUpperMargin());
                lower = lower - (long) (range * getLowerMargin());
            }
            upper = this.timeline.toMillisecond(upper);
            lower = this.timeline.toMillisecond(lower);
            DateRange dr = new DateRange(new Date(lower), new Date(upper));
            setRange(dr, false, false);
        }
    }

    /**
     * Selects an appropriate tick value for the axis.  The strategy is to
     * display as many ticks as possible (selected from an array of 'standard'
     * tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        if (RectangleEdge.isTopOrBottom(edge)) {
            selectHorizontalAutoTickUnit(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            selectVerticalAutoTickUnit(g2, dataArea, edge);
        }
    }

    /**
     * Selects an appropriate tick size for the axis.  The strategy is to
     * display as many ticks as possible (selected from a collection of
     * 'standard' tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area defined by the axes.
     * @param edge  the axis location.
     */
    protected void selectHorizontalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        double zero = valueToJava2D(0.0, dataArea, edge);
        double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());
        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
        double x1 = valueToJava2D(unit1.getSize(), dataArea, edge);
        double unit1Width = Math.abs(x1 - zero);
        // then extrapolate...
        double guess = (tickLabelWidth / unit1Width) * unit1.getSize();
        DateTickUnit unit2 = (DateTickUnit) tickUnits.getCeilingTickUnit(guess);
        double x2 = valueToJava2D(unit2.getSize(), dataArea, edge);
        double unit2Width = Math.abs(x2 - zero);
        tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
        if (tickLabelWidth > unit2Width) {
            unit2 = (DateTickUnit) tickUnits.getLargerTickUnit(unit2);
        }
        setTickUnit(unit2, false, false);
    }

    /**
     * Selects an appropriate tick size for the axis.  The strategy is to
     * display as many ticks as possible (selected from a collection of
     * 'standard' tick units) without the labels overlapping.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the axis location.
     */
    protected void selectVerticalAutoTickUnit(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        // start with the current tick unit...
        TickUnitSource tickUnits = getStandardTickUnits();
        double zero = valueToJava2D(0.0, dataArea, edge);
        // start with a unit that is at least 1/10th of the axis length
        double estimate1 = getRange().getLength() / 10.0;
        DateTickUnit candidate1 = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate1);
        double labelHeight1 = estimateMaximumTickLabelHeight(g2, candidate1);
        double y1 = valueToJava2D(candidate1.getSize(), dataArea, edge);
        double candidate1UnitHeight = Math.abs(y1 - zero);
        // now extrapolate based on label height and unit height...
        double estimate2 = (labelHeight1 / candidate1UnitHeight) * candidate1.getSize();
        DateTickUnit candidate2 = (DateTickUnit) tickUnits.getCeilingTickUnit(estimate2);
        double labelHeight2 = estimateMaximumTickLabelHeight(g2, candidate2);
        double y2 = valueToJava2D(candidate2.getSize(), dataArea, edge);
        double unit2Height = Math.abs(y2 - zero);
        // make final selection...
        DateTickUnit finalUnit;
        if (labelHeight2 < unit2Height) {
            finalUnit = candidate2;
        } else {
            finalUnit = (DateTickUnit) tickUnits.getLargerTickUnit(candidate2);
        }
        setTickUnit(finalUnit, false, false);
    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified
     * tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we
     * just look at two values: the lower bound and the upper bound for the
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     */
    private double estimateMaximumTickLabelWidth(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getLeft() + tickLabelInsets.getRight();
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of
            // the font)...
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr, upperStr;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }

    /**
     * Estimates the maximum width of the tick labels, assuming the specified
     * tick unit is used.
     * <P>
     * Rather than computing the string bounds of every tick on the axis, we
     * just look at two values: the lower bound and the upper bound for the
     * axis.  These two values will usually be representative.
     *
     * @param g2  the graphics device.
     * @param unit  the tick unit to use for calculation.
     *
     * @return The estimated maximum width of the tick labels.
     */
    private double estimateMaximumTickLabelHeight(Graphics2D g2, DateTickUnit unit) {
        RectangleInsets tickLabelInsets = getTickLabelInsets();
        double result = tickLabelInsets.getTop() + tickLabelInsets.getBottom();
        Font tickLabelFont = getTickLabelFont();
        FontRenderContext frc = g2.getFontRenderContext();
        LineMetrics lm = tickLabelFont.getLineMetrics("ABCxyz", frc);
        if (!isVerticalTickLabels()) {
            // all tick labels have the same width (equal to the height of
            // the font)...
            result += lm.getHeight();
        } else {
            // look at lower and upper bounds...
            DateRange range = (DateRange) getRange();
            Date lower = range.getLowerDate();
            Date upper = range.getUpperDate();
            String lowerStr, upperStr;
            DateFormat formatter = getDateFormatOverride();
            if (formatter != null) {
                lowerStr = formatter.format(lower);
                upperStr = formatter.format(upper);
            } else {
                lowerStr = unit.dateToString(lower);
                upperStr = unit.dateToString(upper);
            }
            FontMetrics fm = g2.getFontMetrics(tickLabelFont);
            double w1 = fm.stringWidth(lowerStr);
            double w2 = fm.stringWidth(upperStr);
            result += Math.max(w1, w2);
        }
        return result;
    }

    /**
     * Calculates the positions of the tick labels for the axis, storing the
     * results in the tick label list (ready for drawing).
     *
     * @param g2  the graphics device.
     * @param state  the axis state.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    @Override
    public List<? extends Tick> refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
        List<? extends Tick> result = null;
        if (RectangleEdge.isTopOrBottom(edge)) {
            result = refreshTicksHorizontal(g2, dataArea, edge);
        } else if (RectangleEdge.isLeftOrRight(edge)) {
            result = refreshTicksVertical(g2, dataArea, edge);
        }
        return result;
    }

    /**
     * Corrects the given tick date for the position setting.
     *
     * @param time  the tick date/time.
     * @param unit  the tick unit.
     * @param position  the tick position.
     *
     * @return The adjusted time.
     */
    private Date correctTickDateForPosition(Date time, DateTickUnit unit, DateTickMarkPosition position) {
        Date result = time;
        if (unit.getUnitType().equals(DateTickUnitType.MONTH)) {
            result = calculateDateForPosition(new Month(time, this.timeZone, this.locale), position);
        } else if (unit.getUnitType().equals(DateTickUnitType.YEAR)) {
            result = calculateDateForPosition(new Year(time, this.timeZone, this.locale), position);
        }
        return result;
    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the data is to be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List<? extends Tick> refreshTicksHorizontal(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List<DateTick> result = new ArrayList<>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        boolean hasRolled = false;
        while (tickDate.before(upperDate)) {
            // could add a flag to make the following correction optional...
            if (!hasRolled) {
                tickDate = correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            }
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - distance * minorTick / minorTickSpaces;
                if (minorTickTime > 0 && getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            if (!isHiddenValue(tickDate.getTime())) {
                // work out the value, label and position
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                } else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor, rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.CENTER_RIGHT;
                    rotationAnchor = TextAnchor.CENTER_RIGHT;
                    if (edge == RectangleEdge.TOP) {
                        angle = Math.PI / 2.0;
                    } else {
                        angle = -Math.PI / 2.0;
                    }
                } else {
                    if (edge == RectangleEdge.TOP) {
                        anchor = TextAnchor.BOTTOM_CENTER;
                        rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    } else {
                        anchor = TextAnchor.TOP_CENTER;
                        rotationAnchor = TextAnchor.TOP_CENTER;
                    }
                }
                DateTick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                hasRolled = false;
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    long minorTickTime = currentTickTime + (nextTickTime - currentTickTime) * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                        result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                    }
                }
            } else {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                hasRolled = true;
            }
        }
        return result;
    }

    /**
     * Recalculates the ticks for the date axis.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area in which the plot should be drawn.
     * @param edge  the location of the axis.
     *
     * @return A list of ticks.
     */
    protected List<? extends Tick> refreshTicksVertical(Graphics2D g2, Rectangle2D dataArea, RectangleEdge edge) {
        List<DateTick> result = new ArrayList<>();
        Font tickLabelFont = getTickLabelFont();
        g2.setFont(tickLabelFont);
        if (isAutoTickUnitSelection()) {
            selectAutoTickUnit(g2, dataArea, edge);
        }
        DateTickUnit unit = getTickUnit();
        Date tickDate = calculateLowestVisibleTickValue(unit);
        Date upperDate = getMaximumDate();
        boolean hasRolled = false;
        while (tickDate.before(upperDate)) {
            // could add a flag to make the following correction optional...
            if (!hasRolled) {
                tickDate = correctTickDateForPosition(tickDate, unit, this.tickMarkPosition);
            }
            long lowestTickTime = tickDate.getTime();
            long distance = unit.addToDate(tickDate, this.timeZone).getTime() - lowestTickTime;
            int minorTickSpaces = getMinorTickCount();
            if (minorTickSpaces <= 0) {
                minorTickSpaces = unit.getMinorTickCount();
            }
            for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                long minorTickTime = lowestTickTime - distance * minorTick / minorTickSpaces;
                if (minorTickTime > 0 && getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                    result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                }
            }
            if (!isHiddenValue(tickDate.getTime())) {
                // work out the value, label and position
                String tickLabel;
                DateFormat formatter = getDateFormatOverride();
                if (formatter != null) {
                    tickLabel = formatter.format(tickDate);
                } else {
                    tickLabel = this.tickUnit.dateToString(tickDate);
                }
                TextAnchor anchor, rotationAnchor;
                double angle = 0.0;
                if (isVerticalTickLabels()) {
                    anchor = TextAnchor.BOTTOM_CENTER;
                    rotationAnchor = TextAnchor.BOTTOM_CENTER;
                    if (edge == RectangleEdge.LEFT) {
                        angle = -Math.PI / 2.0;
                    } else {
                        angle = Math.PI / 2.0;
                    }
                } else {
                    if (edge == RectangleEdge.LEFT) {
                        anchor = TextAnchor.CENTER_RIGHT;
                        rotationAnchor = TextAnchor.CENTER_RIGHT;
                    } else {
                        anchor = TextAnchor.CENTER_LEFT;
                        rotationAnchor = TextAnchor.CENTER_LEFT;
                    }
                }
                DateTick tick = new DateTick(tickDate, tickLabel, anchor, rotationAnchor, angle);
                result.add(tick);
                hasRolled = false;
                long currentTickTime = tickDate.getTime();
                tickDate = unit.addToDate(tickDate, this.timeZone);
                long nextTickTime = tickDate.getTime();
                for (int minorTick = 1; minorTick < minorTickSpaces; minorTick++) {
                    long minorTickTime = currentTickTime + (nextTickTime - currentTickTime) * minorTick / minorTickSpaces;
                    if (getRange().contains(minorTickTime) && (!isHiddenValue(minorTickTime))) {
                        result.add(new DateTick(TickType.MINOR, new Date(minorTickTime), "", TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0));
                    }
                }
            } else {
                tickDate = unit.rollDate(tickDate, this.timeZone);
                hasRolled = true;
            }
        }
        return result;
    }

    /**
     * Draws the axis on a Java 2D graphics device (such as the screen or a
     * printer).
     *
     * @param g2  the graphics device ({@code null} not permitted).
     * @param cursor  the cursor location.
     * @param plotArea  the area within which the axes and data should be
     *                  drawn ({@code null} not permitted).
     * @param dataArea  the area within which the data should be drawn
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
            AxisState state = new AxisState(cursor);
            // even though the axis is not visible, we need to refresh ticks in
            // case the grid is being drawn...
            List ticks = refreshTicks(g2, state, dataArea, edge);
            state.setTicks(ticks);
            return state;
        }
        // draw the tick marks and labels...
        AxisState state = drawTickMarksAndLabels(g2, cursor, plotArea, dataArea, edge);
        // draw the axis label (note that 'state' is passed in *and*
        // returned)...
        if (getAttributedLabel() != null) {
            state = drawAttributedLabel(getAttributedLabel(), g2, plotArea, dataArea, edge, state);
        } else {
            state = drawLabel(getLabel(), g2, plotArea, dataArea, edge, state);
        }
        createAndAddEntity(cursor, state, dataArea, edge, plotState);
        return state;
    }

    /**
     * Zooms in on the current range (zoom-in stops once the axis length
     * reaches the equivalent of one millisecond).
     *
     * @param lowerPercent  the new lower bound.
     * @param upperPercent  the new upper bound.
     */
    @Override
    public void zoomRange(double lowerPercent, double upperPercent) {
        double start = this.timeline.toTimelineValue((long) getRange().getLowerBound());
        double end = this.timeline.toTimelineValue((long) getRange().getUpperBound());
        double length = end - start;
        Range adjusted;
        long adjStart, adjEnd;
        if (isInverted()) {
            adjStart = (long) (start + (length * (1 - upperPercent)));
            adjEnd = (long) (start + (length * (1 - lowerPercent)));
        } else {
            adjStart = (long) (start + length * lowerPercent);
            adjEnd = (long) (start + length * upperPercent);
        }
        // when zooming to sub-millisecond ranges, it can be the case that
        // adjEnd == adjStart...and we can't have an axis with zero length
        // so we apply this instead:
        if (adjEnd <= adjStart) {
            adjEnd = adjStart + 1L;
        }
        adjusted = new DateRange(this.timeline.toMillisecond(adjStart), this.timeline.toMillisecond(adjEnd));
        setRange(adjusted);
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
        if (!(obj instanceof DateAxis)) {
            return false;
        }
        DateAxis that = (DateAxis) obj;
        if (!Objects.equals(this.timeZone, that.timeZone)) {
            return false;
        }
        if (!Objects.equals(this.locale, that.locale)) {
            return false;
        }
        if (!Objects.equals(this.tickUnit, that.tickUnit)) {
            return false;
        }
        if (!Objects.equals(this.dateFormatOverride, that.dateFormatOverride)) {
            return false;
        }
        if (!Objects.equals(this.tickMarkPosition, that.tickMarkPosition)) {
            return false;
        }
        if (!Objects.equals(this.timeline, that.timeline)) {
            return false;
        }
        return super.equals(obj);
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
     * Returns a clone of the object.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if some component of the axis does
     *         not support cloning.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        DateAxis clone = (DateAxis) super.clone();
        // 'dateTickUnit' is immutable : no need to clone
        if (this.dateFormatOverride != null) {
            clone.dateFormatOverride = (DateFormat) this.dateFormatOverride.clone();
        }
        // 'tickMarkPosition' is immutable : no need to clone
        return clone;
    }
}
