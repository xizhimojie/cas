package org.apereo.cas.config.couchdb;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;
import org.apereo.cas.couchdb.core.ProfileCouchDbRepository;
import org.apereo.cas.services.ServicesManager;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.ektorp.impl.ObjectMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link AuthenticationConfiguration}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Configuration("authenticationCouchDbConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class AuthenticationConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("defaultObjectMapperFactory")
    private ObjectMapperFactory objectMapperFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Bean
    @RefreshScope
    @ConditionalOnMissingBean(name = "authenticationCouchDbFactory")
    public CouchDbConnectorFactory authenticationCouchDbFactory() {
        return new CouchDbConnectorFactory(casProperties.getAuthn().getCouchDb(), objectMapperFactory);
    }

    @ConditionalOnMissingBean(name = "authenticationCouchDbRepository")
    @Bean
    @RefreshScope
    public ProfileCouchDbRepository authenticationCouchDbRepository(@Qualifier("authenticationCouchDbFactory") final CouchDbConnectorFactory authenticationCouchDbFactory) {
        val repository = new ProfileCouchDbRepository(authenticationCouchDbFactory.getCouchDbConnector(),
            casProperties.getAuthn().getCouchDb().isCreateIfNotExists());
        repository.initStandardDesignDocument();
        return repository;
    }

}
