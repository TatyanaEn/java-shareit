package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.UserMapper;

public class CommentMapper {
    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(ItemMapper.toItem(commentDto.getItem()))
                .author(UserMapper.toUser(commentDto.getAuthor()))
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(ItemMapper.toItemWithDatesDto(comment.getItem()))
                .author(UserMapper.toUserDto(comment.getAuthor()))
                .build();
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreateDate())
                .build();
    }
}
