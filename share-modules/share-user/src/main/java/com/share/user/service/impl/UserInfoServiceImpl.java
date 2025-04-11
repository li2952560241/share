package com.share.user.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.share.common.core.context.SecurityContextHolder;
import com.share.common.core.domain.R;
import com.share.user.domain.UserCountVo;
import com.share.user.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.share.user.mapper.UserInfoMapper;
import com.share.user.service.IUserInfoService;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 用户Service业务层处理
 *
 * @author atguigu
 * @date 2025-04-08
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService
{
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private WxMaService wxMaService;

    /**
     * 查询用户列表
     *
     * @param userInfo 用户
     * @return 用户
     */
    @Override
    public List<UserInfo> selectUserInfoList(UserInfo userInfo)
    {
        return userInfoMapper.selectUserInfoList(userInfo);
    }

    //微信授权登录-远程调用
    @Override
    public UserInfo wxLogin(String code) {
        //1 拿着code + 微信公众平台id  + 秘钥 请求微信接口服务，返回openid
        try {
            WxMaJscode2SessionResult sessionInfo =
                    wxMaService.getUserService().getSessionInfo(code);
            String openid = sessionInfo.getOpenid();

            //2 拿着openid查询数据库表，如果表里面没有openid值，表示第一次登录
            LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserInfo::getWxOpenId,openid);
            UserInfo userInfo = userInfoMapper.selectOne(wrapper);

            //判断
            if(userInfo == null) { //如果表里面没有openid值，表示第一次登录
                // 添加用户信息到数据库表
                userInfo = new UserInfo();
                userInfo.setNickname(String.valueOf(System.currentTimeMillis()));
                userInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
                userInfo.setWxOpenId(openid);
                userInfoMapper.insert(userInfo);
            }

            //3 返回uerInfo用户信息
            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否免押金
     * @return
     */
    @Override

    public Boolean isFreeDeposit() {
        //微信支付分
        //https://pay.weixin.qq.com/wiki/doc/apiv3/payscore.php?chapter=18_1&index=2
        // 默认免押金，模拟实现
        UserInfo userInfo = userInfoMapper.selectById(SecurityContextHolder.getUserId());
        userInfo.setDepositStatus("1");
        this.updateById(userInfo);
        return true;
    }

    //统计2024年每个月注册人数
    //远程调用：统计用户注册数据
    @Override
    public Map<String, Object> getUserCount() {
        List<UserCountVo> list = baseMapper.selectUserCount();

        Map<String,Object> map = new HashMap<>();
        //创建两个list集合，一个对应所有日期，另外一个对应所有数据
        // list -- json数组 []
        //获取所有日期
        List<String> dateList =
                list.stream().map(UserCountVo::getRegisterDate).collect(Collectors.toList());
        //获取所有数据
        List<Integer> countList =
                list.stream().map(UserCountVo::getCount).collect(Collectors.toList());

        //放到map集合，返回
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

}
