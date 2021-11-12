package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import com.epam.esm.model.converter.CertificateFromJsonConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The POJO that describes the {@link Order} entity in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "userorder")
@Table(
        schema = "certificates",
        name = "userorder"
)
@NamedNativeQueries(value = {
        @NamedNativeQuery(name
                = "Order.findById"
                , query = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
                " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
                " from user as u" +
                " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
                " ON u.id = uo.userId where uo.id = ?;",
                resultClass = Order.class),
        @NamedNativeQuery(name
                = "Order.findAll"
                , query = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
                " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
                " from user as u" +
                " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
                " ON u.id = uo.userId;",
                resultClass = Order.class),
        @NamedNativeQuery(name
                = "Order.findAllPagination"
                , query = "select u.id as userId, u.nickName as userNickName, uo.id as userOrderId," +
                " uo.create_date as orderCreateDate, uo.name as orderName, uoc.certificateInJSON as orderCertificate" +
                " from user as u" +
                " LEFT OUTER JOIN (userorder as uo LEFT OUTER JOIN userorder_certificate as uoc ON uo.id = uoc.userOrderId)" +
                " ON u.id = uo.userId" +
                " WHERE uo.id IN (select * from (select id from userorder order by id LIMIT ?, ?) as query1);",
                resultClass = Order.class)
})
public class Order implements DatabaseEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;
    @JoinColumn(name = "userid")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private User user;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Column(name = "create_date", updatable = false)
    @CreationTimestamp
    private LocalDateTime createDate;
    @Column(name = "name", unique = true, nullable = false)
    @NotNull
    private String name;
    @Convert(converter = CertificateFromJsonConverter.class)
    private List<GiftCertificate> certificates;
}
