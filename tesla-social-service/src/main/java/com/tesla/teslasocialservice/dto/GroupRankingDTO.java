package com.tesla.teslasocialservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupRankingDTO {
    private Integer position;
    private Long studentId;
    private String studentName; // Obtendremos esto desde el servicio de usuario más adelante
    private Integer groupExp;
}