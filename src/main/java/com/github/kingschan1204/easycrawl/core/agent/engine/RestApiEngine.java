package com.github.kingschan1204.easycrawl.core.agent.engine;

import com.github.kingschan1204.easycrawl.core.agent.HttpEngine;
import com.github.kingschan1204.easycrawl.core.agent.WebDataAgent;
import com.github.kingschan1204.easycrawl.plugs.freemarker.FreemarkParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.EventListener;

import javax.net.ssl.*;
import java.net.Proxy;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.*;

@Slf4j
public final class RestApiEngine implements WebDataAgent<String> {
    OkHttpClient httpClient;
    HttpEngine engine = new HttpEngine();

    @Override
    public WebDataAgent<String> url(String url) {
        this.engine.setUrl(url);
        return this;
    }

    @Override
    public WebDataAgent<String> referer(String referer) {
        this.engine.setReferer(referer);
        return this;
    }

    @Override
    public WebDataAgent<String> method(HttpEngine.Method method) {
        this.engine.setMethod(method);
        return this;
    }

    @Override
    public WebDataAgent<String> head(Map<String, String> head) {
        this.engine.setHead(head);
        return this;
    }

    @Override
    public WebDataAgent<String> useAgent(String useAgent) {
        this.engine.setUseAgent(useAgent);
        return this;
    }

    @Override
    public WebDataAgent<String> cookie(Map<String, String> cookie) {
        this.engine.setCookie(cookie);
        return this;
    }

    @Override
    public WebDataAgent<String> timeOut(Integer timeOut) {
        this.engine.setTimeOut(timeOut);
        return this;
    }

    @Override
    public WebDataAgent<String> proxy(Proxy proxy) {
        this.engine.setProxy(proxy);
        return this;
    }

    @Override
    public WebDataAgent<String> body(String body) {
        return null;
    }

    @Override
    public String dataPull(Map<String, Object> data) throws Exception {
        FreemarkParser parser = new FreemarkParser();
        String httpUrl = parser.parse(this.engine.url, data).trim();
        log.info("{}", httpUrl);
        // 构造一个 Request 对象
        Request request = null;
        if (this.engine.method == HttpEngine.Method.GET) {
            request = new Request.Builder()
                    // 标识为 GET 请求
                    .get()
                    // 设置请求路径
                    .url(httpUrl)
                    .header("Referer", null == this.engine.referer ? "" : this.engine.referer)
                    .header("User-Agent", this.engine.useAgent)
                    // 添加头信息
                    .addHeader("Content-Type", "application/json")
                    .build();
        } else if (this.engine.method == HttpEngine.Method.POST) {
            String json = "{\"name\": \"admin\",\"id\": 10}";
            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
            request = new Request.Builder()
                    .post(requestBody)
                    .url(httpUrl)
                    .header("Referer", this.engine.referer)
                    .header("User-Agent", this.engine.useAgent)
                    .build();
        } else {
            throw new RuntimeException("目前只支持：get , post 方法！");
        }
        // 通过 HttpClient 把 Request 构造为 Call 对象
        Call call = getHttpClient().newCall(request);
        // 执行同步请求
        Response response = call.execute();
        return Objects.requireNonNull(response.body()).string();
    }

    @Override
    public HttpEngine get() {
        return null;
    }

    OkHttpClient getHttpClient() throws KeyStoreException, NoSuchAlgorithmException {
        if (null == httpClient) {
            return httpClient = new OkHttpClient.Builder()
                    // 设置连接超时时间
                    .connectTimeout(Duration.ofSeconds(10))
                    // 设置读超时时间
                    .readTimeout(Duration.ofSeconds(30))
                    // 设置写超时时间
                    .writeTimeout(Duration.ofSeconds(30))
                    // 设置完整请求超时时间
                    .callTimeout(Duration.ofSeconds(60))
                    // 设置https配置，此处忽略了所有证书
                    .sslSocketFactory(Objects.requireNonNull(sslSocketFactory()), new EasyX509TrustManager(null))
                    // 添加一个拦截器
                    .addInterceptor(chain -> {
                        Request request = chain.request();
                        return chain.proceed(request);
                    })
                    // 注册事件监听器
                    .eventListener(new EventListener() {
                        @Override
                        public void callEnd(Call call) {
                            log.info("----------callEnd--------");
                            super.callEnd(call);
                        }
                    })
                    .proxy(this.engine.proxy)
                    .cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
//                            if(null!=engine.getCookie()){
//                                Cookie c = new Cookie(null,null,null,null,null,null,null,null,null,null);
//                            }
                            return cookies != null ? cookies : new ArrayList<>();
                        }
                    })
                    .build();
        }
        return httpClient;
    }

    public class EasyX509TrustManager implements X509TrustManager {

        private X509TrustManager standardTrustManager = null;

        /**
         * Constructor for EasyX509TrustManager.
         */
        public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException,
                KeyStoreException {
            super();
            TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            factory.init(keystore);
            TrustManager[] trustmanagers = factory.getTrustManagers();
            if (trustmanagers.length == 0) {
                throw new NoSuchAlgorithmException("no trust manager found");
            }
            this.standardTrustManager = (X509TrustManager) trustmanagers[0];
        }

        /**
         * @see X509TrustManager#checkClientTrusted(X509Certificate[],
         * String authType)
         */
        public void checkClientTrusted(X509Certificate[] certificates, String authType)
                throws CertificateException {
            standardTrustManager.checkClientTrusted(certificates, authType);
        }

        /**
         * @see X509TrustManager#checkServerTrusted(X509Certificate[],
         * String authType)
         */
        public void checkServerTrusted(X509Certificate[] certificates, String authType)
                throws CertificateException {
            if ((certificates != null) && (certificates.length == 1)) {
                certificates[0].checkValidity();
            } else {
                standardTrustManager.checkServerTrusted(certificates, authType);
            }
        }

        /**
         * @see X509TrustManager#getAcceptedIssuers()
         */
        public X509Certificate[] getAcceptedIssuers() {
            return this.standardTrustManager.getAcceptedIssuers();
        }
    }

    public SSLSocketFactory sslSocketFactory() {
        try {
            //信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new EasyX509TrustManager(null)}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }
}
