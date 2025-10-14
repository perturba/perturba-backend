package com.hyunwoosing.perturba.domain.asset.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetKind {
    INPUT("입력 이미지"),
    PERTURBED("변환된 이미지"),
    DEEPFAKE_OUTPUT("deepfake 적용 이미지"),
    PERTURBATION_VIS("적용된 perturbation"),
    ;

    private final String description;


}
