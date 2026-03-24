package com.oz.service.impl;
import com.oz.db.repository.RegraRepository;
import com.oz.db.repository.ReservaRepository;
import com.oz.domain.RegraFuncionamento;
import com.oz.domain.Reserva;
import com.oz.domain.exception.RegraNegocioException;
import com.oz.service.ReservaService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

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
            throw new RegraNegocioException("Reservas não são permitidas para este dia (ex: segundas-feiras).");
        }


        if (reservaRepo.existeConflito(reserva.getArea().getId(), reserva.getData())) {
            throw new RegraNegocioException("Esta área já está reservada para o dia selecionado.");
        }

        reservaRepo.salvar(reserva);
    }

    @Override
    public List<Reserva> listarReservasPorData(LocalDate data) {
        return reservaRepo.buscarPorData(data);
    }

    @Override
    public void cancelarReserva(Long id) {
        reservaRepo.excluir(id);
    }

    private void validarCamposObrigatorios(Reserva reserva) throws RegraNegocioException {
        if (reserva == null) {
            throw new RegraNegocioException("A reserva não pode ser nula.");
        }
        if (reserva.getData() == null || reserva.getArea() == null) {
            throw new RegraNegocioException("Dados incompletos para realizar a reserva.");
        }
    }
}