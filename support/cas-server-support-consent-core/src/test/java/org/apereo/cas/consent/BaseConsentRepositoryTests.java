package org.apereo.cas.consent;

import org.apereo.cas.CipherExecutor;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.AbstractRegisteredService;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;

import lombok.Getter;
import lombok.val;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * This is {@link BaseConsentRepositoryTests}.
 *
 * @author Timur Duehr
 * @since 6.0.0
 */
@SpringBootTest(classes = {
    RefreshAutoConfiguration.class
})
@Getter
public class BaseConsentRepositoryTests {
    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    protected static final DefaultConsentDecisionBuilder BUILDER = new DefaultConsentDecisionBuilder(CipherExecutor.noOpOfSerializableToString());
    protected static final Service SVC = RegisteredServiceTestUtils.getService();
    protected static final AbstractRegisteredService REG_SVC = RegisteredServiceTestUtils.getRegisteredService(SVC.getId());

    protected static final Map<String, Object> ATTR = CollectionUtils.wrap("attribute", "value");

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    @Qualifier("consentRepository")
    private ConsentRepository repository;

    public ConsentRepository getRepository(final String testName) {
        return getRepository();
    }

    @Test
    public void verifyConsentDecisionIsNotFound() {
        val repo = getRepository("verifyConsentDecisionIsNotFound");
        val decision = BUILDER.build(SVC, REG_SVC, "casuser", ATTR);
        decision.setId(1);
        repo.storeConsentDecision(decision);

        assertNull(repo.findConsentDecision(SVC, REG_SVC, CoreAuthenticationTestUtils.getAuthentication()));
    }

    @Test
    public void verifyConsentDecisionIsFound() {
        val repo = getRepository("verifyConsentDecisionIsFound");
        val decision = BUILDER.build(SVC, REG_SVC, "casuser2", ATTR);
        decision.setId(100);
        repo.storeConsentDecision(decision);

        val d = repo.findConsentDecision(SVC, REG_SVC, CoreAuthenticationTestUtils.getAuthentication("casuser2"));
        assertNotNull(d);
        assertEquals("casuser2", d.getPrincipal());

        assertTrue(repo.deleteConsentDecision(d.getId(), d.getPrincipal()));
        assertNull(repo.findConsentDecision(SVC, REG_SVC, CoreAuthenticationTestUtils.getAuthentication("casuser2")));
    }
}
