package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import com.pointtils.pointtils.src.core.domain.entities.Appointment;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
class InterpreterSpecificationTest {

    @Test
    void shouldBuildPredicateWithGender() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate genderPredicate = mock(Predicate.class);

        when(cb.conjunction()).thenReturn(basePredicate);
        when(cb.equal(root.get("gender"), Gender.MALE)).thenReturn(genderPredicate);
        when(cb.and(basePredicate, genderPredicate)).thenReturn(genderPredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(null, null, null, null,
                null, Gender.MALE, null, null);

        Predicate result = spec.toPredicate(root, query, cb);

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
        when(modalityPath.in(InterpreterModality.ALL, InterpreterModality.ONLINE, InterpreterModality.PERSONALLY))
                .thenReturn(modalityPredicate);

        Predicate resultPredicate = mock(Predicate.class);
        when(cb.and(basePredicate, modalityPredicate)).thenReturn(resultPredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(InterpreterModality.ALL, null, null,
                null, null, null, null, null);

        Predicate result = spec.toPredicate(root, query, cb);
        assertEquals(resultPredicate, result);
    }

    @Test
    void shouldBuildPredicateWithOnlineModality() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(basePredicate);

        Predicate modalityPredicate = mock(Predicate.class);
        Path modalityPath = mock(Path.class);
        when(root.get("modality")).thenReturn(modalityPath);
        when(modalityPath.in(InterpreterModality.ALL, InterpreterModality.ONLINE))
                .thenReturn(modalityPredicate);

        Predicate resultPredicate = mock(Predicate.class);
        when(cb.and(basePredicate, modalityPredicate)).thenReturn(resultPredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(InterpreterModality.ONLINE, null, null,
                null, null, null, null, null);

        Predicate result = spec.toPredicate(root, query, cb);
        assertEquals(resultPredicate, result);
    }

    @Test
    void shouldBuildPredicateWithCity() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate cityPredicate = mock(Predicate.class);

        Join<Interpreter, ?> locationJoin = (Join<Interpreter, ?>) mock(Join.class);

        Path cityPath = mock(Path.class);
        Expression<String> lowerCityExpression = mock(Expression.class);

        when(root.join("locations", JoinType.LEFT)).thenReturn((Join) locationJoin);
        when(locationJoin.get("city")).thenReturn(cityPath);
        when(cb.lower(cityPath)).thenReturn(lowerCityExpression);
        when(cb.like(lowerCityExpression, "%são paulo%")).thenReturn(cityPredicate);
        when(cb.and(basePredicate, cityPredicate)).thenReturn(cityPredicate);
        when(cb.conjunction()).thenReturn(basePredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(null, null, "São Paulo",
                null, null, null, null, null);

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

        Join<Interpreter, ?> locationJoin = (Join<Interpreter, ?>) mock(Join.class);

        Path ufPath = mock(Path.class);

        when(root.join("locations", JoinType.LEFT)).thenReturn((Join) locationJoin);
        when(locationJoin.get("uf")).thenReturn(ufPath);
        when(cb.equal(ufPath, "RS")).thenReturn(ufPredicate);
        when(cb.and(basePredicate, ufPredicate)).thenReturn(ufPredicate);
        when(cb.conjunction()).thenReturn(basePredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(null, "RS", null,
                null, null, null, null, null);

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(ufPredicate);
        verify(cb).equal(ufPath, "RS");
    }

    @Test
    void shouldBuildPredicateWithAvailableDate() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);

        Join<Interpreter, Schedule> scheduleJoin = (Join<Interpreter, Schedule>) (Join<?, ?>) mock(Join.class);
        when(cb.conjunction()).thenReturn(basePredicate);

        LocalDateTime availableDate = LocalDateTime.of(2025, 10, 6, 10, 0);
        LocalTime requestedStart = availableDate.toLocalTime();
        LocalTime requestedEnd = requestedStart.plusHours(1);

        // Mock schedule predicates
        Predicate schedulePredicate = mock(Predicate.class);
        Path scheduleStartTimePath = mock(Path.class);
        Path scheduleEndTimePath = mock(Path.class);
        when(scheduleJoin.get("startTime")).thenReturn(scheduleStartTimePath);
        when(scheduleJoin.get("endTime")).thenReturn(scheduleEndTimePath);

        when(root.join("schedules", JoinType.LEFT)).thenReturn((Join) scheduleJoin);
        when(cb.equal(scheduleJoin.get("day"), DayOfWeek.MON)).thenReturn(schedulePredicate);
        when(cb.lessThanOrEqualTo(scheduleStartTimePath, requestedStart)).thenReturn(schedulePredicate);
        when(cb.greaterThanOrEqualTo(scheduleEndTimePath, requestedEnd)).thenReturn(schedulePredicate);
        when(cb.and(basePredicate, schedulePredicate)).thenReturn(schedulePredicate);

        // Mock subquery for appointments
        Subquery<UUID> subquery = mock(Subquery.class);
        Root<Appointment> appointmentSubRoot = mock(Root.class);
        when(query.subquery(UUID.class)).thenReturn(subquery);
        when(subquery.from(Appointment.class)).thenReturn(appointmentSubRoot);

        Path interpreterPath = mock(Path.class);
        Path idPath = mock(Path.class);
        when(appointmentSubRoot.get("interpreter")).thenReturn(interpreterPath);
        when(interpreterPath.get("id")).thenReturn(idPath);
        when(subquery.select(idPath)).thenReturn(subquery);

        Predicate subqueryAndPredicate = mock(Predicate.class);
        Predicate firstConditionPredicate = mock(Predicate.class);
        Predicate secondConditionPredicate = mock(Predicate.class);
        Predicate thirdConditionPredicate = mock(Predicate.class);
        when(cb.and(firstConditionPredicate, secondConditionPredicate, thirdConditionPredicate)).thenReturn(subqueryAndPredicate);

        Path statusPath = mock(Path.class);
        when(appointmentSubRoot.get("status")).thenReturn(statusPath);
        when(statusPath.in(AppointmentStatus.ACCEPTED, AppointmentStatus.COMPLETED)).thenReturn(firstConditionPredicate);

        Path datePath = mock(Path.class);
        when(appointmentSubRoot.get("date")).thenReturn(datePath);
        when(cb.equal(datePath, LocalDate.of(2025, 10, 6))).thenReturn(secondConditionPredicate);

        Predicate greaterThanPredicate = mock(Predicate.class);
        Predicate lessThanPredicate = mock(Predicate.class);
        Predicate joinGreaterThanLessThanPredicate = mock(Predicate.class);
        when(cb.greaterThan(any(), eq(requestedStart))).thenReturn(greaterThanPredicate);
        when(cb.lessThan(any(), eq(requestedEnd))).thenReturn(lessThanPredicate);
        when(cb.and(greaterThanPredicate, lessThanPredicate)).thenReturn(joinGreaterThanLessThanPredicate);
        when(cb.lessThanOrEqualTo(any(), eq(requestedStart))).thenReturn(lessThanPredicate);
        when(cb.greaterThanOrEqualTo(any(), eq(requestedEnd))).thenReturn(greaterThanPredicate);
        when(cb.and(lessThanPredicate, greaterThanPredicate)).thenReturn(joinGreaterThanLessThanPredicate);
        when(cb.or(joinGreaterThanLessThanPredicate, joinGreaterThanLessThanPredicate, joinGreaterThanLessThanPredicate))
                .thenReturn(thirdConditionPredicate);

        when(subquery.where(subqueryAndPredicate)).thenReturn(subquery);

        Predicate inSubqueryPredicate = mock(Predicate.class);
        when(root.get("id")).thenReturn(idPath);
        when(idPath.in(subquery)).thenReturn(inSubqueryPredicate);

        when(cb.not(idPath.in(subquery))).thenReturn(inSubqueryPredicate);
        when(cb.and(schedulePredicate, inSubqueryPredicate)).thenReturn(inSubqueryPredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(null, null, null, null,
                null, null, availableDate, null);

        assertDoesNotThrow(() -> spec.toPredicate(root, query, cb));
        verify(cb).equal(scheduleJoin.get("day"), DayOfWeek.MON);
        verify(cb).lessThanOrEqualTo(scheduleStartTimePath, requestedStart);
        verify(cb).greaterThanOrEqualTo(scheduleEndTimePath, requestedEnd);
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

        Specification<Interpreter> spec = InterpreterSpecification.filter(null, null, null, null,
                null, null, null, "SOUZA");

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(namePredicate);
        verify(cb).like(lowerNameExpression, "%souza%");
    }
}