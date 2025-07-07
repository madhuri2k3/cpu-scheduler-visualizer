package scheduler;

import model.Process;
import java.util.*;

public class SRT implements Scheduler {

    @Override
    public List<Process> schedule(List<Process> processList) {
        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processList) {
            // Cloning process to avoid modifying original list
            allProcesses.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }

        List<Process> completed = new ArrayList<>();
        int currentTime = 0;
        Process currentProcess = null;

        while (completed.size() < allProcesses.size()) {
            // Filter available processes
            List<Process> available = new ArrayList<>();
            for (Process p : allProcesses) {
                if (p.arrivalTime <= currentTime && !completed.contains(p) && p.remainingTime > 0) {
                    available.add(p);
                }
            }

            if (available.isEmpty()) {
                currentTime++;
                continue;
            }

            // Choose the one with shortest remaining time
            Process shortest = Collections.min(available, Comparator.comparingInt(p -> p.remainingTime));

            if (shortest != currentProcess) {
                shortest.startTime = currentTime;
                currentProcess = shortest;
            }

            // Run process for 1 unit
            shortest.remainingTime--;
            currentTime++;

            // If finished, mark completion time
            if (shortest.remainingTime == 0) {
                shortest.completionTime = currentTime;
                completed.add(shortest);
            }
        }

        // Sort back to original arrival order or PID order
        completed.sort(Comparator.comparing(p -> p.pid));
        return completed;
    }
}
