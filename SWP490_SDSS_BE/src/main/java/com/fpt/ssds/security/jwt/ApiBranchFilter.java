package com.fpt.ssds.security.jwt;

import com.fpt.ssds.common.exception.SSDSAuthorizationException;
import com.fpt.ssds.common.exception.TokenException;
import com.fpt.ssds.common.filter.SSDSFilter;
import com.fpt.ssds.constant.JWTClaims;
import com.fpt.ssds.domain.User;
import com.fpt.ssds.service.dto.ResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.fpt.ssds.constant.Constants.COMMON.USER;
import static com.fpt.ssds.constant.Constants.COMMON.USER_BRANCH;
import static com.fpt.ssds.utils.ResponseUtils.CODE_OK;

public class ApiBranchFilter extends SSDSFilter {
    private final Logger log = LoggerFactory.getLogger(ApiBranchFilter.class);

    private final TokenProvider tokenProvider;

    private final MessageSource messageSource;

    public ApiBranchFilter(TokenProvider tokenProvider, MessageSource messageSource, HandlerExceptionResolver resolver) {
        super(resolver);
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
    }

    @Override
    protected boolean doFilterInternal(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        ResponseDTO responseDTO = this.tokenProvider.verifyAndParseAccessToken(httpServletRequest);

        if (CODE_OK.equals(responseDTO.getMeta().getCode())) {
            Map<String, Object> jwtClaims = (Map) responseDTO.getData();
            request.setAttribute(USER, jwtClaims.get(JWTClaims.USER));

            //get tenant identifier
            if (jwtClaims.get(JWTClaims.USER) != null) {
                User user = (User) jwtClaims.get(JWTClaims.USER);
                String tenantIdentifier = this.tokenProvider.getBranchesByUser(user.getId(), httpServletRequest);
                log.info("BRANCH_NAME: " + tenantIdentifier);
                request.setAttribute(USER_BRANCH, tenantIdentifier);
            }

            Authentication authentication = (Authentication) jwtClaims.get(JWTClaims.AUTH);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return false;
        }

        if (null != responseDTO.getMeta() && !StringUtils.isEmpty(responseDTO.getMeta().getCode())) {
            throw new TokenException(responseDTO.getMeta().getCode(), responseDTO.getMeta().getMessage());
        }
        throw new SSDSAuthorizationException(HttpStatus.UNAUTHORIZED.name(), responseDTO.getMeta().getMessage());
    }


}
