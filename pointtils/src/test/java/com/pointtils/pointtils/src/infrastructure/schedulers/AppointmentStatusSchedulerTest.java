package com.pointtils.pointtils.src.infrastructure.schedulers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentStatusSchedulerTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentStatusScheduler scheduler;

    @Test
    void shouldCallRepositoryToUpdateExpiredAppointments() {
        // Given
        when(appointmentRepository.updateExpiredPendingAppointmentsToCanceled()).thenReturn(2);
        when(appointmentRepository.updateExpiredAcceptedAppointmentsToCompleted()).thenReturn(3);

        // When
        scheduler.updateExpiredAppointments();

        // Then
        verify(appointmentRepository, times(1)).updateExpiredPendingAppointmentsToCanceled();
        verify(appointmentRepository, times(1)).updateExpiredAcceptedAppointmentsToCompleted();
    }

    @Test
    void shouldHandleExceptionGracefully() {
        // Given
        when(appointmentRepository.updateExpiredPendingAppointmentsToCanceled()).thenThrow(new RuntimeException("Database error"));

        // When
        scheduler.updateExpiredAppointments(); // Should not throw exception

        // Then
        verify(appointmentRepository, times(1)).updateExpiredPendingAppointmentsToCanceled();
    }

    @Test
    void shouldHandleNoExpiredAppointments() {
        // Given
        when(appointmentRepository.updateExpiredPendingAppointmentsToCanceled()).thenReturn(0);
        when(appointmentRepository.updateExpiredAcceptedAppointmentsToCompleted()).thenReturn(0);

        // When
        scheduler.updateExpiredAppointments();

        // Then
        verify(appointmentRepository, times(1)).updateExpiredPendingAppointmentsToCanceled();
        verify(appointmentRepository, times(1)).updateExpiredAcceptedAppointmentsToCompleted();
    }
}