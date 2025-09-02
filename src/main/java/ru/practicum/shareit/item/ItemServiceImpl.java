package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(ItemDto request) {
        userRepository.findById(request.getOwner().getId());
        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.toItem(request)));
    }

    @Override
    public ItemDto updateItem(Long userId, ItemDto request) {
        Item item = itemRepository.findById(request.getId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID '%d' не найдена с ID: ".formatted(request.getId()), log));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Редактировать вещь может только её владелец", log);
        }

        return ItemMapper.toItemDto(itemRepository.save(ItemMapper.updateItemFields(item, request)));
    }

    @Override
    public ItemWithBookingDto getItemById(Long userId, Long itemId) {
        ItemWithBookingDto itemDto = itemRepository.findById(itemId)
                .map(ItemMapper::toItemWithDatesDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id = '%d' не найдена".formatted(itemId), log));
        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId())
                .stream().map(CommentMapper::toCommentDto).toList());
        if (Objects.equals(userId, itemDto.getOwner().getId())) {
            Booking lastBooking = bookingRepository.findTop1ByItem_IdAndStartIsBefore(itemDto.getId(), LocalDateTime.now());
            itemDto.setLastBooking(lastBooking);
            Booking nextBooking = bookingRepository.findTop1ByItem_IdAndStartIsAfter(itemDto.getId(), LocalDateTime.now());
            itemDto.setNextBooking(nextBooking);
        }
        return itemDto;
    }

    @Override
    public List<ItemWithBookingDto> findAllItemsByOwner(Long ownerId) {

        return itemRepository.findAllByOwnerId(ownerId)
                .stream().map(ItemMapper::toItemWithDatesDto)
                .peek(item -> {
                    Booking lastBooking = bookingRepository.findTop1ByItem_IdAndStartIsBefore(item.getId(), LocalDateTime.now());
                    item.setLastBooking(lastBooking);
                    Booking nextBooking = bookingRepository.findTop1ByItem_IdAndStartIsAfter(item.getId(), LocalDateTime.now());
                    item.setNextBooking(nextBooking);
                })
                .toList();
    }

    @Override
    public List<ItemDto> findItems(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.search(text)
                    .stream().map(ItemMapper::toItemDto).toList();
        }
    }

    @Override
    public CommentResponseDto createComment(CommentDto request) {
        Booking booking = bookingRepository.findByBooker_IdAndItem_id(request.getAuthor().getId(),
                request.getItem().getId());
        if (booking == null) {
            throw new ValidationException("Пользователь с ID '%d' не является арендатором вещи. "
                    .formatted(request.getAuthor().getId()), log);
        }
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Пользователь еще не завершил аренду вещи. ", log);
        }
        return CommentMapper.toCommentResponseDto(commentRepository.save(CommentMapper.toComment(request)));
    }
}
