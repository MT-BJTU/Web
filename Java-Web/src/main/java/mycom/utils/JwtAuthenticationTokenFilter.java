package mycom.utils;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import mycom.ActionResult.ResponseResult;
import mycom.bean.User;
import mycom.mapper.UserMapper;
import mycom.model.DetailedUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获取token
        try {
            String token = request.getHeader("token");
            if (!StringUtils.hasText(token)) {
                // 放行
                filterChain.doFilter(request, response);
                return;
            }
            // 解析token
            String userId;
            try {
                Claims claims = JwtUtil.parseJWT(token);
                userId = claims.getSubject();
            } catch (Exception e) {
                throw new RuntimeException("token非法,请重新登录");
            }
            // 查询用户信息
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserId, userId);
            User user = userMapper.selectOne(queryWrapper);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            DetailedUser loginUser = new DetailedUser(user);

            // 封装Authentication对象存入SecurityContextHolder
            // 获取权限信息封装到Authentication中
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser,
                    null, loginUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            // 放行
            filterChain.doFilter(request, response);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            ResponseResult result;
            if (errorMsg.contains("token非法")) {
                result = new ResponseResult(HttpStatus.UNAUTHORIZED.value(), "token非法,可能已过期，请重新登录");
            } else {
                result = new ResponseResult(HttpStatus.UNAUTHORIZED.value(), "认证失败，请重新登录");
            }
            String json = JSON.toJSONString(result);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }
    }
}

