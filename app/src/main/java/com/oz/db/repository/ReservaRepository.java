package com.oz.db.repository;

import java.time.LocalDate;
import java.util.List;
import com.oz.domain.Reserva;

public interface ReservaRepository {
	Reserva salvar(Reserva reserva);

	List<Reserva> buscarPorData(LocalDate data);

	List<Reserva> buscarPorArea(Long areaId);

	boolean existeConflito(Long areaId, LocalDate data);

	void excluir(Long id);
}
