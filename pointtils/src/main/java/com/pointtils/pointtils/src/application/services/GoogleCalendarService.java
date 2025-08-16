package com.pointtils.pointtils.src.application.services;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarService {

    private final Calendar googleCalendar;
    private static final String CALENDAR_ID = "primary"; // ou um ID específico do calendário

    public List<Event> getUpcomingEvents(int maxResults) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        
        Events events = googleCalendar.events().list(CALENDAR_ID)
                .setMaxResults(maxResults)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
                
        return events.getItems();
    }

    public Event createEvent(String summary, String description, LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        // Data e hora de início
        DateTime start = new DateTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        event.setStart(new EventDateTime().setDateTime(start).setTimeZone("America/Sao_Paulo"));

        // Data e hora de fim
        DateTime end = new DateTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        event.setEnd(new EventDateTime().setDateTime(end).setTimeZone("America/Sao_Paulo"));

        Event createdEvent = googleCalendar.events().insert(CALENDAR_ID, event).execute();
        log.info("Evento criado com ID: {}", createdEvent.getId());
        
        return createdEvent;
    }

    public Event updateEvent(String eventId, String summary, String description, LocalDateTime startTime, LocalDateTime endTime) throws IOException {
        Event event = googleCalendar.events().get(CALENDAR_ID, eventId).execute();

        event.setSummary(summary);
        event.setDescription(description);

        DateTime start = new DateTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        event.setStart(new EventDateTime().setDateTime(start).setTimeZone("America/Sao_Paulo"));

        DateTime end = new DateTime(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        event.setEnd(new EventDateTime().setDateTime(end).setTimeZone("America/Sao_Paulo"));

        Event updatedEvent = googleCalendar.events().update(CALENDAR_ID, eventId, event).execute();
        log.info("Evento atualizado: {}", updatedEvent.getId());
        
        return updatedEvent;
    }

    public void deleteEvent(String eventId) throws IOException {
        googleCalendar.events().delete(CALENDAR_ID, eventId).execute();
        log.info("Evento deletado: {}", eventId);
    }

    public Event getEventById(String eventId) throws IOException {
        return googleCalendar.events().get(CALENDAR_ID, eventId).execute();
    }
}