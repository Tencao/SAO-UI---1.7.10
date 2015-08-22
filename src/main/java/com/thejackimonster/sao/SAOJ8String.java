package com.thejackimonster.sao;

public final class SAOJ8String {

	private SAOJ8String() {}

	public static final String join(String s0, String... s1) {
		final StringBuilder builder = new StringBuilder();
		
		if (s1.length > 1) {
			for (int i = 0; i < s1.length; i++) {
				if (i > 0) {
					builder.append(s0);
				}
				
				builder.append(s1[i]);
			}
		} else {
			builder.append(s0);
			
			if (s1.length == 1) {
				builder.append(s1[0]);
			}
		}
		
		return builder.toString();
	}

}
