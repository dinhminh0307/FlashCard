package com.example.flashcard.services;

import android.content.Context;

import com.example.flashcard.models.Events;
import com.example.flashcard.repo.EventsRepository;
import com.example.flashcard.repo.FlashCardRepository;

import java.util.List;

public class EventServices {
    private EventsRepository eventsRepository;

    public EventServices(Context context) {
        eventsRepository = new EventsRepository(context);
    }

    public void saveEventTimeTable(Events events) throws Exception{
        eventsRepository.insertEvent(events);
    }

    public List<Events> getAllEvents() {
        return eventsRepository.getAllEvents();
    }

    public void deleteEvent(String name, String date, String time) throws Exception {
        eventsRepository.deleteEventTimeSlot(name, date, time);
    }
}
