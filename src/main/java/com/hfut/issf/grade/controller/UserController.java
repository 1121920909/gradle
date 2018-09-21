package com.hfut.issf.grade.controller;

import com.hfut.issf.grade.controller.form.GradeQuery;
import com.hfut.issf.grade.controller.form.ImportForm;
import com.hfut.issf.grade.controller.form.SignInForm;
import com.hfut.issf.grade.controller.form.StatisticsQuery;
import com.hfut.issf.grade.domain.Grade;
import com.hfut.issf.grade.domain.Student;
import com.hfut.issf.grade.service.StudentService;
import com.hfut.issf.grade.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class UserController {
    private final UserService userService;
    private final StudentService studentService;
    @Autowired
    public UserController(UserService userService, StudentService studentService) {
        this.userService = userService;
        this.studentService = studentService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/student/queryGrade";
    }

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @PostMapping("/signIn")
    public String singIn(@Valid SignInForm signInForm,
                         BindingResult result,
                         Model model) {
        if (result.hasErrors()) {
            return ("/signIn");
        }
        userService.signIn(signInForm.getUsername(), signInForm.getPassword());
        return "/login";
    }

    @GetMapping("/signIn")
    public String singIn(SignInForm signInForm) {
        return "/signIn";
    }

    @GetMapping("/student/queryGrade")
    public String queryGrade(GradeQuery query, Principal principal, Model model) {
        List<String> courseTypes = studentService.getALlCourseType();
        List<String> semesters = studentService.getAllSemester();
        String username = principal.getName();
        Student student = studentService.getStudentInfo(username);
        String courseName = null;
        String courseType = null;
        String semester = null;
        String courseNum = null;
        if (query != null) {
            courseName = query.getCourseName().equals("") ? null : query.getCourseName();
            courseType = query.getCourseType().equals("") ? null : query.getCourseType();
            semester = query.getSemester().equals("") ? null : query.getSemester();
            courseNum = query.getCourseNum().equals("") ? null : query.getCourseNum();
        }
        List<Grade> gradeList = new ArrayList<>();
        if (student != null) {
            gradeList = studentService.getGrades(student.getNum(),
                                                        courseName,
                                                        courseType,
                                                        -1,
                                                        null,
                                                        semester,
                                                        courseNum);
        }
        model.addAttribute("courseTypes", courseTypes);
        model.addAttribute("semesters", semesters);
        model.addAttribute("gradeList", gradeList);
        model.addAttribute("query", query);
        return "/student/gradeQuery";
    }

    @PostMapping("/student/queryGrade")
    public String queryGradePost(GradeQuery query, Principal principal,Model model) {
        log.info("query: {}",query);
        return null;
    }

    @GetMapping("/student/import")
    public String importGrade(ImportForm form,Model model) {
        model.addAttribute("form",form);
        return "/student/import";
    }

    @PostMapping("/student/import")
    public String importGradePost(ImportForm form,Principal principal) {
        String username = principal.getName();
        studentService.crawlerGrade(form.getUsername(),form.getPassword(),username);
        return "redirect:/student/queryGrade";
    }

    @GetMapping("/student/gradeStatistics")
    public String statistics(StatisticsQuery form, Model model) {
        model.addAttribute("form", form);
        List<String> courseTypes = studentService.getALlCourseType();
        List<String> semesters = studentService.getAllSemester();
        model.addAttribute("courseTypeList", courseTypes);
        model.addAttribute("semesterList", semesters);
        double credit = 0;
        double gpa = 0;
        model.addAttribute("credit", credit);
        model.addAttribute("gpa", gpa);
        return "/student/gradeStatistics";
    }

    @PostMapping("/student/gradeStatistics")
    public String statistics(StatisticsQuery form, Principal principal, Model model) {
        model.addAttribute("form", form);
        List<String> courseTypes = studentService.getALlCourseType();
        List<String> semesters = studentService.getAllSemester();
        model.addAttribute("courseTypeList", courseTypes);
        model.addAttribute("semesterList", semesters);
        List<Grade> gradeList = new ArrayList<>();
        Student student = studentService.getStudentInfo(principal.getName());
        if (student != null) {
            String stuNum = student.getNum();
            for (String semeste : form.getSemesters()) {
                for (String type : form.getCourseType()) {
                    gradeList.addAll(studentService.getGrades(stuNum, null, type, -1, null, semeste, null));
                }
            }
        }
        double credit = 0;
        double gpa = 0;
        for (Grade g : gradeList) {
            credit += Double.parseDouble(g.getCredit());
            gpa += g.getGpa();
        }
        model.addAttribute("credit", credit);
        model.addAttribute("gpa", gpa);
        return "/student/gradeStatistics";
    }
}
