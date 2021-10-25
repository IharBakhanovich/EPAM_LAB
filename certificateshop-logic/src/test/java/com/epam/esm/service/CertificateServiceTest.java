package com.epam.esm.service;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.impl.CertificateServiceImpl;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Contains {@link CertificateService} tests.
 */
@ExtendWith({MockitoExtension.class})
public class CertificateServiceTest {
    @Mock
    CertificateDao certificateDAO;
    @Mock
    TagDao tagDAO;
    @Mock
    CertificateValidator certificateValidator;
    @Mock
    TagValidator tagValidator;
    @Mock
    Translator translator;

    @Spy
    @InjectMocks
    CertificateServiceImpl certificateService;

    /**
     * The test of the findAll() method.
     */
    @Test
    public void findAllTest() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        List<GiftCertificate> certificates = new ArrayList<>();
        certificates.add(giftCertificate);

        given(certificateDAO.findAll()).willReturn(certificates);
        Map<String, String> parameters = new HashMap<>();
        List<GiftCertificate> expectedCertificates = certificateService.findAllCertificates(parameters);
        Assertions.assertEquals(certificates, expectedCertificates);
    }

    /**
     * The test of the createCertificate() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveCertificateWithTheExistingName() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findByName(giftCertificate.getName())).willReturn(Optional.of(giftCertificate));
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(DuplicateException.class, () -> certificateService.createCertificate(giftCertificate));
        verify(certificateDAO, never()).save(any(GiftCertificate.class));
    }

    /**
     * The test of the findCertificateById() method.
     */
    @Test
    public void findByIdTest() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findById(giftCertificate.getId())).willReturn(Optional.of(giftCertificate));
        Optional<GiftCertificate> expectedGiftCertificate
                = Optional.ofNullable(certificateService.findCertificateById(giftCertificate.getId()));
        Assertions.assertEquals(Optional.of(giftCertificate), expectedGiftCertificate);
    }

    /**
     * The test of the findCertificateById() method.
     */
    @Test
    public void shouldThrowErrorByFindByIdTest() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findById(giftCertificate.getId())).willReturn(Optional.empty());
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> certificateService.findCertificateById(giftCertificate.getId()));
    }

    /**
     * The test of the updateCertificate() method.
     */
    @Test
    public void updateCertificateTest() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        when(certificateDAO.findById(giftCertificate.getId())).thenReturn(Optional.of(giftCertificate));
        final GiftCertificate expectedGiftCertificate
                = certificateService.updateCertificate(giftCertificate.getId(), giftCertificate);
        Assertions.assertNotNull(expectedGiftCertificate);
        verify(certificateDAO).update(any(GiftCertificate.class));
    }

    /**
     * The test of the findCertificateByName() method.
     */
    @Test
    public void findByNameTest() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findByName(giftCertificate.getName())).willReturn(Optional.of(giftCertificate));
        Optional<GiftCertificate> expectedGiftCertificate
                = Optional.ofNullable(certificateService.findCertificateByName(giftCertificate.getName()));
        Assertions.assertEquals(Optional.of(giftCertificate), expectedGiftCertificate);
    }

    /**
     * The test of the findCertificateByName() method.
     */
    @Test
    public void shouldThrowErrorByFindByName() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findByName(giftCertificate.getName())).willReturn(Optional.empty());
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> certificateService.findCertificateByName(giftCertificate.getName()));
    }

    /**
     * The test of the deleteCertificate() method.
     */
    @Test
    public void shouldThrowErrorByDeleteCertificateTest() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findById(giftCertificate.getId())).willReturn(Optional.empty());
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> certificateService.deleteCertificate(giftCertificate.getId()));
    }

    /**
     * The test of the deleteCertificate() method.
     */
    @Test
    public void deleteCertificate() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findById(giftCertificate.getId())).willReturn(Optional.of(giftCertificate));
        certificateService.deleteCertificate(giftCertificate.getId());
        verify(certificateDAO, times(1)).delete(giftCertificate.getId());
    }
}
