package com.java.school.online_video_training.config.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.java.school.online_video_training.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtils {

  @Value("${app.jwtSecret}")
  private String jwtSecret;

  @Value("${app.jwtExpirationMs}")
  private int jwtExpirationMs;

  public String generateJwtToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }
  
  public String generateToken(User user, String action) {
	    // Set the subject as the user's email and include the action (approve or reject)
	    return Jwts.builder()
	            .setSubject(user.getEmail())  // Subject is the user's email
	            .claim("action", action)      // The action (approve or reject)
	            .setIssuedAt(new Date())      // Set the issue date
	            .setExpiration(new Date(System.currentTimeMillis() + 86400000))  // Token expires in 24 hours
	            .signWith(key(), SignatureAlgorithm.HS256)
	            .compact();  // Create the token
	}
  
  public Claims getClaimsFromToken(String token) {
	    return Jwts.parser()
	               .setSigningKey(key())
	               .parseClaimsJws(token)
	               .getBody();
	}
 
  
//public String generateJwtToken(User user) {
//return generateJwtToken(user.getUsername());
//}
  
  private Key key() {
	  return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	  //return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  public String getUserNameFromJwtToken(String token) {
    return Jwts.parserBuilder()
    		.setSigningKey(key())
    		.build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
  }

  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
      return true;
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
    	log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
    	log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
    	log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }
}


