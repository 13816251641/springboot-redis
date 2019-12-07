package com.lujieni.service.impl;

import com.lujieni.entity.User;
import com.lujieni.dao.UserMapper;
import com.lujieni.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lujieni
 * @since 2019-12-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
