package model;

public class Process {
    public String pid;
    public int arrivalTime;
    public int burstTime;
    public int remainingTime;
    public int priority;

    public int completionTime;
    public int startTime;

    public Process(String pid, int arrivalTime, int burstTime, int priority) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
    }
}
