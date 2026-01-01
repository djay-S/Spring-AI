package org.demo.geminigenai.chat.entity;

import java.util.Date;
import java.util.List;

public record Student(
        String firstName,
        String lastName,
        String middleName,
        Date dateOfBirth,
        double age,
        Date dateOfAdmission,
        String gender,
        String classRoom,
        String studentId,
        List<Subject> subjects,
        List<ReportCard> reportCards) {}
