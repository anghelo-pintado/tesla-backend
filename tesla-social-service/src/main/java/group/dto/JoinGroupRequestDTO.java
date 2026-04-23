package group.dto;

import lombok.Data;

@Data
public class JoinGroupRequestDTO {
    private String code;
    private Long studentId;
    private String studentName;
}