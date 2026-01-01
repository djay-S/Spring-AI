package org.demo.geminigenai.chat.entity;

import java.util.Map;

public record ReportCard(Map<Subject, Double> marks, String studentId, String classRoom, String semester) {}
