/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.didichuxing.daedalus.util.curl;

import com.google.common.base.Splitter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Basic cURL command parser that handles:
 *
 * @since 5.1
 */
public class BasicCurlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicCurlParser.class);

    private static final int METHOD_OPT = 'X';// $NON-NLS-1$
    private static final int COMPRESSED_OPT = 'c';// $NON-NLS-1$
    private static final int HEADER_OPT = 'H';// $NON-NLS-1$
    private static final int DATA_OPT = 'd';// $NON-NLS-1$
    private static final int DATA_ASCII_OPT = "data-ascii".hashCode();// $NON-NLS-1$
    private static final int DATA_BINARY_OPT = "data-binary".hashCode();// NOSONAR
    private static final int DATA_URLENCODE_OPT = "data-urlencode".hashCode();// NOSONAR
    private static final int DATA_RAW_OPT = "data-raw".hashCode();// NOSONAR
    private static final int FORM_OPT = 'F';// $NON-NLS-1$
    private static final int FORM_STRING_OPT = "form".hashCode();// $NON-NLS-1$
    private static final int USER_AGENT_OPT = 'A';// $NON-NLS-1$
    private static final int CONNECT_TIMEOUT_OPT = "connect-timeout".hashCode();// $NON-NLS-1$
    private static final int COOKIE_OPT = 'b';// $NON-NLS-1$
    private static final int USER_OPT = 'u';// $NON-NLS-1$
    private static final int BASIC_OPT = "basic".hashCode();// NOSONAR
    private static final int DIGEST_OPT = "digest".hashCode();// NOSONAR
    private static final int CERT_OPT = 'E';// $NON-NLS-1$
    private static final int CAFILE_OPT = "cacert".hashCode();// $NON-NLS-1$
    private static final int CAPATH_OPT = "capath".hashCode();// $NON-NLS-1$
    private static final int CIPHERS_OPT = "ciphers".hashCode();// $NON-NLS
    private static final int CERT_STATUS_OPT = "cert-status".hashCode();// $NON-NLS-1$-1$
    private static final int CERT_TYPE_OPT = "cert-type".hashCode();// $NON-NLS-1$-1$
    private static final int KEY_OPT = "key".hashCode();// $NON-NLS-1$-1$
    private static final int KEY_TYPE_OPT = "key-type".hashCode();// $NON-NLS-1$-1$
    private static final int GET_OPT = 'G';// $NON-NLS-1$
    private static final int DNS_OPT = "dns-servers".hashCode();// $NON-NLS-1$
    private static final int NO_KEEPALIVE_OPT = "no-keepalive".hashCode();// $NON-NLS-1$
    private static final int REFERER_OPT = 'e';// $NON-NLS-1$
    private static final int LOCATION_OPT = 'L';// $NON-NLS-1$
    private static final int INCLUDE_OPT = 'i';// $NON-NLS-1$
    private static final int HEAD_OPT = 'I';// $NON-NLS-1$
    private static final int PROXY_OPT = 'x';// $NON-NLS-1$
    private static final int PROXY_USER_OPT = 'U';// $NON-NLS-1$
    private static final int PROXY_NTLM_OPT = "proxy-ntlm".hashCode();// $NON-NLS-1$
    private static final int PROXY_NEGOTIATE_OPT = "proxy-negotiate".hashCode();// $NON-NLS-1$
    private static final int KEEPALIVETILE_OPT = "keepalive-time".hashCode();// $NON-NLS-1$
    private static final int MAX_TIME_OPT = 'm';// $NON-NLS-1$
    private static final int OUTPUT_OPT = 'o';// $NON-NLS-1$
    private static final int CREATE_DIRS_OPT = "create-dir".hashCode();// $NON-NLS-1$
    private static final int INSECURE_OPT = 'k';// $NON-NLS-1$
    private static final int RAW_OPT = "raw".hashCode();// $NON-NLS-1$
    private static final int INTERFACE_OPT = "interface".hashCode();// $NON-NLS-1$
    private static final int DNS_RESOLVER_OPT = "resolve".hashCode();// $NON-NLS-1$
    private static final int LIMIT_RATE_OPT = "limit-rate".hashCode();// $NON-NLS-1$
    private static final int MAX_REDIRS_OPT = "max-redirs".hashCode();// $NON-NLS-1$
    private static final int NOPROXY_OPT = "noproxy".hashCode();// $NON-NLS-1$
    private static final int URL_OPT = "url".hashCode(); // $NON-NLS-1$
    private static final int VERBOSE_OPT = 'v';// $NON-NLS-1$
    private static final int SILENT_OPT = 's';// $NON-NLS-1$

    private static final List<Integer> AUTH_OPT = Arrays.asList(BASIC_OPT, DIGEST_OPT);
    private static final List<Integer> SSL_OPT = Arrays.asList(CAFILE_OPT, CAPATH_OPT, CERT_OPT, CIPHERS_OPT,
            CERT_STATUS_OPT, CERT_TYPE_OPT, KEY_OPT, KEY_TYPE_OPT);
    private static final List<Integer> DATAS_OPT = Arrays.asList(DATA_OPT, DATA_ASCII_OPT, DATA_BINARY_OPT,
            DATA_URLENCODE_OPT, DATA_RAW_OPT);
    private static final List<Integer> FORMS_OPT = Arrays.asList(FORM_OPT, FORM_STRING_OPT);
    private static final List<Integer> IGNORE_OPTIONS_OPT = Arrays.asList(OUTPUT_OPT, CREATE_DIRS_OPT, RAW_OPT,
            INCLUDE_OPT, KEEPALIVETILE_OPT, VERBOSE_OPT, SILENT_OPT);
    private static final List<Integer> NOSUPPORT_OPTIONS_OPT = Arrays.asList(PROXY_NTLM_OPT, PROXY_NEGOTIATE_OPT);
    private static final List<Integer> PROPERTIES_OPT = Arrays.asList(MAX_REDIRS_OPT);
    private static final List<String> DYNAMIC_COOKIES = Arrays.asList("PHPSESSID", "JSESSIONID", "ASPSESSIONID",
            "connect.sid");// $NON-NLS-1$

    public static final class Request {
        private boolean compressed;
        private String url;
        private String urlNoQuery;
        private Map<String, String> headers = new LinkedHashMap<>();
        private String method = "GET";
        private String postData;
        private String interfaceName;
        private double connectTimeout = -1;
        private String cookies = "";
        private String cookieInHeaders = "";
        private String filepathCookie = "";
        private Authorization authorization = new Authorization();
        private String caCert = "";
        private Map<String, String> formData = new LinkedHashMap<>();
        private Map<String, String> formStringData = new LinkedHashMap<>();
        private Map<String, String> urlParams = new LinkedHashMap<>();
        private Set<String> dnsServers = new HashSet<>();
        private boolean isKeepAlive = true;
        private double maxTime = -1;
        private List<String> optionsIgnored = new ArrayList<>();
        private List<String> optionsNoSupport = new ArrayList<>();
        private List<String> optionsInProperties = new ArrayList<>();
        private Map<String, String> proxyServer = new LinkedHashMap<>();
        private String dnsResolver;
        private int limitRate = 0;
        private String noproxy;
        private static final List<String> HEADERS_TO_IGNORE = Arrays.asList("Connection", "Host");// $NON-NLS-1$
        private static final int ONE_KILOBYTE_IN_CPS = 1024;

        public Request() {
            super();
        }


        public String getMethod() {
            return method;
        }

        public String getUrlNoQuery() {
            return urlNoQuery;
        }


        public void setMethod(String method) {
            this.method = method;
        }

        /**
         * @param value the post data
         */
        public void setPostData(String value) {
            this.postData = value;
        }

        /**
         * @return the postData
         */
        public String getPostData() {
            return postData;
        }

        /**
         * @return the compressed
         */
        public boolean isCompressed() {
            return compressed;
        }

        /**
         * @param compressed the compressed to set
         */
        public void setCompressed(boolean compressed) {
            this.compressed = compressed;
        }

        /**
         * @param name  the field of Header
         * @param value the value of Header
         */
        public void addHeader(String name, String value) {
            if ("COOKIE".equalsIgnoreCase(name)) {
                this.cookieInHeaders = value;
            } else if (!HEADERS_TO_IGNORE.contains(name)) {
                headers.put(name, value);
            }
        }

        /**
         * <em>Note that {@link #setCookieInHeaders(String)} will have to be called first to set the cookies from headers.</em>
         *
         * @return the extracted cookies in the earlier set headers
         */
        public List<Cookie> getCookieInHeaders() {
            return Collections.unmodifiableList(stringToCookie(cookieInHeaders));
        }

        /**
         * @param cookieInHeaders the cookieInHeaders to set
         */
        public void setCookieInHeaders(String cookieInHeaders) {
            this.cookieInHeaders = cookieInHeaders;
        }

        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @param url the url to set
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * @return the headers
         */
        public Map<String, String> getHeaders() {
            return Collections.unmodifiableMap(this.headers);
        }

        /**
         * @return the list of options which are ignored
         */
        public List<String> getOptionsInProperties() {
            return Collections.unmodifiableList(this.optionsInProperties);
        }

        /**
         * @param option the option
         */
        public void addOptionsInProperties(String option) {
            this.optionsInProperties.add(option);
        }

        /**
         * @return the maximum transfer rate
         */
        public int getLimitRate() {
            return limitRate;
        }

        /**
         * Transform the bandwidth to cps value (byte/s), cps =
         * bandwidth*1024/8, the unit of bandwidth in JMeter is measured in kbit/s. And
         * the speed in Curl is measured in bytes/second, so the conversion formula is
         * cps=limitRate*1024
         *
         * @param limitRate the maximum transfer rate
         */
        public void setLimitRate(String limitRate) {
            String unit = limitRate.substring(limitRate.length() - 1, limitRate.length()).toLowerCase();
            int value = Integer.parseInt(limitRate.substring(0, limitRate.length() - 1).toLowerCase());
            switch (unit) {
                case "k":
                    this.limitRate = value * ONE_KILOBYTE_IN_CPS;
                    break;
                case "m":
                    this.limitRate = value * ONE_KILOBYTE_IN_CPS * 1000;
                    break;
                case "g":
                    this.limitRate = value * ONE_KILOBYTE_IN_CPS * 1000000;
                    break;
                default:
                    break;
            }
        }

        /**
         * @return this list of hosts which don't use proxy
         */
        public String getNoproxy() {
            return noproxy;
        }

        /**
         * Set the list of hosts which don't use proxy
         *
         * @param noproxy list of hosts that should not be used through the proxy
         */
        public void setNoproxy(String noproxy) {
            this.noproxy = noproxy;
        }

        /**
         * @return the DNS resolver
         */
        public String getDnsResolver() {
            return dnsResolver;
        }

        /**
         * set DNS resolver
         *
         * @param dnsResolver name of the DNS resolver to use
         */
        public void setDnsResolver(String dnsResolver) {
            this.dnsResolver = dnsResolver;
        }

        /**
         * @return the interface name to perform an operation
         */
        public String getInterfaceName() {
            return interfaceName;
        }

        /**
         * @param interfaceName the name of interface
         */
        public void setInterfaceName(String interfaceName) {
            this.interfaceName = interfaceName;
        }

        /**
         * @return the list of options which are ignored
         */
        public List<String> getOptionsIgnored() {
            return Collections.unmodifiableList(this.optionsIgnored);
        }

        /**
         * @param option option is ignored
         */
        public void addOptionsIgnored(String option) {
            this.optionsIgnored.add(option);
        }

        /**
         * @return the list of options which are not supported by JMeter
         */
        public List<String> getOptionsNoSupport() {
            return Collections.unmodifiableList(this.optionsNoSupport);
        }

        /**
         * @param option option is not supported
         */
        public void addOptionsNoSupport(String option) {
            this.optionsNoSupport.add(option);
        }

        /**
         * @return the map of proxy server
         */
        public Map<String, String> getProxyServer() {
            return Collections.unmodifiableMap(this.proxyServer);
        }

        /**
         * @param key   key
         * @param value value
         */
        public void setProxyServer(String key, String value) {
            this.proxyServer.put(key, value);
        }

        /**
         * @return if the Http request keeps alive
         */
        public boolean isKeepAlive() {
            return isKeepAlive;
        }

        /**
         * @param isKeepAlive set if the Http request keeps alive
         */
        public void setKeepAlive(boolean isKeepAlive) {
            this.isKeepAlive = isKeepAlive;
        }

        /**
         * @return the list of DNS server
         */
        public Set<String> getDnsServers() {
            return Collections.unmodifiableSet(this.dnsServers);
        }

        /**
         * @param dnsServer set the list of DNS server
         */
        public void addDnsServers(String dnsServer) {
            this.dnsServers.add(dnsServer);
        }

        /**
         * @return the map of form data
         */
        public Map<String, String> getFormStringData() {
            return Collections.unmodifiableMap(this.formStringData);
        }

        public Map<String, String> getUrlParams() {
            return Collections.unmodifiableMap(this.urlParams);
        }

        /**
         * @param key   the key of form data
         * @param value the value of form data
         */
        public void addFormStringData(String key, String value) {
            formStringData.put(key, value);
        }

        /**
         * @return the map of form data
         */
        public Map<String, String> getFormData() {
            return Collections.unmodifiableMap(this.formData);
        }

        /**
         * @param key   the key of form data
         * @param value the value of form data
         */
        public void addFormData(String key, String value) {
            formData.put(key, value);
        }

        /**
         * @return the certificate of the CA
         */
        public String getCaCert() {
            return caCert;
        }

        /**
         * the options which work for SSL
         *
         * @param caCert cert of the CA
         */
        public void setCaCert(String caCert) {
            this.caCert = caCert;
        }

        /**
         * @return the authorization
         */
        public Authorization getAuthorization() {
            return authorization;
        }

        /**
         * @return the connection time out
         */
        public double getConnectTimeout() {
            return connectTimeout;
        }

        /**
         * @param connectTimeout the connection time out
         */
        public void setConnectTimeout(double connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        /**
         * @return the max time of connection
         */
        public double getMaxTime() {
            return maxTime;
        }

        /**
         * @param maxTime max time of connection
         */
        public void setMaxTime(double maxTime) {
            this.maxTime = maxTime;
        }

        /**
         * @return the filepathCookie
         */
        public String getFilepathCookie() {
            return filepathCookie;
        }

        /**
         * @param filepathCookie the filepathCookie to set
         */
        public void setFilepathCookie(String filepathCookie) {
            this.filepathCookie = filepathCookie;
        }

        /**
         * <em>Note that {@link #setCookies(String)} will have to be called first to set the cookies</em>
         *
         * @return the cookies
         */
        public List<Cookie> getCookies() {
            return Collections.unmodifiableList(stringToCookie(cookies));
        }

        /**
         * @param cookies the cookies to set
         */
        public void setCookies(String cookies) {
            this.cookies = cookies;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Request [compressed=");
            builder.append(compressed);
            builder.append(", url=");
            builder.append(url);
            builder.append(", method=");
            builder.append(method);
            builder.append(", headers=");
            builder.append(headers);
            builder.append("]");
            return builder.toString();
        }
    }

    private static final CLOptionDescriptor D_COMPRESSED_OPT =
            new CLOptionDescriptor("compressed", CLOptionDescriptor.ARGUMENT_DISALLOWED, COMPRESSED_OPT,
                    "Request compressed response (using deflate or gzip)");
    private static final CLOptionDescriptor D_HEADER_OPT =
            new CLOptionDescriptor("header", CLOptionDescriptor.ARGUMENT_REQUIRED | CLOptionDescriptor.DUPLICATES_ALLOWED, HEADER_OPT,
                    "Pass custom header LINE to server");
    private static final CLOptionDescriptor D_METHOD_OPT =
            new CLOptionDescriptor("request", CLOptionDescriptor.ARGUMENT_REQUIRED, METHOD_OPT,
                    "Pass custom header LINE to server");
    private static final CLOptionDescriptor D_DATA_OPT =
            new CLOptionDescriptor("data", CLOptionDescriptor.ARGUMENT_REQUIRED, DATA_OPT,
                    "HTTP POST data");
    private static final CLOptionDescriptor D_DATA_ASCII_OPT = new CLOptionDescriptor("data-ascii",
            CLOptionDescriptor.ARGUMENT_REQUIRED, DATA_ASCII_OPT, "HTTP POST ascii data ");
    private static final CLOptionDescriptor D_DATA_BINARY_OPT = new CLOptionDescriptor("data-binary",
            CLOptionDescriptor.ARGUMENT_REQUIRED, DATA_BINARY_OPT, "HTTP POST binary data ");
    private static final CLOptionDescriptor D_DATA_URLENCODE_OPT = new CLOptionDescriptor("data-urlencode",
            CLOptionDescriptor.ARGUMENT_REQUIRED, DATA_URLENCODE_OPT, "HTTP POST url encoding data ");
    private static final CLOptionDescriptor D_DATA_RAW_OPT = new CLOptionDescriptor("data-raw",
            CLOptionDescriptor.ARGUMENT_REQUIRED, DATA_RAW_OPT, "HTTP POST url allowed '@' ");
    private static final CLOptionDescriptor D_FORM_OPT = new CLOptionDescriptor("form",
            CLOptionDescriptor.ARGUMENT_REQUIRED | CLOptionDescriptor.DUPLICATES_ALLOWED, FORM_OPT,
            "HTTP POST form data allowed '@' and ';Type='");
    private static final CLOptionDescriptor D_FORM_STRING_OPT = new CLOptionDescriptor("form-string",
            CLOptionDescriptor.ARGUMENT_REQUIRED | CLOptionDescriptor.DUPLICATES_ALLOWED, FORM_STRING_OPT,
            "HTTP POST form data  ");
    private static final CLOptionDescriptor D_USER_AGENT_OPT = new CLOptionDescriptor("user-agent",
            CLOptionDescriptor.ARGUMENT_REQUIRED, USER_AGENT_OPT, "The User-Agent string");
    private static final CLOptionDescriptor D_CONNECT_TIMEOUT_OPT = new CLOptionDescriptor("connect-timeout",
            CLOptionDescriptor.ARGUMENT_REQUIRED, CONNECT_TIMEOUT_OPT,
            "Maximum time in seconds that the connection to the server");
    private static final CLOptionDescriptor D_REFERER_OPT = new CLOptionDescriptor("referer",
            CLOptionDescriptor.ARGUMENT_REQUIRED, REFERER_OPT,
            "Sends the 'Referer Page' information to the HTTP server ");
    private static final CLOptionDescriptor D_COOKIE_OPT = new CLOptionDescriptor("cookie",
            CLOptionDescriptor.ARGUMENT_REQUIRED, COOKIE_OPT, "Pass the data to the HTTP server as a cookie");
    private static final CLOptionDescriptor D_URL_OPT = new CLOptionDescriptor("url",
            CLOptionDescriptor.ARGUMENT_REQUIRED, URL_OPT, "url");
    private static final CLOptionDescriptor D_USER_OPT = new CLOptionDescriptor("user",
            CLOptionDescriptor.ARGUMENT_REQUIRED, USER_OPT, "User and password to use for server authentication. ");
    private static final CLOptionDescriptor D_BASIC_OPT = new CLOptionDescriptor("basic",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, BASIC_OPT, "HTTP Basic authentication ");
    private static final CLOptionDescriptor D_DIGEST_OPT = new CLOptionDescriptor("digest",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, DIGEST_OPT, "HTTP digest authentication ");
    private static final CLOptionDescriptor D_CERT_OPT = new CLOptionDescriptor("cert",
            CLOptionDescriptor.ARGUMENT_REQUIRED, CERT_OPT, " The specified client certificate file for SSL");
    private static final CLOptionDescriptor D_CACERT_OPT = new CLOptionDescriptor("cacert",
            CLOptionDescriptor.ARGUMENT_REQUIRED, CAFILE_OPT,
            "Use the specified certificate file to verify the peer. ");
    private static final CLOptionDescriptor D_CAPATH_OPT = new CLOptionDescriptor("capath",
            CLOptionDescriptor.ARGUMENT_REQUIRED, CAPATH_OPT,
            "Use the specified certificate directory to verify the peer. ");
    private static final CLOptionDescriptor D_CIPHERS_OPT = new CLOptionDescriptor("ciphers",
            CLOptionDescriptor.ARGUMENT_REQUIRED, CIPHERS_OPT, "The ciphers to use in the connection. ");
    private static final CLOptionDescriptor D_CERT_STATUS_OPT = new CLOptionDescriptor("cert-status",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, CERT_STATUS_OPT, "Tells curl to verify the status of the server "
            + "certificate by using the Certificate Status Request TLS extension. ");
    private static final CLOptionDescriptor D_CERT_TYPE_OPT = new CLOptionDescriptor("cert-type",
            CLOptionDescriptor.ARGUMENT_REQUIRED, CERT_TYPE_OPT, "Tells curl the type of certificate type of the "
            + "provided certificate. PEM, DER and ENG are recognized types ");
    private static final CLOptionDescriptor D_KEY_OPT = new CLOptionDescriptor("key",
            CLOptionDescriptor.ARGUMENT_REQUIRED, KEY_OPT,
            "Private key file name. Allows you to provide your private key in this separate file. ");
    private static final CLOptionDescriptor D_KEY_TYPE_OPT = new CLOptionDescriptor("key-type",
            CLOptionDescriptor.ARGUMENT_REQUIRED, KEY_TYPE_OPT,
            "Private key file type. Specify which type your --key provided private key is.");
    private static final CLOptionDescriptor D_GET_OPT = new CLOptionDescriptor("get",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, GET_OPT,
            "Put the post data in the url and use get to replace post. ");
    private static final CLOptionDescriptor D_DNS_SERVERS_OPT = new CLOptionDescriptor("dns-servers",
            CLOptionDescriptor.ARGUMENT_REQUIRED, DNS_OPT, "Resolve host name over DOH. ");
    private static final CLOptionDescriptor D_NO_KEEPALIVE_OPT = new CLOptionDescriptor("no-keepalive",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, NO_KEEPALIVE_OPT, "Disabled keep-alive ");
    private static final CLOptionDescriptor D_LOCATION_OPT = new CLOptionDescriptor("location",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, LOCATION_OPT, "Follow Redirect ");
    private static final CLOptionDescriptor D_INCLUDE_OPT = new CLOptionDescriptor("include",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, INCLUDE_OPT, "Include the HTTP-header in the output ");
    private static final CLOptionDescriptor D_HEAD_OPT = new CLOptionDescriptor("head",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, HEAD_OPT, "Fetch the HTTP-header only");
    private static final CLOptionDescriptor D_INSECURE_OPT = new CLOptionDescriptor("insecure",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, INSECURE_OPT,
            "Allows curl to perform insecure SSL connections and transfers");
    private static final CLOptionDescriptor D_PROXY_OPT = new CLOptionDescriptor("proxy",
            CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_OPT,
            "Use the specified HTTP proxy. If the port number" + " is not specified, it is assumed at port 1080.");
    private static final CLOptionDescriptor D_PROXY_USER_OPT = new CLOptionDescriptor("proxy-user",
            CLOptionDescriptor.ARGUMENT_REQUIRED, PROXY_USER_OPT,
            "Specify user and password to use for proxy authentication.");
    private static final CLOptionDescriptor D_PROXY_NTLM_OPT = new CLOptionDescriptor("proxy-ntlm",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, PROXY_NTLM_OPT,
            "Tells curl to use HTTP ntlm authentication when communicating with the given proxy. ");
    private static final CLOptionDescriptor D_PROXY_NEGOTIATE_OPT = new CLOptionDescriptor("proxy-negotiate",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, PROXY_NEGOTIATE_OPT,
            "Tells curl to use HTTP negotiate authentication when communicating with the given proxy. ");
    private static final CLOptionDescriptor D_KEEPALIVETILE_OPT = new CLOptionDescriptor("keepalive-time",
            CLOptionDescriptor.ARGUMENT_REQUIRED, KEEPALIVETILE_OPT,
            " This option sets the  time  a  connection  needs  to  remain  idle  before  sending"
                    + " keepalive  probes and the time between individual keepalive probes..");
    private static final CLOptionDescriptor D_MAX_TIME_OPT = new CLOptionDescriptor("max-time",
            CLOptionDescriptor.ARGUMENT_REQUIRED, MAX_TIME_OPT,
            "Maximum time in seconds that you allow the whole operation to take. ");
    private static final CLOptionDescriptor D_OUTPUT_OPT = new CLOptionDescriptor("output",
            CLOptionDescriptor.ARGUMENT_REQUIRED, OUTPUT_OPT, "Write result to a file");
    private static final CLOptionDescriptor D_CREATE_DIRS_OPT = new CLOptionDescriptor("create-dir",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, CREATE_DIRS_OPT,
            "Create the necessary local directory hierarchy as needed for output file");
    private static final CLOptionDescriptor D_RAW_OPT = new CLOptionDescriptor("raw",
            CLOptionDescriptor.ARGUMENT_DISALLOWED, RAW_OPT,
            "When used, it disables all internal HTTP decoding of content or transfer encodings "
                    + "and instead makes them passed on unaltered raw. ");
    private static final CLOptionDescriptor D_INTERFACE_OPT = new CLOptionDescriptor("interface",
            CLOptionDescriptor.ARGUMENT_REQUIRED, INTERFACE_OPT, "Perform an operation using a specified interface");
    private static final CLOptionDescriptor D_DNS_RESOLVER_OPT = new CLOptionDescriptor("resolve",
            CLOptionDescriptor.ARGUMENT_REQUIRED, DNS_RESOLVER_OPT,
            "Provide a custom address for a specific host and port pair");
    private static final CLOptionDescriptor D_LIMIT_RATE_OPT = new CLOptionDescriptor("limit-rate",
            CLOptionDescriptor.ARGUMENT_REQUIRED, LIMIT_RATE_OPT,
            "Specify the maximum transfer rate you want curl to use");
    private static final CLOptionDescriptor D_MAX_REDIRS = new CLOptionDescriptor("max-redirs",
            CLOptionDescriptor.ARGUMENT_REQUIRED, MAX_REDIRS_OPT, "Set maximum number of redirections");
    private static final CLOptionDescriptor D_NOPROXY = new CLOptionDescriptor("noproxy",
            CLOptionDescriptor.ARGUMENT_REQUIRED, NOPROXY_OPT,
            "Comma-separated list of hosts which do not use a proxy, if one is specified. ");
    private static final CLOptionDescriptor D_SILENT = new CLOptionDescriptor("silent",
            CLOptionDescriptor.ARGUMENT_OPTIONAL, SILENT_OPT, "silent mode");
    private static final CLOptionDescriptor D_VERBOSE = new CLOptionDescriptor("verbose",
            CLOptionDescriptor.ARGUMENT_OPTIONAL, VERBOSE_OPT, "verbose mode");
    private static final Pattern deleteLinePattern = Pattern.compile("\r|\n|\r\n");

    private static final CLOptionDescriptor[] OPTIONS = new CLOptionDescriptor[]{
            D_COMPRESSED_OPT, D_HEADER_OPT, D_METHOD_OPT, D_DATA_OPT, D_DATA_ASCII_OPT, D_DATA_URLENCODE_OPT, D_DATA_RAW_OPT, D_DATA_BINARY_OPT,
            D_FORM_OPT, D_FORM_STRING_OPT, D_USER_AGENT_OPT, D_CONNECT_TIMEOUT_OPT, D_COOKIE_OPT, D_URL_OPT, D_USER_OPT,
            D_BASIC_OPT, D_DIGEST_OPT, D_CACERT_OPT, D_CAPATH_OPT, D_CERT_OPT, D_CERT_STATUS_OPT, D_CERT_TYPE_OPT,
            D_CIPHERS_OPT, D_KEY_OPT, D_KEY_TYPE_OPT, D_GET_OPT, D_DNS_SERVERS_OPT, D_NO_KEEPALIVE_OPT, D_REFERER_OPT,
            D_LOCATION_OPT, D_INCLUDE_OPT, D_INSECURE_OPT, D_HEAD_OPT, D_PROXY_OPT, D_PROXY_USER_OPT, D_PROXY_NTLM_OPT,
            D_PROXY_NEGOTIATE_OPT, D_KEEPALIVETILE_OPT, D_MAX_TIME_OPT, D_OUTPUT_OPT, D_CREATE_DIRS_OPT, D_RAW_OPT,
            D_INTERFACE_OPT, D_DNS_RESOLVER_OPT, D_LIMIT_RATE_OPT, D_MAX_REDIRS, D_NOPROXY, D_VERBOSE, D_SILENT
    };

    public BasicCurlParser() {
        super();
    }

    public Request parse(String commandLine) {
        String[] args = translateCommandline(commandLine);
        CLArgsParser parser = new CLArgsParser(args, OPTIONS);
        String error = parser.getErrorString();
        boolean isPostToGet = false;
        if (error == null) {
            List<CLOption> clOptions = parser.getArguments();
            Request request = new Request();
            for (CLOption option : clOptions) {
                if (option.getDescriptor().getId() == URL_OPT) {
                    request.setUrl(option.getArgument());
                } else if (option.getDescriptor().getId() == COMPRESSED_OPT) {
                    request.setCompressed(true);
                } else if (option.getDescriptor().getId() == HEADER_OPT) {
                    String nameAndValue = option.getArgument(0);
                    int indexOfSemicolon = nameAndValue.indexOf(':');
                    String name = nameAndValue.substring(0, indexOfSemicolon).trim();
                    String value = nameAndValue.substring(indexOfSemicolon + 1).trim();
                    request.addHeader(name, value);
                } else if (option.getDescriptor().getId() == METHOD_OPT) {
                    String value = option.getArgument(0);
                    request.setMethod(value);
                } else if (DATAS_OPT.contains(option.getDescriptor().getId())) {
                    String value = option.getArgument(0);
                    String dataOptionName = option.getDescriptor().getName();
                    value = getPostDataByDifferentOption(value.trim(), dataOptionName);
                    if ("GET".equals(request.getMethod())) {
                        request.setMethod("POST");
                    }
                    request.setPostData(value);
                } else if (FORMS_OPT.contains(option.getDescriptor().getId())) {
                    String nameAndValue = option.getArgument(0);
                    int indexOfEqual = nameAndValue.indexOf('=');
                    String key = nameAndValue.substring(0, indexOfEqual).trim();
                    String value = nameAndValue.substring(indexOfEqual + 1).trim();
                    if ("form-string".equals(option.getDescriptor().getName())) {
                        request.addFormStringData(key, value);
                    } else {
                        request.addFormData(key, value);
                    }
                    request.setMethod("POST");
                } else if (option.getDescriptor().getId() == USER_AGENT_OPT) {
                    request.addHeader("User-Agent", option.getArgument(0));
                } else if (option.getDescriptor().getId() == REFERER_OPT) {
                    request.addHeader("Referer", option.getArgument(0));
                } else if (option.getDescriptor().getId() == CONNECT_TIMEOUT_OPT) {
                    String value = option.getArgument(0);
                    request.setConnectTimeout(Double.parseDouble(value) * 1000);
                } else if (option.getDescriptor().getId() == COOKIE_OPT) {
                    String value = option.getArgument(0);
                    if (isValidCookie(value)) {
                        request.setCookies(value);
                    } else {
                        request.setFilepathCookie(value);
                    }
                } else if (option.getDescriptor().getId() == USER_OPT) {
                    String value = option.getArgument(0);
                    setAuthUserPasswd(value, request.getUrl(), request.getAuthorization());
                } else if (AUTH_OPT.contains(option.getDescriptor().getId())) {
                    String authOption = option.getDescriptor().getName();
                    setAuthMechanism(authOption, request.getAuthorization());
                } else if (SSL_OPT.contains(option.getDescriptor().getId())) {
                    request.setCaCert(option.getDescriptor().getName());
                } else if (option.getDescriptor().getId() == GET_OPT) {
                    isPostToGet = true;
                } else if (option.getDescriptor().getId() == DNS_OPT) {
                    String value = option.getArgument(0);
                    String[] dnsServer = value.split(",");
                    for (String s : dnsServer) {
                        request.addDnsServers(s);
                    }
                } else if (option.getDescriptor().getId() == NO_KEEPALIVE_OPT) {
                    request.setKeepAlive(false);
                } else if (option.getDescriptor().getId() == PROXY_OPT) {
                    String value = option.getArgument(0);
                    setProxyServer(request, value);
                } else if (option.getDescriptor().getId() == PROXY_USER_OPT) {
                    String value = option.getArgument(0);
                    setProxyServerUserInfo(request, value);
                } else if (option.getDescriptor().getId() == MAX_TIME_OPT) {
                    String value = option.getArgument(0);
                    request.setMaxTime(Double.parseDouble(value) * 1000);
                } else if (option.getDescriptor().getId() == HEAD_OPT) {
                    request.setMethod("HEAD");
                } else if (option.getDescriptor().getId() == INTERFACE_OPT) {
                    String value = option.getArgument(0);
                    request.setInterfaceName(value);
                } else if (option.getDescriptor().getId() == DNS_RESOLVER_OPT) {
                    String value = option.getArgument(0);
                    request.setDnsResolver(value);
                } else if (option.getDescriptor().getId() == LIMIT_RATE_OPT) {
                    String value = option.getArgument(0);
                    request.setLimitRate(value);
                } else if (option.getDescriptor().getId() == NOPROXY_OPT) {
                    String value = option.getArgument(0);
                    request.setNoproxy(value);
                } else if (IGNORE_OPTIONS_OPT.contains(option.getDescriptor().getId())) {
                    request.addOptionsIgnored(option.getDescriptor().getName());
                } else if (NOSUPPORT_OPTIONS_OPT.contains(option.getDescriptor().getId())) {
                    request.addOptionsNoSupport(option.getDescriptor().getName());
                } else if (PROPERTIES_OPT.contains(option.getDescriptor().getId())) {
                    request.addOptionsInProperties(
                            "--" + option.getDescriptor().getName() + " is in 'httpsampler.max_redirects(1062 line)'");
                } else if (option.getDescriptor().getId() == CLOption.TEXT_ARGUMENT
                        && !"CURL".equalsIgnoreCase(option.getArgument())) {
                    try {
                        request.setUrl(new URL(option.getArgument()).toExternalForm());
                    } catch (MalformedURLException ex) {
                        LOGGER.warn("Unhandled option {}", option.getArgument());
                    }
                }
            }
            if (isPostToGet) {
                String url = request.getUrl() + "?" + request.getPostData();
                request.setUrl(url);
                request.setPostData(null);
                request.setMethod("GET");
            }

            try {
                URL url = new URL(request.getUrl());
                String query = url.getQuery();
                request.urlNoQuery = url.getProtocol() + "://" + url.getHost() + url.getPath();
                if (StringUtils.isNotBlank(query)) {
                    request.urlParams = Splitter.on("&").splitToList(query).stream().collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            return request;
        } else {
            throw new IllegalArgumentException(
                    "Unexpected format for command line:" + commandLine + ", error:" + error);
        }
    }

    /**
     * Crack a command line.
     *
     * @param toProcess the command line to process.
     * @return the command line broken into strings.
     * An empty or null toProcess parameter results in a zero sized array.
     */
    public static String[] translateCommandline(String toProcess) {
        if (toProcess == null || toProcess.isEmpty()) {
            //no command? no string
            return new String[0];
        }
        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer(toProcess, "\"\' ", true);
        final ArrayList<String> result = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("\'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if ("\'".equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() > 0) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok.replaceAll("^\\\\[\\r\\n]", ""));
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }
        if (lastTokenHasBeenQuoted || current.length() > 0) {
            result.add(current.toString());
        }
        if (state == inQuote || state == inDoubleQuote) {
            throw new IllegalArgumentException("unbalanced quotes in " + toProcess);
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Set the username , password and baseurl of authorization
     *
     * @param authentication the username and password of authorization
     * @param url            the baseurl of authorization
     * @param authorization  the object of authorization
     */
    public void setAuthUserPasswd(String authentication, String url, Authorization authorization) {
        String[] authorizationParameters = authentication.split(":", 2);
        authorization.setUser(authorizationParameters[0].trim());
        authorization.setPass(authorizationParameters[1].trim());
        authorization.setURL(url);
    }

    /**
     * Set the mechanism of authorization
     *
     * @param mechanism     the mechanism of authorization
     * @param authorization the object of authorization
     */
    private void setAuthMechanism(String mechanism, Authorization authorization) {
        switch (mechanism.toLowerCase()) {
            case "basic":
                authorization.setMechanism(Mechanism.BASIC);
                break;
            case "digest":
                authorization.setMechanism(Mechanism.DIGEST);
                break;
            default:
                break;
        }
    }

    /**
     * Set the parameters of proxy server in http request advanced
     *
     * @param request                       http request
     * @param originalProxyServerParameters the parameters of proxy server
     */
    private void setProxyServer(Request request, String originalProxyServerParameters) {
        String proxyServerParameters = originalProxyServerParameters;
        if (!proxyServerParameters.contains("://")) {
            proxyServerParameters = "http://" + proxyServerParameters;
        }
        try {
            URI uriProxy = new URI(proxyServerParameters);
            request.setProxyServer("scheme", uriProxy.getScheme());
            Optional<String> userInfoOptional = Optional.ofNullable(uriProxy.getUserInfo());
            userInfoOptional.ifPresent(s -> setProxyServerUserInfo(request, s));
            Optional<String> hostOptional = Optional.ofNullable(uriProxy.getHost());
            hostOptional.ifPresent(s -> request.setProxyServer("servername", s));
            if (uriProxy.getPort() != -1) {
                request.setProxyServer("port", String.valueOf(uriProxy.getPort()));
            } else {
                request.setProxyServer("port", "1080");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(proxyServerParameters + " cannot be converted to a URL", e);
        }
    }

    /**
     * Set the username and password of proxy server
     *
     * @param request        http request
     * @param authentication the username and password of proxy server
     */
    private void setProxyServerUserInfo(Request request, String authentication) {
        if (authentication.contains(":")) {
            String[] userInfo = authentication.split(":", 2);
            request.setProxyServer("username", userInfo[0]);
            request.setProxyServer("password", userInfo[1]);
        }
    }

    /**
     * Get post data by different type of data option
     *
     * @param originalPostdata the post data
     * @param dataOptionName   the different option of "--data"
     * @return the post data
     */
    private String getPostDataByDifferentOption(final String originalPostdata, String dataOptionName) {
        String postdata = originalPostdata;
        if ("data-urlencode".equals(dataOptionName)) {
            postdata = encodePostdata(postdata);
        } else {
            if (postdata.charAt(0) == '@' && !"data-raw".equals(dataOptionName)) {
                postdata = postdata.substring(1, postdata.length());
                postdata = readFromFile(postdata);
                if (!"data-binary".equals(dataOptionName)) {
                    postdata = deleteLineBreak(postdata);
                }
            }
        }
        return postdata;
    }

    /**
     * Encode the post data
     *
     * @param postdata the post data
     * @return the result of encoding
     */
    private String encodePostdata(String postdata) {
        if (postdata.contains("@")) {
            String contentFile = null;
            String[] arr = postdata.split("@", 2);
            String dataToEncode = readFromFile(arr[1]);
            try {
                contentFile = URLEncoder.encode(dataToEncode, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(dataToEncode + " cannot be encoded", e);
            }
            if (!arr[0].isEmpty()) {
                contentFile = arr[0] + "=" + contentFile;
            }
            return contentFile;
        } else {
            if (!postdata.contains("=")) {
                try {
                    return URLEncoder.encode(postdata, StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(postdata + " cannot be encoded", e);
                }
            } else {
                int index = postdata.indexOf('=');
                try {
                    return postdata.substring(0, index + 1) + URLEncoder
                            .encode(postdata.substring(index + 1, postdata.length()), StandardCharsets.UTF_8.name());
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalArgumentException(
                            postdata.substring(index + 1, postdata.length()) + " cannot be encoded", e);
                }
            }
        }
    }

    /**
     * Read the postdata from file
     *
     * @param filePath
     * @return the content of file
     */
    private static String readFromFile(String filePath) {
        File file = new File(filePath.trim());
        if (file.isFile() && file.exists()) {
            try {
                return FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
            } catch (IOException e) {
                LOGGER.error("Failed to read from File {}", filePath, e);
                throw new IllegalArgumentException("Failed to read from File " + filePath);
            }
        } else {
            throw new IllegalArgumentException(filePath + " is a directory or does not exist");
        }
    }

    private static String deleteLineBreak(String postdata) {
        Matcher m = deleteLinePattern.matcher(postdata);
        return m.replaceAll("");
    }

    /**
     * Verify if the string is cookie or filename
     *
     * @param str the cookie to check
     * @return Whether the format of the string is cookie
     */
    public static boolean isValidCookie(String str) {
        for (String r : str.split(";")) {
            if (!r.contains("=")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert string to cookie
     *
     * @param cookieStr the cookie as a string
     * @return list of cookies
     */
    public static List<Cookie> stringToCookie(String cookieStr) {
        List<Cookie> cookies = new ArrayList<>();
        final StringTokenizer tok = new StringTokenizer(cookieStr, "; ", true);
        while (tok.hasMoreTokens()) {
            String nextCookie = tok.nextToken();
            if (nextCookie.contains("=")) {
                String[] cookieParameters = nextCookie.split("=", 2);
                if (!DYNAMIC_COOKIES.contains(cookieParameters[0])) {
                    Cookie newCookie = new Cookie();
                    newCookie.setName(cookieParameters[0]);
                    newCookie.setValue(cookieParameters[1]);
//                    URL newUrl;
//                    try {
//                        newUrl = new URL(url.trim());
//                        newCookie.setDomain(newUrl.getHost());
//                        newCookie.setPath(newUrl.getPath());
                    cookies.add(newCookie);
//                    } catch (MalformedURLException e) {
//                        throw new IllegalArgumentException(
//                                "unqualified url " + url.trim() + ", unable to create cookies.");
//                    }
                }
            }
        }
        return cookies;
    }

    public static void main(String[] args) {
        Request parse = new BasicCurlParser().parse("curl 'http://done.intra.xiaojuchefu.com/api/caseservice/case/update' \\\n" +
                "  -H 'Connection: keep-alive' \\\n" +
                "  -H 'Pragma: no-cache' \\\n" +
                "  -H 'Cache-Control: no-cache' \\\n" +
                "  -H 'Accept: application/json, text/plain, */*' \\\n" +
                "  -H 'DNT: 1' \\\n" +
                "  -H 'X-Requested-With: XMLHttpRequest' \\\n" +
                "  -H 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36' \\\n" +
                "  -H 'Content-Type: application/json;charset=UTF-8' \\\n" +
                "  -H 'Origin: http://done.intra.xiaojuchefu.com' \\\n" +
                "  -H 'Referer: http://done.intra.xiaojuchefu.com/case/caseManager/304/3992/0' \\\n" +
                "  -H 'Accept-Language: zh-CN,zh;q=0.9,en;q=0.8' \\\n" +
                "  -H 'Cookie: NewSSO_SESSIONID=cno5ekdYMk9TeTN4YktiZW5ZRWZERWNUUjJ0bHVYODI0Qm9aaG1GbVZLdGtqdW5xaFVnbTVCbFhsK3dDcjlNcw%3D%3D; SSO_SESSIONID=cno5ekdYMk9TeTN4YktiZW5ZRWZERWNUUjJ0bHVYODI0Qm9aaG1GbVZLdGtqdW5xaFVnbTVCbFhsK3dDcjlNcw%3D%3D; NewAutoCompanyUser=bDA5bkhxUk9MR29tTmg4SGFVcFZwSCtySnRtd2Z3Ti94ek5Ra3lqV1lsbU9DdFVGUjN2TEdWMGtLcFlhR3R6bXFHdW9RTk1CMnpVeVphbTFjZitGbStENkVoY0liN0hCMXNKd0JxWmxPQ1k5VXdQQ3dLVkNHZ3R5M1cyVTlrQjBVMFpRWUExNis0SDduR3g5K2NqSURvd2VxTEl3K2gzUTlESWJFcFdDOE0rREltWE1FcUVBNDkrdlp4VnNib1o5; AutoCompanyUser=bDA5bkhxUk9MR29tTmg4SGFVcFZwSCtySnRtd2Z3Ti94ek5Ra3lqV1lsbU9DdFVGUjN2TEdWMGtLcFlhR3R6bXFHdW9RTk1CMnpVeVphbTFjZitGbStENkVoY0liN0hCMXNKd0JxWmxPQ1k5VXdQQ3dLVkNHZ3R5M1cyVTlrQjBVMFpRWUExNis0SDduR3g5K2NqSURvd2VxTEl3K2gzUTlESWJFcFdDOE0rREltWE1FcUVBNDkrdlp4VnNib1o5; sso_user=jiangxinyu; sso_user_cn=%E5%A7%9C%E4%BF%A1%E5%AE%87; sso_email=jiangxinyu%40xiaojuchefu.com; sso_ticket=a9b1fb5a0537aacf62264556126886620003248000; JSESSIONID=0142618B431D472CDA4A3746B8DC0D51' \\\n" +
                "  --data-binary '{\"id\":\"3992\",\"title\":\"更新内容，实际不会保存title\",\"modifier\":\"jiangxinyu\",\"caseContent\":\"{\\\"root\\\":{\\\"data\\\":{\\\"created\\\":1597750496912,\\\"text\\\":\\\"营销落地页\\\",\\\"id\\\":\\\"466b36fpsh1e47g8l5as39f9uj\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"C端落地页\\\",\\\"id\\\":\\\"4n9sgq6a5ai25caulscvplb6gu\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496881,\\\"text\\\":\\\"增加入口\\\",\\\"id\\\":\\\"7mn8rvaabf4t13rekua96c9sjk\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496880,\\\"text\\\":\\\"a. 滴滴乘客端，顺风车 → 车服务 → 二手车\\\",\\\"id\\\":\\\"6534p8t3s1mv752ohur81b81bq\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496880,\\\"text\\\":\\\"   b. 滴滴乘客端，车生活  → 二手车\\\",\\\"id\\\":\\\"7relaoe8ulovpjreerk8pp061q\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496880,\\\"text\\\":\\\"   c. 滴滴乘客端，代驾 → 车主服务 → 二手车\\\",\\\"id\\\":\\\"4rdjq23v0kk925jh9uafqdejk4\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496881,\\\"text\\\":\\\"   d. 滴滴车主端，车主服务  → 二手车\\\",\\\"id\\\":\\\"18he495nd50kjquaa5fd8g3bhp\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"首页\\\",\\\"id\\\":\\\"2ni3vu5mva3lacrdso7m8ggjmu\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496881,\\\"text\\\":\\\"头图\\\",\\\"id\\\":\\\"06gj7mugtggnadniisu6uiurbu\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496881,\\\"text\\\":\\\"展示图片，文案：全国买家在线出价，爱车卖高价\\\",\\\"id\\\":\\\"3dr8fjbef0bdeevfvd4phr50bv\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496881,\\\"text\\\":\\\"滚动栏展示最近一条信息\\\",\\\"id\\\":\\\"4i2q2mn9781fr9u7tfo2bn3j0l\\\",\\\"priority\\\":1},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496881,\\\"text\\\":\\\"手机号（车主）刚评估了年款+品牌+车系\\\",\\\"id\\\":\\\"02k2nmjibblp3tej4q1ctdp6vd\\\"},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496886,\\\"text\\\":\\\"在线估价\\\",\\\"id\\\":\\\"5tnt2uidhh1m3erj08thv9f7m4\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496882,\\\"text\\\":\\\"所在城市\\\",\\\"id\\\":\\\"3hv0lm8ob9ju9v4v2iiukqn6cn\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496882,\\\"text\\\":\\\"自动带入当前城市\\\",\\\"id\\\":\\\"7b5nv773tig5rkki49qcc3jjgj\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496882,\\\"text\\\":\\\"可以选择其他城市\\\",\\\"id\\\":\\\"6a9unt4ujvnu57ud11163fj5ml\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496882,\\\"text\\\":\\\"品牌车型\\\",\\\"id\\\":\\\"3lfdmagbe4f6bl3lncbu7131mn\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496882,\\\"text\\\":\\\"列表选择车型\\\",\\\"id\\\":\\\"2ep4tk6codq7rh3163opmvdjq2\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496883,\\\"text\\\":\\\"当前里程\\\",\\\"id\\\":\\\"1776fafs346ss2tmd7qg6mom4b\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496883,\\\"text\\\":\\\"选择当前里程，单位万公里\\\",\\\"id\\\":\\\"7ke7vt2jdb60m10nsqe0vdv0l3\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496882,\\\"text\\\":\\\"一万公里\\\",\\\"id\\\":\\\"6290qid6q3prti8rtdmd3a0duu\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496883,\\\"text\\\":\\\"....\\\",\\\"id\\\":\\\"788bucfc1sjn6jf8lceids38qc\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496883,\\\"text\\\":\\\"二十万公里\\\",\\\"id\\\":\\\"3rgtujolinhg90gofrrrn9skch\\\"},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496883,\\\"text\\\":\\\"首次上牌\\\",\\\"id\\\":\\\"06uiuk9pkeo60heqjjmhll150f\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496883,\\\"text\\\":\\\"时间选择器，精确到月\\\",\\\"id\\\":\\\"3q92et4bo8ln3s6o25pkvos82t\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496886,\\\"text\\\":\\\"开始估价\\\",\\\"id\\\":\\\"33jo3vj6sa44s97ebappdcgk80\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"点击按钮\\\",\\\"id\\\":\\\"2sjtle7577312t75qp71qncgn8\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"点击后弹出确认联系方式浮层\\\",\\\"id\\\":\\\"655qhgm8vk9i2m4s5005be9ig8\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"联系方式\\\",\\\"id\\\":\\\"5ifantfmno6c6sl18o4l9ftqfr\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"自动填充当前手机号，带星号\\\",\\\"id\\\":\\\"16l2u7pt5rf4u934drdo9u4jp0\\\",\\\"priority\\\":1},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"必填\\\",\\\"id\\\":\\\"40hrjelek9vj3ssbgqgqdjfug9\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"可以删除重新填写\\\",\\\"id\\\":\\\"1l6ppru9uvssrftm9llu49cjha\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"左上角X可以关闭\\\",\\\"id\\\":\\\"5v1qtu3jkqvij1h6iq0ecnm7u9\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"个人信息授权书\\\",\\\"id\\\":\\\"6k1u5cpuunm01dt9m1b1o95j7j\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"点击可以打开\\\",\\\"id\\\":\\\"6r77ali66opv8nb847kacletp3\\\",\\\"priority\\\":1},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496884,\\\"text\\\":\\\"默认不勾选\\\",\\\"id\\\":\\\"4r55hq8kkusg23eb0m135lckc3\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"必须勾选才能点击开始估价按钮\\\",\\\"id\\\":\\\"1lmeuq2v11801sg2ma4hm8j6fk\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"开始估价\\\",\\\"id\\\":\\\"2kn2mh2cpo2pqmfusrvdd9icnj\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"校验联系方式、协议勾选\\\",\\\"id\\\":\\\"52lnglcgd37auoc014u3k0m50s\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"成功后提示：您的专属服务顾问会致电联系，帮您的爱车卖个高价。\\\",\\\"id\\\":\\\"3tgdp82hmqn5t9nmdj3cj9it2q\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"右上角X可关闭\\\",\\\"id\\\":\\\"7ask0vv5bu3t03hhlu8dij7b1p\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496885,\\\"text\\\":\\\"传递信息到后台\\\",\\\"id\\\":\\\"690tkf0msouqf5jpkhhfso5c6s\\\",\\\"priority\\\":1},\\\"children\\\":[]}]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496886,\\\"text\\\":\\\"以上四项未填写完整，提示：请选择XX\\\",\\\"id\\\":\\\"2admb721g25r0fpiu18t0dltg0\\\"},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"预约卖车\\\",\\\"id\\\":\\\"4vd6s41ot5pmrve4f7gbhubcml\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496886,\\\"text\\\":\\\"文案：一键预约，全国优质经销商帮卖\\\",\\\"id\\\":\\\"7i8foio2bi1sl0a34d89dpq6bi\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"预约卖车\\\",\\\"id\\\":\\\"4p6uhsct96n5li3p6i7mrjfh1p\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"点击后弹出确认联系方式浮层\\\",\\\"id\\\":\\\"48ockq4tj1o8ihfebgrqdihhrd\\\",\\\"priority\\\":1},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"联系方式\\\",\\\"id\\\":\\\"07vmi7qise4hnlf4mn8m5mjjdf\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496886,\\\"text\\\":\\\"自动填充当前手机号，带星号\\\",\\\"id\\\":\\\"54r4d1k2kqd3mgof9suhkp7i0a\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496886,\\\"text\\\":\\\"必填\\\",\\\"id\\\":\\\"3akg727gkic2jmopma6cu8piku\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"可以删除重新填写\\\",\\\"id\\\":\\\"2i2bv19oskn7tl31puo0pcr9eb\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"左上角X可以关闭\\\",\\\"id\\\":\\\"7pl4r6dnlauho0gtfq9vjo8fb4\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"个人信息授权书\\\",\\\"id\\\":\\\"4je9muq2433e66jasho3jhifqj\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"点击可以打开\\\",\\\"id\\\":\\\"0v3qn7jgove1gif9074dot34hd\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"默认不勾选\\\",\\\"id\\\":\\\"6r4bb1dnv5khi6jjubnhv9sqbv\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"必须勾选才能点击开始估价按钮\\\",\\\"id\\\":\\\"7qeshp8fdd3jvpi0943s0lrajq\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"开始估价\\\",\\\"id\\\":\\\"4kt6iodddnh1k7kqjfk1nvro70\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496887,\\\"text\\\":\\\"校验联系方式、协议勾选\\\",\\\"id\\\":\\\"6u6velkedi6g9o0qdp0q069th3\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"成功后提示：您的专属服务顾问会致电联系，帮您的爱车卖个高价。\\\",\\\"id\\\":\\\"4qemurceh4lvvlnktks8rg6830\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"右上角X可关闭\\\",\\\"id\\\":\\\"6gb7rc4inhfpmt8v5dj8g6nomq\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496888,\\\"text\\\":\\\"传递信息到后台\\\",\\\"id\\\":\\\"5uf3ojc0mhfhrdq7k75vc65sju\\\",\\\"priority\\\":1},\\\"children\\\":[]}]}]}]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"估价结果页\\\",\\\"id\\\":\\\"0cj33b29f1e4le5pttcv93st3v\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496891,\\\"text\\\":\\\"车辆信息\\\",\\\"id\\\":\\\"24iaif41dm18bmajmespbo5k8e\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496889,\\\"text\\\":\\\"title：您的爱车\\\",\\\"id\\\":\\\"40fkg8g9cd837sju5e7v70omai\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496889,\\\"text\\\":\\\"http://wiki.intra.xiaojukeji.com/download/attachments/402017520/image2020-8-4_10-17-7.png?version=1&modificationDate=1596507443000&api=v2\\\",\\\"id\\\":\\\"341722c7s6a3hmrjj0q0ulenj4\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496889,\\\"text\\\":\\\"品牌车型\\\",\\\"id\\\":\\\"2e917svi05efcvp1a6107odk7u\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496889,\\\"text\\\":\\\"展示上一步选择的品牌车型\\\",\\\"id\\\":\\\"6cauoo74d70e304h9m2gbl72g8\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496889,\\\"text\\\":\\\"首次上牌\\\",\\\"id\\\":\\\"490lr4d8ibg16rjbof00ghpe0a\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496889,\\\"text\\\":\\\"展示上一步选择的上牌时间\\\",\\\"id\\\":\\\"389h1purtrmm3v5l1okboep9kb\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496890,\\\"text\\\":\\\"当前里程\\\",\\\"id\\\":\\\"5q42gq5k4neakslv0u6iegke1e\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496890,\\\"text\\\":\\\"展示上一步选择的里程\\\",\\\"id\\\":\\\"72hsjdutm0mum4h9hcioo2uq2e\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496890,\\\"text\\\":\\\"所在城市\\\",\\\"id\\\":\\\"4p92nrp5k676g3mf4gad6js711\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496890,\\\"text\\\":\\\"展示上一步选择的城市\\\",\\\"id\\\":\\\"2lmpaqa1ca8vv95f8ocs57qsc3\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496891,\\\"text\\\":\\\"车辆logo\\\",\\\"id\\\":\\\"7ivuddf9i3gc5vh5ad9o8fnd8s\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496890,\\\"text\\\":\\\"右上角展示选择的车辆logo\\\",\\\"id\\\":\\\"70q1e77t8rk5ap9q4hk28llurl\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496890,\\\"text\\\":\\\"重新选择后变更\\\",\\\"id\\\":\\\"201fv2ojr38n5nn7aokhb0b1j5\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496891,\\\"text\\\":\\\"查不到logo不显示\\\",\\\"id\\\":\\\"62via56ipp3l8co21kj0lm5sb2\\\"},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"估价结果\\\",\\\"id\\\":\\\"49rsv5mo2s8dqcjk412a4qumtv\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496892,\\\"text\\\":\\\"司机端页面显示非营业车/营运车Tab\\\",\\\"id\\\":\\\"6qt4ldj1sh053qcpgh8j9eqhei\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496892,\\\"text\\\":\\\"默认非营运车\\\",\\\"id\\\":\\\"0g4kdbk7j29ojq7e3ofjdai4fc\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496892,\\\"text\\\":\\\"文案：预计您的爱车能卖\\\",\\\"id\\\":\\\"6j7u8qu3qkpri5os9p96kmmci9\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496892,\\\"text\\\":\\\"展示价格区间\\\",\\\"id\\\":\\\"49psc3kc80tr35um1a91vcqg5m\\\",\\\"priority\\\":1},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496892,\\\"text\\\":\\\"车况优秀/车况良好/车况一般可以切换，价格改变\\\",\\\"id\\\":\\\"0bmmtlnrla3g4vv9ttk0bnuuar\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496892,\\\"text\\\":\\\"底部有仅供参考的说明\\\",\\\"id\\\":\\\"4sj2t589a3gl8mer7mesnri4ke\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"预约卖车按钮\\\",\\\"id\\\":\\\"0mm5j80k7b3k2v2frcal4eatl3\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"点击后弹出确认联系方式浮层\\\",\\\"id\\\":\\\"359a2phqbgojnbhu6tkml7q6te\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496893,\\\"text\\\":\\\"联系方式\\\",\\\"id\\\":\\\"1pnv02a2amk3kmm6u9eql6j5p1\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496893,\\\"text\\\":\\\"自动填充当前手机号，带星号\\\",\\\"id\\\":\\\"7nr717kkihd5rm89s5csnlk33s\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496893,\\\"text\\\":\\\"必填\\\",\\\"id\\\":\\\"7gq3fk03r8nfdvg3tv5hpekan2\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496893,\\\"text\\\":\\\"可以删除重新填写\\\",\\\"id\\\":\\\"6ka7scf686i1e10tp20g28ike1\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496893,\\\"text\\\":\\\"左上角X可以关闭\\\",\\\"id\\\":\\\"38a2m1lv5qgkjvgv631gamrgnk\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496896,\\\"text\\\":\\\"个人信息授权书\\\",\\\"id\\\":\\\"3mhhfm7mbl2pt7eedllrp1nr87\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496896,\\\"text\\\":\\\"点击可以打开\\\",\\\"id\\\":\\\"050cmc33bleq91ucj6dr7k531k\\\",\\\"priority\\\":1},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496896,\\\"text\\\":\\\"默认不勾选\\\",\\\"id\\\":\\\"6anrmg94hvqi6uao8tl33eg870\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496896,\\\"text\\\":\\\"必须勾选才能点击开始估价按钮\\\",\\\"id\\\":\\\"6undb629acah0v79c7g8fjqo0t\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"开始估价\\\",\\\"id\\\":\\\"5gqao4oaa8l71rp8veon8obe8j\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496896,\\\"text\\\":\\\"校验联系方式、协议勾选\\\",\\\"id\\\":\\\"1srrf44iohqhfl2v5fbl4v4rfd\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"成功后提示：您的专属服务顾问会致电联系，帮您的爱车卖个高价。\\\",\\\"id\\\":\\\"3si66t2pegkg2f5o0orhklc36k\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"右上角X可关闭\\\",\\\"id\\\":\\\"0vm2bi49um92degn1tvf9h3fsv\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496897,\\\"text\\\":\\\"传递信息到后台\\\",\\\"id\\\":\\\"3ojqkcht0totup3q5ve1jafulv\\\",\\\"priority\\\":1},\\\"children\\\":[]}]}]}]}]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"B端\\\",\\\"id\\\":\\\"5pfhht62cr0tibo6befg4cjvuv\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"carmis-菜单\\\",\\\"id\\\":\\\"3084285p5j3sgjf1esuajdn2pn\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"线索列表页\\\",\\\"id\\\":\\\"7ggr9m1okfc3rqa9bfhds3efvk\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496898,\\\"text\\\":\\\"线索id\\\",\\\"id\\\":\\\"7400ejsagr9at99ms0cm4qhs6i\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496898,\\\"text\\\":\\\"输入框\\\",\\\"id\\\":\\\"70pfhmjin49vrcqmnqqtrf76dt\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496898,\\\"text\\\":\\\"手机号\\\",\\\"id\\\":\\\"191m7su7hrhe6us66q29qm40fr\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496898,\\\"text\\\":\\\"输入框\\\",\\\"id\\\":\\\"2nilu0qu1a92ajc49k32cps02t\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496899,\\\"text\\\":\\\"线索归属\\\",\\\"id\\\":\\\"29f8717va125fpusa9cd9lj2ch\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496899,\\\"text\\\":\\\"下拉框\\\",\\\"id\\\":\\\"2orihorja2n11j51e5qktol35t\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496899,\\\"text\\\":\\\"可选：已有的线索归属防\\\",\\\"id\\\":\\\"6e98u8epgt30n9crpeco84bsaf\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496904,\\\"text\\\":\\\"创建时间\\\",\\\"id\\\":\\\"4pfe4c2if15rcej3bdijumm522\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496904,\\\"text\\\":\\\"2个时间选择器\\\",\\\"id\\\":\\\"1r98h3bnk2ujit541suces46g3\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496904,\\\"text\\\":\\\"输入单个\\\",\\\"id\\\":\\\"046b8oj54r7lgdd8s1hh0i5son\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496904,\\\"text\\\":\\\"开始、结束都输入\\\",\\\"id\\\":\\\"43mtfqij488v2umdbmhjodqvgj\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"线索状态\\\",\\\"id\\\":\\\"1pencrrts9fnb4kdop96n8gcht\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496904,\\\"text\\\":\\\"下拉框\\\",\\\"id\\\":\\\"4gnt8favecrh0ojo16aa0r7oeo\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496904,\\\"text\\\":\\\"可选：已下发、待下发\\\",\\\"id\\\":\\\"0b2j377o3nasluglmtuqo5p30s\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"以上条件组合查询结果正确\\\",\\\"id\\\":\\\"5tout8t89fumuulgbmdf71eb8h\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"翻页\\\",\\\"id\\\":\\\"7vl2dhgol4e4vif9lm2nr33stt\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"翻页结果正确\\\",\\\"id\\\":\\\"2sh33mnj0r01t616bh7tblch3i\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"查询后分页/分页后查询\\\",\\\"id\\\":\\\"6grj08hcgismkd98ujhjp7d5fe\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"每页10个\\\",\\\"id\\\":\\\"1iutcegtit0n6d3okqnehfrolm\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"重置\\\",\\\"id\\\":\\\"7df2b3parajhf8qg61qle8j33f\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"重置所有项\\\",\\\"id\\\":\\\"7m040djv4b8763clb9lqsbnm5m\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496905,\\\"text\\\":\\\"进入页面默认展示所有列表\\\",\\\"id\\\":\\\"7p947qgir9fqnkdhcf22odla9e\\\",\\\"priority\\\":1},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"排序：按创建时间倒序\\\",\\\"id\\\":\\\"6l30gau702mta1pca3tmj4kd0j\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"结果列表\\\",\\\"id\\\":\\\"2bq9ohud718kusg29ub2li8569\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"线索id\\\",\\\"id\\\":\\\"7hh04rivb6mljidnuoirv6obtv\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"创建时间\\\",\\\"id\\\":\\\"3cpimusb2b546iruv4jf9ar9gr\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"手机号\\\",\\\"id\\\":\\\"6tkoa5g9e4j77ognnd8d9vlr9m\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"中间四位*\\\",\\\"id\\\":\\\"4rfnnt77f6mch6csitirlpqh3h\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"UID\\\",\\\"id\\\":\\\"13p43ns09j9488s0voc70fmoc5\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"线索归属\\\",\\\"id\\\":\\\"2cr1dbi5jaea1ramiq7e0m54nj\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496906,\\\"text\\\":\\\"已上字段展示正确\\\",\\\"id\\\":\\\"5u299e4o0g528h61h88hhdhpii\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"操作\\\",\\\"id\\\":\\\"54doc5ri23anq36a1h2pi27hqu\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"查看\\\",\\\"id\\\":\\\"501niicu67hsu0ko5em3cp2p8m\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"进入详情\\\",\\\"id\\\":\\\"7v6gf942bat8tkn7makcjkf24t\\\"},\\\"children\\\":[]}]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"批量导出\\\",\\\"id\\\":\\\"1d2mrp7o190mofiur4q75h051b\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"导出的手机加密\\\",\\\"id\\\":\\\"3b2hfefl5rq0r3rpjlkqgge0eu\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"勾选线索后可以导出，不勾选不可以导出\\\",\\\"id\\\":\\\"5ui0s2e52om5v7icpaf1go28ft\\\",\\\"priority\\\":1},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"线索详情页\\\",\\\"id\\\":\\\"1a9l2csi7h0qb9ptcnptm1da75\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496908,\\\"text\\\":\\\"基本信息展示正确\\\",\\\"id\\\":\\\"10vp2njt9prvqre7clj34rvodn\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"线索id\\\",\\\"id\\\":\\\"1kl6nso98sv2od0h3hs1kl2pf5\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496907,\\\"text\\\":\\\"手机号，加密\\\",\\\"id\\\":\\\"0iphg5ahk7ft4vcbebm6pd8if0\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496908,\\\"text\\\":\\\"uid\\\",\\\"id\\\":\\\"5ifr6u2kloctnpblbji0t8e2ek\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496908,\\\"text\\\":\\\"线索归属\\\",\\\"id\\\":\\\"6b0gsg0c30uk60n8l2kj9o3joe\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496908,\\\"text\\\":\\\"如没有展示-\\\",\\\"id\\\":\\\"44ga1ggmacjvonn2f33j3kmij1\\\"},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"留资记录\\\",\\\"id\\\":\\\"3hp38t7frlllhe0g313didtvcb\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496908,\\\"text\\\":\\\"时间正序排列\\\",\\\"id\\\":\\\"5td98qrqcpft3m9i5qcatdpcv7\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496908,\\\"text\\\":\\\"展示所有留资记录\\\",\\\"id\\\":\\\"52iv53oom285dv3q5k76h06kfb\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"留资时间、城市、车型车款、注册时间、当前里程、渠道来源、场景来源展示正确\\\",\\\"id\\\":\\\"11c7a00l2uassggtodp41hcgr0\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"没有的展示为-，字段如prd\\\",\\\"id\\\":\\\"7fdmbikrui5e9t4rlvoh8cian0\\\"},\\\"children\\\":[]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"返回按钮\\\",\\\"id\\\":\\\"5e8lb4m3l3k7fn8fb41ta7d69u\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"留资逻辑\\\",\\\"id\\\":\\\"2slior774s8suafiqfagop5sjg\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"收到留资后立即下发\\\",\\\"id\\\":\\\"0go49cid4u1m4oc7ea92ij7va8\\\",\\\"priority\\\":1},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"邮件内容：留资时间、城市、手机号、车型车款、注册时间、当前里程、隐私声明\\\",\\\"id\\\":\\\"72qc5gd1val1jal6t2cu7ihcis\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"循环发给配置的客户\\\",\\\"id\\\":\\\"1b4se240h3e8s76mc6t6olv5vj\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496909,\\\"text\\\":\\\"下发后状态变为已下发\\\",\\\"id\\\":\\\"7e1t9bmc2hgm5ahr23d6rqlgfv\\\",\\\"priority\\\":1},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"同一个手机号为同一用户\\\",\\\"id\\\":\\\"4l54548n52hrb45alsq9s1p8k2\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496910,\\\"text\\\":\\\"30天内同一个手机号为为同一个线索，追加留资记录\\\",\\\"id\\\":\\\"6vi30kuulp6g5o8n7coptoel24\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496910,\\\"text\\\":\\\"31天的留资是新的线索\\\",\\\"id\\\":\\\"0obbe3shlrnc6kvhjdmitu0u1r\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496910,\\\"text\\\":\\\"收件人是新循环的客户\\\",\\\"id\\\":\\\"4bmqgcjbbohakr71rj8en8lu9j\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496910,\\\"text\\\":\\\"61天的留资是新的线索\\\",\\\"id\\\":\\\"1lfmtcir2rtmjsidsiue1spf1o\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496910,\\\"text\\\":\\\"收件人是新循环的客户\\\",\\\"id\\\":\\\"5q1ah9dtvo12hbvhphialvinsf\\\"},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"每次留资都发送邮件，收件人为本线索的线索归属\\\",\\\"id\\\":\\\"4slhu3v6rfl0oj70q9fudgrvo9\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"邮件信息正确，为当前的信息\\\",\\\"id\\\":\\\"5a1eu47pnhm60sagbb3j7rvsr9\\\",\\\"priority\\\":1},\\\"children\\\":[]}]},{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"不同uid留资相同手机号是一条\\\",\\\"id\\\":\\\"5m94ijchh4pqbef7ankk8l98p3\\\"},\\\"children\\\":[]},{\\\"data\\\":{\\\"created\\\":1597750496911,\\\"text\\\":\\\"相同uid留资不同手机号不是一条线索\\\",\\\"id\\\":\\\"4aoovvnrpgem9fch4uhbtqk8h7\\\"},\\\"children\\\":[]}]}]}]}]},{\\\"data\\\":{\\\"created\\\":1597750496912,\\\"text\\\":\\\"价格计算\\\",\\\"id\\\":\\\"0ro2r4u0lmbpk8td3a0esldr27\\\"},\\\"children\\\":[{\\\"data\\\":{\\\"created\\\":1597750496912,\\\"text\\\":\\\"负数\\\",\\\"id\\\":\\\"7v84sa9qvhfsf1juee9g9rgn2v\\\"},\\\"children\\\":[]}]}]},\\\"template\\\":\\\"default\\\",\\\"theme\\\":\\\"fresh-blue\\\",\\\"version\\\":\\\"1.4.43\\\",\\\"base\\\":40}\"}' \\\n" +
                "  --compressed \\\n" +
                "  --insecure");
        System.out.println(1);
    }

}