package com.oz.db.repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import com.oz.domain.RegraFuncionamento;

public interface RegraRepository {
	Optional<RegraFuncionamento> buscarRegra(Long areaId, DayOfWeek diaSemana);

	List<RegraFuncionamento> listarRegrasPorArea(Long areaId);

	void atualizarRegra(RegraFuncionamento regra);
}