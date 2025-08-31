package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end,
                                                               Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, String status, Sort sort);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 and b.status = ?2")
    List<Booking> findByOwnerAndStatus(Long ownerId, String status);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 and b.start > ?2" +
            "order by b.start")
    List<Booking> findByOwnerAndStartDateIsAfter(Long ownerId, LocalDateTime end);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 and b.end < ?2" +
            "order by b.start")
    List<Booking> findByOwnerAndEndDateBefore(Long ownerId, LocalDateTime end);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1 and b.start < ?2 and b.end > ?3 " +
            "order by b.start")
    List<Booking> findByOwnerAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    @Query("select b from Booking as b " +
            "join b.item as i " +
            "join i.owner as o " +
            "where o.id = ?1")
    List<Booking> findByOwner(Long ownerId);

    List<Booking> findByItem_Id(Long itemId);

    Booking findByBooker_IdAndItem_id(Long bookerId, Long itemId);

    Booking findTop1ByItem_IdAndStartIsBefore(Long itemId, LocalDateTime start);

    Booking findTop1ByItem_IdAndStartIsAfter(Long itemId, LocalDateTime start);
}
