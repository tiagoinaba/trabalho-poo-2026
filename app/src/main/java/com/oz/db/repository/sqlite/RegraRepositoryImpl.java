package com.oz.db.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.oz.db.repository.RegraRepository;
import com.oz.db.repository.exceptions.PersistenceException;
import com.oz.domain.RegraFuncionamento;

public class RegraRepositoryImpl implements RegraRepository {
	private final Connection conn;
	private final Logger logger = Logger.getLogger(getClass().getSimpleName());

	public RegraRepositoryImpl(Connection conn) {
		this.conn = conn;
	}

	@Override
	public Optional<RegraFuncionamento> buscarRegra(Long areaId, DayOfWeek diaSemana) {
		String sql = """
				select area_id, dia, permitido, horario_inicio, horario_limite
				from regra_funcionamento
				where area_id = ? and dia = ?
				""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, areaId);
			ps.setInt(2, diaSemana.getValue());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapResultSetToRegra(rs));
				}
				return Optional.empty();
			}
		} catch (SQLException e) {
			throw new PersistenceException("erro ao buscar regra", e);
		}
	}

	@Override
	public List<RegraFuncionamento> listarRegrasPorArea(Long areaId) {
		String sql = """
				select area_id, dia, permitido, horario_inicio, horario_limite
				from regra_funcionamento
				where area_id = ?
				order by dia
				""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, areaId);
			try (ResultSet rs = ps.executeQuery()) {
				List<RegraFuncionamento> regras = new ArrayList<>();
				while (rs.next()) {
					regras.add(mapResultSetToRegra(rs));
				}
				return regras;
			}
		} catch (SQLException e) {
			throw new PersistenceException("erro ao listar regras por area", e);
		}
	}

	@Override
	public void atualizarRegra(RegraFuncionamento regra) {
		logger.info("atualizando regra para area " + regra.getAreaId() + " dia " + regra.getDia());
		String sql =
				"""
						insert into regra_funcionamento (area_id, dia, permitido, horario_inicio, horario_limite)
						values (?, ?, ?, ?, ?)
						on conflict(area_id, dia) do update
						set permitido = excluded.permitido,
						    horario_inicio = excluded.horario_inicio,
						    horario_limite = excluded.horario_limite
						""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, regra.getAreaId());
			ps.setInt(2, regra.getDia().getValue());
			ps.setInt(3, regra.isPermitido() ? 1 : 0);
			ps.setString(4,
					regra.getHorarioInicio() != null ? regra.getHorarioInicio().toString() : null);
			ps.setString(5,
					regra.getHorarioLimite() != null ? regra.getHorarioLimite().toString() : null);
			ps.executeUpdate();
			logger.info("regra atualizada com sucesso");
		} catch (SQLException e) {
			throw new PersistenceException("erro ao atualizar regra", e);
		}
	}

	private RegraFuncionamento mapResultSetToRegra(ResultSet rs) throws SQLException {
		int diaInt = rs.getInt("dia");
		LocalTime horarioInicio = null;
		String inicioStr = rs.getString("horario_inicio");
		if (inicioStr != null) {
			horarioInicio = LocalTime.parse(inicioStr);
		}
		LocalTime horarioLimite = null;
		String limiteStr = rs.getString("horario_limite");
		if (limiteStr != null) {
			horarioLimite = LocalTime.parse(limiteStr);
		}
		return new RegraFuncionamento(rs.getLong("area_id"), DayOfWeek.of(diaInt),
				rs.getInt("permitido") == 1, horarioInicio, horarioLimite);
	}
}
