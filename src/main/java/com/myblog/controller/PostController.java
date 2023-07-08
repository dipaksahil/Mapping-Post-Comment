package com.myblog.controller;

import com.myblog.payload.PostDto;
import com.myblog.payload.PostResponse;
import com.myblog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    public PostController(PostService postService) {
        this.postService = postService;
    }

    private PostService postService;

    // http://localhost:8080/posts
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDto postDto, BindingResult result){
        if(result.hasErrors()){
            return  new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //System.out.println(postDto.getContent());
        PostDto dto = postService.createPost(postDto);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
    // http://localhost:8080/posts/id
    @GetMapping("/{id}") //get by id
     public ResponseEntity<PostDto> getPostById(@PathVariable(value = "id") long id){
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }
    // http://localhost:8080/posts/id
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, @PathVariable("id") long id){
        return new ResponseEntity<>(postService.updatePost(postDto,id), HttpStatus.OK);
    }
    // http://localhost:8080/posts/id
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePostById(@PathVariable("id") long id){
        postService.deletePostById(id);
        return new ResponseEntity<String>("Post deleted", HttpStatus.OK);
    }

    // http://localhost:8080/posts?pageNo=0&pageSize=10&sortBy=title&sortDir=asc
    @GetMapping
    public PostResponse getAllPosts(
            @RequestParam(value="pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value="pageSize",defaultValue = "5", required = false) int pageSize,
            @RequestParam(value="sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value="sortDir",defaultValue = "asc", required = false) String sortDir
    ){
        return postService.getAllPosts(pageNo,pageSize,sortBy,sortDir);
    }
}