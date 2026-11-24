package com.xmzs.system.controller;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * packageName com.xmzs.system.controller
 *
 * @author zhuzi
 * @date 2024/8/13
 * @description TODO
 */
public class Test {

    public static void main(String[] args) {
        boolean isValidating = false;
//        while (isValidating){
            String fileStatus = "validating_files";
            if(!"validating_files".equals(fileStatus)){
                System.out.println("~~~");
                isValidating = false;
//                break;
            }
//        }

    }

}
