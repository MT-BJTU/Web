package mycom.service;


import mycom.ActionResult.ResponseResult;
import com.aliyuncs.ram.model.v20150501.ChangePasswordRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import mycom.bean.Answer;
import mycom.bean.Question;
import mycom.view.QuestionView;
import mycom.bean.User;
import mycom.bean.LikeRecord;
import mycom.config.OSSConfig;
import mycom.mapper.AnswerMapper;
import mycom.mapper.QuestionMapper;
import mycom.mapper.QuestionViewMapper;
import mycom.mapper.LikeRecordMapper;
import mycom.mapper.UserMapper;
import mycom.model.DetailedUser;
import mycom.model.QuestionRequestDto;
import mycom.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {
    @Autowired
    private UserMapper userMapper;


    @Autowired
    private QuestionViewMapper questionViewMapper;
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private LikeRecordMapper likeRecordMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OSSConfig oss;


    @Override
    public ResponseResult<?> login(User user) {
        // 通过UsernamePasswordAuthenticationToken获取用户名和密码
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUserName(), user.getPassword());
        System.out.println(user.getUserName());
        // AuthenticationManager委托机制对authenticationToken 进行用户认证
        Authentication authenticate = null;
        try {
            authenticate = authenticationManager.authenticate(authenticationToken);
            // 如果认证通过，使用user生成jwt jwt存入ResponseResult 返回

            // 如果认证通过，拿到这个当前登录用户信息
            DetailedUser loginUser = (DetailedUser) authenticate.getPrincipal();
            // 获取当前用户的userid
            String userId = loginUser.getUserId().toString();

            String jwt = JwtUtil.createJWT(userId);
            Map<String, String> map = new HashMap<>();
            map.put("token", jwt);
            return new ResponseResult<>(200, "登录成功", map);
        } catch (AuthenticationException ex) {
            return new ResponseResult<>(400, "用户或密码错误");
        }
    }
    @Override
    public void logout(){
        SecurityContextHolder.clearContext();
    }
    @Override
    public ResponseResult<?> register(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        User now = userMapper.selectOne(queryWrapper);
        if(now==null){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
        Long userId = user.getUserId();
        if (userId != null) {
            return new ResponseResult<>(200, "注册成功");
        } else {
            return new ResponseResult<>(400, "用户名已存在");
        }
        }
        else {
            return new ResponseResult<>(400, "用户名已存在");
        }
    }
    @Override
    public ResponseResult<?> profile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(queryWrapper);
        return  ResponseResult.success(user);
    }

    @Override
    public ResponseResult<?> save(User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if(!username.equals("guest")){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,user.getUserName());
        User old = userMapper.selectOne(queryWrapper);
        if(old==null||old.getUserName().equals(username)){
        queryWrapper.eq(User::getUserName,username);
        User now = userMapper.selectOne(queryWrapper);
        now.setUserName(user.getUserName());
        if(!user.getProfile().equalsIgnoreCase("这个人很懒，什么都没有写~~")){
            now.setProfile(user.getProfile());
        }
        if(!user.getAvatar().equalsIgnoreCase("https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png")){
            String url= user.getAvatar();
            try{
                url=oss.uploadDataUrl(user.getAvatar());
                now.setAvatar(url);
            }catch (IllegalArgumentException e) {
                now.setAvatar(url);
            }
        }
        now.setTrade(user.getTrade());
            int rows = userMapper.updateById(now);
            if (rows > 0) {
                // 更新成功
                return new ResponseResult<>(200, "更新成功");
            } else {
                // 更新失败
                return new ResponseResult<>(500, "更新失败");
            }
        }
        else
            return new ResponseResult<>(500, "用户名已有");
        }
        else
            return new ResponseResult<>(500, "游客请登陆");
    }
    @Override
    public ResponseResult<?> changePassword(ChangePasswordRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if(!username.equals("guest")){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(queryWrapper);
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())){
            return new ResponseResult<>(500, "密码错误");
        }
        else{
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            int rows = userMapper.updateById(user);
            if (rows > 0) {
                // 更新成功
                return new ResponseResult<>(200, "修改成功");
            } else {
                // 更新失败
                return new ResponseResult<>(500, "修改失败");
            }
        }
        }else
            return new ResponseResult<>(500, "游客请登陆");
    }

    @Override
    public ResponseResult<?> showQues(){
        QueryWrapper<QuestionView> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");
        List<QuestionView> questionList=questionViewMapper.selectList(queryWrapper);
        return   ResponseResult.success(questionList);
    }
    @Override
    public ResponseResult<?>showMyQues(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        QueryWrapper<QuestionView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserID", userId);
        List<QuestionView> questionList=questionViewMapper.selectList(queryWrapper);
        Collections.sort(questionList, Comparator.comparing(QuestionView::getTime).reversed());
        return ResponseResult.success(questionList);
    }

    @Override
    public ResponseResult<?> saveQuestion(QuestionRequestDto questionRequest){
        try {
            // 创建 Question 对象并设置属性值
            Question question = new Question();
            question.setTitle(questionRequest.getTitle());
            question.setDescription(questionRequest.getDescription());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
                DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
                userId = detailedUser.getUserId();
            }
            if(userId!=0){
            question.setAnswers(0);
            question.setUserID(userId);
            // 获取当前时间并格式化为字符串
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedTime = currentTime.format(formatter);
            // 设置问题对象的发布时间
            question.setTime(formattedTime);
            // 执行保存操作，例如调用 questionMapper 的插入方法
            questionMapper.insert(question);
            // 返回成功响应
            return new ResponseResult<>(200, "问题提交成功！");}
            else
                return new ResponseResult<>(500, "游客请登陆");
        } catch (Exception e) {
            // 处理异常情况，并返回错误响应
            return new ResponseResult<>(500, "问题提交失败");
        }
    }

    @Override
    public ResponseResult<?> getQuestion(Long questionId) {
        try {
            Question question = questionMapper.selectById(questionId);
            if (question != null) {
                User user = userMapper.selectById(question.getUserID());
                // 将查询到的用户信息设置到问题对象中
                question.setUser(user);
                return ResponseResult.success(question);
            } else {
                return new ResponseResult<>(500, "获取失败");
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败");
        }
    }

    @Override
    public ResponseResult<?>getAnswersByQuestionId(Long questionId){
        try {
            List<Answer> answerList = answerMapper.selectByQueId(questionId);
            for (Answer answer : answerList) {
                // 根据问题中的用户ID查询对应的用户信息
                User user = userMapper.selectById(answer.getUserID());
                // 将查询到的用户信息设置到问题对象中
                answer.setUser(user);
            }
            Collections.sort(answerList, Comparator.comparing(Answer::getReleaseTime).reversed());
            return ResponseResult.success(answerList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败");
        }
    }

    @Override
    public ResponseResult<?>submitAnswer(Long questionId, Answer newAns){
        try {
            Answer answer = new Answer();
            answer.setContent(newAns.getContent());
            answer.setQueID(questionId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
                DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
                userId = detailedUser.getUserId();
            }
            if(userId!=0){
            answer.setUserID(userId);
            answer.setReleaseTime(newAns.getReleaseTime());
            // 执行保存操作，例如调用 questionMapper 的插入方法
            answerMapper.insert(answer);
            // 返回成功响应
            return new ResponseResult<>(200, "回答提交成功！");
            }
            else
                return new ResponseResult<>(500, "游客请登陆");
        } catch (Exception e) {
            // 处理异常情况，并返回错误响应
            return new ResponseResult<>(500, "回答提交失败");
        }
    }
    @Override
    public ResponseResult<?>deleteQuestion(Long questionId){
        try {
            int affectedRows = questionMapper.deleteById(questionId);
            if (affectedRows > 0) {
                return new ResponseResult<>(200, "问题删除成功！");
            } else {
                return new ResponseResult<>(500, "问题删除失败");
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "问题删除失败");
        }
    }
    @Override
    public ResponseResult<?> likeAnswer(Long answerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        // Check if the user has already liked the answer
        LikeRecord existingLike = likeRecordMapper.selectById(answerId);
        System.out.println(existingLike);
        try {
            if (existingLike == null) {
                LikeRecord likeRecord = new LikeRecord(answerId, userId);
                likeRecordMapper.insert(likeRecord);
                return new ResponseResult<>(200, "点赞成功");
            }
            else{
                likeRecordMapper.deleteById(answerId);
                System.out.println(existingLike);
                System.out.println("behind");
                return new ResponseResult<>(500, "取消点赞");
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "操作失败");
        }

    }

    @Override
    public ResponseResult<String> uploadImage(MultipartFile file){
        if (file.isEmpty()) {
            return ResponseResult.error();
        }
        String imageUrl = oss.uploadOneFile(file);
        if (imageUrl != null) {
            return ResponseResult.success(imageUrl);
        } else {
            return ResponseResult.error();
        }
    }
}



