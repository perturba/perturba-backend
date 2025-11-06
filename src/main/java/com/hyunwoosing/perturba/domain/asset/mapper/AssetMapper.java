package com.hyunwoosing.perturba.domain.asset.mapper;

import com.hyunwoosing.perturba.domain.asset.entity.Asset;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetKind;
import com.hyunwoosing.perturba.domain.asset.entity.enums.AssetStatus;
import com.hyunwoosing.perturba.domain.asset.web.dto.request.UploadUrlRequest;
import com.hyunwoosing.perturba.domain.asset.web.dto.response.CompleteUploadResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AssetMapper {

    public static CompleteUploadResponse toCompleteUploadResponse(Asset asset){
        if (asset == null)
            return CompleteUploadResponse.builder().build();
        return CompleteUploadResponse.builder()
                .assetId(asset.getId())
                .kind(asset.getKind())
                .url(asset.getObjectKey())
                .mimeType(asset.getMimeType())
                .sizeBytes(asset.getSizeBytes())
                .width(asset.getWidth())
                .height(asset.getHeight())
                .sha256Hex(asset.getSha256Hex())
                .build();
    }

    public static Asset requestToAsset(UploadUrlRequest uploadUrlRequest, User owner, String objectKey){
        return Asset.builder()
                .job(null)
                .owner(owner)
                .kind(AssetKind.INPUT)
                .objectKey(objectKey)
                .mimeType(uploadUrlRequest.mimeType())
                .sizeBytes(uploadUrlRequest.sizeBytes())
                .width(uploadUrlRequest.width())
                .height(uploadUrlRequest.height())
                .sha256Hex(uploadUrlRequest.sha256Hex())
                .status(AssetStatus.UPLOADING)
                .build();
    }

    public static CompleteUploadResponse assetToCompleteUploadResponse(Asset asset, String viewUrl){
        return CompleteUploadResponse.builder()
                .assetId(asset.getId())
                .kind(asset.getKind())
                .objectKey(asset.getObjectKey())
                .url(viewUrl)
                .mimeType(asset.getMimeType())
                .sizeBytes(asset.getSizeBytes())
                .width(asset.getWidth())
                .height(asset.getHeight())
                .sha256Hex(asset.getSha256Hex())
                .build();
    }
}
