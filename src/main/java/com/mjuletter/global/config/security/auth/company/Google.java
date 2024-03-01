package com.mjuletter.global.config.security.auth.company;

import com.mjuletter.domain.user.domain.Provider;
import com.mjuletter.global.config.security.auth.OAuth2UserInfo;

import java.util.Map;

public class Google extends OAuth2UserInfo {

    public Google(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {

        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {

        return (String) attributes.get("email");
    }

    @Override
    public String getPicture() {

        return (String) attributes.get("picture");
    }


    @Override
    public String getProvider(){
        return Provider.google.toString();
    }
}
