package com.share.rules.domain;

import com.share.common.core.web.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "费用规则")
public class FeeRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则名称 */
    @Schema(description = "规则名称")
    private String name;

    /** 规则代码 */
    @Schema(description = "规则代码")
    private String rule;

    /** 规则描述 */
    @Schema(description = "规则描述")
    private String description;

    /** 状态代码，1有效，2关闭 */
    @Schema(description = "状态代码，1有效，2关闭")
    private String status;

}