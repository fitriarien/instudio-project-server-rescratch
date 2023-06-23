package com.fitriarien.instudio.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
public class JwtToken implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;

	private final String token;
}