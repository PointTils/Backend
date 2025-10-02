package com.pointtils.pointtils.src.infrastructure.repositories.spec;

import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.DayOfWeek;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings({"rawtypes", "unchecked"})
class ScheduleSpecificationsTest {

    @Test
    void shouldBuildPredicateWithDay() {
        Root<Schedule> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate dayPredicate = mock(Predicate.class);
        ArgumentCaptor<Predicate[]> predicateCaptor = ArgumentCaptor.forClass(Predicate[].class);

        when(criteriaBuilder.equal(root.get("day"), DayOfWeek.FRI)).thenReturn(dayPredicate);
        when(criteriaBuilder.and(predicateCaptor.capture())).thenReturn(dayPredicate);

        Specification<Schedule> spec = ScheduleSpecifications.withFilters(null, DayOfWeek.FRI, null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(dayPredicate, result);
        verify(criteriaBuilder).equal(root.get("day"), DayOfWeek.FRI);
        assertThat(predicateCaptor.getValue())
                .hasSize(1)
                .contains(dayPredicate);
    }

    @Test
    void shouldBuildPredicateWithInterpreterId() {
        Root<Schedule> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate interpreterIdPredicate = mock(Predicate.class);
        Path interpreterPath = mock(Path.class);
        Path<UUID> interpreterIdPath = (Path<UUID>) mock(Path.class);
        ArgumentCaptor<Predicate[]> predicateCaptor = ArgumentCaptor.forClass(Predicate[].class);

        UUID interpreterId = UUID.randomUUID();

        when(root.get("interpreter")).thenReturn(interpreterPath);
        when(interpreterPath.get("id")).thenReturn(interpreterIdPath);
        when(criteriaBuilder.equal(interpreterIdPath, interpreterId)).thenReturn(interpreterIdPredicate);
        when(criteriaBuilder.and(predicateCaptor.capture())).thenReturn(interpreterIdPredicate);

        Specification<Schedule> spec = ScheduleSpecifications.withFilters(interpreterId, null, null, null);
        Predicate result = spec.toPredicate(root, query, criteriaBuilder);

        assertEquals(interpreterIdPredicate, result);
        verify(criteriaBuilder).equal(interpreterPath.get("id"), interpreterId);
        assertThat(predicateCaptor.getValue())
                .hasSize(1)
                .contains(interpreterIdPredicate);
    }
}
