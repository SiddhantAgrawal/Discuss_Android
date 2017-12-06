package com.discuss.ui;

import com.discuss.datatypes.Question;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Deepak Thakur
 */
@Data
@Builder
public class QuestionSummary {
    private int questionId;
    private String text;
    private String imageUrl;
    private int likes;
    private int views;
    private int personId;
    private String personName;
    private String difficulty;
    private boolean liked;
    private boolean bookmarked;
    private boolean answered;
}
