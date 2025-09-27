package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalTime;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.DaysOfWeek;
import com.pointtils.pointtils.src.core.domain.entities.enums.Gender;

import jakarta.persistence.criteria.JoinType;

class InterpreterSpecificationTest {

    @Test
    void shouldBuildPredicateWithGender() {
        @SuppressWarnings("unchecked")
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate genderPredicate = mock(Predicate.class);

        when(cb.conjunction()).thenReturn(basePredicate);
        when(cb.equal(root.get("gender"), Gender.MALE)).thenReturn(genderPredicate);
        when(cb.and(basePredicate, genderPredicate)).thenReturn(genderPredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, null, null, null, Gender.MALE, null, null, null);

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(genderPredicate);
        verify(cb).equal(root.get("gender"), Gender.MALE);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
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

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, "São Paulo", null, null, null, null, null, null);

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(cityPredicate);
        verify(cb).like(lowerCityExpression, "%são paulo%");
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    void shouldBuildPredicateWithScheduleAndTime() {
        Root<Interpreter> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Predicate basePredicate = mock(Predicate.class);
        Predicate dayPredicate = mock(Predicate.class);
        Predicate startPredicate = mock(Predicate.class);
        Predicate endPredicate = mock(Predicate.class);

        Join<Interpreter, Schedule> scheduleJoin = (Join<Interpreter, Schedule>) (Join<?, ?>) mock(Join.class);

        when(root.join("schedules", JoinType.LEFT)).thenReturn((Join) scheduleJoin);
        when(cb.conjunction()).thenReturn(basePredicate);

        LocalTime requestedStart = LocalTime.of(10, 0);
        LocalTime requestedEnd = requestedStart.plusHours(1);

        when(cb.equal(scheduleJoin.get("day"), DaysOfWeek.MON)).thenReturn(dayPredicate);
        when(cb.lessThanOrEqualTo(scheduleJoin.get("startTime"), requestedStart)).thenReturn(startPredicate);
        when(cb.greaterThanOrEqualTo(scheduleJoin.get("endTime"), requestedEnd)).thenReturn(endPredicate);

        when(cb.and(basePredicate, dayPredicate)).thenReturn(dayPredicate);
        when(cb.and(dayPredicate, startPredicate)).thenReturn(startPredicate);
        when(cb.and(startPredicate, endPredicate)).thenReturn(endPredicate);

        Specification<Interpreter> spec = InterpreterSpecification.filter(
                null, null, null, null, null, null, DaysOfWeek.MON, requestedStart, requestedEnd);

        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(endPredicate);
        verify(cb).equal(scheduleJoin.get("day"), DaysOfWeek.MON);
        verify(cb).lessThanOrEqualTo(scheduleJoin.get("startTime"), requestedStart);
        verify(cb).greaterThanOrEqualTo(scheduleJoin.get("endTime"), requestedEnd);
    }
}
