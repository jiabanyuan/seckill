package com.dwj.seckill.controller;

import com.dwj.seckill.common.BaseResponse;
import com.dwj.seckill.model.SeckillItemVo;
import com.dwj.seckill.model.SeckillResult;
import com.dwj.seckill.model.SeckillUrlResult;
import com.dwj.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @GetMapping("/list")
    public String list(Model model) {
        try {
            List<SeckillItemVo> list = seckillService.list();
            if (list == null || list.size() == 0) {
                model.addAttribute("isOpen", false);
                return "list";
            }
            model.addAttribute("isOpen", true);
            model.addAttribute("itemList", list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "list";
    }

    @ResponseBody
    @RequestMapping("/{isOpen}/state")
    public BaseResponse<String> state(@PathVariable Boolean isOpen) {
        try {
            return seckillService.state(isOpen);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseResponse.error("seckill open fail!");
        }
    }


    @ResponseBody
    @PostMapping("/{id}/{md5}/execute")
    public BaseResponse<SeckillResult> execute(@PathVariable Long id, @PathVariable String md5) {
        try {
            return seckillService.seckill(id, md5);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseResponse.error("seckill fail!");
        }
    }

    @ResponseBody
    @GetMapping("/{id}/url")
    public BaseResponse<SeckillUrlResult> url(@PathVariable Long id) {
        try {
            return seckillService.url(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return BaseResponse.error("server fail!");
        }
    }
}
