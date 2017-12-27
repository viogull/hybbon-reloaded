package com.viogull.hybbon.p2p.utils;

/**
 * Created by ghost on 27.12.2017.
 */
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.model.UserPermission;

import java.util.Comparator;


public class UserPermissionComparator implements Comparator<UserPermission> {

    @Override
    public int compare(UserPermission perm1, UserPermission perm2) {
        if (perm1.getPermission() == perm2.getPermission()) {
            return perm1.getUserId().toLowerCase().compareTo(perm2.getUserId().toLowerCase());
        } else {
            return perm1.getPermission() == PermissionType.WRITE ? -1 : 1;
        }
    }
}