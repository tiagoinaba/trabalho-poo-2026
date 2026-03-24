package com.oz.service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import com.oz.domain.AreaComum;
import com.oz.domain.RegraFuncionamento;
import com.oz.domain.exception.RegraNegocioException;

public interface AreaComumService {
	AreaComum cadastrarArea(String nome);
	AreaComum atualizarArea(AreaComum area);

	void atualizarRegra(Long areaId, DayOfWeek dia, boolean permitido, LocalTime inicio, LocalTime limite) throws RegraNegocioException;

	List<AreaComum> listarTodas();

	List<RegraFuncionamento> buscarRegrasDaArea(Long areaId) throws RegraNegocioException;
}