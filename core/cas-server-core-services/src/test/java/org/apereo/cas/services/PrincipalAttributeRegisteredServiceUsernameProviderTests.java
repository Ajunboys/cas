package org.apereo.cas.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.principal.Principal;
import org.apereo.cas.util.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Misagh Moayyed
 * @since 4.1.0
 */
@RunWith(JUnit4.class)
@Slf4j
public class PrincipalAttributeRegisteredServiceUsernameProviderTests {

    private static final File JSON_FILE = new File(FileUtils.getTempDirectoryPath(), "principalAttributeRegisteredServiceUsernameProvider.json");
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @Test
    public void verifyUsernameByPrincipalAttributeWithMapping() {
        final PrincipalAttributeRegisteredServiceUsernameProvider provider =
            new PrincipalAttributeRegisteredServiceUsernameProvider("email");

        final Multimap<String, Object> allowedAttributes = ArrayListMultimap.create();
        final String mappedAttribute = "urn:oid:0.9.2342.19200300.100.1.3";
        allowedAttributes.put("email", mappedAttribute);
        final ReturnMappedAttributeReleasePolicy policy = new ReturnMappedAttributeReleasePolicy(CollectionUtils.wrap(allowedAttributes));
        final AbstractRegisteredService registeredService = RegisteredServiceTestUtils.getRegisteredService();
        registeredService.setAttributeReleasePolicy(policy);

        final Map<String, Object> principalAttributes = new HashMap<>();
        principalAttributes.put("email", "user@example.org");
        final Principal p = mock(Principal.class);
        when(p.getId()).thenReturn("person");
        when(p.getAttributes()).thenReturn(principalAttributes);
        final String id = provider.resolveUsername(p,
            RegisteredServiceTestUtils.getService("verifyUsernameByPrincipalAttributeWithMapping"), registeredService);
        assertEquals("user@example.org", id);
    }

    @Test
    public void verifyUsernameByPrincipalAttributeAsCollection() {
        final PrincipalAttributeRegisteredServiceUsernameProvider provider =
            new PrincipalAttributeRegisteredServiceUsernameProvider("cn");

        final Map<String, Object> attrs = new HashMap<>();
        attrs.put("userid", CollectionUtils.wrap("u1"));
        attrs.put("cn", CollectionUtils.wrap("TheName"));

        final Principal p = mock(Principal.class);
        when(p.getId()).thenReturn("person");
        when(p.getAttributes()).thenReturn(attrs);

        final String id = provider.resolveUsername(p, RegisteredServiceTestUtils.getService("usernameAttributeProviderService"),
            RegisteredServiceTestUtils.getRegisteredService("usernameAttributeProviderService"));
        assertEquals("TheName", id);
    }

    @Test
    public void verifyUsernameByPrincipalAttribute() {
        final PrincipalAttributeRegisteredServiceUsernameProvider provider =
            new PrincipalAttributeRegisteredServiceUsernameProvider("cn");

        final Map<String, Object> attrs = new HashMap<>();
        attrs.put("userid", "u1");
        attrs.put("cn", "TheName");

        final Principal p = mock(Principal.class);
        when(p.getId()).thenReturn("person");
        when(p.getAttributes()).thenReturn(attrs);

        final String id = provider.resolveUsername(p, RegisteredServiceTestUtils.getService("usernameAttributeProviderService"),
            RegisteredServiceTestUtils.getRegisteredService("usernameAttributeProviderService"));
        assertEquals("TheName", id);
    }

    @Test
    public void verifyUsernameByPrincipalAttributeNotFound() {
        final PrincipalAttributeRegisteredServiceUsernameProvider provider =
            new PrincipalAttributeRegisteredServiceUsernameProvider("cn");

        final Map<String, Object> attrs = new HashMap<>();
        attrs.put("userid", "u1");

        final Principal p = mock(Principal.class);
        when(p.getId()).thenReturn("person");
        when(p.getAttributes()).thenReturn(attrs);

        final String id = provider.resolveUsername(p, RegisteredServiceTestUtils.getService("usernameAttributeProviderService"),
            RegisteredServiceTestUtils.getRegisteredService("usernameAttributeProviderService"));
        assertEquals(id, p.getId());
    }

    @Test
    public void verifyEquality() {
        final PrincipalAttributeRegisteredServiceUsernameProvider provider =
            new PrincipalAttributeRegisteredServiceUsernameProvider("cn");
        final PrincipalAttributeRegisteredServiceUsernameProvider provider2 =
            new PrincipalAttributeRegisteredServiceUsernameProvider("cn");
        assertEquals(provider, provider2);
    }

    @Test
    public void verifySerializeAPrincipalAttributeRegisteredServiceUsernameProviderToJson() throws IOException {
        final PrincipalAttributeRegisteredServiceUsernameProvider providerWritten =
            new PrincipalAttributeRegisteredServiceUsernameProvider("cn");
        MAPPER.writeValue(JSON_FILE, providerWritten);
        final RegisteredServiceUsernameAttributeProvider providerRead = MAPPER.readValue(JSON_FILE, PrincipalAttributeRegisteredServiceUsernameProvider.class);
        assertEquals(providerWritten, providerRead);
    }
}
