package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The POJO that describes the {@link GiftCertificate} entity in the system.
 */
public class GiftCertificate implements DatabaseEntity {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private long duration;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastUpdateDate;
    private List<CertificateTag> tags;

    /**
     * Constructs a {@link GiftCertificate}.
     */
    public GiftCertificate() {
    }

    /**
     * Constructs a {@link GiftCertificate} with all the parameters.
     * @param id the 'id'.
     * @param name the 'name'.
     * @param description the 'description'.
     * @param price the 'price'.
     * @param duration the 'duration'.
     * @param createDate the date of creating.
     * @param lastUpdateDate the date of the last updating.
     * @param tags all the tags of this {@link GiftCertificate}.
     */
    public GiftCertificate(long id, String name, String description, BigDecimal price, long duration,
                           LocalDateTime createDate, LocalDateTime lastUpdateDate, List<CertificateTag> tags) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(LocalDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<CertificateTag> getTags() {
        return tags;
    }

    public void setTags(List<CertificateTag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GiftCertificate that = (GiftCertificate) o;
        return id == that.id
                && duration == that.duration
                && name.equals(that.name)
                && description.equals(that.description)
                && price.equals(that.price)
                && createDate.equals(that.createDate)
                && lastUpdateDate.equals(that.lastUpdateDate)
                && tags.equals(that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, duration, createDate, lastUpdateDate, tags);
    }

    @Override
    public String toString() {
        return "GiftCertificate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", duration=" + duration +
                ", createDate=" + createDate +
                ", lastUpdateDate=" + lastUpdateDate +
                ", tags=" + tags +
                '}';
    }
}