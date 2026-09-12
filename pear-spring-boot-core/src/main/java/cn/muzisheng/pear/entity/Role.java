package cn.muzisheng.pear.entity;

import cn.muzisheng.pear.annotation.PearField;
import cn.muzisheng.pear.annotation.PearObject;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "role")
@Data
@lombok.EqualsAndHashCode(callSuper = false)
@TableName("role")
@PearObject(
        desc = "Role Information.",
        path = "/role",
        pluralName = "roles"
)
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PearField(isPrimaryKey = true,isEdit = false)
    private Long id;
    @Column(name = "role_name")
    @PearField
    private String roleName;
    @Column(name = "description")
    @PearField(isOrderable = false, isSearchable = false)
    private String description;

}
