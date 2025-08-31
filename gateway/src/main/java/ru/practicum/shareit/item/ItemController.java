package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    public final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody ItemDto itemRequest) {
        log.info("Creating item {}, userId={}", itemRequest, userId);
        return itemClient.createItem(userId, itemRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("itemId") Long itemId,
                              @RequestBody ItemDto itemRequest) {
        log.info("Update item {}, userId={}", itemRequest, userId);
        return itemClient.updateItem(userId, itemId, itemRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("itemId") Long itemId) {
        log.info("Get item with id {}, userId={}", itemId, userId);
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items by owner_id {}", userId);
        return itemClient.findAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object>  findItems(@RequestParam(name = "text", defaultValue = "") String text) {
        log.info("Get items by text {}", text);
        return itemClient.findItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody CommentDto commentRequest) {
        log.info("create comment {} for item_id = {} by user_id = {}", commentRequest, itemId, userId);
        return itemClient.createComment(userId, itemId, commentRequest);
    }


}
