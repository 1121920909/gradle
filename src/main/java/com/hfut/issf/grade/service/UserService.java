package com.hfut.issf.grade.service;

import com.hfut.issf.grade.dao.UserDao;
import com.hfut.issf.grade.domain.User;
import com.hfut.issf.grade.service.Exception.UsernameAlreadyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserDao userDao;
    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /** 用户注册
     * @param username 用户名
     * @param password 密码
     * @return 用户id
     * @throws UsernameAlreadyException 用户名存在抛出
     */
    public User signIn(String username, String password) throws UsernameAlreadyException {
        if (userDao.selectByUserName(username) != null) {
            throw new UsernameAlreadyException("用户名已经存在");
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);
        String encodingPassword = encoder.encode(password);
        User user = new User();
        user.setUserName(username);
        user.setPassword(encodingPassword);
        userDao.insertUser(user);
        userDao.insertRoles(user.getId(), userDao.selectRoleId("user"));
        return user;
    }

    /** 此方法用于为SpringSecurity 查询用户登陆和授权信息
     * @param s 用户名
     * @return spring security User 对象
     * @throws UsernameNotFoundException 如果无法找到用户抛出
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.selectByUserName(s);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        List<String> roles = userDao.selectAuthenticationById(user.getId());
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(),
                user.getPassword(),
                authorities
        );
    }
}
