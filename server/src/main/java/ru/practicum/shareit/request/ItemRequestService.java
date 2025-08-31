package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto request);

    List<ItemRequestWithAnswerDto> getItemRequestListByUserId(Long userId);

    List<ItemRequestDto> getAllItemRequestList(Long userId);

    ItemRequestWithAnswerDto getItemRequestById(Long requestId);
}
