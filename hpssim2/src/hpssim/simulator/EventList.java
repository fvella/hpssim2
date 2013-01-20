package hpssim.simulator;


import java.util.ArrayList;

/**
 *
 * @author Igor Neri <igor@cimice.net>
 */
public class EventList {

    public EventList() {
        list = new ArrayList<Event>();
    }

    public void insertEvent(Event e) {
        if (list.size()==0) {
            list.add(e);
        } else {
            if (list.get(list.size() - 1) != null && e.time >= list.get(list.size() - 1).time) {
                list.add(e);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    if (e.time < list.get(i).time) {
                        list.add(i, e);
                        break;
                    }
                }
            }
        }
    }
    
    public Event remove(int jobID) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).job !=null && list.get(i).job.id == jobID) {
                Event e = list.get(i);
                list.remove(i);
                return e;
            }
        }
        return null;
    }

    public Event search(int type) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).type == type) {
                Event e = list.get(i);
                list.remove(i);
                return e;
            }
        }
        return null;
    }

    public Event pop() {
        if (list.isEmpty()) {
            return null;
        } else {
            return list.remove(0);
        }
    }
    public ArrayList<Event> list;
}

