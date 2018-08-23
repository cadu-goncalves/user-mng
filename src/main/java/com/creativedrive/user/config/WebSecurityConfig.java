package com.creativedrive.user.config;

import com.creativedrive.user.component.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Bootstrap security configurations.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends GlobalMethodSecurityConfiguration {

    private AnnotationAttributes enableMethodSecurity;

    @Override
    protected AccessDecisionManager accessDecisionManager() {
        // Use security roles without the "ROLE_" prefix
        RoleVoter roleVoter = new RoleVoter();
        roleVoter.setRolePrefix("");

        // Default inherited implementation
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList<>();
        ExpressionBasedPreInvocationAdvice expressionAdvice = new ExpressionBasedPreInvocationAdvice();
        expressionAdvice.setExpressionHandler(getExpressionHandler());
        if (enableMethodSecurity().getBoolean("prePostEnabled")) {
            decisionVoters.add(new PreInvocationAuthorizationAdviceVoter(expressionAdvice));
        }
        if (enableMethodSecurity().getBoolean("jsr250Enabled")) {
            decisionVoters.add(new Jsr250Voter());
        }
        decisionVoters.add(roleVoter); // <<< Custom roleVoter goes here
        decisionVoters.add(new AuthenticatedVoter());
        return new AffirmativeBased(decisionVoters);
    }

    private AnnotationAttributes enableMethodSecurity() {
        if (enableMethodSecurity == null) {
            EnableGlobalMethodSecurity methodSecurityAnnotation = AnnotationUtils
                    .findAnnotation(getClass(), EnableGlobalMethodSecurity.class);
            Map<String, Object> methodSecurityAttrs = AnnotationUtils
                    .getAnnotationAttributes(methodSecurityAnnotation);
            this.enableMethodSecurity = AnnotationAttributes.fromMap(methodSecurityAttrs);
        }
        return this.enableMethodSecurity;
    }

    /**
     * API Web security filters and policies
     */
    @Configuration
    public static class ApiWebSecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthProvider authProvider;

        public static final String BASE_URI = "/api/**/";

        private static final String[] ENDPOINTS = new String[]{"/user/**/", "/users/**/"};

        protected void configure(HttpSecurity http) throws Exception {
            // Auth matchers
            http.antMatcher(BASE_URI).authorizeRequests()
                    .antMatchers(ENDPOINTS).authenticated()
                    .anyRequest().authenticated();

            // Basic auth
            http.httpBasic();
            // Disable CSRF filter
            http.csrf().disable();
            // Stateless
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }


        @Override
        protected void configure(AuthenticationManagerBuilder auth) {
            // Attach auth provider to Spring Security
            auth.authenticationProvider(authProvider);
        }

    }

}
