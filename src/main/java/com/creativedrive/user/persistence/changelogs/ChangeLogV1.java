package com.creativedrive.user.persistence.changelogs;

import com.creativedrive.user.domain.User;
import com.creativedrive.user.domain.UserProfile;
import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.springframework.context.annotation.Profile;

/**
 * Databas change logs V1
 */
@ChangeLog(order = "001")
public final class ChangeLogV1 extends AbstractChangeLog {

    /**
     * Create root user, to make application accessible and ready to use
     *
     * @param jongo {@link org.jongo.Jongo}
     */
    @Profile("production")
    @ChangeSet(order = "001", id = "Create Root User", author = "cadu.goncalves")
    public void createRootUser(Jongo jongo){
        // Entity
        User root = new User();
        root.setName("root");
        root.setPassword(ENCRYPTOR.encryptPassword("change#666"));
        root.setProfile(UserProfile.ADMIN);
        root.setEmail("not_defined@domain");

        // Create
        MongoCollection usersCol = jongo.getCollection("users");
        usersCol.save(root);
    }

    /**
     * Create test user
     *
     * @param jongo {@link org.jongo.Jongo}
     */
    @Profile("dev")
    @ChangeSet(order = "001", id = "Create Test User", author = "cadu.goncalves")
    public void createTestUser(Jongo jongo){
        // Entity
        User test = new User();
        test.setName("test");
        test.setPassword(ENCRYPTOR.encryptPassword("change#666"));
        test.setProfile(UserProfile.ADMIN);
        test.setEmail("not_defined@domain");

        // Create
        MongoCollection usersCol = jongo.getCollection("users");
        usersCol.save(test);
    }
}
