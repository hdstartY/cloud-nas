import org.hdstart.MemberMain;
import org.hdstart.cloud.elasticsearch.entity.ESBlogInfo;
import org.hdstart.cloud.mapper.BlogMapper;
import org.hdstart.cloud.mapper.ImagesMapper;
import org.hdstart.cloud.vo.BlogImgUrlVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(classes = MemberMain.class)
public class MemberMainTest {

    @Autowired
    ImagesMapper imagesMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    BlogMapper blogMapper;

    @Test
    public void test001 () {
        String password = "88888888";
        String encode = passwordEncoder.encode(password);
        System.out.println(encode);
    }

    @Test
    public void test002 () {
        List<ESBlogInfo> esBlogInfos = blogMapper.selectESBlogList(Arrays.asList(233));
        System.out.println(esBlogInfos);
    }
}
