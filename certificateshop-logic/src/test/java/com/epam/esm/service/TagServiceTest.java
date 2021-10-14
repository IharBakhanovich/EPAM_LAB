package com.epam.esm.service;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.service.impl.TagServiceImpl;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class TagServiceTest {

    @Mock
    TagDAO tagDAO;
    @Mock
    TagValidator tagValidator;
    @Mock
    Translator translator;

    TagService tagService;

    @BeforeEach
    void initUseCase() {
        tagService
                = new TagServiceImpl(tagDAO, tagValidator, translator);
    }

    @Test
    public void findAllCertificateTags() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag1 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags = new ArrayList<>();
        certificateTags.add(certificateTag);
        certificateTags.add(certificateTag1);
        given(tagDAO.findAll()).willReturn(certificateTags);
        List<CertificateTag> expectedTags = tagService.findAllCertificateTags();
        Assertions.assertEquals(certificateTags, expectedTags);
    }

    @Test
    public void shouldThrowErrorWhenSaveCertificateWithTheExistingName() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");

        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.of(certificateTag));
        Assertions.assertThrows(NullPointerException.class, () -> tagService.createCertificateTag(certificateTag));
        verify(tagDAO, never()).save(any(CertificateTag.class));
    }

    @Test
    public void findById() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.of(certificateTag));
        Optional<CertificateTag> expectedCertificateTag
                = Optional.ofNullable(tagService.findCertificateTagById(certificateTag.getId()));
        Assertions.assertEquals(Optional.of(certificateTag), expectedCertificateTag);
    }

    @Test
    public void shouldThrowErrorByFindById() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.empty());
        Assertions.assertThrows(NullPointerException.class,
                () -> tagService.findCertificateTagById(certificateTag.getId()));
    }

    @Test
    public void updateCertificateTag() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        when(tagDAO.findById(certificateTag.getId())).thenReturn(Optional.of(certificateTag));
        final CertificateTag expectedCertificateTag
                = tagService.updateCertificateTag(certificateTag.getId(), certificateTag);
        Assertions.assertNotNull(expectedCertificateTag);
        verify(tagDAO).update(any(CertificateTag.class));
    }

    @Test
    public void findByName() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.of(certificateTag));
        Optional<CertificateTag> expectedCertificateTag
                = Optional.ofNullable(tagService.findCertificateTagByName(certificateTag.getName()));
        Assertions.assertEquals(Optional.of(certificateTag), expectedCertificateTag);
    }

    @Test
    public void shouldThrowErrorByFindByName() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.empty());
        Assertions.assertThrows(NullPointerException.class,
                () -> tagService.findCertificateTagByName(certificateTag.getName()));
    }

    @Test
    public void shouldThrowErrorByDeleteCertificateTag() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.empty());
        Assertions.assertThrows(NullPointerException.class,
                () -> tagService.deleteCertificateTag(certificateTag.getId()));
    }

    @Test
    public void deleteCertificate() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.of(certificateTag));
        tagService.deleteCertificateTag(certificateTag.getId());
        verify(tagDAO, times(1)).delete(certificateTag.getId());
    }
}