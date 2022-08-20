package com.voidsow.myrpc.framework.core.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Enumeration;

public class Utils {
    static private final ObjectMapper mapper = new ObjectMapper();

    static public ObjectMapper getMapper() {
        return mapper;
    }

    static public boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    static public String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) ;
                else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("IP地址获取失败" + e.toString());
        }
        return "";
    }


}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }

    public TreeNode constructMaximumBinaryTree(int[] nums) {
        this.nums = nums;
        return dfs(0, nums.length);
    }

    int[] nums;

    TreeNode dfs(int left, int right) {
        if (left >= right)
            return null;
        int index = left;
        for (int i = left; i < right; i++) {
            if (nums[index] < nums[i])
                index = i;
        }
        TreeNode node = new TreeNode(nums[index]);
        node.right = dfs(left, index);
        node.left = dfs(index + 1, right);
        return node;
    }

}
