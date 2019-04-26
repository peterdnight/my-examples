
package org.sample.bootdemo ;

import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository ;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository ;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction ;
import org.springframework.web.reactive.function.client.WebClient ;

@Configuration
public class WebClientConfig {

	public static final String KEYCLOAK_CLIENT_ROLE = "keycloak-client-role" ;

	@Bean
	WebClient webClient (	ClientRegistrationRepository clientRegistrationRepository,
							OAuth2AuthorizedClientRepository authorizedClientRepository ) {

		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
			clientRegistrationRepository, authorizedClientRepository ) ;

		oauth2.setDefaultOAuth2AuthorizedClient( true ) ;
		oauth2.setDefaultClientRegistrationId( KEYCLOAK_CLIENT_ROLE );

		return WebClient.builder()
			.apply( oauth2.oauth2Configuration() )
			.build() ;

	}
}
