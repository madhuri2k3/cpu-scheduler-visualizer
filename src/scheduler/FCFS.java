package scheduler;

import java.util.*;
import model.Process;

public class FCFS implements Scheduler {
    @Override
    public List<Process> schedule(List<Process> processes) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int currentTime = 0;
        for (Process p : processes) {
            p.startTime = Math.max(currentTime, p.arrivalTime);
            currentTime = p.startTime + p.burstTime;
            p.completionTime = currentTime;
        }
        return processes;
    }
}
