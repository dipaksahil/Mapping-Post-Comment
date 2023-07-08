package com.myblog.service.impl;

import com.myblog.entity.Comment;
import com.myblog.entity.Post;
import com.myblog.exception.BlogAPIException;
import com.myblog.exception.ResourceNotFoundException;
import com.myblog.payload.CommentDto;
import com.myblog.repository.CommentRepository;
import com.myblog.repository.PostRepository;
import com.myblog.service.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
    }

    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private ModelMapper modelMapper;
    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        Comment comment = mapToEntity(commentDto);
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId));
          comment.setPost(post);
        Comment newComment = commentRepository.save(comment);
        return mapToDto(newComment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream().map(comment->mapToDto(comment)).collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", postId)
        );
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", commentId)
        );
         if (!comment.getPost().getId().equals(post.getId())){
             throw new BlogAPIException(HttpStatus.BAD_REQUEST, "comment does not belong to post");
         }
        return mapToDto(comment);
    }

    @Override
    public CommentDto updateComment(long postId, long id, CommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", postId)
        );
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "id", id)
        );
        if (!comment.getPost().getId().equals(post.getId())){
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "comment does not belong to post");
        }
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        Comment updatedComment = commentRepository.save(comment);
        return mapToDto(updatedComment);
    }

    @Override
    public void deleteComment(long postId, long id) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", postId)
        );
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Comment", "Id", id)
        );
        if(!comment.getPost().getId().equals(post.getId()) ){
            throw new BlogAPIException (HttpStatus.BAD_REQUEST, "comment does not belongs to post");
            }
        commentRepository.deleteById(id);
    }

    CommentDto mapToDto(Comment newComment) {
        CommentDto dto = modelMapper.map(newComment, CommentDto.class);
        return dto;
    }

    Comment mapToEntity(CommentDto commentDto) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        return comment;
    }
}