package com.project.web.rest.dto;

import com.project.domain.Space;

/**
 * Created by nilpanescoll on 29/3/16.
 */
public class SpaceDTO {
    private Space space;
    private Boolean liked;
    private String masterImage;

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public String getMasterImage() {
        return masterImage;
    }

    public void setMasterImage(String masterImage) {
        this.masterImage = masterImage;
    }
}
