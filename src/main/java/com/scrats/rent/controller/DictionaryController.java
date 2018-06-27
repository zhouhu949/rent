package com.scrats.rent.controller;

import com.scrats.rent.common.JsonResult;
import com.scrats.rent.constant.GlobalConst;
import com.scrats.rent.entity.Dictionary;
import com.scrats.rent.entity.DictionaryIterm;
import com.scrats.rent.service.DictionaryItermService;
import com.scrats.rent.service.DictionaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created with scrat.
 * Description: ${DESCRIPTION}.
 * Email:    guosq@scrats.cn.
 * Author:   lol.
 * Date:     2018/6/6 22:20.
 */
@RestController
@RequestMapping("/dic")
public class DictionaryController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private DictionaryItermService dictionaryItermService;


    @GetMapping("/dicListAll")
    public JsonResult dicListAll() {

        List<Dictionary> list = dictionaryService.selectAll();

        return new JsonResult<List>(list);
    }

    @GetMapping("/dicItermListAll")
    public JsonResult dicItermListAll(String dicCode) {
        List<DictionaryIterm> list = dictionaryItermService.findListBy("dicCode", dicCode);
        return new JsonResult<List>(list);
    }

    @GetMapping("/facilitiestAll")
    public JsonResult facilitiestAll() {
        List<DictionaryIterm> facilities = dictionaryItermService.findListBy("dicCode", GlobalConst.FACILITY_CODE);
        return new JsonResult<List>(facilities);
    }

    @GetMapping("/decorationAll")
    public JsonResult decorationAll() {
        List<DictionaryIterm> decorations = dictionaryItermService.findListBy("dicCode", GlobalConst.DECORATION_CODE);
        return new JsonResult<List>(decorations);
    }

    @GetMapping("/orientationAll")
    public JsonResult orientationAll() {
        List<DictionaryIterm> orientations = dictionaryItermService.findListBy("dicCode", GlobalConst.ORIENTATION_CODE);
        return new JsonResult<List>(orientations);
    }

    @GetMapping("/extraAll")
    public JsonResult extraAll() {
        List<DictionaryIterm> extras = dictionaryItermService.findListBy("dicCode", GlobalConst.EXTRA_CODE);
        return new JsonResult<List>(extras);
    }

    @GetMapping("/depositAll")
    public JsonResult depositAll() {
        List<DictionaryIterm> depositIterms = dictionaryItermService.findListBy("dicCode", GlobalConst.DEPOSIT_ITERM_CODE);
        return new JsonResult<List>(depositIterms);
    }

}
