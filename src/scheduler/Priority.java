package scheduler;

import model.Process;
import java.util.*;

public class Priority implements Scheduler {

    @Override
    public List<Process> schedule(List<Process> processList) {
        List<Process> scheduled = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>(processList);
        int currentTime = 0;

        while (!readyQueue.isEmpty()) {
            
            List<Process> available = new ArrayList<>();
            for (Process p : readyQueue) {
                if (p.arrivalTime <= currentTime) {
                    available.add(p);
                }
            }

            if (available.isEmpty()) {
                currentTime++;
                continue;
            }

            
            Process highestPriority = Collections.min(
                available,
                Comparator.comparingInt(p -> p.priority)
            );

            highestPriority.startTime = currentTime;
            currentTime += highestPriority.burstTime;
            highestPriority.completionTime = currentTime;

            scheduled.add(highestPriority);
            readyQueue.remove(highestPriority);
        }

        return scheduled;
    }
}
