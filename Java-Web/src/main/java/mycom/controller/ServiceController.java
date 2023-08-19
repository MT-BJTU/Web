package mycom.controller;
import mycom.ActionResult.ResponseResult;
import com.aliyuncs.ram.model.v20150501.ChangePasswordRequest;
import mycom.bean.Answer;
import mycom.bean.LikeRecord;
import mycom.bean.Question;
import mycom.bean.User;
import mycom.mapper.AnswerMapper;
import mycom.model.DetailedUser;
import mycom.model.QuestionRequestDto;
import mycom.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private Service service;

    @Autowired
    private AnswerMapper answerMapper;

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
    @PostMapping("/answers/{answerId}/like")
    public ResponseResult<?> likeAnswer(@PathVariable("answerId") Long answerId) {
        try {
            Long userId = getUserId();
            if(userId!=0){
            return service.likeAnswer(answerId);
            }
            else
                return new ResponseResult<>(500, "游客请登陆");
        } catch (Exception e) {
            return ResponseResult.error();
        }
    }

    private Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            return detailedUser.getUserId();
        }
        return null;
    }

    @PostMapping("/upload-image")
    public ResponseResult<?> uploadImage(@RequestParam("file") MultipartFile file) {
     return service.uploadImage(file);
    }
    @PostMapping("/follow-question")
    public ResponseResult<?> follow(@RequestBody Question question) {
        return service.follow(question);
    }

    @GetMapping("/myfollower")
    public ResponseResult<?> myfollow() {
        return service.myfollow();
    }
}
