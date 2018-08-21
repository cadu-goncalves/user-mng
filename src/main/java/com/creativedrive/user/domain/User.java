package com.creativedrive.user.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.*;
import java.util.Objects;

/**
 * User entity model.
 *
 * @see https://docs.spring.io/spring-data/mongodb/docs/2.0.9.RELEASE/reference/html/#mapping-chapter
 */
@Document
public final class User {

    // Binds to '_id' https://docs.mongodb.com/manual/reference/method/ObjectId/
    @Id
    private String id;

    @Field
    @NotNull
    private String name;

    @Field
    @Email
    private String email;

    @Field
    @NotNull
    @Size(min = 6, max = 20)
    private String password;

    @Field
    @Max(255)
    private String address;

    @Field
    @Pattern(regexp = "^\\d{8,10}$")
    private String phone;

    @Field
    @Max(255)
    private String profile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
