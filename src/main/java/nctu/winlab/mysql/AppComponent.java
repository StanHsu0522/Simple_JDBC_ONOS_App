/*
 * Copyright 2021-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nctu.winlab.mysql;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

import com.mysql.cj.jdbc.Driver;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Activate
    protected void activate() {
        log.info("Started");
        
        String DbUrl = "jdbc:mysql://localhost:3306/8021X";
        String DbUser = "winlab";
        String DbPassword = "test";
        Statement stmt = null;
        Connection connection = null;

        try {
            // Nameless object for executing class static clause. (MySQL Connector/J JDBC driver)
            new Driver();

            connection = DriverManager.getConnection(DbUrl, DbUser, DbPassword);
            if (!connection.isClosed()) {
                log.info("Connection to database established...");
                stmt = connection.createStatement();
                ResultSet result = stmt.executeQuery("SELECT * FROM test;");
                log.info("*** TABLE 'test' ***");
                log.info("id\t| name");
                log.info("********************");
                while (result.next()) {
                    log.info("{}\t| {}", result.getInt("id"), result.getString("name"));
                }
                log.info("********************");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("SQLException: " + e.getSQLState());
        }
        finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.info("SQLException: " + e.getSQLState());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                    log.info("SQLException: " + e.getSQLState());
                }
            }
        }
        try {
            if (connection.isClosed()) {
                log.info("Connection to database closed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            log.info("SQLException: " + e.getSQLState());
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
    }

}
