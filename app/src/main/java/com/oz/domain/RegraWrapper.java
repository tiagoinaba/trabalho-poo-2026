package com.oz.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import javafx.beans.property.SimpleObjectProperty;

public class RegraWrapper {
    private static final Map<DayOfWeek, String> DIAS_PORTUGUES = Map.of(
        DayOfWeek.MONDAY, "Segunda-feira",
        DayOfWeek.TUESDAY, "Terça-feira",
        DayOfWeek.WEDNESDAY, "Quarta-feira",
        DayOfWeek.THURSDAY, "Quinta-feira",
        DayOfWeek.FRIDAY, "Sexta-feira",
        DayOfWeek.SATURDAY, "Sábado",
        DayOfWeek.SUNDAY, "Domingo"
    );
    private static final DateTimeFormatter HORA_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final SimpleObjectProperty<DayOfWeek> dia;
    private final SimpleObjectProperty<Boolean> permitido;
    private final SimpleObjectProperty<LocalTime> horarioInicio;
    private final SimpleObjectProperty<LocalTime> horarioLimite;
    private final RegraFuncionamento regraOriginal;

    public RegraWrapper(RegraFuncionamento regra) {
        this.regraOriginal = regra;
        this.dia = new SimpleObjectProperty<>(regra.getDia());
        this.permitido = new SimpleObjectProperty<>(regra.isPermitido());
        this.horarioInicio = new SimpleObjectProperty<>(regra.getHorarioInicio());
        this.horarioLimite = new SimpleObjectProperty<>(regra.getHorarioLimite());
    }

    public DayOfWeek getDia() { return dia.get(); }
    public SimpleObjectProperty<DayOfWeek> diaProperty() { return dia; }
    public String getDiaPortugues() { return DIAS_PORTUGUES.get(dia.get()); }

    public boolean isPermitido() { return permitido.get(); }
    public SimpleObjectProperty<Boolean> permitidoProperty() { return permitido; }
    public String getPermitidoTexto() { return permitido.get() ? "Sim" : "Não"; }

    public LocalTime getHorarioInicio() { return horarioInicio.get(); }
    public SimpleObjectProperty<LocalTime> horarioInicioProperty() { return horarioInicio; }
    public String getHorarioInicioTexto() {
        LocalTime t = horarioInicio.get();
        return t != null ? t.format(HORA_FORMAT) : "--:--";
    }

    public LocalTime getHorarioLimite() { return horarioLimite.get(); }
    public SimpleObjectProperty<LocalTime> horarioLimiteProperty() { return horarioLimite; }
    public String getHorarioLimiteTexto() {
        LocalTime t = horarioLimite.get();
        return t != null ? t.format(HORA_FORMAT) : "--:--";
    }

    public Long getAreaId() { return regraOriginal.getAreaId(); }
    public DayOfWeek getDiaOriginal() { return regraOriginal.getDia(); }
}
