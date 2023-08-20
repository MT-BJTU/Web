package mycom.service;


import mycom.ActionResult.ResponseResult;
import com.aliyuncs.ram.model.v20150501.ChangePasswordRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import mycom.bean.*;
import mycom.mapper.*;
import mycom.view.QuestionView;
import mycom.config.OSSConfig;
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
    public static final Question QUESTION = new Question();
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StarsMapper starsMapper;
    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private FollowersMapper followersMapper;
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
    public void logout() {
        SecurityContextHolder.clearContext();
    }

    @Override
    public ResponseResult<?> register(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, user.getUserName());
        User now = userMapper.selectOne(queryWrapper);
        if (now == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userMapper.insert(user);
            Long userId = user.getUserId();
            if (userId != null) {
                return new ResponseResult<>(200, "注册成功");
            } else {
                return new ResponseResult<>(400, "用户名已存在");
            }
        } else {
            return new ResponseResult<>(400, "用户名已存在");
        }
    }

    @Override
    public ResponseResult<?> profile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, username);
        User user = userMapper.selectOne(queryWrapper);
        return ResponseResult.success(user);
    }

    @Override
    public ResponseResult<?> save(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!username.equals("guest")) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserName, user.getUserName());
            User old = userMapper.selectOne(queryWrapper);
            if (old == null || old.getUserName().equals(username)) {
                queryWrapper.eq(User::getUserName, username);
                User now = userMapper.selectOne(queryWrapper);
                now.setUserName(user.getUserName());
                if (!user.getProfile().equalsIgnoreCase("这个人很懒，什么都没有写~~")) {
                    now.setProfile(user.getProfile());
                }
                if (!user.getAvatar().equalsIgnoreCase("https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png")) {
                    String url = user.getAvatar();
                    try {
                        url = oss.uploadDataUrl(user.getAvatar());
                        now.setAvatar(url);
                    } catch (IllegalArgumentException e) {
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
            } else
                return new ResponseResult<>(500, "用户名已有");
        } else
            return new ResponseResult<>(500, "游客请登陆");
    }

    @Override
    public ResponseResult<?> changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        if (!username.equals("guest")) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserName, username);
            User user = userMapper.selectOne(queryWrapper);
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                return new ResponseResult<>(500, "密码错误");
            } else {
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
        } else
            return new ResponseResult<>(500, "游客请登陆");
    }

    @Override
    public ResponseResult<?> showQues() {
        QueryWrapper<QuestionView> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("time");
        List<QuestionView> questionList = questionViewMapper.selectList(queryWrapper);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        for (int i = 0; i < questionList.size(); i++) {
            Followers exist = followersMapper.selectByUserIdAndQuestionId(userId, questionList.get(i).getQuestionId());
            if (exist != null)
                questionList.get(i).setFollower(true);
        }
        return ResponseResult.success(questionList);
    }

    @Override
    public ResponseResult<?> showMyQues() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        QueryWrapper<QuestionView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserID", userId);
        List<QuestionView> questionList = questionViewMapper.selectList(queryWrapper);
        Collections.sort(questionList, Comparator.comparing(QuestionView::getTime).reversed());
        for (int i = 0; i < questionList.size(); i++) {
            Followers exist = followersMapper.selectByUserIdAndQuestionId(userId, questionList.get(i).getQuestionId());
            if (exist != null)
                questionList.get(i).setFollower(true);
        }
        return ResponseResult.success(questionList);
    }

    @Override
    public ResponseResult<?> saveQuestion(QuestionRequestDto questionRequest) {
        try {
            Question question = new Question();
            question.setTitle(questionRequest.getTitle());
            question.setDescription(questionRequest.getDescription());
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
                DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
                userId = detailedUser.getUserId();
            }
            if (userId != 0) {
                question.setAnswers(0);
                question.setUserID(userId);
                LocalDateTime currentTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = currentTime.format(formatter);
                question.setTime(formattedTime);
                questionMapper.insert(question);
                return new ResponseResult<>(200, "问题提交成功！");
            } else
                return new ResponseResult<>(500, "游客请登陆");
        } catch (Exception e) {
            return new ResponseResult<>(500, "问题提交失败");
        }
    }

    @Override
    public ResponseResult<?> getQuestion(Long questionId) {
        try {
            Question question = questionMapper.selectById(questionId);
            if (question != null) {
                User user = userMapper.selectById(question.getUserID());
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
    public ResponseResult<?> getAnswersByQuestionId(Long questionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        try {
            List<Answer> answerList = answerMapper.selectByQueId(questionId);
            for (Answer answer : answerList) {
                User user = userMapper.selectById(answer.getUserID());
                answer.setUser(user);
                LikeRecord existingLike = likeRecordMapper.selectByUserIdAndAnswerId(userId, answer.getAnswerID());
                if (existingLike != null) {
                    answer.setLiked(true);
                }
            }
            Collections.sort(answerList, Comparator.comparing(Answer::getReleaseTime).reversed());
            return ResponseResult.success(answerList);
        } catch (Exception e) {
            return new ResponseResult<>(500, "获取失败");
        }
    }

    @Override
    public ResponseResult<?> submitAnswer(Long questionId, Answer newAns) {
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
            if (userId != 0) {
                answer.setUserID(userId);
                answer.setReleaseTime(newAns.getReleaseTime());
                answerMapper.insert(answer);
                return new ResponseResult<>(200, "回答提交成功！");
            } else
                return new ResponseResult<>(500, "游客请登陆");
        } catch (Exception e) {
            return new ResponseResult<>(500, "回答提交失败");
        }
    }

    @Override
    public ResponseResult<?> deleteQuestion(Long questionId) {
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
        LikeRecord existingLike = likeRecordMapper.selectByUserIdAndAnswerId(userId, answerId);
        try {
            if (existingLike == null) {
                likeRecordMapper.insert(userId, answerId);
                return new ResponseResult<>(200, "点赞成功");
            } else {
                likeRecordMapper.delete(userId, answerId);
                return new ResponseResult<>(201, "取消点赞");
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "操作失败");
        }
    }

    @Override
    public ResponseResult<String> uploadImage(MultipartFile file) {
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

    @Override
    public ResponseResult<?> follow(Question question) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        if (userId != 0) {
            try {
                Followers exist = followersMapper.selectByUserIdAndQuestionId(userId, question.getQuestionId());
                if (exist == null) {
                    followersMapper.insert(userId, question.getQuestionId());
                    return new ResponseResult<>(200, "关注成功");
                } else {
                    followersMapper.delete(userId, question.getQuestionId());
                    return new ResponseResult<>(201, "取消关注成功");
                }
            } catch (Exception e) {
                return new ResponseResult<>(500, "操作失败");
            }
        } else
            return new ResponseResult<>(500, "游客请登陆");
    }

    @Override
    public ResponseResult<?> myfollow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        QueryWrapper<Followers> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserID", userId);
        List<Followers> queIdList = followersMapper.selectList(queryWrapper);
        List<QuestionView> questionList = new ArrayList<>();
        for (int i = 0; i < queIdList.size(); i++) {
            Long questionId = queIdList.get(i).getQueId();
            QuestionView questionView = questionViewMapper.selectById(questionId);
            if (questionView != null) {
                questionView.setFollower(true);
                questionList.add(questionView);
            }
        }
        Collections.sort(questionList, Comparator.comparing(QuestionView::getTime).reversed());
        return ResponseResult.success(questionList);
    }

    @Override
    public ResponseResult<?> showArticles() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("ReleaseTime");
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        for (int i = 0; i < articleList.size(); i++) {
            Stars exist = starsMapper.selectByUserIdAndEssayId(userId, articleList.get(i).getEssayID());
            articleList.get(i).setUser(userMapper.selectById(articleList.get(i).getUserID()));
            if (exist != null)
                articleList.get(i).setStared(true);
        }
        return ResponseResult.success(articleList);
    }

    @Override
    public ResponseResult<?> saveArticle(Article article) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long userId = null;
            if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
                DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
                userId = detailedUser.getUserId();
            }
            if (userId != 0) {
                article.setUserID(userId);
                LocalDateTime currentTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedTime = currentTime.format(formatter);
                article.setReleaseTime(formattedTime);
                articleMapper.insert(article);
                return new ResponseResult<>(200, "问题提交成功！");
            } else
                return new ResponseResult<>(500, "游客请登陆");
        } catch (Exception e) {
            return new ResponseResult<>(500, "问题提交失败");
        }
    }
    @Override
    public ResponseResult<?> star(Article article){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        if (userId != 0) {
            try {
                Stars exist = starsMapper.selectByUserIdAndEssayId(userId, article.getEssayID());
                if (exist == null) {
                    starsMapper.insert(userId, article.getEssayID());
                    return new ResponseResult<>(200, "收藏成功");
                } else {
                    starsMapper.delete(userId,article.getEssayID());
                    return new ResponseResult<>(201, "取消收藏成功");
                }
            } catch (Exception e) {
                return new ResponseResult<>(500, "操作失败");
            }
        } else
            return new ResponseResult<>(500, "游客请登陆");
    }
    @Override
    public  ResponseResult<?> showMyEssay() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserID", userId);
        List<Article> articlesList = articleMapper.selectList(queryWrapper);
        Collections.sort(articlesList, Comparator.comparing(Article::getReleaseTime).reversed());
        for (Article article : articlesList) {
            Stars exist = starsMapper.selectByUserIdAndEssayId(userId, article.getEssayID());
            if (exist != null)
                article.setStared(true);
           article.setUser(userMapper.selectById(article.getUserID()));
        }
        return ResponseResult.success(articlesList);
    }

    @Override
    public ResponseResult<?> deleteArticle(Long essayId){
        try {
            int affectedRows = articleMapper.deleteById(essayId);
            if (affectedRows > 0) {
                return new ResponseResult<>(200, "文章删除成功！");
            } else {
                return new ResponseResult<>(500, "文章删除失败");
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "文章删除失败");
        }
    }
    @Override
   public ResponseResult<?> showStar(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof DetailedUser) {
            DetailedUser detailedUser = (DetailedUser) authentication.getPrincipal();
            userId = detailedUser.getUserId();
        }
        QueryWrapper<Stars> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserID", userId);
        List<Stars> essayIdList = starsMapper.selectList(queryWrapper);
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i < essayIdList.size(); i++) {
            Long essayId = essayIdList.get(i).getEssayId();
            Article article = articleMapper.selectById(essayId);
            if (article != null) {
                article.setStared(true);
                articleList.add(article);
            }
            article.setUser(userMapper.selectById(article.getUserID()));
        }
        Collections.sort(articleList, Comparator.comparing(Article::getReleaseTime).reversed());
        return ResponseResult.success(articleList);
    }

    @Override
    public ResponseResult<?> deleteAnswer(Long answerID){
        try {
            int affectedRows = answerMapper.deleteById(answerID);
            if (affectedRows > 0) {
                return new ResponseResult<>(200, "问题删除成功！");
            } else {
                return new ResponseResult<>(500, "问题删除失败");
            }
        } catch (Exception e) {
            return new ResponseResult<>(500, "问题删除失败");
        }
    }
}



