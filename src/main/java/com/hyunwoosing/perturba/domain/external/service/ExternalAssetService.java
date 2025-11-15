package com.hyunwoosing.perturba.domain.external.service;

import com.hyunwoosing.perturba.common.storage.S3UploadService;
import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetStatus;
import com.hyunwoosing.perturba.domain.asset.exception.AssetErrorCode;
import com.hyunwoosing.perturba.domain.asset.exception.AssetException;
import com.hyunwoosing.perturba.domain.asset.repository.AssetRepository;
import com.hyunwoosing.perturba.domain.external.mapper.ExternalAssetMapper;
import com.hyunwoosing.perturba.domain.external.web.dto.response.ExternalUploadImageResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import com.hyunwoosing.perturba.common.util.S3ObjectKeyUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExternalAssetService {

    private final AssetRepository assetRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public ExternalUploadImageResponse uploadExternalImage(MultipartFile file, Long ownerUserId) {
        //이미지 체크
        if (file == null || file.isEmpty()) {
            throw new AssetException(AssetErrorCode.EMPTY_FILE, "파일이 비어 있습니다.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("image/jpeg")) {
            throw new AssetException(AssetErrorCode.UNSUPPORTED_MIME_TYPE, "JPEG 이미지만 허용됩니다.");
        }
        validateDimension(file);


        User owner = resolveUser(ownerUserId);
        String objectKey = S3ObjectKeyUtil.inputKey(file.getOriginalFilename(), ownerUserId);
        s3UploadService.upload(objectKey, file);

        Asset asset = Asset.builder()
                .owner(owner)
                .objectKey(objectKey)
                .mimeType("image/jpeg")
                .status(AssetStatus.READY)
                .build();

        Asset saved = assetRepository.save(asset);
        return ExternalAssetMapper.toUploadResponse(saved);
    }


    //private
    private void validateDimension(MultipartFile file) {
        try {
            BufferedImage img = ImageIO.read(file.getInputStream());
            if (img == null) {
                throw new AssetException(AssetErrorCode.INVALID_IMAGE, "이미지 포맷을 인식할 수 없습니다.");
            }
            if (img.getWidth() != 224 || img.getHeight() != 224) {
                throw new AssetException(AssetErrorCode.INVALID_IMAGE_SIZE, "이미지 크기는 " + 224 + "x" + 224 + "만 허용됩니다.");
            }
        } catch (IOException e) {
            throw new AssetException(AssetErrorCode.INVALID_IMAGE, "이미지 분석 중 오류가 발생했습니다.", e);
        }
    }

    @Nullable
    private User resolveUser(@Nullable Long userId) {
        if (userId == null)
            return null;
        return userRepository.findById(userId).orElse(null);
    }
}
