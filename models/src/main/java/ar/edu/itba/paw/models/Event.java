    package ar.edu.itba.paw.models;

    import lombok.Getter;
    import lombok.Setter;
    import org.hibernate.annotations.Formula;
    import javax.persistence.*;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;

    @Getter
    @Entity
    @Table(name="events")
    public class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_id_seq")
        @SequenceGenerator(sequenceName = "events_id_seq", name = "events_id_seq", allocationSize = 1)
        @Column(name = "id")
        private  Long id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        private  User user;

        @Column(name = "event_date", nullable = false)
        @Setter
        private  LocalDate date;

        @Column(length = 2047)
        @Setter
        private  String description;

        @Column(name = "flyer_image_id")
        @Setter
        private Long flyerImageId;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "city_id")
        @Setter
        private  City city;

        @Column(nullable = false)
        @Setter
        private  String title;

        @Column(name = "event_time")
        @Setter
        private  LocalTime time;

        @Column(name = "address", length = 255)
        @Setter
        private  String address;

        @Column(name = "attendees_limit")
        @Setter
        private  Integer attendeesLimit;

        @Formula("(SELECT COUNT(*) FROM event_attendances ea WHERE ea.event_id = id)")
        private  int attendeesCount;

        @Formula("(SELECT AVG(r.rating) FROM ratings r WHERE r.event_id = id)")
        private Double rating;

        @Column(name="deleted", nullable = false)
        @Setter
        private  boolean deleted;

        @Column(name="deleted_message",  length = 1000)
        @Setter
        private String deletionMessage;




        /* For hibernate */ Event() {
        }
        public Event(final User user, final LocalDate date, final String description,
                     final Long flyerImageId, final City city, final String title,
                     final LocalTime time, final String address, final Integer attendeesLimit) {
            this.user = user;
            this.date = date;
            this.description = description;
            this.flyerImageId = flyerImageId;
            this.city = city;
            this.title = title;
            this.time = time;
            this.address = address;
            this.attendeesLimit = attendeesLimit;
            this.deleted = false;

        }
        public Event(final Long id, final User user, final LocalDate date, final String description,
                     final Long flyerImageId, final City city, final String title,
                     final LocalTime time, final String address, final Integer attendeesLimit) {
            this.id = id;
            this.user = user;
            this.date = date;
            this.description = description;
            this.flyerImageId = flyerImageId;
            this.city = city;
            this.title = title;
            this.time = time;
            this.address = address;
            this.attendeesLimit = attendeesLimit;
            this.deleted = false;
        }


        public boolean getFull(){
            return attendeesLimit != null && attendeesLimit <= attendeesCount;
        }

        public boolean getIsFuture() {
            if(time == null) {
                return date.atStartOfDay().isAfter(LocalDateTime.now());
            }

            return LocalDateTime.of(date, time).isAfter(LocalDateTime.now());
        }



        @Override
        public String toString() {
            return "{eventID: " +
                    id +
                    ", user: " +
                    user +
                    ", city: " +
                    city +
                    ", date: \"" +
                    date +
                    "\", time: \"" +
                    (time != null ? time : "all-day") +
                    "\", address: \"" +
                    address +
                    "\", attendeesLimit: " +
                    attendeesLimit +
                    ", description: \"" +
                    description +
                    "\", flyerID: " +
                    flyerImageId +
                    "}";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Event event)) return false;
            return id != null && id.equals(event.id);
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }

    }
