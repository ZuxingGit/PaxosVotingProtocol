package utils;

import java.util.Objects;

public class LamportID {
    private static Long maxId;

    public LamportID(Long maxInCurrentProcess) {
        this.maxId = 0L;
    }

    /**
     * @return the current clock in the process
     */
    public Long getCurrentNumber() {
        return maxId;
    }
    

    /**
     * @param eventID
     * @return the clock of next step
     */
    public Long getNextNumber(Long eventID) {
        this.maxId += 1;
        return maxId;
    }
}
