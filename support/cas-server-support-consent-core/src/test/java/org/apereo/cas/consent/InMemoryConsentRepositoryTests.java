package org.apereo.cas.consent;

import org.apereo.cas.audit.spi.config.CasCoreAuditConfiguration;
import org.apereo.cas.config.CasConsentCoreConfiguration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;

/**
 * This is {@link InMemoryConsentRepositoryTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = {
    CasConsentCoreConfiguration.class,
    RefreshAutoConfiguration.class,
    CasCoreAuditConfiguration.class
})
public class InMemoryConsentRepositoryTests extends BaseConsentRepositoryTests {
}
