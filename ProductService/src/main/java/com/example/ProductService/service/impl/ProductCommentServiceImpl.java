package com.example.ProductService.service.impl;

import com.example.ProductService.dto.ProductCommentDto;
import com.example.ProductService.mapper.ProductMapper;
import com.example.ProductService.domain.comment.ProductComment;
import com.example.ProductService.repository.ProductCommentRepository;
import com.example.ProductService.service.ProductCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCommentServiceImpl implements ProductCommentService {

    private final ProductCommentRepository commentRepository;
    private final ProductMapper mapper;

    @Override
    public ProductCommentDto addComment(String productSku, String userId, String commentText) {
        ProductComment comment = new ProductComment();
        comment.setProductSku(productSku);
        comment.setUserId(userId);
        comment.setContent(commentText);
        comment.setVote(0);
        commentRepository.save(comment);
        return mapper.toCommentDto(comment);
    }

    @Override
    public List<ProductCommentDto> getComments(String productSku) {
        return commentRepository.findAllByProductSku(productSku)
                .stream()
                .map(mapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void voteComment(String commentId, int voteChange) {
        commentRepository.findById(commentId).ifPresent(c -> {
            c.setVote(c.getVote() + voteChange);
            commentRepository.save(c);
        });
    }
}
