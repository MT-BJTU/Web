package mycom.controller;
import mycom.ActionResult.ResponseResult;
import com.aliyuncs.ram.model.v20150501.ChangePasswordRequest;
import mycom.bean.Answer;
import mycom.bean.User;
import mycom.model.QuestionRequestDto;
import mycom.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private Service service;

    @PostMapping("/login")
    public ResponseResult<?>login(@RequestBody User user){
        return service.login(user);
    }

    @GetMapping("/logout")
    public void logout(){
        service.logout();
    }

    @PostMapping("/register")
    public ResponseResult<?>register(@RequestBody User user){
        return service.register(user);
    }

    @GetMapping("/user/profile")
    public ResponseResult<?> profile() {
        return service.profile();
    }

    @PostMapping("/user/save")
    public ResponseResult<?>save(@RequestBody User user){
        return service.save(user);
    }
    @PostMapping("/user/changePassword")
    public ResponseResult<?>changePassword(@RequestBody ChangePasswordRequest request){
        return service.changePassword(request);
    }

    @GetMapping("/questions")
         ResponseResult<?>showQues(){
        return service.showQues();
    }
    @GetMapping("/myquestions")
    ResponseResult<?>showMyQues(){
        return service.showMyQues();
    }
    @PostMapping("/submit-question")
    public ResponseResult<?> submitQuestion(@RequestBody QuestionRequestDto questionRequest) {
        return service.saveQuestion(questionRequest);
    }
    @GetMapping("/questions/{questionId}")
    public ResponseResult<?> getQuestion(@PathVariable("questionId") Long questionId) {
        return service.getQuestion(questionId);
    }
    @GetMapping("/questions/{questionId}/answers")
    public ResponseResult<?> getAnswersByQuestionId(@PathVariable("questionId") Long questionId) {
        return  service.getAnswersByQuestionId(questionId);
    }
    @PostMapping("questions/{questionId}/postanswers")
    public ResponseResult<?> submitAnswer(@PathVariable("questionId")Long questionId,@RequestBody Answer answer) {
        return  service.submitAnswer(questionId, answer);
    }
    @DeleteMapping("/questions/{questionId}")
    public ResponseResult<?> deleteQuestion(@PathVariable("questionId") Long questionId) {
        return service.deleteQuestion(questionId);
    }
}
