    package ar.edu.itba.paw.models;

    import lombok.Getter;
    import lombok.RequiredArgsConstructor;
    import lombok.Setter;
    import lombok.experimental.Accessors;

    import javax.persistence.*;
    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;
    import java.util.List;
    import java.util.Optional;

    @Getter
    @Setter
    @Entity
    @Table(name="events")
    public class Event {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
                "events_id_seq")
        @SequenceGenerator(sequenceName = "events_id_seq", name =
                "events_id_seq", allocationSize = 1)
        @Column(name = "id")
        private  Long id;
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        private  User user;
        @Column(name = "event_date", nullable = false)
        private  LocalDate date;
        @Column(length = 2047)
        private  String description;
        @Column(name = "flyer_image_id")
        private  long flyerImageId;
        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        private  City eventCity;
        @Column
        private  String title;
        @Column(name = "event_time")
        private  LocalTime time;
        @Column(name = "event_address", length = 255)
        private  String address;
        @Column(name = "attendees_limit")
        private  Integer attendeesLimit;
        @Column(name = "attendees_count")
        private  int attendeesCount; //FIXME: yo borraria esto
        @Column(name="deleted", nullable = false)
        private  boolean deleted;

        @Column(name="deletion_message")
        private String deletionMessage;

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "event_attendances",
                joinColumns = @JoinColumn(name = "event_id"),
                inverseJoinColumns = @JoinColumn(name = "user_id"))
        List<User> attendees;

        @OneToMany(fetch = FetchType.LAZY,mappedBy = "event", cascade = CascadeType.ALL)
        List<EventResponse> responses;





        /* For hibernate */ Event() {
        }
        public Event(final User user, final LocalDate date, final String description,
                     final long flyerImageId, final City eventCity, final String title,
                     final LocalTime time, final String address, final Integer attendeesLimit) {
            this.user = user;
            this.date = date;
            this.description = description;
            this.flyerImageId = flyerImageId;
            this.eventCity = eventCity;
            this.title = title;
            this.time = time;
            this.address = address;
            this.attendeesLimit = attendeesLimit;
            this.attendeesCount = 1; //user that created the event @TODO
            this.deleted = false;
            this.attendees = List.of(user); //fixme: REVISAR ESTO CON JPA
            this.responses = List.of();

        }
        public Event(final Long id, final User user, final LocalDate date, final String description,
                     final long flyerImageId, final City eventCity, final String title,
                     final LocalTime time, final String address, final Integer attendeesLimit, final int attendeesCount) {
            this.id = id;
            this.user = user;
            this.date = date;
            this.description = description;
            this.flyerImageId = flyerImageId;
            this.eventCity = eventCity;
            this.title = title;
            this.time = time;
            this.address = address;
            this.attendeesLimit = attendeesLimit;
            this.attendeesCount = attendeesCount; //user that created the event @TODO
            this.deleted = false;
            this.attendees = List.of(user);
            this.responses = List.of();
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
        public boolean hasUserAttending(long userId) {
            return attendees.stream().anyMatch(u -> u.getId() == userId);
        }



        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{eventID: ");
            sb.append(id);
            sb.append(", user: ");
            sb.append(user);
            sb.append(", city: ");
            sb.append(eventCity);
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
