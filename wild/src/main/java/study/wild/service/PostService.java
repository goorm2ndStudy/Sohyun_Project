package study.wild.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import study.wild.domain.Post;
import study.wild.dto.CategoryDto;
import study.wild.dto.PostDto;
import study.wild.repository.CategoryRepository;
import study.wild.repository.PostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    @Autowired
    private EntityManager em;

    /**
     * 게시글 등록
     */
    @Transactional
    public PostDto createPost(PostDto postDto) {
        CategoryDto categoryDto = categoryService.findByPost(postDto);

        Post savedPost = postRepository.save(postDto.toEntity(categoryDto.toEntity()));
        return PostDto.from(savedPost);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public PostDto updatePost(Long postId, PostDto postDto) {
        return postRepository.findById(postId)
                .map(post -> {
                    post.setTitle(postDto.title());
                    post.setContent(postDto.content());
                    return PostDto.from(postRepository.save(post));
                })
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    /**
     * 전체 게시글 조회 (삭제 여부 조건에 필터링한 게시글)
     *
     * @param isDeleted 게시글 삭제 여부
     */
    public List<PostDto> findPosts(boolean isDeleted) {
        List<PostDto> findPosts = postRepository.findAllByAndDeleted(isDeleted)
                .stream()
                .map(PostDto::from)
                .collect(Collectors.toList());
        return findPosts;
    }


    /**
     * 특정 게시글 조회 (삭제 여부 조건에 필터링한 게시글)
     *
     * @param isDeleted 게시글 삭제 여부
     */
    public PostDto findPost(Long postId, boolean isDeleted) {
        PostDto findPost = postRepository.findPostByIdAndIsDeleted(postId, isDeleted)
                .map(PostDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        return findPost;
    }

    /**
     * 특정 카테고리 내 게시물 조회
     */
    public List<PostDto> viewPostsByCategory(Long categoryId, boolean isDeleted) {
        return postRepository.findPostByCategoryIdAndDeleted(categoryId, isDeleted)
                .stream()
                .map(PostDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 삭제 (soft delete)
     */
    @Transactional
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }
}
