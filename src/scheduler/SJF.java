package scheduler;

import model.Process;
import java.util.*;

public class SJF implements Scheduler {

    @Override
    public List<Process> schedule(List<Process> processes) {
        List<Process> scheduled = new ArrayList<>();
        List<Process> readyQueue = new ArrayList<>(processes);
        int currentTime = 0;

        while (!readyQueue.isEmpty()) {
            // Get all processes that have arrived
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

            // Picking process with shortest burst time
            Process shortest = Collections.min(available, Comparator.comparingInt(p -> p.burstTime));

            shortest.startTime = currentTime;
            currentTime += shortest.burstTime;
            shortest.completionTime = currentTime;

            scheduled.add(shortest);
            readyQueue.remove(shortest);
        }

        return scheduled;
    }
}
