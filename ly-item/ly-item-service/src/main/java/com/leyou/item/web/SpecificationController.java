package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specService;

    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable Long cid){
        return ResponseEntity.ok(specService.queryGroupByCid(cid));
    }

    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam Long gid){
        return ResponseEntity.ok(specService.queryParamByGid(gid));
    }


}
