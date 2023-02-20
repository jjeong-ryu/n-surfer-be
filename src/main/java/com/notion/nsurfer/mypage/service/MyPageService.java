package com.notion.nsurfer.mypage.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import com.notion.nsurfer.card.entity.Card;
import com.notion.nsurfer.card.repository.CardRepository;
import com.notion.nsurfer.common.ResponseCode;
import com.notion.nsurfer.common.ResponseDto;
import com.notion.nsurfer.common.config.CloudinaryConfig;
import com.notion.nsurfer.mypage.dto.GetWaveDto;
import com.notion.nsurfer.mypage.dto.UpdateUserProfileDto;
import com.notion.nsurfer.mypage.exception.UserNotFoundException;
import com.notion.nsurfer.user.dto.GetUserProfileDto;
import com.notion.nsurfer.user.entity.User;
import com.notion.nsurfer.user.mapper.UserMapper;
import com.notion.nsurfer.user.repository.UserRepository;
import com.notion.nsurfer.user.repository.UserRepositoryCustom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;
    private final UserMapper userMapper;
    private final Cloudinary cloudinary;
    private final CardRepository cardRepository;

    public ResponseDto<GetUserProfileDto.Response> getUserProfile(User user) {
        final String email = user.getEmail();
        return ResponseDto.<GetUserProfileDto.Response>builder()
                .responseCode(ResponseCode.GET_USER_PROFILE)
                .data(userMapper.getUserProfileToResponse(userRepositoryCustom.findByEmail(email)))
                .build();
    }

    @Transactional
    public ResponseDto<Object> updateUserProfile(UpdateUserProfileDto.Request dto) throws IOException {
        User user = userRepository.findById(dto.getUserInfo().getId())
                .orElseThrow(UserNotFoundException::new);
        user.update(dto);
        MultipartFile uploadedImage = dto.getImage();
        if(uploadedImage != null){
            String imageName = StringUtils.join(List.of(dto.getUserInfo().getEmail(), dto.getUserInfo().getProvider()), "_");
            Map uploadResponse = cloudinary.uploader().upload(uploadedImage, ObjectUtils.asMap("public_id", imageName));
            user.updateImage(uploadResponse.get("url").toString());
        }
        return ResponseDto.builder()
                .responseCode(ResponseCode.UPDATE_USER_PROFILE)
                .data(null).build();
    }

    public ResponseDto<GetWaveDto.Response> getWave(User user){
        List<Card> cards = cardRepository.findCardsWithWaveByUserId(user.getId());
        return ResponseDto.<GetWaveDto.Response>builder()
                .build();
    }
}
