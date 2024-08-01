package org.chad.vxlogin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chad.vxlogin.domain.po.User;
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
