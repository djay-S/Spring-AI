package org.demo.geminigenai.chat.entity;

import java.util.Map;

public record ReportCard(Map<String, Double> marks, String studentId, String classRoom, String semester) {}
