package com.omerkoc.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import org.springframework.data.domain.Persistable;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User implements Persistable<UUID> {
    @Id
    private UUID id;
    private String email;
    private String firstName;
    private String username;
    private String lastName;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @jakarta.persistence.PostLoad
    @jakarta.persistence.PrePersist
    void trackNotNew() {
        this.isNew = false;
    }
}
