package com.hyunwoosing.perturba.domain.apikey.service;

import com.hyunwoosing.perturba.domain.apikey.entity.ApiKey;
import com.hyunwoosing.perturba.domain.apikey.entity.enums.ApiKeyStatus;
import com.hyunwoosing.perturba.domain.apikey.mapper.ApiKeyMapper;
import com.hyunwoosing.perturba.domain.apikey.repository.ApiKeyRepository;
import com.hyunwoosing.perturba.domain.apikey.web.dto.request.IssueApiKeyRequest;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.ApiKeyMetaResponse;
import com.hyunwoosing.perturba.domain.apikey.web.dto.response.IssueApiKeyResponse;
import com.hyunwoosing.perturba.domain.user.entity.User;
import com.hyunwoosing.perturba.domain.user.error.UserErrorCode;
import com.hyunwoosing.perturba.domain.user.error.UserException;
import com.hyunwoosing.perturba.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;

    @Transactional
    public IssueApiKeyResponse issueOrRotate(Long userId, IssueApiKeyRequest request) {
        apiKeyRepository.deleteByOwner_Id(userId); //생성하기 이전에 기존 키 전부 삭제 (정책)

        ApiKeyCrypto.PlainAndHash pair = ApiKeyCrypto.generate();
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        ApiKey saved = apiKeyRepository.save(ApiKeyMapper.toEntity(owner, pair.hashHex, request));

        return ApiKeyMapper.toIssueApiKeyResponse(saved, pair.plaintext);
    }

    @Transactional
    public void revokeMyKey(Long userId) {
        apiKeyRepository.deleteByOwner_Id(userId); //정책상 하나이므로 전부삭제.
    }

    @Transactional
    public ApiKeyMetaResponse getMyKeyMeta(Long userId) {
        Optional<ApiKey> apiKey = apiKeyRepository.findFirstByOwner_IdAndStatus(userId, ApiKeyStatus.ACTIVE)
                .or(() -> apiKeyRepository.findByOwner_Id(userId).stream().findFirst());
        return apiKey.map(ApiKeyMapper::toMeta).orElse(null);
    }
}
