package com.epam.esm.dao;

import com.epam.esm.configuration.LogicConfig;
import com.epam.esm.configuration.TestConfig;
import com.epam.esm.dao.impl.jdbc.JdbcCertificateDaoImpl;
import com.epam.esm.dao.impl.jdbc.JdbcOrderDaoImpl;
import com.epam.esm.dao.impl.jdbc.JdbcTagDaoImpl;
import com.epam.esm.dao.impl.jdbc.JdbcUserDaoImpl;
import com.epam.esm.dao.impl.jpa.JpaCertificateDaoImpl;
import com.epam.esm.model.impl.CertificateTag;
import com.epam.esm.model.impl.GiftCertificate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Contains {@link CertificateDao} tests.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class}, loader = AnnotationConfigContextLoader.class)
public class CertificateDaoTest {

    @Autowired
    @Qualifier("certificateDAO")
    private CertificateDao certificateDao;

    /**
     * The test of the findAll() method.
     */
    @Test
    public void testFindAll() {
        List<GiftCertificate> allGiftCertificates = certificateDao.findAll();
        Assertions.assertEquals(8, allGiftCertificates.size());
    }

    /**
     * The test of the findById() method.
     */
    @Test
    public void testFindById() {
        GiftCertificate giftCertificate = certificateDao.findById(1).get();
        Assertions.assertEquals(1, giftCertificate.getId());
    }

    /**
     * the test of the findByName() method.
     */
    @Test
    public void testFindByName() {
        GiftCertificate giftCertificate = certificateDao.findByName("cert8").get();
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
        certificateDao.save(giftCertificate);
        GiftCertificate giftCertificate1 = certificateDao.findByName("cert9").get();
        Assertions.assertEquals("cert9", giftCertificate1.getName());
    }

    /**
     * The test of the delete() method.
     */
    @Test
    public void testDelete() {
        certificateDao.delete(1);
        Optional<GiftCertificate> giftCertificate = certificateDao.findById(1);
        Assertions.assertFalse(giftCertificate.isPresent());
    }

    /**
     * The test of the update() method.
     */
    @Test
    public void testUpdate() {
        GiftCertificate giftCertificate = certificateDao.findAll().get(0);
        String description = giftCertificate.getDescription();
        giftCertificate.setDescription("NewDescription");
        certificateDao.update(giftCertificate);
        GiftCertificate giftCertificateAfterUpdate = certificateDao.findById(giftCertificate.getId()).get();
        Assertions.assertEquals(giftCertificateAfterUpdate.getDescription(), "NewDescription");
    }

    /**
     * the test of the saveIdsInHas_tagTable() method.
     */
    @Test
    public void testSaveIdsInHas_tagTable() {
        GiftCertificate giftCertificate = certificateDao.findById(8).get();
        Assertions.assertEquals(0, giftCertificate.getTags().size());
        certificateDao.saveIdsInHas_tagTable(8, 1);
        certificateDao.saveIdsInHas_tagTable(8, 2);
        GiftCertificate giftCertificateAfterSaveInHas_tagTable = certificateDao.findById(8).get();
        Assertions.assertEquals(2, giftCertificateAfterSaveInHas_tagTable.getTags().size());
    }

    /**
     * The test of the deleteIdsInHas_TagTable() method.
     */
    @Test
    public void testDeleteIdsInHas_TagTable() {
        GiftCertificate giftCertificate = certificateDao.findById(7).get();
        Assertions.assertEquals(1, giftCertificate.getTags().size());
        certificateDao.deleteIdsInHas_TagTable(7L, 7L);
        GiftCertificate giftCertificateAfterDelete = certificateDao.findById(7).get();
        Assertions.assertEquals(0, giftCertificateAfterDelete.getTags().size());
    }
}
