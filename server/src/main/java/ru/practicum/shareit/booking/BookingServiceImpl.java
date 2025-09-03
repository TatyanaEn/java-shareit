package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto request) {
        User booker = userRepository.findById(request.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. "
                        .formatted(request.getBookerId()), log));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с ID '%d' не найдена. "
                        .formatted(request.getItemId()), log));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь id = '%d' не доступна для бронирования.".formatted(item.getId()), log);
        }

        if (item.getOwner().getId().equals(booker.getId())) {
            throw new NotFoundException("Нельзя бронировать свою вещь", log);
        }


        Booking booking = BookingMapper.toBooking(request);
        booking.setBooker(booker);
        booking.setItem(item);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto acceptBooking(Long userId, Long bookingId, Boolean approved) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Пользователь с ID '%d' не найден. "
                        .formatted(userId), log));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID '%d' не найдено. "
                        .formatted(bookingId), log));
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new ValidationException("Пользователь с ID '%d' не является владельцем этой вещи."
                    .formatted(userId), log);
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. ".formatted(userId), log));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с ID '%d' не найдено. "
                        .formatted(bookingId), log));
        if (!(booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId))) {
            throw new ValidationException("Пользователь с ID '%d' не является владельцем вещи или арендатором. "
                    .formatted(userId), log);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingResponseDto> getBookingListByUserId(Long userId, String state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. ".formatted(userId), log));
        return switch (state) {
            case "ALL" -> bookingRepository.findByBooker_Id(userId, Sort.by("start"))
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "FUTURE" -> bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(),
                    Sort.by("start")).stream().map(BookingMapper::toBookingDto).toList();
            case "PAST" -> bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(),
                    Sort.by("start")).stream().map(BookingMapper::toBookingDto).toList();
            case "CURRENT" -> bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                            LocalDateTime.now(), LocalDateTime.now(), Sort.by("start"))
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "WAITING" -> bookingRepository.findByBooker_IdAndStatus(userId, "WAITING",
                            Sort.by("start"))
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "REJECTED" -> bookingRepository.findByBooker_IdAndStatus(userId, "REJECTED",
                            Sort.by("start"))
                    .stream().map(BookingMapper::toBookingDto).toList();
            default -> List.of();
        };


    }

    @Override
    public List<BookingResponseDto> getBookingListByOwnerId(Long ownerId, String state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID '%d' не найден. ".formatted(ownerId), log));
        return switch (state) {
            case "ALL" -> bookingRepository.findByOwner(ownerId)
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "FUTURE" -> bookingRepository.findByOwnerAndStartDateIsAfter(ownerId, LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "PAST" -> bookingRepository.findByOwnerAndEndDateBefore(ownerId, LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "CURRENT" -> bookingRepository.findByOwnerAndStartIsBeforeAndEndIsAfter(ownerId,
                            LocalDateTime.now(), LocalDateTime.now())
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "WAITING" -> bookingRepository.findByOwnerAndStatus(ownerId, "WAITING")
                    .stream().map(BookingMapper::toBookingDto).toList();
            case "REJECTED" -> bookingRepository.findByOwnerAndStatus(ownerId, "REJECTED")
                    .stream().map(BookingMapper::toBookingDto).toList();
            default -> List.of();
        };
    }


}
