package com.myblog.service.impl;

import com.myblog.entity.Post;
import com.myblog.exception.ResourceNotFoundException;
import com.myblog.payload.PostDto;
import com.myblog.payload.PostResponse;
import com.myblog.repository.PostRepository;
import com.myblog.service.PostService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
private PostRepository postRepository;
private ModelMapper modelmapper;

    public PostServiceImpl(PostRepository postRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.modelmapper = modelMapper;
    }

    @Override
    public PostDto createPost(PostDto postDto) {
        Post post = mapToEntity(postDto);
        Post newPost = postRepository.save(post);

        PostDto dto = mapToDto(newPost);
        return dto;
    }

    @Override
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        // How do we create sort object based on direction
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

// or we can write code like this
// Sort sort = null;   //if(sortDir.equalsIgnoreCase("asc")){//    sort=Sort.by(sortBy).ascending();//}else{
//    sort=Sort.by(sortBy).descending();//}

        PageRequest pageable = PageRequest.of(pageNo,pageSize,sort);
        Page<Post> content = postRepository.findAll(pageable); // convert Page to List
        List<Post> posts = content.getContent();
        List<PostDto> dto = posts.stream().map(post -> mapToDto(post)).collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();
        postResponse.setContent(dto);
        postResponse.setPageNo(content.getNumber());
        postResponse.setPageSize(content.getSize());
        postResponse.setTotalElements(content.getTotalElements());
        postResponse.setTotalPages(content.getTotalPages());
        postResponse.setLast(content.isLast());
        return postResponse;
    }

    @Override
    public PostDto getPostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id)
        );
        return mapToDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto, long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id)
        );
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setDescription(postDto.getDescription());
        Post updatedPost = postRepository.save(post);
        return mapToDto(updatedPost);
    }

    @Override
    public void deletePostById(long id) {
        Post post = postRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Post", "Id", id)
        );
        postRepository.deleteById(id);
    }

    PostDto mapToDto(Post post) {
        PostDto postDto = modelmapper.map(post, PostDto.class);

        return postDto;
    }
    Post mapToEntity(PostDto postDto) {
        Post post = modelmapper.map(postDto, Post.class);
        return post;
    }
}