package com.oz.service;

import java.time.LocalDate;
import java.util.List;
import com.oz.domain.Reserva;
import com.oz.domain.exception.RegraNegocioException;

public interface ReservaService {
	void agendar(Reserva reserva) throws RegraNegocioException;

	List<Reserva> listarReservasPorData(LocalDate data);

	List<Reserva> listarReservasPorArea(Long areaId);

	void cancelarReserva(Long id);
}
