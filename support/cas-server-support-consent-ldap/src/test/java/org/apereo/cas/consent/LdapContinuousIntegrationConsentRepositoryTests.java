package org.apereo.cas.consent;

import org.apereo.cas.adaptors.ldap.LdapIntegrationTestsOperations;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.util.junit.ConditionalIgnore;
import org.apereo.cas.util.junit.RunningContinuousIntegrationCondition;

import com.unboundid.ldap.sdk.LDAPConnection;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;

/**
 * Unit tests for {@link LdapConsentRepository} class.
 *
 * @author Arnold Bergner
 * @since 5.3.0
 */
@TestPropertySource(properties = {
    "cas.consent.ldap.ldapUrl=ldap://localhost:10389",
    "cas.consent.ldap.useSsl=false",
    "cas.consent.ldap.baseDn=ou=people,dc=example,dc=org",
    "cas.consent.ldap.searchFilter=cn={0}",
    "cas.consent.ldap.consentAttributeName=description",
    "cas.consent.ldap.bindDn=cn=Directory Manager",
    "cas.consent.ldap.bindCredential=password"
    })
@ConditionalIgnore(condition = RunningContinuousIntegrationCondition.class)
public class LdapContinuousIntegrationConsentRepositoryTests extends BaseLdapConsentRepositoryTests {
    private static final int LDAP_PORT = 10389;

    @Autowired
    private CasConfigurationProperties casProperties;

    @BeforeClass
    @SneakyThrows
    public static void bootstrap() {
        val localhost = new LDAPConnection("localhost", LDAP_PORT,
            "cn=Directory Manager", "password");
        LdapIntegrationTestsOperations.populateEntries(
            localhost,
            new ClassPathResource("ldif/ldap-consent.ldif").getInputStream(),
            "ou=people,dc=example,dc=org");
    }

    @Override
    @SneakyThrows
    public LDAPConnection getConnection() {
        return new LDAPConnection("localhost", LDAP_PORT,
            casProperties.getConsent().getLdap().getBindDn(),
            casProperties.getConsent().getLdap().getBindCredential());
    }
}
