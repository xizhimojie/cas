package org.apereo.cas.config;

import org.apereo.cas.authentication.AuthenticationEventExecutionPlanConfigurer;
import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalResolver;
import org.apereo.cas.authentication.support.password.PasswordEncoderUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;
import org.apereo.cas.couchdb.core.ProfileCouchDbRepository;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.credentials.password.SpringSecurityPasswordEncoder;
import org.pac4j.couch.profile.service.CouchProfileService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CouchDbAuthenticationConfiguration}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Configuration("couchDbAuthenticationConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class CouchDbAuthenticationConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("personDirectoryPrincipalResolver")
    private PrincipalResolver personDirectoryPrincipalResolver;

    @Autowired
    @Qualifier("authenticationCouchDbFactory")
    private ObjectProvider<CouchDbConnectorFactory> authenticationCouchDbFactory;

    @Autowired
    @Qualifier("couchDbAuthenticationHandler")
    private AuthenticationHandler authenticationHandler;

    @ConditionalOnMissingBean(name = "couchDbAuthenticationEventExecutionPlanConfigurer")
    @Bean
    public AuthenticationEventExecutionPlanConfigurer couchDbAuthenticationEventExecutionPlanConfigurer() {
        return plan -> plan.registerAuthenticationHandlerWithPrincipalResolver(authenticationHandler, personDirectoryPrincipalResolver);
    }

    @ConditionalOnMissingBean(name = "authenticationCouchDbRepository")
    @Bean
    @RefreshScope
    public ProfileCouchDbRepository authenticationCouchDbRepository() {
        val repository = new ProfileCouchDbRepository(authenticationCouchDbFactory.getIfAvailable().getCouchDbConnector(),
            casProperties.getAuthn().getCouchDb().isCreateIfNotExists());
        repository.initStandardDesignDocument();
        return repository;
    }

    @ConditionalOnMissingBean(name = "couchDbAuthenticatorProfileService")
    @Bean
    public CouchProfileService couchDbAuthenticatorProfileService() {
        val couchDb = casProperties.getAuthn().getCouchDb();

        LOGGER.info("Connected to CouchDb instance @ [{}] using database [{}]", couchDb.getUrl(), couchDb.getDbName());

        val encoder = new SpringSecurityPasswordEncoder(PasswordEncoderUtils.newPasswordEncoder(couchDb.getPasswordEncoder()));
        val auth = new CouchProfileService(authenticationCouchDbFactory.getIfAvailable().getCouchDbConnector(), couchDb.getAttributes());
        auth.setUsernameAttribute(couchDb.getUsernameAttribute());
        auth.setPasswordAttribute(couchDb.getPasswordAttribute());
        auth.setPasswordEncoder(encoder);
        return auth;
    }
}
