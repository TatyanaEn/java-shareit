package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static ru.practicum.shareit.constans.AppConstants.USER_ID_FIELD;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(USER_ID_FIELD) Long userId,
                              @Valid @RequestBody ItemDto itemRequest) {
        itemRequest.setOwner(userService.getUserById(userId));
        return itemService.createItem(itemRequest);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestHeader(USER_ID_FIELD) Long userId,
                              @PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemRequest) {
        itemRequest.setId(itemId);
        return itemService.updateItem(userId, itemRequest);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto findById(@RequestHeader(USER_ID_FIELD) Long userId,
                                       @PathVariable("itemId") Long itemId) {
        return itemService.getItemById(userId, itemId);
    }


    @GetMapping
    public List<ItemWithBookingDto> findAllItemsByOwner(@RequestHeader(USER_ID_FIELD) Long userId) {
        return itemService.findAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    List<ItemDto> findItems(@RequestParam(name = "text", defaultValue = "") String text) {
        return itemService.findItems(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(@RequestHeader(USER_ID_FIELD) Long userId,
                                            @PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody CommentDto commentRequest) {
        commentRequest.setItem(itemService.getItemById(userId, itemId));
        commentRequest.setAuthor(userService.getUserById(userId));
        return itemService.createComment(commentRequest);
    }


}
