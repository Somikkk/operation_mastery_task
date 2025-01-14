package kz.som.task.dto;

import lombok.Builder;

@Builder
public record ContactResponseDTO (
        String id,
        String name,
        String phoneNumber,
        String email,
        String address
){
}
