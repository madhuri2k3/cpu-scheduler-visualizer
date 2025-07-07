package scheduler;

import java.util.List;
import model.Process;

public interface Scheduler {
    List<Process> schedule(List<Process> processes);
}

