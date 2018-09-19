package com.hfut.issf.grade.dao;

import com.hfut.issf.grade.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    /**查询所有用户
     * @return List
     */
    List<User> selectAll();

    /** 查询用户权限
     * @param id user id
     * @return
     */
    List<String> selectAuthenticationById(int id);

    /** 插入用户
     * @param user
     * @return
     */
    int insertUser(User user);

    /** 根据用户名查询用户
     * @param userName 用户名
     * @return User
     */
    User selectByUserName(String userName);

    /** 添加权限
     * @param userId 用户Id
     * @param roleId 权限Id
     * @return
     */
    int insertRoles(@Param("userId") int userId, @Param("roleId") int roleId);

    /** 查询角色id
     * @param role 角色名字
     * @return id
     */
    int selectRoleId(String role);
}
