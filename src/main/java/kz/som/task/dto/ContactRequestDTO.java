package kz.som.task.dto;

import lombok.Builder;

@Builder
public record ContactRequestDTO(
    String name,
    String phoneNumber,
    String email,
    String address
) {
}
