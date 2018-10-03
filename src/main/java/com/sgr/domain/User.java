package com.sgr.domain;

import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;

@Entity(name = "user_details")
public class User {
    @Id
    @Column(name = "username", unique = true)
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Transient
    private String passwordConfirm;
    @Transient
    private MultipartFile picture;
    private String phone;
    private boolean active;
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = {@JoinColumn(name = "user_name", referencedColumnName = "username")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private List<Role> roles;
    @Column(name = "profile_pic_link")
    private String profile_pic_link;
    @Column(name = "profile_thumb_url")
    private String profile_thumb_url;
    @Column(name = "pic_public_id")
    private String pic_public_id;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }

    public String getProfile_pic_link() {
        return profile_pic_link;
    }

    public void setProfile_pic_link(String profile_pic_link) {
        this.profile_pic_link = profile_pic_link;
    }

    public String getProfile_thumb_url() {
        return profile_thumb_url;
    }

    public void setProfile_thumb_url(String profile_thumb_url) {
        this.profile_thumb_url = profile_thumb_url;
    }

    public String getPic_public_id() {
        return pic_public_id;
    }

    public void setPic_public_id(String pic_public_id) {
        this.pic_public_id = pic_public_id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", passwordConfirm='" + passwordConfirm + '\'' +
                ", picture=" + picture +
                ", phone='" + phone + '\'' +
                ", active=" + active +
                ", roles=" + roles +
                ", profile_pic_link='" + profile_pic_link + '\'' +
                ", profile_thumb_url='" + profile_thumb_url + '\'' +
                ", pic_public_id='" + pic_public_id + '\'' +
                '}';
    }
}
