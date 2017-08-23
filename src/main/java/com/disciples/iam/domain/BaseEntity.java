package com.disciples.iam.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.TableGenerator;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 2436149785969523480L;
    
    public static final String TABLE_GENERATOR_NAME = "ID_INCREMENT_GENERATOR";
    
    private Integer id;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_GENERATOR_NAME)
    @TableGenerator(name = TABLE_GENERATOR_NAME, allocationSize = 1)
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity that = (BaseEntity) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }
    
}
