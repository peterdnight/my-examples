package org.sample.bootdemo ;

import org.slf4j.Logger ;
import org.slf4j.LoggerFactory ;
import org.springframework.beans.BeansException ;
import org.springframework.beans.factory.config.BeanPostProcessor ;
import org.springframework.boot.autoconfigure.SpringBootApplication ;
import org.springframework.context.annotation.Bean ;

@SpringBootApplication
public class DemoApplication {

	static Logger logger = LoggerFactory.getLogger( DemoApplication.class ) ;

	public static void main ( String[] args ) {

		logger.debug( Helpers.testHeader( ) ) ;

		// SpringApplication.run(DemoApplication.class, args);
		Helpers.run( DemoApplication.class, args ) ;

	}

	@Bean
	public MyBeanPostProcessor myBeanPostProcessor ( ) {

		return new MyBeanPostProcessor( ) ;

	}

	static class MyBeanPostProcessor implements BeanPostProcessor {

		@Override
		public Object postProcessBeforeInitialization ( Object bean , String beanName )
			throws BeansException {
//		    if (bean instanceof MySpringBean) {

//		      System.out.println("--- postProcessBeforeInitialization executed ---");
//		    }

			System.out.println( "--- before:     " + bean.getClass( ).getName( ) ) ;

			return bean ;

		}

		@Override
		public Object postProcessAfterInitialization ( Object bean , String beanName )
			throws BeansException {

//		    if (bean instanceof MySpringBean) {
//		      System.out.println("--- postProcessAfterInitialization executed ---");
//		    }
			System.out.println( "--- after:     " + bean.getClass( ).getName( ) ) ;

			return bean ;

		}

	}

}
