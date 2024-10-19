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
@Table(name = "Notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Notification_ID")
    private Integer notificationId;

    @Column(name = "Sent_At", insertable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "Type", length = 50, nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Booking_ID", nullable = false, referencedColumnName = "Booking_ID")
    @JsonBackReference
    private Booking booking;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking )) return false;
        return notificationId != null && notificationId.equals(((Notification) o).getNotificationId());
    }

    // toString method
    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", booking=" + booking +
                ", sentAt=" + sentAt +
                ", type='" + type + '\'' +
                '}';
    }
}