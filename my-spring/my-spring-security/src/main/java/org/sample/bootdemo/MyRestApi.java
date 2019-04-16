package org.sample.bootdemo ;

import org.springframework.web.bind.annotation.GetMapping ;
import org.springframework.web.bind.annotation.RequestMapping ;
import org.springframework.web.bind.annotation.RestController ;

@RestController
@RequestMapping ( MyRestApi.URI_BASE )
public class MyRestApi {

	public final static String	URI_BASE	= "/api" ;
	private final static String	URI_HI		= "/hi" ;
	public final static String	URI_API_HI		= URI_BASE + URI_HI ;

	@GetMapping ( URI_HI )
	public String hi () {
		return "hi" ;
	}

}
