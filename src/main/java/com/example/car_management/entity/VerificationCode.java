package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Verification_Code")
public class VerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", nullable = false, length = 7)
    private String code;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Booking_ID", nullable = false, referencedColumnName = "Booking_ID")
    @JsonBackReference
    private Booking booking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking )) return false;
        return id != null && id.equals(((VerificationCode) o).getId());
    }


    @Column(name = "sent_at", insertable = false, updatable = false)
    private LocalDateTime sentAt;


    @Override
    public String toString() {
        return "VerificationCode{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", type='" + type + '\'' +
                ", booking=" + booking +
                ", sentAt=" + sentAt +
                '}';
    }
}
