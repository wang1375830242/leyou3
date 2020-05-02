package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * @author sakura
 */
@Table(name = "tb_spu")
@Data
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private String title;
    private String subTitle;

    @JsonIgnore
    private Boolean saleable;
    private Boolean valid;
    private Date createTime;

    @JsonIgnore
    private Date lastUpdateTime;

    @Transient
    private String canme;
    @Transient
    private String bname;

    @Transient
    private List<Sku> skus;

    @Transient
    private SpuDetall spuDetail;

}
