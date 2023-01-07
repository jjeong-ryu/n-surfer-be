package com.notion.nsuffer.user.mapper;

import com.notion.nsuffer.common.CommonMapperConfig;
import com.notion.nsuffer.user.dto.SignUpDto;
import com.notion.nsuffer.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;

@Mapper(config = CommonMapperConfig.class)
public interface UserMapper {

    @Mapping(target = "authorities", ignore = true)
    User signUpToUser(SignUpDto.Request request);
}
