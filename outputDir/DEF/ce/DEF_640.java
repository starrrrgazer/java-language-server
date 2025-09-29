package DEF.ce;
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
 * ---------------
 * SerialDate.java
 * ---------------
 * (C) Copyright 2006-present, by David Gilbert and Contributors.
 *
 * Original Author:  David Gilbert;
 * Contributor(s):   -;
 *
 */
/**
 *  An abstract class that defines our requirements for manipulating dates,
 *  without tying down a particular implementation.
 *  <P>
 *  Requirement 1 : match at least what Excel does for dates;
 *  Requirement 2 : the date represented by the class is immutable;
 *  <P>
 *  Why not just use java.util.Date?  We will, when it makes sense.  At times,
 *  java.util.Date can be *too* precise - it represents an instant in time,
 *  accurate to 1/1000th of a second (with the date itself depending on the
 *  time-zone).  Sometimes we just want to represent a particular day (e.g. 21
 *  January 2015) without concerning ourselves about the time of day, or the
 *  time-zone, or anything else.  That's what we've defined SerialDate for.
 *  <P>
 *  You can call getInstance() to get a concrete subclass of SerialDate,
 *  without worrying about the exact implementation.
 */
public abstract class SerialDate implements Comparable, Serializable, MonthConstants {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = -293716040467423637L;

    /**
     * Date format symbols.
     */
    public static final DateFormatSymbols DATE_FORMAT_SYMBOLS = new SimpleDateFormat().getDateFormatSymbols();

    /**
     * The serial number for 1 January 1900.
     */
    public static final int SERIAL_LOWER_BOUND = 2;

    /**
     * The serial number for 31 December 9999.
     */
    public static final int SERIAL_UPPER_BOUND = 2958465;

    /**
     * The lowest year value supported by this date format.
     */
    public static final int MINIMUM_YEAR_SUPPORTED = 1900;

    /**
     * The highest year value supported by this date format.
     */
    public static final int MAXIMUM_YEAR_SUPPORTED = 9999;

    /**
     * Useful constant for Monday. Equivalent to java.util.Calendar.MONDAY.
     */
    public static final int MONDAY = Calendar.MONDAY;

    /**
     * Useful constant for Tuesday. Equivalent to java.util.Calendar.TUESDAY.
     */
    public static final int TUESDAY = Calendar.TUESDAY;

    /**
     * Useful constant for Wednesday. Equivalent to
     * java.util.Calendar.WEDNESDAY.
     */
    public static final int WEDNESDAY = Calendar.WEDNESDAY;

    /**
     * Useful constant for Thrusday. Equivalent to java.util.Calendar.THURSDAY.
     */
    public static final int THURSDAY = Calendar.THURSDAY;

    /**
     * Useful constant for Friday. Equivalent to java.util.Calendar.FRIDAY.
     */
    public static final int FRIDAY = Calendar.FRIDAY;

    /**
     * Useful constant for Saturday. Equivalent to java.util.Calendar.SATURDAY.
     */
    public static final int SATURDAY = Calendar.SATURDAY;

    /**
     * Useful constant for Sunday. Equivalent to java.util.Calendar.SUNDAY.
     */
    public static final int SUNDAY = Calendar.SUNDAY;

