package com.pali.palindromebackend.service;

import com.pali.palindromebackend.business.custom.CommentBO;
import com.pali.palindromebackend.business.custom.LaunchBO;
import com.pali.palindromebackend.business.custom.ReactionBO;
import com.pali.palindromebackend.business.custom.UserBO;
import com.pali.palindromebackend.business.custom.impl.CommentBOimpl;
import com.pali.palindromebackend.business.custom.impl.ReactionBOimpl;
import com.pali.palindromebackend.business.custom.impl.UserBOimpl;
import com.pali.palindromebackend.dto.CommentDTO;
import com.pali.palindromebackend.dto.ReactionDTO;
import com.pali.palindromebackend.dto.UserDTO;
import com.pali.palindromebackend.entity.custom.LaunchUserDetails;
import com.pali.palindromebackend.model.DashboardLaunchDetail;
import com.pali.palindromebackend.model.LaunchCommentBody;
import com.pali.palindromebackend.model.LaunchReactionBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Mr.Damika Anuapama Nanayakkara <damikaanupama@gmail.com>
 * @since : 7/17/2022
 **/
@Service
public class FullLaunchBodyPackager {
    @Autowired
    private LaunchBO bo;
    @Autowired
    private ReactionBO bo1;
    @Autowired
    private CommentBO bo2;
    @Autowired
    private UserBO bo3;

    @Autowired
    private FileService fileService;


    public DashboardLaunchDetail getLaunch(LaunchUserDetails detail){
        ArrayList<LaunchReactionBody> lr = new ArrayList<>();
        ArrayList<LaunchCommentBody> lc = new ArrayList<>();
        byte[] launchMedia = fileService.getMedia(detail.getMedia());
        byte[] userMedia = fileService.getMedia(detail.getProfilePicture());

        List<ReactionDTO> reactions = bo1.getLaunchReactions(detail.getId());
        List<CommentDTO> comments = bo2.getLaunchComments(detail.getId());

        reactions.forEach(reaction -> {
            UserDTO user = null;
            LaunchReactionBody body;
            byte[] picture = new byte[0];

            try {
                user = bo3.getUser(reaction.getUserId());
                picture = fileService.getMedia(user.getProfilePicture());
            } catch (Exception e) {
                e.printStackTrace();
            }
//                    assert user != null;
            body = new LaunchReactionBody(
                    reaction.getId(),
                    reaction.getType(),
                    reaction.getReactionTime(),
                    reaction.getUpdatedTime(),
                    user.getId(),
                    user.getUsername(),
                    picture,
                    user.getIsActive()
            );
            lr.add(body);
        });

        comments.forEach(comment -> {
            UserDTO user = null;
            LaunchCommentBody body;
            byte[] picture = new byte[0];

            try {
                user = bo3.getUser(comment.getUser());
                picture = fileService.getMedia(user.getProfilePicture());
            } catch (Exception e) {
                e.printStackTrace();
            }
//                    assert user != null;
            body = new LaunchCommentBody(
                    comment.getId(),
                    comment.getComment(),
                    comment.getCommentedDate(),
                    comment.getLastUpdatedDate(),
                    user.getId(),
                    user.getUsername(),
                    picture,
                    user.getIsActive()
            );
            lc.add(body);
        });

        DashboardLaunchDetail dld = new DashboardLaunchDetail(
                detail.getId(),
                launchMedia,
                detail.getMediaType(),
                detail.getDescription(),
                detail.getFeeling(),
                detail.getUserId(),
                detail.getUserName(),
                detail.getShortDescription(),
                userMedia,
                detail.getUpdatedDate(),
                detail.getCreatedDate(),
                lr,
                lc
        );
        return dld;
    }
}
