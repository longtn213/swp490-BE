package com.fpt.ssds.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpt.ssds.aop.logging.dto.LogRequestDto;
import com.fpt.ssds.common.handler.dto.RequestBodyData;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.service.dto.MetadataDTO;
import com.fpt.ssds.service.dto.ResponseDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.*;

import static com.fpt.ssds.constant.Constants.COMMON.*;
import static com.fpt.ssds.utils.HTTPUtils.getAttributeAsString;
import static com.fpt.ssds.utils.HTTPUtils.getBody;

@Aspect
@Component
@Order(value = 1)
public class ApiLoggingHandler {
    private static final String[] ignoreHeaderKeys = new String[]{"Authorization", "Accept", "Accept-Encoding", "Connection", "content-type", "Cookie", "Host", "User-Agent"};

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final Class<?>[] parameterIgnoreClasses = new Class[]{HttpServletRequest.class};
    private final Class<?>[] parameterIgnoreAnnotations = new Class[]{PathVariable.class};

    @Autowired
    private HttpServletRequest request;

    private @Autowired HttpServletResponse response;

    @Autowired
    private Jackson2ObjectMapperBuilder mapperBuilder;

    @Value("${wes.debug-response:false}")
    private boolean debugResponse;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {

    }

    @Pointcut("execution(* *.*(..))")
    protected void allMethod() {
    }

    @Pointcut("execution(public * *(..))")
    protected void loggingPublicOperation() {
    }

    @Pointcut("execution(* *.*(..))")
    protected void loggingAllOperation() {
    }

    /*@Around("controller() && loggingPublicOperation()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String id = getAttributeAsString(request, REQUEST_ID);
        long startTime = System.currentTimeMillis();
        Object responseData = joinPoint.proceed();
        long execTime = System.currentTimeMillis() - startTime;
        String code = "0";
        try {
            ResponseEntity<ResponseDTO> responseEntity = (ResponseEntity<ResponseDTO>) responseData;
            if (Objects.nonNull(responseEntity)) {
                ResponseDTO responseDTO = responseEntity.getBody();
                if (Objects.nonNull(responseDTO)) {
                    MetadataDTO metadataDTO = responseDTO.getMeta();
                    if (Objects.nonNull(metadataDTO)) {
                        code = metadataDTO.getCode();
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        logRequest(request, code, execTime, getRequestBody(joinPoint), responseData, debugResponse);
        response.addHeader(Constants.COMMON.HEADER_REQUEST_ID, id);
        return responseData;
    }
*/
    private void logRequest(HttpServletRequest request, String statusCode, long latency, String requestBody, Object responseBody, boolean debugResponse) {
        String requestId = getAttributeAsString(request, REQUEST_ID);
        LogRequestDto dto = new LogRequestDto();
        dto.setRequestId(requestId);
        dto.setMethod(request.getMethod());
        dto.setUri(request.getRequestURI());
        dto.setQuery(request.getQueryString());
        dto.setBody(getBody(requestBody));
        dto.setCode(statusCode);
        dto.setLatency(latency);
        dto.setHeader(getHeader(request));
        dto.setHttpStatusCode(response.getStatus());
        if (responseBody != null && debugResponse) {
            dto.setResponseData(toJson(responseBody));
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            log.info(mapper.writeValueAsString(dto));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String toJson(Object o) {
        try {
            ObjectMapper mapper = mapperBuilder.build();
            mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    private String getRequestBody(ProceedingJoinPoint point) {

        RequestBodyData requestBodyData = new RequestBodyData();
        requestBodyData.setParameters(new ArrayList<>());
        MethodSignature signature = (MethodSignature) point.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] args = point.getArgs();
        for (int index = 0; index < parameters.length; index++) {
            Parameter parameter = parameters[index];
            boolean isIgnore = isIgnoreParameter(parameter.getType());
            ;
            if (!isIgnore) {
                Annotation[] annotations = parameter.getDeclaredAnnotations();
                if (annotations != null && annotations.length > 0) {
                    for (Annotation annotation : annotations) {
                        if (isIgnoreParameter(annotation)) {
                            isIgnore = true;
                            break;
                        }
                    }
                }
            }
            if (!isIgnore) {
                requestBodyData.getParameters().add(args[index]);
            }
        }
        return toJson(requestBodyData);
    }

    private boolean isIgnoreParameter(Class<?> type) {
        for (Class<?> clazz : this.parameterIgnoreClasses) {
            if (clazz.isAssignableFrom(type) || MultipartFile.class.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIgnoreParameter(Annotation annotation) {

        for (Class<?> clazz : this.parameterIgnoreAnnotations) {
            if (clazz.isInstance(annotation)) {
                return true;
            }
        }
        return false;
    }

    private static String[] getHeader(HttpServletRequest request) {
        Enumeration<String> keys = request.getHeaderNames();
        List<String> stringList = new ArrayList<>();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (isIgnoreHeader(key)) {
                continue;
            }
            String value = request.getHeader(key);
            stringList.add(String.format("%s:%s", key, value));
        }
        return stringList.toArray(new String[0]);
    }

    private static boolean isIgnoreHeader(String key) {
        for (String header : ignoreHeaderKeys) {
            if (header.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }
}
