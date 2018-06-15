package org.apereo.cas.config;

import org.apereo.cas.audit.AuditTrailExecutionPlanConfigurer;
import org.apereo.cas.audit.CouchDbAuditTrailManager;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchdb.AuditActionContextCouchDbRepository;

import lombok.extern.slf4j.Slf4j;
import org.apereo.inspektr.audit.AuditTrailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CasSupportCouchDbAuditConfiguration}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Configuration("casSupportCouchDbAuditConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class CasSupportCouchDbAuditConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("auditActionContextCouchDbRepository")
    private AuditActionContextCouchDbRepository repository;

    @ConditionalOnMissingBean(name = "couchDbAuditTrailManager")
    @Bean
    @RefreshScope
    public AuditTrailManager couchDbAuditTrailManager() {
        repository.initStandardDesignDocument();
        return new CouchDbAuditTrailManager(repository, casProperties.getAudit().getCouchDb().isAsyncronous());
    }

    @ConditionalOnMissingBean(name = "couchDbAuditTrailExecutionPlanConfigurer")
    @Bean
    @RefreshScope
    public AuditTrailExecutionPlanConfigurer couchDbAuditTrailExecutionPlanConfigurer(@Qualifier("couchDbAuditTrailManager") AuditTrailManager auditTrailManager) {
        return plan -> plan.registerAuditTrailManager(auditTrailManager);
    }
}
