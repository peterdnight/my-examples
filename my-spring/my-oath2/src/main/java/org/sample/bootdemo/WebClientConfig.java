
package org.sample.bootdemo ;

import javax.inject.Inject ;
import javax.servlet.http.HttpServletRequest ;
import javax.servlet.http.HttpServletResponse ;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty ;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer ;
import org.springframework.context.annotation.Bean ;
import org.springframework.context.annotation.Configuration ;
import org.springframework.security.core.Authentication ;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient ;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository ;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository ;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction ;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction ;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository ;
import org.springframework.web.reactive.function.client.WebClient ;


@Configuration
@ConditionalOnProperty ( value = "my-examples.security.enabled" )
public class WebClientConfig {

	@Inject 
	SecurityConfiguration securityConfig ;

	@Bean
	WebClient webClientUser (	ClientRegistrationRepository clientRegistrationRepository,
							OAuth2AuthorizedClientRepository authorizedClientRepository ) {

		ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
			clientRegistrationRepository, authorizedClientRepository ) ;

		//oauth2.setDefaultOAuth2AuthorizedClient( true ) ;
		oauth2.setDefaultClientRegistrationId( securityConfig.getOathClientServiceName() );

		return WebClient.builder()
			.apply( oauth2.oauth2Configuration() )
			.build() ;

	}

	// https://stackoverflow.com/questions/55308918/spring-security-5-calling-oauth2-secured-api-in-application-runner-results-in-il/55454870#55454870
	@Bean
	WebClient webClientService (	ClientRegistrationRepository clientRegistrationRepository) {
		
		var oauth2Filter = new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
            new OAuth2AuthorizedClientRepository() {
                @Override
                public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String s,
                        Authentication authentication, HttpServletRequest httpServletRequest) {
                    return null;
                }

                @Override
                public void saveAuthorizedClient(OAuth2AuthorizedClient oAuth2AuthorizedClient,
                        Authentication authentication, HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {

                }

                @Override
                public void removeAuthorizedClient(String s, Authentication authentication,
                        HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

                }
            });



		//oauth2.setDefaultOAuth2AuthorizedClient( true ) ;
		oauth2Filter.setDefaultClientRegistrationId( securityConfig.getOathClientServiceName() );


		return WebClient.builder()
			.apply( oauth2Filter.oauth2Configuration() )
			.build() ;

	}
	
//	public class WebClientSecurityCustomizer implements WebClientCustomizer {
//
//		private ServerOAuth2AuthorizedClientExchangeFilterFunction securityExchangeFilterFunction;
//
//		public WebClientSecurityCustomizer(
//				ServerOAuth2AuthorizedClientExchangeFilterFunction securityExchangeFilterFunction) {
//			this.securityExchangeFilterFunction = securityExchangeFilterFunction;
//		}
//
//		@Override
//		public void customize(WebClient.Builder webClientBuilder) {
//			// Add security exchange filter function to Builder filters list
//			webClientBuilder.filters((filterFunctions) -> {
//				if (!filterFunctions.contains(this.securityExchangeFilterFunction)) {
//					filterFunctions.add(0, this.securityExchangeFilterFunction);
//				}
//			});
//		}
//	}
}
