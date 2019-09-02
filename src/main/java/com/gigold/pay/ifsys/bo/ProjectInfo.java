package com.gigold.pay.ifsys.bo;

import com.gigold.pay.framework.core.Domain;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenkuan
 * on 2017/2/9.
 */

@Component
@Scope("prototype")
public class ProjectInfo extends Domain implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private int id,creatorId,masterId,asRole;
    private String projectName,projectDesc;
    private String ts,state;
    private UserInfo master,creator;
    private List<Member> members;

    public int getAsRole() {
        return asRole;
    }

    public void setAsRole(int asRole) {
        this.asRole = asRole;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserInfo getMaster() {
        return master;
    }

    public void setMaster(UserInfo master) {
        this.master = master;
    }

    public UserInfo getCreator() {
        return creator;
    }

    public void setCreator(UserInfo creator) {
        this.creator = creator;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
