package com.pointtils.pointtils.src.application.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.pointtils.pointtils.src.application.dto.InitialScheduleDTO;
import com.pointtils.pointtils.src.core.domain.entities.Interpreter;
import com.pointtils.pointtils.src.core.domain.entities.Schedule;
import com.pointtils.pointtils.src.core.domain.entities.enums.WeekDay;

public class ScheduleMapper {
    
    public static List<Schedule> toEntities(List<InitialScheduleDTO> dtos, Interpreter interpreter) {
        return dtos.stream()
                .map(dto -> toEntity(dto, interpreter))
                .collect(Collectors.toList());
    }
    
    public static Schedule toEntity(InitialScheduleDTO dto, Interpreter interpreter) {
        Schedule schedule = new Schedule();
        schedule.setDay(WeekDay.fromString(dto.getDay()));
        schedule.setStartTime(dto.getStart());
        schedule.setEndTime(dto.getEnd());
        schedule.setInterpreter(interpreter);
        return schedule;
    }
    
    public static List<InitialScheduleDTO> toDtos(List<Schedule> schedules) {
        return schedules.stream()
                .map(ScheduleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    public static InitialScheduleDTO toDto(Schedule schedule) {
        InitialScheduleDTO dto = new InitialScheduleDTO();
        dto.setDay(schedule.getDay().name().toLowerCase());
        dto.setStart(schedule.getStartTime());
        dto.setEnd(schedule.getEndTime());
        return dto;
    }
}
