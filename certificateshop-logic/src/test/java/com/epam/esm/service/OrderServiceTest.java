package com.epam.esm.service;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.*;
import com.epam.esm.service.impl.OrderServiceImpl;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Contains {@link OrderService} tests.
 */
@ExtendWith({MockitoExtension.class})
//@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderServiceTest {
    @Mock
    CertificateDao certificateDao;
    @Mock
    TagDao tagDAO;
    @Mock
    OrderDao orderDao;
    @Mock
    UserDao userDao;

    @Mock
    CertificateValidator certificateValidator;
    @Mock
    TagValidator tagValidator;
    @Mock
    Translator translator;

    @Spy
    @InjectMocks
    OrderServiceImpl orderService;

    /**
     * The test of the findAllOrders() method.
     */
    @Test
    public void shouldThrowErrorWhenPageNumberLessThan0InFindAllMethodTest() {
        Map<String, String> parameters = new HashMap<String, String>() {{
            put(ColumnNames.PAGE_NUMBER_PARAM_NAME, "-1");
            put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, ColumnNames.DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
        }};
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> orderService.findAllOrders(parameters));
    }

    /**
     * The test of the findAllOrders() method.
     */
    @Test
    public void shouldThrowErrorWhenAmountEntitiesOnThePageLessThan0InFindAllMethodTest() {
        Map<String, String> parameters = new HashMap<String, String>() {{
            put(ColumnNames.PAGE_NUMBER_PARAM_NAME, "0");
            put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, "-1");
        }};
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> orderService.findAllOrders(parameters));
    }

    /**
     * The test of the findAllOrders() method.
     */
    @Test
    public void findAllOrdersTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        certificates2.add(giftCertificate2);
        User user = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user, LocalDateTime.now(), "order1", certificates1);
        Order order2 = new Order(2, user, LocalDateTime.now(), "order2", certificates2);
        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        Map<String, String> parameters = ColumnNames.DEFAULT_PARAMS;
        given(orderDao.findAllPagination(0, 5)).willReturn(orders);
        List<Order> expectedOrders = orderService.findAllOrders(parameters);
        Assertions.assertEquals(orders, expectedOrders);
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithTheExistingIdTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        certificates2.add(giftCertificate2);
        User user = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user, LocalDateTime.now(), "order1", certificates1);
        Order order2 = new Order(1, user, LocalDateTime.now(), "order1", certificates2);
        given(certificateDao.findById(order1.getCertificates().get(1).getId())).willReturn(Optional.of(giftCertificate2));
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.of(user));
        Assertions.assertThrows(RuntimeException.class, () -> orderService.createOrder(order2));
        verify(orderDao, times(1)).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithTheWrongUserIdTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        certificates2.add(giftCertificate2);
        User user = new User(-1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(certificateDao.findById(order1.getCertificates().get(0).getId())).willReturn(Optional.of(giftCertificate1));
        given(certificateDao.findById(order1.getCertificates().get(1).getId())).willReturn(Optional.of(giftCertificate2));
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.of(user));
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithUserThatDoesNotExistTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        certificates2.add(giftCertificate2);
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        User user2 = new User(2, "user2", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(certificateDao.findById(order1.getCertificates().get(0).getId())).willReturn(Optional.of(giftCertificate1));
        given(certificateDao.findById(order1.getCertificates().get(1).getId())).willReturn(Optional.of(giftCertificate2));
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithUserWithEmptyCertificatesTest() {
        List<GiftCertificate> certificates2 = new ArrayList<>();
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates2);
        given(translator.toLocale(any())).willReturn("test");
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithUserWithId0Test() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        User user1 = new User(0, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithCertificateThatHasIdLessThan0Test() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                -2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        certificates2.add(giftCertificate2);
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        User user2 = new User(2, "user2", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(certificateDao.findById(order1.getCertificates().get(0).getId())).willReturn(Optional.of(giftCertificate1));
        given(certificateDao.findById(order1.getCertificates().get(1).getId())).willReturn(Optional.of(giftCertificate2));
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithCertificateThatHasId0Test() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                0, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(certificateDao.findById(order1.getCertificates().get(0).getId())).willReturn(Optional.of(giftCertificate1));
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowErrorWhenSaveOrderWithCertificateThatDoesNotExistTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                0, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        List<GiftCertificate> certificates2 = new ArrayList<>();
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(certificateDao.findById(order1.getCertificates().get(0).getId())).willReturn(Optional.empty());
        given(userDao.findById(order1.getUser().getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(order1));
        verify(orderDao, never()).save(any(Order.class));
    }

    /**
     * The test of the createOrder() method.
     */
    @Test
    public void shouldThrowExceptionInfindByIdMethodIfIdLessThan0Test() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                -2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.findOrderById(-1));
    }

    /**
     * The test of the findOrderById() method.
     */
    @Test
    public void shouldThrowExceptionInfindByIdMethodIfThereIsNoOrderWithSuchIdTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                -2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(translator.toLocale(any())).willReturn("test");
        given(orderDao.findById(order1.getId())).willReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> orderService.findOrderById(1));
    }

    /**
     * The test of the findOrderById() method.
     */
    @Test
    public void shouldReturnOrderInFindByIdMethodIfThereIsOrderWithSuchIdTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<CertificateTag> certificateTags2 = new ArrayList<>();
        final GiftCertificate giftCertificate2 = new GiftCertificate(
                -2, "cert2", "certTwoDescription", BigDecimal.TEN,
                50, LocalDateTime.now(), LocalDateTime.now(), certificateTags2
        );
        certificateTags2.add(certificateTag1);
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        certificates1.add(giftCertificate2);
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        Order order1 = new Order(1, user1, LocalDateTime.now(), "order1", certificates1);
        given(orderDao.findById(order1.getId())).willReturn(Optional.of(order1));
        Optional<Order> expectedOrder = Optional.ofNullable(orderService.findOrderById(order1.getId()));
        Assertions.assertEquals(Optional.of(order1), expectedOrder);
    }
}
