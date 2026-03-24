package com.oz.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class RegraFuncionamento {
    private final Long areaId;
    private final DayOfWeek dia;
    private final boolean permitido;
    private final LocalTime horarioInicio;
    private final LocalTime horarioLimite;

    public RegraFuncionamento(Long areaId, DayOfWeek dia, boolean permitido, LocalTime horarioInicio, LocalTime horarioLimite) {
        this.areaId = areaId;
        this.dia = Objects.requireNonNull(dia, "dia");
        this.permitido = permitido;
        this.horarioInicio = horarioInicio;
        this.horarioLimite = horarioLimite;

        if (permitido && (horarioInicio == null || horarioLimite == null)) {
            throw new IllegalArgumentException("É preciso definir os horários de início e limite quando o uso é permitido");
        }
    }

    public Long getAreaId() { return areaId; }
    public DayOfWeek getDia() { return dia; }
    public boolean isPermitido() { return permitido; }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public LocalTime getHorarioLimite() { return horarioLimite; }
}