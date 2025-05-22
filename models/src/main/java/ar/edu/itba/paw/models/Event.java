    package ar.edu.itba.paw.models;

    import lombok.Getter;
    import lombok.Setter;

    import javax.persistence.*;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.ArrayList;
    import java.util.List;

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
        private  long flyerImageId;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "city_id")
        @Setter
        private  City city;

        @Column
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

        @Column(name = "attendees_count")
        @Setter
        private  int attendeesCount; //FIXME: yo borraria esto
        
        @Column(name="deleted", nullable = false)
        @Setter
        private  boolean deleted;

        @Column(name="deleted_message")
        @Setter
        private String deletionMessage;




        /* For hibernate */ Event() {
        }
        public Event(final User user, final LocalDate date, final String description,
                     final long flyerImageId, final City city, final String title,
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
            this.attendeesCount = 1; //user that created the event @TODO
            this.deleted = false;

        }
        public Event(final Long id, final User user, final LocalDate date, final String description,
                     final long flyerImageId, final City city, final String title,
                     final LocalTime time, final String address, final Integer attendeesLimit, final int attendeesCount) {
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
            this.attendeesCount = attendeesCount; //user that created the event @TODO
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
            StringBuilder sb = new StringBuilder();
            sb.append("{eventID: ");
            sb.append(id);
            sb.append(", user: ");
            sb.append(user);
            sb.append(", city: ");
            sb.append(city);
            sb.append(", date: \"");
            sb.append(date);
            sb.append("\", time: \"");
            sb.append(time != null ? time : "all-day");
            sb.append("\", address: \"");
            sb.append(address);
            sb.append("\", attendeesLimit: ");
            sb.append(attendeesLimit);
            sb.append(", description: \"");
            sb.append(description);
            sb.append("\", flyerID: ");
            sb.append(flyerImageId);
            sb.append("}");
            return sb.toString();
        }

    }
