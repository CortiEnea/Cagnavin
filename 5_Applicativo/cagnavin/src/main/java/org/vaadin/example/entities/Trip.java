package org.vaadin.example.entities;

import com.vaadin.flow.component.textfield.NumberField;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data

public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String destinazione;
    private LocalDate data;
    private Integer nMinPartecipanti;
    private Integer nPartecipanti;
    private Integer nMaxPartecipanti;
    private double quota;
    private String descrizione;
    private String pranzo;

    @Lob
    private byte[] url_immagine;
}
