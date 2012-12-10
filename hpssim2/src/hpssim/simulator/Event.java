/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hpssim.simulator;


/**
 *
 * @author Igor Neri <igor@cimice.net>
 */
public class Event {

    public Event(Job _job, int _type, int _time) {
        job = _job;
        type = _type;
        time = _time;
    }
    public Event(Job _job, int _type, int _time, int _indexQueue) {
    	job = _job;
        type = _type;
        time = _time;
        indexQueue = _indexQueue;
	}
	public Job job;
    public int time;
    public int type;
    public int indexQueue;
    public static final int ENQUEUE = 0;
    public static final int RUN = 1;
    public static final int REQUEUE = 2;
    public static final int FINALIZE = 3;    
    public static final int RESCHEDULE = 4;
    public static final int NULL = 5;

}
