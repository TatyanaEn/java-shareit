package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService{
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto request) {
        User booker = userRepository.findById(request.getRequestor().getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(request.getRequestor().getId()), log));

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(ItemRequestMapper.toItemRequest(request)));
    }

    @Override
    public List<ItemRequestWithAnswerDto> getItemRequestListByUserId(Long userId) {
        return itemRequestRepository.findByRequestor_Id(userId, Sort.by("created").descending())
                .stream().map(ItemRequestMapper::toItemRequestWithAnswerDto)
                .peek(itemRequest -> {
                    itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest.getId()));
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllItemRequestList(Long userId) {
        return itemRequestRepository.findByRequestor_IdNot(userId, Sort.by("created").descending())
                .stream().map(ItemRequestMapper::toItemRequestDto).toList();
    }

    @Override
    public ItemRequestWithAnswerDto getItemRequestById(Long requestId) {
        ItemRequestWithAnswerDto itemRequest = ItemRequestMapper.toItemRequestWithAnswerDto(itemRequestRepository.findById(requestId).get());
        itemRequest.setItems(itemRepository.findAllByRequestId(requestId));
        return itemRequest;
    }
}
