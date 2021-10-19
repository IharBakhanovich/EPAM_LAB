package com.epam.esm.dao;

import com.epam.esm.configuration.TestConfig;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Contains {@link CertificateDAO} tests.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class, loader = AnnotationConfigContextLoader.class)
public class CertificateDAOTest {

    @Autowired
    private CertificateDAO certificateDAO;

    /**
     * The test of the findAll() method.
     */
    @Test
    public void testFindAll() {
        List<GiftCertificate> allGiftCertificates = certificateDAO.findAll();
        Assertions.assertEquals(8, allGiftCertificates.size());
    }

    /**
     * The test of the findById() method.
     */
    @Test
    public void testFindById() {
        GiftCertificate giftCertificate = certificateDAO.findById(1).get();
        Assertions.assertEquals(1, giftCertificate.getId());
    }

    /**
     * the test of the findByName() method.
     */
    @Test
    public void testFindByName() {
        GiftCertificate giftCertificate = certificateDAO.findByName("cert8").get();
        Assertions.assertEquals("cert8", giftCertificate.getName());
    }

    /**
     * The test of the save() method.
     */
    @Test
    public void testSave() {
        List<CertificateTag> tags = new ArrayList<>();
        GiftCertificate giftCertificate = new GiftCertificate(
                0, "cert9", "certNineDescription", BigDecimal.ONE, 30, LocalDateTime.now(), LocalDateTime.now(), tags
        );
        certificateDAO.save(giftCertificate);
        GiftCertificate giftCertificate1 = certificateDAO.findByName("cert9").get();
        Assertions.assertEquals("cert9", giftCertificate1.getName());
    }

    /**
     * The test of the delete() method.
     */
    @Test
    public void testDelete() {
        certificateDAO.delete(1);
        Optional<GiftCertificate> giftCertificate = certificateDAO.findById(1);
        Assertions.assertFalse(giftCertificate.isPresent());
    }

    /**
     * The test of the update() method.
     */
    @Test
    public void testUpdate() {
        GiftCertificate giftCertificate = certificateDAO.findAll().get(0);
        String description = giftCertificate.getDescription();
        giftCertificate.setDescription("NewDescription");
        certificateDAO.update(giftCertificate);
        GiftCertificate giftCertificateAfterUpdate = certificateDAO.findById(giftCertificate.getId()).get();
        Assertions.assertEquals(giftCertificateAfterUpdate.getDescription(), "NewDescription");
    }

    /**
     * the test of the saveIdsInHas_tagTable() method.
     */
    @Test
    public void testSaveIdsInHas_tagTable() {
        GiftCertificate giftCertificate = certificateDAO.findById(8).get();
        Assertions.assertEquals(0, giftCertificate.getTags().size());
        certificateDAO.saveIdsInHas_tagTable(8,1);
        certificateDAO.saveIdsInHas_tagTable(8,2);
        GiftCertificate giftCertificateAfterSaveInHas_tagTable = certificateDAO.findById(8).get();
        Assertions.assertEquals(2, giftCertificateAfterSaveInHas_tagTable.getTags().size());
    }

    /**
     * The test of the deleteIdsInHas_TagTable() method.
     */
    @Test
    public void testDeleteIdsInHas_TagTable() {
        GiftCertificate giftCertificate = certificateDAO.findById(7).get();
        Assertions.assertEquals(1, giftCertificate.getTags().size());
        certificateDAO.deleteIdsInHas_TagTable(7L,7L);
        GiftCertificate giftCertificateAfterDelete = certificateDAO.findById(7).get();
        Assertions.assertEquals(0, giftCertificateAfterDelete.getTags().size());
    }
}