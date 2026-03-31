package com.oz.db.repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.oz.db.repository.ReservaRepository;
import com.oz.db.repository.exceptions.PersistenceException;
import com.oz.domain.AreaComum;
import com.oz.domain.Reserva;

public class ReservaRepositoryImpl implements ReservaRepository {
    private final Connection conn;
    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public ReservaRepositoryImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Reserva salvar(Reserva reserva) {
        logger.info("Salvando reserva: " + reserva.getArea().getName() + " em " + reserva.getData());

        String sql = """
                insert into reserva (area_id, nome_morador, ap, data)
                values (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, reserva.getArea().getId());
            ps.setString(2, reserva.getNomeMorador());
            ps.setString(3, reserva.getAp());
            ps.setString(4, reserva.getData().toString());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    reserva.setId(rs.getLong(1));
                    logger.fine("Reserva salva com ID: " + reserva.getId());
                }
            }
            return reserva;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Falha ao salvar reserva", e);
            throw new PersistenceException("Erro ao salvar reserva", e);
        }
    }

    @Override
    public List<Reserva> buscarPorData(LocalDate data) {
        String sql = """
                select r.id, r.area_id, r.nome_morador, r.ap, r.data,
                       a.id as a_id, a.name
                from reserva r
                join area_comum a on r.area_id = a.id
                where r.data = ?
                """;

        List<Reserva> reservas = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, data.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AreaComum area = new AreaComum(rs.getLong("a_id"), rs.getString("name"));
                    reservas.add(new Reserva(
                            rs.getLong("id"),
                            area,
                            rs.getString("nome_morador"),
                            rs.getString("ap"),
                            LocalDate.parse(rs.getString("data"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar reservas", e);
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorArea(Long areaId) {
        String sql = """
                select r.id, r.area_id, r.nome_morador, r.ap, r.data,
                       a.id as a_id, a.name
                from reserva r
                join area_comum a on r.area_id = a.id
                where r.area_id = ?
                order by r.data
                """;

        List<Reserva> reservas = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, areaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AreaComum area = new AreaComum(rs.getLong("a_id"), rs.getString("name"));
                    reservas.add(new Reserva(
                            rs.getLong("id"),
                            area,
                            rs.getString("nome_morador"),
                            rs.getString("ap"),
                            LocalDate.parse(rs.getString("data"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao buscar reservas por area", e);
        }
        return reservas;
    }

    @Override
    public boolean existeConflito(Long areaId, LocalDate data) {
        String sql = """
                select count(*) from reserva
                where area_id = ? and data = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, areaId);
            ps.setString(2, data.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao verificar conflito", e);
        }
    }

    @Override
    public void excluir(Long id) {
        logger.info("Excluindo reserva ID: " + id);

        String sql = "delete from reserva where id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                logger.warning("Reserva ID " + id + " nao encontrada para exclusao");
            }
        } catch (SQLException e) {
            throw new PersistenceException("Erro ao excluir reserva", e);
        }
    }
}
