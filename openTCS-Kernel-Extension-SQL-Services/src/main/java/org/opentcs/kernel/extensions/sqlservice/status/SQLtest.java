package org.opentcs.kernel.extensions.sqlservice.status;

import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLtest {
    final static Logger logger = LoggerFactory.getLogger(SQLtest.class);
    public static void main(String[] args) {

//        String db = "jdbc:sqlite:" + getDatabasePath("db.sqlite").getAbsolutePath();
        String db = "jdbc:sqlite:" + "/Users/touchmii/IntelliJProjects/OpenTCS-4.17/openTCS-Kernel-Extension-SQL-Services/src/main/java/org/opentcs/kernel/extensions/sqlservice/status/db.sqlite";

        Base.open("org.sqlite.JDBC", db, "", "");
//        Base.open("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost/test", "root", "P@ssmysql");

        createEmployee();
        logger.info("=========> Created employee:");
        selectEmployee();
        updateEmployee();
        logger.info("=========> Updated employee:");
        selectAllEmployees();
        deleteEmployee();
        logger.info("=========> Deleted employee:");
        selectAllEmployees();
        createEmployee();
        logger.info("=========> Created employee:");
        selectEmployee();
        deleteAllEmployees();
        logger.info("=========> Deleted all employees:");
        selectAllEmployees();

        Base.close();
    }

    private static void createEmployee() {
        Employee e = new Employee();
        e.set("first_name", "John");
        e.set("last_name", "Doe");
        e.saveIt();
    }

    private static void selectEmployee() {
        Employee e = Employee.findFirst("first_name = ?", "John");
        logger.info(e.toString());
    }

    private static void updateEmployee() {
        Employee e = Employee.findFirst("first_name = ?", "John");
        e.set("last_name", "Steinbeck").saveIt();
    }

    private static void deleteEmployee() {
        Employee e = Employee.findFirst("first_name = ?", "John");
        e.delete();
    }

    private static void deleteAllEmployees() {
        Employee.deleteAll();
    }

    private static void selectAllEmployees() {
        logger.info("Employees list: " + Employee.findAll());
    }
}
