package com.service;

import com.ActionResult.ResponseResult;
import com.aliyuncs.ram.model.v20150501.ChangePasswordRequest;
import com.bean.Answer;
import com.bean.User;
import com.model.QuestionRequestDto;

public interface Service {
     ResponseResult<?> login(User user);
     void logout();
     ResponseResult<?> register(User user);

     ResponseResult<?> profile();

    ResponseResult<?> save(User user);

    ResponseResult<?> changePassword(ChangePasswordRequest request);
    ResponseResult<?> showQues();
    ResponseResult<?>showMyQues();
    ResponseResult<?> saveQuestion(QuestionRequestDto questionRequest);

    ResponseResult<?> getQuestion(Long questionId);
    ResponseResult<?>getAnswersByQuestionId(Long questionId);
    ResponseResult<?>submitAnswer(Long questionId, Answer answer);
    ResponseResult<?>deleteQuestion(Long questionId);
}
