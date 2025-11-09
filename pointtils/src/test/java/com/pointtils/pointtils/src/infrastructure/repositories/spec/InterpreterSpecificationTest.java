package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import com.pointtils.pointtils.src.application.dto.requests.InterpreterSpecificationFilterDTO;
import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.enums.AppointmentStatus;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;
import com.pointtils.pointtils.src.core.domain.entities.enums.InterpreterModality;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
class InterpreterSpecificationTest {
    @Test
    void shouldBuildPredicateWithGender() {
        // Mocks do JPA Criteria
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate genderPredicate = mock(Predicate.class);

        // Comportamento dos mocks
        when(cb.conjunction()).thenReturn(basePredicate);
        when(cb.equal(root.get("gender"), Gender.MALE)).thenReturn(genderPredicate);
        when(cb.and(basePredicate, genderPredicate)).thenReturn(genderPredicate);

        // Criar DTO de filtro com gender
        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();

        // Chamar o método agora com DTO
        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);
        filterDTO.setGender(Gender.MALE); // importante!

        Predicate result = spec.toPredicate(root, query, cb);

        // Verificações
        assertThat(result).isEqualTo(genderPredicate);
        verify(cb).equal(root.get("gender"), Gender.MALE);
    }

    @Test
    void shouldBuildPredicateWithAllModality() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(basePredicate);

        Predicate modalityPredicate = mock(Predicate.class);
        Path modalityPath = mock(Path.class);
        when(root.get("modality")).thenReturn(modalityPath);
        when(modalityPath.in(InterpreterModality.ALL, InterpreterModality.ONLINE,
                InterpreterModality.PERSONALLY))
                .thenReturn(modalityPredicate);

        Predicate resultPredicate = mock(Predicate.class);
        when(cb.and(basePredicate, modalityPredicate)).thenReturn(resultPredicate);

        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();
        filterDTO.setModality(InterpreterModality.ALL);

        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);

        Predicate result = spec.toPredicate(root, query, cb);
        assertEquals(resultPredicate, result);
    }

    @Test
    void shouldBuildPredicateWithOnlineModality() {
        // Mocks do JPA Criteria
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(basePredicate);

        // Mock do predicate de modalidade
        Predicate modalityPredicate = mock(Predicate.class);
        Path modalityPath = mock(Path.class);
        when(root.get("modality")).thenReturn(modalityPath);
        when(modalityPath.in(InterpreterModality.ALL, InterpreterModality.ONLINE))
                .thenReturn(modalityPredicate);

        // Mock do predicate final
        Predicate resultPredicate = mock(Predicate.class);
        when(cb.and(basePredicate, modalityPredicate)).thenReturn(resultPredicate);

        // Criar DTO com modalidade ONLINE
        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();
        filterDTO.setModality(InterpreterModality.ONLINE);

        // Chamar Specification
        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);
        Predicate result = spec.toPredicate(root, query, cb);

        // Verificações
        assertEquals(resultPredicate, result);
    }

    @Test
    void shouldBuildPredicateWithCity() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate cityPredicate = mock(Predicate.class);

        Join<Interpreter, ?> locationJoin = mock(Join.class);
        Path cityPath = mock(Path.class);
        Expression<String> lowerCityExpression = mock(Expression.class);

        when(cb.conjunction()).thenReturn(basePredicate);
        when(root.join("locations", JoinType.LEFT)).thenReturn((Join) locationJoin);
        when(locationJoin.get("city")).thenReturn(cityPath);
        when(cb.lower(cityPath)).thenReturn(lowerCityExpression);
        when(cb.like(lowerCityExpression, "%são paulo%")).thenReturn(cityPredicate);
        when(cb.and(basePredicate, cityPredicate)).thenReturn(cityPredicate);

        // Preencher o DTO
        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();
        filterDTO.setCity("São Paulo");

        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(cityPredicate);
        verify(cb).like(lowerCityExpression, "%são paulo%");
    }

    @Test
    void shouldBuildPredicateWithUF() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate ufPredicate = mock(Predicate.class);

        Join<Interpreter, ?> locationJoin = mock(Join.class);
        Path ufPath = mock(Path.class);

        when(cb.conjunction()).thenReturn(basePredicate);
        when(root.join("locations", JoinType.LEFT)).thenReturn((Join) locationJoin);
        when(locationJoin.get("uf")).thenReturn(ufPath);
        when(cb.equal(ufPath, "RS")).thenReturn(ufPredicate);
        when(cb.and(basePredicate, ufPredicate)).thenReturn(ufPredicate);

        // Preencher o DTO
        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();
        filterDTO.setUf("RS");

        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(ufPredicate);
        verify(cb).equal(ufPath, "RS");
    }

    @Test
    void shouldBuildPredicateWithAvailableDate() {
        // Mocks do JPA Criteria
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(basePredicate);

        // Mock do join de schedules
        Join scheduleJoin = mock(Join.class);
        when(root.join("schedules", JoinType.LEFT)).thenReturn(scheduleJoin);

        Path dayPath = mock(Path.class);
        Path startTimePath = mock(Path.class);
        Path endTimePath = mock(Path.class);

        when(scheduleJoin.get("day")).thenReturn(dayPath);
        when(scheduleJoin.get("startTime")).thenReturn(startTimePath);
        when(scheduleJoin.get("endTime")).thenReturn(endTimePath);

        Predicate dayPredicate = mock(Predicate.class);
        Predicate startPredicate = mock(Predicate.class);
        Predicate endPredicate = mock(Predicate.class);
        Predicate schedulePredicate = mock(Predicate.class);

        LocalDateTime availableDate = LocalDateTime.of(2025, 10, 6, 10, 0);
        LocalTime requestedStart = availableDate.toLocalTime();
        LocalTime requestedEnd = requestedStart.plusHours(1);

        when(cb.equal(dayPath, DayOfWeek.MON)).thenReturn(dayPredicate);
        when(cb.lessThanOrEqualTo(startTimePath, requestedStart)).thenReturn(startPredicate);
        when(cb.greaterThanOrEqualTo(endTimePath, requestedEnd)).thenReturn(endPredicate);
        when(cb.and(dayPredicate, startPredicate, endPredicate)).thenReturn(schedulePredicate);
        when(cb.and(basePredicate, schedulePredicate)).thenReturn(schedulePredicate);

        // Mock do Subquery de appointments
        Subquery<UUID> subquery = mock(Subquery.class);
        when(query.subquery(UUID.class)).thenReturn(subquery);

        Root appointmentSubRoot = mock(Root.class);
        when(subquery.from(Appointment.class)).thenReturn(appointmentSubRoot);

        Path interpreterPath = mock(Path.class);
        Path appointmentIdPath = mock(Path.class);
        when(appointmentSubRoot.get("interpreter")).thenReturn(interpreterPath);
        when(interpreterPath.get("id")).thenReturn(appointmentIdPath);
        when(subquery.select(appointmentIdPath)).thenReturn(subquery);

        Path statusPath = mock(Path.class);
        when(appointmentSubRoot.get("status")).thenReturn(statusPath);
        Predicate statusPredicate = mock(Predicate.class);
        when(statusPath.in(AppointmentStatus.ACCEPTED, AppointmentStatus.COMPLETED))
                .thenReturn(statusPredicate);

        Path datePath = mock(Path.class);
        when(appointmentSubRoot.get("date")).thenReturn(datePath);
        Predicate datePredicate = mock(Predicate.class);
        when(cb.equal(datePath, availableDate.toLocalDate())).thenReturn(datePredicate);

        Predicate subqueryPredicate = mock(Predicate.class);
        when(cb.and(statusPredicate, datePredicate)).thenReturn(subqueryPredicate);
        when(subquery.where(subqueryPredicate)).thenReturn(subquery);

        // Mock do root.get("id").in(subquery)
        Path<Object> idPath = mock(Path.class);
        when(root.get("id")).thenReturn(idPath);

        Predicate inSubqueryPredicate = mock(Predicate.class);
        when(idPath.in(subquery)).thenReturn(inSubqueryPredicate);
        when(cb.not(inSubqueryPredicate)).thenReturn(inSubqueryPredicate);
        when(cb.and(schedulePredicate, inSubqueryPredicate)).thenReturn(inSubqueryPredicate);

        // DTO
        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();
        filterDTO.setAvailableDate(availableDate);

        // Executa Specification
        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);

        // Executa e verifica se não lança exception
        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
    }

    @Test
    void shouldBuildPredicateWithName() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate namePredicate = mock(Predicate.class);

        Path namePath = mock(Path.class);
        Expression<String> lowerNameExpression = mock(Expression.class);

        when(cb.conjunction()).thenReturn(basePredicate);
        when(root.get("name")).thenReturn(namePath);
        when(cb.lower(namePath)).thenReturn(lowerNameExpression);
        when(cb.like(lowerNameExpression, "%souza%")).thenReturn(namePredicate);
        when(cb.and(basePredicate, namePredicate)).thenReturn(namePredicate);

        // Preencher DTO com nome
        InterpreterSpecificationFilterDTO filterDTO = new InterpreterSpecificationFilterDTO();
        filterDTO.setName("Souza");

        Specification<Interpreter> spec = InterpreterSpecification.filter(filterDTO);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(namePredicate);
        verify(cb).like(lowerNameExpression, "%souza%");
    }

}