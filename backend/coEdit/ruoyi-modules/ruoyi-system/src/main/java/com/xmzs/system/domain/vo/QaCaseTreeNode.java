package com.xmzs.system.domain.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.xmzs.system.domain.vo
 *
 * @author zhuzi
 * @date 2024/12/27
 * @description TODO
 */
@Data
public class QaCaseTreeNode {
    private String value;
    private String label;
    private List<QaCaseTreeNode> children = new ArrayList<>();
}
