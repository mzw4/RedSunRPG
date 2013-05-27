package redsun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import redsun.events.Event;
import redsun.events.EventType;

public class EventManager {

	// represents all listeners and the EventType(s) they are registed to
	private HashMap<EventType, ArrayList<EventListener>> listeners;
	// a queue of events
	private LinkedList<Event> events;

	public EventManager() {
		listeners = new HashMap<EventType, ArrayList<EventListener>>();
		for (EventType type : EventType.values())
			listeners.put(type, new ArrayList<EventListener>());
		events = new LinkedList<Event>();
	}

	// registers a listener to a each event in its list, if not already registered
	// to it
	public void register(EventListener listener, ArrayList<EventType> types) {
		if (listener != null && types != null)
			for (EventType type : types)
				if (!listeners.get(type).contains(listener))
					listeners.get(type).add(listener);
	}

	// registers a listener to an event
	public void register(EventListener listener, EventType type) {
		if (listener != null && type != null
				&& !listeners.get(type).contains(listener))
			listeners.get(type).add(listener);
	}

	// unregisters a listener from an EventType
	public void unregister(EventListener listener, EventType type) {
		if (type == null)
			for (EventType t : listeners.keySet())
				listeners.get(t).remove(listener);
		else
			listeners.get(type).remove(listener);
	}

	// called on every gameloop iteration
	public void update() {
		dispatch(events);
	}

	// adds an event to the dispatch queue
	public void addEvent(Event e) {
		events.offer(e);
	}

	// dispatches an event immediately
	public void sendEvent(Event e) {
		for (EventListener l : listeners.get(e.getType())) {
			l.handleEvent(e);
		}
	}

	// dispatches all events from the queue in the order they were received
	public void dispatch(LinkedList<Event> events) {
		if (events != null) {
			while (!events.isEmpty()) {
				Event e = events.poll();
				for (EventListener l : listeners.get(e.getType()))
					l.handleEvent(e);
			}
		}
	}

	public ArrayList<EventListener> getListeners(EventType type) {
		return listeners.get(type);
	}

	public String toString() {
		return "Listeners: " + listeners + "\nEvents: " + events;
	}

	public static void addd() {
		System.out.println("hi");
	}

}
