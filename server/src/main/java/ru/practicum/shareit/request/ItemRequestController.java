package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswerDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static ru.practicum.shareit.constans.AppConstants.USER_ID_FIELD;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID_FIELD) Long userId,
                                            @Valid @RequestBody ItemRequestDto itemRequestDto) {
        itemRequestDto.setRequestor(userService.getUserById(userId));
        return itemRequestService.createItemRequest(itemRequestDto);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestWithAnswerDto> getItemRequestListByUserId(@RequestHeader(USER_ID_FIELD) Long userId) {

        return itemRequestService.getItemRequestListByUserId(userId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllItemRequestList(@RequestHeader(USER_ID_FIELD) Long userId) {
        return itemRequestService.getAllItemRequestList(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestWithAnswerDto getItemRequestById(@PathVariable("requestId") Long requestId) {
        return itemRequestService.getItemRequestById(requestId);
    }


}
