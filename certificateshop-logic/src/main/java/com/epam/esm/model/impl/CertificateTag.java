package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;

import java.util.Objects;

/**
 * The POJO that describes the CertificateTag.
 */
public class CertificateTag implements DatabaseEntity {
    private long id;
    private String name;

    /**
     * Constructs the {@link CertificateTag}.
     */
    public CertificateTag() {
    }

    /*
     * Constructs new CertificateTag.
     */
    public CertificateTag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * A Getter for the entities id.
     *
     * @return The entities id.
     */
    public Long getId() {
        return id;
    }

    /**
     * The setter of the id.
     *
     * @param id is the {@link long} to set.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * A Getter for the {@link CertificateTag} name.
     *
     * @return The {@link CertificateTag} name.
     */
    public String getName() {
        return name;
    }

    /**
     * The setter of the name.
     *
     * @param name is the {@link String} to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CertificateTag that = (CertificateTag) o;
        return id == that.id && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "CertificateTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}