package com.hsp.fitu.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

// TODO: JsonIgnoreProperty 를 사용해 필요한 필드만 받기

public class KakaoDTO {
    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class KakaoProfile {
        private Long id;
        private String connected_at;
        private Properties properties;
        private KakaoAccount kakao_account;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Getter
        public class Properties {
            private String nickname;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public class KakaoAccount {
            private String email;
            private Boolean is_email_verified;
            private Boolean has_email;
            private Boolean profile_nickname_needs_agreement;
            private Boolean email_needs_agreement;
            private Boolean is_email_valid;
            private Profile profile;

            @Getter
            @JsonIgnoreProperties(ignoreUnknown = true)
            public class Profile {
                private String nickname;
                private Boolean is_default_nickname;
            }
        }
    }
}
