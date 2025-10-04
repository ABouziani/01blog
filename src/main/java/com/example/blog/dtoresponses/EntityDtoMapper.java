package com.example.blog.dtoresponses;

import com.example.blog.model.Post;
import com.example.blog.model.User;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoMapper {

    // Convert User → UserDto (with posts)
    public static UserDto toUserDto(User user) {
        if (user == null) return null;

        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setPhoto(user.getPhoto());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setRole(user.getRole());

        // Map posts (without including creator's posts again to avoid deep nesting)
        if (user.getPosts() != null) {
            dto.setPosts(user.getPosts().stream()
                .map(EntityDtoMapper::toPostDtoShallow) // Use shallow version
                .collect(Collectors.toList()));
        }

        return dto;
    }

    // Convert Post → PostDto (with creator, but creator WITHOUT posts)
    public static PostDto toPostDto(Post post) {
        if (post == null) return null;

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setMedia(post.getMedia());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getCreator() != null) {
            // Creator without posts (shallow user)
            UserDto creatorDto = new UserDto();
            creatorDto.setId(post.getCreator().getId());
            creatorDto.setUsername(post.getCreator().getUsername());
            creatorDto.setEmail(post.getCreator().getEmail());
            creatorDto.setBio(post.getCreator().getBio());
            creatorDto.setPhoto(post.getCreator().getPhoto());
            creatorDto.setCreatedAt(post.getCreator().getCreatedAt());
            creatorDto.setRole(post.getCreator().getRole());
            dto.setCreator(creatorDto);
        }

        return dto;
    }

    // Helper: PostDto without nested creator-posts (used when inside UserDto)
    private static PostDto toPostDtoShallow(Post post) {
        if (post == null) return null;

        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setMedia(post.getMedia());
        dto.setCreatedAt(post.getCreatedAt());
        // Note: We intentionally DO NOT set creator here to avoid deep nesting
        // Or you can set a minimal creator if needed (but be cautious)
        return dto;
    }
}