package se.inera.monitoring.config;

import javax.naming.NamingException;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;

@Configuration
@Profile("!dev")
@ComponentScan("se.inera.monitoring.persistence")
@EnableJpaRepositories(basePackages = "se.inera.monitoring.persistence")
public class PersistenceConfigJndi extends PersistenceConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceConfigJndi.class);

    @Bean(destroyMethod = "close")
    DataSource jndiDataSource() {
        DataSource dataSource = null;
        JndiTemplate jndi = new JndiTemplate();
        try {
            dataSource = (DataSource) jndi.lookup("java:comp/env/jdbc/monitoring");
        } catch (NamingException e) {
            LOGGER.error("Could not find the dataSource", e);
            throw new PersistenceException(e);
        }
        return dataSource;
    }
}
