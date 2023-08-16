package mycom.service;

import mycom.ActionResult.ResponseResult;
import com.aliyuncs.ram.model.v20150501.ChangePasswordRequest;
import mycom.bean.Answer;
import mycom.bean.User;
import mycom.model.QuestionRequestDto;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    void likeAnswer(Long answerId, Long userId);   // Modified to accept userId
    void unlikeAnswer(Long answerId, Long userId); // Modified to accept userId

     ResponseResult<String> uploadImage(MultipartFile file);
}
