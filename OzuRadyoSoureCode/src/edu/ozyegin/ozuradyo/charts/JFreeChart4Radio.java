
package edu.ozyegin.ozuradyo.charts;

import edu.ozyegin.ozuradyo.core.Statistics;
import java.awt.BorderLayout;
import java.awt.Graphics;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author ugurk
 */
public class JFreeChart4Radio extends JPanel{
    XYSeries upload = new XYSeries("Upload");
    XYSeries download = new XYSeries("Download");
    XYSeriesCollection dataset = new XYSeriesCollection();
    private int limit = 20;
    
    @Override
    public void paintComponent(Graphics g){
        updateChart();
        this.dataset.addSeries(upload);
        this.dataset.addSeries(download);
    }
    
    public void updateChart(){
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Network Traffic for OzuRadio", // title
            "Time", // x Label
            "Data(bytes)", // y Label
            dataset,
            PlotOrientation.VERTICAL,
            true, // Show Legend
            true, // Use tooltips
            false
        );
        chart.setTextAntiAlias(true);
        chart.setAntiAlias(true);
        
        this.setLayout(new BorderLayout());
        ChartPanel cp = new ChartPanel(chart);
        cp.setSize(WIDTH, WIDTH);
        this.add(cp);
        this.validate();
    }
    
    /**
     * 
     * @param up    Uploads per time interval.
     * @param down  Downloads per time interval.
     * @param time  Time interval that will be read from {@link Statistics}.
     */
    public void addData(Long up, Long down, Long time){
        if(upload.getItemCount() >= limit){
            upload.remove(0);
            download.remove(0);
        }
        upload.add(time, up);
        download.add(time, down);
    }

}
