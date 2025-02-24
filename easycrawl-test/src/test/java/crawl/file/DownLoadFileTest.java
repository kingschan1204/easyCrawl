package crawl.file;

import com.github.kingschan1204.easycrawl.task.ThinEasyCrawl;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@DisplayName("文件下载测试")
public class DownLoadFileTest {

  @DisplayName("行业分类表下载")
  @Test
  public void industry() {

    // https://www.csindex.com.cn/en/indices/index-detail/000300?u_atoken=4f028673-6855-4e1a-b6e3-d51088282ce3&u_asession=0191lMHN-cFk4FTu5NxDqrMORm-PYV5fbA4JgTRLJiOO4qahEY0Hj_dp7OP9VTmuhaX0KNBwm7Lovlpxjd_P_q4JsKWYrT3W_NKPr8w6oU7K-ZOTExeitkM2_aiBCOYJIyG4gsLs4l7pvgUWFgCuXNRGBkFo3NEHBv0PZUm6pbxQU&u_asig=05xmYejoJhBKUw-EcvuyK5znYBe10PFJRSCoQz1hirrwF9CD1jlSNfnIEaju3RCyhL217PAWyc0UWfuKerpK6GXOH8X1sBIuXE0MRMSDhwDK2086aecfNjlkIepAlgyW1lFpYwrZjGGivAS4ono9BE8uNPAQOt6DjFJs-zRt40Qef9JS7q8ZD7Xtz2Ly-b0kmuyAKRFSVJkkdwVUnyHAIJzSNOgbN91VHCGLjKYNZHiAKxWm_qbhuuvCK4lMFnLyoHHto3vP0rWRYQNQ___aSq3e3h9VXwMyh6PgyDIVSG1W9P7sCprGoJqbDicJlXfdVlY_JwJUEQzxLjmckXwyYmCHdKStySv--fIuXDL3c5IDoxSxHiS52tJ6SAI2SIErj7mWspDxyAEEo4kbsryBKb9Q&u_aref=1WNO9azhObAg%2BB4bHXqjrqgZsao%3D#/dataService/industryClassification
    String curl =
        """
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

    File file = new ThinEasyCrawl(curl).execute().getFile();
    log.info("fileName：{} fileSize：{} kb", file.getName(), file.length() / 1024);
  }
}
