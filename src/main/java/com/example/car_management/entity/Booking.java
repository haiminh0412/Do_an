package com.example.car_management.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Booking {
    public static final String TABLE_NAME = "booking";
    public static final String BOOKING_ID = "bookingId";
    public static final String START_DESTIANTION = "startDestination";
    public static final String END_DESTIANTION = "endDestination";
    public static final String LICENSE_PLATE = "license_plate";
    public static final String DEPARTURE_DATE = "departureDate";
    public static final String BOOKING_AT = "bookingAt";
    public static final String UPDATE_BOOKING_AT = "updatedBookingAt";
    public static final String CUSTOMER = "customer";
    public static final String TRIP_DETAIL = "tripDetail";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Booking_ID")
    private Integer bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Trip_Detail_ID", nullable = false, referencedColumnName = "Trip_Detail_ID")
    @JsonBackReference
    private TripDetail tripDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_ID", nullable = false, referencedColumnName = "Customer_ID")
    @JsonBackReference
    private Customer customer;

    @Column(name = "start_destination")
    private String startDestination;

    @Column(name = "end_destination")
    private String endDestination;

    @Column(name = "departure_date")
    private LocalDate departureDate;

    @Column(name = "booking_at", insertable = false, updatable = false)
    private LocalDateTime bookingAt;

    @Column(name = "update_booking_at", insertable = false, updatable = false)
    private LocalDateTime updateBookingAt;

    @Column(name = "status")
    private String status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking )) return false;
        return bookingId != null && bookingId.equals(((Booking) o).getBookingId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<BookingSeat> bookingSeats = new HashSet<>();

    public void addBookingSeat(BookingSeat bookingSeat) {
        bookingSeats.add(bookingSeat);
        bookingSeat.setBooking(this);
    }

    public void removeBookingSeat(BookingSeat bookingSeat) {
        bookingSeats.remove(bookingSeat);
        bookingSeat.setBooking(null);
    }

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<Notification> notifications = new HashSet<>();

    public void addNotification(Notification notification) {
        if(notification != null) {
            notifications.add(notification);
            notification.setBooking(this);
        }
    }

    public void removeNotification(Notification notification) {
        if(notification != null) {
            notifications.remove(notification);
            notification.setBooking(null);
        }
    }

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<VerificationCode> verificationCodes = new HashSet<>();

    public void addVerificationCode(VerificationCode verificationCode) {
        if(verificationCode != null) {
            verificationCodes.add(verificationCode);
            verificationCode.setBooking(this);
        }
    }

    public void removeVerificationCode(VerificationCode verificationCode) {
        if(verificationCode != null) {
            verificationCodes.remove(verificationCode);
            verificationCode.setBooking(null);
        }
    }

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<HistoryBooking> historyBookings = new HashSet<>();

    public void addHistoryBooking(HistoryBooking historyBooking) {
        historyBookings.add(historyBooking);
        historyBooking.setBooking(this);
    }

    public void removeHistoryBooking(HistoryBooking historyBooking) {
        historyBookings.remove(historyBooking);
        historyBooking.setBooking(null);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", startDestination=" + startDestination + '\'' +
                ", endDestination=" + endDestination + '\'' +
                ", departureDate=" + departureDate + '\'' +
                ", bookingAt=" + bookingAt +
                ", updateBookingAt=" + updateBookingAt +
                '}';
    }
}
