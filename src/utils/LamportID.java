package utils;

public class LamportID {
    public static Long maxId = 0L;

    /**
     * @return the current clock in the process
     */
    public static Long getCurrentNumber() {
        return maxId;
    }

    /**
     * @return the clock for next step
     */
    public static Long getNextNumber() {
        maxId += 1;
        return maxId;
    }
    
    public static void setCurrentNumber(Long num) {
        maxId = num;
    }
}
