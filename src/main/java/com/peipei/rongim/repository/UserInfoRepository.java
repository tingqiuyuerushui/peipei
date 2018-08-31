package com.peipei.rongim.repository;

import com.peipei.rongim.entity.UserInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserInfoRepository extends CrudRepository<UserInfo,Long> {
    List<UserInfo> findByUserId(String userId);
}
