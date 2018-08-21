package com.creativedrive.user.config;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;

/**
 * Bootstrap authentication features
 */
@Configuration
public class AuthConfig extends GlobalAuthenticationConfigurerAdapter {

    /**
     * Factory for password encryptor
     *
     * @return {@link org.jasypt.util.password.PasswordEncryptor}
     * @see http://www.jasypt.org/encrypting-passwords.html
     */
    @Bean
    public PasswordEncryptor passwordEncryptor() {
        ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setAlgorithm("SHA-512");
        passwordEncryptor.setStringOutputType("hexadecimal");
        passwordEncryptor.setPlainDigest(true);
        return passwordEncryptor;
    }
}

