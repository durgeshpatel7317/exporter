package com.table.exporter.service;

import com.table.exporter.enums.Table;
import com.table.exporter.model.UserEntity;
import com.table.exporter.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserService {

    static String EMAIL_QUERY = "SELECT * FROM user u WHERE u.email LIKE CONCAT(?, '%')";

    @Autowired
    private final UserRepository repository;

    @Autowired
    private final DataSource dataSource;

    public UserService(UserRepository repository, DataSource dataSource) {
        this.repository = repository;
        this.dataSource = dataSource;
    }

    public UserEntity saveUser(UserEntity userEntity) {
        return repository.save(userEntity);
    }

    public List<UserEntity> getUsersByEmail(String userEmail) throws SQLException {
        List<UserEntity> response = new ArrayList<>();
        try (var statement = dataSource.getConnection().prepareStatement(EMAIL_QUERY)) {
            statement.setString(1, userEmail);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                UserEntity entity = new UserEntity();
                entity.setId(rs.getLong(1));
                entity.setFirstName(rs.getString(2));
                entity.setLastName(rs.getString(3));
                entity.setEmail(rs.getString(4));
                entity.setPassword(rs.getString(5));

                response.add(entity);
            }
        }

        return response;
    }

    public void exportTable(String table, List<String> columns, Writer writer) throws SQLException {
        Table queryEnum = Table.getQuery(table, columns);

        String query = queryEnum.getSel() + queryEnum.getCols() + queryEnum.getFrom() + queryEnum.getTableName();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            csvPrinter.printRecord(columns);

            try (var statement = dataSource.getConnection().prepareStatement(query)) {
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    List<Object> entity = new ArrayList<>();
                    for(String col: columns) {
                        entity.add(rs.getObject(col));
                    }
                    csvPrinter.printRecord(entity);
                }
            }
        } catch (IOException e) {
            log.error("Error While writing CSV ", e);
        }
    }
}
