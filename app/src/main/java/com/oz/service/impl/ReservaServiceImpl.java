package com.oz.service.impl;

import com.oz.db.repository.RegraRepository;
import com.oz.db.repository.ReservaRepository;
import com.oz.domain.RegraFuncionamento;
import com.oz.domain.Reserva;
import com.oz.domain.exception.RegraNegocioException;
import com.oz.service.ReservaService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepo;
    private final RegraRepository regraRepo;

    public ReservaServiceImpl(ReservaRepository reservaRepo, RegraRepository regraRepo) {
        this.reservaRepo = reservaRepo;
        this.regraRepo = regraRepo;
    }

    @Override
    public void agendar(Reserva reserva) throws RegraNegocioException {
        validarCamposObrigatorios(reserva);

        DayOfWeek diaReserva = reserva.getData().getDayOfWeek();


        RegraFuncionamento regra = regraRepo.buscarRegra(reserva.getArea().getId(), diaReserva)
                .orElseThrow(() -> new RegraNegocioException("Não há regras de funcionamento para este dia/área."));

        if (!regra.isPermitido()) {
            throw new RegraNegocioException("Reservas não são permitidas para este dia.");
        }

        LocalDateTime inicio = LocalDateTime.of(reserva.getData(), reserva.getInicio());
        LocalDateTime fim = LocalDateTime.of(reserva.getData(), reserva.getFim());


        if (fim.isBefore(inicio) || fim.isEqual(inicio)) {
            fim = fim.plusDays(1);
        }


        LocalDateTime limite = LocalDateTime.of(reserva.getData(), regra.getHorarioLimite());
        if (regra.getHorarioLimite().isBefore(LocalTime.NOON)) {
            limite = limite.plusDays(1);
        }


        if (fim.isAfter(limite)) {
            throw new RegraNegocioException("O horário solicitado ultrapassa o limite permitido: " + regra.getHorarioLimite());
        }


        if (reservaRepo.existeConflito(reserva.getArea().getId(), reserva.getData(), reserva.getInicio(), reserva.getFim())) {
            throw new RegraNegocioException("Já existe uma reserva para este horário.");
        }

        reservaRepo.salvar(reserva);
    }

    @Override
    public List<Reserva> listarReservasPorData(LocalDate data) {
        return List.of();
    }

    @Override
    public void cancelarReserva(Long id) {

    }

    private void validarCamposObrigatorios(Reserva reserva) throws RegraNegocioException {
        Objects.requireNonNull(reserva, "reserva");
        if (reserva.getData() == null || reserva.getInicio() == null || reserva.getFim() == null || reserva.getArea() == null) {
            throw new RegraNegocioException("Dados incompletos para realizar a reserva.");
        }
    }


}