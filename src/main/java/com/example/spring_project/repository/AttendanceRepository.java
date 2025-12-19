package com.example.spring_project.repository;

import com.example.spring_project.entity.Attendance;
import com.example.spring_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    Optional<Attendance> findByUserAndAttendanceDate(User user, LocalDate attendanceDate);
    List<Attendance> findByUserOrderByAttendanceDateDesc(User user);
    boolean existsByUserAndAttendanceDate(User user, LocalDate attendanceDate);
}
