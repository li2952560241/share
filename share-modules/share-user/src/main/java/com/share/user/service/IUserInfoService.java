package com.share.user.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.share.user.domain.UserInfo;

/**
 * 用户Service接口
 *
 * @author atguigu
 * @date 2025-04-08
 */
public interface IUserInfoService extends IService<UserInfo>
{

    /**
     * 查询用户列表
     *
     * @param userInfo 用户
     * @return 用户集合
     */
    public List<UserInfo> selectUserInfoList(UserInfo userInfo);

    ////微信授权登录-远程调用
    UserInfo wxLogin(String code);
}
