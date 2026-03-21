package com.oz.db.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oz.db.repository.AreaComumRepository;
import com.oz.db.repository.exceptions.PersistenceException;
import com.oz.domain.AreaComum;

public class AreaComumRepositoryImpl implements AreaComumRepository {
	private final Connection conn;
	private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

	public AreaComumRepositoryImpl(Connection conn) {
		this.conn = conn;
	}

	@Override
	public AreaComum salvar(AreaComum area) {
		logger.info("tentando salvar/atualizar AreaComum: " + area.getName());

		String sql = """
					insert into area_comum (id, name)
					values (?, ?)
					on conflict(id) do update
					set name = excluded.name
					returning id, name;
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			if (area.getId() == null || area.getId() == 0) {
				ps.setNull(1, java.sql.Types.INTEGER);
			} else {
				ps.setLong(1, area.getId());
			}
			ps.setString(2, area.getName());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					AreaComum saved = new AreaComum(rs.getLong(1), rs.getString(2));
					logger.fine("AreaComum salva com sucesso. ID: " + saved.getId());
					return saved;
				} else {
					throw new PersistenceException("impossível salvar AreaComum");
				}
			}
		} catch (SQLException e) {
			logger.log(Level.WARNING, "falha no banco de dados ao salvar AreaComum: {0}",
					area.getName());
			throw new PersistenceException("erro ao salvar AreaComum", e);
		}
	}

	@Override
	public List<AreaComum> buscarTodas() {
		String sql = """
				select id, name from area_comum;
				""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			try (ResultSet rs = ps.executeQuery()) {
				List<AreaComum> areas = new ArrayList<>();
				while (rs.next()) {
					areas.add(new AreaComum(rs.getLong(1), rs.getString(2)));
				}
				return areas;
			}
		} catch (SQLException e) {
			throw new PersistenceException("erro ao buscar AreaComum", e);
		}
	}

	@Override
	public Optional<AreaComum> buscarPorId(Long id) {
		String sql = """
				select id, name from area_comum where id = ?;
				""";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new AreaComum(rs.getLong(1), rs.getString(2)));
				}
				return Optional.empty();
			}
		} catch (SQLException e) {
			throw new PersistenceException("erro ao buscar AreaComum", e);
		}
	}

	@Override
	public void excluir(Long id) {
		logger.info("solicitada exclusão da AreaComum ID: " + id);

		String sql = """
				delete from area_comum where id = ?;
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setLong(1, id);
			int rowsAffected = ps.executeUpdate();
			if (rowsAffected == 0) {
                logger.warning("tentativa de excluir ID " + id + " inexistente");
            } else {
                logger.info("id " + id + " excluído com sucesso");
            }
		} catch (SQLException e) {
			throw new PersistenceException("erro ao deletar AreaComum", e);
		}
	}
}
