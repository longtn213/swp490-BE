package com.fpt.ssds.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fpt.ssds.common.exception.SSDSAuthorizationException;
import com.fpt.ssds.common.exception.TokenException;
import com.fpt.ssds.constant.Constants;
import com.fpt.ssds.constant.JWTClaims;
import com.fpt.ssds.domain.Branch;
import com.fpt.ssds.repository.BranchRepository;
import com.fpt.ssds.repository.UserRepository;
import com.fpt.ssds.service.BranchService;
import com.fpt.ssds.service.dto.BranchDto;
import com.fpt.ssds.service.dto.MetadataDTO;
import com.fpt.ssds.service.dto.ResponseDTO;
import com.fpt.ssds.utils.HTTPUtils;
import com.fpt.ssds.utils.ResponseUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import tech.jhipster.config.JHipsterProperties;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import static com.fpt.ssds.constant.ErrorConstants.*;

@Component
@Slf4j
public class TokenProvider {

    private final JHipsterProperties jHipsterProperties;

    private RSAPublicKey rsaPublicKey;

    @Value("${security.authentication.public-key-file}")
    private String publicKeyFile;

    private final MessageSource messageSource;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private BranchService branchService;

    public TokenProvider(JHipsterProperties jHipsterProperties, MessageSource messageSource) {
        this.jHipsterProperties = jHipsterProperties;
        this.messageSource = messageSource;
    }

    @PostConstruct
    public void init() {
        this.rsaPublicKey = readPublicKey(publicKeyFile);
    }

    private RSAPublicKey readPublicKey(String publicKeyfile) {
        try {
            Resource publicFileStream = resourceLoader.getResource(publicKeyfile);
//            File file = ResourceUtils.getFile(publicKeyfile);
            String publicKeyContent = IOUtils.toString(publicFileStream.getInputStream(), StandardCharsets.UTF_8);
            String publicKeyPEM = publicKeyContent
                .replace("-----BEGIN CERTIFICATE-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END CERTIFICATE-----", "");

            byte[] encoded = Base64.getMimeDecoder().decode(publicKeyPEM);

            InputStream certStream = new ByteArrayInputStream(encoded);
            Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(certStream);
            PublicKey key = cert.getPublicKey();
            return (RSAPublicKey) key;
        } catch (CertificateException | IOException e) {
            log.error("Cannot read public key. Reason: " + e.getMessage());
            return null;
        }
    }

