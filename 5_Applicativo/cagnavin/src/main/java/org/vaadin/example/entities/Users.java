package org.vaadin.example.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data

public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;

    @Lob
    private byte[] profilePicture;
}
