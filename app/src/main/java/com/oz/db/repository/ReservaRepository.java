package com.oz.db.repository;

import java.time.LocalDate;
import java.util.List;
import com.oz.domain.Reserva;

public interface ReservaRepository {
	void salvar(Reserva reserva);

	List<Reserva> buscarPorData(LocalDate data);

	boolean existeConflito(Long areaId, LocalDate data);

	void excluir(Long id);
}