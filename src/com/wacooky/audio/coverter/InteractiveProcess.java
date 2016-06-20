package com.wacooky.audio.coverter;

import java.io.IOException;

/**
 * A process consists of 1. initialize, 2 run steps 3. terminate.
 * The initialization involves variety of instance variable setting.
 * So, no common interface is defined. 
 * 
 * Byte and Second are units independent of data structure.
 * 
 * @author fujimori
 *
 */
public interface InteractiveProcess {
	/**
	 * Get total duration time
	 * @return second
	 */
	public double getTimeTotal();
	/**
	 * Set position of processing.
	 * @param start			in second.
	 * @param end			in second.
	 * @throws IOException	if seekTime fails.
	 */
	public void seekTime(double start, double end) throws IOException;
	/**
	 * Get current processing time.
	 * 
	 * @return double seconds of elapsed time of this process.
	 */
	public double getTimeNow();
	
	public long getCountTotal();
	public void seekCount(long start, long end ) throws IOException;
	public long getCountNow();	
	
	/**
	 * Process one unit. The size of an unit depends on implementation but
	 * it should be large enough for efficiency and small enough for human interaction.
	 * 
	 * @return int	amount of processed data in byte
	 * @throws IOException IO error in reading or writing of file in this process
	 */
	public int process() throws IOException;
	/**
	 * Do closing file kind of things.
	 * @throws IOException	error of reading or writing file.
	 */
	public void terminate() throws IOException;
}
