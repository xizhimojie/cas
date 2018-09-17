package org.apereo.cas.config;

import org.apereo.cas.authentication.AuthenticationHandler;
import org.apereo.cas.authentication.CouchDbAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.PrincipalNameTransformerUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.couchdb.core.CouchDbConnectorFactory;
import org.apereo.cas.services.ServicesManager;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.couch.profile.service.CouchProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is {@link CouchDbAuthenticationHandlerConfiguration}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@Configuration("couchDbAuthenticationHandlerConfiguration")
@EnableConfigurationProperties(CasConfigurationProperties.class)
@Slf4j
public class CouchDbAuthenticationHandlerConfiguration {

    @Autowired
    private CasConfigurationProperties casProperties;

    @Autowired
    @Qualifier("authenticationCouchDbFactory")
    private CouchDbConnectorFactory couchDbFactory;

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    @Autowired
    @Qualifier("couchDbPrincipalFactory")
    private PrincipalFactory couchDbPrincipalFactory;

    @ConditionalOnMissingBean(name = "couchDbAuthenticationHandler")
    @Bean
    @RefreshScope
    public AuthenticationHandler couchDbAuthenticationHandler(
        @Qualifier("couchDbAuthenticatorProfileService") final CouchProfileService couchProfileService,
        @Qualifier("couchDbPrincipalFactory") final PrincipalFactory principalFactory) {
        val couchDb = casProperties.getAuthn().getCouchDb();
        val handler = new CouchDbAuthenticationHandler(couchDb.getName(), servicesManager, principalFactory, couchDb.getOrder());
        handler.setAuthenticator(couchProfileService);
        handler.setPrincipalNameTransformer(PrincipalNameTransformerUtils.newPrincipalNameTransformer(couchDb.getPrincipalTransformation()));
        return handler;
    }
}
