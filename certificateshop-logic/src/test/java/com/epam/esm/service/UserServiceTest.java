package com.epam.esm.service;

import com.epam.esm.configuration.Translator;
import com.epam.esm.dao.CertificateDao;
import com.epam.esm.dao.OrderDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.UserDao;
import com.epam.esm.dao.impl.jdbc.ColumnNames;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.UserDto;
import com.epam.esm.exception.DuplicateException;
import com.epam.esm.exception.MethodArgumentNotValidException;
import com.epam.esm.model.impl.*;
import com.epam.esm.service.impl.UserServiceImpl;
import com.epam.esm.validator.CertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.UserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * Contains {@link UserService} tests.
 */
@ExtendWith({MockitoExtension.class})
//@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @Mock
    CertificateDao certificateDao;
    @Mock
    TagDao tagDAO;
    @Mock
    OrderDao orderDao;
    @Mock
    UserDao userDao;
    @Mock
    ConversionService conversionService;

    @Mock
    CertificateValidator certificateValidator;
    @Mock
    TagValidator tagValidator;
    @Mock
    Translator translator;
    @Mock
    UserValidator userValidator;

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    /**
     * The test of the findAllUsers() method.
     */
    @Test
    public void shouldThrowErrorWhenPageNumberLessThan0InFindAllUsersMethodTest() {
        Map<String, String> parameters = new HashMap<String, String>() {{
            put(ColumnNames.PAGE_NUMBER_PARAM_NAME, "-1");
            put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, ColumnNames.DEFAULT_AMOUNT_ENTITIES_ON_THE_PAGE);
        }};
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findAllUsers(parameters));
    }

    /**
     * The test of the findAllUsers() method.
     */
    @Test
    public void shouldThrowErrorWhenAmountEntitiesOnThePageLessThan0InFindAllUsersMethodTest() {
        Map<String, String> parameters = new HashMap<String, String>() {{
            put(ColumnNames.PAGE_NUMBER_PARAM_NAME, "0");
            put(ColumnNames.AMOUNT_OF_ENTITIES_ON_THE_PAGE_PARAM_NAME, "-1");
        }};
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findAllUsers(parameters));
    }

    /**
     * The test of the findAllUsers() method.
     */
    @Test
    public void findAllUsersTest() {
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        User user2 = new User(2, "user2", "pass", Role.ROLE_USER);
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Map<String, String> parameters = ColumnNames.DEFAULT_PARAMS;
        given(userDao.findAllPagination(0, 5)).willReturn(users);
        List<UserDto> expectedUsers = userService.findAllUsers(parameters);
        List<UserDto> usersAsDtos = new ArrayList<>();
        for (User user : users) {
            usersAsDtos.add(conversionService.convert(user, UserDto.class));
        }
        Assertions.assertEquals(usersAsDtos, expectedUsers);
    }

    /**
     * The test of the findUserById() method.
     */
    @Test
    public void shouldThrowErrorWhenUserIdLessThan0InFindUserByIdMethodTest() {
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findUserById(-1));
    }

    /**
     * The test of the findUserById() method.
     */
    @Test
    public void findUserByIdMethodTest() {
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        given(userDao.findById(1)).willReturn(Optional.of(user1));
        UserDto expectedUser = userService.findUserById(user1.getId());
        Assertions.assertEquals(conversionService.convert(user1, UserDto.class), expectedUser);
    }

    /**
     * The test of the createUser() method.
     */
    @Test
    public void shouldThrowErrorDuringTheAttemptToCreateUserWithTheEmptyNameByCreateUserMethodTest() {
        User user1 = new User(1, "", "pass", Role.ROLE_USER);
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.createUser(user1));
    }

    /**
     * The test of the createUser() method.
     */
    @Test
    public void shouldThrowErrorDuringTheAttemptToCreateUserWithTheNullNameByCreateUserMethodTest() {
        User user1 = new User(1, null, "pass", Role.ROLE_USER);
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.createUser(user1));
    }

    /**
     * The test of the createUser() method.
     */
    @Test
    public void shouldThrowErrorWhenThereIsNoSuchUserInSystemInFindUserByIdMethodTest() {
        User user1 = new User(1, "user1", "pass", Role.ROLE_USER);
        given(userDao.findByName("user1")).willReturn(Optional.of(user1));
        given(translator.toLocale(any())).willReturn("test");
        Assertions.assertThrows(DuplicateException.class, () -> userService.createUser(user1));
    }

    /**
     * The test of the FindUserOrderByOrderIdCostAndTime() method.
     */
    @Test
    public void shouldThrowErrorDuringTheAttemptToGetResultWithUserIdLessThan0ByFindUserOrderByOrderIdCostAndTimeMethodTest() {
        User user1 = new User(1, "", "pass", Role.ROLE_USER);
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findUserOrderByOrderIdCostAndTime(-1, 1));
    }

    /**
     * The test of the FindUserOrderByOrderIdCostAndTime() method.
     */
    @Test
    public void shouldThrowErrorDuringTheAttemptToGetResultWithOrderIdLessThan0ByFindUserOrderByOrderIdCostAndTimeMethodTest() {
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findUserOrderByOrderIdCostAndTime(1, -1));
    }

    /**
     * The test of the FindUserOrderByOrderIdCostAndTime() method.
     */
    @Test
    public void shouldThrowErrorDuringTheAttemptToGetResultWithNoExistedUserByFindUserOrderByOrderIdCostAndTimeMethodTest() {

        given(translator.toLocale(any())).willReturn("test");
        given(userDao.findById(1)).willReturn(Optional.empty());
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findUserOrderByOrderIdCostAndTime(1, 1));
    }

    /**
     * The test of the FindUserOrderByOrderIdCostAndTime() method.
     */
    @Test
    public void shouldThrowErrorDuringTheAttemptToGetResultWithNoExistedOrderByFindUserOrderByOrderIdCostAndTimeMethodTest() {
        User user = new User(1, "user1", "pass", Role.ROLE_USER);
        given(translator.toLocale(any())).willReturn("test");
        given(userDao.findById(1)).willReturn(Optional.of(user));
        Assertions.assertThrows(MethodArgumentNotValidException.class, () -> userService.findUserOrderByOrderIdCostAndTime(1, 1));
    }

    /**
     * The test of the FindUserOrderByOrderIdCostAndTime() method.
     */
    @Test
    public void FindUserOrderByOrderIdCostAndTimeMethodTest() {
        final CertificateTag certificateTag1 = new CertificateTag(1L, "tag1");
        final CertificateTag certificateTag2 = new CertificateTag(2L, "tag2");
        List<CertificateTag> certificateTags1 = new ArrayList<>();
        certificateTags1.add(certificateTag1);
        certificateTags1.add(certificateTag2);
        final GiftCertificate giftCertificate1 = new GiftCertificate(
                1, "cert1", "certOneDescription", BigDecimal.ONE,
                30, LocalDateTime.now(), LocalDateTime.now(), certificateTags1
        );
        List<GiftCertificate> certificates1 = new ArrayList<>();
        certificates1.add(giftCertificate1);
        Order order1 = new Order(1, null, LocalDateTime.now(), "order1", certificates1);
        List<Order> orders = new ArrayList<>();
        orders.add(order1);
        User user = new User(1, "user1", "pass", Role.ROLE_USER);
        order1.setUser(user);
        given(userDao.findById(1)).willReturn(Optional.of(user));
        given(conversionService.convert(order1, OrderDto.class)).willReturn(new OrderDto(order1.getCertificates().get(0).getPrice(),
                order1.getCreateDate()));
        given(orderDao.findAllByUserId(user.getId())).willReturn(orders);
        OrderDto expected = userService.findUserOrderByOrderIdCostAndTime(1, 1);
        Assertions.assertEquals(new OrderDto(order1.getCertificates().get(0).getPrice(),
                order1.getCreateDate()), expected);
    }
}
