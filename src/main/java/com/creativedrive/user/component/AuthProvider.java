package com.creativedrive.user.component;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * Custom authentication for Spring Security, based on user datastore
 */
@Component
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    protected String getLogin(final Authentication authentication) {
        final String login = authentication.getPrincipal().toString();
        if (StringUtils.isEmpty(login)) {
            throw new AuthenticationServiceException("user required");
        }
        return login;
    }

    protected String getPassword(final Authentication authentication) {
        final String password = authentication.getCredentials().toString();
        if (StringUtils.isEmpty(password)) {
            throw new AuthenticationServiceException("password required");
        }
        return password;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = getLogin(authentication);
        String password = getPassword(authentication);

        try {
            Optional<User> user = loginService.checkAuth(username, password);
            if(user.isPresent()){
                // Granted
                String auth = user.get().getProfile();
                return new UsernamePasswordAuthenticationToken(username, password,
                        AuthorityUtils.createAuthorityList(auth));

            } else {
                // Denied
                return null;
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
    }
}
