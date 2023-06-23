package com.fitriarien.instudio.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoginUserRequest implements Serializable {

	private static final long serialVersionUID = 5926468583005150707L;

	@NotBlank
	@Size(max = 100)
	private String username;

	@NotBlank
	private String password;

}