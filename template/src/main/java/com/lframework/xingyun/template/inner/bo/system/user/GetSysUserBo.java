package com.lframework.xingyun.template.inner.bo.system.user;

import com.lframework.starter.common.constants.StringPool;
import com.lframework.starter.common.utils.CollectionUtil;
import com.lframework.starter.common.utils.StringUtil;
import com.lframework.xingyun.template.inner.entity.SysDept;
import com.lframework.xingyun.template.inner.entity.SysPosition;
import com.lframework.xingyun.template.inner.entity.SysRole;
import com.lframework.xingyun.template.core.entity.SysUser;
import com.lframework.xingyun.template.inner.entity.SysUserDept;
import com.lframework.xingyun.template.inner.entity.SysUserPosition;
import com.lframework.xingyun.template.inner.entity.SysUserRole;
import com.lframework.xingyun.template.inner.service.system.SysDeptService;
import com.lframework.xingyun.template.inner.service.system.SysPositionService;
import com.lframework.xingyun.template.inner.service.system.SysRoleService;
import com.lframework.xingyun.template.inner.service.system.SysUserDeptService;
import com.lframework.xingyun.template.inner.service.system.SysUserPositionService;
import com.lframework.xingyun.template.inner.service.system.SysUserRoleService;
import com.lframework.starter.web.bo.BaseBo;
import com.lframework.starter.web.common.utils.ApplicationUtil;
import com.lframework.starter.web.dto.VoidDto;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class GetSysUserBo extends BaseBo<SysUser> {

  /**
   * ID
   */
  @ApiModelProperty("ID")
  private String id;

  /**
   * 编号
   */
  @ApiModelProperty("编号")
  private String code;

  /**
   * 姓名
   */
  @ApiModelProperty("姓名")
  private String name;

  /**
   * 用户名
   */
  @ApiModelProperty("用户名")
  private String username;

  /**
   * 邮箱
   */
  @ApiModelProperty("邮箱")
  private String email;

  /**
   * 联系电话
   */
  @ApiModelProperty("联系电话")
  private String telephone;

  /**
   * 性别
   */
  @ApiModelProperty("性别")
  private Integer gender;

  /**
   * 岗位
   */
  @ApiModelProperty("岗位")
  private List<String> positions;

  /**
   * 岗位名称
   */
  @ApiModelProperty("岗位名称")
  private String positionName;

  /**
   * 部门
   */
  @ApiModelProperty("部门")
  private List<String> depts;

  /**
   * 部门名称
   */
  @ApiModelProperty("部门名称")
  private String deptName;


  /**
   * 角色
   */
  @ApiModelProperty("角色")
  private List<String> roles;

  /**
   * 角色名称
   */
  @ApiModelProperty("角色名称")
  private String roleName;

  /**
   * 状态
   */
  @ApiModelProperty("状态")
  private Boolean available;

  /**
   * 是否锁定
   */
  @ApiModelProperty("是否锁定")
  private Boolean lockStatus;

  /**
   * 备注
   */
  @ApiModelProperty("备注")
  private String description;

  public GetSysUserBo() {

  }

  public GetSysUserBo(SysUser dto) {

    super(dto);
  }

  @Override
  protected void afterInit(SysUser dto) {

    SysUserPositionService sysUserPositionService = ApplicationUtil.getBean(
        SysUserPositionService.class);
    List<SysUserPosition> userPositions = sysUserPositionService.getByUserId(dto.getId());
    if (!CollectionUtil.isEmpty(userPositions)) {
      SysPositionService sysPositionService = ApplicationUtil.getBean(SysPositionService.class);
      this.positions = userPositions.stream().map(SysUserPosition::getPositionId)
          .collect(Collectors.toList());

      this.positionName = StringUtil.join(StringPool.STR_SPLIT_CN,
          userPositions.stream().map(t -> sysPositionService.findById(t.getPositionId()))
              .filter(Objects::nonNull).map(SysPosition::getName)
              .collect(Collectors.toList()));
    }

    SysUserDeptService sysUserDeptService = ApplicationUtil.getBean(SysUserDeptService.class);
    List<SysUserDept> userDepts = sysUserDeptService.getByUserId(dto.getId());
    if (!CollectionUtil.isEmpty(userDepts)) {
      SysDeptService sysDeptService = ApplicationUtil.getBean(SysDeptService.class);
      this.depts = userDepts.stream().map(SysUserDept::getDeptId)
          .collect(Collectors.toList());

      this.deptName = StringUtil.join(StringPool.STR_SPLIT_CN,
          userDepts.stream().map(t -> sysDeptService.findById(t.getDeptId()))
              .filter(Objects::nonNull).map(SysDept::getName)
              .collect(Collectors.toList()));
    }

    SysUserRoleService sysUserRoleService = ApplicationUtil.getBean(SysUserRoleService.class);
    List<SysUserRole> userRoles = sysUserRoleService.getByUserId(dto.getId());
    if (!CollectionUtil.isEmpty(userRoles)) {
      SysRoleService sysRoleService = ApplicationUtil.getBean(SysRoleService.class);
      this.roles = userRoles.stream().map(SysUserRole::getRoleId)
          .collect(Collectors.toList());

      this.roleName = StringUtil.join(StringPool.STR_SPLIT_CN,
          userRoles.stream().map(t -> sysRoleService.findById(t.getRoleId()))
              .filter(Objects::nonNull).map(SysRole::getName)
              .collect(Collectors.toList()));
    }
  }

  @Data
  public static class PositionBo extends BaseBo<VoidDto> {

    /**
     * 岗位ID
     */
    @ApiModelProperty("岗位ID")
    private String id;

    /**
     * 岗位名称
     */
    @ApiModelProperty("岗位名称")
    private String name;
  }

  @Data
  public static class DeptBo extends BaseBo<VoidDto> {

    /**
     * 部门ID
     */
    @ApiModelProperty("部门ID")
    private String id;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String name;
  }

  @Data
  public static class RoleBo extends BaseBo<VoidDto> {

    /**
     * 角色ID
     */
    @ApiModelProperty("角色ID")
    private String id;

    /**
     * 角色名称
     */
    @ApiModelProperty("角色名称")
    private String name;
  }
}
