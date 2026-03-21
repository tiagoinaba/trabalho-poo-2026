package com.oz.db.repository;

import java.util.List;
import java.util.Optional;
import com.oz.domain.AreaComum;

public interface AreaComumRepository {
	AreaComum salvar(AreaComum area);
    List<AreaComum> buscarTodas();
    Optional<AreaComum> buscarPorId(Long id);
    void excluir(Long id);
}
