package com.xmzs.knowledge.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xmzs.knowledge.domain.AccessToken;
import com.xmzs.knowledge.mapper.AccessTokenMapper;
import com.xmzs.knowledge.service.AccessTokenService;
import org.springframework.stereotype.Service;

/**
* @author huangkh
* @description 针对表【access_token(第三方接口访问token)】的数据库操作Service实现
* @createDate 2023-11-06 20:33:39
*/
@Service
public class AccessTokenServiceImpl extends ServiceImpl<AccessTokenMapper, AccessToken>
    implements AccessTokenService{

}




