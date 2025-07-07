package ui;

import model.Process;
import scheduler.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {
    private final DefaultListModel<Process> processListModel = new DefaultListModel<>();
    private final JList<Process> processJList = new JList<>(processListModel);
    private final JComboBox<String> algorithmDropdown;
    private final List<String> algorithmOptions = List.of(
            "FCFS", "SJF", "SRT", "Priority", "Preemptive Priority", "Round Robin", "MLQ", "MLFQ"
    );

    private final JTextField pidField = new JTextField(4);
    private final JTextField arrivalField = new JTextField(4);
    private final JTextField burstField = new JTextField(4);
    private final JTextField priorityField = new JTextField(4);
    private final JTextField quantumField = new JTextField(4);

    private Scheduler scheduler;
    private final GanttChartPanel chartPanel;
    private boolean darkMode = false;

    private final Color LIGHT_BG = Color.WHITE;
    private final Color DARK_BG = new Color(34, 34, 34);
    private final Color LIGHT_TEXT = Color.BLACK;
    private final Color DARK_TEXT = Color.LIGHT_GRAY;
    private final Color PRIMARY_COLOR = new Color(66, 133, 244);

    private List<Process> scheduledResult = new ArrayList<>();

    public ControlPanel(GanttChartPanel chartPanel) {
        this.chartPanel = chartPanel;
        setLayout(new BorderLayout());
        setBackground(LIGHT_BG);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        // ----- Input Panel -----
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(LIGHT_BG);

        addLabeledField(inputPanel, "PID:", pidField, "Process ID");
        addLabeledField(inputPanel, "Arrival:", arrivalField, "Time at which process arrives");
        addLabeledField(inputPanel, "Burst:", burstField, "CPU Burst Time");
        addLabeledField(inputPanel, "Priority:", priorityField, "Lower value = higher priority");
        addLabeledField(inputPanel, "Quantum:", quantumField, "Time slice (used in RR only)");

        JButton addButton = styledButton("➕ Add Process");
        inputPanel.add(addButton);

        // ----- Control Panel -----
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(LIGHT_BG);

        algorithmDropdown = new JComboBox<>(algorithmOptions.toArray(new String[0]));
        algorithmDropdown.setFont(labelFont);
        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algorithmDropdown);

        JButton runButton = styledButton("🚀 Run Scheduler");
        JButton playButton = styledButton("▶ Play Animation");
        JButton stopButton = styledButton("⏹ Stop");
        JButton clearButton = styledButton("🗑 Clear All");
        JButton exportButton = styledButton("💾 Export PNG");
        JButton themeToggle = styledButton("🌓 Toggle Dark Mode");

        controlPanel.add(runButton);
        controlPanel.add(playButton);
        controlPanel.add(stopButton);
        controlPanel.add(clearButton);
        controlPanel.add(exportButton);
        controlPanel.add(themeToggle);

        // ----- Process List Panel -----
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(LIGHT_BG);
        listPanel.add(new JLabel("Processes:"), BorderLayout.NORTH);
        processJList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        listPanel.add(new JScrollPane(processJList), BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(LIGHT_BG);
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(listPanel, BorderLayout.CENTER);

        // ----- Button Actions -----
        addButton.addActionListener(e -> {
            try {
                String pid = pidField.getText().trim();
                int arrival = Integer.parseInt(arrivalField.getText().trim());
                int burst = Integer.parseInt(burstField.getText().trim());
                int priority = Integer.parseInt(priorityField.getText().trim());

                processListModel.addElement(new Process(pid, arrival, burst, priority));

                pidField.setText("");
                arrivalField.setText("");
                burstField.setText("");
                priorityField.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.");
            }
        });

        runButton.addActionListener(e -> runScheduler(false));
        playButton.addActionListener(e -> runScheduler(true));
        stopButton.addActionListener(e -> chartPanel.stopAnimation());

        clearButton.addActionListener(e -> {
            processListModel.clear();
            chartPanel.updateProcesses(null);
            chartPanel.repaint();
        });

        exportButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Gantt Chart");
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String file = chooser.getSelectedFile().getAbsolutePath();
                if (!file.endsWith(".png")) file += ".png";
                chartPanel.exportAsImage(file);
            }
        });

        themeToggle.addActionListener(e -> toggleTheme(this));
    }

    private void runScheduler(boolean animate) {
        List<Process> inputList = new ArrayList<>();
        for (int i = 0; i < processListModel.size(); i++) {
            Process p = processListModel.get(i);
            inputList.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }

        String selected = (String) algorithmDropdown.getSelectedItem();
        scheduler = getScheduler(selected);

        if (scheduler == null) {
            JOptionPane.showMessageDialog(this, "Unknown algorithm selected.");
            return;
        }

        scheduledResult = scheduler.schedule(inputList);
        if (animate) {
            chartPanel.animateSchedule(scheduledResult);
        } else {
            chartPanel.updateProcesses(scheduledResult);
            chartPanel.repaint();
        }

        double totalWaiting = 0;
        double totalTurnaround = 0;

        for (Process p : scheduledResult) {
            int turnaround = p.completionTime - p.arrivalTime;
            int waiting = turnaround - p.burstTime;
            totalTurnaround += turnaround;
            totalWaiting += waiting;
        }

        int n = scheduledResult.size();
        String msg = String.format("Avg Waiting Time: %.2f\nAvg Turnaround Time: %.2f",
                totalWaiting / n, totalTurnaround / n);
        JOptionPane.showMessageDialog(this, msg, "Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void toggleTheme(Component component) {
        darkMode = !darkMode;
        Color bg = darkMode ? DARK_BG : LIGHT_BG;
        Color fg = darkMode ? DARK_TEXT : LIGHT_TEXT;

        component.setBackground(bg);
        if (component instanceof JLabel || component instanceof JButton || component instanceof JTextField || component instanceof JComboBox) {
            component.setForeground(fg);
        }

        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                toggleTheme(child);
            }
        }

        chartPanel.setDarkMode(darkMode);
        chartPanel.repaint();
    }

    private void addLabeledField(JPanel panel, String label, JTextField field, String tooltip) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setToolTipText(tooltip);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl);
        panel.add(field);
    }

    private JButton styledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private Scheduler getScheduler(String name) {
        return switch (name) {
            case "FCFS" -> new FCFS();
            case "SJF" -> new SJF();
            case "SRT" -> new SRT();
            case "Priority" -> new Priority();
            case "Preemptive Priority" -> new PreemptivePriority();
            case "Round Robin" -> {
                int q = 2;
                try {
                    q = Integer.parseInt(quantumField.getText().trim());
                } catch (Exception ignored) {}
                yield new RR(q);
            }
            case "MLQ" -> new MLQ();
            case "MLFQ" -> new MLFQ();
            default -> null;
        };
    }
}
