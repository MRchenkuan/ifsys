package com.gigold.pay.ifsys.aop.authority;

import com.gigold.pay.framework.core.ResponseDto;
import com.gigold.pay.framework.web.interceptor.BusinessDelegate;
import com.gigold.pay.ifsys.RequestDto.*;
import com.gigold.pay.ifsys.annoation.Authority;
import com.gigold.pay.ifsys.bo.InterFaceInfo;
import com.gigold.pay.ifsys.bo.PrivilegeInfo;
import com.gigold.pay.ifsys.bo.TypeRole;
import com.gigold.pay.ifsys.bo.UserInfo;
import com.gigold.pay.ifsys.dao.InterFaceDao;
import com.gigold.pay.ifsys.service.InterFaceService;
import com.gigold.pay.ifsys.service.PrivilegeService;
import com.gigold.pay.ifsys.util.Constant;
import com.gigold.pay.ifsys.util.JSONUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gigold.pay.ifsys.util.JSONUtil.getRequestObject;

/**
 * 权限认证拦截器
 *
 */
public class AuthorityAnnotationInterceptor implements BusinessDelegate {
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    InterFaceService interFaceService;
    @Autowired
    InterFaceDao interFaceDao;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            Class<?> clazz = hm.getBeanType();
            Method m = hm.getMethod();
            try {
                if (clazz != null && m != null) {
                    boolean isClzAnnotation = clazz.isAnnotationPresent(Authority.class);
                    boolean isMethondAnnotation = m.isAnnotationPresent(Authority.class);
                    Authority authority = null;
                    // 如果方法和类声明中同时存在这个注解，那么方法中的会覆盖类中的设定。
                    if (isMethondAnnotation) {
                        authority = m.getAnnotation(Authority.class);
                    } else if (isClzAnnotation) {
                        authority = clazz.getAnnotation(Authority.class);
                    }

                    // 如果注解了权限要求,则需要鉴权,否则不需要
                    if (authority != null) {
                        // 根据请求,判断用户是否拥有对应接口中,所描述的项目的权限
                        if(isUserInRole(request,clazz.getName()+"."+m.getName(),authority.value())){
                            System.out.println(m.getName()+"拥有权限");
                            return true;
                        }else{
                            // 没有权限
                            System.out.println(m.getName()+"没有权限");
                            ResponseDto reDto = new ResponseDto();
                            reDto.setRspCd("D0001");reDto.setRspInf("没有权限");
                            JSONObject json = JSONObject.fromObject(reDto);
                            response.setContentType("application/json;charset=UTF-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(json.toString());
                            return false;
                        }
                    }

                    // 直接放行
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * 判断一个请求,是否具有权限
     * @return
     */
    private boolean isUserInRole(HttpServletRequest request, String mName, TypeRole role) {
        if(role.equals(TypeRole.Anyone))return true;
        String requestStr = "";
        try {
            BufferedReader reader = request.getReader();
            String line;
            while((line = reader.readLine())!=null){
                requestStr += line;
            }
            System.out.println("requestStr:::"+requestStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 简化比较
        mName = mName.replace("com.gigold.pay.ifsys.controller.","");

        System.out.println("mName::"+mName);
        HttpSession session = request.getSession();
        UserInfo userInfo = (UserInfo) session.getAttribute(Constant.LOGIN_KEY);
//        int pid = 0;
        // // TODO: 2017/3/15 已知BUG,userInfo不能为空,另外,当role为Anyone时,直接给true 
        if("InterFaceController.getInterFaceById".equals(mName)
                || "InterFaceController.deleteInterFaceById".equals(mName)
                ){
            InterFaceRequestDto dto = JSONUtil.getRequestObject(requestStr,InterFaceRequestDto.class);
            assert dto != null;
            InterFaceInfo interFaceInfo = dto.getInterFaceInfo();
            int ifId = interFaceInfo.getId();
            if(ifId>0){
                return isUserHasPrivilege(userInfo.getId(),interFaceInfo,role);
            }else{
                return false;
            }
        }

        if("InterFaceController.getAllIfSys".equals(mName)){
            IfSysMockPageDto dto = JSONUtil.getRequestObject(requestStr,IfSysMockPageDto.class);
            assert dto != null;
            int pid = dto.getPid();
            if(pid>0){
                // 比较权限
                return isUserHasPrivilege(userInfo.getId(),pid,role);
            }else{
                return false;
            }
        }

        if("InterFaceController.saveInterfaceDetail".equals(mName)){
            InterFaceDetailReqDto dto = JSONUtil.getRequestObject(requestStr,InterFaceDetailReqDto.class);
            assert dto != null;
            InterFaceInfo interFaceInfo = dto.getInterFaceInfo();
            int ifId = dto.getIfId();
            int sysId = interFaceInfo.getIfSysId();
            // 判断目标系统是否有权限
            if(!isUserHasPrivilegeInSystem(userInfo.getId(),sysId,role)) return false;
            // 新增接口则放行
            if(ifId<=0)return true;
            // 修改接口则判断原始接口的权限
            interFaceInfo.setId(ifId);
            return isUserHasPrivilege(userInfo.getId(),interFaceInfo,role);
        }

        if("InterFaceController.getInterfaceDetail".equals(mName)
                ||"InterFaceEditionController.getEditionsByIfId".equals(mName)){
            InterFaceDetailReqDto dto = JSONUtil.getRequestObject(requestStr,InterFaceDetailReqDto.class);
            assert dto != null;
            int ifId = dto.getIfId();
            InterFaceInfo interFaceInfo = new InterFaceInfo();
            if(ifId>0){
                interFaceInfo.setId(ifId);
                return isUserHasPrivilege(userInfo.getId(),interFaceInfo,role);
            }else{
                return false;
            }
        }

//        if("InterFaceDebugController.interfaceDebug".equals(mName)){
//            String ifUrl = request.getParameter("ifUrl");
//            String sys = request.getParameter("sys");
//            int sysId = Integer.parseInt(sys);
//            InterFaceInfo interFaceInfo = interFaceDao.getInterfaceByUrl(ifUrl,sysId);
//            int ifId = interFaceInfo.getId();
//            if(ifId>0){
//                interFaceInfo.setId(ifId);
//                return isUserHasPrivilege(userInfo.getId(),interFaceInfo,role);
//
//            }else{
//                return false;
//            }
//        }

        if("InterFaceInvokerController.followInterface".equals(mName)){
            Map dto = JSONUtil.getRequestObject(requestStr,Map.class);
            // 获取接口ID
            int ifId = 0;
            if(dto!=null) ifId = Integer.parseInt(String.valueOf(dto.get("ifId")));
            InterFaceInfo interFaceInfo = new InterFaceInfo();
            if(ifId>0){
                interFaceInfo.setId(ifId);
                return isUserHasPrivilege(userInfo.getId(),interFaceInfo,role);
            }else{
                return false;
            }
        }

        if("InterFaceSysController.getAllSysInfo".equals(mName)){
            HashMap map = JSONUtil.getRequestObject(requestStr,HashMap.class);
            // 获取接口ID
            int pid=0;
            if(map!=null)pid = Integer.parseInt(String.valueOf(map.get("pid")));
            if(pid>0){
                return isUserHasPrivilege(userInfo.getId(),pid,role);
            }else{
                return false;
            }
        }

        if("UserController.getMemberList".equals(mName)){
            UserQueryReqDto dto = JSONUtil.getRequestObject(requestStr,UserQueryReqDto.class);
            assert dto != null;
            int pid = dto.getPid();
            if(pid>0){
                return isUserHasPrivilege(userInfo.getId(),pid,role);
            }else{
                return false;
            }
        }

        if("UserController.addProjectMember".equals(mName)){
            InviteReqDto dto = JSONUtil.getRequestObject(requestStr,InviteReqDto.class);
            assert dto != null;
            int pid = dto.getPid();
            if(pid>0){
                return isUserHasPrivilege(userInfo.getId(),pid,role);
            }else{
                return false;
            }
        }

        if("InterFaceSysController.addInterfaceSystem".equals(mName)){
            InterFaceSysAddDto dto = JSONUtil.getRequestObject(requestStr,InterFaceSysAddDto.class);
            assert dto != null;
            int pid;
            if(dto.isSysAdding()){
                pid = dto.getPid();
                return isUserHasPrivilege(userInfo.getId(),pid,role);
            }else{
                int sysId = dto.getSysId();
                return isUserHasPrivilegeInSystem(userInfo.getId(),sysId,role);
            }
        }
        return false;

    }

    /**
     * 判定是否拥有权限
     * @param role 用户需要的权限
     * @return
     */
    private boolean isUserHasPrivilege(int userId, InterFaceInfo interFaceInfo, TypeRole role){
        List<PrivilegeInfo> privilegeInfos = privilegeService.getUserPrivilege(userId,interFaceInfo);
        return comParePrivilege(privilegeInfos,role);

    }
    /**
     * 判定是否拥有权限
     * @param role 用户需要的权限
     * @return
     */
    private boolean isUserHasPrivilege(int userId, int pid, TypeRole role) {
        List<PrivilegeInfo> privilegeInfos = privilegeService.getUserPrivilege(userId,pid);
        return comParePrivilege(privilegeInfos,role);
    }
    /**
     * 判定是否拥有权限
     * @param role 用户需要的权限
     * @return
     */
    private boolean isUserHasPrivilegeInSystem(int userId, int sysId, TypeRole role) {
        List<PrivilegeInfo> privilegeInfos = privilegeService.getUserPrivilegeBySysId(userId,sysId);
        return comParePrivilege(privilegeInfos,role);
    }

    /**
     * 比较权限
     * @param role 用户需要的权限
     * @return
     */
    private boolean comParePrivilege(List<PrivilegeInfo> privilegeInfos, TypeRole role){
        // 接口访问要求的权限
        List<Integer> requirePrivileges = role.getPrivilege();
        List<Integer> hasPrivileges = new ArrayList<>();
        // 进行比较
        for(PrivilegeInfo privilegeInfo :privilegeInfos){
            int privId = privilegeInfo.getId();
            hasPrivileges.add(privId);
        }
        if(hasPrivileges.containsAll(requirePrivileges)){
            return true;
        }else{
            System.out.println("权限不足");
            System.out.println("需要权限:"+requirePrivileges);
            System.out.println("实际权限:"+hasPrivileges);
            return false;
        }
    }
}