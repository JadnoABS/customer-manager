package com.jadno.datum.CustomerManager.db.customer;

import com.jadno.datum.CustomerManager.dto.Status;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CustomerJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Customer> CUSTOMER_ROW_MAPPER = (rs, rowNum) -> Customer.builder()
            .id(rs.getLong("id"))
            .name(rs.getString("name"))
            .cpf(rs.getString("cpf"))
            .email(rs.getString("email"))
            .status(Status.valueOf(rs.getString("status")))
            .build();

    public List<Customer> search(String name, Status status) {
        StringBuilder sql = new StringBuilder("SELECT id, name, cpf, email, status FROM customer WHERE 1 = 1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (name != null && !name.isBlank()) {
            sql.append(" AND LOWER(name) LIKE LOWER(:name)");
            params.addValue("name", "%" + name + "%");
        }

        if (status != null) {
            sql.append(" AND status = :status");
            params.addValue("status", status.name());
        }

        return jdbcTemplate.query(sql.toString(), params, CUSTOMER_ROW_MAPPER);
    }
}