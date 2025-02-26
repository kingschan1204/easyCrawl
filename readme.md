> This project is a crawler toolkit implemented based on Java. The original intention is to crawl Internet data simply
> and efficiently.


## Implemented Functions

- HTTP/HTTPS GET, POST, PUT
- File Download
- HTTP and SOCKS5 Proxies
- Convert cURL Commands to HTTP Requests
- Supports three types of HTTP request engines (`jsoup`, `jdk HttpClient`, `Apache HttpClient5`)

| HTTP request engines | http Proxy | Socket5 Proxy | http compress |
|----------------------|------------|---------------|---------------|
| Jsoup                | &#x2705;   | &#x2705;      |               |
| Jdk HttpClient       | &#x2705;   |               | &#x2705;      |
| Apache HttpClient5   | &#x2705;   |               | &#x2705;      |


## Project Structure
> The jdk version must be 17 or greater
- `easycrawl-core   `     Crawler core code
- `easycrawl-helper   `     General tools
- `easycrawl-schedule   `   Thread Scheduling Related
- `easycrawl-sql  `       Automated database operation related tools, currently under development
- `easycrawl-test  `      Integration test related code



## Configuration Files

> easy-crawl.yml

```yaml
defaultConfig:
  # HTTP request engines: jsoup jdk httpclient5
  httpEngine: jsoup
  # http use-agent
  useAgent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36
  # HTTP request timeout (milliseconds)
  connectTimeout: 10000
  # Default file download directory
  fileFolder: C://temp//
  # Whether to enable http request compression
  httpCompress: true
```

### file download

```
 String curl = """
                curl 'https://www.csindex.com.cn/csindex-home/exportExcel/security-industry-search-excel/CH' \\
                  -H 'Accept: application/json, text/plain, */*' \\
                  -H 'Accept-Language: zh-CN,zh;q=0.9' \\
                  -H 'Cache-Control: no-cache' \\
                  -H 'Connection: keep-alive' \\
                  -H 'Content-Type: application/json;charset=UTF-8' \\
                  -H 'DNT: 1' \\
                  -H 'Origin: https://www.csindex.com.cn' \\
                  -H 'Pragma: no-cache' \\
                  -H 'Referer: https://www.csindex.com.cn/en/indices/index-detail/000300' \\
                  -H 'Sec-Fetch-Dest: empty' \\
                  -H 'Sec-Fetch-Mode: cors' \\
                  -H 'Sec-Fetch-Site: same-origin' \\
                  -H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36' \\
                  -H 'sec-ch-ua: "Google Chrome";v="131", "Chromium";v="131", "Not_A Brand";v="24"' \\
                  -H 'sec-ch-ua-mobile: ?0' \\
                  -H 'sec-ch-ua-platform: "Windows"' \\
                  --data-raw '{"searchInput":"","pageNum":1,"pageSize":10,"sortField":null,"sortOrder":null}'
                """;

        File file = new ThinEasyCrawl(curl).folder("C:\\temp\\").execute().getFile();
        log.info("fileName：{} fileSize：{} kb", file.getName(), file.length() / 1024);
```

### HTTP and SOCKS5 Proxies

```
        String apiUrl = "https://myip.ipip.net/json";
        
        //socks5
        ProxyConfig proxy = ProxyConfig.of(Proxy.Type.SOCKS, "127.0.0.1", 37890);
        HttpResult result = new ThinEasyCrawl(apiUrl,WebAgent.Engine.JSOUP).proxy(proxy).execute();
        System.out.println(result.body());
        
        //http
        ProxyConfig proxy = ProxyConfig.of(Proxy.Type.HTTP, "127.0.0.1", 27890);
        HttpResult result = new ThinEasyCrawl(apiUrl).proxy(proxy).execute();
        System.out.println(result.body());
```

### get response cookies

```
        String curl = "https://example.com";
        Map<String, String> cookies = new ThinEasyCrawl(curl).execute().cookies();
        System.out.println(cookies);
```







