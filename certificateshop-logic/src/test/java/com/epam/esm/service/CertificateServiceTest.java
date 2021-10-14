package com.epam.esm.service;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDAO;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import com.epam.esm.service.impl.CertificateServiceImpl;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class CertificateServiceTest {
    @Mock
    CertificateDAO certificateDAO;
    @Mock
    TagDAO tagDAO;
    @Mock
    CertificateValidator certificateValidator;
    @Mock
    TagValidator tagValidator;
    @Mock
    Translator translator;

    CertificateService certificateService;

    @BeforeEach
    void initUseCase() {
        certificateService
                = new CertificateServiceImpl(certificateDAO, tagDAO, certificateValidator, tagValidator, translator);
    }

    @Test
    public void findAll() {
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

    @Test
    public void shouldThrowErrorWhenSaveCertificateWithTheExistingName() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findByName(giftCertificate.getName())).willReturn(Optional.of(giftCertificate));
        Assertions.assertThrows(NullPointerException.class, () -> certificateService.createCertificate(giftCertificate));
        verify(certificateDAO, never()).save(any(GiftCertificate.class));
    }

    @Test
    public void findById() {
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

    @Test
    public void shouldThrowErrorByFindById() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findById(giftCertificate.getId())).willReturn(Optional.empty());
        Assertions.assertThrows(NullPointerException.class,
                () -> certificateService.findCertificateById(giftCertificate.getId()));
    }

    @Test
    public void updateCertificate() {
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

    @Test
    public void findByName() {
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

    @Test
    public void shouldThrowErrorByFindByName() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findByName(giftCertificate.getName())).willReturn(Optional.empty());
        Assertions.assertThrows(NullPointerException.class,
                () -> certificateService.findCertificateByName(giftCertificate.getName()));
    }

    @Test
    public void shouldThrowErrorByDeleteCertificate() {
        List<CertificateTag> tags = new ArrayList<>();
        final GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        given(certificateDAO.findById(giftCertificate.getId())).willReturn(Optional.empty());
        Assertions.assertThrows(NullPointerException.class,
                () -> certificateService.deleteCertificate(giftCertificate.getId()));
    }

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