package com.epam.esm.service;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.TagDAO;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.service.impl.TagServiceImpl;
import com.epam.esm.validator.TagValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Contains {@link TagService} tests.
 */
@ExtendWith({MockitoExtension.class})
public class TagServiceTest {

    @Mock
    TagDAO tagDAO;
    @Mock
    TagValidator tagValidator;
    @Mock
    Translator translator;

    @Spy
    @InjectMocks
    TagServiceImpl tagService;

    @BeforeEach
    void setUp() {
    }

    /**
     * The test of the findAllCertificateTags() method.
     */
    @Test
    public void findAllCertificateTagsTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag1 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags = new ArrayList<>();
        certificateTags.add(certificateTag);
        certificateTags.add(certificateTag1);
        given(tagDAO.findAll()).willReturn(certificateTags);
        List<CertificateTag> expectedTags = tagService.findAllCertificateTags();
        Assertions.assertEquals(certificateTags, expectedTags);
    }

    /**
     * The test of the createCertificateTag() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveCertificateTagWithTheExistingNameTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.of(certificateTag));
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(DuplicateException.class, () -> tagService.createCertificateTag(certificateTag));
        verify(tagDAO, never()).save(any(CertificateTag.class));
    }

    /**
     * The test of the findCertificateTagById() method.
     */
    @Test
    public void findByIdTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.of(certificateTag));
        Optional<CertificateTag> expectedCertificateTag
                = Optional.ofNullable(tagService.findCertificateTagById(certificateTag.getId()));
        Assertions.assertEquals(Optional.of(certificateTag), expectedCertificateTag);
    }

    /**
     * The test of the findCertificateTagById() method.
     */
    @Test
    public void shouldThrowErrorByFindByIdTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.empty());
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> tagService.findCertificateTagById(certificateTag.getId()));
    }

    /**
     * The test of the updateCertificateTag() method.
     */
    @Test
    public void updateCertificateTagTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        when(tagDAO.findById(certificateTag.getId())).thenReturn(Optional.of(certificateTag));
        final CertificateTag expectedCertificateTag
                = tagService.updateCertificateTag(certificateTag.getId(), certificateTag);
        Assertions.assertNotNull(expectedCertificateTag);
        verify(tagDAO).update(any(CertificateTag.class));
    }

    /**
     * The test of the findCertificateTagByName() method.
     */
    @Test
    public void findByNameTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.of(certificateTag));
        Optional<CertificateTag> expectedCertificateTag
                = Optional.ofNullable(tagService.findCertificateTagByName(certificateTag.getName()));
        Assertions.assertEquals(Optional.of(certificateTag), expectedCertificateTag);
    }

    /**
     * The test of the findCertificateTagByName() method.
     */
    @Test
    public void shouldThrowErrorByFindByNameTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.empty());
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> tagService.findCertificateTagByName(certificateTag.getName()));
    }

    /**
     * The test of the deleteCertificateTag() method.
     */
    @Test
    public void shouldThrowErrorByDeleteCertificateTagTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.empty());
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> tagService.deleteCertificateTag(certificateTag.getId()));
    }

    /**
     * The test of the deleteCertificateTag() method.
     */
    @Test
    public void deleteCertificateTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findById(certificateTag.getId())).willReturn(Optional.of(certificateTag));
        tagService.deleteCertificateTag(certificateTag.getId());
        verify(tagDAO, times(1)).delete(certificateTag.getId());
    }

    /**
     * The test of the createCertificateTag() method.
     */
    @Test
    public void createCertificateTagTest() {
        final CertificateTag certificateTag = new CertificateTag(1L, "tag1");
        given(tagDAO.findByName(certificateTag.getName())).willReturn(Optional.empty());
//        given(tagService.findCertificateTagByName(any())).willReturn(certificateTag);
        doReturn(certificateTag).when(tagService).findCertificateTagByName(any()); //изменили поведение реального метода
        tagService.createCertificateTag(certificateTag);
        verify(tagDAO, times(1)).save(certificateTag);
    }
}