    public MetadataDTO validateTokenWithPublicKey(String authToken) {
        MetadataDTO metadataDTO = null;
        try {
            Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, null);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(authToken);
        } catch (TokenExpiredException ex) {
            throw new TokenException(TOKEN_EXPIRED);
        } catch (JWTVerificationException e) {
            log.info("Invalid API token.");
            log.trace("Invalid API token trace.", e);
            throw new TokenException(TOKEN_INVALID);
        }
        return metadataDTO;
    }

    @Transactional
    public ResponseDTO verifyAndParseAccessToken(HttpServletRequest httpServletRequest) {
        String token = HTTPUtils.resolveToken(httpServletRequest);
        if (StringUtils.isEmpty(token)) {
            throw new TokenException(TOKEN_INVALID);
        }

        ResponseDTO responseDTO = new ResponseDTO();
        MetadataDTO metadataDTO = validateTokenWithPublicKey(token);
        if (null != metadataDTO) {
            responseDTO.setMeta(metadataDTO);
            return responseDTO;
        }

        try {
            Map<String, Object> map = new HashMap<String, Object>();
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Claim> claims = jwt.getClaims();
            String refId = claims.get("sub").asString().replace("auth0|", "");
            if (refId == null || refId.isEmpty()) {
                throw new SSDSAuthorizationException(USER_NOT_EXIST);
            }
            List<com.fpt.ssds.domain.User> users = userRepository.findByRefId(refId);
            if (users == null || users.isEmpty()) {
                throw new SSDSAuthorizationException(USER_NOT_EXIST);
            }
            if (users.size() > 1) {
                log.warn("[PUBLIC] - User have been duplicated.");
            }
            com.fpt.ssds.domain.User user = users.get(0);
            if (!user.getIsActive()) {
                throw new SSDSAuthorizationException(USER_INACTIVE);
            }

            map.put(JWTClaims.USER, user);
            responseDTO.setMeta(new MetadataDTO(ResponseUtils.CODE_OK, ResponseUtils.MESSAGE_OK));
            responseDTO.setData(map);

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(user.getRole().getCode()));
            User principal = new User(user.getUsername(), "", authorities);
            map.put(JWTClaims.AUTH, new UsernamePasswordAuthenticationToken(principal, token, authorities));
            return responseDTO;
        } catch (JWTDecodeException e) {
            String errorMessage = "Failed to decode jwt with rsa. Reason: " + e.getMessage();
            log.debug(errorMessage);
            throw new TokenException(TOKEN_INVALID);
        }

    }

    @Transactional
    public ResponseDTO verifyAndParseAccessTokenExternal(HttpServletRequest httpServletRequest) {
        String token = HTTPUtils.resolveToken(httpServletRequest);
        if (StringUtils.isEmpty(token)) {
            throw new TokenException(TOKEN_INVALID);
        }

        ResponseDTO responseDTO = new ResponseDTO();
        MetadataDTO metadataDTO = validateTokenWithPublicKey(token);
        if (null != metadataDTO) {
            responseDTO.setMeta(metadataDTO);
            return responseDTO;
        }

        try {
            Map<String, Object> map = new HashMap<String, Object>();
            DecodedJWT jwt = JWT.decode(token);
            Map<String, Claim> claims = jwt.getClaims();
            String refId = claims.get("sub").asString();
            if (refId == null || refId.isEmpty()) {
                throw new SSDSAuthorizationException(USER_INVALID);
            }
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            User principal = new User("ext_user", "", authorities);
            map.put(JWTClaims.AUTH, new UsernamePasswordAuthenticationToken(principal, token, authorities));
            responseDTO.setData(map);
            responseDTO.setMeta(new MetadataDTO(ResponseUtils.CODE_OK, ResponseUtils.MESSAGE_OK));
            return responseDTO;
        } catch (JWTDecodeException e) {
            String errorMessage = "Failed to decode jwt with rsa. Reason: " + e.getMessage();
            log.debug(errorMessage);
            throw new TokenException(TOKEN_INVALID);
        }

    }

    @Transactional
    public String getBranchesByUser(Long userId, HttpServletRequest httpServletRequest) {
        /*com.onemount.wes.domain.master.User user = userRepository.getOne(userId);*/
        List<Branch> branches = branchService.findByUserId(userId);
        String dcCode = "";
        if (branches.size() > 1) {
            dcCode = branches.get(0).getCode();
        }
        String branchIdentifier = null;
        if (CollectionUtils.isEmpty(branches)) {
            throw new SSDSAuthorizationException(USER_DOES_NOT_BELONG_TO_ANY_BRANCH);
        } else {
            dcCode = Objects.isNull(HTTPUtils.getRequestHeaderAsString(httpServletRequest, Constants.REQUEST_HEADER.BRANCH_CODE_HEADER)) ? dcCode : HTTPUtils.getRequestHeaderAsString(httpServletRequest, Constants.REQUEST_HEADER.BRANCH_CODE_HEADER);
            String branchCode = Objects.nonNull(dcCode) ? dcCode : HTTPUtils.getRequestHeaderAsString(httpServletRequest, Constants.REQUEST_HEADER.BRANCH_CODE_HEADER);
            if (StringUtils.isEmpty(branchCode)) {
                if (branches.size() == 1) {
                    Optional<Branch> branchOpt = branches.stream().findFirst();
                    if (branchOpt.isPresent()) {
                        branchIdentifier = branchOpt.get().getName();
                    }
                } else {
                    throw new SSDSAuthorizationException(WAREHOUSE_CODE_IS_REQUIRED);
                }
            } else {
                List<Branch> branchByCode = branches.stream().filter(warehouse -> warehouse.getCode().equals(branchCode)).collect(Collectors.toList());
                if (branchByCode.isEmpty()) {
                    throw new SSDSAuthorizationException(USER_DOES_NOT_BELONG_TO_WAREHOUSE);
                }
                branchIdentifier = branchByCode.get(0).getName();
            }
        }
        return branchIdentifier;
    }

}
