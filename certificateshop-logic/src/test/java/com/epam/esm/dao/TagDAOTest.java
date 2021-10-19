package com.epam.esm.dao;

import com.epam.esm.configuration.TestConfig;
import com.epam.esm.model.impl.CertificateTag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.List;
import java.util.Optional;

/**
 * Contains {@link TagDAO} tests.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class TagDAOTest {
    @Autowired
    private TagDAO tagDAO;

    /**
     * the test of the findAll() method.
     */
    @Test
    public void testFindAll() {
        List<CertificateTag> allGiftCertificates = tagDAO.findAll();
        Assertions.assertEquals(7, allGiftCertificates.size());
    }

    /**
     * The test of the findById() method.
     */
    @Test
    public void testFindById() {
        CertificateTag certificateTag = tagDAO.findById(1).get();
        Assertions.assertEquals("tag1", certificateTag.getName());
    }

    /**
     * The test tof the findByName() method.
     */
    @Test
    public void testFindByName() {
        CertificateTag certificateTag = tagDAO.findByName("tag1").get();
        Assertions.assertEquals("tag1", certificateTag.getName());
    }

    /**
     * The test of the save() method.
     */
    @Test
    public void testSave() {
        CertificateTag certificateTag = new CertificateTag(0L, "tag8");
        tagDAO.save(certificateTag);
        CertificateTag certificateTag1 = tagDAO.findByName("tag8").get();
        Assertions.assertEquals("tag8", certificateTag1.getName());
    }

    /**
     * The test of the delete() method.
     */
    @Test
    public void testDelete() {
        tagDAO.delete(1);
        Optional<CertificateTag> certificateTag = tagDAO.findById(1);
        Assertions.assertFalse(certificateTag.isPresent());
    }

    /**
     * The test of the update() method.
     */
    @Test
    public void testUpdate() {
        CertificateTag certificateTag = tagDAO.findAll().get(0);
        String name = certificateTag.getName();
        certificateTag.setName("NewTagName");
        tagDAO.update(certificateTag);
        CertificateTag certificateTagAfterUpdate = tagDAO.findById(certificateTag.getId()).get();
        Assertions.assertEquals(certificateTagAfterUpdate.getName(), "NewTagName");
    }

    /**
     * The test of the Find all tagsByCertificateId method.
     */
    @Test
    public void testFindAllTagsByCertificateId() {
        List<CertificateTag> tags = tagDAO.findAllTagsByCertificateId(2);
        Assertions.assertEquals(3, tags.size());
    }
}