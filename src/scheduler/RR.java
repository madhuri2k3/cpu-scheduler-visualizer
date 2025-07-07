package scheduler;

import model.Process;
import java.util.*;

public class RR implements Scheduler {
    private int timeQuantum;

    public RR(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    @Override
    public List<Process> schedule(List<Process> processList) {
        List<Process> allProcesses = new ArrayList<>();
        for (Process p : processList) {
            // Clone to avoid modifying original
            allProcesses.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
        }

        Queue<Process> queue = new LinkedList<>();
        List<Process> completed = new ArrayList<>();
        int currentTime = 0;
        int n = allProcesses.size();

        // Sort by arrival to enqueue in order
        allProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int index = 0;

        while (completed.size() < n) {
            // Enqueue all arrived processes
            while (index < n && allProcesses.get(index).arrivalTime <= currentTime) {
                queue.offer(allProcesses.get(index++));
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = queue.poll();

            
            if (current.remainingTime == current.burstTime) {
                current.startTime = Math.max(currentTime, current.arrivalTime);
                currentTime = current.startTime;
            }

            int executionTime = Math.min(timeQuantum, current.remainingTime);
            current.remainingTime -= executionTime;
            currentTime += executionTime;

            
            while (index < n && allProcesses.get(index).arrivalTime <= currentTime) {
                queue.offer(allProcesses.get(index++));
            }

            if (current.remainingTime > 0) {
                queue.offer(current); 
            } else {
                current.completionTime = currentTime;
                completed.add(current);
            }
        }

        
        completed.sort(Comparator.comparing(p -> p.pid));
        return completed;
    }
}
