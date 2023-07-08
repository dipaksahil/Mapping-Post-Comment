package com.myblog.controller;
import com.myblog.payload.CommentDto;
import com.myblog.payload.PostDto;
import com.myblog.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    // http://localhost:8080/api/posts/1/comments
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable(value = "postId") long postId,
                                                    @RequestBody CommentDto commentDto){
        return new ResponseEntity<>(commentService.createComment(postId, commentDto), HttpStatus.CREATED);
    }

    // http://localhost:8080/api/posts/1/comments
    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getcommentsByPostId(@PathVariable("postId") long postId){
       return commentService.getCommentsByPostId(postId);
    }
    // http://localhost:8080/api/posts/1/comment/1
    @GetMapping("/posts/{postId}/comment/{id}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable("postId") Long postID,
            @PathVariable("id") Long commentId
    ){
        CommentDto commentDto = commentService.getCommentById(postID, commentId);
        return new ResponseEntity<>(commentDto,HttpStatus.OK);
    }
    //update comment controller
    // http://localhost:8080/api/posts/{postid}/comment/{id}
    @PutMapping("/posts/{postId}/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("postId") long postId,@PathVariable("id") long id,
                                                    @RequestBody CommentDto commentDto) {
        CommentDto dto = commentService.updateComment(postId, id, commentDto);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
    // develop delete comment
    // http://localhost:8080/api/posts/{postid}/comment/{id}
    @DeleteMapping("/posts/{postId}/comment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable("postId") long postId,@PathVariable("id") long id){
        commentService.deleteComment(postId,id);
        return new ResponseEntity<String>("Comment deleted successfully", HttpStatus.OK);
    }

}