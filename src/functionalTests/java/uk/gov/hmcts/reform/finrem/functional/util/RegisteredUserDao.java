package uk.gov.hmcts.reform.finrem.functional.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import javax.sql.DataSource;


@ContextConfiguration(classes = uk.gov.hmcts.reform.finrem.functional.TestContextConfiguration.class)
@TestPropertySource("classpath:resources/application.properties")
@Component
public class RegisteredUserDao {

//    private static String JDBC_DRIVER = "org.postgressql.Driver";
   private static String DB_URL = "jdbc:postgresql://localhost:5430/idam";
//    @Value("${DB_URL}")
//    private String DB_URL;

    private static String USER = "idam";
    private static String PASS = "idam";
    private static String DB_NAME = "idam";
    private static String DRIVER_CLASSNAME = "org.postgresql.Driver";
//    private static DB_URL = jdbc:postgresql://localhost:5430/idam
    private static Boolean INITIALIZE = true;
    private static DataSource dataSource;

    public JdbcTemplate jdbcTemplate;

    @Autowired
    private Environment environment;

    public Map<String,String> getUserDetails() {

        String selectSql = "select id,email from registered_user WHERE user_group_name = 'caseworker'";
        dataSource = getDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
        Map<String,String> user = new HashMap<>();
        List<Map<String, Object>> userDetails = jdbcTemplate.queryForList(selectSql);
        if (userDetails != null && !userDetails.isEmpty()) {
            for (Map<String, Object> userDetail : userDetails) {
                for (Iterator<Map.Entry<String, Object>> it = userDetail.entrySet().iterator(); it.hasNext(); ) {
                    Map.Entry<String, Object> entry = it.next();
                    user.put(entry.getKey().toString(),entry.getValue().toString());
                }
            }
            System.out.println(user);
        }
        return user;
    }

    @Bean
    public DriverManagerDataSource getDataSource() {

        try {
            Class.forName(DRIVER_CLASSNAME);

        } catch(ClassNotFoundException e) {

        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(DRIVER_CLASSNAME);
        dataSource.setUrl(DB_URL);
        dataSource.setUsername(USER);
        dataSource.setPassword(PASS);
        return dataSource;

    }
}

