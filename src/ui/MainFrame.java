package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("CPU Scheduling Visualizer");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        GanttChartPanel chartPanel = new GanttChartPanel(null);
        ControlPanel controlPanel = new ControlPanel(chartPanel);

        add(controlPanel, BorderLayout.NORTH);
        add(chartPanel, BorderLayout.CENTER);
    }
}