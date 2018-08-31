package com.peipei.rongim.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.peipei.rongim.entity.UserInfo;
import com.peipei.rongim.repository.UserInfoRepository;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(path="/peipei")
public class RongApi {

    private static final String appKey = "x4vkb1qpxfiuk";
    private static final String appSecret = "KRZna6Sw0j0l";
    RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
    @Autowired
    private UserInfoRepository userInfoRepository;
    @GetMapping(path = "/getUserToken")
    public  @ResponseBody String getUserToken(@RequestParam String userId,@RequestParam String name,@RequestParam String portraitUri) throws Exception {
        User User = rongCloud.user;
        List<UserInfo> list = userInfoRepository.findByUserId(userId);
        System.out.println(list.size());
        //判断数据库user token 是否注册。如果注册从数据库拿，否则注册新的token 插入到数据库
        if(list.size() <= 0){
            UserModel user = new UserModel()
                    .setId(userId)
                    .setName(name)
                    .setPortrait(portraitUri);
            TokenResult result = User.register(user);
            UserInfo userInfo = new UserInfo();
            JSONObject jsonObject = JSON.parseObject(result.toString());
            userInfo.setUserId(jsonObject.getString("userId"));
            userInfo.setPortraitUri(portraitUri);
            userInfo.setUserToken(jsonObject.getString("token"));
            userInfo.setUserName(name);
            userInfoRepository.save(userInfo);
            return result.toString();
        }else{
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"token\":" + "\"" +list.get(0).getUserToken() + "\"" );
            sb.append("\"userId\":" + "\"" +list.get(0).getUserId() + "\"" );
            sb.append("\"code\":200");
            sb.append("}");
            return sb.toString();
        }
    }

    //更新用户信息
    @GetMapping(path = "/refreshUserInfo")
    public  @ResponseBody String refreshUserInfo(@RequestParam String userId,@RequestParam String name,@RequestParam String portraitUri) throws Exception {
        User User = rongCloud.user;
        UserModel user = new UserModel()
                .setId(userId)
                .setName(name)
                .setPortrait(portraitUri);
        Result refreshResult = User.update(user);
        System.out.println("refresh:  " + refreshResult.toString());
        return refreshResult.toString();
    }
}
