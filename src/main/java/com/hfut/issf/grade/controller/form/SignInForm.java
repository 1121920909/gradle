package com.hfut.issf.grade.controller.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.print.attribute.standard.PrinterState;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.InputStream;

@Data
public class SignInForm {
    @NotEmpty(message = "用户名不能为空")
    @Size(max = 12,min = 6,message = "用户名长度须大于6小于12")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Size(min = 6,message = "密码长度须大于6")
    private String password;

}
