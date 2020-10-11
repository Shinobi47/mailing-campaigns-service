package com.benayed.mailing.campaigns.constants;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class Constants {

	public final static List<String> ILLEGAL_CUSTOM_HEADERS_CHARACTERS = ImmutableList.<String>of(";", ":"); // used as delimiters in persistence

}
