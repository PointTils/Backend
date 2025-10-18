package com.pointtils.pointtils.src.infrastructure.schedulers;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.core.domain.entities.Parameters;
import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;
import com.pointtils.pointtils.src.infrastructure.repositories.ParametersRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppointmentStatusScheduler {

    private final AppointmentRepository appointmentRepository;
    private final ParametersRepository parametersRepository;

    private static final String SCHEDULER_INTERVAL_KEY = "appointment_status_scheduler_interval";

    @Scheduled(fixedDelayString = "#{@appointmentStatusScheduler.getFixedRateMs()}")
    @Transactional
    public void updateExpiredAppointments() {
        try {
            // Atualiza PENDING para CANCELED
            int canceledCount = appointmentRepository.updateExpiredPendingAppointmentsToCanceled();
            if (canceledCount > 0) {
                log.info("Updated {} PENDING appointments to CANCELED status", canceledCount);
            }
            
            // Atualiza ACCEPTED para COMPLETED
            int completedCount = appointmentRepository.updateExpiredAcceptedAppointmentsToCompleted();
            if (completedCount > 0) {
                log.info("Updated {} ACCEPTED appointments to COMPLETED status", completedCount);
            }
            
            if (canceledCount == 0 && completedCount == 0) {
                log.debug("No expired appointments found to update");
            }
        } catch (Exception e) {
            log.error("Error updating expired appointments", e);
        }
    }

    public String getFixedRateMs() {
        return parametersRepository.findByKey(SCHEDULER_INTERVAL_KEY)
                .map(Parameters::getValue)
                .orElse("1800000"); 
    }
}