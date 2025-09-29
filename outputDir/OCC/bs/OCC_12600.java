package OCC.bs;
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
 * ChartFactory.java
 * -----------------
 * (C) Copyright 2001-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Serge V. Grachov;
 *                   Joao Guilherme Del Valle;
 *                   Bill Kelemen;
 *                   Jon Iles;
 *                   Jelai Wang;
 *                   Richard Atkinson;
 *                   David Browning (for Australian Institute of Marine
 *                       Science);
 *                   Benoit Xhenseval;
 */
/**
 * A collection of utility methods for creating some standard charts with
 * JFreeChart.
 */
public abstract class ChartFactory {

    /**
     * The chart theme.
     */
    private static ChartTheme currentTheme = new StandardChartTheme("JFree");

    private ChartFactory() {
        // no requirement to instantiate
    }

    /**
     * Returns the current chart theme used by the factory.
     *
     * @return The chart theme.
     *
     * @see #setChartTheme(ChartTheme)
     * @see ChartUtils#applyCurrentTheme(JFreeChart)
     */
    public static ChartTheme getChartTheme() {
        return currentTheme;
    }

    /**
     * Sets the current chart theme.  This will be applied to all new charts
     * created via methods in this class.
     *
     * @param theme  the theme ({@code null} not permitted).
     *
     * @see #getChartTheme()
     * @see ChartUtils#applyCurrentTheme(JFreeChart)
     */
    public static void setChartTheme(ChartTheme theme) {
        Args.nullNotPermitted(theme, "theme");
        currentTheme = theme;
        // here we do a check to see if the user is installing the "Legacy"
        // theme, and reset the bar painters in that case...
        if (theme instanceof StandardChartTheme) {
            BarRenderer.setDefaultBarPainter(new StandardBarPainter());
            XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());
        }
    }

    /**
     * Creates a pie chart with default settings.
     * <P>
     * The chart object returned by this method uses a {@link PiePlot} instance
     * as the plot.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param locale  the locale ({@code null} not permitted).
     *
     * @return A pie chart.
     */
    public static JFreeChart createPieChart(String title, PieDataset dataset, boolean legend, boolean tooltips, Locale locale) {
        PiePlot plot = new PiePlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(locale));
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator(locale));
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a pie chart with default settings.
     * <P>
     * The chart object returned by this method uses a {@link PiePlot} instance
     * as the plot.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A pie chart.
     */
    public static JFreeChart createPieChart(String title, PieDataset dataset) {
        return createPieChart(title, dataset, true, true, false);
    }

    /**
     * Creates a pie chart with default settings.
     * <P>
     * The chart object returned by this method uses a {@link PiePlot} instance
     * as the plot.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A pie chart.
     */
    public static JFreeChart createPieChart(String title, PieDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        PiePlot plot = new PiePlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator());
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator());
        }
        if (urls) {
            plot.setURLGenerator(new StandardPieURLGenerator());
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a pie chart with default settings that compares 2 datasets.
     * The colour of each section will be determined by the move from the value
     * for the same key in {@code previousDataset}. ie if value1 &gt;
     * value2 then the section will be in green (unless
     * {@code greenForIncrease} is {@code false}, in which case it
     * would be {@code red}). Each section can have a shade of red or
     * green as the difference can be tailored between 0% (black) and
     * percentDiffForMaxScale% (bright red/green).
     * <p>
     * For instance if {@code percentDiffForMaxScale} is 10 (10%), a
     * difference of 5% will have a half shade of red/green, a difference of
     * 10% or more will have a maximum shade/brightness of red/green.
     * <P>
     * The chart object returned by this method uses a {@link PiePlot} instance
     * as the plot.
     * <p>
     * Written by <a href="mailto:opensource@objectlab.co.uk">Benoit
     * Xhenseval</a>.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param previousDataset  the dataset for the last run, this will be used
     *                         to compare each key in the dataset
     * @param percentDiffForMaxScale scale goes from bright red/green to black,
     *                               percentDiffForMaxScale indicate the change
     *                               required to reach top scale.
     * @param greenForIncrease  an increase since previousDataset will be
     *                          displayed in green (decrease red) if true.
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param locale  the locale ({@code null} not permitted).
     * @param subTitle displays a subtitle with colour scheme if true
     * @param showDifference  create a new dataset that will show the %
     *                        difference between the two datasets.
     *
     * @return A pie chart.
     */
    public static JFreeChart createPieChart(String title, PieDataset<String> dataset, PieDataset<String> previousDataset, int percentDiffForMaxScale, boolean greenForIncrease, boolean legend, boolean tooltips, Locale locale, boolean subTitle, boolean showDifference) {
        PiePlot<String> plot = new PiePlot<>(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator<>(locale));
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator<>(locale));
        }
        List<String> keys = dataset.getKeys();
        DefaultPieDataset<String> series = null;
        if (showDifference) {
            series = new DefaultPieDataset<>();
        }
        double colorPerPercent = 255.0 / percentDiffForMaxScale;
        for (String key : keys) {
            Number newValue = dataset.getValue(key);
            Number oldValue = previousDataset.getValue(key);
            if (oldValue == null) {
                if (greenForIncrease) {
                    plot.setSectionPaint(key, Color.GREEN);
                } else {
                    plot.setSectionPaint(key, Color.RED);
                }
                if (showDifference) {
                    // suppresses compiler warning
                    assert series != null;
                    series.setValue(key + " (+100%)", newValue);
                }
            } else {
                double percentChange = (newValue.doubleValue() / oldValue.doubleValue() - 1.0) * 100.0;
                double shade = (Math.abs(percentChange) >= percentDiffForMaxScale ? 255 : Math.abs(percentChange) * colorPerPercent);
                if (greenForIncrease && newValue.doubleValue() > oldValue.doubleValue() || !greenForIncrease && newValue.doubleValue() < oldValue.doubleValue()) {
                    plot.setSectionPaint(key, new Color(0, (int) shade, 0));
                } else {
                    plot.setSectionPaint(key, new Color((int) shade, 0, 0));
                }
                if (showDifference) {
                    // suppresses compiler warning
                    assert series != null;
                    series.setValue(key + " (" + (percentChange >= 0 ? "+" : "") + NumberFormat.getPercentInstance().format(percentChange / 100.0) + ")", newValue);
                }
            }
        }
        if (showDifference) {
            plot.setDataset(series);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        if (subTitle) {
            TextTitle subtitle = new TextTitle("Bright " + (greenForIncrease ? "red" : "green") + "=change >=-" + percentDiffForMaxScale + "%, Bright " + (!greenForIncrease ? "red" : "green") + "=change >=+" + percentDiffForMaxScale + "%", new Font("SansSerif", Font.PLAIN, 10));
            chart.addSubtitle(subtitle);
        }
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a pie chart with default settings that compares 2 datasets.
     * The colour of each section will be determined by the move from the value
     * for the same key in {@code previousDataset}. ie if value1 &gt;
     * value2 then the section will be in green (unless
     * {@code greenForIncrease} is {@code false}, in which case it
     * would be {@code red}). Each section can have a shade of red or
     * green as the difference can be tailored between 0% (black) and
     * percentDiffForMaxScale% (bright red/green).
     * <p>
     * For instance if {@code percentDiffForMaxScale} is 10 (10%), a
     * difference of 5% will have a half shade of red/green, a difference of
     * 10% or more will have a maximum shade/brightness of red/green.
     * <P>
     * The chart object returned by this method uses a {@link PiePlot} instance
     * as the plot.
     * <p>
     * Written by <a href="mailto:opensource@objectlab.co.uk">Benoit
     * Xhenseval</a>.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param previousDataset  the dataset for the last run, this will be used
     *                         to compare each key in the dataset
     * @param percentDiffForMaxScale scale goes from bright red/green to black,
     *                               percentDiffForMaxScale indicate the change
     *                               required to reach top scale.
     * @param greenForIncrease  an increase since previousDataset will be
     *                          displayed in green (decrease red) if true.
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     * @param subTitle displays a subtitle with colour scheme if true
     * @param showDifference  create a new dataset that will show the %
     *                        difference between the two datasets.
     *
     * @return A pie chart.
     */
    public static JFreeChart createPieChart(String title, PieDataset<String> dataset, PieDataset<String> previousDataset, int percentDiffForMaxScale, boolean greenForIncrease, boolean legend, boolean tooltips, boolean urls, boolean subTitle, boolean showDifference) {
        PiePlot<String> plot = new PiePlot<>(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator<>());
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator<>());
        }
        if (urls) {
            plot.setURLGenerator(new StandardPieURLGenerator());
        }
        List<String> keys = dataset.getKeys();
        DefaultPieDataset<String> series = null;
        if (showDifference) {
            series = new DefaultPieDataset();
        }
        double colorPerPercent = 255.0 / percentDiffForMaxScale;
        for (String key : keys) {
            Number newValue = dataset.getValue(key);
            Number oldValue = previousDataset.getValue(key);
            if (oldValue == null) {
                if (greenForIncrease) {
                    plot.setSectionPaint(key, Color.GREEN);
                } else {
                    plot.setSectionPaint(key, Color.RED);
                }
                if (showDifference) {
                    // suppresses compiler warning
                    assert series != null;
                    series.setValue(key + " (+100%)", newValue);
                }
            } else {
                double percentChange = (newValue.doubleValue() / oldValue.doubleValue() - 1.0) * 100.0;
                double shade = (Math.abs(percentChange) >= percentDiffForMaxScale ? 255 : Math.abs(percentChange) * colorPerPercent);
                if (greenForIncrease && newValue.doubleValue() > oldValue.doubleValue() || !greenForIncrease && newValue.doubleValue() < oldValue.doubleValue()) {
                    plot.setSectionPaint(key, new Color(0, (int) shade, 0));
                } else {
                    plot.setSectionPaint(key, new Color((int) shade, 0, 0));
                }
                if (showDifference) {
                    // suppresses compiler warning
                    assert series != null;
                    series.setValue(key + " (" + (percentChange >= 0 ? "+" : "") + NumberFormat.getPercentInstance().format(percentChange / 100.0) + ")", newValue);
                }
            }
        }
        if (showDifference) {
            plot.setDataset(series);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        if (subTitle) {
            TextTitle subtitle = new TextTitle("Bright " + (greenForIncrease ? "red" : "green") + "=change >=-" + percentDiffForMaxScale + "%, Bright " + (!greenForIncrease ? "red" : "green") + "=change >=+" + percentDiffForMaxScale + "%", new Font("SansSerif", Font.PLAIN, 10));
            chart.addSubtitle(subtitle);
        }
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a ring chart with default settings.
     * <P>
     * The chart object returned by this method uses a {@link RingPlot}
     * instance as the plot.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param locale  the locale ({@code null} not permitted).
     *
     * @return A ring chart.
     */
    public static JFreeChart createRingChart(String title, PieDataset dataset, boolean legend, boolean tooltips, Locale locale) {
        RingPlot plot = new RingPlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(locale));
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator(locale));
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a ring chart with default settings.
     * <P>
     * The chart object returned by this method uses a {@link RingPlot}
     * instance as the plot.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A ring chart.
     */
    public static JFreeChart createRingChart(String title, PieDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        RingPlot plot = new RingPlot(dataset);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator());
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        if (tooltips) {
            plot.setToolTipGenerator(new StandardPieToolTipGenerator());
        }
        if (urls) {
            plot.setURLGenerator(new StandardPieURLGenerator());
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a chart that displays multiple pie plots.  The chart object
     * returned by this method uses a {@link MultiplePiePlot} instance as the
     * plot.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset ({@code null} permitted).
     * @param order  the order that the data is extracted (by row or by column)
     *               ({@code null} not permitted).
     * @param legend  include a legend?
     * @param tooltips  generate tooltips?
     * @param urls  generate URLs?
     *
     * @return A chart.
     */
    public static JFreeChart createMultiplePieChart(String title, CategoryDataset dataset, TableOrder order, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(order, "order");
        MultiplePiePlot plot = new MultiplePiePlot(dataset);
        plot.setDataExtractOrder(order);
        plot.setBackgroundPaint(null);
        plot.setOutlineStroke(null);
        if (tooltips) {
            PieToolTipGenerator tooltipGenerator = new StandardPieToolTipGenerator();
            PiePlot pp = (PiePlot) plot.getPieChart().getPlot();
            pp.setToolTipGenerator(tooltipGenerator);
        }
        if (urls) {
            PieURLGenerator urlGenerator = new StandardPieURLGenerator();
            PiePlot pp = (PiePlot) plot.getPieChart().getPlot();
            pp.setURLGenerator(urlGenerator);
        }
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a bar chart with a vertical orientation.  The chart object
     * returned by this method uses a {@link CategoryPlot} instance as the
     * plot, with a {@link CategoryAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link BarRenderer} as the
     * renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis
     *                        ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A bar chart.
     */
    public static JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {
        return createBarChart(title, categoryAxisLabel, valueAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a bar chart.  The chart object returned by this method uses a
     * {@link CategoryPlot} instance as the plot, with a {@link CategoryAxis}
     * for the domain axis, a {@link NumberAxis} as the range axis, and a
     * {@link BarRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis
     *                        ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} not permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A bar chart.
     */
    public static JFreeChart createBarChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        BarRenderer renderer = new BarRenderer();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
            renderer.setDefaultPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT);
            renderer.setDefaultNegativeItemLabelPosition(position2);
        } else if (orientation == PlotOrientation.VERTICAL) {
            ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
            renderer.setDefaultPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
            renderer.setDefaultNegativeItemLabelPosition(position2);
        }
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a stacked bar chart with default settings.  The chart object
     * returned by this method uses a {@link CategoryPlot} instance as the
     * plot, with a {@link CategoryAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link StackedBarRenderer}
     * as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param domainAxisLabel  the label for the category axis
     *                         ({@code null} permitted).
     * @param rangeAxisLabel  the label for the value axis
     *                        ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A stacked bar chart.
     */
    public static JFreeChart createStackedBarChart(String title, String domainAxisLabel, String rangeAxisLabel, CategoryDataset dataset) {
        return createStackedBarChart(title, domainAxisLabel, rangeAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a stacked bar chart with default settings.  The chart object
     * returned by this method uses a {@link CategoryPlot} instance as the
     * plot, with a {@link CategoryAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link StackedBarRenderer}
     * as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param domainAxisLabel  the label for the category axis
     *                         ({@code null} permitted).
     * @param rangeAxisLabel  the label for the value axis
     *                        ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the orientation of the chart (horizontal or
     *                     vertical) ({@code null} not permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A stacked bar chart.
     */
    public static JFreeChart createStackedBarChart(String title, String domainAxisLabel, String rangeAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(domainAxisLabel);
        ValueAxis valueAxis = new NumberAxis(rangeAxisLabel);
        StackedBarRenderer renderer = new StackedBarRenderer();
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates an area chart with default settings.  The chart object returned
     * by this method uses a {@link CategoryPlot} instance as the plot, with a
     * {@link CategoryAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and an {@link AreaRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return An area chart.
     */
    public static JFreeChart createAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {
        return createAreaChart(title, categoryAxisLabel, valueAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates an area chart with default settings.  The chart object returned
     * by this method uses a {@link CategoryPlot} instance as the plot, with a
     * {@link CategoryAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and an {@link AreaRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation ({@code null} not
     *                     permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return An area chart.
     */
    public static JFreeChart createAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        AreaRenderer renderer = new AreaRenderer();
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a stacked area chart with default settings.  The chart object
     * returned by this method uses a {@link CategoryPlot} instance as the
     * plot, with a {@link CategoryAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link StackedAreaRenderer}
     * as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A stacked area chart.
     */
    public static JFreeChart createStackedAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {
        return createStackedAreaChart(title, categoryAxisLabel, valueAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a stacked area chart with default settings.  The chart object
     * returned by this method uses a {@link CategoryPlot} instance as the
     * plot, with a {@link CategoryAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link StackedAreaRenderer}
     * as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} not permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A stacked area chart.
     */
    public static JFreeChart createStackedAreaChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        StackedAreaRenderer renderer = new StackedAreaRenderer();
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a line chart with default settings.  The chart object returned
     * by this method uses a {@link CategoryPlot} instance as the plot, with a
     * {@link CategoryAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and a {@link LineAndShapeRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A line chart.
     */
    public static JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset) {
        return createLineChart(title, categoryAxisLabel, valueAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a line chart with default settings.  The chart object returned
     * by this method uses a {@link CategoryPlot} instance as the plot, with a
     * {@link CategoryAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and a {@link LineAndShapeRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the chart orientation (horizontal or vertical)
     *                     ({@code null} not permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A line chart.
     */
    public static JFreeChart createLineChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        LineAndShapeRenderer renderer = new LineAndShapeRenderer(true, false);
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardCategoryToolTipGenerator());
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a Gantt chart using the supplied attributes plus default values
     * where required.  The chart object returned by this method uses a
     * {@link CategoryPlot} instance as the plot, with a {@link CategoryAxis}
     * for the domain axis, a {@link DateAxis} as the range axis, and a
     * {@link GanttRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param dateAxisLabel  the label for the date axis
     *                       ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A Gantt chart.
     */
    public static JFreeChart createGanttChart(String title, String categoryAxisLabel, String dateAxisLabel, IntervalCategoryDataset dataset) {
        return createGanttChart(title, categoryAxisLabel, dateAxisLabel, dataset, true, true, false);
    }

    /**
     * Creates a Gantt chart using the supplied attributes plus default values
     * where required.  The chart object returned by this method uses a
     * {@link CategoryPlot} instance as the plot, with a {@link CategoryAxis}
     * for the domain axis, a {@link DateAxis} as the range axis, and a
     * {@link GanttRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param dateAxisLabel  the label for the date axis
     *                       ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A Gantt chart.
     */
    public static JFreeChart createGanttChart(String title, String categoryAxisLabel, String dateAxisLabel, IntervalCategoryDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        DateAxis dateAxis = new DateAxis(dateAxisLabel);
        CategoryItemRenderer renderer = new GanttRenderer();
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new IntervalCategoryToolTipGenerator("{3} - {4}", DateFormat.getDateInstance()));
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, dateAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a waterfall chart.  The chart object returned by this method
     * uses a {@link CategoryPlot} instance as the plot, with a
     * {@link CategoryAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and a {@link WaterfallBarRenderer} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  the label for the category axis
     *                           ({@code null} permitted).
     * @param valueAxisLabel  the label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A waterfall chart.
     */
    public static JFreeChart createWaterfallChart(String title, String categoryAxisLabel, String valueAxisLabel, CategoryDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        categoryAxis.setCategoryMargin(0.0);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);
        WaterfallBarRenderer renderer = new WaterfallBarRenderer();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, Math.PI / 2.0);
            renderer.setDefaultPositiveItemLabelPosition(position);
            renderer.setDefaultNegativeItemLabelPosition(position);
        } else if (orientation == PlotOrientation.VERTICAL) {
            ItemLabelPosition position = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0.0);
            renderer.setDefaultPositiveItemLabelPosition(position);
            renderer.setDefaultNegativeItemLabelPosition(position);
        }
        if (tooltips) {
            StandardCategoryToolTipGenerator generator = new StandardCategoryToolTipGenerator();
            renderer.setDefaultToolTipGenerator(generator);
        }
        if (urls) {
            renderer.setDefaultItemURLGenerator(new StandardCategoryURLGenerator());
        }
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        plot.clearRangeMarkers();
        Marker baseline = new ValueMarker(0.0);
        baseline.setPaint(Color.BLACK);
        plot.addRangeMarker(baseline, Layer.FOREGROUND);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a polar plot for the specified dataset (x-values interpreted as
     * angles in degrees).  The chart object returned by this method uses a
     * {@link PolarPlot} instance as the plot, with a {@link NumberAxis} for
     * the radial axis.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset ({@code null} permitted).
     * @param legend  legend required?
     * @param tooltips  tooltips required?
     * @param urls  URLs required?
     *
     * @return A chart.
     */
    public static JFreeChart createPolarChart(String title, XYDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        PolarPlot plot = new PolarPlot();
        plot.setDataset(dataset);
        NumberAxis rangeAxis = new NumberAxis();
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);
        rangeAxis.setTickLabelInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        plot.setAxis(rangeAxis);
        plot.setRenderer(new DefaultPolarItemRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a scatter plot with default settings.  The chart object
     * returned by this method uses an {@link XYPlot} instance as the plot,
     * with a {@link NumberAxis} for the domain axis, a  {@link NumberAxis}
     * as the range axis, and an {@link XYLineAndShapeRenderer} as the
     * renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A scatter plot.
     */
    public static JFreeChart createScatterPlot(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset) {
        return createScatterPlot(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a scatter plot with default settings.  The chart object
     * returned by this method uses an {@link XYPlot} instance as the plot,
     * with a {@link NumberAxis} for the domain axis, a  {@link NumberAxis}
     * as the range axis, and an {@link XYLineAndShapeRenderer} as the
     * renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A scatter plot.
     */
    public static JFreeChart createScatterPlot(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYLineAndShapeRenderer(false, true);
        renderer.setDefaultToolTipGenerator(toolTipGenerator);
        renderer.setURLGenerator(urlGenerator);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates and returns a default instance of an XY bar chart.
     * <P>
     * The chart object returned by this method uses an {@link XYPlot} instance
     * as the plot, with a {@link DateAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link XYBarRenderer} as the
     * renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param dateAxis  make the domain axis display dates?
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return An XY bar chart.
     */
    public static JFreeChart createXYBarChart(String title, String xAxisLabel, boolean dateAxis, String yAxisLabel, IntervalXYDataset dataset) {
        return createXYBarChart(title, xAxisLabel, dateAxis, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates and returns a default instance of an XY bar chart.
     * <P>
     * The chart object returned by this method uses an {@link XYPlot} instance
     * as the plot, with a {@link DateAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link XYBarRenderer} as the
     * renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param dateAxis  make the domain axis display dates?
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return An XY bar chart.
     */
    public static JFreeChart createXYBarChart(String title, String xAxisLabel, boolean dateAxis, String yAxisLabel, IntervalXYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        ValueAxis domainAxis;
        if (dateAxis) {
            domainAxis = new DateAxis(xAxisLabel);
        } else {
            NumberAxis axis = new NumberAxis(xAxisLabel);
            axis.setAutoRangeIncludesZero(false);
            domainAxis = axis;
        }
        ValueAxis valueAxis = new NumberAxis(yAxisLabel);
        XYBarRenderer renderer = new XYBarRenderer();
        if (tooltips) {
            XYToolTipGenerator tt;
            if (dateAxis) {
                tt = StandardXYToolTipGenerator.getTimeSeriesInstance();
            } else {
                tt = new StandardXYToolTipGenerator();
            }
            renderer.setDefaultToolTipGenerator(tt);
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }
        XYPlot plot = new XYPlot(dataset, domainAxis, valueAxis, renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates an area chart using an {@link XYDataset}.
     * <P>
     * The chart object returned by this method uses an {@link XYPlot} instance
     * as the plot, with a {@link NumberAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link XYAreaRenderer} as
     * the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return An XY area chart.
     *
     * @param <S> the type for series keys.
     */
    public static <S extends Comparable<S>> JFreeChart createXYAreaChart(String title, String xAxisLabel, String yAxisLabel, XYDataset<S> dataset) {
        return createXYAreaChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates an area chart using an {@link XYDataset}.
     * <P>
     * The chart object returned by this method uses an {@link XYPlot} instance
     * as the plot, with a {@link NumberAxis} for the domain axis, a
     * {@link NumberAxis} as the range axis, and a {@link XYAreaRenderer} as
     * the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @param <S> the type for series keys.
     *
     * @return An XY area chart.
     */
    public static <S extends Comparable<S>> JFreeChart createXYAreaChart(String title, String xAxisLabel, String yAxisLabel, XYDataset<S> dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYPlot<S> plot = new XYPlot<>(dataset, xAxis, yAxis, null);
        plot.setOrientation(orientation);
        plot.setForegroundAlpha(0.5f);
        XYToolTipGenerator tipGenerator = null;
        if (tooltips) {
            tipGenerator = new StandardXYToolTipGenerator();
        }
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        plot.setRenderer(new XYAreaRenderer(XYAreaRenderer.AREA, tipGenerator, urlGenerator));
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a stacked XY area plot.  The chart object returned by this
     * method uses an {@link XYPlot} instance as the plot, with a
     * {@link NumberAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and a {@link StackedXYAreaRenderer2} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A stacked XY area chart.
     */
    public static JFreeChart createStackedXYAreaChart(String title, String xAxisLabel, String yAxisLabel, TableXYDataset dataset) {
        return createStackedXYAreaChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a stacked XY area plot.  The chart object returned by this
     * method uses an {@link XYPlot} instance as the plot, with a
     * {@link NumberAxis} for the domain axis, a {@link NumberAxis} as the
     * range axis, and a {@link StackedXYAreaRenderer2} as the renderer.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A stacked XY area chart.
     */
    public static JFreeChart createStackedXYAreaChart(String title, String xAxisLabel, String yAxisLabel, TableXYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        xAxis.setLowerMargin(0.0);
        xAxis.setUpperMargin(0.0);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        StackedXYAreaRenderer2 renderer = new StackedXYAreaRenderer2(toolTipGenerator, urlGenerator);
        renderer.setOutline(true);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        // forces recalculation of the axis range
        plot.setRangeAxis(yAxis);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a line chart (based on an {@link XYDataset}) with default
     * settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return The chart.
     */
    public static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset) {
        return createXYLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a line chart (based on an {@link XYDataset}) with default
     * settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }
        JFreeChart chart = null;
        chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a stepped XY plot with default settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset) {
        return createXYStepChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a stepped XY plot with default settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        DateAxis xAxis = new DateAxis(xAxisLabel);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYStepRenderer(toolTipGenerator, urlGenerator);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a filled stepped XY plot with default settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepAreaChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset) {
        return createXYStepAreaChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a filled stepped XY plot with default settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A chart.
     */
    public static JFreeChart createXYStepAreaChart(String title, String xAxisLabel, String yAxisLabel, XYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = new StandardXYToolTipGenerator();
        }
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYItemRenderer renderer = new XYStepAreaRenderer(XYStepAreaRenderer.AREA_AND_SHAPES, toolTipGenerator, urlGenerator);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates and returns a time series chart.  A time series chart is an
     * {@link XYPlot} with a {@link DateAxis} for the x-axis and a
     * {@link NumberAxis} for the y-axis.  The default renderer is an
     * {@link XYLineAndShapeRenderer}.
     * <P>
     * A convenient dataset to use with this chart is a
     * {@link org.jfree.data.time.TimeSeriesCollection}.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param timeAxisLabel  a label for the time axis ({@code null}
     *                       permitted).
     * @param valueAxisLabel  a label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A time series chart.
     */
    public static JFreeChart createTimeSeriesChart(String title, String timeAxisLabel, String valueAxisLabel, XYDataset dataset) {
        return createTimeSeriesChart(title, timeAxisLabel, valueAxisLabel, dataset, true, true, false);
    }

    /**
     * Creates and returns a time series chart.  A time series chart is an
     * {@link XYPlot} with a {@link DateAxis} for the x-axis and a
     * {@link NumberAxis} for the y-axis.  The default renderer is an
     * {@link XYLineAndShapeRenderer}.
     * <P>
     * A convenient dataset to use with this chart is a
     * {@link org.jfree.data.time.TimeSeriesCollection}.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param timeAxisLabel  a label for the time axis ({@code null}
     *                       permitted).
     * @param valueAxisLabel  a label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A time series chart.
     */
    public static JFreeChart createTimeSeriesChart(String title, String timeAxisLabel, String valueAxisLabel, XYDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        // reduce the default margins
        timeAxis.setLowerMargin(0.02);
        timeAxis.setUpperMargin(0.02);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        // override default
        valueAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);
        XYToolTipGenerator toolTipGenerator = null;
        if (tooltips) {
            toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();
        }
        XYURLGenerator urlGenerator = null;
        if (urls) {
            urlGenerator = new StandardXYURLGenerator();
        }
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setDefaultToolTipGenerator(toolTipGenerator);
        renderer.setURLGenerator(urlGenerator);
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates and returns a default instance of a candlesticks chart.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param timeAxisLabel  a label for the time axis ({@code null}
     *                       permitted).
     * @param valueAxisLabel  a label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     *
     * @return A candlestick chart.
     */
    public static JFreeChart createCandlestickChart(String title, String timeAxisLabel, String valueAxisLabel, OHLCDataset dataset, boolean legend) {
        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);
        plot.setRenderer(new CandlestickRenderer());
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates and returns a default instance of a high-low-open-close chart.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param timeAxisLabel  a label for the time axis ({@code null}
     *                       permitted).
     * @param valueAxisLabel  a label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     *
     * @return A high-low-open-close chart.
     */
    public static JFreeChart createHighLowChart(String title, String timeAxisLabel, String valueAxisLabel, OHLCDataset dataset, boolean legend) {
        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setDefaultToolTipGenerator(new HighLowItemLabelGenerator());
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a bubble chart with default settings.  The chart is composed of
     * an {@link XYPlot}, with a {@link NumberAxis} for the domain axis,
     * a {@link NumberAxis} for the range axis, and an {@link XYBubbleRenderer}
     * to draw the data items.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     *
     * @return A bubble chart.
     */
    public static JFreeChart createBubbleChart(String title, String xAxisLabel, String yAxisLabel, XYZDataset dataset) {
        return createBubbleChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a bubble chart with default settings.  The chart is composed of
     * an {@link XYPlot}, with a {@link NumberAxis} for the domain axis,
     * a {@link NumberAxis} for the range axis, and an {@link XYBubbleRenderer}
     * to draw the data items.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the X-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the Y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param orientation  the orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  a flag specifying whether a legend is required.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A bubble chart.
     */
    public static JFreeChart createBubbleChart(String title, String xAxisLabel, String yAxisLabel, XYZDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, null);
        XYItemRenderer renderer = new XYBubbleRenderer(XYBubbleRenderer.SCALE_ON_RANGE_AXIS);
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardXYZToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYZURLGenerator());
        }
        plot.setRenderer(renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a histogram chart.  This chart is constructed with an
     * {@link XYPlot} using an {@link XYBarRenderer}.  The domain and range
     * axes are {@link NumberAxis} instances.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  the x-axis label ({@code null} permitted).
     * @param yAxisLabel  the y-axis label ({@code null} permitted).
     * @param dataset  the dataset ({@code null} permitted).
     *
     * @return A chart.
     */
    public static JFreeChart createHistogram(String title, String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset) {
        return createHistogram(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
    }

    /**
     * Creates a histogram chart.  This chart is constructed with an
     * {@link XYPlot} using an {@link XYBarRenderer}.  The domain and range
     * axes are {@link NumberAxis} instances.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  the x axis label ({@code null} permitted).
     * @param yAxisLabel  the y axis label ({@code null} permitted).
     * @param dataset  the dataset ({@code null} permitted).
     * @param orientation  the orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted).
     * @param legend  create a legend?
     * @param tooltips  display tooltips?
     * @param urls  generate URLs?
     *
     * @return The chart.
     */
    public static JFreeChart createHistogram(String title, String xAxisLabel, String yAxisLabel, IntervalXYDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        NumberAxis xAxis = new NumberAxis(xAxisLabel);
        xAxis.setAutoRangeIncludesZero(false);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);
        XYItemRenderer renderer = new XYBarRenderer();
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(orientation);
        plot.setDomainZeroBaselineVisible(true);
        plot.setRangeZeroBaselineVisible(true);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates and returns a default instance of a box and whisker chart
     * based on data from a {@link BoxAndWhiskerCategoryDataset}.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param categoryAxisLabel  a label for the category axis
     *     ({@code null} permitted).
     * @param valueAxisLabel  a label for the value axis ({@code null}
     *     permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     *
     * @return A box and whisker chart.
     */
    public static JFreeChart createBoxAndWhiskerChart(String title, String categoryAxisLabel, String valueAxisLabel, BoxAndWhiskerCategoryDataset dataset, boolean legend) {
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setDefaultToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates and returns a default instance of a box and whisker chart.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param timeAxisLabel  a label for the time axis ({@code null}
     *                       permitted).
     * @param valueAxisLabel  a label for the value axis ({@code null}
     *                        permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag specifying whether a legend is required.
     *
     * @return A box and whisker chart.
     */
    public static JFreeChart createBoxAndWhiskerChart(String title, String timeAxisLabel, String valueAxisLabel, BoxAndWhiskerXYDataset dataset, boolean legend) {
        ValueAxis timeAxis = new DateAxis(timeAxisLabel);
        NumberAxis valueAxis = new NumberAxis(valueAxisLabel);
        valueAxis.setAutoRangeIncludesZero(false);
        XYBoxAndWhiskerRenderer renderer = new XYBoxAndWhiskerRenderer(10.0);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a wind plot with default settings.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param xAxisLabel  a label for the x-axis ({@code null} permitted).
     * @param yAxisLabel  a label for the y-axis ({@code null} permitted).
     * @param dataset  the dataset for the chart ({@code null} permitted).
     * @param legend  a flag that controls whether a legend is created.
     * @param tooltips  configure chart to generate tool tips?
     * @param urls  configure chart to generate URLs?
     *
     * @return A wind plot.
     */
    public static JFreeChart createWindPlot(String title, String xAxisLabel, String yAxisLabel, WindDataset dataset, boolean legend, boolean tooltips, boolean urls) {
        ValueAxis xAxis = new DateAxis(xAxisLabel);
        ValueAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setRange(-12.0, 12.0);
        WindItemRenderer renderer = new WindItemRenderer();
        if (tooltips) {
            renderer.setDefaultToolTipGenerator(new StandardXYToolTipGenerator());
        }
        if (urls) {
            renderer.setURLGenerator(new StandardXYURLGenerator());
        }
        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
        return chart;
    }

    /**
     * Creates a wafer map chart.
     *
     * @param title  the chart title ({@code null} permitted).
     * @param dataset  the dataset ({@code null} permitted).
     * @param orientation  the plot orientation (horizontal or vertical)
     *                     ({@code null} NOT permitted.
     * @param legend  display a legend?
     * @param tooltips  generate tooltips?
     * @param urls  generate URLs?
     *
     * @return A wafer map chart.
     */
    public static JFreeChart createWaferMapChart(String title, WaferMapDataset dataset, PlotOrientation orientation, boolean legend, boolean tooltips, boolean urls) {
        Args.nullNotPermitted(orientation, "orientation");
        WaferMapPlot plot = new WaferMapPlot(dataset);
        WaferMapRenderer renderer = new WaferMapRenderer();
        plot.setRenderer(renderer);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
        currentTheme.apply(chart);
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
 * ---------------
 * LabelBlock.java
 * ---------------
 * (C) Copyright 2004-present, by David Gilbert.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Pierre-Marie Le Biot;
 *
 */
/**
 * A block containing a label.
 */
public class LabelBlock extends AbstractBlock implements Block, PublicCloneable {

    /**
     * For serialization.
     */
    static final long serialVersionUID = 249626098864178017L;

    /**
     * The text for the label - retained in case the label needs
     * regenerating (for example, to change the font).
     */
    private final String text;

    /**
     * The label.
     */
    private TextBlock label;

    /**
     * The font.
     */
    private Font font;

    /**
     * The tool tip text (can be {@code null}).
     */
    private String toolTipText;

    /**
     * The URL text (can be {@code null}).
     */
    private String urlText;

    /**
     * The default color.
     */
    public static final Paint DEFAULT_PAINT = Color.BLACK;

    /**
     * The paint.
     */
    private transient Paint paint;

    /**
     * The content alignment point.
     */
    private TextBlockAnchor contentAlignmentPoint;

    /**
     * The anchor point for the text.
     */
    private RectangleAnchor textAnchor;

    /**
     * Creates a new label block.
     *
     * @param label  the label ({@code null} not permitted).
     */
    public LabelBlock(String label) {
        this(label, new Font("SansSerif", Font.PLAIN, 10), DEFAULT_PAINT);
    }

    /**
     * Creates a new label block.
     *
     * @param text  the text for the label ({@code null} not permitted).
     * @param font  the font ({@code null} not permitted).
     */
    public LabelBlock(String text, Font font) {
        this(text, font, DEFAULT_PAINT);
    }

    /**
     * Creates a new label block.
     *
     * @param text  the text for the label ({@code null} not permitted).
     * @param font  the font ({@code null} not permitted).
     * @param paint the paint ({@code null} not permitted).
     */
    public LabelBlock(String text, Font font, Paint paint) {
        this.text = text;
        this.paint = paint;
        this.label = TextUtils.createTextBlock(text, font, this.paint);
        this.font = font;
        this.toolTipText = null;
        this.urlText = null;
        this.contentAlignmentPoint = TextBlockAnchor.CENTER;
        this.textAnchor = RectangleAnchor.CENTER;
    }

    /**
     * Returns the font.
     *
     * @return The font (never {@code null}).
     *
     * @see #setFont(Font)
     */
    public Font getFont() {
        return this.font;
    }

    /**
     * Sets the font and regenerates the label.
     *
     * @param font  the font ({@code null} not permitted).
     *
     * @see #getFont()
     */
    public void setFont(Font font) {
        Args.nullNotPermitted(font, "font");
        this.font = font;
        this.label = TextUtils.createTextBlock(this.text, font, this.paint);
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
     * Sets the paint and regenerates the label.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPaint()
     */
    public void setPaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.paint = paint;
        this.label = TextUtils.createTextBlock(this.text, this.font, this.paint);
    }

    /**
     * Returns the tool tip text.
     *
     * @return The tool tip text (possibly {@code null}).
     *
     * @see #setToolTipText(String)
     */
    public String getToolTipText() {
        return this.toolTipText;
    }

    /**
     * Sets the tool tip text.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getToolTipText()
     */
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    /**
     * Returns the URL text.
     *
     * @return The URL text (possibly {@code null}).
     *
     * @see #setURLText(String)
     */
    public String getURLText() {
        return this.urlText;
    }

    /**
     * Sets the URL text.
     *
     * @param text  the text ({@code null} permitted).
     *
     * @see #getURLText()
     */
    public void setURLText(String text) {
        this.urlText = text;
    }

    /**
     * Returns the content alignment point.
     *
     * @return The content alignment point (never {@code null}).
     */
    public TextBlockAnchor getContentAlignmentPoint() {
        return this.contentAlignmentPoint;
    }

    /**
     * Sets the content alignment point.
     *
     * @param anchor  the anchor used to determine the alignment point (never
     *         {@code null}).
     */
    public void setContentAlignmentPoint(TextBlockAnchor anchor) {
        Args.nullNotPermitted(anchor, "anchor");
        this.contentAlignmentPoint = anchor;
    }

    /**
     * Returns the text anchor (never {@code null}).
     *
     * @return The text anchor.
     */
    public RectangleAnchor getTextAnchor() {
        return this.textAnchor;
    }

    /**
     * Sets the text anchor.
     *
     * @param anchor  the anchor ({@code null} not permitted).
     */
    public void setTextAnchor(RectangleAnchor anchor) {
        this.textAnchor = anchor;
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
    @Override
    public Size2D arrange(Graphics2D g2, RectangleConstraint constraint) {
        g2.setFont(this.font);
        Size2D s = this.label.calculateDimensions(g2);
        return new Size2D(calculateTotalWidth(s.getWidth()), calculateTotalHeight(s.getHeight()));
    }

    /**
     * Draws the block.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     */
    @Override
    public void draw(Graphics2D g2, Rectangle2D area) {
        draw(g2, area, null);
    }

    /**
     * Draws the block within the specified area.
     *
     * @param g2  the graphics device.
     * @param area  the area.
     * @param params  ignored ({@code null} permitted).
     *
     * @return Always {@code null}.
     */
    @Override
    public Object draw(Graphics2D g2, Rectangle2D area, Object params) {
        area = trimMargin(area);
        drawBorder(g2, area);
        area = trimBorder(area);
        area = trimPadding(area);
        // check if we need to collect chart entities from the container
        EntityBlockParams ebp = null;
        StandardEntityCollection sec = null;
        Shape entityArea = null;
        if (params instanceof EntityBlockParams) {
            ebp = (EntityBlockParams) params;
            if (ebp.getGenerateEntities()) {
                sec = new StandardEntityCollection();
                entityArea = (Shape) area.clone();
            }
        }
        g2.setPaint(this.paint);
        g2.setFont(this.font);
        Point2D pt = this.textAnchor.getAnchorPoint(area);
        this.label.draw(g2, (float) pt.getX(), (float) pt.getY(), this.contentAlignmentPoint);
        BlockResult result = null;
        if (ebp != null && sec != null) {
            if (this.toolTipText != null || this.urlText != null) {
                ChartEntity entity = new ChartEntity(entityArea, this.toolTipText, this.urlText);
                sec.add(entity);
                result = new BlockResult();
                result.setEntityCollection(sec);
            }
        }
        return result;
    }

    /**
     * Tests this {@code LabelBlock} for equality with an arbitrary object.
     *
     * @param obj  the object ({@code null} permitted).
     *
     * @return A boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LabelBlock)) {
            return false;
        }
        LabelBlock that = (LabelBlock) obj;
        if (!this.text.equals(that.text)) {
            return false;
        }
        if (!this.font.equals(that.font)) {
            return false;
        }
        if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
        }
        if (!Objects.equals(this.toolTipText, that.toolTipText)) {
            return false;
        }
        if (!Objects.equals(this.urlText, that.urlText)) {
            return false;
        }
        if (!this.contentAlignmentPoint.equals(that.contentAlignmentPoint)) {
            return false;
        }
        if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
        }
        return super.equals(obj);
    }

    /**
     * Returns a clone of this {@code LabelBlock} instance.
     *
     * @return A clone.
     *
     * @throws CloneNotSupportedException if there is a problem cloning.
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
     * @throws IOException if there is an I/O error.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtils.writePaint(this.paint, stream);
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
 * XYDifferenceRenderer.java
 * -------------------------
 * (C) Copyright 2003-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   Richard West, Advanced Micro Devices, Inc. (major rewrite
 *                   of difference drawing algorithm);
 *                   Patrick Schlott
 *                   Christoph Schroeder
 *                   Martin Hoeller
 *
 */
/**
 * A renderer for an {@link XYPlot} that highlights the differences between two
 * series.  The example shown here is generated by the
 * {@code DifferenceChartDemo1.java} program included in the JFreeChart
 * demo collection:
 * <br><br>
 * <img src="doc-files/XYDifferenceRendererSample.png"
 * alt="XYDifferenceRendererSample.png">
 */
public class XYDifferenceRenderer extends AbstractXYItemRenderer implements XYItemRenderer, PublicCloneable {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -8447915602375584857L;

    /**
     * The paint used to highlight positive differences (y(0) > y(1)).
     */
    private transient Paint positivePaint;

    /**
     * The paint used to highlight negative differences (y(0) < y(1)).
     */
    private transient Paint negativePaint;

    /**
     * Display shapes at each point?
     */
    private boolean shapesVisible;

    /**
     * The shape to display in the legend item.
     */
    private transient Shape legendLine;

    /**
     * This flag controls whether the x-coordinates (in Java2D space)
     * are rounded to integers.  When set to true, this can avoid the vertical
     * striping that anti-aliasing can generate.  However, the rounding may not
     * be appropriate for output in high resolution formats (for example,
     * vector graphics formats such as SVG and PDF).
     */
    private boolean roundXCoordinates;

    /**
     * Creates a new renderer with default attributes.
     */
    public XYDifferenceRenderer() {
        this(Color.GREEN, Color.RED, false);
    }

    /**
     * Creates a new renderer.
     *
     * @param positivePaint  the highlight color for positive differences
     *                       ({@code null} not permitted).
     * @param negativePaint  the highlight color for negative differences
     *                       ({@code null} not permitted).
     * @param shapes  draw shapes?
     */
    public XYDifferenceRenderer(Paint positivePaint, Paint negativePaint, boolean shapes) {
        Args.nullNotPermitted(positivePaint, "positivePaint");
        Args.nullNotPermitted(negativePaint, "negativePaint");
        this.positivePaint = positivePaint;
        this.negativePaint = negativePaint;
        this.shapesVisible = shapes;
        this.legendLine = new Line2D.Double(-7.0, 0.0, 7.0, 0.0);
        this.roundXCoordinates = false;
    }

    /**
     * Returns the paint used to highlight positive differences.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setPositivePaint(Paint)
     */
    public Paint getPositivePaint() {
        return this.positivePaint;
    }

    /**
     * Sets the paint used to highlight positive differences and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getPositivePaint()
     */
    public void setPositivePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.positivePaint = paint;
        fireChangeEvent();
    }

    /**
     * Returns the paint used to highlight negative differences.
     *
     * @return The paint (never {@code null}).
     *
     * @see #setNegativePaint(Paint)
     */
    public Paint getNegativePaint() {
        return this.negativePaint;
    }

    /**
     * Sets the paint used to highlight negative differences.
     *
     * @param paint  the paint ({@code null} not permitted).
     *
     * @see #getNegativePaint()
     */
    public void setNegativePaint(Paint paint) {
        Args.nullNotPermitted(paint, "paint");
        this.negativePaint = paint;
        notifyListeners(new RendererChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether shapes are drawn for each
     * data value.
     *
     * @return A boolean.
     *
     * @see #setShapesVisible(boolean)
     */
    public boolean getShapesVisible() {
        return this.shapesVisible;
    }

    /**
     * Sets a flag that controls whether shapes are drawn for each
     * data value, and sends a {@link RendererChangeEvent} to all registered
     * listeners.
     *
     * @param flag  the flag.
     *
     * @see #getShapesVisible()
     */
    public void setShapesVisible(boolean flag) {
        this.shapesVisible = flag;
        fireChangeEvent();
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
     * Returns the flag that controls whether the x-coordinates (in
     * Java2D space) are rounded to integer values.
     *
     * @return The flag.
     *
     * @see #setRoundXCoordinates(boolean)
     */
    public boolean getRoundXCoordinates() {
        return this.roundXCoordinates;
    }

    /**
     * Sets the flag that controls whether the x-coordinates (in
     * Java2D space) are rounded to integer values, and sends a
     * {@link RendererChangeEvent} to all registered listeners.
     *
     * @param round  the new flag value.
     *
     * @see #getRoundXCoordinates()
     */
    public void setRoundXCoordinates(boolean round) {
        this.roundXCoordinates = round;
        fireChangeEvent();
    }

    /**
     * Initialises the renderer and returns a state object that should be
     * passed to subsequent calls to the drawItem() method.  This method will
     * be called before the first item is rendered, giving the renderer an
     * opportunity to initialise any state information it wants to maintain.
     * The renderer can do nothing if it chooses.
     *
     * @param g2  the graphics device.
     * @param dataArea  the area inside the axes.
     * @param plot  the plot.
     * @param data  the data.
     * @param info  an optional info collection object to return data back to
     *              the caller.
     *
     * @return A state object.
     */
    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        XYItemRendererState state = super.initialise(g2, dataArea, plot, data, info);
        state.setProcessVisibleItemsOnly(false);
        return state;
    }

    /**
     * Returns {@code 2}, the number of passes required by the renderer.
     * The {@link XYPlot} will run through the dataset this number of times.
     *
     * @return The number of passes required by the renderer.
     */
    @Override
    public int getPassCount() {
        return 2;
    }

    /**
     * Draws the visual representation of a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the area within which the data is being drawn.
     * @param info  collects information about the drawing.
     * @param plot  the plot (can be used to obtain standard color
     *              information etc).
     * @param domainAxis  the domain (horizontal) axis.
     * @param rangeAxis  the range (vertical) axis.
     * @param dataset  the dataset.
     * @param series  the series index (zero-based).
     * @param item  the item index (zero-based).
     * @param crosshairState  crosshair information for the plot
     *                        ({@code null} permitted).
     * @param pass  the pass index.
     */
    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item, CrosshairState crosshairState, int pass) {
        if (pass == 0) {
            drawItemPass0(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState);
        } else if (pass == 1) {
            drawItemPass1(g2, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState);
        }
    }

    /**
     * Draws the visual representation of a single data item, first pass.
     *
     * @param x_graphics  the graphics device.
     * @param x_dataArea  the area within which the data is being drawn.
     * @param x_info  collects information about the drawing.
     * @param x_plot  the plot (can be used to obtain standard color
     *                information etc).
     * @param x_domainAxis  the domain (horizontal) axis.
     * @param x_rangeAxis  the range (vertical) axis.
     * @param x_dataset  the dataset.
     * @param x_series  the series index (zero-based).
     * @param x_item  the item index (zero-based).
     * @param x_crosshairState  crosshair information for the plot
     *                          ({@code null} permitted).
     */
    protected void drawItemPass0(Graphics2D x_graphics, Rectangle2D x_dataArea, PlotRenderingInfo x_info, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, XYDataset x_dataset, int x_series, int x_item, CrosshairState x_crosshairState) {
        if (!((0 == x_series) && (0 == x_item))) {
            return;
        }
        boolean b_impliedZeroSubtrahend = (1 == x_dataset.getSeriesCount());
        // check if either series is a degenerate case (i.e. less than 2 points)
        if (isEitherSeriesDegenerate(x_dataset, b_impliedZeroSubtrahend)) {
            return;
        }
        // check if series are disjoint (i.e. domain-spans do not overlap)
        if (!b_impliedZeroSubtrahend && areSeriesDisjoint(x_dataset)) {
            return;
        }
        // polygon definitions
        LinkedList l_minuendXs = new LinkedList();
        LinkedList l_minuendYs = new LinkedList();
        LinkedList l_subtrahendXs = new LinkedList();
        LinkedList l_subtrahendYs = new LinkedList();
        LinkedList l_polygonXs = new LinkedList();
        LinkedList l_polygonYs = new LinkedList();
        // state
        int l_minuendItem = 0;
        int l_minuendItemCount = x_dataset.getItemCount(0);
        Double l_minuendCurX = null;
        Double l_minuendNextX = null;
        Double l_minuendCurY = null;
        Double l_minuendNextY = null;
        double l_minuendMaxY = Double.NEGATIVE_INFINITY;
        double l_minuendMinY = Double.POSITIVE_INFINITY;
        int l_subtrahendItem = 0;
        // actual value set below
        int l_subtrahendItemCount = 0;
        Double l_subtrahendCurX = null;
        Double l_subtrahendNextX = null;
        Double l_subtrahendCurY = null;
        Double l_subtrahendNextY = null;
        double l_subtrahendMaxY = Double.NEGATIVE_INFINITY;
        double l_subtrahendMinY = Double.POSITIVE_INFINITY;
        // if a subtrahend is not specified, assume it is zero
        if (b_impliedZeroSubtrahend) {
            l_subtrahendItem = 0;
            l_subtrahendItemCount = 2;
            l_subtrahendCurX = x_dataset.getXValue(0, 0);
            l_subtrahendNextX = x_dataset.getXValue(0, (l_minuendItemCount - 1));
            l_subtrahendCurY = 0.0;
            l_subtrahendNextY = 0.0;
            l_subtrahendMaxY = 0.0;
            l_subtrahendMinY = 0.0;
            l_subtrahendXs.add(l_subtrahendCurX);
            l_subtrahendYs.add(l_subtrahendCurY);
        } else {
            l_subtrahendItemCount = x_dataset.getItemCount(1);
        }
        boolean b_minuendDone = false;
        boolean b_minuendAdvanced = true;
        boolean b_minuendAtIntersect = false;
        boolean b_minuendFastForward = false;
        boolean b_subtrahendDone = false;
        boolean b_subtrahendAdvanced = true;
        boolean b_subtrahendAtIntersect = false;
        boolean b_subtrahendFastForward = false;
        boolean b_colinear = false;
        boolean b_positive;
        // coordinate pairs
        // current minuend point
        double l_x1 = 0.0, l_y1 = 0.0;
        // next minuend point
        double l_x2 = 0.0, l_y2 = 0.0;
        // current subtrahend point
        double l_x3 = 0.0, l_y3 = 0.0;
        // next subtrahend point
        double l_x4 = 0.0, l_y4 = 0.0;
        // fast-forward through leading tails
        boolean b_fastForwardDone = false;
        while (!b_fastForwardDone) {
            // get the x and y coordinates
            l_x1 = x_dataset.getXValue(0, l_minuendItem);
            l_y1 = x_dataset.getYValue(0, l_minuendItem);
            l_x2 = x_dataset.getXValue(0, l_minuendItem + 1);
            l_y2 = x_dataset.getYValue(0, l_minuendItem + 1);
            l_minuendCurX = l_x1;
            l_minuendCurY = l_y1;
            l_minuendNextX = l_x2;
            l_minuendNextY = l_y2;
            if (b_impliedZeroSubtrahend) {
                l_x3 = l_subtrahendCurX;
                l_y3 = l_subtrahendCurY;
                l_x4 = l_subtrahendNextX;
                l_y4 = l_subtrahendNextY;
            } else {
                l_x3 = x_dataset.getXValue(1, l_subtrahendItem);
                l_y3 = x_dataset.getYValue(1, l_subtrahendItem);
                l_x4 = x_dataset.getXValue(1, l_subtrahendItem + 1);
                l_y4 = x_dataset.getYValue(1, l_subtrahendItem + 1);
                l_subtrahendCurX = l_x3;
                l_subtrahendCurY = l_y3;
                l_subtrahendNextX = l_x4;
                l_subtrahendNextY = l_y4;
            }
            if (l_x2 <= l_x3) {
                // minuend needs to be fast forwarded
                l_minuendItem++;
                b_minuendFastForward = true;
                continue;
            }
            if (l_x4 <= l_x1) {
                // subtrahend needs to be fast forwarded
                l_subtrahendItem++;
                b_subtrahendFastForward = true;
                continue;
            }
            // check if initial polygon needs to be clipped
            if ((l_x3 < l_x1) && (l_x1 < l_x4)) {
                // project onto subtrahend
                double l_slope = (l_y4 - l_y3) / (l_x4 - l_x3);
                l_subtrahendCurX = l_minuendCurX;
                l_subtrahendCurY = (l_slope * l_x1) + (l_y3 - (l_slope * l_x3));
                l_subtrahendXs.add(l_subtrahendCurX);
                l_subtrahendYs.add(l_subtrahendCurY);
            }
            if ((l_x1 < l_x3) && (l_x3 < l_x2)) {
                // project onto minuend
                double l_slope = (l_y2 - l_y1) / (l_x2 - l_x1);
                l_minuendCurX = l_subtrahendCurX;
                l_minuendCurY = (l_slope * l_x3) + (l_y1 - (l_slope * l_x1));
                l_minuendXs.add(l_minuendCurX);
                l_minuendYs.add(l_minuendCurY);
            }
            l_minuendMaxY = l_minuendCurY;
            l_minuendMinY = l_minuendCurY;
            l_subtrahendMaxY = l_subtrahendCurY;
            l_subtrahendMinY = l_subtrahendCurY;
            b_fastForwardDone = true;
        }
        // start of algorithm
        while (!b_minuendDone && !b_subtrahendDone) {
            if (!b_minuendDone && !b_minuendFastForward && b_minuendAdvanced) {
                l_x1 = x_dataset.getXValue(0, l_minuendItem);
                l_y1 = x_dataset.getYValue(0, l_minuendItem);
                l_minuendCurX = l_x1;
                l_minuendCurY = l_y1;
                if (!b_minuendAtIntersect) {
                    l_minuendXs.add(l_minuendCurX);
                    l_minuendYs.add(l_minuendCurY);
                }
                l_minuendMaxY = Math.max(l_minuendMaxY, l_y1);
                l_minuendMinY = Math.min(l_minuendMinY, l_y1);
                l_x2 = x_dataset.getXValue(0, l_minuendItem + 1);
                l_y2 = x_dataset.getYValue(0, l_minuendItem + 1);
                l_minuendNextX = l_x2;
                l_minuendNextY = l_y2;
            }
            // never updated the subtrahend if it is implied to be zero
            if (!b_impliedZeroSubtrahend && !b_subtrahendDone && !b_subtrahendFastForward && b_subtrahendAdvanced) {
                l_x3 = x_dataset.getXValue(1, l_subtrahendItem);
                l_y3 = x_dataset.getYValue(1, l_subtrahendItem);
                l_subtrahendCurX = l_x3;
                l_subtrahendCurY = l_y3;
                if (!b_subtrahendAtIntersect) {
                    l_subtrahendXs.add(l_subtrahendCurX);
                    l_subtrahendYs.add(l_subtrahendCurY);
                }
                l_subtrahendMaxY = Math.max(l_subtrahendMaxY, l_y3);
                l_subtrahendMinY = Math.min(l_subtrahendMinY, l_y3);
                l_x4 = x_dataset.getXValue(1, l_subtrahendItem + 1);
                l_y4 = x_dataset.getYValue(1, l_subtrahendItem + 1);
                l_subtrahendNextX = l_x4;
                l_subtrahendNextY = l_y4;
            }
            // deassert b_*FastForward (only matters for 1st time through loop)
            b_minuendFastForward = false;
            b_subtrahendFastForward = false;
            Double l_intersectX = null;
            Double l_intersectY = null;
            boolean b_intersect = false;
            b_minuendAtIntersect = false;
            b_subtrahendAtIntersect = false;
            // check for intersect
            if ((l_x2 == l_x4) && (l_y2 == l_y4)) {
                // check if line segments are colinear
                if ((l_x1 == l_x3) && (l_y1 == l_y3)) {
                    b_colinear = true;
                } else {
                    // the intersect is at the next point for both the minuend
                    // and subtrahend
                    l_intersectX = l_x2;
                    l_intersectY = l_y2;
                    b_intersect = true;
                    b_minuendAtIntersect = true;
                    b_subtrahendAtIntersect = true;
                }
            } else {
                // compute common denominator
                double l_denominator = ((l_y4 - l_y3) * (l_x2 - l_x1)) - ((l_x4 - l_x3) * (l_y2 - l_y1));
                // compute common deltas
                double l_deltaY = l_y1 - l_y3;
                double l_deltaX = l_x1 - l_x3;
                // compute numerators
                double l_numeratorA = ((l_x4 - l_x3) * l_deltaY) - ((l_y4 - l_y3) * l_deltaX);
                double l_numeratorB = ((l_x2 - l_x1) * l_deltaY) - ((l_y2 - l_y1) * l_deltaX);
                // check if line segments are colinear
                if ((0 == l_numeratorA) && (0 == l_numeratorB) && (0 == l_denominator)) {
                    b_colinear = true;
                } else {
                    // check if previously colinear
                    if (b_colinear) {
                        // clear colinear points and flag
                        l_minuendXs.clear();
                        l_minuendYs.clear();
                        l_subtrahendXs.clear();
                        l_subtrahendYs.clear();
                        l_polygonXs.clear();
                        l_polygonYs.clear();
                        b_colinear = false;
                        // set new starting point for the polygon
                        boolean b_useMinuend = ((l_x3 <= l_x1) && (l_x1 <= l_x4));
                        l_polygonXs.add(b_useMinuend ? l_minuendCurX : l_subtrahendCurX);
                        l_polygonYs.add(b_useMinuend ? l_minuendCurY : l_subtrahendCurY);
                    }
                }
                // compute slope components
                double l_slopeA = l_numeratorA / l_denominator;
                double l_slopeB = l_numeratorB / l_denominator;
                // test if both grahphs have a vertical rise at the same x-value
                boolean b_vertical = (l_x1 == l_x2) && (l_x3 == l_x4) && (l_x2 == l_x4);
                // check if the line segments intersect
                if (((0 < l_slopeA) && (l_slopeA <= 1) && (0 < l_slopeB) && (l_slopeB <= 1)) || b_vertical) {
                    // compute the point of intersection
                    double l_xi;
                    double l_yi;
                    if (b_vertical) {
                        b_colinear = false;
                        l_xi = l_x2;
                        l_yi = l_x4;
                    } else {
                        l_xi = l_x1 + (l_slopeA * (l_x2 - l_x1));
                        l_yi = l_y1 + (l_slopeA * (l_y2 - l_y1));
                    }
                    l_intersectX = l_xi;
                    l_intersectY = l_yi;
                    b_intersect = true;
                    b_minuendAtIntersect = ((l_xi == l_x2) && (l_yi == l_y2));
                    b_subtrahendAtIntersect = ((l_xi == l_x4) && (l_yi == l_y4));
                    // advance minuend and subtrahend to intesect
                    l_minuendCurX = l_intersectX;
                    l_minuendCurY = l_intersectY;
                    l_subtrahendCurX = l_intersectX;
                    l_subtrahendCurY = l_intersectY;
                }
            }
            if (b_intersect) {
                // create the polygon
                // add the minuend's points to polygon
                l_polygonXs.addAll(l_minuendXs);
                l_polygonYs.addAll(l_minuendYs);
                // add intersection point to the polygon
                l_polygonXs.add(l_intersectX);
                l_polygonYs.add(l_intersectY);
                // add the subtrahend's points to the polygon in reverse
                Collections.reverse(l_subtrahendXs);
                Collections.reverse(l_subtrahendYs);
                l_polygonXs.addAll(l_subtrahendXs);
                l_polygonYs.addAll(l_subtrahendYs);
                // create an actual polygon
                b_positive = (l_subtrahendMaxY <= l_minuendMaxY) && (l_subtrahendMinY <= l_minuendMinY);
                createPolygon(x_graphics, x_dataArea, x_plot, x_domainAxis, x_rangeAxis, b_positive, l_polygonXs, l_polygonYs);
                // clear the point vectors
                l_minuendXs.clear();
                l_minuendYs.clear();
                l_subtrahendXs.clear();
                l_subtrahendYs.clear();
                l_polygonXs.clear();
                l_polygonYs.clear();
                // set the maxY and minY values to intersect y-value
                double l_y = l_intersectY;
                l_minuendMaxY = l_y;
                l_subtrahendMaxY = l_y;
                l_minuendMinY = l_y;
                l_subtrahendMinY = l_y;
                // add interection point to new polygon
                l_polygonXs.add(l_intersectX);
                l_polygonYs.add(l_intersectY);
            }
            // advance the minuend if needed
            if (l_x2 <= l_x4) {
                l_minuendItem++;
                b_minuendAdvanced = true;
            } else {
                b_minuendAdvanced = false;
            }
            // advance the subtrahend if needed
            if (l_x4 <= l_x2) {
                l_subtrahendItem++;
                b_subtrahendAdvanced = true;
            } else {
                b_subtrahendAdvanced = false;
            }
            b_minuendDone = (l_minuendItem == (l_minuendItemCount - 1));
            b_subtrahendDone = (l_subtrahendItem == (l_subtrahendItemCount - 1));
        }
        // check if the final polygon needs to be clipped
        if (b_minuendDone && (l_x3 < l_x2) && (l_x2 < l_x4)) {
            // project onto subtrahend
            double l_slope = (l_y4 - l_y3) / (l_x4 - l_x3);
            l_subtrahendNextX = l_minuendNextX;
            l_subtrahendNextY = (l_slope * l_x2) + (l_y3 - (l_slope * l_x3));
        }
        if (b_subtrahendDone && (l_x1 < l_x4) && (l_x4 < l_x2)) {
            // project onto minuend
            double l_slope = (l_y2 - l_y1) / (l_x2 - l_x1);
            l_minuendNextX = l_subtrahendNextX;
            l_minuendNextY = (l_slope * l_x4) + (l_y1 - (l_slope * l_x1));
        }
        // consider last point of minuend and subtrahend for determining
        // positivity
        l_minuendMaxY = Math.max(l_minuendMaxY, l_minuendNextY);
        l_subtrahendMaxY = Math.max(l_subtrahendMaxY, l_subtrahendNextY);
        l_minuendMinY = Math.min(l_minuendMinY, l_minuendNextY);
        l_subtrahendMinY = Math.min(l_subtrahendMinY, l_subtrahendNextY);
        // add the last point of the minuned and subtrahend
        l_minuendXs.add(l_minuendNextX);
        l_minuendYs.add(l_minuendNextY);
        l_subtrahendXs.add(l_subtrahendNextX);
        l_subtrahendYs.add(l_subtrahendNextY);
        // create the polygon
        // add the minuend's points to polygon
        l_polygonXs.addAll(l_minuendXs);
        l_polygonYs.addAll(l_minuendYs);
        // add the subtrahend's points to the polygon in reverse
        Collections.reverse(l_subtrahendXs);
        Collections.reverse(l_subtrahendYs);
        l_polygonXs.addAll(l_subtrahendXs);
        l_polygonYs.addAll(l_subtrahendYs);
        // create an actual polygon
        b_positive = (l_subtrahendMaxY <= l_minuendMaxY) && (l_subtrahendMinY <= l_minuendMinY);
        createPolygon(x_graphics, x_dataArea, x_plot, x_domainAxis, x_rangeAxis, b_positive, l_polygonXs, l_polygonYs);
    }

    /**
     * Draws the visual representation of a single data item, second pass.  In
     * the second pass, the renderer draws the lines and shapes for the
     * individual points in the two series.
     *
     * @param x_graphics  the graphics device.
     * @param x_dataArea  the area within which the data is being drawn.
     * @param x_info  collects information about the drawing.
     * @param x_plot  the plot (can be used to obtain standard color
     *         information etc).
     * @param x_domainAxis  the domain (horizontal) axis.
     * @param x_rangeAxis  the range (vertical) axis.
     * @param x_dataset  the dataset.
     * @param x_series  the series index (zero-based).
     * @param x_item  the item index (zero-based).
     * @param x_crosshairState  crosshair information for the plot
     *                          ({@code null} permitted).
     */
    protected void drawItemPass1(Graphics2D x_graphics, Rectangle2D x_dataArea, PlotRenderingInfo x_info, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, XYDataset x_dataset, int x_series, int x_item, CrosshairState x_crosshairState) {
        Shape l_entityArea = null;
        EntityCollection l_entities = null;
        if (null != x_info) {
            l_entities = x_info.getOwner().getEntityCollection();
        }
        Paint l_seriesPaint = getItemPaint(x_series, x_item);
        Stroke l_seriesStroke = getItemStroke(x_series, x_item);
        x_graphics.setPaint(l_seriesPaint);
        x_graphics.setStroke(l_seriesStroke);
        PlotOrientation l_orientation = x_plot.getOrientation();
        RectangleEdge l_domainAxisLocation = x_plot.getDomainAxisEdge();
        RectangleEdge l_rangeAxisLocation = x_plot.getRangeAxisEdge();
        double l_x0 = x_dataset.getXValue(x_series, x_item);
        double l_y0 = x_dataset.getYValue(x_series, x_item);
        double l_x1 = x_domainAxis.valueToJava2D(l_x0, x_dataArea, l_domainAxisLocation);
        double l_y1 = x_rangeAxis.valueToJava2D(l_y0, x_dataArea, l_rangeAxisLocation);
        if (getShapesVisible()) {
            Shape l_shape = getItemShape(x_series, x_item);
            if (l_orientation == PlotOrientation.HORIZONTAL) {
                l_shape = ShapeUtils.createTranslatedShape(l_shape, l_y1, l_x1);
            } else {
                l_shape = ShapeUtils.createTranslatedShape(l_shape, l_x1, l_y1);
            }
            if (l_shape.intersects(x_dataArea)) {
                x_graphics.setPaint(getItemPaint(x_series, x_item));
                x_graphics.fill(l_shape);
            }
            l_entityArea = l_shape;
        }
        // add an entity for the item...
        if (null != l_entities) {
            if (null == l_entityArea) {
                l_entityArea = new Rectangle2D.Double((l_x1 - 2), (l_y1 - 2), 4, 4);
            }
            String l_tip = null;
            XYToolTipGenerator l_tipGenerator = getToolTipGenerator(x_series, x_item);
            if (null != l_tipGenerator) {
                l_tip = l_tipGenerator.generateToolTip(x_dataset, x_series, x_item);
            }
            String l_url = null;
            XYURLGenerator l_urlGenerator = getURLGenerator();
            if (null != l_urlGenerator) {
                l_url = l_urlGenerator.generateURL(x_dataset, x_series, x_item);
            }
            XYItemEntity l_entity = new XYItemEntity(l_entityArea, x_dataset, x_series, x_item, l_tip, l_url);
            l_entities.add(l_entity);
        }
        // draw the item label if there is one...
        if (isItemLabelVisible(x_series, x_item)) {
            drawItemLabel(x_graphics, l_orientation, x_dataset, x_series, x_item, l_x1, l_y1, (l_y1 < 0.0));
        }
        int datasetIndex = x_plot.indexOf(x_dataset);
        updateCrosshairValues(x_crosshairState, l_x0, l_y0, datasetIndex, l_x1, l_y1, l_orientation);
        if (0 == x_item) {
            return;
        }
        double l_x2 = x_domainAxis.valueToJava2D(x_dataset.getXValue(x_series, (x_item - 1)), x_dataArea, l_domainAxisLocation);
        double l_y2 = x_rangeAxis.valueToJava2D(x_dataset.getYValue(x_series, (x_item - 1)), x_dataArea, l_rangeAxisLocation);
        Line2D l_line = null;
        if (PlotOrientation.HORIZONTAL == l_orientation) {
            l_line = new Line2D.Double(l_y1, l_x1, l_y2, l_x2);
        } else if (PlotOrientation.VERTICAL == l_orientation) {
            l_line = new Line2D.Double(l_x1, l_y1, l_x2, l_y2);
        }
        if ((null != l_line) && l_line.intersects(x_dataArea)) {
            x_graphics.setPaint(getItemPaint(x_series, x_item));
            x_graphics.setStroke(getItemStroke(x_series, x_item));
            x_graphics.draw(l_line);
        }
    }

    /**
     * Determines if a dataset is degenerate.  A degenerate dataset is a
     * dataset where either series has less than two (2) points.
     *
     * @param x_dataset  the dataset.
     * @param x_impliedZeroSubtrahend  if false, do not check the subtrahend
     *
     * @return true if the dataset is degenerate.
     */
    private boolean isEitherSeriesDegenerate(XYDataset x_dataset, boolean x_impliedZeroSubtrahend) {
        if (x_impliedZeroSubtrahend) {
            return (x_dataset.getItemCount(0) < 2);
        }
        return ((x_dataset.getItemCount(0) < 2) || (x_dataset.getItemCount(1) < 2));
    }

    /**
     * Determines if the two (2) series are disjoint.
     * Disjoint series do not overlap in the domain space.
     *
     * @param x_dataset  the dataset.
     *
     * @return true if the dataset is degenerate.
     */
    private boolean areSeriesDisjoint(XYDataset x_dataset) {
        int l_minuendItemCount = x_dataset.getItemCount(0);
        double l_minuendFirst = x_dataset.getXValue(0, 0);
        double l_minuendLast = x_dataset.getXValue(0, l_minuendItemCount - 1);
        int l_subtrahendItemCount = x_dataset.getItemCount(1);
        double l_subtrahendFirst = x_dataset.getXValue(1, 0);
        double l_subtrahendLast = x_dataset.getXValue(1, l_subtrahendItemCount - 1);
        return ((l_minuendLast < l_subtrahendFirst) || (l_subtrahendLast < l_minuendFirst));
    }

    /**
     * Draws the visual representation of a polygon
     *
     * @param x_graphics  the graphics device.
     * @param x_dataArea  the area within which the data is being drawn.
     * @param x_plot  the plot (can be used to obtain standard color
     *                information etc).
     * @param x_domainAxis  the domain (horizontal) axis.
     * @param x_rangeAxis  the range (vertical) axis.
     * @param x_positive  indicates if the polygon is positive (true) or
     *                    negative (false).
     * @param x_xValues  a linked list of the x values (expects values to be
     *                   of type Double).
     * @param x_yValues  a linked list of the y values (expects values to be
     *                   of type Double).
     */
    private void createPolygon(Graphics2D x_graphics, Rectangle2D x_dataArea, XYPlot x_plot, ValueAxis x_domainAxis, ValueAxis x_rangeAxis, boolean x_positive, LinkedList x_xValues, LinkedList x_yValues) {
        PlotOrientation l_orientation = x_plot.getOrientation();
        RectangleEdge l_domainAxisLocation = x_plot.getDomainAxisEdge();
        RectangleEdge l_rangeAxisLocation = x_plot.getRangeAxisEdge();
        Object[] l_xValues = x_xValues.toArray();
        Object[] l_yValues = x_yValues.toArray();
        GeneralPath l_path = new GeneralPath();
        if (PlotOrientation.VERTICAL == l_orientation) {
            double l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[0]), x_dataArea, l_domainAxisLocation);
            if (this.roundXCoordinates) {
                l_x = Math.rint(l_x);
            }
            double l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[0]), x_dataArea, l_rangeAxisLocation);
            l_path.moveTo((float) l_x, (float) l_y);
            for (int i = 1; i < l_xValues.length; i++) {
                l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[i]), x_dataArea, l_domainAxisLocation);
                if (this.roundXCoordinates) {
                    l_x = Math.rint(l_x);
                }
                l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[i]), x_dataArea, l_rangeAxisLocation);
                l_path.lineTo((float) l_x, (float) l_y);
            }
            l_path.closePath();
        } else {
            double l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[0]), x_dataArea, l_domainAxisLocation);
            if (this.roundXCoordinates) {
                l_x = Math.rint(l_x);
            }
            double l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[0]), x_dataArea, l_rangeAxisLocation);
            l_path.moveTo((float) l_y, (float) l_x);
            for (int i = 1; i < l_xValues.length; i++) {
                l_x = x_domainAxis.valueToJava2D(((Double) l_xValues[i]), x_dataArea, l_domainAxisLocation);
                if (this.roundXCoordinates) {
                    l_x = Math.rint(l_x);
                }
                l_y = x_rangeAxis.valueToJava2D(((Double) l_yValues[i]), x_dataArea, l_rangeAxisLocation);
                l_path.lineTo((float) l_y, (float) l_x);
            }
            l_path.closePath();
        }
        if (l_path.intersects(x_dataArea)) {
            x_graphics.setPaint(x_positive ? getPositivePaint() : getNegativePaint());
            x_graphics.fill(l_path);
        }
    }

    /**
     * Returns a default legend item for the specified series.  Subclasses
     * should override this method to generate customised items.
     *
     * @param datasetIndex  the dataset index (zero-based).
     * @param series  the series index (zero-based).
     *
     * @return A legend item for the series.
     */
    @Override
    public LegendItem getLegendItem(int datasetIndex, int series) {
        LegendItem result = null;
        XYPlot p = getPlot();
        if (p != null) {
            XYDataset dataset = p.getDataset(datasetIndex);
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
                    Paint paint = lookupSeriesPaint(series);
                    Stroke stroke = lookupSeriesStroke(series);
                    Shape line = getLegendLine();
                    result = new LegendItem(label, description, toolTipText, urlText, line, stroke, paint);
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
        }
        return result;
    }

    /**
     * Tests this renderer for equality with an arbitrary object.
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
        if (!(obj instanceof XYDifferenceRenderer)) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        XYDifferenceRenderer that = (XYDifferenceRenderer) obj;
        if (!PaintUtils.equal(this.positivePaint, that.positivePaint)) {
            return false;
        }
        if (!PaintUtils.equal(this.negativePaint, that.negativePaint)) {
            return false;
        }
        if (this.shapesVisible != that.shapesVisible) {
            return false;
        }
        if (!ShapeUtils.equal(this.legendLine, that.legendLine)) {
            return false;
        }
        if (this.roundXCoordinates != that.roundXCoordinates) {
            return false;
        }
        return true;
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
        XYDifferenceRenderer clone = (XYDifferenceRenderer) super.clone();
        clone.legendLine = CloneUtils.clone(this.legendLine);
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
        SerialUtils.writePaint(this.positivePaint, stream);
        SerialUtils.writePaint(this.negativePaint, stream);
        SerialUtils.writeShape(this.legendLine, stream);
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
        this.positivePaint = SerialUtils.readPaint(stream);
        this.negativePaint = SerialUtils.readPaint(stream);
        this.legendLine = SerialUtils.readShape(stream);
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
public class AreaRenderer extends AbstractCategoryItemRenderer implements Cloneable, PublicCloneable, Serializable {

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
