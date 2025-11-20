package com.example.giftcards.giftcards.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter @Setter
public abstract class ModelEntity<ID> {
    @Id
    protected ID id;

    public boolean equals(Object o) {
        return this == o ||
                o != null &&
                        id != null &&
                        getClass() == o.getClass() &&
                        id.equals(((ModelEntity<?>) o).getId()) &&
                        same(o);
    }

    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    protected boolean same(Object o) { return true; }
}
