package com.pointtils.pointtils.src.infrastructure.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pointtils.pointtils.src.infrastructure.repositories.AppointmentRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AppointmentStatusScheduler {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Scheduled(fixedRate = 1800000) // Executa a cada 30 minutos (30 * 60 * 1000 ms)
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
}