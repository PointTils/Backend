package com.pointtils.pointtils.src.application.controllers;

import com.google.api.services.calendar.model.Event;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pointtils.pointtils.src.application.services.GoogleCalendarService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Google Calendar integration endpoints")
public class CalendarController {

    private final GoogleCalendarService calendarService;

    @GetMapping("/events")
    @Operation(summary = "Get upcoming events", description = "Retrieves a list of upcoming calendar events")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved events"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Event>> getUpcomingEvents(
            @Parameter(description = "Maximum number of events to return", example = "10")
            @RequestParam(defaultValue = "10") int maxResults) {
        try {
            List<Event> events = calendarService.getUpcomingEvents(maxResults);
            return ResponseEntity.ok(events);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/events")
    @Operation(summary = "Create event", description = "Creates a new calendar event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event created successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Event> createEvent(
            @Parameter(description = "Event summary/title", required = true)
            @RequestParam String summary,
            @Parameter(description = "Event description")
            @RequestParam(required = false) String description,
            @Parameter(description = "Event start time (ISO format)", required = true, example = "2025-08-16T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "Event end time (ISO format)", required = true, example = "2025-08-16T11:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Event event = calendarService.createEvent(summary, description, startTime, endTime);
            return ResponseEntity.ok(event);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/events/{eventId}")
    @Operation(summary = "Update event", description = "Updates an existing calendar event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event updated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Event> updateEvent(
            @Parameter(description = "ID of the event to update", required = true)
            @PathVariable String eventId,
            @Parameter(description = "Updated event summary/title", required = true)
            @RequestParam String summary,
            @Parameter(description = "Updated event description")
            @RequestParam(required = false) String description,
            @Parameter(description = "Updated start time (ISO format)", required = true, example = "2025-08-16T10:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "Updated end time (ISO format)", required = true, example = "2025-08-16T11:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            Event event = calendarService.updateEvent(eventId, summary, description, startTime, endTime);
            return ResponseEntity.ok(event);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Delete event", description = "Deletes a calendar event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteEvent(
            @Parameter(description = "ID of the event to delete", required = true)
            @PathVariable String eventId) {
        try {
            calendarService.deleteEvent(eventId);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/events/{eventId}")
    @Operation(summary = "Get event by ID", description = "Retrieves a specific calendar event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Event> getEvent(
            @Parameter(description = "ID of the event to retrieve", required = true)
            @PathVariable String eventId) {
        try {
            Event event = calendarService.getEventById(eventId);
            return ResponseEntity.ok(event);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