    /**
     * The number of days in each month in non leap years.
     */
    static final int[] LAST_DAY_OF_MONTH = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    /**
     * The number of days in a (non-leap) year up to the end of each month.
     */
    static final int[] AGGREGATE_DAYS_TO_END_OF_MONTH = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };

    /**
     * The number of days in a year up to the end of the preceding month.
     */
    static final int[] AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = { 0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };

    /**
     * The number of days in a leap year up to the end of each month.
     */
    static final int[] LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_MONTH = { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };

    /**
     * The number of days in a leap year up to the end of the preceding month.
     */
    static final int[] LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = { 0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };

    /**
     * A useful constant for referring to the first week in a month.
     */
    public static final int FIRST_WEEK_IN_MONTH = 1;

    /**
     * A useful constant for referring to the second week in a month.
     */
    public static final int SECOND_WEEK_IN_MONTH = 2;

    /**
     * A useful constant for referring to the third week in a month.
     */
    public static final int THIRD_WEEK_IN_MONTH = 3;

    /**
     * A useful constant for referring to the fourth week in a month.
     */
    public static final int FOURTH_WEEK_IN_MONTH = 4;

    /**
     * A useful constant for referring to the last week in a month.
     */
    public static final int LAST_WEEK_IN_MONTH = 0;

    /**
     * Useful range constant.
     */
    public static final int INCLUDE_NONE = 0;

    /**
     * Useful range constant.
     */
    public static final int INCLUDE_FIRST = 1;

    /**
     * Useful range constant.
     */
    public static final int INCLUDE_SECOND = 2;

    /**
     * Useful range constant.
     */
    public static final int INCLUDE_BOTH = 3;

    /**
     * Useful constant for specifying a day of the week relative to a fixed
     * date.
     */
    public static final int PRECEDING = -1;

    /**
     * Useful constant for specifying a day of the week relative to a fixed
     * date.
     */
    public static final int NEAREST = 0;

    /**
     * Useful constant for specifying a day of the week relative to a fixed
     * date.
     */
    public static final int FOLLOWING = 1;

    /**
     * A description for the date.
     */
    private String description;

    /**
     * Default constructor.
     */
    protected SerialDate() {
    }

    /**
     * Returns {@code true} if the supplied integer code represents a
     * valid day-of-the-week, and {@code false} otherwise.
     *
     * @param code  the code being checked for validity.
     *
     * @return {@code true} if the supplied integer code represents a
     *         valid day-of-the-week, and {@code false} otherwise.
     */
    public static boolean isValidWeekdayCode(int code) {
        switch(code) {
            case SUNDAY:
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
            case SATURDAY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Converts the supplied string to a day of the week.
     *
     * @param s  a string representing the day of the week.
     *
     * @return {@code -1} if the string is not convertable, the day of
     *         the week otherwise.
     */
    public static int stringToWeekdayCode(String s) {
        final String[] shortWeekdayNames = DATE_FORMAT_SYMBOLS.getShortWeekdays();
        final String[] weekDayNames = DATE_FORMAT_SYMBOLS.getWeekdays();
        int result = -1;
        s = s.trim();
        for (int i = 0; i < weekDayNames.length; i++) {
            if (s.equals(shortWeekdayNames[i])) {
                result = i;
                break;
            }
            if (s.equals(weekDayNames[i])) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * Returns a string representing the supplied day-of-the-week.
     * <P>
     * Need to find a better approach.
     *
     * @param weekday  the day of the week.
     *
     * @return a string representing the supplied day-of-the-week.
     */
    public static String weekdayCodeToString(int weekday) {
        final String[] weekdays = DATE_FORMAT_SYMBOLS.getWeekdays();
        return weekdays[weekday];
    }

    /**
     * Returns an array of month names.
     *
     * @return an array of month names.
     */
    public static String[] getMonths() {
        return getMonths(false);
    }

    /**
     * Returns an array of month names.
     *
     * @param shortened  a flag indicating that shortened month names should
     *                   be returned.
     *
     * @return an array of month names.
     */
    public static String[] getMonths(boolean shortened) {
        if (shortened) {
            return DATE_FORMAT_SYMBOLS.getShortMonths();
        } else {
            return DATE_FORMAT_SYMBOLS.getMonths();
        }
    }

    /**
     * Returns true if the supplied integer code represents a valid month.
     *
     * @param code  the code being checked for validity.
     *
     * @return {@code true} if the supplied integer code represents a
     *         valid month.
     */
    public static boolean isValidMonthCode(int code) {
        switch(code) {
            case JANUARY:
            case FEBRUARY:
            case MARCH:
            case APRIL:
            case MAY:
            case JUNE:
            case JULY:
            case AUGUST:
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
            case DECEMBER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns the quarter for the specified month.
     *
     * @param code  the month code (1-12).
     *
     * @return the quarter that the month belongs to.
     */
    public static int monthCodeToQuarter(int code) {
        switch(code) {
            case JANUARY:
            case FEBRUARY:
            case MARCH:
                return 1;
            case APRIL:
            case MAY:
            case JUNE:
                return 2;
            case JULY:
            case AUGUST:
            case SEPTEMBER:
                return 3;
            case OCTOBER:
            case NOVEMBER:
            case DECEMBER:
                return 4;
            default:
                throw new IllegalArgumentException("SerialDate.monthCodeToQuarter: invalid month code.");
        }
    }

    /**
     * Returns a string representing the supplied month.
     * <P>
     * The string returned is the long form of the month name taken from the
     * default locale.
     *
     * @param month  the month.
     *
     * @return a string representing the supplied month.
     */
    public static String monthCodeToString(int month) {
        return monthCodeToString(month, false);
    }

    /**
     * Returns a string representing the supplied month.
     * <P>
     * The string returned is the long or short form of the month name taken
     * from the default locale.
     *
     * @param month  the month.
     * @param shortened  if {@code true} return the abbreviation of the month.
     *
     * @return a string representing the supplied month.
     */
    public static String monthCodeToString(int month, boolean shortened) {
        // check arguments...
        if (!isValidMonthCode(month)) {
            throw new IllegalArgumentException("SerialDate.monthCodeToString: month outside valid range.");
        }
        final String[] months;
        if (shortened) {
            months = DATE_FORMAT_SYMBOLS.getShortMonths();
        } else {
            months = DATE_FORMAT_SYMBOLS.getMonths();
        }
        return months[month - 1];
    }

    /**
     * Converts a string to a month code.
     * <P>
     * This method will return one of the constants JANUARY, FEBRUARY, ...,
     * DECEMBER that corresponds to the string.  If the string is not
     * recognised, this method returns -1.
     *
     * @param s  the string to parse.
     *
     * @return {@code -1} if the string is not parseable, the month of the
     *         year otherwise.
     */
    public static int stringToMonthCode(String s) {
        final String[] shortMonthNames = DATE_FORMAT_SYMBOLS.getShortMonths();
        final String[] monthNames = DATE_FORMAT_SYMBOLS.getMonths();
        int result = -1;
        s = s.trim();
        // first try parsing the string as an integer (1-12)...
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            // suppress
        }
        // now search through the month names...
        if ((result < 1) || (result > 12)) {
            for (int i = 0; i < monthNames.length; i++) {
                if (s.equals(shortMonthNames[i])) {
                    result = i + 1;
                    break;
                }
                if (s.equals(monthNames[i])) {
                    result = i + 1;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns true if the supplied integer code represents a valid
     * week-in-the-month, and false otherwise.
     *
     * @param code  the code being checked for validity.
     * @return {@code true} if the supplied integer code represents a
     *         valid week-in-the-month.
     */
    public static boolean isValidWeekInMonthCode(int code) {
        switch(code) {
            case FIRST_WEEK_IN_MONTH:
            case SECOND_WEEK_IN_MONTH:
            case THIRD_WEEK_IN_MONTH:
            case FOURTH_WEEK_IN_MONTH:
            case LAST_WEEK_IN_MONTH:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determines whether the specified year is a leap year.
     *
     * @param yyyy  the year (in the range 1900 to 9999).
     *
     * @return {@code true} if the specified year is a leap year.
     */
    public static boolean isLeapYear(int yyyy) {
        if ((yyyy % 4) != 0) {
            return false;
        } else if ((yyyy % 400) == 0) {
            return true;
        } else if ((yyyy % 100) == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns the number of leap years from 1900 to the specified year
     * INCLUSIVE.
     * <P>
     * Note that 1900 is not a leap year.
     *
     * @param yyyy  the year (in the range 1900 to 9999).
     *
     * @return the number of leap years from 1900 to the specified year.
     */
    public static int leapYearCount(int yyyy) {
        int leap4 = (yyyy - 1896) / 4;
        int leap100 = (yyyy - 1800) / 100;
        int leap400 = (yyyy - 1600) / 400;
        return leap4 - leap100 + leap400;
    }

    /**
     * Returns the number of the last day of the month, taking into account
     * leap years.
     *
     * @param month  the month.
     * @param yyyy  the year (in the range 1900 to 9999).
     *
     * @return the number of the last day of the month.
     */
    public static int lastDayOfMonth(int month, int yyyy) {
        final int result = LAST_DAY_OF_MONTH[month];
        if (month != FEBRUARY) {
            return result;
        } else if (isLeapYear(yyyy)) {
            return result + 1;
        } else {
            return result;
        }
    }

    /**
     * Creates a new date by adding the specified number of days to the base
     * date.
     *
     * @param days  the number of days to add (can be negative).
     * @param base  the base date.
     *
     * @return a new date.
     */
    public static SerialDate addDays(int days, SerialDate base) {
        int serialDayNumber = base.toSerial() + days;
        return SerialDate.createInstance(serialDayNumber);
    }

    /**
     * Creates a new date by adding the specified number of months to the base
     * date.
     * <P>
     * If the base date is close to the end of the month, the day on the result
     * may be adjusted slightly:  31 May + 1 month = 30 June.
     *
     * @param months  the number of months to add (can be negative).
     * @param base  the base date.
     *
     * @return a new date.
     */
    public static SerialDate addMonths(int months, SerialDate base) {
        int yy = (12 * base.getYYYY() + base.getMonth() + months - 1) / 12;
        if (yy < MINIMUM_YEAR_SUPPORTED || yy > MAXIMUM_YEAR_SUPPORTED) {
            throw new IllegalArgumentException("Call to addMonths resulted in unsupported year");
        }
        int mm = (12 * base.getYYYY() + base.getMonth() + months - 1) % 12 + 1;
        int dd = Math.min(base.getDayOfMonth(), SerialDate.lastDayOfMonth(mm, yy));
        return SerialDate.createInstance(dd, mm, yy);
    }

    /**
     * Creates a new date by adding the specified number of years to the base
     * date.
     *
     * @param years  the number of years to add (can be negative).
     * @param base  the base date.
     *
     * @return A new date.
     */
    public static SerialDate addYears(int years, SerialDate base) {
        int baseY = base.getYYYY();
        int baseM = base.getMonth();
        int baseD = base.getDayOfMonth();
        int targetY = baseY + years;
        if (targetY < MINIMUM_YEAR_SUPPORTED || targetY > MAXIMUM_YEAR_SUPPORTED) {
            throw new IllegalArgumentException("Call to addYears resulted in unsupported year");
        }
        int targetD = Math.min(baseD, SerialDate.lastDayOfMonth(baseM, targetY));
        return SerialDate.createInstance(targetD, baseM, targetY);
    }

    /**
     * Returns the latest date that falls on the specified day-of-the-week and
     * is BEFORE the base date.
     *
     * @param targetWeekday  a code for the target day-of-the-week.
     * @param base  the base date.
     *
     * @return the latest date that falls on the specified day-of-the-week and
     *         is BEFORE the base date.
     */
    public static SerialDate getPreviousDayOfWeek(int targetWeekday, SerialDate base) {
        // check arguments...
        if (!SerialDate.isValidWeekdayCode(targetWeekday)) {
            throw new IllegalArgumentException("Invalid day-of-the-week code.");
        }
        // find the date...
        int adjust;
        int baseDOW = base.getDayOfWeek();
        if (baseDOW > targetWeekday) {
            adjust = Math.min(0, targetWeekday - baseDOW);
        } else {
            adjust = -7 + Math.max(0, targetWeekday - baseDOW);
        }
        return SerialDate.addDays(adjust, base);
    }

    /**
     * Returns the earliest date that falls on the specified day-of-the-week
     * and is AFTER the base date.
     *
     * @param targetWeekday  a code for the target day-of-the-week.
     * @param base  the base date.
     *
     * @return the earliest date that falls on the specified day-of-the-week
     *         and is AFTER the base date.
     */
    public static SerialDate getFollowingDayOfWeek(int targetWeekday, SerialDate base) {
        // check arguments...
        if (!SerialDate.isValidWeekdayCode(targetWeekday)) {
            throw new IllegalArgumentException("Invalid day-of-the-week code.");
        }
        // find the date...
        int adjust;
        int baseDOW = base.getDayOfWeek();
        if (baseDOW > targetWeekday) {
            adjust = 7 + Math.min(0, targetWeekday - baseDOW);
        } else {
            adjust = Math.max(0, targetWeekday - baseDOW);
        }
        return SerialDate.addDays(adjust, base);
    }

    /**
     * Returns the date that falls on the specified day-of-the-week and is
     * CLOSEST to the base date.
     *
     * @param targetDOW  a code for the target day-of-the-week.
     * @param base  the base date.
     *
     * @return the date that falls on the specified day-of-the-week and is
     *         CLOSEST to the base date.
     */
    public static SerialDate getNearestDayOfWeek(int targetDOW, SerialDate base) {
        // check arguments...
        if (!SerialDate.isValidWeekdayCode(targetDOW)) {
            throw new IllegalArgumentException("Invalid day-of-the-week code.");
        }
        // find the date...
        final int baseDOW = base.getDayOfWeek();
        int adjust = -Math.abs(targetDOW - baseDOW);
        if (adjust >= 4) {
            adjust = 7 - adjust;
        }
        if (adjust <= -4) {
            adjust = 7 + adjust;
        }
        return SerialDate.addDays(adjust, base);
    }

    /**
     * Rolls the date forward to the last day of the month.
     *
     * @param base  the base date.
     *
     * @return a new serial date.
     */
    public SerialDate getEndOfCurrentMonth(SerialDate base) {
        int last = SerialDate.lastDayOfMonth(base.getMonth(), base.getYYYY());
        return SerialDate.createInstance(last, base.getMonth(), base.getYYYY());
    }

    /**
     * Returns a string corresponding to the week-in-the-month code.
     * <P>
     * Need to find a better approach.
     *
     * @param count  an integer code representing the week-in-the-month.
     *
     * @return a string corresponding to the week-in-the-month code.
     */
    public static String weekInMonthToString(int count) {
        switch(count) {
            case SerialDate.FIRST_WEEK_IN_MONTH:
                return "First";
            case SerialDate.SECOND_WEEK_IN_MONTH:
                return "Second";
            case SerialDate.THIRD_WEEK_IN_MONTH:
                return "Third";
            case SerialDate.FOURTH_WEEK_IN_MONTH:
                return "Fourth";
            case SerialDate.LAST_WEEK_IN_MONTH:
                return "Last";
            default:
                return "SerialDate.weekInMonthToString(): invalid code.";
        }
    }

    /**
     * Returns a string representing the supplied 'relative'.
     * <P>
     * Need to find a better approach.
     *
     * @param relative  a constant representing the 'relative'.
     *
     * @return a string representing the supplied 'relative'.
     */
    public static String relativeToString(int relative) {
        switch(relative) {
            case SerialDate.PRECEDING:
                return "Preceding";
            case SerialDate.NEAREST:
                return "Nearest";
            case SerialDate.FOLLOWING:
                return "Following";
            default:
                return "ERROR : Relative To String";
        }
    }

    /**
     * Factory method that returns an instance of some concrete subclass of
     * {@link SerialDate}.
     *
     * @param day  the day (1-31).
     * @param month  the month (1-12).
     * @param yyyy  the year (in the range 1900 to 9999).
     *
     * @return An instance of {@link SerialDate}.
     */
    public static SerialDate createInstance(int day, int month, int yyyy) {
        return new SpreadsheetDate(day, month, yyyy);
    }

    /**
     * Factory method that returns an instance of some concrete subclass of
     * {@link SerialDate}.
     *
     * @param serial  the serial number for the day (1 January 1900 = 2).
     *
     * @return a instance of SerialDate.
     */
    public static SerialDate createInstance(int serial) {
        return new SpreadsheetDate(serial);
    }

    /**
     * Factory method that returns an instance of a subclass of SerialDate.
     *
     * @param date  A Java date object.
     *
     * @return a instance of SerialDate.
     */
    public static SerialDate createInstance(java.util.Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return new SpreadsheetDate(calendar.get(Calendar.DATE), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
    }

    /**
     * Returns the serial number for the date, where 1 January 1900 = 2 (this
     * corresponds, almost, to the numbering system used in Microsoft Excel for
     * Windows and Lotus 1-2-3).
     *
     * @return the serial number for the date.
     */
    public abstract int toSerial();

    /**
     * Returns a java.util.Date.  Since java.util.Date has more precision than
     * SerialDate, we need to define a convention for the 'time of day'.
     *
     * @return this as {@code java.util.Date}.
     */
    public abstract java.util.Date toDate();

    /**
     * Returns the description that is attached to the date.  It is not
     * required that a date have a description, but for some applications it
     * is useful.
     *
     * @return The description (possibly {@code null}).
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the description for the date.
     *
     * @param description  the description for this date ({@code null}
     *                     permitted).
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Converts the date to a string.
     *
     * @return  a string representation of the date.
     */
    @Override
    public String toString() {
        return getDayOfMonth() + "-" + SerialDate.monthCodeToString(getMonth()) + "-" + getYYYY();
    }

    /**
     * Returns the year (assume a valid range of 1900 to 9999).
     *
     * @return the year.
     */
    public abstract int getYYYY();

    /**
     * Returns the month (January = 1, February = 2, March = 3).
     *
     * @return the month of the year.
     */
    public abstract int getMonth();

    /**
     * Returns the day of the month.
     *
     * @return the day of the month.
     */
    public abstract int getDayOfMonth();

    /**
     * Returns the day of the week.
     *
     * @return the day of the week.
     */
    public abstract int getDayOfWeek();

    /**
     * Returns the difference (in days) between this date and the specified
     * 'other' date.
     * <P>
     * The result is positive if this date is after the 'other' date and
     * negative if it is before the 'other' date.
     *
     * @param other  the date being compared to.
     *
     * @return the difference between this and the other date.
     */
    public abstract int compare(SerialDate other);

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return {@code true} if this SerialDate represents the same date as
     *         the specified SerialDate.
     */
    public abstract boolean isOn(SerialDate other);

    /**
     * Returns true if this SerialDate represents an earlier date compared to
     * the specified SerialDate.
     *
     * @param other  The date being compared to.
     *
     * @return {@code true} if this SerialDate represents an earlier date
     *         compared to the specified SerialDate.
     */
    public abstract boolean isBefore(SerialDate other);

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return {@code true} if this SerialDate represents the same date
     *         as the specified SerialDate.
     */
    public abstract boolean isOnOrBefore(SerialDate other);

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return {@code true} if this SerialDate represents the same date
     *         as the specified SerialDate.
     */
    public abstract boolean isAfter(SerialDate other);

    /**
     * Returns true if this SerialDate represents the same date as the
     * specified SerialDate.
     *
     * @param other  the date being compared to.
     *
     * @return {@code true} if this SerialDate represents the same date
     *         as the specified SerialDate.
     */
    public abstract boolean isOnOrAfter(SerialDate other);

    /**
     * Returns {@code true} if this {@link SerialDate} is within the
     * specified range (INCLUSIVE).  The date order of d1 and d2 is not
     * important.
     *
     * @param d1  a boundary date for the range.
     * @param d2  the other boundary date for the range.
     *
     * @return A boolean.
     */
    public abstract boolean isInRange(SerialDate d1, SerialDate d2);

    /**
     * Returns {@code true} if this {@link SerialDate} is within the
     * specified range (caller specifies whether the end-points are
     * included).  The date order of d1 and d2 is not important.
     *
     * @param d1  a boundary date for the range.
     * @param d2  the other boundary date for the range.
     * @param include  a code that controls whether the start and end
     *                 dates are included in the range.
     *
     * @return A boolean.
     */
    public abstract boolean isInRange(SerialDate d1, SerialDate d2, int include);

    /**
     * Returns the latest date that falls on the specified day-of-the-week and
     * is BEFORE this date.
     *
     * @param targetDOW  a code for the target day-of-the-week.
     *
     * @return the latest date that falls on the specified day-of-the-week and
     *         is BEFORE this date.
     */
    public SerialDate getPreviousDayOfWeek(int targetDOW) {
        return getPreviousDayOfWeek(targetDOW, this);
    }

    /**
     * Returns the earliest date that falls on the specified day-of-the-week
     * and is AFTER this date.
     *
     * @param targetDOW  a code for the target day-of-the-week.
     *
     * @return the earliest date that falls on the specified day-of-the-week
     *         and is AFTER this date.
     */
    public SerialDate getFollowingDayOfWeek(int targetDOW) {
        return getFollowingDayOfWeek(targetDOW, this);
    }

    /**
     * Returns the nearest date that falls on the specified day-of-the-week.
     *
     * @param targetDOW  a code for the target day-of-the-week.
     *
     * @return the nearest date that falls on the specified day-of-the-week.
     */
    public SerialDate getNearestDayOfWeek(int targetDOW) {
        return getNearestDayOfWeek(targetDOW, this);
    }
}
