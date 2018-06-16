package kr.co.redbrush.webapp.controller.admin;

import kr.co.redbrush.webapp.domain.Account;
import kr.co.redbrush.webapp.dto.FailedResult;
import kr.co.redbrush.webapp.dto.RequestResult;
import kr.co.redbrush.webapp.dto.SuccessfulResult;
import kr.co.redbrush.webapp.enums.MessageKey;
import kr.co.redbrush.webapp.form.SignupForm;
import kr.co.redbrush.webapp.service.MessageSourceService;
import kr.co.redbrush.webapp.service.impl.AccountServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@Controller
@Slf4j
public class AuthenticationController {
    public static final String SPRING_SECURITY_LAST_EXCEPTION = "SPRING_SECURITY_LAST_EXCEPTION";
    public static final String VIEW_LOGIN = "login";
    public static final String VIEW_LOGIN_REDIRECT = "/login/form";
    public static final String VIEW_SIGNUP = "signup";
    public static final String VIEW_SIGNUP_REDIRECT = "/signup";

    @Autowired
    private AccountServiceImpl accountServiceImpl;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageSourceService messageSourceService;

    @GetMapping("/login/form")
    public ModelAndView loginForm(HttpServletRequest request, Map<String, Object> model, String error) {
        setErrorCondition(request, model, error);

        if (accountServiceImpl.getCount() == 0) {
            return new ModelAndView(new RedirectView(VIEW_SIGNUP_REDIRECT));
        } else {
            return new ModelAndView(VIEW_LOGIN, model);
        }
    }

    private void setErrorCondition(HttpServletRequest request, Map<String, Object> model, String error) {
        AuthenticationException authenticationException = (AuthenticationException)request.getSession().getAttribute(SPRING_SECURITY_LAST_EXCEPTION);

        model.put("error", BooleanUtils.toBoolean(error));

        if (authenticationException!=null) {
            model.put("errorMessage", authenticationException.getMessage());
        }
    }

    @GetMapping("/signup")
    public ModelAndView signupForm() {
        if (accountServiceImpl.getCount() == 0) {
            return new ModelAndView(VIEW_SIGNUP);
        } else {
            return new ModelAndView(new RedirectView(VIEW_LOGIN_REDIRECT));
        }
    }

    @PostMapping("/signup")
    public RequestResult signup(@Valid SignupForm signupForm, BindingResult bindingResult) throws BindException {
        if (accountServiceImpl.getCount() == 0) {
            if (bindingResult.hasErrors()) {
                throw new BindException(bindingResult);
            }

            Account account = modelMapper.map(signupForm, Account.class);
            Account savedAccount = accountServiceImpl.insertAdmin(account);

            if (savedAccount != null) {
                return new SuccessfulResult();
            } else {
                return new FailedResult(messageSourceService.getMessage(MessageKey.ADMIN_CREATE_ERROR));
            }
        }

        return new FailedResult(messageSourceService.getMessage(MessageKey.ADMIN_ALREADY_CREATED));
    }
}