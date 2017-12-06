package com.discuss.ui;


import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Deepak Thakur
 */
@Data
@Builder
public class CommentSummary {
    private int commentId;
    private String text;
    private String imageUrl;
    private int likes;
    private int views;
    private int personId;
    private String personName;
    private boolean liked;
}
