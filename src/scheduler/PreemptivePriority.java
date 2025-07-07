package scheduler;

import model.Process;
import java.util.*;

public class PreemptivePriority implements Scheduler {

    @Override
    public List<Process> schedule(List<Process> processList) {
        List<Process> processes = new ArrayList<>();
        for (Process p : processList) {
            processes.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }

        List<Process> result = new ArrayList<>();
        int time = 0, completed = 0;
        int n = processes.size();

        Process currentProcess = null;
        int minPriority = Integer.MAX_VALUE;

        while (completed < n) {
            Process best = null;
            for (Process p : processes) {
                if (p.arrivalTime <= time && p.remainingTime > 0) {
                    if (p.priority < minPriority ||
                       (p.priority == minPriority && p.arrivalTime < (best != null ? best.arrivalTime : Integer.MAX_VALUE))) {
                        minPriority = p.priority;
                        best = p;
                    }
                }
            }

            if (best != null) {
                if (best != currentProcess) {
                    currentProcess = best;
                    if (currentProcess.startTime == -1) {
                        currentProcess.startTime = time;
                    }
                }
                currentProcess.remainingTime--;
                time++;

                if (currentProcess.remainingTime == 0) {
                    currentProcess.completionTime = time;
                    result.add(currentProcess);
                    completed++;
                    minPriority = Integer.MAX_VALUE; // Reset for next 
                    currentProcess = null;
                }
            } else {
                time++; // idle time
            }
        }

        result.sort(Comparator.comparing(p -> p.pid));
        return result;
    }
}
