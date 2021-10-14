package com.epam.esm.model.impl;

import com.epam.esm.model.DatabaseEntity;

import java.util.Objects;

public class CertificateTag implements DatabaseEntity {
    private long id;
    private String name;

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

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

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