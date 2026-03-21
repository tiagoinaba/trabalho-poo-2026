package com.oz.service.impl;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import com.oz.db.repository.AreaComumRepository;
import com.oz.domain.AreaComum;
import com.oz.domain.RegraFuncionamento;
import com.oz.service.AreaComumService;

public class AreaComumServiceImpl implements AreaComumService {

	private final AreaComumRepository repo;

	public AreaComumServiceImpl(AreaComumRepository repo) {
		this.repo = repo;
	}

	@Override
	public AreaComum cadastrarArea(String nome) {
		return repo.salvar(new AreaComum(nome));
	}

	@Override
	public AreaComum atualizarArea(AreaComum area) {
		return repo.salvar(area);
	}

	@Override
	public List<AreaComum> listarTodas() {
		return repo.buscarTodas();
	}

	@Override
	public void atualizarRegra(Long areaId, DayOfWeek dia, LocalTime limite, boolean permitido) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'atualizarRegra'");
	}

	@Override
	public List<RegraFuncionamento> buscarRegrasDaArea(Long areaId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'buscarRegrasDaArea'");
	}
}
