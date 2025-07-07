package scheduler;

import model.Process;
import java.util.*;

public class MLFQ implements Scheduler {

    @Override
    public List<Process> schedule(List<Process> processList) {
        Queue<Process> q0 = new LinkedList<>(); // RR(2)
        Queue<Process> q1 = new LinkedList<>(); // RR(4)
        Queue<Process> q2 = new LinkedList<>(); // FCFS

        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processList) {
            allProcesses.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }

        allProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int index = 0;
        List<Process> completed = new ArrayList<>();

        while (completed.size() < allProcesses.size()) {
            
            while (index < allProcesses.size() && allProcesses.get(index).arrivalTime <= currentTime) {
                q0.offer(allProcesses.get(index++));
            }

            Process current = null;

            if (!q0.isEmpty()) {
                current = q0.poll();
                current = runRR(current, 2, currentTime); // RR Q2
            } else if (!q1.isEmpty()) {
                current = q1.poll();
                current = runRR(current, 4, currentTime); // RR Q4
            } else if (!q2.isEmpty()) {
                current = q2.poll();
                current.startTime = Math.max(current.arrivalTime, currentTime);
                currentTime = current.startTime + current.remainingTime;
                current.remainingTime = 0;
                current.completionTime = currentTime;
                completed.add(current);
                continue;
            } else {
                currentTime++;
                continue;
            }

            
            while (index < allProcesses.size() && allProcesses.get(index).arrivalTime <= currentTime) {
                q0.offer(allProcesses.get(index++));
            }

            if (current.remainingTime > 0) {
                
                if (current.priority == 0) {
                    current.priority = 1;
                    q1.offer(current);
                } else {
                    current.priority = 2;
                    q2.offer(current);
                }
            } else {
                current.completionTime = currentTime;
                completed.add(current);
            }
        }

        completed.sort(Comparator.comparing(p -> p.pid));
        return completed;
    }

    private Process runRR(Process p, int quantum, int currentTime) {
        if (p.remainingTime == p.burstTime) {
            p.startTime = Math.max(currentTime, p.arrivalTime);
        }
        int actualRun = Math.min(p.remainingTime, quantum);
        p.remainingTime -= actualRun;
        return p;
    }
}
