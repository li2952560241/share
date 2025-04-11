package com.share.user.api;

import com.share.common.core.constant.ServiceNameConstants;
import com.share.common.core.domain.R;
import com.share.user.domain.UserInfo;
import com.share.user.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户服务
 *
 * @author share
 */
@FeignClient(contextId = "remoteUserInfoService",
        value = ServiceNameConstants.SHARE_USER,
        fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{

    //小程序授权登录
    @GetMapping("/userInfo/wxLogin/{code}")
    public R<UserInfo> wxLogin(@PathVariable("code") String code);

    //获取用户详细信息
    @GetMapping(value = "/userInfo/getUserInfo/{id}")
    public R<UserInfo> getInfo(@PathVariable("id") Long id);

    // 获取用户数量
    @GetMapping("/userInfo/getUserCount")
    public R getUserCount();
}
