package com.example.dados.persistence;

import com.example.dados.persistence.entity.EmployeeEntity;
import com.mysql.cj.jdbc.StatementImpl;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.ZoneOffset.UTC;

public class EmployeeDAO {

    public void insert(final EmployeeEntity entity){

        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement();
                ) {

            var sql = "INSERT INTO employees (name, salary, birthday) values ('"
                    + entity.getName() + "', " +
                    entity.getSalary().toString() + ", " +
                    "'" + formatOffsetDateTime(entity.getBirthday()) + "' )";
            statement.executeUpdate(sql);
            System.out.printf("Foram afetados %s registros na base de dados.", statement.getUpdateCount());

            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertID());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void update(final EmployeeEntity entity){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement()
        ){
            var sql = "UPDATE employees set " +
                    "name     = '" + entity.getName() + "'," +
                    "salary   = " + entity.getSalary().toString() + "," +
                    "birthday = '" + formatOffsetDateTime(entity.getBirthday()) + "'" +
                    "WHERE id = " + entity.getId();
            statement.executeUpdate(sql);
            System.out.printf("Foram afetados %s registros na base de dados", statement.getUpdateCount());
            if (statement instanceof StatementImpl impl)
                entity.setId(impl.getLastInsertID());
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void delete(final long id){
        try(
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement()
        ){
            var sql = "DELETE FROM employees WHERE id = " + id;
            statement.executeUpdate(sql);
        }catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public List<EmployeeEntity> findAll(){
        List<EmployeeEntity> entities = new ArrayList<>();

        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement();
        ) {
            statement.executeQuery("SELECT * FROM employees ORDER BY name");
            var resutSet = statement.getResultSet();
            while(resutSet.next()){
                var entity = new EmployeeEntity();
                entity.setId(resutSet.getLong("id"));
                entity.setName(resutSet.getString("name"));
                entity.setSalary(resutSet.getBigDecimal("salary"));
                var birthdayInstant = resutSet.getTimestamp("birthday").toInstant();
                entity.setBirthday(OffsetDateTime.ofInstant(birthdayInstant, UTC));
                entities.add(entity);
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return  entities;
    }

    public EmployeeEntity findById (final long id){
        var entity = new EmployeeEntity();

        try (
                var connection = ConnectionUtil.getConnection();
                var statement = connection.createStatement();
        ) {
            statement.executeQuery("SELECT * FROM employees WHERE id = " + id);
            var resutSet = statement.getResultSet();
            if(resutSet.next()){
                entity.setId(resutSet.getLong("id"));
                entity.setName(resutSet.getString("name"));
                entity.setSalary(resutSet.getBigDecimal("salary"));
                var birthdayInstant = resutSet.getTimestamp("birthday").toInstant();
                entity.setBirthday(OffsetDateTime.ofInstant(birthdayInstant, UTC));

            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return  entity;
    }

    private String formatOffsetDateTime(final OffsetDateTime dateTime){
        var utcDatetime = dateTime.withOffsetSameInstant(UTC);
        return utcDatetime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"));
    }
}
