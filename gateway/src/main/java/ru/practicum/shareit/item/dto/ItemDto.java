package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;

    @NotBlank(message = "не должно быть пустым")
    private String name;

    @NotBlank(message = "не должно быть пустым")
    private String description;

    @NotNull(message = "не должно быть null")
    private Boolean available;

    private UserDto owner;

    private Long requestId;
}