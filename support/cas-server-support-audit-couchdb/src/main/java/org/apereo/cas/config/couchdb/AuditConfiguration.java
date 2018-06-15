package org.apereo.cas.config.couchdb;

import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchdb.AuditActionContextCouchDbRepository;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;

import lombok.extern.slf4j.Slf4j;
import org.ektorp.impl.ObjectMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link AuditConfiguration}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Configuration("couchDbAuditConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class AuditConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("defaultObjectMapperFactory")
    private ObjectMapperFactory defaultObjectMapperFactory;

    @Autowired
    @Qualifier("auditCouchDbFactory")
    private CouchDbConnectorFactory auditCouchDbFactory;

    @ConditionalOnMissingBean(name = "auditCouchDbFactory")
    @Bean
    @RefreshScope
    public CouchDbConnectorFactory auditCouchDbFactory() {
        return new CouchDbConnectorFactory(casProperties.getAudit().getCouchDb(), defaultObjectMapperFactory);
    }

    @ConditionalOnMissingBean(name = "auditActionContextCouchDbRepository")
    @Bean
    @RefreshScope
    public AuditActionContextCouchDbRepository auditActionContextCouchDbRepository() {
        return new AuditActionContextCouchDbRepository(auditCouchDbFactory.getCouchDbConnector(), casProperties.getAudit().getCouchDb().isCreateIfNotExists());
    }

}
