package com.foodsave.backend.security.aspect;

import com.foodsave.backend.security.PermissionHandler;
import com.foodsave.backend.security.annotation.RequirePermission;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    private final PermissionHandler permissionHandler;

    public PermissionAspect(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        permissionHandler.checkPermission(requirePermission);
    }
} 