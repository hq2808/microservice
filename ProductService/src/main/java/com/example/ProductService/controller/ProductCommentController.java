package com.example.ProductService.controller;

import com.example.ProductService.dto.ProductCommentDto;
import com.example.ProductService.service.ProductCommentService;
import com.example.common_security.security.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{productSku}/comments")
@RequiredArgsConstructor
public class ProductCommentController {

    private final ProductCommentService commentService;

    @PostMapping
    public ResponseEntity<ProductCommentDto> addComment(
            @PathVariable String productSku,
            @RequestBody Map<String, String> body
    ) {
        String commentText = body.get("comment");
        String userId = SecurityContextUtils.getUserId();
        return ResponseEntity.ok(commentService.addComment(productSku, userId, commentText));
    }

    @GetMapping
    public ResponseEntity<List<ProductCommentDto>> getComments(@PathVariable String productSku) {
        return ResponseEntity.ok(commentService.getComments(productSku));
    }

    @PostMapping("/{commentId}/vote")
    public ResponseEntity<Void> voteComment(@PathVariable String commentId, @RequestParam int vote) {
        commentService.voteComment(commentId, vote);
        return ResponseEntity.ok().build();
    }
}

