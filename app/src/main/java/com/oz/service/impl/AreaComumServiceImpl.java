package com.oz.service.impl;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.oz.db.repository.AreaComumRepository;
import com.oz.db.repository.RegraRepository;
import com.oz.domain.AreaComum;
import com.oz.domain.RegraFuncionamento;
import com.oz.domain.exception.RegraNegocioException;
import com.oz.service.AreaComumService;

public class AreaComumServiceImpl implements AreaComumService {

	private final AreaComumRepository areaRepo;
	private final RegraRepository regraRepo;

	public AreaComumServiceImpl(AreaComumRepository areaRepo, RegraRepository regraRepo) {
		this.areaRepo = areaRepo;
		this.regraRepo = regraRepo;
	}

	@Override
	public AreaComum cadastrarArea(String nome) {
		return areaRepo.salvar(new AreaComum(nome));
	}

	@Override
	public AreaComum atualizarArea(AreaComum area) {
		return areaRepo.salvar(area);
	}

	@Override
	public List<AreaComum> listarTodas() {
		return areaRepo.buscarTodas();
	}

	@Override
	public void atualizarRegra(Long areaId, DayOfWeek dia, boolean permitido, LocalTime inicio, LocalTime limite) throws RegraNegocioException {
		Optional<AreaComum> areaOptional = areaRepo.buscarPorId(areaId);
		if (areaOptional.isEmpty()) {
			throw new RegraNegocioException("Área comum não encontrada!");
		}

		RegraFuncionamento novaRegra = new RegraFuncionamento(areaId, dia, permitido, inicio, limite);
		regraRepo.atualizarRegra(novaRegra);
	}

	@Override
	public List<RegraFuncionamento> buscarRegrasDaArea(Long areaId) throws RegraNegocioException {
		Optional<AreaComum> areaOptional = areaRepo.buscarPorId(areaId);
		if (areaOptional.isEmpty()) {
			throw new RegraNegocioException("Área comum não encontrada!");
		}
		
		return regraRepo.listarRegrasPorArea(areaId);
	}
}
