# CPU Scheduling Visualizer

A Java-based simulation tool that visualizes and compares various CPU scheduling algorithms using an interactive GUI.

#Features

- Visualizes CPU scheduling algorithms:
  - FCFS
  - SJF (Non-preemptive & Preemptive)
  - Priority Scheduling (Preemptive)
  - Round Robin
  - Multilevel Queue (MLQ)
  - Multilevel Feedback Queue (MLFQ)
- Dark Mode & Animated Gantt Chart
- Metrics display: Waiting Time, Turnaround Time, Response Time (per process + averages)
- Export Gantt chart as PNG

#Technologies Used

- Java
- Swing (GUI)
- OOP & Data Structures

## 🚀 How to Run

```bash
javac model/*.java scheduler/*.java ui/*.java *.java
java Main
