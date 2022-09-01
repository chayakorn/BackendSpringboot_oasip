package oasip.us1.backend.repositories;

import oasip.us1.backend.entities.Event;
import oasip.us1.backend.entities.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Integer> {

    @Query(
            value = "select * from event where ((:start between addtime(eventStartTime,'00:01:00') and addtime(eventStartTime,concat('00:',eventDuration-1,':00'))) or ( :end between addtime(eventStartTime,'00:01:00') and addtime(eventStartTime,concat('00:',eventDuration-1,':00')) )or (addtime(eventStartTime,'00:01:00') between :start and :end) or (addtime(eventStartTime,concat('00:',eventDuration-1,':00')) between :start and :end))  and eventCategoryId = :catid",
            nativeQuery = true
    )
    Collection<Event> findByEventStartTimeBetween(@Param("start") String start, @Param("end") String end, @Param("catid") int catid);


    @Query(
            value = "select * from event where ((:start between addtime(eventStartTime,'00:01:00') and addtime(eventStartTime,concat('00:',eventDuration-1,':00'))) or ( :end between addtime(eventStartTime,'00:01:00') and addtime(eventStartTime,concat('00:',eventDuration-1,':00')) )or (addtime(eventStartTime,'00:01:00') between :start and :end) or (addtime(eventStartTime,concat('00:',eventDuration-1,':00')) between :start and :end))  and eventCategoryId = :catid and bookingId != :bid",
            nativeQuery = true
    )
    Collection<Event> findByEventStartTimeBetweenForPut(@Param("start") String start,@Param("end") String end,@Param("catid") int catid,@Param("bid") int bid);


    @Query(
            value = "select * from event where eventCategoryId in :id",
            countQuery = "select count(*) from event where eventCategoryId in :id",
            nativeQuery = true
    )
    Page<Event> findByCategoryId(@Param("id") Collection<String> id, @Param("page") Pageable pageable);

    @Query(
            value = "select * from event where (addtime(eventStartTime, concat(:offSet':00')) between :date and concat(:date,' 23:59:59')) and eventCategoryId = :catid",
            nativeQuery = true
    )
    List<Event> findByEventStartTimeAndEventCategoryIdForPlus(@Param("catid") int catid, @Param("date") String date, @Param("offSet") String offset);

    @Query(
            value = "select * from event where (subtime(eventStartTime, concat(:offSet,':00') between :date and concat(:date,' 23:59:59')) and eventCategoryId = :catid",
            nativeQuery = true
    )
    List<Event> findByEventStartTimeAndEventCategoryIdForMinus(@Param("catid") int catid,@Param("date") String date,@Param("offSet") String offset);

    @Query(
            value = "select * from event where addtime(eventStartTime, concat(:offSet,':00')) between :date and concat(:date,' 23:59:59')",
            countQuery = "select count(*) from event where addtime(eventStartTime, concat(:offSet,':00')) between :date and concat(:date,' 23:59:59')",
            nativeQuery = true)
    Page<Event> findByDateForPlus(@Param("date") String date,@Param("offSet") String offSet, Pageable pageable);


    @Query(
            value = "select * from event where subtimeeventStartTime, concat(:offSet,':00')) between :date and concat(:date,' 23:59:59')",
            countQuery = "select count(*) from event where subtime(eventStartTime, concat(:offSet,':00')) between :date and concat(:date,' 23:59:59')",
            nativeQuery = true)
    Page<Event> findByDateForMinus(@Param("date") String date,@Param("offSet") String offSet,Pageable pageable);

    @Query(
            value = "select * from event where eventCategoryId in :id and eventStartTime < :now",
            countQuery = "select count(*) from event where eventCategoryId in :id and eventStartTime < :now",
            nativeQuery = true
    )
    Page<Event> findByCategoryIdPast(@Param("id") Collection<String> id, @Param("page") Pageable pageable, @Param("now") String now);

    @Query(
            value = "select * from event where eventCategoryId in :id and eventStartTime > :now",
            countQuery = "select count(*) from event where eventCategoryId in :id and eventStartTime > :now",
            nativeQuery = true
    )
    Page<Event> findByCategoryIdUpcomming(@Param("id") Collection<String> id, @Param("page") Pageable pageable, @Param("now") String now);

    List<Event> findEventByEventCategoryId(EventCategory eventcategory);

}