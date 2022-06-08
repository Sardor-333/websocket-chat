package com.example.websocketgroupdemo.projection;

public interface UserProjection {

    Long getId();

    String getUsername();

    String getFullName();

    Long getImgId();

    boolean getOnline();
}
