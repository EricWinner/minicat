package org.mcxiaoke.fancooker.auth2;

public class RequestToken extends OAuthToken {
	private static final long serialVersionUID = -8214365845469757952L;

	public RequestToken(String token, String tokenSecret) {
		super(token, tokenSecret);
	}
	
	public RequestToken(String content) {
		super(content);
	}
}
