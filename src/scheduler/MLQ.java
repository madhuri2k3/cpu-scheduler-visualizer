package scheduler;

import model.Process;
import java.util.*;

public class MLQ implements Scheduler {

    @Override
    public List<Process> schedule(List<Process> processList) {
        List<Process> queue0 = new ArrayList<>();
        List<Process> queue1 = new ArrayList<>();

        for (Process p : processList) {
            if (p.priority == 0) {
                queue0.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
            } else {
                queue1.add(new Process(p.pid, p.arrivalTime, p.burstTime, p.priority));
            }
        }

        
        List<Process> combinedResult = new ArrayList<>();
        RR rrScheduler = new RR(2); // TQ = 2
        List<Process> rrScheduled = rrScheduler.schedule(queue0);
        combinedResult.addAll(rrScheduled);

        
        FCFS fcfsScheduler = new FCFS();
        List<Process> fcfsScheduled = fcfsScheduler.schedule(queue1);
        combinedResult.addAll(fcfsScheduled);

        combinedResult.sort(Comparator.comparingInt(p -> p.completionTime));

        return combinedResult;
    }
}
