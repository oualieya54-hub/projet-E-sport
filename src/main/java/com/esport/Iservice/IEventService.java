package com.esport.Iservice;

import com.esport.Models.Event;
import java.util.List;

public interface IEventService {
    void addEvent(Event event);
    void updateEvent(Event event);
    void deleteEvent(int id);
    Event getEventById(int id);
    List<Event> getAllEvents();
}
