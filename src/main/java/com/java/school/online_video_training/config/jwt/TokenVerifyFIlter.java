package com.java.school.online_video_training.config.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.java.school.online_video_training.exception.ApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenVerifyFIlter extends OncePerRequestFilter{
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			filterChain.doFilter(request, response);
			return;
		}
		String authorizationHeader = request.getHeader("Authorization");
		if(Objects.isNull(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = authorizationHeader.replace("Bearer ", "");
		String secretKey = "sddfasfsdfsfsfdddddddddddddddddsddfasfsdfsfsfddddddddddddddddd";
		try {
			 Jws<Claims> claimsJws = Jwts.parserBuilder()
					 .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
					 .build()
					 .parseClaimsJws(token);
			 Claims body = claimsJws.getBody();
			 String username = body.getSubject();
			 List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");
					 
			 Set<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
					 .map(x -> new SimpleGrantedAuthority(x.get("authority")))
					 .collect(Collectors.toSet());
					 
			Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		}catch (ExpiredJwtException e) {
			log.info(e.getMessage());
			e.printStackTrace();
			throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
		catch (SignatureException e) {
	        log.error("JWT Signature Verification Failed: " + e.getMessage());
	        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token signature");
	    } catch (JwtException e) {
	        log.error("JWT Processing Error: " + e.getMessage());
	        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token");
	    }
	}
}
