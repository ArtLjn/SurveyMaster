package org.practice.surveymaster.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.practice.surveymaster.dto.SubmitAnswerDTO;
import org.practice.surveymaster.dto.UpdateAnswerDTO;
import org.practice.surveymaster.exception.BusinessException;
import org.practice.surveymaster.model.mongo.Answer;
import org.practice.surveymaster.repository.AnswerRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * <p>
 * AnswerServiceImpl 单元测试类
 * </p>
 *
 * <p>
 * 测试AnswerServiceImpl的各种业务方法，包括提交答案、查询答案、更新答案、删除答案等功能。
 * 使用Mockito进行依赖模拟，确保业务逻辑的正确性。
 * </p>
 *
 * @author ljn
 * @since 2025/9/25 下午3:00
 */
@ExtendWith(MockitoExtension.class)
class AnswerServiceImplTest {

    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private SubmitAnswerDTO submitAnswerDTO;
    private Answer mockAnswer;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        SubmitAnswerDTO.QuestionAnswerDTO questionAnswer1 = new SubmitAnswerDTO.QuestionAnswerDTO(1L, "选项A");
        SubmitAnswerDTO.QuestionAnswerDTO questionAnswer2 = new SubmitAnswerDTO.QuestionAnswerDTO(2L, Arrays.asList("选项B", "选项C"));
        
        submitAnswerDTO = new SubmitAnswerDTO();
        submitAnswerDTO.setSurveyId(1L);
        submitAnswerDTO.setUserId(1L);
        submitAnswerDTO.setAnswers(Arrays.asList(questionAnswer1, questionAnswer2));

        mockAnswer = new Answer();
        mockAnswer.setId("60f1b2a3c4d5e6f7g8h9i0j1");
        mockAnswer.setSurveyId(1L);
        mockAnswer.setUserId(1L);
        mockAnswer.setSubmittedAt(LocalDateTime.now());
        
