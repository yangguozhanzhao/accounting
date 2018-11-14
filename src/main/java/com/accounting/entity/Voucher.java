package com.accounting.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;
    
    // 内容,可以空
    @Column(nullable = true,name="content")
    private String content ;
    
    // 图片
    @Column(nullable = false,name="imagePath")
	private String imagePath;
    
    //记账日期
    @Column(nullable = false,name="recordDate")
    private Date recordDate;
    
    @ManyToOne(targetEntity=User.class)
	@JoinColumn(nullable=false)
	private User user;
    
    @CreatedDate
    private Date createTime;
    @LastModifiedDate
    private Date updateTime;
    
    public Voucher () {
		
	}
    public Voucher(Date recordDate,String imagePath,String content,User user) {
        
        this.imagePath=imagePath;
        this.recordDate=recordDate;
        this.content=content;
        this.user=user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    @Temporal(TemporalType.DATE)
    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }
    
    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    public Date getCreateTime() {
    	return createTime;
	}
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}