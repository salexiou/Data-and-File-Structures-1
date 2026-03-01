package structure;

/* A class with static member variables and methods that can be used to count access on disk. 
* safe.
*/ 

public class AccessCounter {
	
	/**
	 * variable holding our counters. We support up to 1000 counters
	 */
	private static long[] counters = new long[1000];
	
	
	/**
	 * Resets the internal counter of counterIndex to zero
	 */
	public static void resetCounter(int counterIndex) {
		if (counterIndex < counters.length)
			counters[counterIndex] = 0;
	}
	
	/**
	 * Returns the current count for given counterIndex
	 * 
	 * @return the current count for given counterIndex
	 */
	public static long getCount(int counterIndex) {
		if (counterIndex  < counters.length )
			return counters[counterIndex];
		return 0;
	}
	
	/**
	 * Increases the current count of counterIndex by 1. Returns always true so that it can be used
	 * in boolean statements
	 * 
	 * @return always true
	 */
	public static boolean increaseCounter(int counterIndex) {
		if (counterIndex  < counters.length )
			counters[counterIndex]++;
		return true;
	}
	
	/**
	 * Increases the current count of counter given by counterIndex by step. Returns always true so that it can be
	 * used in boolean statements. Step could be negative. It is up to the specific
	 * usage scenario whether this is desirable or not.
	 * 
	 * @param step The amount to increase the counter
	 * @return always true
	 */
	public static boolean increaseCounter(int counterIndex, int step) {
		if (counterIndex  < counters.length )
			counters[counterIndex] = counters[counterIndex] + step;
		return true;
	}

	public static long[] getCounters() {
		return counters;
	}

	public static void setCounters(long[] counters) {
		AccessCounter.counters = counters;
	}
	
	
	

}