        Answer.QuestionAnswer qa1 = new Answer.QuestionAnswer(1L, "选项A");
        Answer.QuestionAnswer qa2 = new Answer.QuestionAnswer(2L, Arrays.asList("选项B", "选项C"));
        mockAnswer.setAnswers(Arrays.asList(qa1, qa2));
    }

    @Test
    void testSubmitAnswer_Success() {
        // Given
        when(answerRepository.existsBySurveyIdAndUserId(1L, 1L)).thenReturn(false);
        when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);

        // When
        Answer result = answerService.submitAnswer(submitAnswerDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getSurveyId());
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getAnswers().size());
        
        verify(answerRepository).existsBySurveyIdAndUserId(1L, 1L);
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void testSubmitAnswer_UserAlreadyAnswered() {
        // Given
        when(answerRepository.existsBySurveyIdAndUserId(1L, 1L)).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> answerService.submitAnswer(submitAnswerDTO));
        
        assertEquals("用户已经回答过该问卷，不能重复提交", exception.getMessage());
        verify(answerRepository).existsBySurveyIdAndUserId(1L, 1L);
        verify(answerRepository, never()).save(any());
    }

    @Test
    void testGetAnswerById_Success() {
        // Given
        String answerId = "60f1b2a3c4d5e6f7g8h9i0j1";
        when(answerRepository.findById(answerId)).thenReturn(Optional.of(mockAnswer));

        // When
        Optional<Answer> result = answerService.getAnswerById(answerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockAnswer.getId(), result.get().getId());
        verify(answerRepository).findById(answerId);
    }

    @Test
    void testGetAnswerById_NotFound() {
        // Given
        String answerId = "nonexistent";
        when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

        // When
        Optional<Answer> result = answerService.getAnswerById(answerId);

        // Then
        assertFalse(result.isPresent());
        verify(answerRepository).findById(answerId);
    }

    @Test
    void testGetAnswerBySurveyIdAndUserId_Success() {
        // Given
        when(answerRepository.findBySurveyIdAndUserId(1L, 1L)).thenReturn(Optional.of(mockAnswer));

        // When
        Optional<Answer> result = answerService.getAnswerBySurveyIdAndUserId(1L, 1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(mockAnswer, result.get());
        verify(answerRepository).findBySurveyIdAndUserId(1L, 1L);
    }

    @Test
    void testGetAnswersBySurveyId() {
        // Given
        List<Answer> mockAnswers = Arrays.asList(mockAnswer);
        when(answerRepository.findBySurveyId(1L)).thenReturn(mockAnswers);

        // When
        List<Answer> result = answerService.getAnswersBySurveyId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(mockAnswer, result.get(0));
        verify(answerRepository).findBySurveyId(1L);
    }

    @Test
    void testUpdateAnswer_Success() {
        // Given
        UpdateAnswerDTO updateDTO = new UpdateAnswerDTO();
        updateDTO.setId("60f1b2a3c4d5e6f7g8h9i0j1");
        
        UpdateAnswerDTO.QuestionAnswerDTO updatedQA = new UpdateAnswerDTO.QuestionAnswerDTO(1L, "选项D");
        updateDTO.setAnswers(Arrays.asList(updatedQA));

        when(answerRepository.findById("60f1b2a3c4d5e6f7g8h9i0j1")).thenReturn(Optional.of(mockAnswer));
        when(answerRepository.save(any(Answer.class))).thenReturn(mockAnswer);

        // When
        Answer result = answerService.updateAnswer(updateDTO);

        // Then
        assertNotNull(result);
        verify(answerRepository).findById("60f1b2a3c4d5e6f7g8h9i0j1");
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void testUpdateAnswer_NotFound() {
        // Given
        UpdateAnswerDTO updateDTO = new UpdateAnswerDTO();
        updateDTO.setId("nonexistent");
        updateDTO.setAnswers(Arrays.asList());

        when(answerRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, 
            () -> answerService.updateAnswer(updateDTO));
        
        assertEquals("答案记录不存在", exception.getMessage());
        verify(answerRepository).findById("nonexistent");
        verify(answerRepository, never()).save(any());
    }

    @Test
    void testDeleteAnswer_Success() {
        // Given
        String answerId = "60f1b2a3c4d5e6f7g8h9i0j1";
        when(answerRepository.existsById(answerId)).thenReturn(true);

        // When
        boolean result = answerService.deleteAnswer(answerId);

        // Then
        assertTrue(result);
        verify(answerRepository).existsById(answerId);
        verify(answerRepository).deleteById(answerId);
    }

    @Test
    void testDeleteAnswer_NotFound() {
        // Given
        String answerId = "nonexistent";
        when(answerRepository.existsById(answerId)).thenReturn(false);

        // When
        boolean result = answerService.deleteAnswer(answerId);

        // Then
        assertFalse(result);
        verify(answerRepository).existsById(answerId);
        verify(answerRepository, never()).deleteById(answerId);
    }

    @Test
    void testDeleteAnswersBySurveyId() {
        // Given
        when(answerRepository.deleteBySurveyId(1L)).thenReturn(3L);

        // When
        long result = answerService.deleteAnswersBySurveyId(1L);

        // Then
        assertEquals(3L, result);
        verify(answerRepository).deleteBySurveyId(1L);
    }

    @Test
    void testCountAnswersBySurveyId() {
        // Given
        when(answerRepository.countBySurveyId(1L)).thenReturn(5L);

        // When
        long result = answerService.countAnswersBySurveyId(1L);

        // Then
        assertEquals(5L, result);
        verify(answerRepository).countBySurveyId(1L);
    }

    @Test
    void testHasUserAnswered() {
        // Given
        when(answerRepository.existsBySurveyIdAndUserId(1L, 1L)).thenReturn(true);

        // When
        boolean result = answerService.hasUserAnswered(1L, 1L);

        // Then
        assertTrue(result);
        verify(answerRepository).existsBySurveyIdAndUserId(1L, 1L);
    }
}