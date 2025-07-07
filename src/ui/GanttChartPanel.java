package ui;

import model.Process;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GanttChartPanel extends JPanel {
    private List<Process> processes;
    private final List<Rectangle> ganttBlocks = new ArrayList<>();
    private final List<String> tooltips = new ArrayList<>();
    private boolean darkMode = false;
    private int animationTime = 0;
    private Timer animationTimer;

    public GanttChartPanel(List<Process> processes) {
        this.processes = processes;
        setPreferredSize(new Dimension(800, 200));
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    public void updateProcesses(List<Process> newProcesses) {
        this.processes = newProcesses;
        this.animationTime = Integer.MAX_VALUE; // disable animation
        repaint();
    }

    public void animateSchedule(List<Process> newProcesses) {
        this.processes = newProcesses;
        this.animationTime = 0;
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(200, e -> {
            animationTime++;
            repaint();
            int maxTime = 0;
            for (Process p : processes) {
                maxTime = Math.max(maxTime, p.completionTime);
            }
            if (animationTime >= maxTime) {
                animationTimer.stop();
                animationTime = Integer.MAX_VALUE; // stop animation mode
            }
        });
        animationTimer.start();
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTime = Integer.MAX_VALUE;
            repaint();
        }
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
        setBackground(darkMode ? new Color(34, 34, 34) : Color.WHITE);
        repaint();
    }

    public void exportAsImage(String filePath) {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        g2.dispose();

        try {
            ImageIO.write(image, "png", new File(filePath));
            JOptionPane.showMessageDialog(this, "Gantt Chart saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (processes == null || processes.isEmpty()) return;

        ganttBlocks.clear();
        tooltips.clear();

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 20;
        int y = 30;
        int height = 40;

        int totalTime = 0;
        for (Process p : processes) {
            totalTime = Math.max(totalTime, p.completionTime);
        }

        int panelWidth = getWidth() - 40;
        double unitWidth = (double) panelWidth / totalTime;

        for (Process p : processes) {
            if (p.startTime >= animationTime) continue; // skip if not yet started in animation

            int start = p.startTime;
            int end = Math.min(p.completionTime, animationTime);
            int startX = x + (int) (start * unitWidth);
            int endX = x + (int) (end * unitWidth);
            int width = endX - startX;

            if (width <= 0) continue;

            Color blockColor = getColorForProcess(p.pid);
            g.setColor(blockColor);
            g.fillRect(startX, y, width, height);

            g.setColor(darkMode ? Color.WHITE : Color.BLACK);
            g.drawRect(startX, y, width, height);

            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(p.pid, startX + 5, y + height / 2 + 5);

            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(String.valueOf(p.startTime), startX - 5, y + height + 15);

            Rectangle rect = new Rectangle(startX, y, width, height);
            ganttBlocks.add(rect);
            tooltips.add(p.pid + " | Arrival: " + p.arrivalTime + " | Burst: " + p.burstTime + " | Priority: " + p.priority);
        }

        if (!processes.isEmpty()) {
            int finalTime = processes.get(processes.size() - 1).completionTime;
            if (animationTime >= finalTime) {
                int finalX = x + (int) (finalTime * unitWidth);
                g.setColor(darkMode ? Color.LIGHT_GRAY : Color.BLACK);
                g.drawString(String.valueOf(finalTime), finalX - 5, y + height + 15);
            }
        }

        drawMetrics(g2d);
    }

    private void drawMetrics(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        int baseY = 100;
        int stepY = 15;
        g2d.setColor(darkMode ? Color.WHITE : Color.BLACK);
        g2d.drawString("PID   AT  BT  CT  WT  TAT  RT", 20, baseY);

        int totalWT = 0, totalTAT = 0, totalRT = 0;

        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
            int wt = p.completionTime - p.arrivalTime - p.burstTime;
            int tat = p.completionTime - p.arrivalTime;
            int rt = p.startTime - p.arrivalTime;

            totalWT += wt;
            totalTAT += tat;
            totalRT += rt;

            String row = String.format("%-5s %-3d %-3d %-3d %-3d %-4d %-3d", p.pid, p.arrivalTime, p.burstTime, p.completionTime, wt, tat, rt);
            g2d.drawString(row, 20, baseY + ((i + 1) * stepY));
        }

        int n = processes.size();
        if (n > 0) {
            int finalY = baseY + ((n + 2) * stepY);
            g2d.drawString(String.format("Avg WT: %.2f, Avg TAT: %.2f, Avg RT: %.2f",
                    totalWT * 1.0 / n, totalTAT * 1.0 / n, totalRT * 1.0 / n), 20, finalY);
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        for (int i = 0; i < ganttBlocks.size(); i++) {
            if (ganttBlocks.get(i).contains(e.getPoint())) {
                return tooltips.get(i);
            }
        }
        return null;
    }

    private Color getColorForProcess(String pid) {
        int hash = Math.abs(pid.hashCode());
        int r = (hash >> 3) % 128 + 64;
        int g = (hash >> 5) % 128 + 64;
        int b = (hash >> 7) % 128 + 64;
        return new Color(r, g, b);
    }
}
