package com.oz.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Reserva {
    private Long id;
    private final AreaComum area;
    private final String nomeMorador;
    private final String ap;
    private final LocalDate data;

    public Reserva(AreaComum area, String nomeMorador, String ap, LocalDate data) {
        this.id = null;
        this.area = Objects.requireNonNull(area, "area");
        this.nomeMorador = Objects.requireNonNull(nomeMorador, "nomeMorador");
        this.ap = Objects.requireNonNull(ap, "ap");
        this.data = Objects.requireNonNull(data, "data");
    }

    public Reserva(Long id, AreaComum area, String nomeMorador, String ap, LocalDate data) {
        this.id = id;
        this.area = Objects.requireNonNull(area, "area");
        this.nomeMorador = Objects.requireNonNull(nomeMorador, "nomeMorador");
        this.ap = Objects.requireNonNull(ap, "ap");
        this.data = Objects.requireNonNull(data, "data");
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AreaComum getArea() { return area; }
    public String getNomeMorador() { return nomeMorador; }
    public String getAp() { return ap; }
    public LocalDate getData() { return data; }
}
