package com.fpt.ssds.common.handler;

import com.fpt.ssds.aop.logging.dto.ErrorDTO;
import com.fpt.ssds.common.exception.*;
import com.fpt.ssds.service.ErrorMessageService;
import com.fpt.ssds.service.dto.ErrorMessageDto;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.ResponseUtils;
import com.fpt.ssds.utils.Utils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.fpt.ssds.constant.Constants.COMMON.REQUEST_ID;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    private final Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
    private final ErrorMessageService errorMessageService;

    @Autowired
    public ExceptionHandlerAdvice(ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
    }

    @ExceptionHandler(SSDSBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, SSDSBusinessException e) {
        // handle exception
        buildInfoLog(request, e);
        ResponseDTO responseDTO = getResponseDTO(request, e.getCode(), e.getMessage(), e.getData());
        if (CollectionUtils.isNotEmpty(e.getFieldErrorDTOS())) {
            responseDTO.setData(e.getFieldErrorDTOS());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
            body(responseDTO);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, Throwable e) {
        buildErrorLog(request, "Unknown", e.getMessage(), e.getStackTrace(), e.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
            body(createErrorBody(request, "Unknown", e.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, BindException e) {
        buildErrorLog(request, String.valueOf(HttpStatus.BAD_REQUEST.value()), e.getMessage(), e.getStackTrace(), e.getClass().getName());
        FieldError fieldError = e.getFieldError();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
            body(createErrorBody(request, String.valueOf(HttpStatus.BAD_REQUEST.value()), Objects.nonNull(fieldError) ? fieldError.getDefaultMessage() : "Unknown"));
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, Exception e) {
        buildErrorLog(request, "Unknown", e.getMessage(), e.getStackTrace(), e.getClass().getName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
            body(createErrorBody(request, "Unknown", e.getMessage()));
    }

    private void buildInfoLog(HttpServletRequest request, SSDSRuntimeException e) {
        buildInfoLog(request, e.getCode(), e.getMessage(), e.getStackTrace());
    }

    private void buildInfoLog(HttpServletRequest request, String code, String message, StackTraceElement[] stackTrace) {
        log.info(Utils.convertObjectToJsonString(buildLog(request, code, message, stackTrace, null)));
    }

    private ErrorDTO buildLog(HttpServletRequest request, String code, String message,
                              StackTraceElement[] stackTrace, String throwName) {
        String requestId = getAttributeAsString(request, REQUEST_ID);
        ErrorDTO dto = new ErrorDTO();
        dto.setRequestId(requestId);
        dto.setMethod(request.getMethod());
        dto.setUri(request.getRequestURI());
        dto.setQuery(request.getQueryString());
//        dto.setBody(getBody(request));
        dto.setCode(code);
        dto.setMessage(message);
        dto.setThrowName(throwName);
        dto.setStackTraces(stackTrace);
        return dto;
    }

    public static String getAttributeAsString(HttpServletRequest request, String key) {
        return (String) request.getAttribute(key);
    }

    private ResponseDTO getResponseDTO(HttpServletRequest request, String code, String message, List<?> data) {
        // handle exception
        ErrorMessageDto errorMessageDTO = errorMessageService.findByCode(code);
        if (Objects.nonNull(errorMessageDTO)) {
            data = Objects.isNull(data) ? new ArrayList<>() : data;
            String configMessage = new MessageFormat(errorMessageDTO.getMessageVi()).format(data.toArray());
            return createErrorBody(request, errorMessageDTO.getReturnCode(), configMessage);
        } else {
            return createErrorBody(request, code, message);
        }
    }

    private ResponseDTO createErrorBody(HttpServletRequest request, String error, String message) {
        return createErrorBody(request, ResponseUtils.buildError(error, message));
    }

    private ResponseDTO createErrorBody(HttpServletRequest request, SSDSRuntimeException e) {
        return createErrorBody(request, ResponseUtils.buildError(e));
    }

    private ResponseDTO createErrorBody(HttpServletRequest request, ResponseDTO body) {

        if (body.getMeta() != null) {
            String message = body.getMeta().getMessage();
            if (message == null) {
                message = "";
            }
            String requestId = getAttributeAsString(request, REQUEST_ID);
            if (requestId != null) {
                String fiveLastChar;
                if (requestId.length() > 5) {
                    fiveLastChar = requestId.substring(requestId.length() - 5);
                } else {
                    fiveLastChar = requestId;
                }

                message = message + String.format("(%s)", fiveLastChar);
            }
            body.getMeta().setMessage(message);

        }
        return body;
    }

    private void buildErrorLog(HttpServletRequest request, String code, String message, StackTraceElement[] stackTrace, String throwName) {
        log.error(Utils.convertObjectToJsonString(buildLog(request, code, message, stackTrace, throwName)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, AccessDeniedException e) {
        buildErrorLog(request, String.valueOf(HttpStatus.FORBIDDEN.value()), e.getMessage(), e.getStackTrace(), e.getClass().getName());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).
            body(createErrorBody(request, String.valueOf(HttpStatus.FORBIDDEN.value()), e.getMessage()));
    }

    @ExceptionHandler(OldTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, OldTokenException e) {
        buildInfoLog(request, e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(createErrorBody(request, e));
    }

    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, TokenException e) {
        buildInfoLog(request, e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
            body(getResponseDTO(request, e.getCode(), e.getMessage(), null));
    }

    @ExceptionHandler(SSDSAuthorizationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseDTO> handle(HttpServletRequest request, SSDSAuthorizationException e) {
        buildInfoLog(request, e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
            body(getResponseDTO(request, e.getCode(), e.getMessage(), null));
    }
}
