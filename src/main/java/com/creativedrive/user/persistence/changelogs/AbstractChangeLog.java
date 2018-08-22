package com.creativedrive.user.persistence.changelogs;

import com.creativedrive.user.config.AuthConfig;
import org.jasypt.util.password.PasswordEncryptor;

/**
 * Database change logs
 */
public abstract class AbstractChangeLog {

    protected static PasswordEncryptor ENCRYPTOR;

    // Work around since Mongobee doesn't support Spring Contexts for DI
    static {
        ENCRYPTOR = new AuthConfig().passwordEncryptor();
    }
}
