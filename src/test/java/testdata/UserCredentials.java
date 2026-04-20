package testdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserCredentials(String adminUsername, String adminPassword, String invalidPassword) {
}
