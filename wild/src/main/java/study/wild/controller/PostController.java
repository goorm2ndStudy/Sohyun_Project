package study.wild.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import study.wild.dto.PostDto;
import study.wild.service.PostCommentService;
import study.wild.service.PostService;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final PostCommentService postCommentService;

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto save(@RequestBody PostDto postDto) {
        return postService.createPost(postDto);
    }

    @PutMapping("/posts/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public PostDto updatePost(@PathVariable Long postId, @RequestBody PostDto postDto) {
        return postService.editPost(postId, postDto);
    }

    @GetMapping("/posts")
    public List<PostDto> getAllPost() {
        return postService.viewPosts(false);
    }

    @GetMapping("/categories/{categoryId}/posts")
    public List<PostDto> getPostByCategory(@PathVariable Long categoryId) {
        return postService.viewPostsByCategory(categoryId, false);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@PathVariable Long postId) {
        return postService.viewPostDetail(postId, false);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable Long postId) {
        postCommentService.deletePost(postId);
    }
}
